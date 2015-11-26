package loon.geom;

import loon.action.camera.BaseCamera;
import loon.action.collision.c2d.Polygon2D;
import loon.action.collision.c3d.Polygon3D;
import loon.action.collision.c3d.Sphere3D;
import loon.utils.MathUtils;

public class Stereoscopic {

    public static final int LEFT   = 0;
    public static final int RIGHT  = 1;
    public static final int TOP    = 2;
    public static final int BOTTOM = 3;
    public static final int NEAR   = 4;
    public static final int FAR    = 5;

    public static final int TOP_LEFT_FAR      = 0;
    public static final int TOP_RIGHT_FAR     = 1;
    public static final int TOP_RIGHT_NEAR    = 2;
    public static final int TOP_LEFT_NEAR     = 3;
    public static final int BOTTOM_LEFT_FAR   = 4;
    public static final int BOTTOM_RIGHT_FAR  = 5;
    public static final int BOTTOM_RIGHT_NEAR = 6;
    public static final int BOTTOM_LEFT_NEAR  = 7;

    public static final int TOP_LEFT     = 0;
    public static final int TOP_RIGHT    = 1;
    public static final int BOTTOM_LEFT  = 2;
    public static final int BOTTOM_RIGHT = 3;

    private Plane[] planes;

    private Matrix4 StereoscopicMatrix;

    private Vector3f[]  StereoscopicCorners;
    private Vector2f[]  StereoscopicPolygonVertices;
    private Polygon2D    StereoscopicPolygon;
    private Polygon3D StereoscopicPolyhedron;

    public Stereoscopic()
    {
        planes = new Plane[6];
        for (int i = 0; i < planes.length; i++){
            planes[i] = new Plane();
        }

        StereoscopicMatrix = new Matrix4().idt();
        StereoscopicCorners = new Vector3f[8];

        for (int i = 0; i < 8; i++){
            StereoscopicCorners[i] = new Vector3f();
        }

        StereoscopicPolygonVertices = new Vector2f[4];

        StereoscopicPolygon = new Polygon2D();
        for (int i = 0; i < 4; i++)
        {
            StereoscopicPolygonVertices[i] = new Vector2f();
            StereoscopicPolygon.addVertex(StereoscopicPolygonVertices[i]);
        }

        StereoscopicPolyhedron = new Polygon3D();
        StereoscopicPolyhedron.addVertex(StereoscopicCorners[BOTTOM_LEFT_NEAR]);
        StereoscopicPolyhedron.addVertex(StereoscopicCorners[BOTTOM_RIGHT_NEAR]);
        StereoscopicPolyhedron.addVertex(StereoscopicCorners[TOP_LEFT_NEAR]);
        StereoscopicPolyhedron.addVertex(StereoscopicCorners[TOP_RIGHT_NEAR]);

        StereoscopicPolyhedron.addVertex(StereoscopicCorners[TOP_RIGHT_NEAR]);
        StereoscopicPolyhedron.addVertex(StereoscopicCorners[BOTTOM_RIGHT_NEAR]);
        StereoscopicPolyhedron.addVertex(StereoscopicCorners[TOP_RIGHT_FAR]);
        StereoscopicPolyhedron.addVertex(StereoscopicCorners[BOTTOM_RIGHT_FAR]);

        StereoscopicPolyhedron.addVertex(StereoscopicCorners[BOTTOM_RIGHT_FAR]);
        StereoscopicPolyhedron.addVertex(StereoscopicCorners[BOTTOM_LEFT_FAR]);
        StereoscopicPolyhedron.addVertex(StereoscopicCorners[TOP_RIGHT_FAR]);
        StereoscopicPolyhedron.addVertex(StereoscopicCorners[TOP_LEFT_FAR]);

        StereoscopicPolyhedron.addVertex(StereoscopicCorners[TOP_LEFT_FAR]);
        StereoscopicPolyhedron.addVertex(StereoscopicCorners[BOTTOM_LEFT_FAR]);
        StereoscopicPolyhedron.addVertex(StereoscopicCorners[TOP_LEFT_NEAR]);
        StereoscopicPolyhedron.addVertex(StereoscopicCorners[BOTTOM_LEFT_NEAR]);

        StereoscopicPolyhedron.addVertex(StereoscopicCorners[BOTTOM_LEFT_NEAR]);
        StereoscopicPolyhedron.addVertex(StereoscopicCorners[BOTTOM_LEFT_FAR]);
        StereoscopicPolyhedron.addVertex(StereoscopicCorners[BOTTOM_RIGHT_NEAR]);
        StereoscopicPolyhedron.addVertex(StereoscopicCorners[BOTTOM_RIGHT_FAR]);

        StereoscopicPolyhedron.addVertex(StereoscopicCorners[BOTTOM_RIGHT_FAR]);
        StereoscopicPolyhedron.addVertex(StereoscopicCorners[TOP_LEFT_NEAR]);

        StereoscopicPolyhedron.addVertex(StereoscopicCorners[TOP_LEFT_NEAR]);
        StereoscopicPolyhedron.addVertex(StereoscopicCorners[TOP_RIGHT_NEAR]);
        StereoscopicPolyhedron.addVertex(StereoscopicCorners[TOP_LEFT_FAR]);
        StereoscopicPolyhedron.addVertex(StereoscopicCorners[TOP_RIGHT_FAR]);
    }

