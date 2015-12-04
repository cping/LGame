package loon.live2d.motion;

import loon.live2d.*;
import loon.live2d.util.*;
import loon.utils.ArrayByte;
import loon.utils.ListMap;
import loon.utils.TArray;

public class Live2DMotion extends AMotion
{
    static final String e = "VISIBLE:";
    static final String f = "LAYOUT:";
    TArray list;
    float h;
    int i;
    int j;
    int k;
    boolean l;
    int m;
    float n;
    static int o;
    static transient a p;
    static final int q = 1;
    
    static {
        Live2DMotion.o = 0;
        Live2DMotion.p = new a();
    }
    
    public Live2DMotion() {
        this.list = new TArray();
        this.m = Live2DMotion.o++;
        this.h = 30.0f;
        this.i = 0;
        this.l = false;
        this.k = -1;
        this.n = 0.0f;
    }
    
    public static Live2DMotion loadMotion(final ArrayByte bin) {
        return loadMotion(bin.getData());
    }
    
    public static Live2DMotion loadMotion(final byte[] str) {
        final Live2DMotion live2DMotion = new Live2DMotion();
        final ListMap<String, Integer> hashMap = new ListMap<String, Integer>();
        final ListMap<String, Integer> hashMap2 = new ListMap<String, Integer>();
        final int[] array = { 0 };
        final int length = str.length;
        live2DMotion.i = 0;
        for (int i = 0; i < length; ++i) {
            final char c = (char)(str[i] & 0xFF);
            if (c != '\n') {
                if (c != '\r') {
                    if (c == '#') {
                        while (i < length) {
                            if (str[i] == 10) {
                                break;
                            }
                            if (str[i] == 13) {
                                break;
                            }
                            ++i;
                        }
                    }
                    else if (c == '$') {
                        final int n = i;
                        int n2 = -1;
                        int n3 = -1;
                        while (i < length) {
                            final char c2 = (char)(str[i] & 0xFF);
                            if (c2 == '\r') {
                                break;
                            }
                            if (c2 == '\n') {
                                break;
                            }
                            if (c2 == ':') {
                                n3 = i + 1;
                            }
                            else if (c2 == '=') {
                                n2 = i;
                                break;
                            }
                            ++i;
                        }
                        if (n2 >= 0) {
                            float h = -1.0f;
                            for (int j = n2 + 1; j < length; ++j) {
                                final char c3 = (char)(str[j] & 0xFF);
                                if (c3 == '\r') {
                                    break;
                                }
                                if (c3 == '\n') {
                                    break;
                                }
                                if (c3 != ',' && c3 != ' ') {
                                    if (c3 != '\t') {
                                        final float n4 = (float)loon.live2d.util.StringUtil.indexOf(str, length, j, array);
                                        if (array[0] > 0) {
                                            h = n4;
                                        }
                                        j = array[0] - 1;
                                    }
                                }
                            }
                            if (n2 == n + 4 && str[n + 1] == 102 && str[n + 2] == 112 && str[n + 3] == 115 && 5.0f < h && h < 121.0f) {
                                live2DMotion.h = h;
                            }
                            if (str[n + 1] == 102 && str[n + 2] == 97 && str[n + 3] == 100 && str[n + 4] == 101 && str[n + 5] == 105 && str[n + 6] == 110) {
                                if (str[n + 7] == 61) {
                                    if (0.0f <= h) {
                                        live2DMotion.a = (int)h;
                                    }
                                }
                                else if (str[n + 7] == 58) {
                                    hashMap.put(new String(str, n3, n2 - n3), (int)h);
                                }
                            }
                            if (str[n + 1] == 102 && str[n + 2] == 97 && str[n + 3] == 100 && str[n + 4] == 101 && str[n + 5] == 111 && str[n + 6] == 117 && str[n + 7] == 116) {
                                if (str[n + 8] == 61) {
                                    if (0.0f <= h) {
                                        live2DMotion.b = (int)h;
                                    }
                                }
                                else if (str[n + 8] == 58) {
                                    hashMap2.put(new String(str, n3, n2 - n3), (int)h);
                                }
                            }
                            i = array[0] - 1;
                        }
                        while (i < length) {
                            if (str[i] == 10) {
                                break;
                            }
                            if (str[i] == 13) {
                                break;
                            }
                            ++i;
                        }
                    }
                    else if (('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z') || c == '_') {
                        final int n5 = i;
                        int n6 = -1;
                        while (i < length) {
                            final char c4 = (char)(str[i] & 0xFF);
                            if (c4 == '\r') {
                                break;
                            }
                            if (c4 == '\n') {
                                break;
                            }
                            if (c4 == '=') {
                                n6 = i;
                                break;
                            }
                            ++i;
                        }
                        if (n6 >= 0) {
                            final Motion motion = new Motion();
                            if (loon.live2d.util.StringUtil.indexOf(str, n5, "VISIBLE:")) {
                                motion.m = 1;
                                motion.k = new String(str, n5, n6 - n5);
                            }
                            else if (loon.live2d.util.StringUtil.indexOf(str, n5, "LAYOUT:")) {
                                motion.k = new String(str, n5 + 7, n6 - n5 - 7);
                                if (loon.live2d.util.StringUtil.indexOf(str, n5 + 7, "ANCHOR_X")) {
                                    motion.m = 102;
                                }
                                else if (loon.live2d.util.StringUtil.indexOf(str, n5 + 7, "ANCHOR_Y")) {
                                    motion.m = 103;
                                }
                                else if (loon.live2d.util.StringUtil.indexOf(str, n5 + 7, "SCALE_X")) {
                                    motion.m = 104;
                                }
                                else if (loon.live2d.util.StringUtil.indexOf(str, n5 + 7, "SCALE_Y")) {
                                    motion.m = 105;
                                }
                                else if (loon.live2d.util.StringUtil.indexOf(str, n5 + 7, "X")) {
                                    motion.m = 100;
                                }
                                else if (loon.live2d.util.StringUtil.indexOf(str, n5 + 7, "Y")) {
                                    motion.m = 101;
                                }
                            }
                            else {
                                motion.m = 0;
                                motion.k = new String(str, n5, n6 - n5);
                            }
                            live2DMotion.list.add(motion);
                            int k = 0;
                            Live2DMotion.p.a();
                            for (i = n6 + 1; i < length; ++i) {
                                final char c5 = (char)(str[i] & 0xFF);
                                if (c5 == '\r') {
                                    break;
                                }
                                if (c5 == '\n') {
                                    break;
                                }
                                if (c5 != ',' && c5 != ' ') {
                                    if (c5 != '\t') {
                                        final float n7 = (float)loon.live2d.util.StringUtil.indexOf(str, length, i, array);
                                        if (array[0] > 0) {
                                            Live2DMotion.p.a(n7);
                                            ++k;
                                            final int n8 = array[0];
                                            if (n8 < i) {
                                                break;
                                            }
                                            i = n8 - 1;
                                        }
                                    }
                                }
                            }
                            motion.l = Live2DMotion.p.b();
                            if (k > live2DMotion.i) {
                                live2DMotion.i = k;
                            }
                        }
                    }
                }
            }
        }
        live2DMotion.k = (int)(1000 * live2DMotion.i / live2DMotion.h);
        for (int i=0;i<hashMap.size;i++) {
            live2DMotion.a(hashMap.getKeyAt(i),hashMap.getValueAt(i));
        }
        for (int i=0;i<hashMap2.size;i++) {
            live2DMotion.b(hashMap2.getKeyAt(i),hashMap2.getValueAt(i));
        }
        return live2DMotion;
    }
    
