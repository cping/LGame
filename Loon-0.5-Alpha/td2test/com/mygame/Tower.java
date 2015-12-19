package com.mygame;

import loon.LTexture;
import loon.LTextures;
import loon.action.sprite.SpriteBatch;
import loon.action.sprite.painting.DrawableGameComponent;
import loon.action.sprite.painting.IGameComponent;
import loon.canvas.LColor;
import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.utils.timer.GameTime;

public class Tower extends DrawableGameComponent implements IGameComponent {

	private LTexture bashTexture;
	private int currentUpgradeLevel;
	private float elapsedTime;
	private MainGame game;
	private boolean isSelected;
	private boolean isUpgrading;
	private LTexture levelTexture;
	private LurWeapon lurWeapon;
	private Missile missile;
	private boolean obeyGameOpacity;
	private LTexture occupiedTexture;
	private LTexture occupiedTextureGreen;
	private Vector2f occupiedTexturePosition;
	private LTexture occupiedTextureRed;
	private Vector2f position;

	private LTexture texture;
	private String textureFile;
	private TowerMan towerMan;
	private ProgressBar upgradeProgressBar;
	private double upgradeTimeLeft;

	public Tower(MainGame game, TowerType towerType, Capability capability,
			String textureFile) {
		super(game);
		this.obeyGameOpacity = true;
		this.setTowerType(towerType);
		this.setCapability(capability);
		this.game = game;
		this.textureFile = textureFile;
		this.setPlaced(false);
		this.elapsedTime = 9999f;
		this.currentUpgradeLevel = 0;
		this.isSelected = false;
		this.isUpgrading = false;
		this.setPosition(game.getGameplayScreen().getLastTouchPosition());
		this.SetDrawOrder();
	}

	public final boolean CanPlace() {
		if (((this.getGridX() < 2) || (this.getGridX() >= 15))
				|| ((this.getGridY() < 0) || (this.getGridY() >= 0x12))) {
			return false;
		}
		if (this.game.getGameplayScreen().IsOccupied(this.getGridX(),
				this.getGridY(), 2)) {
			return false;
		}
		for (Vector2f point : this.game.getGameplayScreen().getLevelSettings()
				.getTowerBlockingGridCells()) {
			if ((((this.getGridX() != point.x) || (this.getGridY() != point.y)) && (((this
					.getGridX() + 1) != point.x) || (this.getGridY() != point.y)))
					&& (((this.getGridX() != point.x) || ((this.getGridY() + 1) != point.y)) && (((this
							.getGridX() + 1) != point.x) || ((this.getGridY() + 1) != point.y)))) {
				continue;
			}
			return false;
		}
		return true;
	}

	public final boolean CanUpgrade() {
		if (!this.isUpgrading && this.IsMoreUpgradeLevelsAvailable()) {
			if (this.game.getGameplayScreen().getCash().getCurrentCash() >= this
					.GetUpgradeCost()) {
				return true;
			}
		}
		return false;
	}

	public final RectBox CentralCollisionArea() {
		return new RectBox((int) this.getDrawPosition().x,
				(int) this.getDrawPosition().y, 0x24, 0x26);
	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		if (this.occupiedTexture != null) {
			batch.draw(this.occupiedTexture, this.occupiedTexturePosition,
					this.game.getGameplayScreen().getGameOpacity());
		}
		LColor gameOpacity = this.game.getGameplayScreen().getGameOpacity();
		if (!this.obeyGameOpacity) {
			gameOpacity = LColor.white;
		}
		batch.draw(this.texture, this.getDrawPosition(), gameOpacity);
		int num = this.isUpgrading ? (this.currentUpgradeLevel - 1)
				: this.currentUpgradeLevel;
		for (int i = 0; i < num; i++) {
			batch.draw(this.levelTexture,
					this.getDrawPosition().add((5 + (i * 10)), 35f), this.game
							.getGameplayScreen().getGameOpacity());
		}
		if (this.isSelected) {
			int range = (int) this.getRange();
			batch.draw(this.bashTexture, position.x - range,
					position.y - range, range * 2, range * 2);
		}
		super.draw(batch, gameTime);
	}

