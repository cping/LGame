package loon.action.sprite.effect;

import loon.LTexture;
import loon.action.sprite.SpriteBatch;
import loon.action.sprite.SpriteBatch.SpriteEffects;
import loon.canvas.LColor;
import loon.geom.Vector2f;
import loon.utils.MathUtils;

/*
 * 单纯绘制一个电元素
 */
public class LightningLine {

	private final static float imageThickness = 8;


	private Vector2f capOrigin = new Vector2f();
	private Vector2f middleOrigin = new Vector2f();
	private Vector2f middleScale = new Vector2f();

	public Vector2f lineA;
	public Vector2f lineB;
	public float thickness;

	public LightningLine(Vector2f a, Vector2f b) {
		this(a, b, 1f);
	}

	public LightningLine(Vector2f a, Vector2f b, float t) {
		this.lineA = a;
		this.lineB = b;
		this.thickness = t;
	}
	public void draw(SpriteBatch batch, LColor tint) {

		Vector2f tangent = lineB.sub(lineA);
		float theta = MathUtils.atan2(tangent.y, tangent.x);
		float thicknessScale = thickness / imageThickness;
		
		LTexture HalfCircle = LightningEffect.get().getHalfCircle();
		LTexture LightningSegment = LightningEffect.get().getLightningSegment();
		
		capOrigin.set(HalfCircle.getWidth(), HalfCircle.getHeight() / 2f);
		middleOrigin.set(0, LightningSegment.getHeight() / 2f);
		middleScale.set(tangent.length(), thicknessScale);

		batch.draw(LightningSegment, lineA, tint, theta * MathUtils.RAD_TO_DEG, middleOrigin, middleScale,
				SpriteEffects.None);
		batch.draw(HalfCircle, lineA, tint, theta * MathUtils.RAD_TO_DEG, capOrigin, thicknessScale,
				SpriteEffects.None);
		batch.draw(HalfCircle, lineB, tint, (theta + MathUtils.PI) * MathUtils.RAD_TO_DEG, capOrigin,
				thicknessScale, SpriteEffects.None);
	}
}
