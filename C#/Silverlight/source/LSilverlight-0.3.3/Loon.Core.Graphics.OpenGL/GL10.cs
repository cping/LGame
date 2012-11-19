using Microsoft.Xna.Framework.Graphics;
using Microsoft.Xna.Framework;
using System.Collections.Generic;
using Loon.Java;
using System;
namespace Loon.Core.Graphics.OpenGL
{
   public class GL10 
    {
        private static BlendFunction AlphaBlendFunction = BlendFunction.Add;
        private Blend AlphaDestinationBlend;
        private bool alphaTest;
        private AlphaTestEffect alphaTestEffect;
        private int bindTexture;
        private Color clearColor;
        private float clearDepth;
        private float[] color;
        private ColorWriteChannels ColorWriteChannels;
        private bool depthBuffer;
        private CompareFunction depthFunc;
        private bool depthMask;
        private BasicEffect effect;
        public const int GL_ALPHA_TEST = 0xbc0;
        public const int GL_ALWAYS = 0x207;
        public const int GL_BLEND = 0xbe2;
        public const int GL_BYTE = 0x1400;
        public const int GL_CCW = 0x901;
        public const int GL_CLAMP_TO_EDGE = 0x812f;
        public const int GL_COLOR_ARRAY = 0x8076;
        public const int GL_COLOR_BUFFER_BIT = 0x4000;
        public const int GL_COMPRESSED_TEXTURE_FORMATS = 0x86a3;
        public const int GL_CULL_FACE = 0xb44;
        public const int GL_CW = 0x900;
        public const int GL_DEPTH_BUFFER_BIT = 0x100;
        public const int GL_DEPTH_TEST = 0xb71;
        public const int GL_DITHER = 0xbd0;
        public const int GL_EQUAL = 0x202;
        public const int GL_FASTEST = 0x1101;
        public const int GL_FLOAT = 0x1406;
        public const int GL_FOG = 0xb60;
        public const int GL_FOG_COLOR = 0xb66;
        public const int GL_FOG_END = 0xb64;
        public const int GL_FOG_MODE = 0xb65;
        public const int GL_FOG_START = 0xb63;
        public const int GL_GEQUAL = 0x206;
        public const int GL_GREATER = 0x204;
        public const int GL_INCR = 0x1e02;
        public const int GL_KEEP = 0x1e00;
        public const int GL_LEQUAL = 0x203;
        public const int GL_LIGHTING = 0xb50;
        public const int GL_LINE_LOOP = 2;
        public const int GL_LINEAR = 0x2601;
        public const int GL_LUMINANCE = 0x1909;
        public const int GL_LUMINANCE_ALPHA = 0x190a;
        public const int GL_MAX_TEXTURE_SIZE = 0xd33;
        public const int GL_MODELVIEW = 0x1700;
        public const int GL_MODULATE = 0x2100;
        public const int GL_NEAREST = 0x2600;
        public const int GL_NO_ERROR = 0;
        public const int GL_NORMAL_ARRAY = 0x8075;
        public const int GL_NOTEQUAL = 0x205;
        public const int GL_NUM_COMPRESSED_TEXTURE_FORMATS = 0x86a2;
        public const int GL_ONE = 1;
        public const int GL_ONE_MINUS_SRC_ALPHA = 0x303;
        public const int GL_PALETTE8_RGBA4_OES = 0x8b98;
        public const int GL_PALETTE8_RGBA8_OES = 0x8b96;
        public const int GL_PERSPECTIVE_CORRECTION_HINT = 0xc50;
        public const int GL_PROJECTION = 0x1701;
        public const int GL_REPEAT = 0x2901;
        public const int GL_RGB = 0x1907;
        public const int GL_RGBA = 0x1908;
        public const int GL_SCISSOR_TEST = 0xc11;
        public const int GL_SHORT = 0x1402;
        public const int GL_SMOOTH = 0x1d01;
        public const int GL_SRC_ALPHA = 770;
        public const int GL_STENCIL_BUFFER_BIT = 0x400;
        public const int GL_STENCIL_TEST = 0xb90;
        public const int GL_TEXTURE = 0x1702;
        public const int GL_TEXTURE_2D = 0xde1;
        public const int GL_TEXTURE_COORD_ARRAY = 0x8078;
        public const int GL_TEXTURE_ENV = 0x2300;
        public const int GL_TEXTURE_ENV_MODE = 0x2200;
        public const int GL_TEXTURE_MAG_FILTER = 0x2800;
        public const int GL_TEXTURE_MIN_FILTER = 0x2801;
        public const int GL_TEXTURE_WRAP_S = 0x2802;
        public const int GL_TEXTURE_WRAP_T = 0x2803;
        public const int GL_TRIANGLE_FAN = 6;
        public const int GL_TRIANGLE_STRIP = 5;
        public const int GL_UNSIGNED_BYTE = 0x1401;
        public const int GL_UNSIGNED_SHORT_4_4_4_4 = 0x8033;
        public const int GL_VERTEX_ARRAY = 0x8074;
        private int matrixMode;
        private Stack<Matrix> matrixStack;
        private Texture[] texture;
        private TextureAddressMode textureAddressU;
        private TextureAddressMode textureAddressV;
        private TextureFilter textureFilter;
        private Vertex[] vertex;