	public final int GetSellValue() {
		if (GameplayScreen.getGameState() != GameState.Started) {
			return this.getValue();
		}
		return (this.getValue() / 2);
	}

	public final Monster GetTargetMonster() {
		for (Monster monster : this.game.getGameplayScreen().getWaveManager()
				.GetAllActiveMonsters()) {
			float num = monster.getPosition().x - this.getPosition().x;
			float num2 = monster.getPosition().y - this.getPosition().y;
			float num3 = this.getRange() + monster.getRadius();
			if ((((((num * num) + (num2 * num2)) <= (num3 * num3)) && ((this
					.getCapability() != Capability.Bash) || (monster
					.getMonsterType() != MonsterType.Chicken))) && ((this
					.getCapability() != Capability.Air) || (monster
					.getMonsterType() == MonsterType.Chicken)))
					&& ((monster.getHitPoints() - monster
							.getReservedHitPoints()) > 0)) {
				return monster;
			}
		}
		return null;
	}

	public final java.util.ArrayList<Monster> GetTargetMonstersForLurWeapon() {
		java.util.ArrayList<Monster> list = new java.util.ArrayList<Monster>();
		for (Monster monster : this.game.getGameplayScreen().getWaveManager()
				.GetAllActiveMonsters()) {
			if ((monster.getMonsterType() != MonsterType.Chicken)
					&& (Utils.GetDistance(this.getPosition(),
							monster.getPosition()) < this.getRange())) {
				list.add(monster);
			}
		}
		if (list.size() <= 0) {
			return null;
		}
		return list;
	}

	public final Integer GetUpgradeCost() {
		if (this.IsMoreUpgradeLevelsAvailable()) {
			return new Integer(
					this.getTowerLevels()[this.currentUpgradeLevel + 1]
							.getCost());
		}
		return null;
	}

	public final Integer GetUpgradeDamage() {
		if (this.IsMoreUpgradeLevelsAvailable()) {
			return new Integer(
					this.getTowerLevels()[this.currentUpgradeLevel + 1]
							.getDamage() - this.getDamage());
		}
		return null;
	}

	public final Integer GetUpgradeRange() {
		if (this.IsMoreUpgradeLevelsAvailable()) {
			return new Integer(
					((int) this.getTowerLevels()[this.currentUpgradeLevel + 1]
							.getRange()) - ((int) this.getRange()));
		}
		return null;
	}

	public final boolean IsMoreUpgradeLevelsAvailable() {
		return ((this.currentUpgradeLevel + 1) < this.getTowerLevels().length);
	}

	@Override
	protected void loadContent() {
		super.loadContent();
		this.texture = LTextures.loadTexture(this.textureFile);
		this.bashTexture = LTextures.loadTexture("assets/bash.png");
		this.occupiedTextureGreen = LTextures
				.loadTexture("assets/green_square.png");
		this.occupiedTextureRed = LTextures
				.loadTexture("assets/red_square.png");
		this.levelTexture = LTextures.loadTexture("assets/star.png");
	}

	public final void Place() {
		this.setPlaced(true);
		this.occupiedTexture = null;
		this.game.getGameplayScreen().getCash().Decrease(this.getValue());
		this.SetDrawOrder();
		switch (this.getTowerType()) {
		case Axe:
			this.towerMan = new TowerManAxe(this.game, this);
			break;

		case Spear:
			this.towerMan = new TowerManSpear(this.game, this);
			break;

		case AirDefence:
			this.towerMan = new TowerManSpear(this.game, this);
			break;

		case Lur:
			this.towerMan = new TowerManLur(this.game, this);
			break;
		}
		this.game.Components().add(this.towerMan);
	}

	public final void remove() {
		if (this.towerMan != null) {
			this.game.Components().remove(this.towerMan);
		}
		if (this.upgradeProgressBar != null) {
			this.game.Components().remove(this.upgradeProgressBar);
		}
		this.game.Components().remove(this);
	}

