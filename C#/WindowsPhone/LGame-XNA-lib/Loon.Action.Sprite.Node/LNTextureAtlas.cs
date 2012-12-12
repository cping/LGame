using System.Collections.Generic;
using Loon.Core.Geom;
using Loon.Core.Graphics.Opengl;
using Loon.Core.Graphics;
using Loon.Utils;
namespace Loon.Action.Sprite.Node {
	
	public class LNTextureAtlas {
	
		private List<RectBox> _rectList;
	
		private LTexture _texture;
	
		private int _totalRect;
	
		public Vector2f anchor = new Vector2f();
	
		public LNTextureAtlas(LTexture texture, int capacity) {
			this._texture = texture;
			this._totalRect = capacity;
			this._rectList = new List<RectBox>();
		}
	
		public void AddRect(RectBox rect) {
			this._rectList.Add(rect);
		}
	
		public void Draw(int idx, SpriteBatch batch, Vector2f absPos,
				float rotation, Vector2f scale, LColor color) {
			RectBox rect = this._rectList[idx];
			batch.SetColor(color);
			batch.Draw(_texture, absPos.x, absPos.y, anchor.x, anchor.y,
					rect.width, rect.height, scale.x, scale.y,
					MathUtils.ToDegrees(rotation), rect.x, rect.y, rect.width,
					rect.height, false, false);
			batch.ResetColor();
		}
	
		public void Draw(int idx, SpriteBatch batch, float x, float y,
				float rotation, float sx, float sy, LColor color) {
			RectBox rect = this._rectList[idx];
			batch.SetColor(color);
			batch.Draw(_texture, x, y, anchor.x, anchor.y, rect.width, rect.height,
					sx, sy, MathUtils.ToDegrees(rotation), rect.x, rect.y,
					rect.width, rect.height, false, false);
			batch.ResetColor();
		}
	
		public void ResetRect() {
			this._rectList.Clear();
		}
	
		public LTexture GetTexture() {
			return this._texture;
		}
	
		public int GetTotalRect() {
			return _totalRect;
		}
	}
}
