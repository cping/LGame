using Loon.Core;
using Loon.Core.Graphics.Opengl;
using Loon.Utils;
using System;
using Loon.Core.Graphics;
namespace Loon.Action.Sprite {
	
	public class SpriteSheet : LRelease {
	
		private int margin, spacing;
	
		private int tw, th;
	
		private int width, height;
	
		private LTexture[][] subImages;
	
		private LTexture target;
	
		public SpriteSheet(string fileName, int tw, int th, int s, int m) :	this(new LTexture(fileName), tw, th, s, m){
		
		}
	
		public SpriteSheet(string fileName, int tw, int th):this(new LTexture(fileName), tw, th, 0, 0) {
			
		}
	
		public SpriteSheet(LTexture image, int tw, int th) :this(image, tw, th, 0, 0){
			
		}
	
		public SpriteSheet(LTexture img, int tw, int th, int s, int m) {
			this.width = img.GetWidth();
			this.height = img.GetHeight();
			this.target = img;
			this.tw = tw;
			this.th = th;
			this.margin = m;
			this.spacing = s;
		}

        private void Update()
        {
            if (subImages != null)
            {
                return;
            }
            if (!target.isLoaded)
            {
                target.LoadTexture();
            }
            int tilesAcross = ((width - (margin * 2) - tw) / (tw + spacing)) + 1;
            int tilesDown = ((height - (margin * 2) - th) / (th + spacing)) + 1;
            if ((height - th) % (th + spacing) != 0)
            {
                tilesDown++;
            }
            subImages = (LTexture[][])CollectionUtils.XNA_CreateJaggedArray(typeof(LTexture), tilesAcross, tilesDown);
            for (int x = 0; x < tilesAcross; x++)
            {
                for (int y = 0; y < tilesDown; y++)
                {
                    subImages[x][y] = GetImage(x, y);
                }
            }
        }
	
		public LTexture[][] GetTextures() {
			return subImages;
		}
	
		private void CheckImage(int x, int y) {
			Update();
			if ((x < 0) || (x >= subImages.Length)) {
				throw new Exception("SubImage out of sheet bounds " + x
						+ "," + y);
			}
            if ((y < 0) || (y >= subImages[0].Length))
            {
				throw new Exception("SubImage out of sheet bounds " + x
						+ "," + y);
			}
		}
	
		public LTexture GetImage(int x, int y) {
			CheckImage(x, y);
            if ((x < 0) || (x >= subImages.Length))
            {
				throw new Exception("SubTexture2D out of sheet bounds: " + x
						+ "," + y);
			}
            if ((y < 0) || (y >= subImages[0].Length))
            {
				throw new Exception("SubTexture2D out of sheet bounds: " + x
						+ "," + y);
			}
			return target.GetSubTexture(x * (tw + spacing) + margin, y
					* (th + spacing) + margin, tw, th);
		}
	
		public int GetHorizontalCount() {
			Update();
            return subImages.Length;
		}
	
		public int GetVerticalCount() {
			Update();
            return subImages[0].Length;
		}
	
		public LTexture GetSubImage(int x, int y) {
			CheckImage(x, y);
			return subImages[x][y];
		}
	
		public void Draw(GLEx g, int x, int y, int sx, int sy) {
			Draw(g, x, y, sx, sy, null);
		}
	
		public void Draw(GLEx g, int x, int y, int sx, int sy, LColor[] color) {
			if (target.IsBatch()) {
				float nx = sx * tw;
				float ny = sy * th;
				target.Draw(x, y, tw, th, nx, ny, nx + tw, ny + th, color);
			} else {
				CheckImage(sx, sy);
				g.DrawTexture(subImages[sx][sy], x, y);
			}
		}

        public void GLBegin()
        {
            target.GLBegin(GL.GL_TRIANGLE_FAN);
        }
	
		public void GLEnd() {
			target.GLEnd();
		}
	
		public int GetMargin() {
			return margin;
		}
	
		public void SetMargin(int margin) {
			this.margin = margin;
		}
	
		public int GetSpacing() {
			return spacing;
		}
	
		public void SetSpacing(int spacing) {
			this.spacing = spacing;
		}
	
		public Loon.Core.Graphics.Opengl.LTextureBatch.GLCache NewCache() {
			return target.NewBatchCache();
		}

        public Loon.Core.Graphics.Opengl.LTextureBatch.GLCache NewCache(bool reset)
        {
			return target.NewBatchCache(reset);
		}
	
		public LTexture GetTarget() {
			return target;
		}
	
		public void SetTarget(LTexture target) {
			if (this.target != null) {
				this.target.Destroy();
				this.target = null;
			}
			this.target = target;
		}
	
		public void Dispose() {
			if (subImages != null) {
				 lock (subImages) {
								for (int i = 0; i < subImages.Length; i++) {
                                    for (int j = 0; j < subImages[i].Length; j++)
                                    {
										subImages[i][j].Destroy();
									}
								}
								this.subImages = null;
							}
			}
			if (target != null) {
				target.Destroy();
				target = null;
			}
		}
	}
}
