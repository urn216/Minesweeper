package code.ai;

import java.util.ArrayList;
import java.util.List;

import code.math.Vector2;
import code.core.Scene;

public class MineAI {
  private final Scene scene;
  private final int mapSX;
  private final int mapSY;

  private boolean redo = false;
  private boolean suggest = false;

  private double minProb = 1;

  public MineAI(Scene scene) {
    this.scene = scene;
    this.mapSX = scene.getMapSX();
    this.mapSY = scene.getMapSY();
  }

  public boolean Suggests() {return suggest;}

  public boolean step() {
    redo = false;
    int[][] map = scene.getMap();
    double[][] probs = getProbabilities(map);

    if (redo) {
      return step();
    }
    return clickMin(probs);
  }

  public double[][] getProbabilities(int[][] map) {
    double[][] probs = new double[mapSX][mapSY];

    minProb = 1;

    for (int i = 0; i < mapSX; i++) {
      for (int j = 0; j < mapSY; j++) {
        if (map[i][j] == -1)  {
          probs[i][j] = findProbability(map, i, j);
          minProb = Math.min(minProb, probs[i][j]);
        }
        else probs[i][j] = 1;
      }
    }
    return probs;
  }

  /**
  * Finds the probability that a given tile is a mine.
  * This is the bulk of the work. This is code I wrote two years ago. I need to redo it
  *
  * @param map the board represented by integers.
  * @param c the column of the given tile
  * @param r the row of the given tile
  *
  * @return the probability that this tile is a mine
  */
  public double findProbability(int[][] map, int c, int r) {
    double probability = -1;
    //look at each uncovered tile around this one
    for (int rs=Math.max(r-1,0); rs<Math.min(r+2, mapSY); rs++){
      for (int cs=Math.max(c-1,0); cs<Math.min(c+2, mapSX); cs++){
        int snumer = map[cs][rs];
        if (snumer > 0) {
          int sdenom = 0;
          List<Vector2> covered = new ArrayList<Vector2>();
          List<Vector2> uncovered = new ArrayList<Vector2>();
          //look at each covered tile around this uncovered one
          for (int rp=Math.max(rs-1,0); rp<Math.min(rs+2, mapSY); rp++){
            for (int cp=Math.max(cs-1,0); cp<Math.min(cs+2, mapSX); cp++){
              int ptarget = map[cp][rp];
              if (ptarget == -2) {
                snumer --;
              }
              else if (ptarget == -1) {
                covered.add(new Vector2(cp, rp));
                sdenom ++;
              }
              else if (ptarget > 0 && (cp != cs || rp != rs)) {
                uncovered.add(new Vector2(cp, rp));
              }
            }
          }
          for (Vector2 uc : uncovered) {
            if (covered.isEmpty()) break;
            Vector2 prb = independent(map, (int)uc.x, (int)uc.y, c, r, covered);
            if (prb == null) continue;
            snumer -= prb.x;
            sdenom -= prb.y;
            break;
          }

          double prob = ((double)snumer)/sdenom;
          if (prob > probability || prob == 0) {
            if (map[c][r] == -2) {
              probability = 1;
            }
            else if (probability != 0) {
              scene.setChance(scene.createPoint(c, r), snumer, sdenom);
              probability = prob;
            }
          }
        }
      }
    }
    if (probability == 1 && map[c][r] != -2) {
      Vector2 p = scene.createPoint(c, r);
      scene.press(p);
      scene.flag(p);
      redo = true;
    }
    if (probability == -1) {
      int[] stats = scene.getStats();
      int snumer = stats[0];
      int sdenom = (mapSX*mapSY)-stats[1];
      probability = (double)snumer/sdenom;
      scene.setChance(scene.createPoint(c, r), snumer, sdenom);
    }
    return probability;
  }

  /**
  * Finds whether or not the given uncovered tile is independent from a given set of covered tiles
  *
  * @param map the board represented by integers.
  * @param ucx the x coord of the tile to check independence of
  * @param ucy the y coord of the tile to check independence of
  * @param covered the list of tiles to check against
  *
  * @return the probability of this tile's surroundings
  */
  public Vector2 independent(int[][] map, int ucx, int ucy, int c, int r, List<Vector2> covered) {
    int numer = map[ucx][ucy];
    int denom = 0;
    for (int rp=Math.max(ucy-1,0); rp<Math.min(ucy+2, mapSY); rp++){
      for (int cp=Math.max(ucx-1,0); cp<Math.min(ucx+2, mapSX); cp++){
        if (c == cp && r == rp) return null;
        int ptarget = map[cp][rp];
        if (ptarget == -2) numer --;
        else if (ptarget == -1) {
          denom ++;
          if (!contains(covered, new Vector2(cp, rp))) {
            // figure out what to do here.
            return null;
          }
        }
      }
    }
    if (denom < numer || numer <= 0) return null;
    return new Vector2(numer, denom);
  }

  public boolean contains(List<Vector2> covered, Vector2 t) {
    for (Vector2 v : covered) {
      if (v.equals(t)) return true;
    }
    return false;
  }

  public boolean clickMin(double[][] probs) {
    for (int i = 0; i < mapSY; i++) {
      for (int j = 0; j < mapSX; j++) {
        if (probs[j][i] == minProb)  {
          Vector2 p = scene.createPoint(j, i);
          scene.press(p);
          if (!suggest && minProb == 0) return scene.click(p);
          // if (!suggest) return scene.click(p);
          return true;
        }
      }
    }
    return false;
  }
}
