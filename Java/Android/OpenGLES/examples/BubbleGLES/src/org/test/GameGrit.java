package org.test;

import java.util.ArrayList;

import loon.action.sprite.SpriteBatch;
import loon.action.sprite.SpriteBatch.SpriteEffects;
import loon.core.geom.Vector2f;
import loon.core.graphics.opengl.LTextures;
import loon.core.timer.GameTime;
import loon.utils.MathUtils;

public class GameGrit extends GameObject {
	private float animateGrit = 20f;
	private Ball[][] ballGrit = new Ball[9][13];
	private int[] colorsInGrit = new int[10];
	private float currentMoveDownCooldown;
	private int gameOverRow;
	private float moveDownCooldown;
	private int moveDownTimes;
	private int shot;
	private static float yoffset = (Ball.ballGraphWidth * 0.85f);

	public GameGrit() {
		super.setPosition(240f, -684f);
		super.setTexture(LTextures.loadTexture("assets/Grit.png"));
		super.setSource(0, 0, 480, 300);
		super.setOrigin((float) (super.getSource().getWidth() / 2),
				(float) super.getSource().getHeight());
		this.Reset(1f);
	}

	public final void AddBall(int x, int y, Ball ball) {
		if (this.ballGrit[x][y] == null) {
			ball.SetPosition(this.getPositionFromCoordinates(x, y));
			this.ballGrit[x][y] = ball;
			if ((this.ballGrit[x][y].GetColor() != Ball.BombColor)
					&& (this.ballGrit[x][y].GetColor() != Ball.JokerColor)) {
				this.colorsInGrit[this.ballGrit[x][y].GetColor()]++;
			}
		}
	}

	private void AddBallOnHit(int x, int y, Ball ball) {
		int num2;
		this.AddBall(x, y, ball);
		ball.StuckInGrit();
		this.shot--;
		int color = ball.GetColor();
		ArrayList<Vector2f> list = new ArrayList<Vector2f>();
		ArrayList<Vector2f> list2 = new ArrayList<Vector2f>();
		list.add(new Vector2f(x, y));
		list2.add(new Vector2f(x, y));
		ArrayList<Vector2f> list3 = new ArrayList<Vector2f>();
		while (list2.size() > 0) {
			list3 = this.GetNeighbours(list2.get(0).x(), list2.get(0).y(),
					false);
			list2.remove(0);
			num2 = 0;
			while (num2 < list3.size()) {
				if (!(list.contains(list3.get(num2)) || (this.ballGrit[list3
						.get(num2).x()][list3.get(num2).y()].GetColor() != color))) {
					list.add(list3.get(num2));
					list2.add(list3.get(num2));
				}
				num2++;
			}
		}
		if (list.size() >= 3) {
			for (num2 = 0; num2 < list.size(); num2++) {
				this.RemoveBallFromGrit(list.get(num2).x(), list.get(num2).y());
			}
			this.CheckBreakDown();
		}
		if (this.IsWon()) {
			ball.LastBallEffect();
		}
	}

	private void AnimateGrit() {
		int num;
		int num2;
		switch ( MathUtils.random(3)) {
		case 0:
			for (num = 0; num < this.ballGrit.length; num++) {
				num2 = 0;
				while (num2 < this.ballGrit[0].length) {
					if (this.ballGrit[num][num2] != null) {
						this.ballGrit[num][num2]
								.BubbleAnimateBall(1f + (num * 0.1f));
					}
					num2++;
				}
			}
			break;

		case 1:
			num = this.ballGrit.length - 1;
			while (num >= 0) {
				for (num2 = 0; num2 < this.ballGrit[0].length; num2++) {
					if (this.ballGrit[num][num2] != null) {
						this.ballGrit[num][num2]
								.BubbleAnimateBall(1f + (num * 0.1f));
					}
				}
				num--;
			}
			break;

		case 2:
			for (num2 = 0; num2 < this.ballGrit[0].length; num2++) {
				num = 0;
				while (num < this.ballGrit.length) {
					if (this.ballGrit[num][num2] != null) {
						this.ballGrit[num][num2]
								.BubbleAnimateBall(1f + (num2 * 0.1f));
					}
					num++;
				}
			}
			break;

		case 3:
			for (num2 = 0; num2 < this.ballGrit[0].length; num2++) {
				for (num = 0; num < this.ballGrit.length; num++) {
					if (this.ballGrit[num][num2] != null) {
						this.ballGrit[num][num2]
								.BubbleAnimateBall((1f + (num2 * 0.1f))
										+ (num * 0.1f));
					}
				}
			}
			break;
		}
	}

