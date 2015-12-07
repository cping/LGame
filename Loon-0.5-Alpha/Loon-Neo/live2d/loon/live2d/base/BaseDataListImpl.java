package loon.live2d.base;

import loon.live2d.*;
import loon.live2d.context.*;
import loon.live2d.id.*;
import loon.live2d.io.*;
import loon.live2d.util.*;
import loon.utils.TArray;

public class BaseDataListImpl extends IBaseData
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	loon.live2d.param.ParamIOList a;
    TArray<Object> list;
    static float[] c;
    static float[] d;
    static float[] e;
    static float[] f;
    static float[] g;
    static float[] h;
    static boolean[] i;
    static final boolean _flag;
    
    static {
    	_flag = true;
        c = new float[2];
        d = new float[2];
        e = new float[2];
        f = new float[2];
        g = new float[2];
        h = new float[2];
        i = new boolean[1];
    }
    
    public BaseDataListImpl() {
        this.a = null;
        this.list = null;
    }
    
    public void init() {
        (this.a = new loon.live2d.param.ParamIOList()).init();
        this.list = new TArray<Object>();
    }
    
    @Override
    public int b() {
        return 1;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
    public void readV2(final BReader br) {
        super.readV2(br);
        this.a = (loon.live2d.param.ParamIOList)br.reader();
        this.list = (TArray)br.reader();
        super.a(br);
    }
    
    @Override
    public IBaseContext a(final ModelContext modelContext) {
        final a a = new a(this);
        a.b = new loon.live2d.base.Vertex();
        if (this.f()) {
            a.c = new loon.live2d.base.Vertex();
        }
        return a;
    }
    
    @Override
    public void a(final ModelContext modelContext, final IBaseContext baseContext) {
        if (!loon.live2d.base.BaseDataListImpl._flag && this != baseContext.b()) {
            throw new AssertionError();
        }
        final a a = (a)baseContext;
        if (!this.a.a(modelContext)) {
            return;
        }
        final boolean[] i = loon.live2d.base.BaseDataListImpl.i;
        i[0] = false;
        final int a2 = this.a.loadParam(modelContext, i);
        baseContext.b(i[0]);
        this.a(modelContext, this.a, baseContext, i);
        final short[] tmpPivotTableIndicesRef = modelContext.getTmpPivotTableIndicesRef();
        final float[] tmpT_ArrayRef = modelContext.getTmpT_ArrayRef();
        this.a.a(tmpPivotTableIndicesRef, tmpT_ArrayRef, a2);
        if (a2 <= 0) {
            a.b.a((loon.live2d.base.Vertex)this.list.get(tmpPivotTableIndicesRef[0]));
        }
        else if (a2 == 1) {
            final loon.live2d.base.Vertex a3 = (loon.live2d.base.Vertex) this.list.get(tmpPivotTableIndicesRef[0]);
            final loon.live2d.base.Vertex a4 = (loon.live2d.base.Vertex) this.list.get(tmpPivotTableIndicesRef[1]);
            final float n = tmpT_ArrayRef[0];
            a.b.a = a3.a + (a4.a - a3.a) * n;
            a.b.b = a3.b + (a4.b - a3.b) * n;
            a.b.c = a3.c + (a4.c - a3.c) * n;
            a.b.d = a3.d + (a4.d - a3.d) * n;
            a.b.e = a3.e + (a4.e - a3.e) * n;
        }
        else if (a2 == 2) {
            final loon.live2d.base.Vertex a5 = (loon.live2d.base.Vertex) this.list.get(tmpPivotTableIndicesRef[0]);
            final loon.live2d.base.Vertex a6 = (loon.live2d.base.Vertex) this.list.get(tmpPivotTableIndicesRef[1]);
            final loon.live2d.base.Vertex a7 = (loon.live2d.base.Vertex) this.list.get(tmpPivotTableIndicesRef[2]);
            final loon.live2d.base.Vertex a8 = (loon.live2d.base.Vertex) this.list.get(tmpPivotTableIndicesRef[3]);
            final float n2 = tmpT_ArrayRef[0];
            final float n3 = tmpT_ArrayRef[1];
            final float n4 = a5.a + (a6.a - a5.a) * n2;
            a.b.a = n4 + (a7.a + (a8.a - a7.a) * n2 - n4) * n3;
            final float n5 = a5.b + (a6.b - a5.b) * n2;
            a.b.b = n5 + (a7.b + (a8.b - a7.b) * n2 - n5) * n3;
            final float n6 = a5.c + (a6.c - a5.c) * n2;
            a.b.c = n6 + (a7.c + (a8.c - a7.c) * n2 - n6) * n3;
            final float n7 = a5.d + (a6.d - a5.d) * n2;
            a.b.d = n7 + (a7.d + (a8.d - a7.d) * n2 - n7) * n3;
            final float n8 = a5.e + (a6.e - a5.e) * n2;
            a.b.e = n8 + (a7.e + (a8.e - a7.e) * n2 - n8) * n3;
        }
        else if (a2 == 3) {
            final loon.live2d.base.Vertex a9 = (loon.live2d.base.Vertex) this.list.get(tmpPivotTableIndicesRef[0]);
            final loon.live2d.base.Vertex a10 = (loon.live2d.base.Vertex) this.list.get(tmpPivotTableIndicesRef[1]);
            final loon.live2d.base.Vertex a11 = (loon.live2d.base.Vertex) this.list.get(tmpPivotTableIndicesRef[2]);
            final loon.live2d.base.Vertex a12 = (loon.live2d.base.Vertex) this.list.get(tmpPivotTableIndicesRef[3]);
            final loon.live2d.base.Vertex a13 = (loon.live2d.base.Vertex) this.list.get(tmpPivotTableIndicesRef[4]);
            final loon.live2d.base.Vertex a14 = (loon.live2d.base.Vertex) this.list.get(tmpPivotTableIndicesRef[5]);
            final loon.live2d.base.Vertex a15 = (loon.live2d.base.Vertex) this.list.get(tmpPivotTableIndicesRef[6]);
            final loon.live2d.base.Vertex a16 = (loon.live2d.base.Vertex) this.list.get(tmpPivotTableIndicesRef[7]);
            final float n9 = tmpT_ArrayRef[0];
            final float n10 = tmpT_ArrayRef[1];
            final float n11 = tmpT_ArrayRef[2];
            final float n12 = a9.a + (a10.a - a9.a) * n9;
            final float n13 = a11.a + (a12.a - a11.a) * n9;
            final float n14 = a13.a + (a14.a - a13.a) * n9;
            a.b.a = (1.0f - n11) * (n12 + (n13 - n12) * n10) + n11 * (n14 + (a15.a + (a16.a - a15.a) * n9 - n14) * n10);
            final float n15 = a9.b + (a10.b - a9.b) * n9;
            final float n16 = a11.b + (a12.b - a11.b) * n9;
            final float n17 = a13.b + (a14.b - a13.b) * n9;
            a.b.b = (1.0f - n11) * (n15 + (n16 - n15) * n10) + n11 * (n17 + (a15.b + (a16.b - a15.b) * n9 - n17) * n10);
            final float n18 = a9.c + (a10.c - a9.c) * n9;
            final float n19 = a11.c + (a12.c - a11.c) * n9;
            final float n20 = a13.c + (a14.c - a13.c) * n9;
            a.b.c = (1.0f - n11) * (n18 + (n19 - n18) * n10) + n11 * (n20 + (a15.c + (a16.c - a15.c) * n9 - n20) * n10);
            final float n21 = a9.d + (a10.d - a9.d) * n9;
            final float n22 = a11.d + (a12.d - a11.d) * n9;
            final float n23 = a13.d + (a14.d - a13.d) * n9;
            a.b.d = (1.0f - n11) * (n21 + (n22 - n21) * n10) + n11 * (n23 + (a15.d + (a16.d - a15.d) * n9 - n23) * n10);
            final float n24 = a9.e + (a10.e - a9.e) * n9;
            final float n25 = a11.e + (a12.e - a11.e) * n9;
            final float n26 = a13.e + (a14.e - a13.e) * n9;
            a.b.e = (1.0f - n11) * (n24 + (n25 - n24) * n10) + n11 * (n26 + (a15.e + (a16.e - a15.e) * n9 - n26) * n10);
        }
        else if (a2 == 4) {
            final loon.live2d.base.Vertex a17 = (loon.live2d.base.Vertex) this.list.get(tmpPivotTableIndicesRef[0]);
            final loon.live2d.base.Vertex a18 =  (loon.live2d.base.Vertex)this.list.get(tmpPivotTableIndicesRef[1]);
            final loon.live2d.base.Vertex a19 =  (loon.live2d.base.Vertex)this.list.get(tmpPivotTableIndicesRef[2]);
            final loon.live2d.base.Vertex a20 =  (loon.live2d.base.Vertex)this.list.get(tmpPivotTableIndicesRef[3]);
            final loon.live2d.base.Vertex a21 =  (loon.live2d.base.Vertex)this.list.get(tmpPivotTableIndicesRef[4]);
            final loon.live2d.base.Vertex a22 =  (loon.live2d.base.Vertex)this.list.get(tmpPivotTableIndicesRef[5]);
            final loon.live2d.base.Vertex a23 =  (loon.live2d.base.Vertex)this.list.get(tmpPivotTableIndicesRef[6]);
            final loon.live2d.base.Vertex a24 =  (loon.live2d.base.Vertex)this.list.get(tmpPivotTableIndicesRef[7]);
            final loon.live2d.base.Vertex a25 = (loon.live2d.base.Vertex) this.list.get(tmpPivotTableIndicesRef[8]);
            final loon.live2d.base.Vertex a26 = (loon.live2d.base.Vertex) this.list.get(tmpPivotTableIndicesRef[9]);
            final loon.live2d.base.Vertex a27 = (loon.live2d.base.Vertex) this.list.get(tmpPivotTableIndicesRef[10]);
            final loon.live2d.base.Vertex a28 = (loon.live2d.base.Vertex) this.list.get(tmpPivotTableIndicesRef[11]);
            final loon.live2d.base.Vertex a29 = (loon.live2d.base.Vertex) this.list.get(tmpPivotTableIndicesRef[12]);
            final loon.live2d.base.Vertex a30 = (loon.live2d.base.Vertex) this.list.get(tmpPivotTableIndicesRef[13]);
            final loon.live2d.base.Vertex a31 =  (loon.live2d.base.Vertex)this.list.get(tmpPivotTableIndicesRef[14]);
            final loon.live2d.base.Vertex a32 =  (loon.live2d.base.Vertex)this.list.get(tmpPivotTableIndicesRef[15]);
            final float n27 = tmpT_ArrayRef[0];
            final float n28 = tmpT_ArrayRef[1];
            final float n29 = tmpT_ArrayRef[2];
            final float n30 = tmpT_ArrayRef[3];
            final float n31 = a17.a + (a18.a - a17.a) * n27;
            final float n32 = a19.a + (a20.a - a19.a) * n27;
            final float n33 = a21.a + (a22.a - a21.a) * n27;
            final float n34 = a23.a + (a24.a - a23.a) * n27;
            final float n35 = a25.a + (a26.a - a25.a) * n27;
            final float n36 = a27.a + (a28.a - a27.a) * n27;
            final float n37 = a29.a + (a30.a - a29.a) * n27;
            a.b.a = (1.0f - n30) * ((1.0f - n29) * (n31 + (n32 - n31) * n28) + n29 * (n33 + (n34 - n33) * n28)) + n30 * ((1.0f - n29) * (n35 + (n36 - n35) * n28) + n29 * (n37 + (a31.a + (a32.a - a31.a) * n27 - n37) * n28));
            final float n38 = a17.b + (a18.b - a17.b) * n27;
            final float n39 = a19.b + (a20.b - a19.b) * n27;
            final float n40 = a21.b + (a22.b - a21.b) * n27;
            final float n41 = a23.b + (a24.b - a23.b) * n27;
            final float n42 = a25.b + (a26.b - a25.b) * n27;
            final float n43 = a27.b + (a28.b - a27.b) * n27;
            final float n44 = a29.b + (a30.b - a29.b) * n27;
            a.b.b = (1.0f - n30) * ((1.0f - n29) * (n38 + (n39 - n38) * n28) + n29 * (n40 + (n41 - n40) * n28)) + n30 * ((1.0f - n29) * (n42 + (n43 - n42) * n28) + n29 * (n44 + (a31.b + (a32.b - a31.b) * n27 - n44) * n28));
            final float n45 = a17.c + (a18.c - a17.c) * n27;
            final float n46 = a19.c + (a20.c - a19.c) * n27;
            final float n47 = a21.c + (a22.c - a21.c) * n27;
            final float n48 = a23.c + (a24.c - a23.c) * n27;
            final float n49 = a25.c + (a26.c - a25.c) * n27;
            final float n50 = a27.c + (a28.c - a27.c) * n27;
            final float n51 = a29.c + (a30.c - a29.c) * n27;
            a.b.c = (1.0f - n30) * ((1.0f - n29) * (n45 + (n46 - n45) * n28) + n29 * (n47 + (n48 - n47) * n28)) + n30 * ((1.0f - n29) * (n49 + (n50 - n49) * n28) + n29 * (n51 + (a31.c + (a32.c - a31.c) * n27 - n51) * n28));
            final float n52 = a17.d + (a18.d - a17.d) * n27;
            final float n53 = a19.d + (a20.d - a19.d) * n27;
            final float n54 = a21.d + (a22.d - a21.d) * n27;
            final float n55 = a23.d + (a24.d - a23.d) * n27;
            final float n56 = a25.d + (a26.d - a25.d) * n27;
            final float n57 = a27.d + (a28.d - a27.d) * n27;
            final float n58 = a29.d + (a30.d - a29.d) * n27;
            a.b.d = (1.0f - n30) * ((1.0f - n29) * (n52 + (n53 - n52) * n28) + n29 * (n54 + (n55 - n54) * n28)) + n30 * ((1.0f - n29) * (n56 + (n57 - n56) * n28) + n29 * (n58 + (a31.d + (a32.d - a31.d) * n27 - n58) * n28));
            final float n59 = a17.e + (a18.e - a17.e) * n27;
            final float n60 = a19.e + (a20.e - a19.e) * n27;
            final float n61 = a21.e + (a22.e - a21.e) * n27;
            final float n62 = a23.e + (a24.e - a23.e) * n27;
            final float n63 = a25.e + (a26.e - a25.e) * n27;
            final float n64 = a27.e + (a28.e - a27.e) * n27;
            final float n65 = a29.e + (a30.e - a29.e) * n27;
            a.b.e = (1.0f - n30) * ((1.0f - n29) * (n59 + (n60 - n59) * n28) + n29 * (n61 + (n62 - n61) * n28)) + n30 * ((1.0f - n29) * (n63 + (n64 - n63) * n28) + n29 * (n65 + (a31.e + (a32.e - a31.e) * n27 - n65) * n28));
        }
        else {
            final int n66 = (int)Math.pow(2.0, a2);
            final float[] array = new float[n66];
            for (int j = 0; j < n66; ++j) {
                int n67 = j;
                float n68 = 1.0f;
                for (int k = 0; k < a2; ++k) {
                    n68 *= ((n67 % 2 == 0) ? (1.0f - tmpT_ArrayRef[k]) : tmpT_ArrayRef[k]);
                    n67 /= 2;
                }
                array[j] = n68;
            }
            final loon.live2d.base.Vertex[] array2 = new loon.live2d.base.Vertex[n66];
            for (int l = 0; l < n66; ++l) {
                array2[l] = (loon.live2d.base.Vertex)this.list.get(tmpPivotTableIndicesRef[l]);
            }
            float a33 = 0.0f;
            float b = 0.0f;
            float c = 0.0f;
            float d = 0.0f;
            float e = 0.0f;
            for (int n69 = 0; n69 < n66; ++n69) {
                a33 += array[n69] * array2[n69].a;
                b += array[n69] * array2[n69].b;
                c += array[n69] * array2[n69].c;
                d += array[n69] * array2[n69].d;
                e += array[n69] * array2[n69].e;
            }
            a.b.a = a33;
            a.b.b = b;
            a.b.c = c;
            a.b.d = d;
            a.b.e = e;
        }
        final loon.live2d.base.Vertex a34 = (loon.live2d.base.Vertex) this.list.get(tmpPivotTableIndicesRef[0]);
        a.b.f = a34.f;
        a.b.g = a34.g;
    }
    
    @Override
    public void b(final ModelContext modelContext, final IBaseContext baseContext) {
        if (!loon.live2d.base.BaseDataListImpl._flag && this != baseContext.b()) {
            throw new AssertionError();
        }
        final a a = (a)baseContext;
        a.a(true);
        if (!this.f()) {
            a.a(a.b.c);
            a.c(a.f());
        }
        else {
            final BaseDataID d = this.d();
            if (a.a == -2) {
                a.a = modelContext.getBaseDataIndex(d);
            }
            if (a.a < 0) {
                a.a(false);
            }
            else {
                final IBaseData baseData = modelContext.getBaseData(a.a);
                if (baseData != null) {
                    final IBaseContext baseContext2 = modelContext.getBaseContext(a.a);
                    final float[] c = loon.live2d.base.BaseDataListImpl.c;
                    c[0] = a.b.a;
                    c[1] = a.b.b;
                    final float[] d2 = loon.live2d.base.BaseDataListImpl.d;
                    d2[0] = 0.0f;
                    d2[1] = -0.1f;
                    if (baseContext2.b().b() == 1) {
                        d2[1] = -10.0f;
                    }
                    else {
                        d2[1] = -0.1f;
                    }
                    final float[] e = loon.live2d.base.BaseDataListImpl.e;
                    this.a(modelContext, baseData, baseContext2, c, d2, e);
                    final float n = UtMath.getAngleNotAbs(d2, e);
                    baseData.a(modelContext, baseContext2, c, c, 1, 0, 2);
                    a.c.a = c[0];
                    a.c.b = c[1];
                    a.c.c = a.b.c;
                    a.c.d = a.b.d;
                    a.c.e = a.b.e - n * UtMath.d;
                    a.a(baseContext2.e() * a.c.c);
                    a.c(baseContext2.g() * a.f());
                    a.c.f = a.b.f;
                    a.c.g = a.b.g;
                    a.a(baseContext2.a());
                }
                else {
                    a.a(false);
                }
            }
        }
    }
    
    @Override
    public void a(final ModelContext modelContext, final IBaseContext baseContext, final float[] array, final float[] array2, final int n, final int n2, final int n3) {
        if (!loon.live2d.base.BaseDataListImpl._flag && this != baseContext.b()) {
            throw new AssertionError();
        }
        final a a = (a)baseContext;
        final loon.live2d.base.Vertex a2 = (a.c != null) ? a.c : a.b;
        final float n4 = (float)Math.sin(UtMath.b * a2.e);
        final float n5 = (float)Math.cos(UtMath.b * a2.e);
        final float e = a.e();
        final float n6 = a2.f ? -1 : 1;
        final float n7 = a2.g ? -1 : 1;
        final float n8 = n5 * e * n6;
        final float n9 = -n4 * e * n7;
        final float n10 = n4 * e * n6;
        final float n11 = n5 * e * n7;
        final float a3 = a2.a;
        final float b = a2.b;
        for (int n12 = n * n3, i = n2; i < n12; i += n3) {
            final float n13 = array[i];
            final float n14 = array[i + 1];
            array2[i] = n8 * n13 + n9 * n14 + a3;
            array2[i + 1] = n10 * n13 + n11 * n14 + b;
        }
    }
    
    void a(final ModelContext modelContext, final IBaseData baseData, final IBaseContext baseContext, final float[] array, final float[] array2, final float[] array3) {
        if (!loon.live2d.base.BaseDataListImpl._flag && baseData != baseContext.b()) {
            throw new AssertionError();
        }
        final float[] f = loon.live2d.base.BaseDataListImpl.f;
        loon.live2d.base.BaseDataListImpl.f[0] = array[0];
        loon.live2d.base.BaseDataListImpl.f[1] = array[1];
        baseData.a(modelContext, baseContext, f, f, 1, 0, 2);
        final float[] g = loon.live2d.base.BaseDataListImpl.g;
        final float[] h = loon.live2d.base.BaseDataListImpl.h;
        final int n = 10;
        float n2 = 1.0f;
        for (int i = 0; i < n; ++i) {
            h[0] = array[0] + n2 * array2[0];
            h[1] = array[1] + n2 * array2[1];
            baseData.a(modelContext, baseContext, h, g, 1, 0, 2);
            final float[] array4 = g;
            final int n3 = 0;
            array4[n3] -= f[0];
            final float[] array5 = g;
            final int n4 = 1;
            array5[n4] -= f[1];
            if (g[0] != 0.0f || g[1] != 0.0f) {
                array3[0] = g[0];
                array3[1] = g[1];
                return;
            }
            h[0] = array[0] - n2 * array2[0];
            h[1] = array[1] - n2 * array2[1];
            baseData.a(modelContext, baseContext, h, g, 1, 0, 2);
            final float[] array6 = g;
            final int n5 = 0;
            array6[n5] -= f[0];
            final float[] array7 = g;
            final int n6 = 1;
            array7[n6] -= f[1];
            if (g[0] != 0.0f || g[1] != 0.0f) {
                g[0] = -g[0];
                array3[0] = (g[0] = -g[0]);
                array3[1] = g[1];
                return;
            }
            n2 *= 0.1;
        }
    }
    
    public class a extends IBaseContext
    {
        int a;
        loon.live2d.base.Vertex b;
        loon.live2d.base.Vertex c;
        
        a(final BaseDataListImpl src) {
            super(src);
            this.a = -2;
            this.b = null;
            this.c = null;
        }
    }
}