    public Stereoscopic update(BaseCamera camera)
    {
        return update(camera.getView());
    }

    public Stereoscopic update(Matrix4 view)
    {
        planes[LEFT].set(StereoscopicMatrix.get(0, 3) + StereoscopicMatrix.get(0, 0),
                StereoscopicMatrix.get(1, 3) + StereoscopicMatrix.get(1, 0),
                StereoscopicMatrix.get(2, 3) + StereoscopicMatrix.get(2, 0),
                StereoscopicMatrix.get(3, 3) + StereoscopicMatrix.get(3, 0));

        planes[RIGHT].set(StereoscopicMatrix.get(0, 3) - StereoscopicMatrix.get(0, 0),
                StereoscopicMatrix.get(1, 3) - StereoscopicMatrix.get(1, 0),
                StereoscopicMatrix.get(2, 3) - StereoscopicMatrix.get(2, 0),
                StereoscopicMatrix.get(3, 3) - StereoscopicMatrix.get(3, 0));

        planes[TOP].set(StereoscopicMatrix.get(0, 3) - StereoscopicMatrix.get(0, 1),
                StereoscopicMatrix.get(1, 3) - StereoscopicMatrix.get(1, 1),
                StereoscopicMatrix.get(2, 3) - StereoscopicMatrix.get(2, 1),
                StereoscopicMatrix.get(3, 3) - StereoscopicMatrix.get(3, 1));

        planes[BOTTOM].set(StereoscopicMatrix.get(0, 3) + StereoscopicMatrix.get(0, 1),
                StereoscopicMatrix.get(1, 3) + StereoscopicMatrix.get(1, 1),
                StereoscopicMatrix.get(2, 3) + StereoscopicMatrix.get(2, 1),
                StereoscopicMatrix.get(3, 3) + StereoscopicMatrix.get(3, 1));

        planes[NEAR].set(StereoscopicMatrix.get(0, 3) + StereoscopicMatrix.get(0, 2),
                StereoscopicMatrix.get(1, 3) + StereoscopicMatrix.get(1, 2),
                StereoscopicMatrix.get(2, 3) + StereoscopicMatrix.get(2, 2),
                StereoscopicMatrix.get(3, 3) + StereoscopicMatrix.get(3, 2));

        planes[FAR].set(StereoscopicMatrix.get(0, 3) - StereoscopicMatrix.get(0, 2),
                StereoscopicMatrix.get(1, 3) - StereoscopicMatrix.get(1, 2),
                StereoscopicMatrix.get(2, 3) - StereoscopicMatrix.get(2, 2),
                StereoscopicMatrix.get(3, 3) - StereoscopicMatrix.get(3, 2));

        Plane.intersection(planes[TOP], planes[LEFT], planes[FAR], StereoscopicCorners[TOP_LEFT_FAR]);
        Plane.intersection(planes[TOP], planes[RIGHT], planes[FAR], StereoscopicCorners[TOP_RIGHT_FAR]);
        Plane.intersection(planes[TOP], planes[RIGHT], planes[NEAR], StereoscopicCorners[TOP_RIGHT_NEAR]);
        Plane.intersection(planes[TOP], planes[LEFT], planes[NEAR], StereoscopicCorners[TOP_LEFT_NEAR]);

        Plane.intersection(planes[BOTTOM], planes[LEFT], planes[FAR], StereoscopicCorners[BOTTOM_LEFT_FAR]);
        Plane.intersection(planes[BOTTOM], planes[RIGHT], planes[FAR], StereoscopicCorners[BOTTOM_RIGHT_FAR]);
        Plane.intersection(planes[BOTTOM], planes[RIGHT], planes[NEAR], StereoscopicCorners[BOTTOM_RIGHT_NEAR]);
        Plane.intersection(planes[BOTTOM], planes[LEFT], planes[NEAR], StereoscopicCorners[BOTTOM_LEFT_NEAR]);

        StereoscopicPolygonVertices[TOP_LEFT].set(StereoscopicCorners[TOP_LEFT_NEAR].x, StereoscopicCorners[TOP_LEFT_NEAR].y);
        StereoscopicPolygonVertices[TOP_RIGHT].set(StereoscopicCorners[TOP_RIGHT_NEAR].x, StereoscopicCorners[TOP_RIGHT_NEAR].y);
        StereoscopicPolygonVertices[BOTTOM_RIGHT].set(StereoscopicCorners[BOTTOM_RIGHT_NEAR].x, StereoscopicCorners[BOTTOM_RIGHT_NEAR].y);
        StereoscopicPolygonVertices[BOTTOM_LEFT].set(StereoscopicCorners[BOTTOM_LEFT_NEAR].x, StereoscopicCorners[BOTTOM_LEFT_NEAR].y);

        return this;
    }

