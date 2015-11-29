package loon.opengl.d3d;

import loon.LSystem;
import loon.geom.Transform;
import loon.utils.TArray;

public class Model {
	
    private TArray<MeshPart> meshes;

    public Model()
    {
        meshes = new TArray<MeshPart>();
    }


    public static Model load(String filePath)
    {
        if (LSystem.getExtension(filePath).equalsIgnoreCase("obj")){
            return new OBJModel(filePath);
        }
        //开发中……
        return null;
    }

    public void render(Transform transform)
    {
       //开发中……
    }

    public void dispose()
    {      //开发中……
    }

    public TArray<MeshPart> getMeshes()
    {
        return meshes;
    }

}
