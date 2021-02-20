//https://web.archive.org/web/20140704120535/http://www.codethesis.com/blog/unload-java-jni-dll
package com.r6753.sdlgamepad;

import com.r6753.sdlgamepad.SDL_version;
import static com.r6753.sdlgamepad.JSDLGamepad_SwigInterface.*;

import java.lang.ClassLoader;
import java.net.URLDecoder;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class JSDLGamepad {
  {
    Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
      public void run() {
        stopThread();
        SDL_Quit();
        System.out.println();
      }
    }));
  }

  private class InnerListener implements Runnable {
    {
      String platform = "";
      String os = System.getProperty("os.name");
      System.out.println("JSDLGamepad on " + os);
      if(os.startsWith("Windows")) {
        platform = "win";
      } else if(os.startsWith("Linux")) {
        platform = "lin";
      } else {
        throw new RuntimeException("os not supported");
      }
      loadNative(platform);

      SDL_version compiledWith = new SDL_version();
      SDL_version linkedWith = new SDL_version();
      SDL_VERSION(compiledWith);
      SDL_GetVersion(linkedWith);
      System.out.println(
        "SDL - compiled with version " +
        compiledWith.getMajor() + "." +
        compiledWith.getMinor() + "." +
        compiledWith.getPatch()
      );
      System.out.println(
        "SDL - linked with version " +
        linkedWith.getMajor() + "." +
        linkedWith.getMinor() + "." +
        linkedWith.getPatch()
      );
    }

    private void loadNative(String platform) {
      try {
        System.loadLibrary("SDL2");
        String filename = "lin".equals(platform) ?
          "JSDLGamepad_SwigInterface.so" :
          "JSDLGamepad_SwigInterface.dll";
        String fspath = URLDecoder.decode(
          JSDLGamepad.class
            .getProtectionDomain()
              .getCodeSource()
                .getLocation().getPath(), "UTF-8");
        File ifFile = new File(new File(fspath).getParent(), filename);
        if(!ifFile.exists()) {
          Files.copy(
            JSDLGamepad.class.getResourceAsStream(filename),
            ifFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        System.load(ifFile.getPath());
      } catch(Throwable ex) {
        ex.printStackTrace();
      }
    }

    Thread go() {
      Thread thread = new Thread(
        this, "JSDLGamepad Event Thread");
      thread.setDaemon(true);
      thread.start();
      return thread;
    }

    public void run() {
      try {
        listening = true;
        SDL_Init(SDL_INIT_GAMECONTROLLER);
        SDL_LockJoysticks();
        while(true) {
          SDL_Event ev = new SDL_Event();
          SDL_WaitEvent(ev);

          if(ev.getType() == SDL_QUIT) {
            break;
          }

          SDL_ControllerDeviceEvent cde = ev.getCdevice();
          if(cde == null) {
            continue;
          }
          int type = cde.getType();

          if(type == SDL_CONTROLLERDEVICEADDED) {
            // just using CONTROLLERDEVICE and related methods for now
            // could be more general with the JOYDEVICE equivalents
            SDL_GameControllerOpen(cde.getWhich());
            printLastError();
            gamepadAttached(++uid);
            continue;
          }
          if(type == SDL_JOYDEVICEREMOVED) {
            int id = cde.getWhich();
            long pointer = gamepadPointer(id);
            SDL_GameControllerClose(pointer);
            printLastError();
            gamepadRemoved(id);
            continue;
          }
          if(type == SDL_CONTROLLERAXISMOTION) {
            SDL_ControllerAxisEvent cae = ev.getCaxis();
            axisMotion(cae.getWhich(), cae.getAxis(), cae.getValue());
            continue;
          }
          if(type == SDL_CONTROLLERBUTTONDOWN) {
            SDL_ControllerButtonEvent cbe = ev.getCbutton();
            buttonPressed(cbe.getWhich(), cbe.getButton());
            continue;
          }
          if(type == SDL_CONTROLLERBUTTONUP) {
            SDL_ControllerButtonEvent cbe = ev.getCbutton();
            buttonReleased(cbe.getWhich(), cbe.getButton());
            continue;
          }
        }
      } catch(Throwable ex) {
        ex.printStackTrace();
      } finally {
        SDL_UnlockJoysticks();
        SDL_Quit();
        listening = false;
      }
    }
  }

/******************************************************************************/

  public static final int AXIS_LEFTX = SDL_CONTROLLER_AXIS_LEFTX;
  public static final int AXIS_LEFTY = SDL_CONTROLLER_AXIS_LEFTY;
  public static final int AXIS_RIGHTX = SDL_CONTROLLER_AXIS_RIGHTX;
  public static final int AXIS_RIGHTY = SDL_CONTROLLER_AXIS_RIGHTY;
  public static final int AXIS_TRIGGERLEFT = SDL_CONTROLLER_AXIS_TRIGGERLEFT;
  public static final int AXIS_TRIGGERRIGHT = SDL_CONTROLLER_AXIS_TRIGGERRIGHT;

  public static final int BUTTON_A = SDL_CONTROLLER_BUTTON_A;
  public static final int BUTTON_B = SDL_CONTROLLER_BUTTON_B;
  public static final int BUTTON_X = SDL_CONTROLLER_BUTTON_X;
  public static final int BUTTON_Y = SDL_CONTROLLER_BUTTON_Y;
  public static final int BUTTON_BACK = SDL_CONTROLLER_BUTTON_BACK;
  public static final int BUTTON_GUIDE = SDL_CONTROLLER_BUTTON_GUIDE;
  public static final int BUTTON_START = SDL_CONTROLLER_BUTTON_START;
  public static final int BUTTON_LEFTSTICK = SDL_CONTROLLER_BUTTON_LEFTSTICK;
  public static final int BUTTON_RIGHTSTICK = SDL_CONTROLLER_BUTTON_RIGHTSTICK;
  public static final int BUTTON_LEFTSHOULDER = SDL_CONTROLLER_BUTTON_LEFTSHOULDER;
  public static final int BUTTON_RIGHTSHOULDER = SDL_CONTROLLER_BUTTON_RIGHTSHOULDER;
  public static final int BUTTON_DPAD_UP = SDL_CONTROLLER_BUTTON_DPAD_UP;
  public static final int BUTTON_DPAD_DOWN = SDL_CONTROLLER_BUTTON_DPAD_DOWN;
  public static final int BUTTON_DPAD_LEFT = SDL_CONTROLLER_BUTTON_DPAD_LEFT;
  public static final int BUTTON_DPAD_RIGHT = SDL_CONTROLLER_BUTTON_DPAD_RIGHT;

/******************************************************************************/

  private Thread eventThread;
  private volatile boolean listening;
  private volatile int uid = -1;
  //SDL equivalent apparently somehow never ever gets reset

/******************************************************************************/

  private InnerListener listener() {
    try {
      return (InnerListener) (new ClassLoader(){})
        .loadClass("com.r6753.sdlgamepad.JSDLGamepad$InnerListener")
          .getDeclaredConstructor(JSDLGamepad.class).newInstance(this);
    } catch(Throwable ex) {
      ex.printStackTrace();
      return null;
    }
  }

  public void startThread() {
    if(listening) {
      return;
    }
    eventThread = listener().go();
  }

  public void signalQuit() {
    SDL_Event ev = new SDL_Event();
    ev.setType(SDL_QUIT);
    SDL_PushEvent(ev);
  }

  public void stopThread() {
    if(!listening) {
      return;
    }
    try {
      signalQuit();
      if(Thread.currentThread() == eventThread) {
        // can't wait for event thread to die here
        // if called from event thread
        // eventThread = null; //<-- libSDL segfault
        return;
      }
      eventThread.join();
      eventThread = null;
    } catch(Throwable ex) {
      ex.printStackTrace();
    }
  }

  public void reload() {
    stopThread();
    startThread();
  }

  public void printLastError() {
    String error = SDL_GetError();
    if(!error.isEmpty()) {
      System.err.println(error);
      SDL_ClearError();
    }
  }

  public String stringForButton(int button) {
    return SDL_GameControllerGetStringForButton(button);
  }

  public String stringForAxis(int axis) {
    return SDL_GameControllerGetStringForAxis(axis);
  }

  public long gamepadPointer(int uid) {
    // returns SDL_GameController address
    return SDL_GameControllerFromInstanceID(uid);
  }

  public long joystickPointer(int uid) {
    // returns SDL_Joystick address
    return SDL_GameControllerGetJoystick(gamepadPointer(uid));
  }

  public String getGamepadName(int uid) {
    // Joystick names seem to be better than GameController ones
    return SDL_JoystickName(joystickPointer(uid));
  }

  public void gamepadAttached(int id) {}
  public void gamepadRemoved(int id) {}
  public void buttonPressed(int id, int button) {}
  public void buttonReleased(int id, int button) {}
  public void axisMotion(int id, int axis, int value) {}

  public JSDLGamepad() {}
}
