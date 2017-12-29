cls
@echo off
echo Building files ...
javac -d out -g -parameters -cp "./src/libs/*";"./" src/com/issta/*.java
echo Build completed

mkdir instrumented
mkdir instrumented\jar
mkdir instrumented\classes
@echo on

java -cp "src/libs/*";"./out/" com.issta.Main "C:/Users/User/Desktop/ISSTA/jar/Testing.jar" 
java -cp .\instrumented\jar\Testing.jar;.\out com.asmproj.test.Testing_2