        public GL10()
        {
            int num;
            this.color = new float[4];
            this.clearColor = Color.Black;
            this.ColorWriteChannels = ColorWriteChannels.All;
            this.AlphaDestinationBlend = Blend.InverseSourceAlpha;
            this.depthFunc = CompareFunction.Always;
            this.textureFilter = TextureFilter.Linear;
            this.textureAddressU = TextureAddressMode.Clamp;
            this.textureAddressV = TextureAddressMode.Clamp;
            this.vertex = new Vertex[8];
            this.texture = new Texture[0x100];
            this.matrixStack = new Stack<Matrix>();
            this.effect = new BasicEffect(GLEx.device);
            this.effect.VertexColorEnabled = true;
            this.alphaTestEffect = new AlphaTestEffect(GLEx.device);
            this.alphaTestEffect.VertexColorEnabled = true;
            for (num = 0; num < this.vertex.Length; num++)
            {
                this.vertex[num] = new Vertex();
            }
            for (num = 0; num < 4; num++)
            {
                this.color[num] = 1f;
            }
        }

        public void GLAlphaFunc(int func, int _ref)
        {
            switch (func)
            {
                case 0x202:
                    this.alphaTestEffect.AlphaFunction = CompareFunction.Equal;
                    break;

                case 0x204:
                    this.alphaTestEffect.AlphaFunction = CompareFunction.Greater;
                    break;
            }
            this.alphaTestEffect.ReferenceAlpha = _ref;
        }

        public void GLBindTexture(int target, int texture)
        {
            this.bindTexture = texture;
        }

        public static void GLBlendEquationOES(int equ)
        {
            AlphaBlendFunction = (equ == 0x8006) ? BlendFunction.Add : BlendFunction.ReverseSubtract;
        }

        public void GLBlendFunc(int sfactor, int dfactor)
        {
            this.AlphaDestinationBlend = (dfactor == 1) ? Blend.One : Blend.InverseSourceAlpha;
        }

        public void GLClear(int mask)
        {
            GLEx.device.Clear(((((mask & 0x4000) != 0) ? ClearOptions.Target : ((ClearOptions)0)) | (((mask & 0x100) != 0) ? ClearOptions.DepthBuffer : ((ClearOptions)0))) | (((mask & 0x400) != 0) ? ClearOptions.Stencil : ((ClearOptions)0)), this.clearColor, this.clearDepth, 0);
        }

        public void GLClearColor(float red, float green, float blue, float alpha)
        {
            this.clearColor = new Color(red, green, blue, alpha);
        }

        public void GLClearDepthf(float depth)
        {
            this.clearDepth = depth;
        }

        public void GLColor4f(float red, float green, float blue, float alpha)
        {
            this.color[0] = red;
            this.color[1] = green;
            this.color[2] = blue;
            this.color[3] = alpha;
        }

        public void GLColorMask(bool red, bool green, bool blue, bool alpha)
        {
            this.ColorWriteChannels = (((red ? ColorWriteChannels.Red : ColorWriteChannels.None) | (green ? ColorWriteChannels.Green : ColorWriteChannels.None)) | (blue ? ColorWriteChannels.Blue : ColorWriteChannels.None)) | (alpha ? ColorWriteChannels.Alpha : ColorWriteChannels.None);
        }

