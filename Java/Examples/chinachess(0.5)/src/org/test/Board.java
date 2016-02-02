package org.test;

import loon.utils.ArrayMap;


public class Board {
	public final int BOARD_WIDTH = 9, BOARD_HEIGHT = 10;
	public ArrayMap pieces;
	public char player = 'r';
	private Piece[][] cells = new Piece[BOARD_HEIGHT][BOARD_WIDTH];

	public boolean isInside(int[] position) {
		return isInside(position[0], position[1]);
	}

	public boolean isInside(int x, int y) {
		return !(x < 0 || x >= BOARD_HEIGHT || y < 0 || y >= BOARD_WIDTH);
	}

	public boolean isEmpty(int[] position) {
		return isEmpty(position[0], position[1]);
	}

	public boolean isEmpty(int x, int y) {
		return isInside(x, y) && cells[x][y] == null;
	}

	public boolean update(Piece piece) {
		int[] pos = piece.position;
		cells[pos[0]][pos[1]] = piece;
		return true;
	}

	public Piece updatePiece(String key, int[] newPos) {
		Piece orig = (Piece) pieces.get(key);
		Piece inNewPos = getPiece(newPos);
		if (inNewPos != null) {
			pieces.remove(inNewPos.key);
		}
		int[] origPos = orig.position;
		cells[origPos[0]][origPos[1]] = null;
		cells[newPos[0]][newPos[1]] = orig;
		orig.position = newPos;
		player = (player == ChessFlag.MY) ? ChessFlag.PC : ChessFlag.MY;
		return inNewPos;
	}

	public boolean backPiece(String key) {
		Piece p = (Piece)pieces.get(key);
		int[] origPos = p.position;
		cells[origPos[0]][origPos[1]] = p;
		return true;
	}

	public Piece getPiece(int[] pos) {
		return getPiece(pos[0], pos[1]);
	}

	public Piece getPiece(int x, int y) {
		return cells[x][y];
	}
}
