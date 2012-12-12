using System;
namespace Loon.Core.Geom {
	
	public class TriangleNeat : Triangle {
		private const long serialVersionUID = 1L;
	
		private const float EPSILON = 1E-006F;
	
		private float[] pointsX;
	
		private float[] pointsY;
	
		private int numPoints;
	
		private Edge[] edges;
	
		private int[] sV;
	
		private int numEdges;
	
		private Triangle[] triangles;
	
		private int numTriangles;
	
		private float offset;
	
		public TriangleNeat() {
			this.offset = EPSILON;
			pointsX = new float[100];
			pointsY = new float[100];
			numPoints = 0;
			edges = new Edge[100];
			numEdges = 0;
			triangles = new Triangle[100];
			numTriangles = 0;
		}
	
		public void Clear() {
			numPoints = 0;
			numEdges = 0;
			numTriangles = 0;
		}
	
		private int FindEdge(int i, int j) {
			int k;
			int l;
			if (i < j) {
				k = i;
				l = j;
			} else {
				k = j;
				l = i;
			}
			for (int i1 = 0; i1 < numEdges; i1++) {
				if (edges[i1].v0 == k && edges[i1].v1 == l) {
					return i1;
				}
			}
			return -1;
		}
	
		private void AddEdge(int i, int j, int k) {
			int l1 = FindEdge(i, j);
			int j1;
			int k1;
			Edge edge;
			if (l1 < 0) {
				if (numEdges == edges.Length) {
					Edge[] aedge = new Edge[edges.Length * 2];
					System.Array.Copy((Array)(edges),0,(Array)(aedge),0,numEdges);
					edges = aedge;
				}
				j1 = -1;
				k1 = -1;
				l1 = numEdges++;
				edge = edges[l1] = new Edge();
			} else {
				edge = edges[l1];
				j1 = edge.t0;
				k1 = edge.t1;
			}
			int l;
			int i1;
			if (i < j) {
				l = i;
				i1 = j;
				j1 = k;
			} else {
				l = j;
				i1 = i;
				k1 = k;
			}
			edge.v0 = l;
			edge.v1 = i1;
			edge.t0 = j1;
			edge.t1 = k1;
			edge.suspect = true;
		}
	
		internal void MarkSuspect(int i, int j, bool flag) {
			int k;
			if (0 > (k = FindEdge(i, j))) {
				throw new Exception("Attempt to mark unknown edge");
			} else {
				edges[k].suspect = flag;
				return;
			}
		}
	
		private static bool InsideTriangle(float f, float f1, float f2,
				float f3, float f4, float f5, float f6, float f7) {
			float f8 = f4 - f2;
			float f9 = f5 - f3;
			float f10 = f - f4;
			float f11 = f1 - f5;
			float f12 = f2 - f;
			float f13 = f3 - f1;
			float f14 = f6 - f;
			float f15 = f7 - f1;
			float f16 = f6 - f2;
			float f17 = f7 - f3;
			float f18 = f6 - f4;
			float f19 = f7 - f5;
			float f22 = f8 * f17 - f9 * f16;
			float f20 = f12 * f15 - f13 * f14;
			float f21 = f10 * f19 - f11 * f18;
			return f22 >= 0.0D && f21 >= 0.0D && f20 >= 0.0D;
		}
	
		private bool Snip(int i, int j, int k, int l) {
			float f = pointsX[sV[i]];
			float f1 = pointsY[sV[i]];
			float f2 = pointsX[sV[j]];
			float f3 = pointsY[sV[j]];
			float f4 = pointsX[sV[k]];
			float f5 = pointsY[sV[k]];
			if (1E-006F > (f2 - f) * (f5 - f1) - (f3 - f1) * (f4 - f))
				return false;
			for (int i1 = 0; i1 < l; i1++)
				if (i1 != i && i1 != j && i1 != k) {
					float f6 = pointsX[sV[i1]];
					float f7 = pointsY[sV[i1]];
					if (InsideTriangle(f, f1, f2, f3, f4, f5, f6, f7))
						return false;
				}
	
			return true;
		}
	
