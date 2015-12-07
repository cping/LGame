package loon.live2d.base;

import loon.live2d.*;
import loon.live2d.context.*;
import loon.live2d.id.*;
import loon.live2d.io.*;
import loon.utils.TArray;

public class BaseDataImpl extends IBaseData
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int a;
    int b;
    loon.live2d.param.ParamIOList c;
    TArray<Object> list;
    static boolean[] e;
    
    static {
        e = new boolean[1];
    }
    
    public BaseDataImpl() {
        this.a = 0;
        this.b = 0;
        this.c = null;
        this.list = null;
    }
    
    public void init() {
        (this.c = new loon.live2d.param.ParamIOList()).init();
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    public void readV2(final BReader br) {
        super.readV2(br);
        this.b = br.readInt();
        this.a = br.readInt();
        this.c = (loon.live2d.param.ParamIOList)br.reader();
        this.list = (TArray)br.reader();
        super.a(br);
    }
    
    @Override
    public IBaseContext a(final ModelContext modelContext) {
        final BaseContextImpl a = new BaseContextImpl(this);
        final int n = (this.a + 1) * (this.b + 1);
        if (a.b != null) {
            a.b = null;
        }
        a.b = new float[n * 2];
        if (a.c != null) {
            a.c = null;
        }
        if (this.f()) {
            a.c = new float[n * 2];
        }
        else {
            a.c = null;
        }
        return a;
    }
    
    @Override
    public void a(final ModelContext modelContext, final IBaseContext baseContext) {
        final BaseContextImpl a = (BaseContextImpl)baseContext;
        if (!this.c.a(modelContext)) {
            return;
        }
        final int c = this.c();
        final boolean[] e = loon.live2d.base.BaseDataImpl.e;
        e[0] = false;
        loon.live2d.util.ModelContextUtil.loadModel(modelContext, this.c, e, c, this.list, a.b, 0, 2);
        baseContext.b(e[0]);
        this.a(modelContext, this.c, baseContext, e);
    }
    
    @Override
    public void b(final ModelContext modelContext, final IBaseContext baseContext) {
        final BaseContextImpl a = (BaseContextImpl)baseContext;
        a.a(true);
        if (!this.f()) {
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
                final IBaseContext baseContext2 = modelContext.getBaseContext(a.a);
                if (baseData != null && baseContext2.a()) {
                    a.a(baseContext2.e());
                    a.c(baseContext2.g() * a.f());
                    baseData.a(modelContext, baseContext2, a.b, a.c, this.c(), 0, 2);
                    a.a(true);
                }
                else {
                    a.a(false);
                }
            }
        }
    }
    
    @Override
    public void a(final ModelContext modelContext, final IBaseContext baseContext, final float[] array, final float[] array2, final int n, final int n2, final int n3) {
        if (Live2D.L2D_DEFORMER_EXTEND) {
            final BaseContextImpl a = (BaseContextImpl)baseContext;
            a(array, array2, n, n2, n3, (a.c != null) ? a.c : a.b, this.a, this.b);
        }
        else {
            this.b(modelContext, baseContext, array, array2, n, n2, n3);
        }
    }
    
    public static void a(final float[] array, final float[] array2, final int n, final int n2, final int n3, final float[] array3, final int n4, final int n5) {
        final int n6 = n * n3;
        float n7 = 0.0f;
        float n8 = 0.0f;
        float n9 = 0.0f;
        float n10 = 0.0f;
        float n11 = 0.0f;
        float n12 = 0.0f;
        int n13 = 0;
        for (int i = n2; i < n6; i += n3) {
            final float n14 = array[i];
            final float n15 = array[i + 1];
            final float n16 = n14 * n4;
            final float n17 = n15 * n5;
            if (n16 < 0.0f || n17 < 0.0f || n4 <= n16 || n5 <= n17) {
                final int n18 = n4 + 1;
                if (n13 == 0) {
                    n13 = 1;
                    final float n19 = 0.25f * (array3[(0 + 0 * n18) * 2] + array3[(n4 + 0 * n18) * 2] + array3[(0 + n5 * n18) * 2] + array3[(n4 + n5 * n18) * 2]);
                    final float n20 = 0.25f * (array3[(0 + 0 * n18) * 2 + 1] + array3[(n4 + 0 * n18) * 2 + 1] + array3[(0 + n5 * n18) * 2 + 1] + array3[(n4 + n5 * n18) * 2 + 1]);
                    final float n21 = array3[(n4 + n5 * n18) * 2] - array3[(0 + 0 * n18) * 2];
                    final float n22 = array3[(n4 + n5 * n18) * 2 + 1] - array3[(0 + 0 * n18) * 2 + 1];
                    final float n23 = array3[(n4 + 0 * n18) * 2] - array3[(0 + n5 * n18) * 2];
                    final float n24 = array3[(n4 + 0 * n18) * 2 + 1] - array3[(0 + n5 * n18) * 2 + 1];
                    n9 = (n21 + n23) * 0.5f;
                    n10 = (n22 + n24) * 0.5f;
                    n11 = (n21 - n23) * 0.5f;
                    n12 = (n22 - n24) * 0.5f;
                    if (n9 == 0.0f) {}
                    if (n11 == 0.0f) {}
                    n7 = n19 - 0.5f * (n9 + n11);
                    n8 = n20 - 0.5f * (n10 + n12);
                }
                if (-2.0f < n14 && n14 < 3.0f && -2.0f < n15 && n15 < 3.0f) {
                    if (n14 <= 0.0f) {
                        if (n15 <= 0.0f) {
                            final float n25 = array3[(0 + 0 * n18) * 2];
                            final float n26 = array3[(0 + 0 * n18) * 2 + 1];
                            final float n27 = n7 - 2.0f * n9;
                            final float n28 = n8 - 2.0f * n10;
                            final float n29 = n7 - 2.0f * n11;
                            final float n30 = n8 - 2.0f * n12;
                            final float n31 = n7 - 2.0f * n9 - 2.0f * n11;
                            final float n32 = n8 - 2.0f * n10 - 2.0f * n12;
                            final float n33 = 0.5f * (n14 + 2.0f);
                            final float n34 = 0.5f * (n15 + 2.0f);
                            if (n33 + n34 <= 1.0f) {
                                array2[i] = n31 + (n29 - n31) * n33 + (n27 - n31) * n34;
                                array2[i + 1] = n32 + (n30 - n32) * n33 + (n28 - n32) * n34;
                            }
                            else {
                                array2[i] = n25 + (n27 - n25) * (1.0f - n33) + (n29 - n25) * (1.0f - n34);
                                array2[i + 1] = n26 + (n28 - n26) * (1.0f - n33) + (n30 - n26) * (1.0f - n34);
                            }
                        }
                        else if (n15 >= 1.0f) {
                            final float n35 = array3[(0 + n5 * n18) * 2];
                            final float n36 = array3[(0 + n5 * n18) * 2 + 1];
                            final float n37 = n7 - 2.0f * n9 + 1.0f * n11;
                            final float n38 = n8 - 2.0f * n10 + 1.0f * n12;
                            final float n39 = n7 + 3.0f * n11;
                            final float n40 = n8 + 3.0f * n12;
                            final float n41 = n7 - 2.0f * n9 + 3.0f * n11;
                            final float n42 = n8 - 2.0f * n10 + 3.0f * n12;
                            final float n43 = 0.5f * (n14 + 2.0f);
                            final float n44 = 0.5f * (n15 - 1.0f);
                            if (n43 + n44 <= 1.0f) {
                                array2[i] = n37 + (n35 - n37) * n43 + (n41 - n37) * n44;
                                array2[i + 1] = n38 + (n36 - n38) * n43 + (n42 - n38) * n44;
                            }
                            else {
                                array2[i] = n39 + (n41 - n39) * (1.0f - n43) + (n35 - n39) * (1.0f - n44);
                                array2[i + 1] = n40 + (n42 - n40) * (1.0f - n43) + (n36 - n40) * (1.0f - n44);
                            }
                        }
                        else {
                            int n45 = (int)n17;
                            if (n45 == n5) {
                                n45 = n5 - 1;
                            }
                            final float n46 = 0.5f * (n14 + 2.0f);
                            final float n47 = n17 - n45;
                            final float n48 = n45 / n5;
                            final float n49 = (n45 + 1) / n5;
                            final float n50 = array3[(0 + n45 * n18) * 2];
                            final float n51 = array3[(0 + n45 * n18) * 2 + 1];
                            final float n52 = array3[(0 + (n45 + 1) * n18) * 2];
                            final float n53 = array3[(0 + (n45 + 1) * n18) * 2 + 1];
                            final float n54 = n7 - 2.0f * n9 + n48 * n11;
                            final float n55 = n8 - 2.0f * n10 + n48 * n12;
                            final float n56 = n7 - 2.0f * n9 + n49 * n11;
                            final float n57 = n8 - 2.0f * n10 + n49 * n12;
                            if (n46 + n47 <= 1.0f) {
                                array2[i] = n54 + (n50 - n54) * n46 + (n56 - n54) * n47;
                                array2[i + 1] = n55 + (n51 - n55) * n46 + (n57 - n55) * n47;
                            }
                            else {
                                array2[i] = n52 + (n56 - n52) * (1.0f - n46) + (n50 - n52) * (1.0f - n47);
                                array2[i + 1] = n53 + (n57 - n53) * (1.0f - n46) + (n51 - n53) * (1.0f - n47);
                            }
                        }
                    }
                    else if (1.0f <= n14) {
                        if (n15 <= 0.0f) {
                            final float n58 = array3[(n4 + 0 * n18) * 2];
                            final float n59 = array3[(n4 + 0 * n18) * 2 + 1];
                            final float n60 = n7 + 3.0f * n9;
                            final float n61 = n8 + 3.0f * n10;
                            final float n62 = n7 + 1.0f * n9 - 2.0f * n11;
                            final float n63 = n8 + 1.0f * n10 - 2.0f * n12;
                            final float n64 = n7 + 3.0f * n9 - 2.0f * n11;
                            final float n65 = n8 + 3.0f * n10 - 2.0f * n12;
                            final float n66 = 0.5f * (n14 - 1.0f);
                            final float n67 = 0.5f * (n15 + 2.0f);
                            if (n66 + n67 <= 1.0f) {
                                array2[i] = n62 + (n64 - n62) * n66 + (n58 - n62) * n67;
                                array2[i + 1] = n63 + (n65 - n63) * n66 + (n59 - n63) * n67;
                            }
                            else {
                                array2[i] = n60 + (n58 - n60) * (1.0f - n66) + (n64 - n60) * (1.0f - n67);
                                array2[i + 1] = n61 + (n59 - n61) * (1.0f - n66) + (n65 - n61) * (1.0f - n67);
                            }
                        }
                        else if (n15 >= 1.0f) {
                            final float n68 = array3[(n4 + n5 * n18) * 2];
                            final float n69 = array3[(n4 + n5 * n18) * 2 + 1];
                            final float n70 = n7 + 3.0f * n9 + 1.0f * n11;
                            final float n71 = n8 + 3.0f * n10 + 1.0f * n12;
                            final float n72 = n7 + 1.0f * n9 + 3.0f * n11;
                            final float n73 = n8 + 1.0f * n10 + 3.0f * n12;
                            final float n74 = n7 + 3.0f * n9 + 3.0f * n11;
                            final float n75 = n8 + 3.0f * n10 + 3.0f * n12;
                            final float n76 = 0.5f * (n14 - 1.0f);
                            final float n77 = 0.5f * (n15 - 1.0f);
                            if (n76 + n77 <= 1.0f) {
                                array2[i] = n68 + (n70 - n68) * n76 + (n72 - n68) * n77;
                                array2[i + 1] = n69 + (n71 - n69) * n76 + (n73 - n69) * n77;
                            }
                            else {
                                array2[i] = n74 + (n72 - n74) * (1.0f - n76) + (n70 - n74) * (1.0f - n77);
                                array2[i + 1] = n75 + (n73 - n75) * (1.0f - n76) + (n71 - n75) * (1.0f - n77);
                            }
                        }
                        else {
                            int n78 = (int)n17;
                            if (n78 == n5) {
                                n78 = n5 - 1;
                            }
                            final float n79 = 0.5f * (n14 - 1.0f);
                            final float n80 = n17 - n78;
                            final float n81 = n78 / n5;
                            final float n82 = (n78 + 1) / n5;
                            final float n83 = array3[(n4 + n78 * n18) * 2];
                            final float n84 = array3[(n4 + n78 * n18) * 2 + 1];
                            final float n85 = array3[(n4 + (n78 + 1) * n18) * 2];
                            final float n86 = array3[(n4 + (n78 + 1) * n18) * 2 + 1];
                            final float n87 = n7 + 3.0f * n9 + n81 * n11;
                            final float n88 = n8 + 3.0f * n10 + n81 * n12;
                            final float n89 = n7 + 3.0f * n9 + n82 * n11;
                            final float n90 = n8 + 3.0f * n10 + n82 * n12;
                            if (n79 + n80 <= 1.0f) {
                                array2[i] = n83 + (n87 - n83) * n79 + (n85 - n83) * n80;
                                array2[i + 1] = n84 + (n88 - n84) * n79 + (n86 - n84) * n80;
                            }
                            else {
                                array2[i] = n89 + (n85 - n89) * (1.0f - n79) + (n87 - n89) * (1.0f - n80);
                                array2[i + 1] = n90 + (n86 - n90) * (1.0f - n79) + (n88 - n90) * (1.0f - n80);
                            }
                        }
                    }
                    else if (n15 <= 0.0f) {
                        int n91 = (int)n16;
                        if (n91 == n4) {
                            n91 = n4 - 1;
                        }
                        final float n92 = n16 - n91;
                        final float n93 = 0.5f * (n15 + 2.0f);
                        final float n94 = n91 / n4;
                        final float n95 = (n91 + 1) / n4;
                        final float n96 = array3[(n91 + 0 * n18) * 2];
                        final float n97 = array3[(n91 + 0 * n18) * 2 + 1];
                        final float n98 = array3[(n91 + 1 + 0 * n18) * 2];
                        final float n99 = array3[(n91 + 1 + 0 * n18) * 2 + 1];
                        final float n100 = n7 + n94 * n9 - 2.0f * n11;
                        final float n101 = n8 + n94 * n10 - 2.0f * n12;
                        final float n102 = n7 + n95 * n9 - 2.0f * n11;
                        final float n103 = n8 + n95 * n10 - 2.0f * n12;
                        if (n92 + n93 <= 1.0f) {
                            array2[i] = n100 + (n102 - n100) * n92 + (n96 - n100) * n93;
                            array2[i + 1] = n101 + (n103 - n101) * n92 + (n97 - n101) * n93;
                        }
                        else {
                            array2[i] = n98 + (n96 - n98) * (1.0f - n92) + (n102 - n98) * (1.0f - n93);
                            array2[i + 1] = n99 + (n97 - n99) * (1.0f - n92) + (n103 - n99) * (1.0f - n93);
                        }
                    }
                    else if (n15 >= 1.0f) {
                        int n104 = (int)n16;
                        if (n104 == n4) {
                            n104 = n4 - 1;
                        }
                        final float n105 = n16 - n104;
                        final float n106 = 0.5f * (n15 - 1.0f);
                        final float n107 = n104 / n4;
                        final float n108 = (n104 + 1) / n4;
                        final float n109 = array3[(n104 + n5 * n18) * 2];
                        final float n110 = array3[(n104 + n5 * n18) * 2 + 1];
                        final float n111 = array3[(n104 + 1 + n5 * n18) * 2];
                        final float n112 = array3[(n104 + 1 + n5 * n18) * 2 + 1];
                        final float n113 = n7 + n107 * n9 + 3.0f * n11;
                        final float n114 = n8 + n107 * n10 + 3.0f * n12;
                        final float n115 = n7 + n108 * n9 + 3.0f * n11;
                        final float n116 = n8 + n108 * n10 + 3.0f * n12;
                        if (n105 + n106 <= 1.0f) {
                            array2[i] = n109 + (n111 - n109) * n105 + (n113 - n109) * n106;
                            array2[i + 1] = n110 + (n112 - n110) * n105 + (n114 - n110) * n106;
                        }
                        else {
                            array2[i] = n115 + (n113 - n115) * (1.0f - n105) + (n111 - n115) * (1.0f - n106);
                            array2[i + 1] = n116 + (n114 - n116) * (1.0f - n105) + (n112 - n116) * (1.0f - n106);
                        }
                    }
                }
                else {
                    array2[i] = n7 + n14 * n9 + n15 * n11;
                    array2[i + 1] = n8 + n14 * n10 + n15 * n12;
                }
            }
            else {
                final float n117 = n16 - (int)n16;
                final float n118 = n17 - (int)n17;
                final int n119 = 2 * ((int)n16 + (int)n17 * (n4 + 1));
                if (n117 + n118 < 1.0f) {
                    array2[i] = array3[n119] * (1.0f - n117 - n118) + array3[n119 + 2] * n117 + array3[n119 + 2 * (n4 + 1)] * n118;
                    array2[i + 1] = array3[n119 + 1] * (1.0f - n117 - n118) + array3[n119 + 3] * n117 + array3[n119 + 2 * (n4 + 1) + 1] * n118;
                }
                else {
                    array2[i] = array3[n119 + 2 * (n4 + 1) + 2] * (n117 - 1.0f + n118) + array3[n119 + 2 * (n4 + 1)] * (1.0f - n117) + array3[n119 + 2] * (1.0f - n118);
                    array2[i + 1] = array3[n119 + 2 * (n4 + 1) + 3] * (n117 - 1.0f + n118) + array3[n119 + 2 * (n4 + 1) + 1] * (1.0f - n117) + array3[n119 + 3] * (1.0f - n118);
                }
            }
        }
    }
    
    public void b(final ModelContext modelContext, final IBaseContext baseContext, final float[] array, final float[] array2, final int n, final int n2, final int n3) {
        final BaseContextImpl a = (BaseContextImpl)baseContext;
        final int a2 = this.a;
        final int b = this.b;
        final int n4 = n * n3;
        final float[] array3 = (a.c != null) ? a.c : a.b;
        for (int i = n2; i < n4; i += n3) {
            float n11;
            float n12;
            int n13;
            if (Live2D.L2D_RANGE_CHECK_POINT) {
                float n5 = array[i];
                float n6 = array[i + 1];
                if (n5 < 0.0f) {
                    n5 = 0.0f;
                }
                else if (n5 > 1.0f) {
                    n5 = 1.0f;
                }
                if (n6 < 0.0f) {
                    n6 = 0.0f;
                }
                else if (n6 > 1.0f) {
                    n6 = 1.0f;
                }
                final float n7 = n5 * a2;
                final float n8 = n6 * b;
                int n9 = (int)n7;
                int n10 = (int)n8;
                if (n9 > a2 - 1) {
                    n9 = a2 - 1;
                }
                if (n10 > b - 1) {
                    n10 = b - 1;
                }
                n11 = n7 - n9;
                n12 = n8 - n10;
                n13 = 2 * (n9 + n10 * (a2 + 1));
            }
            else {
                final float n14 = array[i] * a2;
                final float n15 = array[i + 1] * b;
                n11 = n14 - (int)n14;
                n12 = n15 - (int)n15;
                n13 = 2 * ((int)n14 + (int)n15 * (a2 + 1));
            }
            if (n11 + n12 < 1.0f) {
                array2[i] = array3[n13] * (1.0f - n11 - n12) + array3[n13 + 2] * n11 + array3[n13 + 2 * (a2 + 1)] * n12;
                array2[i + 1] = array3[n13 + 1] * (1.0f - n11 - n12) + array3[n13 + 3] * n11 + array3[n13 + 2 * (a2 + 1) + 1] * n12;
            }
            else {
                array2[i] = array3[n13 + 2 * (a2 + 1) + 2] * (n11 - 1.0f + n12) + array3[n13 + 2 * (a2 + 1)] * (1.0f - n11) + array3[n13 + 2] * (1.0f - n12);
                array2[i + 1] = array3[n13 + 2 * (a2 + 1) + 3] * (n11 - 1.0f + n12) + array3[n13 + 2 * (a2 + 1) + 1] * (1.0f - n11) + array3[n13 + 3] * (1.0f - n12);
            }
        }
    }
    
    public int c() {
        return (this.a + 1) * (this.b + 1);
    }
    
    @Override
    public int b() {
        return 1;
    }
    
    class BaseContextImpl extends IBaseContext
    {
        int a;
        float[] b;
        float[] c;
        
        public BaseContextImpl(final BaseDataImpl src) {
            super(src);
            this.a = -2;
            this.b = null;
            this.c = null;
        }
    }
}