	public final void Sell() {
		this.game.getGameplayScreen().getCash().Increase(this.GetSellValue());
		this.remove();
	}

	private void SetDrawOrder() {
		super.setDrawOrder(8 + this.getGridY());
	}

	public final void SetInitialValue() {
		this.setValue(this.getTowerLevels()[0].getCost());
	}

	public final void SetValuesFromTowerLevel(int level) {
		this.setRange(this.getTowerLevels()[level].getRange());
		this.setReleaseTime(this.getTowerLevels()[level].getReleaseTime());
		this.setReloadTime(this.getTowerLevels()[level].getReloadTime());
		this.setDamage(this.getTowerLevels()[level].getDamage());
		this.setUpgradeCost(this.getTowerLevels()[level].getCost());
		this.setUpgradeTime(this.getTowerLevels()[level].getUpgradeTime());
	}

	public final void StartedSelection() {
		this.isSelected = true;
		this.obeyGameOpacity = false;
	}

	private void StartUpgrade() {
		this.currentUpgradeLevel++;
		this.SetValuesFromTowerLevel(this.currentUpgradeLevel);
		this.game.getGameplayScreen().getCash().Decrease(this.getUpgradeCost());
		this.setValue(this.getValue() + this.getUpgradeCost());
		if (GameplayScreen.getGameState() == GameState.Started) {
			this.upgradeProgressBar = new ProgressBar(this.game, 40, false);
			this.upgradeProgressBar.setPosition(this.getDrawPosition().add(5f,
					10f));
			this.game.Components().add(this.upgradeProgressBar);
			this.isUpgrading = true;
			this.upgradeTimeLeft = this.getUpgradeTime() * 1000f;
		} else {
			this.UpgradeCompleted();
		}
	}

	public final void StoppedSelection() {
		this.isSelected = false;
		this.obeyGameOpacity = true;
	}

	@Override
	public void update(GameTime gameTime) {
		super.update(gameTime);
		this.elapsedTime += 0.03333334f;
		if (!this.getPlaced()) {
			Vector2f vector = new Vector2f(25f, -40f);
			Vector2f point = Utils.ConvertToGridPoint(this.game
					.getGameplayScreen().getLastTouchPosition().add(vector));
			this.setGridX(point.x());
			this.setGridY(point.y());
			this.setPosition(Utils.ConvertToPositionCoordinates(
					new Vector2f(this.getGridX(), this.getGridY())).add(20f,
					20f));
			this.occupiedTexturePosition = new Vector2f(
					this.getPosition().x - 20f, this.getPosition().y - 20f);
			this.occupiedTexture = this.CanPlace() ? this.occupiedTextureGreen
					: this.occupiedTextureRed;
			this.SetDrawOrder();
		} else if (GameplayScreen.getGameState() == GameState.Started) {
			if ((this.missile != null) && this.missile.getHasHitTarget()) {
				this.missile = null;
			}
			if ((this.lurWeapon != null) && this.lurWeapon.getHasHitTarget()) {
				this.lurWeapon = null;
			}
			if (this.isUpgrading) {
				this.upgradeTimeLeft -= gameTime.getMilliseconds();
				this.upgradeProgressBar
						.setCurrentPercent((int) (((this.getUpgradeTime() - (this.upgradeTimeLeft / 1000.0)) / ((double) this
								.getUpgradeTime())) * 100.0));
				if (this.upgradeTimeLeft < 0.0) {
					this.UpgradeCompleted();
				}
			}
			if ((this.elapsedTime > this.getReloadTime()) && !this.isUpgrading) {
				if (this.getTowerType() == TowerType.Lur) {
					java.util.ArrayList<Monster> targetMonstersForLurWeapon = this
							.GetTargetMonstersForLurWeapon();
					if ((targetMonstersForLurWeapon != null)
							&& (this.lurWeapon == null)) {
						this.lurWeapon = new LurWeapon(this.game, this,
								targetMonstersForLurWeapon);
						super.getGame().Components().add(this.lurWeapon);
						this.towerMan.PlayAnimation();
						this.elapsedTime = 0f;
					}
				} else if (this.missile == null) {
					Monster targetMonster = this.GetTargetMonster();
					if (targetMonster != null) {
						switch (this.getTowerType()) {
						case Axe:
							this.missile = new MissileAxe(this.game,
									targetMonster, this);
							break;

						case Spear:
							this.missile = new MissileSpear(this.game,
									targetMonster, this);
							break;

						case AirDefence:
							this.missile = new MissileSpear(this.game,
									targetMonster, this);
							break;
						default:
							break;
						}
						super.getGame().Components().add(this.missile);
						this.towerMan.UpdateThrowDirection(this.missile
								.getDirection());
						this.towerMan.PlayAnimation();
						this.elapsedTime = 0f;
					}
				}
			}
		}
	}

