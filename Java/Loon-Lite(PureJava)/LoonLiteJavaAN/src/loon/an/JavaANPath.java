package loon.an;

import android.graphics.Path;

public class JavaANPath implements loon.canvas.Path {

    private boolean setMoved = false;
    Path path;

    JavaANPath() {
        path = new Path();
    }

    @Override
    public loon.canvas.Path bezierTo(float c1x, float c1y, float c2x, float c2y, float x, float y) {
        path.cubicTo(c1x, c1y, c2x, c2y, x, y);
        return this;
    }

    @Override
    public loon.canvas.Path close() {
        path.close();
        setMoved = false;
        return this;
    }

    @Override
    public loon.canvas.Path lineTo(float x, float y) {
        if (setMoved) {
            path.lineTo(x, y);
        } else {
            moveTo(x, y);
        }
        return this;
    }

    @Override
    public loon.canvas.Path moveTo(float x, float y) {
        path.moveTo(x, y);
        setMoved = true;
        return this;
    }

    @Override
    public loon.canvas.Path quadraticCurveTo(float cpx, float cpy, float x, float y) {
        path.quadTo(cpx, cpy, x, y);
        return this;
    }

    @Override
    public loon.canvas.Path reset() {
        path.reset();
        setMoved = false;
        return this;
    }
}
