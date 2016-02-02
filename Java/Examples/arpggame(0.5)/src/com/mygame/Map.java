package com.mygame;

import loon.geom.RectBox;

class Map
{

    public Map()
    {
        tileHeight = 50;
        tileWidth = 50;
        height = 50;
        width = 50;
        numTiles = 255;
        upExit = 0;
        downExit = 0;
        leftExit = 0;
        rightExit = 0;
        tiles = new int[width][height];
        for(int j = 0; j < height; j++)
        {
            for(int i = 0; i < width; i++)
                tiles[i][j] = 0;

        }

        tileCol = new RectBox[width][height];
        System.out.println((new StringBuilder("tiles: ")).append(tiles.length).toString());
    }

    public void resize()
    {
        tiles = new int[width][height];
        tileCol = new RectBox[width][height];
    }

    public void setCol()
    {
        for(int j = 0; j < height; j++)
        {
            for(int i = 0; i < width; i++)
                if(tileInfo[tiles[i][j]] == 1)
                    tileCol[i][j] = new RectBox(i * tileWidth, j * tileHeight, tileWidth, tileHeight);
                else
                    tileCol[i][j] = new RectBox(0, 0, 0, 0);

        }

    }

    int tileHeight;
    int tileWidth;
    int height;
    int width;
    int numTiles;
    int tiles[][];
    RectBox tileCol[][];
    int tileInfo[] = {
        0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 
        0, 1, 1, 1, 1, 1, 0, 0, 1, 1, 
        1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 
        1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 
        0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 
        1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 
        1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 
        1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 
        1, 1, 1, 1, 1, 1, 1, 1
    };
    int upExit;
    int downExit;
    int leftExit;
    int rightExit;
}
