package d2013.puzzle;

import loon.core.LSystem;
import loon.core.LSystem.ApplicationType;
import loon.core.graphics.LColor;
import loon.core.graphics.Screen;
import loon.core.graphics.opengl.GLEx;
import loon.core.input.LTouch;
import loon.core.timer.LTimerContext;

public class ApoDetectiveChess4k extends Screen {

	private final String[] levels = new String[] {
			"030001" + "400000" + "000020" + "110000" + "000010" + "304012"
					+ "111110",

			"000400" + "210300" + "001001" + "010000" + "031004" + "200000"
					+ "111110",

			"404010" + "010520" + "030002" + "051010" + "100003" + "000000"
					+ "111110",

			"000000" + "010004" + "000101" + "040020" + "010000" + "303021"
					+ "111110",

			"310040" + "000100" + "100000" + "000010" + "000240" + "021300"
					+ "111110",

			"00000302" + "10001000" + "00034000" + "00000001" + "00000410"
					+ "10000000" + "02000000" + "00000000" + "11111000",

			// "02000000"+
			// "00230100"+
			// "00000000"+
			// "00000100"+
			// "00004510"+
			// "04000000"+
			// "00010000"+
			// "00003100"+
			// "11111000",

			"00010500" + "00003002" + "00000000" + "00000020" + "01000010"
					+ "00000000" + "00403100" + "00100000" + "11111000",

			// "00310010"+
			// "04000040"+
			// "10000100"+
			// "00000000"+
			// "00000020"+
			// "20030000"+
			// "00001000"+
			// "00000000"+
			// "11111000",

			"00000000" + "00024300" + "00000000" + "30100000" + "04000000"
					+ "01000000" + "12000100" + "00001000" + "11111000",

			// "00030410"+
			// "00020000"+
			// "02000000"+
			// "00000000"+
			// "00000041"+
			// "05003100"+
			// "00101000"+
			// "00000000"+
			// "11111000",

			"00000030" + "04001100" + "00205000" + "01500010" + "00000200"
					+ "04000010" + "00010100" + "30000010" + "11222000",

			"11011000" + "04401103" + "10600560" + "00000000" + "32151000"
					+ "00010000" + "00000000" + "00200000" + "22222000",

			"00201000" + "00010000" + "10320000" + "01000000" + "10405004"
					+ "00100001" + "51000310" + "00001000" + "22222000",

			"10140002" + "60001000" + "04001200" + "10000300" + "01050600"
					+ "00031500" + "00000011" + "00000010" + "22222000",

			"00000000" + "00030001" + "00400100" + "00004500" + "21110100"
					+ "02010000" + "00003000" + "00500001" + "11222000",

			"01651000" + "01000300" + "00610001" + "00010500" + "00010410"
					+ "01000040" + "00000230" + "02000100" + "22222000",

			"30000001" + "00150000" + "10401601" + "40051000" + "00000000"
					+ "00000101" + "02000000" + "00013610" + "22222000", };

	/**
	 * p[0] == X-Wert Maus p[1] == Y-Wert Maus p[2] == aktuelles Level p[3] ==
	 * aktuelle Moveanzahl p[4] == Maus gedrückt p[5] == Maus losgelassen p[6]
	 * == Spiel gestartet p[7] == Level geschafft Klick
	 */
	private final int[] p = new int[8];

	long lastTime = System.nanoTime();
	long think = 10000000L;

	int width = 8;
	int[][] level = new int[width + 1][width];

	int realWidth = 50;
	int[][] danger = new int[width][width];
	int[] setter = new int[12];

	public void onLoad() {
		lastTime = System.nanoTime();
		think = 10000000L;

		level[0][0] = -1;

	}

