using Microsoft.Xna.Framework.Graphics;
using System.Collections.Generic;
using Microsoft.Xna.Framework;
using Loon.Java;
using System;
using Loon.Utils;

namespace Loon.Core.Graphics.Opengl
{

    /// <summary>
    /// GLEx专用摄影机，用以矫正GLEx与位置相关的2D显示结果
    /// </summary>
    public class GLExCamera
    {


        private float rotation;

        private Vector2 position;

        private Vector2 scale;

        private Vector2 offset;

        private Rectangle visibleArea;

        internal Microsoft.Xna.Framework.Matrix viewMatrix;

        public Rectangle VisibleArea
        {
            get { return visibleArea; }
        }

        public int ViewingWidth
        {
            get { return visibleArea.Width; }
            set { visibleArea.Width = value; }
        }

        public int ViewingHeight
        {
            get { return visibleArea.Height; }
            set { visibleArea.Height = value; }
        }

        public Vector2 ScreenPosition
        {
            get { return new Vector2(GL.device.Viewport.Width / 2, GL.device.Viewport.Height / 2); }
        }

        public GLExCamera()
        {
            this.visibleArea = new Rectangle(0, 0, GL.device.Viewport.Width, GL.device.Viewport.Height);
            this.position = Vector2.Zero;
            this.scale = Vector2.One;
            this.rotation = 0.0f;
            this.offset = Vector2.Zero;
        }

        public Vector2 Offset
        {
            get { return offset; }
            set
            {
                offset = value;
                visibleArea.X = (int)(position.X + offset.X - visibleArea.Width / 2);
                visibleArea.Y = (int)(position.Y + offset.Y - visibleArea.Height / 2);
            }
        }

        public float Rotation
        {
            get { return this.rotation; }
            set { this.rotation = value; }
        }

        public Vector2 Scale
        {
            get { return this.scale; }
            set
            {
                this.scale = value;
            }
        }

        public void SetScale(float x, float y)
        {
            scale.X = x;
            scale.Y = y;
        }

        public Vector2 Position
        {
            get { return this.position; }
            set { this.position = value; }
        }

        public void SetTranslate(float x, float y)
        {
            position.X += x;
            position.Y += y;
        }

        public Microsoft.Xna.Framework.Matrix Old
        {
            get
            {
                this.position = Vector2.Zero;
                this.scale = Vector2.One;
                this.rotation = 0.0f;
                return Result;
            }
        }

        public Microsoft.Xna.Framework.Matrix Result
        {
            get
            {
                Vector3 matrixRotOrigin = new Vector3((-Position) + Offset, 0);
                Vector3 matrixScreenPos = new Vector3(ScreenPosition, 0.0f);

                Microsoft.Xna.Framework.Matrix result = Microsoft.Xna.Framework.Matrix.CreateTranslation(-matrixRotOrigin)
                    * Microsoft.Xna.Framework.Matrix.CreateScale(Scale.X, Scale.Y, 1.0f)
                    * Microsoft.Xna.Framework.Matrix.CreateRotationZ(rotation);


                return result;
            }
        }


    }

    public class GL
    {

        private System.Collections.Generic.Dictionary<Texture2D, TextureType> textureTypes = new Dictionary<Texture2D, TextureType>(CollectionUtils.INITIAL_CAPACITY);

        public const int MODE_NORMAL = 1;
        public const int MODE_ALPHA_MAP = 2;
        public const int MODE_ALPHA_BLEND = 3;
        public const int MODE_COLOR_MULTIPLY = 4;
        public const int MODE_ADD = 5;
        public const int MODE_SCREEN = 6;
        public const int MODE_ALPHA = 7;
        public const int MODE_SPEED = 8;
        public const int MODE_ALPHA_ONE = 9;
        public const int MODE_NONE = 10;
        public const int GL_QUADS = -1;
        public const int GL_POLYGON = 0x0006;
        public const int GL_POINTS = 0x0000;
        public const int GL_LINES = 0x0001;
        public const int GL_LINE_LOOP = 0x0002;
        public const int GL_LINE_STRIP = 0x0003;
        public const int GL_TRIANGLES = 0x0004;
        public const int GL_TRIANGLE_STRIP = 0x0005;
        public const int GL_TRIANGLE_FAN = 0x0006;

        private static BlendFunction AlphaBlendFunction = BlendFunction.Add;
        private Blend AlphaDestinationBlend;
        private bool alphaTest;
        private AlphaTestEffect alphaTestEffect;

