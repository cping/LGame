using Microsoft.Xna.Framework.Graphics;
using Microsoft.Xna.Framework;
using System;
using System.Collections.Generic;
using Loon.Utils;
using Loon.Java;
namespace Loon.Core.Graphics.Opengl
{


    public class LTextureBatch
    {

	public bool quad = true;

	internal static bool isBatchCacheDitry;

    private static System.Collections.Generic.Dictionary<Int32, LTextureBatch> batchPools = new System.Collections.Generic.Dictionary<Int32, LTextureBatch>(
			10);

	public  static void ClearBatchCaches() {
		if (LTextureBatch.isBatchCacheDitry) {
			System.Collections.Generic.Dictionary<Int32, LTextureBatch> batchCaches;
			lock (batchPools) {
				batchCaches = new System.Collections.Generic.Dictionary<Int32, LTextureBatch>(batchPools);
				batchPools.Clear();
			}
			foreach (LTextureBatch bt in batchCaches.Values) {
				if (bt != null) {
					lock (bt) {
						bt.Dispose();
					}
				}
			}
			batchCaches = null;
			LTextureBatch.isBatchCacheDitry = false;
		}
	}

	public  static LTextureBatch BindBatchCache( LTexture texture) {
		return BindBatchCache(0, texture);
	}

	public  static LTextureBatch BindBatchCache( int index,
			 LTexture texture) {
		if (texture == null) {
			return null;
		}
		int texId = texture.textureID;
		return BindBatchCache(index, texId, texture);
	}

	public static LTextureBatch BindBatchCache( object o,
			 int texId,  LTexture texture) {
		return BindBatchCache(o.GetHashCode(), texId, texture);
	}

	public  static LTextureBatch BindBatchCache( int index,
			 int texId,  LTexture texture) {
		if (batchPools.Count > 128) {
			ClearBatchCaches();
		}
		int key = LSystem.Unite(index, texId);
        LTextureBatch pBatch = (LTextureBatch)CollectionUtils.Get(batchPools, key);
		if (pBatch == null) {
			lock (batchPools) {
				pBatch = new LTextureBatch(texture);
                CollectionUtils.Put(batchPools,key, pBatch);
			}
		}
		return pBatch;
	}

	public  static LTextureBatch DisposeBatchCache(int texId) {
		lock (batchPools) {
            LTextureBatch pBatch = (LTextureBatch)CollectionUtils.Remove(batchPools, texId);
			if (pBatch != null) {
				lock (pBatch) {
					pBatch.Dispose();
				}
			}
			return pBatch;
		}
	}

        public class GLCache : LRelease
        {
            internal VertexPositionColorTexture[] m_cache;

            public float x, y;

            internal int batchCount;

            internal bool isColor;

            internal short[] m_quadIndices;

            internal int m_type;

            public GLCache(LTextureBatch batch)
                : this(batch, true)
            {

            }

            public GLCache(LTextureBatch batch, bool Reset)
            {
                if (Reset)
                {
                    batch.InsertVertices();
                }
                this.m_type = batch.batchType;
                this.m_cache = new VertexPositionColorTexture[batch.glbase.GetVerticesSize()];
                Array.Copy(batch.glbase.Vertices, m_cache, batch.glbase.GetVerticesSize());
                this.m_quadIndices = new short[batch.glbase.GetQuadIndicesSize()];
                Array.Copy(batch.glbase.QuadIndices, m_quadIndices, batch.glbase.GetQuadIndicesSize());
                this.batchCount = batch.batchCount;
                this.isColor = batch.isColor;
                this.x = batch.moveX;
                this.y = batch.moveY;
            }

            public void Dispose()
            {
                if (m_cache != null)
                {
                    this.m_cache = null;
                }
                if (m_quadIndices != null)
                {
                    this.m_quadIndices = null;
                }
            }
        }


        private LTexture textureBuffer;

        private float moveX, moveY;

        private int batchType;

        private int ver, col, tex,maxCount;

        private int texWidth, texHeight;

        private float xOff, yOff, widthRatio, heightRatio;

        private float drawWidth, drawHeight;

        private float textureSrcX, textureSrcY;

        private float srcWidth, srcHeight;

        private float renderWidth, renderHeight;

        internal bool useBegin, lockCoord, isColor, isLocked;

        float invTexWidth;

