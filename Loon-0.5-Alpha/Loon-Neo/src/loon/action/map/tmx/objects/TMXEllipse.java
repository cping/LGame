package loon.action.map.tmx.objects;

public class TMXEllipse
{
    private int x;
    private int y;
    private int radiusX;
    private int radiusY;

    public void set(int x, int y, int width, int height)
    {
        this.x = x + (width / 2);
        this.y = y + (height / 2);
        this.radiusX = width / 2;
        this.radiusY = height / 2;
    }

    public int getCenterX()
    {
        return x;
    }

    public int getCenterY()
    {
        return y;
    }

    public int getRadiusX()
    {
        return radiusX;
    }

    public int getRadiusY()
    {
        return radiusY;
    }
}
