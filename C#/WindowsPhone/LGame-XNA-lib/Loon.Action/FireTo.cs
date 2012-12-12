using Loon.Utils;
namespace Loon.Action {
	
	public class FireTo : ActionEvent {
	
		private double direction;
	
		private int x, y;
	
		private int vx, vy;
	
		private int endX, endY;
	
		private double speed;
	
		public FireTo(int endX_0, int endY_1, double speed_2) {
			this.endX = endX_0;
			this.endY = endY_1;
			this.speed = speed_2;
		}
	
		public override bool IsComplete() {
			return isComplete;
		}
	
		public override void OnLoad() {
			this.x = (int) original.GetX();
			this.y = (int) original.GetY();
			this.direction = MathUtils.Atan2(endY - y, endX - x);
			this.vx = (int) (MathUtils.Cos(direction) * this.speed);
			this.vy = (int) (MathUtils.Sin(direction) * this.speed);
		}
	
		public override void Update(long elapsedTime) {
			this.x += this.vx;
			this.y += this.vy;
			if (x == 0 && y == 0) {
				isComplete = true;
				return;
			}
			if (original.IsContainer() && original.IsBounded()) {
				if (original.InContains(x, y, original.GetWidth(),
						original.GetHeight())) {
					 lock (original) {
										original.SetLocation(x + offsetX, y + offsetY);
									}
				} else {
					isComplete = true;
				}
			} else {
				if (x + original.GetWidth() < 0) {
					isComplete = true;
				} else if (x > original.GetContainerWidth() + original.GetWidth()) {
					isComplete = true;
				}
				if (y + original.GetHeight() < 0) {
					isComplete = true;
				} else if (y > original.GetContainerHeight() + original.GetHeight()) {
					isComplete = true;
				}
				 lock (original) {
								original.SetLocation(x + offsetX, y + offsetY);
							}
			}
		}
	
		public double GetDirection() {
			return direction;
		}
	
	}
}