    @Override
    public int getDurationMSec() {
        return this.l ? -1 : this.k;
    }
    
    @Override
    public int getLoopDurationMSec() {
        return this.k;
    }
    
    
    @Override
    public void updateParamExe(final ALive2DModel model, final long timeMSec, final float _weight, final MotionQueueManager.MotionQueueEnt motionQueueEnt) {
        final float n = (timeMSec - motionQueueEnt.d) * this.h / 1000.0f;
        final int n2 = (int)n;
        final float n3 = n - n2;
        final int n4 = (int)((this.a == 0) ? 1.0f : UtMath.a((timeMSec - motionQueueEnt.e) / this.a));
        final int n5 = (int)((this.b == 0 || motionQueueEnt.f < 0L) ? 1.0f : UtMath.a((motionQueueEnt.f - timeMSec) / this.b));
        for (int i = 0; i < this.list.size; ++i) {
            final Motion motion = (Motion) this.list.get(i);
            final int length = motion.l.length;
            final String k = motion.k;
            if (motion.m == 1) {
                model.setParamFloat(k, motion.l[(n2 >= length) ? (length - 1) : n2]);
            }
            else if (100 > motion.m || motion.m > 105) {
                final int paramIndex = model.getParamIndex(k);
                final ModelContext modelContext = model.getModelContext();
                final float n6 = 0.4f * (modelContext.getParamMax(paramIndex) - modelContext.getParamMin(paramIndex));
                final float paramFloat = modelContext.getParamFloat(paramIndex);
                final float n7 = motion.l[(n2 >= length) ? (length - 1) : n2];
                final float n8 = motion.l[(n2 + 1 >= length) ? (length - 1) : (n2 + 1)];
                float n9;
                if ((n7 < n8 && n8 - n7 > n6) || (n7 > n8 && n7 - n8 > n6)) {
                    n9 = n7;
                }
                else {
                    n9 = n7 + (n8 - n7) * n3;
                }
                float value;
                if (motion.n < 0 && motion.o < 0) {
                    value = paramFloat + (n9 - paramFloat) * _weight;
                }
                else {
                    float n10;
                    if (motion.n < 0) {
                        n10 = n4;
                    }
                    else {
                        n10 = ((motion.n == 0) ? 1.0f : UtMath.a((timeMSec - motionQueueEnt.e) / motion.n));
                    }
                    float n11;
                    if (motion.o < 0) {
                        n11 = n5;
                    }
                    else {
                        n11 = ((motion.o == 0 || motionQueueEnt.f < 0L) ? 1.0f : UtMath.a((motionQueueEnt.f - timeMSec) / motion.o));
                    }
                    value = paramFloat + (n9 - paramFloat) * (this.c * n10 * n11);
                }
                model.setParamFloat(k, value);
            }
        }
        if (n2 >= this.i) {
            if (this.l) {
                motionQueueEnt.d = timeMSec;
                motionQueueEnt.e = timeMSec;
            }
            else {
                motionQueueEnt.c = true;
            }
        }
        this.n = this.c;
    }
    
