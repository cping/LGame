package org.test;

import java.util.ArrayList;

import loon.action.sprite.SpriteBatch;
import loon.action.sprite.painting.DrawableScreen;
import loon.core.LSystem;
import loon.core.event.ActionKey;
import loon.core.geom.RectBox;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.LFont;
import loon.core.graphics.opengl.LTexture;
import loon.core.input.LInputFactory;
import loon.core.input.LInputFactory.Key;
import loon.core.input.LKey;
import loon.core.input.LTouch;
import loon.core.input.LTouchCollection;
import loon.core.input.LTouchLocation;
import loon.core.input.LTransition;
import loon.core.store.Session;
import loon.core.timer.GameTime;
import loon.utils.MathUtils;
import loon.utils.StringUtils;

public class MainGame extends DrawableScreen {

	private float acc;
	private ImageView accImage;
	private LTexture[] accImageR = new LTexture[7];
	private Label accLabel;
	private ImageView achievementButton;
	private ArrayList<AnimationView> animationViews;
	private int bestScoreClassic;
	private int bestScoreCrazy;
	private ImageView bgImage;
	private LTexture[] bgImageR = new LTexture[4];
	private Block[][] block = new Block[8][];
	private ArrayList<LTexture> blockBombAnimationImages;
	private AnimationView[][] blockBreak = new AnimationView[8][];
	private ArrayList<ArrayList<LTexture>> blockBreakAnimationImages = new ArrayList<ArrayList<LTexture>>(
			10);
	private ArrayList<LTexture> blockColorAnimationImages;
	private ImageView blockFrame;
	private ArrayList<LTexture> blockLightningAnimationImages;
	private ArrayList<LTexture> bombAnimationImages;
	private int bombCount;
	private SoundEffect bombSound;
	private SoundEffect breakSound;
	private SoundEffect buttonSound;

	private ImageView classicButton;
	private ImageView classicLeaderButton;
	private ImageView crazyButton;
	private ImageView crazyLeaderButton;
	private String currentView = "Intro";
	private float dt;
	private int endScore;
	private int finalScore;
	private ImageView gameBgImage;
	private LTexture[] gameBgImageR = new LTexture[3];
	private String gameMode = "";
	private ArrayList<ImageView> gameView;
	private SoundEffect gateSound;
	private AnimationView gear;
	private ImageView helpButton;
	private int helpCurrentPage;
	private ImageView[] helpList = new ImageView[6];
	private ImageView[] helpMenuButton = new ImageView[6];
	private ImageView helpNextButton;
	private ImageView helpPrevButton;
	private ScrollView helpView;
	private AnimationView hint;
	private AnimationView hint2;
	private ImageView hintButton;
	private boolean isIntroContentUnloaded;
	private boolean isMainTimerRunning;
	private boolean isMoving;
	private boolean isResumable;
	private boolean isSound;
	private boolean isVib;
	private LTexture[] jewelR = new LTexture[7];
	private float lastBreakTime;
	private int level;
	private ImageView levelImage;
	private LTexture[] levelImageR = new LTexture[8];
	private MovingView levelInfo;
	private Label levelInfoEndScore;
	private ImageView[] levelInfoImage;
	private Label levelInfoLabel;
	private Label levelLabel;
	private AnimationView light;
	private ArrayList<LTexture> lightningAnimationImages;
	private int lightningCount;
	private SoundEffect lightningSound;
	private ImageView listBottom;
	private ImageView[] listButton = new ImageView[30];
	private ImageView[] listTitle = new ImageView[3];
	private ImageView listTop;
	private ImageView mainBottom;
	private MovingView mainBottomView;
	private ImageView mainTop;
	private MovingView mainTopView;
	private int maxAcc;
	private int maxPlasma;
	private ImageView menuButton;
	private ImageView nextButton;
	private int plasmaCount;
	private SoundEffect plasmaSound;
	private ImageView prevButton;
	private ImageView puzzleButton;
	private int puzzleCurrentPage;
	private MovingView puzzleListBottomView;
	private ImageView puzzleListMenuButton;
	private MovingView puzzleListTopView;
	private ImageView puzzleMenuButton;
	private int puzzlePlayingLevel;
	private ImageView puzzleResultAgainButton;
	private ImageView puzzleResultBg;
	private MovingView puzzleResultInfo;
	private Label puzzleResultLevelLabel;
	private ImageView puzzleResultListButton;
	private ImageView puzzleResultNextButton;
	private ImageView puzzleRetryButton;
	private ScrollView puzzleScrollView;
	private int puzzleUnlockedLevel;
	private Random random;
	private Label resultBestLabel;
	private ImageView resultGoMainButton;
	private MovingView resultInfo;
	private ImageView resultInfoBg;
	private ImageView resultNewGameButton;
	private ImageView[] resultScoreImage = new ImageView[7];
	private ImageView resumeButton;
	private int score;
	private ImageView[] scoreImage = new ImageView[7];
	private LTexture[] scoreImageR = new LTexture[10];
	private ArrayList<String> selectedList;
	private SoundEffect selectSound;
	private ImageView soundButton;
	private int startScore;
	private float t;
	private ImageView timeAlarm;
	private ImageView[] timeImage = new ImageView[3];
	private Label timeLabel;
	private int tTotal;
	private ImageView vibButton;
	private int x;
	private int x2;
	private int y;
	private int y2;

	private Session session;

	public MainGame() {

		session = Session.load(MainGame.class.getName());

		LInputFactory.startTouchCollection();
	}

	private void checkMatch() {
		int num = 0;
		for (int i = 0; i < 8; i++) {
			for (int num3 = 0; num3 < 8; num3++) {
				int color = this.block[i][num3].color;
				if (this.block[i][num3].item == 3) {
					num++;
				}
				if (((((this.block[i][num3].state < 10) && ((i - 1) >= 0)) && ((this.block[i - 1][num3].state < 10) && (this.block[i - 1][num3].color == color))) && ((((i - 2) >= 0) && (this.block[i - 2][num3].state < 10)) && ((this.block[i - 2][num3].color == color) && ((i - 3) >= 0))))
						&& ((((this.block[i - 3][num3].state < 10) && (this.block[i - 3][num3].color == color)) && (((i - 4) >= 0) && (this.block[i - 4][num3].state < 10))) && (this.block[i - 4][num3].color == color))) {
					for (int num5 = 0; num5 < 5; num5++) {
						if ((this.block[i - num5][num3].state == 1)
								&& (this.block[i - num5][num3].item < 10)) {
							if (this.block[i - num5][num3].item == 1) {
								this.itemBomb(i - num5, num3);
							} else if (this.block[i - num5][num3].item == 2) {
								this.itemLight(i - num5, num3);
							}
							this.block[i - num5][num3].item = 13;
							this.block[i - num5][num3].backgroundAnimationImages = this.blockColorAnimationImages;
						}
					}
				}
				if (((((this.block[i][num3].state < 10) && ((num3 - 1) >= 0)) && ((this.block[i][num3 - 1].state < 10) && (this.block[i][num3 - 1].color == color))) && ((((num3 - 2) >= 0) && (this.block[i][num3 - 2].state < 10)) && ((this.block[i][num3 - 2].color == color) && ((num3 - 3) >= 0))))
						&& ((((this.block[i][num3 - 3].state < 10) && (this.block[i][num3 - 3].color == color)) && (((num3 - 4) >= 0) && (this.block[i][num3 - 4].state < 10))) && (this.block[i][num3 - 4].color == color))) {
					for (int num6 = 0; num6 < 5; num6++) {
						if ((this.block[i][num3 - num6].state == 1)
								&& (this.block[i][num3 - num6].item < 10)) {
							if (this.block[i][num3 - num6].item == 1) {
								this.itemBomb(i, num3 - num6);
							} else if (this.block[i][num3 - num6].item == 2) {
								this.itemLight(i, num3 - num6);
							}
							this.block[i][num3 - num6].item = 13;
							this.block[i][num3 - num6].backgroundAnimationImages = this.blockColorAnimationImages;
						}
					}
				}
			}
		}
		if (num > this.maxPlasma) {
			this.maxPlasma = num;
			session.set("maxPlasma", this.maxPlasma);
		}
		for (int j = 0; j < 8; j++) {
			for (int num8 = 0; num8 < 8; num8++) {
				int num9 = this.block[j][num8].color;
				if ((((this.block[j][num8].state < 10) && ((j - 1) >= 0)) && ((this.block[j - 1][num8].state < 10) && (this.block[j - 1][num8].color == num9)))
						&& ((((j - 2) >= 0) && (this.block[j - 2][num8].state < 10)) && (this.block[j - 2][num8].color == num9))) {
					for (int num10 = 0; num10 < 3; num10++) {
						if ((this.block[j - num10][num8].state == 1)
								&& (this.block[j - num10][num8].item < 10)) {
							for (int num11 = 0; num11 < 3; num11++) {
								if ((((((num8 + num11) < 8) && (this.block[j
										- num10][num8 + num11].state < 10)) && ((this.block[j
										- num10][num8 + num11].color == num9) && (((num8 + num11) - 1) >= 0))) && (((this.block[j
										- num10][(num8 + num11) - 1].state < 10) && (this.block[j
										- num10][(num8 + num11) - 1].color == num9)) && ((((num8 + num11) - 2) >= 0) && (this.block[j
										- num10][(num8 + num11) - 2].state < 10))))
										&& (this.block[j - num10][(num8 + num11) - 2].color == num9)) {
									if (this.block[j - num10][num8].item == 1) {
										this.itemBomb(j - num10, num8);
									} else if (this.block[j - num10][num8].item == 2) {
										this.itemLight(j - num10, num8);
									}
									this.block[j - num10][num8].item = 12;
									this.block[j - num10][num8].backgroundAnimationImages = this.blockLightningAnimationImages;
								}
							}
						}
					}
				}
			}
		}
		for (int k = 0; k < 8; k++) {
			for (int num13 = 0; num13 < 8; num13++) {
				int num14 = this.block[k][num13].color;
				if (((((this.block[k][num13].state < 10) && ((k - 1) >= 0)) && ((this.block[k - 1][num13].state < 10) && (this.block[k - 1][num13].color == num14))) && ((((k - 2) >= 0) && (this.block[k - 2][num13].state < 10)) && ((this.block[k - 2][num13].color == num14) && ((k - 3) >= 0))))
						&& ((this.block[k - 3][num13].state < 10) && (this.block[k - 3][num13].color == num14))) {
					for (int num15 = 0; num15 < 4; num15++) {
						if ((this.block[k - num15][num13].state == 1)
								&& (this.block[k - num15][num13].item < 10)) {
							if (this.block[k - num15][num13].item == 1) {
								this.itemBomb(k - num15, num13);
							} else if (this.block[k - num15][num13].item == 2) {
								this.itemLight(k - num15, num13);
							}
							this.block[k - num15][num13].item = 11;
							this.block[k - num15][num13].backgroundAnimationImages = this.blockBombAnimationImages;
						}
					}
				}
				if (((((this.block[k][num13].state < 10) && ((num13 - 1) >= 0)) && ((this.block[k][num13 - 1].state < 10) && (this.block[k][num13 - 1].color == num14))) && ((((num13 - 2) >= 0) && (this.block[k][num13 - 2].state < 10)) && ((this.block[k][num13 - 2].color == num14) && ((num13 - 3) >= 0))))
						&& ((this.block[k][num13 - 3].state < 10) && (this.block[k][num13 - 3].color == num14))) {
					for (int num16 = 0; num16 < 4; num16++) {
						if ((this.block[k][num13 - num16].state == 1)
								&& (this.block[k][num13 - num16].item < 10)) {
							if (this.block[k][num13 - num16].item == 1) {
								this.itemBomb(k, num13 - num16);
							} else if (this.block[k][num13 - num16].item == 2) {
								this.itemLight(k, num13 - num16);
							}
							this.block[k][num13 - num16].item = 11;
							this.block[k][num13 - num16].backgroundAnimationImages = this.blockBombAnimationImages;
						}
					}
				}
			}
		}
		for (int m = 0; m < 8; m++) {
			for (int num18 = 0; num18 < 8; num18++) {
				int num19 = this.block[m][num18].color;
				if (num19 != -1) {
					if ((((this.block[m][num18].state < 10) && ((m - 1) >= 0)) && ((this.block[m - 1][num18].state < 10) && (this.block[m - 1][num18].color == num19)))
							&& ((((m - 2) >= 0) && (this.block[m - 2][num18].state < 10)) && (this.block[m - 2][num18].color == num19))) {
						for (int num20 = 0; num20 < 3; num20++) {
							this.block[m - num20][num18].setBreakState();
							this.block[m - num20][num18].alpha = 0;
						}
					}
					if ((((this.block[m][num18].state < 10) && ((num18 - 1) >= 0)) && ((this.block[m][num18 - 1].state < 10) && (this.block[m][num18 - 1].color == num19)))
							&& ((((num18 - 2) >= 0) && (this.block[m][num18 - 2].state < 10)) && (this.block[m][num18 - 2].color == num19))) {
						for (int num21 = 0; num21 < 3; num21++) {
							this.block[m][num18 - num21].setBreakState();
							this.block[m][num18 - num21].alpha = 0;
						}
					}
				}
			}
		}
		for (int n = 0; n < 8; n++) {
			for (int num23 = 0; num23 < 8; num23++) {
				if ((this.block[n][num23].state == 3)
						&& (this.block[n][num23].item == 1)) {
					this.block[n][num23].item = 0;
					this.itemBomb(n, num23);
					n = 0;
					num23 = 0;
				}
				if ((this.block[n][num23].state == 3)
						&& (this.block[n][num23].item == 2)) {
					this.block[n][num23].item = 0;
					this.itemLight(n, num23);
					n = 0;
					num23 = 0;
				}
				if ((this.block[n][num23].state == 1)
						&& (this.block[n][num23].item == 3)) {
					if (this.block[n][num23].color >= 100) {
						Block block1 = this.block[n][num23];
						block1.color -= 100;
					}
					this.plasmaCount++;
					session.set("plasmaCount", this.plasmaCount);
					for (int num24 = 0; num24 < 8; num24++) {
						for (int num25 = 0; num25 < 8; num25++) {
							if (this.block[num24][num25].color == this.block[n][num23].color) {
								this.block[num24][num25].setBreakState();
							}
						}
					}
					if (this.isSound) {
						this.plasmaSound.Play();
					}

					n = 0;
					num23 = 0;
				}
			}
		}
		boolean flag = false;
		for (int num26 = 0; num26 < 8; num26++) {
			for (int num27 = 0; num27 < 8; num27++) {
				if (this.block[num26][num27].state == 3) {
					if (this.block[num26][num27].item >= 10) {
						Block block2 = this.block[num26][num27];
						block2.item -= 10;
						this.block[num26][num27].state = 0;
						this.block[num26][num27].delay = 0f;
						this.block[num26][num27].alpha = 1;
						if (this.block[num26][num27].item == 3) {
							this.block[num26][num27].color = -1;
						}
					} else {
						this.block[num26][num27].state = 30;
						this.blockBreak[num26][num27].animationImages = this.blockBreakAnimationImages
								.get(this.block[num26][num27].color % 10);
						this.blockBreak[num26][num27].animationIndex = 0;
						this.blockBreak[num26][num27].isAnimating = true;
						this.blockBreak[num26][num27].alpha = 1f;
						this.block[num26][num27].breakDelay = 0.5f;
						flag = true;
					}
				}
			}
		}
		if (flag && this.isSound) {
			this.breakSound.Play();
		}
		for (int num28 = 0; num28 < this.selectedList.size(); num28++) {
			String s = this.selectedList.get(num28);
			int num29 = Integer.parseInt(s);
			int index = (num29 / 0x3e8) % 10;
			int num31 = (num29 / 100) % 10;
			int num32 = (num29 / 10) % 10;
			int num33 = num29 % 10;
			if ((this.block[index][num31].state == 1)
					&& (this.block[num32][num33].state == 1)) {
				if (this.gameMode.equalsIgnoreCase("Crazy")) {
					this.block[index][num31].state = 0;
					this.block[num32][num33].state = 0;
					this.block[index][num31].alpha = 1;
					this.block[num32][num33].alpha = 1;
				} else {
					this.block[index][num31].state = 20;
					this.block[num32][num33].state = 20;
					this.block[index][num31].delay = 0.3f;
					this.block[num32][num33].delay = 0.3f;
					this.block[index][num31].alpha = 1;
					this.block[num32][num33].alpha = 1;
					this.block[index][num31].position2 = new Vector2f(
							(float) (14 + (num32 * 0x39)),
							(float) (0xc3 + (num33 * 0x39)));
					this.block[index][num31].velocity = new Vector2f(
							((num32 - index) * 0x39) * 0.2f,
							((num33 - num31) * 0x39) * 0.2f);
					this.block[index][num31].isMoving = true;
					this.block[num32][num33].position2 = new Vector2f(
							(float) (14 + (index * 0x39)),
							(float) (0xc3 + (num31 * 0x39)));
					this.block[num32][num33].velocity = new Vector2f(
							((index - num32) * 0x39) * 0.2f,
							((num31 - num33) * 0x39) * 0.2f);
					this.block[num32][num33].isMoving = true;
					this.block[index][num31].state = 20;
					this.block[num32][num33].state = 20;
					this.block[index][num31].delay = 0.3f;
					this.block[num32][num33].delay = 0.3f;
					Block block = this.block[index][num31];
					this.block[index][num31] = this.block[num32][num33];
					this.block[num32][num33] = block;
					this.selectedList.remove(s);
					num28--;
				}
			} else if (this.block[num32][num33].state == 1) {
				this.block[num32][num33].state = 0;
				this.block[num32][num33].alpha = 1;
				this.selectedList.remove(s);
				num28--;
			} else if (this.block[index][num31].state == 1) {
				this.block[index][num31].state = 0;
				this.block[index][num31].alpha = 1;
				this.selectedList.remove(s);
				num28--;
			} else if ((this.block[index][num31].state != 10)
					&& (this.block[num32][num33].state != 10)) {
				this.selectedList.remove(s);
				num28--;
			}
		}
	}