        float invTexHeight;

        private const int DEFAULT_MAX_VERTICES = 2048;

        internal Color default_color = Color.White;

        private int batchCount;

        public void Lock()
        {
            this.isLocked = true;
        }

        public void UnLock()
        {
            this.isLocked = false;
        }

        public void GLCacheCommit()
        {
            if (batchCount == 0)
            {
                return;
            }
            if (isLocked)
            {
                Submit();
            }
        }

        private void InsertVertices()
        {
            if (isLocked)
            {
                return;
            }
            glbase.Transform(this.batchType, this.batchCount, this.vectors, this.coords, this.colors, this.isColor, true, this.default_color);
        }

        public void SetTexture(LTexture tex2d)
        {
            if (!tex2d.isLoaded)
            {
                tex2d.LoadTexture();
            }
            this.textureBuffer = tex2d;
            this.texWidth = textureBuffer.width;
            this.texHeight = textureBuffer.height;
            this.invTexWidth = (1f / texWidth) * textureBuffer.widthRatio;
            this.invTexHeight = (1f / texHeight) * textureBuffer.heightRatio;
        }

        public LTextureBatch(LTexture tex2d)
            : this(tex2d, DEFAULT_MAX_VERTICES)
        {

        }

        public LTextureBatch(LTexture tex2d, int batchCount)
        {
            if (tex2d != null)
            {
                this.SetTexture(tex2d);
            }
            this.isLocked = false;
            this.Make(batchCount);
        }

        public LTextureBatch(GLBatch batch)
        {
            this.isLocked = false;
            this.Make(batch);
        }

        private List<Vector3> vectors;

        private List<Vector2> coords;

        private List<Color> colors;

        private GraphicsDevice device;

        private GLBase glbase;

        private void Make(GLBatch batch)
        {
            device = GL.device;
            if (vectors == null)
            {
                ver = batch.GetMaxVertices();
                vectors = new List<Vector3>(ver);
            }
            if (colors == null)
            {
                col = batch.GetMaxVertices() / 4;
                colors = new List<Color>(col);
            }
            if (coords == null)
            {
                tex = batch.GetMaxVertices() / 2;
                coords = new List<Vector2>(tex);
            }
            this.glbase = batch.Base;
            this.maxCount = batch.GetMaxVertices();
        }

        private void Make(int size)
        {
            device = GL.device;
            if (vectors == null)
            {
                ver = size;
                vectors = new List<Vector3>(ver);
            }
            if (colors == null)
            {
                col = size / 4;
                colors = new List<Color>(col);
            }
            if (coords == null)
            {
                tex = size / 2;
                coords = new List<Vector2>(tex);
            }
            this.glbase = new GLBase(size);
            this.maxCount = size;
        }

        public void GLBegin()
        {
            GLBegin(GL.GL_TRIANGLE_FAN);
        }

        protected internal bool ClearBatch = true;

        public void GLBegin(int type)
        {
            this.batchType = type;
            this.useBegin = true;
            this.isColor = false;
            if (!isLocked)
            {
                if (ClearBatch)
                {
                    this.glbase.Clear(batchCount);
                }
                this.batchCount = 0;
            }
        }

        public bool UseBegin
        {
            get
            {
                return useBegin;
            }
        }

        private void Submit()
        {
            GL gl = GLEx.GL;
            gl.GLBind(textureBuffer);
            if (moveX != 0 || moveY != 0)
            {
                gl.GLPushMatrix();
                gl.GLTranslate(moveX, moveY, 0);
            }
            glbase.Send(batchType, batchCount);
            if (moveX != 0 || moveY != 0)
            {
                gl.GLPopMatrix();
            }
        }

        public void GLEnd()
        {
            if (this.batchCount == 0 || !useBegin)
            {
                this.useBegin = false;
                return;
            }
            this.InsertVertices();
            this.Submit();
            useBegin = false;
        }

        public void GLColor4f(LColor c)
        {
            colors.Add(c);
            isColor = true;
        }

        public void GLColor4f(float r, float g, float b, float a)
        {
            colors.Add(new Color(r, g, b, a));
            isColor = true;
        }

        public void GLTexCoord2f(float fcol, float frow)
        {
            coords.Add(new Vector2(fcol, frow));
        }

