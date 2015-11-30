package loon.opengl.d3d.models;

import loon.canvas.LColor;
import loon.utils.TArray;

public class ModelMaterial {
	public enum MaterialType {
		Lambert,
		Phong
	}
	
	public String id;
	
	public MaterialType type;
	
	public LColor ambient;
	public LColor diffuse;
	public LColor specular;
	public LColor emissive;
	
	public float shininess;
	public float opacity = 1.f;
	
	public TArray<ModelTexture> textures;
}
