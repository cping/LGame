package game.test;

import java.util.LinkedList;

import loon.action.sprite.ISprite;
import loon.action.sprite.Label;
import loon.action.sprite.Picture;
import loon.action.sprite.Sprite;
import loon.action.sprite.Sprites;
import loon.action.sprite.StatusBar;
import loon.core.graphics.LColor;
import loon.core.graphics.LFont;
import loon.core.graphics.Screen;
import loon.core.graphics.component.LMessage;
import loon.core.graphics.component.LPaper;
import loon.core.graphics.component.LSelect;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LTexture;
import loon.core.input.LTouch;
import loon.core.resource.LPKResource;
import loon.core.timer.LTimer;
import loon.core.timer.LTimerContext;

public class LLKScreen extends Screen {

	final private static String SORRY = "抱歉";

	final private static String START_MES = "游戏开始！", SORRY1_MES = SORRY
			+ ", <r刷新/> 在目前使用了。", SORRY2_MES = SORRY + ", <r提示/> 在目前无法使用了。",
			SORRY3_MES = SORRY + ", <r炸弹/> 在目前无法使用了。", EASY_MES = "好的，这非常容易～";

	final private static String WAIT_MES = "预备……", HELP_MES = "我能为你提供什么服务吗？";

	private int bomb_number, refresh_number, tip_number, progress_number;

	private int xBound;

	private int yBound;

	private int pcount;

	private int refreshcount;

	private int bombcount;

	private int tipcount;

	private int sub;

	private LTimer timer, timer1;

	private Level levelInfo;

	private Grid grid[][];

	private Grid nexts;

	private Grid nexte;

	private LinkedList<Grid>[] path;

	private StatusBar progress;

	private Label stage, time;

	private Picture role;

	private LPaper title, over;

	private Grid prev;

	private LMessage mes;

	private LSelect select;

	private Sprite helpRole;

	private boolean wingame, failgame, init, overFlag;

	private int stageNo, count;

	private int offsetX, offsetY;

	public LLKScreen() {

	}

	public LTexture getImage(int i) {
		return images[i];
	}

	private static LTexture images[];

	public void onLoad() {

		LPKResource.CACHE = true;
		images = new LTexture[17];
		for (int i = 0; i < 8; i++) {
			images[i] = new LTexture("assets/" + i + ".jpg");
		}

		final String res = "assets/res.lpk";

		images[8] = LPKResource.openTexture(res, "a0.png");
		images[9] = LPKResource.openTexture(res, "dot.png");
		images[10] = LPKResource.openTexture(res, "background.jpg");
		images[11] = LPKResource.openTexture(res, "role0.png");
		images[12] = LPKResource.openTexture(res, "role1.png");
		images[13] = LPKResource.openTexture(res, "role2.png");
		images[14] = LPKResource.openTexture(res, "win.png");
		images[15] = LPKResource.openTexture(res, "start.png");
		images[16] = LPKResource.openTexture(res, "gameover.png");
		setBackground(getImage(10));
		stage(1);
	}

	private class AnimateThread extends Thread {

		private LinkedList<Grid> v;

		public AnimateThread(LinkedList<Grid> temp) {

			v = temp;
		}

		public void run() {
			Grid prev = null;
			for (int j = 0; j < v.size();) {
				prev = (Grid) v.remove(0);
				prev.setVisible(true);
				v.add(prev);
				j++;
				try {
					sleep(20L);
				} catch (InterruptedException ire) {
				}
			}
			Grid current = prev;
			prev = (Grid) v.remove(0);
			while (!v.isEmpty()) {
				Grid o = (Grid) v.remove(0);
				o.setVisible(false);
				try {
					sleep(20L);
				} catch (InterruptedException ire) {
				}
			}
			prev.setVisible(false);
			current.setVisible(false);
			current.setImage(getImage(9));
			prev.setImage(getImage(9));
			current.setBorder(1);
			prev.setBorder(1);
			if (!findPair()) {
				refreshs();
			}
		}

	}

	@SuppressWarnings("unchecked")
	private void reset() {
		overFlag = false;
		failgame = false;
		if (path == null) {
			path = new LinkedList[3];
		}
		path[0] = new LinkedList<Grid>();
		path[1] = new LinkedList<Grid>();
		path[2] = new LinkedList<Grid>();
		init = false;
		count = 0;
		if (progress != null) {
			progress.set(progress_number);
		}
		initUI();
	}

