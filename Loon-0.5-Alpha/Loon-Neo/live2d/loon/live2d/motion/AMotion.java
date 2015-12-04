package loon.live2d.motion;

import loon.live2d.*;
import loon.live2d.util.*;

public abstract class AMotion
{
    int a;
    int b;
    float c;
    static final /* synthetic */ boolean d;
    
    static {
        d = !AMotion.class.desiredAssertionStatus();
    }
    
    public AMotion() {
        this.a = 1000;
        this.b = 1000;
        this.c = 1.0f;
        this.reinit();
    }
    
    public void reinit() {
    }
    
    public void setFadeIn(final int fadeInMsec) {
        this.a = fadeInMsec;
    }
    
    public void setFadeOut(final int fadeOutMsec) {
        this.b = fadeOutMsec;
    }
    
    public void setWeight(final float weight) {
        this.c = weight;
    }
    
    public int getFadeOut() {
        return this.b;
    }
    
    public int getFadeIn() {
        return this.b;
    }
    
    public float getWeight() {
        return this.c;
    }
    
    public int getDurationMSec() {
        return -1;
    }
    
    public int getLoopDurationMSec() {
        return -1;
    }
    
    public static float getEasing(final float time, final float totalTime, final float accelerateTime) {
        final float n = time / totalTime;
        final float n3;
        final float n2 = n3 = accelerateTime / totalTime;
        final float n4 = 0.33333334f;
        final float n5 = 0.6666667f;
        final float n6 = 1.0f - (1.0f - n2) * (1.0f - n2);
        final float n7 = 1.0f - (1.0f - n3) * (1.0f - n3);
        final float n8 = 0.0f;
        final float n9 = (1.0f - n2) * n4 * n6 + (n3 * n5 + (1.0f - n3) * n4) * (1.0f - n6);
        final float n10 = (n3 + (1.0f - n3) * n5) * n7 + (n2 * n4 + (1.0f - n2) * n5) * (1.0f - n7);
        final float n11 = 1.0f - 3.0f * n10 + 3.0f * n9 - n8;
        final float n12 = 3.0f * n10 - 6.0f * n9 + 3.0f * n8;
        final float n13 = 3.0f * n9 - 3.0f * n8;
        final float n14 = n8;
        if (n <= 0.0f) {
            return 0.0f;
        }
        if (n >= 1.0f) {
            return 1.0f;
        }
        final float n15 = n;
        final float n16 = n15 * n15;
        return n11 * (n15 * n16) + n12 * n16 + n13 * n15 + n14;
    }
    
    public void updateParam(final ALive2DModel model, final MotionQueueManager.MotionQueueEnt motionQueueEnt) {
        if (!motionQueueEnt.b || motionQueueEnt.c) {
            return;
        }
        final long userTimeMSec = UtSystem.getUserTimeMSec();
        if (motionQueueEnt.d < 0L) {
            motionQueueEnt.d = userTimeMSec;
            motionQueueEnt.e = userTimeMSec;
            final int durationMSec = this.getDurationMSec();
            if (motionQueueEnt.f < 0L) {
                motionQueueEnt.f = ((durationMSec <= 0) ? -1L : (motionQueueEnt.d + durationMSec));
            }
        }
        final float n = this.c * ((this.a == 0) ? 1.0f : UtMath.a((userTimeMSec - motionQueueEnt.e) / this.a)) * ((this.b == 0 || motionQueueEnt.f < 0L) ? 1.0f : UtMath.a((motionQueueEnt.f - userTimeMSec) / this.b));
        if (!AMotion.d && (0.0f > n || n > 1.0f)) {
            throw new AssertionError();
        }
        this.updateParamExe(model, userTimeMSec, n, motionQueueEnt);
        if (motionQueueEnt.f > 0L && motionQueueEnt.f < userTimeMSec) {
            motionQueueEnt.c = true;
        }
    }
    
    public abstract void updateParamExe(final ALive2DModel p0, final long p1, final float p2, final MotionQueueManager.MotionQueueEnt p3);
}
