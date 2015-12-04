package loon.live2d.param;

import loon.live2d.id.*;
import loon.live2d.io.*;

public class ParamIO implements loon.live2d.io.IOBase
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static final int a = -2;
    int b;
    ParamID c;
    float[] d;
    int e;
    int f;
    int g;
    float h;
    
    public ParamIO() {
        this.b = 0;
        this.c = null;
        this.d = null;
        this.e = -2;
        this.f = -1;
        this.g = 0;
        this.h = 0.0f;
    }
    
    @Override
    public void readV2(final BReader br) {
        this.c = (ParamID)br.reader();
        this.b = br.readInt();
        this.d = (float[])br.reader();
    }
    
    public int a(final int n) {
        if (this.f != n) {
            this.e = -2;
        }
        return this.e;
    }
    
    public void a(final int e, final int f) {
        this.e = e;
        this.f = f;
    }
    
    public ParamID a() {
        return this.c;
    }
    
    public void a(final ParamID c) {
        this.c = c;
    }
    
    public int b() {
        return this.b;
    }
    
    public float[] c() {
        return this.d;
    }
    
    public void a(final int b, final float[] d) {
        this.b = b;
        this.d = d;
    }
    
    public int d() {
        return this.g;
    }
    
    public void b(final int g) {
        this.g = g;
    }
    
    public float e() {
        return this.h;
    }
    
    public void a(final float h) {
        this.h = h;
    }
}
