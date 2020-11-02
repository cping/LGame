using java.lang;
using loon.canvas;

namespace loon.jni
{
    public class NativeSupport : Support
    {

        public override void FilterColor(int maxPixel, int pixelStart, int pixelEnd,
                int[] src, int[] dst, int[] colors, int c1, int c2)
        {

            int length = src.Length;
            if (pixelStart < pixelEnd)
            {
                int start = pixelStart + 1;
                int end = pixelEnd + 1;
                if (end > maxPixel)
                {
                    return;
                }
                for (int i = 0; i < length; i++)
                {
                    if (dst[i] != 0xffffff)
                    {
                        for (int pixIndex = start; pixIndex < end; pixIndex++)
                        {
                            if (colors[pixIndex] == src[i])
                            {
                                dst[i] = 0xffffff;
                            }
                            else if (src[i] == c1)
                            {
                                dst[i] = 0xffffff;
                            }
                        }
                    }
                }
            }
            else
            {
                int start = pixelEnd - 1;
                int end = pixelStart;
                if (start < 0)
                {
                    return;
                }
                for (int i = 0; i < length; i++)
                {
                    if (dst[i] != 0xffffff)
                    {
                        for (int pixIndex = start; pixIndex < end; pixIndex++)
                        {
                            if (colors[pixIndex] == src[i])
                            {
                                dst[i] = 0xffffff;
                            }
                            else if (src[i] == c2)
                            {
                                dst[i] = 0xffffff;
                            }
                        }
                    }
                }
            }

        }


        public override void FilterFractions(int size, float[] fractions, int width,
                int height, int[] pixels, int numElements)
        {

            int x, y;
            int idx = 0;
            for (int j = 0; j < size; j++)
            {
                idx = j * numElements;
                if (fractions[idx + 4] != 0xffffff)
                {
                    if (fractions[idx + 5] <= 0)
                    {
                        fractions[idx + 0] += fractions[idx + 2];
                        fractions[idx + 1] += fractions[idx + 3];
                        fractions[idx + 3] += 0.1f;
                    }
                    else
                    {
                        fractions[idx + 5]--;
                    }
                    x = (int)fractions[idx + 0];
                    y = (int)fractions[idx + 1];
                    if (x > -1 && y > -1 && x < width && y < height)
                    {
                        pixels[x + y * width] = (int)fractions[idx + 4];
                    }
                }
            }

        }

        public override void Mul(float[] mata, float[] matb)
        {

            float[] tmp = new float[16];
            tmp[M00] = mata[M00] * matb[M00] + mata[M01] * matb[M10] + mata[M02]
                    * matb[M20] + mata[M03] * matb[M30];
            tmp[M01] = mata[M00] * matb[M01] + mata[M01] * matb[M11] + mata[M02]
                    * matb[M21] + mata[M03] * matb[M31];
            tmp[M02] = mata[M00] * matb[M02] + mata[M01] * matb[M12] + mata[M02]
                    * matb[M22] + mata[M03] * matb[M32];
            tmp[M03] = mata[M00] * matb[M03] + mata[M01] * matb[M13] + mata[M02]
                    * matb[M23] + mata[M03] * matb[M33];
            tmp[M10] = mata[M10] * matb[M00] + mata[M11] * matb[M10] + mata[M12]
                    * matb[M20] + mata[M13] * matb[M30];
            tmp[M11] = mata[M10] * matb[M01] + mata[M11] * matb[M11] + mata[M12]
                    * matb[M21] + mata[M13] * matb[M31];
            tmp[M12] = mata[M10] * matb[M02] + mata[M11] * matb[M12] + mata[M12]
                    * matb[M22] + mata[M13] * matb[M32];
            tmp[M13] = mata[M10] * matb[M03] + mata[M11] * matb[M13] + mata[M12]
                    * matb[M23] + mata[M13] * matb[M33];
            tmp[M20] = mata[M20] * matb[M00] + mata[M21] * matb[M10] + mata[M22]
                    * matb[M20] + mata[M23] * matb[M30];
            tmp[M21] = mata[M20] * matb[M01] + mata[M21] * matb[M11] + mata[M22]
                    * matb[M21] + mata[M23] * matb[M31];
            tmp[M22] = mata[M20] * matb[M02] + mata[M21] * matb[M12] + mata[M22]
                    * matb[M22] + mata[M23] * matb[M32];
            tmp[M23] = mata[M20] * matb[M03] + mata[M21] * matb[M13] + mata[M22]
                    * matb[M23] + mata[M23] * matb[M33];
            tmp[M30] = mata[M30] * matb[M00] + mata[M31] * matb[M10] + mata[M32]
                    * matb[M20] + mata[M33] * matb[M30];
            tmp[M31] = mata[M30] * matb[M01] + mata[M31] * matb[M11] + mata[M32]
                    * matb[M21] + mata[M33] * matb[M31];
            tmp[M32] = mata[M30] * matb[M02] + mata[M31] * matb[M12] + mata[M32]
                    * matb[M22] + mata[M33] * matb[M32];
            tmp[M33] = mata[M30] * matb[M03] + mata[M31] * matb[M13] + mata[M32]
                    * matb[M23] + mata[M33] * matb[M33];
            JavaSystem.Arraycopy(tmp, 0, mata, 0, 16);

        }


