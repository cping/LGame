package loon.action.collision;

import loon.action.collision.c3d.Polygon3D;
import loon.geom.Vector2f;
import loon.geom.Vector3f;
import loon.utils.MathUtils;
import loon.utils.TArray;

public class Collision3D {

	private static Response tmpResponse = new Response();

	public static boolean checkPolyhedronCollision(Polygon3D a, Polygon3D b) {
		return checkPolyhedronCollision(a, b, null);
	}

	public static boolean checkPolyhedronCollision(Polygon3D a, Polygon3D b,
			Response response) {
		if (response == null) {
			response = tmpResponse.clear();
		}

		Vector3f tmpAxis = Vector3f.TMP();
		Vector3f tmpEdge1 = Vector3f.TMP();
		Vector3f tmpEdge2 = Vector3f.TMP();

		Vector3f v1, v2, v3;

		for (int v = 0; v < a.vertexCount() - 2; v++) {
			if ((v & 1) != 0) {

				v1 = a.getVertex(v);
				v2 = a.getVertex(v + 1);
				v3 = a.getVertex(v + 2);
			} else {

				v1 = a.getVertex(v);
				v2 = a.getVertex(v + 2);
				v3 = a.getVertex(v + 1);
			}

			tmpEdge1.set(v2).addSelf(a.getPosition()).subtractSelf(v1);
			tmpEdge2.set(v3).addSelf(a.getPosition()).subtractSelf(v1);

			tmpAxis.set(tmpEdge1).crossSelf(tmpEdge2).normalizeSelf();

			if (tmpAxis.lengthSquared() == 0) {
				continue;
			}

			if (isSeparatingAxis(a, b, tmpAxis, response)) {
				return false;
			}
		}

		for (int v = 0; v < b.vertexCount() - 2; v++) {
			if ((v & 1) != 0) {
				v1 = b.getVertex(v);
				v2 = b.getVertex(v + 1);
				v3 = b.getVertex(v + 2);
			} else {
				v1 = b.getVertex(v);
				v2 = b.getVertex(v + 2);
				v3 = b.getVertex(v + 1);
			}

			tmpEdge1.set(v2).subtractSelf(v1);
			tmpEdge2.set(v3).subtractSelf(v1);

			tmpAxis.set(tmpEdge1).crossSelf(tmpEdge2).normalizeSelf();

			if (tmpAxis.lengthSquared() == 0) {
				continue;
			}

			if (isSeparatingAxis(a, b, tmpAxis, response)) {

				return false;
			}
		}

		response.a = a;
		response.b = b;
		response.intersection = true;
		response.overlapV.set(response.overlapN).scaleSelf(response.overlap);

		return true;
	}

	public static boolean isSeparatingAxis(Polygon3D a, Polygon3D b,
			Vector3f axis, Response response) {
		if (response == null) {
			response = tmpResponse.clear();
		}

		Vector3f tmpOffset = Vector3f.TMP();
		Vector2f tmpRangeA = Vector2f.TMP();
		Vector2f tmpRangeB = Vector2f.TMP();

		Vector3f offset = tmpOffset.set(b.getPosition()).subtractSelf(
				a.getPosition());
		float projectedOffset = offset.dot(axis);

		Vector2f rangeA = flattenPoints(a.getVertices(), axis, tmpRangeA);
		Vector2f rangeB = flattenPoints(b.getVertices(), axis, tmpRangeB);

		rangeB.addSelf(projectedOffset, projectedOffset);

		if (rangeA.x > rangeB.y || rangeB.x > rangeA.y) {

			return true;
		}

		float overlap;

		if (rangeA.x < rangeB.x) {
			response.aInB = false;

			if (rangeA.y < rangeB.y) {
				overlap = rangeA.y - rangeB.x;
				response.bInA = false;
			} else {
				float option1 = rangeA.y - rangeB.x;
				float option2 = rangeB.y - rangeA.x;
				overlap = option1 < option2 ? option1 : -option2;
			}
		} else {
			response.bInA = false;

			if (rangeA.y > rangeB.y) {
				overlap = rangeA.y - rangeB.x;
				response.aInB = false;
			} else {
				float option1 = rangeA.y - rangeB.x;
				float option2 = rangeB.y - rangeA.x;
				overlap = option1 < option2 ? option1 : -option2;
			}
		}

		overlap = MathUtils.abs(overlap);

		if (overlap < response.overlap) {
			response.overlap = overlap;
			response.overlapN.set(axis.normalizeSelf());

			if (overlap < 0)
				response.overlapN.negateSelf();
		}

		return false;
	}

	private static Vector2f flattenPoints(TArray<Vector3f> vertices,
			Vector3f axis, Vector2f projection) {
		float min = axis.dot(vertices.get(0));
		float max = min;

		for (Vector3f v : vertices) {
			float dot = axis.dot(v);

			if (dot < min)
				min = dot;
			if (dot > max)
				max = dot;
		}

		return projection.set(min, max);
	}

	public static Response getResponse() {
		return tmpResponse;
	}

	public static class Response {
		private Polygon3D a;
		private Polygon3D b;

		private Vector3f overlapV;
		private Vector3f overlapN;

		private float overlap;

		private boolean aInB;
		private boolean bInA;
		private boolean intersection;

		public Response() {
			a = b = null;
			overlapV = new Vector3f();
			overlapN = new Vector3f();

			clear();
		}

		public Response clear() {
			aInB = true;
			bInA = true;
			intersection = false;

			overlap = Float.POSITIVE_INFINITY;
			return this;
		}

		public Polygon3D getPolygonA() {
			return a;
		}

		public Polygon3D getPolygonB() {
			return b;
		}

		public Vector3f getMinimumTranslationVector() {
			return intersection ? overlapV : Vector3f.ZERO();
		}

		public Vector3f getOverlapAxis() {
			return intersection ? overlapN : Vector3f.ZERO();
		}

		public float getOverlapDistance() {
			return intersection ? overlap : 0;
		}

		public boolean isAInsideB() {
			return aInB && intersection;
		}

		public boolean isBInsideA() {
			return bInA && intersection;
		}

	}
}
