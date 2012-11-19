 namespace Loon.Action {

     using Loon.Utils;

	public class ScaleTo : ActionEvent {
	
		private float dt;
	
		private float deltaX, deltaY;
	
		private float startX, startY;
	
		private float endX, endY;
	
		public ScaleTo(float s):this(s, s) {
			
		}
	
		public ScaleTo(float sx, float sy) {
			this.endX = sx;
			this.endY = sy;
		}
	
		public override bool IsComplete() {
			return isComplete;
		}
	
		public override void OnLoad() {
			if (original != null) {
				startX = original.GetScaleX();
				startY = original.GetScaleY();
				deltaX = endX - startX;
				deltaY = endY - startY;
			}
		}
	
		public override void Update(long elapsedTime) {
			if (original != null) {
				 lock (original) {
								if (original != null) {
									dt += MathUtils.Max((elapsedTime / 1000), 0.01f);
									original.SetScale(startX + (deltaX * dt), startY
											+ (deltaY * dt));
									isComplete = ((deltaX > 0) ? (original.GetScaleX() >= endX)
											: (original.GetScaleX() <= endX))
											&& ((deltaY > 0) ? (original.GetScaleY() >= endY)
													: (original.GetScaleY() <= endY));
								}
							}
			} else {
				isComplete = true;
			}
		}
	}
}
