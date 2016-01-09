package org.test;

import loon.utils.ArrayMap.Entry;

public class EvalModel {

	private int[][] values = new int[2][2];

	public int eval(Board board, char player) {
		for (int i = 0; i < board.pieces.size(); i++) {
			Entry entry = board.pieces.getEntry(i);
			if (entry != null) {
				Piece piece = (Piece) entry.getValue();
				int[] reversePosition = new int[] {
						board.BOARD_HEIGHT - 1 - piece.position[0],
						piece.position[1] };
				switch (piece.character) {
				case 'b':
					if (piece.color == 'r')
						values[0][0] += evalPieceValue(0);
					else
						values[1][0] += evalPieceValue(0);
					break;
				case 's':
					if (piece.color == 'r')
						values[0][0] += evalPieceValue(1);
					else
						values[1][0] += evalPieceValue(1);
					break;
				case 'x':
					if (piece.color == 'r')
						values[0][0] += evalPieceValue(2);
					else
						values[1][0] += evalPieceValue(2);
					break;
				case 'm':
					if (piece.color == 'r') {
						values[0][0] += evalPieceValue(3);
						values[0][1] += evalPiecePosition(3, piece.position);
					} else {
						values[1][0] += evalPieceValue(3);
						values[1][1] += evalPiecePosition(3, reversePosition);
					}
					break;
				case 'j':
					if (piece.color == 'r') {
						values[0][0] += evalPieceValue(4);
						values[0][1] += evalPiecePosition(4, piece.position);
					} else {
						values[1][0] += evalPieceValue(4);
						values[1][1] += evalPiecePosition(4, reversePosition);
					}
					break;
				case 'p':
					if (piece.color == 'r') {
						values[0][0] += evalPieceValue(5);
						values[0][1] += evalPiecePosition(5, piece.position);
					} else {
						values[1][0] += evalPieceValue(5);
						values[1][1] += evalPiecePosition(5, reversePosition);
					}
					break;
				case 'z':
					if (piece.color == 'r') {
						values[0][0] += evalPieceValue(6);
						values[0][1] += evalPiecePosition(6, piece.position);
					} else {
						values[1][0] += evalPieceValue(6);
						values[1][1] += evalPiecePosition(6, reversePosition);
					}
					break;
				}
			}
		}
		int sumRed = values[0][0] + values[0][1] * 8, sumBlack = values[1][0]
				+ values[1][1] * 8;
		switch (player) {
		case 'r':
			return sumRed - sumBlack;
		case 'b':
			return sumBlack - sumRed;
		default:
			return -1;
		}
	}

	private int evalPieceValue(int p) {
		int[] pieceValue = new int[] { 1000000, 110, 110, 300, 600, 300, 70 };
		return pieceValue[p];
	}

	private int evalPiecePosition(int p, int[] pos) {
		int[][] pPosition = new int[][] { { 6, 4, 0, -10, -12, -10, 0, 4, 6 },
				{ 2, 2, 0, -4, -14, -4, 0, 2, 2 },
				{ 2, 2, 0, -10, -8, -10, 0, 2, 2 },
				{ 0, 0, -2, 4, 10, 4, -2, 0, 0 },
				{ 0, 0, 0, 2, 8, 2, 0, 0, 0 }, { -2, 0, 4, 2, 6, 2, 4, 0, -2 },
				{ 0, 0, 0, 2, 4, 2, 0, 0, 0 }, { 4, 0, 8, 6, 10, 6, 8, 0, 4 },
				{ 0, 2, 4, 6, 6, 6, 4, 2, 0 }, { 0, 0, 2, 6, 6, 6, 2, 0, 0 } };
		int[][] mPosition = new int[][] { { 4, 8, 16, 12, 4, 12, 16, 8, 4 },
				{ 4, 10, 28, 16, 8, 16, 28, 10, 4 },
				{ 12, 14, 16, 20, 18, 20, 16, 14, 12 },
				{ 8, 24, 18, 24, 20, 24, 18, 24, 8 },
				{ 6, 16, 14, 18, 16, 18, 14, 16, 6 },
				{ 4, 12, 16, 14, 12, 14, 16, 12, 4 },
				{ 2, 6, 8, 6, 10, 6, 8, 6, 2 }, { 4, 2, 8, 8, 4, 8, 8, 2, 4 },
				{ 0, 2, 4, 4, -2, 4, 4, 2, 0 }, { 0, -4, 0, 0, 0, 0, 0, -4, 0 } };
		int[][] jPosition = new int[][] {
				{ 14, 14, 12, 18, 16, 18, 12, 14, 14 },
				{ 16, 20, 18, 24, 26, 24, 18, 20, 16 },
				{ 12, 12, 12, 18, 18, 18, 12, 12, 12 },
				{ 12, 18, 16, 22, 22, 22, 16, 18, 12 },
				{ 12, 14, 12, 18, 18, 18, 12, 14, 12 },
				{ 12, 16, 14, 20, 20, 20, 14, 16, 12 },
				{ 6, 10, 8, 14, 14, 14, 8, 10, 6 },
				{ 4, 8, 6, 14, 12, 14, 6, 8, 4 },
				{ 8, 4, 8, 16, 8, 16, 8, 4, 8 },
				{ -2, 10, 6, 14, 12, 14, 6, 10, -2 } };
		int[][] zPosition = new int[][] { { 0, 3, 6, 9, 12, 9, 6, 3, 0 },
				{ 18, 36, 56, 80, 120, 80, 56, 36, 18 },
				{ 14, 26, 42, 60, 80, 60, 42, 26, 14 },
				{ 10, 20, 30, 34, 40, 34, 30, 20, 10 },
				{ 6, 12, 18, 18, 20, 18, 18, 12, 6 },
				{ 2, 0, 8, 0, 8, 0, 8, 0, 2 },
				{ 0, 0, -2, 0, 4, 0, -2, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0 } };
		if (p == 3) {
			return mPosition[pos[0]][pos[1]];
		}
		if (p == 4) {
			return jPosition[pos[0]][pos[1]];
		}
		if (p == 5) {
			return pPosition[pos[0]][pos[1]];
		}
		if (p == 6) {
			return zPosition[pos[0]][pos[1]];
		}
		return -1;
	}
}
