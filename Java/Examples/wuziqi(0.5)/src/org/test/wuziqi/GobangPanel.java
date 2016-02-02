package org.test.wuziqi;

import loon.LSystem;
import loon.LTexture;
import loon.Screen;
import loon.canvas.Canvas;
import loon.canvas.Image;
import loon.canvas.LColor;
import loon.component.LComponent;
import loon.component.LPanel;
import loon.component.LToast;
import loon.event.ClickListener;
import loon.event.SysTouch;
import loon.font.IFont;
import loon.geom.PointI;
import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.processes.RealtimeProcess;
import loon.utils.timer.LTimerContext;

public class GobangPanel extends LPanel {
	
	private ComputurTask task = null; // 自动任务
	private LToast toast = null; // 提示信息
	private final int OFFSET = 40;// 棋盘偏移
	private final int CELL_WIDTH = 40;// 棋格宽度
	private int computerSide = Chess.BLACK;// 默认机器持黑
	private int humanSide = Chess.WHITE;
	private int cx = WuziBoard.CENTER, cy = WuziBoard.CENTER;
	private boolean isShowOrder = false;// 显示落子顺序
	private int[] lastStep;// 上一个落子点
	private WuziBoard bd;// 棋盘，重要
	private Brain br;// AI，重要
	public static final int MANUAL = 0;// 双人模式
	public static final int HALF = 1;// 人机模式
	public static final int AUTO = 2;// 双机模式
	public static final int EVAL = 3;// 估值函数
	public static final int TREE = 4;// 估值函数+搜索树
	private int mode;// 模式
	private int intel;// 智能
	private boolean isGameOver = true;

	// 显示落子顺序
	public void troggleOrder() {
		isShowOrder = !isShowOrder;
	}

	// 悔棋
	public void undo() {
		PointI p = bd.undo();
		if (p != null) {
			lastStep[0] = p.x;
			lastStep[1] = p.y;
		}
	}

	public GobangPanel() {
		super(0, 0, 650, 650);

		lastStep = new int[2];

		this.setBackground(LColor.orange);

		bd = new WuziBoard();

		this.customRendering = false;

		SetClick(new ClickListener() {

			@Override
			public void UpClick(LComponent comp, float x, float y) {
				// TODO Auto-generated method stub

			}

			@Override
			public void DragClick(LComponent comp, float x, float y) {

			}

			@Override
			public void DownClick(LComponent comp, float x1, float y1) {

				if (isGameOver) {
					showMessage("请开始新游戏！");
					return;
				}
				int x = MathUtils.round((x1 - OFFSET) * 1.0f / CELL_WIDTH) + 1;
				int y = MathUtils.round((y1 - OFFSET) * 1.0f / CELL_WIDTH) + 1;
				if (cx >= 1 && cx <= WuziBoard.BOARD_SIZE && cy >= 1
						&& cy <= WuziBoard.BOARD_SIZE) {
					if (mode == MANUAL) {// 双人

						putChess(x, y);
					} else if (mode == HALF) {// 人机

						if (bd.getPlayer() == humanSide) {

							if (putChess(x, y)) {
								Log.debug("\n----白棋完毕----");
								if (intel == EVAL) {
									int[] bestStep = br.findOneBestStep();// 估值函数AI
									putChess(bestStep[0], bestStep[1]);
								} else if (intel == TREE) {
									int[] bestStep = br.findTreeBestStep();// 估值函数+搜索树AI
									putChess(bestStep[0], bestStep[1]);
								}
								Log.debug("\n----黑棋完毕----");
							}

						}
					}
				}

			}

			@Override
			public void DoClick(LComponent comp) {
				// TODO Auto-generated method stub

			}
		});
	}

