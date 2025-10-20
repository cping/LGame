package org.test.towerdefense;

import java.util.HashMap;
import java.util.LinkedList;

import loon.LTexture;
import loon.LTextures;
import loon.action.sprite.SpriteBatch;
import loon.action.sprite.painting.GameComponentCollection;
import loon.canvas.LColor;
import loon.events.LTouchCollection;
import loon.events.LTouchLocation;
import loon.events.LTouchLocationState;
import loon.events.SysTouch;
import loon.font.LFont;
import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.utils.LIterator;
import loon.utils.TArray;
import loon.utils.reply.ObjRef;
import loon.utils.timer.GameTime;

public class GameplayScreen extends GameScreen {

	private LTexture currentBackground;
	private LFont fontSize26Extra;
	private MainGame game;
	private LTexture gameBackground;
	private LTexture gameBackgroundWithGrid;
	private GameEndedState gameEndedState;
	private GamePausedScreen gamePausedScreen;
	private boolean isPlacing;
	private float loseOrWinScreenDelay = 2000f;
	private MonsterToolbar monsterToolbar;
	private java.util.ArrayList<Tower> placedTowers = new java.util.ArrayList<Tower>();
	private Monster selectedMonster;
	private Tower selectedTower;
	private ShowMenuButton showMenuButton;
	private StartGameButton startGameButton;
	private PathNode[][] tempDirs = new PathNode[0x12][0x13];
	private Tower tower;
	private java.util.ArrayList<TowerButton> towerButtons;
	private java.util.ArrayList<Tower> towers = new java.util.ArrayList<Tower>();
	private TowerToolbar towerToolbar;

	public GameplayScreen(MainGame game, Difficulty difficulty, int level) {
		this.setLevel(level);
		this.setLevelSettings(new LevelSettings(game, this.getLevel()));
		super.setIsSerializable(true);
		this.game = game;
		this.game.setGameplayScreen(this);
		this.setDifficulty(difficulty);
		setGameState(GameState.PlacingInitialTowers);
		this.startGameButton = new StartGameButton(game);
		game.Components().add(this.startGameButton);
		this.setOccupiedGrid(new boolean[0x12][0x13]);
		this.showMenuButton = new ShowMenuButton(game);
		game.Components().add(this.showMenuButton);
		this.towerButtons = new java.util.ArrayList<TowerButton>();
		this.towerButtons.add(new TowerButtonAxe(game));
		this.towerButtons.add(new TowerButtonSpear(game));
		this.towerButtons.add(new TowerButtonAirDefence(game));
		this.towerButtons.add(new TowerButtonLur(game));
		for (TowerButton button : this.towerButtons) {
			game.Components().add(button);
		}
		this.setRemainingLives(new RemainingLives(game, 20));
		game.Components().add(this.getRemainingLives());
		this.setCash(new Cash(game, 50));
		this.setGameOpacity(LColor.white);
		this.ClearGrid();
		game.Components().add(this.getCash());
		this.setDirs(new PathNode[0x12][0x13]);
		this.MikkelsPathFinding(false);
		if (this.getLevelSettings().getInfoSpriteWithText() != null) {
			this.getLevelSettings().getInfoSpriteWithText().setDrawOrder(60);
			game.Components().add(this.getLevelSettings().getInfoSpriteWithText());
		}
	}

	public final void AvailableCashChanged() {
		this.UpdateTowerButtons();
		this.game.getGameplayScreen().UpdateUpgradeButtonState();
	}

	private void ClearGrid() {
		for (int i = 0; i < this.getOccupiedGrid().length; i++) {
			for (int j = 0; j < this.getOccupiedGrid()[0].length; j++) {
				if (i <= 1) {
					if (((j == (this.getLevelSettings().getStartPoint().y() - 1))
							|| (j == this.getLevelSettings().getStartPoint().y()))
							|| (j == (this.getLevelSettings().getStartPoint().y() + 1))) {
						this.getOccupiedGrid()[i][j] = false;
					} else {
						this.getOccupiedGrid()[i][j] = true;
					}
				} else if ((i == this.getLevelSettings().getEndPoint().x())
						|| (i == (this.getLevelSettings().getEndPoint().x() - 1))) {
					if (((j == (this.getLevelSettings().getEndPoint().y() - 1))
							|| (j == this.getLevelSettings().getEndPoint().y()))
							|| (j == (this.getLevelSettings().getEndPoint().y() + 1))) {
						this.getOccupiedGrid()[i][j] = false;
					} else {
						this.getOccupiedGrid()[i][j] = true;
					}
				} else {
					this.getOccupiedGrid()[i][j] = false;
				}
			}
		}
		for (Vector2f point : this.getLevelSettings().getLevelSpecificOccupiedGridCells()) {
			this.getOccupiedGrid()[point.x()][point.y()] = true;
		}
	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		batch.draw(this.currentBackground, 0f, 0f, this.getGameOpacity());
		super.draw(batch, gameTime);
	}

