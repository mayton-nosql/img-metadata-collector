#!/bin/bash

java -XX:+FlightRecorder \
     -XX:StartFlightRecording=maxsize=2048Mb,dumponexit=true,filename=img-metadata-collector.jfr \
     -jar img-meta-collector.jar -s /bigdata/foto -o /backup/out

