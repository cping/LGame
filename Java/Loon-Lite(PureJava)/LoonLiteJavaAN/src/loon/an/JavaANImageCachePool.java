package loon.an;

import android.graphics.Bitmap;
import loon.utils.MathUtils;
import loon.utils.cache.Pool;

public class JavaANImageCachePool extends Pool<Bitmap> {

    private static JavaANImageCachePool _instance;

    public static void freeStatic() {
        _instance = null;
    }

    public static final JavaANImageCachePool get() {
        if (_instance == null) {
            synchronized (JavaANImageCachePool.class) {
                if (_instance == null) {
                    _instance = new JavaANImageCachePool();
                }
            }
        }
        return _instance;
    }

    private int _imageWidth;

    private int _imageHeight;

    private Bitmap.Config _pixelConfig;

    public JavaANImageCachePool() {
        super();
    }

    public JavaANImageCachePool findImage(Bitmap.Config reader, int w, int h) {
        this._imageWidth = w;
        this._imageHeight = h;
        this._pixelConfig = reader;
        return this;
    }

    public Bitmap find(Bitmap.Config reader, int w, int h) {
        findImage(reader, w, h);
        return obtain();
    }

    @Override
    public Bitmap obtain() {
        if (freeObjects.size == 0) {
            return newObject();
        }
        Bitmap image = freeObjects.find((img) -> {
            return MathUtils.equal(MathUtils.floor(img.getWidth()), _imageWidth)
                    && MathUtils.equal(MathUtils.floor(img.getHeight()), _imageHeight);
        });
        if (image == null) {
            return newObject();
        } else {
            final int[] rgba = new int[_imageWidth * _imageHeight];
            image.setPixels(rgba, 0, _imageWidth, 0, 0, _imageWidth, _imageHeight);
            freeObjects.remove(image);
        }
        return image;
    }

    @Override
    protected Bitmap newObject() {
        if (_pixelConfig != null) {
            return Bitmap.createBitmap( _imageWidth, _imageHeight,_pixelConfig);
        }
        return Bitmap.createBitmap(_imageWidth, _imageHeight,Bitmap.Config.ARGB_8888);
    }

    @Override
    public boolean isLimit(Bitmap src, Bitmap old) {
        if (src == null) {
            return true;
        }
        return src.getWidth() > 1024 && src.getHeight() > 1024;
    }

    @Override
    protected Bitmap filterObtain(Bitmap o) {
        return o;
    }
}