        private Color clearColor;
        private float clearDepth;
        private float[] color;
        private Microsoft.Xna.Framework.Graphics.ColorWriteChannels ColorWriteChannels;
        private bool depthBuffer;
        private CompareFunction depthFunc;
        private bool depthMask;
        public static GraphicsDevice device;
        internal BasicEffect effect;

        public const int GL_RENDERER = 0x00;
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
        public const int GL_REPLACE = 0x1E01;
        public const int GL_FLAT = 0x1D00;
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
        public const int GL_SRC_COLOR = 0x0300;
        public const int GL_ONE_MINUS_SRC_COLOR = 0x0301;
        public const int GL_DST_ALPHA = 0x0304;
        public const int GL_ONE_MINUS_DST_ALPHA = 0x0305;
        public const int GL_UNSIGNED_BYTE = 0x1401;
        public const int GL_UNSIGNED_SHORT_4_4_4_4 = 0x8033;
        public const int GL_VERTEX_ARRAY = 0x8074;
        public const int GL_LINE_SMOOTH = 0x0B20;
        internal RasterizerState rstateScissor;

        private int matrixMode;
        private Stack<Matrix> matrixStack;

        private int sTextureId = 0;
        protected List<Texture2D> sTextures = new List<Texture2D>(CollectionUtils.INITIAL_CAPACITY);

        private Vertex[] vertex;

        private GLExCamera camera;

        internal GLExCamera GLCamera
        {
            get
            {
                return camera;
            }
        }

        public Microsoft.Xna.Framework.Matrix GLView
        {
            get
            {
                //XNA上有一处比较麻烦的设定，SpriteBatch的基础画面大小，不随Graphics设定改变，所以此处需要额外加入屏幕缩放比例，以供SpriteBatch处理。 
                if (LSystem.scaleWidth == 1 && LSystem.scaleHeight == 1)
                {
                    return camera.viewMatrix;
                }
                else
                {
                    return camera.viewMatrix * Matrix.CreateScale(LSystem.scaleWidth, LSystem.scaleHeight, 0f);
                }
            }
        }

        public GL()
        {
            int num;
            camera = new GLExCamera();
            camera.viewMatrix = camera.Result;
            this.rstateScissor = new RasterizerState();
            this.rstateScissor.CullMode = CullMode.None;
            this.rstateScissor.ScissorTestEnable = true;
            this.color = new float[4];
            this.clearColor = Color.Black;
            this.ColorWriteChannels = Microsoft.Xna.Framework.Graphics.ColorWriteChannels.All;
            this.AlphaDestinationBlend = Blend.InverseSourceAlpha;
            this.depthFunc = CompareFunction.Always;
            this.vertex = new Vertex[8];
            sTextures.Add(null);
            this.matrixStack = new Stack<Matrix>();
            this.matrixStack.Push(Matrix.Identity);
            this.effect = new BasicEffect(device);
            this.effect.VertexColorEnabled = true;
            this.effect.Projection = Microsoft.Xna.Framework.Matrix.CreateOrthographicOffCenter(0f, LSystem.screenRect.width, LSystem.screenRect.height, 0f, -1.0f, 1.0f);
            this.alphaTestEffect = new AlphaTestEffect(device);
            this.alphaTestEffect.VertexColorEnabled = true;
            device.RasterizerState = RasterizerState.CullNone;
            device.DepthStencilState = DepthStencilState.None;
            for (num = 0; num < this.vertex.Length; num++)
            {
                this.vertex[num] = new Vertex();
            }
            for (num = 0; num < 4; num++)
            {
                this.color[num] = 1f;
            }
            EnableTextures();
        }

        public void GLBindTexture(int inTextureId)
        {
            if (inTextureId != sTextureId)
            {
                sTextureId = inTextureId;
                effect.Texture = sTextures[sTextureId];
            }
        }

        public string GLGetString(int type)
        {
            return "XNA_GL";
        }

        private bool seColorArray, seTextures;

        public int CreateTexture(Texture2D inTexture2d)
        {
            int count = sTextures.Count;
            sTextures.Add(inTexture2d);
            if (!textureTypes.ContainsKey(inTexture2d))
            {
                textureTypes.Add(inTexture2d, new TextureType());
            }
            return count;
        }

        public void DisableColorArray()
        {
            if (seColorArray)
            {
                seColorArray = false;
                effect.VertexColorEnabled = false;
            }
        }

        public void DisableTextures()
        {
            if (seTextures)
            {
                seTextures = false;
                effect.TextureEnabled = false;
            }
        }

