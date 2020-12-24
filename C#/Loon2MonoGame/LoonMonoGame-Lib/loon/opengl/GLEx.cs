using loon.canvas;
using loon.font;
using loon.geom;
using loon.utils;

namespace loon.opengl
{
    public class GLEx : BlendMethod
    {
        private class BrushSave
        {
            internal uint baseColor = LColor.DEF_COLOR;
            internal uint fillColor = LColor.DEF_COLOR;
            internal int pixSkip = def_skip;
            internal float lineWidth = 1f;
            internal float baseAlpha = 1f;
            internal int blend = MODE_NORMAL;

            internal bool alltextures = false;
            internal IFont font = null;
            internal LTexture patternTex = null;

            internal virtual BrushSave Cpy()
            {
                BrushSave save = new BrushSave();
                save.baseColor = this.baseColor;
                save.fillColor = this.fillColor;
                save.pixSkip = this.pixSkip;
                save.lineWidth = this.lineWidth;
                save.alltextures = this.alltextures;
                save.font = this.font;
                save.patternTex = this.patternTex;
                save.blend = this.blend;
                return save;
            }
        }

        public enum Direction
        {
            TRANS_NONE,
            TRANS_MIRROR,
            TRANS_FLIP,
            TRANS_MF
        }

        private const int def_skip = 5;

        private const int def_skip_html5 = 15;

        private bool saveToFrameBufferTexture;

        private LColor tmpColor = new LColor();

        private Vector2f tempLocation = new Vector2f();

        private readonly IntMap<PointF> rhombusArray = new IntMap<PointF>();

        //private readonly Array<LTextureImage> frameBuffers = new Array<LTextureImage>();

        private readonly Array<Affine2f> affineStack = new Array<Affine2f>();

        private readonly Array<BrushSave> brushStack = new Array<BrushSave>();

        private readonly TArray<RectBox> scissors = new TArray<RectBox>();

        private readonly TArray<LTexture> bufferTextures = new TArray<LTexture>();

        private readonly LTexture colorTex;

        protected internal RenderTarget target;

        private int scissorDepth;

        private bool isClosed = false;

        private Graphics gfx;

        private BaseBatch batch;

        private Affine2f lastTrans;

        private BrushSave lastBrush;

        //private LTextureImage lastFrameBuffer;

        private float triangleValue = 0.5235988f;

        private float scaleX = 1f, scaleY = 1f;

        private float offsetStringX = 0, offsetStringY = 0;

        public GLEx(Graphics gfx, RenderTarget target, BaseBatch def, bool alltex, bool saveFrameBuffer)
        {
            this.gfx = gfx;
            this.target = target;
            this.batch = def;
            this.affineStack.Add(lastTrans = new Affine2f());
            this.colorTex = gfx.FinalColorTex();
            if (target != null)
            {
                this.Scale(scaleX = target.Xscale(), scaleY = target.Yscale());
            }
            else
            {
                this.Scale(scaleX = LSystem.GetScaleWidth(), scaleY = LSystem.GetScaleHeight());
            }
            this.lastBrush = new BrushSave();
            this.lastBrush.font = LSystem.GetSystemGameFont();
            this.lastBrush.alltextures = alltex;
            this.lastBrush.pixSkip = LSystem.IsHTML5() ? def_skip_html5 : def_skip;
            this.lastBrush.blend = BlendMethod.MODE_NORMAL;
            this.brushStack.Add(lastBrush);
            this.saveToFrameBufferTexture = saveFrameBuffer;
            this.Update();
        }

        public GLEx(Graphics gfx, RenderTarget target, GL20 gl, bool alltex, bool saveFrameBuffer) : this(gfx, target, CreateDefaultBatch(gl), alltex, saveFrameBuffer)
        {
        }

        public GLEx(Graphics gfx, RenderTarget target, GL20 gl) : this(gfx, target, CreateDefaultBatch(gl), false, false)
        {
        }

        public virtual Graphics Gfx()
        {
            return this.gfx;
        }

        /// <summary>
        /// 启动一系列渲染命令
        /// 
        /// @return
        /// </summary>
        public virtual GLEx Begin()
        {
            if (isClosed)
            {
                return this;
            }
            if (batch == null)
            {
                return this;
            }
            target.Bind();
            BeginBatch(batch);
            //startFrameBuffer();
            return this;
        }

        public virtual GLEx End()
        {
            if (isClosed)
            {
                return this;
            }
            if (batch == null)
            {
                return this;
            }
            //StopFrameBuffer();
            batch.End();
            return this;
        }

        public virtual GLEx Flush()
        {
            if (isClosed)
            {
                return this;
            }
            if (batch == null)
            {
                return this;
            }
            batch.Flush();
            return this;
        }

        public virtual BaseBatch Batch()
        {
            return batch;
        }

        public virtual bool Running()
        {
            if (isClosed)
            {
                return false;
            }
            if (batch == null)
            {
                return false;
            }
            return batch.Running();
        }