        public override void MulVec(float[] mat, float[] vec)
        {

            float x = vec[0] * mat[M00] + vec[1] * mat[M01] + vec[2] * mat[M02]
                    + mat[M03];
            float y = vec[0] * mat[M10] + vec[1] * mat[M11] + vec[2] * mat[M12]
                    + mat[M13];
            float z = vec[0] * mat[M20] + vec[1] * mat[M21] + vec[2] * mat[M22]
                    + mat[M23];
            vec[0] = x;
            vec[1] = y;
            vec[2] = z;

        }


        public override void MulVec(float[] mat, float[] vecs, int offset, int numVecs,
                int stride)
        {

            for (int i = 0; i < numVecs; i++)
            {
                float[] vecPtr = new float[stride];
                JavaSystem.Arraycopy(vecs, offset, vecPtr, 0, stride);
                MulVec(mat, vecPtr);
            }

        }


        public override void Prj(float[] mat, float[] vec)
        {

            float inv_w = 1.0f / (vec[0] * mat[M30] + vec[1] * mat[M31] + vec[2]
                    * mat[M32] + mat[M33]);
            float x = (vec[0] * mat[M00] + vec[1] * mat[M01] + vec[2] * mat[M02] + mat[M03])
                    * inv_w;
            float y = (vec[0] * mat[M10] + vec[1] * mat[M11] + vec[2] * mat[M12] + mat[M13])
                    * inv_w;
            float z = (vec[0] * mat[M20] + vec[1] * mat[M21] + vec[2] * mat[M22] + mat[M23])
                    * inv_w;
            vec[0] = x;
            vec[1] = y;
            vec[2] = z;

        }


        public override void Prj(float[] mat, float[] vecs, int offset, int numVecs,
                int stride)
        {

            for (int i = 0; i < numVecs; i++)
            {
                float[] vecPtr = new float[stride];
                JavaSystem.Arraycopy(vecs, offset, vecPtr, 0, stride);
                Prj(mat, vecPtr);
            }

        }


        public override void Rot(float[] mat, float[] vec)
        {

            float x = vec[0] * mat[M00] + vec[1] * mat[M01] + vec[2] * mat[M02];
            float y = vec[0] * mat[M10] + vec[1] * mat[M11] + vec[2] * mat[M12];
            float z = vec[0] * mat[M20] + vec[1] * mat[M21] + vec[2] * mat[M22];
            vec[0] = x;
            vec[1] = y;
            vec[2] = z;

        }


        public override void Rot(float[] mat, float[] vecs, int offset, int numVecs,
                int stride)
        {

            for (int i = 0; i < numVecs; i++)
            {
                float[] vecPtr = new float[stride];
                JavaSystem.Arraycopy(vecs, offset, vecPtr, 0, stride);
                Rot(mat, vecPtr);
            }

        }


