package loon.live2d.util;

import loon.live2d.*;
import loon.utils.TArray;

public class ModelContextUtil
{
    public static int loadModel(final ModelContext modelContext, final loon.live2d.param.ParamIOList b, final boolean[] array, final int[] array2) {
        final int a = b.loadParam(modelContext, array);
        final short[] tmpPivotTableIndicesRef = modelContext.getTmpPivotTableIndicesRef();
        final float[] tmpT_ArrayRef = modelContext.getTmpT_ArrayRef();
        b.a(tmpPivotTableIndicesRef, tmpT_ArrayRef, a);
        if (a <= 0) {
            return array2[tmpPivotTableIndicesRef[0]];
        }
        if (a == 1) {
            final int n = array2[tmpPivotTableIndicesRef[0]];
            return (int)(n + (array2[tmpPivotTableIndicesRef[1]] - n) * tmpT_ArrayRef[0]);
        }
        if (a == 2) {
            final int n2 = array2[tmpPivotTableIndicesRef[0]];
            final int n3 = array2[tmpPivotTableIndicesRef[1]];
            final int n4 = array2[tmpPivotTableIndicesRef[2]];
            final int n5 = array2[tmpPivotTableIndicesRef[3]];
            final float n6 = tmpT_ArrayRef[0];
            final float n7 = tmpT_ArrayRef[1];
            final int n8 = (int)(n2 + (n3 - n2) * n6);
            return (int)(n8 + ((int)(n4 + (n5 - n4) * n6) - n8) * n7);
        }
        if (a == 3) {
            final int n9 = array2[tmpPivotTableIndicesRef[0]];
            final int n10 = array2[tmpPivotTableIndicesRef[1]];
            final int n11 = array2[tmpPivotTableIndicesRef[2]];
            final int n12 = array2[tmpPivotTableIndicesRef[3]];
            final int n13 = array2[tmpPivotTableIndicesRef[4]];
            final int n14 = array2[tmpPivotTableIndicesRef[5]];
            final int n15 = array2[tmpPivotTableIndicesRef[6]];
            final int n16 = array2[tmpPivotTableIndicesRef[7]];
            final float n17 = tmpT_ArrayRef[0];
            final float n18 = tmpT_ArrayRef[1];
            final float n19 = tmpT_ArrayRef[2];
            final int n20 = (int)(n9 + (n10 - n9) * n17);
            final int n21 = (int)(n11 + (n12 - n11) * n17);
            final int n22 = (int)(n13 + (n14 - n13) * n17);
            final int n23 = (int)(n15 + (n16 - n15) * n17);
            final int n24 = (int)(n20 + (n21 - n20) * n18);
            return (int)(n24 + ((int)(n22 + (n23 - n22) * n18) - n24) * n19);
        }
        if (a == 4) {
            final int n25 = array2[tmpPivotTableIndicesRef[0]];
            final int n26 = array2[tmpPivotTableIndicesRef[1]];
            final int n27 = array2[tmpPivotTableIndicesRef[2]];
            final int n28 = array2[tmpPivotTableIndicesRef[3]];
            final int n29 = array2[tmpPivotTableIndicesRef[4]];
            final int n30 = array2[tmpPivotTableIndicesRef[5]];
            final int n31 = array2[tmpPivotTableIndicesRef[6]];
            final int n32 = array2[tmpPivotTableIndicesRef[7]];
            final int n33 = array2[tmpPivotTableIndicesRef[8]];
            final int n34 = array2[tmpPivotTableIndicesRef[9]];
            final int n35 = array2[tmpPivotTableIndicesRef[10]];
            final int n36 = array2[tmpPivotTableIndicesRef[11]];
            final int n37 = array2[tmpPivotTableIndicesRef[12]];
            final int n38 = array2[tmpPivotTableIndicesRef[13]];
            final int n39 = array2[tmpPivotTableIndicesRef[14]];
            final int n40 = array2[tmpPivotTableIndicesRef[15]];
            final float n41 = tmpT_ArrayRef[0];
            final float n42 = tmpT_ArrayRef[1];
            final float n43 = tmpT_ArrayRef[2];
            final float n44 = tmpT_ArrayRef[3];
            final int n45 = (int)(n25 + (n26 - n25) * n41);
            final int n46 = (int)(n27 + (n28 - n27) * n41);
            final int n47 = (int)(n29 + (n30 - n29) * n41);
            final int n48 = (int)(n31 + (n32 - n31) * n41);
            final int n49 = (int)(n33 + (n34 - n33) * n41);
            final int n50 = (int)(n35 + (n36 - n35) * n41);
            final int n51 = (int)(n37 + (n38 - n37) * n41);
            final int n52 = (int)(n39 + (n40 - n39) * n41);
            final int n53 = (int)(n45 + (n46 - n45) * n42);
            final int n54 = (int)(n47 + (n48 - n47) * n42);
            final int n55 = (int)(n49 + (n50 - n49) * n42);
            final int n56 = (int)(n51 + (n52 - n51) * n42);
            final int n57 = (int)(n53 + (n54 - n53) * n43);
            return (int)(n57 + ((int)(n55 + (n56 - n55) * n43) - n57) * n44);
        }
        final int n58 = 1 << a;
        final float[] array3 = new float[n58];
        for (int i = 0; i < n58; ++i) {
            int n59 = i;
            float n60 = 1.0f;
            for (int j = 0; j < a; ++j) {
                n60 *= ((n59 % 2 == 0) ? (1.0f - tmpT_ArrayRef[j]) : tmpT_ArrayRef[j]);
                n59 /= 2;
            }
            array3[i] = n60;
        }
        final float[] array4 = new float[n58];
        for (int k = 0; k < n58; ++k) {
            array4[k] = array2[tmpPivotTableIndicesRef[k]];
        }
        float n61 = 0.0f;
        for (int l = 0; l < n58; ++l) {
            n61 += array3[l] * array4[l];
        }
        return (int)(n61 + 0.5f);
    }
    