	public final void GameIsActivated() {
		super.setScreenState(ScreenState.Active);
	}

	public final void GameIsDeactivated() {
		super.setScreenState(ScreenState.Hidden);
	}

	public final void GamePaused(boolean addGamePausedScreen) {
		if (getGameState() == GameState.Started) {
			setGameState(GameState.Paused);
		}
		this.ResetSelectedMonsterOrTower();
		this.setGameOpacity(this.getGameOpacityWhenPaused());
		if (addGamePausedScreen && (this.gamePausedScreen == null)) {
			this.gamePausedScreen = new GamePausedScreen(this.game, ScreenType.GameplayScreen);
			super.getScreenManager().AddScreen(this.gamePausedScreen);
		}
		this.HideButtons();
	}

	public final void GameResumed() {
		this.game.getGameplayScreen().setGameOpacity(LColor.white);
		if (getGameState() != GameState.PlacingInitialTowers) {
			setGameState(GameState.Started);
		}
		if (this.startGameButton != null) {
			this.startGameButton.Show();
		}
		this.gamePausedScreen = null;
		this.ShowButtons();
	}

	public final Vector2f GetNextGridPoint(Vector2f sourceGridPoint) {
		PathNode node = this.getDirs()[sourceGridPoint.x()][sourceGridPoint.y()];
		if (node == null) {
			return new Vector2f();
		}
		return new Vector2f(sourceGridPoint.x() + node.x(), sourceGridPoint.y() + node.y());
	}

	private Tower GetSelectedTower(RectBox touchRect) {
		for (Tower tower : this.towers) {
			if (tower.CentralCollisionArea().intersects(touchRect)) {
				return tower;
			}
		}
		return null;
	}

	private void HideButtons() {
		this.showMenuButton.Hide();
		for (TowerButton button : this.towerButtons) {
			button.Hide();
		}
	}

	public final boolean IsOccupied(int gridX, int gridY, int width) {
		if (width == 1) {
			return this.getOccupiedGrid()[gridX][gridY];
		}
		if (width != 2) {
			throw new RuntimeException("2");
		}
		if ((!this.getOccupiedGrid()[gridX][gridY] && !this.getOccupiedGrid()[gridX + 1][gridY])
				&& !this.getOccupiedGrid()[gridX][gridY + 1]) {
			return this.getOccupiedGrid()[gridX + 1][gridY + 1];
		}
		return true;
	}

	@Override
	public void LoadContent() {
		super.LoadContent();
		this.gameBackground = LTextures.loadTexture(this.getLevelSettings().getBackgroundTextureFile());
		this.gameBackgroundWithGrid = LTextures.loadTexture(this.getLevelSettings().getBackgroundWithGridTextureFile());
		this.fontSize26Extra = LFont.getFont(26);
		this.currentBackground = this.gameBackground;
		super.LoadContent();
	}

	public final void Lose() {
		this.gameEndedState = GameEndedState.Lose;
		this.GamePaused(false);
		setGameState(GameState.Ended);
	}

	public final boolean MikkelsPathFinding(boolean checkIfMonsterIsBlocking) {
		boolean flag = false;
		ObjRef<Boolean> tempRef_flag = new ObjRef<Boolean>(flag);
		boolean tempVar = this.MikkelsPathFinding(checkIfMonsterIsBlocking, tempRef_flag);
		flag = tempRef_flag.get();
		return tempVar;
	}

