
namespace Loon.Core.Geom {

    using System;
    using System.Collections.Generic;
    using Loon.Utils;

	public class Ellipse : Shape {
	
		protected internal const int DEFAULT_SEGMENT_MAX_COUNT = 50;
	
		private int segmentCount;
	
		private float radius1;
	
		private float radius2;
	
		public Ellipse(float centerPointX, float centerPointY, float radius1_0,
				float radius2_1) {
			this.Set(centerPointX, centerPointY, radius1_0, radius2_1);
		}
	
		public Ellipse(float centerPointX, float centerPointY, float radius1_0,
				float radius2_1, int segmentCount_2) {
			this.Set(centerPointX, centerPointY, radius1_0, radius2_1, segmentCount_2);
		}
	
		public void Set(float centerPointX, float centerPointY, float radius1_0,
				float radius2_1) {
			Set(centerPointX, centerPointY, radius1_0, radius2_1,
					DEFAULT_SEGMENT_MAX_COUNT);
		}
	
		public void Set(float centerPointX, float centerPointY, float radius1_0,
				float radius2_1, int segmentCount_2) {
			this.x = centerPointX - radius1_0;
			this.y = centerPointY - radius2_1;
			this.radius1 = radius1_0;
			this.radius2 = radius2_1;
			this.segmentCount = segmentCount_2;
			this.type = Loon.Core.Geom.ShapeType.ELLIPSE_SHAPE;
			CheckPoints();
		}
	
		/// <summary>
		/// 设定当前椭圆形半径
		/// </summary>
		///
		/// <param name="radius1_0"></param>
		/// <param name="radius2_1"></param>
		public void SetRadii(float radius1_0, float radius2_1) {
			SetRadius1(radius1_0);
			SetRadius2(radius2_1);
		}
	
		public float GetRadius1() {
			return radius1;
		}
	
		public void SetRadius1(float radius1_0) {
			if (radius1_0 != this.radius1) {
				this.radius1 = radius1_0;
				pointsDirty = true;
			}
		}
	
		public float GetRadius2() {
			return radius2;
		}
	
		public void SetRadius2(float radius2_0) {
			if (radius2_0 != this.radius2) {
				this.radius2 = radius2_0;
				pointsDirty = true;
			}
		}
	
		protected internal override void CreatePoints() {
			List<Single> tempPoints = new List<Single>();
	
			maxX = -System.Single.MinValue;
			maxY = -System.Single.MinValue;
			minX = System.Single.MaxValue;
			minY = System.Single.MaxValue;
	
			float start = 0;
			float end = 359;
	
			float cx = x + radius1;
			float cy = y + radius2;
	
			int step = 360 / segmentCount;
	
			for (float a = start; a <= end + step; a += step) {
				float ang = a;
				if (ang > end) {
					ang = end;
				}
                float newX = (float)(cx + (Loon.Utils.MathUtils.Cos(Loon.Utils.MathUtils.ToRadians(ang)) * radius1));
                float newY = (float)(cy + (Loon.Utils.MathUtils.Sin(Loon.Utils.MathUtils.ToRadians(ang)) * radius2));
	
				if (newX > maxX) {
					maxX = newX;
				}
				if (newY > maxY) {
					maxY = newY;
				}
				if (newX < minX) {
					minX = newX;
				}
				if (newY < minY) {
					minY = newY;
				}

                CollectionUtils.Add(tempPoints, newX);
                CollectionUtils.Add(tempPoints, newY);
			}
			points = new float[tempPoints.Count];
			for (int i = 0; i < points.Length; i++) {
				points[i] = (tempPoints[i]);
			}
		}
	
		protected internal override void FindCenter() {
			center = new float[2];
			center[0] = x + radius1;
			center[1] = y + radius2;
		}
	
		protected internal override void CalculateRadius() {
			boundingCircleRadius = (radius1 > radius2) ? radius1 : radius2;
		}
	
		public override int GetHashCode() {
			long bits = BitConverter.DoubleToInt64Bits(GetX());
			bits += BitConverter.DoubleToInt64Bits(GetY()) * 37;
			bits += BitConverter.DoubleToInt64Bits(GetWidth()) * 43;
			bits += BitConverter.DoubleToInt64Bits(GetHeight()) * 47;
			return (((int) bits) ^ ((int) (bits >> 32)));
		}
	
		public override Shape Transform(Matrix transform) {
			CheckPoints();
	
			Polygon resultPolygon = new Polygon();
	
			float[] result = new float[points.Length];
			transform.Transform(points, 0, result, 0, points.Length / 2);
			resultPolygon.points = result;
			resultPolygon.CheckPoints();
	
			return resultPolygon;
		}
	
	}
}
