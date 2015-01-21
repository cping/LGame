package loon.core.graphics.opengl.math;

import loon.core.BoundingBox;
import loon.core.graphics.opengl.math.Plane.PlaneSide;
import loon.jni.NativeSupport;

public class Frustum {
	protected static final Location3[] clipSpacePlanePoints = {
			new Location3(-1, -1, -1), new Location3(1, -1, -1),
			new Location3(1, 1, -1),
			new Location3(-1, 1, -1), 
			new Location3(-1, -1, 1), new Location3(1, -1, 1),
			new Location3(1, 1, 1), new Location3(-1, 1, 1) }; 
	protected static final float[] clipSpacePlanePointsArray = new float[8 * 3];

	static {
		int j = 0;
		for (Location3 v : clipSpacePlanePoints) {
			clipSpacePlanePointsArray[j++] = v.x;
			clipSpacePlanePointsArray[j++] = v.y;
			clipSpacePlanePointsArray[j++] = v.z;
		}
	}

	public final Plane[] planes = new Plane[6];

	public final Location3[] planePoints = { new Location3(), new Location3(),
			new Location3(), new Location3(), new Location3(), new Location3(),
			new Location3(), new Location3() };
	protected final float[] planePointsArray = new float[8 * 3];

	public Frustum() {
		for (int i = 0; i < 6; i++) {
			planes[i] = new Plane(new Location3(), 0);
		}
	}

	public void update(Transform4 inverseProjectionView) {
		System.arraycopy(clipSpacePlanePointsArray, 0, planePointsArray, 0,
				clipSpacePlanePointsArray.length);
		NativeSupport.prj(inverseProjectionView.val, planePointsArray, 0, 8, 3);
		for (int i = 0, j = 0; i < 8; i++) {
			Location3 v = planePoints[i];
			v.x = planePointsArray[j++];
			v.y = planePointsArray[j++];
			v.z = planePointsArray[j++];
		}

		planes[0].set(planePoints[1], planePoints[0], planePoints[2]);
		planes[1].set(planePoints[4], planePoints[5], planePoints[7]);
		planes[2].set(planePoints[0], planePoints[4], planePoints[3]);
		planes[3].set(planePoints[5], planePoints[1], planePoints[6]);
		planes[4].set(planePoints[2], planePoints[3], planePoints[6]);
		planes[5].set(planePoints[4], planePoints[0], planePoints[1]);
	}

	public boolean pointInFrustum(Location3 point) {
		for (int i = 0; i < planes.length; i++) {
			PlaneSide result = planes[i].testPoint(point);
			if (result == PlaneSide.Back){
				return false;
			}
		}
		return true;
	}

	public boolean pointInFrustum(float x, float y, float z) {
		for (int i = 0; i < planes.length; i++) {
			PlaneSide result = planes[i].testPoint(x, y, z);
			if (result == PlaneSide.Back){
				return false;
			}
		}
		return true;
	}

	public boolean sphereInFrustum(Location3 center, float radius) {
		for (int i = 0; i < 6; i++){
			if ((planes[i].normal.x * center.x + planes[i].normal.y * center.y + planes[i].normal.z
					* center.z) < (-radius - planes[i].d)){
				return false;
			}
		}
		return true;
	}

	public boolean sphereInFrustum(float x, float y, float z, float radius) {
		for (int i = 0; i < 6; i++){
			if ((planes[i].normal.x * x + planes[i].normal.y * y + planes[i].normal.z
					* z) < (-radius - planes[i].d)){
				return false;
			}
		}
		return true;
	}

	public boolean sphereInFrustumWithoutNearFar(Location3 center, float radius) {
		for (int i = 2; i < 6; i++){
			if ((planes[i].normal.x * center.x + planes[i].normal.y * center.y + planes[i].normal.z
					* center.z) < (-radius - planes[i].d)){
				return false;
			}
		}
		return true;
	}

	public boolean sphereInFrustumWithoutNearFar(float x, float y, float z,
			float radius) {
		for (int i = 2; i < 6; i++){
			if ((planes[i].normal.x * x + planes[i].normal.y * y + planes[i].normal.z
					* z) < (-radius - planes[i].d)){
				return false;
			}
		}
		return true;
	}

	public boolean boundsInFrustum(BoundingBox bounds) {
		Location3[] corners = bounds.getCorners();
		int len = corners.length;
		for (int i = 0, len2 = planes.length; i < len2; i++) {
			int out = 0;
			for (int j = 0; j < len; j++){
				if (planes[i].testPoint(corners[j]) == PlaneSide.Back){
					out++;
				}
			}
			if (out == 8){
				return false;
			}
		}
		return true;
	}

	public boolean boundsInFrustum(Location3 center, Location3 dimensions) {
		return boundsInFrustum(center.x, center.y, center.z, dimensions.x / 2,
				dimensions.y / 2, dimensions.z / 2);
	}

	public boolean boundsInFrustum(float x, float y, float z, float halfWidth,
			float halfHeight, float halfDepth) {
		for (int i = 0, len2 = planes.length; i < len2; i++) {
			if (planes[i].testPoint(x + halfWidth, y + halfHeight, z
					+ halfDepth) != PlaneSide.Back)
				continue;
			if (planes[i].testPoint(x + halfWidth, y + halfHeight, z
					- halfDepth) != PlaneSide.Back)
				continue;
			if (planes[i].testPoint(x + halfWidth, y - halfHeight, z
					+ halfDepth) != PlaneSide.Back)
				continue;
			if (planes[i].testPoint(x + halfWidth, y - halfHeight, z
					- halfDepth) != PlaneSide.Back)
				continue;
			if (planes[i].testPoint(x - halfWidth, y + halfHeight, z
					+ halfDepth) != PlaneSide.Back)
				continue;
			if (planes[i].testPoint(x - halfWidth, y + halfHeight, z
					- halfDepth) != PlaneSide.Back)
				continue;
			if (planes[i].testPoint(x - halfWidth, y - halfHeight, z
					+ halfDepth) != PlaneSide.Back)
				continue;
			if (planes[i].testPoint(x - halfWidth, y - halfHeight, z
					- halfDepth) != PlaneSide.Back)
				continue;
			return false;
		}

		return true;
	}

}
