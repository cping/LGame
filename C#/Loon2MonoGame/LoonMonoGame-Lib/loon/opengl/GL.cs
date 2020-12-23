using java.io;
using loon.utils;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using System;
using System.Collections.Generic;

namespace loon.opengl
{
    /// <summary>
    /// 模拟OpenGL的API（monogame在大部分环境都把OpenGL本地适配为XNA的API，我为了方便移植又反向模拟回来……）
    /// </summary>
    public class GL
    {
        public class TextureType
        {
            public TextureFilter textureFilter = TextureFilter.Linear;
            public TextureAddressMode textureAddressU = TextureAddressMode.Clamp;
            public TextureAddressMode textureAddressV = TextureAddressMode.Clamp;
        }

        public class GLExCamera
        {


            private float rotation;

            private Vector2 position;

            private Vector2 scale;

            private Vector2 offset;

            private Rectangle visibleArea;

            private readonly GraphicsDevice _graphicsDevice;

            internal Microsoft.Xna.Framework.Matrix viewMatrix;

            public GLExCamera(GraphicsDevice graphicsDevice, int w, int h)
            {
                this._graphicsDevice = graphicsDevice;
                this.visibleArea = new Rectangle(0, 0, w, h);
                this.position = Vector2.Zero;
                this.scale = Vector2.One;
                this.rotation = 0.0f;
                this.offset = Vector2.Zero;
            }

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
                get { return new Vector2(_graphicsDevice.Viewport.Width / 2, _graphicsDevice.Viewport.Height / 2); }
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

            public Matrix Result
            {
                get
                {
                    Vector3 matrixRotOrigin = new Vector3((-Position) + Offset, 0);
                    Vector3 matrixScreenPos = new Vector3(ScreenPosition, 0.0f);

                    Matrix result = Matrix.CreateTranslation(-matrixRotOrigin)
                        * Matrix.CreateScale(Scale.X, Scale.Y, 1.0f)
                        * Matrix.CreateRotationZ(rotation);


                    return result;
                }
            }


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
                        case GL_BYTE:
                        case GL_UNSIGNED_BYTE:
                            stride = size;
                            break;

                        case GL_SHORT:
                            stride = size * 2;
                            break;

                        case GL_FLOAT:
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

        public const int GL_QUADS = -1;
        public const int GL_POLYGON = 0x0006;
        public const int GL_POINTS = 0x0000;
        public const int GL_LINES = 0x0001;
        public const int GL_LINE_LOOP = 0x0002;
        public const int GL_LINE_STRIP = 0x0003;
        public const int GL_TRIANGLES = 0x0004;
        public const int GL_TRIANGLE_STRIP = 0x0005;
        public const int GL_TRIANGLE_FAN = 0x0006;

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

        private Dictionary<Texture2D, TextureType> _textureTypes = new Dictionary<Texture2D, TextureType>(CollectionUtils.INITIAL_CAPACITY);

        private GLExCamera _camera;

        private bool _seTextures;

        protected internal readonly IntMap<Texture2D> _textures;

        protected internal readonly GraphicsDevice _device;

        protected internal BasicEffect _effect;

        protected internal RasterizerState _rstateScissor;

        private Rectangle _scissorRect;
        private int _matrixMode;
        private System.Collections.Generic.Stack<Matrix> _matrixStack;

        private int _textureId;

        private Vertex[] _vertex;


        private BlendFunction _alphaBlendFunction = BlendFunction.Add;
        private Blend _alphaDestinationBlend;
        private bool _alphaTest;
        private AlphaTestEffect _alphaTestEffect;

        private int _textureCount;
        private bool _colorArray;
        private Color _clearColor;
        private float _clearDepth;
        private float[] _color;
        private ColorWriteChannels _colorWriteChannels;
        private bool _depthBuffer;
        private CompareFunction _depthFunc;
        private bool _depthMask;

        private  Microsoft.Xna.Framework.Graphics.BlendState _blendState;