        public void EnableColorArray()
        {
            if (!seColorArray)
            {
                seColorArray = true;
                effect.VertexColorEnabled = true;
            }
        }

        public void EnableTextures()
        {
            if (!seTextures)
            {
                seTextures = true;
                effect.TextureEnabled = true;
            }
        }

        public Texture2D GetTexture(int inIndex)
        {
            return sTextures[inIndex];
        }

        public void GLActiveTexture(int sTextures)
        {
        }

        public void GLAlphaFunc(int func, float _ref)
        {
            GLAlphaFunc(func, (int)(_ref * 255f));
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

        public void GLBindTexture(int target, int sTextures)
        {
            this.sTextureId = sTextures;
        }

        public void GLBindTexture(int target, LTexture sTexture)
        {
            this.sTextureId = sTexture.GetTextureID();
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
            device.Clear(((((mask & 0x4000) != 0) ? ClearOptions.Target : ((ClearOptions)0)) | (((mask & 0x100) != 0) ? ClearOptions.DepthBuffer : ((ClearOptions)0))) | (((mask & 0x400) != 0) ? ClearOptions.Stencil : ((ClearOptions)0)), this.clearColor, this.clearDepth, 0);
        }

        public void GLClearColor(float red, float green, float blue, float alpha)
        {
            this.clearColor = new Color(red, green, blue, alpha);
        }

        public void GLClearColor()
        {
            this.clearColor = Color.Black;
        }

        public void GLClear(Color c)
        {
            this.clearColor = c;
        }

        public void GLClearDepthf(float depth)
        {
            this.clearDepth = depth;
        }

        public void GLClearStencil(int s)
        {
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
            this.ColorWriteChannels = (((red ? Microsoft.Xna.Framework.Graphics.ColorWriteChannels.Red : Microsoft.Xna.Framework.Graphics.ColorWriteChannels.None) | (green ? Microsoft.Xna.Framework.Graphics.ColorWriteChannels.Green : Microsoft.Xna.Framework.Graphics.ColorWriteChannels.None)) | (blue ? Microsoft.Xna.Framework.Graphics.ColorWriteChannels.Blue : Microsoft.Xna.Framework.Graphics.ColorWriteChannels.None)) | (alpha ? Microsoft.Xna.Framework.Graphics.ColorWriteChannels.Alpha : Microsoft.Xna.Framework.Graphics.ColorWriteChannels.None);
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
            this.sTextures[this.sTextureId] = new Texture2D(device, width, height);
            this.sTextures[this.sTextureId].SetData<byte>(destinationArray);
        }

        public void GLCompressedTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int imageSize, ByteBuffer data)
        {
        }