	/**
	 * 选择游戏关卡
	 * 
	 * @param no
	 */
	private void stage(int no) {
		switch (no) {
		case 1:
			stageNo = 1;
			sub = 4;
			bomb_number = 3;
			refresh_number = 3;
			tip_number = 3;
			progress_number = 3200;
			levelInfo = new Level(4, 4);
			break;
		case 2:
			stageNo = 2;
			sub = 6;
			bomb_number = 2;
			refresh_number = 2;
			tip_number = 2;
			progress_number = 6400;
			levelInfo = new Level(6, 4);
			break;
		case 3:
			stageNo = 3;
			sub = 6;
			bomb_number = 1;
			refresh_number = 1;
			tip_number = 1;
			progress_number = 5400;
			levelInfo = new Level(6, 4);
			break;
		case 4:
			stageNo = 4;
			sub = 6;
			bomb_number = 1;
			refresh_number = 1;
			tip_number = 1;
			progress_number = 10800;
			levelInfo = new Level(6, 5);
			break;
		case 5:
			stageNo = 5;
			sub = 8;
			bomb_number = 1;
			refresh_number = 1;
			tip_number = 1;
			progress_number = 12400;
			levelInfo = new Level(8, 5);
			break;
		default:
			stageNo++;
			sub = 20;
			bomb_number = 1;
			refresh_number = 1;
			tip_number = 1;
			progress_number = 19400;
			levelInfo = new Level(8, 5);
			break;
		}
		reset();
	}

	public void dispose() {

	}

	private void initRole() {
		role = new Picture(getImage(11));
		mes = new LMessage(getImage(14), (getWidth() - 460) / 2,
				getHeight() - 126 - 10) {
			public void doClick() {
				if (!init) {
					if (count == 0) {
						role.setImage(getImage(12));
						setMessage(START_MES);
					} else if (isComplete()) {
						Runnable runnable = new Runnable() {
							public void run() {

								stage = new Label("Stage - " + stageNo, 160, 25);
								stage.setColor(LColor.black);
								stage.setFont(LFont.getFont("Dialog", 1, 20));
								LLKScreen.this.add(stage);
								time = new Label("Time", 270, 25);
								time.setColor(LColor.black);
								time.setFont(LFont.getFont("Dialog", 1, 20));
								LLKScreen.this.add(time);
								setVisible(false);
								role.setVisible(false);
								setVisible(false);
								init = true;
								count = 0;

								progress = new StatusBar(progress_number,
										progress_number, 325, 5, 150, 25);
								progress.setDead(true);
								LLKScreen.this.add(progress);
								if (title == null) {
									title = new LPaper(getImage(15), 55, 55);
								} else {
									title.setLocation(55, 55);
								}
								centerOn(title);
								LLKScreen.this.add(title);
								if (stageNo < 5) {
									if (helpRole == null) {
										helpRole = new Sprite(getImage(8));
										helpRole.setLocation(
												LLKScreen.this.getWidth()
														- helpRole.getWidth()
														- 10,
												LLKScreen.this.getHeight()
														- helpRole.getHeight()
														- 10);
										LLKScreen.this.add(helpRole);
									} else {
										helpRole.setVisible(true);
										LLKScreen.this.add(helpRole);
									}
								} else {
									if (helpRole != null) {
										helpRole.setVisible(false);
									}
								}

							}
						};
						callEvent(runnable);

					}
					count++;
				}

				if (HELP_MES.equalsIgnoreCase(getMessage()) && isComplete()) {
					setVisible(false);
					select = new LSelect(getImage(14),
							(LLKScreen.this.getWidth() - 460) / 2,
							LLKScreen.this.getHeight() - 126 - 10) {
						public void doClick() {

							switch (getResultIndex()) {
							case 0:
								mes.setVisible(true);
								if (refreshcount > 0) {
									mes.setMessage(EASY_MES);
									LLKScreen.this.refreshs();
								} else {
									mes.setMessage(SORRY1_MES);
								}
								LLKScreen.this.remove(this);
								break;
							case 1:
								mes.setVisible(true);
								if (tipcount > 0) {
									mes.setMessage(EASY_MES);
									LLKScreen.this.showNext();
								} else {
									mes.setMessage(SORRY2_MES);
								}
								LLKScreen.this.remove(this);
								break;
							case 2:
								mes.setVisible(true);
								if (bombcount > 0) {
									mes.setMessage(EASY_MES);
									LLKScreen.this.useBomb();
								} else {
									mes.setMessage(SORRY3_MES);
								}
								LLKScreen.this.remove(this);
								break;
							case 3:
								mes.setVisible(true);
								LLKScreen.this.remove(this);
								mes.setVisible(false);
								role.setVisible(false);
								helpRole.setVisible(true);
								if (stage != null) {
									stage.setVisible(true);
								}
								break;
							default:
								break;
							}
						}

					};
					select.setFontColor(LColor.black);
					select.setAlpha(0.8f);
					select.setTopOffset(-5);
					select.setMessage(new String[] { "1.刷新", "2.提示", "3.炸弹",
							"4.取消" });
					LLKScreen.this.add(select);
					return;

				} else if ((EASY_MES.equalsIgnoreCase(getMessage()) || getMessage()
						.startsWith(SORRY)) && isComplete()) {

					mes.setVisible(false);
					role.setVisible(false);
					helpRole.setVisible(true);
					if (stage != null) {
						stage.setVisible(true);
					}
				}
			}
		};
		mes.setMessageLength(20);
		mes.setAlpha(0.8f);
		mes.setFontColor(LColor.black);
		mes.setMessage(WAIT_MES);
		add(role);
		add(mes);

	}