        private GraphicsDevice _graphicsDevice;
        public GL(GraphicsDevice d, int w, int h)
        {
            this._graphicsDevice = d;
            this._textures = new IntMap<Texture2D>();
            this._camera = new GLExCamera(d, w, h);
            this._rstateScissor = new RasterizerState();
            this._rstateScissor.CullMode = CullMode.None;
            this._rstateScissor.ScissorTestEnable = true;
            this._color = new float[4];
            this._clearColor = Color.Black;
            this._colorWriteChannels = ColorWriteChannels.All;
            this._alphaDestinationBlend = Blend.InverseSourceAlpha;
            this._depthFunc = CompareFunction.Always;
            this._vertex = new Vertex[8];
            this._matrixStack = new System.Collections.Generic.Stack<Matrix>();
            this._matrixStack.Push(Matrix.Identity);
            this._effect = new BasicEffect(d);
            this._effect.VertexColorEnabled = true;
            this._effect.Projection = Matrix.CreateOrthographicOffCenter(0f, w, h, 0f, -1.0f, 1.0f);
            this._alphaTestEffect = new AlphaTestEffect(d);
            this._alphaTestEffect.VertexColorEnabled = true;
            d.RasterizerState = RasterizerState.CullNone;
            d.DepthStencilState = DepthStencilState.None;
            for (int i = 0; i < this._vertex.Length; i++)
            {
                this._vertex[i] = new Vertex();
            }
            for (int i = 0; i < 4; i++)
            {
                this._color[i] = 1f;
            }
            EnableTextures();
        }
        public void GLOrthof(float left, float right, float bottom, float top, float zNear, float zFar)
        {
            switch (this._matrixMode)
            {
                case GL_MODELVIEW:
                    this._effect.World *= Matrix.CreateOrthographicOffCenter(left, right, bottom, top, zNear, zFar);
                    break;
                case GL_PROJECTION:
                    this._effect.Projection *= Matrix.CreateOrthographicOffCenter(left, right, bottom, top, zNear, zFar);
                    break;
            }
        }

        public void GLTexParameteri(int target, int pname, int param)
        {
            GLTexParameteri(_effect.Texture, target, pname, param);
        }

        public void GLTexParameteri(Texture2D texture, int target, int pname, int param)
        {
            TextureType texType = _textureTypes[texture];
            if (texType != null)
            {
                switch (pname)
                {
                    case GL_TEXTURE_MAG_FILTER:
                        switch (param)
                        {
                            case GL_NEAREST:
                                texType.textureFilter = TextureFilter.Point;
                                break;

                            case GL_LINEAR:
                                texType.textureFilter = TextureFilter.Linear;
                                break;
                        }
                        break;

                    case GL_TEXTURE_WRAP_S:
                        switch (param)
                        {
                            case GL_REPEAT:
                                texType.textureAddressU = TextureAddressMode.Wrap;
                                break;

                            case GL_CLAMP_TO_EDGE:
                                texType.textureAddressU = TextureAddressMode.Clamp;
                                break;
                        }
                        break;

                    case GL_TEXTURE_WRAP_T:
                        switch (param)
                        {
                            case GL_REPEAT:
                                texType.textureAddressV = TextureAddressMode.Wrap;
                                break;

                            case GL_CLAMP_TO_EDGE:
                                texType.textureAddressV = TextureAddressMode.Clamp;
                                break;
                        }
                        break;
                }
            }
        }

        public void GLBlendEquationOES(int equ)
        {
            _alphaBlendFunction = (equ == 0x8006) ? BlendFunction.Add : BlendFunction.ReverseSubtract;
        }

        public void GLClear(int mask)
        {
            _graphicsDevice.Clear(((((mask & GL_COLOR_BUFFER_BIT) != 0) ? ClearOptions.Target : ((ClearOptions)0)) | (((mask & GL_DEPTH_BUFFER_BIT) != 0) ? ClearOptions.DepthBuffer : ((ClearOptions)0))) | (((mask & GL_STENCIL_BUFFER_BIT) != 0) ? ClearOptions.Stencil : ((ClearOptions)0)), this._clearColor, this._clearDepth, 0);
        }

