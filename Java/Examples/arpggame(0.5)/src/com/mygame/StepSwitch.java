package com.mygame;

import java.util.Vector;

import loon.geom.RectBox;

public class StepSwitch extends Switch
{

    StepSwitch(int x, int y, Vector<Thing> blocks)
    {
        this.x = x;
        this.y = y;
        this.blocks = blocks;
        orgblocks = new Vector<Thing>();
        for(int i = 0; i < blocks.size(); i++)
            orgblocks.add((Thing)blocks.get(i));

        height = 50;
        width = 50;
        active = false;
        col = new RectBox(x, y, width, height);
    }
    
    
    public void update(Player player, Vector<Thing> things)
    {
        boolean b = false;
        for(int i = 0; i < things.size(); i++)
            if(col.intersects((int)((Thing)things.get(i)).x, (int)((Thing)things.get(i)).y, ((Thing)things.get(i)).width, ((Thing)things.get(i)).height) && !b)
            {
                b = true;
                active = true;
                blocks.clear();
            }

        if(col.intersects((int)player.x, (int)player.y, player.width, player.height))
        {
            b = true;
            active = true;
            blocks.clear();
        }
        if(!b)
        {
            active = false;
            if(blocks.isEmpty())
            {
                for(int i = 0; i < orgblocks.size(); i++)
                    blocks.add((Thing)orgblocks.get(i));

            }
        }
    }
}
