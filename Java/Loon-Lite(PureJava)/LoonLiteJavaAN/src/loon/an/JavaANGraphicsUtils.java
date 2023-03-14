package loon.an;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import loon.LSystem;
import loon.canvas.Image;
import loon.geom.RectBox;
import loon.opengl.TextureSource;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;

public class JavaANGraphicsUtils {

    final static public Matrix matrix = new Matrix();

    final static public Canvas canvas = new Canvas();

    /**
     * 剪切指定图像
     *
     * @param image
     * @param objectWidth
     * @param objectHeight
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public static Image drawClipImage(final Image image, int objectWidth, int objectHeight, int x1, int y1, int x2,
                                      int y2, Config config) {
        if (image == null) {
            return null;
        }
        if (objectWidth > image.getWidth()) {
            objectWidth = image.getWidth();
        }
        if (objectHeight > image.getHeight()) {
            objectHeight = image.getHeight();
        }

        Bitmap bitmap = Bitmap.createBitmap(objectWidth, objectHeight, config);
        canvas.setBitmap(bitmap);
        canvas.drawBitmap(((JavaANImage) image).buffer, new Rect(x1, y1, x2, y2),
                new Rect(0, 0, objectWidth, objectHeight), null);
        return new JavaANImage(LSystem.base().graphics(), LSystem.base().graphics().scale(), bitmap,
                TextureSource.RenderCanvas);
    }

    /**
     * 剪切指定图像
     *
     * @param image
     * @param objectWidth
     * @param objectHeight
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public static Image drawClipImage(final Image image, int objectWidth, int objectHeight, int x1, int y1, int x2,
                                      int y2) {
        return drawClipImage(image, objectWidth, objectHeight, x1, y1, x2, y2,
                ((JavaANImage) image).buffer.getConfig());
    }

    /**
     * 剪切指定图像
     *
     * @param image
     * @param objectWidth
     * @param objectHeight
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param flag
     * @return
     */
    public static Image drawClipImage(final Image image, int objectWidth, int objectHeight, int x1, int y1, int x2,
                                      int y2, boolean flag) {
        return drawClipImage(image, objectWidth, objectHeight, x1, y1, x2, y2,
                flag ? ((JavaANImage) image).buffer.getConfig() : Config.RGB_565);
    }

    /**
     * 剪切指定图像
     *
     * @param image
     * @param objectWidth
     * @param objectHeight
     * @param x
     * @param y
     * @param flag
     * @return
     */
    public static Image drawClipImage(final Image image, int objectWidth, int objectHeight, int x, int y,
                                      boolean flag) {
        return drawClipImage(image, objectWidth, objectHeight, x, y,
                flag ? ((JavaANImage) image).buffer.getConfig() : Config.RGB_565);
    }

    /**
     * 剪切指定图像
     *
     * @param image
     * @param objectWidth
     * @param objectHeight
     * @param x
     * @param y
     * @param config
     * @return
     */
    public static Image drawClipImage(final Image image, int objectWidth, int objectHeight, int x, int y,
                                      Config config) {
        if (image.getWidth() == objectWidth && image.getHeight() == objectHeight) {
            return image;
        }
        Bitmap bitmap = Bitmap.createBitmap(objectWidth, objectHeight, config);
        canvas.setBitmap(bitmap);
        canvas.drawBitmap(((JavaANImage) image).buffer, new Rect(x, y, x + objectWidth, objectHeight + y),
                new Rect(0, 0, objectWidth, objectHeight), null);
        return new JavaANImage(LSystem.base().graphics(), LSystem.base().graphics().scale(), bitmap,
                TextureSource.RenderCanvas);
    }

    /**
     * 剪切指定图像
     *
     * @param image
     * @param objectWidth
     * @param objectHeight
     * @param x
     * @param y
     * @return
     */
    public static Image drawCropImage(final Image image, int x, int y, int objectWidth, int objectHeight) {
        return drawClipImage(image, objectWidth, objectHeight, x, y,
                ((JavaANImage) image).buffer.getConfig());
    }

