package code.core;

import code.ai.MineAI;
// import java.text.SimpleDateFormat;
// import java.util.Date;

import code.math.IOHelp;
import code.math.Vector2;

import code.ui.UITextfield;
import code.ui.UIController;

import code.board.Camera;

//import java.util.*;
//import java.awt.Color;
//import java.awt.Font;
import java.awt.image.BufferedImage;
import java.awt.Insets;
//import java.awt.Toolkit;

import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentAdapter;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;

enum State {
  MAINMENU,
  RUN,
  DEATH,
  VICTORY
}

/**
* Core class for the currently unnamed game
*/
public class Core extends JPanel {
  private static final long serialVersionUID = 1;

  public static final Vector2 DEFAULT_SCREEN_SIZE = new Vector2(1920, 1080);

  private JFrame f;
  private boolean maximized = true;

  public int toolBarLeft, toolBarRight, toolBarTop, toolBarBot;

  private boolean[] keyDown = new boolean[65536];
  private boolean[] mouseDown = new boolean[4];
  private Vector2 mousePos;
  private Vector2 mousePre;

  UIController uiCon = new UIController();

  Scene current;
  Scene previous;

  Camera cam;
  int screenSizeX;
  int screenSizeY;
  int smallScreenX = (int)DEFAULT_SCREEN_SIZE.x;
  int smallScreenY = (int)DEFAULT_SCREEN_SIZE.y;

  String tranName;

  MineAI ai = null;

  // int pauseCool = 0;
  int deathTime = 0;

  private UITextfield activeTextField = null;

  /** Current game state */
  private State state = State.MAINMENU;

  /**
  * Main method. Called on execution. Performs basic startup
  *
  * @param args Ignored for now
  */
  public static void main(String[] args) {
    Core core = new Core();
    core.start();
    core.run();
  }

