namespace Loon.Action.Sprite.Painting {
	
    using Loon.Core;
    using Loon.Core.Geom;
    using Loon.Core.Input;
    using Loon.Core.Timer;
    using Loon.Utils;
	
	public abstract class Drawable : LRelease {
	
		public bool IsPopup = false;
	
		public float transitionOnTime = 0;
	
		public float transitionOffTime = 0;
	
		public Vector2f bottomLeftPosition = new Vector2f();
	
		public DrawableScreen drawableScreen;
	
		internal bool otherScreenHasFocus;
	
		protected internal float _transitionPosition = 1f;
	
		protected internal DrawableState _drawableState = Painting.DrawableState.TransitionOn;
	
		protected internal bool _enabled = true;
	
		protected internal bool _isExiting = false;
	
		public DrawableScreen GetDrawableScreen() {
			return drawableScreen;
		}
	
		public void ExitScreen() {
			if (this.transitionOffTime == 0f) {
				this.drawableScreen.RemoveDrawable(this);
			} else {
				this._isExiting = true;
			}
		}
	
		public Vector2f GetBottomLeftPosition() {
			return bottomLeftPosition;
		}
	
		public DrawableState GetDrawableState() {
			return _drawableState;
		}
	
		public void SetDrawableState(DrawableState state) {
			_drawableState = state;
		}
	
		public float GetTransitionAlpha() {
			return (1f - this._transitionPosition);
		}
	
		public float GetTransitionPosition() {
			return _transitionPosition;
		}
	
		public abstract void HandleInput(LInput input);
	
		public bool IsActive() {
			return !otherScreenHasFocus
					&& (_drawableState == Painting.DrawableState.TransitionOn || _drawableState == Painting.DrawableState.Active);
		}
	
		public bool IsExiting() {
			return _isExiting;
		}
	
		public abstract void LoadContent();
	
		public abstract void UnloadContent();
	
		public abstract void Draw(SpriteBatch batch, GameTime elapsedTime);
	
		public abstract void Update(GameTime elapsedTime);
	
		public void Update(GameTime gameTime, bool otherScreenHasFocus,
				bool coveredByOtherScreen) {
			this.otherScreenHasFocus = otherScreenHasFocus;
			if (this._isExiting) {
				this._drawableState = Painting.DrawableState.TransitionOff;
				if (!this.UpdateTransition(gameTime, this.transitionOffTime, 1)) {
					this.drawableScreen.RemoveDrawable(this);
				}
			} else if (coveredByOtherScreen) {
				if (this.UpdateTransition(gameTime, this.transitionOffTime, 1)) {
					this._drawableState = Painting.DrawableState.TransitionOff;
				} else {
					this._drawableState = Painting.DrawableState.Hidden;
				}
			} else if (this.UpdateTransition(gameTime, this.transitionOnTime, -1)) {
				this._drawableState = Painting.DrawableState.TransitionOn;
			} else {
				this._drawableState = Painting.DrawableState.Active;
			}
	
			Update(gameTime);
		}
	
		private bool UpdateTransition(GameTime gameTime, float time,
				int direction) {
			float num;
			if (time == 0f) {
				num = 1f;
			} else {
				num = (gameTime.GetElapsedGameTime() / time);
			}
	
			this._transitionPosition += num * direction;
			if (((direction < 0) && (this._transitionPosition <= 0f))
					|| ((direction > 0) && (this._transitionPosition >= 1f))) {
				this._transitionPosition = MathUtils.Clamp(
						this._transitionPosition, 0f, 1f);
				return false;
			}
			return true;
		}
	
		public abstract void Pressed(LTouch e);
	
		public abstract void Released(LTouch e);
	
		public abstract void Move(LTouch e);
	
		public abstract void Pressed(LKey e);
	
		public abstract void Released(LKey e);
	
		public bool IsEnabled() {
			return _enabled;
		}
	
		public void SetEnabled(bool e) {
			this._enabled = e;
		}
	
		public void Dispose() {
			this._enabled = false;
		}
	}
}
