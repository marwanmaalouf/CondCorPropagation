cls
@echo off
echo Building files ...
javac -d out -g -parameters -cp "./src/libs/*";"./" src/com/issta/*.java
echo Build completed

mkdir instrumented
mkdir instrumented\jar
mkdir instrumented\classes
mkdir instrumented\profile
@echo on