	private void doHint() {
		this.blockFrame.alpha = 0f;
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				int color = this.block[i][j].color;
				if ((((i + 2) < 8) && ((j - 1) >= 0))
						&& ((this.block[i + 1][j].color == color) && (this.block[i + 2][j - 1].color == color))) {
					this.hint2.position = new Vector2f(
							(this.block[i + 2][j - 1].position.x + this.block[i + 2][j].position.x) / 2f,
							(this.block[i + 2][j - 1].position.y + this.block[i + 2][j].position.y) / 2f);
					this.hint2.alpha = 1f;
					this.hint2.isAnimating = true;
					if (this.isSound) {
						this.buttonSound.Play();
					}
					return;
				}
				if ((((i + 2) < 8) && ((j + 1) < 8))
						&& ((this.block[i + 1][j].color == color) && (this.block[i + 2][j + 1].color == color))) {
					this.hint2.position = new Vector2f(
							(this.block[i + 2][j + 1].position.x + this.block[i + 2][j].position.x) / 2f,
							(this.block[i + 2][j + 1].position.y + this.block[i + 2][j].position.y) / 2f);
					this.hint2.alpha = 1f;
					this.hint2.isAnimating = true;
					if (this.isSound) {
						this.buttonSound.Play();
					}
					return;
				}
				if ((((i + 3) < 8) && (this.block[i + 1][j].color == color))
						&& (this.block[i + 3][j].color == color)) {
					this.hint.position = new Vector2f(
							(this.block[i + 3][j].position.x + this.block[i + 2][j].position.x) / 2f,
							(this.block[i + 3][j].position.y + this.block[i + 2][j].position.y) / 2f);
					this.hint.alpha = 1f;
					this.hint.isAnimating = true;
					if (this.isSound) {
						this.buttonSound.Play();
					}
					return;
				}
				if ((((i + 3) < 8) && (this.block[i + 2][j].color == color))
						&& (this.block[i + 3][j].color == color)) {
					this.hint.position = new Vector2f(
							(this.block[i][j].position.x + this.block[i + 1][j].position.x) / 2f,
							(this.block[i][j].position.y + this.block[i + 1][j].position.y) / 2f);
					this.hint.alpha = 1f;
					this.hint.isAnimating = true;
					if (this.isSound) {
						this.buttonSound.Play();
					}
					return;
				}
				if ((((i + 2) < 8) && ((j - 1) >= 0))
						&& ((this.block[i + 1][j - 1].color == color) && (this.block[i + 2][j - 1].color == color))) {
					this.hint2.position = new Vector2f(
							(this.block[i][j].position.x + this.block[i][j - 1].position.x) / 2f,
							(this.block[i][j].position.y + this.block[i][j - 1].position.y) / 2f);
					this.hint2.alpha = 1f;
					this.hint2.isAnimating = true;
					if (this.isSound) {
						this.buttonSound.Play();
					}
					return;
				}
				if ((((i + 2) < 8) && ((j + 1) < 8))
						&& ((this.block[i + 1][j + 1].color == color) && (this.block[i + 2][j + 1].color == color))) {
					this.hint2.position = new Vector2f(
							(this.block[i][j].position.x + this.block[i][j + 1].position.x) / 2f,
							(this.block[i][j].position.y + this.block[i][j + 1].position.y) / 2f);
					this.hint2.alpha = 1f;
					this.hint2.isAnimating = true;
					if (this.isSound) {
						this.buttonSound.Play();
					}
					return;
				}
				if ((((i + 2) < 8) && ((j - 1) >= 0))
						&& ((this.block[i + 1][j - 1].color == color) && (this.block[i + 2][j].color == color))) {
					this.hint2.position = new Vector2f(
							(this.block[i + 1][j].position.x + this.block[i + 1][j - 1].position.x) / 2f,
							(this.block[i + 1][j].position.y + this.block[i + 1][j - 1].position.y) / 2f);
					this.hint2.alpha = 1f;
					this.hint2.isAnimating = true;
					if (this.isSound) {
						this.buttonSound.Play();
					}
					return;
				}
				if ((((i + 2) < 8) && ((j + 1) < 8))
						&& ((this.block[i + 1][j + 1].color == color) && (this.block[i + 2][j].color == color))) {
					this.hint2.position = new Vector2f(
							(this.block[i + 1][j].position.x + this.block[i + 1][j + 1].position.x) / 2f,
							(this.block[i + 1][j].position.y + this.block[i + 1][j + 1].position.y) / 2f);
					this.hint2.alpha = 1f;
					this.hint2.isAnimating = true;
					if (this.isSound) {
						this.buttonSound.Play();
					}
					return;
				}
				color = this.block[j][i].color;
				if ((((i + 2) < 8) && ((j - 1) >= 0))
						&& ((this.block[j][i + 1].color == color) && (this.block[j - 1][i + 2].color == color))) {
					this.hint.position = new Vector2f(
							(this.block[j - 1][i + 2].position.x + this.block[j][i + 2].position.x) / 2f,
							(this.block[j - 1][i + 2].position.y + this.block[j][i + 2].position.y) / 2f);
					this.hint.alpha = 1f;
					this.hint.isAnimating = true;
					if (this.isSound) {
						this.buttonSound.Play();
					}
					return;
				}
				if ((((i + 2) < 8) && ((j + 1) < 8))
						&& ((this.block[j][i + 1].color == color) && (this.block[j + 1][i + 2].color == color))) {
					this.hint.position = new Vector2f(
							(this.block[j + 1][i + 2].position.x + this.block[j][i + 2].position.x) / 2f,
							(this.block[j + 1][i + 2].position.y + this.block[j][i + 2].position.y) / 2f);
					this.hint.alpha = 1f;
					this.hint.isAnimating = true;
					if (this.isSound) {
						this.buttonSound.Play();
					}
					return;
				}
				if ((((i + 3) < 8) && (this.block[j][i + 1].color == color))
						&& (this.block[j][i + 3].color == color)) {
					this.hint2.position = new Vector2f(
							(this.block[j][i + 3].position.x + this.block[j][i + 2].position.x) / 2f,
							(this.block[j][i + 3].position.y + this.block[j][i + 2].position.y) / 2f);
					this.hint2.alpha = 1f;
					this.hint2.isAnimating = true;
					if (this.isSound) {
						this.buttonSound.Play();
					}
					return;
				}
				if ((((i + 3) < 8) && (this.block[j][i + 2].color == color))
						&& (this.block[j][i + 3].color == color)) {
					this.hint2.position = new Vector2f(
							(this.block[j][i].position.x + this.block[j][i + 1].position.x) / 2f,
							(this.block[j][i].position.y + this.block[j][i + 1].position.y) / 2f);
					this.hint2.alpha = 1f;
					this.hint2.isAnimating = true;
					if (this.isSound) {
						this.buttonSound.Play();
					}
					return;
				}
				if ((((i + 2) < 8) && ((j - 1) >= 0))
						&& ((this.block[j - 1][i + 1].color == color) && (this.block[j - 1][i + 2].color == color))) {
					this.hint.position = new Vector2f(
							(this.block[j][i].position.x + this.block[j - 1][i].position.x) / 2f,
							(this.block[j][i].position.y + this.block[j - 1][i].position.y) / 2f);
					this.hint.alpha = 1f;
					this.hint.isAnimating = true;
					if (this.isSound) {
						this.buttonSound.Play();
					}
					return;
				}
				if ((((i + 2) < 8) && ((j + 1) < 8))
						&& ((this.block[j + 1][i + 1].color == color) && (this.block[j + 1][i + 2].color == color))) {
					this.hint.position = new Vector2f(
							(this.block[j][i].position.x + this.block[j + 1][i].position.x) / 2f,
							(this.block[j][i].position.y + this.block[j + 1][i].position.y) / 2f);
					this.hint.alpha = 1f;
					this.hint.isAnimating = true;
					if (this.isSound) {
						this.buttonSound.Play();
					}
					return;
				}
				if ((((i + 2) < 8) && ((j - 1) >= 0))
						&& ((this.block[j - 1][i + 1].color == color) && (this.block[j][i + 2].color == color))) {
					this.hint.position = new Vector2f(
							(this.block[j][i + 1].position.x + this.block[j - 1][i + 1].position.x) / 2f,
							(this.block[j][i + 1].position.y + this.block[j - 1][i + 1].position.y) / 2f);
					this.hint.alpha = 1f;
					this.hint.isAnimating = true;
					if (this.isSound) {
						this.buttonSound.Play();
					}
					return;
				}
				if ((((i + 2) < 8) && ((j + 1) < 8))
						&& ((this.block[j + 1][i + 1].color == color) && (this.block[j][i + 2].color == color))) {
					this.hint.position = new Vector2f(
							(this.block[j][i + 1].position.x + this.block[j + 1][i + 1].position.x) / 2f,
							(this.block[j][i + 1].position.y + this.block[j + 1][i + 1].position.y) / 2f);
					this.hint.alpha = 1f;
					this.hint.isAnimating = true;
					if (this.isSound) {
						this.buttonSound.Play();
					}
					return;
				}
				if ((((i - 1) >= 0) && (this.block[i][j].item == 3))
						&& (this.block[i - 1][j].item != 3)) {
					this.hint.position = new Vector2f(
							(this.block[i][j].position.x + this.block[i - 1][j].position.x) / 2f,
							(this.block[i][j].position.y + this.block[i - 1][j].position.y) / 2f);
					this.hint.alpha = 1f;
					this.hint.isAnimating = true;
					if (this.isSound) {
						this.buttonSound.Play();
					}
					return;
				}
				if ((((i + 1) < 8) && (this.block[i][j].item == 3))
						&& (this.block[i + 1][j].item != 3)) {
					this.hint.position = new Vector2f(
							(this.block[i][j].position.x + this.block[i + 1][j].position.x) / 2f,
							(this.block[i][j].position.y + this.block[i + 1][j].position.y) / 2f);
					this.hint.alpha = 1f;
					this.hint.isAnimating = true;
					if (this.isSound) {
						this.buttonSound.Play();
					}
					return;
				}
				if ((((j - 1) >= 0) && (this.block[i][j].item == 3))
						&& (this.block[i][j - 1].item != 3)) {
					this.hint2.position = new Vector2f(
							(this.block[i][j].position.x + this.block[i][j - 1].position.x) / 2f,
							(this.block[i][j].position.y + this.block[i][j - 1].position.y) / 2f);
					this.hint2.alpha = 1f;
					this.hint2.isAnimating = true;
					if (this.isSound) {
						this.buttonSound.Play();
					}
					return;
				}
				if ((((j + 1) < 8) && (this.block[i][j].item == 3))
						&& (this.block[i][j + 1].item != 3)) {
					this.hint2.position = new Vector2f(
							(this.block[i][j].position.x + this.block[i][j + 1].position.x) / 2f,
							(this.block[i][j].position.y + this.block[i][j + 1].position.y) / 2f);
					this.hint2.alpha = 1f;
					this.hint2.isAnimating = true;
					if (this.isSound) {
						this.buttonSound.Play();
					}
					return;
				}
			}
		}
		if (this.gameMode.equalsIgnoreCase("Classic")) {
			this.isMainTimerRunning = false;
			this.showResult(-1);
		}
	}

	public LTransition onTransition() {
		return LTransition.newEmpty();
	}

	private ArrayList<LTexture> textures = new ArrayList<LTexture>(10);

	public void draw(SpriteBatch batch) {
		float gameTime = getGameTime().getTotalGameTime();
		if (((this.currentView.equalsIgnoreCase("Intro")) || (this.currentView
				.equalsIgnoreCase("Main"))) && (gameTime <= 10f)) {

			Vector2f v = batch.getFont().getOrigin("游戏资源加载中");

			batch.drawString("游戏资源加载中", (getWidth() - v.x) / 2 - 40, 100);

		}
		if (((gameTime > 3) && (gameTime < 4.0))
				&& !this.isIntroContentUnloaded) {
			this.isIntroContentUnloaded = true;
		}
		if ((this.currentView.equalsIgnoreCase("Game"))
				|| ((this.currentView.equalsIgnoreCase("Main")) && ((this.gameMode
						.equalsIgnoreCase("Classic")) || (this.gameMode
						.equalsIgnoreCase("Crazy"))))) {
			for (ImageView view : this.gameView) {
				if (view.alpha != 0f) {
					if (view.isFramed) {
						batch.draw((view.state == 1) ? view.selectedImage
								: view.image, view.frame, LColor.white);
					} else {
						batch.draw((view.state == 1) ? view.selectedImage
								: view.image, view.position, LColor.white);
					}
				}
			}
			batch.drawString(this.levelLabel.font, this.levelLabel.text,
					this.levelLabel.textPosition(), LColor.yellow);
			if (!this.gameMode.equalsIgnoreCase("Puzzle")) {
				batch.drawString(this.accLabel.font, this.accLabel.text,
						this.accLabel.textPosition(), LColor.blue);
			}
			batch.drawString(this.timeLabel.font, this.timeLabel.text,
					this.timeLabel.textPosition(), LColor.white);
			Block block = null;
			textures.clear();
			for (int i = 7; i >= 0; --i) {
				for (int j = 7; j >= 0; --j) {
					block = this.block[i][j];
					LTexture newTexture = block.image;
					if (!textures.contains(newTexture)) {
						textures.add(newTexture);
						newTexture.glBegin();
					}
					if ((block.backgroundAnimationImages != null)
							&& (block.alpha != 0)) {
						block.backgroundAnimationIndex++;
						if (block.item != 3) {
							newTexture.draw(block.position.x, block.position.y);
						}
						newTexture = block.backgroundAnimationImages
								.get((block.backgroundAnimationIndex / 5)
										% block.backgroundAnimationImages
												.size());
						if (!textures.contains(newTexture)) {
							textures.add(newTexture);
							newTexture.glBegin();
						}
						newTexture.draw(block.position.x, block.position.y);
					} else if ((block.color != -1)
							|| ((block.item != 0) && (block.alpha != 0))) {
						newTexture.draw(block.position.x, block.position.y);
					}
				}
			}
			for (LTexture tex2d : textures) {
				tex2d.glEnd();
			}
			textures.clear();
			for (AnimationView view2 : this.animationViews) {
				if (!view2.isAnimating) {
					continue;
				}
				int size = view2.animationImages.size();
				LTexture newTexture = view2.animationImages
						.get((view2.animationIndex / (view2.animationDelay + 1))
								% size);
				if (!textures.contains(newTexture)) {
					textures.add(newTexture);
					newTexture.glBegin();
				}
				view2.animationTimer -= getGameTime().getElapsedGameTime();
				if (view2.alpha != 0f) {
					if (view2.isFramed) {
						newTexture.draw(view2.frame.x, view2.frame.y);
					} else {
						newTexture.draw(view2.position.x, view2.position.y);
					}
				}
				view2.animationIndex++;
				if ((view2.repeatCount == 1)
						&& (view2.animationIndex > (view2.animationImages
								.size() - 1))) {
					view2.isAnimating = false;
				}
			}
			for (LTexture tex2d : textures) {
				tex2d.glEnd();
			}

			for (ImageView view3 : this.levelInfo.subviews) {
				if (view3.alpha != 0f) {
					if (view3.isFramed) {
						batch.draw((view3.state == 1) ? view3.selectedImage
								: view3.image, view3.frame.x, view3.frame.y);
					} else {
						batch.draw((view3.state == 1) ? view3.selectedImage
								: view3.image, view3.position
								.add(this.levelInfo.position), LColor.white);
					}
				}
			}

			batch.drawString(this.levelInfoLabel.font,
					this.levelInfoLabel.text, this.levelInfoLabel
							.textPosition().add(this.levelInfo.position),
					this.levelInfoLabel.color);
			batch.drawString(this.levelInfoEndScore.font,
					this.levelInfoEndScore.text, this.levelInfoEndScore
							.textPosition().add(this.levelInfo.position),
					this.levelInfoEndScore.color);

			for (ImageView view4 : this.resultInfo.subviews) {
				if (view4.alpha != 0f) {
					if (view4.isFramed) {
						batch.draw((view4.state == 1) ? view4.selectedImage
								: view4.image, view4.frame.x
								+ (this.resultInfo.position.x), view4.frame.y
								+ (this.resultInfo.position.y), view4.frame
								.getWidth(), view4.frame.getHeight());
					} else {
						batch.draw((view4.state == 1) ? view4.selectedImage
								: view4.image, view4.position
								.add(this.resultInfo.position), LColor.white);
					}
				}
			}

			batch.drawString(this.resultBestLabel.font,
					this.resultBestLabel.text, this.resultBestLabel
							.textPosition().add(this.resultInfo.position),
					this.resultBestLabel.color);

			for (ImageView view5 : this.puzzleResultInfo.subviews) {
				if (view5.alpha != 0f) {
					if (view5.isFramed) {
						batch.draw(
								(view5.state == 1) ? view5.selectedImage
										: view5.image,
								view5.frame.x
										+ (int) this.puzzleResultInfo.position.x,
								view5.frame.y
										+ (int) this.puzzleResultInfo.position.y,
								view5.frame.getWidth(), view5.frame.getHeight());
					} else {
						batch.draw((view5.state == 1) ? view5.selectedImage
								: view5.image, view5.position
								.add(this.puzzleResultInfo.position),
								LColor.white);
					}
				}
			}

			batch.drawString(
					this.puzzleResultLevelLabel.font,
					this.puzzleResultLevelLabel.text,
					this.puzzleResultLevelLabel.textPosition().add(
							this.puzzleResultInfo.position),
					this.puzzleResultLevelLabel.color);
		}

		if ((this.currentView.equalsIgnoreCase("PuzzleList"))
				|| ((this.currentView.equalsIgnoreCase("Main")) && (this.gameMode
						.equalsIgnoreCase("Puzzle")))) {

			for (ImageView view6 : this.puzzleListTopView.subviews) {
				if (view6.alpha != 0f) {
					if (view6.isFramed) {
						batch.draw((view6.state == 1) ? view6.selectedImage
								: view6.image, view6.frame, LColor.white);
					} else {
						batch.draw((view6.state == 1) ? view6.selectedImage
								: view6.image, view6.position
								.add(this.puzzleListTopView.position),
								LColor.white);
					}
				}
			}

			for (ImageView view7 : this.puzzleListBottomView.subviews) {
				if (view7.alpha != 0f) {
					if (view7.isFramed) {
						batch.draw((view7.state == 1) ? view7.selectedImage
								: view7.image, view7.frame, LColor.white);
					} else {
						batch.draw((view7.state == 1) ? view7.selectedImage
								: view7.image, view7.position
								.add(this.puzzleListBottomView.position),
								LColor.white);
					}
				}
			}

			for (ImageView view8 : this.puzzleScrollView.subviews) {
				if (view8.alpha != 0f) {
					if (view8.isFramed) {
						batch.draw(view8.image, view8.frame, LColor.white);
					} else {
						batch.draw(view8.image, ((view8.position
								.add(this.puzzleScrollView.position))
								.add(this.puzzleListBottomView.position))
								.sub(this.puzzleScrollView.offset),
								LColor.white);
					}
				}
			}
		}
		if (((this.currentView.equalsIgnoreCase("Main")) || (this.currentView
				.equalsIgnoreCase("Game")))
				|| (this.currentView.equalsIgnoreCase("PuzzleList"))) {
			for (ImageView view9 : this.mainTopView.subviews) {
				if (view9.alpha != 0f) {
					if (view9.isFramed) {
						batch.draw(view9.image, view9.frame, LColor.white);
					} else {
						batch.draw(view9.image,
								view9.position.add(this.mainTopView.position),
								LColor.white);
					}
				}
			}
			for (ImageView view10 : this.mainBottomView.subviews) {
				if (view10.alpha != 0f) {
					if (view10.isFramed) {
						batch.draw((view10.state == 1) ? view10.selectedImage
								: view10.image, view10.frame, LColor.white);
					} else {
						batch.draw((view10.state == 1) ? view10.selectedImage
								: view10.image, view10.position
								.add(this.mainBottomView.position),
								LColor.white);
					}
				}
			}
		}
		if (this.currentView.equalsIgnoreCase("Help")) {
			for (ImageView view11 : this.helpView.subviews) {
				if (view11.alpha != 0f) {
					if (view11.isFramed) {
						batch.draw((view11.state == 1) ? view11.selectedImage
								: view11.image, view11.frame, LColor.white);
					} else {
						if ((view11 == this.helpPrevButton)
								|| (view11 == this.helpNextButton)) {
							batch.draw(
									(view11.state == 1) ? view11.selectedImage
											: view11.image, view11.position,
									LColor.white);
							continue;
						}
						batch.draw((view11.state == 1) ? view11.selectedImage
								: view11.image, view11.position
								.sub(this.helpView.offset), LColor.white);
					}
				}
			}
		}
	}

	private ArrayList<Block> source = new ArrayList<Block>();

	private Block[][] blockArray = new Block[8][];

	private void dropBlock() {
		source.clear();
		for (int i = 0; i < 8; i++) {
			if (blockArray[i] == null) {
				blockArray[i] = new Block[8];
			}
			for (int m = 0; m < 8; m++) {
				blockArray[i][m] = this.block[i][m];
			}
		}
		Block block = null;
		for (int j = 7; j >= 0; j--) {
			for (int n = 7; n >= 0; n--) {
				block = blockArray[j][n];
				if ((block.state == 0x1f) || (block.state == 30)) {
					source.add(block);
				} else if (source.size() > 0) {
					this.block[j][n + source.size()] = block;
					this.block[j][n + source.size()].state = 40;
					this.block[j][n + source.size()].delay = 0.5f;
					this.block[j][n + source.size()].newTop = block.position
							.y();
				}
			}
			for (int num5 = 0; num5 < source.size(); num5++) {
				this.block[j][num5] = source.get(num5);
				block = this.block[j][num5];
				if (this.gameMode.equalsIgnoreCase("Puzzle")) {
					block.color = -1;
					block.state = 40;
					block.delay = 0.3f;
					block.item = 0;
					block.image = null;
					block.backgroundAnimationImages = null;
					block.alpha = 0;
					block.position.set((14 + (j * 0x39)),
							(0xc3 - ((source.size() - num5) * 0x39)));
					block.newTop = 0xc3 - ((source.size() - num5) * 0x39);
				} else {
					int num7;
					if (this.level >= 10) {
						num7 = this.random.Next(7);
					} else if (this.level >= 6) {
						num7 = this.random.Next(6);
					} else if (this.level >= 3) {
						num7 = this.random.Next(5);
					} else {
						num7 = this.random.Next(4);
					}
					block.newTop = 0xc3 - ((source.size() - num5) * 0x39);
					block.position.set((14 + (j * 0x39)),
							(0xc3 - ((source.size() - num5) * 0x39)));
					block.image = this.jewelR[num7];
					block.color = num7;
					block.state = 40;
					block.delay = 0.5f;
					block.alpha = 1;
					block.item = 0;
					block.backgroundAnimationImages = null;
					this.lastBreakTime = this.t;
					this.hint.alpha = 0f;
					this.hint2.alpha = 0f;
				}
			}
			this.acc += source.size() * 0.2f;
			this.score += (source.size() * 50)
					* ((MathUtils.floor(this.acc)) + 1);
			source.clear();
		}
		this.setScore();
		this.setAcc();
		for (int k = 7; k >= 0; k--) {
			for (int m = 0; m < 8; m++) {
				block = this.block[m][k];
				if ((block.position.y != (0xc3 + (k * 0x39)))
						&& (block.position2.y != (0xc3 + (k * 0x39)))) {
					int num8 = ((0xc3 + (k * 0x39)) - block.newTop) / 0x39;
					if (num8 < 1) {
						num8 = 1;
					}
					if (num8 > 8) {
						num8 = 8;
					}
					block.alpha = 1;
					block.position2 = new Vector2f((14 + (m * 0x39)),
							(0xc3 + (k * 0x39)));
					block.velocity = new Vector2f(0f,
							(block.position2.y - block.newTop) * 0.15f);
					block.isMoving = true;
				} else if ((block.state != 3) && (block.state != 30)) {
					block.alpha = 1;
				}
			}
		}
	}

	private void goNextLevel() {

		if (this.gameMode.equalsIgnoreCase("Puzzle")) {
			String str;
			this.level++;
			if (this.level < 1) {
				this.level = 1;
			}
			if (this.level > 30) {
				this.level = 30;
			}
			this.score = 0;
			this.tTotal = 60;
			this.t = this.tTotal;
			this.x = -1;
			this.y = -1;
			this.selectedList.clear();
			this.blockFrame.alpha = 0f;
			this.timeAlarm.alpha = 0f;
			this.bgImage.image = this.bgImageR[(this.level + 1) % 4];
			for (int i = 0; i < 8; i++) {
				for (int k = 0; k < 8; k++) {
					int num3 = -1;
					this.block[i][k].state = 0;
					this.block[i][k].delay = 0f;
					this.block[i][k].color = num3;
					this.block[i][k].item = 0;
					this.block[i][k].alpha = 0;
					this.block[i][k].image = null;
					this.block[i][k].backgroundAnimationImages = null;
					this.blockBreak[i][k].animationImages = null;
				}
			}
			if (this.level == 1) {
				str = "3010 3140 3210 4040 4110 4240";
			} else if (this.level == 2) {
				str = "3010 3140 3210 4040 4110 4240 4340 3340 2040 4430";
			} else if (this.level == 3) {
				str = "1040 2040 3010 3120 3220 3330 4020 4110 4210 4340 5030 6030";
			} else if (this.level == 4) {
				str = "3010 3120 3210 3310 4030 4110 5010 5120 6010";
			} else if (this.level == 5) {
				str = "0040 0140 0230 0330 1010 1130 2010 2140 3020 4010 5020 5130 6020 6140 7030 7130 7240 7340";
			} else if (this.level == 6) {
				str = "5000 5100 5230 5300 4040 4130 4200 4330 4440 3000 2000";
			} else if (this.level == 7) {
				str = "1010 2020 2110 2210 3010 3130 3240 3350 4010 4110 5040 5130 5230 5310 6030 6140 6240";
			} else if (this.level == 8) {
				str = "0010 0120 0230 0320 0430 0520 0630 1010 1120 1230 1320 1430 1520 1630 2020 2140 2210 2340 2440 3010 3120 3230 3320 3430 3520 3630 4020 4120 4230 4320 4430 4520 4630 5010 6020 7020";
			} else if (this.level == 9) {
				str = "2030 2110 2230 2340 3030 3140 3220 3310 3420 3520 4010 4110 4240 4340 5010 5120 5220 5310 5420";
			} else if (this.level == 10) {
				str = "1040 2020 2140 3010 3110 3220 4040 4130 4210 4320 4440 5010 5110 5230 5340 6030 6140 6210";
			} else if (this.level == 11) {
				str = "0060 0100 0260 0350 0440 0530 0620 0710 1000 1193 1250 1340 1430 1520 1610 1720 2060 2150 2240 2330 2420 2510 2620 2730 3050 3140 3230 3320 3410 3520 3630 7060 7100 7260 7350 7440 7530 7620 7710 6000 6193 6250 6340 6430 6520 6610 6720 5060 5150 5240 5330 5420 5510 5620 5730 4050 4140 4230 4320 4410 4520 4630 0060";
			} else if (this.level == 12) {
				str = "0020 0110 1020 1140 1210 2030 2120 2240 2310 3020 3130 3220 3330 4020 4130 4220 4330 5030 5120 5250 5310 6020 6150 6210 7020 7110";
			} else if (this.level == 13) {
				str = "0010 1010 2040 2130 2240 3010 3110 3230 3330 3440 4010 4130 5030 5140 6010 6110 6240 6340 6430 7010 7140";
			} else if (this.level == 14) {
				str = "2030 2110 2230 2320 2410 3010 3130 3220 3330 3420 3510 3610 3702 4030 4110 4230 4320 4410 5000 6000";
			} else if (this.level == 15) {
				str = "0000 0120 0200 1010 1193 1210 1320 2000 2120 2200 3000 4010 5010 5120 5210 6000 6193 6200 6320 7010 7120 7210";
			} else if (this.level == 0x10) {
				str = "0000 0110 0210 1010 2000 2120 2200 2320 3020 3100 3200 4000 4130";
			} else if (this.level == 0x11) {
				str = "2010 2110 2200 2300 2410 2510 2600 2700 3000 3100 3210 3310 3400 3500 3610 3710 4010 4110 4200 4300 4410 4510 4600 4700";
			} else if (this.level == 0x12) {
				str = "0030 1000 2000 2110 2200 3010 3100 3200 3310 3430 4000 4110 5000 6030";
			} else if (this.level == 0x13) {
				str = "0060 0130 0260 1030 1101 1230 2020 2100 2220 3010 3140 3210 3310 4040 4130 4240 5030 5120 5230 6020 6110 6220 7010 7100 7210";
			} else if (this.level == 20) {
				str = "1010 1110 1200 1300 1410 1510 2010 2110 2230 2300 2410 2510 3030 3130 3200 3320 3400 3500 4000 4130 4200 4330 4430 4510 5020 5120 5230 5300 5420 5520 6020 6120 6230 6300 6420 6520";
			} else if (this.level == 0x15) {
				str = "1040 1100 1200 2030 2100 2240 3020 3130 3200 3340 4000 4120 4230 5020 5130 5200 5340 6030 6100 6240 7040 7100 7200";
			} else if (this.level == 0x16) {
				str = "2010 3010 4000 4130 5010 5140 5220 6010 6130 6240 6320 6420 6530 7030 7110 7230 7300 7440 7530";
			} else if (this.level == 0x17) {
				str = "0000 0110 0200 0310 0400 0510 0600 0710 1010 1100 1210 1300 1410 1500 1610 1700 2000 2110 2200 2310 2400 2510 2600 2710 3010 3100 3210 3300 3410 3500 3610 3700 4000 4110 4200 4310 4400 4510 4600 4710 5010 5100 5210 5300 5410 5500 5610 5700 6000 6110 6200 6310 6400 6510 6600 6710 7010 7100 7210 7300 7410 7500 7610 7700";
			} else if (this.level == 0x18) {
				str = "0010 0120 0230 0340 3040 3130 3250 3340 4050 4110 4230 4310 5040 5120 5250 5350 5440 5510 5650 6010 6150 6240 6340 7093 7193 7293 7393";
			} else if (this.level == 0x19) {
				str = "0000 0110 0210 0300 1010 1100 1200 1310 2010 2100 2200 2310 3000 3110 3210 3300 4010 4120 4220 4310 5020 5110 5210 5320 6020 6110 6210 6320 7010 7120 7220 7310";
			} else if (this.level == 0x1a) {
				str = "0010 0100 0210 1020 1110 1220 2030 2120 2230 7010 7100 7210 6020 6110 6220 5030 5120 5230 3000 4000 3130 4131 3200 4200 3300";
			} else if (this.level == 0x1b) {
				str = "0010 0100 0210 0310 0400 0510 0610 1020 1110 1220 1320 2030 2120 2230 2330 3040 3130 3240 3340 4050 4140 4250 4350 5060 5150 5260 5360 6000 6160 6200 6300";
			} else if (this.level == 0x1c) {
				str = "2010 2130 2210 2330 2410 2530 3040 3110 3230 3310 3430 3510 4010 4130 4210 4330 4410 4530";
			} else if (this.level == 0x1d) {
				str = "0010 0120 0210 0310 1030 1130 1220 1330 2010 2120 2210 2310 3030 3130 3220 3330 4030 4120 4230 4330 5010 5110 5220 5310 5430 5530 6030 6120 6230 6330 7010 7110 7220 7312";
			} else {
				str = "0060 0100 0200 0360 0400 0500 1010 1160 1210 2060 2110 3010 3160 3240 3360 3460 4060 4140 4260 4340 4440 5060 5150 5240 5340 5450 6050 6160 7050 7160";
			}
			String[] strArray = str.split("[ ]", -1);
			this.finalScore = strArray.length * 100;
			for (int j = 0; j < strArray.length; j++) {
				int num5 = Integer.parseInt(strArray[j]);
				int index = num5 / 0x3e8;
				int num7 = 7 - ((num5 / 100) % 10);
				int num8 = (num5 / 10) % 10;
				int num9 = num5 % 10;
				if (num8 == 9) {
					num8 = -1;
				}
				if ((num8 >= 0) && (num8 < 8)) {
					this.block[index][num7].image = this.jewelR[num8];
				}
				this.block[index][num7].color = num8;
				this.block[index][num7].alpha = 1;
				this.block[index][num7].item = num9;
				if (num9 > 0) {
					if (this.block[index][num7].item == 1) {
						this.block[index][num7].backgroundAnimationImages = this.blockBombAnimationImages;
					} else if (this.block[index][num7].item == 2) {
						this.block[index][num7].backgroundAnimationImages = this.blockLightningAnimationImages;
					} else if (this.block[index][num7].item == 3) {
						this.block[index][num7].backgroundAnimationImages = this.blockColorAnimationImages;
					}
				}
			}
			this.initBlock();
			this.setTimeBar();
			this.setScore();
			this.setLevel();
			this.isMainTimerRunning = true;
		} else {
			if (!this.gameMode.equalsIgnoreCase("Crazy")) {
				this.tTotal = 0x5b;
			} else {
				this.tTotal = 0x3d;
			}
			this.t = this.tTotal;
			this.x = -1;
			this.y = -1;
			this.selectedList.clear();
			if (!this.gameMode.equalsIgnoreCase("Crazy")) {
				this.acc = 0f;
			}
			this.lastBreakTime = this.t;
			this.hint.alpha = 0f;
			this.hint2.alpha = 0f;
			this.blockFrame.alpha = 0f;
			this.timeAlarm.alpha = 0f;
			if (this.level == 0) {
				this.startScore = 0;
			} else if (this.level == 1) {
				this.startScore = 0x4e20;
			} else if (this.level == 2) {
				this.startScore = 0xafc8;
			} else if (this.level == 3) {
				this.startScore = 0xea60;
			} else if (this.level == 4) {
				this.startScore = 0x13880;
			} else if (this.level == 5) {
				this.startScore = 0x19a28;
			} else if (this.level == 6) {
				this.startScore = 0x1c138;
			} else if (this.level == 7) {
				this.startScore = 0x1fbd0;
			} else if (this.level == 8) {
				this.startScore = 0x249f0;
			} else if (this.level == 9) {
				this.startScore = 0x2ab98;
			} else if (this.level >= 10) {
				this.startScore = 0x2d2a8 + (((0x1388 * (this.level - 10)) * ((this.level - 10) + 5)) / 2);
			}
			this.level++;
			if (this.level == 0) {
				this.endScore = 0;
			} else if (this.level == 1) {
				this.endScore = 0x4e20;
			} else if (this.level == 2) {
				this.endScore = 0xafc8;
			} else if (this.level == 3) {
				this.endScore = 0xea60;
			} else if (this.level == 4) {
				this.endScore = 0x13880;
			} else if (this.level == 5) {
				this.endScore = 0x19a28;
			} else if (this.level == 6) {
				this.endScore = 0x1c138;
			} else if (this.level == 7) {
				this.endScore = 0x1fbd0;
			} else if (this.level == 8) {
				this.endScore = 0x249f0;
			} else if (this.level == 9) {
				this.endScore = 0x2ab98;
			} else if (this.level >= 10) {
				this.endScore = 0x2d2a8 + (((0x1388 * (this.level - 10)) * ((this.level - 10) + 5)) / 2);
			}
			this.bgImage.image = this.bgImageR[(this.gameMode
					.equalsIgnoreCase("Crazy")) ? ((this.level + 3) % 4)
					: ((this.level + 1) % 4)];
			if ((!this.gameMode.equalsIgnoreCase("Crazy")) || (this.level == 1)) {
				for (int m = 0; m < 8; m++) {
					for (int n = 0; n < 8; n++) {
						int num12;
						if (this.level >= 10) {
							num12 = this.random.Next(7);
						} else if (this.level >= 6) {
							num12 = this.random.Next(6);
						} else if (this.level >= 3) {
							num12 = this.random.Next(5);
						} else {
							num12 = this.random.Next(4);
						}
						if (((((m - 1) >= 0) && (this.block[m - 1][n].color == num12)) && (((m - 2) >= 0) && (this.block[m - 2][n].color == num12)))
								|| ((((n - 1) >= 0) && (this.block[m][n - 1].color == num12)) && (((n - 2) >= 0) && (this.block[m][n - 2].color == num12)))) {
							if (n == 0) {
								m--;
								n = 7;
							} else {
								n--;
							}
						} else {
							this.block[m][n].color = num12;
							this.block[m][n].alpha = 1;
							this.blockBreak[m][n].animationImages = this.blockBreakAnimationImages
									.get(num12);
							this.blockBreak[m][n].animationIndex = 0;
							this.blockBreak[m][n].isAnimating = false;
							this.blockBreak[m][n].repeatCount = 1;
							this.blockBreak[m][n].alpha = 1f;
							this.block[m][n].image = this.jewelR[num12];
							if (this.block[m][n].item == 3) {
								this.block[m][n].color = -1;
							} else {
								this.block[m][n].color = num12;
							}
							if (this.block[m][n].item == 1) {
								this.block[m][n].backgroundAnimationImages = this.blockBombAnimationImages;
							} else if (this.block[m][n].item == 2) {
								this.block[m][n].backgroundAnimationImages = this.blockLightningAnimationImages;
							} else if (this.block[m][n].item == 3) {
								this.block[m][n].backgroundAnimationImages = this.blockColorAnimationImages;
							}
						}
					}
				}
				this.initBlock();
			}
			this.setTimeBar();
			this.setScore();
			this.setLevel();
			this.showLevelInfo();
			this.isMainTimerRunning = true;
		}
	}

	private void hideMain() {
		this.mainTopView.position = new Vector2f(0f, 0f);
		this.mainTopView.position2 = new Vector2f(0f, -310f);
		this.mainTopView.velocity = new Vector2f(0f, -30f);
		this.mainTopView.isMoving = true;
		this.mainBottomView.position = new Vector2f(0f, 276f);
		this.mainBottomView.position2 = new Vector2f(0f, 800f);
		this.mainBottomView.velocity = new Vector2f(0f, 50f);
		this.mainBottomView.isMoving = true;
		if (this.isSound) {
			this.gateSound.Play();
		}
	}

	private void initBlock() {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				this.block[i][j].position = new Vector2f(
						(float) (14 + (i * 0x39)),
						(float) ((0xc3 + ((j * 0x39) * 2)) - 0x390));
				this.block[i][j].state = 40;
				this.block[i][j].delay = 1f;
				int num = 8 - j;
				if (num < 1) {
					num = 1;
				}
				if (num > 8) {
					num = 8;
				}
				this.block[i][j].alpha = 1;
				this.block[i][j].position2 = new Vector2f(
						(float) (14 + (i * 0x39)), (float) (0xc3 + (j * 0x39)));
				this.block[i][j].velocity = new Vector2f(0f, 10f);
				this.block[i][j].isMoving = true;
			}
		}
	}

	private boolean isPositionInImageView(Vector2f vector, ImageView imageView) {
		return (((vector.x >= imageView.position.x) && (vector.x <= (imageView.position.x + imageView.image
				.getWidth()))) && ((vector.y >= imageView.position.y) && (vector.y <= (imageView.position.y + imageView.image
				.getHeight()))));
	}

	private void itemBomb(int i, int j) {
		AnimationView item = new AnimationView(new RectBox((14 + (0x39 * i))
				+ -61, (0xc3 + (0x39 * j)) + -61, 180, 180));
		item.animationImages = this.bombAnimationImages;
		item.animationIndex = 0;
		item.repeatCount = 1;
		item.isAnimating = true;
		this.animationViews.add(item);
		if (((i - 1) >= 0) && ((j - 1) >= 0)) {
			this.block[i - 1][j - 1].setBreakState();
		}
		if ((j - 1) >= 0) {
			this.block[i][j - 1].setBreakState();
		}
		if (((i + 1) < 8) && ((j - 1) >= 0)) {
			this.block[i + 1][j - 1].setBreakState();
		}
		if ((i - 1) >= 0) {
			this.block[i - 1][j].setBreakState();
		}
		if ((i + 1) < 8) {
			this.block[i + 1][j].setBreakState();
		}
		if (((i - 1) >= 0) && ((j + 1) < 8)) {
			this.block[i - 1][j + 1].setBreakState();
		}
		if ((j + 1) < 8) {
			this.block[i][j + 1].setBreakState();
		}
		if (((i + 1) < 8) && ((j + 1) < 8)) {
			this.block[i + 1][j + 1].setBreakState();
		}
		if (this.isSound) {
			this.bombSound.Play();
		}
		this.bombCount++;
		session.set("bombCount", bombCount);
	}

	private void itemLight(int i, int j) {
		AnimationView item = new AnimationView(new RectBox((14 + (0x39 * i))
				+ -321, (0xc3 + (0x39 * j)) + -321, 700, 700));
		item.animationImages = this.lightningAnimationImages;
		item.animationIndex = 0;
		item.repeatCount = 1;
		item.isAnimating = true;
		this.animationViews.add(item);
		for (int k = 0; k < 8; k++) {
			this.block[k][j].setBreakState();
			this.block[i][k].setBreakState();
		}
		if (this.isSound) {
			this.lightningSound.Play();
		}
		this.lightningCount++;
		session.set("lightningCount", lightningCount);
	}

	private void loadHelpContent() {
		for (int i = 0; i < 6; i++) {
			this.helpList[i].image = Global.Load("Images\\howtoplay_0"
					+ (i + 1));
			this.helpMenuButton[i].image = Global
					.Load("Images\\howtoplay_bt_menu");
			this.helpMenuButton[i].selectedImage = Global
					.Load("Images\\howtoplay_bt_menu_on");
		}
		this.helpPrevButton.image = Global.Load("Images\\puzzle_left");
		this.helpPrevButton.selectedImage = Global
				.Load("Images\\puzzle_left_on");
		this.helpNextButton.image = Global.Load("Images\\puzzle_right");
		this.helpNextButton.selectedImage = Global
				.Load("Images\\puzzle_right_on");
	}

	private void mainTimer(GameTime gameTime) {
		if (this.isMainTimerRunning
				&& (this.currentView.equalsIgnoreCase("Game"))) {
			this.dt = gameTime.getMilliseconds() * 0.001f;
			this.t -= this.dt;
			if (this.t < 0f) {
				this.t = 0f;
			}
			this.setTimeBar();
			this.acc *= 0.99f;
			for (int i = 0; i < 8; i++) {
				for (int j = 0; j < 8; j++) {
					if ((this.block[i][j].delay - this.dt) > 0f) {
						Block block1 = this.block[i][j];
						block1.delay -= this.dt;
					} else {
						this.block[i][j].delay = 0f;
						if (this.block[i][j].state == 40) {
							this.block[i][j].state = 0;
							this.block[i][j].alpha = 1;
						} else if (this.block[i][j].state == 20) {
							this.block[i][j].state = 0;
							this.block[i][j].alpha = 1;
						} else if (this.block[i][j].state == 30) {
							this.block[i][j].state = 0x1f;
						} else if (this.block[i][j].state == 10) {
							this.block[i][j].state = 1;
						}
					}
					if ((this.block[i][j].breakDelay - this.dt) > 0f) {
						Block block2 = this.block[i][j];
						block2.breakDelay -= this.dt;
					} else {
						this.block[i][j].breakDelay = 0f;
					}
				}
			}
			this.dropBlock();
			this.checkMatch();
			if (this.gameMode.equalsIgnoreCase("Puzzle")) {
				boolean flag = true;
				for (int k = 0; k < 8; k++) {
					for (int m = 0; m < 8; m++) {
						if (((this.block[k][m].color != -1) || (this.block[k][m].item != 0))
								|| (this.block[k][m].breakDelay != 0f)) {
							flag = false;
						}
					}
				}
				if (flag) {
					this.score = this.finalScore;
					this.setScore();
					if (this.level > this.puzzleUnlockedLevel) {
						this.puzzleUnlockedLevel = this.level;
						session.set("puzzleUnlockedLevel", puzzleUnlockedLevel);
					}
					this.isMainTimerRunning = false;
					this.showResult(1);
				} else if (this.t <= 0f) {
					this.isMainTimerRunning = false;
					this.showResult(0);
				}
			} else {
				int index = (int) (((float) (7 * (this.score - this.startScore))) / ((float) (this.endScore - this.startScore)));
				if (index < 0) {
					index = 0;
				}
				if (index > 7) {
					index = 7;
				}
				this.levelImage.image = this.levelImageR[index];
				if (this.score > this.endScore) {
					this.isMainTimerRunning = false;
					this.goNextLevel();
				} else if (this.t <= 0f) {
					this.isMainTimerRunning = false;
					this.showResult(0);
				} else if ((this.lastBreakTime - 5f) > this.t) {
					this.lastBreakTime = this.t;
					if (this.gameMode.equalsIgnoreCase("Classic")) {
						this.doHint();
					}
				}
			}
		}
	}

	private void newGame() {
		if (this.gameMode.equalsIgnoreCase("Puzzle")) {
			this.level = this.puzzlePlayingLevel;
			this.score = 0;
			this.acc = 0f;
			if (this.gameMode.equalsIgnoreCase("Puzzle")) {
				this.gameBgImage.image = this.gameBgImageR[2];
			} else if (this.gameMode.equalsIgnoreCase("Crazy")) {
				this.gameBgImage.image = this.gameBgImageR[1];
			} else {
				this.gameBgImage.image = this.gameBgImageR[0];
			}
			this.menuButton.alpha = 0f;
			this.hintButton.alpha = 0f;
			this.puzzleMenuButton.alpha = 1f;
			this.puzzleRetryButton.alpha = 1f;
			this.accImage.alpha = 0f;
			this.levelLabel.frame = new RectBox(0x20, 0x2d9, 100, 50);
			this.level--;
			this.goNextLevel();
		} else {
			this.level = 0;
			this.score = 0;
			this.acc = 0f;
			if (this.gameMode.equalsIgnoreCase("Puzzle")) {
				this.gameBgImage.image = this.gameBgImageR[2];
			} else if (this.gameMode.equalsIgnoreCase("Crazy")) {
				this.gameBgImage.image = this.gameBgImageR[1];
			} else {
				this.gameBgImage.image = this.gameBgImageR[0];
			}
			this.menuButton.alpha = 1f;
			this.hintButton.alpha = (this.gameMode.equalsIgnoreCase("Classic")) ? ((float) 1)
					: ((float) 0);
			this.puzzleMenuButton.alpha = 0f;
			this.puzzleRetryButton.alpha = 0f;
			this.accImage.alpha = 1f;
			this.levelLabel.frame = new RectBox(50, 0x2e1, 100, 50);
			if (this.resultInfo.position.y == 100f) {
				this.resultInfo.position = new Vector2f(41f, -470f);
				this.resultInfo.isMoving = false;
			}
			for (int i = 0; i < 8; i++) {
				for (int j = 0; j < 8; j++) {
					this.block[i][j].item = 0;
					this.block[i][j].backgroundAnimationImages = null;
				}
			}
			this.goNextLevel();
		}
	}

	private void setAcc() {
		this.accLabel.text = "x" + ((MathUtils.floor(this.acc)) + 1);
		if ((this.acc + 1f) > this.maxAcc) {
			this.maxAcc = (MathUtils.floor(this.acc)) + 1;
			session.set("maxAcc", maxAcc);
		}
		if (this.acc != ((MathUtils.floor(this.acc)) + 1)) {
			int num = (MathUtils.floor(this.acc)) + 1;
			if (num < 8) {
				this.accImage.image = this.accImageR[num - 1];
			} else {
				this.accImage.image = this.accImageR[6];
			}
		}
	}

	private void setHelpList() {
		if (this.helpCurrentPage == 0) {
			this.helpPrevButton.state = 0;
			this.helpNextButton.state = 1;
		} else if (this.helpCurrentPage == 5) {
			this.helpPrevButton.state = 1;
			this.helpNextButton.state = 0;
		} else {
			this.helpPrevButton.state = 1;
			this.helpNextButton.state = 1;
		}
	}

	private void setLevel() {
		this.levelLabel.text = "" + this.level;
	}

	private void setPuzzleList() {
		if (this.puzzleCurrentPage == 0) {
			this.prevButton.state = 0;
			this.nextButton.state = 1;
		} else if (this.puzzleCurrentPage == 2) {
			this.prevButton.state = 1;
			this.nextButton.state = 0;
		} else {
			this.prevButton.state = 1;
			this.nextButton.state = 1;
		}
	}

	private void setScore() {
		for (int i = 0; i < 7; i++) {
			this.scoreImage[i].image = this.scoreImageR[(this.score / ((int) Math
					.pow(10.0, (double) i))) % 10];
			if ((this.score >= Math.pow(10.0, (double) i)) || (i == 0)) {
				this.scoreImage[i].alpha = 1f;
			} else {
				this.scoreImage[i].alpha = 0f;
			}
		}
	}

	private void setTimeBar() {
		float num4;
		float num5;
		float num6;
		int num = (int) Math.round((double) this.t);
		this.timeLabel.text = StringUtils.concat(num / 60, ":",
				((num % 60) < 10) ? "0" : "", num % 60);
		int num2 = 0x1c8;
		float num3 = (num2 * this.t) / (this.tTotal * 1f);
		if (num3 > 16f) {
			num4 = 8f;
			num5 = num3 - 16f;
			num6 = 8f;
		} else {
			num4 = num3 / 2f;
			num5 = 0f;
			num6 = num3 - num4;
		}
		float num7 = Math.min((float) 20f, (float) (num3 * 2f));
		this.timeImage[0].frame = new RectBox(12, 180 - ((int) (num7 * 0.5f)),
				(int) num4, (int) num7);
		this.timeImage[1].frame = new RectBox(12 + ((int) num4),
				180 - ((int) (num7 * 0.5f)), (int) num5, (int) num7);
		this.timeImage[2].frame = new RectBox((12 + ((int) num4))
				+ ((int) num5), 180 - ((int) (num7 * 0.5f)), (int) num6,
				(int) num7);
		if (this.t <= 5f) {
			for (int i = 5; i >= 0; i--) {
				if ((this.t < i) && (this.t >= (i - this.dt))) {
					this.timeAlarm.alpha = 1f;
					return;
				}
				if ((this.t < (i - 0.5f)) && (this.t >= ((i - 0.5f) - this.dt))) {
					this.timeAlarm.alpha = 0f;
					return;
				}
			}
		}
	}

	private void showLevelInfo() {
		if (this.gameMode != "Puzzle") {
			int num;
			this.levelInfoLabel.text = "" + this.level;
			this.levelInfoEndScore.text = "" + this.endScore;
			if (this.level >= 10) {
				num = 7;
			} else if (this.level >= 6) {
				num = 6;
			} else if (this.level >= 3) {
				num = 5;
			} else {
				num = 4;
			}
			for (int i = 0; i < 7; i++) {
				if (i < num) {
					this.levelInfoImage[i].alpha = 1f;
				} else {
					this.levelInfoImage[i].alpha = 0f;
				}
			}
			if ((this.level != 1) && this.isSound) {
				this.gateSound.Play();
			}
			this.levelInfo.position2 = new Vector2f(41f, 100f);
			this.levelInfo.velocity = new Vector2f(0f, 10f);
			this.levelInfo.isMoving = true;
		}
	}

	private void showMain() {
		if (this.isResumable) {
			this.resumeButton.alpha = 1f;
			this.resumeButton.position = new Vector2f(150f, 95f);
			this.classicButton.position = new Vector2f(150f, 170f);
			this.crazyButton.position = new Vector2f(150f, 245f);
			this.puzzleButton.position = new Vector2f(150f, 320f);
			this.soundButton.position = new Vector2f(107f, 435f);
			this.vibButton.position = new Vector2f(207f, 435f);
			this.helpButton.position = new Vector2f(307f, 435f);
			this.classicLeaderButton.position = new Vector2f(350f, 170f);
			this.crazyLeaderButton.position = new Vector2f(350f, 245f);
			this.achievementButton.position = new Vector2f(35f, 320f);
		} else {
			this.resumeButton.alpha = 0f;
			this.resumeButton.position = new Vector2f(150f, 105f);
			this.classicButton.position = new Vector2f(150f, 105f);
			this.crazyButton.position = new Vector2f(150f, 190f);
			this.puzzleButton.position = new Vector2f(150f, 275f);
			this.soundButton.position = new Vector2f(107f, 400f);
			this.vibButton.position = new Vector2f(207f, 400f);
			this.helpButton.position = new Vector2f(307f, 400f);
			this.classicLeaderButton.position = new Vector2f(350f, 105f);
			this.crazyLeaderButton.position = new Vector2f(350f, 190f);
			this.achievementButton.position = new Vector2f(35f, 275f);
		}
		this.mainTopView.position = new Vector2f(0f, -310f);
		this.mainTopView.position2 = new Vector2f(0f, 0f);
		this.mainTopView.velocity = new Vector2f(0f, 30f);
		this.mainTopView.isMoving = true;
		this.mainBottomView.position = new Vector2f(0f, 800f);
		this.mainBottomView.position2 = new Vector2f(0f, 276f);
		this.mainBottomView.velocity = new Vector2f(0f, -50f);
		this.mainBottomView.isMoving = true;
		if (this.isSound) {
			this.gateSound.Play();
		}
	}

	private void showPuzzleList() {
		for (int i = 0; i < 30; i++) {
			if (i > this.puzzleUnlockedLevel) {
				this.listButton[i].image = Global.Load("Images\\puzzle_lock");
			} else {
				this.listButton[i].image = Global.Load("Images\\"
						+ ((i < 10) ? "puzzle_easy" : ((i < 20) ? "puzzle_mid"
								: "puzzle_hard")));
			}
		}
	}

	private void showResult(int success) {
		if (this.gameMode.equalsIgnoreCase("Puzzle")) {
			this.puzzleResultLevelLabel.text = "Level: " + this.level;
			if (success == 1) {
				if (this.level < 30) {
					this.puzzleResultBg.image = Global
							.Load("Images\\puzzle_levelclear");
				} else {
					this.puzzleResultBg.image = Global
							.Load("Images\\puzzle_alllevelsclear");
				}
			} else {
				this.puzzleResultBg.image = Global
						.Load("Images\\puzzle_failed");
			}
			if ((success == 1) && (this.level < 30)) {
				this.puzzleResultNextButton.alpha = 0f;
			} else {
				this.puzzleResultNextButton.alpha = 1f;
			}
			if (this.isSound) {
				this.gateSound.Play();
			}
			this.puzzleResultInfo.position2 = new Vector2f(41f, 100f);
			this.puzzleResultInfo.velocity = new Vector2f(0f, 20f);
			this.puzzleResultInfo.isMoving = true;
		} else {
			if (this.gameMode.equalsIgnoreCase("Classic")) {
				if (this.score > this.bestScoreClassic) {
					this.bestScoreClassic = this.score;
				}
				this.resultBestLabel.text = "" + this.bestScoreClassic;
				session.set("bestScoreClassic", this.bestScoreClassic);
			} else {
				if (this.score > this.bestScoreCrazy) {
					this.bestScoreCrazy = this.score;
				}
				this.resultBestLabel.text = "" + this.bestScoreCrazy;
				session.set("bestScoreCrazy", bestScoreCrazy);
			}
			if (success == -1) {
				this.resultInfoBg.image = Global
						.Load("Images\\gameresult_timeover");
			} else {
				this.resultInfoBg.image = Global.Load("Images\\gameresult");
			}
			for (int i = 0; i < 7; i++) {
				this.resultScoreImage[i].image = this.scoreImageR[(this.score / ((int) Math
						.pow(10.0, (double) i))) % 10];
				if ((this.score >= Math.pow(10.0, (double) i)) || (i == 0)) {
					this.resultScoreImage[i].alpha = 1f;
				} else {
					this.resultScoreImage[i].alpha = 0f;
				}
			}
			if (this.isSound) {
				this.gateSound.Play();
			}
			this.resultInfo.position2 = new Vector2f(41f, 100f);
			this.resultInfo.velocity = new Vector2f(0f, 20f);
			this.resultInfo.isMoving = true;
		}
	}

	private void unloadHelpContent() {

	}

	@Override
	public void loadContent() {

		this.animationViews = new ArrayList<AnimationView>();
		this.isSound = session.getBoolean("isSound");
		this.isVib = session.getBoolean("isVib");
		this.bestScoreClassic = session.getInt("bestScoreClassic");
		if (bestScoreClassic == -1) {
			bestScoreClassic = 0;
		}
		this.bestScoreCrazy = session.getInt("bestScoreCrazy");
		if (bestScoreCrazy == -1) {
			bestScoreCrazy = 0;
		}
		this.puzzleUnlockedLevel = session.getInt("puzzleUnlockedLevel");
		if (puzzleUnlockedLevel == -1) {
			puzzleUnlockedLevel = 0;
		}
		this.bombCount = session.getInt("bombCount");
		if (bombCount == -1) {
			bombCount = 0;
		}
		this.lightningCount = session.getInt("lightningCount");
		if (lightningCount == -1) {
			lightningCount = 0;
		}
		this.plasmaCount = session.getInt("plasmaCount");
		if (plasmaCount == -1) {
			plasmaCount = 0;
		}
		this.maxPlasma = session.getInt("maxPlasma");
		if (maxPlasma == -1) {
			maxPlasma = 0;
		}
		this.maxAcc = session.getInt("maxAcc");
		if (maxAcc == -1) {
			maxAcc = 0;
		}

		this.t = 60f;
		this.tTotal = 60;
		this.dt = 0.15f;
		this.level = 12;
		this.score = 0;
		this.acc = 1f;
		this.x = -1;
		this.y = -1;
		this.x2 = -1;
		this.y2 = -1;
		this.startScore = 0;
		this.endScore = 0x4e20;
		this.lastBreakTime = this.t;
		this.isMainTimerRunning = false;
		this.isMoving = false;
		this.selectedList = new ArrayList<String>();
		this.random = new Random();
		this.helpCurrentPage = 0;

		this.mainTopView = new MovingView(new Vector2f(0f, 0f));
		this.mainBottomView = new MovingView(new Vector2f(0f, 276f));
		this.mainTop = new ImageView(new Vector2f(0f, 0f));
		this.mainTop.image = Global.Load("Images\\jewel_main01");
		this.mainTopView.subviews.add(this.mainTop);
		this.mainBottom = new ImageView(new Vector2f(0f, 0f));
		this.mainBottom.image = Global.Load("Images\\jewel_main02");
		this.mainBottomView.subviews.add(this.mainBottom);
		this.resumeButton = new ImageView(new Vector2f(0f, 0f));
		this.resumeButton.image = Global.Load("Images\\bt_resume");
		this.resumeButton.selectedImage = Global.Load("Images\\bt_resume_on");
		this.mainBottomView.subviews.add(this.resumeButton);
		this.classicButton = new ImageView(new Vector2f(0f, 0f));
		this.classicButton.image = Global.Load("Images\\bt_classic");
		this.classicButton.selectedImage = Global.Load("Images\\bt_classic_on");
		this.mainBottomView.subviews.add(this.classicButton);
		this.crazyButton = new ImageView(new Vector2f(0f, 0f));
		this.crazyButton.image = Global.Load("Images\\bt_crazy");
		this.crazyButton.selectedImage = Global.Load("Images\\bt_crazy_on");
		this.mainBottomView.subviews.add(this.crazyButton);
		this.puzzleButton = new ImageView(new Vector2f(0f, 0f));
		this.puzzleButton.image = Global.Load("Images\\bt_puzzle");
		this.puzzleButton.selectedImage = Global.Load("Images\\bt_puzzle_on");
		this.mainBottomView.subviews.add(this.puzzleButton);
		this.classicLeaderButton = new ImageView(new Vector2f(0f, 0f));
		this.classicLeaderButton.image = Global.Load("Images\\bt_classic_gc");
		this.classicLeaderButton.selectedImage = Global
				.Load("Images\\bt_classic_gc_on");
		this.classicLeaderButton.alpha = 0f;
		this.mainBottomView.subviews.add(this.classicLeaderButton);
		this.crazyLeaderButton = new ImageView(new Vector2f(0f, 0f));
		this.crazyLeaderButton.image = Global.Load("Images\\bt_crazy_gc");
		this.crazyLeaderButton.selectedImage = Global
				.Load("Images\\bt_crazy_gc_on");
		this.crazyLeaderButton.alpha = 0f;
		this.mainBottomView.subviews.add(this.crazyLeaderButton);
		this.achievementButton = new ImageView(new Vector2f(0f, 0f));
		this.achievementButton.image = Global.Load("Images\\bt_achieve");
		this.achievementButton.selectedImage = Global
				.Load("Images\\bt_achieve_on");
		this.achievementButton.alpha = 0f;
		this.mainBottomView.subviews.add(this.achievementButton);
		this.soundButton = new ImageView(new Vector2f(107f, 410f));
		this.soundButton.image = Global.Load("Images\\bt_sound");
		this.soundButton.selectedImage = Global.Load("Images\\bt_sound_off");
		this.soundButton.state = this.isSound ? 0 : 1;
		this.mainBottomView.subviews.add(this.soundButton);
		this.vibButton = new ImageView(new Vector2f(207f, 410f));
		this.vibButton.image = Global.Load("Images\\bt_electric");
		this.vibButton.selectedImage = Global.Load("Images\\bt_electric_off");
		this.vibButton.state = this.isVib ? 0 : 1;
		this.mainBottomView.subviews.add(this.vibButton);
		this.helpButton = new ImageView(new Vector2f(307f, 410f));
		this.helpButton.image = Global.Load("Images\\bt_help");
		this.helpButton.selectedImage = Global.Load("Images\\bt_help_off");
		this.mainBottomView.subviews.add(this.helpButton);
		this.gear = new AnimationView(new Vector2f(0f, 456f));
		this.gear.image = Global.Load("Images\\loading_1");
		this.gear.animationImages = new ArrayList<LTexture>();
		for (int j = 0; j < 3; j++) {
			this.gear.animationImages.add(Global.Load("Images\\loading_"
					+ (j + 1)));
		}
		this.light = new AnimationView(new Vector2f(0f, 260f));
		this.light.image = Global.Load("Images\\main_light1");
		this.light.animationImages = new ArrayList<LTexture>();
		for (int k = 0; k < 2; k++) {
			this.light.animationImages.add(Global.Load("Images\\main_light"
					+ (k + 1)));
		}
		this.buttonSound = new SoundEffect();
		this.selectSound = new SoundEffect();
		this.bombSound = new SoundEffect();
		this.lightningSound = new SoundEffect();
		this.plasmaSound = new SoundEffect();
		this.breakSound = new SoundEffect();
		this.gateSound = new SoundEffect();

		LFont font = LFont.getFont(20);
		LFont font2 = LFont.getFont(20);
		LFont font3 = LFont.getFont(20);
		LFont font4 = LFont.getFont(20);
		LFont font5 = LFont.getFont(20);

		this.gameView = new ArrayList<ImageView>();
		for (int i = 0; i < 4; i++) {
			this.bgImageR[i] = Global.Load("Images\\bg_0" + (i + 1));
		}
		for (int j = 0; j < 10; j++) {
			this.scoreImageR[j] = Global.Load("Images\\score_" + j);
		}
		LTexture textured = Global.Load("Images\\back");
		for (int k = 0; k < 7; k++) {
			this.jewelR[k] = Global.Load("Images\\jewel_0" + (k + 1));
		}
		for (int m = 0; m < 8; m++) {
			this.levelImageR[m] = Global.Load("Images\\level_" + (m + 1));
		}
		for (int n = 0; n < 7; n++) {
			this.accImageR[n] = Global.Load("Images\\lcd_0" + (n + 1));
		}
		this.gameBgImageR[0] = Global.Load("Images\\classic_bg");
		this.gameBgImageR[1] = Global.Load("Images\\crazy_bg");
		this.gameBgImageR[2] = Global.Load("Images\\puzzle_bg");
		this.bgImage = new ImageView(new Vector2f(0f, 0f));
		this.bgImage.image = this.bgImageR[this.level % 4];
		this.gameView.add(this.bgImage);
		this.levelImage = new ImageView(new Vector2f(30f, 704f));
		this.levelImage.image = this.levelImageR[0];
		this.gameView.add(this.levelImage);
		this.accImage = new ImageView(new Vector2f(156f, 670f));
		this.accImage.image = this.accImageR[0];
		this.gameView.add(this.accImage);
		this.gameBgImage = new ImageView(new Vector2f(0f, 0f));
		this.gameBgImage.image = this.gameBgImageR[0];
		this.gameView.add(this.gameBgImage);
		this.hintButton = new ImageView(new Vector2f(371f, 691f));
		this.hintButton.image = Global.Load("Images\\bt_hint");
		this.hintButton.selectedImage = Global.Load("Images\\bt_hint_on");
		this.gameView.add(this.hintButton);
		this.menuButton = new ImageView(new Vector2f(30f, 30f));
		this.menuButton.image = Global.Load("Images\\game_pause");
		this.menuButton.selectedImage = Global.Load("Images\\game_pause_on");
		this.gameView.add(this.menuButton);
		this.timeAlarm = new ImageView(new Vector2f(12f, 172f));
		this.timeAlarm.image = Global.Load("Images\\time_bar_4");
		this.timeAlarm.alpha = 0f;
		this.gameView.add(this.timeAlarm);
		for (int num6 = 0; num6 < 3; num6++) {
			this.timeImage[num6] = new ImageView(new RectBox(0, 0, 0, 0));
			this.timeImage[num6].image = Global.Load("Images\\time_bar_"
					+ (num6 + 1));
			this.gameView.add(this.timeImage[num6]);
		}
		for (int num7 = 0; num7 < 7; num7++) {
			this.scoreImage[num7] = new ImageView(new RectBox(
					190 + (0x26 * (6 - num7)), 0x47, 0x20, 0x36));
			this.scoreImage[num7].image = textured;
			this.gameView.add(this.scoreImage[num7]);
			this.scoreImage[num7] = new ImageView(new Vector2f(
					(float) (190 + (0x26 * (6 - num7))), 71f));
			this.scoreImage[num7].image = this.scoreImageR[0];
			this.gameView.add(this.scoreImage[num7]);
		}
		this.levelLabel = new Label(new RectBox(50, 0x2e1, 100, 50));
		this.levelLabel.font = font2;
		this.levelLabel.text = "" + this.level;
		this.levelLabel.alignment = 3;
		this.accLabel = new Label(new RectBox(0xc3, 0x2d8, 100, 50));
		this.accLabel.font = font;
		this.accLabel.text = "x1";
		this.accLabel.alignment = 3;
		this.timeLabel = new Label(new RectBox(190, 0xa9, 100, 20));
		this.timeLabel.font = font3;
		this.timeLabel.text = "1:00";
		this.timeLabel.alignment = 3;
		this.blockFrame = new ImageView(new Vector2f(0f, 0f));
		this.blockFrame.image = Global.Load("Images\\select");
		this.blockFrame.alpha = 0f;
		this.gameView.add(this.blockFrame);
		this.hint = new AnimationView(new Vector2f(0f, 0f));
		this.hint.animationImages = new ArrayList<LTexture>();
		this.hint.animationImages.add(Global.Load("Images\\hint_2"));
		this.hint.animationImages.add(Global.Load("Images\\hint_2_1"));
		this.hint.animationImages.add(Global.Load("Images\\hint_4"));
		this.hint.animationImages.add(Global.Load("Images\\hint_4_1"));
		this.hint.alpha = 0f;
		this.hint.repeatCount = 0;
		this.hint.animationDelay = 5;
		this.animationViews.add(this.hint);
		this.hint2 = new AnimationView(new Vector2f(0f, 0f));
		this.hint2.animationImages = new ArrayList<LTexture>();
		this.hint2.animationImages.add(Global.Load("Images\\hint_1"));
		this.hint2.animationImages.add(Global.Load("Images\\hint_1_1"));
		this.hint2.animationImages.add(Global.Load("Images\\hint_3"));
		this.hint2.animationImages.add(Global.Load("Images\\hint_3_1"));
		this.hint2.alpha = 0f;
		this.hint2.repeatCount = 0;
		this.hint2.animationDelay = 5;
		this.animationViews.add(this.hint2);
		this.blockColorAnimationImages = new ArrayList<LTexture>();
		for (int num8 = 0; num8 < 4; num8++) {
			this.blockColorAnimationImages.add(Global
					.Load("Images\\jewel_color_" + (num8 + 1)));
		}
		this.blockLightningAnimationImages = new ArrayList<LTexture>();
		for (int num9 = 0; num9 < 5; num9++) {
			this.blockLightningAnimationImages.add(Global
					.Load("Images\\jewel_electric_0" + (num9 + 1)));
		}
		this.blockBombAnimationImages = new ArrayList<LTexture>();
		for (int num10 = 0; num10 < 5; num10++) {
			this.blockBombAnimationImages.add(Global
					.Load("Images\\jewel_fire_0" + (num10 + 1)));
		}
		this.bombAnimationImages = new ArrayList<LTexture>();
		for (int num11 = 1; num11 < 15; num11 += 2) {
			this.bombAnimationImages.add(Global
					.Load("Images\\jewel_fire_effect_"
							+ (((num11 + 1) < 10) ? "0" : "") + (num11 + 1)));
		}
		this.lightningAnimationImages = new ArrayList<LTexture>();
		for (int num12 = 1; num12 < 15; num12 += 2) {
			this.lightningAnimationImages.add(Global
					.Load("Images\\jewel_electric_effect_"
							+ (((num12 + 1) < 10) ? "0" : "") + (num12 + 1)));
		}
		for (int num13 = 0; num13 < 7; num13++) {
			this.blockBreakAnimationImages
					.add(num13, new ArrayList<LTexture>());
			for (int num14 = 0; num14 < 7; num14++) {
				this.blockBreakAnimationImages.get(num13).add(
						Global.Load(StringUtils.concat("Images\\jewel_0",
								num13 + 1, "_break_", num14 + 1)));
			}
		}
		for (int num15 = 0; num15 < 8; num15++) {
			this.block[num15] = new Block[8];
			this.blockBreak[num15] = new AnimationView[8];
			for (int num16 = 0; num16 < 8; num16++) {
				this.block[num15][num16] = new Block(new Vector2f(
						(float) (14 + (0x39 * num15)),
						(float) (0xc3 + (0x39 * num16))));
				this.blockBreak[num15][num16] = new AnimationView(new RectBox(
						(14 + (0x39 * num15)) + -61, (0xc3 + (0x39 * num16))
								+ -61, 180, 180));
				this.blockBreak[num15][num16].animationIndex = 0;
				this.blockBreak[num15][num16].isAnimating = false;
				this.blockBreak[num15][num16].repeatCount = 1;
				this.animationViews.add(this.blockBreak[num15][num16]);
			}
		}
		this.levelInfo = new MovingView(new Vector2f(41f, -323f));
		ImageView item = new ImageView(new Vector2f(0f, 0f));
		item.image = Global.Load("Images\\gameclear");
		this.levelInfo.subviews.add(item);
		this.levelInfoLabel = new Label(new RectBox(270, 50, 100, 50));
		this.levelInfoLabel.text = "" + this.level;
		this.levelInfoLabel.alignment = 3;
		this.levelInfoLabel.font = font4;
		this.levelInfoLabel.color = new LColor(0xff, 0xff, 100);
		this.levelInfoEndScore = new Label(new RectBox(200, 0xe1, 120, 40));
		this.levelInfoEndScore.text = "" + this.endScore;
		this.levelInfoEndScore.alignment = 3;
		this.levelInfoEndScore.font = font5;
		this.levelInfoEndScore.color = new LColor(0xff, 0xff, 100);
		int num17 = 7;
		this.levelInfoImage = new ImageView[7];
		for (int num18 = 0; num18 < num17; num18++) {
			this.levelInfoImage[num18] = new ImageView(new Vector2f(
					(float) ((10 + (0x39 * num18)) + ((7 - num17) * 0x12)),
					150f));
			this.levelInfoImage[num18].image = this.jewelR[num18];
			this.levelInfo.subviews.add(this.levelInfoImage[num18]);
		}
		this.resultInfo = new MovingView(new Vector2f(41f, -470f));
		this.resultInfoBg = new ImageView(new Vector2f(0f, 0f));
		this.resultInfoBg.image = Global.Load("Images\\gameresult_timeover");
		this.resultInfo.subviews.add(this.resultInfoBg);
		for (int num19 = 0; num19 < 7; num19++) {
			this.resultScoreImage[num19] = new ImageView(new RectBox(
					80 + (0x26 * (6 - num19)), 0xf5, 0x20, 0x36));
			this.resultScoreImage[num19].image = textured;
			this.resultInfo.subviews.add(this.resultScoreImage[num19]);
			this.resultScoreImage[num19] = new ImageView(new Vector2f(
					(float) (80 + (0x26 * (6 - num19))), 245f));
			this.resultScoreImage[num19].image = this.scoreImageR[0];
			this.resultInfo.subviews.add(this.resultScoreImage[num19]);
		}
		this.resultBestLabel = new Label(new RectBox(250, 0x76, 120, 40));
		this.resultBestLabel.text = "" + this.bestScoreClassic;
		this.resultBestLabel.alignment = 3;
		this.resultBestLabel.font = font4;
		this.resultBestLabel.color = new LColor(0xff, 0xff, 100);
		this.resultNewGameButton = new ImageView(new Vector2f(24f, 343f));
		this.resultNewGameButton.image = Global
				.Load("Images\\result_bt_newgame");
		this.resultNewGameButton.alpha = 0f;
		this.resultInfo.subviews.add(this.resultNewGameButton);
		this.resultGoMainButton = new ImageView(new Vector2f(211f, 343f));
		this.resultGoMainButton.image = Global.Load("Images\\result_bt_gomain");
		this.resultGoMainButton.alpha = 0f;
		this.resultInfo.subviews.add(this.resultGoMainButton);
		this.puzzleResultInfo = new MovingView(new Vector2f(41f, -356f));
		this.puzzleResultBg = new ImageView(new Vector2f(0f, 0f));
		this.puzzleResultBg.image = Global.Load("Images\\puzzle_levelclear");
		this.puzzleResultInfo.subviews.add(this.puzzleResultBg);
		this.puzzleResultLevelLabel = new Label(
				new RectBox(0x63, 0x9b, 200, 50));
		this.puzzleResultLevelLabel.text = "" + this.level;
		this.puzzleResultLevelLabel.alignment = 3;
		this.puzzleResultLevelLabel.font = font4;
		this.puzzleResultLevelLabel.color = new LColor(0xff, 0xff, 100);
		this.puzzleResultListButton = new ImageView(new Vector2f(45f, 239f));
		this.puzzleResultListButton.image = Global
				.Load("Images\\puzzle_bt_list");
		this.puzzleResultListButton.alpha = 0f;
		this.puzzleResultInfo.subviews.add(this.puzzleResultListButton);
		this.puzzleResultAgainButton = new ImageView(new Vector2f(160f, 239f));
		this.puzzleResultAgainButton.image = Global
				.Load("Images\\puzzle_bt_replay");
		this.puzzleResultAgainButton.alpha = 0f;
		this.puzzleResultInfo.subviews.add(this.puzzleResultAgainButton);
		this.puzzleResultNextButton = new ImageView(new Vector2f(275f, 239f));
		this.puzzleResultNextButton.image = Global
				.Load("Images\\puzzle_bt_next");
		this.puzzleResultNextButton.alpha = 0f;
		this.puzzleResultInfo.subviews.add(this.puzzleResultNextButton);
		this.puzzleRetryButton = new ImageView(new Vector2f(313f, 712f));
		this.puzzleRetryButton.image = Global.Load("Images\\bt_restart");
		this.puzzleRetryButton.selectedImage = Global
				.Load("Images\\bt_restart_on");
		this.gameView.add(this.puzzleRetryButton);
		this.puzzleMenuButton = new ImageView(new Vector2f(30f, 30f));
		this.puzzleMenuButton.image = Global.Load("Images\\game_menu");
		this.puzzleMenuButton.selectedImage = Global
				.Load("Images\\game_menu_on");
		this.gameView.add(this.puzzleMenuButton);
		this.puzzleListTopView = new MovingView(new Vector2f(0f, 0f));
		this.puzzleListBottomView = new MovingView(new Vector2f(0f, 276f));
		this.listTop = new ImageView(new Vector2f(0f, 0f));
		this.listTop.image = Global.Load("Images\\puzzle_main01");
		this.puzzleListTopView.subviews.add(this.listTop);
		this.puzzleListMenuButton = new ImageView(new Vector2f(363f, 235f));
		this.puzzleListMenuButton.image = Global.Load("Images\\puzzle_menu");
		this.puzzleListMenuButton.selectedImage = Global
				.Load("Images\\puzzle_menu_on");
		this.puzzleListTopView.subviews.add(this.puzzleListMenuButton);
		this.listBottom = new ImageView(new Vector2f(0f, 0f));
		this.listBottom.image = Global.Load("Images\\jewel_main02");
		this.puzzleListBottomView.subviews.add(this.listBottom);
		this.prevButton = new ImageView(new Vector2f(30f, 404f));
		this.prevButton.image = Global.Load("Images\\puzzle_left");
		this.prevButton.selectedImage = Global.Load("Images\\puzzle_left_on");
		this.puzzleListBottomView.subviews.add(this.prevButton);
		this.nextButton = new ImageView(new Vector2f(343f, 404f));
		this.nextButton.image = Global.Load("Images\\puzzle_right");
		this.nextButton.selectedImage = Global.Load("Images\\puzzle_right_on");
		this.nextButton.state = 1;
		this.puzzleListBottomView.subviews.add(this.nextButton);
		this.puzzleScrollView = new ScrollView(new Vector2f(0f, 80f));
		for (int num20 = 0; num20 < 3; num20++) {
			this.listTitle[num20] = new ImageView(new Vector2f(
					(float) (30 + (480 * num20)), 0f));
			this.listTitle[num20].image = Global
					.Load("Images\\puzzle_main_"
							+ ((num20 == 0) ? "easy" : ((num20 == 1) ? "mid"
									: "hard")));
			this.puzzleScrollView.subviews.add(this.listTitle[num20]);
		}
		for (int num21 = 0; num21 < 30; num21++) {
			this.listButton[num21] = new ImageView(new Vector2f(
					(float) ((20 + (90 * (num21 % 5))) + (480 * (num21 / 10))),
					(float) (50 + (120 * ((num21 / 5) - ((num21 / 10) * 2))))));
			if (num21 > this.puzzleUnlockedLevel) {
				this.listButton[num21].image = Global
						.Load("Images\\puzzle_lock");
				this.listButton[num21].selectedImage = Global
						.Load("Images\\puzzle_lock");
			} else {
				this.listButton[num21].image = Global
						.Load("Images\\"
								+ ((num21 < 10) ? "puzzle_easy"
										: ((num21 < 20) ? "puzzle_mid"
												: "puzzle_hard")));
				this.listButton[num21].selectedImage = Global
						.Load("Images\\"
								+ ((num21 < 10) ? "puzzle_easy"
										: ((num21 < 20) ? "puzzle_mid"
												: "puzzle_hard")));
			}
			this.puzzleScrollView.subviews.add(this.listButton[num21]);
			ImageView view2 = new ImageView(
					new Vector2f(
							(float) (((20 + (90 * (num21 % 5))) + (480 * (num21 / 10))) + 30),
							(float) ((50 + (120 * ((num21 / 5) - ((num21 / 10) * 2)))) + 0x21)));
			view2.image = Global.Load("Images\\puzzle_"
					+ (((num21 + 1) < 10) ? "0" : "") + (num21 + 1));
			this.puzzleScrollView.subviews.add(view2);
		}
		this.helpView = new ScrollView(new Vector2f(0f, 0f));
		for (int num22 = 0; num22 < 6; num22++) {
			this.helpList[num22] = new ImageView(new Vector2f(
					(float) (480 * num22), 0f));
			this.helpView.subviews.add(this.helpList[num22]);
			this.helpMenuButton[num22] = new ImageView(new Vector2f(
					(float) (30 + (480 * num22)), 30f));
			this.helpMenuButton[num22].selectedImage = Global
					.Load("Images\\howtoplay_bt_menu_on");
			this.helpView.subviews.add(this.helpMenuButton[num22]);
		}
		this.helpPrevButton = new ImageView(new Vector2f(10f, 710f));
		this.helpView.subviews.add(this.helpPrevButton);
		this.helpNextButton = new ImageView(new Vector2f(363f, 710f));
		this.helpNextButton.state = 1;
		this.helpView.subviews.add(this.helpNextButton);
		this.currentView = "Main";
		this.showMain();

	}

	@Override
	public void unloadContent() {
		session.save();
	}

	@Override
	public void pressed(LTouch e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void released(LTouch e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void move(LTouch e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drag(LTouch e) {
		// TODO Auto-generated method stub

	}

	ActionKey KeyValue = new ActionKey(ActionKey.DETECT_INITIAL_PRESS_ONLY);

	@Override
	public void pressed(LKey e) {
		if (e.getKeyCode() == Key.BACK) {
			KeyValue.press();
		}

	}

	@Override
	public void released(LKey e) {
		if (e.getKeyCode() == Key.BACK) {
			KeyValue.release();
		}

	}

	@Override
	public void update(GameTime gameTime) {
		if (Key.isKeyPressed(Key.BACK) && KeyValue.isPressed()) {
			if (this.currentView.equalsIgnoreCase("Intro")) {
				LSystem.exit();
			} else if (this.currentView.equalsIgnoreCase("Main")) {
				LSystem.exit();
			} else if (this.currentView.equalsIgnoreCase("Game")) {
				if (this.gameMode.equalsIgnoreCase("Puzzle")) {
					this.currentView = "PuzzleList";
					this.isResumable = true;
					this.showPuzzleList();
				} else {
					this.currentView = "Main";
					this.isResumable = true;
					this.showMain();
				}
			} else if (this.currentView.equalsIgnoreCase("PuzzleList")) {
				this.currentView = "Main";
				this.showMain();
			} else if (this.currentView.equalsIgnoreCase("Help")) {
				this.currentView = "Main";
			}
		}
		if (this.currentView.equalsIgnoreCase("Game")) {
			this.mainTimer(gameTime);
			for (int i = 0; i < 8; i++) {
				for (int j = 0; j < 8; j++) {
					this.block[i][j].transition(this.dt);
				}
			}
			this.levelInfo.transition(this.dt);
			this.resultInfo.transition(this.dt);
			this.puzzleResultInfo.transition(this.dt);
			if (this.levelInfo.position.y == 100f) {
				this.levelInfo.position2 = new Vector2f(41f, -323f);
				this.levelInfo.velocity = new Vector2f(0f, -10f);
				this.levelInfo.isMoving = true;
			}
		}
		if (this.currentView.equalsIgnoreCase("Help")) {
			this.helpView.transition(this.dt);
		}
		if (this.currentView.equalsIgnoreCase("PuzzleList")) {
			this.puzzleScrollView.transition(this.dt);
		}
		if (((this.currentView.equalsIgnoreCase("Main")) || (this.currentView
				.equalsIgnoreCase("Game")))
				|| (this.currentView.equalsIgnoreCase("PuzzleList"))) {
			this.mainTopView.transition(this.dt);
			this.mainBottomView.transition(this.dt);
		}
		LTouchCollection state = LInputFactory.getTouchState();
		if (!this.currentView.equalsIgnoreCase("Intro")) {
			if (this.currentView.equalsIgnoreCase("Main")) {
				for (LTouchLocation location : state) {
					if (location.isDown()) {
						if (this.isPositionInImageView(location.getPosition()
								.sub(this.mainBottomView.position),
								this.resumeButton)) {
							if (this.isSound) {
								this.buttonSound.Play();
							}
							this.resumeButton.state = 1;
						}
						if (this.isPositionInImageView(location.getPosition()
								.sub(this.mainBottomView.position),
								this.classicButton)) {
							if (this.isSound) {
								this.buttonSound.Play();
							}
							this.classicButton.state = 1;
						}
						if (this.isPositionInImageView(location.getPosition()
								.sub(this.mainBottomView.position),
								this.crazyButton)) {
							if (this.isSound) {
								this.buttonSound.Play();
							}
							this.crazyButton.state = 1;
						}
						if (this.isPositionInImageView(location.getPosition()
								.sub(this.mainBottomView.position),
								this.puzzleButton)) {
							if (this.isSound) {
								this.buttonSound.Play();
							}
							this.puzzleButton.state = 1;
						}
						if (this.isPositionInImageView(location.getPosition()
								.sub(this.mainBottomView.position),
								this.soundButton)) {
							this.isSound = !this.isSound;
							this.soundButton.state = this.isSound ? 0 : 1;
							session.set("isSound", isSound);
							if (this.isSound) {
								this.buttonSound.Play();
							}
						}
						if (this.isPositionInImageView(location.getPosition()
								.sub(this.mainBottomView.position),
								this.vibButton)) {
							if (this.isSound) {
								this.buttonSound.Play();
							}
							this.isVib = !this.isVib;
							this.vibButton.state = this.isVib ? 0 : 1;
							session.set("isVib", isVib);
						}
						if (this.isPositionInImageView(location.getPosition()
								.sub(this.mainBottomView.position),
								this.helpButton)) {
							if (this.isSound) {
								this.buttonSound.Play();
							}
							this.helpButton.state = 1;
						}
					}
					if (location.isUp()) {
						if ((this.resumeButton.state == 1)
								&& this.isPositionInImageView(
										location.getPosition().sub(
												this.mainBottomView.position),
										this.resumeButton)) {
							this.currentView = "Game";
							this.hideMain();
						}
						if ((this.classicButton.state == 1)
								&& this.isPositionInImageView(
										location.getPosition().sub(
												this.mainBottomView.position),
										this.classicButton)) {
							this.gameMode = "Classic";
							this.currentView = "Game";
							this.isResumable = false;
							this.newGame();
							this.hideMain();
						}
						if ((this.crazyButton.state == 1)
								&& this.isPositionInImageView(
										location.getPosition().sub(
												this.mainBottomView.position),
										this.crazyButton)) {
							this.gameMode = "Crazy";
							this.currentView = "Game";
							this.isResumable = false;
							this.newGame();
							this.hideMain();
						}
						if ((this.puzzleButton.state == 1)
								&& this.isPositionInImageView(
										location.getPosition().sub(
												this.mainBottomView.position),
										this.puzzleButton)) {
							this.gameMode = "Puzzle";
							this.currentView = "PuzzleList";
							this.isResumable = false;
							this.hideMain();
						}
						if ((this.helpButton.state == 1)
								&& this.isPositionInImageView(
										location.getPosition().sub(
												this.mainBottomView.position),
										this.helpButton)) {
							this.loadHelpContent();
							this.currentView = "Help";
						}
						this.resumeButton.state = 0;
						this.classicButton.state = 0;
						this.crazyButton.state = 0;
						this.puzzleButton.state = 0;
						this.helpButton.state = 0;
					}
				}
			} else if (this.currentView.equalsIgnoreCase("Game")) {
				for (LTouchLocation location2 : state) {
					if (location2.isDown()) {
						if (!(this.gameMode.equalsIgnoreCase("Puzzle"))
								&& this.isPositionInImageView(
										location2.getPosition(),
										this.menuButton)) {
							if (this.isSound) {
								this.buttonSound.Play();
							}
							this.menuButton.state = 1;
						}
						if ((this.gameMode.equalsIgnoreCase("Classic"))
								&& this.isPositionInImageView(
										location2.getPosition(),
										this.hintButton)) {
							this.hintButton.state = 1;
						}
						if ((this.gameMode.equalsIgnoreCase("Puzzle"))
								&& this.isPositionInImageView(
										location2.getPosition(),
										this.puzzleMenuButton)) {
							if (this.isSound) {
								this.buttonSound.Play();
							}
							this.puzzleMenuButton.state = 1;
						}
						if ((this.gameMode.equalsIgnoreCase("Puzzle"))
								&& this.isPositionInImageView(
										location2.getPosition(),
										this.puzzleRetryButton)) {
							if (this.isSound) {
								this.buttonSound.Play();
							}
							this.puzzleRetryButton.state = 1;
						}
						if (this.isPositionInImageView(location2.getPosition()
								.sub(this.resultInfo.position),
								this.resultNewGameButton)) {
							if (this.isSound) {
								this.buttonSound.Play();
							}
							this.resultNewGameButton.alpha = 1f;
						}
						if (this.isPositionInImageView(location2.getPosition()
								.sub(this.resultInfo.position),
								this.resultGoMainButton)) {
							if (this.isSound) {
								this.buttonSound.Play();
							}
							this.resultGoMainButton.alpha = 1f;
						}
						if (this.isPositionInImageView(location2.getPosition()
								.sub(this.puzzleResultInfo.position),
								this.puzzleResultListButton)) {
							if (this.isSound) {
								this.buttonSound.Play();
							}
							this.puzzleResultListButton.alpha = 1f;
						}
						if (this.isPositionInImageView(location2.getPosition()
								.sub(this.puzzleResultInfo.position),
								this.puzzleResultAgainButton)) {
							if (this.isSound) {
								this.buttonSound.Play();
							}
							this.puzzleResultAgainButton.alpha = 1f;
						}
						if (this.isPositionInImageView(location2.getPosition()
								.sub(this.puzzleResultInfo.position),
								this.puzzleResultNextButton)) {
							if (this.isSound) {
								this.buttonSound.Play();
							}
							this.puzzleResultNextButton.alpha = 1f;
						}
					}
					if (location2.isUp()) {
						if ((!(this.gameMode.equalsIgnoreCase("Puzzle")) && (this.menuButton.state == 1))
								&& this.isPositionInImageView(
										location2.getPosition(),
										this.menuButton)) {
							this.currentView = "Main";
							this.isResumable = true;
							this.showMain();
						}
						if (((this.gameMode.equalsIgnoreCase("Classic")) && (this.hintButton.state == 1))
								&& this.isPositionInImageView(
										location2.getPosition(),
										this.hintButton)) {
							this.doHint();
						}
						if (((this.gameMode.equalsIgnoreCase("Puzzle")) && (this.puzzleMenuButton.state == 1))
								&& this.isPositionInImageView(
										location2.getPosition(),
										this.puzzleMenuButton)) {
							this.puzzleResultInfo.position2.set(41f, -356f);
							this.puzzleResultInfo.velocity.set(0f, -20f);
							this.puzzleResultInfo.isMoving = true;
							this.currentView = "PuzzleList";
							this.showPuzzleList();
						}
						if (((this.gameMode.equalsIgnoreCase("Puzzle")) && (this.puzzleRetryButton.state == 1))
								&& this.isPositionInImageView(
										location2.getPosition(),
										this.puzzleRetryButton)) {
							this.puzzleResultInfo.position2.set(41f, -356f);
							this.puzzleResultInfo.velocity.set(0f, -20f);
							this.puzzleResultInfo.isMoving = true;
							this.level--;
							this.goNextLevel();
						}
						if ((this.resultNewGameButton.alpha == 1f)
								&& this.isPositionInImageView(
										location2.getPosition().sub(
												this.resultInfo.position),
										this.resultNewGameButton)) {
							this.resultInfo.position2.set(41f, -470f);
							this.resultInfo.velocity.set(0f, -20f);
							this.resultInfo.isMoving = true;
							this.newGame();
						}
						if ((this.resultGoMainButton.alpha == 1f)
								&& this.isPositionInImageView(
										location2.getPosition().sub(
												this.resultInfo.position),
										this.resultGoMainButton)) {
							this.resultInfo.position2.set(41f, -470f);
							this.resultInfo.velocity.set(0f, -20f);
							this.resultInfo.isMoving = true;
							this.currentView = "Main";
							this.isResumable = false;
							this.showMain();
						}
						if ((this.puzzleResultListButton.alpha == 1f)
								&& this.isPositionInImageView(
										location2.getPosition().sub(
												this.puzzleResultInfo.position),
										this.puzzleResultListButton)) {
							this.puzzleResultInfo.position2.set(41f, -356f);
							this.puzzleResultInfo.velocity.set(0f, -20f);
							this.puzzleResultInfo.isMoving = true;
							this.currentView = "PuzzleList";
							this.showPuzzleList();
						}
						if ((this.puzzleResultAgainButton.alpha == 1f)
								&& this.isPositionInImageView(
										location2.getPosition().sub(
												this.puzzleResultInfo.position),
										this.puzzleResultAgainButton)) {
							this.puzzleResultInfo.position2.set(41f, -356f);
							this.puzzleResultInfo.velocity.set(0f, -20f);
							this.puzzleResultInfo.isMoving = true;
							this.level--;
							this.goNextLevel();
						}
						if ((this.puzzleResultNextButton.alpha == 1f)
								&& this.isPositionInImageView(
										location2.getPosition().sub(
												this.puzzleResultInfo.position),
										this.puzzleResultNextButton)) {
							this.puzzleResultInfo.position2.set(41f, -356f);
							this.puzzleResultInfo.velocity.set(0f, -20f);
							this.puzzleResultInfo.isMoving = true;
							this.goNextLevel();
						}
						this.menuButton.state = 0;
						this.hintButton.state = 0;
						this.puzzleMenuButton.state = 0;
						this.puzzleRetryButton.state = 0;
						this.resultNewGameButton.alpha = 0f;
						this.resultGoMainButton.alpha = 0f;
						this.puzzleResultListButton.alpha = 0f;
						this.puzzleResultAgainButton.alpha = 0f;
						this.puzzleResultNextButton.alpha = 0f;
					}
					if (((location2.isDown()) || (location2.isDrag()))
							|| (location2.isUp())) {
						this.x2 = ((int) (location2.getPosition().x - 14f)) / 0x39;
						this.y2 = ((int) (location2.getPosition().y - 195f)) / 0x39;
						if (((location2.getPosition().x < 14f) || (location2
								.getPosition().y < 195f))
								|| ((this.x2 > 7) || (this.y2 > 7))) {
							return;
						}
						this.hint.alpha = 0f;
						this.hint2.alpha = 0f;
						if (((this.block[this.x2][this.y2].state != 0) || (this.block[this.x2][this.y2].alpha == 0))
								|| (!this.isMoving && (location2.isDrag()))) {
							return;
						}
						if ((location2.isDown()) || (location2.isDrag())) {
							this.isMoving = true;
							if ((this.x == -1) && (location2.isDown())) {
								this.x = this.x2;
								this.y = this.y2;
								this.blockFrame.position.set(
										((14 + (this.x * 0x39)) - 3),
										((0xc3 + (this.y * 0x39)) - 3));
								this.blockFrame.alpha = 1f;
								if (this.isSound) {
									this.selectSound.Play();
								}
								continue;
							}
							if (((((this.x2 - this.x) == 0) && ((this.y2 - this.y) == -1)) || (((this.x2 - this.x) == 0) && ((this.y2 - this.y) == 1)))
									|| ((((this.x2 - this.x) == 1) && ((this.y2 - this.y) == 0)) || (((this.x2 - this.x) == -1) && ((this.y2 - this.y) == 0)))) {
								if (location2.isDrag()) {
									this.isMoving = false;
								}
								if ((this.gameMode.equalsIgnoreCase("Puzzle"))
										&& (((this.block[this.x][this.y].color == -1) && (this.block[this.x][this.y].item == 0)) || ((this.block[this.x2][this.y2].color == -1) && (this.block[this.x2][this.y2].item == 0)))) {
									return;
								}
								this.blockFrame.alpha = 0f;
								if ((this.block[this.x][this.y].item == 3)
										&& (this.block[this.x2][this.y2].item == 3)) {
									return;
								}
								if (this.block[this.x][this.y].item == 3) {
									this.block[this.x][this.y].color = this.block[this.x2][this.y2].color + 100;
								}
								if (this.block[this.x2][this.y2].item == 3) {
									this.block[this.x2][this.y2].color = this.block[this.x][this.y].color + 100;
								}
								this.block[this.x][this.y].position2 = new Vector2f(
										(float) (14 + (this.x2 * 0x39)),
										(float) (0xc3 + (this.y2 * 0x39)));
								this.block[this.x][this.y].velocity = new Vector2f(
										((this.x2 - this.x) * 0x39) * 0.2f,
										((this.y2 - this.y) * 0x39) * 0.2f);
								this.block[this.x][this.y].isMoving = true;
								this.block[this.x2][this.y2].position2 = new Vector2f(
										(float) (14 + (this.x * 0x39)),
										(float) (0xc3 + (this.y * 0x39)));
								this.block[this.x2][this.y2].velocity = new Vector2f(
										((this.x - this.x2) * 0x39) * 0.2f,
										((this.y - this.y2) * 0x39) * 0.2f);
								this.block[this.x2][this.y2].isMoving = true;
								if (((((this.x2 - this.x) != 1) || ((this.y2 - this.y) != 0)) && (((this.x2 - this.x) != -1) || ((this.y2 - this.y) != 0)))
										&& ((((this.x2 - this.x) != 0) || ((this.y2 - this.y) != -1)) && ((this.x2 - this.x) == 0))) {
									// int num1 = this.y2 - this.y;
								}
								this.block[this.x][this.y].state = 10;
								this.block[this.x2][this.y2].state = 10;
								this.block[this.x][this.y].delay = 0.3f;
								this.block[this.x2][this.y2].delay = 0.3f;
								Block block = this.block[this.x][this.y];
								this.block[this.x][this.y] = this.block[this.x2][this.y2];
								this.block[this.x2][this.y2] = block;
								String item = StringUtils.concat(this.x,
										this.y, this.x2, this.y2);
								this.selectedList.add(item);
								this.x = -1;
								this.y = -1;
								continue;
							}
							if ((((this.x2 != this.x) || (this.y2 != this.y)) || (!location2
									.isDown())) && (location2.isDown())) {
								this.blockFrame.alpha = 0f;
								this.x = this.x2;
								this.y = this.y2;
								this.blockFrame.position.set(
										((14 + (this.x * 0x39)) - 3),
										((0xc3 + (this.y * 0x39)) - 3));
								this.blockFrame.alpha = 1f;
							}
						}
					}
				}
			} else if (this.currentView.equalsIgnoreCase("PuzzleList")) {
				for (LTouchLocation location3 : state) {
					if (location3.isDown()) {
						if (this.isPositionInImageView(location3.getPosition()
								.sub(this.puzzleListBottomView.position),
								this.prevButton)) {
							if (this.isSound) {
								this.buttonSound.Play();
							}
							this.prevButton.state = 1;
						}
						if (this.isPositionInImageView(location3.getPosition()
								.sub(this.puzzleListBottomView.position),
								this.nextButton)) {
							if (this.isSound) {
								this.buttonSound.Play();
							}
							this.nextButton.state = 1;
						}
						if (this.isPositionInImageView(location3.getPosition()
								.sub(this.puzzleListTopView.position),
								this.puzzleListMenuButton)) {
							if (this.isSound) {
								this.buttonSound.Play();
							}
							this.puzzleListMenuButton.state = 1;
						}
						this.puzzleScrollView.touchX = (int) location3
								.getPosition().x;
						this.puzzleScrollView.touchX2 = (int) location3
								.getPosition().x;
					}
					if (location3.isDrag()) {
						this.puzzleScrollView.touchX2 = (int) location3
								.getPosition().x;
						this.puzzleScrollView.offset
								.set((float) (((480 * this.puzzleCurrentPage) + this.puzzleScrollView.touchX) - this.puzzleScrollView.touchX2),
										0f);
					}
					if (location3.isUp()) {
						if ((this.prevButton.state == 1)
								&& this.isPositionInImageView(
										location3
												.getPosition()
												.sub(this.puzzleListBottomView.position),
										this.prevButton)) {
							this.puzzleCurrentPage--;
							if (this.puzzleCurrentPage < 0) {
								this.puzzleCurrentPage = 0;
							}
							this.puzzleScrollView.offset2.set(
									(float) (this.puzzleCurrentPage * 480), 0f);
							this.puzzleScrollView.velocity.set(-50f, 0f);
							this.puzzleScrollView.isMoving = true;
							this.setPuzzleList();
						}
						if ((this.nextButton.state == 1)
								&& this.isPositionInImageView(
										location3
												.getPosition()
												.sub(this.puzzleListBottomView.position),
										this.nextButton)) {
							this.puzzleCurrentPage++;
							if (this.puzzleCurrentPage > 2) {
								this.puzzleCurrentPage = 2;
							}
							this.puzzleScrollView.offset2.set(
									(float) (this.puzzleCurrentPage * 480), 0f);
							this.puzzleScrollView.velocity.set(50f, 0f);
							this.puzzleScrollView.isMoving = true;
							this.setPuzzleList();
						}
						if ((this.puzzleListMenuButton.state == 1)
								&& this.isPositionInImageView(
										location3
												.getPosition()
												.sub(this.puzzleListTopView.position),
										this.puzzleListMenuButton)) {
							this.currentView = "Main";
							this.showMain();
						}
						this.puzzleListMenuButton.state = 0;
						if (this.puzzleScrollView.touchX < this.puzzleScrollView.touchX2) {
							this.puzzleCurrentPage--;
							if (this.puzzleCurrentPage < 0) {
								this.puzzleCurrentPage = 0;
							}
							this.puzzleScrollView.offset2.set(
									(float) (this.puzzleCurrentPage * 480), 0f);
							this.puzzleScrollView.velocity.set(-50f, 0f);
							this.puzzleScrollView.isMoving = true;
							this.setPuzzleList();
						} else if (this.puzzleScrollView.touchX > this.puzzleScrollView.touchX2) {
							this.puzzleCurrentPage++;
							if (this.puzzleCurrentPage > 2) {
								this.puzzleCurrentPage = 2;
							}
							this.puzzleScrollView.offset2.set(
									(float) (this.puzzleCurrentPage * 480), 0f);
							this.puzzleScrollView.velocity.set(50f, 0f);
							this.puzzleScrollView.isMoving = true;
							this.setPuzzleList();
						}
						if (!this.puzzleScrollView.isMoving) {
							for (int k = 0; k < 30; k++) {
								if ((this.puzzleUnlockedLevel >= k)
										&& this.isPositionInImageView(
												(location3.getPosition()
														.sub(this.puzzleListBottomView.position))
														.sub(this.puzzleScrollView.position),
												this.listButton[k])) {
									if (this.isSound) {
										this.buttonSound.Play();
									}
									this.puzzlePlayingLevel = k + 1;
									this.gameMode = "Puzzle";
									this.currentView = "Game";
									this.newGame();
								}
							}
						}
					}
				}
			} else if (this.currentView.equalsIgnoreCase("Puzzle")) {
				/*
				 * for (LTouchLocation location4 : state) { LTouchLocation
				 * state1 = location4; }
				 */
			} else if (this.currentView.equalsIgnoreCase("Help")) {
				for (LTouchLocation location5 : state) {
					if (location5.isDown()) {
						if (this.isPositionInImageView(location5.getPosition(),
								this.helpPrevButton)) {
							if (this.isSound) {
								this.buttonSound.Play();
							}
							this.helpPrevButton.state = 1;
						}
						if (this.isPositionInImageView(location5.getPosition(),
								this.helpNextButton)) {
							if (this.isSound) {
								this.buttonSound.Play();
							}
							this.helpNextButton.state = 1;
						}
						if (this.isPositionInImageView(location5.getPosition(),
								this.helpMenuButton[0])) {
							if (this.isSound) {
								this.buttonSound.Play();
							}
							this.helpMenuButton[this.helpCurrentPage].state = 1;
						}
						this.helpView.touchX = (int) location5.getPosition().x;
						this.helpView.touchX2 = (int) location5.getPosition().x;
					}
					if (location5.isDrag()) {
						this.helpView.touchX2 = (int) location5.getPosition().x;
						this.helpView.offset
								.set((float) (((480 * this.helpCurrentPage) + this.helpView.touchX) - this.helpView.touchX2),
										0f);
					}
					if (location5.isUp()) {
						if ((this.helpPrevButton.state == 1)
								&& this.isPositionInImageView(
										location5.getPosition(),
										this.helpPrevButton)) {
							this.helpCurrentPage--;
							if (this.helpCurrentPage < 0) {
								this.helpCurrentPage = 0;
							}
							this.helpView.offset2.set(
									(this.helpCurrentPage * 480), 0f);
							this.helpView.velocity.set(-75f, 0f);
							this.helpView.isMoving = true;
							this.setHelpList();
						}
						if ((this.helpNextButton.state == 1)
								&& this.isPositionInImageView(
										location5.getPosition(),
										this.helpNextButton)) {
							this.helpCurrentPage++;
							if (this.helpCurrentPage > 5) {
								this.helpCurrentPage = 5;
							}
							this.helpView.offset2.set(
									(this.helpCurrentPage * 480), 0f);
							this.helpView.velocity.set(75f, 0f);
							this.helpView.isMoving = true;
							this.setHelpList();
						}
						if ((this.helpMenuButton[this.helpCurrentPage].state == 1)
								&& this.isPositionInImageView(
										location5.getPosition(),
										this.helpMenuButton[0])) {
							this.currentView = "Main";
							this.unloadHelpContent();
						}
						this.helpMenuButton[this.helpCurrentPage].state = 0;
						if (this.helpView.touchX < this.helpView.touchX2) {
							this.helpCurrentPage--;
							if (this.helpCurrentPage < 0) {
								this.helpCurrentPage = 0;
							}
							this.helpView.offset2.set(
									(float) (this.helpCurrentPage * 480), 0f);
							this.helpView.velocity.set(-75f, 0f);
							this.helpView.isMoving = true;
							this.setHelpList();
							continue;
						}
						if (this.helpView.touchX > this.helpView.touchX2) {
							this.helpCurrentPage++;
							if (this.helpCurrentPage > 5) {
								this.helpCurrentPage = 5;
							}
							this.helpView.offset2.set(
									(float) (this.helpCurrentPage * 480), 0f);
							this.helpView.velocity.set(75f, 0f);
							this.helpView.isMoving = true;
							this.setHelpList();
						}
					}
				}
			}
		}

	}

}