	private void initUI() {

		xBound = levelInfo.getXBound() + 2;
		yBound = levelInfo.getYBound() + 2;

		grid = new Grid[yBound][xBound];
		int count = 0;

		Grid temp[] = new Grid[xBound * yBound];
		for (int y = 0; y < yBound; y++) {
			for (int x = 0; x < xBound; x++) {
				grid[y][x] = new Grid(x, y);
				if (x == 0 || x == xBound - 1 || y == 0 || y == yBound - 1) {
					LTexture img = getImage(count % sub);
					int nx = offsetX + x * img.getWidth();
					int ny = offsetY + y * img.getHeight();

					grid[y][x].setLocation(nx, ny);
					grid[y][x].setImage(getImage(9));
					grid[y][x].setVisible(false);
				} else {
					LTexture img = getImage(count % sub);
					grid[y][x].setImage(img);
					grid[y][x].setBorder(3);
					int nx = offsetX + x * img.getWidth();
					int ny = offsetY + y * img.getHeight();

					grid[y][x].setLocation(nx, ny);
					temp[count] = grid[y][x];
					count++;
				}
				getSprites().add(grid[y][x]);
			}

		}

		shuffle(temp, count);
		wingame = false;
		tipcount = tip_number;
		bombcount = bomb_number;
		refreshcount = refresh_number;
		initRole();
	}

	public void setPaused(boolean p) {
		if (p) {
			getSprites().setVisible(false);
		} else {
			getSprites().setVisible(true);
		}
	}

	public boolean isWait() {
		boolean result = false;
		if (role != null) {
			result = role.isVisible();
		}
		return result;
	}

	private void shuffle(Grid array[], int count) {
		if (wingame) {
			return;
		}
		int number = 0;
		do {
			getSprites().setVisible(false);
			for (int i = 0; i < count; i++) {
				int j = (int) (Math.random() * (double) count);
				int k = (int) (Math.random() * (double) count);
				LTexture temp = array[k].getBitmap();

				array[k].setImage(array[j].getBitmap());
				array[j].setImage(temp);
			}

			getSprites().setVisible(true);
			number++;
			if (number > 5) {
				wingame = true;
				break;
			}
		} while (!findPair());
	}

	public void refreshs() {
		if (wingame || progress.getValue() == 0) {
			return;
		}
		if (progress != null) {
			progress.set(progress_number);
		}
		wingame = false;
		overFlag = false;
		failgame = false;
		init = false;
		Grid temp[] = new Grid[xBound * yBound];
		int count = 0;
		for (int y = 1; y < yBound - 1; y++) {
			for (int x = 1; x < xBound - 1; x++)
				if (grid[y][x].isVisible()) {
					int nx = offsetX + x * grid[y][x].getWidth();
					int ny = offsetY + y * grid[y][x].getHeight();
					grid[y][x].setLocation(nx, ny);
					grid[y][x].setBorder(3);
					temp[count] = grid[y][x];
					count++;
				}

		}
		if (count != 0) {
			refreshcount--;
			shuffle(temp, count);
		} else {
			wingame = true;
		}
	}

	private boolean xdirect(Grid start, Grid end, LinkedList<Grid> path) {
		if (start.getYpos() != end.getYpos())
			return false;
		int direct = 1;
		if (start.getXpos() > end.getXpos()) {
			direct = -1;
		}
		path.clear();
		for (int x = start.getXpos() + direct; x != end.getXpos() && x < xBound
				&& x >= 0; x += direct) {
			if (grid[start.getYpos()][x].isVisible()) {
				return false;
			}
			path.add(grid[start.getYpos()][x]);
		}

		path.add(end);
		return true;
	}

