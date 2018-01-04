cls
@echo off
echo Building files ...
javac -d out -g -parameters -cp "./src/libs/*";"./" src/com/issta/*.java
javac -d out -g -parameters -cp "./src/libs/*";"./" src/com/issta/test/*.java
echo Build completed

mkdir out
mkdir instrumented
mkdir instrumented\jar
mkdir instrumented\classes
mkdir instrumented\profile
@echo on