        public virtual GLEx ResetFont()
        {
            this.lastBrush.font = LSystem.GetSystemGameFont();
            return this;
        }

        public GLEx ResetColor()
        {
            return SetColor(LColor.DEF_COLOR);
        }


        public virtual GLEx SetAlpha(float alpha)
        {
            // fix alpha
            if (alpha < 0.01f)
            {
                alpha = 0.01f;
                this.lastBrush.baseAlpha = 0;
            }
            else if (alpha > 1f)
            {
                alpha = 1f;
                this.lastBrush.baseAlpha = 1f;
            }
            else
            {
                this.lastBrush.baseAlpha = alpha;
            }
            int ialpha = (int)(0xFF * MathUtils.Clamp(alpha, 0, 1));
            this.lastBrush.baseColor = (uint)((ialpha << 24) | ((int)this.lastBrush.baseColor & 0xFFFFFF));
            return this;
        }

        public virtual GLEx Reset(float red, float green, float blue, float alpha)
        {
            if (isClosed)
            {
                return this;
            }
            GLUtils.SetClearColor(batch.gl, red, green, blue, alpha);
            this.SetFont(LSystem.GetSystemGameFont());
            this.lastBrush.baseColor = LColor.DEF_COLOR;
            this.lastBrush.fillColor = LColor.DEF_COLOR;
            this.lastBrush.baseAlpha = 1f;
            this.lastBrush.patternTex = null;
            this.SetBlendMode(BlendMethod.MODE_NORMAL);
            this.ResetLineWidth();
            return this;
        }

        public virtual GLEx Reset()
        {
            if (isClosed)
            {
                return this;
            }
            tmpColor.SetColor(this.lastBrush.baseColor);
            return Reset(tmpColor.r, tmpColor.g, tmpColor.b, tmpColor.a);
        }

        public virtual int Color()
        {
            return (int)this.lastBrush.baseColor;
        }

        public virtual int GetTint()
        {
            return Color();
        }

        public virtual LColor GetColor()
        {
            return new LColor(this.lastBrush.baseColor);
        }

        public virtual GLEx SetColor(LColor color)
        {
            if (color == null)
            {
                return this;
            }
            int argb = color.GetARGB();
            SetColor(argb);
            return this;
        }

        public virtual GLEx SetColor(int r, int g, int b)
        {
            return SetColor(LColor.GetRGB(r, g, b));
        }

        public virtual GLEx SetColor(int r, int g, int b, int a)
        {
            return SetColor(LColor.GetARGB(r, g, b, a));
        }

        public virtual GLEx SetColor(float r, float g, float b, float a)
        {
            return SetColor(LColor.GetARGB((int)(r > 1 ? r : r * 255), (int)(g > 1 ? g : r * 255), (int)(b > 1 ? b : b * 255), (int)(a > 1 ? a : a * 255)));
        }

        public virtual GLEx SetColor(int c)
        {
            return SetColor((uint)c);
        }

        public virtual GLEx SetColor(uint c)
        {
            this.SetTint(c);
            this.SetAlpha(LColor.GetAlpha(this.lastBrush.baseColor));
            this.lastBrush.fillColor = c;
            this.lastBrush.patternTex = null;
            return this;
        }

        public virtual GLEx SetTint(int r, int g, int b)
        {
            return SetColor(LColor.GetRGB(r, g, b));
        }

        public virtual GLEx SetTint(int r, int g, int b, int a)
        {
            return SetColor(LColor.GetARGB(r, g, b, a));
        }

        public virtual GLEx SetTint(float r, float g, float b, float a)
        {
            return SetColor(LColor.GetARGB((int)(r > 1 ? r : r * 255), (int)(g > 1 ? g : r * 255), (int)(b > 1 ? b : b * 255), (int)(a > 1 ? a : a * 255)));
        }

        public virtual GLEx SetTint(LColor color)
        {
            return this.SetTint(color.GetARGB());
        }

        public virtual GLEx SetTint(int c)
        {
            return SetTint((uint)c);
        }

        public virtual GLEx SetTint(uint c)
        {
            if (this.lastBrush.baseAlpha != 1f)
            {
                this.lastBrush.baseColor = c;
                int ialpha = (int)(0xFF * MathUtils.Clamp(this.lastBrush.baseAlpha, 0, 1));
                this.lastBrush.baseColor = (uint)((ialpha << 24) | ((int)this.lastBrush.baseColor & 0xFFFFFF));
            }
            else
            {
                this.lastBrush.baseColor = c;
            }
            return this;
        }

        public virtual int CombineColor(int c)
        {
            return CombineColor((uint)c);
        }

        public virtual int CombineColor(uint c)
        {
            uint otint = this.lastBrush.baseColor;
            if (c != LColor.DEF_COLOR)
            {
                this.lastBrush.baseColor = otint = (uint)LColor.Combine(c, otint);
            }
            return unchecked((int)otint);
        }

        public virtual GLEx SetFillColor(int color)
        {
            return SetFillColor((uint)color);
        }