        public void GLVertex3f(float x, float y, float z)
        {
            vectors.Add(new Vector3(x, y, z));
            batchCount++;
            if (batchCount >= GetLimit(maxCount))
            {
                GLEnd();
            }
        }

        private int GetLimit(int count)
        {
            switch (batchType)
            {
                case GL.GL_TRIANGLES:
                    return count / 3 ;
                case GL.GL_LINES:
                    return count / 2;
                case GL.GL_QUADS:
                case GL.GL_TRIANGLE_FAN:
                    return count / 4;
            }
            return count;
        }

        private LColor[] image_colors;

        public void SetImageColor(float r, float g, float b, float a)
        {
            SetColor(LTexture.TOP_LEFT, r, g, b, a);
            SetColor(LTexture.TOP_RIGHT, r, g, b, a);
            SetColor(LTexture.BOTTOM_LEFT, r, g, b, a);
            SetColor(LTexture.BOTTOM_RIGHT, r, g, b, a);
        }

        public void SetImageColor(float r, float g, float b)
        {
            SetColor(LTexture.TOP_LEFT, r, g, b);
            SetColor(LTexture.TOP_RIGHT, r, g, b);
            SetColor(LTexture.BOTTOM_LEFT, r, g, b);
            SetColor(LTexture.BOTTOM_RIGHT, r, g, b);
        }

        public void SetImageColor(LColor c)
        {
            if (c == null)
            {
                return;
            }
            if (image_colors == null)
            {
                image_colors = new LColor[] { new LColor(1f, 1f, 1f, 1f),
					new LColor(1f, 1f, 1f, 1f), new LColor(1f, 1f, 1f, 1f),
					new LColor(1f, 1f, 1f, 1f) };
            }
            image_colors[LTexture.TOP_LEFT] = c;
            image_colors[LTexture.TOP_RIGHT] = c;
            image_colors[LTexture.BOTTOM_LEFT] = c;
            image_colors[LTexture.BOTTOM_RIGHT] = c;
        }


        public void SetColor(int corner, float r, float g, float b, float a)
        {
            if (image_colors == null)
            {
                image_colors = new LColor[] { new LColor(1f, 1f, 1f, 1f),
					new LColor(1f, 1f, 1f, 1f), new LColor(1f, 1f, 1f, 1f),
					new LColor(1f, 1f, 1f, 1f) };
            }
            image_colors[corner].SetFloatColor(r, g, b, a);

        }

        public void SetColor(int corner, float r, float g, float b)
        {
            if (image_colors == null)
            {
                image_colors = new LColor[] { new LColor(1f, 1f, 1f, 1f),
					new LColor(1f, 1f, 1f, 1f), new LColor(1f, 1f, 1f, 1f),
					new LColor(1f, 1f, 1f, 1f) };
            }
            image_colors[corner].SetFloatColor(r, g, b, 1f);
        }

        public void DrawQuad(float drawX, float drawY, float drawX2, float drawY2,
                float srcX, float srcY, float srcX2, float srcY2)
        {

            drawWidth = drawX2 - drawX;
            drawHeight = drawY2 - drawY;
            textureSrcX = ((srcX / texWidth) * textureBuffer.widthRatio) + textureBuffer.xOff;
            textureSrcY = ((srcY / texHeight) * textureBuffer.heightRatio) + textureBuffer.yOff;
            srcWidth = srcX2 - srcX;
            srcHeight = srcY2 - srcY;
            renderWidth = ((srcWidth / texWidth) * textureBuffer.widthRatio);
            renderHeight = ((srcHeight / texHeight) * textureBuffer.heightRatio);

            GLTexCoord2f(textureSrcX, textureSrcY);
            GLVertex3f(drawX, drawY, 0);
            GLTexCoord2f(textureSrcX, textureSrcY + renderHeight);
            GLVertex3f(drawX, drawY + drawHeight, 0);
            GLTexCoord2f(textureSrcX + renderWidth, textureSrcY + renderHeight);
            GLVertex3f(drawX + drawWidth, drawY + drawHeight, 0);
            GLTexCoord2f(textureSrcX + renderWidth, textureSrcY);
            GLVertex3f(drawX + drawWidth, drawY, 0);
        }

        public void Draw(float x, float y)
        {
            Draw(image_colors, x, y, textureBuffer.width, textureBuffer.height, 0, 0, textureBuffer.width,
                    textureBuffer.height);
        }

