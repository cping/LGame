package com.example.arpg_gles;

import java.util.Vector;


public class ChargeShot
{

    float x;
    float y;
    float angle;
    float speed;
    float height;
    float width;
    float decay;
    float time;
    boolean dead;
    public ChargeShot(float x, float y)
    {
        time = 10F;
        height = 5F;
        width = 5F;
        speed = 15F;
        decay = 0.1F;
        this.x = x;
        this.y = y;
        dead = false;
    }

    public ChargeShot(float x, float y, boolean dead)
    {
        time = 1.0F;
        height = 5F;
        width = 5F;
        speed = 15F;
        decay = 0.03F;
        this.x = x;
        this.y = y;
        this.dead = dead;
    }

    public void resetBullet(float x, float y, float angle)
    {
        time = 1.0F;
        this.x = x;
        this.y = y;
        this.angle = angle;
        dead = false;
    }

    public void update(Map map, Vector<?> vector, Bullet abullet[], int i)
    {
    }

    public void update(Map map, Player player1, Bullet abullet[], int i)
    {
    }

    public void collision(Vector<?> enemies)
    {
        for(int i = 0; i < enemies.size(); i++)
            if(((Enemy)enemies.get(i)).state != Enemy.STATE_DYING && ((Enemy)enemies.get(i)).state != Enemy.STATE_DEATH && ((Enemy)enemies.get(i)).flickerTime <= 0 && ((Enemy)enemies.get(i)).hp > 0 && (double)(x + width) > ((Enemy)enemies.get(i)).x && (double)x < ((Enemy)enemies.get(i)).x + (double)((Enemy)enemies.get(i)).width && (double)(y + height) > ((Enemy)enemies.get(i)).y && (double)y < ((Enemy)enemies.get(i)).y + (double)((Enemy)enemies.get(i)).height)
            {
                ((Enemy)enemies.get(i)).hp -= 5;
                ((Enemy)enemies.get(i)).flickerTime = 50;
                dead = true;
                if(((Enemy)enemies.get(i)).hp > 0)
                {
            
                } else
                {
                    enemies.remove(i);
        
                }
            }

    }

    public void collision(Player player)
    {
        if(player.state != Enemy.STATE_DYING && player.state != Enemy.STATE_DEATH && player.flickerTime <= 0 && player.hp > 0 && x + width > player.x && x < player.x + (float)player.width && y + height > player.y && y < player.y + (float)player.height)
        {
            player.hp--;
            player.flickerTime = 50;
            dead = true;

        }
    }

    public boolean mapCheck(Map m, float x, float y, float xmod, float ymod)
    {
        if((int)(x / (float)m.tileWidth + xmod) >= 0 && (int)(y / (float)m.tileHeight + ymod) >= 0 && (int)(x / (float)m.tileWidth + xmod) < m.width && (int)(y / (float)m.tileHeight + ymod) < m.height)
            return m.tileInfo[m.tiles[(int)(x / (float)m.tileWidth + xmod)][(int)(y / (float)m.tileHeight + ymod)]] != 0;
        else
            return false;
    }

}