        public virtual GLEx SetFillColor(uint color)
        {
            this.lastBrush.fillColor = color;
            this.lastBrush.patternTex = null;
            return this;
        }

        public virtual GLEx SetFillPattern(LTexture texture)
        {
            this.lastBrush.patternTex = texture;
            return this;
        }

        public virtual GLEx Clear()
        {
            return Clear(0, 0, 0, 0);
        }

        public virtual GLEx Clear(float red, float green, float blue, float alpha)
        {
            GLUtils.SetClearColor(batch.gl, red, green, blue, alpha);
            return this;
        }

        public GLEx Clear(LColor color)
        {
            GLUtils.SetClearColor(batch.gl, color);
            return this;
        }

        public virtual GLEx SetFont(IFont font)
        {
            if (font == null)
            {
                return this;
            }
            this.lastBrush.font = font;
            return this;
        }

        public virtual IFont GetFont()
        {
            return this.lastBrush.font;
        }

        public virtual BaseBatch PushBatch(BaseBatch b)
        {
            if (isClosed)
            {
                return null;
            }
            if (b == null)
            {
                return null;
            }
            BaseBatch oldBatch = batch;
            Save();
            batch.End();
            batch = BeginBatch(b);
            return oldBatch;
        }

        public virtual BaseBatch PopBatch(BaseBatch oldBatch)
        {
            if (isClosed)
            {
                return null;
            }
            if (oldBatch != null)
            {
                batch.End();
                batch = BeginBatch(oldBatch);
                Restore();
            }
            return batch;
        }
        public GLEx Update()
        {
            if (isClosed)
            {
                return this;
            }
            if (batch != null)
            {
                GL20 gl = batch.gl;
                // 刷新原始设置
                GLUtils.Reset(gl);
                // 清空背景为黑色
                GLUtils.SetClearColor(gl, LColor.black);
                if (!LSystem.IsHTML5())
                {
                    // 禁用色彩抖动
                    GLUtils.DisableDither(gl);
                    // 禁用深度测试
                    GLUtils.DisableDepthTest(gl);
                    // 禁用双面剪切
                    GLUtils.DisableCulling(gl);
                }
                // 设定画布渲染模式为默认
                this.SetBlendMode(BlendMethod.MODE_NORMAL);
            }
            return this;
        }

        public virtual GLEx SetBlendMode(int mode)
        {
            if (isClosed)
            {
                return this;
            }
            lastBrush.blend = mode;
            GLUtils.SetBlendMode(batch.gl, mode);
            return this;
        }
        public virtual int GetBlendMode()
        {
            return GLUtils.GetBlendMode();
        }

        public virtual float GetAlpha()
        {
            return Alpha();
        }

        public virtual float Alpha()
        {
            return ((this.lastBrush.baseColor >> 24) & 0xFF) / 255f;
        }

        public virtual GLEx InitBatch()
        {
            if (batch != null)
            {
                batch.Init();
            }
            return this;
        }

        private BaseBatch BeginBatch(BaseBatch batch)
        {
            batch.Begin(target.Width(), target.Height(), target.Flip());
            return batch;
        }

        public virtual GLEx DrawScale(Painter texture, float x, float y, float w, float h, LColor color, float scaleX, float scaleY, float rotation)
        {
            if (isClosed)
            {
                return this;
            }
            if (texture == null)
            {
                return this;
            }
            return Draw(texture, x, y, texture.Width(), texture.Height(), 0, 0, texture.Width(), texture.Height(), color, rotation, scaleX, scaleY, null, Direction.TRANS_NONE);
        }

        public virtual GLEx DrawScale(Painter texture, float x, float y, float w, float h, LColor color, float scaleX, float scaleY)
        {
            if (isClosed)
            {
                return this;
            }
            if (texture == null)
            {
                return this;
            }
            return Draw(texture, x, y, texture.Width(), texture.Height(), 0, 0, texture.Width(), texture.Height(), color, 0, null, Direction.TRANS_NONE);
        }

        public virtual GLEx DrawMirrorScale(Painter texture, float x, float y, float w, float h, LColor color, float scaleX, float scaleY, float rotation)
        {
            if (isClosed)
            {
                return this;
            }
            if (texture == null)
            {
                return this;
            }
            return Draw(texture, x, y, w, h, 0, 0, texture.Width(), texture.Height(), color, rotation, scaleX, scaleY, null, Direction.TRANS_MIRROR);
        }

        public virtual GLEx DrawMirrorScale(Painter texture, float x, float y, float w, float h, LColor color, float scaleX, float scaleY)
        {
            if (isClosed)
            {
                return this;
            }
            if (texture == null)
            {
                return this;
            }
            return Draw(texture, x, y, w, h, 0, 0, texture.Width(), texture.Height(), color, 0, scaleX, scaleY, null, Direction.TRANS_MIRROR);
        }

