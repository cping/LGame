package loon.action.map.tmx.tiles;

public class TMXAnimationFrame
{
    private int tileID;
    private int duration;

    public TMXAnimationFrame(int tileID, int duration)
    {
        this.tileID = tileID;
        this.duration = duration;
    }

    public int getTileID()
    {
        return tileID;
    }

    public int getDuration()
    {
        return duration;
    }
}
