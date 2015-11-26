package loon.action.collision.c3d;

import loon.geom.Vector3f;
import loon.utils.MathUtils;

public class Sphere3D extends Polygon3D {
	
	private float radius;

	public Sphere3D(Vector3f position, float radius) {
		this.radius = radius;
		setPosition(position);
		updateVertices();
	}

	private void updateVertices() {
		clearVertices();

		final int bandPower = 6;
		final int bandPoints = (int) MathUtils.pow(2, bandPower);
		final int bandMask = bandPoints - 2;
		final int sectionsInBand = (bandPoints / 2) - 1;
		final int totalPoints = sectionsInBand * bandPoints;

		final float sectionArc = MathUtils.TWO_PI / sectionsInBand;
		final float radius = -this.radius;

		float xAngle;
		float yAngle;

		for (int i = 0; i < totalPoints; i++) {
			xAngle = (i & 1) + (i >> bandPower);
			yAngle = ((i & bandMask) >> 1)
					+ ((i >> bandPower) * sectionsInBand);

			xAngle *= sectionArc / 2f;
			yAngle *= sectionArc * -1;

			float x = (radius * MathUtils.sin(xAngle) * MathUtils.sin(yAngle));
			float y = (radius * MathUtils.cos(xAngle));
			float z = (radius * MathUtils.sin(xAngle) * MathUtils.cos(yAngle));

			addVertex(new Vector3f(x, y, z));
		}
	}

	public float getRadius() {
		return getWidth() / 2;
	}
}