        public virtual GLEx DrawFlipScale(Painter texture, float x, float y, float w, float h, LColor color, float scaleX, float scaleY, float rotation)
        {
            if (isClosed)
            {
                return this;
            }
            if (texture == null)
            {
                return this;
            }
            return Draw(texture, x, y, w, h, 0, 0, texture.Width(), texture.Height(), color, rotation, scaleX, scaleY, null, Direction.TRANS_FLIP);
        }

        public virtual GLEx DrawFlipScale(Painter texture, float x, float y, float w, float h, LColor color, float scaleX, float scaleY)
        {
            if (isClosed)
            {
                return this;
            }
            if (texture == null)
            {
                return this;
            }
            return Draw(texture, x, y, w, h, 0, 0, texture.Width(), texture.Height(), color, 0, scaleX, scaleY, null, Direction.TRANS_FLIP);
        }

        public GLEx Draw(Painter texture, float x, float y, Direction dir)
        {
            if (isClosed)
            {
                return this;
            }
            if (texture == null)
            {
                return this;
            }
            return Draw(texture, x, y, texture.Width(), texture.Height(), 0, 0, texture.Width(), texture.Height(), null, 0, null, dir);
        }

        public GLEx Draw(Painter texture, float x, float y, LColor color, float rotation)
        {
            if (isClosed)
            {
                return this;
            }
            if (texture == null)
            {
                return this;
            }
            return Draw(texture, x, y, texture.Width(), texture.Height(), 0, 0, texture.Width(), texture.Height(), color, rotation, null, default);
        }

        public virtual GLEx Draw(Painter texture, float x, float y)
        {
            if (isClosed)
            {
                return this;
            }
            if (texture == null)
            {
                return this;
            }
            return Draw(texture, x, y, texture.Width(), texture.Height());
        }

        public virtual GLEx Draw(Painter texture, float x, float y, LColor color)
        {
            if (isClosed)
            {
                return this;
            }
            if (texture == null)
            {
                return this;
            }
            return Draw(texture, x, y, texture.Width(), texture.Height(), color);
        }

        public virtual GLEx Draw(Painter texture, float x, float y, float w, float h, LColor color)
        {
            if (isClosed)
            {
                return this;
            }
            if (texture == null)
            {
                return this;
            }
            uint argb = this.lastBrush.baseColor;
            if (color != null)
            {
                argb = (uint)color.GetARGB(Alpha());
            }
            texture.AddToBatch(batch, argb, Tx, x, y, w, h);
            return this;
        }

        public virtual GLEx Draw(Painter texture, float x, float y, float rotation)
        {
            if (isClosed)
            {
                return this;
            }
            if (texture == null)
            {
                return this;
            }
            return Draw(texture, x, y, texture.Width(), texture.Height(), rotation);
        }

        public virtual GLEx Draw(Painter texture, float x, float y, LColor color, float rotation, Vector2f pivot)
        {
            if (isClosed)
            {
                return this;
            }
            if (texture == null)
            {
                return this;
            }
            return Draw(texture, x, y, texture.Width(), texture.Height(), color, rotation, pivot);
        }

        public virtual GLEx Draw(Painter texture, float x, float y, float w, float h, float rotation)
        {
            if (isClosed)
            {
                return this;
            }
            if (texture == null)
            {
                return this;
            }
            Affine2f xf = Tx;
            if (rotation != 0)
            {
                xf = new Affine2f();
                float w1 = x + w / 2;
                float h1 = y + h / 2;
                xf.Translate(w1, h1);
                xf.PreRotate(rotation);
                xf.Translate(-w1, -h1);
                Affine2f.Multiply(Tx, xf, xf);
            }
            texture.AddToBatch(batch, this.lastBrush.baseColor, xf, x, y, w, h);
            return this;
        }

        public virtual GLEx Draw(Painter texture, float x, float y, float w, float h, LColor color, float rotation, Vector2f pivot)
        {
            return Draw(texture, x, y, w, h, color, rotation, pivot, 1f, 1f);
        }

        public virtual GLEx Draw(Painter texture, float x, float y, float w, float h, LColor color, float rotation, Vector2f pivot, float sx, float sy)
        {
            return Draw(texture, x, y, w, h, color, rotation, pivot, sx, sy, false, false);
        }

        public virtual GLEx Draw(Painter texture, float x, float y, float w, float h, LColor color, float rotation, float sx, float sy, bool flipX, bool flipY)
        {
            return Draw(texture, x, y, w, h, color, rotation, null, sx, sy, flipX, flipY);
        }

