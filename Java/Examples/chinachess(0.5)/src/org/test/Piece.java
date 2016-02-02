package org.test;

import loon.utils.CollectionUtils;

public class Piece {

	public String key;
	public char color;
	public char character;
	public char index;
	public int[] position = new int[2];

	public Piece(String name, int[] position) {
		this.key = name;
		this.color = name.charAt(0);
		this.character = name.charAt(1);
		this.index = name.charAt(2);
		this.position = position;
	}

	public Piece cpy() {
		Piece p = new Piece(key, CollectionUtils.copyOf(position));
		return p;
	}

}
