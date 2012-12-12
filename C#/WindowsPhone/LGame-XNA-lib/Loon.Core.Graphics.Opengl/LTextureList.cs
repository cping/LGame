using Loon.Core.Resource;
using System.IO;
using System.Collections.Generic;
using Loon.Utils.Xml;
using Loon.Utils;
using System;
using Loon.Action.Sprite;
namespace Loon.Core.Graphics.Opengl
{

    public class LTextureList : LRelease
    {

        private readonly int width = LSystem.screenRect.width;

        private readonly int height = LSystem.screenRect.height;

        private class ImageData
        {

            public int index;

            public int x;

            public int y;

            public int w;

            public int h;

            public float scale;

            public int scaleType;

            public LColor mask;

            public string xref;
        }

        private System.Collections.Generic.Dictionary<string, ImageData> imageList;

        public LTextureList(string res)
            : this(Resources.OpenStream(res))
        {

        }

        public LTextureList(Stream ins0)
        {
            this.imageList = new Dictionary<string, ImageData>(10);
            this.autoExpand = false;
            this.visible = true;

            int index = 0;

            string x = "x", y = "y", w = "w", h = "h";
            string scale = "scale", src = "src", maskName = "mask", empty = "empty";
            string name = "name", filterName = "filter", n = "nearest", l = "linear";

            XMLDocument doc = XMLParser.Parse(ins0);
            List<XMLElement> images = doc.GetRoot().Find("image");

            if (images.Count > 0)
            {
                IEnumerator<XMLElement> it = images.GetEnumerator();
                for (; it.MoveNext(); )
                {
                    XMLElement ele = it.Current;
                    if (ele != null)
                    {
                        ImageData data = new ImageData();
                        data.x = ele.GetIntAttribute(x, 0);
                        data.y = ele.GetIntAttribute(y, 0);
                        data.w = ele.GetIntAttribute(w, 0);
                        data.h = ele.GetIntAttribute(h, 0);
                        data.scale = ele.GetFloatAttribute(scale, 0);
                        data.xref = ele.GetAttribute(src, empty);
                        XMLElement mask = ele.GetChildrenByName(maskName);
                        if (mask != null)
                        {
                            int r = mask.GetIntAttribute("r", 0);
                            int g = mask.GetIntAttribute("g", 0);
                            int b = mask.GetIntAttribute("b", 0);
                            int a = mask.GetIntAttribute("a", 0);
                            data.mask = new LColor(r, g, b, a);
                        }
                        else
                        {
                            data.mask = null;
                        }
                        string filter = ele.GetAttribute(filterName, n);
                        if (filter.Equals(n))
                        {
                            data.scaleType = 0;
                        }
                        if (filter.Equals(l))
                        {
                            data.scaleType = 1;
                        }
                        data.index = index;
                        XMLElement parent = ele.GetParent();
                        if (parent != null)
                        {
                            CollectionUtils.Put(imageList, parent.GetAttribute(name, empty), data);
                            index++;
                        }
                    }
                }
            }
            this.count = imageList.Count;
            this.values = new LTextureObject[count];
        }

        public LTexture LoadTexture(string name)
        {
            if (imageList == null)
            {
                throw new Exception("Xml data not loaded !");
            }
            ImageData data = (ImageData)CollectionUtils.Get(imageList, name);
            if (data == null)
            {
                throw new Exception("No such image reference: '" + name
                        + "'");
            }
            if (this.values[data.index] != null)
            {
                return this.values[data.index].texture;
            }
            LTexture img = null;
            if (data.mask != null)
            {
                img = TextureUtils.FilterColor(data.xref, data.mask,
                        data.scaleType == 0 ? Loon.Core.Graphics.Opengl.LTexture.Format.DEFAULT : Loon.Core.Graphics.Opengl.LTexture.Format.LINEAR);
            }
            else
            {
                img = LTextures.LoadTexture(data.xref,
                        data.scaleType == 0 ? Loon.Core.Graphics.Opengl.LTexture.Format.DEFAULT : Loon.Core.Graphics.Opengl.LTexture.Format.LINEAR);
            }
            if ((data.w != 0) && (data.h != 0))
            {
                img = img.GetSubTexture(data.x, data.y, data.w, data.h);
            }
            if (data.scale != 0)
            {
                img = img.Scale(data.scale);
            }
            this.values[data.index] = new LTextureObject(img, 0, 0);
            return img;
        }
        public int GetTextureX(string name)
        {
            return ((ImageData)CollectionUtils.Get(imageList, name)).x;
        }

        public int GetTextureY(string name)
        {
            return ((ImageData)CollectionUtils.Get(imageList, name)).y;
        }

        public int GetTextureWidth(string name)
        {
            return ((ImageData)CollectionUtils.Get(imageList, name)).w;
        }

        public int GetTextureHeight(string name)
        {
            return ((ImageData)CollectionUtils.Get(imageList, name)).h;
        }

        public float GetTextureScale(string name)
        {
            return ((ImageData)CollectionUtils.Get(imageList, name)).scale;
        }


        public sealed class LTextureObject
        {

            public LTexture texture;

            public float x, y;

            public int width, height;

            public bool visible;

            public LTextureObject(LTexture texture, float x, float y)
            {
                this.texture = texture;
                this.x = x;
                this.y = y;
                this.width = texture.GetWidth();
                this.height = texture.GetHeight();
                this.visible = true;
            }
        }

        private float alpha = 1;

        private bool visible;

        private int count = 0;

        private readonly bool autoExpand;

        internal LTextureObject[] values;

        internal float nx, ny;

        internal LTextureList(LTextureList tiles)
        {
            this.autoExpand = tiles.autoExpand;
            this.values = (LTextureObject[])CollectionUtils.CopyOf(tiles.values);
            this.count = tiles.count;
            this.visible = tiles.visible;
        }

