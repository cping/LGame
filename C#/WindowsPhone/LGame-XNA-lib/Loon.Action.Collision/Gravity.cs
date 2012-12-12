using Loon.Core;
namespace Loon.Action.Collision {
	
	public class Gravity : LRelease {
	
		internal object obj0;
	
		internal ActionBind bind;
	
		internal bool enabled;
	
		internal float bounce;
	
		internal float gadd;
	
		internal float g;
	
		internal float accelerationX;
	
		internal float accelerationY;
	
		internal float velocityX;
	
		internal float velocityY;
	
		internal float angularVelocity;
	
		internal string name;

        public Gravity(string name, ActionBind o)
        {
			this.name = name;
			this.obj0 = o;
			this.bind = o;
			this.enabled = true;
		}
	
		public Gravity(ActionBind o) {
			this.obj0 = o;
			this.bind = o;
			this.enabled = true;
		}
	
		public bool IsEnabled() {
			return this.enabled;
		}
	
		public void SetEnabled(bool enabled) {
			this.enabled = enabled;
		}
	
		public float GetVelocityX() {
			return this.velocityX;
		}
	
		public float GetVelocityY() {
			return this.velocityY;
		}
	
		public void SetVelocityX(float velocityX) {
			this.velocityX = velocityX;
		}
	
		public void SetVelocityY(float velocityY) {
			this.velocityY = velocityY;
		}
	
		public void SetVelocity(float velocity) {
			this.velocityX = velocity;
			this.velocityY = velocity;
		}
	
		public void SetVelocity(float velocityX, float velocityY) {
			this.velocityX = velocityX;
			this.velocityY = velocityY;
		}
	
		public float GetAccelerationX() {
			return this.accelerationX;
		}
	
		public float GetAccelerationY() {
			return this.accelerationY;
		}
	
		public void SetAccelerationX(float accelerationX) {
			this.accelerationX = accelerationX;
		}
	
		public void SetAccelerationY(float accelerationY) {
			this.accelerationY = accelerationY;
		}
	
		public void SetAcceleration(float accelerationX,
				float accelerationY) {
			this.accelerationX = accelerationX;
			this.accelerationY = accelerationY;
		}
	
		public void SetAcceleration(float acceleration) {
			this.accelerationX = acceleration;
			this.accelerationY = acceleration;
		}
	
		public void Accelerate(float accelerationX, float accelerationY) {
			this.accelerationX += accelerationX;
			this.accelerationY += accelerationY;
		}
	
		public float GetAngularVelocity() {
			return this.angularVelocity;
		}
	
		public void SetAngularVelocity(float angularVelocity) {
			this.angularVelocity = angularVelocity;
		}
	
		public ActionBind getBind() {
			return bind;
		}
	
		public object GetObject() {
			return obj0;
		}
	
		public string GetName() {
			return name;
		}
	
		public float GetG() {
			return g;
		}
	
		public void SetG(float g) {
			this.g = g;
		}
	
		public float GetBounce() {
			return bounce;
		}
	
		public void SetBounce(float bounce) {
			this.bounce = bounce;
		}
	
		public void Reset() {
			this.accelerationX = 0;
			this.accelerationY = 0;
			this.gadd = 0;
			this.g = 0;
			this.bounce = 0;
			this.velocityX = 0;
			this.velocityY = 0;
			this.angularVelocity = 0;
		}
	
		public void Dispose() {
			this.enabled = false;
			this.bind = null;
		}
	
	}
}
