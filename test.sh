#!/bin/bash
curl -H "Content-Type: application/json" -X POST -d '{"name":"xyz","age":50}' http://localhost:9000/api/v1/test
curl -H "Content-Type: application/json" -X POST -d '{"name":"yzx","age":50}' http://localhost:9000/api/v1/test
curl -H "Content-Type: application/json" -X POST -d '{"name":"zyx","age":50}' http://localhost:9000/api/v1/test
sleep 5
curl -v  http://localhost:9000/api/v1/test
sleep 5
curl -v  http://localhost:9000/api/v1/test
sleep 5
curl -v  http://localhost:9000/api/v1/test
sleep 5
curl -v  http://localhost:9000/api/v1/test

