REM check [path to class file]
cls
echo "Printing class bytecode"
javac -cp "./src/libs/*";"./" Visualizer\Visualize.java 
java  -cp "./src/libs/*";"./Visualizer/" Visualize %1