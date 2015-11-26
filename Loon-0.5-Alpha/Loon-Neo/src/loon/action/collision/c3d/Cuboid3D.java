package loon.action.collision.c3d;

import loon.geom.Vector3f;

public class Cuboid3D extends Polygon3D
{
    private float width;
    private float height;
    private float thickness;

    private Vector3f[] vertices;

    public Cuboid3D(Vector3f position, float width, float height, float thickness)
    {
        this();

        this.width = width;
        this.height = height;
        this.thickness = thickness;

        setPosition(position);

        updateVertices();
    }

    public Cuboid3D()
    {
        vertices = new Vector3f[26];

        for (int i = 0; i < vertices.length; i++){
            vertices[i] = new Vector3f();
        }

        width = height = thickness = 1;

        setPosition(Vector3f.ZERO());
        updateVertices();
    }

    public Cuboid3D(Vector3f min, Vector3f max)
    {
        this();

        Vector3f size = max.subtract(min);

        width = size.x;
        height = size.y;
        thickness = size.z;

        setPosition(min.add(max).scaleSelf(0.5f));

        updateVertices();
    }

    private void updateVertices()
    {
        clearVertices();

        addVertex(vertices[0].set(-width / 2, -height / 2, +thickness / 2));
        addVertex(vertices[1].set(+width / 2, -height / 2, +thickness / 2));
        addVertex(vertices[2].set(-width / 2, +height / 2, +thickness / 2));
        addVertex(vertices[3].set(+width / 2, +height / 2, +thickness / 2));

        addVertex(vertices[4].set(+width / 2, +height / 2, +thickness / 2));
        addVertex(vertices[5].set(+width / 2, -height / 2, +thickness / 2));
        addVertex(vertices[6].set(+width / 2, +height / 2, -thickness / 2));
        addVertex(vertices[7].set(+width / 2, -height / 2, -thickness / 2));

        addVertex(vertices[8].set(+width / 2, -height / 2, -thickness / 2));
        addVertex(vertices[9].set(-width / 2, -height / 2, -thickness / 2));
        addVertex(vertices[10].set(+width / 2, +height / 2, -thickness / 2));
        addVertex(vertices[11].set(-width / 2, +height / 2, -thickness / 2));

        addVertex(vertices[12].set(-width / 2, +height / 2, -thickness / 2));
        addVertex(vertices[13].set(-width / 2, -height / 2, -thickness / 2));
        addVertex(vertices[14].set(-width / 2, +height / 2, +thickness / 2));
        addVertex(vertices[15].set(-width / 2, -height / 2, +thickness / 2));

        addVertex(vertices[16].set(-width / 2, -height / 2, +thickness / 2));
        addVertex(vertices[17].set(-width / 2, -height / 2, -thickness / 2));
        addVertex(vertices[18].set(+width / 2, -height / 2, +thickness / 2));
        addVertex(vertices[19].set(+width / 2, -height / 2, -thickness / 2));

        addVertex(vertices[20].set(+width / 2, -height / 2, -thickness / 2));
        addVertex(vertices[21].set(-width / 2, +height / 2, +thickness / 2));

        addVertex(vertices[22].set(-width / 2, +height / 2, +thickness / 2));
        addVertex(vertices[23].set(+width / 2, +height / 2, +thickness / 2));
        addVertex(vertices[24].set(-width / 2, +height / 2, -thickness / 2));
        addVertex(vertices[25].set(+width / 2, +height / 2, -thickness / 2));
    }

    @Override
    public float getWidth()
    {
        return width;
    }

    @Override
    public float getHeight()
    {
        return height;
    }

    @Override
    public float getThickness()
    {
        return thickness;
    }

    public void set(float width, float height, float thickness, Vector3f position)
    {
        setPosition(position);

        this.width = width;
        this.height = height;
        this.thickness = thickness;

        updateVertices();
    }

    public float getIntersectionWidth(Cuboid3D aabb)
    {
        if (aabb.getRotationX() != 0 || aabb.getRotationY() != 0 || aabb.getRotationZ() != 0){
            aabb = aabb.getBounds();
        }

        Cuboid3D self = (getRotationX() == 0 && getRotationY() == 0 && getRotationZ() == 0) ? this : getBounds();

        float tx1 = self.getPosition().x - self.getWidth() / 2;
        float rx1 = aabb.getPosition().x - aabb.getWidth() / 2;

        float tx2 = tx1 + self.getWidth();
        float rx2 = rx1 + aabb.getWidth();

        return tx2 > rx2 ? rx2 - tx1 : tx2 - rx1;
    }

    public float getIntersectionHeight(Cuboid3D aabb)
    {
        if (aabb.getRotationX() != 0 || aabb.getRotationY() != 0 || aabb.getRotationZ() != 0){
            aabb = aabb.getBounds();
        }

        Cuboid3D self = (getRotationX() == 0 && getRotationY() == 0 && getRotationZ() == 0) ? this : getBounds();

        float ty1 = self.getPosition().y - self.getHeight() / 2;
        float ry1 = aabb.getPosition().y - aabb.getHeight() / 2;

        float ty2 = ty1 + self.getHeight();
        float ry2 = ry1 + aabb.getHeight();

        return ty2 > ry2 ? ry2 - ty1 : ty2 - ry1;
    }

    public float getIntersectionThickness(Cuboid3D aabb)
    {
        if (aabb.getRotationX() != 0 || aabb.getRotationY() != 0 || aabb.getRotationZ() != 0){
            aabb = aabb.getBounds();
        }

        Cuboid3D self = (getRotationX() == 0 && getRotationY() == 0 && getRotationZ() == 0) ? this : getBounds();

        float tz1 = self.getPosition().z - self.getThickness() / 2;
        float rz1 = aabb.getPosition().z - aabb.getThickness() / 2;

        float tz2 = tz1 + self.getThickness();
        float rz2 = rz1 + aabb.getThickness();

        return tz2 > rz2 ? rz2 - tz1 : tz2 - rz1;
    }
}