import com.r6753.sdlgamepad.JSDLGamepad;
import static com.r6753.sdlgamepad.JSDLGamepad.*;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import java.util.Collections;
import java.util.ArrayList;
import java.util.HashSet;

class Example01 {
  JFrame frame;
  DrawStuff ds;

  public static void main(String[] args) {
    new Example01();
  }

  Example01() {
    frame = new JFrame("Example01");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    ds = new DrawStuff();
    frame.add(ds);
    frame.pack();
    frame.setVisible(true);

    ScheduledExecutorService ses =
      Executors.newScheduledThreadPool(1);
    ses.scheduleAtFixedRate(new Runnable() {
      public void run() {
        ds.repaint();
      }
    }, 0, 16, TimeUnit.MILLISECONDS);

    new Input();
  }

  void forwardInput(boolean pressed) {
    ds.go = pressed;
  }

  void leftInput(boolean pressed) {
    ds.left = pressed;
  }

  void rightInput(boolean pressed) {
    ds.right = pressed;
  }

  void blinkInput() {
    ds.forward(50);
  }

  void exit() {
    WindowEvent ev = new WindowEvent(
      frame, WindowEvent.WINDOW_CLOSING);
    Toolkit.getDefaultToolkit()
      .getSystemEventQueue().postEvent(ev);
  }

  class Input extends JSDLGamepad {
    int controllerID = -1;
    HashSet<Integer> attachedControllers;
    HashSet<Integer> pressed;

    Input() {
      attachedControllers = new HashSet<Integer>();
      pressed = new HashSet<Integer>();
      startThread();
    }

    // pick one controller to use for input ------------------------------------

    public void gamepadAttached(int id) {
      controllerID = id;
      attachedControllers.add(id);
    }

    public void gamepadRemoved(int id) {
      attachedControllers.remove(id);
      if(id == controllerID) {
        if(attachedControllers.size() > 0) {
          ArrayList<Integer> cont = new ArrayList<Integer>();
          cont.addAll(attachedControllers);
          Collections.sort(cont);
          controllerID =
            cont.toArray(new Integer[]{})[0];
        } else {
          controllerID = -1;
        }
      }
    }

    // -------------------------------------------------------------------------

    public void buttonPressed(int id, int button) {
      if(id == controllerID) {
        if(button == BUTTON_START) {
          exit();
        }
        if(button == BUTTON_RIGHTSHOULDER) {
          blinkInput();
        }
        if(button == BUTTON_A) {
          forwardInput(true);
        }
        if(button == BUTTON_DPAD_LEFT) {
          leftInput(true);
        }
        if(button == BUTTON_DPAD_RIGHT) {
          rightInput(true);
        }
      }
    }

    public void buttonReleased(int id, int button) {
      if(button == BUTTON_A) {
        forwardInput(false);
      }
      if(button == BUTTON_DPAD_LEFT) {
        leftInput(false);
      }
      if(button == BUTTON_DPAD_RIGHT) {
        rightInput(false);
      }
    }
  }

  class DrawStuff extends JPanel {
    volatile float[] pos;
    volatile boolean go, left, right;

    float scl = 100;
    int ww = 1280;
    int hh = 720;

    DrawStuff() {
      setPreferredSize(new Dimension(ww, hh));
      pos = new float[] {
        ww * 0.5f, hh * 0.5f, 0f
      };
    }

    void forward(float amt) {
      if(!go) {
        return;
      }
      pos[0] += Math.cos(pos[2]) * amt;
      pos[1] += Math.sin(pos[2]) * amt;

      pos[0] += pos[0] < 0 ? ww : 0;
      pos[0] -= pos[0] > ww ? ww : 0;
      pos[1] += pos[1] < 0 ? hh : 0;
      pos[1] -= pos[1] > hh ? hh : 0;
    }

    void rotate(float amt) {
      pos[2] += amt;
    }

    public void paint(Graphics gfx) {
      if(left) {
        rotate(-0.03f);
      }
      if(right) {
        rotate(0.03f);
      }
      forward(3);
      gfx.setColor(Color.BLACK);
      gfx.fillRect(0, 0, ww, hh);
      gfx.setColor(Color.BLACK);
      gfx.fillRect(0, 0, ww, hh);
      gfx.setColor(Color.WHITE);
      tri(gfx, pos);
    }

    int roundInt(double in) {
      return (int) Math.round(in);
    }

    void tri(Graphics gfx, float[] pos) {
      int[] xvert = new int[3];
      int[] yvert = new int[3];
      int xx = roundInt(pos[0]);
      int yy = roundInt(pos[1]);
      float heading = pos[2];
      float perp = heading + (float) Math.PI * 0.5f;
      xvert[0] = xx + roundInt(Math.cos(heading) * scl);
      yvert[0] = yy + roundInt(Math.sin(heading) * scl);
      xvert[1] = xx + roundInt(Math.cos(perp) * scl * 0.35);
      yvert[1] = yy + roundInt(Math.sin(perp) * scl * 0.35);
      xvert[2] = xx - roundInt(Math.cos(perp) * scl * 0.35);
      yvert[2] = yy - roundInt(Math.sin(perp) * scl * 0.35);
      gfx.drawPolygon(xvert, yvert, 3);
    }
  }
}
