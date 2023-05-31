package code.core;

import mki.math.vector.Vector2;

import mki.ui.control.UIColours;
import mki.ui.control.UIController;
import mki.ui.control.UIHelp;
import mki.ui.control.UIPane;
import mki.ui.control.UIState;
import mki.ui.components.UIComponent;
import mki.ui.components.UIInteractable;
import mki.ui.components.UIText;
import mki.ui.components.interactables.*;
import mki.ui.elements.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

class UICreator {
  // private static final UIElement VIRTUAL_KEYBOARD = new ElemKeyboard();
  
  private static final double COMPON_HEIGHT = 0.075;
  private static final double BUFFER_HEIGHT = 0.015;
  
  /**
  * Creates the UI pane for the main menu.
  */
  public static UIPane createMain() {
    UIPane mainMenu = new UIPane();
    
    UIElement title = new UIElement(
    new Vector2(0   , 0),
    new Vector2(0.34, 0.14),
    new boolean[]{true, false, true, false}
    ){
      protected void init() {components = new UIComponent[]{new UIText("Minesweeper", 0.6, Font.BOLD)};}
      protected void draw(Graphics2D g, int screenSizeY, Vector2 tL, Vector2 bR, Color[] c, UIInteractable highlighted) {
        components[0].draw(g, (float)tL.x, (float)tL.y, (float)(bR.x-tL.x), (float)(bR.y-tL.y), c[UIColours.TEXT]);
      }
    };
    
    UIElement outPanel = leftMenu(
      new Vector2(0, 0.28), 
      0.2, 
      new UIButton("Play",       () -> UIController.setState(UIState.NEW_GAME)),
      new UIButton("Quick Game", () -> {Core.GAME_SETTINGS.revertChanges(); Core.newGame();}),
      new UIButton("Quit to Desktop", Core::quitToDesk)
    );
    
    UIElement newGame = leftMenu(
      new Vector2(0, 0.28), 
      0.2,
      new UISlider.Integer(
        "Map Width: %.0f", 
        () -> Core.GAME_SETTINGS.getIntSetting("mapW"), 
        (i) -> Core.GAME_SETTINGS.setIntSetting("mapW", i), 
        10, 
        100
      ),
      new UISlider.Integer(
        "Map Height: %.0f", 
        () -> Core.GAME_SETTINGS.getIntSetting("mapH"), 
        (i) -> Core.GAME_SETTINGS.setIntSetting("mapH", i), 
        10, 
        100
      ),
      new UISlider.Float(
        "Num Mines: %.2f", 
        () -> (float)Core.GAME_SETTINGS.getDoubleSetting("mines"), 
        (f) -> Core.GAME_SETTINGS.setDoubleSetting("mines", f), 
        0.1f,
        0.5f,
        0.01f
      ),
      new UIToggle("Use AI?", () -> Core.GAME_SETTINGS.getBoolSetting("useAI"), (b)->Core.GAME_SETTINGS.setBoolSetting("useAI", b)),
      new UIButton("BEGIN!", () -> {Core.GAME_SETTINGS.saveChanges(); Core.newGame();})
    );
    
    mainMenu.addState(UIState.DEFAULT,  title   );
    mainMenu.addState(UIState.DEFAULT,  outPanel);
    mainMenu.addState(UIState.NEW_GAME, title    , UIState.DEFAULT);
    mainMenu.addState(UIState.NEW_GAME, newGame );
    
    mainMenu.clear();
    
    return mainMenu;
  }
  
  /**
  * Creates the HUD for use during gameplay.
  */
  public static UIPane createHUD() {
    UIPane HUD = new UIPane();
    
    UIElement greyed = new UIElement(
    new Vector2(0,0),
    new Vector2(1, 1),
    new boolean[]{false, false, false, false}
    ){
      protected void init() {this.backgroundColour = UIColours.SCREEN_TINT;}
      protected void draw(Graphics2D g, int screenSizeY, Vector2 tL, Vector2 bR, Color[] c, UIInteractable highlighted) {}
    };

    UIElement outPause = leftMenu(
      new Vector2(0, 0.7), 
      0.2, 
      new UIButton("Restart"        , Core::restart),
      new UIButton("Quit to Menu"   , Core::toMenu),
      new UIButton("Quit to Desktop", Core::quitToDesk)
    );

    UIElement win = centreMenu(
      new Vector2(0.5, 0.5), 
      0.2, 
      new UIText  ("You Win!"    , 1, Font.BOLD),
      new UIButton("Restart"     , Core::restart),
      new UIButton("Quit to Menu", Core::toMenu)
    );

    UIElement lose = centreMenu(
      new Vector2(0.5, 0.5), 
      0.2, 
      new UIText  ("You Lose!"    , 1, Font.BOLD),
      new UIButton("Restart"     , Core::restart),
      new UIButton("Quit to Menu", Core::toMenu)
    );

    HUD.addState(UIState.DEFAULT, outPause);
    HUD.addState(UIState.WIN, greyed, UIState.DEFAULT);
    HUD.addState(UIState.WIN, win);
    HUD.addState(UIState.LOSE, greyed, UIState.DEFAULT);
    HUD.addState(UIState.LOSE, lose);
    
    HUD.clear();
    
    return HUD;
  }



  private static UIElement leftMenu(Vector2 tl, double w, UIComponent... comps) {
    return new ElemList(
      tl,
      tl.add(w, UIHelp.calculateListHeight(BUFFER_HEIGHT, UIHelp.calculateComponentHeights(COMPON_HEIGHT, comps))),
      COMPON_HEIGHT,
      BUFFER_HEIGHT,
      comps,
      new boolean[]{false, false, true, false}
    );
  }

  private static UIElement centreMenu(Vector2 c, double w, UIComponent... comps) {
    double h = UIHelp.calculateListHeight(BUFFER_HEIGHT, UIHelp.calculateComponentHeights(COMPON_HEIGHT, comps));
    Vector2 tl = new Vector2(0.5 - w/2, 0.5 - h/2);
    return new ElemList(
      tl,
      tl.add(w, h),
      COMPON_HEIGHT,
      BUFFER_HEIGHT,
      comps,
      new boolean[]{false, true, true, true}
    );
  }
}
