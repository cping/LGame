package loon.an;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import loon.Graphics;
import loon.LSetting;
import loon.canvas.Canvas;
import loon.font.Font;
import loon.font.TextFormat;
import loon.font.TextLayout;
import loon.font.TextWrap;
import loon.geom.Dimension;
import loon.opengl.TextureSource;
import loon.utils.Scale;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Pair;

public class JavaANGraphics extends Graphics {

    public interface Refreshable {

        void onSurfaceLost();

        void onSurfaceCreated();
    }

    final Bitmap.Config preferredBitmapConfig = Bitmap.Config.ARGB_8888;
    private final JavaANGame game;

    protected JavaANCanvas canvas;
    private final Map<Pair<String, Font.Style>, Typeface> fonts = new HashMap<Pair<String, Font.Style>, Typeface>();
    private final Map<Pair<String, Font.Style>, String[]> ligatureHacks = new HashMap<Pair<String, Font.Style>, String[]>();

    private final Dimension screenSize = new Dimension();
    private ScaleFunc canvasScaleFunc = new ScaleFunc() {
        @Override
        public Scale computeScale(float width, float height, Scale gfxScale) {
            return gfxScale;
        }
    };

    protected JavaANGraphics(JavaANGame game) {
        this(game, true);
    }

    protected JavaANGraphics(JavaANGame game, boolean resized) {
        this(game, Scale.ONE, resized);
    }

    protected JavaANGraphics(JavaANGame game, Scale scale, boolean resized) {
        super(game, scale);
        this.game = game;
        this.createCanvas(game.setting, scale, resized);
    }

    protected Canvas createCanvas(LSetting setting, Scale scale, boolean resized) {
        if (canvas == null) {
            JavaANImage image = null;
            if (resized) {
                image = new JavaANImage(this.game, scale.scaledFloor(setting.getShowWidth()),
                        scale.scaledFloor(setting.getShowHeight()));
            } else {
                image = new JavaANImage(this.game, scale.scaledFloor(setting.width), scale.scaledFloor(setting.height));
            }
            canvas = new JavaANCanvas(this, image, true);
        }
        return canvas;
    }

    public void onSizeChanged(int viewWidth, int viewHeight) {
        if (!isAllowResize(viewWidth, viewHeight)) {
            return;
        }
        screenSize.width = viewWidth / scale.factor;
        screenSize.height = viewHeight / scale.factor;
        game.log().info("Updating size " + viewWidth + "x" + viewHeight + " / " + scale.factor + " -> " + screenSize);
        viewportChanged(scale, viewWidth, viewHeight);
    }

    public void registerFont(String path, String name, Font.Style style, String... ligatureGlyphs) {
        try {
            registerFont(((JavaANAssets)game.assets()).getTypeface(path), name, style, ligatureGlyphs);
        } catch (Exception e) {
            game.reportError("Failed to load font [name=" + name + ", path=" + path + "]", e);
        }
    }

    public void registerFont(Typeface face, String name, Font.Style style, String... ligatureGlyphs) {
        Pair<String, Font.Style> key = Pair.create(name, style);
        fonts.put(key, face);
        ligatureHacks.put(key, ligatureGlyphs);
    }

    public void setCanvasFilterBitmaps(boolean filterBitmaps) {
        if (filterBitmaps) {
            JavaANCanvasState.PAINT_FLAGS |= Paint.FILTER_BITMAP_FLAG;
        } else {
            JavaANCanvasState.PAINT_FLAGS &= ~Paint.FILTER_BITMAP_FLAG;
        }
    }

    public interface ScaleFunc {

        Scale computeScale(float width, float height, Scale gfxScale);
    }

    public void setCanvasScaleFunc(ScaleFunc scaleFunc) {
        if (scaleFunc == null) {
            throw new NullPointerException("Scale func have not null");
        }
        canvasScaleFunc = scaleFunc;
    }

    @Override
    public Dimension screenSize() {
        return screenSize;
    }

    @Override
    public Canvas createCanvas(float width, float height) {
        Scale scale = canvasScaleFunc.computeScale(width, height, this.scale);
        return createCanvasImpl(scale, scale.scaledCeil(width), scale.scaledCeil(height));
    }

    @Override
    public TextLayout layoutText(String text, TextFormat format) {
        return JavaANTextLayout.layoutText(this, text, format);
    }

    @Override
    public TextLayout[] layoutText(String text, TextFormat format, TextWrap wrap) {
        return JavaANTextLayout.layoutText(this, text, format, wrap);
    }

    @Override
    protected Canvas createCanvasImpl(Scale scale, int pixelWidth, int pixelHeight) {
        Bitmap bitmap = Bitmap.createBitmap(pixelWidth, pixelHeight, preferredBitmapConfig);
        return new JavaANCanvas(this, new JavaANImage(this, scale, bitmap, TextureSource.RenderCanvas),false);
    }

    @Override
    public JavaANCanvas getCanvas() {
        return canvas;
    }

    JavaANFont resolveFont(Font font) {
        if (font == null) {
            return JavaANFont.DEFAULT;
        }
        Pair<String, Font.Style> key = Pair.create(font.name, font.style);
        Typeface face = fonts.get(key);
        if (face == null) {
            fonts.put(key, face = JavaANFont.create(font));
        }
        return new JavaANFont(face, font.size, ligatureHacks.get(key));
    }

}