	@Override
	public void createUI(GLEx g, int x1, int y1, LComponent component,
			LTexture[] buttonImage) {
		// 保存原始画笔
		g.saveBrush();
		// 画棋盘
		drawBoard(g);
		// 画天元和星
		drawStar(g, WuziBoard.CENTER, WuziBoard.CENTER);
		drawStar(g, (WuziBoard.BOARD_SIZE + 1) / 4,
				(WuziBoard.BOARD_SIZE + 1) / 4);
		drawStar(g, (WuziBoard.BOARD_SIZE + 1) / 4,
				(WuziBoard.BOARD_SIZE + 1) * 3 / 4);
		drawStar(g, (WuziBoard.BOARD_SIZE + 1) * 3 / 4,
				(WuziBoard.BOARD_SIZE + 1) / 4);
		drawStar(g, (WuziBoard.BOARD_SIZE + 1) * 3 / 4,
				(WuziBoard.BOARD_SIZE + 1) * 3 / 4);
		// 画数字和字母
		drawNumAndLetter(g);
		// 画提示框
		drawCell(g, cx, cy, 0);

		if (!isGameOver) {
			// 画所有棋子
			for (int x = 1; x <= WuziBoard.BOARD_SIZE; ++x) {
				for (int y = 1; y <= WuziBoard.BOARD_SIZE; ++y) {
					drawChess(g, x, y, bd.getData()[x][y].getSide());
				}
			}
			// 画顺序
			if (isShowOrder)
				drawOrder(g);
			else {
				if (lastStep[0] > 0 && lastStep[1] > 0) {
					g.setColor(LColor.red);
					g.fillRect((lastStep[0] - 1) * CELL_WIDTH + OFFSET
							- CELL_WIDTH / 10, (lastStep[1] - 1) * CELL_WIDTH
							+ OFFSET - CELL_WIDTH / 10, CELL_WIDTH / 5,
							CELL_WIDTH / 5);

				}
			}
		}
		// 还原画笔
		g.restoreBrush();
	}

	private LTexture boardTexture;

	// 画棋盘
	private void drawBoard(GLEx g) {
		if (boardTexture == null) {
			Image img = Image.createImage(width(), height());
			Canvas cs = img.getCanvas();
			cs.setColor(LColor.black);
			cs.setStrokeWidth(2f);
			for (int x = 0; x < WuziBoard.BOARD_SIZE; ++x) {
				cs.drawLine(x * CELL_WIDTH + OFFSET, OFFSET, x * CELL_WIDTH
						+ OFFSET, (WuziBoard.BOARD_SIZE - 1) * CELL_WIDTH
						+ OFFSET);

			}
			for (int y = 0; y < WuziBoard.BOARD_SIZE; ++y) {
				cs.drawLine(OFFSET, y * CELL_WIDTH + OFFSET,
						(WuziBoard.BOARD_SIZE - 1) * CELL_WIDTH + OFFSET, y
								* CELL_WIDTH + OFFSET);

			}
			boardTexture = img.texture();
			img.close();
		}
		g.draw(boardTexture, 0, 0);
	}

	// 画天元和星
	private void drawStar(GLEx g, int cx, int cy) {
		g.fillOval((cx - 1) * CELL_WIDTH + OFFSET - 4, (cy - 1) * CELL_WIDTH
				+ OFFSET - 4, 8, 8);
	}

	// 画数字和字母
	private void drawNumAndLetter(GLEx g) {
		IFont fm = g.getFont();
		float stringWidth, stringAscent;
		stringAscent = -fm.getAscent();
		for (int i = 1; i <= WuziBoard.BOARD_SIZE; i++) {

			String num = String.valueOf(WuziBoard.BOARD_SIZE - i + 1);
			stringWidth = fm.stringWidth(num);
			g.drawString(String.valueOf(WuziBoard.BOARD_SIZE - i + 1), OFFSET
					/ 4 - stringWidth / 2, OFFSET + (CELL_WIDTH * (i - 1))
					+ stringAscent / 2);

			String letter = String.valueOf((char) (64 + i));
			stringWidth = fm.stringWidth(letter);
			g.drawString(String.valueOf((char) (64 + i)), OFFSET
					+ (CELL_WIDTH * (i - 1)) - stringWidth / 2, OFFSET * 3 / 4
					+ OFFSET + CELL_WIDTH * (WuziBoard.BOARD_SIZE - 1)
					+ stringAscent / 2);
		}
	}

	// 画棋子
	private void drawChess(GLEx g, int cx, int cy, int player) {
		if (player == 0)
			return;
		int size = CELL_WIDTH * 5 / 6;
		g.setColor(player == Chess.BLACK ? LColor.black : LColor.white);
		g.fillOval((cx - 1) * CELL_WIDTH + OFFSET - size / 2, (cy - 1)
				* CELL_WIDTH - size / 2 + OFFSET, size, size);
	}

