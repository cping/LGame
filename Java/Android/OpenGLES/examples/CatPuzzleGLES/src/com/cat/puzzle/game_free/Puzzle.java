package com.cat.puzzle.game_free;

import java.util.ArrayList;

import loon.core.LSystem;
import loon.core.geom.RectBox;
import loon.core.graphics.BMFont;
import loon.core.graphics.LColor;
import loon.core.graphics.Screen;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;
import loon.core.input.LInputFactory.Key;
import loon.core.input.LKey;
import loon.core.input.LTouch;
import loon.core.timer.LTimerContext;
import loon.utils.MathUtils;

public class Puzzle extends Screen {

	private static final int BOARD_TOP = -816;

	private static final int BOARD_LEFT = 12;

	public static final int SPIN_SCALAR = 2;

	public static final int MAX_PIECE = 6;

	public static final int TILE = 0;

	public static final int REMOVE_MARKER = 1;

	public static final int FALL_MARKER = 2;

	public static final int MATCH_HINT = 3;

	public static final int PLAY = 0;

	public static final int SWAP = 1;

	public static final int REMOVE = 2;

	public static final int FALL = 3;

	public static final int SWAP_BACK = 4;

	public static final int HINT = 5;

	private LTexture bottom;

	private LTexture edge;

	private LTexture[] kitis = new LTexture[9];

	private LTexture logo;

	private LTexture matchesLabel;

	private LTexture scoreLabel;

	private LTexture timeLabel;

	private LTexture kitty;

	private BMFont font;

	private BMFont numbers;

	private boolean starting = true;

	private float ang;

	private int time;

	private int score;

	private int matches;

	private int[][][] tiles = new int[8][20][10];

	private int selectedx;

	private int selectedy;

	private boolean selected;

	private int tappedx;

	private int tappedy;

	private int state;

	private float swapx;

	private float swapy;

	private float removeStep;

	private int fallOffset;

	private long start;

	private RectBox hintRect;

	private RectBox quitRect;

	private int gameOverCounter;

	private int movesLeft;

	public Puzzle() {
		LTexture.ALL_LINEAR = true;
	}

	private String pad(int value, int len) {
		String v = "" + value;

		while (v.length() < len) {
			v = "0" + v;
		}

		return v;
	}

	public void onLoad() {

		try {
			font = new BMFont("assets/puzzle.fnt", "assets/puzzle_00.tga");
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			numbers = new BMFont("assets/numbers.fnt", "assets/numbers_00.tga");
		} catch (Exception e) {
			e.printStackTrace();
		}

		
		bottom = LTextures.loadTexture("assets/bottom.png");
		edge = LTextures.loadTexture("assets/edges.png");
		logo = LTextures.loadTexture("assets/logo.png");
		matchesLabel = LTextures.loadTexture("assets/matches.png");
		scoreLabel = LTextures.loadTexture("assets/score.png");
		timeLabel = LTextures.loadTexture("assets/time.png");
		kitty = LTextures.loadTexture("assets/kitty.png");
		for (int i = 1; i < 10; i++) {
			kitis[i - 1] = LTextures.loadTexture("assets/" + i + ".png");
		}
		kitis[2] = kitis[6];
		kitis[4] = kitis[8];

		hintRect = new RectBox(469, 375, 100, 30);
		quitRect = new RectBox(468, 415, 100, 30);

		clearBoard();
		
		setBackground("assets/background.png");
	}

	ArrayList<Integer> list = new ArrayList<Integer>();

	private ArrayList<Integer> getValidPieces(int x, int y) {
		list.clear();
		for (int i = 0; i < MAX_PIECE; i++) {
			list.add(new Integer(i));
		}
		if (x > 0) {
			list.remove(new Integer(tiles[x - 1][y][TILE]));
		}
		if (x < 7) {
			list.remove(new Integer(tiles[x + 1][y][TILE]));
		}
		if (y > 0) {
			list.remove(new Integer(tiles[x][y - 1][TILE]));
		}
		if (y < 9) {
			list.remove(new Integer(tiles[x][y + 1][TILE]));
		}

		return list;
	}

	private void start() {
		gameOverCounter = 0;

		start = System.currentTimeMillis();
		starting = false;
		matches = 0;
		score = 0;
		time = 0;

		clearBoard();
		fillSpaces();

		state = PLAY;
	}

