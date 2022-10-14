package code.ui;

// import code.ui.UIButton;

import java.awt.Color;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/**
* Class for making functional Buttons
*/
public class UITextfield extends UIButton
{
  private final String confirmation;

  private char[] text;
  private String string = "";
  private int ind = 0;

  /**
  * Constructor for Text Fields
  */
  public UITextfield(int maxLength, String confirmation) {
    super("UI_Text_Field");
    text = new char[maxLength];
    this.confirmation = confirmation;
  }

  public String confirm() {
    return confirmation;
  }

  public String getText() {
    string = "";
    for (char c : text) {
      if (c == '\u0000') return string;
      string+=c;
    }
    return string;
  }

  public boolean checkValid(String check) {
    if (text[0]=='\u0000') return false;
    char[] checker = check.toCharArray();
    for (char cc : checker) {
      for (char tc : text) {
        if (tc == cc) {return false;}
      }
    }
    return true;
  }

  public void print(char c) {
    if (ind>=text.length) return;
    System.out.print(c);
    text[ind] = c;
    ind++;
    getText();
  }

  public void backspace() {
    if (ind==0) return;
    ind--;
    text[ind] = '\u0000';
    getText();
  }

  public void reset() {
    string = "";
    text = new char[text.length];
    ind = 0;
  }

  @Override
  public void drawOut(Graphics2D g, float x, float y, float width, float height, Color outCol, Color accCol) {
    g.setColor(outCol);
    g.fill(new Rectangle2D.Double(x, y, width, height));
    g.setColor(accCol);
    g.draw(new Rectangle2D.Double(x, y, width, height));
    g.drawString(string, x+width/32, y+((height - metrics.getHeight())/2) + metrics.getAscent());
  }

  @Override
  public void drawHov(Graphics2D g, float x, float y, float width, float height, Color outCol, Color accCol) {
    g.setColor(outCol);
    g.fill(new Rectangle2D.Double(x, y, width, height));
    g.setColor(accCol);
    g.draw(new Rectangle2D.Double(x, y, width, height));
    g.drawString(string, x+width/32, y+((height - metrics.getHeight())/2) + metrics.getAscent());
  }

  @Override
  public void drawIn(Graphics2D g, float x, float y, float width, float height, Color inCol, Color accCol) {
    g.setColor(inCol);
    g.fill(new Rectangle2D.Double(x+2, y+2, width-2, height-2));
    g.setColor(accCol);
    g.draw(new Rectangle2D.Double(x, y, width, height));
    g.drawString(string, x+2+width/32, y+2+((height - metrics.getHeight())/2) + metrics.getAscent());
  }

  @Override
  public void drawLocked(Graphics2D g, float x, float y, float width, float height, Color lockCol, Color accCol) {
    g.setColor(lockCol);
    g.fill(new Rectangle2D.Double(x, y, width, height));
    g.setColor(accCol);
    g.draw(new Rectangle2D.Double(x, y, width, height));
    g.drawString(string, x+width/32, y+((height - metrics.getHeight())/2) + metrics.getAscent());
  }
}
