package loon.live2d.type;

public class LDPoint
{
    public int a;
    public int b;
    
    public LDPoint() {
    }
    
    public LDPoint(final int x, final int y) {
        this.a = x;
        this.b = y;
    }
    
    public LDPoint(final LDPoint pt) {
        this.a = pt.a;
        this.b = pt.b;
    }
    
    public void setPoint(final LDPoint pt) {
        this.a = pt.a;
        this.b = pt.b;
    }
    
    public void setPoint(final int x, final int y) {
        this.a = x;
        this.b = y;
    }
}