        public void GLColorPointer(int size, int type, int stride, ByteBuffer pointer)
        {
            this.vertex[6].Set(size, type, stride, pointer);
        }

        public void GLCompressedTexImage2D(int target, int level, int internalformat, int width, int height, int border, int imageSize, ByteBuffer data)
        {
            int num;
            byte[] sourceArray = new byte[imageSize];
            byte[] destinationArray = new byte[(width * height) * 4];
            for (num = 0; num < imageSize; num++)
            {
                sourceArray[num] = (byte)data.Get();
            }
            if (internalformat == 0x8b96)
            {
                for (num = 0; num < (width * height); num++)
                {
                    Array.Copy(sourceArray, sourceArray[0x400 + num] * 4, destinationArray, num * 4, 4);
                }
            }
            else
            {
                for (num = 0; num < (width * height); num++)
                {
                    int index = sourceArray[0x200 + num] * 2;
                    destinationArray[(num * 4) + 3] = (byte)(sourceArray[index] << 4);
                    destinationArray[(num * 4) + 2] = (byte)(sourceArray[index] & 240);
                    destinationArray[(num * 4) + 1] = (byte)(sourceArray[index + 1] << 4);
                    destinationArray[num * 4] = (byte)(sourceArray[index + 1] & 240);
                }
            }
            this.texture[this.bindTexture].texture = new Texture2D(GLEx.device, width, height);
            this.texture[this.bindTexture].texture.SetData<byte>(destinationArray);
        }

        public void GLDeleteTextures(int n, int[] textures, int offset)
        {
            for (int i = 0; i < n; i++)
            {
                int index = textures[i + offset];
                if (this.texture[index] != null)
                {
                    if (this.texture[index].texture != null)
                    {
                        this.texture[index].texture.Dispose();
                    }
                    this.texture[index] = null;
                }
            }
        }

        public void GLDepthFunc(int func)
        {
            switch (func)
            {
                case 0x206:
                    this.depthFunc = CompareFunction.GreaterEqual;
                    break;

                case 0x207:
                    this.depthFunc = CompareFunction.Always;
                    break;

                case 0x202:
                    this.depthFunc = CompareFunction.Equal;
                    break;
            }
        }

        public void GLDepthMask(bool flag)
        {
            this.depthMask = flag;
        }

        public void GLDisable(int cap)
        {
            this.GLEnable(cap, false);
        }

        public void GLDisableClientState(int array)
        {
            this.vertex[array & 7].enable = false;
        }