	public final boolean MikkelsPathFinding(boolean checkIfMonsterIsBlocking,
			ObjRef<Boolean> failedBecauseMonsterIsBlocking) {
		boolean flag = false;
		failedBecauseMonsterIsBlocking.set(false);
		java.util.ArrayList<PathNode> list = new java.util.ArrayList<PathNode>();
		for (int i = 0; i < 0x12; i++) {
			for (int k = 0; k < 0x13; k++) {
				this.tempDirs[i][k] = null;
			}
		}
		list.add(new PathNode(this.getLevelSettings().getEndPoint().x(), this.getLevelSettings().getEndPoint().y(), 0));
		list.add(new PathNode(this.getLevelSettings().getEndPoint().x(), this.getLevelSettings().getEndPoint().y() + 1,
				0));
		list.add(new PathNode(this.getLevelSettings().getEndPoint().x(), this.getLevelSettings().getEndPoint().y() - 1,
				0));
		while (list.size() > 0) {
			PathNode item = list.get(0);
			if ((item.x() == this.getLevelSettings().getStartPoint().x())
					&& (item.y() == this.getLevelSettings().getStartPoint().y())) {
				flag = true;
			}
			list.remove(item);
			int cost = item.getCost();
			boolean flag2 = this.VisitNode(item, 0, 1, list, cost + 2);
			boolean flag3 = this.VisitNode(item, 0, -1, list, cost + 2);
			boolean flag4 = this.VisitNode(item, -1, 0, list, cost + 2);
			boolean flag5 = this.VisitNode(item, 1, 0, list, cost + 2);
			if (flag2 && flag4) {
				this.VisitNode(item, -1, 1, list, cost + 3);
			}
			if (flag2 && flag5) {
				this.VisitNode(item, 1, 1, list, cost + 3);
			}
			if (flag3 && flag4) {
				this.VisitNode(item, -1, -1, list, cost + 3);
			}
			if (flag3 && flag5) {
				this.VisitNode(item, 1, -1, list, cost + 3);
			}
		}
		if (!flag) {
			return false;
		}
		if (checkIfMonsterIsBlocking) {
			for (Monster monster : this.getWaveManager().GetAllActiveMonsters()) {
				if (monster.getMonsterType() != MonsterType.Chicken) {
					if (this.tempDirs[monster.getGridPosition().x()][monster.getGridPosition().y()] == null) {
						failedBecauseMonsterIsBlocking.set(true);
						return false;
					}
					Vector2f nextGridPoint = this.GetNextGridPoint(monster.getGridPosition());
					if (this.tempDirs[nextGridPoint.x()][nextGridPoint.y()] == null) {
						failedBecauseMonsterIsBlocking.set(true);
						return false;
					}
				}
			}
		}
		for (int j = 0; j < 0x12; j++) {
			for (int m = 0; m < 0x13; m++) {
				this.getDirs()[j][m] = this.tempDirs[j][m];
				this.tempDirs[j][m] = null;
			}
		}
		return true;
	}

	public final void MonsterDied(Monster monster) {
		this.ResetSelectedMonsterIfSelected(monster);
	}

	public final void MonsterSurvived(Monster monster) {
		this.ResetSelectedMonsterIfSelected(monster);
	}

	public final void RemoveAllGameComponents() {
		for (int i = 0; i < this.towerButtons.size(); i++) {
			this.game.Components().remove(this.towerButtons.get(i));
		}
		for (int j = 0; j < this.towers.size(); j++) {
			this.towers.get(j).remove();
		}
		if (this.getRemainingLives() != null) {
			this.game.Components().remove(this.getRemainingLives());
		}
		if (this.getCash() != null) {
			this.game.Components().remove(this.getCash());
		}
		try {
			this.game.Components().remove(this.startGameButton);
		} catch (RuntimeException e) {
		}
		if (this.getWaveManager() != null) {
			this.getWaveManager().Remove();
		}
		if (this.showMenuButton != null) {
			this.game.Components().remove(this.showMenuButton);
		}
		this.RemoveTowerToolbarIfNotNull();
	}

	private void RemoveTower(Tower tower) {
		this.towers.remove(tower);
		this.placedTowers.remove(tower);
		tower.remove();
		tower = null;
	}

	private void RemoveTowerToolbarIfNotNull() {
		if (this.towerToolbar != null) {
			this.towerToolbar.Remove();
			this.towerToolbar = null;
		}
	}

	private void ResetSelectedMonsterIfSelected(Monster monster) {
		if (this.selectedMonster == monster) {
			this.ResetSelectedMonsterOrTower();
		}
	}

