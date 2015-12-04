package loon.live2d.type;

public class LDRectF
{
    public float a;
    public float b;
    public float c;
    public float d;
    
    public LDRectF() {
    }
    
    public LDRectF(final float x, final float y, final float width, final float height) {
        this.a = x;
        this.b = y;
        this.c = width;
        this.d = height;
    }
    
    public float getCenterX() {
        return this.a + 0.5f * this.c;
    }
    
    public float getCenterY() {
        return this.b + 0.5f * this.d;
    }
    
    public float getRight() {
        return this.a + this.c;
    }
    
    public float getBottom() {
        return this.b + this.d;
    }
    
    public void setRect(final float x, final float y, final float width, final float height) {
        this.a = x;
        this.b = y;
        this.c = width;
        this.d = height;
    }
    
    public void setRect(final LDRectF r) {
        this.a = r.a;
        this.b = r.b;
        this.c = r.c;
        this.d = r.d;
    }
    
    public boolean contains(final float x, final float y) {
        return this.a <= x && this.b <= y && x <= this.a + this.c && y <= this.b + this.d;
    }
}