        public void Draw(float x, float y, float width, float height)
        {
            Draw(image_colors, x, y, width, height, 0, 0, textureBuffer.width, textureBuffer.height);
        }

        public void Draw(float x, float y, float width, float height, float srcX,
                float srcY, float srcWidth, float srcHeight)
        {
            Draw(image_colors, x, y, width, height, srcX, srcY, srcWidth, srcHeight);
        }

        public void Draw(LColor[] colors, float x, float y, float width,
                float height)
        {
            Draw(colors, x, y, width, height, 0, 0, textureBuffer.width, textureBuffer.height);
        }

        public void Draw(LColor[] colors, float x, float y, float width,
                float height, float srcX, float srcY, float srcWidth,
                float srcHeight)
        {
            if (!useBegin)
            {
                return;
            }
            if (isLocked)
            {
                return;
            }

            xOff = srcX * invTexWidth + textureBuffer.xOff;
            yOff = srcY * invTexHeight + textureBuffer.yOff;
            widthRatio = srcWidth * invTexWidth;
            heightRatio = srcHeight * invTexHeight;

            float fx2 = x + width;
            float fy2 = y + height;

            if (colors == null)
            {
                GLTexCoord2f(xOff, yOff);
                GLVertex3f(x, y, 0);
                GLTexCoord2f(xOff, heightRatio);
                GLVertex3f(x, fy2, 0);
                GLTexCoord2f(widthRatio, heightRatio);
                GLVertex3f(fx2, fy2, 0);
                GLTexCoord2f(widthRatio, yOff);
                GLVertex3f(fx2, y, 0);
            }
            else
            {
                isColor = true;
                GLColor4f(colors[LTexture.TOP_LEFT]);
                GLTexCoord2f(xOff, yOff);
                GLVertex3f(x, y, 0);
                GLColor4f(colors[LTexture.BOTTOM_LEFT]);
                GLTexCoord2f(xOff, heightRatio);
                GLVertex3f(x, fy2, 0);
                GLColor4f(colors[LTexture.BOTTOM_RIGHT]);
                GLTexCoord2f(widthRatio, heightRatio);
                GLVertex3f(fx2, fy2, 0);
                GLColor4f(colors[LTexture.TOP_RIGHT]);
                GLTexCoord2f(widthRatio, yOff);
                GLVertex3f(fx2, y, 0);
            }
        }

        public void Draw(LColor[] colors, float x, float y, float rotation)
        {
            Draw(colors, x, y, textureBuffer.width / 2, textureBuffer.height / 2,
                    textureBuffer.width, textureBuffer.height, 1f, 1f, rotation, 0,
                    0, textureBuffer.width, textureBuffer.height, false, false);
        }

        public void Draw(LColor[] colors, float x, float y, float width,
                float height, float rotation)
        {
            Draw(colors, x, y, textureBuffer.width / 2, textureBuffer.height / 2,
                    width, height, 1f, 1f, rotation, 0, 0, textureBuffer.width,
                    textureBuffer.height, false, false);
        }

        public void Draw(LColor[] colors, float x, float y, float srcX, float srcY,
                float srcWidth, float srcHeight, float rotation)
        {
            Draw(colors, x, y, textureBuffer.width / 2, textureBuffer.height / 2,
                    textureBuffer.width, textureBuffer.height, 1f, 1f, rotation,
                    srcX, srcY, srcWidth, srcHeight, false, false);
        }

        public void Draw(LColor[] colors, float x, float y, float width,
                float height, float srcX, float srcY, float srcWidth,
                float srcHeight, float rotation)
        {
            Draw(colors, x, y, width / 2, height / 2, width, height, 1f, 1f,
                    rotation, srcX, srcY, srcWidth, srcHeight, false, false);
        }

        public void Draw(float x, float y, float originX,
                float originY, float width, float height, float scaleX,
                float scaleY, float rotation, float srcX, float srcY,
                float srcWidth, float srcHeight, bool flipX, bool flipY)
        {
            Draw(image_colors, x, y, originX, originY, width, height, scaleX, scaleY,
                    rotation, srcX, srcY, srcWidth, srcHeight, flipX, flipY);
        }

