using Loon.Core.Geom;
namespace Loon.Action.Sprite.Node {
	
	public class LNLabelAtlas : LNAtlasNode {
	
		private int _charWidth;
	
		private char _startchar;
	
		private string _text;
	
		private LNLabelAtlas.LabelType  _type;
	
		public LNFrameStruct fs;
	
		public LNLabelAtlas() {
			this._type = LabelType.TEXT_ALIGNMENT_LEFT;
		}
	
		public LNLabelAtlas(string fsName, LNLabelAtlas.LabelType  type, string text,
				char startchar, int itemWidth, int itemHeight, int charWidth):base(fsName, itemWidth, itemHeight) {
			
			this.fs = LNDataCache.GetFrameStruct(fsName);
			base._left = (int) this.fs._textCoords.x;
			base._top = (int) this.fs._textCoords.y;
			this._type = type;
			this._charWidth = charWidth;
			this._startchar = startchar;
			this.SetString(text);
		}
	
		private float[] pos;
	
		private float[] scale;
	
		private float rotation;
	
		new public void Draw(SpriteBatch batch) {
			if (base._visible) {
				pos = base.ConvertToWorldPos();
				scale = base.ConvertToWorldScale();
				rotation = base.ConvertToWorldRot();
				int size = _text.Length;
				if (this._type == LabelType.TEXT_ALIGNMENT_LEFT) {
					for (int i = 0; i < size; i++) {
						base._textureAtlas.Draw(i, batch, pos[0] + i
								* this._charWidth, pos[1], rotation, scale[0],
								scale[1], batch.GetColor());
					}
				} else if (this._type == LabelType.TEXT_ALIGNMENT_RIGHT) {
					for (int j = 0; j < size; j++) {
						base._textureAtlas.Draw(j, batch, pos[0]
								- (size * this._charWidth) + (j * this._charWidth),
								pos[1], rotation, scale[0], scale[1],
								batch.GetColor());
					}
				} else {
					for (int k = 0; k < size; k++) {
						base._textureAtlas.Draw(k, batch, pos[0]
								- ((size * this._charWidth) / 2)
								+ (k * this._charWidth), pos[1], rotation,
								scale[0], scale[1], batch.GetColor());
					}
				}
			}
		}
	
		public void SetString(string text) {
			this._text = text;
			base._textureAtlas.ResetRect();
			for (int i = 0; i < this._text.Length; i++) {
				int num2 = this._text[i] - this._startchar;
				base._textureAtlas.AddRect(new RectBox(((int) base._left)
						+ (num2 * base._itemWidth), (int) base._top,
						base._itemWidth, base._itemHeight));
			}
		}
	
		public enum LabelType {
			TEXT_ALIGNMENT_LEFT, TEXT_ALIGNMENT_RIGHT, TEXT_ALIGNMENT_CENTER
		}
	}
}