        public LTextureList()
            : this(CollectionUtils.INITIAL_CAPACITY)
        {

        }

        public LTextureList(int size)
            : this(true, size)
        {

        }

        public LTextureList(bool expand, int size)
        {
            this.autoExpand = expand;
            this.values = new LTextureObject[size];
            this.count = size;
            this.visible = true;
        }

        public int add(LTexture tex2d, float x, float y)
        {
            return Add(new LTextureObject(tex2d, x, y));
        }

        public int Add(string res, float x, float y)
        {
            return Add(res, Loon.Core.Graphics.Opengl.LTexture.Format.SPEED, x, y);
        }

        public int Add(string res, Loon.Core.Graphics.Opengl.LTexture.Format format, float x, float y)
        {
            return Add(new LTextureObject(LTextures.LoadTexture(res, format), x, y));
        }

        public int Add(LTextureObject tex2d)
        {
            if (this.count == this.values.Length)
            {
                LTextureObject[] oldValue = this.values;
                if (this.autoExpand)
                    this.values = new LTextureObject[(oldValue.Length << 1) + 1];
                else
                {
                    this.values = new LTextureObject[oldValue.Length + 1];
                }
                System.Array.Copy((Array)(oldValue), 0, (Array)(this.values), 0, oldValue.Length);
            }
            this.values[this.count] = tex2d;
            return this.count++;
        }

        public LTextureObject Remove(int i)
        {
            if ((i >= this.count) || (i < 0))
            {
                throw new IndexOutOfRangeException("Referenced " + i + ", size = "
                                    + this.count.ToString());
            }
            LTextureObject ret = this.values[i];
            if (i < this.count - 1)
            {
                System.Array.Copy((Array)(this.values), i + 1, (Array)(this.values), i, this.count - i
                                    - 1);
            }
            this.count -= 1;
            return ret;
        }

        public void AddAll(LTextureObject[] t)
        {
            Capacity(this.count + t.Length);
            System.Array.Copy((Array)(t), 0, (Array)(this.values), this.count, t.Length);
            this.count += t.Length;
        }

        public void AddAll(LTextureList t)
        {
            Capacity(this.count + t.count);
            System.Array.Copy((Array)(t.values), 0, (Array)(this.values), this.count, t.count);
            this.count += t.count;
        }

        public void Draw(GLEx g)
        {
            Draw(g, 0, 0, width, height);
        }

        private SpriteBatch batch = new SpriteBatch(1000);

        public void Draw(GLEx g, int minX, int minY, int maxX, int maxY)
        {
            if (!visible)
            {
                return;
            }
            lock (values)
            {
                batch.Begin();
                if (alpha > 0 && alpha < 1)
                {
                    batch.SetAlpha(alpha);
                }
                for (int i = 0; i < count; i++)
                {
                    LTextureObject tex2d = this.values[i];
                    if (tex2d == null || !tex2d.visible)
                    {
                        continue;
                    }
                    nx = minX + tex2d.x;
                    ny = minY + tex2d.y;
                    if (nx + tex2d.width < minX || nx > maxX
                            || ny + tex2d.height < minY || ny > maxY)
                    {
                        continue;
                    }
                    LTexture texture = tex2d.texture;
                    if (texture == null)
                    {
                        continue;
                    }
                    if (tex2d.width != 0 && tex2d.height != 0)
                    {
                        batch.Draw(texture, tex2d.x, tex2d.y, tex2d.width,
                                tex2d.height);
                    }
                    else
                    {
                        batch.Draw(texture, tex2d.x, tex2d.y);
                    }
                }
                if (alpha > 0 && alpha < 1)
                {
                    batch.ResetColor();
                }
                batch.End();
            }
        }

        public void SetAlpha(float alpha)
        {
            this.alpha = alpha;
        }

        public float GetAlpha()
        {
            return alpha;
        }

        public bool IsVisible()
        {
            return visible;
        }

        public void SetVisible(bool visible)
        {
            this.visible = visible;
        }

        public LTextureObject[] Array()
        {
            return this.values;
        }

        public int Capacity()
        {
            return this.values.Length;
        }

        public void Clear()
        {
            this.count = 0;
        }

        public void Capacity(int size)
        {
            if (this.values.Length >= size)
            {
                return;
            }
            LTextureObject[] oldValue = this.values;
            this.values = new LTextureObject[size];
            System.Array.Copy((Array)(oldValue), 0, (Array)(this.values), 0, oldValue.Length);
        }

        public LTextureObject Get(int index)
        {
            return this.values[index];
        }

        public bool IsEmpty()
        {
            return this.count == 0;
        }

        public int Size()
        {
            return this.count;
        }

        public virtual LTextureList Clone()
        {
            return new LTextureList(this);
        }

        public void Update()
        {
            if (this.count == this.values.Length)
            {
                return;
            }
            LTextureObject[] oldValue = this.values;
            this.values = new LTextureObject[this.count];
            System.Array.Copy((Array)(oldValue), 0, (Array)(this.values), 0, this.count);
        }

        public LTextureObject[] ToArray(LTextureObject[] dest)
        {
            System.Array.Copy((Array)(this.values), 0, (Array)(dest), 0, this.count);
            return dest;
        }

        public void Dispose()
        {
            this.visible = false;
            foreach (LTextureObject tex2d in this.values)
            {
                if (tex2d != null)
                {
                    if (tex2d.texture != null)
                    {
                        tex2d.texture.Destroy();
                        tex2d.texture = null;
                    }
                }
            }
            if (batch != null)
            {
                batch.Dispose();
            }
        }

    }
}