        public void GLDrawArrays(int mode, int first, int count)
        {
            int num2;
            int num3;
            PrimitiveType trianGLeList = PrimitiveType.TriangleList;
            int primitiveCount = 0;
            bool flag = (AlphaBlendFunction == BlendFunction.ReverseSubtract) && !this.effect.TextureEnabled;
            short[] indexData = null;
            switch (mode)
            {
                case 2:
                    trianGLeList = PrimitiveType.LineStrip;
                    primitiveCount = count;
                    indexData = new short[count + 1];
                    for (num2 = 0; num2 < count; num2++)
                    {
                        indexData[num2] = (short)num2;
                    }
                    indexData[count] = 0;
                    break;

                case 5:
                    trianGLeList = PrimitiveType.TriangleStrip;
                    primitiveCount = count - 2;
                    indexData = new short[count];
                    for (num2 = 0; num2 < count; num2++)
                    {
                        indexData[num2] = (short)num2;
                    }
                    break;

                case 6:
                    trianGLeList = PrimitiveType.TriangleList;
                    primitiveCount = count - 2;
                    indexData = new short[primitiveCount * 3];
                    num2 = 2;
                    num3 = 0;
                    while (num2 < count)
                    {
                        indexData[num3++] = 0;
                        indexData[num3++] = (short)(num2 - 1);
                        indexData[num3++] = (short)num2;
                        num2++;
                    }
                    break;
            }
            if (this.effect.TextureEnabled && (this.texture[this.bindTexture] != null))
            {
                this.effect.Texture = this.texture[this.bindTexture].texture;
            }
            else
            {
                this.effect.Texture = null;
            }
            if (this.alphaTest && (this.effect.Texture != null))
            {
                this.alphaTestEffect.World = this.effect.World;
                this.alphaTestEffect.Projection = this.effect.Projection;
                this.alphaTestEffect.Texture = this.effect.Texture;
                this.alphaTestEffect.CurrentTechnique.Passes[0].Apply();
            }
            else
            {
                this.effect.CurrentTechnique.Passes[0].Apply();
            }
            VertexPositionColorTexture[] vertexData = new VertexPositionColorTexture[count];
            float[] numArray2 = new float[3];
            float[] numArray3 = new float[2];
            byte[] buffer = new byte[4];
            num3 = 0;
            while (num3 < 4)
            {
                buffer[num3] = (byte)(this.color[num3] * 255f);
                num3++;
            }
            for (num2 = 0; num2 < count; num2++)
            {
                int num4;
                Vertex vertex = this.vertex[4];
                if (vertex.enable)
                {
                    num4 = vertex.pos + (num2 * vertex.stride);
                    num3 = 0;
                    while (num3 < vertex.size)
                    {
                        switch (vertex.type)
                        {
                            case 0x1400:
                                numArray2[num3] = vertex.pointer.Get(num4 + num3);
                                break;

                            case 0x1401:
                                numArray2[num3] = (byte)vertex.pointer.Get(num4 + num3);
                                break;

                            case 0x1402:
                                numArray2[num3] = vertex.pointer.GetShort(num4 + (num3 * 2));
                                break;

                            case 0x1406:
                                numArray2[num3] = vertex.pointer.GetFloat(num4 + (num3 * 4));
                                break;
                        }
                        num3++;
                    }
                }
                vertex = this.vertex[0];
                if (vertex.enable)
                {
                    num4 = vertex.pos + (num2 * vertex.stride);
                    num3 = 0;
                    while (num3 < vertex.size)
                    {
                        switch (vertex.type)
                        {
                            case 0x1400:
                                numArray3[num3] = vertex.pointer.Get(num4 + num3);
                                break;

                            case 0x1401:
                                numArray3[num3] = (byte)vertex.pointer.Get(num4 + num3);
                                break;

                            case 0x1402:
                                numArray3[num3] = vertex.pointer.GetShort(num4 + (num3 * 2));
                                break;

                            case 0x1406:
                                numArray3[num3] = vertex.pointer.GetFloat(num4 + (num3 * 4));
                                break;
                        }
                        num3++;
                    }
                }
                vertex = this.vertex[6];
                if (vertex.enable)
                {
                    num4 = vertex.pos + (num2 * vertex.stride);
                    for (num3 = 0; num3 < vertex.size; num3++)
                    {
                        if (vertex.type == 0x1401)
                        {
                            buffer[num3] = (byte)vertex.pointer.Get(num4 + num3);
                        }
                    }
                }
                vertexData[num2].Position.X = numArray2[0] - 0.5f;
                vertexData[num2].Position.Y = numArray2[1] - 0.5f;
                vertexData[num2].Position.Z = numArray2[2] - 0.5f;
                vertexData[num2].TextureCoordinate.X = numArray3[0];
                vertexData[num2].TextureCoordinate.Y = numArray3[1];
                vertexData[num2].Color.R = buffer[0];
                vertexData[num2].Color.G = buffer[1];
                vertexData[num2].Color.B = buffer[2];
                vertexData[num2].Color.A = buffer[3];
                if (flag)
                {
                    byte num6;
                    vertexData[num2].Color.B = (byte)(num6 = 0);
                    vertexData[num2].Color.R = vertexData[num2].Color.G = num6;
                    vertexData[num2].Color.A = (byte)(((buffer[0] + buffer[1]) + buffer[2]) / 3);
                }
            }
            BlendState blendState = GLEx.device.BlendState;
            if (((blendState.AlphaDestinationBlend != this.AlphaDestinationBlend) || (blendState.ColorWriteChannels != this.ColorWriteChannels)) || (blendState.AlphaBlendFunction != AlphaBlendFunction))
            {
                blendState = new BlendState();
                blendState.ColorSourceBlend = Blend.SourceAlpha;
                blendState.AlphaSourceBlend = Blend.SourceAlpha;
                blendState.ColorDestinationBlend = this.AlphaDestinationBlend;
                blendState.AlphaDestinationBlend = this.AlphaDestinationBlend;
                blendState.ColorWriteChannels = this.ColorWriteChannels;
                blendState.ColorWriteChannels1 = ColorWriteChannels.None;
                blendState.ColorWriteChannels2 = ColorWriteChannels.None;
                blendState.ColorWriteChannels3 = ColorWriteChannels.None;
                if (flag)
                {
                    blendState.ColorDestinationBlend = blendState.AlphaDestinationBlend = Blend.InverseSourceAlpha;
                }
                GLEx.device.BlendState = blendState;
            }
            SamplerState state2 = GLEx.device.SamplerStates[0];
            if (((state2.AddressU != this.textureAddressU) || (state2.AddressV != this.textureAddressV)) || (state2.Filter != this.textureFilter))
            {
                state2 = new SamplerState();
                state2.AddressU = this.textureAddressU;
                state2.AddressV = this.textureAddressV;
                state2.Filter = this.textureFilter;
                GLEx.device.SamplerStates[0] = state2;
            }
            DepthStencilState depthStencilState = GLEx.device.DepthStencilState;
            if (((depthStencilState.DepthBufferEnable != this.depthBuffer) || (depthStencilState.DepthBufferWriteEnable != this.depthMask)) || (depthStencilState.DepthBufferFunction != this.depthFunc))
            {
                depthStencilState = new DepthStencilState();
                depthStencilState.DepthBufferEnable = this.depthBuffer;
                depthStencilState.DepthBufferWriteEnable = this.depthMask;
                depthStencilState.DepthBufferFunction = this.depthFunc;
                GLEx.device.DepthStencilState = depthStencilState;
            }
            GLEx.device.DrawUserIndexedPrimitives<VertexPositionColorTexture>(trianGLeList, vertexData, 0, count, indexData, 0, primitiveCount);
        }

