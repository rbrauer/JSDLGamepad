#!/bin/bash

set -euxo pipefail
export W64_DEV=w64/SDL2-2.0.10/x86_64-w64-mingw32

: "------------------------------------------------------------------ build-w64"
x86_64-w64-mingw32-gcc -Wall -v -shared -static-libgcc \
  -I $JAVA_HOME/include -I $JAVA_HOME/include/linux \
  -D_REENTRANT -I $W64_DEV/include \
  src/swig/JSDLGamepad_SwigInterface_wrap.c $W64_DEV/lib/libSDL2.dll.a \
  -o bin/com/r6753/sdlgamepad/JSDLGamepad_SwigInterface.dll
:
