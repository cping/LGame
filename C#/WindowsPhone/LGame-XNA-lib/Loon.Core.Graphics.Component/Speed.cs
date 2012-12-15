using Loon.Core.Geom;
using System.Collections.Generic;
using Loon.Utils;
namespace Loon.Core.Graphics.Component {

	public class Speed {
	
		private static Vector2f gravity = new Vector2f(0.0f, 0.6f);
	
		private float dx = 0.0f;
	
		private float dy = 0.0f;
	
		private float direction = 0;
	
		private float length;
	
		public Speed() {
		}
	
		public Speed(float direction_0, float length_1) {
			this.Set(direction_0, length_1);
		}
	
		public static Vector2f GetVelocity(Vector2f velocity, List<Vector2f> forces) {
			foreach (Vector2f v  in  forces) {
				velocity.Add(v);
			}
			return velocity;
		}
	
		public static Vector2f ElasticForce(Vector2f displacement,
				float forceConstant) {
			float forceX = -forceConstant * displacement.GetX();
			float forceY = -forceConstant * displacement.GetY();
			Vector2f theForce = new Vector2f(forceX, forceY);
			return theForce;
		}
	
		public static Vector2f GetVelocity(Vector2f velocity, Vector2f force) {
			velocity.Add(force);
			return velocity;
		}
	
		public static Vector2f GetVelocity(Vector2f velocity, Vector2f force,
				float mass) {
			Vector2f acceleration = new Vector2f(force.GetX() / mass, force.GetY()
					/ mass);
			velocity.Add(acceleration);
			return velocity;
		}
	
		public static void SetGravity(int g) {
			gravity.SetY(g);
		}
	
		public static Vector2f Gravity() {
			return gravity;
		}
	
		public void Set(float direction_0, float length_1) {
			this.length = length_1;
			this.direction = direction_0;
			this.dx = (length_1 * MathUtils.Cos(MathUtils
					.ToRadians(direction_0)));
			this.dy = (length_1 * MathUtils.Sin(MathUtils
					.ToRadians(direction_0)));
		}
	
		public void SetDirection(float direction_0) {
			this.direction = direction_0;
			this.dx = (this.length * MathUtils.Cos(MathUtils
					.ToRadians(direction_0)));
			this.dy = (this.length * MathUtils.Sin(MathUtils
					.ToRadians(direction_0)));
		}
	
		public void Add(Speed other) {
			this.dx += other.dx;
			this.dy += other.dy;
			this.direction = (int) MathUtils.ToDegrees(MathUtils.Atan2(this.dy,
					this.dx));
			this.length = MathUtils.Sqrt(this.dx * this.dx + this.dy
					* this.dy);
		}
	
		public float GetX() {
			return this.dx;
		}
	
		public float GetY() {
			return this.dy;
		}
	
		public float GetDirection() {
			return this.direction;
		}
	
		public float GetLength() {
			return this.length;
		}
	
		public Speed Copy() {
			Speed copy = new Speed();
			copy.dx = this.dx;
			copy.dy = this.dy;
			copy.direction = this.direction;
			copy.length = this.length;
			return copy;
		}
	}
}
