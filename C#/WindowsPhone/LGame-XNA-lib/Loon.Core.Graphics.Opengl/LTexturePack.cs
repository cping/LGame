using Loon.Utils.Collection;
using Loon.Action.Sprite;
using System;
using Loon.Core.Resource;
using System.IO;
using Loon.Java;
using Loon.Utils.Xml;
using System.Collections.Generic;
using System.Runtime.CompilerServices;
using System.Text;
using Microsoft.Xna.Framework;
using Loon.Core.Graphics.Device;
using Loon.Core.Geom;

namespace Loon.Core.Graphics.Opengl {
	
	public class LTexturePack : LRelease {
	
		private readonly Loon.Core.Geom.Point.Point2i blittedSize = new Loon.Core.Geom.Point.Point2i();
	
		private readonly ArrayMap temps = new ArrayMap();
	
		private LTexture texture = null;
	
		private int count;
	
		internal LColor colorMask;
	
		internal bool useAlpha, packed, packing;
	
		private string fileName, name;
	
		private Loon.Core.Graphics.Opengl.LTexture.Format format = Loon.Core.Graphics.Opengl.LTexture.Format.DEFAULT;
	
		public LTexture GetTexture(string name) {
			this.Pack();
			PackEntry entry = GetEntry(name);
			if (entry == null) {
				return null;
			}
			return texture.GetSubTexture(entry.bounds.left, entry.bounds.top,
					entry.bounds.right, entry.bounds.bottom);
		}
	
		public LTexture GetTexture(int id) {
			this.Pack();
			PackEntry entry = GetEntry(id);
			if (entry == null) {
				return null;
			}
			return texture.GetSubTexture(entry.bounds.left, entry.bounds.top,
					entry.bounds.right, entry.bounds.bottom);
		}
	
		public SpriteRegion CreateSpriteRegion(int id) {
			this.Pack();
			PackEntry entry = GetEntry(id);
			if (entry == null) {
				return null;
			}
			SpriteRegion region = new SpriteRegion(texture, entry.bounds.left,
					entry.bounds.top, entry.bounds.right, entry.bounds.bottom);
			return region;
		}
	
		public SpriteRegion CreateSpriteRegion(string name) {
			this.Pack();
			PackEntry entry = GetEntry(name);
			if (entry == null) {
				return null;
			}
			SpriteRegion region = new SpriteRegion(texture, entry.bounds.left,
					entry.bounds.top, entry.bounds.right, entry.bounds.bottom);
			return region;
		}
	
		public LTextureRegion CreateTextureRegion(int id) {
			this.Pack();
			PackEntry entry = GetEntry(id);
			if (entry == null) {
				return null;
			}
			LTextureRegion region = new LTextureRegion(texture, entry.bounds.left,
					entry.bounds.top, entry.bounds.right, entry.bounds.bottom);
			return region;
		}
	
		public LTextureRegion CreateTextureRegion(string name) {
			this.Pack();
			PackEntry entry = GetEntry(name);
			if (entry == null) {
				return null;
			}
			LTextureRegion region = new LTextureRegion(texture, entry.bounds.left,
					entry.bounds.top, entry.bounds.right, entry.bounds.bottom);
			return region;
		}
	
		public LTexturePack():this(true) {
			
		}
	
		public LTexturePack(bool hasAlpha) {
			this.useAlpha = hasAlpha;
		}
	
		public LTexturePack(string path) {
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
                Loon.Utils.Debugging.Log.Exception(e);
            }
		}
	
		public LTexturePack(Stream ins) {
			Set(ins);
		}
	
		public LTexturePack(XMLElement p) {
			Set(p);
		}
	
		private void Set(Stream ins) {
			XMLDocument doc = XMLParser.Parse(ins);
			XMLElement Pack = doc.GetRoot();
			Set(Pack);
		}
	
