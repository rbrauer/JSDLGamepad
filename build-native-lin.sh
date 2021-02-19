#!/bin/bash

set -euxo pipefail

: "-------------------------------------------------------------------- compile"
gcc -Wall -c \
  -I $JAVA_HOME/include -I $JAVA_HOME/include/linux \
  src/swig/JSDLGamepad_SwigInterface_wrap.c \
  -o bin/com/r6753/sdlgamepad/JSDLGamepad_SwigInterface.o \
  $(pkg-config --cflags --libs sdl2)
:

: "----------------------------------------------------------------------- link"
gcc -Wall -shared \
  /usr/lib/x86_64-linux-gnu/libSDL2.a \
  bin/com/r6753/sdlgamepad/JSDLGamepad_SwigInterface.o \
  -o bin/com/r6753/sdlgamepad/JSDLGamepad_SwigInterface.so \
  $(pkg-config --cflags --libs sdl2)

rm bin/com/r6753/sdlgamepad/JSDLGamepad_SwigInterface.o
:
