package org.test;

public class Brain {
	private WuziBoard bd;
	private int INFINITY = 1000000;
	private int movex, movey;
	private int level;// 深度
	private int node;// 每层结点

	public Brain(WuziBoard bd, int level, int node) {
		this.bd = bd;
		this.level = level;
		this.node = node;
	}

	// 估值函数
	public int[] findOneBestStep() {
		Chess[] arr = bd.getSortedChess(bd.getPlayer());
		Chess c = bd.getData()[arr[0].x][arr[0].y];
		int[] result = { c.x, c.y };
		return result;
	}

	// 估值函数+搜索树
	public int[] findTreeBestStep() {
		alpha_beta(0, bd, -INFINITY, INFINITY);
		int[] result = { movex, movey };
		return result;
	}

	// alpha-beta剪枝搜索算法
	public int alpha_beta(int depth, WuziBoard board, int alpha, int beta) {
		if (depth == level || board.isGameOver() != 0) {
			Chess[] sorted = board.getSorted();
			Chess move = board.getData()[sorted[0].x][sorted[0].y];
			// 搜索树辅助输出
			Log.debug("\t- " + "【" + (char) (64 + move.x)
					+ (16 - move.y) + "】," + move.getSum());
			return move.getSum();// 局面估分
		}
		// 对局面下得分最高的几个点进行拓展
		WuziBoard temp = new WuziBoard(board);
		Chess[] sorted = temp.getSorted();
		int score;
		for (int i = 0; i < node; i++) {
			int x = sorted[i].x;
			int y = sorted[i].y;
			// 搜索树辅助输出
			if (depth >= 1) {
				for (int k = 0; k < depth; k++){
					Log.debug("\t");
				}
			}
			// 走这个走法
			if (!temp.putChess(x, y))
				continue;
			if (sorted[i].getOffense() >= WuziBoard.Level.ALIVE_4.score) {
				Log.debug("我们快要赢啦！");
				score = INFINITY + 1;
			} else if (sorted[i].getDefence() >= WuziBoard.Level.ALIVE_4.score) {
				Log.debug("对方快要赢啦！");
				score = INFINITY;
			} else {
				score = alpha_beta(depth + 1, temp, alpha, beta);
			}
			temp = new WuziBoard(board);// 撤消这个走法

			if (depth % 2 == 0) {// MAX
				if (score > alpha) {
					alpha = score;
					if (depth == 0) {
						movex = x;
						movey = y;
					}
				}
				if (alpha >= beta) {
					score = alpha;
					return score;
				}
			} else {// MIN
				if (score < beta) {
					beta = score;
				}
				if (alpha >= beta) {
					score = beta;
					return score;
				}
			}

		}
		return depth % 2 == 0 ? alpha : beta;
	}

}
