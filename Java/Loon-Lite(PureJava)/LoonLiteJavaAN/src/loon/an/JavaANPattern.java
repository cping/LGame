package loon.an;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Shader;

import loon.canvas.Pattern;

public class JavaANPattern extends Pattern {
    final BitmapShader shader;

    JavaANPattern(boolean repeatX, boolean repeatY, Bitmap bitmap) {
        super(repeatX, repeatY);
        this.shader = new BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
    }
}