		private void Set(XMLElement Pack) {
			this.fileName = Pack.GetAttribute("file", null);
			this.name = Pack.GetAttribute("name", fileName);
			int r = Pack.GetIntAttribute("r", -1);
			int g = Pack.GetIntAttribute("g", -1);
			int b = Pack.GetIntAttribute("b", -1);
			int a = Pack.GetIntAttribute("a", -1);
			if (r != -1 && g != -1 && b != -1 && a != -1) {
				colorMask = new LColor(r, g, b, a);
			}
			if (fileName != null) {
				List<XMLElement> blocks = Pack.List("block");
				foreach (XMLElement e  in  blocks) {
					PackEntry entry = new PackEntry(null);
					int id = e.GetIntAttribute("id", count);
					entry.id = id;
					entry.fileName = e.GetAttribute("name", null);
					entry.bounds.left = e.GetIntAttribute("left", 0);
					entry.bounds.top = e.GetIntAttribute("top", 0);
					entry.bounds.right = e.GetIntAttribute("right", 0);
					entry.bounds.bottom = e.GetIntAttribute("bottom", 0);
					if (entry.fileName != null) {
						temps.Put(entry.fileName, entry);
					} else {
						temps.Put(Convert.ToString(id), entry);
					}
					count++;
				}
				this.packing = false;
				this.packed = true;
			}
			this.useAlpha = true;
		}
	
		public LImage GetImage(int id) {
			PackEntry entry = GetEntry(id);
			if (entry != null) {
				if (entry.image != null && !entry.image.IsClose()) {
					return entry.image;
				} else if (entry.fileName != null) {
					return LImage.CreateImage(entry.fileName);
				}
			}
			return null;
		}
	
		public LImage GetImage(string name) {
			PackEntry entry = GetEntry(name);
			if (entry != null) {
				if (entry.image != null && !entry.image.IsClose()) {
					return entry.image;
				} else if (entry.fileName != null) {
					return LImage.CreateImage(entry.fileName);
				}
			}
			return null;
		}
	
		[MethodImpl(MethodImplOptions.Synchronized)]
		public int PutImage(string res) {
			return PutImage(res, LImage.CreateImage(res));
		}
	
		public int PutImage(LImage image) {
			return PutImage(
					JavaRuntime.CurrentTimeMillis() + "|" + Convert.ToString((count + 1)),
					image);
		}
	
		public int PutImage(string name, LImage image) {
			CheckPacked();
			if (image == null) {
				throw new NullReferenceException();
			}
			if (image.GetWidth() <= 0 || image.GetHeight() <= 0) {
				throw new ArgumentException(
						"width and height must be positive");
			}
			this.temps.Put(name, new PackEntry(image));
			this.packing = true;
			this.count++;
			return temps.Size() - 1;
		}
	
		public int PutImage(string name, LTexture tex2d) {
			if (tex2d != null) {
				return PutImage(name, tex2d.GetImage());
			}
			return count;
		}
	
		public int PutImage(LTexture tex2d) {
			if (tex2d != null) {
				return PutImage(tex2d.GetImage());
			}
			return count;
		}
	
