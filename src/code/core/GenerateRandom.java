package code.core;

// import code.math.IOHelp;

import code.board.Tile;

//import java.io.*;
//import java.nio.file.*;

import java.util.*;

/**
* class for generating a random map for playing the game
*/
public class GenerateRandom
{

  private static final int DEFAULT_MAP_SIZE = 30;
  private static final int DEFAULT_MINE_COUNT = 150;

  public static Tile[][] generate() {
    long seed = System.nanoTime();
    return generate(seed, DEFAULT_MAP_SIZE, DEFAULT_MAP_SIZE, DEFAULT_MINE_COUNT);
  }

  public static Tile[][] generate(long seed, int width, int height, int numMines) {
    Random rand = new Random(seed);
    Tile[][] map = new Tile[width][height];
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        map[x][y] = new Tile((int) (x-width/2), (int) (y-height/2), false);
      }
    }
    for (int i = 0; i < numMines; i++) {
      Tile t = map[rand.nextInt(width)][rand.nextInt(height)];
      if (t.isMine()) {
        i--;
        continue;
      }
      t.setMine();
    }

    return map;
  }
}
