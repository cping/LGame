/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.5
 */
package loon.particle;

import loon.geom.Affine2f;
import loon.particle.ParticleBuffer.Initializer;
import loon.stage.Player;
import loon.utils.MathUtils;

public class ParticleTransform {
	
    public static Initializer identity () {
        return constant(0, 0);
    }

    public static Initializer constant (float tx, float ty) {
        return constant(1, 0, tx, ty);
    }

    public static Initializer constant (float scale, float rot, final float tx, final float ty) {
        float sina = MathUtils.sin(rot), cosa = MathUtils.cos(rot);
        final float m00 = cosa * scale, m01 = sina * scale, m10 = -sina * scale, m11 = cosa * scale;
        return new Initializer() {
            @Override public void init (int index, float[] data, int start) {
                data[start + ParticleBuffer.M00] = m00;
                data[start + ParticleBuffer.M01] = m01;
                data[start + ParticleBuffer.M10] = m10;
                data[start + ParticleBuffer.M11] = m11;
                data[start + ParticleBuffer.TX] = tx;
                data[start + ParticleBuffer.TY] = ty;
            }
        };
    }

    public static Initializer scale (final float scale)
    {
        return new Initializer() {
            @Override public void init (int index, float[] data, int start) {
                data[start + ParticleBuffer.M00] *= scale;
                data[start + ParticleBuffer.M01] *= scale;
                data[start + ParticleBuffer.M10] *= scale;
                data[start + ParticleBuffer.M11] *= scale;
            }
        };
    }

    public static Initializer randomScale (final float minScale,
                                           final float maxScale)
    {
        return new Initializer() {
            @Override public void init (int index, float[] data, int start) {
                float scale = MathUtils.random(minScale, maxScale);
                data[start + ParticleBuffer.M00] *= scale;
                data[start + ParticleBuffer.M01] *= scale;
                data[start + ParticleBuffer.M10] *= scale;
                data[start + ParticleBuffer.M11] *= scale;
            }
        };
    }

    public static Initializer layer (final Player layer) {
        return new Initializer() {
            protected final Affine2f xform = new Affine2f();

            protected final float[] matrix = new float[6];
            @Override public void willInit (int count) {
                xform.setTransform(1, 0, 0, 1, 0, 0);
                Player xlayer = layer;
                while (xlayer != null) {
                    Affine2f.multiply(xlayer.affine(), xform, xform);
                    xlayer = xlayer.parent();
                }
                xform.get(matrix);
            }
            @Override public void init (int index, float[] data, int start) {
                System.arraycopy(matrix, 0, data, start + ParticleBuffer.M00, 6);
            }

        };
    }

    public static Initializer randomPos ( final float x, final float y,
                                         final float width, final float height) {
        return new Initializer() {
            @Override public void init (int index, float[] data, int start) {
                data[start + ParticleBuffer.TX] = x +  MathUtils.random(width);
                data[start + ParticleBuffer.TY] = y +  MathUtils.random(height);
            }
        };
    }

    public static Initializer randomOffset (final float noise) {
        return new Initializer() {
            @Override public void init (int index, float[] data, int start) {
                data[start + ParticleBuffer.TX] +=  MathUtils.random(-noise, noise);
                data[start + ParticleBuffer.TY] +=  MathUtils.random(-noise, noise);
            }
        };
    }
}
