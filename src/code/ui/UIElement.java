package code.ui;

import code.math.Vector2;
// import code.math.MathHelp;

import java.awt.Graphics2D;
// import java.awt.geom.Rectangle2D;

import java.awt.Color;
// import java.awt.Font;
// import java.awt.FontMetrics;

/**
* Write a description of class Element here.
*
* @author (your name)
* @version (a version number or a date)
*/
public class UIElement {
  protected Vector2 topLeft;
  protected Vector2 botRight;

  protected double fadeDist = 0.08;
  protected double fadeCount = 0;
  protected double animTimeMillis = 0;
  protected long startTimeMillis = System.currentTimeMillis();

  protected boolean topTied;
  protected boolean botTied;
  protected boolean leftTied;
  protected boolean rightTied;

  protected Color bgCol;

  protected boolean active = false;
  protected boolean transIn = false;
  protected boolean transOut = false;

  /**
  * Gets this element type
  *
  * @return the type of element this is
  */
  public String getType() {
    return "Ah";
  }

  /**
  * Gets the active state of this element
  *
  * @return true if the element is active
  */
  public boolean isActive() {
    return !transOut && active && !transIn;
  }

  /**
  * Gets the transition state of this element
  *
  * @return true if the element is transitioning
  */
  public boolean isTrans() {
    return transOut || transIn;
  }

  /**
  * Tells the element to transition out if it is not already doing so
  */
  public void transOut() {
    if (!transOut) startTimeMillis = System.currentTimeMillis();
    transOut = active;
  }

  /**
  * Tells the element to transition in if it is not already doing so
  */
  public void transIn() {
    if (!transIn) startTimeMillis = System.currentTimeMillis();
    transIn = !active;
  }

  /**
  * toggles the state of this element
  *
  * @return true if the element is now transitioning in
  */
  public boolean toggle() {
    transIn = !active;
    transOut = active;
    startTimeMillis = System.currentTimeMillis();
    return !active;
  }

  /**
  * checks to see if the cursor is hovering over a feature of this element
  *
  * @param x The x coord of the cursor
  * @param y The y coord of the cursor
  */
  public void hover(double x, double y) {

  }

  /**
  * presses down any features currently targeted by the cursor
  *
  * @param x The x coord of the cursor
  * @param y The y coord of the cursor
  */
  public void press(double x, double y) {

  }

  /**
  * activates any features currently pressed down by the cursor
  *
  * @param x The x coord of the cursor
  * @param y The y coord of the cursor
  */
  public String release(double x, double y) {
    return null;
  }

  /**
  * retrieves the text field at a given position
  *
  * @param x The x coord of the given position
  * @param y The y coord of the given position
  */
  public UITextfield getTextField(double x, double y) {
    return null;
  }

  /**
  * draws the current element
  *
  * @param g The Graphics2D object to draw to
  * @param UIscale The scale to magnify the UI to
  * @param screenSizeX The length of the screen
  * @param screenSizeY The height of the screen
  * @param playerStats The stats of the player to draw onto the screen
  */
  public void draw(Graphics2D g, double UIscale, int screenSizeX, int screenSizeY, int[] playerStats) {

  }
}
