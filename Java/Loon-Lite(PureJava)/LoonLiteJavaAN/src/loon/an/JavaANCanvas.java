package loon.an;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;

import java.util.LinkedList;

import loon.Graphics;
import loon.canvas.Canvas;
import loon.canvas.Gradient;
import loon.canvas.Image;
import loon.canvas.LColor;
import loon.canvas.Path;
import loon.font.LFont;
import loon.font.TextLayout;
import loon.geom.Affine2f;
import loon.opengl.BlendMethod;
import loon.opengl.TextureSource;
import loon.utils.MathUtils;
import loon.utils.Scale;

public class JavaANCanvas extends Canvas {

    private final Matrix m = new Matrix();
    private final Rect srcR = new Rect();
    private final RectF dstR = new RectF();
    private final float[] _transform = new float[9];

    protected android.graphics.Canvas context;
    private final LinkedList<JavaANCanvasState> paintStack = new LinkedList<JavaANCanvasState>();
    private final boolean graphicsMain;
    private boolean saveClip;

    public JavaANCanvas(Graphics gfx, JavaANImage image) {
        this(gfx, image, false);
    }

    public JavaANCanvas(Graphics gfx, JavaANImage image, boolean gm) {
        super(gfx, image);
        if (this.image != null) {
            this.image = image;
            this.width = image.getWidth();
            this.height = image.getHeight();
            this.context = new android.graphics.Canvas(image.anImage());
            this.isDirty = true;
        } else {
            this.isDirty = false;
        }
        this.graphicsMain = gm;
        setContextInit(context);
    }

    public void updateContext(android.graphics.Canvas g) {
        this.context = g;
    }

    public void setContextInit(android.graphics.Canvas g) {
        this.updateContext(g);
        if (this.context != null) {
            paintStack.addFirst(new JavaANCanvasState());
            float factor = image.scale().factor;
            scale(factor, factor);
            context.drawColor(0, PorterDuff.Mode.SRC);
        }
    }

    void draw(Bitmap bitmap, float x, float y, float w, float h, float x1, float y1, float w1, float h1) {
        srcR.set(MathUtils.floor(x1), MathUtils.floor(y1), MathUtils.floor(x1 + w1), MathUtils.floor(y1 + h1));
        dstR.set(x, y, x + w, y + h);
        context.drawBitmap(bitmap, srcR, dstR, currentState().prepareImage());
        isDirty = true;
    }

    public Canvas setColor(LColor color) {
        int argb = color.getARGB();
        this.setStrokeColor(argb);
        this.setFillColor(argb);
        return this;
    }

    protected int getLColorToAN(LColor c) {
        if (c == null) {
            return Color.WHITE;
        }
        return c.getARGB();
    }

    private LColor getANToLColor(int c) {
        return new LColor(c);
    }

    @Override
    public LColor getStroketoLColor() {
        return getANToLColor(currentState().strokeColor);
    }

    @Override
    public Canvas setColor(int r, int g, int b, int a) {
        int argb = LColor.getARGB(r, g, b, a);
        this.setStrokeColor(argb);
        this.setFillColor(argb);
        return this;
    }

    @Override
    public Canvas setColor(int r, int g, int b) {
        int rgb = LColor.getRGB(r, g, b);
        this.setStrokeColor(rgb);
        this.setFillColor(rgb);
        return this;
    }

    @Override
    public int getStrokeColor() {
        return currentState().strokeColor;
    }

    @Override
    public LColor getFilltoLColor() {
        return new LColor(currentState().fillColor);
    }

    @Override
    public int getFillColor() {
        return currentState().fillColor;
    }

    @Override
    public Canvas updateDirty() {
        this.isDirty = true;
        return this;
    }

    public float alpha() {
        return currentState().alpha;
    }

    @Override
    public Canvas clear() {
        context.drawColor(0, PorterDuff.Mode.SRC);
        isDirty = true;
        return this;
    }

    @Override
    public Canvas clear(LColor color) {
        context.drawColor(color.getRGB(), PorterDuff.Mode.SRC);
        isDirty = true;
        return this;
    }

    @Override
    public Canvas clearRect(float x, float y, float width, float height) {
        context.save();
        context.clipRect(x, y, x + width, y + height);
        context.drawColor(0, PorterDuff.Mode.SRC);
        context.restore();
        isDirty = true;
        return this;
    }

