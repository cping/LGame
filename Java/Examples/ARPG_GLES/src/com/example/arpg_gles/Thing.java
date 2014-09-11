package com.example.arpg_gles;

import loon.core.geom.RectBox;


public class Thing
{

    public static int STATE_NORMAL = 0;
    public static int STATE_DEATH = 2;
    int height;
    int width;
    float x;
    float y;
    int state;
    boolean moveable;
    boolean solid;

    public Thing()
    {
        height = 26;
        width = 16;
        x = 400 - width / 2;
        y = 300 - height / 2;
        state = 0;
        moveable = false;
        solid = false;
    }

    public void update(Player player1, int i)
    {
    }

    public void collision(Player player1)
    {
    }

    public RectBox[] getColTop(Map m)
    {
        RectBox col[] = new RectBox[3];
        int startX = (int)(x / (float)m.tileWidth);
        int startY = (int)(y / (float)m.tileWidth);
        for(int i = -1; i <= 1; i++)
            try
            {
                if(m.tileInfo[m.tiles[startX + i][startY - 1]] == 1)
                    col[i + 1] = m.tileCol[startX + i][startY - 1];
                else
                    col[i + 1] = new RectBox(0, 0, 0, 0);
            }
            catch(Exception e)
            {
                col[i + 1] = new RectBox(0, 0, 0, 0);
            }

        return col;
    }

    public RectBox[] getColBottom(Map m)
    {
        RectBox col[] = new RectBox[3];
        int startX = (int)(x / (float)m.tileWidth);
        int startY = (int)(y / (float)m.tileWidth);
        for(int i = -1; i <= 1; i++)
            try
            {
                if(m.tileInfo[m.tiles[startX + i][startY + 1]] == 1)
                    col[i + 1] = m.tileCol[startX + i][startY + 1];
                else
                    col[i + 1] = new RectBox(0, 0, 0, 0);
            }
            catch(Exception e)
            {
                col[i + 1] = new RectBox(0, 0, 0, 0);
            }

        return col;
    }

    public RectBox[] getColLeft(Map m)
    {
        RectBox col[] = new RectBox[3];
        int startX = (int)(x / (float)m.tileWidth);
        int startY = (int)(y / (float)m.tileWidth);
        for(int i = -1; i <= 1; i++)
            try
            {
                if(m.tileInfo[m.tiles[startX - 1][startY + i]] == 1)
                    col[i + 1] = m.tileCol[startX - 1][startY + i];
                else
                    col[i + 1] = new RectBox(0, 0, 0, 0);
            }
            catch(Exception e)
            {
                col[i + 1] = new RectBox(0, 0, 0, 0);
            }

        return col;
    }

    public RectBox[] getColRight(Map m)
    {
        RectBox col[] = new RectBox[3];
        int startX = (int)(x / (float)m.tileWidth);
        int startY = (int)(y / (float)m.tileWidth);
        for(int i = -1; i <= 1; i++)
            try
            {
                if(m.tileInfo[m.tiles[startX + 1][startY + i]] == 1)
                    col[i + 1] = m.tileCol[startX + 1][startY + i];
                else
                    col[i + 1] = new RectBox(0, 0, 0, 0);
            }
            catch(Exception e)
            {
                col[i + 1] = new RectBox(0, 0, 0, 0);
            }

        return col;
    }

}
