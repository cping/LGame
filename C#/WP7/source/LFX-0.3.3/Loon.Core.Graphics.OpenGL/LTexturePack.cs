namespace Loon.Core.Graphics.OpenGL
{
    using System;
    using System.IO;
    using System.Collections.Generic;
    using System.Runtime.CompilerServices;
    using System.Text;
    using Microsoft.Xna.Framework;
    using Loon.Core.Geom;
    using Loon.Core.Resource;
    using Loon.Utils.Collection;
    using Loon.Java;
    using Loon.Utils.Xml;
    using Loon.Utils.Debug;
    using Loon.Action.Sprite;
 
    public class LTexturePack : LRelease
    {

        private Loon.Core.Geom.Point.Point2i blittedSize = new Loon.Core.Geom.Point.Point2i();

        private ArrayMap temps = new ArrayMap();

        private LTexture texture = null;

        private int count;

        Color colorMask;

        bool useAlpha, packed, packing;

        private string fileName, name;

        public SpriteRegion CreateSpriteRegion(int id)
        {
            this.Pack();
            PackEntry entry = GetEntry(id);
            if (entry == null)
            {
                return null;
            }
            SpriteRegion region = new SpriteRegion(texture, entry.bounds.left,
                    entry.bounds.top, entry.bounds.right, entry.bounds.bottom);
            return region;
        }

        public SpriteRegion CreateSpriteRegion(String name)
        {
            this.Pack();
            PackEntry entry = GetEntry(name);
            if (entry == null)
            {
                return null;
            }
            SpriteRegion region = new SpriteRegion(texture, entry.bounds.left,
                    entry.bounds.top, entry.bounds.right, entry.bounds.bottom);
            return region;
        }

        public LTextureRegion CreateTextureRegion(int id)
        {
            this.Pack();
            PackEntry entry = GetEntry(id);
            if (entry == null)
            {
                return null;
            }
            LTextureRegion region = new LTextureRegion(texture, entry.bounds.left,
                    entry.bounds.top, entry.bounds.right, entry.bounds.bottom);
            return region;
        }

        public LTextureRegion CreateTextureRegion(String name)
        {
            this.Pack();
            PackEntry entry = GetEntry(name);
            if (entry == null)
            {
                return null;
            }
            LTextureRegion region = new LTextureRegion(texture, entry.bounds.left,
                    entry.bounds.top, entry.bounds.right, entry.bounds.bottom);
            return region;
        }

        public LTexturePack()
            : this(true)
        {

        }

        public LTexturePack(bool hasAlpha)
        {
            this.useAlpha = hasAlpha;
        }

        public LTexturePack(string path)
        {
            if (path == null || "".Equals(path))
            {
                throw new RuntimeException(path + " not found !");
            }
            try
            {
                Set(Resources.OpenStream(path));
            }
            catch (IOException e)
            {
                Log.Exception(e);
            }
        }

        public LTexturePack(Stream ins)
        {
            Set(ins);
        }

        public LTexturePack(XMLElement pack)
        {
            Set(pack);
        }

        private void Set(Stream ins)
        {
            XMLDocument doc = XMLParser.Parse(ins);
            XMLElement pack = doc.GetRoot();
            Set(pack);
        }

        private void Set(XMLElement pack)
        {
            this.fileName = pack.GetAttribute("file", null);
            this.name = pack.GetAttribute("name", fileName);
            int r = pack.GetIntAttribute("r", -1);
            int g = pack.GetIntAttribute("g", -1);
            int b = pack.GetIntAttribute("b", -1);
            int a = pack.GetIntAttribute("a", -1);
            if (r != -1 && g != -1 && b != -1 && a != -1)
            {
                colorMask = new Color(r, g, b, a);
            }
            if (fileName != null)
            {
                List<XMLElement> blocks = pack.List("block");
                foreach (XMLElement e in blocks)
                {
                    PackEntry entry = new PackEntry(null);
                    int id = e.GetIntAttribute("id", count);
                    entry.id = id;
                    entry.fileName = e.GetAttribute("name", null);
                    entry.bounds.left = e.GetIntAttribute("left", 0);
                    entry.bounds.top = e.GetIntAttribute("top", 0);
                    entry.bounds.right = e.GetIntAttribute("right", 0);
                    entry.bounds.bottom = e.GetIntAttribute("bottom", 0);
                    if (entry.fileName != null)
                    {
                        temps.Put(entry.fileName, entry);
                    }
                    else
                    {
                        temps.Put("" + id, entry);
                    }
                    count++;
                }
                this.packing = false;
                this.packed = true;
            }
            this.useAlpha = true;
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public LPixmap GetImage(int id)
        {
            PackEntry entry = GetEntry(id);
            if (entry != null)
            {
                if (entry.image != null && !entry.image.IsClose())
                {
                    return entry.image;
                }
                else if (entry.fileName != null)
                {
                    return new LPixmap(entry.fileName);
                }
            }
            return null;
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public LPixmap GetImage(string name)
        {
            PackEntry entry = GetEntry(name);
            if (entry != null)
            {
                if (entry.image != null && !entry.image.IsClose())
                {
                    return entry.image;
                }
                else if (entry.fileName != null)
                {
                    return new LPixmap(entry.fileName);
                }
            }
            return null;
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public int PutImage(string res)
        {
            return PutImage(res, new LPixmap(res));
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public int PutImage(LPixmap image)
        {
            return PutImage(
                    JavaRuntime.CurrentTimeMillis() + "|" + (count + 1),
                    image);
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public int PutImage(string name, LPixmap image)
        {
            CheckPacked();
            if (image == null)
            {
                throw new Exception();
            }
            if (image.GetWidth() <= 0 || image.GetHeight() <= 0)
            {
                throw new ArgumentException(
                        "width and height must be positive");
            }
            this.temps.Put(name, new PackEntry(image));
            this.packing = true;
            this.count++;
            return temps.Size() - 1;
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public int PutImage(string name, LTexture tex2d)
        {
            if (tex2d != null)
            {
                return PutImage(name, tex2d.GetImage());
            }
            return count;
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public int PutImage(LTexture tex2d)
        {
            if (tex2d != null)
            {
                return PutImage(tex2d.GetImage());
            }
            return count;
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public int RemoveImage(string name)
        {
            if (name != null)
            {
                PackEntry e = GetEntry(name);
                if (e != null)
                {
                    if (e.image != null)
                    {
                        e.image.Dispose();
                        e.image = null;
                        this.count--;
                        this.packing = true;
                        return temps.Size() - 1;
                    }
                }
            }
            return count;
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public int RemoveImage(int id)
        {
            if (id > -1)
            {
                PackEntry e = GetEntry(id);
                if (e != null)
                {
                    if (e.image != null)
                    {
                        e.image.Dispose();
                        e.image = null;
                        this.count--;
                        this.packing = true;
                        return temps.Size() - 1;
                    }
                }
            }
            return count;
        }

        public int Size()
        {
            return count;
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        private LPixmap PackImage()
        {
            CheckPacked();
            if (packing)
            {
                if (temps.IsEmpty())
                {
                    throw new Exception("Nothing to Pack !");
                }
                int maxWidth = 0;
                int maxHeight = 0;
                int totalArea = 0;
                for (int i = 0; i < temps.Size(); i++)
                {
                    PackEntry entry = (PackEntry)temps.Get(i);
                    int width = entry.image.GetWidth();
                    int height = entry.image.GetHeight();
                    if (width > maxWidth)
                    {
                        maxWidth = width;
                    }
                    if (height > maxHeight)
                    {
                        maxHeight = height;
                    }
                    totalArea += width * height;
                }
                Loon.Core.Geom.Point.Point2i size = new Loon.Core.Geom.Point.Point2i(CloseTwoPower(maxWidth),
                        CloseTwoPower(maxHeight));
                bool fitAll = false;
                while (!fitAll)
                {
                    int area = size.x * size.y;
                    if (area < totalArea)
                    {
                        NextSize(size);
                        continue;
                    }
                    Node root = new Node(size.x, size.y);
                    for (int i = 0; i < temps.Size(); i++)
                    {
                        PackEntry entry = (PackEntry)temps.Get(i);
                        Node inserted = root.Insert(entry);
                        if (inserted == null)
                        {
                            NextSize(size);
                            continue;
                        }
                    }
                    fitAll = true;
                }
                LPixmap image = new LPixmap(size.x, size.y, useAlpha);
                for (int i = 0; i < temps.Size(); i++)
                {
                    PackEntry entry = (PackEntry)temps.Get(i);
                    image.DrawPixmap(entry.image, entry.bounds.left, entry.bounds.top);
                }
                packing = false;
                return image;
            }
            return null;
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public LTexture Pack()
        {
            if (texture != null && !packing)
            {
                return texture;
            }
            if (fileName != null)
            {
                texture = new LTexture(fileName);
            }
            else
            {
                LPixmap image = PackImage();
                if (image == null)
                {
                    return null;
                }
                if (texture != null)
                {
                    texture.Destroy();
                    texture = null;
                }
                if (colorMask != null)
                {
                    Color[] pixels = image.GetData();
                    int size = pixels.Length;
                    uint color = colorMask.PackedValue;
                    for (int i = 0; i < size; i++)
                    {
                        if (pixels[i].PackedValue == color)
                        {
                            pixels[i].PackedValue = LSystem.TRANSPARENT;
                        }
                    }
                    image.SetData(pixels);
                }
                texture = image.Texture;
            }
            return texture;
        }

        public LTexture GetTexture()
        {
            return texture;
        }

        public PackEntry GetEntry(int id)
        {
            return (PackEntry)temps.Get(id);
        }

        public PackEntry GetEntry(string name)
        {
            return (PackEntry)temps.Get(name);
        }


        public bool IsBatch()
        {
            return texture != null && texture.IsBatch();
        }

        public void GLBegin()
        {
            if (count > 0)
            {
                Pack();
                texture.GLBegin();
            }
        }


        public void GLEnd()
        {
            if (count > 0)
            {
                texture.GLEnd();
            }
        }

        public Loon.Core.Geom.Point.Point2i Draw(int id, float x, float y)
        {
            return Draw(id, x, y, 0, null);
        }

        public Loon.Core.Geom.Point.Point2i Draw(int id, float x, float y, LColor color)
        {
            return Draw(id, x, y, 0, color);
        }

        public Loon.Core.Geom.Point.Point2i Draw(int id, float x, float y, float rotation, LColor color)
        {
            this.Pack();
            if (GLEx.Self != null)
            {
                PackEntry entry = GetEntry(id);
                if (entry == null)
                {
                    return null;
                }
                if (texture.IsBatch())
                {
                    texture.Draw(x, y, entry.bounds.Width(), entry.bounds.Height(),
                            entry.bounds.left, entry.bounds.top,
                            entry.bounds.right, entry.bounds.bottom, rotation,
                            color);
                }
                else
                {
                    GLEx.Self.DrawTexture(texture, x, y, entry.bounds.Width(),
                            entry.bounds.Height(), entry.bounds.left,
                            entry.bounds.top, entry.bounds.right,
                            entry.bounds.bottom, rotation, color.Color);
                }
                blittedSize.Set(entry.bounds.Width(), entry.bounds.Height());
            }
            return blittedSize;
        }

        public void DrawOnlyBatch(int id, float x, float y, LColor c)
        {
            this.Pack();
            PackEntry entry = GetEntry(id);
            if (entry == null)
            {
                return;
            }
            if (texture.IsBatch())
            {
                texture.Draw(x, y, entry.bounds.Width(), entry.bounds.Height(),
                        entry.bounds.left, entry.bounds.top, entry.bounds.right,
                        entry.bounds.bottom, c);
            }
        }

        public Loon.Core.Geom.Point.Point2i Draw(int id, float x, float y, float w, float h)
        {
            return Draw(id, x, y, w, h, 0, null);
        }

        public Loon.Core.Geom.Point.Point2i Draw(int id, float x, float y, float w, float h,
                LColor color)
        {
            return Draw(id, x, y, w, h, 0, color);
        }

        public Loon.Core.Geom.Point.Point2i Draw(int id, float x, float y, float w, float h,
                float rotation, LColor color)
        {
            this.Pack();
            if (GLEx.Self != null)
            {
                PackEntry entry = GetEntry(id);
                if (entry == null)
                {
                    return null;
                }
                if (texture.IsBatch())
                {
                    texture.Draw(x, y, w, h, entry.bounds.left, entry.bounds.top,
                            entry.bounds.right, entry.bounds.bottom, rotation,
                            color);
                }
                else
                {
                    GLEx.Self.DrawTexture(texture, x, y, w, h, entry.bounds.left,
                            entry.bounds.top, entry.bounds.right,
                            entry.bounds.bottom, rotation, color.Color);
                }
                blittedSize.Set(entry.bounds.Width(), entry.bounds.Height());
            }
            return blittedSize;
        }

        public Loon.Core.Geom.Point.Point2i Draw(int id, float dx1, float dy1, float dx2, float dy2,
                float sx1, float sy1, float sx2, float sy2)
        {
            return Draw(id, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, 0);
        }

        public Loon.Core.Geom.Point.Point2i Draw(int id, float dx1, float dy1, float dx2, float dy2,
                float sx1, float sy1, float sx2, float sy2, float rotation)
        {
            return Draw(id, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, rotation, null);
        }

        public Loon.Core.Geom.Point.Point2i Draw(int id, float dx1, float dy1, float dx2, float dy2,
                float sx1, float sy1, float sx2, float sy2, LColor color)
        {
            return Draw(id, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, 0, color);
        }

        public void DrawOnlyBatch(int id, float dx1, float dy1, float dx2,
                float dy2, float sx1, float sy1, float sx2, float sy2,
                LColor color)
        {
            this.Pack();
            PackEntry entry = GetEntry(id);
            if (entry == null)
            {
                return;
            }
            if (texture.IsBatch())
            {
                texture.Draw(dx1, dy1, dx2, dy2, sx1 + entry.bounds.left, sy1
                        + entry.bounds.top, sx2 + entry.bounds.left, sy2
                        + entry.bounds.top, color);
            }
            blittedSize.Set(entry.bounds.Width(), entry.bounds.Height());
        }

        public Loon.Core.Geom.Point.Point2i Draw(int id, float dx1, float dy1, float dx2, float dy2,
                float sx1, float sy1, float sx2, float sy2, float rotation,
                LColor color)
        {
            this.Pack();
            if (GLEx.Self != null)
            {
                PackEntry entry = GetEntry(id);
                if (entry == null)
                {
                    return null;
                }
                if (texture.IsBatch())
                {
                    texture.Draw(dx1, dy1, dx2, dy2, sx1 + entry.bounds.left, sy1
                            + entry.bounds.top, sx2 + entry.bounds.left, sy2
                            + entry.bounds.top, rotation, color);
                }
                else
                {
                    GLEx.Self.DrawTexture(texture, dx1, dy1, dx2, dy2, sx1
                            + entry.bounds.left, sy1 + entry.bounds.top, sx2
                            + entry.bounds.left, sy2 + entry.bounds.top, rotation,
                            color.Color);
                }
                blittedSize.Set(entry.bounds.Width(), entry.bounds.Height());
            }
            return blittedSize;
        }

        public Loon.Core.Geom.Point.Point2i Draw(string name, float x, float y)
        {
            return Draw(name, x, y, 0, null);
        }

        public Loon.Core.Geom.Point.Point2i Draw(string name, float x, float y, LColor color)
        {
            return Draw(name, x, y, 0, color);
        }

        public void DrawOnlyBatch(string name, float x, float y, LColor c)
        {
            this.Pack();
            PackEntry entry = GetEntry(name);
            if (texture.IsBatch())
            {
                texture.Draw(x, y, entry.bounds.Width(), entry.bounds.Height(),
                        entry.bounds.left, entry.bounds.top, entry.bounds.right,
                        entry.bounds.bottom, c);
            }
        }

        public void DrawOnlyBatch(string name, float dx1, float dy1, float dx2,
                float dy2, float sx1, float sy1, float sx2, float sy2,
                LColor color)
        {
            this.Pack();
            PackEntry entry = GetEntry(name);
            if (entry == null)
            {
                return;
            }
            if (texture.IsBatch())
            {
                texture.Draw(dx1, dy1, dx2, dy2, sx1 + entry.bounds.left, sy1
                        + entry.bounds.top, sx2 + entry.bounds.left, sy2
                        + entry.bounds.top, color);
            }
            blittedSize.Set(entry.bounds.Width(), entry.bounds.Height());
        }

        public Loon.Core.Geom.Point.Point2i Draw(string name, float x, float y, float rotation,
                LColor color)
        {
            this.Pack();
            if (GLEx.Self != null)
            {
                PackEntry entry = GetEntry(name);
                if (entry == null)
                {
                    return null;
                }
                if (texture.IsBatch())
                {
                    texture.Draw(x, y, entry.bounds.Width(), entry.bounds.Height(),
                            entry.bounds.left, entry.bounds.top,
                            entry.bounds.right, entry.bounds.bottom, rotation,
                            color);
                }
                else
                {
                    GLEx.Self.DrawTexture(texture, x, y, entry.bounds.Width(),
                            entry.bounds.Height(), entry.bounds.left,
                            entry.bounds.top, entry.bounds.right,
                            entry.bounds.bottom, rotation, color.Color);
                }
                blittedSize.Set(entry.bounds.Width(), entry.bounds.Height());
            }
            return blittedSize;
        }

        public Loon.Core.Geom.Point.Point2i Draw(string name, float x, float y, float w, float h)
        {
            return Draw(name, x, y, w, h, 0, null);
        }

        public Loon.Core.Geom.Point.Point2i Draw(string name, float x, float y, float w, float h,
                LColor color)
        {
            return Draw(name, x, y, w, h, 0, color);
        }

        public Loon.Core.Geom.Point.Point2i Draw(string name, float x, float y, float w, float h,
                float rotation, LColor color)
        {
            this.Pack();
            if (GLEx.Self != null)
            {
                PackEntry entry = GetEntry(name);
                if (entry == null)
                {
                    return null;
                }
                if (texture.IsBatch())
                {
                    texture.Draw(x, y, w, h, entry.bounds.left, entry.bounds.top,
                            entry.bounds.right, entry.bounds.bottom, rotation,
                            color);
                }
                else
                {
                    GLEx.Self.DrawTexture(texture, x, y, w, h, entry.bounds.left,
                            entry.bounds.top, entry.bounds.right,
                            entry.bounds.bottom, rotation, color.Color);
                }
                blittedSize.Set(entry.bounds.Width(), entry.bounds.Height());
            }
            return blittedSize;
        }

        public Loon.Core.Geom.Point.Point2i Draw(string name, float dx1, float dy1, float dx2,
                float dy2, float sx1, float sy1, float sx2, float sy2)
        {
            return Draw(name, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, 0);
        }

        public Loon.Core.Geom.Point.Point2i Draw(string name, float dx1, float dy1, float dx2,
                float dy2, float sx1, float sy1, float sx2, float sy2,
                float rotation)
        {
            return Draw(name, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, 0, null);
        }

        public Loon.Core.Geom.Point.Point2i Draw(string name, float dx1, float dy1, float dx2,
                float dy2, float sx1, float sy1, float sx2, float sy2, LColor color)
        {
            return Draw(name, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, 0, color);
        }

        public void drawOnlyBatch(string name, float dx1, float dy1, float dx2,
                float dy2, float sx1, float sy1, float sx2, float sy2,
                LColor color)
        {
            this.Pack();
            PackEntry entry = GetEntry(name);
            if (entry == null)
            {
                return;
            }
            if (texture.IsBatch())
            {
                texture.Draw(dx1, dy1, dx2, dy2, sx1 + entry.bounds.left, sy1
                        + entry.bounds.top, sx2 + entry.bounds.left, sy2
                        + entry.bounds.top, color);
            }
            blittedSize.Set(entry.bounds.Width(), entry.bounds.Height());
        }

        public Loon.Core.Geom.Point.Point2i Draw(string name, float dx1, float dy1, float dx2,
                float dy2, float sx1, float sy1, float sx2, float sy2,
                float rotation, LColor color)
        {
            this.Pack();
            if (GLEx.Self != null)
            {
                PackEntry entry = GetEntry(name);
                if (entry == null)
                {
                    return null;
                }
                if (texture.IsBatch())
                {
                    texture.Draw(dx1, dy1, dx2, dy2, sx1 + entry.bounds.left, sy1
                            + entry.bounds.top, sx2 + entry.bounds.left, sy2
                            + entry.bounds.top, rotation, color);
                }
                else
                {
                    GLEx.Self.DrawTexture(texture, dx1, dy1, dx2, dy2, sx1
                            + entry.bounds.left, sy1 + entry.bounds.top, sx2
                            + entry.bounds.left, sy2 + entry.bounds.top, rotation,
                            color.Color);
                }
                blittedSize.Set(entry.bounds.Width(), entry.bounds.Height());
            }
            return blittedSize;
        }

        public RectBox GetImageRect(int id)
        {
            PackEntry entry = GetEntry(id);
            if (entry == null)
            {
                return new RectBox(0, 0, 0, 0);
            }
            return new RectBox(0, 0, entry.width, entry.height);
        }

        public int[] GetImageRectArray(int id)
        {
            PackEntry entry = GetEntry(id);
            if (entry == null)
            {
                return new int[] { 0, 0 };
            }
            return new int[] { entry.width, entry.height };
        }

        public RectBox.Rect2i GetImageSize(int id)
        {
            PackEntry entry = GetEntry(id);
            if (entry == null)
            {
                return null;
            }
            return entry.bounds;
        }

        public RectBox.Rect2i GetImageSize(string name)
        {
            PackEntry entry = GetEntry(name);
            if (entry == null)
            {
                return null;
            }
            return entry.bounds;
        }

        private void NextSize(Loon.Core.Geom.Point.Point2i size)
        {
            if (size.x > size.y)
            {
                size.y <<= 1;
            }
            else
            {
                size.x <<= 1;
            }
        }

        public string GetFileName()
        {
            return fileName;
        }

        private int CloseTwoPower(int i)
        {
            int power = 1;
            while (power < i)
            {
                power <<= 1;
            }
            return power;
        }

        private void CheckPacked()
        {
            if (packed)
            {
                throw new Exception("the packed !");
            }
        }


        [MethodImpl(MethodImplOptions.Synchronized)]
        public void Packed()
        {
            this.Pack();
            this.packed = true;
            this.Free();
        }

        public class PackEntry
        {

            public RectBox.Rect2i bounds = new RectBox.Rect2i();

            public LPixmap image;

            public string fileName;

            public int id;

            public int width, height;

            public PackEntry(LPixmap image)
            {
                this.image = image;
                if (image != null)
                {
                    this.fileName = image.GetPath();
                    this.width = image.GetWidth();
                    this.height = image.GetHeight();
                }
            }

            public int Id()
            {
                return id;
            }

            public string Name()
            {
                return fileName;
            }

            public int Width()
            {
                return bounds.Width();
            }

            public int Height()
            {
                return bounds.Height();
            }
        }

        public class Node
        {

            private Node[] child = new Node[2];

            private RectBox.Rect2i bounds = new RectBox.Rect2i();

            private PackEntry entry;

            public Node()
            {
            }

            public Node(int width, int height)
            {
                bounds.Set(0, 0, width, height);
            }

            public bool IsLeaf()
            {
                return (child[0] == null) && (child[1] == null);
            }

            public Node Insert(PackEntry entry)
            {
                if (IsLeaf())
                {
                    if (this.entry != null)
                    {
                        return null;
                    }
                    int width = entry.image.GetWidth();
                    int height = entry.image.GetHeight();

                    if ((width > bounds.Width()) || (height > bounds.Height()))
                    {
                        return null;
                    }

                    if ((width == bounds.Width()) && (height == bounds.Height()))
                    {
                        this.entry = entry;
                        this.entry.bounds.Set(this.bounds);
                        return this;
                    }

                    child[0] = new Node();
                    child[1] = new Node();

                    int dw = bounds.Width() - width;
                    int dh = bounds.Height() - height;

                    if (dw > dh)
                    {
                        child[0].bounds.Set(bounds.left, bounds.top, bounds.left
                                + width, bounds.bottom);
                        child[1].bounds.Set(bounds.left + width, bounds.top,
                                bounds.right, bounds.bottom);
                    }
                    else
                    {
                        child[0].bounds.Set(bounds.left, bounds.top, bounds.right,
                                bounds.top + height);
                        child[1].bounds.Set(bounds.left, bounds.top + height,
                                bounds.right, bounds.bottom);
                    }
                    return child[0].Insert(entry);
                }
                else
                {
                    Node newNode = child[0].Insert(entry);
                    if (newNode != null)
                    {
                        return newNode;
                    }
                    return child[1].Insert(entry);
                }
            }

        }

        public string GetName()
        {
            return name;
        }

        public Color GetColorMask()
        {
            return colorMask;
        }

        public void SetColorMask(Color colorMask)
        {
            this.colorMask = colorMask;
        }

        private void Free()
        {
            if (temps != null)
            {
                for (int i = 0; i < temps.Size(); i++)
                {
                    PackEntry e = (PackEntry)temps.Get(i);
                    if (e != null)
                    {
                        if (e.image != null)
                        {
                            e.image.Dispose();
                            e.image = null;
                        }
                    }
                }
            }
        }

        public override string ToString()
        {
            StringBuilder sbr = new StringBuilder(1000);
            sbr.Append("<?xml version=\"1.0\" standalone=\"yes\" ?>\n");
            if (colorMask != null)
            {
                sbr.Append("<pack file=\"" + fileName + "\" mask=\""
                        + colorMask.R + "," + colorMask.G + ","
                        + colorMask.B + "\">\n");
            }
            else
            {
                sbr.Append("<pack file=\"" + fileName + "\">\n");
            }
            for (int i = 0; i < temps.Size(); i++)
            {
                PackEntry e = (PackEntry)temps.Get(i);
                if (e != null && e.bounds != null)
                {
                    sbr.Append("<block id=\"" + i + "\" name=\"" + e.fileName
                            + "\" left=\"" + e.bounds.left + "\" top=\""
                            + e.bounds.top + "\" right=\"" + e.bounds.right
                            + "\" bottom=\"" + e.bounds.bottom + "\"/>\n");
                }
            }
            sbr.Append("</pack>");
            return sbr.ToString();
        }

        public void Dispose()
        {
            Free();
            if (texture != null)
            {
                texture.Destroy();
                texture = null;
            }
        }

    }
}

