package loon.live2d.motion;

import java.util.*;

import loon.live2d.*;
import loon.live2d.util.*;
import loon.utils.TArray;

public class MotionQueueManager
{
    TArray list;
    
    public MotionQueueManager() {
        this.list = new TArray();
    }
    
    public TArray getMotions_test() {
        return this.list;
    }
    
    public int startMotion(final AMotion motion, final boolean autoDelete) {
        final int size = this.list.size;
        for (int i = 0; i < size; ++i) {
            final MotionQueueEnt motionQueueEnt = (MotionQueueEnt) this.list.get(i);
            if (motionQueueEnt != null) {
                motionQueueEnt.startFadeout(motionQueueEnt.a.getFadeOut());
            }
        }
        if (motion == null) {
            return -1;
        }
        final MotionQueueEnt motionQueueEnt2 = new MotionQueueEnt();
        motionQueueEnt2.a = motion;
        this.list.add(motionQueueEnt2);
        final int h = motionQueueEnt2.h;
        return h;
    }
    
    public boolean updateParam(final ALive2DModel model) {
        try {
            boolean b = false;
            final Iterator iterator = this.list.iterator();
            while (iterator.hasNext()) {
                final MotionQueueEnt motionQueueEnt = (MotionQueueEnt) iterator.next();
                if (motionQueueEnt == null) {
                    iterator.remove();
                }
                else {
                    final AMotion a = motionQueueEnt.a;
                    if (a == null) {
                        iterator.remove();
                    }
                    else {
                        a.updateParam(model, motionQueueEnt);
                        b = true;
                        if (!motionQueueEnt.a()) {
                            continue;
                        }
                        iterator.remove();
                    }
                }
            }
            return b;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return true;
        }
    }
    
    public boolean isFinished() {
        final Iterator<MotionQueueEnt> iterator = (Iterator<MotionQueueEnt>)this.list.iterator();
        while (iterator.hasNext()) {
            final MotionQueueEnt motionQueueEnt = iterator.next();
            if (motionQueueEnt == null) {
                iterator.remove();
            }
            else if (motionQueueEnt.a == null) {
                iterator.remove();
            }
            else {
                if (!motionQueueEnt.a()) {
                    return false;
                }
                continue;
            }
        }
        return true;
    }
    
    public boolean isFinished(final int _motionQueueEntNo) {
        for (final Object e : this.list) {
        	MotionQueueEnt motionQueueEnt=(MotionQueueEnt)e;
            if (motionQueueEnt == null) {
                continue;
            }
            if (motionQueueEnt.h == _motionQueueEntNo && !motionQueueEnt.a()) {
                return false;
            }
        }
        return true;
    }
    
    public void stopAllMotions() {
        final Iterator<MotionQueueEnt> iterator = (Iterator<MotionQueueEnt>)this.list.iterator();
        while (iterator.hasNext()) {
            final MotionQueueEnt motionQueueEnt = iterator.next();
            if (motionQueueEnt == null) {
                iterator.remove();
            }
            else if (motionQueueEnt.a == null) {
                iterator.remove();
            }
            else {
                iterator.remove();
            }
        }
    }
    
 
    public static class MotionQueueEnt
    {
        AMotion a;
        boolean b;
        boolean c;
        long d;
        long e;
        long f;
        static int g;
        int h;
        
        static {
            MotionQueueEnt.g = 0;
        }
        
        MotionQueueEnt() {
            this.a = null;
            this.b = true;
            this.c = false;
            this.d = -1L;
            this.e = -1L;
            this.f = -1L;
            this.h = MotionQueueEnt.g++;
        }
        
        boolean a() {
            return this.c;
        }
        
        public void startFadeout(final long fadeOutMsec) {
            final long f = UtSystem.getUserTimeMSec() + fadeOutMsec;
            if (this.f < 0L || f < this.f) {
                this.f = f;
            }
        }
        
        public int getMotionQueueNo_test() {
            return this.h;
        }
    }
}