    public boolean intersects(Polygon2D polygon)
    {
        Vector2f center = polygon.getCenter();

        if (isInside(center.x, center.y, 0)){
            return true;
        }

        for (Vector2f v : polygon.getVertices()){
            if (isInside(v.x + polygon.getPosition().x, v.y + polygon.getPosition().y, planes[NEAR].d)){
                return true;
            }
        }

        return polygon.intersects(StereoscopicPolygon);
    }

    public boolean isInside(Polygon2D polygon)
    {
        if (!isInside(polygon.getCenter().x, polygon.getCenter().y, planes[NEAR].d)){
            return false;
        }

        boolean inside = false;

        Vector3f temp = Vector3f.TMP();
        for (Vector2f v : polygon.getVertices())
        {
            temp.set(v.x, v.y, planes[NEAR].d).addSelf(polygon.getPosition(), 0);
            inside = isInside(temp);

            if (!inside){
                break;
            }
        }

        return inside;
    }

    public boolean intersects(Polygon3D polyhedron)
    {
        Vector3f position = polyhedron.getPosition();

        if (isInside(position)){
            return true;
        }

        if (intersects(position, polyhedron.getWidth(), polyhedron.getHeight(), polyhedron.getThickness()))
        {
            if (polyhedron instanceof Sphere3D){
                return intersects(position, ((Sphere3D) polyhedron).getRadius());
            }

            for (Vector3f v : polyhedron.getVertices()){
                if (isInside(v.x + position.x, v.y + position.y, v.z + position.z)){
                    return true;
                }
            }

            return polyhedron.intersects(StereoscopicPolyhedron);
        }

        return false;
    }

    public boolean intersects(Vector3f position, float radius)
    {
        if (isInside(position)){
            return true;
        }

        for (Plane plane : planes){
            if (plane.normal.dot(position) + radius + plane.d < 0){
                return false;
            }
        }

        return true;
    }