    public boolean isLoop() {
        return this.l;
    }
    
    public void setLoop(final boolean loop) {
        this.l = loop;
    }
    
    public float getFPS() {
        return this.h;
    }
    
    public void setFPS(final float fps) {
        this.h = fps;
    }
    
    void a(final String s, final int n) {
        for (int i = 0; i < this.list.size; ++i) {
            final Motion motion = (Motion) this.list.get(i);
            if (s.equals(motion.k)) {
                motion.n = n;
                return;
            }
        }
    }
    
    void b(final String s, final int o) {
        for (int i = 0; i < this.list.size; ++i) {
            final Motion motion = (Motion) this.list.get(i);
            if (s.equals(motion.k)) {
                motion.o = o;
                return;
            }
        }
    }
    
    float a(final String s) {
        for (int i = 0; i < this.list.size; ++i) {
            final Motion motion = (Motion) this.list.get(i);
            if (s.equals(motion.k)) {
                return motion.n;
            }
        }
        return -1.0f;
    }
    
    float b(final String s) {
        for (int i = 0; i < this.list.size; ++i) {
            final Motion motion = (Motion) this.list.get(i);
            if (s.equals(motion.k)) {
                return motion.o;
            }
        }
        return -1.0f;
    }
    
    static class a
    {
        float[] a;
        int b;
        
        a() {
            this.a = new float[100];
            this.b = 0;
        }
        
        void a() {
            this.b = 0;
        }
        
        void a(final float n) {
            if (this.a.length <= this.b) {
                final float[] a = new float[this.b * 2];
                System.arraycopy(this.a, 0, a, 0, this.b);
                this.a = a;
            }
            this.a[this.b++] = n;
        }
        
        float[] b() {
            final float[] array = new float[this.b];
            System.arraycopy(this.a, 0, array, 0, this.b);
            return array;
        }
    }
    
    static class Motion
    {
        public static final int a = 0;
        public static final int b = 1;
        public static final int c = 2;
        public static final int d = 3;
        public static final int e = 100;
        public static final int f = 101;
        public static final int g = 102;
        public static final int h = 103;
        public static final int i = 104;
        public static final int j = 105;
        String k;
        float[] l;
        int m;
        int n;
        int o;
        
        public Motion() {
            this.k = null;
            this.n = -1;
            this.o = -1;
        }
    }
}