    @Override
    public Canvas clearRect(float x, float y, float width, float height, LColor color) {
        context.save();
        context.clipRect(x, y, x + width, y + height);
        context.drawColor(color == null ? 0 : color.getABGR(), PorterDuff.Mode.SRC);
        context.restore();
        isDirty = true;
        return this;
    }

    @Override
    public Canvas clip(Path clipPath) {
        context.save();
        context.clipPath(((JavaANPath) clipPath).path);
        saveClip = true;
        isDirty = true;
        return this;
    }

    @Override
    public Canvas clipRect(float x, float y, float width, float height) {
        context.save();
        context.clipRect(x, y, x + width, y + height);
        saveClip = true;
        isDirty = true;
        return this;
    }

    @Override
    public Canvas resetClip() {
        context.clipRect(0, 0, width, height);
        if (saveClip) {
            context.restore();
        }
        isDirty = true;
        return this;
    }

    @Override
    public JavaANPath createPath() {
        return new JavaANPath();
    }

    @Override
    public JavaANGradient createGradient(Gradient.Config cfg) {
        return new JavaANGradient(cfg);
    }

    @Override
    public Canvas setBlendMethod(int blend) {
        Composite mode;
        switch (blend) {
            case BlendMethod.MODE_ADD:
            case BlendMethod.MODE_ALPHA_ONE:
                mode = Composite.ADD;
                break;
            case BlendMethod.MODE_MULTIPLY:
            case BlendMethod.MODE_COLOR_MULTIPLY:
                mode = Composite.MULTIPLY;
                break;
            case BlendMethod.MODE_ALPHA:
            case BlendMethod.MODE_NORMAL:
            default:
                mode = Composite.SRC_OVER;
                break;
        }
        currentState().setCompositeOperation(mode);
        return this;
    }

    @Override
    public Canvas drawOval(float x, float y, float w, float h) {
        context.drawOval(x, y, w, h, currentState().prepareStroke());
        isDirty = true;
        return this;
    }

    @Override
    public Canvas drawLine(float x0, float y0, float x1, float y1) {
        context.drawLine(x0, y0, x1, y1, currentState().prepareStroke());
        isDirty = true;
        return this;
    }

    @Override
    public Canvas drawPoint(float x, float y) {
        context.drawPoint(x, y, currentState().prepareStroke());
        isDirty = true;
        return this;
    }

    @Override
    public Canvas drawArc(float x, float y, float w, float h, float startAngle, float endAngle, LColor color) {
        context.drawArc(x, y, x + w, y + h, startAngle, endAngle, true, currentState().prepareStroke());
        isDirty = true;
        return this;
    }

    @Override
    public Canvas drawOval(float x, float y, float w, float h, LColor color) {
        int tmp = getStrokeColor();
        setStrokeColor(color);
        context.drawOval(x, y, x + w, y + h, currentState().prepareStroke());
        setStrokeColor(tmp);
        isDirty = true;
        return this;
    }

    @Override
    public Canvas fillOval(float x, float y, float w, float h, LColor c) {
        int tmp = getFillColor();
        setFillColor(c);
        context.drawOval(x, y, x + w, y + h, currentState().prepareFill());
        setFillColor(tmp);
        isDirty = true;
        return this;
    }

    @Override
    public Canvas drawText(String text, float x, float y) {
        if (_font == null) {
            _font = LFont.getDefaultFont();
        }
        return fillText(_font.getLayoutText(text), x, y);
    }

    @Override
    public Canvas drawText(String text, float x, float y, LColor color) {
        if (_font == null) {
            _font = LFont.getDefaultFont();
        }
        int tmp = getFillColor();
        setFillColor(color);
        fillText(_font.getLayoutText(text), x, y);
        setFillColor(tmp);
        return this;
    }

    @Override
    public boolean isMainCanvas() {
        return this.graphicsMain;
    }

    @Override
    public Canvas fillCircle(float x, float y, float radius) {
        context.drawCircle(x, y, radius, currentState().prepareFill());
        isDirty = true;
        return this;
    }

    @Override
    public Canvas fillOval(float x, float y, float width, float height) {
        context.drawOval(x, y, x + width, y + width, currentState().prepareFill());
        isDirty = true;
        return this;
    }

