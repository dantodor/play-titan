enablePlugins(JavaAppPackaging)

name         := "titan-http"
organization := "com.nodalweb"
version      := "1.0"
scalaVersion := "2.11.7"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

val titanV = "1.0.0"

libraryDependencies ++= {
  val akkaV       = "2.3.12"
  val akkaStreamV = "1.0"
  val scalaTestV  = "2.2.5"
  Seq(
    "com.typesafe.akka" %% "akka-actor"                           % akkaV,
    "com.typesafe.akka" %% "akka-stream-experimental"             % akkaStreamV,
    "com.typesafe.akka" %% "akka-http-core-experimental"          % akkaStreamV,
    "com.typesafe.akka" %% "akka-http-experimental"               % akkaStreamV,
    "com.typesafe.akka" %% "akka-http-spray-json-experimental"    % akkaStreamV,
    "com.typesafe.akka" %% "akka-http-testkit-experimental"       % akkaStreamV,
    "com.michaelpollmeier" %% "gremlin-scala" % "3.0.1-incubating",
    "org.scalatest"     %% "scalatest"                            % scalaTestV,
    "com.thinkaurelius.titan" % "titan-core" % titanV,
    "com.thinkaurelius.titan" % "titan-cassandra" % titanV,
    "com.thinkaurelius.titan" % "titan-es" % titanV
  )
}

Revolver.settings
