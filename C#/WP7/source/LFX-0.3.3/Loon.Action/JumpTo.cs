namespace Loon.Action {

    using Loon.Core.Geom;

	public class JumpTo : ActionEvent {
	
		private float moveY;
	
		private float moveX;
	
		private int moveJump;
	
		private float g;
	
		public JumpTo(int m, float g_0) {
			this.moveJump = m;
			this.g = g_0;
		}
	
		public override bool IsComplete() {
			return isComplete;
		}
	
		public override void OnLoad() {
			this.moveY = moveJump;
		}
	
		public float GetMoveX() {
			return moveX;
		}
	
		public void SetMoveX(float moveX_0) {
			this.moveX = moveX_0;
		}
	
		public float GetMoveY() {
			return moveY;
		}
	
		public void SetMoveY(float moveY_0) {
			this.moveY = moveY_0;
		}
	
		public override void Update(long elapsedTime) {
			if (moveJump < 0) {
				if (this.moveY > -(moveJump)) {
					this.moveY = -moveJump;
				}
			} else {
				if (this.moveY > (moveJump)) {
					this.moveY = moveJump;
				}
			}
			original.SetLocation(offsetX + (original.GetX() + this.moveX), offsetY
					+ (original.GetY() + this.moveY));
			if (moveJump < 0) {
				this.moveY += g;
			} else {
				this.moveY -= g;
			}
	
			if (moveJump > 0) {
				if (original.GetY() + original.GetHeight() > original
						.GetContainerHeight() + original.GetHeight()) {
					isComplete = true;
				}
			} else if (original.GetY() + original.GetHeight() < 0) {
				isComplete = true;
			}
			bool isLimit = original.IsBounded();
			if (isLimit) {
				RectBox rect = original.GetRectBox();
				int limitWidth = (int) (original.GetContainerWidth() - rect
						.GetWidth());
				int limitHeight = (int) rect.GetHeight();
				if (original.GetX() > limitWidth) {
					original.SetLocation(offsetX + limitWidth,
							offsetY + original.GetY());
					isComplete = true;
				} else if (original.GetX() < 0) {
					original.SetLocation(offsetX, offsetY + original.GetY());
					isComplete = true;
				}
				if (original.GetY() < 0) {
					original.SetLocation(offsetX + original.GetX(), offsetY
							+ limitHeight);
					isComplete = true;
				} else if (original.GetY() > original.GetHeight() - limitHeight) {
					original.SetLocation(offsetX + original.GetX(), offsetY
							+ (original.GetContainerHeight() - limitHeight));
					isComplete = true;
				}
			}
		}
	
	}
}
