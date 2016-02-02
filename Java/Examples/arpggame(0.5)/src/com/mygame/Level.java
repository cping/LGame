package com.mygame;

import java.util.Vector;

public class Level
{

    Map map;
    Vector<Object> enemies;
    Vector<Thing> things;
    Vector<Switch> switches;
    public Level()
    {
        map = new Map();
        enemies = new Vector<Object>();
        things = new Vector<Thing>();
        switches = new Vector<Switch>();
    }

}