	public final boolean CheckBallCollision(Ball ball) {
		int num4;
		ArrayList<Vector2f> list2;
		ArrayList<Vector2f> list3;
		int num5;
		for (int i = 0; i < this.ballGrit.length; i++) {
			for (int j = 0; j < this.ballGrit[0].length; j++) {
				if (this.ballGrit[i][j] != null) {
					Vector2f vector = this.ballGrit[i][j].GetPosition().sub(
							ball.GetPosition());
					if (vector.len() <= Ball.ballPhysicsWidth) {
						ArrayList<Vector2f> list = this
								.GetNeighbours(i, j, true);
						int num3 = 0;
						num4 = 0;
						while (num4 < list.size()) {
							vector = this.getPositionFromCoordinates(
									list.get(num4).x(), list.get(num4).y())
									.sub(ball.GetPosition());
							vector = this.getPositionFromCoordinates(
									list.get(num3).x(), list.get(num3).y())
									.sub(ball.GetPosition());
							if (vector.len() < vector.len()) {
								num3 = num4;
							}
							num4++;
						}
						list2 = this.GetNeighbours(list.get(num3).x(), list
								.get(num3).y(), false);
						this.CheckJoker(ball, list2);
						this.AddBallOnHit(list.get(num3).x(), list.get(num3)
								.y(), ball);
						list2 = this.GetNeighbours(list.get(num3).x(), list
								.get(num3).y(), false);
						num4 = 0;
						while (num4 < list2.size()) {
							if ((this.ballGrit[list2.get(num4).x()][list2.get(
									num4).y()] != null)
									&& (this.ballGrit[list2.get(num4).x()][list2
											.get(num4).y()].GetColor() == Ball.BombColor)) {
								list3 = this.GetNeighbours(list2.get(num4).x(),
										list2.get(num4).y(), false);
								list3.add(new Vector2f(list2.get(num4).x, list2
										.get(num4).y));
								num5 = 0;
								while (num5 < list3.size()) {
									this.RemoveBallFromGrit(
											list3.get(num5).x(), list3
													.get(num5).y());
									num5++;
								}
								this.CheckBreakDown();
							}
							num4++;
						}
						return true;
					}
				}
			}
		}
		if ((ball.GetPosition().y - (Ball.ballGraphWidth * 0.5f)) <= super
				.getPosition().y) {
			Vector2f point = this.getNextGritPosition(ball.GetPosition());
			list2 = this.GetNeighbours(point.x(), point.y(), false);
			for (num4 = 0; num4 < list2.size(); num4++) {
				this.ballGrit[list2.get(num4).x()][list2.get(num4).y()]
						.HitByBall();
				if (this.ballGrit[list2.get(num4).x()][list2.get(num4).y()]
						.GetColor() == Ball.JokerColor) {
					this.ballGrit[list2.get(num4).x()][list2.get(num4).y()]
							.ChangeColor(ball.GetColor());
				}
			}
			this.AddBallOnHit(point.x(), point.y(), ball);
			list2 = this.GetNeighbours(point.x(), point.y(), false);
			for (num4 = 0; num4 < list2.size(); num4++) {
				if ((this.ballGrit[list2.get(num4).x()][list2.get(num4).y()] != null)
						&& (this.ballGrit[list2.get(num4).x()][list2.get(num4)
								.y()].GetColor() == Ball.BombColor)) {
					list3 = this.GetNeighbours(list2.get(num4).x(),
							list2.get(num4).y(), false);
					list3.add(new Vector2f(list2.get(num4).x, list2.get(num4).y));
					for (num5 = 0; num5 < list3.size(); num5++) {
						this.RemoveBallFromGrit(list3.get(num5).x(),
								list3.get(num5).y());
					}
					this.CheckBreakDown();
				}
			}
			return true;
		}
		return false;
	}

	private ArrayList<Vector2f> CheckBreakDown() {
		ArrayList<Vector2f> list = new ArrayList<Vector2f>();
		ArrayList<Vector2f> list2 = new ArrayList<Vector2f>();
		ArrayList<Vector2f> list3 = new ArrayList<Vector2f>();
		int x = 0;
		while (x < this.ballGrit.length) {
			if (this.ballGrit[x][0] != null) {
				list.add(new Vector2f(x, 0));
				list2.add(new Vector2f(x, 0));
			}
			x++;
		}
		while (list2.size() > 0) {
			list3 = this.GetNeighbours(list2.get(0).x(), list2.get(0).y(),
					false);
			list2.remove(0);
			for (x = 0; x < list3.size(); x++) {
				if (!list.contains(list3.get(x))) {
					list.add(list3.get(x));
					list2.add(list3.get(x));
				}
			}
		}
		for (int i = 0; i < this.ballGrit.length; i++) {
			for (int j = 0; j < this.ballGrit[0].length; j++) {
				if (!((this.ballGrit[i][j] == null) || list
						.contains(new Vector2f(i, j)))) {
					this.RemoveBallFromGrit(i, j);
				}
			}
		}
		return list3;
	}

