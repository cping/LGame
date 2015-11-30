package loon.opengl.d3d.models;

import loon.geom.Vector2f;

public class ModelTexture {
	
	public final static int USAGE_UNKNOWN = 0;
	public final static int USAGE_NONE = 1;
	public final static int USAGE_DIFFUSE = 2;
	public final static int USAGE_EMISSIVE = 3;
	public final static int USAGE_AMBIENT = 4;
	public final static int USAGE_SPECULAR = 5;
	public final static int USAGE_SHININESS = 6;
	public final static int USAGE_NORMAL = 7;
	public final static int USAGE_BUMP = 8;
	public final static int USAGE_TRANSPARENCY = 9;
	public final static int USAGE_REFLECTION = 10;
	
	public String id;
	public String fileName;
	public Vector2f uvTranslation;
	public Vector2f uvScaling;
	public int usage;
	
}
