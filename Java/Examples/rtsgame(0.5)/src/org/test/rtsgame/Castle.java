package org.test.rtsgame;

import loon.action.sprite.SpriteBatch;
import loon.canvas.LColorPool;
import loon.geom.RectBox;
import loon.geom.Vector2f;
//城堡用类（我方进入敌方城堡则我方胜利，敌方进入我方城堡则敌方胜利）
public class Castle {
	
	public RectBox AttackRectangle;
	
	public RectBox BoundingRectangle;
	
	public boolean ChangeFlag;
	
	private GameContent gameContent;
	
	public Vector2f position;
	
	public Shape shape = Shape.square;

	public Castle(GameContent gameContent, Shape shape, Vector2f tileCenter) {
		this.shape = shape;
		this.gameContent = gameContent;
		this.position = tileCenter.cpy();
		int num = 15;
		this.BoundingRectangle = new RectBox((this.position.x) - num,
				(this.position.y) - num, 2 * num, 2 * num);
		this.AttackRectangle = this.BoundingRectangle;
		this.AttackRectangle.inflate(120, 360);
	}

	public final void Draw(SpriteBatch batch) {
		batch
				.draw(this.gameContent.castle[this.shape.getValue()],
						(this.position.add(0f, 15f))
								.sub(this.gameContent.castleOrigin), LColorPool
								.get().getColor(1f, 1f, 1f, 0.8f));
	}
}