using Microsoft.Xna.Framework.Graphics;
using Loon.Java;
using Loon.Utils;
using System;
using System.Runtime.CompilerServices;
using Loon.Core.Event;
using Loon.Core.Geom;
using Loon.Action.Collision;
namespace Loon.Core.Graphics.Opengl
{

    public class LTexture
    {
        public class Mask : LRelease
        {
            public void Dispose()
            {
                data = null;
            }

            private int height;

            private int width;

            private bool[][] data;

            public Mask(int width, int height)
            {
                this.width = width;
                this.height = height;
            }

            public Mask(bool[][] data, int width, int height)
            {
                this.data = data;
                this.width = width;
                this.height = height;
            }

            public bool[][] getData()
            {
                return data;
            }

            public bool GetPixel(int x, int y)
            {
                if (x < 0 || x >= width || y < 0 || y >= height)
                {
                    return false;
                }
                return data[y][x];
            }

            public void SetData(bool[][] data)
            {
                this.data = data;
            }

            public int GetWidth()
            {
                return width;
            }

            public int GetHeight()
            {
                return height;
            }


        }


        public object Tag;

        internal int refCount = 0;

        private LTextureBatch batch;

        protected internal bool isExt;

        private bool isBatch;

        public LTextureData imageData;

        private int subX, subY, subWidth, subHeight;

        public const int TOP_LEFT = 0;

        public const int TOP_RIGHT = 1;

        public const int BOTTOM_RIGHT = 2;

        public const int BOTTOM_LEFT = 3;

        public static bool ALL_LINEAR = false;

        public static bool ALL_NEAREST = false;

        public static void AUTO_LINEAR()
        {
            if (LSystem.scaleWidth != 1 || LSystem.scaleHeight != 1)
            {
                LTexture.ALL_LINEAR = true;
            }
        }

        public static void AUTO_NEAREST()
        {
            if (LSystem.scaleWidth != 1 || LSystem.scaleHeight != 1)
            {
                LTexture.ALL_NEAREST = true;
            }
        }

        public enum Format
        {
            DEFAULT, NEAREST, LINEAR, SPEED, STATIC, FONT, BILINEAR, REPEATING, REPEATING_BILINEAR, REPEATING_BILINEAR_PREMULTIPLYALPHA
        }

        internal bool replace, reload;

        internal bool isLoaded, isClose, hasAlpha;

        internal bool isVisible = true;

        internal bool isChild;

        internal int width, texWidth;

        internal int height, texHeight;

        internal int textureID, bufferID;

        public float xOff = 0.0f;

        public float yOff = 0.0f;

        public float widthRatio = 1.0f;

        public float heightRatio = 1.0f;

        int[] crops = { 0, 0, 0, 0 };

        float[] dataCords;

        internal int vertexSize;

        internal int texSize;

        internal Format format;

        internal string lazyName;

        internal bool isStatic;

        internal LTexture parent;

        private LColor[] colors;

        internal System.Collections.Generic.Dictionary<Int32, LTexture> childs;

        public int GetTextureID()
        {
            return textureID;
        }

        public LTexture(LTexture texture)
        {
            if (texture == null)
            {
                throw new RuntimeException("texture is Null !");
            }
            this.imageData = texture.imageData;
            this.parent = texture.parent;
            this.format = texture.format;
            if (texture.colors != null)
            {
                this.colors = (LColor[])CollectionUtils.CopyOf(texture.colors);
            }
            if (texture.dataCords != null)
            {
                this.dataCords = (float[])CollectionUtils
                        .CopyOf(texture.dataCords);
            }
            this.hasAlpha = texture.hasAlpha;
            this.textureID = texture.textureID;
            this.bufferID = texture.bufferID;
            this.width = texture.width;
            this.height = texture.height;
            this.parent = texture.parent;
            this.childs = texture.childs;
            this.texWidth = texture.texWidth;
            this.texHeight = texture.texHeight;
            this.xOff = texture.xOff;
            this.yOff = texture.yOff;
            this.widthRatio = texture.widthRatio;
            this.heightRatio = texture.heightRatio;
            this.replace = texture.replace;
            this.isLoaded = texture.isLoaded;
            this.isClose = texture.isClose;
            this.isStatic = texture.isStatic;
            this.isVisible = texture.isVisible;
            Array.Copy(texture.crops, 0, crops, 0, crops.Length);
        }

        private LTexture()
        {
            format = Format.DEFAULT;
            imageData = null;
            CheckReplace();
        }

        public LTexture(string res, Format format)
            : this(GLLoader.GetTextureData(res), format)
        {

        }

        public LTexture(LImage pix)
            : this(pix, Format.DEFAULT)
        {

        }

