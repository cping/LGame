using System;
using System.Collections.Generic;
using Loon.Core.Graphics.Opengl;
using Loon.Utils;
using Loon.Core.Graphics.Device;
namespace Loon.Core.Graphics {
	
	public class LGradation : LRelease {
	
		private static Dictionary<string, LGradation> gradations;
	
		private LColor start;
	
		private LColor end;
	
		private int width, height, alpha;
	
		private LTexture drawTexWidth, drawTexHeight;
	
		private LImage drawImgWidth, drawImgHeight;
	
		public static LGradation GetInstance(LColor s, LColor e, int w, int h) {
            return GetInstance(s, e, w, h, 125);
		}
	
		public static LGradation GetInstance(LColor s, LColor e, int w, int h,
				int alpha) {
			if (gradations == null) {
                gradations = new Dictionary<string, LGradation>(10);
			}
			int hashCode = 1;
			hashCode = LSystem.Unite(hashCode, s.GetRGB());
			hashCode = LSystem.Unite(hashCode, e.GetRGB());
			hashCode = LSystem.Unite(hashCode, w);
			hashCode = LSystem.Unite(hashCode, h);
			hashCode = LSystem.Unite(hashCode, alpha);
			string key = Convert.ToString(hashCode);
			LGradation o = (LGradation) CollectionUtils.Get(gradations,key);
			if (o == null) {
                CollectionUtils.Put(gradations,key, o = new LGradation(s, e, w, h, alpha));
			}
			return o;
		}
	
		private LGradation() {
	
		}
	
		private LGradation(LColor s, LColor e, int w, int h, int alpha) {
			this.start = s;
			this.end = e;
			this.width = w;
			this.height = h;
			this.alpha = alpha;
		}
	
		public void DrawWidth(GLEx g, int x, int y) {
			try {
				if (drawTexWidth == null) {
					LImage img = LImage.CreateImage(width, height, true);
					LGraphics gl = img.GetLGraphics();
					for (int i = 0; i < width; i++) {
						gl.SetColor(
								(start.GetRed() * (width - i)) / width
										+ (end.GetRed() * i) / width,
								(start.GetGreen() * (width - i)) / width
										+ (end.GetGreen() * i) / width,
								(start.GetBlue() * (width - i)) / width
										+ (end.GetBlue() * i) / width, alpha);
						gl.DrawLine(i, 0, i, height);
					}
					drawTexWidth = new LTexture(GLLoader.GetTextureData(img),
							Loon.Core.Graphics.Opengl.LTexture.Format.SPEED);
					gl.Dispose();
					gl = null;
				}
				g.DrawTexture(drawTexWidth, x, y);
			} catch (Exception) {
				for (int i = 0; i < width; i++) {
					g.SetColorValue(
							(start.GetRed() * (width - i)) / width
									+ (end.GetRed() * i) / width,
							(start.GetGreen() * (width - i)) / width
									+ (end.GetGreen() * i) / width,
							(start.GetBlue() * (width - i)) / width
									+ (end.GetBlue() * i) / width, alpha);
					g.DrawLine(i + x, y, i + x, y + height);
				}
			}
		}
	
		public void DrawHeight(GLEx g, int x, int y) {
			try {
				if (drawTexHeight == null) {
					LImage img = LImage.CreateImage(width, height, true);
					LGraphics gl = img.GetLGraphics();
					for (int i = 0; i < height; i++) {
						gl.SetColor(
								(start.GetRed() * (height - i)) / height
										+ (end.GetRed() * i) / height,
								(start.GetGreen() * (height - i)) / height
										+ (end.GetGreen() * i) / height,
								(start.GetBlue() * (height - i)) / height
										+ (end.GetBlue() * i) / height, alpha);
						gl.DrawLine(0, i, width, i);
					}
					drawTexHeight = new LTexture(GLLoader.GetTextureData(img),
							Loon.Core.Graphics.Opengl.LTexture.Format.SPEED);
					gl.Dispose();
					gl = null;
				}
				g.DrawTexture(drawTexHeight, x, y);
			} catch (Exception) {
				for (int i = 0; i < height; i++) {
					g.SetColorValue(
							(start.GetRed() * (height - i)) / height
									+ (end.GetRed() * i) / height,
							(start.GetGreen() * (height - i)) / height
									+ (end.GetGreen() * i) / height,
							(start.GetBlue() * (height - i)) / height
									+ (end.GetBlue() * i) / height, alpha);
					g.DrawLine(x, i + y, x + width, i + y);
				}
			}
		}
	
		public void DrawWidth(LGraphics g, int x, int y) {
			try {
				if (drawImgWidth == null) {
					drawImgWidth = LImage.CreateImage(width, height, true);
					LGraphics gl = drawImgWidth.GetLGraphics();
					for (int i = 0; i < width; i++) {
						gl.SetColor(
								(start.GetRed() * (width - i)) / width
										+ (end.GetRed() * i) / width,
								(start.GetGreen() * (width - i)) / width
										+ (end.GetGreen() * i) / width,
								(start.GetBlue() * (width - i)) / width
										+ (end.GetBlue() * i) / width, alpha);
						gl.DrawLine(i, 0, i, height);
					}
					gl.Dispose();
					gl = null;
				}
				g.DrawImage(drawImgWidth, x, y);
			} catch (Exception) {
				for (int i = 0; i < width; i++) {
					g.SetColor(
							(start.GetRed() * (width - i)) / width
									+ (end.GetRed() * i) / width,
							(start.GetGreen() * (width - i)) / width
									+ (end.GetGreen() * i) / width,
							(start.GetBlue() * (width - i)) / width
									+ (end.GetBlue() * i) / width, alpha);
					g.DrawLine(i + x, y, i + x, y + height);
				}
			}
		}
	
		public void DrawHeight(LGraphics g, int x, int y) {
			try {
				if (drawImgHeight == null) {
					drawImgHeight = LImage.CreateImage(width, height, true);
					LGraphics gl = drawImgHeight.GetLGraphics();
					for (int i = 0; i < height; i++) {
						gl.SetColor(
								(start.GetRed() * (height - i)) / height
										+ (end.GetRed() * i) / height,
								(start.GetGreen() * (height - i)) / height
										+ (end.GetGreen() * i) / height,
								(start.GetBlue() * (height - i)) / height
										+ (end.GetBlue() * i) / height, alpha);
						gl.DrawLine(0, i, width, i);
					}
					gl.Dispose();
					gl = null;
				}
				g.DrawImage(drawImgHeight, x, y);
			} catch (Exception) {
				for (int i = 0; i < height; i++) {
					g.SetColor(
							(start.GetRed() * (height - i)) / height
									+ (end.GetRed() * i) / height,
							(start.GetGreen() * (height - i)) / height
									+ (end.GetGreen() * i) / height,
							(start.GetBlue() * (height - i)) / height
									+ (end.GetBlue() * i) / height, alpha);
					g.DrawLine(x, i + y, x + width, i + y);
				}
			}
		}
	
		public static void Close() {
            if (gradations == null)
            {
                return;
            }
            foreach (LGradation g in gradations.Values)
            {
                if (g != null)
                {
                    g.Dispose();
                }
            }
            gradations.Clear();
		}
	
		public void Dispose() {
			if (drawTexWidth != null) {
				drawTexWidth.Destroy();
			}
			if (drawTexHeight != null) {
				drawTexHeight.Destroy();
			}
			if (drawImgWidth != null) {
				drawImgWidth.Dispose();
			}
			if (drawImgWidth != null) {
				drawImgWidth.Dispose();
			}
		}
	
	}
}