    @Override
    public Canvas fillArc(float x, float y, float w, float h, float startAngle, float endAngle) {
        context.drawArc(x, y, x + w, y + h, startAngle, endAngle, true, currentState().prepareFill());
        isDirty = true;
        return this;
    }

    @Override
    public Canvas fillPath(Path path) {
        context.drawPath(((JavaANPath) path).path, currentState().prepareFill());
        isDirty = true;
        return this;
    }

    @Override
    public Canvas fillRect(float x, float y, float width, float height) {
        final float left = x;
        final float top = y;
        final float right = left + width;
        final float bottom = top + height;
        context.drawRect(left, top, right, bottom, currentState().prepareFill());
        isDirty = true;
        return this;
    }

    @Override
    public Canvas fillRect(float x, float y, float width, float height, LColor c) {
        int tmp = getFillColor();
        setFillColor(c);
        fillRect(x, y, width, height);
        setFillColor(tmp);
        return this;
    }

    @Override
    public Canvas fillRoundRect(float x, float y, float width, float height, float radius) {
        context.translate(x, y);
        dstR.set(0, 0, width, height);
        context.drawRoundRect(dstR, radius, radius, currentState().prepareFill());
        context.translate(-x, -y);
        isDirty = true;
        return this;
    }

    @Override
    public Canvas fillText(TextLayout layout, float x, float y) {
        ((JavaANTextLayout) layout).draw(context, x, y, currentState().prepareFill());
        isDirty = true;
        return this;
    }

    @Override
    public Canvas save() {
        context.save();
        paintStack.addFirst(new JavaANCanvasState(currentState()));
        return this;
    }

    @Override
    public Canvas restore() {
        context.restore();
        paintStack.removeFirst();
        return this;
    }

    @Override
    public Canvas rotate(float angle) {
        context.rotate(MathUtils.degToRad(angle));
        return this;
    }

    @Override
    public Canvas scale(float x, float y) {
        context.scale(x, y);
        return this;
    }

    @Override
    public Canvas setAlpha(float alpha) {
        currentState().setAlpha(alpha);
        return this;
    }

    @Override
    public Canvas setCompositeOperation(Composite composite) {
        currentState().setCompositeOperation(composite);
        return this;
    }

    @Override
    public Canvas setFillColor(LColor color) {
        if (color == null) {
            currentState().fillColor = Color.WHITE;
        } else {
            currentState().fillColor = color.getARGB();
        }
        return this;
    }

    @Override
    public Canvas setFillColor(int color) {
        currentState().setFillColor(color);
        return this;
    }

    @Override
    public Canvas setFillGradient(Gradient gradient) {
        currentState().setFillGradient((JavaANGradient) gradient);
        return this;
    }

    @Override
    public Canvas setLineCap(LineCap cap) {
        currentState().setLineCap(cap);
        return this;
    }

    @Override
    public Canvas setLineJoin(LineJoin join) {
        currentState().setLineJoin(join);
        return this;
    }

    @Override
    public Canvas setMiterLimit(float miter) {
        currentState().setMiterLimit(miter);
        return this;
    }

    @Override
    public Canvas setStrokeColor(int color) {
        currentState().setStrokeColor(color);
        return this;
    }

    @Override
    public Canvas setStrokeColor(LColor color) {
        if (color == null) {
            currentState().strokeColor = Color.WHITE;
        } else {
            currentState().strokeColor = color.getARGB();
        }
        return this;
    }

    @Override
    public Canvas setStrokeWidth(float strokeWidth) {
        currentState().setStrokeWidth(strokeWidth);
        return this;
    }

    protected JavaANImage toANImage() {
        return (JavaANImage) image;
    }

    protected void setANImage(Image img, Bitmap write) {
        Bitmap oldImg = ((JavaANImage) img).buffer;
        if (oldImg != write) {
            context.setBitmap(write);
            ((JavaANImage) img).buffer = write;
        }
        img.setDirty(false);
    }

