#!/bin/bash
#
# Start script for test-data-generator

PORT=8080

exec java -jar -Dserver.port="${PORT}" "test-data-generator.jar"