	private void clearBoard() {
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 20; y++) {
				tiles[x][y][TILE] = -1;
			}
		}
	}

	private void drawNumberString(BMFont font, String str, int y) {
		font.drawString((456 + ((180 - (numbers.getWidth(str))) / 2)), y + 1,
				str);
		font.drawString((455 + ((180 - (numbers.getWidth(str))) / 2)), y, str);
	}

	public void render(GLEx g) {
	}

	private void finishRemove() {
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 20; y++) {
				if (tiles[x][y][REMOVE_MARKER] == 1) {
					tiles[x][y][REMOVE_MARKER] = 0;
					tiles[x][y][TILE] = -1;
				}
			}
		}
	}

	private void fillSpaces() {
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 20; y++) {
				ArrayList<Integer> pieces = getValidPieces(x, y);
				if (tiles[x][y][TILE] == -1) {
					tiles[x][y][TILE] = ((Integer) pieces.get((int) (Math
							.random() * pieces.size()))).intValue();
				}
			}
		}
	}

	private int findFalls() {
		int foundFalls = 0;

		for (int y = 19; y > 0; y--) {
			for (int x = 0; x < 8; x++) {
				tiles[x][y][FALL_MARKER] = 0;

				if ((tiles[x][y][TILE] == -1) && (tiles[x][y - 1][TILE] != -1)) {
					tiles[x][y][TILE] = tiles[x][y - 1][TILE];
					tiles[x][y - 1][TILE] = -1;
					tiles[x][y][FALL_MARKER] = 1;
					foundFalls++;
					fallOffset = 64;
				}
			}
		}

		return foundFalls;
	}

	private int findMoves() {
		int total = 0;

		for (int y = 13; y < 20; y++) {
			for (int x = 0; x < 8; x++) {
				if (x < 7) {
					swap(x, y, x + 1, y);
					int matches = findMatches(false);
					swap(x, y, x + 1, y);

					if (matches != 0) {
						total++;
					}
				}
				if (y < 19) {
					swap(x, y, x, y + 1);
					int matches = findMatches(false);
					swap(x, y, x, y + 1);

					if (matches != 0) {
						total++;
					}
				}
			}
		}

		return total;
	}

	private void hint() {
		if (state == PLAY) {
			int total = findMoves();
			if (total == 0) {
			} else {
				int move = (int) (Math.random() * total);

				for (int y = 13; y < 20; y++) {
					for (int x = 0; x < 8; x++) {
						if (x < 7) {
							swap(x, y, x + 1, y);
							int matches = findMatches(false);
							swap(x, y, x + 1, y);

							if (matches != 0) {
								move--;
								if (move <= 1) {
									tiles[x][y][MATCH_HINT] = 360 * SPIN_SCALAR;
									tiles[x + 1][y][MATCH_HINT] = 360 * SPIN_SCALAR;
									state = HINT;
									return;
								}
							}
						}
						if (y < 19) {
							swap(x, y, x, y + 1);
							int matches = findMatches(false);
							swap(x, y, x, y + 1);

							if (matches != 0) {
								move--;
								if (move <= 1) {
									tiles[x][y][MATCH_HINT] = 360 * SPIN_SCALAR;
									tiles[x][y + 1][MATCH_HINT] = 360 * SPIN_SCALAR;
									state = HINT;
									return;
								}
							}
						}
					}
				}
			}
		}
	}

	private void swap(int x1, int y1, int x2, int y2) {
		int t1 = tiles[x1][y1][TILE];
		tiles[x1][y1][TILE] = tiles[x2][y2][TILE];
		tiles[x2][y2][TILE] = t1;
	}

	private int findMatches() {
		return findMatches(true);
	}

	private int findMatches(boolean updateGameState) {
		int foundMatches = 0;

		for (int y = 19; y > 12; y--) {
			for (int x = 0; x < 8; x++) {
				int ours = tiles[x][y][TILE];
				if (ours != -1) {
					if ((x > 0) && (x < 7)) {
						if ((tiles[x - 1][y][TILE] == ours)
								&& (tiles[x + 1][y][TILE] == ours)) {
							int bonus = 100;
							boolean newMatch = true;

							if (tiles[x - 1][y][REMOVE_MARKER] == 1) {
								newMatch = false;
								bonus *= 2;
							}
							if (tiles[x + 1][y][REMOVE_MARKER] == 1) {
								newMatch = false;
								bonus *= 2;
							}

							foundMatches++;

							if (updateGameState) {
								tiles[x - 1][y][REMOVE_MARKER] = 1;
								tiles[x][y][REMOVE_MARKER] = 1;
								tiles[x + 1][y][REMOVE_MARKER] = 1;

								score += bonus;
								if (newMatch) {
									matches++;
								}
							}
						}
					}

					if ((y > 13) && (y < 19)) {
						if ((tiles[x][y - 1][TILE] == ours)
								&& (tiles[x][y + 1][TILE] == ours)) {
							int bonus = 100;
							boolean newMatch = true;

							if (tiles[x][y - 1][REMOVE_MARKER] == 1) {
								newMatch = false;
								bonus *= 2;
							}
							if (tiles[x][y + 1][REMOVE_MARKER] == 1) {
								newMatch = false;
								bonus *= 2;
							}

							foundMatches++;

							if (updateGameState) {
								tiles[x][y - 1][REMOVE_MARKER] = 1;
								tiles[x][y][REMOVE_MARKER] = 1;
								tiles[x][y + 1][REMOVE_MARKER] = 1;

								score += bonus;
								if (newMatch) {
									matches++;
								}
							}
						}
					}
				}
			}
		}

		return foundMatches;
	}

	private void swap() {
		int xd = tappedx - selectedx;
		int yd = tappedy - selectedy;

		swapx = xd * 54;
		swapy = yd * 64;

		selected = false;

		state = SWAP;
	}

	public void mousePressed(int button, int x, int y) {
		if (starting) {
			start();
		} else {
			if (quitRect.contains(x, y)) {
				starting = true;
				return;
			}
			if (hintRect.contains(x, y)) {
				hint();
				return;
			}
			if (state == PLAY) {
				if (button != 0) {
					selected = false;
					return;
				}

				x -= 11;
				y -= -831;

				x /= 54;
				y /= 64;

				if ((x >= 0) && (x < 8) && (y >= 2) && (y < 20)) {
					if (!selected) {
						selected = true;
						selectedx = x;
						selectedy = y;
					} else {
						int xd = Math.abs(x - selectedx);
						int yd = Math.abs(y - selectedy);

						if ((xd == 0) && (yd == 0)) {
							selected = false;
						} else if (xd + yd == 1) {
							tappedx = x;
							tappedy = y;
							selected = false;
							swap();
						}
					}
				} else {
					selected = false;
				}
			}
		}
	}

	public void draw(GLEx g) {

		if(!isOnLoadComplete()){
			return;
		}
		g.scale(0.8f, 1);
		bottom.draw(2, 450);
		edge.draw(9, 0, edge.getWidth(), 450);
		g.restore();

		matchesLabel.draw(460, 10);
		scoreLabel.draw(480, 130);
		timeLabel.draw(490, 250);
		kitty.draw(535, 340);

		font.drawString(470, 386, "HELP", LColor.black);
		font.drawString(469, 426, "QUIT", LColor.black);

		font.drawString(469, 385, "HELP", LColor.white);
		font.drawString(468, 425, "QUIT", LColor.white);

		if (numbers != null) {
			String str = pad(matches, 4);
			drawNumberString(numbers, str, 50);
			str = pad(score, 7);
			drawNumberString(numbers, str, 170);
			str = pad(time / 60, 2) + ":" + pad(time % 60, 2);
			drawNumberString(numbers, str, 290);
		}
		
		for(int i=0;i<kitis.length;i++){
			kitis[i].glBegin();
		}

		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 20; y++) {
				int tile = tiles[x][y][TILE];

				float yofs = 0;
				float xofs = 0;

				if (tile != -1) {
					if (selected) {
						if ((selectedx == x) && (selectedy == y)) {
							yofs = (MathUtils.cos(ang) * 4);
							g.setColor(1f, 1f, 0f, 0.5f);
							g.fillOval(BOARD_LEFT + (x * 54),
									(BOARD_TOP + (y * 64)), 54, 44);
							g.setColor(LColor.black);
							g.drawOval(BOARD_LEFT + (x * 54),
									 (BOARD_TOP + (y * 64)), 54, 44);
							g.resetColor();
						}
					}
					if ((state == SWAP) || (state == SWAP_BACK)) {
						int xbase = swapx < 0 ? -54 : 54;
						int ybase = swapy < 0 ? -64 : 64;

						if ((selectedx == x) && (selectedy == y)) {
							if (swapx != 0) {
								xofs = xbase - swapx;
							}
							if (swapy != 0) {
								yofs = ybase - swapy;
							}
						}
						if ((tappedx == x) && (tappedy == y)) {
							if (swapx != 0) {
								xofs = -(xbase - swapx);
							}
							if (swapy != 0) {
								yofs = -(ybase - swapy);
							}
						}
					}
					float size = 1;
					if (state == REMOVE) {
						if (tiles[x][y][REMOVE_MARKER] == 1) {
							size = removeStep;
							xofs = (54 - (54 * size)) / 2;
							yofs = (64 - (64 * size)) / 2;
						}
					}
					if (state == FALL) {
						if (tiles[x][y][FALL_MARKER] == 1) {
							yofs = -fallOffset;
						}
					}
					if ((state == HINT) && (tiles[x][y][MATCH_HINT] > 0)) {
						int xp = 11 + (x * 54);
						int yp = -831 + (y * 64);
						kitis[tile].draw(xp, yp,tiles[x][y][MATCH_HINT]
								/ SPIN_SCALAR);
					} else {
						kitis[tile].draw( (11 + (x * 54) + xofs),
								(-831 + (y * 64) + yofs),
								 (54 * size),  (64 * size));
					}
				}
			}
		}

		for(int i=0;i<kitis.length;i++){
			kitis[i].glEnd();
		}

		if (starting) {

			g.resetColor();
			g.setColor(0, 0, 0, 0.7f);
			g.fillRect(50, 50, 540, 380);
			g.setColor(0, 0, 0);
			g.drawRect(50, 50, 540, 380);
			g.drawRect(48, 48, 544, 384);
			g.resetColor();
			g.drawRect(49, 49, 542, 382);
			logo.draw(180, 60);

			if (gameOverCounter > 0) {
				g.drawString("Well Done!", 280, 300);
			} else {
				String mes = "这是一款直接山寨(Copy)过来的游戏示例";
				g.drawString(mes,
						(getWidth() - g.getFont().stringWidth(mes)) / 2, 180);
				g.drawString("Click to Start", 255,
						 (385 + (MathUtils.cos(ang) * 8)));
			}
		}

	}

	public void alter(LTimerContext timer) {

		if(!isOnLoadComplete()){
			return;
		}
		long delta = timer.timeSinceLastUpdate;

		time = (int) ((System.currentTimeMillis() - start) / 1000);
		if (starting) {
			time = 0;
		}
		ang += delta * 0.01f;

		if (state == HINT) {
			boolean found = false;

			for (int x = 0; x < 8; x++) {
				for (int y = 0; y < 20; y++) {
					if (tiles[x][y][MATCH_HINT] > 0) {
						tiles[x][y][MATCH_HINT] -= delta;
						found = true;
					}
				}
			}

			if (!found) {
				state = PLAY;
			}
		}
		if (state == REMOVE) {
			removeStep -= delta * 0.005f;
			if (removeStep < 0) {
				removeStep = 0;
				finishRemove();
				int falls = findFalls();
				if (falls == 0) {
					fillSpaces();
					int matches = findMatches();
					if (matches == 0) {
						state = PLAY;
					} else {
						state = REMOVE;
						removeStep = 1;
					}
				} else {
					fallOffset = 64;
					state = FALL;
				}
			}
		}
		if (state == FALL) {
			fallOffset -= delta * 0.5f;
			if (fallOffset <= 0) {
				fallOffset = 0;
				int falls = findFalls();
				if (falls == 0) {
					fillSpaces();
					int matches = findMatches();
					if (matches == 0) {
						state = PLAY;
					} else {
						state = REMOVE;
						removeStep = 1;
					}
				} else {
					fallOffset = 64;
					state = FALL;
				}
			}
		}
		if ((state == SWAP) || (state == SWAP_BACK)) {
			float swapSpeed = 0.17f;

			if (swapx > 0) {
				swapx -= delta * swapSpeed;
				swapx = Math.max(0, swapx);
			}
			if (swapy > 0) {
				swapy -= delta * swapSpeed;
				swapy = Math.max(0, swapy);
			}
			if (swapx < 0) {
				swapx += delta * swapSpeed;
				swapx = Math.min(0, swapx);
			}
			if (swapy < 0) {
				swapy += delta * swapSpeed;
				swapy = Math.min(0, swapy);
			}

			if ((swapx == 0) && (swapy == 0)) {
				int temp = tiles[tappedx][tappedy][TILE];
				tiles[tappedx][tappedy][TILE] = tiles[selectedx][selectedy][TILE];
				tiles[selectedx][selectedy][TILE] = temp;

				if (state == SWAP_BACK) {
					state = PLAY;
				} else {
					int matches = findMatches();
					if (matches == 0) {
						swap();
						state = SWAP_BACK;
					} else {
						removeStep = 1;
						state = REMOVE;
					}
				}
			}
		}

		if (gameOverCounter > 0) {
			gameOverCounter -= delta;
			if (gameOverCounter <= 0) {
				clearBoard();
			}
		}

		if ((!starting) && (state == PLAY)) {
			movesLeft = findMoves();

			if (movesLeft == 0) {
				starting = true;
				gameOverCounter = 2000;
			}
		}

	}

	public void touchDown(LTouch e) {
		mousePressed(e.getButton(), e.x(), e.y());
	}

	public void touchUp(LTouch e) {

	}

	public void touchMove(LTouch e) {

	}

	public void touchDrag(LTouch e) {

	}

	public void onKeyDown(LKey e) {
		if (e.getKeyCode() == Key.ESCAPE) {
			if (starting) {
				LSystem.exit();
			} else {
				starting = true;
			}
		}
	}


}
