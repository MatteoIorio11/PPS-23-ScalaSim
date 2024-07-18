#!/bin/bash
cd ./scalasim/
sbt assembly
cd ..
java -jar --enable-preview ./scalasim/target/scala-3.4.0/scalasim.jar