        public void GLEnable(int cap)
        {
            this.GLEnable(cap, true);
        }

        public void GLEnable(int cap, bool enabled)
        {
            int num = cap;
            if (num <= 0xb90)
            {
                switch (num)
                {
                    case 0xb60:
                        this.effect.FogEnabled = enabled;
                        return;

                    case 0xb71:
                        this.depthBuffer = enabled;
                        return;
                }
            }
            else if (num == 0xbc0)
            {
                this.alphaTest = enabled;
            }
            else if ((num != 0xbe2) && (num == 0xde1))
            {
                this.effect.TextureEnabled = enabled;
            }
        }

        public void GLEnableClientState(int array)
        {
            this.vertex[array & 7].enable = true;
        }

        public void GLFogf(int pname, float param)
        {
            switch (pname)
            {
                case 0xb63:
                    this.effect.FogStart = param;
                    break;

                case 0xb64:
                    this.effect.FogEnd = param;
                    break;
            }
        }

        public void GLFogfv(int pname, float[] param)
        {
            if (pname == 0xb66)
            {
                this.effect.FogColor = new Vector3(param[0], param[1], param[2]);
            }
        }

        public void GLGenTextures(int n, int[] textures, int offset)
        {
            for (int i = 0; i < n; i++)
            {
                for (int j = 1; j < this.texture.Length; j++)
                {
                    if (this.texture[j] == null)
                    {
                        this.texture[j] = new Texture();
                        textures[i + offset] = j;
                        break;
                    }
                }
            }
        }

        public void GLGetFloatv(int pname, float[] param, int offset)
        {
            if (pname == 0xb00)
            {
                Array.Copy(this.color, 0, param, offset, 4);
            }
        }

        public void GLLoadIdentity()
        {
            switch (this.matrixMode)
            {
                case 0x1700:
                    this.effect.World = Matrix.Identity;
                    break;

                case 0x1701:
                    this.effect.Projection = Matrix.Identity;
                    break;
            }
        }

        public void GLLoadMatrixf(float[] m, int offset)
        {
            Matrix matrix = new Matrix(m[offset], m[offset + 1], m[offset + 2], m[offset + 3], m[offset + 4], m[offset + 5], m[offset + 6], m[offset + 7], m[offset + 8], m[offset + 9], m[offset + 10], m[offset + 11], m[offset + 12], m[offset + 13], m[offset + 14], m[offset + 15]);
            switch (this.matrixMode)
            {
                case 0x1700:
                    this.effect.World = matrix;
                    break;

                case 0x1701:
                    this.effect.Projection = matrix;
                    break;
            }
        }

        public void GLMatrixMode(int mode)
        {
            this.matrixMode = mode;
        }

