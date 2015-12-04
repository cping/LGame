package loon.live2d.physics;

import loon.live2d.*;
import loon.live2d.util.*;
import loon.utils.MathUtils;
import loon.utils.TArray;

public class PhysicsHair
{
    c a;
    c b;
    float c;
    float d;
    float e;
    float f;
    float g;
    float h;
    long i;
    long j;
    TArray k;
    TArray l;
    
    public PhysicsHair() {
        this.a = new c();
        this.b = new c();
        this.c = 0.0f;
        this.d = 0.0f;
        this.e = 0.0f;
        this.f = 0.0f;
        this.g = 0.0f;
        this.h = 0.0f;
        this.i = 0L;
        this.j = 0L;
        this.k = new TArray();
        this.l = new TArray();
        this.setup(0.3f, 0.5f, 0.1f);
    }
    
    public PhysicsHair(final float _baseLengthM, final float _airRegistance, final float _mass) {
        this.a = new c();
        this.b = new c();
        this.c = 0.0f;
        this.d = 0.0f;
        this.e = 0.0f;
        this.f = 0.0f;
        this.g = 0.0f;
        this.h = 0.0f;
        this.i = 0L;
        this.j = 0L;
        this.k = new TArray();
        this.l = new TArray();
        this.setup(_baseLengthM, _airRegistance, _mass);
    }
    
    public void setup(final float _baseLengthM, final float _airRegistance, final float _mass) {
        this.c = _baseLengthM;
        this.e = _airRegistance;
        this.a.a = _mass;
        this.b.a = _mass;
        this.b.c = _baseLengthM;
        this.setup();
    }
    
    c a() {
        return this.a;
    }
    
    c b() {
        return this.b;
    }
    
    float c() {
        return this.d;
    }
    
    void a(final float d) {
        this.d = d;
    }
    
    float d() {
        return this.f;
    }
    
    float e() {
        return this.h;
    }
    
    float f() {
        return (float)(-180.0 * MathUtils.atan2(this.a.b - this.b.b, -(this.a.c - this.b.c)) / 3.141592653589793);
    }
    
    public void setup() {
        this.g = this.f();
        this.b.a();
    }
    
    public void addSrcParam(final Src srcType, final String paramID, final float scale, final float weight) {
        this.k.add(new d(srcType, paramID, scale, weight));
    }
    
    public void addTargetParam(final Target targetType, final String paramID, final float scale, final float weight) {
        this.l.add(new e(targetType, paramID, scale, weight));
    }
    
    public void update(final ALive2DModel model, final long time) {
        if (this.i == 0L) {
            this.j = time;
            this.i = time;
            this.c = (float)MathUtils.sqrt((this.a.b - this.b.b) * (this.a.b - this.b.b) + (this.a.c - this.b.c) * (this.a.c - this.b.c));
            return;
        }
        final float n = (time - this.j) / 1000.0f;
        if (n != 0.0f) {
            for (int i = this.k.size - 1; i >= 0; --i) {
                ((a)this.k.get(i)).imp(model, this);
            }
            this.a(model, n);
            this.f = this.f();
            this.h = (this.f - this.g) / n;
            this.g = this.f;
        }
        for (int j = this.l.size - 1; j >= 0; --j) {
            ((b)this.l.get(j)).a(model, this);
        }
        this.j = time;
    }
    
