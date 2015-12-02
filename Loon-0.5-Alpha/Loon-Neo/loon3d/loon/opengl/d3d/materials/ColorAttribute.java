package loon.opengl.d3d.materials;

import loon.canvas.LColor;

public class ColorAttribute extends Material.Attribute {
	
    public final static String DiffuseAlias = "diffuseColor";
    public final static long Diffuse = register(DiffuseAlias);
    public final static String SpecularAlias = "specularColor";
    public final static long Specular = register(SpecularAlias);
    public final static String AmbientAlias = "ambientColor";
    public static final long Ambient = register("ambientColor");
    public final static String EmissiveAlias = "emissiveColor";
    public static final long Emissive = register("emissiveColor");

    protected static long Mask = Ambient | Diffuse | Specular | Emissive;

    public final static boolean is(final long mask) {
        return (mask & Mask) != 0;
    }

    public final static ColorAttribute createDiffuse(final LColor color) {
        return new ColorAttribute(Diffuse, color);
    }

    public final static ColorAttribute createDiffuse(float r, float g, float b, float a) {
        return new ColorAttribute(Diffuse, r, g, b, a);
    }

    public final static ColorAttribute createSpecular(final LColor color) {
        return new ColorAttribute(Specular, color);
    }

    public final static ColorAttribute createSpecular(float r, float g, float b, float a) {
        return new ColorAttribute(Specular, r, g, b, a);
    }

    public final LColor color = new LColor(LColor.white);

    public ColorAttribute(final long type) {
        super(type);
        if (!is(type)){
            throw new RuntimeException("Invalid type specified");
        }
    }

    public ColorAttribute(final long type, final LColor color) {
        this(type);
        if (color != null){
            this.color.setColor(color);
        }
    }

    public ColorAttribute(final long type, float r, float g, float b, float a) {
        this(type);
        this.color.setColor(r,g,b,a);
    }

    public ColorAttribute(final ColorAttribute copyFrom) {
        this(copyFrom.type, copyFrom.color);
    }

    @Override
    public Material.Attribute cpy () {
        return new ColorAttribute(this);
    }

    @Override
    protected boolean equals (Material.Attribute other) {
        return ((ColorAttribute)other).color.equals(color);
    }
}
