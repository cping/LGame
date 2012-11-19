namespace Loon.Action {
    using Loon.Utils;
	// 投球事件
	public class BallTo : ActionEvent {
	
		private int radius;
	
		private int x, y;
	
		private double vx, vy;
	
		private double gravity;
	
		public BallTo(int vx_0, int vy_1):this(0, vx_0, vy_1, 0.3d) {
			
		}
	
		public BallTo(int vx_0, int vy_1, double g) :this(0, vx_0, vy_1, g){
			
		}
	
		public BallTo(int r, int vx_0, int vy_1, double g) {
			this.radius = r;
			this.SetVelocity(vx_0, vy_1);
			this.gravity = g;
		}
	
		/// <summary>
		/// 判定球体是否处于指定半径之内
		/// </summary>
		///
		/// <param name="bx"></param>
		/// <param name="by"></param>
		/// <returns></returns>
		public bool Inside(int bx, int by) {
			return ((x - bx) * (x - bx) + (y - by) * (y - by)) - radius * radius <= 0;
		}
	
		/// <summary>
		/// 判定球体是否与指定坐标相撞
		/// </summary>
		///
		/// <param name="bx"></param>
		/// <param name="by"></param>
		/// <returns></returns>
		public bool IsCollide(int bx, int by) {
			int nx = bx;
			int ny = by;
			if (Inside(nx, ny)) {
				double d = MathUtils.Atan2(x - nx, y - ny);
				x = nx + (int) MathUtils.Round(radius * MathUtils.Sin(d));
				y = ny + (int) MathUtils.Round(radius * MathUtils.Cos(d));
				double d1 = MathUtils.Sqrt(vx * vx + vy * vy) * 0.90000000000000002D;
				double d2 = MathUtils.Atan2(vx, -vy);
				d2 = 2D * d - d2;
				vx = d1 * MathUtils.Sin(d2);
				vy = d1 * MathUtils.Cos(d2);
				return true;
			}
			nx = bx + 60;
			if (Inside(nx, ny)) {
				double d_0 = MathUtils.Atan2(x - nx, y - ny);
				x = nx + (int) MathUtils.Round(radius * MathUtils.Sin(d_0));
				y = ny + (int) MathUtils.Round(radius * MathUtils.Cos(d_0));
				double d1_1 = MathUtils.Sqrt(vx * vx + vy * vy) * 0.90000000000000002D;
				double d2_2 = MathUtils.Atan2(vx, -vy);
				d2_2 = 2D * d_0 - d2_2;
				vx = d1_1 * MathUtils.Sin(d2_2);
				vy = d1_1 * MathUtils.Cos(d2_2);
				return true;
			} else {
				return false;
			}
		}
	
		public void Gravity(double d) {
			double d1 = MathUtils.Sqrt(vx * vx + vy * vy) * (1.0D - d);
			double d2 = MathUtils.Atan2(vx, vy);
			this.vx = d1 * MathUtils.Sin(d2);
			this.vy = d1 * MathUtils.Cos(d2);
		}
	
		public void SetVelocity(double d, double d1) {
			this.vx = d;
			this.vy = d1;
		}
	
		public bool Move(int x_0, int y_1, int w, int h) {
			this.x = x_0;
			this.y = y_1;
			if (original.IsBounded()) {
				return !CheckWall(w, h);
			} else {
				return true;
			}
		}
	
		/// <summary>
		/// 进行球体运动检测
		/// </summary>
		///
		/// <param name="w"></param>
		/// <param name="h"></param>
		public void Check(int w, int h) {
			x += (int)vx;
            y += (int)vy;
			Move(x, y, w, h);
			if (MathUtils.Abs(vx) < 1.0D && MathUtils.Abs(vy) < 2D
					&& y == h - radius) {
				isComplete = true;
				vx = 0.0D;
				vy = 0.0D;
			}
			vy += 0.80000000000000004D;
		}
	
		/// <summary>
		/// 判断是否碰触到墙壁边缘
		/// </summary>
		///
		/// <param name="w"></param>
		/// <param name="h"></param>
		/// <returns></returns>
		public bool CheckWall(int w, int h) {
			if (x <= radius) {
				x = 2 * radius - x;
				vx = -vx;
				Gravity(gravity);
				return true;
			}
			if (x >= w - radius) {
				x = 2 * (w - radius) - x;
				vx = -vx;
				Gravity(gravity);
				return true;
			}
			if (y >= h - radius) {
				y = 2 * (h - radius) - y;
				vy = -vy;
				Gravity(gravity);
				return true;
			} else {
				return false;
			}
		}
	
		public override bool IsComplete() {
			return isComplete;
		}
	
		public override void OnLoad() {
			x = (int) original.GetX();
			y = (int) original.GetY();
			isComplete = false;
		}
	
		public double GetGravity() {
			return gravity;
		}
	
		public void SetGravity(double gravity_0) {
			this.gravity = gravity_0;
		}
	
		public int GetRadius() {
			return radius;
		}
	
		public void SetRadius(int radius_0) {
			this.radius = radius_0;
		}
	
		public double GetVx() {
			return vx;
		}
	
		public void SetVx(double vx_0) {
			this.vx = vx_0;
		}
	
		public double GetVy() {
			return vy;
		}
	
		public void SetVy(double vy_0) {
			this.vy = vy_0;
		}
	
		public int GetX() {
			return x;
		}
	
		public void SetX(int x_0) {
			this.x = x_0;
		}
	
		public int GetY() {
			return y;
		}
	
		public void SetY(int y_0) {
			this.y = y_0;
		}
	
		public override void Update(long elapsedTime) {
			if (original.IsContainer()) {
				Check(original.GetContainerWidth(), original.GetContainerHeight());
			}
			 lock (original) {
						original.SetLocation(x + offsetX, y + offsetY);
					}
		}
	
	}
}
