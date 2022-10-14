package code.ui;

// import code.math.Vector2;

import java.util.*;
import java.awt.Graphics2D;

// import java.awt.Color;
//import java.awt.Font;

/**
* Write a description of class Pane here.
*
* @author (your name)
* @version (a version number or a date)
*/
public class UIPane {
  private List<UIElement> elements;
  private Map<String, Mode> modes;
  private String current;

  private UIElement lastE = null;

  /**
  * Constructor for objects of class Pane
  */
  public UIPane() {
    elements = new ArrayList<UIElement>();
    modes = new HashMap<String, Mode>();
    modes.put("Default", new Mode(new ArrayList<UIElement>()));
    // current = "Default";
  }

  public void reset() {
    current = null;
    setMode("Default");
  }

  public UIElement getPane(int i) {
    return elements.get(i);
  }

  public void addElement(UIElement e) {
    elements.add(e);
  }

  // public void addElement(Vector2 tL, Vector2 bR, Color bg, boolean[] ties) {
  //   elements.add(new UIElement(tL, bR, bg, ties));
  // }

  // public void addElement(Vector2 tL, Vector2 bR, String title, int style, int size, Color bg, Color acc, boolean[] ties) {
  //   elements.add(new UIElement(tL, bR, title, style, size, bg, acc, ties));
  // }

  // public void addElement(Vector2 tL, Vector2 bR, double buff, Color bg, Color acc, Color hov, Color out, Color in, Color lock, String[] bNames, boolean[] ties) {
  //   elements.add(new UIElement(tL, bR, buff, bg, acc, hov, out, in, lock, bNames, ties));
  // }

  public void transOut() {
    for (UIElement e : elements) {
      e.transOut();
    }
  }

  public void transIn() {
    for (UIElement e : elements) {
      e.transIn();
    }
  }

  public boolean isTrans() {
    for (UIElement e : elements) {
      if (e.isTrans()) return true;
    }
    return false;
  }

  public void retMode() {
    setMode(modes.get(current).getParent());
  }

  public void setMode(String name) {
    if (isTrans()
    ||  name == null
    ||  modes.get(name) == null
    ||  name.equals(current)) return;

    Mode mode = modes.get(name);
    current = name;
    for (UIElement e : elements) {
      if (mode.contains(e)) {e.transIn();}
      else {e.transOut();}
    }
  }

  public String getMode() {
    return current;
  }

  public void addMode(String name, UIElement e) {
    if (!modes.containsKey(name)) {modes.put(name, new Mode(new ArrayList<UIElement>()));}
    modes.get(name).add(e);
  }

  public void addMode(String name, UIElement e, String parent) {
    if (!modes.containsKey(name)) {modes.put(name, new Mode(new ArrayList<UIElement>(), parent));}
    modes.get(name).add(e);
  }

  public void setModeParent(String name, String parent) {
    Mode mode = modes.get(name);
    if (mode == null) return;
    mode.setParent(parent);
  }

  public void hover(double x, double y) {
    for (UIElement e : elements) {
      if (e.getType().equals("BUTTONS") && e.isActive()) {e.hover(x, y);}
    }
  }

  public void press(double x, double y) {
    for (UIElement e : elements) {
      if (e.getType().equals("BUTTONS") && e.isActive()) {e.press(x, y);}
    }
  }

  public String release(double x, double y) {
    for (UIElement e : elements) {
      if (e.getType().equals("BUTTONS") && e.isActive()) {
        String name = e.release(x, y);
        if (name != null) {lastE = e; return name;}
      }
    }
    return null;
  }

  public UITextfield getTextField(double x, double y) {
    return lastE.getTextField(x, y);
  }

  public void draw(Graphics2D g, double UIscale, int screenSizeX, int screenSizeY) {
    int[] empty = {};
    for (UIElement e : elements) {
      e.draw(g, UIscale, screenSizeX, screenSizeY, empty);
    }
  }

  public void draw(Graphics2D g, double UIscale, int screenSizeX, int screenSizeY, int[] playerStats) {
    for (UIElement e : elements) {
      e.draw(g, UIscale, screenSizeX, screenSizeY, playerStats);
    }
  }
}

class Mode {
  private String parent = null;
  private List<UIElement> elems;

  public Mode(List<UIElement> elems) {
    this.elems = elems;
  }

  public Mode(List<UIElement> elems, String parent) {
    this.elems = elems;
    this.parent = parent;
  }

  public void add(UIElement e) {
    elems.add(e);
  }

  public boolean contains(Object e) {
    return elems.contains(e);
  }

  public String getParent() {
    return parent;
  }

  public void setParent(String parent) {
    this.parent = parent;
  }
}