	private void CheckJoker(Ball ball,
			ArrayList<Vector2f> checkSpecialHits) {
		for (int i = 0; i < checkSpecialHits.size(); i++) {
			this.ballGrit[checkSpecialHits.get(i).x()][checkSpecialHits.get(i)
					.y()].HitByBall();
			if (this.ballGrit[checkSpecialHits.get(i).x()][checkSpecialHits
					.get(i).y()].GetColor() == Ball.JokerColor) {
				this.ballGrit[checkSpecialHits.get(i).x()][checkSpecialHits
						.get(i).y()].ChangeColor(ball.GetColor());
				this.colorsInGrit[ball.GetColor()]++;
				this.CheckJoker(ball, this.GetNeighbours(checkSpecialHits
						.get(i).x(), checkSpecialHits.get(i).y(), false));
			}
		}
	}

	@Override
	public void Draw(GameTime gameTime, SpriteBatch spriteBatch) {
		for (int i = 0; i < this.ballGrit.length; i++) {
			for (int j = 0; j < this.ballGrit[0].length; j++) {
				if (this.ballGrit[i][j] != null) {
					this.ballGrit[i][j].Draw(gameTime, spriteBatch);
				}
			}
		}
		super.Draw(gameTime, spriteBatch);
		spriteBatch.draw(super.getTexture(), super.getPosition().sub(0f, 280f),
				super.getSource(), super.getColor(), super.getRotation(),
				super.getOrigin(), super.getScale(),
				SpriteEffects.FlipVertically);
	}

