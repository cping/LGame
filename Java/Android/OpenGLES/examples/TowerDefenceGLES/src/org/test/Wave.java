package org.test;

import loon.action.sprite.painting.DrawableGameComponent;
import loon.action.sprite.painting.IGameComponent;
import loon.core.geom.RectBox;
import loon.core.timer.GameTime;

public class Wave extends DrawableGameComponent implements IGameComponent {

	private MainGame game;
	private int numberOfMonstersAdded;
	private int numMonsters;
	private float speed;
	private double spread;
	private int startHitPoints;
	private double timeUntilNextMonsterAdd;
	private int value;

	public Wave(MainGame game, int num_monsters, int startHitPoints,
			float speed, double spread, int value, MonsterType monsterType) {
		super(game);
		this.game = game;
		this.privateMonsters = new java.util.ArrayList<Monster>(10);
		this.numMonsters = num_monsters;
		this.startHitPoints = startHitPoints;
		this.speed = speed;
		this.spread = spread;
		this.value = value;
		this.setMonsterType(monsterType);
		this.setWaveState(WaveState.NotStarted);
		this.timeUntilNextMonsterAdd = 0.0;
		game.Components().add(this);
	}

	public final void AddMonster(Monster monster) {
		this.getMonsters().add(monster);
	}

	public final Monster GetSelectedMonster(RectBox touchRect) {
		for (Monster monster : this.getMonsters()) {
			if (monster.CentralCollisionArea().intersects(touchRect)) {
				return monster;
			}
		}
		return null;
	}

	public final void Remove() {
		this.RemoveAllMonsters();
	}

	private void RemoveAllMonsters() {
		for (int i = 0; i < this.getMonsters().size(); i++) {
			this.getMonsters().get(i).Remove();
		}
	}

	public final void RemoveMonster(Monster monster) {
		this.getMonsters().remove(monster);
		if ((this.getMonsters().size() == 0)
				&& (this.numberOfMonstersAdded == this.numMonsters)) {
			this.game.getGameplayScreen().getWaveManager()
					.RemoveActiveWave(this);
		}
	}

	@Override
	public void update(GameTime gameTime) {
		if ((GameplayScreen.getGameState() == GameState.Started)
				&& (this.getWaveState() == WaveState.Started)) {
			this.timeUntilNextMonsterAdd -= gameTime.getMilliseconds();
			if ((this.timeUntilNextMonsterAdd < 0.0)
					&& (this.numberOfMonstersAdded < this.numMonsters)) {
				Monster monster;
				switch (this.getMonsterType()) {
				case Peasant:
					monster = new MonsterPeasant(this.game, this, this.speed,
							this.startHitPoints, this.value);
					break;

				case Peon:
					monster = new MonsterPeon(this.game, this, this.speed,
							this.startHitPoints, this.value);
					break;

				case Berserker:
					monster = new MonsterBerserker(this.game, this, this.speed,
							this.startHitPoints, this.value);
					break;

				case Chicken:
					monster = new MonsterChicken(this.game, this, this.speed,
							this.startHitPoints, this.value);
					break;

				case Doctor:
					monster = new MonsterDoctor(this.game, this, this.speed,
							this.startHitPoints, this.value);
					break;

				case Chieftain:
					monster = new MonsterChieftain(this.game, this, this.speed,
							this.startHitPoints, this.value);
					break;

				default:
					monster = null;
					break;
				}
				this.AddMonster(monster);
				this.timeUntilNextMonsterAdd = this.spread;
				this.numberOfMonstersAdded++;
			}
		}
		super.update(gameTime);
	}

	private java.util.ArrayList<Monster> privateMonsters;

	public final java.util.ArrayList<Monster> getMonsters() {
		return privateMonsters;
	}

	public final void setMonsters(java.util.ArrayList<Monster> value) {
		privateMonsters = value;
	}

	private MonsterType privateMonsterType;

	public final MonsterType getMonsterType() {
		return privateMonsterType;
	}

	public final void setMonsterType(MonsterType value) {
		privateMonsterType = value;
	}

	private WaveState privateWaveState;

	public final WaveState getWaveState() {
		return privateWaveState;
	}

	public final void setWaveState(WaveState value) {
		privateWaveState = value;
	}
}