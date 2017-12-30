cls
echo "Printing class bytecode"
javac -cp "./src/libs/*";"./" Visualizer\Visualize.java 
java  -cp "./src/libs/*";"./Visualizer/" Visualize .\instrumented\classes\Example1.class 