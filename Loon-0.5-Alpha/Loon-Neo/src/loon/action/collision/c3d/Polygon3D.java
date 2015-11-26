package loon.action.collision.c3d;

import loon.action.collision.Collision3D;
import loon.geom.Quaternion;
import loon.geom.Vector3f;
import loon.utils.TArray;

public class Polygon3D {
	
	private Vector3f position;

    private TArray<Vector3f> vertices;

    private float minX;
    private float minY;
    private float minZ;
    private float maxX;
    private float maxY;
    private float maxZ;

    private float rotationX;
    private float rotationY;
    private float rotationZ;

    private Vector3f scale;

    private Cuboid3D bounds;

    private Quaternion tempQuat;

    public Polygon3D(Polygon3D other)
    {
        this();
        position.set(other.position);
        vertices.addAll(other.vertices);
    }

    public Polygon3D()
    {
        vertices = new TArray<Vector3f>();
        position = new Vector3f();

        tempQuat = new Quaternion();
        scale = new Vector3f(1, 1, 1);

        clearVertices();
    }

    public void clearVertices()
    {
        vertices.clear();

        minX = minY = minZ = Float.POSITIVE_INFINITY;
        maxX = maxY = maxZ = Float.NEGATIVE_INFINITY;
    }

    public void addVertex(float x, float y, float z)
    {
        addVertex(new Vector3f(x, y, z));
    }

    public void addVertex(Vector3f v)
    {
        vertices.add(v);

        minX = Math.min(minX, v.x);
        minY = Math.min(minY, v.y);
        minZ = Math.min(minZ, v.z);
        maxX = Math.max(maxX, v.x);
        maxY = Math.max(maxY, v.y);
        maxZ = Math.max(maxZ, v.z);
    }

    private void updateBounds()
    {
        float minX, minY, minZ, maxX, maxY, maxZ;

        minX = minY = minZ = Float.POSITIVE_INFINITY;
        maxX = maxY = maxZ = Float.NEGATIVE_INFINITY;

        for (Vector3f v : vertices)
        {
            minX = Math.min(minX, v.x);
            minY = Math.min(minY, v.y);
            minZ = Math.min(minZ, v.z);
            maxX = Math.max(maxX, v.x);
            maxY = Math.max(maxY, v.y);
            maxZ = Math.max(maxZ, v.z);
        }

        if (bounds == null){
            bounds = new Cuboid3D(new Vector3f(minX, minY, minZ).add(position),
                    new Vector3f(maxX, maxY, maxZ).add(position));
        }
        else{
            bounds.set(maxX - minX, maxY - minY, maxZ - minZ, position);
        }
    }

    public void rotate(float rx, float ry, float rz)
    {
        for (Vector3f v : vertices){
            tempQuat.set(rx, ry, rz).multiply(v, v);
        }

        updateBounds();

        rotationX += rx;
        rotationY += ry;
        rotationZ += rz;
    }

    public void setRotation(float rx, float ry, float rz)
    {
        rotate(rx - rotationX, ry - rotationY, rz - rotationZ);
    }

    public void scale(float s)
    {
        scale(s, s, s);
    }

    public void scale(float sx, float sy, float sz)
    {
        for (Vector3f v : vertices)
            v.scaleSelf(sx, sy, sz);

        updateBounds();

        scale.addSelf(sx, sy, sz);
    }

    public void setScale(float sx, float sy, float sz)
    {
        scale(sx - scale.x, sy - scale.y, sz - scale.z);
    }

    public void translate(float x, float y, float z)
    {
        for (Vector3f v : vertices)
            v.addSelf(x, y, z);

        updateBounds();
    }

    public boolean intersects(Polygon3D other)
    {
        return Collision3D.checkPolyhedronCollision(this, other);
    }

    public boolean contains(Vector3f p)
    {
        int i, j = getVertices().size - 1;
        boolean oddNodes = false;

        Vector3f vi = Vector3f.TMP();
        Vector3f vj = Vector3f.TMP();

        for (i = 0; i < getVertices().size; j = i++)
        {
            vi.set(getVertex(i)).addSelf(position);
            vj.set(getVertex(j)).addSelf(position);

            if ((((vi.getY() <= p.getY()) && (p.getY() < vj.getY())) ||
                 ((vj.getY() <= p.getY()) && (p.getY() < vi.getY())) ||
                 ((vj.getZ() <= p.getZ()) && (p.getZ() < vi.getZ()))) &&
                (p.getX() < (vj.getX() - vi.getX()) * (p.getY() - vi.getY()) / (vj.getY() - vi.getY()) + vi.getX()))
                oddNodes = !oddNodes;
        }


        return oddNodes;
    }

    public TArray<Vector3f> getVertices()
    {
        return vertices;
    }

    public Vector3f getVertex(int index)
    {
        return vertices.get(index);
    }

    public Polygon3D copy()
    {
        return new Polygon3D(this);
    }

    public Vector3f getPosition()
    {
        return position;
    }

    public void setPosition(Vector3f position)
    {
        this.position.set(position);

        if (bounds != null){
            bounds.setPosition(position);
        }
    }

    public Vector3f getScale()
    {
        return scale;
    }

    public int vertexCount()
    {
        return vertices.size;
    }

    public Cuboid3D getBounds()
    {
        updateBounds();
        return bounds;
    }

    public float getWidth()
    {
        return maxX - minX;
    }

    public float getHeight()
    {
        return maxY - minY;
    }

    public float getThickness()
    {
        return maxZ - minZ;
    }

    public float getRotationX()
    {
        return rotationX;
    }

    public float getRotationY()
    {
        return rotationY;
    }

    public float getRotationZ()
    {
        return rotationZ;
    }
}