        public override bool Inv(float[] values)
        {

            float[] tmp = new float[16];
            float l_det = Det(values);
            if (l_det == 0)
                return false;
            tmp[M00] = values[M12] * values[M23] * values[M31] - values[M13]
                    * values[M22] * values[M31] + values[M13] * values[M21]
                    * values[M32] - values[M11] * values[M23] * values[M32]
                    - values[M12] * values[M21] * values[M33] + values[M11]
                    * values[M22] * values[M33];
            tmp[M01] = values[M03] * values[M22] * values[M31] - values[M02]
                    * values[M23] * values[M31] - values[M03] * values[M21]
                    * values[M32] + values[M01] * values[M23] * values[M32]
                    + values[M02] * values[M21] * values[M33] - values[M01]
                    * values[M22] * values[M33];
            tmp[M02] = values[M02] * values[M13] * values[M31] - values[M03]
                    * values[M12] * values[M31] + values[M03] * values[M11]
                    * values[M32] - values[M01] * values[M13] * values[M32]
                    - values[M02] * values[M11] * values[M33] + values[M01]
                    * values[M12] * values[M33];
            tmp[M03] = values[M03] * values[M12] * values[M21] - values[M02]
                    * values[M13] * values[M21] - values[M03] * values[M11]
                    * values[M22] + values[M01] * values[M13] * values[M22]
                    + values[M02] * values[M11] * values[M23] - values[M01]
                    * values[M12] * values[M23];
            tmp[M10] = values[M13] * values[M22] * values[M30] - values[M12]
                    * values[M23] * values[M30] - values[M13] * values[M20]
                    * values[M32] + values[M10] * values[M23] * values[M32]
                    + values[M12] * values[M20] * values[M33] - values[M10]
                    * values[M22] * values[M33];
            tmp[M11] = values[M02] * values[M23] * values[M30] - values[M03]
                    * values[M22] * values[M30] + values[M03] * values[M20]
                    * values[M32] - values[M00] * values[M23] * values[M32]
                    - values[M02] * values[M20] * values[M33] + values[M00]
                    * values[M22] * values[M33];
            tmp[M12] = values[M03] * values[M12] * values[M30] - values[M02]
                    * values[M13] * values[M30] - values[M03] * values[M10]
                    * values[M32] + values[M00] * values[M13] * values[M32]
                    + values[M02] * values[M10] * values[M33] - values[M00]
                    * values[M12] * values[M33];
            tmp[M13] = values[M02] * values[M13] * values[M20] - values[M03]
                    * values[M12] * values[M20] + values[M03] * values[M10]
                    * values[M22] - values[M00] * values[M13] * values[M22]
                    - values[M02] * values[M10] * values[M23] + values[M00]
                    * values[M12] * values[M23];
            tmp[M20] = values[M11] * values[M23] * values[M30] - values[M13]
                    * values[M21] * values[M30] + values[M13] * values[M20]
                    * values[M31] - values[M10] * values[M23] * values[M31]
                    - values[M11] * values[M20] * values[M33] + values[M10]
                    * values[M21] * values[M33];
            tmp[M21] = values[M03] * values[M21] * values[M30] - values[M01]
                    * values[M23] * values[M30] - values[M03] * values[M20]
                    * values[M31] + values[M00] * values[M23] * values[M31]
                    + values[M01] * values[M20] * values[M33] - values[M00]
                    * values[M21] * values[M33];
            tmp[M22] = values[M01] * values[M13] * values[M30] - values[M03]
                    * values[M11] * values[M30] + values[M03] * values[M10]
                    * values[M31] - values[M00] * values[M13] * values[M31]
                    - values[M01] * values[M10] * values[M33] + values[M00]
                    * values[M11] * values[M33];
            tmp[M23] = values[M03] * values[M11] * values[M20] - values[M01]
                    * values[M13] * values[M20] - values[M03] * values[M10]
                    * values[M21] + values[M00] * values[M13] * values[M21]
                    + values[M01] * values[M10] * values[M23] - values[M00]
                    * values[M11] * values[M23];
            tmp[M30] = values[M12] * values[M21] * values[M30] - values[M11]
                    * values[M22] * values[M30] - values[M12] * values[M20]
                    * values[M31] + values[M10] * values[M22] * values[M31]
                    + values[M11] * values[M20] * values[M32] - values[M10]
                    * values[M21] * values[M32];
            tmp[M31] = values[M01] * values[M22] * values[M30] - values[M02]
                    * values[M21] * values[M30] + values[M02] * values[M20]
                    * values[M31] - values[M00] * values[M22] * values[M31]
                    - values[M01] * values[M20] * values[M32] + values[M00]
                    * values[M21] * values[M32];
            tmp[M32] = values[M02] * values[M11] * values[M30] - values[M01]
                    * values[M12] * values[M30] - values[M02] * values[M10]
                    * values[M31] + values[M00] * values[M12] * values[M31]
                    + values[M01] * values[M10] * values[M32] - values[M00]
                    * values[M11] * values[M32];
            tmp[M33] = values[M01] * values[M12] * values[M20] - values[M02]
                    * values[M11] * values[M20] + values[M02] * values[M10]
                    * values[M21] - values[M00] * values[M12] * values[M21]
                    - values[M01] * values[M10] * values[M22] + values[M00]
                    * values[M11] * values[M22];

            float inv_det = 1.0f / l_det;
            values[M00] = tmp[M00] * inv_det;
            values[M01] = tmp[M01] * inv_det;
            values[M02] = tmp[M02] * inv_det;
            values[M03] = tmp[M03] * inv_det;
            values[M10] = tmp[M10] * inv_det;
            values[M11] = tmp[M11] * inv_det;
            values[M12] = tmp[M12] * inv_det;
            values[M13] = tmp[M13] * inv_det;
            values[M20] = tmp[M20] * inv_det;
            values[M21] = tmp[M21] * inv_det;
            values[M22] = tmp[M22] * inv_det;
            values[M23] = tmp[M23] * inv_det;
            values[M30] = tmp[M30] * inv_det;
            values[M31] = tmp[M31] * inv_det;
            values[M32] = tmp[M32] * inv_det;
            values[M33] = tmp[M33] * inv_det;
            return true;

        }


