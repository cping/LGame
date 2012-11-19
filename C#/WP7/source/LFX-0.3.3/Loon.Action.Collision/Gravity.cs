namespace Loon.Action.Collision {
	
	using Loon.Action.Sprite;
	using Loon.Core;

	public class Gravity : LRelease {
	
		internal object obj0;
	
		internal Bind bind;
	
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
	
		public Gravity(string name_0, object o) {
			this.name = name_0;
			this.obj0 = o;
			this.bind = new Bind(o);
			this.enabled = true;
		}
	
		public Gravity(object o) {
			this.obj0 = o;
			this.bind = new Bind(o);
			this.enabled = true;
		}
	
		public bool IsEnabled() {
			return this.enabled;
		}
	
		public void SetEnabled(bool enabled_0) {
			this.enabled = enabled_0;
		}
	
		public float GetVelocityX() {
			return this.velocityX;
		}
	
		public float GetVelocityY() {
			return this.velocityY;
		}
	
		public void SetVelocityX(float velocityX_0) {
			this.velocityX = velocityX_0;
		}
	
		public void SetVelocityY(float velocityY_0) {
			this.velocityY = velocityY_0;
		}
	
		public void SetVelocity(float velocity) {
			this.velocityX = velocity;
			this.velocityY = velocity;
		}
	
		public void SetVelocity(float velocityX_0, float velocityY_1) {
			this.velocityX = velocityX_0;
			this.velocityY = velocityY_1;
		}
	
		public float GetAccelerationX() {
			return this.accelerationX;
		}
	
		public float GetAccelerationY() {
			return this.accelerationY;
		}
	
		public void SetAccelerationX(float accelerationX_0) {
			this.accelerationX = accelerationX_0;
		}
	
		public void SetAccelerationY(float accelerationY_0) {
			this.accelerationY = accelerationY_0;
		}
	
		public void SetAcceleration(float accelerationX_0,
				float accelerationY_1) {
			this.accelerationX = accelerationX_0;
			this.accelerationY = accelerationY_1;
		}
	
		public void SetAcceleration(float acceleration) {
			this.accelerationX = acceleration;
			this.accelerationY = acceleration;
		}
	
		public void Accelerate(float accelerationX_0, float accelerationY_1) {
			this.accelerationX += accelerationX_0;
			this.accelerationY += accelerationY_1;
		}
	
		public float GetAngularVelocity() {
			return this.angularVelocity;
		}
	
		public void SetAngularVelocity(float angularVelocity_0) {
			this.angularVelocity = angularVelocity_0;
		}
	
		public Bind GetBind() {
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
	
		public void SetG(float g_0) {
			this.g = g_0;
		}
	
		public float GetBounce() {
			return bounce;
		}
	
		public void SetBounce(float b) {
			this.bounce = b;
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
	
		public virtual void Dispose() {
			this.enabled = false;
			this.bind = null;
		}
	
	}
}
