using Loon.Core.Graphics.Opengl;
using Loon.Core.Geom;
using System;
using Loon.Core;
using Loon.Utils;
namespace Loon.Action.Sprite.Node {
	
	public class LNSprite : LNNode {
	
		private BlendState blendState = BlendState.NonPremultiplied;
	
		private float rotation;
	
		protected internal LTexture _texture;
	
		private float[] pos, scale;
	
		protected internal LNAnimation _ans;
	
		protected internal bool _flipX = false, _flipY = false;
	
		protected internal System.Collections.Generic.Dictionary<string, LNAnimation> _anims;
	
		public LNSprite(RectBox rect):base(rect) {
			this._ans = null;
			base._anchor = new Vector2f(0f, 0f);
		}
	
		public LNSprite(int x, int y, int w, int h):base(x,y,w,h) {
			this._ans = null;
			base._anchor = new Vector2f(0f, 0f);
		}

        public LNSprite(string fsName)
            : base()
        {
            this._ans = null;
            LNFrameStruct struc0 = LNDataCache.GetFrameStruct(fsName);
            if (struc0 == null)
            {
                throw new Exception("");
            }
            this._texture = struc0._texture;
            base._left = struc0._textCoords.X();
            base._top = struc0._textCoords.Y();
            base._orig_width = struc0._orig_width;
            base._orig_height = struc0._orig_height;
            base.SetNodeSize(struc0._size_width, struc0._size_height);
            base._anchor.Set(struc0._anchor);
            blendState = struc0._state;
            if (!struc0._place.Equals(0, 0))
            {
                SetPosition(struc0._place);
            }
        }
	
		public LNSprite():this(LSystem.screenRect) {
			
		}
	
		public static LNSprite GInitWithTexture(LTexture tex2d) {
			LNSprite sprite = new LNSprite();
			sprite.InitWithTexture(tex2d);
			return sprite;
		}
	
		public static LNSprite GInitWithFilename(string file) {
			LNSprite sprite = new LNSprite();
			sprite.InitWithFilename(file);
			return sprite;
		}
	
		public static LNSprite GInitWithFrameStruct(LNFrameStruct fs) {
			LNSprite sprite = new LNSprite();
			sprite.InitWithFrameStruct(fs);
			return sprite;
		}
	
		public static LNSprite GInitWithFrameStruct(string fsName) {
			return GInitWithFrameStruct(LNDataCache.GetFrameStruct(fsName));
		}
	
		public static LNSprite GInitWithAnimation(LNAnimation ans) {
			LNSprite sprite = new LNSprite();
			sprite.InitWithAnimation(ans, 0);
			return sprite;
		}
	
		public override void Draw(SpriteBatch batch) {
			if (base._visible && (this._texture != null)) {
				pos = base.ConvertToWorldPos();
				if (_screenRect.Intersects(pos[0], pos[1], GetWidth(), GetHeight())
						|| _screenRect.Contains(pos[0], pos[1])) {
					if (_parent != null) {
						rotation = ConvertToWorldRot();
						scale = ConvertToWorldScale();
					} else {
						rotation = _rotation;
						scale[0] = _scale.x;
						scale[1] = _scale.y;
					}
					batch.SetColor(base._color.r, base._color.g, base._color.b,
							base._alpha);
					if (rotation == 0) {
						batch.Draw(_texture, pos[0], pos[1], base._size_width
								* scale[0], base._size_height * scale[1],
								base._left, base._top, base._orig_width,
								base._orig_height, _flipX, _flipY);
					} else {
						batch.Draw(_texture, pos[0], pos[1], _anchor.x, _anchor.y,
								base._size_width, base._size_height, scale[0],
								scale[1], MathUtils.ToDegrees(rotation),
								base._left, base._top, base._orig_width,
								base._orig_height, _flipX, _flipY);
					}
					batch.ResetColor();
					BlendState oldState = batch.GetBlendState();
					if (blendState != oldState) {
						batch.Flush(blendState);
						batch.SetBlendState(oldState);
					}
				}
			}
		}
	
		public void InitWithTexture(LTexture tex2d) {
			this._texture = tex2d;
			base._left = 0;
			base._top = 0;
			base.SetNodeSize(_texture.GetWidth(),_texture.GetHeight());
			base._anchor.Set(base.GetWidth() / 2f, base.GetHeight() / 2f);
		}
	
		public void InitWithFilename(string filename) {
			this._texture = LTextures.LoadTexture(filename, Loon.Core.Graphics.Opengl.LTexture.Format.LINEAR);
			base._left = 0;
			base._top = 0;
			base.SetNodeSize(_texture.GetWidth(),_texture.GetHeight());
			base._anchor.Set(base.GetWidth() / 2f, base.GetHeight() / 2f);
		}
	
		public void InitWithFrameStruct(LNFrameStruct fs) {
			this._texture = fs._texture;
			blendState = fs._state;
			base._left = fs._textCoords.X();
			base._top = fs._textCoords.Y();
			base._orig_width = fs._orig_width;
			base._orig_height = fs._orig_height;
			base.SetNodeSize(fs._size_width,fs._size_height);
			base._anchor.Set(fs._anchor);
		}
	
		public void InitWithAnimation(LNAnimation ans, int idx) {
			if (ans != null) {
				if (this._anims == null) {
					this._anims = new System.Collections.Generic.Dictionary<string, LNAnimation>();
				}
				this._ans = ans;
				InitWithFrameStruct(this._ans.GetFrame(idx));
				CollectionUtils.Put(_anims,ans.GetName(),ans);
			}
		}
	
		public void AddAnimation(LNAnimation anim) {
			InitWithAnimation(anim, 0);
		}
	
		public void SetFrame(string animName, int index) {
			if (this._anims == null) {
                this._anims = new System.Collections.Generic.Dictionary<string, LNAnimation>();
			}
			if (this._anims.ContainsKey(animName)) {
				this._ans = (LNAnimation)CollectionUtils.Get(this._anims,animName);
				InitWithAnimation(this._ans, index);
			}
		}
	
		public void SetFrame(int idx) {
			if (_ans != null) {
				InitWithFrameStruct(_ans.GetFrame(idx));
			}
		}
	
		public void SetFrameTime(float time) {
			if (_ans != null) {
				InitWithFrameStruct(_ans.GetFrameByTime(time));
			}
		}
	
		public LTexture GetFrameTexture() {
			return this._texture;
		}
	
		public LNAnimation GetAnimation() {
			return this._ans;
		}
	
		public void SetAnimation(LNAnimation ans) {
			this._ans = ans;
			this.InitWithTexture(this._ans.GetFrame(0)._texture);
		}
	
		public bool IsFlipX() {
			return _flipX;
		}
	
		public void SetFlipX(bool flipX) {
			this._flipX = flipX;
		}
	
		public bool IsFlipY() {
			return _flipY;
		}
	
		public void SetFlipY(bool flipY) {
			this._flipY = flipY;
		}
	
		public BlendState GetBlendState() {
			return blendState;
		}
	
		public void SetBlendState(BlendState blendState) {
			this.blendState = blendState;
		}
	}
}
