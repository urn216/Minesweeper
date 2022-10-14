package code.ui.elements;

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
public class ElemPlain extends UIElement {

  /**
  * Plain box element
  *
  * @param tL The percent inwards from the top left corner of the screen for the top left corner of this element
  * @param bR The percent inwards from the top left corner of the screen for the bottom right corner of this element
  * @param bg The colour of this element
  * @param ties Determines which directions should be faded from/towards in transitions
  */
  public ElemPlain(Vector2 tL, Vector2 bR, Color bg, boolean[] ties) {
    assert(ties.length==4);
    topLeft = tL;
    botRight = bR;
    bgCol = bg;
    topTied = ties[0];
    botTied = ties[1];
    leftTied = ties[2];
    rightTied = ties[3];
  }

  @Override
  public String getType() {
    return "PLAIN";
  }

  @Override
  public void draw(Graphics2D g, double UIscale, int screenSizeX, int screenSizeY, int[] playerStats) {
    if (!active && !transIn) {return;}
    double fadeDist = this.fadeDist*screenSizeY;
    Color bg = bgCol;
    if (transIn) {
      if (fadeCount >= fadeDist) {transIn = false; active = true; fadeCount = 0;}
      else {
        fadeCount = Math.min(fadeDist, MathHelp.lerp(0, fadeDist, (System.currentTimeMillis()-startTimeMillis)/animTimeMillis));
        bg = new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), (int)(bg.getAlpha()*(fadeCount/fadeDist)));
      }
    }
    else if (transOut) {
      if (fadeCount >= fadeDist) {transOut = false; active = false; fadeCount = 0; return;}
      else {
        fadeCount = Math.min(fadeDist, MathHelp.lerp(0, fadeDist, (System.currentTimeMillis()-startTimeMillis)/animTimeMillis));
        bg = new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), (int)(bg.getAlpha()*(1-fadeCount/fadeDist)));
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

  }
}
