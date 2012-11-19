package org.test;

import loon.action.sprite.SpriteBatch;
import loon.action.sprite.SpriteBatch.SpriteEffects;
import loon.core.LSystem;
import loon.core.geom.RectBox;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.timer.GameTime;
import loon.utils.MathUtils;
//战斗控制用类，用来处理敌我战斗行为(需要和RoleMoveControl类混用)
public class RoleControl {
	private AnimationPlayer animationPlayer = new AnimationPlayer();
	private java.util.ArrayList<Bullet> bullets = new java.util.ArrayList<Bullet>();
	private float Damage;
	private float defaultRotaion;
	private java.util.ArrayList<Vector2f> destination = new java.util.ArrayList<Vector2f>();
	private Animation dieAnimation;
	private GameContent gameContent;
	private float health;
	private Animation idleAnimation;
	private float MaxHealth;
	private float MaxReloadTime;
	private float MoveSpeed;
	private float pathVisibleTime;
	public Vector2f position;
	private float Range;
	private RoleRank rank;
	private float reloadTime;
	private float removeTime = 1.8f;
	private float rotation;
	public Shape shape;
	private Animation walkAnimation;

	public RoleControl(GameContent gameContent, Shape shape, RoleRank rank,
			Vector2f position) {
		this.gameContent = gameContent;
		this.shape = shape;
		this.rank = rank;
		this.position = position;
		this.walkAnimation = new Animation(gameContent.walk[shape.getValue()],
				2, 0.1f, true, new Vector2f(0.5f));
		this.idleAnimation = new Animation(gameContent.idle[shape.getValue()],
				1, 0.1f, true, new Vector2f(0.5f));
		this.dieAnimation = new Animation(gameContent.die[shape.getValue()], 3,
				0.1f, false, new Vector2f(0.5f));
		this.animationPlayer.PlayAnimation(this.idleAnimation);
		RoleDetails details = RoleDetails.Load(rank);
		this.Damage = details.Damage;
		this.health = this.MaxHealth = details.Health;
		this.MaxReloadTime = details.ReloadTime;
		this.Range = details.Range;
		this.MoveSpeed = details.MoveSpeed;
		this.rotation = this.defaultRotaion = (shape == Shape.square) ? 0f
				: 3.141593f;
	}

	public final void AddPosition(Vector2f nextDestPosition) {
		Vector2f vector;
		nextDestPosition.y = MathUtils.clamp(nextDestPosition.y, 0f,
				LSystem.screenRect.height - 1f);
		nextDestPosition.x = MathUtils.clamp(nextDestPosition.x, 0f,
				LSystem.screenRect.width - 1f);

		vector = nextDestPosition;
		Vector2f position = this.position.cpy();
		if (this.destination.size() != 0) {
			position = this.destination.get(this.destination.size() - 1).cpy();
		}
		float num = MathUtils.atan2((nextDestPosition.y - position.y),
				(nextDestPosition.x - position.x));

		if (Vector2f.dst(position, nextDestPosition) > Tile.size) {
			vector = position.add(new Vector2f(MathUtils.cos(num) * Tile.size,
					MathUtils.sin(num) * Tile.size));
		}

		Vector2f item = new Vector2f(Tile.getBounds(vector).getCenterX(), Tile
				.getBounds(vector).getCenterY());

		if (!this.destination.contains(item)) {
			this.destination.add(item);
			AddPosition(vector);
		}
		int index = this.destination.indexOf(item);
		this.destination.subList(index + 1,
				(this.destination.size() - index) - 1 + index + 1).clear();
	}

	private RectBox rect = new RectBox();

	public final void Draw(GameTime gameTime, SpriteBatch batch) {
		for (Bullet bullet : this.bullets) {
			bullet.Draw(batch);
		}
		if (!this.isAlive()) {
			this.removeTime = Math.max(
					(this.removeTime - gameTime.getElapsedGameTime()), 0f);
		}

		LColor colour = LColor.white;
		this.animationPlayer
				.Draw(gameTime,
						batch,
						this.position,
						colour,
						0f,
						(Math.abs(this.rotation) > 1.570796f) ? SpriteEffects.FlipHorizontally
								: SpriteEffects.None);
		batch.draw(
				this.gameContent.weapon[(int) this.rank.getValue()],
				this.position,
				null,
				colour,
				MathUtils.radToDeg(this.rotation),
				15,
				15,
				1f,
				(Math.abs(this.rotation) > 1.570796f) ? SpriteEffects.FlipVertically
						: SpriteEffects.None);
		if (this.isAlive()) {
			batch.draw(this.gameContent.healthBar, this.position.sub(
					(this.gameContent.healthBar.getWidth() / 2), 20f),
					LColor.white);
			rect.setBounds(
					0,
					0,
					(int) ((this.health / this.MaxHealth) * this.gameContent.healthBar
							.getWidth()), this.gameContent.healthBar
							.getHeight());
			batch.draw(this.gameContent.healthBar, this.position.sub(
					(this.gameContent.healthBar.getWidth() / 2), 20f), rect,
					LColor.red);
			batch.flush();
		}
	}