        public LTexture(LImage pix, Format format)
            : this(GLLoader.GetTextureData(pix), format)
        {

        }

        public LTexture(int width, int height, Format format)
            : this(width, height, true, format)
        {

        }

        public LTexture(int width, int height, bool hasAlpha, Format format)
            : this(LImage.CreateImage(width, height, hasAlpha), format)
        {

        }

        public LTexture(int width, int height, bool hasAlpha)
            : this(LImage.CreateImage(width, height, hasAlpha), Format.DEFAULT)
        {

        }

        public LTexture(LTextureData data)
            : this(data, Format.DEFAULT)
        {

        }

        public LTexture(LTextureData d, Format format)
        {
            this.Init(d, format);
        }

        public LTexture(string resName)
            : this(resName, Format.DEFAULT, false)
        {

        }

        public LTexture(string resName, bool multipyAlpha)
            : this(resName, Format.DEFAULT, multipyAlpha)
        {

        }

        public LTexture(string resName,
                Format format, bool multipyAlpha)
        {
            LTextureData data = GLLoader.GetTextureData(resName);
            data.SetMultipyAlpha(multipyAlpha);
            this.Init(data, format);
        }

        private void Init(LTextureData d, Format format)
        {
            this.isExt = d.isExt;
            this.format = format;
            this.imageData = d;
            this.texWidth = d.texWidth;
            this.texHeight = d.texHeight;
            this.width = d.width;
            this.height = d.height;
            this.widthRatio = (float)width / (texWidth < 1 ? width : texWidth);
            this.heightRatio = (float)height
                    / (texHeight < 1 ? height : texHeight);
        }

        public void CheckReplace()
        {
            this.replace = Format.BILINEAR == format || Format.BILINEAR == format
                    || Format.REPEATING_BILINEAR == format;
            this.isStatic = format == Format.SPEED || format == Format.STATIC;
        }

