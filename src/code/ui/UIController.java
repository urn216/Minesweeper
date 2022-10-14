package code.ui;

import code.core.Core;

import code.math.Vector2;

import code.ui.elements.*;

import java.util.*;
import java.awt.Graphics2D;

import java.awt.Color;
import java.awt.Font;

/**
* UI class
*/
public class UIController {

  private static final String[] returners = {
    "Back",
    "Cancel",
    "Resume"
  };

  private HashMap<String, UIPane> panes;
  private UIPane current;

  public UIController() {
    panes = new HashMap<String, UIPane>();
    panes.put("Main Menu", UICreator.createMain()); //get rid of these. Put them somewhere else to make this a self-contained package
    panes.put("HUD", UICreator.createHUD());
  }

  public UIPane getPane(String name) {
    return panes.get(name);
  }

  public UIPane setCurrent(String name) {
    current = panes.get(name);
    current.reset();
    return current;
  }

  public void setMode(String name) {
    current.setMode(name);
  }

  public String getMode() {
    return current.getMode();
  }

  public void transOut() {
    current.transOut();
  }

  public void transIn() {
    current.transIn();
  }

  public boolean isTrans() {
    return current.isTrans();
  }

  public void hover(double x, double y) {
    current.hover(x, y);
  }

  public void press(double x, double y) {
    current.press(x, y);
  }

  public String release(double x, double y) {
    String name = current.release(x, y);
    if (name != null) {input(name);}
    return name;
  }

  public void input(String button) {
    for (String parent : returners) {
      if (button.equals(parent)) {
        current.retMode();
        return;
      }
    }
    current.setMode(button);
  }

  public UITextfield getTextField(double x, double y) {
    return current.getTextField(x, y);
  }

  public void draw(Graphics2D g, int screenSizeX, int screenSizeY, int[] playerStats) {
    if (current != null) {current.draw(g, screenSizeY/Core.DEFAULT_SCREEN_SIZE.y, screenSizeX, screenSizeY, playerStats);}
  }

  public void draw(Graphics2D g, int screenSizeX, int screenSizeY) {
    if (current != null) {current.draw(g, screenSizeY/Core.DEFAULT_SCREEN_SIZE.y, screenSizeX, screenSizeY);}
  }
}

class UICreator {
  /**
  * Creates the UI pane for the main menu.
  */
  protected static UIPane createMain() {
    UIPane mainMenu = new UIPane();
    boolean[] Tties = {true, false, true, false};
    UIElement title = new ElemTitle(
    new Vector2(0, 0),
    new Vector2(0.28, 0.14),
    "Minesweeper",
    Font.BOLD,
    75,
    ColourPack.DEFAULT_BACKGROUND,
    ColourPack.DEFAULT_BUTTON_OUT_ACC,
    Tties
    );
    boolean[] Bties = {false, false, true, false};
    String[] names = {"Play", "Quit to Desktop"};
    UIElement outPanel = new ElemButtons(
    new Vector2(0, 0.28),
    new Vector2(0.24, 0.5),
    15,
    ColourPack.DEFAULT_BACKGROUND,
    ColourPack.DEFAULT_BUTTON_OUT_ACC,
    ColourPack.DEFAULT_BUTTON_HOVER,
    ColourPack.DEFAULT_BUTTON_BACKGROUND,
    ColourPack.DEFAULT_BUTTON_IN_ACC,
    ColourPack.DEFAULT_BUTTON_LOCKED,
    names,
    Bties
    );
    mainMenu.addElement(title);
    mainMenu.addElement(outPanel);
    mainMenu.addMode("Default", title);
    mainMenu.addMode("Default", outPanel);

    return mainMenu;
  }

  /**
  * Creates the HUD for use during gameplay.
  */
  protected static UIPane createHUD() {
    UIPane HUD = new UIPane();
    boolean[] hTies = {false, true, false, true};
    int[] hI = {0, 1};
    UIElement stats = new ElemInfo(
    new Vector2(0.875, 0.8),
    new Vector2(1, 1),
    0,
    hI,
    Font.BOLD,
    30,
    ColourPack.DEFAULT_BACKGROUND,
    ColourPack.DEFAULT_BUTTON_OUT_ACC,
    hTies
    );
    boolean[] Bties = {false, true, true, false};
    String[] names = {"Restart", "Quit to Title", "Quit to Desktop"};
    UIElement outPause = new ElemButtons(
    new Vector2(0, 0.8),
    new Vector2(0.125, 1),
    15,
    ColourPack.DEFAULT_BACKGROUND,
    ColourPack.DEFAULT_BUTTON_OUT_ACC,
    ColourPack.DEFAULT_BUTTON_HOVER,
    ColourPack.DEFAULT_BUTTON_BACKGROUND,
    ColourPack.DEFAULT_BUTTON_IN_ACC,
    ColourPack.DEFAULT_BUTTON_LOCKED,
    names,
    Bties
    );
    boolean[] Tties = {false, true, true, true};
    UIElement victory = new ElemTitle(
    new Vector2(0.36, 0.43),
    new Vector2(0.64, 0.57),
    "You Win!",
    Font.BOLD,
    75,
    ColourPack.DEFAULT_BACKGROUND,
    ColourPack.DEFAULT_BUTTON_OUT_ACC,
    Tties
    );
    UIElement defeat = new ElemTitle(
    new Vector2(0.36, 0.43),
    new Vector2(0.64, 0.57),
    "You Lose!",
    Font.BOLD,
    75,
    ColourPack.DEFAULT_BACKGROUND,
    ColourPack.DEFAULT_BUTTON_OUT_ACC,
    Tties
    );
    HUD.addElement(stats);
    HUD.addElement(outPause);
    HUD.addElement(victory);
    HUD.addElement(defeat);
    HUD.addMode("Default", stats);
    HUD.addMode("Default", outPause);
    HUD.addMode("Victory", stats);
    HUD.addMode("Victory", outPause);
    HUD.addMode("Victory", victory);
    HUD.addMode("Defeat", stats);
    HUD.addMode("Defeat", outPause);
    HUD.addMode("Defeat", defeat);

    return HUD;
  }
}

class ColourPack {
  public static final Color DEFAULT_BACKGROUND = new Color(100, 100, 100, 156);
  public static final Color DEFAULT_SCREEN_TINT = new Color(50, 50, 50, 156);
  public static final Color DEFAULT_BUTTON_OUT_ACC = new Color(200, 200, 200);
  public static final Color DEFAULT_BUTTON_BACKGROUND = new Color(160, 160, 160, 160);
  public static final Color DEFAULT_BUTTON_IN_ACC = new Color(0, 255, 255);
  public static final Color DEFAULT_BUTTON_LOCKED = new Color(180, 180, 180);
  public static final Color DEFAULT_BUTTON_HOVER = new Color(0, 180, 180);
}
