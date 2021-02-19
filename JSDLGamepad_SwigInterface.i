%module JSDLGamepad_SwigInterface
%include "enumsimple.swg"

%{
  #include "SDL2/SDL.h"
  #include "SDL2/SDL_version.h"
  #include "SDL2/SDL_error.h"
  #include "SDL2/SDL_events.h"
  #include "SDL2/SDL_joystick.h"
  #include "SDL2/SDL_gamecontroller.h"
%}

%constant const int SDL_QUIT;
%constant const int SDL_INIT_GAMECONTROLLER;
%constant const int SDL_CONTROLLERDEVICEADDED;
%constant const int SDL_JOYDEVICEREMOVED;
%constant const int SDL_CONTROLLERDEVICEREMAPPED;
%constant const int SDL_CONTROLLERAXISMOTION;
%constant const int SDL_CONTROLLERBUTTONDOWN;
%constant const int SDL_CONTROLLERBUTTONUP;

%javaconst(1);
extern enum SDL_GameControllerAxis {
  SDL_CONTROLLER_AXIS_LEFTX,
  SDL_CONTROLLER_AXIS_LEFTY,
  SDL_CONTROLLER_AXIS_RIGHTX,
  SDL_CONTROLLER_AXIS_RIGHTY,
  SDL_CONTROLLER_AXIS_TRIGGERLEFT,
  SDL_CONTROLLER_AXIS_TRIGGERRIGHT
};

extern enum SDL_GameControllerButton {
  SDL_CONTROLLER_BUTTON_A,
  SDL_CONTROLLER_BUTTON_B,
  SDL_CONTROLLER_BUTTON_X,
  SDL_CONTROLLER_BUTTON_Y,
  SDL_CONTROLLER_BUTTON_BACK,
  SDL_CONTROLLER_BUTTON_GUIDE,
  SDL_CONTROLLER_BUTTON_START,
  SDL_CONTROLLER_BUTTON_LEFTSTICK,
  SDL_CONTROLLER_BUTTON_RIGHTSTICK,
  SDL_CONTROLLER_BUTTON_LEFTSHOULDER,
  SDL_CONTROLLER_BUTTON_RIGHTSHOULDER,
  SDL_CONTROLLER_BUTTON_DPAD_UP,
  SDL_CONTROLLER_BUTTON_DPAD_DOWN,
  SDL_CONTROLLER_BUTTON_DPAD_LEFT,
  SDL_CONTROLLER_BUTTON_DPAD_RIGHT
};

extern void SDL_VERSION(SDL_version* ver);
extern void SDL_GetVersion(SDL_version* ver);
extern int SDL_Init(int flags);
extern void SDL_Quit(void);
extern const char* SDL_GetError(void);
extern void SDL_ClearError(void);

extern void SDL_LockJoysticks(void);
extern void SDL_UnlockJoysticks(void);
extern int SDL_PushEvent(SDL_Event* event);
extern int SDL_WaitEvent(SDL_Event* event);
extern int SDL_GameControllerOpen(int joystick_index);
// returns integer from pointer without cast, returns SDL_GameController*
extern void SDL_GameControllerClose(jlong gamecontroller);
// arg pointer from integer without cast, takes SDL_GameController*
extern jlong SDL_GameControllerFromInstanceID(int joyid);
// returns integer from pointer without cast, returns SDL_GameController*

extern const char* SDL_GameControllerGetStringForAxis(SDL_GameControllerAxis axis);
extern const char* SDL_GameControllerGetStringForButton(SDL_GameControllerButton button);
jlong SDL_GameControllerGetJoystick(jlong gamecontroller);
const char* SDL_JoystickName(jlong joystick);

//SDL_ControllerDeviceEvent
//SDL_ControllerAxisEvent
//SDL_ControllerButtonEvent
//SDL_version

%immutable;
typedef struct SDL_ControllerDeviceEvent
{
    int type;        /**< ::SDL_CONTROLLERDEVICEADDED, ::SDL_CONTROLLERDEVICEREMOVED, or ::SDL_CONTROLLERDEVICEREMAPPED */
    int timestamp;   /**< In milliseconds, populated using SDL_GetTicks() */
    int which;       /**< The joystick device index for the ADDED event, instance id for the REMOVED or REMAPPED event */
} SDL_ControllerDeviceEvent;

// typedef struct SDL_ControllerDeviceEvent
// {
//     Uint32 type;        /**< ::SDL_CONTROLLERDEVICEADDED, ::SDL_CONTROLLERDEVICEREMOVED, or ::SDL_CONTROLLERDEVICEREMAPPED */
//     Uint32 timestamp;   /**< In milliseconds, populated using SDL_GetTicks() */
//     Sint32 which;       /**< The joystick device index for the ADDED event, instance id for the REMOVED or REMAPPED event */
// } SDL_ControllerDeviceEvent;