        public string GetFileName()
        {
            if (imageData != null)
            {
                return imageData.fileName;
            }
            return null;
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public void LoadTexture()
        {
            if (parent != null)
            {
                parent.LoadTexture();
                textureID = parent.textureID;
                isLoaded = parent.isLoaded;
                return;
            }
            if (imageData == null || isLoaded)
            {
                return;
            }
            isLoaded = true;
            LoadTextureBuffer();
            SetFormat(format);
            LTextures.LoadTexture(this);
            LTextureBatch.isBatchCacheDitry = true;
        }

        public int CreateTextureID()
        {
            return GLEx.GL.CreateTexture(imageData.buffer);
        }

        private void LoadTextureBuffer()
        {
            if (!reload)
            {
                this.textureID = CreateTextureID();
                this.reload = false;
            }
            hasAlpha = imageData.hasAlpha;
            SetWidth(imageData.width);
            SetHeight(imageData.height);
            SetTextureWidth(imageData.texWidth);
            SetTextureHeight(imageData.texHeight);
            imageData.CreateTexture();
        }

        internal int _hashCode = 1;

        class UpdateReload : Updateable
        {

            private LTexture tex2d;

            public UpdateReload(LTexture tex)
            {
                this.tex2d = tex;
            }

            public void Action()
            {
                tex2d.LoadTexture();
                for (int i = 0; i < tex2d.childs.Count; i++)
                {
                    LTexture child = tex2d.childs[i];
                    if (child != null)
                    {
                        child.textureID = tex2d.textureID;
                        child.isLoaded = tex2d.isLoaded;
                        child.reload = tex2d.reload;
                    }
                }
                LTextureBatch.isBatchCacheDitry = true;
            }
        }

        public void Reload()
        {
            this.isLoaded = false;
            this.reload = true;
            this._hashCode = 1;
            if (childs != null)
            {
                LSystem.Load(new UpdateReload(this));
            }
        }

        public bool IsReplace()
        {
            return replace;
        }

        public void SetFormat(Format format)
        {

            int minFilter = GL.GL_NEAREST;
            int maxFilter = GL.GL_NEAREST;
            int wrapS = GL.GL_CLAMP_TO_EDGE;
            int wrapT = GL.GL_CLAMP_TO_EDGE;
            int texEnv = GL.GL_MODULATE;

            if (imageData != null)
            {
                if (format == Format.DEFAULT && imageData.hasAlpha)
                {
                    format = Format.SPEED;
                }
                else if (format == Format.DEFAULT && !imageData.hasAlpha)
                {
                    format = Format.STATIC;
                    this.format = format;
                }
            }

            switch (format)
            {
                case Format.DEFAULT:
                case Format.NEAREST:
                    break;
                case Format.LINEAR:
                    minFilter = GL.GL_LINEAR;
                    maxFilter = GL.GL_LINEAR;
                    wrapS = GL.GL_CLAMP_TO_EDGE;
                    wrapT = GL.GL_CLAMP_TO_EDGE;
                    texEnv = GL.GL_MODULATE;
                    break;
                case Format.STATIC:
                case Format.SPEED:
                    minFilter = GL.GL_NEAREST;
                    maxFilter = GL.GL_NEAREST;
                    wrapS = GL.GL_REPEAT;
                    wrapT = GL.GL_REPEAT;
                    texEnv = GL.GL_REPLACE;
                    break;
                case Format.BILINEAR:
                    minFilter = GL.GL_LINEAR;
                    maxFilter = GL.GL_LINEAR;
                    wrapS = GL.GL_CLAMP_TO_EDGE;
                    wrapT = GL.GL_CLAMP_TO_EDGE;
                    texEnv = GL.GL_REPLACE;
                    break;
                case Format.REPEATING:
                    minFilter = GL.GL_NEAREST;
                    maxFilter = GL.GL_NEAREST;
                    wrapS = GL.GL_REPEAT;
                    wrapT = GL.GL_REPEAT;
                    texEnv = GL.GL_REPLACE;
                    break;
                case Format.REPEATING_BILINEAR:
                    minFilter = GL.GL_LINEAR;
                    maxFilter = GL.GL_LINEAR;
                    wrapS = GL.GL_REPEAT;
                    wrapT = GL.GL_REPEAT;
                    texEnv = GL.GL_REPLACE;
                    break;
                case Format.REPEATING_BILINEAR_PREMULTIPLYALPHA:
                    minFilter = GL.GL_LINEAR;
                    maxFilter = GL.GL_LINEAR;
                    wrapS = GL.GL_REPEAT;
                    wrapT = GL.GL_REPEAT;
                    texEnv = GL.GL_MODULATE;
                    break;
                default:
                    break;
            }
            if (format != Format.FONT)
            {
                if (ALL_LINEAR && !ALL_NEAREST)
                {
                    minFilter = GL10.GL_LINEAR;
                    maxFilter = GL10.GL_LINEAR;
                }
                else if (ALL_NEAREST && !ALL_LINEAR)
                {
                    minFilter = GL10.GL_NEAREST;
                    maxFilter = GL10.GL_NEAREST;
                }
                else if (ALL_NEAREST && ALL_LINEAR)
                {
                    minFilter = GL10.GL_NEAREST;
                    maxFilter = GL10.GL_LINEAR;
                }
            }
            GL gl10 = GLEx.GL;
            if (gl10 == null)
            {
                return;
            }
            gl10.GLTexParameterf(this, GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER,
                    minFilter);
            gl10.GLTexParameterf(this, GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
                    maxFilter);
            gl10.GLTexParameterf(this, GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, wrapS);
            gl10.GLTexParameterf(this, GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, wrapT);
            gl10.GLTexEnvf(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, texEnv);
        }

        public void SetVertCords(int width, int height)
        {
            if (dataCords == null)
            {
                dataCords = new float[] { 0.0f, 0.0f, width, 0.0f, 0.0f, height,
					width, height, xOff, yOff, widthRatio, yOff, xOff,
					heightRatio, widthRatio, heightRatio };

                vertexSize = 8 * 4;

                texSize = 8 * 4;
            }
            dataCords[0] = 0;
            dataCords[1] = 0;
            dataCords[2] = width;
            dataCords[3] = 0;
            dataCords[4] = 0;
            dataCords[5] = height;
            dataCords[6] = width;
            dataCords[7] = height;

            this.width = width;
            this.height = height;
        }

        public void SetTexCords(float texXOff, float texYOff, float texWidthRatio,
                float texHeightRatio)
        {
            if (dataCords == null)
            {

                dataCords = new float[] { 0.0f, 0.0f, imageData.width, 0.0f, 0.0f,
					imageData.height, imageData.width, imageData.height, xOff,
					yOff, widthRatio, yOff, xOff, heightRatio, widthRatio,
					heightRatio };

                vertexSize = 8 * 4;

                texSize = 8 * 4;
            }
            dataCords[8] = texXOff;
            dataCords[9] = texYOff;
            dataCords[10] = texWidthRatio;
            dataCords[11] = texYOff;
            dataCords[12] = texXOff;
            dataCords[13] = texHeightRatio;
            dataCords[14] = texWidthRatio;
            dataCords[15] = texHeightRatio;

            this.xOff = texXOff;
            this.yOff = texYOff;
            this.widthRatio = texWidthRatio;
            this.heightRatio = texHeightRatio;
        }

        public void SetWidth(int width)
        {
            this.width = width;
            SetVertCords(width, height);
        }

        public void SetHeight(int height)
        {
            this.height = height;
            SetVertCords(width, height);
        }

        public void SetTextureSize(int textureWidth, int textureHeight)
        {
            this.texWidth = textureWidth;
            this.texHeight = textureHeight;
            SetTexCordRatio();
        }

        private void SetTexCordRatio()
        {
            widthRatio = (float)width / (texWidth < 1 ? width : texWidth);
            heightRatio = (float)height / (texHeight < 1 ? height : texHeight);
            SetTexCords(xOff, yOff, widthRatio, heightRatio);
        }

        public LTexture GetSubTexture(int x, int y, int width,
                 int height)
        {

            int hashCode = 1;

            hashCode = LSystem.Unite(hashCode, x);
            hashCode = LSystem.Unite(hashCode, y);
            hashCode = LSystem.Unite(hashCode, width);
            hashCode = LSystem.Unite(hashCode, height);

            if (childs == null)
            {
                childs = new System.Collections.Generic.Dictionary<Int32, LTexture>(10);
            }

            lock (childs)
            {
                LTexture cache = (LTexture)CollectionUtils.Get(childs, hashCode);

                if (cache != null)
                {
                    return cache;
                }

                LTexture sub = new LTexture();

                if (isLoaded)
                {
                    sub.parent = this;
                    sub.textureID = textureID;
                    sub.isLoaded = isLoaded;
                    sub.imageData = imageData;
                    sub.hasAlpha = hasAlpha;
                    sub.replace = replace;
                    sub.isStatic = isStatic;
                    sub.reload = reload;
                    sub.format = format;
                    sub.width = width;
                    sub.height = height;
                    sub.texWidth = texWidth;
                    sub.texHeight = texHeight;
                    sub.SetVertCords(width, height);
                    sub.xOff = (((float)x / this.width) * widthRatio) + xOff;
                    sub.yOff = (((float)y / this.height) * heightRatio) + yOff;
                    sub.widthRatio = (((float)width / this.width) * widthRatio)
                            + sub.xOff;
                    sub.heightRatio = (((float)height / this.height) * heightRatio)
                            + sub.yOff;
                    sub.SetTexCords(sub.xOff, sub.yOff, sub.widthRatio,
                            sub.heightRatio);
                    Crop(sub, x, y, width, height);
                }
                else
                {

                    sub.width = width;
                    sub.height = height;
                    sub.texWidth = texWidth;
                    sub.texHeight = texHeight;
                    sub.imageData = imageData;
                    sub.subX = x;
                    sub.subY = y;
                    sub.subWidth = width;
                    sub.subHeight = height;
                    sub.isVisible = false;

                    LSystem.Load(new SubUpdate(this, sub, x, y, width, height));
                }
                isChild = true;
                CollectionUtils.Put(childs, hashCode, sub);
                return sub;
            }
        }


        class SubUpdate : Updateable
        {

            private LTexture src, dst;

            private int x, y, width, height;

            public SubUpdate(LTexture src, LTexture dst, int x, int y, int w, int h)
            {
                this.x = x;
                this.y = y;
                this.width = w;
                this.height = h;
                this.src = src;
                this.dst = dst;
            }

            public void Action()
            {

                src.LoadTexture();

                dst.parent = src;
                dst.textureID = src.textureID;
                dst.isLoaded = src.isLoaded;
                dst.imageData = src.imageData;
                dst.hasAlpha = src.hasAlpha;
                dst.replace = src.replace;
                dst.isStatic = src.isStatic;
                dst.reload = src.reload;
                dst.format = src.format;
                dst.width = src.width;
                dst.height = src.height;
                dst.texWidth = src.texWidth;
                dst.texHeight = src.texHeight;
                dst.SetVertCords(src.width, src.height);
                dst.xOff = (((float)x / src.width) * src.widthRatio)
                        + src.xOff;
                dst.yOff = (((float)y / src.height) * src.heightRatio)
                        + src.yOff;
                dst.widthRatio = (((float)width / src.width) * src.widthRatio)
                        + dst.xOff;
                dst.heightRatio = (((float)height / src.height) * src.heightRatio)
                        + dst.yOff;
                dst.SetTexCords(dst.xOff, dst.yOff, dst.widthRatio,
                        dst.heightRatio);
                src.Crop(dst, x, y, width, height);

                dst.isVisible = true;

            }
        };

        public LTexture Scale(float scale)
        {
            int nW = (int)(width * scale);
            int nH = (int)(height * scale);
            return Copy(nW, nH, false, false);
        }

        public LTexture Scale(int width, int height)
        {
            return Copy(width, height, false, false);
        }

        public LTexture Copy()
        {
            return Copy(width, height, false, false);
        }

        public LTexture Flip(bool flipHorizontal, bool flipVertial)
        {
            return Copy(width, height, flipHorizontal, flipVertial);
        }

        class CopyUpdate : Updateable
        {

            private LTexture src, dst;

            private int x, y, width, height;

            private bool flipVertial, flipHorizontal;

            public CopyUpdate(LTexture src, LTexture dst, int x, int y, int w, int h, bool flipHorizontal, bool flipVertial)
            {
                this.x = x;
                this.y = y;
                this.width = w;
                this.height = h;
                this.src = src;
                this.dst = dst;
                this.flipVertial = flipVertial;
                this.flipHorizontal = flipHorizontal;
            }

            public void Action()
            {

                src.LoadTexture();

                dst.parent = src;
						dst.imageData = src.imageData;
                        dst.textureID = src.textureID;
                        dst.isLoaded = src.isLoaded;
                        dst.replace = src.replace;
                        dst.isStatic = src.isStatic;
                        dst.reload = src.reload;
                        dst.format = src.format;
                        dst.hasAlpha = src.hasAlpha;
                        dst.SetVertCords(width, height);
                        dst.texWidth = src.texWidth;
                        dst.texHeight = src.texHeight;
                        dst.SetTexCords(src.xOff, src.yOff, src.widthRatio, src.heightRatio);
						if (flipHorizontal) {
							src.Swap(8, 10, dst.dataCords);
							src.Swap(12, 14, dst.dataCords);
						}
						if (flipVertial) {
							src.Swap(9, 13, dst.dataCords);
							src.Swap(11, 15, dst.dataCords);
						}
						dst.xOff = src.dataCords[8];
						dst.yOff = src.dataCords[9];
						dst.widthRatio = src.dataCords[14];
						dst.heightRatio = src.dataCords[15];

                        System.Array.Copy(src.crops, 0, dst.crops, 0, dst.crops.Length);
						
						dst.isVisible = true;

            }
        };

        private LTexture Copy(int width, int height,
                bool flipHorizontal, bool flipVertial)
        {

            int hashCode = 1;

            hashCode = LSystem.Unite(hashCode, width);
            hashCode = LSystem.Unite(hashCode, height);
            hashCode = LSystem.Unite(hashCode, flipHorizontal);
            hashCode = LSystem.Unite(hashCode, flipVertial);

            if (childs == null)
            {
                childs = new System.Collections.Generic.Dictionary<Int32, LTexture>(10);
            }

            lock (childs)
            {

                LTexture cache = (LTexture)CollectionUtils.Get(childs, hashCode);

                if (cache != null)
                {
                    return cache;
                }

                if (dataCords == null)
                {
                    SetVertCords(this.GetWidth(), this.GetHeight());
                }

                LTexture copy = new LTexture();

                if (isLoaded)
                {


                    copy.parent = this;
                    copy.imageData = imageData;
                    copy.textureID = textureID;
                    copy.isLoaded = isLoaded;
                    copy.replace = replace;
                    copy.isStatic = isStatic;
                    copy.reload = reload;
                    copy.format = format;
                    copy.hasAlpha = hasAlpha;
                    copy.SetVertCords(width, height);
                    copy.texWidth = texWidth;
                    copy.texHeight = texHeight;
                    copy.SetTexCords(xOff, yOff, widthRatio, heightRatio);
                    if (flipHorizontal)
                    {
                        Swap(8, 10, copy.dataCords);
                        Swap(12, 14, copy.dataCords);
                    }
                    if (flipVertial)
                    {
                        Swap(9, 13, copy.dataCords);
                        Swap(11, 15, copy.dataCords);
                    }
                    copy.xOff = dataCords[8];
                    copy.yOff = dataCords[9];
                    copy.widthRatio = dataCords[14];
                    copy.heightRatio = dataCords[15];

                    System.Array.Copy(crops, 0, copy.crops, 0, crops.Length);

                }
                else
                {

                    copy.width = width;
                    copy.height = height;
                    copy.texWidth = texWidth;
                    copy.texHeight = texHeight;
                    copy.imageData = imageData;
                    copy.subX = 0;
                    copy.subY = 0;
                    copy.subWidth = width;
                    copy.subHeight = height;
                    copy.isVisible = false;

                    LSystem.Load(new CopyUpdate(this, copy, 0, 0, width, height, flipHorizontal, flipVertial));

                }
                isChild = true;
                CollectionUtils.Put(childs, hashCode, copy);
                return copy;
            }
        }

        public override int GetHashCode()
        {
            if (_hashCode == 1 && imageData.buffer != null)
            {
                int[] buffer = new int[imageData.texWidth * imageData.texHeight];
                imageData.buffer.GetData<int>(buffer);
                int skip = 3;
                int limit = buffer.Length;
                if (limit < 512)
                {
                    skip = 1;
                }
                for (int j = 0; j < limit; j += skip)
                {
                    if (j < limit)
                    {
                        _hashCode = LSystem.Unite(_hashCode, buffer[j]);
                    }
                }
                if (dataCords != null)
                {
                    for (int i = 0; i < dataCords.Length; i++)
                    {
                        _hashCode = LSystem.Unite(_hashCode, dataCords[i]);
                    }
                }
            }
            return _hashCode;
        }

        public bool IsLoaded()
        {
            return isLoaded;
        }

        public void SetLoaded(bool isLoaded)
        {
            this.isLoaded = isLoaded;
        }

        public bool IsClose()
        {
            return isClose;
        }

        public LTextureData GetImageData()
        {
            return imageData;
        }

        public LTexture GetParent()
        {
            return parent;
        }

        public bool IsChild()
        {
            return isChild;
        }

        public int GetWidth()
        {
            if (width == 0 && imageData != null)
            {
                return imageData.GetWidth();
            }
            return width;
        }

        public int GetHeight()
        {
            if (height == 0 && imageData != null)
            {
                return imageData.GetHeight();
            }
            return height;
        }

        public void SetTextureWidth(int textureWidth)
        {
            SetTextureSize(textureWidth, texHeight);
        }

        public void SetTextureHeight(int textureHeight)
        {
            SetTextureSize(texWidth, textureHeight);
        }

        public float GetTextureWidth()
        {
            return texWidth;
        }

        public float GetTextureHeight()
        {
            return texHeight;
        }

        private void Crop(LTexture texture, int x, int y, int width, int height)
        {
            texture.crops[0] = x;
            texture.crops[1] = height + y;
            texture.crops[2] = width;
            texture.crops[3] = -height;
            texture.subX = x;
            texture.subY = y;
            texture.subWidth = width;
            texture.subHeight = height;
        }

        private void Swap(int idx1, int idx2, float[] texCords)
        {
            float tmp = texCords[idx1];
            texCords[idx1] = texCords[idx2];
            texCords[idx2] = tmp;
        }


        public Format GetFormat()
        {
            return format;
        }

        public void CloseChildAll()
        {
            if (childs != null)
            {
                foreach (LTexture tex2d in childs.Values)
                {
                    if (tex2d != null && !tex2d.isClose)
                    {
                        tex2d.Destroy();
                    }
                }
            }
        }

        public bool IsChildAllClose()
        {
            if (childs != null)
            {
                foreach (LTexture tex2d in childs.Values)
                {
                    if (tex2d != null && !tex2d.isClose)
                    {
                        return false;
                    }
                }
            }
            return true;
        }



        public void SetImageColor(float r, float g, float b, float a)
        {
            SetColor(TOP_LEFT, r, g, b, a);
            SetColor(TOP_RIGHT, r, g, b, a);
            SetColor(BOTTOM_LEFT, r, g, b, a);
            SetColor(BOTTOM_RIGHT, r, g, b, a);
        }

        public void SetImageColor(float r, float g, float b)
        {
            SetColor(TOP_LEFT, r, g, b);
            SetColor(TOP_RIGHT, r, g, b);
            SetColor(BOTTOM_LEFT, r, g, b);
            SetColor(BOTTOM_RIGHT, r, g, b);
        }

        public void SetImageColor(LColor c)
        {
            if (c == null)
            {
                return;
            }
            SetImageColor(c.r, c.g, c.b, c.a);
        }

        public void SetColor(int corner, float r, float g, float b, float a)
        {
            if (colors == null)
            {
                colors = new LColor[] { new LColor(1f, 1f, 1f, 1f),
					new LColor(1f, 1f, 1f, 1f), new LColor(1f, 1f, 1f, 1f),
					new LColor(1f, 1f, 1f, 1f) };
            }
            colors[corner].r = r;
            colors[corner].g = g;
            colors[corner].b = b;
            colors[corner].a = a;
        }

        public void SetColor(int corner, float r, float g, float b)
        {
            if (colors == null)
            {
                colors = new LColor[] { new LColor(1f, 1f, 1f, 1f),
					new LColor(1f, 1f, 1f, 1f), new LColor(1f, 1f, 1f, 1f),
					new LColor(1f, 1f, 1f, 1f) };
            }
            colors[corner].r = r;
            colors[corner].g = g;
            colors[corner].b = b;
        }

        public LTextureBatch GetTextureBatch()
        {
            MakeBatch();
            return batch;
        }

        protected internal int MaxBatchSize = 1024;

        void MakeBatch()
        {
            if (!isBatch)
            {
                batch = new LTextureBatch(this, MaxBatchSize);
                isBatch = true;
            }
        }

        void FreeBatch()
        {
            if (isBatch)
            {
                if (batch != null)
                {
                    batch.Dispose();
                    batch = null;
                    isBatch = false;
                }
            }
        }

        public bool IsBatch()
        {
            return (isBatch && batch.useBegin);
        }

        public void GLBegin()
        {
            MakeBatch();
            batch.GLBegin();
        }

        public void GLBegin(int type)
        {
            MakeBatch();
            batch.GLBegin(type);
        }

        public void GLEnd()
        {
            if (isBatch)
            {
                batch.GLEnd();
            }
        }

        public void SetBatchPos(float x, float y)
        {
            if (isBatch)
            {
                batch.SetLocation(x, y);
            }
        }

        public bool IsBatchLocked()
        {
            return isBatch && batch.isLocked;
        }

        public void GLCacheCommit()
        {
            if (isBatch)
            {
                batch.GLCacheCommit();
            }
        }

        public void GLLock()
        {
            if (isBatch)
            {
                batch.Lock();
            }
        }

        public void GLUnLock()
        {
            if (isBatch)
            {
                batch.UnLock();
            }
        }

        public void Draw(float x, float y)
        {
            Draw(x, y, width, height);
        }

        public void Draw(float x, float y, float width, float height)
        {
            if (isBatch)
            {
                batch.Draw(colors, x, y, width, height);
            }
            else
            {
                GLEx.Self.DrawTexture(this, x, y, width, height);
            }
        }

        public void Draw(float x, float y, LColor[] c)
        {
            if (isBatch)
            {
                batch.Draw(c, x, y, width, height);
            }
            else
            {
                GLEx.Self.DrawTexture(this, x, y, width, height);
            }
        }

        public void Draw(float x, float y, LColor c)
        {
            if (isBatch)
            {
                bool update = CheckUpdateColor(c);
                if (update)
                {
                    SetImageColor(c);
                }
                batch.Draw(colors, x, y, width, height);
                if (update)
                {
                    SetImageColor(LColor.white);
                }
            }
            else
            {
                GLEx.Self.DrawTexture(this, x, y, width, height, c);
            }
        }

        public void Draw(float x, float y, float width, float height, LColor c)
        {
            if (isBatch)
            {
                bool update = CheckUpdateColor(c);
                if (update)
                {
                    SetImageColor(c);
                }
                batch.Draw(colors, x, y, width, height);
                if (update)
                {
                    SetImageColor(LColor.white);
                }
            }
            else
            {
                GLEx.Self.DrawTexture(this, x, y, width, height, c);
            }
        }

        public void DrawFlipX(float x, float y, LColor c)
        {
            if (isBatch)
            {
                bool update = CheckUpdateColor(c);
                if (update)
                {
                    SetImageColor(c);
                }
                batch.Draw(colors, x, y, width, height, 0, 0, width, height, true,
                        false);
                if (update)
                {
                    SetImageColor(LColor.white);
                }
            }
            else
            {
                GLEx.Self.DrawFlipTexture(this, x, y, c);
            }
        }

        public void DrawFlipY(float x, float y, LColor c)
        {
            if (isBatch)
            {
                bool update = CheckUpdateColor(c);
                if (update)
                {
                    SetImageColor(c);
                }
                batch.Draw(colors, x, y, width, height, 0, 0, width, height, false,
                        true);
                if (update)
                {
                    SetImageColor(LColor.white);
                }
            }
            else
            {
                GLEx.Self.DrawMirrorTexture(this, x, y, c);
            }
        }

        public void Draw(float x, float y, float width, float height, float x1,
                float y1, float x2, float y2, LColor[] c)
        {
            if (isBatch)
            {
                batch.Draw(c, x, y, width, height, x1, y1, x2, y2);
            }
            else
            {
                GLEx.Self.DrawTexture(this, x, y, width, height, x1, y1, x2, y2);
            }
        }

        public void Draw(float x, float y, float width, float height, float x1,
                float y1, float x2, float y2, LColor c)
        {
            if (isBatch)
            {
                bool update = CheckUpdateColor(c);
                if (update)
                {
                    SetImageColor(c);
                }
                batch.Draw(colors, x, y, width, height, x1, y1, x2, y2);
                if (update)
                {
                    SetImageColor(LColor.white);
                }
            }
            else
            {
                GLEx.Self.DrawTexture(this, x, y, width, height, x1, y1, x2, y2);
            }
        }

        public void Draw(float x, float y, float srcX, float srcY, float srcWidth,
                float srcHeight)
        {
            if (isBatch)
            {
                batch.Draw(colors, x, y, srcWidth - srcX, srcHeight - srcY, srcX,
                        srcY, srcWidth, srcHeight);
            }
            else
            {
                GLEx.Self.DrawTexture(this, x, y, srcWidth - srcX,
                        srcHeight - srcY, srcX, srcY, srcWidth, srcHeight);
            }
        }

        public void DrawEmbedded(float x, float y, float width, float height,
                float x1, float y1, float x2, float y2)
        {
            Draw(x, y, width - x, height - y, x1, y1, x2, y2);
        }

        public void Draw(float x, float y, float width, float height, float x1,
                float y1, float x2, float y2)
        {
            if (isBatch)
            {
                batch.Draw(colors, x, y, width, height, x1, y1, x2, y2);
            }
            else
            {
                GLEx.Self.DrawTexture(this, x, y, width, height, x1, y1, x2, y2);
            }
        }

        public void Draw(float x, float y, float rotation)
        {
            Draw(x, y, this.width, this.height, 0, 0, this.width, this.height,
                    rotation, LColor.white);
        }

        public void Draw(float x, float y, float w, float h, float rotation,
                LColor c)
        {
            Draw(x, y, w, h, 0, 0, this.width, this.height, rotation, c);
        }

        public void DrawEmbedded(float x, float y, float width, float height,
                float x1, float y1, float x2, float y2, LColor c)
        {
            Draw(x, y, width - x, height - y, x1, y1, x2, y2, c);
        }

        public void Draw(float x, float y, float width, float height, float x1,
                float y1, float x2, float y2, float rotation, LColor c)
        {
            if (rotation == 0)
            {
                Draw(x, y, width, height, x1, y1, x2, y2, c);
                return;
            }
            if (isBatch)
            {
                bool update = CheckUpdateColor(c);
                if (update)
                {
                    SetImageColor(c);
                }
                batch.Draw(colors, x, y, width, height, x1, y1, x2, y2, rotation);
                if (update)
                {
                    SetImageColor(LColor.white);
                }
            }
            else
            {
                GLEx.Self.DrawTexture(this, x, y, width, height, x1, y1, x2, y2, c,
                        rotation);
            }
        }

        private bool CheckUpdateColor(LColor c)
        {
            return c != null && !LColor.white.Equals(c);
        }

        public Loon.Core.Graphics.Opengl.LTextureBatch.GLCache NewBatchCache()
        {
            if (isBatch)
            {
                return batch.NewGLCache();
            }
            return null;
        }

        public Loon.Core.Graphics.Opengl.LTextureBatch.GLCache NewBatchCache(bool flag)
        {
            if (isBatch)
            {
                return batch.NewGLCache(flag);
            }
            return null;
        }

        public void PostLastBatchCache()
        {
            if (isBatch)
            {
                batch.PostLastCache();
            }
        }

        public void FreeCache()
        {
            if (shapeCache != null)
            {
                shapeCache = null;
            }
            if (maskCache != null)
            {
                maskCache.Dispose();
                maskCache = null;
            }
        }


        private Shape shapeCache;

        private Mask maskCache;

        public Shape GetShape()
        {
            if (shapeCache != null)
            {
                return shapeCache;
            }
            LImage shapeImage = GetImage();
            if (shapeImage != null)
            {
                Polygon polygon = CollisionMask.MakePolygon(shapeImage);
                if (shapeImage != null)
                {
                    shapeImage.Dispose();
                    shapeImage = null;
                }
                return (shapeCache = polygon);
            }
            throw new RuntimeException("Create texture for shape fail !");
        }

        public Mask GetMask()
        {
            if (maskCache != null)
            {
                return maskCache;
            }

            Mask mask = CollisionMask.CreateMask(imageData.buffer);

            return (maskCache = mask);
        }


        public LImage GetImage()
        {
            return LImage.NewImage(imageData.buffer);
        }

        public void Dispose()
        {
            Dispose(true);
        }

        internal void Dispose(bool remove)
        {
            if (!IsChildAllClose())
            {
                return;
            }
            LTextures.RemoveTexture(this, remove);
        }

        public bool IsRecycled()
        {
            return this.isClose;
        }

        public Texture2D Texture
        {
            get
            {
                return imageData == null ? null : imageData.buffer;
            }
        }

        public int Width
        {
            get
            {
                return width;
            }
        }

        public int Height
        {
            get
            {
                return height;
            }
        }

        public void Destroy()
        {
            Destroy(true);
        }

        public void Destroy(bool remove)
        {
            Dispose(remove);
            FreeCache();
            FreeBatch();
        }

        public static implicit operator Microsoft.Xna.Framework.Graphics.Texture2D(LTexture t)
        {
            if (t == null)
            {
                return null;
            }
            return t.imageData.buffer;
        }
    }
}
