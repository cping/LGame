namespace Loon.Core.Graphics.OpenGL
{
    using System;
    using System.IO;
    using System.Collections.Generic;
    using Microsoft.Xna.Framework.Graphics;
    using Microsoft.Xna.Framework;
    using Loon.Core.Graphics.Device;
    using Loon.Core.Geom;
    using Loon.Action.Sprite;
    using Loon.Action.Collision;
    using Loon.Java;
    using Loon.Utils;
    using Loon.Utils.Debug;
    using Loon.Core.Event;

    public class LTexture : LRelease
    {

        private class Copy_Updateable : Updateable
        {

            LTexture copy, src;

            private int w, h;

            public Copy_Updateable(LTexture copy, LTexture src, int w,
        int h)
            {
                this.copy = copy;
                this.src = src;
                this.w = w;
                this.h = h;

            }

            public void Action()
            {
                src.LoadTexture();

                copy.parent = src;
                copy.isLoaded = src.isLoaded;
                copy.isExt = src.isExt;
                copy.isOpaque = src.isOpaque;
                copy.tex2d = src.tex2d;

                copy.xOff = src.xOff;
                copy.yOff = src.yOff;
                copy.widthRatio = src.widthRatio;
                copy.heightRatio = src.heightRatio;

                copy.width = w;
                copy.height = h;
                copy.texWidth = src.texWidth;
                copy.texHeight = src.texHeight;

            }
        }

        private class Sub_Updateable : Updateable
        {

            LTexture sub, src;

            private int x, y, w, h;

            public Sub_Updateable(LTexture sub, LTexture src, int x, int y, int w,
        int h)
            {
                this.sub = sub;
                this.src = src;
                this.x = x;
                this.y = y;
                this.w = w;
                this.h = h;

            }

            public void Action()
            {

                src.LoadTexture();

                sub.parent = src;
                sub.isLoaded = src.isLoaded;
                sub.isExt = src.isExt;
                sub.isOpaque = src.isOpaque;
                sub.tex2d = src.tex2d;

                sub.xOff = (((float)x / src.width) * src.widthRatio) + src.xOff;
                sub.yOff = (((float)y / src.height) * src.heightRatio) + src.yOff;
                sub.widthRatio = (((float)w / src.width) * src.widthRatio)
                        + sub.xOff;
                sub.heightRatio = (((float)h / src.height) * src.heightRatio)
                        + sub.yOff;

                sub.width = w;
                sub.height = h;
                sub.texWidth = src.width;
                sub.texHeight = src.height;


            }
        }

        public LTextureRegion GetTextureRegion(int x, int y, int width, int height)
        {
            return new LTextureRegion(this, x, y, width, height);
        }

        public LTextureRegion[][] Split(int tileWidth, int tileHeight)
        {
            return new LTextureRegion(this).Split(tileWidth, tileHeight);
        }

        internal static Texture2D LoadFromResource(string name)
        {
            Texture2D tex = Texture2D.FromStream(GLEx.Device, XNAConfig.ResourceManager.GetStream(name));
            return new LTexture(tex);
        }

        public class Mask : LRelease
        {

            private int height;

            private int width;

            private bool[][] data;

            public Mask(int w, int h)
            {
                this.width = w;
                this.height = h;
            }

            public Mask(bool[][] d, int w, int h)
            {
                this.data = d;
                this.width = w;
                this.height = h;
            }

            public bool[][] GetData()
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

            public void SetData(bool[][] d)
            {
                this.data = d;
            }

            public int GetWidth()
            {
                return width;
            }

            public int GetHeight()
            {
                return height;
            }

            public virtual void Dispose()
            {
                if (data != null)
                {
                    data = null;
                }
            }

        }

        public object Tag;

        internal bool isOpaque;

        private Shape shapeCache;

        private Mask maskCache;

        public bool IsLoaded()
        {
            return isLoaded;
        }

        public void SetLoaded(bool l)
        {
            this.isLoaded = l;
        }

        internal int refCount = 0;

        internal int textureID;

        private LColor color = new LColor(LColor.white);

        internal bool isLoaded, isBatch, reload, isClose;

        internal Texture2D tex2d;

        private Stream ins;

        internal int texWidth, texHeight;

        internal int width, height;

        private bool zoom;

        internal bool isExt;

        internal string ext;

        internal bool isChild;

        private LTexture parent;

        internal int hashCode;

        internal float xOff = 0.0f;

        internal float yOff = 0.0f;

        internal float widthRatio = 1.0f;

        internal float heightRatio = 1.0f;

        internal string fileName;

        internal string lazyName;

        internal SurfaceFormat format;

        internal List<LTexture> childs = new List<LTexture>();

        LTextureBatch batch;

        public int GetWidth()
        {
            return Width;
        }

        public int GetHeight()
        {
            return Height;
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

            xOff = (((float)0 / this.width) * widthRatio) + xOff;
            yOff = (((float)0 / this.height) * heightRatio) + yOff;
            widthRatio = (((float)width / this.width) * widthRatio)
                    + xOff;
            heightRatio = (((float)height / this.height) * heightRatio)
                    + yOff;
        }

        public int Width
        {
            get
            {
                if (width != 0)
                {
                    return width;
                }
                LoadTexture();
                return width;
            }
        }

        public int Height
        {
            get
            {
                if (height != 0)
                {
                    return height;
                }
                LoadTexture();
                return height;
            }
        }

        public bool IsChild
        {
            get
            {
                return isChild;
            }
        }

        LTexture()
        {

        }


        public LTexture Scale(float scale)
        {
            int nW = (int)(Width * scale);
            int nH = (int)(Height * scale);
            return Copy(nW, nH);
        }

        public LTexture Scale(int width, int height)
        {
            return Copy(width, height);
        }

        public LTexture Copy()
        {
            return Copy(width, height);
        }

        private LTexture Copy(int w, int h)
        {

            LTexture copy = new LTexture();
            if (GLEx.device != null)
            {

                LoadTexture();

                copy.parent = this;
                copy.isLoaded = isLoaded;
                copy.isExt = isExt;
                copy.isOpaque = isOpaque;
                copy.tex2d = tex2d;

                copy.xOff = xOff;
                copy.yOff = yOff;
                copy.widthRatio = widthRatio;
                copy.heightRatio = heightRatio;

                copy.width = w;
                copy.height = h;
                copy.texWidth = texWidth;
                copy.texHeight = texHeight;
            }
            else
            {
                copy.isLoaded = isLoaded;
                copy.isExt = isExt;
                copy.isOpaque = isOpaque;

                copy.xOff = xOff;
                copy.yOff = yOff;
                copy.widthRatio = widthRatio;
                copy.heightRatio = heightRatio;

                copy.width = w;
                copy.height = h;
                copy.texWidth = texWidth;
                copy.texHeight = texHeight;

                LSystem.Load(new Copy_Updateable(copy, this, w, h));
            }
            copy.isChild = true;

            childs.Add(copy);

            return copy;
        }

        public LTexture GetSubTexture(int x, int y, int w,
                int h)
        {

            LTexture sub = new LTexture();
            if (GLEx.device != null)
            {
                LoadTexture();

                sub.parent = this;
                sub.isLoaded = isLoaded;
                sub.isExt = isExt;
                sub.isOpaque = isOpaque;
                sub.tex2d = tex2d;

                sub.xOff = (((float)x / this.width) * widthRatio) + xOff;
                sub.yOff = (((float)y / this.height) * heightRatio) + yOff;
                sub.widthRatio = (((float)w / this.width) * widthRatio)
                        + sub.xOff;
                sub.heightRatio = (((float)h / this.height) * heightRatio)
                        + sub.yOff;

                sub.width = w;
                sub.height = h;
                sub.texWidth = width;
                sub.texHeight = height;

            }
            else
            {
                sub.isLoaded = isLoaded;
                sub.isExt = isExt;
                sub.isOpaque = isOpaque;

                sub.xOff = (((float)x / this.width) * widthRatio) + xOff;
                sub.yOff = (((float)y / this.height) * heightRatio) + yOff;
                sub.widthRatio = (((float)w / this.width) * widthRatio)
                        + sub.xOff;
                sub.heightRatio = (((float)h / this.height) * heightRatio)
                        + sub.yOff;

                sub.width = w;
                sub.height = h;
                sub.texWidth = width;
                sub.texHeight = height;

                LSystem.Load(new Sub_Updateable(sub, this, x, y, w, h));
            }

            sub.isChild = true;

            childs.Add(sub);
            return sub;
        }

        public Texture2D Texture
        {
            get
            {
                if (isLoaded)
                {
                    return tex2d;
                }
                LoadTexture();
                return tex2d;
            }
        }

        public LTexture(String resName, int width, int height)
            : this(Resource.Resources.OpenStream(resName), width, height)
        {
            this.ext = FileUtils.GetExtension(resName);
            if ("".Equals(ext))
            {
                this.isExt = false;
            }
            else
            {
                this.isExt = true;
            }
            this.fileName = resName;
            NextID();
        }

        public LTexture(Stream ins, int width, int height)
        {
            this.zoom = true;
            this.width = width;
            this.height = height;
            this.texWidth = width;
            this.texHeight = height;
        }

        private bool isBitmapTexture;

        public LTexture(Texture2D pTex)
        {
            this.isLoaded = true;
            this.tex2d = pTex;
            this.width = tex2d.Width;
            this.height = tex2d.Height;
            this.texWidth = width;
            this.texHeight = height;
            LTextures.LoadTexture(this);
            NextID();
        }

        public LTexture(int w, int h, bool alpha)
            : this(w, h, alpha ? SurfaceFormat.Color : SurfaceFormat.Bgr565)
        {
        }

        public LTexture(int w, int h, SurfaceFormat format)
        {
            this.format = format;
            this.isBitmapTexture = true;
            this.width = w;
            this.height = h;
            this.texWidth = width;
            this.texHeight = height;
            NextID();
        }

        public LTexture(string @resName)
        {
            this.ext = FileUtils.GetExtension(resName);
            if ("".Equals(ext) || "xna".Equals(ext, StringComparison.InvariantCultureIgnoreCase))
            {
                this.isExt = false;
            }
            else
            {
                this.isExt = true;
            }
            if (isExt)
            {
                BitmapDecoder image = new BitmapDecoder(resName);
                this.width = image.GetWidth();
                this.height = image.GetHeight();
            }
            else
            {
                if (LSystem.screenProcess != null)
                {
                    Screen screen = LSystem.screenProcess.GetScreen();
                    if (screen != null && screen.IsOnLoadComplete())
                    {
                        tex2d = LFXPlus.Get.Load<Texture2D>(StringUtils.ReplaceIgnoreCase(resName, ".xnb", ""));
                        width = tex2d.Width;
                        height = tex2d.Height;
                    }
                }
            }
            this.texWidth = width;
            this.texHeight = height;
            this.fileName = resName;
            if (isExt)
            {
                this.ins = Resource.Resources.OpenStream(resName);
            }
            NextID();
        }

        public LTexture(Stream pIns)
        {
            this.ins = pIns;
            this.ext = "IN";
            this.isExt = true;
            NextID();
        }

        public string GetFileName()
        {
            return fileName;
        }

        public string GetEXT()
        {
            return ext;
        }

        public LTexture GetParent()
        {
            return parent;
        }

        public bool IsChildAllClose()
        {
            if (childs != null)
            {
                foreach (LTexture c in childs)
                {
                    if (tex2d != null && !c.isClose)
                    {
                        return false;
                    }
                }
            }
            return true;
        }

        public void CloseChildAll()
        {
            if (childs != null)
            {
                foreach (LTexture c in childs)
                {
                    if (c != null && !c.isClose)
                    {
                        c.Destroy();
                    }
                }
                childs.Clear();
            }
        }

        public void LoadTexture()
        {
            lock (this)
            {
                if (isLoaded)
                {
                    return;
                }
                if (parent != null)
                {
                    textureID = parent.textureID;
                    isLoaded = parent.isLoaded;
                    parent.LoadTexture();
                    isOpaque = parent.isOpaque;
                    if (width == 0)
                    {
                        width = parent.Width;
                    }
                    if (height == 0)
                    {
                        height = parent.Height;
                    }
                    if (texWidth == 0)
                    {
                        texWidth = width;
                    }
                    if (texHeight == 0)
                    {
                        texHeight = height;
                    }
                    isLoaded = true;
                    return;
                }

                if (!isLoaded)
                {
                    if (isBitmapTexture)
                    {
                        if (format.Equals(null))
                        {
                            tex2d = new Texture2D(GLEx.Device, width, height);
                        }
                        else
                        {
                            tex2d = new Texture2D(GLEx.Device, width, height, false, format);
                        }
                        bool alpha = SurfaceFormat.Color.Equals(format);
                        int size = width * height;
                        Color[] pixels = new Color[size];
                        for (int i = 0; i < size; i++)
                        {
                            if (alpha)
                            {
                                pixels[i] = new Color(0, 0, 0, 0);
                            }
                            else
                            {
                                pixels[i] = new Color(0, 0, 0, 255);
                            }
                        }
                        tex2d.SetData(pixels);
                    }
                    else
                    {

                        if (!isExt)
                        {
                            if (tex2d == null)
                            {
                                this.tex2d = LFXPlus.Get.Load<Texture2D>(fileName);
                            }
                        }
                        else
                        {
                            if (zoom)
                            {
                                if (tex2d == null)
                                {
                                    tex2d = Texture2D.FromStream(GLEx.Device, ins, width, height, zoom);
                                }
                            }
                            else
                            {
                                if (tex2d == null)
                                {
                                    tex2d = Texture2D.FromStream(GLEx.Device, ins);
                                }
                            }
                        }
                    }
                    this.isLoaded = true;
                    this.width = tex2d.Width;
                    this.height = tex2d.Height;
                    this.texWidth = width;
                    this.texHeight = height;
                    if (ins != null)
                    {
                        ins.Close();
                        ins = null;
                    }
                    LTextures.LoadTexture(this);
                }
            }

            this.isOpaque = (BlendState.Opaque == tex2d.GraphicsDevice.BlendState) && !isExt;
            this.widthRatio = (float)width / (texWidth < 1 ? width : texWidth);
            this.heightRatio = (float)height / (texHeight < 1 ? height : texHeight);
            this.CheckPowerOfTwoSize();

        }


        internal bool IsPowerOfTwoSize { get; private set; }

        protected void CheckPowerOfTwoSize()
        {
            IsPowerOfTwoSize = ((tex2d.Width == 1) || (tex2d.Width == 2) || (tex2d.Width == 4) || (tex2d.Width == 8) || (tex2d.Width == 16) || (tex2d.Width == 32) || (tex2d.Width == 64) || (tex2d.Width == 128) || (tex2d.Width == 256) || (tex2d.Width == 512) || (tex2d.Width == 1024) || (tex2d.Width == 2048) || (tex2d.Width == 4096)) && ((tex2d.Height == 1) || (tex2d.Height == 2) || (tex2d.Height == 4) || (tex2d.Height == 8) || (tex2d.Height == 16) || (tex2d.Height == 32) || (tex2d.Height == 64) || (tex2d.Height == 128) || (tex2d.Height == 256) || (tex2d.Height == 512) || (tex2d.Height == 1024) || (tex2d.Height == 2048) || (tex2d.Height == 4096));
        }

        public void SetImageColor(float r, float g, float b, float a)
        {
            color.SetColor(r, g, b, a);
        }

        public void SetImageColor(byte r, byte g, byte b, byte a)
        {
            color.SetColor(r, g, b, a);
        }

        public void SetImageColor(LColor c)
        {
            color.SetColor(c);
        }


        void MakeBatch()
        {
            if (!isBatch)
            {

                batch = new LTextureBatch(this);
                isBatch = true;
                LoadTexture();
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

        public void GLBegin(BlendState state)
        {
            MakeBatch();
            batch.GLBegin(state);
        }

        public void GLEnd()
        {
            if (isBatch)
            {
                batch.GLEnd();
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
                batch.Draw(x, y, width, height, color);
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
                batch.Draw(x, y, c);
            }
            else
            {
                GLEx.Self.DrawTexture(this, x, y, c.Color);
            }
        }

        public void Draw(float x, float y, float width, float height, LColor c)
        {
            if (isBatch)
            {
                batch.Draw(x, y, width, height, c);
            }
            else
            {
                GLEx.Self.DrawTexture(this, x, y, width, height, c.Color);
            }
        }

        public void Draw(float x, float y, float width, float height, float x1,
                float y1, float x2, float y2, LColor c)
        {
            if (isBatch)
            {
                batch.Draw(x, y, width, height, x1, y1, x2, y2, 0, c);
            }
            else
            {
                GLEx.Self.DrawTexture(this, x, y, width, height, x1, y1, x2, y2, 0, c.Color);
            }
        }

        public void Draw(float x, float y, float srcX, float srcY, float srcWidth, float srcHeight)
        {
            if (isBatch)
            {
                batch.Draw(x, y, srcWidth, srcHeight, srcX,
                        srcY, srcWidth, srcHeight, 0, color);
            }
            else
            {
                GLEx.Self.DrawTexture(this, x, y, srcWidth,
                        srcHeight, srcX, srcY, srcWidth, srcHeight);
            }
        }

        public void Draw(float x, float y, float width, float height, float x1,
                float y1, float x2, float y2)
        {
            if (isBatch)
            {
                batch.Draw(x, y, width, height, x1, y1, x2, y2, 0, color);
            }
            else
            {
                GLEx.Self.DrawTexture(this, x, y, width, height, x1, y1, x2, y2, 0);
            }
        }

        public void Draw(float x, float y, float w, float h, float rotation,
                LColor c)
        {
            Draw(x, y, w, h, 0, 0, this.width, this.height, rotation, c);
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
                batch.Draw(x, y, width, height, x1, y1, x2, y2, rotation, c);
            }
            else
            {
                GLEx.Self.DrawTexture(this, x, y, width, height, x1, y1, x2, y2,
                        rotation, c.Color);
            }
        }

        public LPixmap GetImage()
        {
            LPixmap pixmap = new LPixmap(this);
            if (isChild)
            {
                int newX = (int)(xOff * texWidth);
                int newY = (int)(yOff * texHeight);
                int newWidth = (int)((texWidth * widthRatio)) - newX;
                int newHeight = (int)((texHeight * heightRatio)) - newY;
                return pixmap.Copy(newX, newY, newWidth, newHeight);
            }
            else
            {
                return pixmap;
            }
        }

        public Mask GetMask()
        {
            if (maskCache != null)
            {
                return maskCache;
            }
            LPixmap maskImage = GetImage();
            if (maskImage != null)
            {
                Mask mask = CollisionMask.CreateMask(maskImage);
                return (maskCache = mask);
            }
            throw new RuntimeException("Create texture for shape fail !");
        }

        public Shape GetShape()
        {
            if (shapeCache != null)
            {
                return shapeCache;
            }
            LPixmap shapeImage = GetImage();
            if (shapeImage != null)
            {
                Polygon polygon = CollisionMask.MakePolygon(shapeImage);
                return (shapeCache = polygon);
            }
            throw new RuntimeException("Create texture for shape fail !");
        }

        public bool IsClose()
        {
            return isClose;
        }

        public int GetTextureID()
        {
            return textureID;
        }

        private void NextID()
        {
            textureID++;
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

        public void Dispose()
        {
            Dispose(true);
        }

        public void Dispose(bool remove)
        {
            if (!IsChildAllClose())
            {
                return;
            }
            isClose = true;
            textureID--;
            if (parent == null)
            {
                if (tex2d != null)
                {
                    tex2d.Dispose();
                    tex2d = null;
                }
            }
            LTextures.RemoveTexture(this, remove);
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
            return t.Texture;
        }

    }
}