	private boolean ydirect(Grid start, Grid end, LinkedList<Grid> path) {
		if (start.getXpos() != end.getXpos()) {
			return false;
		}
		int direct = 1;
		if (start.getYpos() > end.getYpos()) {
			direct = -1;
		}
		path.clear();
		for (int y = start.getYpos() + direct; y != end.getYpos() && y < yBound
				&& y >= 0; y += direct) {
			if (grid[y][start.getXpos()].isVisible()) {
				return false;
			}
			path.add(grid[y][start.getXpos()]);
		}

		path.add(end);
		return true;
	}

	private int findPath(Grid start, Grid end) {
		if (xdirect(start, end, path[0])) {
			return 1;
		}
		if (ydirect(start, end, path[0])) {
			return 1;
		}
		Grid xy = grid[start.getYpos()][end.getXpos()];
		if (!xy.isVisible() && xdirect(start, xy, path[0])
				&& ydirect(xy, end, path[1])) {
			return 2;
		}
		Grid yx = grid[end.getYpos()][start.getXpos()];
		if (!yx.isVisible() && ydirect(start, yx, path[0])
				&& xdirect(yx, end, path[1])) {
			return 2;
		}
		path[0].clear();
		for (int y = start.getYpos() - 1; y >= 0; y--) {
			xy = grid[y][start.getXpos()];
			yx = grid[y][end.getXpos()];
			if (xy.isVisible()) {
				break;
			}
			path[0].add(xy);
			if (!yx.isVisible() && xdirect(xy, yx, path[1])
					&& ydirect(yx, end, path[2])) {
				return 3;
			}
		}

		path[0].clear();
		for (int y = start.getYpos() + 1; y < yBound; y++) {
			xy = grid[y][start.getXpos()];
			yx = grid[y][end.getXpos()];
			if (xy.isVisible()) {
				break;
			}
			path[0].add(xy);
			if (!yx.isVisible() && xdirect(xy, yx, path[1])
					&& ydirect(yx, end, path[2])) {
				return 3;
			}
		}

		path[0].clear();
		for (int x = start.getXpos() - 1; x >= 0; x--) {
			yx = grid[start.getYpos()][x];
			xy = grid[end.getYpos()][x];
			if (yx.isVisible()) {
				break;
			}
			path[0].add(yx);
			if (!xy.isVisible() && ydirect(yx, xy, path[1])
					&& xdirect(xy, end, path[2])) {
				return 3;
			}
		}

		path[0].clear();
		for (int x = start.getXpos() + 1; x < xBound; x++) {
			yx = grid[start.getYpos()][x];
			xy = grid[end.getYpos()][x];
			if (yx.isVisible()) {
				break;
			}
			path[0].add(yx);
			if (!xy.isVisible() && ydirect(yx, xy, path[1])
					&& xdirect(xy, end, path[2])) {
				return 3;
			}
		}

		return 0;
	}

	private void deletePair(Grid prev, Grid current) {
		LinkedList<Grid> temp = new LinkedList<Grid>();
		temp.add(prev);
		for (int i = 0; i < pcount; i++) {
			temp.addAll(path[i]);
			path[i].clear();
		}

		AnimateThread thread = new AnimateThread(temp);
		thread.setPriority(Thread.NORM_PRIORITY);
		thread.start();

	}

	public void showNext() {
		if (wingame || progress.getValue() == 0 || tipcount == 0) {
			return;
		}
		tipcount--;
		if (nexts != null && nexte != null) {
			nexts.setBorder(2);
			nexte.setBorder(2);
		}
	}

	public void useBomb() {
		if (wingame || progress.getValue() == 0 || bombcount == 0) {
			return;
		}
		bombcount--;
		if (nexts != null && nexte != null) {
			deletePair(nexts, nexte);
		}
	}

	private boolean findPair() {
		nexts = null;
		nexte = null;
		for (int sy = 1; sy < yBound - 1; sy++) {
			for (int sx = 1; sx < xBound - 1; sx++)
				if (grid[sy][sx].isVisible()) {
					for (int ey = sy; ey < yBound - 1; ey++) {
						for (int ex = 1; ex < xBound - 1; ex++)
							if (grid[ey][ex].isVisible()
									&& (ey != sy || ex != sx)
									&& grid[sy][sx].equals(grid[ey][ex])) {
								pcount = findPath(grid[sy][sx], grid[ey][ex]);
								if (pcount != 0) {
									nexts = grid[sy][sx];
									nexte = grid[ey][ex];
									return true;
								}
							}

					}

				}

		}

		return false;
	}

