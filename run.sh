#!/bin/bash

DefectsDir=/home/WIN2K/mki06/Desktop/ISSTA
pack=Math
testDir=test-classes
profilerDir=/home/WIN2K/mki06/Desktop/DONOTDELETE

cd ${DefectsDir}/${pack}/
for version in {1..106}
do 
    cd v${version}
    echo "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! $version !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
    rm -r target
    ant compile
    ant compile.tests

    cd target

    ## Start with instrumenting the classes
    cd classes
    jar cf classes.jar org
    rm -r org
    java -cp .:${profilerDir}/src/libs/*:${profilerDir}/CC_Profiler.jar com.issta.Main classes.jar
    jar -xf classes_instrumented.jar
    cd ..
    
    if [ -d "tests" ]; then
        testDir=tests
    fi

    cd ${testDir}
    jar cf tests.jar org
    rm -r org
    java -cp .:${profilerDir}/src/libs/*:${profilerDir}/CC_Profiler.jar:tests.jar:../classes/classes.jar com.issta.test.TestMain tests.jar
    jar -xf tests_instrumented.jar
    cd ..

    cd ..
    /home/WIN2K/mki06/Documents/defects4j/framework/bin/defects4j test

    cd ${DefectsDir}/${pack}/
done