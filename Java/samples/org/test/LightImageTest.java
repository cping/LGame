package org.test;

import loon.Screen;
import loon.action.sprite.ISprite;
import loon.action.sprite.Sprite;
import loon.events.FrameLoopEvent;
import loon.events.GameTouch;
import loon.geom.PointI;
import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.timer.LTimerContext;

public class LightImageTest extends Screen {

	int tick = 0;
	int frequency = 80;
	int type = 0;

	@Override
	public void draw(GLEx g) {

	}

	@Override
	public void onLoad() {
		
		add(MultiScreenTest.getBackButton(this, 1));
		final int limit = 40;
		loop(0, new FrameLoopEvent() {

			@Override
			public void invoke(long elapsedTime, Screen e) {

				if (tick > frequency) {
					tick = 0;

					Sprite laser = new Sprite("laser/laser0" + ((type % 5) + 1)
							+ ".png");
					laser.setStatus(0);
					
					type++;

					PointI pos1;
					PointI pos2;
					if (type % 2 == 0) {
						pos1 = new PointI((int) (-limit * MathUtils.random()),
								(int) MathUtils.random() * getHeight());
						pos2 = new PointI((int) (getWidth() * MathUtils
								.random()), (int) MathUtils.random()
								* getHeight() + limit);

					} else {
						pos1 = new PointI(
								(int) MathUtils.random() * getWidth(),
								(int) (-limit * MathUtils.random()));
						pos2 = new PointI(
								(int) MathUtils.random() * getWidth(),
								(int) ((getHeight() + limit) * MathUtils
										.random()));
					}

					int distX = pos1.x - pos2.x;
					int distY = pos1.y - pos2.y;

					int dist = (int) MathUtils.sqrt(distX * distX + distY * distY);

					laser.setScaleX(dist);
					laser.setLocation(pos1);

					laser.blendLight();

					laser.setRotation((MathUtils.atan2(distY, distX) + MathUtils.PI)
							* 180 / MathUtils.PI);

					add(laser);

					frequency *= 0.9;
				}

				ISprite[] sprites = ELF().getSprites();
				for (int i = 0; i < sprites.length; i++) {
					Sprite laser = (Sprite) sprites[i];
					laser.setStatus(laser.getStatus() + 1);
					if (laser.getStatus() > 60 * 0.3) {
						laser.setAlpha(laser.getAlpha() * 0.9f);
						laser.setScaleY(laser.getAlpha());
						if (laser.getAlpha() < 0.01) {
							sprites = ELF().getSprites();
							remove(laser);
							i--;
						}
					}
				}
				tick += 1;
			}

			@Override
			public void completed() {
				

			}
		});
	}

	@Override
	public void alter(LTimerContext timer) {
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void touchDown(GameTouch e) {

	}

	@Override
	public void touchUp(GameTouch e) {

	}

	@Override
	public void touchMove(GameTouch e) {

	}

	@Override
	public void touchDrag(GameTouch e) {

	}

	@Override
	public void resume() {

	}

	@Override
	public void pause() {

	}

	@Override
	public void close() {

	}

}
