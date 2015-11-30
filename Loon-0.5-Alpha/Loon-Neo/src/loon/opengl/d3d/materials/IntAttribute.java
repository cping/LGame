package loon.opengl.d3d.materials;

public class IntAttribute extends Material.Attribute {
	
	public static final String CullFaceAlias = "cullface";
	public static final long CullFace = register(CullFaceAlias);
	
	public static IntAttribute createCullFace(int value) {
		return new IntAttribute(CullFace, value);
	}
	
	public int value;
	
	public IntAttribute(long type) {
		super(type);
	}
	
	public IntAttribute(long type, int value) {
		super(type);
		this.value = value;
	}

	@Override
	public Material.Attribute cpy () {
		return new IntAttribute(type, value);
	}

	@Override
	protected boolean equals (Material.Attribute other) {
		return ((IntAttribute)other).value == value;
	}
}