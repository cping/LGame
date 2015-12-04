package loon.live2d.type;

public class LDRect
{
    public int a;
    public int b;
    public int c;
    public int d;
    
    public LDRect() {
    }
    
    public LDRect(final int x, final int y, final int width, final int height) {
        this.a = x;
        this.b = y;
        this.c = width;
        this.d = height;
    }
    
    public LDRect(final LDRect r) {
        this.a = r.a;
        this.b = r.b;
        this.c = r.c;
        this.d = r.d;
    }
    
    public float getCenterX() {
        return 0.5f * (this.a + this.a + this.c);
    }
    
    public float getCenterY() {
        return 0.5f * (this.b + this.b + this.d);
    }
    
    public int getRight() {
        return this.a + this.c;
    }
    
    public int getBottom() {
        return this.b + this.d;
    }
    
    public void setRect(final int x, final int y, final int width, final int height) {
        this.a = x;
        this.b = y;
        this.c = width;
        this.d = height;
    }
    
    public void setRect(final LDRect r) {
        this.a = r.a;
        this.b = r.b;
        this.c = r.c;
        this.d = r.d;
    }
}
