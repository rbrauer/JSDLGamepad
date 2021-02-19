#!/bin/bash
# zip - ./bin/* | sudo nc -w 3 -l 80

set -euxo pipefail
export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64

: "---------------------------------------------------------------------- clean"
rm -f ./src/swig/*
rm -rf ./bin/com
rm -f ./bin/*.*
:

: "------------------------------------------------------------------- generate"
cd src/swig
swig4.0 -java -v -package com.r6753.sdlgamepad \
  -outcurrentdir -outdir . \
  ../../JSDLGamepad_SwigInterface.i
cd ../..
:

: "--------------------------------------------------------------- java compile"
javac -d bin src/*.java src/swig/*.java
:

./build-native-lin.sh
./build-native-win.sh

: "------------------------------------------------------------------- java jar"
cd bin
jar cf JSDLGamepad.jar * com/*
cd ..
:

: "------------------------------------------------------------------------ run"
# java -ea -Xcheck:jni -cp bin Test
java -ea -Xcheck:jni -cp bin/JSDLGamepad.jar Test
:
