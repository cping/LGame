package loon.live2d.param;

import loon.live2d.*;
import loon.live2d.id.*;
import loon.live2d.io.*;
import loon.utils.TArray;

public class ParamIOList implements loon.live2d.io.IOBase
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	TArray<ParamIO> list;
    
    public ParamIOList() {
        this.list = null;
    }
    
    public void init() {
        this.list = new TArray<ParamIO>();
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
    public void readV2(final BReader br) {
        this.list = (TArray)br.reader();
    }
    
    public boolean a(final ModelContext modelContext) {
        if (modelContext.requireSetup()) {
            return true;
        }
        final int initVersion = modelContext.getInitVersion();
        for (int i = this.list.size - 1; i >= 0; --i) {
            int paramIndex = ((ParamIO)this.list.get(i)).a(initVersion);
            if (paramIndex == -2) {
            	paramIndex = modelContext.getParamIndex(((ParamIO)this.list.get(i)).a());
            }
            if (modelContext.isParamUpdated(paramIndex)) {
                return true;
            }
        }
        return false;
    }
    
    public int loadParam(final ModelContext modelContext, final boolean[] array) {
        final int size = this.list.size;
        final int initVersion = modelContext.getInitVersion();
        int n = 0;
        for (int i = 0; i < size; ++i) {
            final ParamIO a = (loon.live2d.param.ParamIO) this.list.get(i);
            int paramIndex = a.a(initVersion);
            if (paramIndex == -2) {
                paramIndex = modelContext.getParamIndex(a.a());
                a.a(paramIndex, initVersion);
            }
            if (paramIndex < 0) {
                throw new RuntimeException("PivotManager#calcPivotValue() :tmpParamIndex < 0 " + a.a());
            }
            final float n2 = (paramIndex < 0) ? 0.0f : modelContext.getParamFloat(paramIndex);
            final int b = a.b();
            final float[] c = a.c();
            int n3 = -1;
            float n4 = 0.0f;
            if (b >= 1) {
                if (b == 1) {
                    final float n5 = c[0];
                    if (n5 - 1.0E-4f < n2 && n2 < n5 + 1.0E-4f) {
                        n3 = 0;
                        n4 = 0.0f;
                    }
                    else {
                        n3 = 0;
                        array[0] = true;
                    }
                }
                else {
                    float n6 = c[0];
                    if (n2 < n6 - 1.0E-4f) {
                        n3 = 0;
                        array[0] = true;
                    }
                    else if (n2 < n6 + 1.0E-4f) {
                        n3 = 0;
                    }
                    else {
                        boolean b2 = false;
                        for (int j = 1; j < b; ++j) {
                            final float n7 = c[j];
                            if (n2 < n7 + 1.0E-4f) {
                                if (n7 - 1.0E-4f < n2) {
                                    n3 = j;
                                }
                                else {
                                    n3 = j - 1;
                                    n4 = (n2 - n6) / (n7 - n6);
                                    ++n;
                                }
                                b2 = true;
                                break;
                            }
                            n6 = n7;
                        }
                        if (!b2) {
                            n3 = b - 1;
                            n4 = 0.0f;
                            array[0] = true;
                        }
                    }
                }
            }
            a.b(n3);
            a.a(n4);
        }
        return n;
    }
    
    public void a(final short[] array, final float[] array2, final int n) {
        final int n2 = 1 << n;
        final int size = this.list.size;
        int n3 = 1;
        int n4 = 1;
        int n5 = 0;
        for (int i = 0; i < n2; ++i) {
            array[i] = 0;
        }
        for (int j = 0; j < size; ++j) {
            final ParamIO a = (loon.live2d.param.ParamIO) this.list.get(j);
            if (a.e() == 0.0f) {
                final int n6 = a.d() * n3;
                for (int k = 0; k < n2; ++k) {
                    final int n7 = k;
                    array[n7] += (short)n6;
                }
            }
            else {
                final int n8 = n3 * a.d();
                final int n9 = n3 * (a.d() + 1);
                for (int l = 0; l < n2; ++l) {
                    final int n10 = l;
                    array[n10] += (short)((l / n4 % 2 == 0) ? n8 : n9);
                }
                array2[n5++] = a.e();
                n4 *= 2;
            }
            n3 *= a.b();
        }
        array[n2] = 32767;
        array2[n5] = -1.0f;
    }
    
    public void a(final ParamID paramID, final int n, final float[] array) {
        final float[] array2 = new float[n];
        for (int i = 0; i < n; ++i) {
            array2[i] = array[i];
        }
        final ParamIO a = new ParamIO();
        a.a(paramID);
        a.a(n, array2);
        this.list.add(a);
    }
    
    public void a(final int n) {
        int n2 = n;
        for (int size = this.list.size, i = 0; i < size; ++i) {
            final ParamIO a = (loon.live2d.param.ParamIO) this.list.get(i);
            final int b = a.b();
            final int n3 = n2 % a.b();
            n2 /= b;
        }
    }
    
    public int size() {
        return this.list.size;
    }
    
    public TArray<ParamIO> getList() {
        return this.list;
    }
}