    /**
     * 剪切指定图像
     *
     * @param image
     * @param objectWidth
     * @param objectHeight
     * @param x
     * @param y
     * @return
     */
    public static Image drawClipImage(final Image image, int objectWidth, int objectHeight, int x, int y) {
        return drawClipImage(image, objectWidth, objectHeight, x, y,
                ((JavaANImage) image).buffer.getConfig());
    }

    /**
     * 改变指定Image大小
     *
     * @param image
     * @param w
     * @param h
     * @return
     */
    public static Image getResize(Image image, int w, int h) {
        Bitmap bitmap = getResize(((JavaANImage) image).buffer, w, h);
        return new JavaANImage(LSystem.base().graphics(), LSystem.base().graphics().scale(), bitmap,
                TextureSource.RenderCanvas);
    }

    /**
     * 改变指定Bitmap大小
     *
     * @param image
     * @param w
     * @param h
     * @return
     */
    public static Bitmap getResize(Bitmap image, int w, int h, boolean flag) {
        int width = image.getWidth();
        int height = image.getHeight();
        if (width == w && height == h) {
            return image;
        }
        int newWidth = w;
        int newHeight = h;
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        matrix.reset();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(image, 0, 0, width, height, matrix, flag);
        return resizedBitmap;
    }

    /**
     * 改变指定Bitmap大小
     *
     * @param image
     * @param w
     * @param h
     * @return
     */
    public static Bitmap getResize(Bitmap image, int w, int h) {
        return getResize(image, w, h, true);
    }

    /**
     * 矫正指定的长、宽大小为适当值
     *
     * @param srcWidth
     * @param srcHeight
     * @param dstWidth
     * @param dstHeight
     * @return
     */
    final static public RectBox fitLimitSize(int srcWidth, int srcHeight, int dstWidth, int dstHeight) {
        int dw = dstWidth;
        int dh = dstHeight;
        if (dw != 0 && dh != 0) {
            double waspect = (double) dw / srcWidth;
            double haspect = (double) dh / srcHeight;
            if (waspect > haspect) {
                dw = (int) (srcWidth * haspect);
            } else {
                dh = (int) (srcHeight * waspect);
            }
        }
        return new RectBox(0, 0, dw, dh);
    }

    /**
     * 生成Bitmap文件的hash序列
     *
     * @param bitmap
     * @return
     */
    public static int hashBitmap(Bitmap bitmap) {
        int hash_result = 0;
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        hash_result = (hash_result << 7) ^ h;
        hash_result = (hash_result << 7) ^ w;
        for (int pixel = 0; pixel < 20; ++pixel) {
            int x = (pixel * 50) % w;
            int y = (pixel * 100) % h;
            hash_result = (hash_result << 7) ^ bitmap.getPixel(x, y);
        }
        return hash_result;
    }

    public static void drawBitmap(final Bitmap src, final Bitmap dst, int x, int y) {
        if (src == null || dst == null) {
            return;
        }
        canvas.setBitmap(src);
        canvas.drawBitmap(dst, x, y, null);
    }

    public static void setPixels(final Bitmap bit, final int[] pixels, int w, int h) {
        bit.setPixels(pixels, 0, w, 0, 0, w, h);
    }

    /**
     * 从一个单独的Bitmap中获得其像素信息
     *
     * @param bit
     * @return
     */
    public static int[] getPixels(final Bitmap bit) {
        if (bit == null) {
            return null;
        }
        int width = bit.getWidth();
        int height = bit.getHeight();
        int pixels[] = new int[width * height];
        bit.getPixels(pixels, 0, width, 0, 0, width, height);
        return pixels;
    }

    /**
     * 从一个单独的Bitmap中获得其像素信息
     *
     * @param bit
     * @return
     */
    public static byte[] getBytePixels(final Bitmap bit) {
        if (bit == null) {
            return null;
        }
        final byte[] pixels = new byte[bit.getWidth() * bit.getHeight() * 4];
        final ByteBuffer buf = ByteBuffer.wrap(pixels);
        buf.order(ByteOrder.nativeOrder());
        bit.copyPixelsToBuffer(buf);
        return pixels;
    }

}