		private float Area() {
			float f = 0.0F;
			int i = numPoints - 1;
			for (int j = 0; j < numPoints;) {
				f += pointsX[i] * pointsY[j] - pointsY[i] * pointsX[j];
				i = j++;
			}
	
			return f * 0.5F;
		}
	
		public void BasicTriangulation() {
			int i = numPoints;
			if (i < 3)
				return;
			numEdges = 0;
			numTriangles = 0;
			sV = new int[i];
	
			if (0.0D < Area()) {
				for (int k = 0; k < i; k++)
					sV[k] = k;
	
			} else {
				for (int l = 0; l < i; l++)
					sV[l] = numPoints - 1 - l;
	
			}
			int k1 = 2 * i;
			int i1 = i - 1;
			while (i > 2) {
				if (0 >= k1--) {
					throw new Exception("Bad polygon");
				}
	
				int j = i1;
				if (i <= j)
					j = 0;
				i1 = j + 1;
				if (i <= i1)
					i1 = 0;
				int j1 = i1 + 1;
				if (i <= j1)
					j1 = 0;
				if (Snip(j, i1, j1, i)) {
					int l1 = sV[j];
					int i2 = sV[i1];
					int j2 = sV[j1];
					if (numTriangles == triangles.Length) {
						Triangle[] atriangle = new Triangle[triangles.Length * 2];
						System.Array.Copy((triangles),0,(atriangle),0,numTriangles);
						triangles = atriangle;
					}
					triangles[numTriangles] = new Triangle(l1, i2, j2);
					AddEdge(l1, i2, numTriangles);
					AddEdge(i2, j2, numTriangles);
					AddEdge(j2, l1, numTriangles);
					numTriangles++;
					int k2 = i1;
					for (int l2 = i1 + 1; l2 < i; l2++) {
						sV[k2] = sV[l2];
						k2++;
					}
	
					i--;
					k1 = 2 * i;
				}
			}
			sV = null;
		}
	
		public virtual bool Triangulate() {
			try {
				BasicTriangulation();
				return true;
			} catch (Exception) {
				numEdges = 0;
			}
			return false;
		}
	
		public virtual void AddPolyPoint(float x, float y) {
			for (int i = 0; i < numPoints; i++) {
				if ((pointsX[i] == x) && (pointsY[i] == y)) {
					y += offset;
					offset += EPSILON;
				}
			}
	
			if (numPoints == pointsX.Length) {
				float[] af = new float[numPoints * 2];
				System.Array.Copy((Array)(pointsX),0,(Array)(af),0,numPoints);
				pointsX = af;
				af = new float[numPoints * 2];
				System.Array.Copy((Array)(pointsY),0,(Array)(af),0,numPoints);
				pointsY = af;
			}
	
			pointsX[numPoints] = x;
			pointsY[numPoints] = y;
			numPoints++;
		}
	
		internal class Triangle {
			internal int[] v;
	
			internal Triangle(int i, int j, int k) {
				v = new int[3];
				v[0] = i;
				v[1] = j;
				v[2] = k;
			}
		}
	
		internal class Edge {
	
			internal int v0;
	
			internal int v1;
	
			internal int t0;
	
			internal int t1;
	
			internal bool suspect;
	
			internal Edge() {
				v0 = -1;
				v1 = -1;
				t0 = -1;
				t1 = -1;
			}
		}
	
		public virtual int GetTriangleCount() {
			return numTriangles;
		}
	
		public virtual float[] GetTrianglePoint(int tri, int i) {
			float xp = pointsX[triangles[tri].v[i]];
			float yp = pointsY[triangles[tri].v[i]];
	
			return new float[] { xp, yp };
		}
	
		public virtual void StartHole() {
		}
	}
}
