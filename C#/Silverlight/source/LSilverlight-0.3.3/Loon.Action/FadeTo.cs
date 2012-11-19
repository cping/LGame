namespace Loon.Action {

    using Loon.Action.Sprite.Effect;

	public class FadeTo : ActionEvent {
	
		public float time;
	
		public float currentFrame;
	
		public int type;
	
		private float opacity;
	
		public FadeTo(int type_0, int speed) {
			this.type = type_0;
			this.SetSpeed(speed);
		}

        public int GetCodeType()
        {
			return type;
		}

        public void SetCodeType(int type_0)
        {
			this.type = type_0;
		}
	
		internal void SetOpacity(float opacity_0) {
			this.opacity = opacity_0;
		}
	
		public float GetOpacity() {
			return opacity;
		}
	
		public override bool IsComplete() {
			return isComplete;
		}
	
		public float GetSpeed() {
			return time;
		}
	
		public void SetSpeed(int delay) {
			this.time = delay;
            if (type == FadeEffect.TYPE_FADE_IN)
            {
				this.currentFrame = this.time;
			} else {
				this.currentFrame = 0;
			}
		}
	
		public override void OnLoad() {
	
		}
	
		public override void Update(long elapsedTime) {
            if (type == FadeEffect.TYPE_FADE_IN)
            {
				currentFrame--;
				if (currentFrame == 0) {
					SetOpacity(0);
					isComplete = true;
				}
			} else {
				currentFrame++;
				if (currentFrame == time) {
					SetOpacity(0);
					isComplete = true;
				}
			}
			SetOpacity((currentFrame / time) * 255);
			if (opacity > 0) {
				original.SetAlpha((opacity / 255));
			}
		}
	
	}
}
