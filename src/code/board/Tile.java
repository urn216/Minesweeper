package code.board;

// import code.math.MathHelp;
import mki.math.vector.Vector2;

// import java.util.*;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.Polygon;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;

/**
* Write a description of class Tile here.
*
* @author (your name)
* @version (a version number or a date)
*/
public class Tile
{
  public static final int TILE_SIZE = 64;
  private static final boolean DRAW_CHANCE = true;

  private int aiNum = 0;
  private int aiDen = 0;

  private Vector2 origin;

  private boolean visible = true;
  private boolean isMine;
  private boolean isFlagged;
  private boolean isClicked;
  private boolean isIn;

  private Tile[][] nb;

  private int number = 0;

  String text = "";
  Color colour = Color.white;

  /**
  * Constructor for Tiles
  */
  public Tile(double x, double y, boolean isMine)
  {
    origin = new Vector2(x*TILE_SIZE, y*TILE_SIZE);
    this.isMine = isMine;
  }

  public void setMine() {isMine = true;}

  public void unsetMine() {isMine = false;}

  public boolean isMine() {return isMine;}

  public void toggleFlag() {isFlagged = !isFlagged;}

  public boolean isFlagged() {return isFlagged;}

  public void click() {isClicked = true;}

  public void unClick() {isClicked = false;}

  public boolean isClicked() {return isClicked;}

  public void setIn() {isIn = true;}

  public void unsetIn() {isIn = false;}

  public boolean isIn() {return isIn;}

  public boolean isVis() {return visible;}

  public void pressNeighbours() {
    for (int x = 0; x < 3; x++) {
      for (int y = 0; y < 3; y++) {
        Tile t = nb[x][y];
        if (t == null || t.isClicked() || t.isFlagged()) continue;
        t.setIn();
      }
    }
  }

  public int clickNeighbours() {
    int count = 0;
    for (int x = 0; x < 3; x++) {
      for (int y = 0; y < 3; y++) {
        Tile t = nb[x][y];
        if (t == null || t.isClicked() || t.isFlagged()) continue;
        t.click();
        count++;
        if (t.getNumber() == 0) count += t.clickNeighbours();
      }
    }
    return count;
  }

  public boolean neighbourFlags() {
    int f = number;
    for (int x = 0; x < 3; x++) {
      for (int y = 0; y < 3; y++) {
        Tile t = nb[x][y];
        if (t != null && t.isFlagged()) f--;
      }
    }
    if (f == 0) return true;
    return false;
  }

  public void getNeighbours(Tile[][] map, int x, int y, int mapSX, int mapSY) {
    nb = new Tile[3][3];
    if (x == 0) {
      nb[0][0] = null;
      nb[0][1] = null;
      nb[0][2] = null;
    } else {
      nb[0][0] = y == 0 ? null : map[x-1][y-1];
      nb[0][1] = map[x-1][y];
      nb[0][2] = y == mapSY-1 ? null : map[x-1][y+1];
    }
    nb[1][0] = y == 0 ? null : map[x][y-1];
    nb[1][1] = null;
    nb[1][2] = y == mapSY-1 ? null : map[x][y+1];
    if (x == mapSX-1) {
      nb[2][0] = null;
      nb[2][1] = null;
      nb[2][2] = null;
    } else {
      nb[2][0] = y == 0 ? null : map[x+1][y-1];
      nb[2][1] = map[x+1][y];
      nb[2][2] = y == mapSY-1 ? null : map[x+1][y+1];
    }
    setNumber();
  }

  public int getNumber() {return number;}

  public void setNumber() {
    if (isMine) {
      number = 9;
      text = "*";
      colour = Color.red;
      return;
    }
    number = 0;
    for (int x = 0; x < 3; x++) {
      for (int y = 0; y < 3; y++) {
        if (nb[x][y] != null) {
          if (nb[x][y].isMine()) number++;
        }
      }
    }
    text = ""+number;
    colour = MineColours.colours[number];
  }

  public void setChance(int num, int den) {
    aiNum = num;
    aiDen = den;
  }

  public void reveal() {
    colour = Color.black;
    click();
  }

  public boolean equals(Object other) {
    if (other.getClass() != this.getClass()) return false;
    Tile t = (Tile)other;
    return origin.equals(t.origin);
  }