        public void GLClearColor(float red, float green, float blue, float alpha)
        {
            this._clearColor = new Color(red, green, blue, alpha);
        }

        public void GLClearColor()
        {
            this._clearColor = Color.Black;
        }

        public void GLClear(Color c)
        {
            this._clearColor = c;
        }

        public void GLClearDepthf(float depth)
        {
            this._clearDepth = depth;
        }

        public void GLColor4f(float red, float green, float blue, float alpha)
        {
            this._color[0] = red;
            this._color[1] = green;
            this._color[2] = blue;
            this._color[3] = alpha;
        }

        public void GLColorMask(bool red, bool green, bool blue, bool alpha)
        {
            this._colorWriteChannels = (((red ? ColorWriteChannels.Red : ColorWriteChannels.None) | (green ? ColorWriteChannels.Green : ColorWriteChannels.None)) | (blue ? ColorWriteChannels.Blue : ColorWriteChannels.None)) | (alpha ? ColorWriteChannels.Alpha : ColorWriteChannels.None);
        }

        public void GLColorPointer(int size, int type, int stride, ByteBuffer pointer)
        {
            this._vertex[6].Set(size, type, stride, pointer);
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
            if (internalformat == GL_PALETTE8_RGBA8_OES)
            {
                for (num = 0; num < (width * height); num++)
                {
                    Array.Copy(sourceArray, sourceArray[num] * 4, destinationArray, num * 4, 4);
                }
            }
            else
            {
                for (num = 0; num < (width * height); num++)
                {
                    int index = sourceArray[num] * 2;
                    destinationArray[(num * 4) + 3] = (byte)(sourceArray[index] << 4);
                    destinationArray[(num * 4) + 2] = (byte)(sourceArray[index] & 240);
                    destinationArray[(num * 4) + 1] = (byte)(sourceArray[index + 1] << 4);
                    destinationArray[num * 4] = (byte)(sourceArray[index + 1] & 240);
                }
            }
            this._textures.Put(this._textureId, new Texture2D(_graphicsDevice, width, height));
            this._textures.Get(this._textureId).SetData<byte>(destinationArray);
        }

        public void GLEnable(int cap)
        {
            this.GLEnable(cap, true);
        }

        public void GLEnable(int cap, bool enabled)
        {
            int num = cap;
            if (num <= GL_STENCIL_TEST)
            {
                switch (num)
                {
                    case GL_FOG:
                        this._effect.FogEnabled = enabled;
                        return;

                    case GL_DEPTH_TEST:
                        this._depthBuffer = enabled;
                        return;
                }
            }
            else if (num == GL_ALPHA_TEST)
            {
                this._alphaTest = enabled;
            }
            else if ((num != GL_BLEND) && (num == GL_TEXTURE_2D))
            {
                this._effect.TextureEnabled = enabled;
            }
        }

        public void SetBlendState(Microsoft.Xna.Framework.Graphics.BlendState b)
        {
            this._blendState = b;
        }

        public Microsoft.Xna.Framework.Graphics.BlendState GetBlendState()
        {
            return this._blendState;
        }

