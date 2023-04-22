package code.core;

import code.ai.MineAI;
// import java.text.SimpleDateFormat;
// import java.util.Date;

import ui.control.UIController;
// import ui.control.UIState;
import ui.control.UIState;
import code.board.Camera;

import java.awt.Graphics;
import java.awt.Graphics2D;

enum State {
  MAINMENU,
  RUN,
  DEATH,
  VICTORY
}

/**
* Core class for minesweeper
*/
public abstract class Core {

  public static final Window WINDOW;
  
  public static final Settings GLOBAL_SETTINGS;

  public static final Settings GAME_SETTINGS;
  
  public static final String BLACKLISTED_CHARS = "/\\.?!*\n";
  
  public static final int DEFAULT_MAP_SIZE = 32;
  
  private static final double TICKS_PER_SECOND = 60;
  private static final double MILLISECONDS_PER_TICK = 1000/TICKS_PER_SECOND;
  
  private static MineAI ai = null;
  
  private static boolean quit = false;
  
  private static Scene currentScene;

  /** Current game state */
  private static State state = State.MAINMENU;

  /**
  * Main method. Called on execution. Performs basic startup
  *
  * @param args Ignored for now
  */
  public static void main(String[] args) {
    toMenu();
    Controls.initialiseControls(WINDOW.FRAME);
    run();
  }
  
  static {
    WINDOW = new Window();

    GLOBAL_SETTINGS = new Settings();

    GAME_SETTINGS = new Settings("lastGame",
      "mapH" , " " + 10    + "\n",
      "mapW" , " " + 10    + "\n",
      "mines", " " + 0.1   + "\n",
      "useAI", " " + false + "\n"
    );
    
    UIController.putPane("Main Menu", UICreator.createMain());
    UIController.putPane("HUD"      , UICreator.createHUD ());
  }
  
  /**
  * @return the currently active scene
  */
  public static Scene getCurrentScene() {
    return currentScene;
  }
  
  /**
  * @return the currently active camera
  */
  public static Camera getActiveCam() {
    return currentScene == null ? null : currentScene.getCam();
  }
  
  public static State getState() {
    return state;
  }

  /**
  * Oh no! You lost
  */
  public static void lose() {
    state = State.DEATH;
    UIController.forceState(UIState.LOSE);
  }

  /**
  * You Win!
  */
  public static void win() {
    state = State.VICTORY;
    UIController.forceState(UIState.WIN);
  }
  
  public static void restart() {
    currentScene.reset(); 
    state = State.RUN; 
    UIController.forceState(UIState.DEFAULT);
  }

  public static void newGame() {
    currentScene = new Scene();
    state = State.RUN;
    UIController.setCurrentPane("HUD");
    ai = GAME_SETTINGS.getBoolSetting("useAI")
       ? new MineAI(currentScene, false)
       : null;
  }

  public static void toMenu() {
    currentScene = Scene.MENU_SCENE;
    state = State.MAINMENU;
    UIController.setCurrentPane("Main Menu");
    ai = null;
  }
  
  /**
  * Sets a flag to close the program at the nearest convenience
  */
  public static void quitToDesk() {
    quit = true;
  }

  /**
  * Main loop. Should always be running. Runs the rest of the game engine
  */
  public static void run() {
    while (true) {
      long tickTime = System.currentTimeMillis();

      switch (state) {
        case MAINMENU:
        break;
        case RUN:
        if (ai != null && !currentScene.hasPressedTile()) {
          if (!ai.step()) lose();
          if (currentScene.checkWin()) win();
        }
        Controls.cameraMovement();
        break;
        case DEATH:
        // input("Restart");
        case VICTORY:
        Controls.cameraMovement();
        break;
      }
      
      if (quit) System.exit(0);

      WINDOW.PANEL.repaint();
      tickTime = System.currentTimeMillis() - tickTime;
      try {
        Thread.sleep(Math.max((long)(MILLISECONDS_PER_TICK - tickTime), 0));
      } catch(InterruptedException e){System.out.println(e); System.exit(0);}
    }
  }

  public static void paintComponent(Graphics gra) {
    Graphics2D g = (Graphics2D) gra;

    switch (state) {
      case MAINMENU:
      if (currentScene != null) {currentScene.draw(g, false);}
      break;
      case RUN:
      currentScene.draw(g, false);
      break;
      case DEATH:
      currentScene.draw(g, true);
      break;
      case VICTORY:
      currentScene.draw(g, false);
      break;
    }
    UIController.draw(g, WINDOW.screenWidth(), WINDOW.screenHeight());
  }
}