typedef struct SDL_ControllerAxisEvent
{
    int type;        /**< ::SDL_CONTROLLERAXISMOTION */
    int timestamp;   /**< In milliseconds, populated using SDL_GetTicks() */
    int which; /**< The joystick instance id */
    char axis;         /**< The controller axis (SDL_GameControllerAxis) */
    char padding1;
    char padding2;
    char padding3;
    short value;       /**< The axis value (range: -32768 to 32767) */
    short padding4;
} SDL_ControllerAxisEvent;

// typedef struct SDL_ControllerAxisEvent
// {
//     Uint32 type;        /**< ::SDL_CONTROLLERAXISMOTION */
//     Uint32 timestamp;   /**< In milliseconds, populated using SDL_GetTicks() */
//     SDL_JoystickID which; /**< The joystick instance id */
//     Uint8 axis;         /**< The controller axis (SDL_GameControllerAxis) */
//     Uint8 padding1;
//     Uint8 padding2;
//     Uint8 padding3;
//     Sint16 value;       /**< The axis value (range: -32768 to 32767) */
//     Uint16 padding4;
// } SDL_ControllerAxisEvent;

typedef struct SDL_ControllerButtonEvent
{
    int type;        /**< ::SDL_CONTROLLERBUTTONDOWN or ::SDL_CONTROLLERBUTTONUP */
    int timestamp;   /**< In milliseconds, populated using SDL_GetTicks() */
    int which; /**< The joystick instance id */
    char button;       /**< The controller button (SDL_GameControllerButton) */
    char state;        /**< ::SDL_PRESSED or ::SDL_RELEASED */
    char padding1;
    char padding2;
} SDL_ControllerButtonEvent;

// typedef struct SDL_ControllerButtonEvent
// {
//     Uint32 type;        /**< ::SDL_CONTROLLERBUTTONDOWN or ::SDL_CONTROLLERBUTTONUP */
//     Uint32 timestamp;   /**< In milliseconds, populated using SDL_GetTicks() */
//     SDL_JoystickID which; /**< The joystick instance id */
//     Uint8 button;       /**< The controller button (SDL_GameControllerButton) */
//     Uint8 state;        /**< ::SDL_PRESSED or ::SDL_RELEASED */
//     Uint8 padding1;
//     Uint8 padding2;
// } SDL_ControllerButtonEvent;

%mutable;
//mutable only to signal listener thread
//by pushing event with type set to SDL_QUIT
typedef union SDL_Event
{
    int type;                    /**< Event type, shared with all events */
    SDL_CommonEvent common;         /**< Common event data */
    SDL_DisplayEvent display;       /**< Window event data */
    SDL_WindowEvent window;         /**< Window event data */
    SDL_KeyboardEvent key;          /**< Keyboard event data */
    SDL_TextEditingEvent edit;      /**< Text editing event data */
    SDL_TextInputEvent text;        /**< Text input event data */
    SDL_MouseMotionEvent motion;    /**< Mouse motion event data */
    SDL_MouseButtonEvent button;    /**< Mouse button event data */
    SDL_MouseWheelEvent wheel;      /**< Mouse wheel event data */
    SDL_JoyAxisEvent jaxis;         /**< Joystick axis event data */
    SDL_JoyBallEvent jball;         /**< Joystick ball event data */
    SDL_JoyHatEvent jhat;           /**< Joystick hat event data */
    SDL_JoyButtonEvent jbutton;     /**< Joystick button event data */
    SDL_JoyDeviceEvent jdevice;     /**< Joystick device change event data */
    SDL_ControllerAxisEvent caxis;      /**< Game Controller axis event data */
    SDL_ControllerButtonEvent cbutton;  /**< Game Controller button event data */
    SDL_ControllerDeviceEvent cdevice;  /**< Game Controller device event data */
    SDL_AudioDeviceEvent adevice;   /**< Audio device event data */
    SDL_SensorEvent sensor;         /**< Sensor event data */
    SDL_QuitEvent quit;             /**< Quit request event data */
    SDL_UserEvent user;             /**< Custom event data */
    SDL_SysWMEvent syswm;           /**< System dependent window event data */
    SDL_TouchFingerEvent tfinger;   /**< Touch finger event data */
    SDL_MultiGestureEvent mgesture; /**< Gesture event data */
    SDL_DollarGestureEvent dgesture; /**< Gesture event data */
    SDL_DropEvent drop;             /**< Drag and drop event data */

    /* This is necessary for ABI compatibility between Visual C++ and GCC
       Visual C++ will respect the push pack pragma and use 52 bytes for
       this structure, and GCC will use the alignment of the largest datatype
       within the union, which is 8 bytes.

       So... we'll add padding to force the size to be 56 bytes for both.
    */
    Uint8 padding[56];
} SDL_Event;

%immutable;
typedef struct SDL_version
{
    int major;        /**< major version */
    int minor;        /**< minor version */
    int patch;        /**< update version */
} SDL_version;

// typedef struct SDL_version
// {
//     Uint8 major;        /**< major version */
//     Uint8 minor;        /**< minor version */
//     Uint8 patch;        /**< update version */
// } SDL_version;