        public void GLDrawArrays(int mode, int first, int count)
        {
            int num2;
            int num3;
            PrimitiveType triangleList = PrimitiveType.TriangleList;
            int primitiveCount = 0;
            bool flag = (_alphaBlendFunction == BlendFunction.ReverseSubtract) && !this._effect.TextureEnabled;
            short[] indexData = null;
            switch (mode)
            {
                case GL_LINE_LOOP:
                    triangleList = PrimitiveType.LineStrip;
                    primitiveCount = count;
                    indexData = new short[count + 1];
                    for (num2 = 0; num2 < count; num2++)
                    {
                        indexData[num2] = (short)num2;
                    }
                    indexData[count] = 0;
                    break;

                case GL_TRIANGLE_STRIP:
                    triangleList = PrimitiveType.TriangleStrip;
                    primitiveCount = count - 2;
                    indexData = new short[count];
                    for (num2 = 0; num2 < count; num2++)
                    {
                        indexData[num2] = (short)num2;
                    }
                    break;

                case GL_TRIANGLE_FAN:
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
            if (this._effect.TextureEnabled && (this._textures.ContainsKey(this._textureId)))
            {
                this._effect.Texture = this._textures.Get(this._textureId);
            }
            else
            {
                this._effect.Texture = null;
            }
            if (this._alphaTest && (this._effect.Texture != null))
            {
                this._alphaTestEffect.World = this._effect.World;
                this._alphaTestEffect.Projection = this._effect.Projection;
                this._alphaTestEffect.Texture = this._effect.Texture;
                this._alphaTestEffect.CurrentTechnique.Passes[0].Apply();
            }
            else
            {
                this._effect.CurrentTechnique.Passes[0].Apply();
            }
            VertexPositionColorTexture[] vertexData = new VertexPositionColorTexture[count];
            float[] numArray2 = new float[3];
            float[] numArray3 = new float[2];
            byte[] buffer = new byte[4];
            num3 = 0;
            while (num3 < 4)
            {
                buffer[num3] = (byte)(this._color[num3] * 255f);
                num3++;
            }
            for (num2 = 0; num2 < count; num2++)
            {
                int num4;
                Vertex vertex = this._vertex[4];
                if (vertex.enable)
                {
                    num4 = vertex.pos + (num2 * vertex.stride);
                    num3 = 0;
                    while (num3 < vertex.size)
                    {
                        switch (vertex.type)
                        {
                            case GL_BYTE:
                                numArray2[num3] = vertex.pointer.Get(num4 + num3);
                                break;

                            case GL_UNSIGNED_BYTE:
                                numArray2[num3] = (byte)vertex.pointer.Get(num4 + num3);
                                break;

                            case GL_SHORT:
                                numArray2[num3] = vertex.pointer.GetShort(num4 + (num3 * 2));
                                break;

                            case GL_FLOAT:
                                numArray2[num3] = vertex.pointer.GetFloat(num4 + (num3 * 4));
                                break;
                        }
                        num3++;
                    }
                }
                vertex = this._vertex[0];
                if (vertex.enable)
                {
                    num4 = vertex.pos + (num2 * vertex.stride);
                    num3 = 0;
                    while (num3 < vertex.size)
                    {
                        switch (vertex.type)
                        {
                            case GL_BYTE:
                                numArray3[num3] = vertex.pointer.Get(num4 + num3);
                                break;

                            case GL_UNSIGNED_BYTE:
                                numArray3[num3] = (byte)vertex.pointer.Get(num4 + num3);
                                break;

                            case GL_SHORT:
                                numArray3[num3] = vertex.pointer.GetShort(num4 + (num3 * 2));
                                break;

                            case GL_FLOAT:
                                numArray3[num3] = vertex.pointer.GetFloat(num4 + (num3 * 4));
                                break;
                        }
                        num3++;
                    }
                }
                vertex = this._vertex[6];
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
            if (_blendState == null)
            {
                Microsoft.Xna.Framework.Graphics.BlendState blendState = _graphicsDevice.BlendState;
                if (((blendState.AlphaDestinationBlend != this._alphaDestinationBlend) || (blendState.ColorWriteChannels != this._colorWriteChannels)) || (blendState.AlphaBlendFunction != _alphaBlendFunction))
                {
                    blendState = new Microsoft.Xna.Framework.Graphics.BlendState();
                    blendState.ColorSourceBlend = Blend.SourceAlpha;
                    blendState.AlphaSourceBlend = Blend.SourceAlpha;
                    blendState.ColorDestinationBlend = this._alphaDestinationBlend;
                    blendState.AlphaDestinationBlend = this._alphaDestinationBlend;
                    blendState.ColorWriteChannels = this._colorWriteChannels;
                    blendState.ColorWriteChannels1 = ColorWriteChannels.None;
                    blendState.ColorWriteChannels2 = ColorWriteChannels.None;
                    blendState.ColorWriteChannels3 = ColorWriteChannels.None;
                    if (flag)
                    {
                        blendState.ColorDestinationBlend = blendState.AlphaDestinationBlend = Blend.InverseSourceAlpha;
                    }
                    _graphicsDevice.BlendState = blendState;
                }
            }
            else
            {
                _graphicsDevice.BlendState = _blendState;
            }
            if (_seTextures)
            {
                TextureType texType = _textureTypes[_effect.Texture];
                if (texType != null)
                {
                    SamplerState updateState = _graphicsDevice.SamplerStates[0];
                    if (((updateState.AddressU != texType.textureAddressU) || (updateState.AddressV != texType.textureAddressV)) || (updateState.Filter != texType.textureFilter))
                    {
                        updateState = new SamplerState();
                        updateState.AddressU = texType.textureAddressU;
                        updateState.AddressV = texType.textureAddressV;
                        updateState.Filter = texType.textureFilter;
                        _graphicsDevice.SamplerStates[0] = updateState;
                    }
                }
            }
            DepthStencilState depthStencilState = _graphicsDevice.DepthStencilState;
            if (((depthStencilState.DepthBufferEnable != this._depthBuffer) || (depthStencilState.DepthBufferWriteEnable != this._depthMask)) || (depthStencilState.DepthBufferFunction != this._depthFunc))
            {
                depthStencilState = new DepthStencilState();
                depthStencilState.DepthBufferEnable = this._depthBuffer;
                depthStencilState.DepthBufferWriteEnable = this._depthMask;
                depthStencilState.DepthBufferFunction = this._depthFunc;
                _graphicsDevice.DepthStencilState = depthStencilState;
            }
            _graphicsDevice.DrawUserIndexedPrimitives<VertexPositionColorTexture>(triangleList, vertexData, 0, count, indexData, 0, primitiveCount);
        }

        public void GLEnableClientState(int array)
        {
            this._vertex[array & 7].enable = true;
        }

        public void GLFogf(int pname, float param)
        {
            switch (pname)
            {
                case GL_FOG_START:
                    this._effect.FogStart = param;
                    break;

                case GL_FOG_END:
                    this._effect.FogEnd = param;
                    break;
            }
        }

        public void GLFogfv(int pname, float[] param)
        {
            if (pname == GL_FOG_COLOR)
            {
                this._effect.FogColor = new Vector3(param[0], param[1], param[2]);
            }
        }

        public void GLDeleteTextures(int n, int[] textures, int offset)
        {
            for (int i = 0; i < n; i++)
            {
                int index = textures[i + offset];
                GLDeleteTexture(index);
            }
        }

        public void GLDeleteTexture(int texid)
        {
            Texture2D tex = this._textures.Get(texid);
            if (tex != null)
            {
                tex.Dispose();
                this._textures.Remove(texid);
                this._textureTypes.Remove(tex);
            }
        }

        public int GetCurrentTextureID()
        {
            return _textureId;
        }

        public void GLBindTexture(int texid)
        {
            if (texid != _textureId && _textures.ContainsKey(texid))
            {
                _textureId = texid;
                _effect.Texture = _textures.Get(texid);
            }
        }

        public void GLDepthFunc(int func)
        {
            switch (func)
            {
                case GL_GEQUAL:
                    this._depthFunc = CompareFunction.GreaterEqual;
                    break;

                case GL_ALWAYS:
                    this._depthFunc = CompareFunction.Always;
                    break;

                case GL_EQUAL:
                    this._depthFunc = CompareFunction.Equal;
                    break;
            }
        }

        public void GLDepthMask(bool flag)
        {
            this._depthMask = flag;
        }


        public void DisableColorArray()
        {
            if (_colorArray)
            {
                _colorArray = false;
                _effect.VertexColorEnabled = false;
            }
        }

        public void DisableTextures()
        {
            if (_seTextures)
            {
                _seTextures = false;
                _effect.TextureEnabled = false;
            }
        }
        public void EnableColorArray()
        {
            if (!_colorArray)
            {
                _colorArray = true;
                _effect.VertexColorEnabled = true;
            }
        }
        public void EnableTextures()
        {
            if (!_seTextures)
            {
                _seTextures = true;
                _effect.TextureEnabled = true;
            }
        }


        public Texture2D GetTexture(int idx)
        {
            return _textures.Get(idx);
        }

        public int CreateTexture(Texture2D tex)
        {
            return CreateTexture(tex, null);
        }

        public int CreateTexture(Texture2D tex, TextureType textype)
        {
            int idx = GLGenTexture();
            _textures.Put(idx, tex);
            if (!_textureTypes.ContainsKey(tex))
            {
                if (textype != null)
                {
                    _textureTypes.Add(tex, textype);
                }
                else
                {
                    _textureTypes.Add(tex, new TextureType());
                }
            }
            return idx;
        }

        public int GLGenTexture()
        {
            return _textureCount++;
        }

        public void GLTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int type, ByteBuffer pixels)
        {
            uint[] data = new uint[width * height];
            for (int i = 0; i < data.Length; i++)
            {
                data[i] = (uint)pixels.GetInt();
            }
            Texture2D tex = this._textures.Get(this._textureId);
            if (this._effect.Texture == tex)
            {
                this._effect.Texture = null;
                this._effect.CurrentTechnique.Passes[0].Apply();
            }
            tex.SetData<uint>(0, new Rectangle(xoffset, yoffset, width, height), data, 0, data.Length);
        }

        public void GLVertexPointer(int size, int type, int stride, ByteBuffer pointer)
        {
            this._vertex[4].Set(size, type, stride, pointer);
        }

        public void GLVertices(int primitiveType, int numVertices, VertexPositionColorTexture[] vertices, List<Vector3> vertexsBuffer, List<Vector2> texCoordsBuffer, List<Color> colorsBuffer, bool hasCols, bool hasClear, Color defaultColor)
        {
            if (numVertices <= 0)
            {
                return;
            }
            bool hasCoord = (texCoordsBuffer.Count != 0);
            switch (primitiveType)
            {
                case GL_QUADS:
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

        public void Submit(int count, VertexPositionColorTexture[] vertices, short[] indices)
        {
            Submit(GL_TRIANGLE_FAN, count, vertices, indices, null);
        }

        public void Submit(int primitiveType, int count, VertexPositionColorTexture[] vertices, short[] indices, short[] fan)
        {
            if (count <= 0)
            {
                return;
            }
            bool flag = (_alphaBlendFunction == BlendFunction.ReverseSubtract) && !this._effect.TextureEnabled;
            if (this._alphaTest && (this._effect.Texture != null))
            {
                this._alphaTestEffect.World = this._effect.World;
                this._alphaTestEffect.Projection = this._effect.Projection;
                this._alphaTestEffect.Texture = this._effect.Texture;
                this._alphaTestEffect.CurrentTechnique.Passes[0].Apply();
            }
            else
            {
                this._effect.CurrentTechnique.Passes[0].Apply();
            }
            if (_blendState == null)
            {
                Microsoft.Xna.Framework.Graphics.BlendState blendState = _graphicsDevice.BlendState;
                if (((blendState.AlphaDestinationBlend != this._alphaDestinationBlend) || (blendState.ColorWriteChannels != this._colorWriteChannels)) || (blendState.AlphaBlendFunction != _alphaBlendFunction))
                {
                    blendState = new Microsoft.Xna.Framework.Graphics.BlendState();
                    blendState.ColorSourceBlend = Blend.SourceAlpha;
                    blendState.AlphaSourceBlend = Blend.SourceAlpha;
                    blendState.ColorDestinationBlend = this._alphaDestinationBlend;
                    blendState.AlphaDestinationBlend = this._alphaDestinationBlend;
                    blendState.ColorWriteChannels = this._colorWriteChannels;
                    blendState.ColorWriteChannels1 = ColorWriteChannels.None;
                    blendState.ColorWriteChannels2 = ColorWriteChannels.None;
                    blendState.ColorWriteChannels3 = ColorWriteChannels.None;
                    if (flag)
                    {
                        blendState.ColorDestinationBlend = blendState.AlphaDestinationBlend = Blend.InverseSourceAlpha;
                    }
                    _graphicsDevice.BlendState = blendState;
                }
            }
            else
            {
                _graphicsDevice.BlendState = _blendState;
            }
            if (_seTextures)
            {
                TextureType texType = _textureTypes[_effect.Texture];
                if (texType != null)
                {
                    SamplerState updateState = _graphicsDevice.SamplerStates[0];
                    if (((updateState.AddressU != texType.textureAddressU) || (updateState.AddressV != texType.textureAddressV)) || (updateState.Filter != texType.textureFilter))
                    {
                        updateState = new SamplerState();
                        updateState.AddressU = texType.textureAddressU;
                        updateState.AddressV = texType.textureAddressV;
                        updateState.Filter = texType.textureFilter;
                        _graphicsDevice.SamplerStates[0] = updateState;
                    }
                }
            }

            DepthStencilState depthStencilState = _graphicsDevice.DepthStencilState;
            if (((depthStencilState.DepthBufferEnable != this._depthBuffer) || (depthStencilState.DepthBufferWriteEnable != this._depthMask)) || (depthStencilState.DepthBufferFunction != this._depthFunc))
            {
                depthStencilState = new DepthStencilState();
                depthStencilState.DepthBufferEnable = this._depthBuffer;
                depthStencilState.DepthBufferWriteEnable = this._depthMask;
                depthStencilState.DepthBufferFunction = this._depthFunc;
                _graphicsDevice.DepthStencilState = depthStencilState;
            }

            switch (primitiveType)
            {
                case GL.GL_QUADS:
                case GL_TRIANGLE_FAN:
                    _graphicsDevice.DrawUserIndexedPrimitives<VertexPositionColorTexture>(PrimitiveType.TriangleList, vertices, 0, count * 4, indices, 0, count * 2);
                    break;
                case GL_TRIANGLES:
                    _graphicsDevice.DrawUserIndexedPrimitives<VertexPositionColorTexture>(PrimitiveType.TriangleList, vertices, 0, count, fan, 0, count - 2);
                    break;
                case GL_LINE_LOOP:
                    _graphicsDevice.DrawUserPrimitives<VertexPositionColorTexture>(PrimitiveType.LineStrip, vertices, 0, count);
                    break;
                case GL_LINE_STRIP:
                    _graphicsDevice.DrawUserPrimitives<VertexPositionColorTexture>(PrimitiveType.LineStrip, vertices, 0, count - 1);
                    break;
                case GL_POINTS:
                case GL_LINES:
                    _graphicsDevice.DrawUserPrimitives<VertexPositionColorTexture>(PrimitiveType.LineList, vertices, 0, count);
                    break;
            }
        }

        public void GLAlphaFunc(int func, float r)
        {
            GLAlphaFunc(func, (int)(r * 255f));
        }

        public void GLAlphaFunc(int func, int r)
        {
            switch (func)
            {
                case GL_EQUAL:
                    this._alphaTestEffect.AlphaFunction = CompareFunction.Equal;
                    break;

                case GL_GREATER:
                    this._alphaTestEffect.AlphaFunction = CompareFunction.Greater;
                    break;
            }
            this._alphaTestEffect.ReferenceAlpha = r;
        }

        private void GLUpdate()
        {
            _camera.viewMatrix = _camera.Result;
            switch (this._matrixMode)
            {
                case GL_MODELVIEW:
                    this._effect.World = _camera.viewMatrix;
                    break;

                case GL_PROJECTION:
                    this._effect.Projection = _camera.viewMatrix;
                    break;
            }
        }


        public void GLRotate(float r)
        {
            _camera.Rotation = MathHelper.ToRadians(r);
            GLUpdate();
        }

        public float GetRotation()
        {
            return _camera.Rotation;
        }

        public void GLRotatef(float angle, float x, float y, float z)
        {
            GLTranslate(x, y, 0f);
            _camera.Rotation = MathHelper.ToRadians(angle);
            GLUpdate();
        }

        public void GLTranslate(float x, float y, float z)
        {
            if (x != 0 || y != 0)
            {
                this._camera.SetTranslate(x, y);
                GLUpdate();
            }
        }

        public void GLScale(float inXScale, float inYScale, float inZScale)
        {
            if (((inXScale != 1f) || (inYScale != 1f)) || (inZScale != 1f))
            {
                this._camera.SetScale(inXScale, inYScale);
                GLUpdate();
            }
        }

        public void GLReset()
        {
            _camera.viewMatrix = _camera.Old;
        }

        public void GLLoadIdentity()
        {
            switch (this._matrixMode)
            {
                case GL_MODELVIEW:
                    this._effect.World = Matrix.Identity;
                    break;

                case GL_PROJECTION:
                    this._effect.Projection = Matrix.Identity;
                    break;
            }

        }


        public void GLMatrixMode(int mode)
        {
            this._matrixMode = mode;
        }

        public void GLMultMatrixf(float[] m, int offset)
        {
            Matrix matrix = new Matrix(m[offset], m[offset + 1], m[offset + 2], m[offset + 3], m[offset + 4], m[offset + 5], m[offset + 6], m[offset + 7], m[offset + 8], m[offset + 9], m[offset + 10], m[offset + 11], m[offset + 12], m[offset + 13], m[offset + 14], m[offset + 15]);
            switch (this._matrixMode)
            {
                case GL_MODELVIEW:
                    this._effect.World = Matrix.Multiply(matrix, this._effect.World);
                    break;

                case GL_PROJECTION:
                    this._effect.Projection = Matrix.Multiply(matrix, this._effect.Projection);
                    break;
            }
        }

        public void GLPopMatrix()
        {
            if (_matrixStack.Count > 0)
            {
                _camera.viewMatrix = _camera.Old;
                switch (this._matrixMode)
                {
                    case GL_MODELVIEW:
                        this._effect.World = this._matrixStack.Pop();
                        break;

                    case GL_PROJECTION:
                        this._effect.Projection = this._matrixStack.Pop();
                        break;
                }
            }
        }

        public void GLPushMatrix()
        {
            switch (this._matrixMode)
            {
                case GL_MODELVIEW:
                    this._matrixStack.Push(this._effect.World);
                    break;

                case GL_PROJECTION:
                    this._matrixStack.Push(this._effect.Projection);
                    break;
            }
        }

        public void GLViewport(int x, int y, int width, int height)
        {
            if (width > _device.PresentationParameters.BackBufferWidth)
            {
                width = _device.PresentationParameters.BackBufferWidth;
            }
            if (height > _device.PresentationParameters.BackBufferHeight)
            {
                height = _device.PresentationParameters.BackBufferHeight;
            }
            Viewport view = new Viewport(x, y, width, height);
            _device.Viewport = view;
        }

        public Rectangle GetScissorRect()
        {
            return _scissorRect;
        }

        public void GLScissor(int x, int y, int width, int height)
        {
            if (((x != 0) || (y != 0)) || ((width != _device.PresentationParameters.BackBufferWidth) || (height != _device.PresentationParameters.BackBufferHeight)))
            {
                this._scissorRect.X = x;
                this._scissorRect.Y = y;
                this._scissorRect.Width = width;
                this._scissorRect.Height = height;
                _device.RasterizerState = this._rstateScissor;
                _device.ScissorRectangle = this._scissorRect;
            }
            else
            {
                _device.RasterizerState = RasterizerState.CullNone;
            }
        }

        public string GLGetString(int type)
        {
            return "XNA_GL";
        }
    }
}