        public virtual GLEx Draw(Painter texture, float x, float y, float w, float h, LColor color, float rotation, Vector2f pivot, float sx, float sy, bool flipX, bool flipY)
        {
            if (isClosed)
            {
                return this;
            }
            if (texture == null)
            {
                return this;
            }
            uint argb = this.lastBrush.baseColor;
            if (color != null)
            {
                argb = (uint)color.GetARGB(Alpha());
            }
            Affine2f xf = Tx;
            if (rotation != 0 || sx != 1f || sy != 1f || flipX || flipY)
            {
                xf = new Affine2f();
                float centerX = x + w / 2;
                float centerY = y + h / 2;
                if (pivot != null && (pivot.x != -1 && pivot.y != -1))
                {
                    centerX = x + pivot.x;
                    centerX = y + pivot.y;
                }
                if (rotation != 0)
                {
                    xf.Translate(centerX, centerY);
                    xf.PreRotate(rotation);
                    xf.Translate(-centerX, -centerY);
                }
                if (flipX || flipY)
                {
                    if (flipX && flipY)
                    {
                        Affine2f.Transform(xf, x, y, Affine2f.TRANS_ROT180, w, h);
                    }
                    else if (flipX)
                    {
                        Affine2f.Transform(xf, x, y, Affine2f.TRANS_MIRROR, w, h);
                    }
                    else if (flipY)
                    {
                        Affine2f.Transform(xf, x, y, Affine2f.TRANS_MIRROR_ROT180, w, h);
                    }
                }
                if (sx != 1f || sy != 1f)
                {
                    xf.Translate(centerX, centerY);
                    xf.PreScale(sx, sy);
                    xf.Translate(-centerX, -centerY);
                }
                Affine2f.Multiply(Tx, xf, xf);
            }
            texture.AddToBatch(batch, argb, xf, x, y, w, h);
            return this;
        }

        public virtual GLEx Draw(Painter texture, float x, float y, float w, float h, LColor color, float rotation)
        {
            if (isClosed)
            {
                return this;
            }
            if (texture == null)
            {
                return this;
            }
            uint argb = this.lastBrush.baseColor;
            if (color != null)
            {
                argb = (uint)color.GetARGB(Alpha());
            }
            Affine2f xf = Tx;
            if (rotation != 0)
            {
                xf = new Affine2f();
                float w1 = x + w / 2;
                float h1 = y + h / 2;
                xf.Translate(w1, h1);
                xf.PreRotate(rotation);
                xf.Translate(-w1, -h1);
                Affine2f.Multiply(Tx, xf, xf);
            }
            texture.AddToBatch(batch, argb, xf, x, y, w, h);
            return this;
        }

        public virtual GLEx Draw(Painter texture, float x, float y, float w, float h)
        {
            if (isClosed)
            {
                return this;
            }
            if (texture == null)
            {
                return this;
            }
            texture.AddToBatch(batch, this.lastBrush.baseColor, Tx, x, y, w, h);
            return this;
        }

        public virtual GLEx Draw(Painter texture, float dx, float dy, float sx, float sy, float sw, float sh)
        {
            if (isClosed)
            {
                return this;
            }
            if (texture == null)
            {
                return this;
            }
            texture.AddToBatch(batch, this.lastBrush.baseColor, Tx, dx, dy, sw, sh, sx, sy, sw, sh);
            return this;
        }

        public virtual GLEx Draw(Painter texture, float dx, float dy, float dw, float dh, float sx, float sy, float sw, float sh)
        {
            if (isClosed)
            {
                return this;
            }
            if (texture == null)
            {
                return this;
            }
            texture.AddToBatch(batch, this.lastBrush.baseColor, Tx, dx, dy, dw, dh, sx, sy, sw, sh);
            return this;
        }

        public virtual GLEx Draw(Painter texture, float dx, float dy, float dw, float dh, float sx, float sy, float sw, float sh, LColor color)
        {
            if (isClosed)
            {
                return this;
            }
            if (texture == null)
            {
                return this;
            }
            if (LColor.white.Equals(color))
            {
                texture.AddToBatch(batch, this.lastBrush.baseColor, Tx, dx, dy, dw, dh, sx, sy, sw, sh);
                return this;
            }
            uint argb = this.lastBrush.baseColor;
            if (color != null)
            {
                argb = (uint)color.GetARGB(Alpha());
            }
            texture.AddToBatch(batch, argb, Tx, dx, dy, dw, dh, sx, sy, sw, sh);
            return this;
        }

        public virtual GLEx Draw(Painter texture, float dx, float dy, float dw, float dh, float sx, float sy, float sw, float sh, float rotation)
        {
            if (isClosed)
            {
                return this;
            }
            if (texture == null)
            {
                return this;
            }
            if (rotation == 0)
            {
                texture.AddToBatch(batch, this.lastBrush.baseColor, Tx, dx, dy, dw, dh, sx, sy, sw, sh);
                return this;
            }
            Affine2f xf = Tx;
            if (rotation != 0)
            {
                xf = new Affine2f();
                float w1 = dx + dw / 2;
                float h1 = dy + dh / 2;
                xf.Translate(w1, h1);
                xf.PreRotate(rotation);
                xf.Translate(-w1, -h1);
                Affine2f.Multiply(Tx, xf, xf);
            }
            texture.AddToBatch(batch, this.lastBrush.baseColor, xf, dx, dy, dw, dh, sx, sy, sw, sh);
            return this;
        }

