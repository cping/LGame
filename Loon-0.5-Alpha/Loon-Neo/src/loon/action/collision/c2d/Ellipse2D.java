package loon.action.collision.c2d;

import loon.geom.Vector2f;
import loon.utils.MathUtils;

public class Ellipse2D extends Polygon2D
{
    public Ellipse2D(float rx, float ry)
    {
        this(0, 0, rx, ry);
    }

    public Ellipse2D(float x, float y, float rx, float ry)
    {
        this(new Vector2f(x, y), rx, ry);
    }

    public Ellipse2D(Vector2f center, float rx, float ry)
    {
        updateVertices(rx, ry);
        setCenter(center);
    }

    private void updateVertices(float rx, float ry)
    {
        clearVertices();

        float x = getPosition().x;
        float y = getPosition().y;

        for (int i = 0; i < 360; i++){
            addVertex(new Vector2f(x + rx + MathUtils.cos(i) * rx, y + ry + MathUtils.sin(i) * ry));
        }
    }

    public float getRadiusX()
    {
        return getBounds().getWidth() / 2;
    }

    public void setRadiusX(float rx)
    {
        float rotation = getRotation();
        updateVertices(rx, getRadiusY());
        setRotation(rotation);
    }

    public float getRadiusY()
    {
        return getBounds().getHeight() / 2;
    }

    public void setRadiusY(float ry)
    {
        float rotation = getRotation();
        updateVertices(getRadiusX(), ry);
        setRotation(rotation);
    }
}