	@Override
	public void draw(GLEx g) {

		long now = System.nanoTime();
		long delta = now - lastTime;
		think += delta;

		// Update / think
		// Wenn 10 ms vergangen sind, dann denke nach
		while (think >= 10000000L) {
			think -= 10000000L;

			if (level[0][0] == -1) {
				if (p[2] < 0) {
					p[2] = levels.length - 1;
				}
				if (p[2] >= levels.length) {
					p[2] = 0;
				}
				String l = levels[p[2]];
				width = 8;
				if (l.length() == 42) {
					width = 6;
				}
				realWidth = 400 / width;

				level = new int[width + 1][width];
				for (int y = 0; y < width; y++) {
					for (int x = 0; x < width; x++) {
						level[y][x] = Integer.valueOf(l.substring(
								y * width + x, y * width + x + 1));
					}
				}
				setter = new int[12];
				for (int x = 0; x < 6; x++) {
					setter[x + 6] = setter[x] = Integer.valueOf(l.substring(
							width * width + x, width * width + x + 1));
				}
				danger = new int[width][width];
				p[7] = p[3] = p[5] = 0;
			}
			if (p[5] > 0) {
				if (p[7] > 0) {
					p[2] += 1;
					level[0][0] = -1;
					think += 10000000L;
				} else {
					if ((p[0] > 75) && (p[0] < 135) && (p[1] > 480)
							&& (p[1] < 520)) {
						p[2] -= 1;
						level[0][0] = -1;
						think += 10000000L;
					}
					if ((p[0] > 135) && (p[0] < 275) && (p[1] > 480)
							&& (p[1] < 520)) {
						level[0][0] = -1;
						think += 10000000L;
					}
					if ((p[0] > 275) && (p[0] < 335) && (p[1] > 480)
							&& (p[1] < 520)) {
						p[2] += 1;
						level[0][0] = -1;
						think += 10000000L;
					}
					if ((p[0] > 10) && (p[0] < 410) && (p[1] > 60)
							&& (p[1] < 460)) {
						int levelX = (p[0] - 10) / realWidth;
						int levelY = (p[1] - 60) / realWidth;
						p[3] += 1;
						// Feld darf genommen werden
						if ((level[levelY][levelX] == 1)
								|| (level[levelY][levelX] >= 11)) {
							int value = 0;
							int next = -1;
							if (level[levelY][levelX] >= 11) {
								value = level[levelY][levelX] - 11 + 1;
								setter[value - 1 + 6] += 1;

							}
							while (next < 0) {
								if (value == 6) {
									level[levelY][levelX] = 1;
									next = value;
								} else if ((setter[value] > 0)
										&& (setter[value + 6] > 0)) {
									next = value;
									setter[value + 6] -= 1;
									level[levelY][levelX] = 11 + value;
								}
								value += 1;
							}
							danger = new int[width][width];
							for (int y = 0; y < width; y++) {
								for (int x = 0; x < width; x++) {
									// king
									if (level[y][x] == 11) {
										for (int ky = y - 1; ky < y + 2; ky++) {
											for (int kx = x - 1; kx < x + 2; kx++) {
												if ((kx >= 0)
														&& (ky >= 0)
														&& (kx < width)
														&& (ky < width)
														&& ((kx != x) || (ky != y))) {
													danger[ky][kx] += 1;
												}
											}
										}
									}
									// queen + rook
									if ((level[y][x] == 12)
											|| (level[y][x] == 13)) {
										boolean[] bBreak = new boolean[4];
										for (int ky = 1; ky < width; ky++) {
											if ((y - ky >= 0) && (!bBreak[0])) {
												if (level[y - ky][x] <= 10) {
													danger[y - ky][x] += 1;
												} else {
													bBreak[0] = true;
												}
											}
											if ((y + ky < width)
													&& (!bBreak[1])) {
												if (level[y + ky][x] <= 10) {
													danger[y + ky][x] += 1;
												} else {
													bBreak[1] = true;
												}
											}
											if ((x - ky >= 0) && (!bBreak[2])) {
												if (level[y][x - ky] <= 10) {
													danger[y][x - ky] += 1;
												} else {
													bBreak[2] = true;
												}
											}
											if ((x + ky < width)
													&& (!bBreak[3])) {
												if (level[y][x + ky] <= 10) {
													danger[y][x + ky] += 1;
												} else {
													bBreak[3] = true;
												}
											}
										}
									}
									// queen + bischop
									if ((level[y][x] == 12)
											|| (level[y][x] == 14)) {
										boolean[] bBreak = new boolean[4];
										for (int ky = 1; ky < width; ky++) {
											if ((y - ky >= 0) && (x - ky >= 0)
													&& (!bBreak[0])) {
												if (level[y - ky][x - ky] <= 10) {
													danger[y - ky][x - ky] += 1;
												} else {
													bBreak[0] = true;
												}
											}
											if ((y + ky < width)
													&& (x - ky >= 0)
													&& (!bBreak[1])) {
												if (level[y + ky][x - ky] <= 10) {
													danger[y + ky][x - ky] += 1;
												} else {
													bBreak[1] = true;
												}
											}
											if ((y + ky < width)
													&& (x + ky < width)
													&& (!bBreak[2])) {
												if (level[y + ky][x + ky] <= 10) {
													danger[y + ky][x + ky] += 1;
												} else {
													bBreak[2] = true;
												}
											}
											if ((y - ky >= 0)
													&& (x + ky < width)
													&& (!bBreak[3])) {
												if (level[y - ky][x + ky] <= 10) {
													danger[y - ky][x + ky] += 1;
												} else {
													bBreak[3] = true;
												}
											}
										}
									}
									// KNIGHT
									if (level[y][x] == 15) {
										if (y + 2 < width) {
											if (x - 1 >= 0) {
												danger[y + 2][x - 1] += 1;
											}
											if (x + 1 < width) {
												danger[y + 2][x + 1] += 1;
											}
										}
										if (y - 2 >= 0) {
											if (x - 1 >= 0) {
												danger[y - 2][x - 1] += 1;
											}
											if (x + 1 < width) {
												danger[y - 2][x + 1] += 1;
											}
										}
										if (x + 2 < width) {
											if (y - 1 >= 0) {
												danger[y - 1][x + 2] += 1;
											}
											if (y + 1 < width) {
												danger[y + 1][x + 2] += 1;
											}
										}
										if (x - 2 >= 0) {
											if (y - 1 >= 0) {
												danger[y - 1][x - 2] += 1;
											}
											if (y + 1 < width) {
												danger[y + 1][x - 2] += 1;
											}
										}
									}
								}
							}
							boolean bWin = true;
							for (int i = 0; i < 6; i++) {
								if (setter[i + 6] != 0) {
									bWin = false;
								}
							}
							if (bWin) {
								for (int y = 0; y < width; y++) {
									for (int x = 0; x < width; x++) {
										if ((level[y][x] >= 2)
												&& (level[y][x] < 11)) {
											if (level[y][x] - 2 != danger[y][x]) {
												bWin = false;
											}
										}
									}
								}
								if (bWin) {
									p[7] = 1;
								}
							}
						}
					}
				}
			}

			p[4] = 0;
			p[5] = 0;
		}

		lastTime = now;

		// Renderabschnitt
		// Hintergrund malen
		g.setLineWidth(1);
		g.setColor(LColor.lightGray);
		g.fillRect(0, 0, 500, 520);
		g.setColor(LColor.white);
		for (int y = 0; y < width; y++) {
			for (int x = 0; x < width; x++) {
				if ((x + y) % 2 == 0) {
					g.fillRect(10 + x * realWidth, 60 + y * realWidth,
							realWidth, realWidth);
				}
			}
		}
		g.setColor(LColor.darkGray);
		g.drawRect(75, 480, 260, 39);
		g.drawLine(135, 480, 135, 520);
		g.drawLine(275, 480, 275, 520);
		for (int i = 0; i < width + 1; i++) {
			g.drawLine(10 + i * realWidth, 60, 10 + i * realWidth, 400 + 60);
			g.drawLine(10, 60 + i * realWidth, 400 + 10, 60 + i * realWidth);
		}

		g.setLineWidth(3);
		// g.setFont(g.getFont().deriveFont(30f).deriveFont(1));
		for (int y = 0; y < width; y++) {
			for (int x = 0; x < width; x++) {
				if ((level[y][x] >= 2) && (level[y][x] < 11)) {
					String s = String.valueOf(level[y][x] - 2);
					if (danger[y][x] > level[y][x] - 2) {
						g.setColor(LColor.red);
					}
					if (danger[y][x] == level[y][x] - 2) {
						g.setColor(LColor.green.darker().darker());
					}
					g.drawString(s, 10 + x * realWidth + realWidth / 2
							- g.getFont().stringWidth(s) / 2, 60 + y
							* realWidth + 37);
				}
				g.setColor(LColor.black);
				if ((level[y][x] == 1) || (level[y][x] > 10)) {
					g.drawRect(10 + x * realWidth + 2, 60 + y * realWidth + 2,
							realWidth - 3, realWidth - 3);
				}
				int addX = 10 + x * realWidth + realWidth / 2;
				int addY = 60 + y * realWidth + realWidth / 2 + 3;
				int changeX = 0;
				int changeY = 0;
				// draw King
				if ((level[y][x] == 11)
						|| ((x == 0) && (y == 0) && (setter[0] > 0))) {
					if ((x == 0) && (y == 0) && (setter[0] > 0)) {
						changeX = 400 - x * realWidth + (width - 8) * 2;
						changeY = 0;

						g.drawString(String.valueOf(setter[0 + 6]) + "x", 460,
								60 + realWidth * 1 - 10);
					}
					g.drawRect(addX + changeX - 8, addY + changeY + 8, 16, 8);
					g.drawLine(addX + changeX + 8, addY + changeY + 2, addX
							+ changeX - 8, addY + changeY + 2);
					g.drawLine(addX + changeX - 8, addY + changeY + 2, addX
							+ changeX - 16, addY + changeY + -12);
					g.drawLine(addX + changeX - 16, addY + changeY + -12, addX
							+ changeX + 16, addY + changeY + -12);
					g.drawLine(addX + changeX + 16, addY + changeY + -12, addX
							+ changeX + 8, addY + changeY + 2);
					g.drawLine(addX + changeX - 4, addY + changeY + -17, addX
							+ changeX + 4, addY + changeY + -17);
					g.drawLine(addX + changeX + 0, addY + changeY + -12, addX
							+ changeX + 0, addY + changeY + -20);
					// draw queen
				}
				if ((level[y][x] == 12)
						|| ((x == 0) && (y == 0) && (setter[1] > 0))) {
					if ((x == 0) && (y == 0) && (setter[1] > 0)) {
						changeX = 400 - x * realWidth + (width - 8) * 2;
						changeY = realWidth * 1;

						g.drawString(String.valueOf(setter[1 + 6]) + "x", 460,
								60 + realWidth * 2 - 10);
					}
					g.drawRect(addX + changeX + -8, addY + changeY + 8, 16, 8);
					g.drawLine(addX + changeX + 8, addY + changeY + 2, addX
							+ changeX + -8, addY + changeY + 2);
					g.drawLine(addX + changeX + -8, addY + changeY + 2, addX
							+ changeX + -16, addY + changeY + -10);
					g.drawLine(addX + changeX + -16, addY + changeY + -10, addX
							+ changeX + -6, addY + changeY + -7);
					g.drawLine(addX + changeX + -6, addY + changeY + -7, addX
							+ changeX + 0, addY + changeY + -17);
					g.drawLine(addX + changeX + 0, addY + changeY + -17, addX
							+ changeX + 6, addY + changeY + -7);
					g.drawLine(addX + changeX + 6, addY + changeY + -7, addX
							+ changeX + 16, addY + changeY + -10);
					g.drawLine(addX + changeX + 16, addY + changeY + -10, addX
							+ changeX + 8, addY + changeY + 2);
				}
				// draw rook
				if ((level[y][x] == 13)
						|| ((x == 0) && (y == 0) && (setter[2] > 0))) {
					if ((x == 0) && (y == 0) && (setter[2] > 0)) {
						changeX = 400 - x * realWidth + (width - 8) * 2;
						changeY = realWidth * 2;

						g.drawString(String.valueOf(setter[2 + 6]) + "x", 460,
								60 + realWidth * 3 - 10);
					}
					g.drawRect(addX + changeX + -8, addY + changeY + 8, 16, 8);
					g.drawRect(addX + changeX + -16, addY + changeY + -14, 32,
							16);
					g.drawLine(addX + changeX + -8, addY + changeY + -14, addX
							+ changeX + -8, addY + changeY + -8);
					g.drawLine(addX + changeX + 0, addY + changeY + -14, addX
							+ changeX + 0, addY + changeY + -8);
					g.drawLine(addX + changeX + 8, addY + changeY + -14, addX
							+ changeX + 8, addY + changeY + -8);
				}
				// draw bischop
				if ((level[y][x] == 14)
						|| ((x == 0) && (y == 0) && (setter[3] > 0))) {
					if ((x == 0) && (y == 0) && (setter[3] > 0)) {
						changeX = 400 - x * realWidth + (width - 8) * 2;
						changeY = realWidth * 3;

						g.drawString(String.valueOf(setter[3 + 6]) + "x", 460,
								60 + realWidth * 4 - 10);
					}
					g.drawRect(addX + changeX + -8, addY + changeY + -4, 16, 20);
					g.drawLine(addX + changeX + 0, addY + changeY + -18, addX
							+ changeX + 6, addY + changeY + -12);
					g.drawLine(addX + changeX + 6, addY + changeY + -12, addX
							+ changeX + 0, addY + changeY + -6);
					g.drawLine(addX + changeX + 0, addY + changeY + -6, addX
							+ changeX + -6, addY + changeY + -12);
					g.drawLine(addX + changeX + -6, addY + changeY + -12, addX
							+ changeX + 0, addY + changeY + -18);
				}
				// draw knight
				if ((level[y][x] == 15)
						|| ((x == 0) && (y == 0) && (setter[4] > 0))) {
					if ((x == 0) && (y == 0) && (setter[4] > 0)) {
						changeX = 400 - x * realWidth + (width - 8) * 2;
						changeY = realWidth * 4;

						g.drawString(String.valueOf(setter[4 + 6]) + "x", 460,
								60 + realWidth * 5 - 10);
					}
					g.drawLine(addX + changeX + 14, addY + changeY + 16, addX
							+ changeX + -10, addY + changeY + 16);
					g.drawLine(addX + changeX + -10, addY + changeY + 16, addX
							+ changeX + -1, addY + changeY + -1);
					g.drawLine(addX + changeX + -1, addY + changeY + -1, addX
							+ changeX + -12, addY + changeY + 2);
					g.drawLine(addX + changeX + -12, addY + changeY + 2, addX
							+ changeX + -16, addY + changeY + -6);
					g.drawLine(addX + changeX + -16, addY + changeY + -6, addX
							+ changeX + 1, addY + changeY + -16);
					g.drawLine(addX + changeX + -3, addY + changeY + -20, addX
							+ changeX + 14, addY + changeY + -4);
					g.drawLine(addX + changeX + 14, addY + changeY + -4, addX
							+ changeX + 14, addY + changeY + 16);
					g.fillOval(addX + changeX + -2, addY + changeY + -10, 4, 4);
				}
			}
		}
		g.setColor(LColor.black);
		// g.setFont(g.getFont().deriveFont(20f).deriveFont(1));
		String s = "ApoDetectiveChess4k";
		g.drawString(s, 220 - g.getFont().stringWidth(s) / 2, 40);

		if (p[7] > 0) {
			g.setColor(LColor.lightGray);
			g.fillRect(0, 465, 500, 55);

			g.setColor(LColor.black);
			s = "Congratulation!";
			g.drawString(s, 250 - g.getFont().stringWidth(s) / 2, 490);

			s = "Click to start the next level!";
			g.drawString(s, 250 - g.getFont().stringWidth(s) / 2, 512);
		} else {
			// g.setFont(g.getFont().deriveFont(15f).deriveFont(1));
			s = "level: " + String.valueOf((int) (p[2] + 1)) + " / "
					+ String.valueOf(levels.length);
			g.drawString(s, 10, 40);

			s = "clicks: " + String.valueOf((int) (p[3]));
			g.drawString(s, 410 - g.getFont().stringWidth(s), 40);

			// g.setFont(g.getFont().deriveFont(30f).deriveFont(1));
			if ((p[0] > 135) && (p[0] < 275) && (p[1] > 480) && (p[1] < 520)) {
				g.setColor(LColor.yellow.darker().darker());
			}
			s = "reset";
			g.drawString(s, 210 - g.getFont().stringWidth(s) / 2, 510);
			g.setColor(LColor.black);
			if ((p[0] > 75) && (p[0] < 135) && (p[1] > 480) && (p[1] < 520)) {
				g.setColor(LColor.yellow.darker().darker());
			}
			g.drawLine(95, 500, 115, 490);
			g.drawLine(95, 500, 115, 510);
			g.setColor(LColor.black);
			if ((p[0] > 275) && (p[0] < 335) && (p[1] > 480) && (p[1] < 520)) {
				g.setColor(LColor.yellow.darker().darker());
			}
			g.drawLine(315, 500, 295, 490);
			g.drawLine(315, 500, 295, 510);
		}

	}

	@Override
	public void alter(LTimerContext timer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void touchDown(LTouch e) {
		p[4] = 1;
		p[5] = 0;

	}

	@Override
	public void touchUp(LTouch e) {
		p[4] = 0;
		p[5] = 1;

	}

	@Override
	public void touchMove(LTouch e) {
		if (LSystem.type != ApplicationType.JavaSE) {
			p[0] = e.x();
			p[1] = e.y();
		}

	}

	@Override
	public void touchDrag(LTouch e) {
		p[0] = e.x();
		p[1] = e.y();

	}

}