using Loon.Core.Geom;
namespace Loon.Action.Sprite.Node {
	
	public class LNUI : LNNode {

		public float _bold;
	
		protected internal int _touchID;
	
		public LNUI() : base() {
			this.Init();
		}
	
		private void Init() {
			this._touchID = -1;
			this._enabled = true;
			this._bold = 0f;
			base.SetNodeSize(0,0);
		}
	
		public bool IsInside(Vector2f point) {
			float[] pos = base.ConvertToWorldPos();
			return (((point.x >= (pos[0] - this._bold)) && (point.x < ((pos[0] + base.GetWidth()) + this._bold))) && ((point.y >= (pos[1] - this._bold)) && (point.y < ((pos[1] + base.GetHeight()) + this._bold))));
		}
	
		public void TouchesCancel() {
			this._touchID = -1;
		}
	
		public void SetBold(float b) {
			this._bold = b;
		}
	
		public float GetBold() {
			return this._bold;
		}
	}
}
