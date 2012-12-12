namespace Loon.Action {
	
    using Loon.Action.Map;
    using Loon.Utils;
    using Loon.Core;
	
	public class ArrowTo : ActionEvent {
	
		private float gravity;
	
		private float startX;
		private float startY;
	
		private float endX;
		private float endY;
	
		private float vx;
		private float vy;
	
		private float speed;
	
		private int dir;
	
		public ArrowTo(float tx, float ty, float speed_0, float g) {
			this.gravity = 200;
			this.endX = tx;
			this.endY = ty;
			this.speed = speed_0;
			this.gravity = g;
			this.speed = speed_0;
		}
	
		public ArrowTo(float tx, float ty):this(tx, ty, 3f, 200f) {
			
		}
	
		public override bool IsComplete() {
			return isComplete;
		}
	
		public override void OnLoad() {
			this.startX = original.GetX();
			this.startY = original.GetY();
			float dx = endX - startX;
			float dy = endY - startY;
			this.vx = dx / speed;
			this.vy = 1 / speed * (dy - 1.0f / 2.0f * gravity * speed * speed);
			this.dir = Field2D.GetDirection(MathUtils.Atan2(endX - startX, endY
					- startY));
		}
	
		public override void Update(long elapsedTime) {
			float dt = MathUtils.Min(elapsedTime / 1000f, 0.1f);
			vy += gravity * dt;
			startX += vx * dt;
			startY += vy * dt;
			if (original.IsContainer() && original.IsBounded()) {
				if (startX < -original.GetWidth() || startY < -original.GetHeight()
						|| startX > original.GetContainerWidth()
						|| startY > original.GetContainerHeight()) {
					isComplete = true;
				}
			} else if (startX < -original.GetWidth() * 2
					|| startY < -original.GetHeight() * 2
					|| startX > LSystem.screenRect.width + original.GetWidth() * 2
					|| startY > LSystem.screenRect.height + original.GetHeight()
							* 2) {
				isComplete = true;
			}
			 lock (original) {
						float slope = vy / vx;
						float theta = MathUtils.Atan(slope);
						original.SetRotation(theta * MathUtils.RAD_TO_DEG);
						original.SetLocation(startX, startY);
					}
		}
	
		public int GetDirection() {
			return dir;
		}
	
	}
}
