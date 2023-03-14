package loon.an;

import android.graphics.LinearGradient;
import android.graphics.RadialGradient;
import android.graphics.Shader;

import loon.canvas.Gradient;

public class JavaANGradient extends Gradient {
    final Shader shader;

    public JavaANGradient(Config cfg) {
        if (cfg instanceof Radial) {
            Radial rc = (Radial) cfg;
            this.shader = new RadialGradient(rc.x, rc.y, rc.r, rc.colors, rc.positions, Shader.TileMode.CLAMP);

        } else if (cfg instanceof Linear) {
            Linear lc = (Linear) cfg;
            this.shader = new LinearGradient(lc.x0, lc.y0, lc.x1, lc.y1, lc.colors, lc.positions,
                    Shader.TileMode.CLAMP);
        } else
            throw new IllegalArgumentException("Unknown config " + cfg);
    }
}
