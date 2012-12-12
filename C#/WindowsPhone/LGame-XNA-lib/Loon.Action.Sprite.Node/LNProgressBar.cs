using Loon.Utils;
namespace Loon.Action.Sprite.Node {

	public class LNProgressBar : LNSprite {
		private float _percent;
		private LNProgressBar.BarType  _type;
	
		public LNProgressBar() {
			this._type = BarType.PROGRESS_BAR_LEFT;
		}
	
		public LNProgressBar(string fsName):base(fsName) {
		
			this._type = BarType.PROGRESS_BAR_LEFT;
			this._percent = 1f;
		}
	
		private float[] pos;
	
		private float rotation;
	
		private float[] scale;
	
		public override void Draw(SpriteBatch batch) {
			if (base._visible && (base._texture != null)) {
				pos = base.ConvertToWorldPos();
				rotation = base.ConvertToWorldRot();
				scale = base.ConvertToWorldScale();
				batch.SetColor(base._color.r, base._color.g, base._color.b,
						base._alpha);
				if (this._type == BarType.PROGRESS_BAR_LEFT) {
					batch.Draw(_texture, pos[0], pos[1], _anchor.x, _anchor.y,
							base._size_width, base._size_height, scale[0],
							scale[1], MathUtils.ToDegrees(rotation), base._left,
							base._top, base._orig_width * this._percent,
							base._orig_height, _flipX, _flipY);
				} else if (this._type == BarType.PROGRESS_BAR_RIGHT) {
					int offsetX = ((int) (base._orig_width * (1f - this._percent)));
					batch.Draw(_texture, pos[0] + offsetX, pos[1], _anchor.x,
							_anchor.y, base._size_width, base._size_height,
							scale[0], scale[1], MathUtils.ToDegrees(rotation),
							((int) base._left) + offsetX, (int) base._top,
							(int) (base._orig_width * this._percent),
							(int) base._orig_height, _flipX, _flipY);
				} else if (this._type == BarType.PROGRESS_BAR_TOP) {
                    batch.Draw(_texture, pos[0], pos[1], _anchor.x, _anchor.y,
							base._size_width, base._size_height, scale[0],
							scale[1], MathUtils.ToDegrees(rotation), base._left,
							base._top, base._orig_width,
							(base._orig_height * this._percent), _flipX, _flipY);
				} else if (this._type == BarType.PROGRESS_BAR_BOTTOM) {
					int offsetY = ((int) (base._orig_height * (1f - this._percent)));
                    batch.Draw(_texture, pos[0], pos[1] + offsetY, _anchor.x,
							_anchor.y, base._size_width, base._size_height,
							scale[0], scale[1], MathUtils.ToDegrees(rotation),
							(int) base._left, ((int) base._top)
									+ ((int) (base._orig_height * this._percent)),
							(int) base._orig_width,
							(int) (base._orig_height * this._percent), _flipX,
							_flipY);
	
				}
				batch.ResetColor();
			}
		}
	
		public void SetType(LNProgressBar.BarType  type) {
			this._type = type;
		}
	
		public float GetPercent() {
			return this._percent;
		}
	
		public void SetPercent(float p) {
			this._percent = p;
		}
	
		public enum BarType {
			PROGRESS_BAR_LEFT, PROGRESS_BAR_RIGHT, PROGRESS_BAR_TOP, PROGRESS_BAR_BOTTOM
		}
	}}