    public boolean intersects(Vector3f position, float width, float height, float thickness)
    {
        if (isInside(position)){
            return true;
        }

        float halfWidth = width / 2;
        float halfHeight = height / 2;
        float halfThickness = thickness / 2;

        float x = position.x;
        float y = position.y;
        float z = position.z;

        for (Plane plane : planes)
        {
            if (plane.testPoint(x + halfWidth, y + halfHeight, z + halfThickness) == Plane.Side.BACK &&
                plane.testPoint(x + halfWidth, y + halfHeight, z - halfThickness) == Plane.Side.BACK &&
                plane.testPoint(x + halfWidth, y - halfHeight, z + halfThickness) == Plane.Side.BACK &&
                plane.testPoint(x + halfWidth, y - halfHeight, z - halfThickness) == Plane.Side.BACK &&
                plane.testPoint(x - halfWidth, y + halfHeight, z + halfThickness) == Plane.Side.BACK &&
                plane.testPoint(x - halfWidth, y + halfHeight, z - halfThickness) == Plane.Side.BACK &&
                plane.testPoint(x - halfWidth, y - halfHeight, z + halfThickness) == Plane.Side.BACK &&
                plane.testPoint(x - halfWidth, y - halfHeight, z - halfThickness) == Plane.Side.BACK)
                return false;
        }

        return true;
    }

    public boolean isInside(Polygon3D polyhedron)
    {
        if (!isInside(polyhedron.getPosition())){
            return false;
        }

        boolean inside = false;

        Vector3f temp = Vector3f.TMP();
        for (Vector3f v : polyhedron.getVertices())
        {
            temp.set(v).addSelf(polyhedron.getPosition());
            inside = isInside(temp);

            if (!inside){
                break;
            }
        }

        return inside;
    }

    public boolean isInside(Vector3f point, float width, float height, float thickness)
    {
        if (!isInside(point)){
            return false;
        }

        for (Plane plane : planes)
        {
            float m = plane.normal.dot(point);
            float n = width / 2 * MathUtils.abs(point.x - width / 2) +
                      height / 2 * MathUtils.abs(point.y - height / 2) +
                      thickness / 2 * MathUtils.abs(point.z - thickness / 2);

            if (m + n < 0)
                return false;
        }

        return true;
    }

    public boolean isInside(Vector3f point, float radius)
    {
        if (!isInside(point)){
            return false;
        }

        for (Plane plane : planes)
        {
            float m = plane.normal.dot(point);
            float n = radius * MathUtils.abs(point.x - radius) +
                      radius * MathUtils.abs(point.y - radius) +
                      radius * MathUtils.abs(point.z - radius);

            if (m + n < 0){
                return false;
            }
        }

        return true;
    }

    public boolean isInside(Vector3f point)
    {
        return isInside(point.x, point.y, point.z);
    }

    public boolean isInside(float x, float y, float z)
    {
        boolean inside = false;

        for (Plane plane : planes)
        {
            if (!(inside = plane.testPoint(x, y, z) == Plane.Side.FRONT))
                break;
        }

        return inside;
    }

    public Plane getPlane(int id)
    {
        return planes[id];
    }

    public Vector3f getCorner(int id)
    {
        return StereoscopicCorners[id];
    }

    public Vector2f getCorner2D(int id)
    {
        return StereoscopicPolygonVertices[id];
    }

    public Polygon2D getPolygon()
    {
        return StereoscopicPolygon;
    }

    public Polygon3D getPolyhedron()
    {
        return StereoscopicPolyhedron;
    }

    @Override
    public String toString()
    {
        return "Stereoscopic{" +
               "planeLeft=" + getPlane(LEFT) +
               ", planeRight=" + getPlane(RIGHT) +
               ", planeTop=" + getPlane(TOP) +
               ", planeBottom=" + getPlane(BOTTOM) +
               ", planeNear=" + getPlane(NEAR) +
               ", planeFar=" + getPlane(FAR) +
               '}';
    }
}
