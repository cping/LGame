package loon.live2d.context;

import loon.live2d.base.*;

public class IBaseContext
{
    IBaseData e;
    int f;
    protected boolean g;
    protected boolean h;
    protected float i;
    protected float j;
    protected float k;
    
    public boolean a() {
        return this.h && !this.g;
    }
    
    public void a(final boolean h) {
        this.h = h;
    }
    
    public IBaseContext(final IBaseData src) {
        this.g = false;
        this.h = true;
        this.i = 1.0f;
        this.j = 1.0f;
        this.k = 1.0f;
        this.e = src;
    }
    
    public IBaseData b() {
        return this.e;
    }
    
    public void a(final int f) {
        this.f = f;
    }
    
    public int c() {
        return this.f;
    }
    
    public boolean d() {
        return this.g;
    }
    
    public void b(final boolean g) {
        this.g = g;
    }
    
    public float e() {
        return this.i;
    }
    
    public void a(final float i) {
        this.i = i;
    }
    
    public float f() {
        return this.j;
    }
    
    public void b(final float j) {
        this.j = j;
    }
    
    public float g() {
        return this.k;
    }
    
    public void c(final float k) {
        this.k = k;
    }
}
