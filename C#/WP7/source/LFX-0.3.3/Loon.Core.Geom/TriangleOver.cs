namespace Loon.Core.Geom {

    using Loon.Java.Collections;

	public class TriangleOver : Triangle {
	
		private float[][] triangles;
	
		public TriangleOver(Triangle t) {
			triangles = (float[][])Arrays.CreateJaggedArray(typeof(float), t.GetTriangleCount() * 6 * 3, 2);
	
			int tcount = 0;
			for (int i = 0; i < t.GetTriangleCount(); i++) {
				float cx = 0;
				float cy = 0;
				for (int p = 0; p < 3; p++) {
					float[] pt = t.GetTrianglePoint(i, p);
					cx += pt[0];
					cy += pt[1];
				}
	
				cx /= 3;
				cy /= 3;
	
				for (int p_0 = 0; p_0 < 3; p_0++) {
					int n = p_0 + 1;
					if (n > 2) {
						n = 0;
					}
	
					float[] pt1 = t.GetTrianglePoint(i, p_0);
					float[] pt2 = t.GetTrianglePoint(i, n);
	
					pt1[0] = (pt1[0] + pt2[0]) / 2;
					pt1[1] = (pt1[1] + pt2[1]) / 2;
	
					triangles[(tcount * 3) + 0][0] = cx;
					triangles[(tcount * 3) + 0][1] = cy;
					triangles[(tcount * 3) + 1][0] = pt1[0];
					triangles[(tcount * 3) + 1][1] = pt1[1];
					triangles[(tcount * 3) + 2][0] = pt2[0];
					triangles[(tcount * 3) + 2][1] = pt2[1];
					tcount++;
				}
	
				for (int p_1 = 0; p_1 < 3; p_1++) {
					int n_2 = p_1 + 1;
					if (n_2 > 2) {
						n_2 = 0;
					}
	
					float[] pt1_3 = t.GetTrianglePoint(i, p_1);
					float[] pt2_4 = t.GetTrianglePoint(i, n_2);
	
					pt2_4[0] = (pt1_3[0] + pt2_4[0]) / 2;
					pt2_4[1] = (pt1_3[1] + pt2_4[1]) / 2;
	
					triangles[(tcount * 3) + 0][0] = cx;
					triangles[(tcount * 3) + 0][1] = cy;
					triangles[(tcount * 3) + 1][0] = pt1_3[0];
					triangles[(tcount * 3) + 1][1] = pt1_3[1];
					triangles[(tcount * 3) + 2][0] = pt2_4[0];
					triangles[(tcount * 3) + 2][1] = pt2_4[1];
					tcount++;
				}
			}
		}
	
		public virtual void AddPolyPoint(float x, float y) {
		}
	
		public virtual int GetTriangleCount() {
			return triangles.Length / 3;
		}
	
		public virtual float[] GetTrianglePoint(int tri, int i) {
			float[] pt = triangles[(tri * 3) + i];
	
			return new float[] { pt[0], pt[1] };
		}
	
		public virtual void StartHole() {
		}
	
		public virtual bool Triangulate() {
			return true;
		}
	
	}
}
