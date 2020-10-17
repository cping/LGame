package org.test;

import loon.LSystem;
import loon.events.Updateable;
import loon.utils.TArray;
import loon.utils.ArrayMap.Entry;

public class SearchModel {
	private static int DEPTH = 2;
	private Board board;
	private GameController controller = new GameController();

	public AlphaBetaNode search(Board board) {
		this.board = board;
		if (board.pieces.size() < 28) {
			DEPTH = 3;
		}
		if (board.pieces.size() < 16) {
			DEPTH = 4;
		}
		if (board.pieces.size() < 6) {
			DEPTH = 5;
		}
		if (board.pieces.size() < 4) {
			DEPTH = 6;
		}
		AlphaBetaNode best = null;
		TArray<AlphaBetaNode> moves = generateMovesForAll(true);
		for (AlphaBetaNode n : moves) {
			Piece eaten = board.updatePiece(n.piece, n.to);
			n.value = alphaBeta(DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE,
					false);
			if (best == null || n.value >= best.value) {
				best = n;
			}
			board.updatePiece(n.piece, n.from);
			if (eaten != null) {
				board.pieces.put(eaten.key, eaten);
				board.backPiece(eaten.key);
			}
		}
		return best;
	}

	private int alphaBeta(int depth, int alpha, int beta, boolean isMax) {
		if (depth == 0 || controller.hasWin(board) != ChessFlag.END) {
			return new EvalModel().eval(board, ChessFlag.PC);
		}
		TArray<AlphaBetaNode> moves = generateMovesForAll(isMax);
		synchronized (this) {
			for (final AlphaBetaNode n : moves) {
				Piece eaten = board.updatePiece(n.piece, n.to);
				final int finalBeta = beta;
				final int finalAlpha = alpha;
				final int finalDepth = depth;
				final int[] temp = new int[1];
				if (depth == 2) {
					if (isMax) {
						Updateable update = new Updateable() {

							@Override
							public void action(Object a) {
								temp[0] = Math.max(
										finalAlpha,
										alphaBeta(finalDepth - 1, finalAlpha,
												finalBeta, false));
							}
						};
						LSystem.load(update);
						alpha = temp[0];
					} else {
						Updateable update = new Updateable() {

							@Override
							public void action(Object a) {

								temp[0] = Math.min(
										finalBeta,
										alphaBeta(finalDepth - 1, finalAlpha,
												finalBeta, true));

							}
						};
						LSystem.load(update);
						beta = temp[0];
					}
				} else {
					if (isMax) {
						alpha = Math.max(alpha,
								alphaBeta(depth - 1, alpha, beta, false));
					} else {
						beta = Math.min(beta,
								alphaBeta(depth - 1, alpha, beta, true));
					}
				}
				board.updatePiece(n.piece, n.from);
				if (eaten != null) {
					board.pieces.put(eaten.key, eaten);
					board.backPiece(eaten.key);
				}
				if (beta <= alpha) {
					break;
				}
			}
		}
		return isMax ? alpha : beta;
	}

	private TArray<AlphaBetaNode> generateMovesForAll(boolean isMax) {
		TArray<AlphaBetaNode> moves = new TArray<AlphaBetaNode>();
		for (int i = 0; i < board.pieces.size(); i++) {
			Entry e = board.pieces.getEntry(i);
			if (e != null) {
				Piece piece = (Piece) e.getValue();
				if (isMax && piece.color == ChessFlag.MY) {
					continue;
				}
				if (!isMax && piece.color == ChessFlag.PC) {
					continue;
				}
				for (int[] nxt : Rules.getNextMove(piece.key, piece.position,
						board)){
					moves.add(new AlphaBetaNode(piece.key, piece.position, nxt));
				}
			}
		}
		return moves;
	}
}