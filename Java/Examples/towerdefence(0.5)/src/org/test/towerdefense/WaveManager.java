package org.test.towerdefense;

import loon.LTexture;
import loon.LTextures;
import loon.action.sprite.SpriteBatch;
import loon.action.sprite.painting.DrawableGameComponent;
import loon.action.sprite.painting.IGameComponent;
import loon.canvas.LColor;
import loon.font.LFont;
import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.utils.timer.GameTime;

public class WaveManager extends DrawableGameComponent implements
		IGameComponent {

	private java.util.ArrayList<Wave> activeWaves;
	private Vector2f drawPosition;
	private LFont font;
	private MainGame game;
	private boolean isLastWave;
	private AnimatedSprite nextWaveMonsterType;
	private LTexture texture;
	private double timeUntilNextWave;
	private int waveNumber;
	private java.util.ArrayList<Wave> waves;

	public WaveManager(MainGame game, Difficulty difficulty) {
		super(game);
		this.waves = new java.util.ArrayList<Wave>();
		this.activeWaves = new java.util.ArrayList<Wave>();
		this.drawPosition = new Vector2f(70f, -4f);
		this.game = game;
		switch (difficulty) {
		case Easy:
			this.waves.add(new Wave(game, 8, 20, 1f, 1500.0, 1,
					MonsterType.Peasant));
			this.waves.add(new Wave(game, 8, 20, 1f, 1000.0, 1,
					MonsterType.Peasant));
			this.waves.add(new Wave(game, 10, 30, 1.6f, 1000.0, 2,
					MonsterType.Peon));
			this.waves.add(new Wave(game, 10, 30, 1f, 200.0, 2,
					MonsterType.Peasant));
			this.waves.add(new Wave(game, 10, 30, 1f, 1000.0, 2,
					MonsterType.Chicken));
			this.waves.add(new Wave(game, 10, 50, 1f, 1000.0, 2,
					MonsterType.Berserker));
			this.waves.add(new Wave(game, 10, 70, 1f, 1000.0, 2,
					MonsterType.Chicken));
			this.waves.add(new Wave(game, 10, 80, 1f, 200.0, 2,
					MonsterType.Berserker));
			this.waves.add(new Wave(game, 1, 600, 1f, 1000.0, 0x19,
					MonsterType.Chieftain));
			this.waves.add(new Wave(game, 10, 100, 1.6f, 300.0, 3,
					MonsterType.Peon));
			this.waves.add(new Wave(game, 10, 130, 1f, 1000.0, 3,
					MonsterType.Berserker));
			this.waves.add(new Wave(game, 13, 110, 1.2f, 800.0, 3,
					MonsterType.Chicken));
			this.waves.add(new Wave(game, 2, 0x3e8, 1f, 1000.0, 0x19,
					MonsterType.Doctor));
			this.waves.add(new Wave(game, 10, 150, 1f, 200.0, 3,
					MonsterType.Berserker));
			this.waves.add(new Wave(game, 10, 100, 1.6f, 1000.0, 3,
					MonsterType.Peon));
			game.Components().add(this);
			this.timeUntilNextWave = -1.0;
			break;

		case Medium:
			this.waves.add(new Wave(game, 10, 40, 1f, 1500.0, 1,
					MonsterType.Peasant));
			this.waves.add(new Wave(game, 10, 0x2d, 1.6f, 1000.0, 2,
					MonsterType.Peon));
			this.waves.add(new Wave(game, 10, 50, 1f, 1000.0, 1,
					MonsterType.Chicken));
			this.waves.add(new Wave(game, 5, 100, 1f, 400.0, 4,
					MonsterType.Berserker));
			this.waves.add(new Wave(game, 10, 60, 1f, 1000.0, 2,
					MonsterType.Chicken));
			this.waves.add(new Wave(game, 5, 0x4b, 1f, 200.0, 8,
					MonsterType.Doctor));
			this.waves.add(new Wave(game, 10, 80, 1.6f, 1000.0, 2,
					MonsterType.Peon));
			this.waves.add(new Wave(game, 10, 100, 1f, 1000.0, 2,
					MonsterType.Peasant));
			this.waves.add(new Wave(game, 10, 120, 1f, 1000.0, 2,
					MonsterType.Chicken));
			this.waves.add(new Wave(game, 10, 130, 1f, 200.0, 2,
					MonsterType.Berserker));
			this.waves.add(new Wave(game, 10, 150, 1f, 1000.0, 2,
					MonsterType.Berserker));
			this.waves.add(new Wave(game, 1, 0x7d0, 1f, 1000.0, 0x19,
					MonsterType.Chieftain));
			this.waves.add(new Wave(game, 10, 0xaf, 1.6f, 300.0, 3,
					MonsterType.Peon));
			this.waves.add(new Wave(game, 10, 200, 1f, 1000.0, 3,
					MonsterType.Berserker));
			this.waves.add(new Wave(game, 13, 200, 1.2f, 800.0, 3,
					MonsterType.Chicken));
			this.waves.add(new Wave(game, 3, 0x3e8, 1f, 1000.0, 20,
					MonsterType.Doctor));
			this.waves.add(new Wave(game, 10, 200, 1f, 200.0, 3,
					MonsterType.Berserker));
			this.waves.add(new Wave(game, 10, 220, 1.6f, 1000.0, 3,
					MonsterType.Peon));
			this.waves.add(new Wave(game, 1, 0xbb8, 1f, 1000.0, 30,
					MonsterType.Chieftain));
			this.waves.add(new Wave(game, 10, 250, 1f, 1000.0, 4,
					MonsterType.Peasant));
			this.waves.add(new Wave(game, 5, 800, 1f, 3000.0, 20,
					MonsterType.Doctor));
			this.waves.add(new Wave(game, 10, 300, 1f, 1000.0, 4,
					MonsterType.Chicken));
			this.waves.add(new Wave(game, 10, 300, 1f, 1000.0, 4,
					MonsterType.Peasant));
			this.waves.add(new Wave(game, 10, 350, 1f, 1000.0, 4,
					MonsterType.Berserker));
			this.waves.add(new Wave(game, 1, 0xdac, 1f, 1000.0, 4,
					MonsterType.Chieftain));
			this.waves.add(new Wave(game, 10, 400, 1f, 200.0, 5,
					MonsterType.Peon));
			this.waves.add(new Wave(game, 10, 450, 1f, 1000.0, 5,
					MonsterType.Peasant));
			this.waves.add(new Wave(game, 10, 500, 1f, 500.0, 5,
					MonsterType.Chicken));
			this.waves.add(new Wave(game, 1, 0x157c, 1f, 1000.0, 40,
					MonsterType.Chieftain));
			this.waves.add(new Wave(game, 4, 0x4b0, 1f, 1000.0, 40,
					MonsterType.Doctor));
			game.Components().add(this);
			this.timeUntilNextWave = -1.0;
			break;
		case Hard:
			this.waves.add(new Wave(game, 12, 60, 1f, 1000.0, 1,
					MonsterType.Peasant));
			this.waves.add(new Wave(game, 12, 0x41, 1.8f, 1000.0, 1,
					MonsterType.Peon));
			this.waves.add(new Wave(game, 10, 60, 1f, 1000.0, 1,
					MonsterType.Chicken));
			this.waves.add(new Wave(game, 12, 80, 1f, 1000.0, 1,
					MonsterType.Peasant));
			this.waves.add(new Wave(game, 12, 130, 1f, 1000.0, 1,
					MonsterType.Berserker));
			this.waves.add(new Wave(game, 6, 100, 1f, 2000.0, 2,
					MonsterType.Doctor));
			this.waves.add(new Wave(game, 12, 120, 1f, 1000.0, 1,
					MonsterType.Peasant));
			this.waves.add(new Wave(game, 10, 90, 1f, 1000.0, 1,
					MonsterType.Chicken));
			this.waves.add(new Wave(game, 12, 150, 1f, 1000.0, 2,
					MonsterType.Peasant));
			this.waves.add(new Wave(game, 12, 150, 1.8f, 1000.0, 2,
					MonsterType.Peon));
			this.waves.add(new Wave(game, 10, 190, 1f, 300.0, 2,
					MonsterType.Berserker));
			this.waves.add(new Wave(game, 8, 100, 1f, 1000.0, 2,
					MonsterType.Chicken));
			this.waves.add(new Wave(game, 10, 110, 1f, 200.0, 2,
					MonsterType.Chicken));
			if (game.getGameplayScreen().getLevel() != 2) {
				this.waves.add(new Wave(game, 12, 240, 1.8f, 1000.0, 2,
						MonsterType.Peon));
				this.waves.add(new Wave(game, 1, 0x4b0, 1f, 1000.0, 0x19,
						MonsterType.Chieftain));
				break;
			}
			this.waves.add(new Wave(game, 12, 200, 1.8f, 1000.0, 2,
					MonsterType.Peon));
			this.waves.add(new Wave(game, 1, 0x3e8, 1f, 1000.0, 0x19,
					MonsterType.Chieftain));
			break;

		default:
			throw new RuntimeException("Unknown difficulty in wavemanager!");
		}
		this.waves.add(new Wave(game, 0x12, 200, 1f, 1000.0, 2,
				MonsterType.Peasant));
		this.waves.add(new Wave(game, 12, 260, 1.8f, 600.0, 2,
				MonsterType.Peasant));
		this.waves
				.add(new Wave(game, 3, 500, 1f, 2000.0, 4, MonsterType.Doctor));
		this.waves.add(new Wave(game, 8, 0x9b, 1f, 1000.0, 3,
				MonsterType.Chicken));
		this.waves.add(new Wave(game, 12, 220, 1f, 300.0, 2,
				MonsterType.Peasant));
		this.waves.add(new Wave(game, 12, 260, 1f, 1000.0, 3,
				MonsterType.Berserker));
		this.waves
				.add(new Wave(game, 10, 280, 2f, 1000.0, 3, MonsterType.Peon));
		this.waves.add(new Wave(game, 10, 170, 1f, 600.0, 3,
				MonsterType.Chicken));
		this.waves
				.add(new Wave(game, 10, 360, 1.8f, 200.0, 3, MonsterType.Peon));
		this.waves.add(new Wave(game, 10, 500, 1f, 1000.0, 3,
				MonsterType.Berserker));
		this.waves.add(new Wave(game, 1, 0xdac, 1f, 1000.0, 30,
				MonsterType.Chieftain));
		this.waves.add(new Wave(game, 10, 310, 1f, 1000.0, 3,
				MonsterType.Chicken));
		this.waves.add(new Wave(game, 10, 500, 1f, 1000.0, 3,
				MonsterType.Peasant));
		this.waves
				.add(new Wave(game, 5, 900, 1f, 2000.0, 6, MonsterType.Doctor));
		this.waves.add(new Wave(game, 20, 550, 1f, 1000.0, 2,
				MonsterType.Berserker));
		this.waves.add(new Wave(game, 10, 500, 1f, 1000.0, 3,
				MonsterType.Chicken));
		this.waves
				.add(new Wave(game, 10, 700, 1.8f, 400.0, 3, MonsterType.Peon));
		this.waves.add(new Wave(game, 12, 800, 1f, 5000.0, 3,
				MonsterType.Peasant));
		if (game.getGameplayScreen().getLevel() == 2) {
			this.waves.add(new Wave(game, 10, 700, 1f, 1000.0, 3,
					MonsterType.Berserker));
			this.waves.add(new Wave(game, 2, 0xc80, 1f, 1000.0, 30,
					MonsterType.Chieftain));
			this.waves.add(new Wave(game, 10, 420, 1f, 1000.0, 3,
					MonsterType.Chicken));
			this.waves.add(new Wave(game, 10, 800, 1f, 1000.0, 3,
					MonsterType.Peasant));
			this.waves.add(new Wave(game, 10, 950, 2f, 1000.0, 3,
					MonsterType.Peon));
			this.waves.add(new Wave(game, 10, 0x44c, 1f, 500.0, 3,
					MonsterType.Berserker));
			this.waves.add(new Wave(game, 5, 0xce4, 1f, 4000.0, 30,
					MonsterType.Chieftain));
		} else {
			this.waves.add(new Wave(game, 10, 900, 1f, 1000.0, 3,
					MonsterType.Berserker));
			this.waves.add(new Wave(game, 2, 0xfa0, 1f, 1000.0, 30,
					MonsterType.Chieftain));
			this.waves.add(new Wave(game, 10, 450, 1f, 1000.0, 3,
					MonsterType.Chicken));
			this.waves.add(new Wave(game, 10, 0x3e8, 1f, 1000.0, 3,
					MonsterType.Peasant));
			this.waves.add(new Wave(game, 10, 0x41a, 2f, 1000.0, 3,
					MonsterType.Peon));
			this.waves.add(new Wave(game, 10, 0x4b0, 1f, 500.0, 3,
					MonsterType.Berserker));
			this.waves.add(new Wave(game, 5, 0xfa0, 1f, 4000.0, 30,
					MonsterType.Chieftain));
		}

	}

	public final void AddMonsterToCurrentWave(Monster monster) {
		this.waves.get(this.waveNumber - 1).AddMonster(monster);
	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		batch.draw(this.texture, this.drawPosition, LColor.white);
		batch.drawString(this.font, LanguageResources.getWave() + " "
				+ this.waveNumber + " " + LanguageResources.getof() + " "
				+ this.waves.size(), this.drawPosition.x + 17f,
				this.drawPosition.y + 2f, LColor.white);
		if (!this.isLastWave) {
			Utils.DrawStringAlignRight(batch, this.font,
					LanguageResources.getNext(), this.drawPosition.x + 136f,
					this.drawPosition.y + 7f, LColor.white);
			int num2 = ((int) Math.ceil(this.timeUntilNextWave)) / 0x3e8;
			batch.drawString(this.font, LanguageResources.getNextWave() + " "
					+ num2, this.drawPosition.x + 17f,
					this.drawPosition.y + 18f, LColor.white);
		}
	}

	public final java.util.ArrayList<Monster> GetAllActiveMonsters() {
		java.util.ArrayList<Monster> list = new java.util.ArrayList<Monster>();
		for (Wave wave : this.game.getGameplayScreen().getWaveManager().activeWaves) {
			list.addAll(wave.getMonsters());
		}
		return list;
	}

	public final Monster GetSelectedMonster(RectBox touchRect) {
		for (Wave wave : this.activeWaves) {
			Monster selectedMonster = wave.GetSelectedMonster(touchRect);
			if (selectedMonster != null) {
				return selectedMonster;
			}
		}
		return null;
	}

	@Override
	protected void loadContent() {
		this.texture = LTextures.loadTexture("assets/wave_x_of_y.png");
		this.font = LFont.getFont(10);
		super.loadContent();
	}

	public final void Remove() {
		for (int i = 0; i < this.activeWaves.size(); i++) {
			this.activeWaves.get(i).Remove();
		}
		if (this.nextWaveMonsterType != null) {
			super.getGame().Components().remove(this.nextWaveMonsterType);
		}
		super.getGame().Components().remove(this);
	}

	public final void RemoveActiveWave(Wave wave) {
		this.activeWaves.remove(wave);
	}

	@Override
	public void update(GameTime gameTime) {
		if (GameplayScreen.getGameState() == GameState.Started) {
			this.timeUntilNextWave -= gameTime.getMilliseconds();
			if (this.timeUntilNextWave < 0.0) {
				if (this.waveNumber < this.waves.size()) {
					this.activeWaves.add(this.waves.get(this.waveNumber));
					this.waves.get(this.waveNumber).setWaveState(
							WaveState.Started);
					if (this.nextWaveMonsterType != null) {
						this.game.Components().remove(this.nextWaveMonsterType);
					}
					this.timeUntilNextWave = 20000.0;
					if ((this.waveNumber + 1) < this.waves.size()) {
						this.nextWaveMonsterType = AnimatedSpriteMonster
								.GetSmallAnimatedSpriteMonster(this.game,
										this.waves.get(this.waveNumber + 1)
												.getMonsterType());
						this.nextWaveMonsterType.setAnimationSpeedRatio(3);
						this.nextWaveMonsterType.setObeyGameOpacity(false);
						this.game.Components().add(this.nextWaveMonsterType);
					} else {
						this.isLastWave = true;
					}
					this.waveNumber++;
				} else {
					boolean flag = true;
					for (Wave wave : this.activeWaves) {
						// ?testing
						if (wave.getMonsters().size() > 0) {
							flag = false;
							break;
						}
					}
					if (flag) {
						this.game.getGameplayScreen().Win();
					}
				}
			}
		}
		super.update(gameTime);
	}

	public final int getRemainingWaves() {
		return this.waves.size();
	}
}