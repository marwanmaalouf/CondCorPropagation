set ANT_HOME=C:\Apache\apache-ant-1.10.1
set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_111
set PATH=%ANT_HOME%\bin;%PATH%
set pack=Lang
set pathToDir=C:\Users\User\Desktop\MarwanMaalouf
set pathToInstrumenter=C:\Users\User\Desktop\ISSTA
set javaPathToCurrDir=C:/Users/User/Desktop/MarwanMaalouf
set profiler=%pathToInstrumenter%\Profiler.jar



rem FOR /D %%i IN (%pathToDir%\%pack%\?34b) DO (
rem 	echo found %%i
rem 	for /r %%j in (%%i\target\test-classes\*) DO (
rem 		echo %%j
rem 	)	
rem )


rem FOR /D %%i IN (%pathToDir%\%pack%\?34b) DO (
rem 	break>test.txt  
rem 	cd %%i\target\test-classes\
rem 	for /r %%j in (*.class) DO echo C:%%~pj%%~nj>>%pathToInstrumenter%\test.txt
rem 	cd %pathToInstrumenter%
rem 	for /F "tokens=*" %%A in (test.txt) do java -cp "./src/libs/*";%profiler%;%%i\target\classes;%%i\target\test-classes org.junit.runner.JUnitCore %%A
rem )

cd %pathToDir%\%pack%\
FOR /D %%i IN (?34b) DO (
	echo Compiling version %%i
	cd %%i
	rmdir /s /q target
	ant compile
	ant compile.tests
	cd target\
	jar cf classes.jar classes
	jar cf test-classes.jar test-classes
	cd %pathToInstrumenter%
	call run.bat "%javaPathToCurrDir%/%pack%/%%i/target/classes.jar" "%javaPathToCurrDir%/%pack%/%%i/target/test-classes.jar"
	copy %pathToInstrumenter%\instrumented\jar\classes.jar %pathToCurrDir%\%pack%\%%i\target\classes.jar /Y
	copy %pathToInstrumenter%\instrumented\jar\test-classes.jar %pathToCurrDir%\%pack%\%%i\target\test-classes.jar /Y
	cd %pathToCurrDir%\%pack%\%%i\target\
	rmdir /s /q classes
	rmdir /s /q test-classes
	jar -xvf classes.jar
	jar -xvf test-classes.jar


	echo this part works fine
	break>%pathToInstrumenter%\test.txt  
	cd %pathToDir%\%pack%\%%i\target\test-classes\
	call %pathToInstrumenter%\getTest.bat
	cd %pathToInstrumenter%
	for /F "tokens=*" %%A in (test.txt) do java -cp "./src/libs/*";%pathToInstrumenter%\out\;%pathToDir%\%pack%\%%i\target\classes;%pathToDir%\%pack%\%%i\target\test-classes org.junit.runner.JUnitCore %%A
)