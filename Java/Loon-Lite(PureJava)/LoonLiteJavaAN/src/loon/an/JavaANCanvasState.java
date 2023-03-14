package loon.an;

import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;

import loon.canvas.Canvas;

public class JavaANCanvasState {

    final static PorterDuffXfermode[] xfermodes;

    static int PAINT_FLAGS = Paint.ANTI_ALIAS_FLAG | Paint.SUBPIXEL_TEXT_FLAG;

    Paint paint;
    int fillColor;
    int strokeColor;
    JavaANGradient gradient;
    JavaANPattern pattern;
    float alpha;
    Canvas.Composite composite;

    static {
        xfermodes = new PorterDuffXfermode[Canvas.Composite.values().length];
        for (Canvas.Composite composite : Canvas.Composite.values()) {
            xfermodes[composite.ordinal()] = new PorterDuffXfermode(PorterDuff.Mode.valueOf(composite.name()));
        }
    }

    JavaANCanvasState() {
        this(new Paint(PAINT_FLAGS), 0xff000000, 0xffffffff, null, null, Canvas.Composite.SRC_OVER, 1f);
    }

    JavaANCanvasState(JavaANCanvasState toCopy) {
        this(copy(toCopy.paint), toCopy.fillColor, toCopy.strokeColor, toCopy.gradient, toCopy.pattern,
                toCopy.composite, toCopy.alpha);
    }

    JavaANCanvasState(Paint paint, int fillColor, int strokeColor, JavaANGradient gradient, JavaANPattern pattern,
                      Canvas.Composite composite, float alpha) {
        this.paint = paint;
        this.fillColor = fillColor;
        this.strokeColor = strokeColor;
        this.gradient = gradient;
        this.pattern = pattern;
        this.composite = composite;
        this.alpha = alpha;
    }

    void setFillColor(int color) {
        this.fillColor = color;
    }

    void setFillGradient(JavaANGradient gradient) {
        this.gradient = gradient;
    }

    void setFillPattern(JavaANPattern pattern) {
        this.pattern = pattern;
    }

    void setLineCap(Canvas.LineCap cap) {
        paint.setStrokeCap(convertCap(cap));
    }

    void setLineJoin(Canvas.LineJoin join) {
        paint.setStrokeJoin(convertJoin(join));
    }

    void setMiterLimit(float miter) {
        paint.setStrokeMiter(miter);
    }

    void setStrokeColor(int color) {
        this.strokeColor = color;
    }

    void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    void setCompositeOperation(Canvas.Composite composite) {
        this.composite = composite;
    }

    void setStrokeWidth(float strokeWidth) {
        paint.setStrokeWidth(strokeWidth);
    }

    Paint prepareFill() {
        paint.setStyle(Paint.Style.FILL);
        paint.setXfermode(convertComposite(composite));
        if (gradient != null) {
            paint.setShader(gradient.shader);
        } else if (pattern != null) {
            paint.setShader(pattern.shader);
        } else {
            paint.setShader(null);
            paint.setColor(fillColor);
            if (alpha < 1) {
                paint.setAlpha((int) (alpha * (fillColor >>> 24)));
            }
        }
        return paint;
    }

    Paint prepareStroke() {
        paint.setShader(null);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(strokeColor);
        if (alpha < 1f) {
            paint.setAlpha((int) (alpha * (strokeColor >>> 24)));
        }
        paint.setXfermode(convertComposite(composite));
        return paint;
    }

    Paint prepareImage() {
        paint.setAlpha((int) (alpha * 255));
        paint.setXfermode(convertComposite(composite));
        return paint;
    }

    private Paint.Cap convertCap(Canvas.LineCap cap) {
        switch (cap) {
            case BUTT:
                return Paint.Cap.BUTT;
            case ROUND:
                return Paint.Cap.ROUND;
            case SQUARE:
                return Paint.Cap.SQUARE;
        }
        return Paint.Cap.BUTT;
    }

    private Xfermode convertComposite(Canvas.Composite composite) {
        return xfermodes[composite.ordinal()];
    }

    private Paint.Join convertJoin(Canvas.LineJoin join) {
        switch (join) {
            case BEVEL:
                return Paint.Join.BEVEL;
            case MITER:
                return Paint.Join.MITER;
            case ROUND:
                return Paint.Join.ROUND;
        }
        return Paint.Join.MITER;
    }

    private static Paint copy(Paint source) {
        Paint clone = new Paint(PAINT_FLAGS);
        clone.set(source);
        return clone;
    }
}