	public void alter(LTimerContext t) {

		if (isWait()) {
			return;
		}
		if (timer1 == null) {
			timer1 = new LTimer(50);
		}
		if (title != null && timer1.action(t.getTimeSinceLastUpdate())) {
			if (title.getY() > 50) {
				title.move_up(8);
				title.validatePosition();
			} else if (title.getAlpha() > 0.2f) {
				title.setAlpha(title.getAlpha() - 0.1f);
			} else {
				title.setVisible(false);
				remove(title);
				title = null;
			}
			return;
		} else if (over != null && timer1.action(t.getTimeSinceLastUpdate())
				&& !overFlag) {
			if (over.getY() < (getHeight() - over.getHeight()) / 2) {
				over.move_down(8);
				over.validatePosition();
			} else if (over.getAlpha() < 1.0f) {
				over.setAlpha(over.getAlpha() + 0.1f);
			} else {
				centerOn(over);
				overFlag = true;
			}

			return;
		}
		if (!wingame) {
			if (timer == null) {
				timer = new LTimer(100);
			}
			if (timer.action(t.getTimeSinceLastUpdate())) {
				if (progress != null) {

					progress.setUpdate(progress.getValue() - (stageNo * 30));
					if (progress.getValue() <= 100 && !failgame) {

						failgame = true;
						getSprites().setVisible(false);

						over = new LPaper(getImage(16), 0, 0) {
							public void doClick() {
								if (getAlpha() >= 1.0 && overFlag) {
									over = null;
									removeAll();
									stage(stageNo);
									getSprites().setVisible(true);
								}
							}
						};
						over.setAlpha(0.1f);
						centerOn(over);
						over.setY(0);
						add(over);
					}
				}

			}

		} else {
			wingame = false;
			removeAll();
			stage(stageNo + 1);
		}
	}

	public void draw(GLEx g) {

	}

	private Grid getGrid(int x, int y) {

		Sprites ss = getSprites();
		if (ss == null) {
			return null;
		}
		ISprite[] s = ss.getSprites();
		for (int i = 0; i < s.length; i++) {
			if (s[i] instanceof Grid) {
				Grid g = (Grid) s[i];
				if (g.getCollisionBox().contains(x, y)) {
					return g;
				}
			}
		}
		return null;
	}

	class Level {

		private int xBound;

		private int yBound;

		public Level() {
			xBound = 8;
			yBound = 6;
		}

		public Level(int x, int y) {
			xBound = x;
			yBound = y;
		}

		public int getXBound() {
			return xBound;
		}

		public int getYBound() {
			return yBound;
		}
	}

	@Override
	public void touchDown(LTouch e) {
		if (!init) {
			return;
		}
		if (failgame) {
			return;
		}
		if (wingame || progress.getValue() == 0) {
			return;
		}
		if (nexte != null && nexts != null) {
			if (helpRole != null) {
				if (!role.isVisible() && helpRole.isVisible()) {
					if (failgame) {
						return;
					}
					if (onClick(helpRole)) {
						if (stage != null) {
							stage.setVisible(false);
						}
						helpRole.setVisible(false);
						role.setImage(getImage(13));
						role.setVisible(true);
						mes.setMessageLength(20);
						mes.setMessage(HELP_MES);
						mes.setVisible(true);
						return;
					}
				}
			}
		}

		Runnable runnable = new Runnable() {
			public void run() {
				Grid current = null;
				try {
					if (prev != null) {
						prev.setBorder(3);
					}

					if (prev == null) {
						prev = getGrid(getTouchX(), getTouchY());
						if (prev != null) {
							prev.setBorder(0);
						}
					} else {
						if (progress.getValue() == 0) {
							return;
						}

						current = getGrid(getTouchX(), getTouchY());
						if (current == prev) {
							return;
						}
						if (current == null) {
							prev = null;
						}
						if (prev == null) {
							return;
						}
						if (current.equals(prev)) {
							if (!findPair()) {
								refreshs();
							}
							pcount = findPath(prev, current);
							if (pcount != 0) {
								deletePair(prev, current);
								prev = null;
								return;
							}
						}
						prev.setBorder(1);
						prev = current;
						prev.setBorder(0);
						if (!findPair()) {
							refreshs();
						}
					}
				} catch (Exception ex) {
					if (prev != null) {
						prev.setBorder(3);
					}
					if (current != null) {
						current.setBorder(3);
					}
				}
			}
		};
		callEvent(runnable);
		return;

	}

	public void touchMove(LTouch e) {

	}

	public void touchUp(LTouch e) {

	}

}
