import akka.actor.{ActorRef, Props, Actor, ActorSystem}
import akka.pattern.ask
import akka.event.{LoggingAdapter, Logging}
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.{HttpResponse, HttpRequest}
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.{ActorMaterializer, Materializer}
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.util.Timeout
import com.thinkaurelius.titan.core.TitanGraph
import gremlin.scala._
import org.apache.commons.configuration.BaseConfiguration

import scala.concurrent.duration._
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import scala.concurrent.{ExecutionContextExecutor, Future}
import spray.json.DefaultJsonProtocol



case class Test(name: String, age: Int)
case object Ask


trait Protocols extends DefaultJsonProtocol {
  implicit val testFormat = jsonFormat2(Test.apply)
}

class DbActor extends Actor {
  val db = new DbAdapter
  def receive = {
    case Ask => sender ! Test("Users",db.getAgeSum)
    case t:Test => db.saveTest(t)
    case _       => println("huh?")
  }
}

class DbAdapter {

  val g = connect()
  val gs = g.asScala

  def getTitanConf = {
    val conf = new BaseConfiguration();

    conf.setProperty("storage.backend","cassandrathrift");
    conf.setProperty("storage.hostname","127.0.0.1");
    conf.setProperty("cache.db-cache","false");
    conf.setProperty("cache.db-cache-clean-wait","20");
    conf.setProperty("cache.db-cache-time","0");
    conf.setProperty("cache.db-cache-size","0.25");

    conf
  }

  def connect(): TitanGraph = {

    import com.thinkaurelius.titan.core.TitanFactory
    TitanFactory.open(getTitanConf)
  }

  def saveTest ( t: Test ) = {
    gs.addVertex("test", Map("name"->t.name,"age"->t.age))

  }

  def getAgeSum = {
    gs.V.values[Int]("age").toList().sum

  }


}

trait Service extends Protocols {

  implicit val system: ActorSystem
  implicit def executor: ExecutionContextExecutor
  implicit val materializer: Materializer

  def config: Config
  val logger: LoggingAdapter

  val dbActor: ActorRef

  implicit val timeout = Timeout(5 seconds)

  val routes = {
    logRequestResult("test") {
      pathPrefix("api") {
        (get ) {
          logger.info("got get request")
          complete {
            dbActor.ask(Ask).mapTo[Test]
          }
        } ~
          (post & entity(as[Test])) { test =>
            complete {
              logger.info("got Test entity: "+test)
              dbActor ! test
              Created
            }
          }
      }
    }
  }
}

object Main extends App with Service {
  override implicit val system = ActorSystem()
  override implicit val executor = system.dispatcher
  override implicit val materializer = ActorMaterializer()

  override val config = ConfigFactory.load()
  override val logger = Logging(system, getClass)
  override val dbActor = system.actorOf(Props[DbActor],"dbActor")

  Http().bindAndHandle(routes, config.getString("http.interface"), config.getInt("http.port"))
}
