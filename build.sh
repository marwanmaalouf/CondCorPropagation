#!/bin/bash
javac -d out -cp .:src/libs/* src/com/issta/*.java
javac -d out -cp .:src/libs/* src/com/issta/test/*.java
cd out
jar cf CC_Profiler.jar com
mv CC_Profiler.jar ../CC_Profiler.jar
cd ..