        public void Draw(LColor[] colors, float x, float y, float originX,
                float originY, float width, float height, float scaleX,
                float scaleY, float rotation, float srcX, float srcY,
                float srcWidth, float srcHeight, bool flipX, bool flipY)
        {

            float worldOriginX = x + originX;
            float worldOriginY = y + originY;
            float fx = -originX;
            float fy = -originY;
            float fx2 = width - originX;
            float fy2 = height - originY;

            if (scaleX != 1 || scaleY != 1)
            {
                fx *= scaleX;
                fy *= scaleY;
                fx2 *= scaleX;
                fy2 *= scaleY;
            }

            float p1x = fx;
            float p1y = fy;
            float p2x = fx;
            float p2y = fy2;
            float p3x = fx2;
            float p3y = fy2;
            float p4x = fx2;
            float p4y = fy;

            float x1;
            float y1;
            float x2;
            float y2;
            float x3;
            float y3;
            float x4;
            float y4;

            if (rotation != 0)
            {
                float cos = MathUtils.CosDeg(rotation);
                float sin = MathUtils.SinDeg(rotation);

                x1 = cos * p1x - sin * p1y;
                y1 = sin * p1x + cos * p1y;

                x2 = cos * p2x - sin * p2y;
                y2 = sin * p2x + cos * p2y;

                x3 = cos * p3x - sin * p3y;
                y3 = sin * p3x + cos * p3y;

                x4 = x1 + (x3 - x2);
                y4 = y3 - (y2 - y1);
            }
            else
            {
                x1 = p1x;
                y1 = p1y;

                x2 = p2x;
                y2 = p2y;

                x3 = p3x;
                y3 = p3y;

                x4 = p4x;
                y4 = p4y;
            }

            x1 += worldOriginX;
            y1 += worldOriginY;
            x2 += worldOriginX;
            y2 += worldOriginY;
            x3 += worldOriginX;
            y3 += worldOriginY;
            x4 += worldOriginX;
            y4 += worldOriginY;

            xOff = srcX * invTexWidth + textureBuffer.xOff;
            yOff = srcY * invTexHeight + textureBuffer.yOff;
            widthRatio = srcWidth * invTexWidth;
            heightRatio = srcHeight * invTexHeight;

            if (flipX)
            {
                float tmp = xOff;
                xOff = widthRatio;
                widthRatio = tmp;
            }

            if (flipY)
            {
                float tmp = yOff;
                yOff = heightRatio;
                heightRatio = tmp;
            }

            if (colors == null)
            {
                GLTexCoord2f(xOff, yOff);
                GLVertex3f(x1, y1, 0);
                GLTexCoord2f(xOff, heightRatio);
                GLVertex3f(x2, y2, 0);
                GLTexCoord2f(widthRatio, heightRatio);
                GLVertex3f(x3, y3, 0);
                GLTexCoord2f(widthRatio, yOff);
                GLVertex3f(x4, y4, 0);
            }
            else
            {
                isColor = true;
                GLColor4f(colors[LTexture.TOP_LEFT]);
                GLTexCoord2f(xOff, yOff);
                GLVertex3f(x1, y1, 0);
                GLColor4f(colors[LTexture.BOTTOM_LEFT]);
                GLTexCoord2f(xOff, heightRatio);
                GLVertex3f(x2, y2, 0);
                GLColor4f(colors[LTexture.BOTTOM_RIGHT]);
                GLTexCoord2f(widthRatio, heightRatio);
                GLVertex3f(x3, y3, 0);
                GLColor4f(colors[LTexture.TOP_RIGHT]);
                GLTexCoord2f(widthRatio, yOff);
                GLVertex3f(x4, y4, 0);
            }

        }

        public void Draw(float x, float y, float width,
                float height, float srcX, float srcY, float srcWidth,
                float srcHeight, bool flipX, bool flipY)
        {
            Draw(image_colors, x, y, width, height, srcX, srcY, srcWidth, srcHeight, flipX, flipY);
        }