	private void ResetSelectedMonsterOrTower() {
		this.setGameOpacity(LColor.white);
		if ((this.selectedMonster != null) || (this.selectedTower != null)) {
			this.ShowButtons();
		}
		if (this.selectedMonster != null) {
			this.selectedMonster.StoppedSelection();
			this.selectedMonster = null;
		}
		if (this.monsterToolbar != null) {
			this.monsterToolbar.Remove();
			this.monsterToolbar = null;
		}
		this.RemoveTowerToolbarIfNotNull();
		if (this.selectedTower != null) {
			this.selectedTower.StoppedSelection();
			this.selectedTower = null;
		}
	}

	private void SellTower(Tower tower) {
		tower.Sell();
		this.SetOccupiedGridValuesForTower(tower.getGridX(), tower.getGridY(), false);
		this.placedTowers.remove(tower);
		this.towers.remove(tower);
		this.MikkelsPathFinding(false);
	}

	private void SetOccupiedGridValuesForTower(int x, int y, boolean isOccupied) {
		this.getOccupiedGrid()[x][y] = isOccupied;
		this.getOccupiedGrid()[x + 1][y] = isOccupied;
		this.getOccupiedGrid()[x][y + 1] = isOccupied;
		this.getOccupiedGrid()[x + 1][y + 1] = isOccupied;
	}

	private void ShowButtons() {
		this.showMenuButton.Show();
		for (TowerButton button : this.towerButtons) {
			button.Show();
		}
	}

	public final void StartedPlacing(TowerButton towerButton) {
		if (!this.isPlacing) {
			this.currentBackground = this.gameBackgroundWithGrid;
			switch (towerButton.getTowerType()) {
			case Axe:
				this.tower = new TowerAxe(this.game);
				break;

			case Spear:
				this.tower = new TowerSpear(this.game);
				break;

			case AirDefence:
				this.tower = new TowerAirDefence(this.game);
				break;

			case Lur:
				this.tower = new TowerLur(this.game);
				break;
			}
			if (towerButton.getIsActive()) {
				this.towers.add(this.tower);
				this.game.Components().add(this.tower);
				this.isPlacing = true;
			} else {
				this.StoppedPlacing();
			}
		}
	}

	public final void StartGame() {
		this.setWaveManager(new WaveManager(this.game, this.getDifficulty()));
		setGameState(GameState.Started);
	}

	public final void StoppedPlacing() {
		this.currentBackground = this.gameBackground;
		this.isPlacing = false;
		if (this.tower != null) {
			if (this.tower.CanPlace()) {
				boolean flag2 = false;
				this.SetOccupiedGridValuesForTower(this.tower.getGridX(), this.tower.getGridY(), true);
				boolean checkIfMonsterIsBlocking = getGameState() != GameState.PlacingInitialTowers;
				ObjRef<Boolean> tempRef_flag2 = new ObjRef<Boolean>(flag2);
				boolean tempVar = !this.MikkelsPathFinding(checkIfMonsterIsBlocking, tempRef_flag2);
				flag2 = tempRef_flag2.get();
				if (tempVar) {
					if (!flag2) {
						HashMap<Vector2f, String> textAndRelativePosition = new HashMap<Vector2f, String>();
						textAndRelativePosition.put(new Vector2f(0f, 0f),
								LanguageResources.getBlocking().toUpperCase());
						SpriteWithText item = new SpriteWithText(this.game, "assets/blocking.png", 0x7d0,
								new Vector2f(164f - (this.fontSize26Extra
										.stringWidth(LanguageResources.getBlocking().toUpperCase()) / 2f), 80f),
								textAndRelativePosition, this.fontSize26Extra);
						item.setDrawOrder(100);
						this.game.Components().add(item);
					}
					this.SetOccupiedGridValuesForTower(this.tower.getGridX(), this.tower.getGridY(), false);
					this.RemoveTower(this.tower);
				} else {
					this.tower.Place();
					this.placedTowers.add(this.tower);
				}
			} else {
				this.RemoveTower(this.tower);
			}
		}
	}

	@Override
	public void UnloadContent() {
		GameComponentCollection components = super.getScreenManager().getGame().Components();
		for (int i = 0; i < components.size(); i++) {
			if ((components.get(i) != this) && (components.get(i) != super.getScreenManager())) {
				components.removeAt(i);
				i--;
			}
		}
		super.UnloadContent();
	}

