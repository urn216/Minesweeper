package code.ui.elements;

import code.ui.UIButton;
import code.ui.UIElement;

import code.math.Vector2;
import code.math.MathHelp;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import java.awt.Color;

/**
* Write a description of class Element here.
*
* @author (your name)
* @version (a version number or a date)
*/
public class ElemButtons extends UIElement {

  private double buffer;

  private Color accCol;
  private Color buthovCol;
  private Color butoutCol;
  private Color butinCol;
  private Color butlockCol;

  protected UIButton[] buttons;

  /**
  * Vertical Button box element
  *
  * @param tL The percent inwards from the top left corner of the screen for the top left corner of this element
  * @param bR The percent inwards from the top left corner of the screen for the bottom right corner of this element
  * @param buff The amount of buffer space between buttons
  * @param bg The colour of this element's background
  * @param acc The colour of this element's text
  * @param hov The colour of this element's hovered text
  * @param out The colour of this element's button boxes
  * @param in The colour of this element's pressed in button boxes
  * @param lock The colour of this element's text when the button is locked
  * @param bNames an array containing all the buttons to have in the column
  * @param ties Determines which directions should be faded from/towards in transitions
  */
  public ElemButtons(Vector2 tL, Vector2 bR, double buff, Color bg, Color acc, Color hov, Color out, Color in, Color lock, String[] bNames, boolean[] ties) {
    assert(ties.length==4);
    topLeft = tL;
    botRight = bR;
    buffer = buff;
    bgCol = bg;
    accCol = acc;
    buthovCol = hov;
    butoutCol = out;
    butinCol = in;
    butlockCol = lock;
    buttons = new UIButton[bNames.length];
    for (int i=0; i<bNames.length; i++) {
      buttons[i] = new UIButton(bNames[i]);
    }
    topTied = ties[0];
    botTied = ties[1];
    leftTied = ties[2];
    rightTied = ties[3];
  }

  @Override
  public String getType() {
    return "BUTTONS";
  }

  @Override
  public void transOut() {
    for (UIButton b : buttons) b.setUnHov();
    if (!transOut) startTimeMillis = System.currentTimeMillis();
    transOut = active;
  }

  @Override
  public void hover(double x, double y) {
    for (UIButton b : buttons) {
      if (b.touching(x, y)) {
        b.setHov();
      }
      else {
        b.setUnHov();
      }
    }
  }

  @Override
  public void press(double x, double y) {
    for (UIButton b : buttons) {
      if (b.touching(x, y)) {
        b.setIn();
      }
      else {
        b.setOut();
      }
    }
  }

  @Override
  public String release(double x, double y) {
    String name = null;
    for (UIButton b : buttons) {
      if (b.touching(x, y) && b.isIn()) {
        name = b.getName();
      }
      b.setOut();
    }
    return name;
  }

  @Override
  public void draw(Graphics2D g, double UIscale, int screenSizeX, int screenSizeY, int[] playerStats) {
    if (!active && !transIn) {return;}
    double fadeDist = this.fadeDist*screenSizeY;
    Color bg = bgCol;
    Color acc = accCol;
    Color out = butoutCol;
    if (transIn) {
      if (fadeCount >= fadeDist) {transIn = false; active = true; fadeCount = 0;}
      else {
        fadeCount = Math.min(fadeDist, MathHelp.lerp(0, fadeDist, (System.currentTimeMillis()-startTimeMillis)/animTimeMillis));
        bg = new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), (int)(bg.getAlpha()*(fadeCount/fadeDist)));
        acc = new Color(acc.getRed(), acc.getGreen(), acc.getBlue(), (int)(acc.getAlpha()*(fadeCount/fadeDist)));
        out = new Color(out.getRed(), out.getGreen(), out.getBlue(), (int)(out.getAlpha()*(fadeCount/fadeDist)));
      }
    }
    else if (transOut) {
      if (fadeCount >= fadeDist) {transOut = false; active = false; fadeCount = 0; return;}
      else {
        fadeCount = Math.min(fadeDist, MathHelp.lerp(0, fadeDist, (System.currentTimeMillis()-startTimeMillis)/animTimeMillis));
        bg = new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), (int)(bg.getAlpha()*(1-fadeCount/fadeDist)));
        acc = new Color(acc.getRed(), acc.getGreen(), acc.getBlue(), (int)(acc.getAlpha()*(1-fadeCount/fadeDist)));
        out = new Color(out.getRed(), out.getGreen(), out.getBlue(), (int)(out.getAlpha()*(1-fadeCount/fadeDist)));
      }
    }
    Vector2 tL = new Vector2(topLeft.x*screenSizeX, topLeft.y*screenSizeY);
    Vector2 bR = new Vector2(botRight.x*screenSizeX, botRight.y*screenSizeY);
    if (transIn) {
      if (leftTied&&rightTied) {tL.x -= fadeDist-fadeCount; bR.x += fadeDist-fadeCount;}
      else if (leftTied) {tL.x -= fadeDist-fadeCount; bR.x -= fadeDist-fadeCount;}
      else if (rightTied) {tL.x += fadeDist-fadeCount; bR.x += fadeDist-fadeCount;}
      if (topTied&&botTied) {tL.y -= fadeDist-fadeCount; bR.y += fadeDist-fadeCount;}
      else if (topTied) {tL.y -= fadeDist-fadeCount; bR.y -= fadeDist-fadeCount;}
      else if (botTied) {tL.y += fadeDist-fadeCount; bR.y += fadeDist-fadeCount;}
    }
    else if (transOut) {
      if (leftTied&&rightTied) {tL.x -= fadeCount; bR.x += fadeCount;}
      else if (leftTied) {tL.x -= fadeCount; bR.x -= fadeCount;}
      else if (rightTied) {tL.x += fadeCount; bR.x += fadeCount;}
      if (topTied&&botTied) {tL.y -= fadeCount; bR.y += fadeCount;}
      else if (topTied) {tL.y -= fadeCount; bR.y -= fadeCount;}
      else if (botTied) {tL.y += fadeCount; bR.y += fadeCount;}
    }
    g.setColor(bg);
    g.fill(new Rectangle2D.Double(tL.x, tL.y, bR.x-tL.x, bR.y-tL.y));
    float buff = (float) (buffer*UIscale);

    float x = (float) tL.x + buff;
    float y = (float) tL.y + buff;
    float width = (float) bR.x - buff - x;
    float height = (float) (bR.y-y)/(buttons.length)-buff;
    for (int i=0; i<buttons.length; i++) {
      buttons[i].draw(g, x, y+(buff+height)*i, width, height, UIscale, buthovCol, out, acc, butinCol, butlockCol);
    }
  }
}
