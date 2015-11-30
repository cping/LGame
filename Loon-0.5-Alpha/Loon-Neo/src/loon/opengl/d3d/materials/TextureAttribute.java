package loon.opengl.d3d.materials;

import loon.LTexture;


public class TextureAttribute extends Material.Attribute {
	
	public final static String DiffuseAlias = "diffuseTexture";
	public final static long Diffuse = register(DiffuseAlias);
	public final static String SpecularAlias = "specularTexture";
	public final static long Specular = register(SpecularAlias);
	public final static String BumpAlias = "bumpTexture";
	public final static long Bump = register(BumpAlias);
	public final static String NormalAlias = "normalTexture";
	public final static long Normal = register(NormalAlias);
	
	protected static long Mask = Diffuse | Specular | Bump | Normal;
	
	public final static boolean is(final long mask) {
		return (mask & Mask) != 0;
	}
	
	public static TextureAttribute createDiffuse(final LTexture texture) {
		return new TextureAttribute(Diffuse, texture);
	}
	
	public static TextureAttribute createSpecular(final LTexture texture) {
		return new TextureAttribute(Specular, texture);
	}
	
	public final TextureDescriptor textureDescription;
	
	public TextureAttribute(final long type, final TextureDescriptor textureDescription) {
		super(type);
		if (!is(type)){
			throw new RuntimeException("Invalid type specified");
		}
		this.textureDescription = textureDescription; 
	}
	
	public TextureAttribute(final long type) {
		this(type, new TextureDescriptor());
	}
	
	public TextureAttribute(final long type, final LTexture texture) {
		this(type, new TextureDescriptor(texture));
	}
	
	public TextureAttribute(final TextureAttribute copyFrom) {
		this(copyFrom.type, copyFrom.textureDescription);
	}
	
	@Override
	public Material.Attribute cpy () {
		return new TextureAttribute(this);
	}

	@Override
	protected boolean equals (Material.Attribute other) {
		return ((TextureAttribute)other).textureDescription.equals(textureDescription);
	}
}