	@Override
	public void Update(GameTime gameTime, boolean otherScreenHasFocus, boolean coveredByOtherScreen) {
		LTouchCollection state = SysTouch.getTouchState();

		if (state.size() > 0) {
			for (LIterator<LTouchLocation> it = state.listIterator(); it.hasNext();) {
				LTouchLocation location = it.next();
				LTouchLocation location3 = state.get(0);
				if (location.getId() == location3.getId()) {
					LTouchLocation location2 = null;
					boolean flag = true;
					ObjRef<LTouchLocation> tempRef_location2 = new ObjRef<LTouchLocation>(location2);
					location.tryGetPreviousLocation(tempRef_location2);
					location2 = tempRef_location2.get();
					flag = location2.getState() == LTouchLocationState.Pressed;
					this.setLastTouchPosition(new Vector2f((location.getPosition().x), (location.getPosition().y)));
					RectBox rectangle = new RectBox(this.getLastTouchPosition().x, this.getLastTouchPosition().y, 1, 1);
					if (((getGameState() == GameState.PlacingInitialTowers)
							&& this.startGameButton.CentralCollisionArea().intersects(rectangle)) && flag) {
						this.game.Components().remove(this.startGameButton);
						this.startGameButton = null;
						this.StartGame();
						break;
					}
					if (((getGameState() == GameState.Started) || (getGameState() == GameState.PlacingInitialTowers))
							&& ((this.showMenuButton.getIsVisible()
									&& this.showMenuButton.CentralCollisionArea().intersects(rectangle)) && flag)) {
						if (this.startGameButton != null) {
							this.startGameButton.Hide();
						}
						this.GamePaused(true);
						break;
					}
					if ((getGameState() == GameState.Started) || (getGameState() == GameState.PlacingInitialTowers)) {
						if ((this.towerToolbar != null) && (this.selectedTower != null)) {
							if (this.towerToolbar.CentralCollisionAreaSellButton().intersects(rectangle) && flag) {
								this.SellTower(this.selectedTower);
								this.ResetSelectedMonsterOrTower();
								break;
							}
							if (this.towerToolbar.CentralCollisionAreaUpgradeButton().intersects(rectangle) && flag) {
								this.selectedTower.Upgrade();
								this.ResetSelectedMonsterOrTower();
								break;
							}
						}
						for (TowerButton button : this.towerButtons) {
							if (!button.CentralCollisionArea().intersects(rectangle) || !flag) {
								continue;
							}

							this.StartedPlacing(button);

							break;
						}
						if ((this.getWaveManager() != null) && !this.isPlacing) {
							Monster selectedMonster = this.getWaveManager().GetSelectedMonster(rectangle);
							if (selectedMonster != null) {
								this.ResetSelectedMonsterOrTower();
								this.setGameOpacity(LColor.gray);
								this.selectedMonster = selectedMonster;
								this.selectedMonster.StartedSelection();
								if (this.monsterToolbar != null) {
									this.monsterToolbar.Remove();
								}
								this.RemoveTowerToolbarIfNotNull();
								this.monsterToolbar = new MonsterToolbar(this.game, this.selectedMonster);
								this.game.Components().add(this.monsterToolbar);
								this.HideButtons();
								break;
							}
						}
						Tower selectedTower = this.GetSelectedTower(rectangle);
						if (!this.isPlacing && (selectedTower != null)) {
							if (this.selectedTower != null) {
								this.selectedTower.StoppedSelection();
							}
							this.ResetSelectedMonsterOrTower();
							this.setGameOpacity(LColor.gray);
							this.selectedTower = selectedTower;
							this.selectedTower.StartedSelection();
							this.RemoveTowerToolbarIfNotNull();
							if (this.monsterToolbar != null) {
								this.monsterToolbar.Remove();
								this.monsterToolbar = null;
							}
							this.HideButtons();
							this.towerToolbar = new TowerToolbar(this.game, this.selectedTower);
							this.game.Components().add(this.towerToolbar);
							break;
						}
						this.ResetSelectedMonsterOrTower();
					}
				}
			}
		} else if (this.isPlacing
				&& ((getGameState() == GameState.Started) || (getGameState() == GameState.PlacingInitialTowers))) {
			this.StoppedPlacing();
		}
		if (getGameState() == GameState.Ended) {
			if (this.loseOrWinScreenDelay > 0.0) {
				this.loseOrWinScreenDelay -= gameTime.getMilliseconds();
			} else {
				this.RemoveAllGameComponents();
				super.getScreenManager().ExitAllScreens();
				if (this.gameEndedState == GameEndedState.Lose) {
					super.getScreenManager().AddScreen(new LoseScreen(this.game, ScreenType.GameplayScreen));
				} else if (this.gameEndedState == GameEndedState.Win) {
					super.getScreenManager().AddScreen(new WinScreen(this.game, ScreenType.GameplayScreen));
				}
			}
		}
		super.Update(gameTime, otherScreenHasFocus, coveredByOtherScreen);
	}

