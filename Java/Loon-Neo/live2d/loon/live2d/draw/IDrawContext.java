package loon.live2d.draw;

public class IDrawContext
{
    public IDrawData e;
    public int f;
    public int g;
    public float h;
    public boolean[] i;
    public float j;
    public boolean k;
    public float l;
    
    protected IDrawContext(final IDrawData src) {
        this.i = new boolean[1];
        this.k = true;
        this.l = 1.0f;
        this.e = src;
    }
    
    public boolean b() {
        return this.i[0];
    }
    
    public boolean exist() {
        return this.k && !this.i[0];
    }
    
    public IDrawData d() {
        return this.e;
    }
}
