package loon.live2d.motion;

import loon.live2d.*;
import loon.live2d.util.*;

public class EyeBlinkMotion
{
    long a;
    long b;
    EYE_STATE c;
    boolean d;
    String e;
    String f;
    int g;
    int h;
    int i;
    int j;
    private static /* synthetic */ int[] k;
    
    public EyeBlinkMotion() {
        this.c = EYE_STATE.a;
        this.g = 4000;
        this.h = 100;
        this.i = 50;
        this.j = 150;
        this.d = true;
        this.e = "PARAM_EYE_L_OPEN";
        this.f = "PARAM_EYE_R_OPEN";
    }
    
    public long calcNextBlink() {
        return (long)(UtSystem.getUserTimeMSec() + Math.random() * (2 * this.g - 1));
    }
    
    public void setInterval(final int blinkIntervalMsec) {
        this.g = blinkIntervalMsec;
    }
    
    public void setEyeMotion(final int closingMotionMsec, final int closedMotionMsec, final int openingMotionMsec) {
        this.h = closingMotionMsec;
        this.i = closedMotionMsec;
        this.j = openingMotionMsec;
    }
    
    public void setParam(final ALive2DModel model) {
        final long userTimeMSec = UtSystem.getUserTimeMSec();
        float n2 = 0.0f;
        switch (a()[this.c.ordinal()]) {
            case 3: {
                float n = (userTimeMSec - this.b) / this.h;
                if (n >= 1.0f) {
                    n = 1.0f;
                    this.c = EYE_STATE.d;
                    this.b = userTimeMSec;
                }
                n2 = 1.0f - n;
                break;
            }
            case 4: {
                if ((userTimeMSec - this.b) / this.i >= 1.0f) {
                    this.c = EYE_STATE.e;
                    this.b = userTimeMSec;
                }
                n2 = 0.0f;
                break;
            }
            case 5: {
                float n3 = (userTimeMSec - this.b) / this.j;
                if (n3 >= 1.0f) {
                    n3 = 1.0f;
                    this.c = EYE_STATE.b;
                    this.a = this.calcNextBlink();
                }
                n2 = n3;
                break;
            }
            case 2: {
                if (this.a < userTimeMSec) {
                    this.c = EYE_STATE.c;
                    this.b = userTimeMSec;
                }
                n2 = 1.0f;
                break;
            }
            default: {
                this.c = EYE_STATE.b;
                this.a = this.calcNextBlink();
                n2 = 1.0f;
                break;
            }
        }
        if (!this.d) {
            n2 = -n2;
        }
        model.setParamFloat(this.e, n2);
        model.setParamFloat(this.f, n2);
    }
    
    static /* synthetic */ int[] a() {
        final int[] k = EyeBlinkMotion.k;
        if (k != null) {
            return k;
        }
        final int[] i = new int[EYE_STATE.values().length];
        try {
            i[EYE_STATE.d.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            i[EYE_STATE.c.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError2) {}
        try {
            i[EYE_STATE.a.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError3) {}
        try {
            i[EYE_STATE.b.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError4) {}
        try {
            i[EYE_STATE.e.ordinal()] = 5;
        }
        catch (NoSuchFieldError noSuchFieldError5) {}
        return EyeBlinkMotion.k = i;
    }
    
    enum EYE_STATE
    {
        a,
        b,
        c,
        d,
        e
        
    }
}
