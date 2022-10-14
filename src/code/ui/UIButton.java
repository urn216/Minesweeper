package code.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/**
* Class for making functional Buttons
*/
public class UIButton
{

  protected String name;
  protected boolean in = false;
  protected boolean locked = false;
  protected boolean highlighted = false;

  protected FontMetrics metrics;

  protected double x = 0;
  protected double y = 0;
  protected double width = 0;
  protected double height = 0;

  /**
  * Constructor for Buttons
  */
  public UIButton(String name)
  {
    this.name = name;
  }

  public String setName(String name) {
    this.name = name;
    return this.name;
  }

  public String getName() {
    return name;
  }

  public void setHov() {highlighted = true;}

  public void setUnHov() {highlighted = false;}

  public void setIn() {in = true;}

  public void setOut() {in = false;}

  public void lock() {locked = true;}

  public void unlock() {locked = false;}

  public boolean isIn() {return in;}

  public boolean touching(double oX, double oY) {
    if (oX > x && oX < x+width && oY > y && oY < y+height) {
      return true;
    }
    return false;
  }

  public void draw(Graphics2D g, float x, float y, float width, float height, double UIscale, Color hovCol, Color outCol, Color accCol, Color inCol, Color lockCol) {
    // Font font = new Font("Copperplate", Font.BOLD, (int) Math.round((18*UIscale)));
    Font font = new Font("Copperplate", Font.BOLD, Math.min((int) Math.round(height/2), (int) Math.round(2*width/name.length())));
    metrics = g.getFontMetrics(font);
    g.setFont(font);
    if (locked) {
      drawLocked(g, x, y, width, height, lockCol, accCol);
    }
    else if (in) {
      drawIn(g, x, y, width, height, accCol, inCol);
    }
    else if (highlighted) {
      drawHov(g, x, y, width, height, outCol, hovCol);
    }
    else {
      drawOut(g, x, y, width, height, outCol, accCol);
    }
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }

  public void drawOut(Graphics2D g, float x, float y, float width, float height, Color outCol, Color accCol) {
    g.setColor(outCol);
    g.fill(new Rectangle2D.Double(x, y, width, height));
    g.setColor(accCol);
    g.draw(new Rectangle2D.Double(x, y, width, height));
    g.drawString(name, x+(width-metrics.stringWidth(name))/2, y+((height - metrics.getHeight())/2) + metrics.getAscent());
  }

  public void drawHov(Graphics2D g, float x, float y, float width, float height, Color outCol, Color accCol) {
    g.setColor(outCol);
    g.fill(new Rectangle2D.Double(x, y, width, height));
    g.setColor(accCol);
    g.draw(new Rectangle2D.Double(x, y, width, height));
    g.drawString(name, x+(width-metrics.stringWidth(name))/2, y+((height - metrics.getHeight())/2) + metrics.getAscent());
  }

  public void drawIn(Graphics2D g, float x, float y, float width, float height, Color inCol, Color accCol) {
    g.setColor(inCol);
    g.fill(new Rectangle2D.Double(x+2, y+2, width-2, height-2));
    g.setColor(accCol);
    g.draw(new Rectangle2D.Double(x, y, width, height));
    g.drawString(name, x+2+(width-metrics.stringWidth(name))/2, y+2+((height - metrics.getHeight())/2) + metrics.getAscent());
  }

  public void drawLocked(Graphics2D g, float x, float y, float width, float height, Color lockCol, Color accCol) {
    g.setColor(lockCol);
    g.fill(new Rectangle2D.Double(x, y, width, height));
    g.setColor(accCol);
    g.draw(new Rectangle2D.Double(x, y, width, height));
    g.drawString(name, x+(width-metrics.stringWidth(name))/2, y+((height - metrics.getHeight())/2) + metrics.getAscent());
  }
}