        public virtual GLEx DrawFlip(Painter texture, float x, float y)
        {
            if (isClosed)
            {
                return this;
            }
            if (texture == null)
            {
                return this;
            }
            return DrawFlip(texture, x, y, LColor.white);
        }

        public virtual GLEx DrawFlip(Painter texture, float x, float y, LColor color)
        {
            if (isClosed)
            {
                return this;
            }
            if (texture == null)
            {
                return this;
            }
            return Draw(texture, x, y, texture.Width(), texture.Height(), 0, 0, texture.Width(), texture.Height(), color, 0, null, Direction.TRANS_FLIP);
        }

        public virtual GLEx DrawFlip(Painter texture, float x, float y, float w, float h)
        {
            if (isClosed)
            {
                return this;
            }
            if (texture == null)
            {
                return this;
            }
            return Draw(texture, x, y, w, h, 0, 0, texture.Width(), texture.Height(), LColor.white, 0f, null, Direction.TRANS_FLIP);
        }

        public virtual GLEx DrawFlip(Painter texture, float x, float y, float w, float h, LColor color, float rotation)
        {
            if (isClosed)
            {
                return this;
            }
            if (texture == null)
            {
                return this;
            }
            return Draw(texture, x, y, w, h, 0, 0, texture.Width(), texture.Height(), color, rotation, null, Direction.TRANS_FLIP);
        }

        public virtual GLEx DrawFlip(Painter texture, float x, float y, LColor color, float rotation)
        {
            if (isClosed)
            {
                return this;
            }
            if (texture == null)
            {
                return this;
            }
            return Draw(texture, x, y, texture.Width(), texture.Height(), 0, 0, texture.Width(), texture.Height(), color, rotation, null, Direction.TRANS_FLIP);
        }

        public virtual GLEx DrawMirror(Painter texture, float x, float y, LColor color)
        {
            if (isClosed)
            {
                return this;
            }
            if (texture == null)
            {
                return this;
            }
            return Draw(texture, x, y, texture.Width(), texture.Height(), 0, 0, texture.Width(), texture.Height(), color, 0, null, Direction.TRANS_MIRROR);
        }

        public virtual GLEx DrawMirror(Painter texture, float x, float y, LColor color, float rotation)
        {
            if (isClosed)
            {
                return this;
            }
            if (texture == null)
            {
                return this;
            }
            return Draw(texture, x, y, texture.Width(), texture.Height(), 0, 0, texture.Width(), texture.Height(), color, rotation, null, Direction.TRANS_MIRROR);
        }

        public virtual GLEx DrawMirror(Painter texture, float x, float y, float w, float h, float rotation)
        {
            if (isClosed)
            {
                return this;
            }
            if (texture == null)
            {
                return this;
            }
            return Draw(texture, x, y, w, h, 0, 0, texture.Width(), texture.Height(), null, rotation, null, Direction.TRANS_MIRROR);
        }

        public virtual GLEx DrawMirror(Painter texture, float x, float y, float w, float h, LColor color, float rotation)
        {
            if (isClosed)
            {
                return this;
            }
            if (texture == null)
            {
                return this;
            }
            return Draw(texture, x, y, w, h, 0, 0, texture.Width(), texture.Height(), color, rotation, null, Direction.TRANS_MIRROR);
        }

        public virtual GLEx Draw(Painter texture, float x, float y, LColor color, Direction dir)
        {
            if (isClosed)
            {
                return this;
            }
            if (texture == null)
            {
                return this;
            }
            return Draw(texture, x, y, texture.Width(), texture.Height(), 0, 0, texture.Width(), texture.Height(), color, 0, null, dir);
        }

        public virtual GLEx Draw(Painter texture, float x, float y, LColor color, float rotation, Vector2f origin, Direction dir)
        {
            if (isClosed)
            {
                return this;
            }
            if (texture == null)
            {
                return this;
            }
            return Draw(texture, x, y, texture.Width(), texture.Height(), 0, 0, texture.Width(), texture.Height(), color, rotation, origin, dir);
        }

        public virtual GLEx Draw(Painter texture, RectBox destRect, RectBox srcRect, LColor color, float rotation)
        {
            if (rotation == 0)
            {
                return Draw(texture, destRect.x, destRect.y, destRect.width, destRect.height, srcRect.x, srcRect.y, srcRect.width, srcRect.height, color);
            }
            return Draw(texture, destRect.x, destRect.y, destRect.width, destRect.height, srcRect.x, srcRect.y, srcRect.width, srcRect.height, color, rotation, null, default);
        }

        public virtual GLEx Draw(Painter texture, float x, float y, float Width, float Height, float srcX, float srcY, float srcWidth, float srcHeight, LColor c, float rotation)
        {
            if (rotation == 0)
            {
                return Draw(texture, x, y, Width, Height, srcX, srcY, srcWidth, srcHeight, c);
            }
            return Draw(texture, x, y, Width, Height, srcX, srcY, srcWidth, srcHeight, c, rotation, null, default);
        }