	// 画预选框
	private void drawCell(GLEx g, int x, int y, int c) {// c 是style
		int length = CELL_WIDTH / 4;
		int xx = (x - 1) * CELL_WIDTH + OFFSET;
		int yy = (y - 1) * CELL_WIDTH + OFFSET;
		int x1, y1, x2, y2, x3, y3, x4, y4;
		x1 = x4 = xx - CELL_WIDTH / 2;
		x2 = x3 = xx + CELL_WIDTH / 2;
		y1 = y2 = yy - CELL_WIDTH / 2;
		y3 = y4 = yy + CELL_WIDTH / 2;
		g.setColor(LColor.red);
		g.drawLine(x1, y1, x1 + length, y1);
		g.drawLine(x1, y1, x1, y1 + length);
		g.drawLine(x2, y2, x2 - length, y2);
		g.drawLine(x2, y2, x2, y2 + length);
		g.drawLine(x3, y3, x3 - length, y3);
		g.drawLine(x3, y3, x3, y3 - length);
		g.drawLine(x4, y4, x4 + length, y4);
		g.drawLine(x4, y4, x4, y4 - length);
	}

	// 画落子顺序
	private void drawOrder(GLEx g) {
		int[][] history = bd.getHistory();
		if (history.length > 0) {
			g.setColor(LColor.red);
			for (int i = 0; i < history.length; i++) {
				int x = history[i][0];
				int y = history[i][1];
				String text = String.valueOf(i + 1);
				// 居中
				IFont fm = g.getFont();
				int stringWidth = fm.stringWidth(text);
				float stringAscent = -fm.getAscent();
				g.drawString(text, (x - 1) * CELL_WIDTH + OFFSET - stringWidth
						/ 2, (y - 1) * CELL_WIDTH + OFFSET + stringAscent / 2);
			}
		}
	}

	// 开始游戏
	public void startGame(int mode, int intel, int level, int node) {
		Screen.removeProcess(task);
		// 清除
		bd.reset();
		isGameOver = true;
		this.mode = mode;
		this.intel = intel;
		bd.reset();
		lastStep[0] = lastStep[1] = WuziBoard.CENTER;
		br = new Brain(bd, level, node);
		bd.start();
		isGameOver = false;
		showMessage("游戏开始！");
		if (mode == AUTO) {// 双机
			task = new ComputurTask();
			task.setDelay(LSystem.SECOND);
			Screen.addProcess(task);
		}

	}

	@Override
	public void update(long t) {
		if (contains(SysTouch.getX(), SysTouch.getY())) {
			int tx = MathUtils.round((SysTouch.getX() - OFFSET) * 1.0f / CELL_WIDTH) + 1;
			int ty = MathUtils.round((SysTouch.getY() - OFFSET) * 1.0f / CELL_WIDTH) + 1;
			if (tx >= 1 && tx <= WuziBoard.BOARD_SIZE && ty >= 1
					&& ty <= WuziBoard.BOARD_SIZE) {
				cx = tx;
				cy = ty;
			}
		}
	}

	private boolean putChess(int x, int y) {
		if (bd.putChess(x, y)) {
			lastStep[0] = x;// 保存上一步落子点
			lastStep[1] = y;
			int winSide = bd.isGameOver();// 判断终局
			if (winSide > 0) {
				if (winSide == humanSide) {
					showMessage("白方赢了！");
				} else if (winSide == computerSide) {
					showMessage("黑方赢了！");
				} else {
					showMessage("双方平手");
				}

				// 清除
				bd.reset();
				isGameOver = true;
				return false;
			}

			return true;
		}
		return false;

	}

	public void showMessage(String mes) {
		if (toast != null) {
			getScreen().remove(toast);
		}
		getScreen().add(toast = LToast.makeText(mes, LToast.Style.ERROR));
	}

	// 双机
	private class ComputurTask extends RealtimeProcess {

		@Override
		public void run(LTimerContext time) {

			int[] bestStep = br.findTreeBestStep();
			if (!putChess(bestStep[0], bestStep[1])) {
				this.kill();
			}

		}

	}
}
