package loon.live2d.type;

public class LDColor
{
    public int a;
    
    public LDColor() {
    }
    
    public LDColor(int color, final boolean useAlpha) {
        if (!useAlpha) {
            color |= 0xFF000000;
        }
        this.a = color;
    }
}
