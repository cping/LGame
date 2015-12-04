package loon.live2d.base;

import loon.live2d.*;

public class Indexs
{
  
    float a;
    float b;
    float c;
    float d;
    float e;
    float f;
    int g;
    int h;
    
    public Indexs() {
        this.a = 1.0f;
        this.b = 0.0f;
        this.c = 0.0f;
        this.d = 1.0f;
        this.e = 0.0f;
        this.f = 0.0f;
        this.g = 0;
        this.h = 0;
    }
    
    public Indexs(final float a, final float b, final float c, final float d, final float e, final float f) {
        this.a = 1.0f;
        this.b = 0.0f;
        this.c = 0.0f;
        this.d = 1.0f;
        this.e = 0.0f;
        this.f = 0.0f;
        this.g = 0;
        this.h = 0;
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.e = e;
        this.f = f;
        this.a();
    }
    
    public void a(final float[] array, final float[] array2, int n) {
        int n2 = 0;
        int n3 = 0;
        switch (this.g) {
            default: {}
            case 7: {
                final float a = this.a;
                final float c = this.c;
                final float e = this.e;
                final float b = this.b;
                final float d = this.d;
                final float f = this.f;
                while (--n >= 0) {
                    final float n4 = array[n2++];
                    final float n5 = array[n2++];
                    array2[n3++] = a * n4 + c * n5 + e;
                    array2[n3++] = b * n4 + d * n5 + f;
                }
            }
            case 6: {
                final float a2 = this.a;
                final float c2 = this.c;
                final float b2 = this.b;
                final float d2 = this.d;
                while (--n >= 0) {
                    final float n6 = array[n2++];
                    final float n7 = array[n2++];
                    array2[n3++] = a2 * n6 + c2 * n7;
                    array2[n3++] = b2 * n6 + d2 * n7;
                }
            }
            case 5: {
                final float c3 = this.c;
                final float e2 = this.e;
                final float b3 = this.b;
                final float f2 = this.f;
                while (--n >= 0) {
                    final float n8 = array[n2++];
                    array2[n3++] = c3 * array[n2++] + e2;
                    array2[n3++] = b3 * n8 + f2;
                }
            }
            case 4: {
                final float c4 = this.c;
                final float b4 = this.b;
                while (--n >= 0) {
                    final float n9 = array[n2++];
                    array2[n3++] = c4 * array[n2++];
                    array2[n3++] = b4 * n9;
                }
            }
            case 3: {
                final float a3 = this.a;
                final float e3 = this.e;
                final float d3 = this.d;
                final float f3 = this.f;
                while (--n >= 0) {
                    array2[n3++] = a3 * array[n2++] + e3;
                    array2[n3++] = d3 * array[n2++] + f3;
                }
            }
            case 2: {
                final float a4 = this.a;
                final float d4 = this.d;
                while (--n >= 0) {
                    array2[n3++] = a4 * array[n2++];
                    array2[n3++] = d4 * array[n2++];
                }
            }
            case 1: {
                final float e4 = this.e;
                final float f4 = this.f;
                while (--n >= 0) {
                    array2[n3++] = array[n2++] + e4;
                    array2[n3++] = array[n2++] + f4;
                }
            }
            case 0: {
                if (array != array2 || n2 != n3) {
                    System.arraycopy(array, n2, array2, n3, n * 2);
                }
            }
        }
    }
    
    void a() {
        if (this.c == 0.0f && this.b == 0.0f) {
            if (this.a == 1.0f && this.d == 1.0f) {
                if (this.e == 0.0f && this.f == 0.0f) {
                    this.g = 0;
                    this.h = 0;
                }
                else {
                    this.g = 1;
                    this.h = 1;
                }
            }
            else if (this.e == 0.0f && this.f == 0.0f) {
                this.g = 2;
                this.h = -1;
            }
            else {
                this.g = 3;
                this.h = -1;
            }
        }
        else if (this.a == 0.0f && this.d == 0.0f) {
            if (this.e == 0.0f && this.f == 0.0f) {
                this.g = 4;
                this.h = -1;
            }
            else {
                this.g = 5;
                this.h = -1;
            }
        }
        else if (this.e == 0.0f && this.f == 0.0f) {
            this.g = 6;
            this.h = -1;
        }
        else {
            this.g = 7;
            this.h = -1;
        }
    }
    
    public void a(final float[] array) {
        this.c(array);
        final float n = array[0];
        final float n2 = array[2];
        final float n3 = array[1];
        final float n4 = array[3];
        final float n5 = (float)Math.sqrt(n * n + n3 * n3);
        final float n6 = n * n4 - n2 * n3;
        if (n5 == 0.0f) {
            if (Live2D.L2D_VERBOSE) {
            }
        }
        else {
            array[1] = n6 / (array[0] = n5);
            array[2] = (n3 * n4 + n * n2) / n6;
            array[3] = (float)Math.atan2(n3, n);
        }
    }
    
    void a(final Indexs d, final Indexs d2, final float n, final Indexs d3) {
        final float[] array = new float[6];
        final float[] array2 = new float[6];
        d.a(array);
        d2.a(array2);
        d3.b(new float[] { array[0] + (array2[0] - array[0]) * n, array[1] + (array2[1] - array[1]) * n, array[2] + (array2[2] - array[2]) * n, array[3] + (array2[3] - array[3]) * n, array[4] + (array2[4] - array[4]) * n, array[5] + (array2[5] - array[5]) * n });
    }
    
    public void b(final float[] array) {
        final float n = (float)Math.cos(array[3]);
        final float n2 = (float)Math.sin(array[3]);
        this.a = array[0] * n;
        this.b = array[0] * n2;
        this.c = array[1] * (array[2] * n - n2);
        this.d = array[1] * (array[2] * n2 + n);
        this.e = array[4];
        this.f = array[5];
        this.a();
    }
    
    void c(final float[] array) {
        array[0] = this.a;
        array[1] = this.b;
        array[2] = this.c;
        array[3] = this.d;
        array[4] = this.e;
        array[5] = this.f;
    }
}
