package code.core;

// import code.math.IOHelp;
// import code.math.MathHelp;
import code.math.Vector2;

import code.board.Camera;
import code.board.Decal;
import code.board.Tile;

// import java.util.*;
import java.awt.Graphics2D;

/**
* Scene class
*/
public class Scene
{
  public static final Scene MENU_SCENE = new Scene(0);

  private final Camera cam;

  private int mapSX;
  private int mapSY;
  private Tile[][] map;

  private Decal bg = new Decal(1920, 1080, "BG/Menu.png", false);

  private int numMines;
  private int numClicked;

  private boolean first;

  /**
  * Constructor for Scenes
  */
  public Scene() {
    cam = new Camera(new Vector2(), new Vector2(), 1);
    gameSetup();
  }

  private Scene(int a) {
    cam = new Camera(new Vector2(), new Vector2(), 1);
    map = new Tile[0][0];
    mapSX = mapSY = 0;
    numClicked = 0;
  }

  private void gameSetup() {
    mapSX = Core.GAME_SETTINGS.getIntSetting("mapW");
    mapSY = Core.GAME_SETTINGS.getIntSetting("mapH");
    map = GenerateRandom.generate(System.nanoTime(), mapSX, mapSY, (int)(Core.GAME_SETTINGS.getDoubleSetting("mines")*mapSX*mapSY));
    first = true;
    countMines();
    numClicked = 0;
  }

  public void countMines() {
    numMines = 0;
    for (int i = 0; i < mapSX; i++) {
      for (int j = 0; j < mapSY; j++) {
        map[i][j].getNeighbours(map, i, j, mapSX, mapSY);
        if (map[i][j].isMine()) {
          numMines++;
        }
      }
    }
  }

  public void reset() {
    gameSetup();
  }

  public int[] getStats() {
    int[] stats = {numMines, numClicked};
    return stats;
  }

  public Camera getCam() {return cam;}

  public int getMapSX() {return mapSX;}

  public int getMapSY() {return mapSY;}

  public Tile getTile(Vector2 p) {return map[(int)((p.x/Tile.TILE_SIZE)+mapSX/2)][(int)((p.y/Tile.TILE_SIZE)+mapSY/2)];}

  public Vector2 createPoint(int x, int y) {return new Vector2((x-mapSX/2)*Tile.TILE_SIZE, (y-mapSY/2)*Tile.TILE_SIZE);}

  public int[][] getMap() {
    int[][] intMap = new int[mapSX][mapSY];

    for (int i = 0; i < mapSX; i++) {
      for (int j = 0; j < mapSY; j++) {
        intMap[i][j] = map[i][j].isClicked() ? map[i][j].getNumber() : (map[i][j].isFlagged() ? -2 : -1);
      }
    }

    return intMap;
  }

  public void setChance(Vector2 p, int num, int den) {
    getTile(p).setChance(num, den);
  }

  public boolean validate(Vector2 p) {
    int x = (int)((p.x/Tile.TILE_SIZE)+mapSX/2);
    int y = (int)((p.y/Tile.TILE_SIZE)+mapSY/2);
    if (x < 0 || x >= mapSX
    ||  y < 0 || y >= mapSY) return false;
    return true;
  }

  public void press(Vector2 p) {
    if (!validate(p)) return;
    Tile t = getTile(p);
    t.setIn();
    if (t.isClicked()) t.pressNeighbours();
  }

  public boolean click(Vector2 p) {
    if (validate(p)) {
      Tile t = getTile(p);
      if (t.isIn() && !t.isFlagged()) {
        if (first) {
          first = false;
          if (t.isMine()) {moveMine(t);}
        }
        if (t.isClicked()) {
          if (t.neighbourFlags()) t.clickNeighbours();
        }
        else {
          t.click();
          numClicked++;
          if (t.getNumber() == 0) numClicked += t.clickNeighbours();
        }
      }
    }

    for (int i = 0; i < mapSX; i++) {
      for (int j = 0; j < mapSY; j++) {
        Tile t = map[i][j];
        t.unsetIn();
        if (t.isMine() && t.isClicked()) return false;
      }
    }
    return true;
  }

  public void flag(Vector2 p) {
    if (validate(p)) {
      Tile t = getTile(p);
      if (t.isIn() && !t.isClicked()) {
        t.toggleFlag();
        if (t.isFlagged()) {
          numClicked++;
          numMines--;
        }
        else {
          numClicked--;
          numMines++;
        }
      }
    }

    for (int i = 0; i < mapSX; i++) {
      for (int j = 0; j < mapSY; j++) {
        map[i][j].unsetIn();
      }
    }
  }

  public boolean checkWin() {
    for (int i = 0; i < mapSX; i++) {
      for (int j = 0; j < mapSY; j++) {
        Tile t = map[i][j];
        if (!t.isMine() && !t.isClicked()) return false;
      }
    }
    return true;
  }

  public void moveMine(Tile o) {
    for (int i = 0; i < mapSX; i++) {
      for (int j = 0; j < mapSY; j++) {
        Tile t = map[i][j];
        if (!t.isMine()) {
          t.setMine();
          o.unsetMine();
          countMines();
          return;
        }
      }
    }
  }

  public void draw(Graphics2D g, boolean revealAll) {
    bg.draw(g, cam);

    for (int i = 0; i < mapSX; i++) {
      int x = i-mapSX/2;
      for (int j = 0; j < mapSY; j++) {
        int y = j-mapSY/2;
        Tile t = map[i][j];
        if (revealAll && t.isMine() && !t.isClicked()) t.reveal();
        if (cam.canSee(x*Tile.TILE_SIZE, y*Tile.TILE_SIZE, (x+1)*Tile.TILE_SIZE, (y+1)*Tile.TILE_SIZE)) 
            t.draw(g, cam);
      }
    }
  }
}