    public static float a(final ModelContext modelContext, final loon.live2d.param.ParamIOList b, final boolean[] array, final float[] array2) {
        final int a = b.loadParam(modelContext, array);
        final short[] tmpPivotTableIndicesRef = modelContext.getTmpPivotTableIndicesRef();
        final float[] tmpT_ArrayRef = modelContext.getTmpT_ArrayRef();
        b.a(tmpPivotTableIndicesRef, tmpT_ArrayRef, a);
        if (a <= 0) {
            return array2[tmpPivotTableIndicesRef[0]];
        }
        if (a == 1) {
            final float n = array2[tmpPivotTableIndicesRef[0]];
            return n + (array2[tmpPivotTableIndicesRef[1]] - n) * tmpT_ArrayRef[0];
        }
        if (a == 2) {
            final float n2 = array2[tmpPivotTableIndicesRef[0]];
            final float n3 = array2[tmpPivotTableIndicesRef[1]];
            final float n4 = array2[tmpPivotTableIndicesRef[2]];
            final float n5 = array2[tmpPivotTableIndicesRef[3]];
            final float n6 = tmpT_ArrayRef[0];
            final float n7 = tmpT_ArrayRef[1];
            return (1.0f - n7) * (n2 + (n3 - n2) * n6) + n7 * (n4 + (n5 - n4) * n6);
        }
        if (a == 3) {
            final float n8 = array2[tmpPivotTableIndicesRef[0]];
            final float n9 = array2[tmpPivotTableIndicesRef[1]];
            final float n10 = array2[tmpPivotTableIndicesRef[2]];
            final float n11 = array2[tmpPivotTableIndicesRef[3]];
            final float n12 = array2[tmpPivotTableIndicesRef[4]];
            final float n13 = array2[tmpPivotTableIndicesRef[5]];
            final float n14 = array2[tmpPivotTableIndicesRef[6]];
            final float n15 = array2[tmpPivotTableIndicesRef[7]];
            final float n16 = tmpT_ArrayRef[0];
            final float n17 = tmpT_ArrayRef[1];
            final float n18 = tmpT_ArrayRef[2];
            return (1.0f - n18) * ((1.0f - n17) * (n8 + (n9 - n8) * n16) + n17 * (n10 + (n11 - n10) * n16)) + n18 * ((1.0f - n17) * (n12 + (n13 - n12) * n16) + n17 * (n14 + (n15 - n14) * n16));
        }
        if (a == 4) {
            final float n19 = array2[tmpPivotTableIndicesRef[0]];
            final float n20 = array2[tmpPivotTableIndicesRef[1]];
            final float n21 = array2[tmpPivotTableIndicesRef[2]];
            final float n22 = array2[tmpPivotTableIndicesRef[3]];
            final float n23 = array2[tmpPivotTableIndicesRef[4]];
            final float n24 = array2[tmpPivotTableIndicesRef[5]];
            final float n25 = array2[tmpPivotTableIndicesRef[6]];
            final float n26 = array2[tmpPivotTableIndicesRef[7]];
            final float n27 = array2[tmpPivotTableIndicesRef[8]];
            final float n28 = array2[tmpPivotTableIndicesRef[9]];
            final float n29 = array2[tmpPivotTableIndicesRef[10]];
            final float n30 = array2[tmpPivotTableIndicesRef[11]];
            final float n31 = array2[tmpPivotTableIndicesRef[12]];
            final float n32 = array2[tmpPivotTableIndicesRef[13]];
            final float n33 = array2[tmpPivotTableIndicesRef[14]];
            final float n34 = array2[tmpPivotTableIndicesRef[15]];
            final float n35 = tmpT_ArrayRef[0];
            final float n36 = tmpT_ArrayRef[1];
            final float n37 = tmpT_ArrayRef[2];
            final float n38 = tmpT_ArrayRef[3];
            return (1.0f - n38) * ((1.0f - n37) * ((1.0f - n36) * (n19 + (n20 - n19) * n35) + n36 * (n21 + (n22 - n21) * n35)) + n37 * ((1.0f - n36) * (n23 + (n24 - n23) * n35) + n36 * (n25 + (n26 - n25) * n35))) + n38 * ((1.0f - n37) * ((1.0f - n36) * (n27 + (n28 - n27) * n35) + n36 * (n29 + (n30 - n29) * n35)) + n37 * ((1.0f - n36) * (n31 + (n32 - n31) * n35) + n36 * (n33 + (n34 - n33) * n35)));
        }
        final int n39 = 1 << a;
        final float[] array3 = new float[n39];
        for (int i = 0; i < n39; ++i) {
            int n40 = i;
            float n41 = 1.0f;
            for (int j = 0; j < a; ++j) {
                n41 *= ((n40 % 2 == 0) ? (1.0f - tmpT_ArrayRef[j]) : tmpT_ArrayRef[j]);
                n40 /= 2;
            }
            array3[i] = n41;
        }
        final float[] array4 = new float[n39];
        for (int k = 0; k < n39; ++k) {
            array4[k] = array2[tmpPivotTableIndicesRef[k]];
        }
        float n42 = 0.0f;
        for (int l = 0; l < n39; ++l) {
            n42 += array3[l] * array4[l];
        }
        return n42;
    }
    
