import com.r6753.sdlgamepad.JSDLGamepad;
import static com.r6753.sdlgamepad.JSDLGamepad.*;

class Test extends JSDLGamepad {
  public static void main(String[] args) {
    new Test();
  }

  Test() {
    try {
      System.out.println("-----------start");
      startThread();
      Thread.currentThread().sleep(10000);
      System.out.println("-----------reload");
      reload();
      Thread.currentThread().sleep(10000);
    } catch(Throwable ex) {
      ex.printStackTrace();
    }
  }

  public void gamepadAttached(int id) {
    System.out.println("Controller -" + id + "- attached [" +
      getGamepadName(id) + "].");
  }
  public void gamepadRemoved(int id) {
    System.out.println("Controller -" + id + "- detached.");
  }
  public void buttonPressed(int id, int button) {
    System.out.println("Controller -" + id + "- " +
      stringForButton(button) + " pressed.");
  }
  public void buttonReleased(int id, int button) {
    System.out.println("Controller -" + id + "- " +
      stringForButton(button) + " released.");
  }
  public void axisMotion(int id, int axis, int value) {
    System.out.println("Controller -" + id + "- " +
      stringForAxis(axis) + " " + value + ".");
  }
}
