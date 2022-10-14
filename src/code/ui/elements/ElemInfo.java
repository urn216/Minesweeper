package code.ui.elements;

import code.ui.UIElement;

import code.math.Vector2;
import code.math.MathHelp;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;

/**
* Write a description of class Element here.
*
* @author (your name)
* @version (a version number or a date)
*/
public class ElemInfo extends UIElement {

  private double buffer;

  private String title;

  private String[] text = {"Mines Left: ", "Checked: ", "Pos Y: ", "Vel X: ", "Vel Y: ", "FPS: "};
  private int[] indices;

  private int fontStyle;
  private int fontSize;

  private Color accCol;

  /**
  * Text box element
  *
  * @param tL The percent inwards from the top left corner of the screen for the top left corner of this element
  * @param bR The percent inwards from the top left corner of the screen for the bottom right corner of this element
  * @param buff The amount of buffer space between buttons
  * @param names The text that should appear on each row of this element
  * @param ind The information indices to use for each row of this element
  * @param style The Font style the text should adopt
  * @param size The Font size the text should adopt
  * @param bg The colour of this element's background
  * @param acc The colour of this element's text
  * @param ties Determines which directions should be faded from/towards in transitions
  */
  public ElemInfo(Vector2 tL, Vector2 bR, double buff, int[] ind, int style, int size, Color bg, Color acc, boolean[] ties) {
    assert(ties.length==4);
    topLeft = tL;
    botRight = bR;
    buffer = buff;
    this.indices = ind;
    this.fontStyle = style;
    this.fontSize = size;
    bgCol = bg;
    accCol = acc;
    indices = ind;
    topTied = ties[0];
    botTied = ties[1];
    leftTied = ties[2];
    rightTied = ties[3];
  }

  @Override
  public String getType() {
    return "INFO";
  }

  @Override
  public void draw(Graphics2D g, double UIscale, int screenSizeX, int screenSizeY, int[] playerStats) {
    if (!active && !transIn) {return;}
    double fadeDist = this.fadeDist*screenSizeY;
    Color bg = bgCol;
    Color acc = accCol;
    if (transIn) {
      if (fadeCount >= fadeDist) {transIn = false; active = true; fadeCount = 0;}
      else {
        fadeCount = Math.min(fadeDist, MathHelp.lerp(0, fadeDist, (System.currentTimeMillis()-startTimeMillis)/animTimeMillis));
        bg = new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), (int)(bg.getAlpha()*(fadeCount/fadeDist)));
        acc = new Color(acc.getRed(), acc.getGreen(), acc.getBlue(), (int)(acc.getAlpha()*(fadeCount/fadeDist)));
      }
    }
    else if (transOut) {
      if (fadeCount >= fadeDist) {transOut = false; active = false; fadeCount = 0; return;}
      else {
        fadeCount = Math.min(fadeDist, MathHelp.lerp(0, fadeDist, (System.currentTimeMillis()-startTimeMillis)/animTimeMillis));
        bg = new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), (int)(bg.getAlpha()*(1-fadeCount/fadeDist)));
        acc = new Color(acc.getRed(), acc.getGreen(), acc.getBlue(), (int)(acc.getAlpha()*(1-fadeCount/fadeDist)));
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
    Font font = new Font("Copperplate", fontStyle, (int) Math.round(fontSize*UIscale));
    FontMetrics metrics = g.getFontMetrics(font);
    float buff = (float) (buffer*UIscale);

    g.setFont(font);
    g.setColor(acc);
    float tp = (float) (tL.y + buff);
    float ht = (float) ((bR.y-tp)/(indices.length)-buff);
    for (int i=0; i<indices.length; i++) {
      int ind = indices[i];
      title = text[ind]+playerStats[ind];
      g.drawString(title, (float) (tL.x + (bR.x-tL.x-metrics.stringWidth(title))/2), (float) (tp + (buff+ht)*i + ((ht - metrics.getHeight())/2) + metrics.getAscent()));
    }
  }
}