	public final ArrayList<Integer> getColorsInGrit() {
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < this.colorsInGrit.length; i++) {
			if (this.colorsInGrit[i] > 0) {
				list.add(i);
			}
		}
		if (list.isEmpty()) {
			list.add(0);
		}
		return list;
	}

	private ArrayList<Vector2f> GetNeighbours(int x, int y,
			boolean free) {
		ArrayList<Vector2f> list = new ArrayList<Vector2f>();
		if ((x > 0) && this.IsNeighbour(x - 1, y, free)) {
			list.add(new Vector2f(x - 1, y));
		}
		if ((y % 2) == 0) {
			if ((x < 8) && this.IsNeighbour(x + 1, y, free)) {
				list.add(new Vector2f(x + 1, y));
			}
			if (y > 0) {
				if ((x > 0) && this.IsNeighbour(x - 1, y - 1, free)) {
					list.add(new Vector2f(x - 1, y - 1));
				}
				if ((x <= 7) && this.IsNeighbour(x, y - 1, free)) {
					list.add(new Vector2f(x, y - 1));
				}
			}
			if (y < this.gameOverRow) {
				if ((x > 0)
						&& (((x > 0) && ((y + 1) < this.ballGrit[0].length)) && this
								.IsNeighbour(x - 1, y + 1, free))) {
					list.add(new Vector2f(x - 1, y + 1));
				}
				if (((x <= 7) && ((y + 1) < this.ballGrit[0].length))
						&& this.IsNeighbour(x, y + 1, free)) {
					list.add(new Vector2f(x, y + 1));
				}
			}
			return list;
		}
		if ((x < 7) && this.IsNeighbour(x + 1, y, free)) {
			list.add(new Vector2f(x + 1, y));
		}
		if (y > 0) {
			if (this.IsNeighbour(x, y - 1, free)) {
				list.add(new Vector2f(x, y - 1));
			}
			if ((x < 8) && this.IsNeighbour(x + 1, y - 1, free)) {
				list.add(new Vector2f(x + 1, y - 1));
			}
		}
		if (y < 0x11) {
			if (((y + 1) < this.ballGrit[0].length)
					&& this.IsNeighbour(x, y + 1, free)) {
				list.add(new Vector2f(x, y + 1));
			}
			if (((x < 8) && ((y + 1) < this.ballGrit[0].length))
					&& this.IsNeighbour(x + 1, y + 1, free)) {
				list.add(new Vector2f(x + 1, y + 1));
			}
		}
		return list;
	}

	private Vector2f getNextGritPosition(Vector2f ballPosition) {
		int y = 0;
		for (float i = super.getPosition().y + (Ball.ballGraphWidth * 0.5f); Math
				.abs((float) (i - ballPosition.y)) > (Ball.ballGraphWidth * 0.5f); i += yoffset) {
			y++;
		}
		int x = 0;
		float num4 = (10f + (((y % 2) * Ball.ballGraphWidth) * 0.5f))
				+ (Ball.ballGraphWidth * 0.5f);
		while (MathUtils.abs((num4 - ballPosition.x)) > (Ball.ballGraphWidth * 0.5f)) {
			num4 += Ball.ballGraphWidth;
			x++;
		}
		return new Vector2f(x, y);
	}

	private Vector2f getPositionFromCoordinates(int x, int y) {
		Vector2f position = super.getPosition().cpy();
		position.y += (Ball.ballGraphWidth * 0.5f) + (y * yoffset);
		position.x = (10f + (Ball.ballGraphWidth * (((y % 2) == 0) ? 0.5f : 1f)))
				+ (Ball.ballGraphWidth * x);
		return position;
	}

	public final boolean IsGameOver() {
		for (int i = 0; i < this.ballGrit.length; i++) {
			if (this.ballGrit[i][this.gameOverRow] != null) {
				return true;
			}
		}
		return false;
	}

	private boolean IsNeighbour(int x, int y, boolean free) {
		if (free) {
			return (this.ballGrit[x][y] == null);
		}
		return (this.ballGrit[x][y] != null);
	}

	public final boolean IsWon() {
		for (int i = 0; i < this.ballGrit.length; i++) {
			if (this.ballGrit[i][0] != null) {
				return false;
			}
		}
		return true;
	}

	public final void MoveGritDown() {
		super.setPosition(super.getPosition().x, super.getPosition().y
				+ yoffset);
		this.gameOverRow--;
		for (int i = 0; i < this.ballGrit.length; i++) {
			for (int j = 0; j < this.ballGrit[0].length; j++) {
				if (this.ballGrit[i][j] != null) {
					this.ballGrit[i][j].setPosition(
							this.ballGrit[i][j].GetPosition().x,
							this.ballGrit[i][j].GetPosition().y + yoffset);
				}
			}
		}
	}

	public final void RemoveBallFromGrit(int x, int y) {
		if (this.ballGrit[x][y].GetColor() != Ball.BombColor) {
			this.colorsInGrit[this.ballGrit[x][y].GetColor()]--;
		}
		this.ballGrit[x][y].RemoveFromGrit();
		Ball.AddBallToPool(this.ballGrit[x][y]);
		this.ballGrit[x][y] = null;
	}

	public final void Reset(float time) {
		this.shot = 8;
		this.animateGrit = 5f;
		this.gameOverRow = 12;
		this.moveDownTimes = 20;
		this.gameOverRow += this.moveDownTimes;
		this.moveDownCooldown = time / ((float) this.moveDownTimes);
		this.currentMoveDownCooldown = this.moveDownCooldown;
		super.setPosition(240f, 116f - (this.moveDownTimes * yoffset));
		for (int i = 0; i < this.colorsInGrit.length; i++) {
			this.colorsInGrit[i] = 0;
		}
		for (int j = 0; j < this.ballGrit.length; j++) {
			for (int k = 0; k < this.ballGrit[0].length; k++) {
				Ball.AddBallToPool(this.ballGrit[j][k]);
				this.ballGrit[j][k] = null;
			}
		}
	}

	@Override
	public void SetPosition(Vector2f newPosition) {
		Vector2f vector = super.getPosition().sub(newPosition);
		if (!vector.equals(0, 0)) {
			for (int i = 0; i < this.ballGrit.length; i++) {
				for (int j = 0; j < this.ballGrit[0].length; j++) {
					if (this.ballGrit[i][j] != null) {
						this.ballGrit[i][j].SetPosition(this.ballGrit[i][j]
								.GetPosition().sub(vector));
					}
				}
			}
		}
		super.SetPosition(newPosition);
	}

	@Override
	public void update(GameTime gameTime) {
		super.update(gameTime);
		this.animateGrit -= gameTime.getElapsedGameTime();
		if (this.animateGrit <= 0f) {
			this.AnimateGrit();
			this.animateGrit += 20f;
		}
		if (this.moveDownTimes > 0) {
			this.currentMoveDownCooldown -= gameTime.getElapsedGameTime();
			while (this.currentMoveDownCooldown <= 0f) {
				this.currentMoveDownCooldown += this.moveDownCooldown;
				this.moveDownTimes--;
				this.MoveGritDown();
			}
		}
		for (int i = 0; i < this.ballGrit.length; i++) {
			for (int j = 0; j < this.ballGrit[0].length; j++) {
				if (this.ballGrit[i][j] != null) {
					this.ballGrit[i][j].update(gameTime);
				}
			}
		}
		if (this.shot == 0) {
			this.shot = 8;
			this.MoveGritDown();
		}
	}
}