        public void Draw(LColor[] colors, float x, float y, float width,
                float height, float srcX, float srcY, float srcWidth,
                float srcHeight, bool flipX, bool flipY)
        {

            xOff = srcX * invTexWidth + textureBuffer.xOff;
            yOff = srcY * invTexHeight + textureBuffer.yOff;
            widthRatio = srcWidth * invTexWidth;
            heightRatio = srcHeight * invTexHeight;

            float fx2 = x + width;
            float fy2 = y + height;

            if (flipX)
            {
                float tmp = xOff;
                xOff = widthRatio;
                widthRatio = tmp;
            }

            if (flipY)
            {
                float tmp = yOff;
                yOff = heightRatio;
                heightRatio = tmp;
            }

            if (colors == null)
            {
                GLTexCoord2f(xOff, yOff);
                GLVertex3f(x, y, 0);
                GLTexCoord2f(xOff, heightRatio);
                GLVertex3f(x, fy2, 0);
                GLTexCoord2f(widthRatio, heightRatio);
                GLVertex3f(fx2, fx2, 0);
                GLTexCoord2f(widthRatio, yOff);
                GLVertex3f(fx2, y, 0);
            }
            else
            {
                isColor = true;
                GLColor4f(colors[LTexture.TOP_LEFT]);
                GLTexCoord2f(xOff, yOff);
                GLVertex3f(x, y, 0);
                GLColor4f(colors[LTexture.BOTTOM_LEFT]);
                GLTexCoord2f(xOff, heightRatio);
                GLVertex3f(x, fy2, 0);
                GLColor4f(colors[LTexture.BOTTOM_RIGHT]);
                GLTexCoord2f(widthRatio, heightRatio);
                GLVertex3f(fx2, fx2, 0);
                GLColor4f(colors[LTexture.TOP_RIGHT]);
                GLTexCoord2f(widthRatio, yOff);
                GLVertex3f(fx2, y, 0);
            }
        }


        public void Draw(float x, float y, LColor[] c)
        {
            Draw(c, x, y, textureBuffer.width, textureBuffer.height);
        }

        public void Draw(float x, float y, LColor c)
        {
            bool update = CheckUpdateColor(c);
            if (update)
            {
                SetImageColor(c);
            }
            Draw(image_colors, x, y, textureBuffer.width, textureBuffer.height);
            if (update)
            {
                SetImageColor(LColor.white);
            }
        }

        public void Draw(float x, float y, float width, float height, LColor c)
        {
            bool update = CheckUpdateColor(c);
            if (update)
            {
                SetImageColor(c);
            }
            Draw(image_colors, x, y, width, height);
            if (update)
            {
                SetImageColor(LColor.white);
            }
        }

        public void Draw(float x, float y, float width, float height, float x1,
                float y1, float x2, float y2, LColor[] c)
        {
            Draw(c, x, y, width, height, x1, y1, x2, y2);
        }

        public void Draw(float x, float y, float width, float height, float x1,
                float y1, float x2, float y2, LColor c)
        {
            bool update = CheckUpdateColor(c);
            if (update)
            {
                SetImageColor(c);
            }
            Draw(image_colors, x, y, width, height, x1, y1, x2, y2);
            if (update)
            {
                SetImageColor(LColor.white);
            }
        }

        public void Draw(float x, float y, float w, float h, float rotation,
                LColor c)
        {
            bool update = CheckUpdateColor(c);
            if (update)
            {
                SetImageColor(c);
            }
            Draw(image_colors, x, y, w, h, rotation);
            if (update)
            {
                SetImageColor(LColor.white);
            }
        }

        private bool CheckUpdateColor(LColor c)
        {
            return c != null && !LColor.white.Equals(c);
        }

        public LTexture GetTexture()
        {
            return textureBuffer;
        }

        public int GetHeight()
        {
            return texHeight;
        }

        public int GetWidth()
        {
            return texWidth;
        }

        public float GetX()
        {
            return moveX;
        }

        public void SetX(float x)
        {
            this.moveX = x;
        }

        public float GetY()
        {
            return moveY;
        }

        public void SetY(float y)
        {
            this.moveY = y;
        }

        public void SetLocation(float x, float y)
        {
            this.moveX = x;
            this.moveY = y;
        }

        public bool IsLockCoord()
        {
            return lockCoord;
        }

        public void SetLockCoord(bool lockCoord)
        {
            this.lockCoord = lockCoord;
        }

        private GLCache m_lastCache;

        public void PostLastCache()
        {
            if (m_lastCache != null)
            {
                LTextureBatch.Commit(textureBuffer, m_lastCache);
            }
        }

        public static void Commit(LTexture tex2d, GLCache cache)
        {
            Commit(tex2d, cache, true);
        }