  public void draw(Graphics2D g, Camera cam) {
    double z = cam.getZoom();
    double conX = cam.conX();
    double conY = cam.conY();

    if (isClicked) {
      Font font = new Font("Copperplate", Font.BOLD, (int) Math.round((TILE_SIZE*0.7*z)));
      FontMetrics metrics = g.getFontMetrics(font);
      g.setFont(font);

      g.setColor(Color.white);
      g.fill(new Rectangle2D.Double(origin.x*z-conX, origin.y*z-conY, TILE_SIZE*z, TILE_SIZE*z));
      g.setColor(Color.lightGray);
      g.draw(new Rectangle2D.Double(origin.x*z-conX, origin.y*z-conY, TILE_SIZE*z, TILE_SIZE*z));
      g.setColor(colour);
      g.drawString(text, (int)((origin.x*z+(TILE_SIZE*z-metrics.stringWidth(text))/2)-conX), (int)((origin.y*z+((TILE_SIZE*z - metrics.getHeight())/2) + metrics.getAscent())-conY));
    }
    else {
      g.setColor(Color.lightGray);
      g.fill(new Rectangle2D.Double(origin.x*z-conX, origin.y*z-conY, TILE_SIZE*z, TILE_SIZE*z));
      Color tl = Color.white;
      Color br = Color.gray;

      if (isFlagged) {
        drawFlag(g, cam);
      }
      else if (DRAW_CHANCE && aiDen != 0) {
        Font font = new Font("Copperplate", Font.BOLD, (int) Math.round((TILE_SIZE*1.6*z/(aiNum+"/"+aiDen).length())));
        FontMetrics metrics = g.getFontMetrics(font);
        g.setFont(font);

        g.setColor(Color.gray);
        g.drawString(aiNum+"/"+aiDen, (int)((origin.x*z+(TILE_SIZE*z-metrics.stringWidth(aiNum+"/"+aiDen))/2)-conX), (int)((origin.y*z+((TILE_SIZE*z - metrics.getHeight())/2) + metrics.getAscent())-conY));
      }

      if (isIn) {
        tl = Color.gray;
        br = Color.white;
      }

      g.setColor(tl);
      g.fill(new Rectangle2D.Double(origin.x*z-conX, origin.y*z-conY, TILE_SIZE/16*z, TILE_SIZE*z));
      g.setColor(br);
      g.fill(new Rectangle2D.Double(origin.x*z-conX, (origin.y+TILE_SIZE*15/16)*z-conY, TILE_SIZE*z, TILE_SIZE/16*z));
      g.fill(new Rectangle2D.Double((origin.x+TILE_SIZE*15/16)*z-conX, origin.y*z-conY, TILE_SIZE/16*z, TILE_SIZE*z));
      g.setColor(tl);
      g.fill(new Rectangle2D.Double(origin.x*z-conX, origin.y*z-conY, TILE_SIZE*z, TILE_SIZE/16*z));
    }
  }

  private void drawFlag(Graphics2D g, Camera cam) {
    double z = cam.getZoom();
    double conX = cam.conX();
    double conY = cam.conY();

    g.setColor(Color.black);
    g.fill(new Rectangle2D.Double(origin.x*z-conX+TILE_SIZE*7.5/16*z, origin.y*z-conY+TILE_SIZE*3/16*z, TILE_SIZE/16*z, TILE_SIZE*10/16*z));
    g.fill(new Rectangle2D.Double(origin.x*z-conX+TILE_SIZE*4/16*z, origin.y*z-conY+TILE_SIZE*12/16*z, TILE_SIZE*8/16*z, TILE_SIZE/16*z));
    g.setColor(MineColours.colours[5]);
    int[] xs = {(int)(origin.x*z-conX+TILE_SIZE*7.5/16*z), (int)(origin.x*z-conX+TILE_SIZE*8/16*z), (int)(origin.x*z-conX+TILE_SIZE*3/16*z)};
    int[] ys = {(int)(origin.y*z-conY+TILE_SIZE*3/16*z), (int)(origin.y*z-conY+TILE_SIZE*7.5/16*z), (int)(origin.y*z-conY+TILE_SIZE*7.5/16*z)};
    g.fill(new Polygon(xs, ys, 3));
  }
}

class MineColours {
  static Color[] colours = {
    new Color(255, 255, 255),
    new Color(0, 0, 255),
    new Color(0, 100, 0),
    new Color(160, 0, 0),
    new Color(100, 0, 140),
    new Color(80, 10, 0),
    new Color(0, 80, 80),
    new Color(0, 0, 0),
    new Color(0, 0, 0)};
}


/* Shitty old unit passoff code
if (unitPos.x < topLeft.x && nb[0][1] != null) {
if (unitPos.y < topLeft.y && nb[0][0] != null) {nb[0][0].passOff(unit);}
else if (unitPos.y >= botRight.y && nb[0][2] != null) {nb[0][2].passOff(unit);}
else {nb[0][1].passOff(unit);}
units.remove(i);
i--;
}
else if (unitPos.x >= botRight.x && nb[2][1] != null) {
if (unitPos.y < topLeft.y && nb[2][0] != null) {nb[2][0].passOff(unit);}
else if (unitPos.y >= botRight.y && nb[2][2] != null) {nb[2][2].passOff(unit);}
else {nb[2][1].passOff(unit);}
units.remove(i);
i--;
}
else if (unitPos.y < topLeft.y && nb[1][0] != null) {
nb[1][0].passOff(unit);
units.remove(i);
i--;
}
else if (unitPos.y >= botRight.y && nb[1][2] != null) {
nb[1][2].passOff(unit);
units.remove(i);
i--;
}
*/