		[MethodImpl(MethodImplOptions.Synchronized)]
		public int RemoveImage(string name) {
			if (name != null) {
				PackEntry e = GetEntry(name);
				if (e != null) {
					if (e.image != null) {
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
		public int RemoveImage(int id) {
			if (id > -1) {
				PackEntry e = GetEntry(id);
				if (e != null) {
					if (e.image != null) {
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
	
		public int Size() {
			return count;
		}
	
		private LImage PackImage() {
			CheckPacked();
			if (packing) {
				if (temps.IsEmpty()) {
					throw new InvalidOperationException("Nothing to Pack !");
				}
				int maxWidth = 0;
				int maxHeight = 0;
				int totalArea = 0;
				for (int i = 0; i < temps.Size(); i++) {
					PackEntry entry = (PackEntry) temps.Get(i);
					int width = entry.image.GetWidth();
					int height = entry.image.GetHeight();
					if (width > maxWidth) {
						maxWidth = width;
					}
					if (height > maxHeight) {
						maxHeight = height;
					}
					totalArea += width * height;
				}
				Loon.Core.Geom.Point.Point2i size = new Loon.Core.Geom.Point.Point2i(CloseTwoPower(maxWidth),
						CloseTwoPower(maxHeight));
				bool fitAll = false;
				loop: {
					while (!fitAll) {
						int area = size.x * size.y;
						if (area < totalArea) {
							NextSize(size);
							continue;
						}
						Node root = new Node(size.x, size.y);
						for (int i = 0; i < temps.Size(); i++) {
							PackEntry entry = (PackEntry) temps.Get(i);
							Node inserted = root.Insert(entry);
							if (inserted == null) {
								NextSize(size);
								goto loop;
							}
						}
						fitAll = true;
					}
				}
                int srcWidth = size.x;
                int srcHeight = size.y;
                //由于LGraphics操作较耗时，此处直接处理像素（其实也快不了几毫秒，未来将改写为C#混合C/C++）
                LImage image = LImage.CreateImage(srcWidth, srcHeight, useAlpha);
                int[] dstPixels = image.GetIntPixels();
                int[] srcPixels = null;
                for (int i = 0; i < temps.Size(); i++)
                {
                    PackEntry entry = (PackEntry)temps.Get(i);
                    LImage img = entry.image;
                    srcPixels = img.GetIntPixels();
                    int w = img.Width;
                    int h = img.Height;
                    int x = entry.bounds.left;
                    int y = entry.bounds.top;
                    if (x < 0)
                    {
                        w += x;
                        x = 0;
                    }
                    if (y < 0)
                    {
                        h += y;
                        y = 0;
                    }
                    if (x + w > srcWidth)
                    {
                        w = srcWidth - x;
                    }
                    if (y + h > srcHeight)
                    {
                        h = srcHeight - y;
                    }
                    if (img.hasAlpha)
                    {
                        int findIndex = y * srcWidth + x;
                        int drawIndex = 0;
                        int moveFind = srcWidth - w;
                        for (int col = 0; col < h; col++)
                        {
                            for (int row = 0; row < w; )
                            {
                                if (srcPixels[drawIndex] != 0)
                                {
                                    dstPixels[findIndex] = srcPixels[drawIndex];
                                }
                                row++;
                                findIndex++;
                                drawIndex++;
                            }
                            findIndex += moveFind;
                        }
                    }
                    else
                    {
                        for (int count = 0; count < h; count++)
                        {
                            System.Array.Copy(srcPixels, count * w, dstPixels,
                                             (y + count) * srcWidth + x, w);
                        }
                    }
                }
                image.SetIntPixels(dstPixels);
                srcPixels = null;
                dstPixels = null;
				packing = false;
				return image;
			}
			return null;
		}
	
		public LTexture Pack() {
            return Pack(format);
		}

        public LTexture Pack(Loon.Core.Graphics.Opengl.LTexture.Format format)
        {
			if (texture != null && !packing) {
				return texture;
			}
			if (fileName != null) {
				texture = new LTexture(GLLoader.GetTextureData(fileName), format);
                texture.isExt = true;
			} else {
				LImage image = PackImage();
				if (image == null) {
					return null;
				}
				if (texture != null) {
					texture.Destroy();
					texture = null;
				}
				if (colorMask != null) {
					Color[] pixels = image.GetPixels();
					int size = pixels.Length;
                    uint color = colorMask.GetRGB();
					for (int i = 0; i < size; i++) {
						if (pixels[i].PackedValue == color) {
							pixels[i].PackedValue = LSystem.TRANSPARENT;
						}
					}
					image.SetPixels(pixels, image.GetWidth(), image.GetHeight());
				}
				texture = new LTexture(GLLoader.GetTextureData(image), format);
                texture.isExt = true;
				if (image != null) {
					image.Dispose();
					image = null;
				}
			}
			return texture;
		}
	
		public LTexture GetTexture() {
			return texture;
		}
	
		public PackEntry GetEntry(int id) {
			return (PackEntry) temps.Get(id);
		}
	
		public PackEntry GetEntry(string name) {
			return (PackEntry) temps.Get(name);
		}
	
		public void GLCache() {
			if (texture != null) {
				texture.GLLock();
				texture.GLCacheCommit();
				texture.GLUnLock();
			}
		}
	
		public bool IsBatch() {
			return texture != null && texture.IsBatch();
		}
	
		public void GLBegin(int mode) {
			if (count > 0) {
				Pack();
				texture.GLBegin(mode);
			}
		}
	
		public void GLBegin() {
			if (count > 0) {
				Pack();
				texture.GLBegin();
			}
		}
	
		public void GLEnd() {
			if (count > 0) {
				texture.GLEnd();
			}
		}
	
		public bool IsBatchLocked() {
			return texture != null && texture.IsBatchLocked();
		}
	
		public Loon.Core.Geom.Point.Point2i Draw(int id, float x, float y) {
			return Draw(id, x, y, 0, null);
		}
	
		public Loon.Core.Geom.Point.Point2i Draw(int id, float x, float y, LColor color) {
			return Draw(id, x, y, 0, color);
		}
	
		public Loon.Core.Geom.Point.Point2i Draw(int id, float x, float y, float rotation, LColor color) {
			this.Pack();
			if (GLEx.Self != null) {
				PackEntry entry = GetEntry(id);
				if (entry == null) {
					return null;
				}
				if (texture.IsBatch()) {
					texture.Draw(x, y, entry.bounds.Width(), entry.bounds.Height(),
							entry.bounds.left, entry.bounds.top,
							entry.bounds.right, entry.bounds.bottom, rotation,
							color);
				} else {
					GLEx.Self.DrawTexture(texture, x, y, entry.bounds.Width(),
							entry.bounds.Height(), entry.bounds.left,
							entry.bounds.top, entry.bounds.right,
							entry.bounds.bottom, rotation, color);
				}
				blittedSize.Set(entry.bounds.Width(), entry.bounds.Height());
			}
			return blittedSize;
		}

        public void DrawOnlyBatch(int id, float x, float y, LColor[] c)
        {
			this.Pack();
			PackEntry entry = GetEntry(id);
			if (entry == null) {
				return;
			}
			if (texture.IsBatch()) {
				texture.Draw(x, y, entry.bounds.Width(), entry.bounds.Height(),
						entry.bounds.left, entry.bounds.top, entry.bounds.right,
						entry.bounds.bottom, c);
			}
		}
	
		public Loon.Core.Geom.Point.Point2i Draw(int id, float x, float y, float w, float h) {
			return Draw(id, x, y, w, h, 0, null);
		}
	
		public Loon.Core.Geom.Point.Point2i Draw(int id, float x, float y, float w, float h, LColor color) {
			return Draw(id, x, y, w, h, 0, color);
		}
	
		public Loon.Core.Geom.Point.Point2i Draw(int id, float x, float y, float w, float h,
				float rotation, LColor color) {
			this.Pack();
			if (GLEx.Self != null) {
				PackEntry entry = GetEntry(id);
				if (entry == null) {
					return null;
				}
				if (texture.IsBatch()) {
					texture.Draw(x, y, w, h, entry.bounds.left, entry.bounds.top,
							entry.bounds.right, entry.bounds.bottom, rotation,
							color);
				} else {
					GLEx.Self.DrawTexture(texture, x, y, w, h, entry.bounds.left,
							entry.bounds.top, entry.bounds.right,
							entry.bounds.bottom, rotation, color);
				}
				blittedSize.Set(entry.bounds.Width(), entry.bounds.Height());
			}
			return blittedSize;
		}
	
		public Loon.Core.Geom.Point.Point2i Draw(int id, float dx1, float dy1, float dx2, float dy2,
				float sx1, float sy1, float sx2, float sy2) {
			return Draw(id, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, 0);
		}
	
		public Loon.Core.Geom.Point.Point2i Draw(int id, float dx1, float dy1, float dx2, float dy2,
				float sx1, float sy1, float sx2, float sy2, float rotation) {
			return Draw(id, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, rotation, null);
		}
	
		public Loon.Core.Geom.Point.Point2i Draw(int id, float dx1, float dy1, float dx2, float dy2,
				float sx1, float sy1, float sx2, float sy2, LColor color) {
			return Draw(id, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, 0, color);
		}
	
		public void DrawOnlyBatch(int id, float dx1, float dy1, float dx2,
				float dy2, float sx1, float sy1, float sx2, float sy2,
				LColor[] color) {
			this.Pack();
			PackEntry entry = GetEntry(id);
			if (entry == null) {
				return;
			}
			if (texture.IsBatch()) {
				texture.Draw(dx1, dy1, dx2, dy2, sx1 + entry.bounds.left, sy1
						+ entry.bounds.top, sx2 + entry.bounds.left, sy2
						+ entry.bounds.top, color);
			}
			blittedSize.Set(entry.bounds.Width(), entry.bounds.Height());
		}
	
		public Loon.Core.Geom.Point.Point2i Draw(int id, float dx1, float dy1, float dx2, float dy2,
				float sx1, float sy1, float sx2, float sy2, float rotation,
				LColor color) {
			this.Pack();
			if (GLEx.Self != null) {
				PackEntry entry = GetEntry(id);
				if (entry == null) {
					return null;
				}
				if (texture.IsBatch()) {
					texture.Draw(dx1, dy1, dx2, dy2, sx1 + entry.bounds.left, sy1
							+ entry.bounds.top, sx2 + entry.bounds.left, sy2
							+ entry.bounds.top, rotation, color);
				} else {
					GLEx.Self.DrawTexture(texture, dx1, dy1, dx2, dy2, sx1
							+ entry.bounds.left, sy1 + entry.bounds.top, sx2
							+ entry.bounds.left, sy2 + entry.bounds.top, rotation,
							color);
				}
				blittedSize.Set(entry.bounds.Width(), entry.bounds.Height());
			}
			return blittedSize;
		}
	
		public Loon.Core.Geom.Point.Point2i Draw(string name, float x, float y) {
			return Draw(name, x, y, 0, null);
		}
	
		public Loon.Core.Geom.Point.Point2i Draw(string name, float x, float y, LColor color) {
			return Draw(name, x, y, 0, color);
		}

        public void DrawOnlyBatch(string name, float x, float y, LColor[] c)
        {
			this.Pack();
			PackEntry entry = GetEntry(name);
			if (texture.IsBatch()) {
				texture.Draw(x, y, entry.bounds.Width(), entry.bounds.Height(),
						entry.bounds.left, entry.bounds.top, entry.bounds.right,
						entry.bounds.bottom, c);
			}
		}
	
		public Loon.Core.Geom.Point.Point2i Draw(string name, float x, float y, float rotation,
				LColor color) {
			this.Pack();
			if (GLEx.Self != null) {
				PackEntry entry = GetEntry(name);
				if (entry == null) {
					return null;
				}
				if (texture.IsBatch()) {
					texture.Draw(x, y, entry.bounds.Width(), entry.bounds.Height(),
							entry.bounds.left, entry.bounds.top,
							entry.bounds.right, entry.bounds.bottom, rotation,
							color);
				} else {
					GLEx.Self.DrawTexture(texture, x, y, entry.bounds.Width(),
							entry.bounds.Height(), entry.bounds.left,
							entry.bounds.top, entry.bounds.right,
							entry.bounds.bottom, rotation, color);
				}
				blittedSize.Set(entry.bounds.Width(), entry.bounds.Height());
			}
			return blittedSize;
		}
	
		public Loon.Core.Geom.Point.Point2i Draw(string name, float x, float y, float w, float h) {
			return Draw(name, x, y, w, h, 0, null);
		}
	
		public Loon.Core.Geom.Point.Point2i Draw(string name, float x, float y, float w, float h,
				LColor color) {
			return Draw(name, x, y, w, h, 0, color);
		}
	
		public Loon.Core.Geom.Point.Point2i Draw(string name, float x, float y, float w, float h,
				float rotation, LColor color) {
			this.Pack();
			if (GLEx.Self != null) {
				PackEntry entry = GetEntry(name);
				if (entry == null) {
					return null;
				}
				if (texture.IsBatch()) {
					texture.Draw(x, y, w, h, entry.bounds.left, entry.bounds.top,
							entry.bounds.right, entry.bounds.bottom, rotation,
							color);
				} else {
					GLEx.Self.DrawTexture(texture, x, y, w, h, entry.bounds.left,
							entry.bounds.top, entry.bounds.right,
							entry.bounds.bottom, rotation, color);
				}
				blittedSize.Set(entry.bounds.Width(), entry.bounds.Height());
			}
			return blittedSize;
		}
	
		public Loon.Core.Geom.Point.Point2i Draw(string name, float dx1, float dy1, float dx2,
				float dy2, float sx1, float sy1, float sx2, float sy2) {
			return Draw(name, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, 0);
		}
	
		public Loon.Core.Geom.Point.Point2i Draw(string name, float dx1, float dy1, float dx2,
				float dy2, float sx1, float sy1, float sx2, float sy2,
				float rotation) {
			return Draw(name, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, 0, null);
		}
	
		public Loon.Core.Geom.Point.Point2i Draw(string name, float dx1, float dy1, float dx2,
				float dy2, float sx1, float sy1, float sx2, float sy2, LColor color) {
			return Draw(name, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, 0, color);
		}

        public void DrawOnlyBatch(string name, float dx1, float dy1, float dx2,
				float dy2, float sx1, float sy1, float sx2, float sy2,
				LColor[] color) {
			this.Pack();
			PackEntry entry = GetEntry(name);
			if (entry == null) {
				return;
			}
			if (texture.IsBatch()) {
				texture.Draw(dx1, dy1, dx2, dy2, sx1 + entry.bounds.left, sy1
						+ entry.bounds.top, sx2 + entry.bounds.left, sy2
						+ entry.bounds.top, color);
			}
			blittedSize.Set(entry.bounds.Width(), entry.bounds.Height());
		}
	
		public Loon.Core.Geom.Point.Point2i Draw(string name, float dx1, float dy1, float dx2,
				float dy2, float sx1, float sy1, float sx2, float sy2,
				float rotation, LColor color) {
			this.Pack();
			if (GLEx.Self != null) {
				PackEntry entry = GetEntry(name);
				if (entry == null) {
					return null;
				}
				if (texture.IsBatch()) {
					texture.Draw(dx1, dy1, dx2, dy2, sx1 + entry.bounds.left, sy1
							+ entry.bounds.top, sx2 + entry.bounds.left, sy2
							+ entry.bounds.top, rotation, color);
				} else {
					GLEx.Self.DrawTexture(texture, dx1, dy1, dx2, dy2, sx1
							+ entry.bounds.left, sy1 + entry.bounds.top, sx2
							+ entry.bounds.left, sy2 + entry.bounds.top, rotation,
							color);
				}
				blittedSize.Set(entry.bounds.Width(), entry.bounds.Height());
			}
			return blittedSize;
		}
	
		public RectBox GetImageRect(int id) {
			PackEntry entry = GetEntry(id);
			if (entry == null) {
				return new RectBox(0, 0, 0, 0);
			}
			return new RectBox(0, 0, entry.width, entry.height);
		}
	
		public int[] GetImageRectArray(int id) {
			PackEntry entry = GetEntry(id);
			if (entry == null) {
				return new int[] { 0, 0 };
			}
			return new int[] { entry.width, entry.height };
		}
	
		public Loon.Core.Geom.RectBox.Rect2i GetImageSize(int id) {
			PackEntry entry = GetEntry(id);
			if (entry == null) {
				return null;
			}
			return entry.bounds;
		}
	
		public Loon.Core.Geom.RectBox.Rect2i GetImageSize(string name) {
			PackEntry entry = GetEntry(name);
			if (entry == null) {
				return null;
			}
			return entry.bounds;
		}
	
		private void NextSize(Loon.Core.Geom.Point.Point2i size) {
			if (size.x > size.y) {
				size.y <<= 1;
			} else {
				size.x <<= 1;
			}
		}
	
		public string GetFileName() {
			return fileName;
		}
	
		private int CloseTwoPower(int i) {
			int power = 1;
			while (power < i) {
				power <<= 1;
			}
			return power;
		}
	
		private void CheckPacked() {
			if (packed) {
				throw new InvalidOperationException("the packed !");
			}
		}
	
		public void Packed() {
            this.Packed(Loon.Core.Graphics.Opengl.LTexture.Format.DEFAULT);
		}
	
		[MethodImpl(MethodImplOptions.Synchronized)]
		public void Packed(Loon.Core.Graphics.Opengl.LTexture.Format format) {
			this.Pack(format);
			this.packed = true;
			this.Free();
		}
	
		public class PackEntry {
	
			internal readonly Loon.Core.Geom.RectBox.Rect2i bounds = new Loon.Core.Geom.RectBox.Rect2i();

            internal LImage image;

            internal string fileName;

            internal int id;

            internal int width, height;

            internal PackEntry(LImage image)
            {
				this.image = image;
				if (image != null) {
					this.fileName = image.GetPath();
					this.width = image.GetWidth();
					this.height = image.GetHeight();
				}
			}
	
			public int Id() {
				return id;
			}
	
			public string Name() {
				return fileName;
			}
	
			public Loon.Core.Geom.RectBox.Rect2i getBounds() {
				return bounds;
			}
	
			public int Width() {
				return bounds.Width();
			}
	
			public int Height() {
				return bounds.Height();
			}
		}
	
		private class Node {

            internal readonly Node[] child = new Node[2];

            internal readonly Loon.Core.Geom.RectBox.Rect2i bounds = new Loon.Core.Geom.RectBox.Rect2i();

            internal PackEntry entry;

            internal Node()
            {
			}

            internal Node(int width, int height)
            {
				bounds.Set(0, 0, width, height);
			}

            internal bool IsLeaf()
            {
				return (child[0] == null) && (child[1] == null);
			}

            internal Node Insert(PackEntry entry)
            {
				if (IsLeaf()) {
					if (this.entry != null) {
						return null;
					}
					int width = entry.image.GetWidth();
					int height = entry.image.GetHeight();
	
					if ((width > bounds.Width()) || (height > bounds.Height())) {
						return null;
					}
	
					if ((width == bounds.Width()) && (height == bounds.Height())) {
						this.entry = entry;
						this.entry.bounds.Set(this.bounds);
						return this;
					}
	
					child[0] = new Node();
					child[1] = new Node();
	
					int dw = bounds.Width() - width;
					int dh = bounds.Height() - height;
	
					if (dw > dh) {
						child[0].bounds.Set(bounds.left, bounds.top, bounds.left
								+ width, bounds.bottom);
						child[1].bounds.Set(bounds.left + width, bounds.top,
								bounds.right, bounds.bottom);
					} else {
						child[0].bounds.Set(bounds.left, bounds.top, bounds.right,
								bounds.top + height);
						child[1].bounds.Set(bounds.left, bounds.top + height,
								bounds.right, bounds.bottom);
					}
					return child[0].Insert(entry);
				} else {
					Node newNode = child[0].Insert(entry);
					if (newNode != null) {
						return newNode;
					}
					return child[1].Insert(entry);
				}
			}
	
		}
	
		public string GetName() {
			return name;
		}
	
		public LColor GetColorMask() {
			return colorMask;
		}
	
		public void SetColorMask(LColor colorMask) {
			this.colorMask = colorMask;
		}
	
		private void Free() {
			if (temps != null) {
				for (int i = 0; i < temps.Size(); i++) {
					PackEntry e = (PackEntry) temps.Get(i);
					if (e != null) {
						if (e.image != null) {
							e.image.Dispose();
							e.image = null;
						}
					}
				}
			}
		}
	
		public override string ToString() {
			StringBuilder sbr = new StringBuilder(1000);
			sbr.Append("<?xml version=\"1.0\" standalone=\"yes\" ?>\n");
			if (colorMask != null) {
				sbr.Append("<Pack file=\"" + fileName + "\" mask=\""
						+ colorMask.R + "," + colorMask.G + ","
						+ colorMask.B + "\">\n");
			} else {
				sbr.Append("<Pack file=\"" + fileName + "\">\n");
			}
			for (int i = 0; i < temps.Size(); i++) {
				PackEntry e = (PackEntry) temps.Get(i);
				if (e != null && e.bounds != null) {
					sbr.Append("<block id=\"" + i + "\" name=\"" + e.fileName
							+ "\" left=\"" + e.bounds.left + "\" top=\""
							+ e.bounds.top + "\" right=\"" + e.bounds.right
							+ "\" bottom=\"" + e.bounds.bottom + "\"/>\n");
				}
			}
			sbr.Append("</Pack>");
			return sbr.ToString();
		}
	
		[MethodImpl(MethodImplOptions.Synchronized)]
		public void Dispose() {
			Free();
			if (texture != null) {
				texture.Destroy();
				texture = null;
			}
		}
	
		public Loon.Core.Graphics.Opengl.LTexture.Format GetFormat() {
			return format;
		}
	
		public void SetFormat(Loon.Core.Graphics.Opengl.LTexture.Format format) {
			this.format = format;
		}
	
	}
}
