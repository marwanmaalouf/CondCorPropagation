cls
@echo off
echo Building files ...
javac -d out -g -parameters -cp "./src/libs/*";"./" src/com/issta/*.java
echo Build completed

mkdir instrumented
mkdir instrumented\jar
mkdir instrumented\classes
@echo on

java -cp "src/libs/*";"./out/" com.issta.Main "C:/Users/User/Desktop/ISSTA/SourceCodeToTest/Test.class" 
java -cp .\instrumented\classes;.\out Test