	public final void Upgrade() {
		if (this.CanUpgrade()) {
			this.StartUpgrade();
		}
	}

	private void UpgradeCompleted() {
		this.game.Components().remove(this.upgradeProgressBar);
		this.upgradeProgressBar = null;
		this.game.getGameplayScreen().UpdateUpgradeButtonState();
		this.isUpgrading = false;
	}

	private Capability privateCapability;

	public final Capability getCapability() {
		return privateCapability;
	}

	public final void setCapability(Capability value) {
		privateCapability = value;
	}

	private int privateDamage;

	public final int getDamage() {
		return privateDamage;
	}

	public final void setDamage(int value) {
		privateDamage = value;
	}

	private Vector2f privateDrawPosition;

	public final Vector2f getDrawPosition() {
		return privateDrawPosition;
	}

	public final void setDrawPosition(Vector2f value) {
		privateDrawPosition = value;
	}

	private int privateGridX;

	public final int getGridX() {
		return privateGridX;
	}

	public final void setGridX(int value) {
		privateGridX = value;
	}

	private int privateGridY;

	public final int getGridY() {
		return privateGridY;
	}

	public final void setGridY(int value) {
		privateGridY = value;
	}

	private boolean privatePlaced;

	public final boolean getPlaced() {
		return privatePlaced;
	}

	public final void setPlaced(boolean value) {
		privatePlaced = value;
	}

	public final Vector2f getPosition() {
		return this.position;
	}

	public final void setPosition(Vector2f value) {
		this.position = value;
		this.setDrawPosition(new Vector2f(value.x - 26f, value.y - 38f));
	}

	private float privateRange;

	public final float getRange() {
		return privateRange;
	}

	public final void setRange(float value) {
		privateRange = value;
	}

	private float privateReleaseTime;

	public final float getReleaseTime() {
		return privateReleaseTime;
	}

	public final void setReleaseTime(float value) {
		privateReleaseTime = value;
	}

	private float privateReloadTime;

	public final float getReloadTime() {
		return privateReloadTime;
	}

	public final void setReloadTime(float value) {
		privateReloadTime = value;
	}

	private TowerLevel[] privateTowerLevels;

	protected final TowerLevel[] getTowerLevels() {
		return privateTowerLevels;
	}

	protected final void setTowerLevels(TowerLevel[] value) {
		privateTowerLevels = value;
	}

	private TowerType privateTowerType;

	public final TowerType getTowerType() {
		return privateTowerType;
	}

	public final void setTowerType(TowerType value) {
		privateTowerType = value;
	}

	private int privateUpgradeCost;

	public final int getUpgradeCost() {
		return privateUpgradeCost;
	}

	public final void setUpgradeCost(int value) {
		privateUpgradeCost = value;
	}

	private float privateUpgradeTime;

	public final float getUpgradeTime() {
		return privateUpgradeTime;
	}

	public final void setUpgradeTime(float value) {
		privateUpgradeTime = value;
	}

	private int privateValue;

	public final int getValue() {
		return privateValue;
	}

	public final void setValue(int value) {
		privateValue = value;
	}
}