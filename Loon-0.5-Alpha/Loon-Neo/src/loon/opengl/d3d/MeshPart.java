package loon.opengl.d3d;

import loon.IDGenerator;
import loon.geom.Vector2f;
import loon.geom.Vector3f;
import loon.utils.TArray;

public class MeshPart {

    private Material material;

    private TArray<Face>    faces;
    private TArray<Vector3f> vertices;
    private TArray<Vector3f> normals;
    private TArray<Vector2f> texcoords;

    private int id;

    private boolean preferStatic;
    private boolean wireFrame;

    public MeshPart()
    {
        material = new Material("Default");

        faces = new TArray<Face>();
        vertices = new TArray<Vector3f>();
        normals = new TArray<Vector3f>();
        texcoords = new TArray<Vector2f>();

        id = IDGenerator.generate();

        preferStatic = false;
        wireFrame = false;
    }

    public Material getMaterial()
    {
        return material;
    }

    public void setMaterial(Material material)
    {
        this.material = material;
    }

    public TArray<Face> getFaces()
    {
        return faces;
    }

    public TArray<Vector3f> getVertices()
    {
        return vertices;
    }

    public TArray<Vector3f> getNormals()
    {
        return normals;
    }

    public TArray<Vector2f> getTexcoords()
    {
        return texcoords;
    }

    public int getNumberOfVertices()
    {
        return getFaces().size * 3;
    }

    public int getID()
    {
        return id;
    }

    public boolean prefersStatic()
    {
        return preferStatic;
    }

    public void setPreferStatic(boolean preferStatic)
    {
        this.preferStatic = preferStatic;
    }

    public boolean isWireFrame()
    {
        return wireFrame;
    }

    public void setWireFrame(boolean wireFrame)
    {
        this.wireFrame = wireFrame;
    }

    @Override
    public boolean equals(Object object)
    {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        MeshPart mesh = (MeshPart) object;

        return id == mesh.getID() &&
               preferStatic == mesh.prefersStatic() &&
               isWireFrame() == mesh.isWireFrame();
    }

    @Override
    public int hashCode()
    {
        int result = id;
        result = 31 * result + (preferStatic ? 1 : 0);
        result = 31 * result + (isWireFrame() ? 1 : 0);
        return result;
    }
}