	public final void DrawMouseOver(SpriteBatch batch) {
		this.pathVisibleTime = 0.8f;
		batch.draw(this.gameContent.mouseOver,
				this.position.sub(this.gameContent.mouseOverOrigin),
				LColor.gold);
		batch.flush();
	}

	public final void DrawPath(GameTime gameTime, SpriteBatch batch) {
		LColor color = LColor.white;
		for (int i = 0; i < (this.destination.size() - 1); i++) {
			float rotation = MathUtils
					.atan2((this.destination.get(i + 1).y - this.destination
							.get(i).y),
							(this.destination.get(i + 1).x - this.destination
									.get(i).x));
			batch.draw(this.gameContent.pathArrow, this.destination.get(i),
					null, color, MathUtils.radToDeg(rotation), 15f, 15f, 1f,
					SpriteEffects.None);
		}
		if (this.destination.size() > 0) {
			batch.draw(this.gameContent.pathCross,
					this.destination.get(this.destination.size() - 1), null,
					color, 0f, 15f, 15f, 1f, SpriteEffects.None);
		}
		this.pathVisibleTime = MathUtils.max(
				(this.pathVisibleTime - (gameTime.getElapsedGameTime())), 0f);

	}

	public final void EnemyAI(RectBox enemyCastleBounds) {
		if (this.destination.isEmpty()) {
			if (this.position.x < 250f) {
				this.AddPosition(new Vector2f(enemyCastleBounds.getCenterX(),
						enemyCastleBounds.getCenterY()));
			} else {
				int num = this.gameContent.random
						.nextInt(LSystem.screenRect.width) - 1;
				int num2 = this.gameContent.random
						.nextInt(LSystem.screenRect.height) - 1;
				this.AddPosition(new Vector2f(num, num2));
			}
		}
	}

	public final void HandleEnemy(java.util.ArrayList<RoleControl> armies) {
		RoleControl army = null;
		float positiveInfinity = Float.POSITIVE_INFINITY;
		for (int i = 0; i < armies.size(); i++) {
			if ((armies.get(i) != this) && (armies.get(i).shape != this.shape)) {
				float num3 = Vector2f
						.dst(armies.get(i).position, this.position);
				if ((positiveInfinity > num3) && armies.get(i).isAlive()) {
					positiveInfinity = num3;
					army = armies.get(i);
				}
				for (int j = armies.get(i).bullets.size() - 1; j >= 0; j--) {
					if (this.getBoundingRectangle()
							.intersects(
									armies.get(i).bullets.get(j)
											.getBoundingRectangle())) {
						this.health = MathUtils.max(
								(this.health - armies.get(i).Damage), 0f);
						armies.get(i).bullets.remove(j);
						if (!this.isAlive()) {
							this.gameContent.dieSound.Play();
							this.animationPlayer
									.PlayAnimation(this.dieAnimation);
						}
					}
				}
			}
		}
		if (army != null) {
			this.rotation = MathUtils.atan2(
					(army.position.y - this.position.y),
					(army.position.x - this.position.x));
		}
		if (this.rank.equals(RoleRank.bazooka)) {
			this.rotation = this.defaultRotaion;
		}
		if (((army != null) && (positiveInfinity < this.Range))
				&& (this.reloadTime > this.MaxReloadTime)) {
			this.reloadTime = 0f;
			this.bullets.add(new Bullet(this.gameContent.bullet[this.rank
					.getValue()], this.rank, this.position, this.rotation));
			this.gameContent.noise[this.rank.getValue()].Play();
		}
	}

	public final void Reset() {
		this.destination.clear();
	}

	public final void Update(GameTime gameTime) {
		for (int i = this.bullets.size() - 1; i >= 0; i--) {
			this.bullets.get(i).Update(gameTime);
			if (this.bullets.get(i).totalDistance > this.Range) {
				this.bullets.remove(this.bullets.get(i));
			}
		}
		if (this.isAlive()) {
			if (this.destination.size() > 0) {
				Vector2f vector = this.destination.get(0);
				float num2 = Vector2f.dst(vector, this.position);
				float num3 = MathUtils.atan2((vector.y - this.position.y),
						(vector.x - this.position.x));
				this.rotation = num3;
				this.position.addLocal(((new Vector2f(MathUtils.cos(num3),
						MathUtils.sin(num3)).mul(this.MoveSpeed).mul(gameTime
						.getElapsedGameTime())).mul(40f)));
				if (num2 <= this.MoveSpeed) {
					this.position.set(vector);
					this.destination.remove(0);
				}

				this.animationPlayer.PlayAnimation(this.walkAnimation);
			} else {
				this.animationPlayer.PlayAnimation(this.idleAnimation);
			}
			this.reloadTime += gameTime.getElapsedGameTime();
		}
	}

	public final RectBox getBoundingRectangle() {
		int num = 7;
		rect.setBounds(((int) this.position.x) - num, ((int) this.position.y)
				- num, 2 * num, 2 * num);
		return rect;
	}

	public final boolean isAlive() {
		return (this.health > 0f);
	}

	public final RectBox getMouseBounds() {
		int num = 15;
		rect.setBounds(((int) this.position.x) - num, ((int) this.position.y)
				- num, 2 * num, 2 * num);
		return rect;
	}
}