        public override float Det(float[] values)
        {

            return values[M30] * values[M21] * values[M12] * values[M03]
                    - values[M20] * values[M31] * values[M12] * values[M03]
                    - values[M30] * values[M11] * values[M22] * values[M03]
                    + values[M10] * values[M31] * values[M22] * values[M03]
                    + values[M20] * values[M11] * values[M32] * values[M03]
                    - values[M10] * values[M21] * values[M32] * values[M03]
                    - values[M30] * values[M21] * values[M02] * values[M13]
                    + values[M20] * values[M31] * values[M02] * values[M13]
                    + values[M30] * values[M01] * values[M22] * values[M13]
                    - values[M00] * values[M31] * values[M22] * values[M13]
                    - values[M20] * values[M01] * values[M32] * values[M13]
                    + values[M00] * values[M21] * values[M32] * values[M13]
                    + values[M30] * values[M11] * values[M02] * values[M23]
                    - values[M10] * values[M31] * values[M02] * values[M23]
                    - values[M30] * values[M01] * values[M12] * values[M23]
                    + values[M00] * values[M31] * values[M12] * values[M23]
                    + values[M10] * values[M01] * values[M32] * values[M23]
                    - values[M00] * values[M11] * values[M32] * values[M23]
                    - values[M20] * values[M11] * values[M02] * values[M33]
                    + values[M10] * values[M21] * values[M02] * values[M33]
                    + values[M20] * values[M01] * values[M12] * values[M33]
                    - values[M00] * values[M21] * values[M12] * values[M33]
                    - values[M10] * values[M01] * values[M22] * values[M33]
                    + values[M00] * values[M11] * values[M22] * values[M33];

        }


        public override int[] ToColorKey(int[] buffer, int colorKey)
        {

            int size = buffer.Length;
            for (int i = 0; i < size; i++)
            {
                int pixel = buffer[i];
                if (pixel == colorKey)
                {
                    buffer[i] = 0x00FFFFFF;
                }
            }

            return buffer;
        }


        public override int[] ToColorKeys(int[] buffer, int[] colors)
        {

            int length = colors.Length;
            int size = buffer.Length;
            for (int n = 0; n < length; n++)
            {
                for (int i = 0; i < size; i++)
                {
                    int pixel = buffer[i];
                    if (pixel == colors[n])
                    {
                        buffer[i] = 0x00FFFFFF;
                    }
                }
            }

            return buffer;
        }


        public override int[] ToColorKeyLimit(int[] buffer, int start, int end)
        {

            int sred = LColor.GetRed(start);
            int sgreen = LColor.GetGreen(start);
            int sblue = LColor.GetBlue(start);
            int ered = LColor.GetRed(end);
            int egreen = LColor.GetGreen(end);
            int eblue = LColor.GetBlue(end);
            int size = buffer.Length;
            for (int i = 0; i < size; i++)
            {
                int pixel = buffer[i];
                int r = LColor.GetRed(pixel);
                int g = LColor.GetGreen(pixel);
                int b = LColor.GetBlue(pixel);
                if ((r >= sred && g >= sgreen && b >= sblue)
                        && (r <= ered && g <= egreen && b <= eblue))
                {
                    buffer[i] = 0x00FFFFFF;
                }
            }

            return buffer;
        }


        public override int[] ToGray(int[] buffer, int w, int h)
        {

            int size = w * h;
            int[] newResult = new int[size];
            JavaSystem.Arraycopy(buffer, 0, newResult, 0, size);
            int alpha = 0xFF << 24;
            for (int i = 0; i < h; i++)
            {
                for (int j = 0; j < w; j++)
                {
                    int idx = w * i + j;
                    int color = newResult[idx];
                    if (color != 0x00FFFFFF)
                    {
                        int red = ((color & 0x00FF0000) >> 16);
                        int green = ((color & 0x0000FF00) >> 8);
                        int blue = color & 0x000000FF;
                        color = (red + green + blue) / 3;
                        color = alpha | (color << 16) | (color << 8) | color;
                        newResult[idx] = color;
                    }
                }
            }
            return newResult;

        }


    }
}
