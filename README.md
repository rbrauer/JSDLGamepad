## JSDLGamepad
JSDLGamepad is a minimal wrapper for using [SDL](http://libsdl.org/) to get gamepad input in Java. The native glue is generated using [SWIG](http://swig.org/) to cover a subset of SDL gamepad handling.

### Build
The ```build.sh``` shell script generates Java and native glue code, compiles the native code for Linux and Windows x64, compiles the Java code, and packages everything in a jar. Assuming everything builds okay and SDL2 is available, it also runs the included example code. The build looks for the Windows SDL2 dev files in the w64 file for the Windows cross-compile. They can be downloaded from the SDL site linked above.

### Use
The SDL2 library must be available on ```java.library.path``` to use JSDLGamepad. Get it from the link above or your package manager.

The library consists of one class managing a thread that blocks on ```SDL_WaitEvent(...)``` to recieve events. Event data is passed through to one of the following methods based on type. To listen for events, extend ```JSDLGamepad``` and override some or all of these, then call ```startThread()``` to start the event thread.
```java
public void gamepadAttached(int id) {}
public void gamepadRemoved(int id) {}
public void buttonPressed(int id, int button) {}
public void buttonReleased(int id, int button){}
public void axisMotion(int id, int axis, int value){}
```
The IDs used by the library start at zero and increase every time a controller connection has been detected. When ```startThread()``` is called, an "attached" event should be produced for every currently connected gamepad. Controllers should be detected no matter when the are connected -- "hot plugging" and "after plugging" should work.

The library can be restarted with ```reload()``` if necessary. This will produce new "attached" events for all connected gamepads. IDs do not reset. Controllers will be given higher IDs than what they had before the restart.

### Button and Axis Constants
Below are the constants identifying buttons and axes (left and right sticks and analog triggers) usable from subclasses.
```java
AXIS_LEFTX
AXIS_LEFTY
AXIS_RIGHTX
AXIS_RIGHTY
AXIS_TRIGGERLEFT
AXIS_TRIGGERRIGHT
BUTTON_A
BUTTON_B
BUTTON_X
BUTTON_Y
BUTTON_BACK
BUTTON_GUIDE
BUTTON_START
BUTTON_LEFTSTICK
BUTTON_RIGHTSTICK
BUTTON_LEFTSHOULDER
BUTTON_RIGHTSHOULDER
BUTTON_DPAD_UP
BUTTON_DPAD_DOWN
BUTTON_DPAD_LEFT
BUTTON_DPAD_RIGHT
```