  /**
  * Performs initialisation of the program. Only to be run on startup
  */
  public void start() {
    f = new JFrame("Minesweeper");
    f.getContentPane().add(this);
    f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    f.setResizable(true);
    BufferedImage image = IOHelp.readImage("icon.png");
    f.setIconImage(image);
    f.addWindowListener( new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        System.exit(0);
      }
    });
    f.addComponentListener( new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent e) {
        screenSizeX = f.getWidth() - toolBarLeft - toolBarRight;
        screenSizeY = f.getHeight() - toolBarTop - toolBarBot;
        if (cam != null) {cam.setScreenSize(screenSizeX, screenSizeY);}
        // System.out.println(screenSizeX + ", " + screenSizeY);
      }
    });
    f.setExtendedState(JFrame.MAXIMIZED_BOTH);
    f.setUndecorated(true);
    f.setVisible(true);
    f.requestFocus();
    screenSizeX = f.getWidth();
    screenSizeY = f.getHeight();

    current = new Scene(true);
    cam = new Camera(new Vector2(), new Vector2(), 1, screenSizeX, screenSizeY);
    uiCon.setCurrent("Main Menu");

    initialiseControls();
  }

  /**
  * Provides system functionality for buttons pressed in the UI. For UI functionality, use the equivalent method in the UI class.
  *
  * @param button The button pressed to activate
  */
  private void input(String button) {
    if (button.equals("Quit to Desktop")) {System.exit(0);}
    if (button.equals("Restart")) {current.reset(); state = State.RUN; uiCon.setMode("Default"); return;}
    if (button.equals("Quit to Title"))   {toScene(true); return;}
    if (button.equals("Play")) {newGame(); return;}
  }

  /**
  * Switches the current scene to a new one via the new scene's name
  *
  * @param name The name of the scene to switch to
  */
  public void toScene(boolean menu) {
    current = new Scene(menu);
    cam = new Camera(new Vector2(), new Vector2(), 1, screenSizeX, screenSizeY);

    if (menu) {
      state = State.MAINMENU;
      uiCon.setCurrent("Main Menu");
      ai = null;
      return;
    }
    state = State.RUN;
    uiCon.setCurrent("HUD");
    ai = new MineAI(current, current.getMapSX(), current.getMapSY());
  }

  /**
  * A helper method that updates the window insets to match their current state
  */
  public void updateInsets() {
    Insets i = f.getInsets(); //Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration())
    // System.out.println(i);
    toolBarLeft = i.left;
    toolBarRight = i.right;
    toolBarTop = i.top;
    toolBarBot = i.bottom;
  }

  /**
  * A helper method that toggles fullscreen for the window
  */
  public void doFull() {
    f.removeNotify();
    if (maximized) {
      f.setExtendedState(JFrame.NORMAL);
      f.setUndecorated(false);
      f.addNotify();
      updateInsets();
      f.setSize(smallScreenX + toolBarLeft + toolBarRight, smallScreenY + toolBarTop + toolBarBot);
    }
    else {
      smallScreenX = screenSizeX;
      smallScreenY = screenSizeY;
      f.setVisible(false);
      f.setExtendedState(JFrame.MAXIMIZED_BOTH);
      f.setUndecorated(true);
      f.setVisible(true);
      updateInsets();
      f.addNotify();
    }
    f.requestFocus();
    maximized = !maximized;
  }

  /**
  * Starts up a new game in the current save game slot designated by 'saveName' and switches to the opening scene
  */
  public void newGame() {
    try {
      Thread.sleep(32);
    } catch(InterruptedException e){Thread.currentThread().interrupt();}
    toScene(false);
  }

  /**
  * Oh no! You lost
  */
  public void lose() {
    state = State.DEATH;
    uiCon.setMode("Defeat");
  }

  /**
  * You Win!
  */
  public void win() {
    state = State.VICTORY;
    uiCon.setMode("Victory");
  }

  /**
  * Main loop. Should always be running. Runs the rest of the game engine
  */
  public void run() {
    while (true) {

      switch (state) {
        case MAINMENU:
        break;
        case RUN:
        uiCon.setMode("Default");
        if (ai != null && !ai.Suggests()) {
          if (!ai.step()) lose();
          if (current.checkWin()) win();
        }
        if (mouseDown[2]) {
          cam.setOffset(cam.getOffset().add(mousePos.subtract(mousePre)));
          mousePre = mousePos.copy();
        }
        break;
        case DEATH:
        // input("Restart");
        case VICTORY:
        if (mouseDown[2]) {
          cam.setOffset(cam.getOffset().add(mousePos.subtract(mousePre)));
          mousePre = mousePos.copy();
        }
        break;
      }

      repaint();

      try {
        Thread.sleep(32);
      } catch(InterruptedException e){Thread.currentThread().interrupt();}
    }
  }

  public void paintComponent(Graphics gra) {
    Graphics2D g = (Graphics2D) gra;

    switch (state) {
      case MAINMENU:
      if (current != null) {current.draw(g, cam, false);}
      uiCon.draw(g, screenSizeX, screenSizeY);
      break;
      case RUN:
      current.draw(g, cam, false);
      uiCon.draw(g, screenSizeX, screenSizeY, current.getStats());
      break;
      case DEATH:
      current.draw(g, cam, true);
      uiCon.draw(g, screenSizeX, screenSizeY, current.getStats());
      break;
      case VICTORY:
      current.draw(g, cam, false);
      uiCon.draw(g, screenSizeX, screenSizeY, current.getStats());
      break;
    }
  }

  /**
  * Starts up all the listeners for the window. Only to be called once on startup.
  */
  private void initialiseControls() {

    //Mouse Controls
    f.addMouseMotionListener(new MouseAdapter() {
      @Override
      public void mouseMoved(MouseEvent e) {
        double x = e.getX() - toolBarLeft;
        double y = e.getY() - toolBarTop;
        mousePos = new Vector2(x, y);
        uiCon.hover(x, y);
      }

      @Override
      public void mouseDragged(MouseEvent e) {
        double x = e.getX() - toolBarLeft;
        double y = e.getY() - toolBarTop;
        mousePos = new Vector2(x, y);
        uiCon.hover(x, y);
      }
    });
    f.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        double x = e.getX() - toolBarLeft;
        double y = e.getY() - toolBarTop;
        mousePos = new Vector2(x, y);
        mouseDown[e.getButton()] = true;

        if (e.getButton() == 2) {mousePre = mousePos.copy(); return;}
        if (e.getButton() == 1) {uiCon.press(x, y);}
        if (state != State.RUN) return;
        current.press(new Vector2 ((mousePos.x+cam.conX())/cam.getZoom(), (mousePos.y+cam.conY())/cam.getZoom()));
      }

      @Override
      public void mouseReleased(MouseEvent e){
        double x = e.getX() - toolBarLeft;
        double y = e.getY() - toolBarTop;
        mouseDown[e.getButton()] = false;
        if (e.getButton() == 1) {
          if (!current.click(new Vector2 ((mousePos.x+cam.conX())/cam.getZoom(), (mousePos.y+cam.conY())/cam.getZoom()))) lose();
          if (current.checkWin()) win();
          String button = uiCon.release(x, y);
          activeTextField = null;
          if (button == null) return;
          if (button.equals("UI_Text_Field")) {activeTextField = uiCon.getTextField(x, y); return;}
          input(button);
        }
        else if (e.getButton() == 3) {
          current.flag(new Vector2 ((mousePos.x+cam.conX())/cam.getZoom(), (mousePos.y+cam.conY())/cam.getZoom()));
        }
      }
    });

    f.addMouseWheelListener(new MouseAdapter() {
			public void mouseWheelMoved(MouseWheelEvent e) {
        double z = cam.getZoom();
				z = e.getWheelRotation()<0 ? z*1.5 : z/1.5;
        cam.setZoom(z);
			}
		});

    //Keyboard Controls
    f.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        // System.out.print(keyCode);
        if (keyCode == KeyEvent.VK_F11) {
          doFull();
        }
        if (keyCode == KeyEvent.VK_ESCAPE) {
          uiCon.input("Back");
        }
        if (keyCode == KeyEvent.VK_ENTER) {
          if (ai != null) {
            if (!ai.step()) lose();
            if (current.checkWin()) win();
          }
        }
        if (keyCode == KeyEvent.VK_MINUS) {
          cam.setZoom(cam.getZoom()/2);
        }
        if (keyCode == KeyEvent.VK_EQUALS) {
          cam.setZoom(cam.getZoom()*2);
        }
        keyDown[keyCode] = true;

        if (activeTextField == null || keyDown[KeyEvent.VK_CONTROL]) return;

        if (keyCode >= 32) {activeTextField.print(e.getKeyChar()); return;}
        if (keyCode == KeyEvent.VK_BACK_SPACE) {activeTextField.backspace(); return;}
        if (keyCode == KeyEvent.VK_ENTER) {input(activeTextField.confirm()); return;}
      }

      // @Override
      // public void keyTyped(KeyEvent e) {
      //
      // }

      @Override
      public void keyReleased(KeyEvent e){
        keyDown[e.getKeyCode()] = false;
      }
    });
  }
}