    @Override
    public Image newSnapshot() {
        Scale scale = null;
        Bitmap newImage = null;
        if (image != null) {
            Bitmap img = ((JavaANImage) image).anImage();
            scale = image.scale();
            newImage = JavaANImageCachePool.get().find(img.getConfig(), MathUtils.floorInt(img.getWidth()),
                    MathUtils.floorInt(img.getHeight()));
        } else {
            scale = Scale.ONE;
            newImage = JavaANImageCachePool.get().find(null, MathUtils.floorInt(width), MathUtils.floorInt(height));
        }
        return new JavaANImage(gfx, scale, newImage, TextureSource.RenderCanvas);
    }

    @Override
    public Image snapshot() {
        if (image == null) {
            Bitmap writeImage = JavaANImageCachePool.get().find(null, MathUtils.floorInt(width),
                    MathUtils.floorInt(height));
            image = new JavaANImage(gfx, image.scale(), writeImage.copy(writeImage.getConfig(), true),
                    TextureSource.RenderCanvas);
            setANImage(image, writeImage);
            return image;
        }
        if (image.isDirty() || isDirty) {
            Bitmap writeImage = toANImage().buffer;
            setANImage(image, writeImage);
            isDirty = false;
        }
        return image;
    }

    @Override
    public Canvas strokeCircle(float x, float y, float radius) {
        context.drawCircle(x, y, radius, currentState().prepareStroke());
        isDirty = true;
        return this;
    }

    @Override
    public Canvas strokePath(Path path) {
        context.drawPath(((JavaANPath) path).path, currentState().prepareStroke());
        isDirty = true;
        return this;
    }

    @Override
    public Canvas drawRect(float x, float y, float width, float height, LColor color) {
        int strokeColor = getStrokeColor();
        setStrokeColor(color);
        strokeRect(x, y, width, height);
        setStrokeColor(strokeColor);
        return this;
    }

    @Override
    public Canvas strokeRect(float x, float y, float width, float height) {
        float left = x;
        float top = y;
        float right = left + width;
        float bottom = top + height;
        context.drawRect(left, top, right, bottom, currentState().prepareStroke());
        isDirty = true;
        return this;
    }

    @Override
    public Canvas strokeRoundRect(float x, float y, float width, float height, float radius) {
        context.translate(x, y);
        dstR.set(0, 0, width, height);
        context.drawRoundRect(dstR, radius, radius, currentState().prepareStroke());
        context.translate(-x, -y);
        isDirty = true;
        return this;
    }

    @Override
    public Canvas strokeText(TextLayout layout, float x, float y) {
        ((JavaANTextLayout) layout).draw(context, x, y, currentState().prepareStroke());
        isDirty = true;
        return this;
    }

    @Override
    public Canvas drawRoundRect(float x, float y, float width, float height, float radius) {
        context.translate(x, y);
        dstR.set(0, 0, width, height);
        context.drawRoundRect(dstR, radius, radius, currentState().prepareStroke());
        context.translate(-x, -y);
        isDirty = true;
        return this;
    }

    @Override
    public Canvas setLineWidth(float lineWidth) {
        this.currentState().setStrokeWidth(lineWidth);
        isDirty = true;
        return this;
    }

    private float[] setArrayTransform(float m11, float m12, float m21, float m22, float dx, float dy) {
        _transform[0] = m11;
        _transform[1] = m21;
        _transform[2] = dx;
        _transform[3] = m12;
        _transform[4] = m22;
        _transform[5] = dy;
        _transform[6] = 0f;
        _transform[7] = 0f;
        _transform[8] = 1f;
        return _transform;
    }

    @Override
    public Canvas transform(float m11, float m12, float m21, float m22, float dx, float dy) {
        m.setValues(setArrayTransform(m11, m12, m21, m22, dx, dy));
        context.concat(m);
        isDirty = true;
        return this;
    }

    @Override
    public Canvas setTransform(Affine2f aff) {
        m.setValues(setArrayTransform(aff.m00, aff.m01, aff.m10, aff.m11, aff.tx, aff.ty));
        context.setMatrix(m);
        isDirty = true;
        return this;
    }

    @Override
    public Canvas translate(float x, float y) {
        context.translate(x, y);
        isDirty = true;
        return this;
    }

    @Override
    protected JavaANCanvas gc() {
        return this;
    }

    private JavaANCanvasState currentState() {
        return paintStack.peek();
    }

    @Override
    protected void closeImpl() {
        if (context != null) {
            context = null;
            closed = true;
        }
    }
}
