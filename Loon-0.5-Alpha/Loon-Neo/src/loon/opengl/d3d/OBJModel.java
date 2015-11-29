package loon.opengl.d3d;

import loon.BaseIO;
import loon.LTexture;
import loon.canvas.LColor;
import loon.geom.Vector2f;
import loon.geom.Vector3f;
import loon.utils.ObjectMap;
import loon.utils.StringUtils;
import loon.utils.TArray;

public class OBJModel extends Model
{
    private TArray<Vector3f> vertices;
    private TArray<Vector3f> normals;
    private TArray<Vector2f> texcoords;

    private ObjectMap<String, Material> materials;

    public OBJModel(String path)
    {
        super();

        vertices = new TArray<Vector3f>();
        normals = new TArray<Vector3f>();
        texcoords = new TArray<Vector2f>();
        materials = new ObjectMap<String, Material>();

        vertices.add(new Vector3f());
        normals.add(new Vector3f());
        texcoords.add(new Vector2f());

        parseOBJModel(path);
    }

    private void parseOBJModel(String path)
    {
        Material material;
        MeshPart mesh = null;

        String[] lines = StringUtils.split(BaseIO.loadText(path),'\n');

        if (lines != null)
        {
            for (String line : lines)
            {
                if (line.startsWith("v "))
                    parseVertex(line);

                else if (line.startsWith("vn "))
                    parseNormal(line);

                else if (line.startsWith("vt "))
                    parseTextureCoords(line);

                else if (line.startsWith("f "))
                    parseFace(line, mesh);

                else if (line.startsWith("usemtl "))
                {
                    material = materials.get(line.replaceAll("usemtl ", "").trim());
                    if (mesh != null) getMeshes().add(mesh);
                    mesh = new MeshPart();
                    mesh.setMaterial(material);
                }

                else if (line.startsWith("mtllib "))
                    parseMaterialLib(path, line);
            }
        }

        getMeshes().add(mesh);

        vertices = null;
        normals = null;
        texcoords = null;
        materials = null;
    }

    private void parseVertex(String line)
    {
        String[] values = line.split(" ");

        float x = Float.parseFloat(values[1]);
        float y = Float.parseFloat(values[2]);
        float z = Float.parseFloat(values[3]);

        vertices.add(new Vector3f(x, y, z));
    }

    private void parseNormal(String line)
    {
        String[] values = line.split(" ");

        float x = Float.parseFloat(values[1]);
        float y = Float.parseFloat(values[2]);
        float z = Float.parseFloat(values[3]);

        normals.add(new Vector3f(x, y, z));
    }

    private void parseTextureCoords(String line)
    {
        String[] values = line.split(" ");

        float x = Float.parseFloat(values[1]);
        float y = Float.parseFloat(values[2]);

        texcoords.add(new Vector2f(x, 1 - y));
    }

    private void parseFace(String line, MeshPart mesh)
    {
        String[] values = line.split(" ");

        float v1 = Float.parseFloat(values[1].split("/")[0]);
        float v2 = Float.parseFloat(values[2].split("/")[0]);
        float v3 = Float.parseFloat(values[3].split("/")[0]);

        float vt1, vt2, vt3;

        Material material = mesh.getMaterial();

        if (material.getDiffuseMap() != null)
        {
            vt1 = Float.parseFloat(values[1].split("/")[1]);
            vt2 = Float.parseFloat(values[2].split("/")[1]);
            vt3 = Float.parseFloat(values[3].split("/")[1]);
        }
        else
        {
            vt1 = vt2 = vt3 = 0;
        }

        float vn1 = Float.parseFloat(values[1].split("/")[2]);
        float vn2 = Float.parseFloat(values[2].split("/")[2]);
        float vn3 = Float.parseFloat(values[3].split("/")[2]);

        mesh.getVertices().add(vertices.get((int) v1));
        mesh.getVertices().add(vertices.get((int) v2));
        mesh.getVertices().add(vertices.get((int) v3));

        mesh.getNormals().add(normals.get((int) vn1));
        mesh.getNormals().add(normals.get((int) vn2));
        mesh.getNormals().add(normals.get((int) vn3));

        mesh.getTexcoords().add(texcoords.get((int) vt1));
        mesh.getTexcoords().add(texcoords.get((int) vt2));
        mesh.getTexcoords().add(texcoords.get((int) vt3));

        Face face = new Face();
        face.vertexIndex.x = mesh.getVertices().size - 3;
        face.vertexIndex.y = mesh.getVertices().size - 2;
        face.vertexIndex.z = mesh.getVertices().size - 1;

        face.normalIndex.x = mesh.getNormals().size - 3;
        face.normalIndex.y = mesh.getNormals().size - 2;
        face.normalIndex.z = mesh.getNormals().size - 1;

        face.texcoordIndex.x = mesh.getTexcoords().size - 3;
        face.texcoordIndex.y = mesh.getTexcoords().size - 2;
        face.texcoordIndex.z = mesh.getTexcoords().size - 1;

        mesh.getFaces().add(face);
    }

    private void parseMaterialLib(String objFile, String mtlLine)
    {
    	String mtlLib = BaseIO.loadText(mtlLine.split(" ", 2)[1].trim());

        String[] lines = StringUtils.split(BaseIO.loadText(mtlLib),'\n');
        
        Material material = null;

        if (lines != null)
        {
            for (String line : lines)
            {
                if (line.startsWith("newmtl "))
                {
                    if (material != null)
                        materials.put(material.getName(), material);

                    material = new Material();
                    material.setName(line.split(" ", 2)[1]);
                }

                if (line.startsWith("Ka "))
                    parseMaterialAmbientColor(line, material);

                if (line.startsWith("Kd "))
                    parseMaterialDiffuseColor(line, material);

                if (line.startsWith("Ks "))
                    parseMaterialSpecularColor(line, material);

                if (line.startsWith("d "))
                    if (material != null)
                        material.setDissolve(Float.parseFloat(line.split(" ")[1]));

                if (line.startsWith("Ns "))
                    if (material != null)
                        material.setSpecularPower(Float.parseFloat(line.split(" ")[1]));

                if (line.startsWith("map_Kd"))
                    if (material != null)
                        parseMaterialDiffuseMap(mtlLib, line, material);
            }
        }

        if (material != null)
            materials.put(material.getName(), material);
    }

    private void parseMaterialAmbientColor(String line, Material material)
    {
        String[] values = line.split(" ");

        float r = Float.parseFloat(values[1]);
        float g = Float.parseFloat(values[2]);
        float b = Float.parseFloat(values[3]);

        material.setAmbient(new LColor(r, g, b));
    }

    private void parseMaterialDiffuseColor(String line, Material material)
    {
        String[] values = line.split(" ");

        float r = Float.parseFloat(values[1]);
        float g = Float.parseFloat(values[2]);
        float b = Float.parseFloat(values[3]);

        material.setDiffuse(new LColor(r, g, b));
    }

    private void parseMaterialSpecularColor(String line, Material material)
    {
        String[] values = line.split(" ");

        float r = Float.parseFloat(values[1]);
        float g = Float.parseFloat(values[2]);
        float b = Float.parseFloat(values[3]);

        material.setSpecular(new LColor(r, g, b));
    }

    private void parseMaterialDiffuseMap(String mtlLib, String line, Material material)
    {
        String fileName = line.split(" ", 2)[1].trim();
        String filePath = fileName;
        material.setDiffuseMap(LTexture.createTexture(filePath));
    }
}
