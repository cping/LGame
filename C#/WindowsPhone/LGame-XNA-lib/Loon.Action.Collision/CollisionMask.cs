namespace Loon.Action.Collision {

    using System.Collections.Generic;
	using Loon.Action.Sprite;
	using Loon.Core;
	using Loon.Core.Geom;
    using Loon.Core.Graphics;
    using Loon.Utils;
    using Loon.Java;
    using Loon.Core.Graphics.Opengl;
    using Microsoft.Xna.Framework.Graphics;

	public class CollisionMask : LRelease {
	
		private int top, left, right, bottom;
	
		private LTexture.Mask data;
	
		private RectBox rect;
	
		public static Polygon MakePolygon(string res) {
            return MakePolygon(LImage.CreateImage(res));
		}

        public static Polygon MakePolygon(Texture2D tex2d)
        {
            if (tex2d == null)
            {
                throw new RuntimeException("Image is null !");
            }
            int[] pixels = new int[tex2d.Width * tex2d.Height];
            tex2d.GetData<int>(pixels);
            return MakePolygon(pixels, tex2d.Width,
                    tex2d.Height);
        }

		public static Polygon MakePolygon(LImage image) {
			if (image == null) {
                throw new RuntimeException("Image is null !");
			}
			return MakePolygon(image.GetIntPixels(), image.GetWidth(),
					image.GetHeight());
		}
	
		public static Polygon MakePolygon(int[] pixels, int w, int h) {
			return MakePolygon(pixels, 0, 0, 0, 0, w, h, 3);
		}
	
		public static Polygon MakePolygon(int[] pixels, int offsetX, int offsetY,
				int startX, int startY, int limitX, int limitY, int interval) {
			Polygon split = null;
			Polygon result = null;
			List<Point[]> points = new List<Point[]>();
			Point[] tmpPoint;
			int x1, y1, x2, y2;
			bool secondPoint;
			int pixel = 0;
			for (int y = startY; y < limitY - interval; y += interval) {
	
				secondPoint = false;
				x1 = y1 = -1;
				x2 = y2 = -1;
				for (int x = startX; x < limitX; x++) {
					pixel = pixels[x + limitX * y];
					if (!secondPoint) {
                        if ((pixel & LSystem.TRANSPARENT) == LSystem.TRANSPARENT)
                        {
							x1 = x;
							y1 = y;
							secondPoint = true;
						}
					} else {
                        if ((pixel & LSystem.TRANSPARENT) == LSystem.TRANSPARENT)
                        {
							x2 = x;
							y2 = y;
						}
					}
				}
				if (secondPoint && (x2 > -1) && (y2 > -1)) {
					tmpPoint = new Point[2];
					tmpPoint[0] = new Point(offsetX + x1, offsetY + y1);
					tmpPoint[1] = new Point(offsetX + x2, offsetY + y2);
					CollectionUtils.Add(points,tmpPoint);
				}
			}
			split = MakePolygon(points);
			if (split != null) {
				points = new List<Point[]>();
				for (int x_0 = startX; x_0 < limitX - interval; x_0 += interval) {
					secondPoint = false;
					x1 = y1 = -1;
					x2 = y2 = -1;
					for (int y_1 = startY; y_1 < limitY; y_1++) {
						pixel = pixels[x_0 + limitX * y_1];
						if (!secondPoint) {
							if ((pixel & LSystem.TRANSPARENT) == LSystem.TRANSPARENT) {
								x1 = x_0;
								y1 = y_1;
								secondPoint = true;
							}
						} else {
							if ((pixel & LSystem.TRANSPARENT) == LSystem.TRANSPARENT) {
								x2 = x_0;
								y2 = y_1;
							}
						}
					}
					if (secondPoint && (x2 > -1) && (y2 > -1)) {
						tmpPoint = new Point[2];
						tmpPoint[0] = new Point(offsetX + x1, offsetY + y1);
						tmpPoint[1] = new Point(offsetX + x2, offsetY + y2);
                        CollectionUtils.Add(points, tmpPoint);
					}
				}
				result = MakePolygon(points);
	
			}
			return result;
		}
	
		/// <summary>
		/// 将指定的Point集合注入Polygon当中
		/// </summary>
		///
		/// <param name="points"></param>
		/// <returns></returns>
		private static Polygon MakePolygon(List<Point[]> points) {
			Polygon polygon = null;
			if (!(points.Count==0)) {
				int size = points.Count;
				polygon = new Polygon();
				for (int i = 0; i < size; i++) {
					Point p = ((Point[]) points[i])[0];
					polygon.AddPoint(p.x, p.y);
				}
				for (int i_0 = size - 1; i_0 >= 0; i_0--) {
					Point p_1 = ((Point[]) points[i_0])[1];
					polygon.AddPoint(p_1.x, p_1.y);
				}
			}
			return polygon;
		}
	
		public static LTexture.Mask CreateMask(string res) {
            return CreateMask(LImage.CreateImage(res));
		}

        public static LTexture.Mask CreateMask(Texture2D tex2d)
        {
            if (tex2d == null)
            {
                throw new RuntimeException("Image is null !");
            }
            int[] pixels = new int[tex2d.Width * tex2d.Height];
            tex2d.GetData<int>(pixels);
            return CreateMask(pixels, tex2d.Width,
                    tex2d.Height);
        }

        public static LTexture.Mask CreateMask(LImage image)
        {
			if (image == null) {
				throw new RuntimeException("Image is null !");
			}
			return CreateMask(image.GetIntPixels(), image.GetWidth(),
					image.GetHeight());
		}
	
		public static LTexture.Mask CreateMask(int[] pixels, int w, int h) {
			int width = w;
			int height = h;
			LTexture.Mask d = new LTexture.Mask(width, height);
			bool[][] mask = (bool[][])CollectionUtils.XNA_CreateJaggedArray(typeof(bool), height, width);
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					mask[y][x] = (pixels[x + w * y] & LSystem.TRANSPARENT) == LSystem.TRANSPARENT;
				}
			}
			d.SetData(mask);
			return d;
		}
	
		public CollisionMask(LTexture.Mask d) {
			Set(d, 0, 0, d.GetWidth(), d.GetHeight());
		}
	
		public void Set(LTexture.Mask d, int x, int y, int w, int h) {
			this.data = d;
			if (rect == null) {
				this.rect = new RectBox(x, y, w, h);
			} else {
				this.rect.SetBounds(x, y, w, h);
			}
		}
	
		public RectBox GetBounds() {
			return rect;
		}
	
		private void CalculateBoundingBox() {
			top = (int) (rect.y - rect.height / 2);
			left = (int) (rect.x - rect.width / 2);
			right = (left + rect.width);
			bottom = (top + rect.height);
		}
	
		public bool CheckBoundingBoxCollision(CollisionMask other) {
			return rect.Intersects(other.GetBounds())
					|| rect.Contains(other.GetBounds());
		}
	
		public bool CheckBoundingBoxCollision(int x, int y) {
			return rect.Intersects(x, y) || rect.Contains(x, y);
		}
	
		public bool CollidesWith(CollisionMask other) {
			if (CheckBoundingBoxCollision(other)) {
				other.CalculateBoundingBox();
				CalculateBoundingBox();
				bool a = false;
				bool b = false;
				for (int y = top; y < bottom; y++) {
					for (int x = left; x < right; x++) {
						a = data.GetPixel(x - left, y - top);
						b = other.data.GetPixel(x - other.left, y - other.top);
						if (a && b) {
							return true;
						}
					}
				}
			}
			return false;
		}
	
		public bool CollidesWith(int x, int y) {
			if (CheckBoundingBoxCollision(x, y)) {
				CalculateBoundingBox();
				return data.GetPixel(x - left, y - top);
			}
			return false;
		}
	
		public virtual void Dispose() {
			if (data != null) {
				data.Dispose();
				data = null;
			}
		}
	
	}
}
