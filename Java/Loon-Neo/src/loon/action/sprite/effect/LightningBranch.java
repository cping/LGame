package loon.action.sprite.effect;

import loon.action.sprite.SpriteBatch;
import loon.canvas.LColor;
import loon.event.QueryEvent;
import loon.geom.Quaternion;
import loon.geom.Vector2f;
import loon.geom.Vector3f;
import loon.utils.FloatArray;
import loon.utils.MathUtils;
import loon.utils.TArray;
import loon.utils.timer.LTimer;

/*
 * 绘制一个存在支流的闪电线 
 */
public class LightningBranch implements ILightning {

	private LTimer timer = new LTimer(0);
	private Vector2f end;
	private Vector2f direction;
	private TArray<LightningBolt> bolts = new TArray<LightningBolt>();

	public LightningBranch(Vector2f s, Vector2f e) {
		this(s, e, LColor.white);
	}

	public LightningBranch(Vector2f s, Vector2f e, LColor c) {
		this.end = e;
		this.direction = Vector2f.nor(e.sub(s));
		this.create(s, e, c);
	}

	public boolean isComplete() {
		return bolts.isEmpty();
	}

	public void setDelay(long delay) {
		timer.setDelay(delay);
	}

	public long getDelay() {
		return timer.getDelay();
	}

	public void update(long elapsedTime) {
		if (timer.action(elapsedTime)) {
			bolts = bolts.where(new QueryEvent<LightningBolt>() {

				@Override
				public boolean hit(LightningBolt t) {
					return !t.isComplete();
				}
			});
			for (LightningBolt bolt : bolts) {
				bolt.update(elapsedTime);
			}
		}
	}

	public void draw(SpriteBatch spriteBatch) {
		for (LightningBolt bolt : bolts) {
			bolt.draw(spriteBatch);
		}
	}

	private void create(Vector2f start, Vector2f end, LColor c) {
		LightningBolt mainBolt = new LightningBolt(start, end, c);
		bolts.add(mainBolt);
		int numBranches = MathUtils.random(3, 6);
		Vector2f diff = end.sub(start);
		FloatArray branchPoints = FloatArray.range(1, numBranches + 1).where(new QueryEvent<Float>() {

			@Override
			public boolean hit(Float t) {
				return MathUtils.nextBoolean();
			}
		}).sort();
		for (int i = 0; i < branchPoints.length; i++) {
			Vector2f boltStart = mainBolt.getPoint(branchPoints.get(i));
			Quaternion rot = Quaternion.createFromAxisAngle(Vector3f.AXIS_Z(),
					MathUtils.toRadians(30 * ((i & 1) == 0 ? 1 : -1)));
			Vector2f boltEnd = Vector2f.transform(diff.mul(1 - branchPoints.get(i)), rot).add(boltStart);
			bolts.add(new LightningBolt(boltStart, boltEnd, c));
		}
	}

	public Vector2f getDirection() {
		return direction;
	}

	public Vector2f getEnd() {
		return end;
	}

	@Override
	public void close() {
		bolts.clear();
	}

}
