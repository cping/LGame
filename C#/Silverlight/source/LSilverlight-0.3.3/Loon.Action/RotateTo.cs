namespace Loon.Action {
	
	public class RotateTo : ActionEvent {
	
		private float dstAngle;
	
		private float diffAngle;
	
		private float startAngle;
	
		private float speed;
	
		public RotateTo(float dstAngle_0, float speed_1) {
			this.dstAngle = dstAngle_0;
			if (this.dstAngle > 360) {
				this.dstAngle = 360;
			} else if (this.dstAngle < 0) {
				this.dstAngle = 0;
			}
			this.speed = speed_1;
		}
	
		public override bool IsComplete() {
			return isComplete;
		}
	
		public override void OnLoad() {
			startAngle = original.GetRotation();
			diffAngle = 1;
		}
	
		public override void Update(long elapsedTime) {
			startAngle += diffAngle * speed;
			original.SetRotation(startAngle);
			if (startAngle >= dstAngle) {
				isComplete = true;
			}
		}
	
	}
}
