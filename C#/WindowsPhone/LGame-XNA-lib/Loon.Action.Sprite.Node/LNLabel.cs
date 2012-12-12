
namespace Loon.Action.Sprite.Node {
	
	using Node;
	using System;
	using System.Collections;
	using System.ComponentModel;
	using System.IO;
	using System.Runtime.CompilerServices;
    using Loon.Core.Graphics;
    using Loon.Utils;
    using Loon.Core.Geom;
	
	public class LNLabel : LNNode {
	
		private LFont _spriteFont;
	
		private string _text;
	
		private LNLabel.LabelType  _type;
	
		public LNLabel() {
			this._type = LabelType.TEXT_ALIGNMENT_LEFT;
			this._spriteFont = LFont.GetDefaultFont();
			this.SetNodeSize(1, 1);
		}
	
		public LNLabel(string text) :this(text, LabelType.TEXT_ALIGNMENT_LEFT){
			
		}
	
		public LNLabel(string text, LNLabel.LabelType  type):this(text, type, LFont.GetDefaultFont()) {
			
		}

        public LNLabel(string text, LNLabel.LabelType type, LFont spriteFont)
            : base()
        {
            this._spriteFont = spriteFont;
            this._type = type;
            SetString(text);
            this.SetNodeSize(_spriteFont.StringWidth(text), _spriteFont.GetHeight());
        }
	
		private float[] pos;
	
		private float[] scale;
	
		private float rotation;
	
		public override void Draw(SpriteBatch batch) {
			if (base._visible) {
				pos = base.ConvertToWorldPos();
				scale = base.ConvertToWorldScale();
				rotation = base.ConvertToWorldRot();
				batch.SetColor(base._color.r, base._color.g, base._color.b,
						base._alpha);
				LFont font = batch.GetFont();
				batch.SetFont(_spriteFont);
				batch.DrawString(this._text, pos[0], pos[1], scale[0], scale[1],
						_anchor.x, _anchor.y, MathUtils.ToDegrees(rotation),
						batch.GetColor());
				batch.SetFont(font);
				batch.ResetColor();
			}
		}
	
		public void SetText(string text) {
			SetString(text);
		}
	
		public void SetString(string text) {
			this._text = text;
			if (this._type == LabelType.TEXT_ALIGNMENT_LEFT) {
				base._anchor = new Vector2f(0f,
						this._spriteFont.StringWidth(this._text) / 2f);
			} else if (this._type == LabelType.TEXT_ALIGNMENT_RIGHT) {
				base._anchor = new Vector2f(
						this._spriteFont.StringWidth(this._text),
						this._spriteFont.StringWidth(this._text) / 2f);
			} else if (this._type == LabelType.TEXT_ALIGNMENT_CENTER) {
				base._anchor = new Vector2f(
						this._spriteFont.StringWidth(this._text) / 2f,
						this._spriteFont.GetHeight() / 2f);
			}
		}
	
		public enum LabelType {
			TEXT_ALIGNMENT_LEFT, TEXT_ALIGNMENT_RIGHT, TEXT_ALIGNMENT_CENTER
		}
	}
}