    public static void loadModel(final ModelContext modelContext, final loon.live2d.param.ParamIOList b, final boolean[] array, final int n, final TArray list, final float[] array2, final int n2, final int n3) {
        final int a = b.loadParam(modelContext, array);
        final short[] tmpPivotTableIndicesRef = modelContext.getTmpPivotTableIndicesRef();
        final float[] tmpT_ArrayRef = modelContext.getTmpT_ArrayRef();
        b.a(tmpPivotTableIndicesRef, tmpT_ArrayRef, a);
        final int n4 = n * 2;
        int n5 = n2;
        if (a <= 0) {
            final float[] array3 = (float[]) list.get(tmpPivotTableIndicesRef[0]);
            if (n3 == 2 && n2 == 0) {
                System.arraycopy(array3, 0, array2, 0, n4);
            }
            else {
                for (int i = 0; i < n4; array2[n5] = array3[i++], array2[n5 + 1] = array3[i++], n5 += n3) {}
            }
        }
        else if (a == 1) {
            final float[] array4 = (float[]) list.get(tmpPivotTableIndicesRef[0]);
            final float[] array5 = (float[]) list.get(tmpPivotTableIndicesRef[1]);
            final float n6 = tmpT_ArrayRef[0];
            final float n7 = 1.0f - n6;
            for (int j = 0; j < n4; ++j, array2[n5 + 1] = array4[j] * n7 + array5[j] * n6, ++j, n5 += n3) {
                array2[n5] = array4[j] * n7 + array5[j] * n6;
            }
        }
        else if (a == 2) {
            final float[] array6 = (float[]) list.get(tmpPivotTableIndicesRef[0]);
            final float[] array7 = (float[]) list.get(tmpPivotTableIndicesRef[1]);
            final float[] array8 = (float[]) list.get(tmpPivotTableIndicesRef[2]);
            final float[] array9 = (float[]) list.get(tmpPivotTableIndicesRef[3]);
            final float n8 = tmpT_ArrayRef[0];
            final float n9 = tmpT_ArrayRef[1];
            final float n10 = 1.0f - n8;
            final float n11 = 1.0f - n9;
            final float n12 = n11 * n10;
            final float n13 = n11 * n8;
            final float n14 = n9 * n10;
            final float n15 = n9 * n8;
            for (int k = 0; k < n4; ++k, array2[n5 + 1] = n12 * array6[k] + n13 * array7[k] + n14 * array8[k] + n15 * array9[k], ++k, n5 += n3) {
                array2[n5] = n12 * array6[k] + n13 * array7[k] + n14 * array8[k] + n15 * array9[k];
            }
        }
        else if (a == 3) {
            final float[] array10 = (float[]) list.get(tmpPivotTableIndicesRef[0]);
            final float[] array11 =  (float[])list.get(tmpPivotTableIndicesRef[1]);
            final float[] array12 =  (float[])list.get(tmpPivotTableIndicesRef[2]);
            final float[] array13 =  (float[])list.get(tmpPivotTableIndicesRef[3]);
            final float[] array14 = (float[]) list.get(tmpPivotTableIndicesRef[4]);
            final float[] array15 = (float[]) list.get(tmpPivotTableIndicesRef[5]);
            final float[] array16 = (float[]) list.get(tmpPivotTableIndicesRef[6]);
            final float[] array17 = (float[]) list.get(tmpPivotTableIndicesRef[7]);
            final float n16 = tmpT_ArrayRef[0];
            final float n17 = tmpT_ArrayRef[1];
            final float n18 = tmpT_ArrayRef[2];
            final float n19 = 1.0f - n16;
            final float n20 = 1.0f - n17;
            final float n21 = 1.0f - n18;
            final float n22 = n21 * n20 * n19;
            final float n23 = n21 * n20 * n16;
            final float n24 = n21 * n17 * n19;
            final float n25 = n21 * n17 * n16;
            final float n26 = n18 * n20 * n19;
            final float n27 = n18 * n20 * n16;
            final float n28 = n18 * n17 * n19;
            final float n29 = n18 * n17 * n16;
            for (int l = 0; l < n4; ++l, array2[n5 + 1] = n22 * array10[l] + n23 * array11[l] + n24 * array12[l] + n25 * array13[l] + n26 * array14[l] + n27 * array15[l] + n28 * array16[l] + n29 * array17[l], ++l, n5 += n3) {
                array2[n5] = n22 * array10[l] + n23 * array11[l] + n24 * array12[l] + n25 * array13[l] + n26 * array14[l] + n27 * array15[l] + n28 * array16[l] + n29 * array17[l];
            }
        }
        else if (a == 4) {
            final float[] array18 = (float[]) list.get(tmpPivotTableIndicesRef[0]);
            final float[] array19 = (float[]) list.get(tmpPivotTableIndicesRef[1]);
            final float[] array20 = (float[]) list.get(tmpPivotTableIndicesRef[2]);
            final float[] array21 = (float[]) list.get(tmpPivotTableIndicesRef[3]);
            final float[] array22 = (float[]) list.get(tmpPivotTableIndicesRef[4]);
            final float[] array23 = (float[]) list.get(tmpPivotTableIndicesRef[5]);
            final float[] array24 = (float[]) list.get(tmpPivotTableIndicesRef[6]);
            final float[] array25 = (float[]) list.get(tmpPivotTableIndicesRef[7]);
            final float[] array26 = (float[]) list.get(tmpPivotTableIndicesRef[8]);
            final float[] array27 =  (float[])list.get(tmpPivotTableIndicesRef[9]);
            final float[] array28 = (float[]) list.get(tmpPivotTableIndicesRef[10]);
            final float[] array29 = (float[]) list.get(tmpPivotTableIndicesRef[11]);
            final float[] array30 = (float[]) list.get(tmpPivotTableIndicesRef[12]);
            final float[] array31 = (float[]) list.get(tmpPivotTableIndicesRef[13]);
            final float[] array32 = (float[]) list.get(tmpPivotTableIndicesRef[14]);
            final float[] array33 = (float[]) list.get(tmpPivotTableIndicesRef[15]);
            final float n30 = tmpT_ArrayRef[0];
            final float n31 = tmpT_ArrayRef[1];
            final float n32 = tmpT_ArrayRef[2];
            final float n33 = tmpT_ArrayRef[3];
            final float n34 = 1.0f - n30;
            final float n35 = 1.0f - n31;
            final float n36 = 1.0f - n32;
            final float n37 = 1.0f - n33;
            final float n38 = n37 * n36 * n35 * n34;
            final float n39 = n37 * n36 * n35 * n30;
            final float n40 = n37 * n36 * n31 * n34;
            final float n41 = n37 * n36 * n31 * n30;
            final float n42 = n37 * n32 * n35 * n34;
            final float n43 = n37 * n32 * n35 * n30;
            final float n44 = n37 * n32 * n31 * n34;
            final float n45 = n37 * n32 * n31 * n30;
            final float n46 = n33 * n36 * n35 * n34;
            final float n47 = n33 * n36 * n35 * n30;
            final float n48 = n33 * n36 * n31 * n34;
            final float n49 = n33 * n36 * n31 * n30;
            final float n50 = n33 * n32 * n35 * n34;
            final float n51 = n33 * n32 * n35 * n30;
            final float n52 = n33 * n32 * n31 * n34;
            final float n53 = n33 * n32 * n31 * n30;
            for (int n54 = 0; n54 < n4; ++n54, array2[n5 + 1] = n38 * array18[n54] + n39 * array19[n54] + n40 * array20[n54] + n41 * array21[n54] + n42 * array22[n54] + n43 * array23[n54] + n44 * array24[n54] + n45 * array25[n54] + n46 * array26[n54] + n47 * array27[n54] + n48 * array28[n54] + n49 * array29[n54] + n50 * array30[n54] + n51 * array31[n54] + n52 * array32[n54] + n53 * array33[n54], ++n54, n5 += n3) {
                array2[n5] = n38 * array18[n54] + n39 * array19[n54] + n40 * array20[n54] + n41 * array21[n54] + n42 * array22[n54] + n43 * array23[n54] + n44 * array24[n54] + n45 * array25[n54] + n46 * array26[n54] + n47 * array27[n54] + n48 * array28[n54] + n49 * array29[n54] + n50 * array30[n54] + n51 * array31[n54] + n52 * array32[n54] + n53 * array33[n54];
            }
        }
        else {
            final int n55 = 1 << a;
            final float[] array34 = new float[n55];
            for (int n56 = 0; n56 < n55; ++n56) {
                int n57 = n56;
                float n58 = 1.0f;
                for (int n59 = 0; n59 < a; ++n59) {
                    n58 *= ((n57 % 2 == 0) ? (1.0f - tmpT_ArrayRef[n59]) : tmpT_ArrayRef[n59]);
                    n57 /= 2;
                }
                array34[n56] = n58;
            }
            final float[][] array35 = new float[n55][];
            for (int n60 = 0; n60 < n55; ++n60) {
                array35[n60] = (float[]) list.get(tmpPivotTableIndicesRef[n60]);
            }
            float n62;
            float n63;
            for (int n61 = 0; n61 < n4; n61 += 2, array2[n5] = n62, array2[n5 + 1] = n63, n5 += n3) {
                n62 = 0.0f;
                n63 = 0.0f;
                final int n64 = n61 + 1;
                for (int n65 = 0; n65 < n55; ++n65) {
                    n62 += array34[n65] * array35[n65][n61];
                    n63 += array34[n65] * array35[n65][n64];
                }
            }
        }
    }
}