        public virtual GLEx Draw(Painter texture, float x, float y, float Width, float Height, float srcX, float srcY, float srcWidth, float srcHeight, LColor color, float rotation, Vector2f origin, Direction dir)
        {
            return Draw(texture, x, y, Width, Height, srcX, srcY, srcWidth, srcHeight, color, rotation, 1f, 1f, origin, dir);
        }

        public virtual GLEx Draw(Painter texture, float x, float y, Vector2f origin, float Width, float Height, float scaleX, float scaleY, float rotation, float srcX, float srcY, float srcWidth, float srcHeight, bool flipX, bool flipY, LColor color)
        {
            if (!flipX && !flipY)
            {
                return Draw(texture, x, y, Width, Height, srcX, srcY, srcWidth, srcHeight, color, rotation, scaleX, scaleY, origin, Direction.TRANS_NONE);
            }
            else if (flipX && !flipY)
            {
                return Draw(texture, x, y, Width, Height, srcX, srcY, srcWidth, srcHeight, color, rotation, scaleX, scaleY, origin, Direction.TRANS_FLIP);
            }
            else if (!flipX && flipY)
            {
                return Draw(texture, x, y, Width, Height, srcX, srcY, srcWidth, srcHeight, color, rotation, scaleX, scaleY, origin, Direction.TRANS_MIRROR);
            }
            else
            {
                return Draw(texture, x, y, Width, Height, srcX, srcY, srcWidth, srcHeight, color, rotation, scaleX, scaleY, origin, Direction.TRANS_MF);
            }
        }

        public virtual GLEx Draw(Painter texture, float x, float y, float Width, float Height, float srcX, float srcY, float srcWidth, float srcHeight, LColor color, float rotation, float scaleX, float scaleY, Vector2f origin, Direction dir)
        {
            return Draw(texture, x, y, Width, Height, srcX, srcY, srcWidth, srcHeight, color, rotation, scaleX, scaleY, origin, null, dir);
        }

        public virtual GLEx Draw(Painter texture, float x, float y, float Width, float Height, float srcX, float srcY, float srcWidth, float srcHeight, LColor color, float rotation, float scaleX, float scaleY, Vector2f origin, Vector2f pivot, Direction dir)
        {
            if (isClosed)
            {
                return this;
            }
            if (texture == null)
            {
                return this;
            }

            Affine2f xf = Tx;

            bool dirDirty = (dir != default && dir != Direction.TRANS_NONE);

            bool rotDirty = (rotation != 0 || pivot != null);

            bool oriDirty = (origin != null && (origin.x != 0 || origin.y != 0));

            bool scaleDirty = !(scaleX == 1 && scaleY == 1);

            if (dirDirty || rotDirty || oriDirty || scaleDirty)
            {
                xf = new Affine2f();
                if (oriDirty)
                {
                    xf.Translate(origin.x, origin.y);
                }
                if (rotDirty)
                {
                    float centerX = x + Width / 2;
                    float centerY = y + Height / 2;
                    if (pivot != null && (pivot.x != -1 && pivot.y != -1))
                    {
                        centerX = x + pivot.x;
                        centerX = y + pivot.y;
                    }
                    xf.Translate(centerX, centerY);
                    xf.PreRotate(rotation);
                    xf.Translate(-centerX, -centerY);
                }
                if (scaleDirty)
                {
                    float centerX = x + Width / 2;
                    float centerY = y + Height / 2;
                    if (pivot != null && (pivot.x != -1 && pivot.y != -1))
                    {
                        centerX = x + pivot.x;
                        centerX = y + pivot.y;
                    }
                    xf.Translate(centerX, centerY);
                    xf.PreScale(scaleX, scaleY);
                    xf.Translate(-centerX, -centerY);
                }
                if (dirDirty)
                {
                    switch (dir)
                    {
                        case loon.opengl.GLEx.Direction.TRANS_MIRROR:
                            Affine2f.Transform(xf, x, y, Affine2f.TRANS_MIRROR, Width, Height);
                            break;
                        case loon.opengl.GLEx.Direction.TRANS_FLIP:
                            Affine2f.Transform(xf, x, y, Affine2f.TRANS_MIRROR_ROT180, Width, Height);
                            break;
                        case loon.opengl.GLEx.Direction.TRANS_MF:
                            Affine2f.Transform(xf, x, y, Affine2f.TRANS_ROT180, Width, Height);
                            break;
                        default:
                            break;
                    }
                }
                Affine2f.Multiply(Tx, xf, xf);
            }

            uint argb = this.lastBrush.baseColor;
            if (color != null)
            {
                argb = (uint)color.GetARGB(Alpha());
            }
            texture.AddToBatch(batch, argb, xf, x, y, Width, Height, srcX, srcY, srcWidth, srcHeight);
            return this;
        }

        public virtual GLEx DrawCentered(Painter texture, float x, float y)
        {
            if (isClosed)
            {
                return this;
            }
            if (texture == null)
            {
                return this;
            }
            return Draw(texture, x - texture.Width() / 2, y - texture.Height() / 2);
        }