	private void UpdateTowerButtons() {
		for (TowerButton button : this.towerButtons) {
			button.UpdateStatus(this.getCash().getCurrentCash());
		}
	}

	public final void UpdateUpgradeButtonState() {
		if (this.towerToolbar != null) {
			this.towerToolbar.SetUpgradeButtonState();
		}
	}

	private boolean VisitNode(PathNode parent, int dx, int dy, java.util.ArrayList<PathNode> unvisited_nodes,
			int cost) {
		int gridX = parent.x() + dx;
		int gridY = parent.y() + dy;
		if (((gridX < 0) || (gridX >= 0x12)) || ((gridY < 0) || (gridY >= 0x13))) {
			return false;
		}
		if (this.IsOccupied(gridX, gridY, 1)) {
			return false;
		}
		if ((this.tempDirs[gridX][gridY] == null) || (cost < this.tempDirs[gridX][gridY].getCost())) {
			this.tempDirs[gridX][gridY] = new PathNode(-dx, -dy, cost);
			unvisited_nodes.add(new PathNode(gridX, gridY, cost));
		}
		return true;
	}

	public final void Win() {
		CompletedLevel.PersistLevelCompleted(this.game, this.game.getGameplayScreen().getDifficulty(),
				this.game.getGameplayScreen().getLevel());
		this.gameEndedState = GameEndedState.Win;
		this.GamePaused(false);
		setGameState(GameState.Ended);
	}

	private Cash privateCash;

	public final Cash getCash() {
		return privateCash;
	}

	public final void setCash(Cash value) {
		privateCash = value;
	}

	private Difficulty privateDifficulty;

	public final Difficulty getDifficulty() {
		return privateDifficulty;
	}

	public final void setDifficulty(Difficulty value) {
		privateDifficulty = value;
	}

	private PathNode[][] privateDirs;

	public final PathNode[][] getDirs() {
		return privateDirs;
	}

	public final void setDirs(PathNode[][] value) {
		privateDirs = value;
	}

	private LColor privateGameOpacity;

	public final LColor getGameOpacity() {
		return privateGameOpacity;
	}

	public final void setGameOpacity(LColor value) {
		privateGameOpacity = value;
	}

	public final LColor getGameOpacityWhenPaused() {
		return LColor.darkGray;
	}

	public final void setGameOpacityWhenPaused(LColor value) {
	}

	private static GameState privateGameState;

	public static GameState getGameState() {
		return privateGameState;
	}

	public static void setGameState(GameState value) {
		privateGameState = value;
	}

	private Vector2f privateLastTouchPosition;

	public final Vector2f getLastTouchPosition() {
		return privateLastTouchPosition;
	}

	public final void setLastTouchPosition(Vector2f value) {
		privateLastTouchPosition = value;
	}

	private int privateLevel;

	public final int getLevel() {
		return privateLevel;
	}

	public final void setLevel(int value) {
		privateLevel = value;
	}

	private LevelSettings privateLevelSettings;

	public final LevelSettings getLevelSettings() {
		return privateLevelSettings;
	}

	public final void setLevelSettings(LevelSettings value) {
		privateLevelSettings = value;
	}

	private boolean[][] privateOccupiedGrid;

	public final boolean[][] getOccupiedGrid() {
		return privateOccupiedGrid;
	}

	public final void setOccupiedGrid(boolean[][] value) {
		privateOccupiedGrid = value;
	}

	private RemainingLives privateRemainingLives;

	public final RemainingLives getRemainingLives() {
		return privateRemainingLives;
	}

	public final void setRemainingLives(RemainingLives value) {
		privateRemainingLives = value;
	}

	private WaveManager privateWaveManager;

	public final WaveManager getWaveManager() {
		return privateWaveManager;
	}

	public final void setWaveManager(WaveManager value) {
		privateWaveManager = value;
	}
}