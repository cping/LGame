using Loon.Core;
using Loon.Action.Map;
using Loon.Core.Geom;
using Loon.Core.Graphics;
namespace Loon.Action.Sprite {

	public abstract class SpriteBatchObject : LObject,
			LRelease, ActionBind {
	
		internal float scaleX = 1, scaleY = 1;
	
		public void SetScale(float s) {
			this.SetScale(s, s);
		}
	
		public void SetScale(float sx, float sy) {
			if (this.scaleX == sx && this.scaleY == sy) {
				return;
			}
			this.scaleX = sx;
			this.scaleY = sy;
		}
	
		public float GetScaleX() {
			return this.scaleX;
		}
	
		public float GetScaleY() {
			return this.scaleY;
		}
	
		protected internal Attribute attribute;
	
		protected internal Animation animation;
	
		protected internal TileMap tiles;
	
		protected internal RectBox rectBox;
	
		protected internal float dstWidth, dstHeight;
	
		protected internal bool mirror;
	
		private LColor filterColor = new LColor(1f, 1f, 1f, 1f);
	
		public SpriteBatchObject(float x, float y, float dw, float dh,
				Animation animation, TileMap map) {
			this.SetLocation(x, y);
			this.tiles = map;
			this.animation = animation;
			this.dstWidth = dw;
			this.dstHeight = dh;
            Loon.Core.Graphics.Opengl.LTexture texture = animation.GetSpriteImage();
			if (dw < 1 && dh < 1) {
                this.rectBox = new RectBox(x, y, texture
                        .GetWidth(), texture.GetHeight());
			} else {
				this.rectBox = new RectBox(x, y, dw, dh);
			}
		}

        public SpriteBatchObject(float x, float y,
            Animation animation, TileMap map)
        {
            this.SetLocation(x, y);
            this.tiles = map;
            this.animation = animation;
            Loon.Core.Graphics.Opengl.LTexture texture = animation.GetSpriteImage();
            this.dstWidth = texture.GetWidth();
            this.dstHeight = texture.GetHeight();
            if (dstWidth < 1 && dstHeight < 1)
            {
                this.rectBox = new RectBox(x, y, texture
                        .GetWidth(), texture.GetHeight());
            }
            else
            {
                this.rectBox = new RectBox(x, y, dstWidth, dstHeight);
            }
        }

		public void Draw(SpriteBatch batch, float offsetX, float offsetY) {
			if (alpha != 1f) {
				batch.SetAlpha(alpha);
			}
			if (!filterColor.Equals(1f, 1f, 1f, 1f)) {
				batch.SetColor(filterColor);
			}
			if (scaleX == 1 && scaleY == 1) {
				if (mirror) {
					if (GetRotation() != 0) {
						if (dstWidth < 1 && dstHeight < 1) {
							batch.DrawFlipX(animation.GetSpriteImage(), GetX()
									+ offsetX, GetY() + offsetY, GetRotation());
						} else {
							batch.DrawFlipX(animation.GetSpriteImage(), GetX()
									+ offsetX, GetY() + offsetY, dstWidth,
									dstHeight, GetRotation());
						}
					} else {
						if (dstWidth < 1 && dstHeight < 1) {
							batch.DrawFlipX(animation.GetSpriteImage(), GetX()
									+ offsetX, GetY() + offsetY);
						} else {
							batch.DrawFlipX(animation.GetSpriteImage(), GetX()
									+ offsetX, GetY() + offsetY, dstWidth,
									dstHeight);
						}
					}
				} else {
					if (GetRotation() != 0) {
						if (dstWidth < 1 && dstHeight < 1) {
							batch.Draw(animation.GetSpriteImage(),
									GetX() + offsetX, GetY() + offsetY,
									GetRotation());
						} else {
							batch.Draw(animation.GetSpriteImage(),
									GetX() + offsetX, GetY() + offsetY, dstWidth,
									dstHeight, GetRotation());
						}
					} else {
						if (dstWidth < 1 && dstHeight < 1) {
							batch.Draw(animation.GetSpriteImage(),
									GetX() + offsetX, GetY() + offsetY);
						} else {
							batch.Draw(animation.GetSpriteImage(),
									GetX() + offsetX, GetY() + offsetY, dstWidth,
									dstHeight);
						}
					}
				}
			} else {
				float width = animation.GetSpriteImage().GetWidth();
				float height = animation.GetSpriteImage().GetHeight();
				if (mirror) {
					if (GetRotation() != 0) {
						if (dstWidth < 1 && dstHeight < 1) {
							batch.DrawFlipX(animation.GetSpriteImage(), GetX()
									+ offsetX, GetY() + offsetY, width * scaleX,
									height * scaleY, GetRotation());
						} else {
							batch.DrawFlipX(animation.GetSpriteImage(), GetX()
									+ offsetX, GetY() + offsetY, dstWidth * scaleX,
									dstHeight * scaleY, GetRotation());
						}
					} else {
						if (dstWidth < 1 && dstHeight < 1) {
							batch.DrawFlipX(animation.GetSpriteImage(), GetX()
									+ offsetX, width * scaleX, height * scaleY,
									GetY() + offsetY);
						} else {
							batch.DrawFlipX(animation.GetSpriteImage(), GetX()
									+ offsetX, GetY() + offsetY, dstWidth * scaleX,
									dstHeight * scaleY);
						}
					}
				} else {
					if (GetRotation() != 0) {
						if (dstWidth < 1 && dstHeight < 1) {
							batch.Draw(animation.GetSpriteImage(),
									GetX() + offsetX, GetY() + offsetY, width
											* scaleX, height * scaleY,
									GetRotation());
						} else {
							batch.Draw(animation.GetSpriteImage(),
									GetX() + offsetX, GetY() + offsetY, dstWidth
											* scaleX, dstHeight * scaleY,
									GetRotation());
						}
					} else {
						if (dstWidth < 1 && dstHeight < 1) {
							batch.Draw(animation.GetSpriteImage(),
									GetX() + offsetX, GetY() + offsetY, width
											* scaleX, height * scaleY);
						} else {
							batch.Draw(animation.GetSpriteImage(),
									GetX() + offsetX, GetY() + offsetY, dstWidth
											* scaleX, dstHeight * scaleY);
						}
					}
				}
			}
			if (alpha != 1f || !filterColor.Equals(1f, 1f, 1f, 1f)) {
				batch.ResetColor();
			}
		}
	
		public TileMap GetTileMap() {
			return tiles;
		}
	
		public Field2D GetField2D() {
			return tiles.GetField();
		}
	
		public void SetFilterColor(LColor f) {
			this.filterColor.SetColor(f);
		}
	
		public LColor GetFilterColor() {
			return this.filterColor;
		}
	
		public void SetSize(int width, int height) {
			this.dstWidth = width;
			this.dstHeight = height;
		}
	
		public bool IsCollision(SpriteBatchObject o) {
			RectBox src = GetCollisionArea();
			RectBox dst = o.GetCollisionArea();
			if (src.Intersects(dst)) {
				return true;
			}
			return false;
		}
	
		public override int GetWidth() {
			return (int) (((dstWidth > 1) ? (int) dstWidth : animation
					.GetSpriteImage().GetWidth()) * scaleX);
		}

        public override int GetHeight()
        {
			return (int) (((dstHeight > 1) ? (int) dstHeight : animation
					.GetSpriteImage().GetHeight()) * scaleY);
		}
	
		public Attribute GetAttribute() {
			return attribute;
		}
	
		public void SetAttribute(Attribute attribute) {
			this.attribute = attribute;
		}
	
		public virtual void Dispose() {
			if (animation != null) {
				animation.Dispose();
			}
		}
	
		public Animation GetAnimation() {
			return animation;
		}
	
		public void SetAnimation(Animation a) {
			this.animation = a;
		}
	
		public void SetIndex(int index) {
			if (animation  is  AnimationStorage) {
				((AnimationStorage) animation).PlayIndex(index);
			}
		}
	
		public bool IsMirror() {
			return mirror;
		}
	
		public void SetMirror(bool mirror) {
			this.mirror = mirror;
		}
	
		public bool IsBounded() {
			return false;
		}
	
		public bool IsContainer() {
			return false;
		}
	
		public bool InContains(int x, int y, int w, int h) {
			return false;
		}
	
		public RectBox GetRectBox() {
			return GetCollisionArea();
		}
	
		public int GetContainerWidth() {
			return 0;
		}
	
		public int GetContainerHeight() {
			return 0;
		}
	}
}
