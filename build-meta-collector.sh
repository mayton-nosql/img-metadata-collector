#!/bin/bash -v

rm -f bin/*
mvn clean
mvn package -P meta-collector
mvn install dependency:copy-dependencies -P meta-collector
cp -f target/img-meta-collector.jar ./bin
rsync target/dependency/* ./bin -r
