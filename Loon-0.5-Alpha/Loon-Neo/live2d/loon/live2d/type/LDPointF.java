package loon.live2d.type;

public class LDPointF
{
    public float a;
    public float b;
    
    public LDPointF() {
    }
    
    public LDPointF(final float x, final float y) {
        this.a = x;
        this.b = y;
    }
    
    public LDPointF(final LDPointF pt) {
        this.a = pt.a;
        this.b = pt.b;
    }
    
    public void setPoint(final float x, final float y) {
        this.a = x;
        this.b = y;
    }
    
    public void setPoint(final LDPointF pt) {
        this.a = pt.a;
        this.b = pt.b;
    }
}
