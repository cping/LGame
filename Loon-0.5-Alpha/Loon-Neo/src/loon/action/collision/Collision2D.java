package loon.action.collision;

import loon.action.collision.c2d.Polygon2D;
import loon.geom.Vector2f;
import loon.utils.MathUtils;
import loon.utils.TArray;

public final class Collision2D {
	
	private static Response tmpResponse = new Response();

	private Collision2D() {
	}

	public static boolean checkPolygonCollision(Polygon2D a, Polygon2D b,
			Response response) {
		if (response == null){
			response = tmpResponse.clear();
		}

		Vector2f tmpNormal = Vector2f.TMP();

		for (int i = 0; i < a.vertexCount(); i++) {
			Vector2f e1 = a.getVertex(i);
			Vector2f e2 = a.getVertex((i + 1) % a.vertexCount());

			Vector2f edge = tmpNormal.set(e2).subtractSelf(e1);
			Vector2f normal = edge.perpendicularSelf().normalizeSelf();

			if (isSeparatingAxis(a, b, normal, response)) {
				return false;
			}
		}

		for (int i = 0; i < b.vertexCount(); i++) {
			Vector2f e1 = b.getVertex(i);
			Vector2f e2 = b.getVertex((i + 1) % b.vertexCount());

			Vector2f edge = tmpNormal.set(e2).subtractSelf(e1);
			Vector2f normal = edge.perpendicularSelf().normalizeSelf();

			if (isSeparatingAxis(a, b, normal, response)) {
				return false;
			}
		}

		response.a = a;
		response.b = b;
		response.overlapV.set(response.overlapN).scaleSelf(response.overlap);
		response.intersection = true;

		return true;
	}

	public static boolean isSeparatingAxis(Polygon2D a, Polygon2D b, Vector2f axis,
			Response response) {
		if (response == null){
			response = tmpResponse.clear();
		}

		Vector2f tmpOffset = Vector2f.TMP();
		Vector2f tmpRangeA = Vector2f.TMP();
		Vector2f tmpRangeB = Vector2f.TMP();

		Vector2f offset = tmpOffset.set(b.getPosition()).subtractSelf(
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

	private static Vector2f flattenPoints(TArray<Vector2f> vertices,
			Vector2f normal, Vector2f projection) {
		float min = Float.MAX_VALUE;
		float max = -min;

		for (Vector2f vertex : vertices) {
			float dot = vertex.dot(normal);

			if (dot < min){
				min = dot;
			}
			if (dot > max){
				max = dot;
			}
		}

		return projection.set(min, max);
	}


	public static class Response {
		private Polygon2D a;
		private Polygon2D b;

		private Vector2f overlapV;
		private Vector2f overlapN;

		private float overlap;

		private boolean aInB;
		private boolean bInA;
		private boolean intersection;

		public Response() {
			a = b = null;
			overlapV = new Vector2f();
			overlapN = new Vector2f();

			clear();
		}

		public Response clear() {
			aInB = true;
			bInA = true;
			intersection = false;

			overlap = Float.MAX_VALUE;
			return this;
		}

		public Polygon2D getPolygonA() {
			return a;
		}

		public Polygon2D getPolygonB() {
			return b;
		}

		public Vector2f getMinimumTranslationVector() {
			return intersection ? overlapV : Vector2f.ZERO();
		}

		public Vector2f getOverlapAxis() {
			return intersection ? overlapN : Vector2f.ZERO();
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