    void a(final ALive2DModel aLive2DModel, final float n) {
        final float n2 = 1.0f / n;
        this.a.d = (this.a.b - this.a.j) * n2;
        this.a.e = (this.a.c - this.a.k) * n2;
        this.a.f = (this.a.d - this.a.l) * n2;
        this.a.g = (this.a.e - this.a.m) * n2;
        this.a.h = this.a.f * this.a.a;
        this.a.i = this.a.g * this.a.a;
        this.a.a();
        final float n3 = -(float)MathUtils.atan2(this.a.c - this.b.c, this.a.b - this.b.b);
        final float n4 = (float)MathUtils.cos(n3);
        final float n5 = (float)MathUtils.sin(n3);
        final float n6 = (float)(9.8f * this.b.a * MathUtils.cos(n3 - this.d * UtMath.b));
        final float n7 = n6 * n5;
        final float n8 = n6 * n4;
        final float n9 = -this.a.h * n5 * n5;
        final float n10 = -this.a.i * n5 * n4;
        final float n11 = -this.b.d * this.e;
        final float n12 = -this.b.e * this.e;
        this.b.h = n7 + n9 + n11;
        this.b.i = n8 + n10 + n12;
        this.b.f = this.b.h / this.b.a;
        this.b.g = this.b.i / this.b.a;
        final c b = this.b;
        b.d += this.b.f * n;
        final c b2 = this.b;
        b2.e += this.b.g * n;
        final c b3 = this.b;
        b3.b += this.b.d * n;
        final c b4 = this.b;
        b4.c += this.b.e * n;
        final float n13 = MathUtils.sqrt((this.a.b - this.b.b) * (this.a.b - this.b.b) + (this.a.c - this.b.c) * (this.a.c - this.b.c));
        this.b.b = this.a.b + this.c * (this.b.b - this.a.b) / n13;
        this.b.c = this.a.c + this.c * (this.b.c - this.a.c) / n13;
        this.b.d = (this.b.b - this.b.j) * n2;
        this.b.e = (this.b.c - this.b.k) * n2;
        this.b.a();
    }
    
    static class a
    {
        String a;
        float b;
        float c;
        
        a(final String a, final float b, final float c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }
        
        void imp(final ALive2DModel aLive2DModel, final PhysicsHair physicsHair) {
        }
    }
    
    static class b
    {
        String a;
        float b;
        float c;
        
        b(final String a, final float b, final float c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }
        
        void a(final ALive2DModel aLive2DModel, final PhysicsHair physicsHair) {
        }
    }
    
    static class c
    {
        float a;
        float b;
        float c;
        float d;
        float e;
        float f;
        float g;
        float h;
        float i;
        float j;
        float k;
        float l;
        float m;
        
        c() {
            this.a = 1.0f;
            this.b = 0.0f;
            this.c = 0.0f;
            this.d = 0.0f;
            this.e = 0.0f;
            this.f = 0.0f;
            this.g = 0.0f;
            this.h = 0.0f;
            this.i = 0.0f;
            this.j = 0.0f;
            this.k = 0.0f;
            this.l = 0.0f;
            this.m = 0.0f;
        }
        
        void a() {
            this.j = this.b;
            this.k = this.c;
            this.l = this.d;
            this.m = this.e;
        }
    }
    
    static class d extends a
    {
        Src d;

        d(final Src d, final String s, final float n, final float n2) {
            super(s, n, n2);
            this.d = d;
        }
        
        @Override
        void imp(final ALive2DModel aLive2DModel, final PhysicsHair physicsHair) {
            final float n = this.b * aLive2DModel.getParamFloat(this.a);
            final c a = physicsHair.a();
            switch (d) {
                default: {
                    a.b += (n - a.b) * this.c;
                    break;
                }
                case SRC_TO_Y: {
                    a.c += (n - a.c) * this.c;
                    break;
                }
                case SRC_TO_G_ANGLE: {
                    final float c = physicsHair.c();
                    physicsHair.a(c + (n - c) * this.c);
                    break;
                }
            }
        }
        
    }
    
    public enum Src
    {
        SRC_TO_X,
        SRC_TO_Y,
        SRC_TO_G_ANGLE;
      
    }
    
    static class e extends b
    {
        Target d;

        e(final Target d, final String s, final float n, final float n2) {
            super(s, n, n2);
            this.d = d;
        }
        
        @Override
        void a(final ALive2DModel aLive2DModel, final PhysicsHair physicsHair) {
            switch (d) {
                default: {
                    aLive2DModel.setParamFloat(this.a, this.b * physicsHair.d(), this.c);
                    break;
                }
                case TARGET_FROM_ANGLE_V: {
                    aLive2DModel.setParamFloat(this.a, this.b * physicsHair.e(), this.c);
                    break;
                }
            }
        }
        
    }
    
    public enum Target
    {
    	
        TARGET_FROM_ANGLE, 
        TARGET_FROM_ANGLE_V;
     
    }
}