        public virtual int GetWidth()
        {
            if (target != null)
            {
                return (int)(target.Width() / target.Xscale());
            }
            return LSystem.viewSize.GetWidth();
        }

        public virtual int GetHeight()
        {
            if (target != null)
            {
                return (int)(target.Height() / target.Yscale());
            }
            return LSystem.viewSize.GetHeight();
        }

        public virtual GLEx Translate(float x, float y)
        {
            lastTrans.Translate(x, y);
            return this;
        }

        public virtual GLEx Scale(float s)
        {
            lastTrans.Scale(s, s);
            return this;
        }

        public virtual GLEx Scale(float sx, float sy)
        {
            lastTrans.Scale(sx, sy);
            return this;
        }

        public virtual GLEx SaveBrush()
        {
            if (isClosed)
            {
                return this;
            }
            if (lastBrush != null)
            {
                brushStack.Add(lastBrush = lastBrush.Cpy());
            }
            return this;
        }

        public virtual GLEx ClearBrushs()
        {
            if (isClosed)
            {
                return this;
            }
            brushStack.Clear();
            return this;
        }

        public virtual GLEx RestoreBrush(int idx)
        {
            if (isClosed)
            {
                return this;
            }
            lastBrush = brushStack.Get(idx);
            if (lastBrush != null)
            {
                this.SetFont(lastBrush.font);
                this.SetLineWidth(lastBrush.lineWidth);
                this.SetBlendMode(lastBrush.blend);
            }
            return this;
        }

        public virtual GLEx RestoreBrush()
        {
            if (isClosed)
            {
                return this;
            }
            lastBrush = brushStack.PreviousPop();
            if (lastBrush != null)
            {
                this.SetFont(lastBrush.font);
                this.SetLineWidth(lastBrush.lineWidth);
                this.SetBlendMode(lastBrush.blend);
            }
            return this;
        }

        public virtual GLEx RestoreBrushDef()
        {
            this.lastBrush.baseAlpha = 1f;
            this.lastBrush.baseColor = LColor.DEF_COLOR;
            this.lastBrush.fillColor = LColor.DEF_COLOR;
            this.lastBrush.patternTex = null;
            this.lastBrush.alltextures = LSystem.IsHTML5();
            //this.SetPixSkip(lastBrush.alltextures ? def_skip_html5 : def_skip);
            this.SetFont(LSystem.GetSystemGameFont());
            this.SetLineWidth(1f);
            this.SetBlendMode(BlendMethod.MODE_NORMAL);
            brushStack.Pop();
            return this;
        }

        public virtual GLEx Save()
        {
            this.SaveTx();
            this.SaveBrush();
            return this;
        }

        public virtual int SaveCount()
        {
            Save();
            int size = affineStack.Size();
            int idx = size > 0 ? size - 1 : 0;
            return idx;
        }

        public virtual GLEx Restore()
        {
            this.RestoreTx();
            this.RestoreBrush();
            return this;
        }

        public virtual GLEx RestoreToCount(int idx)
        {
            idx = MathUtils.Clamp(idx, 0, affineStack.Size() - 1);
            this.RestoreTx(idx);
            this.RestoreBrush(idx);
            return this;
        }

        public virtual GLEx SaveTx()
        {
            if (isClosed)
            {
                return this;
            }
            if (lastTrans != null)
            {
                affineStack.Add(lastTrans = lastTrans.Cpy());
            }
            return this;
        }

        public virtual GLEx ClearTxs()
        {
            if (isClosed)
            {
                return this;
            }
            affineStack.Clear();
            return this;
        }

        public virtual GLEx RestoreTx(int idx)
        {
            if (isClosed)
            {
                return this;
            }
            lastTrans = affineStack.Get(idx);
            return this;
        }

        public virtual GLEx RestoreTx()
        {
            if (isClosed)
            {
                return this;
            }
            lastTrans = affineStack.PreviousPop();
            return this;
        }

        public virtual GLEx RestoreTxDef()
        {
            if (isClosed)
            {
                return this;
            }
            lastTrans = new Affine2f();
            Scale(scaleX, scaleY);
            affineStack.Pop();
            return this;
        }

        public virtual Affine2f Tx
        {
            get
            {
                return lastTrans;
            }
        }

        public virtual GLEx SetLineWidth(float width)
        {
            if (isClosed)
            {
                return this;
            }
            if (width != this.lastBrush.lineWidth)
            {
                this.lastBrush.lineWidth = width;
                batch.gl.GLLineWidth(width);
            }
            return this;
        }

        public virtual GLEx ResetLineWidth()
        {
            if (isClosed)
            {
                return this;
            }
            if (this.lastBrush.lineWidth != 1f)
            {
                batch.gl.GLLineWidth(1f);
                this.lastBrush.lineWidth = 1f;
            }
            return this;
        }

        public static BaseBatch CreateDefaultBatch(GL20 gl)
        {
            return null;
        }

    }
}