        public void GLMultMatrixf(float[] m, int offset)
        {
            Matrix matrix = new Matrix(m[offset], m[offset + 1], m[offset + 2], m[offset + 3], m[offset + 4], m[offset + 5], m[offset + 6], m[offset + 7], m[offset + 8], m[offset + 9], m[offset + 10], m[offset + 11], m[offset + 12], m[offset + 13], m[offset + 14], m[offset + 15]);
            switch (this.matrixMode)
            {
                case 0x1700:
                    this.effect.World = Matrix.Multiply(matrix, this.effect.World);
                    break;

                case 0x1701:
                    this.effect.Projection = Matrix.Multiply(matrix, this.effect.Projection);
                    break;
            }
        }

        public void GLPopMatrix()
        {
            switch (this.matrixMode)
            {
                case 0x1700:
                    this.effect.World = this.matrixStack.Pop();
                    break;

                case 0x1701:
                    this.effect.Projection = this.matrixStack.Pop();
                    break;
            }
        }

        public void GLPushMatrix()
        {
            switch (this.matrixMode)
            {
                case 0x1700:
                    this.matrixStack.Push(this.effect.World);
                    break;

                case 0x1701:
                    this.matrixStack.Push(this.effect.Projection);
                    break;
            }
        }

        public void GLTexCoordPointer(int size, int type, int stride, ByteBuffer pointer)
        {
            this.vertex[0].Set(size, type, stride, pointer);
        }

        public void GLTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, ByteBuffer pixels)
        {
            if ((width != 0) && (height != 0))
            {
                this.texture[this.bindTexture].texture = new Texture2D(GLEx.device, width, height);
                uint[] data = new uint[width * height];
                for (int i = 0; i < data.Length; i++)
                {
                    data[i] = (uint)pixels.GetInt();
                }
                this.texture[this.bindTexture].texture.SetData<uint>(data);
            }
        }

        public void GLTexParameterf(int target, int pname, float param)
        {
            switch (pname)
            {
                case 0x2800:
                    switch (((int)param))
                    {
                        case 0x2600:
                            this.textureFilter = TextureFilter.Point;
                            break;

                        case 0x2601:
                            this.textureFilter = TextureFilter.Linear;
                            break;
                    }
                    break;

                case 0x2802:
                    switch (((int)param))
                    {
                        case 0x2901:
                            this.textureAddressU = TextureAddressMode.Wrap;
                            break;

                        case 0x812f:
                            this.textureAddressU = TextureAddressMode.Clamp;
                            break;
                    }
                    break;

                case 0x2803:
                    switch (((int)param))
                    {
                        case 0x2901:
                            this.textureAddressV = TextureAddressMode.Wrap;
                            break;

                        case 0x812f:
                            this.textureAddressV = TextureAddressMode.Clamp;
                            break;
                    }
                    break;
            }
        }

        public void GLTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int type, ByteBuffer pixels)
        {
            uint[] data = new uint[width * height];
            for (int i = 0; i < data.Length; i++)
            {
                data[i] = (uint)pixels.GetInt();
            }
            if (this.effect.Texture == this.texture[this.bindTexture].texture)
            {
                this.effect.Texture = null;
                this.effect.CurrentTechnique.Passes[0].Apply();
            }
            this.texture[this.bindTexture].texture.SetData<uint>(0, new Rectangle(xoffset, yoffset, width, height), data, 0, data.Length);
        }

        public void GLVertexPointer(int size, int type, int stride, ByteBuffer pointer)
        {
            this.vertex[4].Set(size, type, stride, pointer);
        }

        private class Texture
        {
            public Texture2D texture;
        }

        private class Vertex
        {
            public bool enable;
            public ByteBuffer pointer;
            public int pos;
            public int size;
            public int stride;
            public int type;

            public void Set(int size, int type, int stride, ByteBuffer pointer)
            {
                if (stride == 0)
                {
                    switch (type)
                    {
                        case 0x1400:
                        case 0x1401:
                            stride = size;
                            break;

                        case 0x1402:
                            stride = size * 2;
                            break;

                        case 0x1406:
                            stride = size * 4;
                            break;
                    }
                }
                this.size = size;
                this.type = type;
                this.stride = stride;
                this.pointer = pointer;
                this.pos = pointer.Position();
            }
        }

    }
}
