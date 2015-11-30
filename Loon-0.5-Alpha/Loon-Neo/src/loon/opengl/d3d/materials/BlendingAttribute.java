package loon.opengl.d3d.materials;

import loon.opengl.GL20;


public class BlendingAttribute extends Material.Attribute {
	
	public final static String Alias = "blended";
	public final static long Type = register(Alias);
	
	public final static boolean is(final long mask) {
		return (mask & Type) == mask;
	}
 
	public int sourceFunction;
	public int destFunction;
	public float opacity = 1.f;

	public BlendingAttribute() { 
		this(null); 
	}

	public BlendingAttribute(final int sourceFunc, final int destFunc, final float opacity) {
		super(Type);
		sourceFunction = sourceFunc;
		destFunction = destFunc;
		this.opacity = opacity; 
	}
	
	public BlendingAttribute(final int sourceFunc, final int destFunc) {
		this(sourceFunc, destFunc, 1.f);
	}
	
	public BlendingAttribute(final BlendingAttribute copyFrom) {
		this(copyFrom == null ? GL20.GL_SRC_ALPHA : copyFrom.sourceFunction,
			copyFrom == null ? GL20.GL_ONE_MINUS_SRC_ALPHA : copyFrom.destFunction,
			copyFrom == null ? 1.f : copyFrom.opacity);
	}
	
	@Override
	public BlendingAttribute cpy () {
		return new BlendingAttribute(this);
	}
	
	@Override
	protected boolean equals (final Material.Attribute other) {
		return ((BlendingAttribute)other).sourceFunction == sourceFunction && 
			((BlendingAttribute)other).destFunction == destFunction; 
	}
}