        public void GLDeleteTextures(int n, int[] textures, int offset)
        {
            for (int i = 0; i < n; i++)
            {
                int index = textures[i + offset];
                if (this.sTextures[index] != null)
                {
                    if (this.sTextures[index] != null)
                    {
                        this.sTextures[index].Dispose();
                    }
                    this.sTextures[index] = null;
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

        public void GLDepthRangef(float zNear, float zFar)
        {
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
            PrimitiveType triangleList = PrimitiveType.TriangleList;
            int primitiveCount = 0;
            bool flag = (AlphaBlendFunction == BlendFunction.ReverseSubtract) && !this.effect.TextureEnabled;
            short[] indexData = null;
            switch (mode)
            {
                case 2:
                    triangleList = PrimitiveType.LineStrip;
                    primitiveCount = count;
                    indexData = new short[count + 1];
                    for (num2 = 0; num2 < count; num2++)
                    {
                        indexData[num2] = (short)num2;
                    }
                    indexData[count] = 0;
                    break;

                case 5:
                    triangleList = PrimitiveType.TriangleStrip;
                    primitiveCount = count - 2;
                    indexData = new short[count];
                    for (num2 = 0; num2 < count; num2++)
                    {
                        indexData[num2] = (short)num2;
                    }
                    break;

                case 6:
                    triangleList = PrimitiveType.TriangleList;
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
            if (this.effect.TextureEnabled && (this.sTextures[this.sTextureId] != null))
            {
                this.effect.Texture = this.sTextures[this.sTextureId];
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
            if (XNA_BlendState == null)
            {
                BlendState blendState = device.BlendState;
                if (((blendState.AlphaDestinationBlend != this.AlphaDestinationBlend) || (blendState.ColorWriteChannels != this.ColorWriteChannels)) || (blendState.AlphaBlendFunction != AlphaBlendFunction))
                {
                    blendState = new BlendState();
                    blendState.ColorSourceBlend = Blend.SourceAlpha;
                    blendState.AlphaSourceBlend = Blend.SourceAlpha;
                    blendState.ColorDestinationBlend = this.AlphaDestinationBlend;
                    blendState.AlphaDestinationBlend = this.AlphaDestinationBlend;
                    blendState.ColorWriteChannels = this.ColorWriteChannels;
                    blendState.ColorWriteChannels1 = Microsoft.Xna.Framework.Graphics.ColorWriteChannels.None;
                    blendState.ColorWriteChannels2 = Microsoft.Xna.Framework.Graphics.ColorWriteChannels.None;
                    blendState.ColorWriteChannels3 = Microsoft.Xna.Framework.Graphics.ColorWriteChannels.None;
                    if (flag)
                    {
                        blendState.ColorDestinationBlend = blendState.AlphaDestinationBlend = Blend.InverseSourceAlpha;
                    }
                    device.BlendState = blendState;
                }
            }
            else
            {
                device.BlendState = XNA_BlendState;
            }
            if (seTextures)
            {
                TextureType texType = textureTypes[effect.Texture];
                if (texType != null)
                {
                    SamplerState updateState = device.SamplerStates[0];
                    if (((updateState.AddressU != texType.textureAddressU) || (updateState.AddressV != texType.textureAddressV)) || (updateState.Filter != texType.textureFilter))
                    {
                        updateState = new SamplerState();
                        updateState.AddressU = texType.textureAddressU;
                        updateState.AddressV = texType.textureAddressV;
                        updateState.Filter = texType.textureFilter;
                        device.SamplerStates[0] = updateState;
                    }
                }
            }
            DepthStencilState depthStencilState = device.DepthStencilState;
            if (((depthStencilState.DepthBufferEnable != this.depthBuffer) || (depthStencilState.DepthBufferWriteEnable != this.depthMask)) || (depthStencilState.DepthBufferFunction != this.depthFunc))
            {
                depthStencilState = new DepthStencilState();
                depthStencilState.DepthBufferEnable = this.depthBuffer;
                depthStencilState.DepthBufferWriteEnable = this.depthMask;
                depthStencilState.DepthBufferFunction = this.depthFunc;
                device.DepthStencilState = depthStencilState;
            }
            device.DrawUserIndexedPrimitives<VertexPositionColorTexture>(triangleList, vertexData, 0, count, indexData, 0, primitiveCount);
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

        public void GLFinish()
        {
        }

        public void GLFlush()
        {
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

        public void GLFrontFace(int mode)
        {
        }

        public void GLGenTextures(int n, int[] textures, int offset)
        {
            for (int i = 0; i < n; i++)
            {
                for (int j = 1; j < this.sTextures.Count; j++)
                {
                    if (this.sTextures[j] == null)
                    {
                        textures[i + offset] = j;
                        break;
                    }
                }
            }
        }

        internal void GLBind(LTexture tex2d)
        {
            if (tex2d != null)
            {
                EnableTextures();
                if (tex2d.isExt && !tex2d.isStatic)
                {
                    XNA_BlendState = BlendState.NonPremultiplied;
                }
                else if (tex2d.GetFileName() == null)
                {
                    XNA_BlendState = BlendState.AlphaBlend;
                }
            }
            else
            {
                DisableTextures();
            }
            if (tex2d.imageData != null)
            {
                this.effect.Texture = tex2d.imageData.buffer;
            }
        }

        internal void GLOnlyBind(LTexture tex2d)
        {
            if (tex2d != null)
            {
                EnableTextures();
            }
            else
            {
                DisableTextures();
            }
            if (tex2d.imageData != null)
            {
                this.effect.Texture = tex2d.imageData.buffer;
            }
        }

        public int GLGetError()
        {
            return 0;
        }

        public void GLGetFloatv(int pname, float[] param, int offset)
        {
            if (pname == 0xb00)
            {
                Array.Copy(this.color, 0, param, offset, 4);
            }
        }

        public void GLGetIntegerv(int pname, int[] param, int offset)
        {
        }

        public void GLHint(int target, int mode)
        {
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

        public void GLNormalPointer(int type, int stride, ByteBuffer pointer)
        {
        }

        public void GLReplaceTexture(int inIndex, Texture2D inTexture2d)
        {
            if (inIndex > -1 && inIndex < sTextures.Count)
            {
                sTextures[inIndex] = inTexture2d;
            }
        }

        private void GLUpdate()
        {
            camera.viewMatrix = camera.Result;
            switch (this.matrixMode)
            {
                case 0x1700:
                    this.effect.World = camera.viewMatrix;
                    break;

                case 0x1701:
                    this.effect.Projection = camera.viewMatrix;
                    break;
            }
        }

        public void GLRotate(float r)
        {
            camera.Rotation = MathHelper.ToRadians(r);
            GLUpdate();
        }

        public float GetRotation()
        {
            return camera.Rotation;
        }

        public void GLRotatef(float angle, float x, float y, float z)
        {
            GLTranslate(x, y, 0f);
            camera.Rotation = MathHelper.ToRadians(angle);
            GLUpdate();
        }

        public void GLTranslate(float x, float y, float z)
        {
            if (x != 0 || y != 0)
            {
                this.camera.SetTranslate(x, y);
                GLUpdate();
            }
        }

        public void GLScale(float inXScale, float inYScale, float inZScale)
        {
            if (((inXScale != 1f) || (inYScale != 1f)) || (inZScale != 1f))
            {
                this.camera.SetScale(inXScale, inYScale);
                GLUpdate();
            }
        }

        public void GLReset()
        {
            camera.viewMatrix = camera.Old;
        }

        public void GLPopMatrix()
        {
            if (matrixStack.Count > 0)
            {
                camera.viewMatrix = camera.Old;
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

        public static BlendState XNA_BlendState;

        public void Submit(int primitiveType, int count, VertexPositionColorTexture[] vertices, short[] quad, short[] fan)
        {
            bool flag = (AlphaBlendFunction == BlendFunction.ReverseSubtract) && !this.effect.TextureEnabled;
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
            if (XNA_BlendState == null)
            {
                BlendState blendState = device.BlendState;
                if (((blendState.AlphaDestinationBlend != this.AlphaDestinationBlend) || (blendState.ColorWriteChannels != this.ColorWriteChannels)) || (blendState.AlphaBlendFunction != AlphaBlendFunction))
                {
                    blendState = new BlendState();
                    blendState.ColorSourceBlend = Blend.SourceAlpha;
                    blendState.AlphaSourceBlend = Blend.SourceAlpha;
                    blendState.ColorDestinationBlend = this.AlphaDestinationBlend;
                    blendState.AlphaDestinationBlend = this.AlphaDestinationBlend;
                    blendState.ColorWriteChannels = this.ColorWriteChannels;
                    blendState.ColorWriteChannels1 = Microsoft.Xna.Framework.Graphics.ColorWriteChannels.None;
                    blendState.ColorWriteChannels2 = Microsoft.Xna.Framework.Graphics.ColorWriteChannels.None;
                    blendState.ColorWriteChannels3 = Microsoft.Xna.Framework.Graphics.ColorWriteChannels.None;
                    if (flag)
                    {
                        blendState.ColorDestinationBlend = blendState.AlphaDestinationBlend = Blend.InverseSourceAlpha;
                    }
                    device.BlendState = blendState;
                }
            }
            else
            {
                device.BlendState = XNA_BlendState;
            }
            if (seTextures)
            {
                TextureType texType = textureTypes[effect.Texture];
                if (texType != null)
                {
                    SamplerState updateState = device.SamplerStates[0];
                    if (((updateState.AddressU != texType.textureAddressU) || (updateState.AddressV != texType.textureAddressV)) || (updateState.Filter != texType.textureFilter))
                    {
                        updateState = new SamplerState();
                        updateState.AddressU = texType.textureAddressU;
                        updateState.AddressV = texType.textureAddressV;
                        updateState.Filter = texType.textureFilter;
                        device.SamplerStates[0] = updateState;

                    }
                }
            }

            DepthStencilState depthStencilState = device.DepthStencilState;
            if (((depthStencilState.DepthBufferEnable != this.depthBuffer) || (depthStencilState.DepthBufferWriteEnable != this.depthMask)) || (depthStencilState.DepthBufferFunction != this.depthFunc))
            {
                depthStencilState = new DepthStencilState();
                depthStencilState.DepthBufferEnable = this.depthBuffer;
                depthStencilState.DepthBufferWriteEnable = this.depthMask;
                depthStencilState.DepthBufferFunction = this.depthFunc;
                device.DepthStencilState = depthStencilState;
            }

            switch (primitiveType)
            {
                case GL.GL_QUADS:
                case GL_TRIANGLE_FAN:
                    device.DrawUserIndexedPrimitives<VertexPositionColorTexture>(PrimitiveType.TriangleList, vertices, 0, count * 4, quad, 0, count * 2);
                    break;
                case GL_TRIANGLES:
                    device.DrawUserIndexedPrimitives<VertexPositionColorTexture>(PrimitiveType.TriangleList, vertices, 0, count, fan, 0, count - 2);
                    break;
                case GL_LINE_LOOP:
                    device.DrawUserPrimitives<VertexPositionColorTexture>(PrimitiveType.LineStrip, vertices, 0, count);
                    break;
                case GL_LINE_STRIP:
                    device.DrawUserPrimitives<VertexPositionColorTexture>(PrimitiveType.LineStrip, vertices, 0, count - 1);
                    break;
                case GL_POINTS:
                case GL_LINES:
                    device.DrawUserPrimitives<VertexPositionColorTexture>(PrimitiveType.LineList, vertices, 0, count);
                    break;
            }
        }

        public static void GLVertices(int primitiveType, int numVertices, VertexPositionColorTexture[] vertices, List<Vector3> vertexsBuffer, List<Vector2> texCoordsBuffer, List<Color> colorsBuffer, bool hasCols, bool hasClear, Color defaultColor)
        {
            bool hasCoord = (texCoordsBuffer.Count != 0);
            switch (primitiveType)
            {
                case GL.GL_QUADS:
                case GL_TRIANGLE_FAN:
                    for (int index = 0; index < numVertices; index += 4)
                    {
                        if (hasCols)
                        {
                            vertices[index].Position = vertexsBuffer[index];
                            vertices[index].Color = colorsBuffer[index];
                            if (hasCoord)
                            {
                                vertices[index].TextureCoordinate = texCoordsBuffer[index];
                            }
                            if (index < numVertices - 1)
                            {
                                vertices[index + 1].Position = vertexsBuffer[index + 1];
                                vertices[index + 1].Color = colorsBuffer[index + 1];
                                if (hasCoord)
                                {
                                    vertices[index + 1].TextureCoordinate = texCoordsBuffer[index + 1];
                                }
                            }
                            if (index < numVertices - 2)
                            {
                                vertices[index + 2].Position = vertexsBuffer[index + 2];
                                vertices[index + 2].Color = colorsBuffer[index + 2];
                                if (hasCoord)
                                {
                                    vertices[index + 2].TextureCoordinate = texCoordsBuffer[index + 2];
                                }
                            }
                            if (index < numVertices - 3)
                            {
                                vertices[index + 3].Position = vertexsBuffer[index + 3];
                                vertices[index + 3].Color = colorsBuffer[index + 3];
                                if (hasCoord)
                                {
                                    vertices[index + 3].TextureCoordinate = texCoordsBuffer[index + 3];
                                }
                            }
                        }
                        else
                        {
                            vertices[index].Position = vertexsBuffer[index];
                            vertices[index].Color = defaultColor;
                            if (hasCoord)
                            {
                                vertices[index].TextureCoordinate = texCoordsBuffer[index];
                            }
                            if (index < numVertices - 1)
                            {
                                vertices[index + 1].Position = vertexsBuffer[index + 1];
                                vertices[index + 1].Color = defaultColor;
                                if (hasCoord)
                                {
                                    vertices[index + 1].TextureCoordinate = texCoordsBuffer[index + 1];
                                }
                            }
                            if (index < numVertices - 2)
                            {
                                vertices[index + 2].Position = vertexsBuffer[index + 2];
                                vertices[index + 2].Color = defaultColor;
                                if (hasCoord)
                                {
                                    vertices[index + 2].TextureCoordinate = texCoordsBuffer[index + 2];
                                }
                            }
                            if (index < numVertices - 3)
                            {
                                vertices[index + 3].Position = vertexsBuffer[index + 3];
                                vertices[index + 3].Color = defaultColor;
                                if (hasCoord)
                                {
                                    vertices[index + 3].TextureCoordinate = texCoordsBuffer[index + 3];
                                }
                            }
                        }
                    }
                    break;
                case GL_TRIANGLES:
                    for (int index = 0; index < numVertices; index += 3)
                    {
                        if (hasCols)
                        {
                            vertices[index].Position = vertexsBuffer[index];
                            vertices[index].Color = colorsBuffer[index];
                            if (hasCoord)
                            {
                                vertices[index].TextureCoordinate = texCoordsBuffer[index];
                            }
                            if (index < numVertices - 1)
                            {
                                vertices[index + 1].Position = vertexsBuffer[index + 1];
                                vertices[index + 1].Color = colorsBuffer[index + 1];
                                if (hasCoord)
                                {
                                    vertices[index + 1].TextureCoordinate = texCoordsBuffer[index + 1];
                                }
                            }
                            if (index < numVertices - 2)
                            {
                                vertices[index + 2].Position = vertexsBuffer[index + 2];
                                vertices[index + 2].Color = colorsBuffer[index + 2];
                                if (hasCoord)
                                {
                                    vertices[index + 2].TextureCoordinate = texCoordsBuffer[index + 2];
                                }
                            }
                        }
                        else
                        {
                            vertices[index].Position = vertexsBuffer[index];
                            vertices[index].Color = defaultColor;
                            if (hasCoord)
                            {
                                vertices[index].TextureCoordinate = texCoordsBuffer[index];
                            }
                            if (index < numVertices - 1)
                            {
                                vertices[index + 1].Position = vertexsBuffer[index + 1];
                                vertices[index + 1].Color = defaultColor;
                                if (hasCoord)
                                {
                                    vertices[index + 1].TextureCoordinate = texCoordsBuffer[index + 1];
                                }
                            }
                            if (index < numVertices - 2)
                            {
                                vertices[index + 2].Position = vertexsBuffer[index + 2];
                                vertices[index + 2].Color = defaultColor;
                                if (hasCoord)
                                {
                                    vertices[index + 2].TextureCoordinate = texCoordsBuffer[index + 2];
                                }
                            }
                        }
                    }
                    break;
                case GL_LINE_LOOP:
                    if (numVertices % 2 == 0)
                    {
                        vertexsBuffer.Add(new Vector3(vertexsBuffer[0].X, vertexsBuffer[0].Y, vertexsBuffer[0].Z));
                        numVertices++;
                    }
                    for (int index = 0; index < numVertices; index += 2)
                    {
                        if (hasCols)
                        {
                            vertices[index].Position = vertexsBuffer[index];
                            vertices[index].Color = colorsBuffer[index];
                            if (index < numVertices - 1)
                            {
                                vertices[index + 1].Position = vertexsBuffer[index + 1];
                                vertices[index + 1].Color = colorsBuffer[index + 1];
                            }
                        }
                        else
                        {
                            vertices[index].Position = vertexsBuffer[index];
                            vertices[index].Color = defaultColor;
                            if (index < numVertices - 1)
                            {
                                vertices[index + 1].Position = vertexsBuffer[index + 1];
                                vertices[index + 1].Color = defaultColor;
                            }

                        }
                    }
                    break;
                case GL_LINE_STRIP:
                    for (int index = 0; index < numVertices; index += 2)
                    {
                        if (hasCols)
                        {
                            vertices[index].Position = vertexsBuffer[index];
                            vertices[index].Color = colorsBuffer[index];
                            if (index < numVertices - 1)
                            {
                                vertices[index + 1].Position = vertexsBuffer[index + 1];
                                vertices[index + 1].Color = colorsBuffer[index + 1];
                            }
                        }
                        else
                        {
                            vertices[index].Position = vertexsBuffer[index];
                            vertices[index].Color = defaultColor;
                            if (index < numVertices - 1)
                            {
                                vertices[index + 1].Position = vertexsBuffer[index + 1];
                                vertices[index + 1].Color = defaultColor;
                            }

                        }
                    }
                    break;
                case GL_POINTS:
                case GL_LINES:
                    for (int index = 0; index < numVertices; index++)
                    {
                        if (hasCols)
                        {
                            vertices[index].Position = vertexsBuffer[index];
                            vertices[index].Color = colorsBuffer[0];
                        }
                        else
                        {
                            vertices[index].Position = vertexsBuffer[index];
                            vertices[index].Color = defaultColor;
                        }
                    }
                    break;
            }
            if (hasClear)
            {
                vertexsBuffer.Clear();
                texCoordsBuffer.Clear();
                colorsBuffer.Clear();
            }
        }

        private Rectangle scissorRect;

        public void GLScissor(int x, int y, int width, int height)
        {
            if (((x != 0) || (y != 0)) || ((width != device.PresentationParameters.BackBufferWidth) || (height != device.PresentationParameters.BackBufferHeight)))
            {
                this.scissorRect.X = x;
                this.scissorRect.Y = y;
                this.scissorRect.Width = width;
                this.scissorRect.Height = height;
                device.RasterizerState = this.rstateScissor;
                device.ScissorRectangle = this.scissorRect;
            }
            else
            {
                device.RasterizerState = RasterizerState.CullNone;
            }
        }

        public void GLShadeModel(int mode)
        {
        }

        public void GLStencilFunc(int func, int _ref, int mask)
        {
        }

        public void GLStencilOp(int fail, int zfail, int zpass)
        {
        }

        public void GLTexCoordPointer(int size, int type, int stride, ByteBuffer pointer)
        {
            this.vertex[0].Set(size, type, stride, pointer);
        }

        public void GLTexEnvf(int target, int pname, float param)
        {
        }

        public void GLTexImage2D(LTexture bitmap)
        {
            try
            {
                this.sTextures[this.sTextureId] = bitmap.Texture;
            }
            catch (Exception)
            {
            }
        }

        public void GLTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, ByteBuffer pixels)
        {
            if ((width != 0) && (height != 0))
            {
                this.sTextures[this.sTextureId] = new Texture2D(device, width, height);
                uint[] data = new uint[width * height];
                for (int i = 0; i < data.Length; i++)
                {
                    data[i] = (uint)pixels.GetInt();
                }
                this.sTextures[this.sTextureId].SetData<uint>(data);
            }
        }

        public void GLTexParameterf(int target, int pname, float param)
        {
            GLTexParameterf(effect.Texture, target, pname, param);
        }

        public void GLTexParameterf(LTexture texture, int target, int pname, float param)
        {
            GLTexParameterf(texture.imageData.buffer, target, pname, param);
        }

        public void GLTexParameterf(Texture2D texture, int target, int pname, float param)
        {
            TextureType texType = textureTypes[texture];
            if (texType != null)
            {
                switch (pname)
                {
                    case 0x2800:
                        switch (((int)param))
                        {
                            case 0x2600:
                                texType.textureFilter = TextureFilter.Point;
                                break;

                            case 0x2601:
                                texType.textureFilter = TextureFilter.Linear;
                                break;
                        }
                        break;

                    case 0x2802:
                        switch (((int)param))
                        {
                            case 0x2901:
                                texType.textureAddressU = TextureAddressMode.Wrap;
                                break;

                            case 0x812f:
                                texType.textureAddressU = TextureAddressMode.Clamp;
                                break;
                        }
                        break;

                    case 0x2803:
                        switch (((int)param))
                        {
                            case 0x2901:
                                texType.textureAddressV = TextureAddressMode.Wrap;
                                break;

                            case 0x812f:
                                texType.textureAddressV = TextureAddressMode.Clamp;
                                break;
                        }
                        break;
                }
            }
        }

        public void GLTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int type, ByteBuffer pixels)
        {
            uint[] data = new uint[width * height];
            for (int i = 0; i < data.Length; i++)
            {
                data[i] = (uint)pixels.GetInt();
            }
            if (this.effect.Texture == this.sTextures[this.sTextureId])
            {
                this.effect.Texture = null;
                this.effect.CurrentTechnique.Passes[0].Apply();
            }
            this.sTextures[this.sTextureId].SetData<uint>(0, new Rectangle(xoffset, yoffset, width, height), data, 0, data.Length);
        }

        public void GLVertexPointer(int size, int type, int stride, ByteBuffer pointer)
        {
            this.vertex[4].Set(size, type, stride, pointer);
        }

        public void GLViewport(int x, int y, int width, int height)
        {
            if (width > device.PresentationParameters.BackBufferWidth)
            {
                width = device.PresentationParameters.BackBufferWidth;
            }
            if (height > device.PresentationParameters.BackBufferHeight)
            {
                height = device.PresentationParameters.BackBufferHeight;
            }
            Viewport view = new Viewport(x, y, width, height);
            device.Viewport = view;
        }

        public void GLOrthof(float left, float right, float bottom, float top, float zNear, float zFar)
        {
            switch (this.matrixMode)
            {
                case 0x1700:
                    this.effect.World *= Matrix.CreateOrthographicOffCenter(left, right, bottom, top, zNear, zFar);
                    break;
                case 0x1701:
                    this.effect.Projection *= Matrix.CreateOrthographicOffCenter(left, right, bottom, top, zNear, zFar);
                    break;
            }
        }

        public class TextureType
        {
            public TextureFilter textureFilter = TextureFilter.Linear;
            public TextureAddressMode textureAddressU = TextureAddressMode.Clamp;
            public TextureAddressMode textureAddressV = TextureAddressMode.Clamp;
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