        public static void Commit(LTexture tex2d, GLCache cache, bool update)
        {
            if (cache.batchCount == 0)
            {
                return;
            }
            GL gl = GLEx.GL;
            gl.GLBind(tex2d);
            if (update)
            {
                if (cache.x != 0 || cache.y != 0)
                {
                    gl.GLPushMatrix();
                    gl.GLTranslate(cache.x, cache.y, 0);
                }
            }
            GLEx.GL.Submit(cache.m_type, cache.batchCount, cache.m_cache, cache.m_quadIndices, null);
            if (update)
            {
                if (cache.x != 0 || cache.y != 0)
                {
                    gl.GLPopMatrix();
                }
            }
        }

        public void CommitQuad(LColor c, float x, float y, float sx, float sy,
        float ax, float ay, float rotation)
        {
            if (batchCount == 0 || !useBegin)
            {
                this.useBegin = false;
                return;
            }
            this.isColor = false;
            if (c != null)
            {
                default_color = c;
            }
            this.InsertVertices();
            GL gl = GLEx.GL;
            gl.GLBind(textureBuffer);
            gl.GLPushMatrix();
            if (x != 0 || y != 0)
            {
                gl.GLTranslate(x, y, 0);
            }
            if (sx != 0 || sx != 0)
            {
                gl.GLScale(sx, sy, 0);
            }
            if (rotation != 0)
            {
                if (ax != 0 || ay != 0)
                {
                    gl.GLTranslate(ax, ay, 0f);
                    gl.GLRotate(rotation);
                    gl.GLTranslate(-ax, -ay, 0f);
                }
                else
                {
                    gl.GLTranslate(textureBuffer.width / 2,
                            textureBuffer.height / 2, 0f);
                    gl.GLRotate(rotation);
                    gl.GLTranslate(-textureBuffer.width / 2,
                            -textureBuffer.height / 2, 0f);
                }
            }
            glbase.Send(batchType,batchCount);
            gl.GLPopMatrix();
        }

        public static void CommitQuad(LTexture tex2d, GLCache cache,
        LColor c, float x, float y, float sx, float sy, float ax, float ay,
        float rotation)
        {
            if (cache.batchCount == 0)
            {
                return;
            }
            GL gl = GLEx.GL;
            gl.GLBind(tex2d);
            gl.GLPushMatrix();
            if (x != 0 || y != 0)
            {
                gl.GLTranslate(x, y, 0);
            }
            if (sx != 0 || sx != 0)
            {
                gl.GLScale(sx, sy, 0);
            }
            if (rotation != 0)
            {
                if (ax != 0 || ay != 0)
                {
                    gl.GLTranslate(ax, ay, 0f);
                    gl.GLRotate(rotation);
                    gl.GLTranslate(-ax, -ay, 0f);
                }
                else
                {
                    gl.GLTranslate(tex2d.width / 2,
                            tex2d.height / 2, 0f);
                    gl.GLRotate(rotation);
                    gl.GLTranslate(-tex2d.width / 2,
                           -tex2d.height / 2, 0f);
                }
            }
            GLEx.GL.Submit(cache.m_type, cache.batchCount, cache.m_cache, cache.m_quadIndices, null);
            gl.GLPopMatrix();
        }

        public GLCache GetLastCache()
        {
            return m_lastCache;
        }

        public GLCache NewGLCache(bool Reset)
        {
            return m_lastCache = new GLCache(this, Reset);
        }

        public GLCache NewGLCache()
        {
            return NewGLCache(false);
        }

        public void DisposeLastCache()
        {
            if (m_lastCache != null)
            {
                m_lastCache.Dispose();
                m_lastCache = null;
            }
        }

        public void DestoryAll()
        {
            Dispose();
            Destroy();
        }

        public void Destroy()
        {
            if (textureBuffer != null)
            {
                textureBuffer.Destroy();
            }
        }

        public void Dispose()
        {
            this.batchCount = 0;
            this.useBegin = false;
            this.isLocked = true;
            this.vectors = null;
            this.colors = null;
            this.coords = null;
            if (glbase != null)
            {
                this.glbase.Dispose();
                this.glbase = null;
            }
            if (m_lastCache != null)
            {
                m_lastCache.Dispose();
                m_lastCache = null;
            }
            if (image_colors != null)
            {
                image_colors = null;
            }
        }

    }
}
