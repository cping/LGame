package org.doudizhu.test;

import loon.LTexture;
import loon.canvas.LColor;
import loon.canvas.Paint;
import loon.canvas.Paint.Style;
import loon.geom.RectF;
import loon.opengl.GLEx;

public class Desk {
	public static int winId = -1;
	LTexture cardImg;
	LTexture redoImage;
	LTexture passImage;
	LTexture chuPaiImage;
	LTexture tiShiImage;
	LTexture farmerImage;
	LTexture landlordImage;

	private int[] scores = new int[3];
	private int[] threeCards = new int[3];// 三张底牌
	private int[][] threeCardsPosition = {{170, 10}, {220, 10}, {270, 10}};
	private int[][] timeLimitePosition = {{130, 190}, {80, 80}, {360, 80}};
	private int[][] passPosition = {{130, 190}, {80, 80}, {360, 80}};
	private int[][] playerLatestCardsPosition = {{130, 140}, {80, 60}, {360, 60}};
	private int[][] playerCardsPosition = {{30, 210}, {30, 60}, {410, 60}};
	private int[][] scorePosition = {{70, 290}, {70, 30}, {340, 30}};
	private int[][] iconPosition = {{30, 270}, {30, 10}, {410, 10}};
	private int buttonPosition_X = 240;
	private int buttonPosition_Y = 160;
	private boolean[] canPass = new boolean[3];
	private int[][] playerCards = new int[3][17];
	private boolean canDrawLatestCards = false;
	private int[] allCards = new int[54];// 一副扑克牌
	private int currentScore = 10;// 当前分数
	private int currentId = 0;// 当前操作的人
	private int currentCircle = 0;// 本轮次数
	public static CardsHolder cardsOnDesktop = null;// 最新的一手牌
	private int timeLimite = 300;
	// 存储胜负得分信息
	private int result[] = new int[3];
	/**
	 * * -1:重新开始 0:游戏中 1:本局结束
	 */
	private int op = -1;// 游戏的进度控制
	public static Player[] players = new Player[3];// 三个玩家
	public static int multiple = 1;// 当前倍数
	public static int boss = 0;// 地主
	public boolean ifClickChupai = false;

	public Desk() {
		redoImage = Game.getImage("btn_redo");
		passImage = Game.getImage( "btn_pass");
		chuPaiImage = Game.getImage( "btn_chupai");
		tiShiImage = Game.getImage( "btn_tishi");
		farmerImage = Game.getImage( "icon_farmer");
		landlordImage = Game.getImage(
				"icon_landlord");
	}

	public void gameLogic() {
		switch (op) {
			case -1 :
				init();
				op = 0;
				break;
			case 0 :
				checkGameOver();
				break;
			case 1 :
				break;
		}
	}

	public void controlPaint(GLEx canvas) {
		switch (op) {
			case -1 :
				break;
			case 0 :
				paintGaming(canvas);
				break;
			case 1 :
				paintResult(canvas);
				break;

		}
	}

	private void checkGameOver() {
		for (int k = 0; k < 3; k++) {
			// 当三个人中其中一个人牌的数量为0，则游戏结束
			if (players[k].cards.length == 0) {
				// 切换到游戏结束状态
				op = 1;
				// 得到最先出去的人的id
				winId = k;
				// 判断哪方获胜
				if (boss == winId) {
					// 地主方获胜后的积分操作
					for (int i = 0; i < 3; i++) {
						if (i == boss) {
							// 地主需要加两倍积分
							result[i] = currentScore * multiple * 2;
							scores[i] += currentScore * multiple * 2;
						}
						else {
							// 农民方需要减分
							result[i] = -currentScore * multiple;
							scores[i] -= currentScore * multiple;
						}
					}
				}
				else {
					// 如果农民方胜利
					for (int i = 0; i < 3; i++) {
						if (i != boss) {
							// 农民方加分
							result[i] = currentScore * multiple;
							scores[i] += currentScore * multiple;
						}
						else {
							// 地主方减分
							result[i] = -currentScore * multiple * 2;
							scores[i] -= currentScore * multiple * 2;
						}
					}
				}
				return;
			}
		}

		// 游戏没有结束，继续。
		// 如果轮到电脑出牌
		if (currentId == 1 || currentId == 2) {
			if (timeLimite <= 300 && timeLimite >= 0) {
				// 获取手中的牌中能够打过当前手牌
				CardsHolder tempcard = players[currentId].chupaiAI(cardsOnDesktop);
				if (tempcard != null) {
					// 手中有大过的牌，则出
					cardsOnDesktop = tempcard;
					nextPerson();
				}
				else {
					// 没有打过的牌，则不要
					buyao();
				}
			}

		}

		// 如果轮到本人出牌
		if (currentId == 0) {
			if (timeLimite <= 300 && timeLimite >= 0) {
				if (ifClickChupai == true) {
					CardsHolder card = players[0].chupai(cardsOnDesktop);
					if (card != null) {
						cardsOnDesktop = card;
						nextPerson();
					}
					ifClickChupai = false;
				}

			}
			else {
				if (currentCircle != 0) {
					buyao();
				}
				else {
					CardsHolder autoCard = players[currentId].chupaiAI(cardsOnDesktop);
					cardsOnDesktop = autoCard;
					nextPerson();

				}

			}

		}
		// 时间倒计时
		timeLimite -= 2;
		canDrawLatestCards = true;

	}
	// 初始化游戏
	public void init() {
		allCards = new int[54];
		playerCards = new int[3][17];
		threeCards = new int[3];
		winId = -1;
		currentScore = 3;
		multiple = 1;
		cardsOnDesktop = null;
		currentCircle = 0;
		currentId = 0;
		for (int i = 0; i < 3; i++) {
			scores[i] = 50;
		}
		for (int i = 0; i < 3; i++) {
			canPass[i] = false;
		}
		for (int i = 0; i < allCards.length; i++) {
			allCards[i] = i;
		}
		CardsManager.shuffle(allCards);
		fapai(allCards);
		chooseBoss();
		CardsManager.sort(playerCards[0]);
		CardsManager.sort(playerCards[1]);
		CardsManager.sort(playerCards[2]);
		players[0] = new Player(playerCards[0], playerCardsPosition[0][0],
				playerCardsPosition[0][1], CardsType.direction_Horizontal, 0, this);
		players[1] = new Player(playerCards[1], playerCardsPosition[1][0],
				playerCardsPosition[1][1], CardsType.direction_Vertical, 1, this);
		players[2] = new Player(playerCards[2], playerCardsPosition[2][0],
				playerCardsPosition[2][1], CardsType.direction_Vertical, 2, this);
		players[0].setLastAndNext(players[1], players[2]);
		players[1].setLastAndNext(players[2], players[0]);
		players[2].setLastAndNext(players[0], players[1]);
		// CardsAnalyzer ana = CardsAnalyzer.getInstance();
		//
		// for (int i = 0; i < players.length; i++) {
		// boolean b = ana.testAnalyze(playerCards[i]);
		// if (!b) {
		// init();
		// System.out.println("chongqinglaiguo");
		// break;
		// }
		// }
		// for (int i = 0; i < 3; i++) {
		// StringBuffer sb = new StringBuffer();
		// sb.append("chushipai---" + i + ":");
		// for (int j = 0; j < playerCards[i].length; j++) {
		// sb.append(playerCards[i][j] + ",");
		// }
		// System.out.println(sb.toString());
		// }
	}

	// 发牌
	public void fapai(int[] cards) {
		for (int i = 0; i < 51; i++) {
			playerCards[i / 17][i % 17] = cards[i];
		}
		threeCards[0] = cards[51];
		threeCards[1] = cards[52];
		threeCards[2] = cards[53];
	}

	// 随机地主，将三张底牌给地主
	private void chooseBoss() {
		// boss = CardsManager.getBoss();
		currentId = boss;
		int[] diZhuCards = new int[20];
		for (int i = 0; i < 17; i++) {
			diZhuCards[i] = playerCards[boss][i];
		}
		diZhuCards[17] = threeCards[0];
		diZhuCards[18] = threeCards[1];
		diZhuCards[19] = threeCards[2];
		playerCards[boss] = diZhuCards;
	}

	// 不要牌的操作
	private void buyao() {
		// 清空当前不要牌的人的最后一手牌
		players[currentId].latestCards = null;
		canPass[currentId] = true;
		// 定位下一个人的id
		nextPerson();
		// 如果已经转回来，则该人继续出牌，本轮清空，新一轮开始
		if (cardsOnDesktop != null && currentId == cardsOnDesktop.playerId) {
			currentCircle = 0;
			cardsOnDesktop = null;// 转回到最大牌的那个人再出牌
			players[currentId].latestCards = null;
		}
	}

	// 定位下一个人的id并重新倒计时
	private void nextPerson() {
		switch (currentId) {
			case 0 :
				currentId = 2;
				break;
			case 1 :
				currentId = 0;
				break;
			case 2 :
				currentId = 1;
				break;
		}
		currentCircle++;
		timeLimite = 300;
	}

	// 绘制游戏画面
	private void paintGaming(GLEx canvas) {

		players[0].paint(canvas);
		players[1].paint(canvas);
		players[2].paint(canvas);
		paintThreeCards(canvas);
		paintIconAndScore(canvas);
		paintTimeLimite(canvas);

		// 如果轮到本人出牌，画“不要”“出牌”“重新开始”按钮
		if (currentId == 0) {
			RectF.Range src = new RectF.Range();
			RectF.Range dst = new RectF.Range();

			src.set(0, 0, chuPaiImage.getWidth(), chuPaiImage.getHeight());
			dst.set((int) (buttonPosition_X * Game.SCALE_HORIAONTAL),
					(int) (buttonPosition_Y * Game.SCALE_VERTICAL),
					(int) ((buttonPosition_X + 80) * Game.SCALE_HORIAONTAL),
					(int) ((buttonPosition_Y + 40) * Game.SCALE_VERTICAL));
			canvas.drawBitmap(chuPaiImage, src, dst, null);

			if (currentCircle != 0) {
				src.set(0, 0, passImage.getWidth(), passImage.getHeight());
				dst.set((int) ((buttonPosition_X - 80) * Game.SCALE_HORIAONTAL),
						(int) (buttonPosition_Y * Game.SCALE_VERTICAL),
						(int) ((buttonPosition_X) * Game.SCALE_HORIAONTAL),
						(int) ((buttonPosition_Y + 40) * Game.SCALE_VERTICAL));
				canvas.drawBitmap(passImage, src, dst, null);
			}

			src.set(0, 0, redoImage.getWidth(), redoImage.getHeight());
			dst.set((int) ((buttonPosition_X + 80) * Game.SCALE_HORIAONTAL),
					(int) ((buttonPosition_Y) * Game.SCALE_VERTICAL),
					(int) ((buttonPosition_X + 160) * Game.SCALE_HORIAONTAL),
					(int) ((buttonPosition_Y + 40) * Game.SCALE_VERTICAL));
			canvas.drawBitmap(redoImage, src, dst, null);

			src.set(0, 0, tiShiImage.getWidth(), tiShiImage.getHeight());
			dst.set((int) ((buttonPosition_X + 160) * Game.SCALE_HORIAONTAL),
					(int) ((buttonPosition_Y) * Game.SCALE_VERTICAL),
					(int) ((buttonPosition_X + 240) * Game.SCALE_HORIAONTAL),
					(int) ((buttonPosition_Y + 40) * Game.SCALE_VERTICAL));
			canvas.drawBitmap(tiShiImage, src, dst, null);

		}

		// 画各自刚出的牌或“不要”
		for (int i = 0; i < 3; i++) {
			if (currentId != i && players[i].latestCards != null && canDrawLatestCards == true) {
				players[i].latestCards.paint(canvas, playerLatestCardsPosition[i][0],
						playerLatestCardsPosition[i][1], players[i].paintDirection);
			}
			if (currentId != i && players[i].latestCards == null && canPass[i] == true) {
				paintPass(canvas, i);
			}
		}

	}

	// 画倒计时
	private void paintTimeLimite(GLEx canvas) {
		Paint paint = new Paint();
		paint.setColor(LColor.blue);
		paint.setTextSize((int) (16 * Game.SCALE_HORIAONTAL));
		for (int i = 0; i < 3; i++) {
			if (i == currentId) {
				canvas.drawText("" + (timeLimite / 10),
						(int) (timeLimitePosition[i][0] * Game.SCALE_HORIAONTAL),
						(int) (timeLimitePosition[i][1] * Game.SCALE_VERTICAL), paint);
			}
		}
	}

	// 画“不要”二字
	private void paintPass(GLEx canvas, int id) {
		Paint paint = new Paint();
		paint.setColor(LColor.blue);
		paint.setTextSize((int) (16 * Game.SCALE_HORIAONTAL));
		canvas.drawText("不要", (int) (passPosition[id][0] * Game.SCALE_HORIAONTAL),
				(int) (passPosition[id][1] * Game.SCALE_VERTICAL), paint);

	}

	// 画游戏中的分数
	private void paintIconAndScore(GLEx canvas) {

		Paint paint = new Paint();
		paint.setTextSize((int) (16 * Game.SCALE_VERTICAL));
		RectF.Range src = new RectF.Range();
		RectF.Range dst = new RectF.Range();
		for (int i = 0; i < 3; i++) {
			if (boss == i) {
				paint.setStyle(Style.STROKE);
				paint.setColor(LColor.black);
				paint.setStrokeWidth(1);
				src.set(0, 0, landlordImage.getWidth(), landlordImage.getHeight());
				dst.set((int) (iconPosition[i][0] * Game.SCALE_HORIAONTAL),
						(int) (iconPosition[i][1] * Game.SCALE_VERTICAL),
						(int) ((iconPosition[i][0] + 40) * Game.SCALE_HORIAONTAL),
						(int) ((iconPosition[i][1] + 40) * Game.SCALE_VERTICAL));
				RectF.Range rectF = new RectF.Range(dst);
				canvas.rect(rectF, 5, 5, paint);
				canvas.drawBitmap(landlordImage, src, dst, paint);

				paint.setStyle(Style.FILL);
				paint.setColor(LColor.white);
				canvas.drawText("玩家" + i,
						(int) (scorePosition[i][0] * Game.SCALE_HORIAONTAL),
						(int) (scorePosition[i][1] * Game.SCALE_VERTICAL), paint);
				canvas.drawText("得分：" + scores[i],
						(int) (scorePosition[i][0] * Game.SCALE_HORIAONTAL),
						(int) ((scorePosition[i][1] + 20) * Game.SCALE_VERTICAL), paint);
			}
			else {
				paint.setStyle(Style.STROKE);
				paint.setColor(LColor.black);
				paint.setStrokeWidth(1);
				src.set(0, 0, farmerImage.getWidth(), farmerImage.getHeight());
				dst.set((int) (iconPosition[i][0] * Game.SCALE_HORIAONTAL),
						(int) (iconPosition[i][1] * Game.SCALE_VERTICAL),
						(int) ((iconPosition[i][0] + 40) * Game.SCALE_HORIAONTAL),
						(int) ((iconPosition[i][1] + 40) * Game.SCALE_VERTICAL));
				RectF.Range rectF = new RectF.Range(dst);
				canvas.rect(rectF, 5, 5, paint);
				canvas.drawBitmap(farmerImage, src, rectF, paint);

				paint.setStyle(Style.FILL);
				paint.setColor(LColor.white);
				canvas.drawText("玩家" + i,
						(int) (scorePosition[i][0] * Game.SCALE_HORIAONTAL),
						(int) (scorePosition[i][1] * Game.SCALE_VERTICAL), paint);
				canvas.drawText("得分：" + scores[i],
						(int) (scorePosition[i][0] * Game.SCALE_HORIAONTAL),
						(int) ((scorePosition[i][1] + 20) * Game.SCALE_VERTICAL), paint);
			}
		}

		paint.setStyle(Style.FILL);
		paint.setColor(LColor.white);
		canvas.drawText("当前底分：" + currentScore + "  当前倍数：" + multiple,
				(int) (150 * Game.SCALE_HORIAONTAL),
				(int) (150 * Game.SCALE_VERTICAL), paint);
	}

	// 画游戏结束时的分数和各自剩余牌
	private void paintResult(GLEx canvas) {
		Paint paint = new Paint();
		paint.setColor(LColor.white);
		paint.setTextSize((int) (20 * Game.SCALE_HORIAONTAL));
		for (int i = 0; i < 3; i++) {
			canvas.drawText("玩家" + i + ":本局得分:" + result[i] + "   总分：" + scores[i],
					(int) (110 * Game.SCALE_HORIAONTAL),
					(int) ((96 + i * 30) * Game.SCALE_VERTICAL), paint);
		}
		for (int i = 0; i < 3; i++) {
			players[i].paintResultCards(canvas);
		}

	}

	// 画地主的三张牌
	private void paintThreeCards(GLEx canvas) {
		RectF.Range src = new RectF.Range();
		RectF.Range dst = new RectF.Range();
		Paint paint = new Paint();
		paint.setStyle(Style.STROKE);
		paint.setColor(LColor.black);
		paint.setStrokeWidth(1);
		for (int i = 0; i < 3; i++) {
			int row = CardsManager.getImageRow(threeCards[i]);
			int col = CardsManager.getImageCol(threeCards[i]);
			LTexture image = Game.getImage(
					CardImage.cardImages[row][col]);
			src.set(0, 0, image.getWidth(), image.getHeight());
			dst.set((int) (threeCardsPosition[i][0] * Game.SCALE_HORIAONTAL),
					(int) (threeCardsPosition[i][1] * Game.SCALE_VERTICAL),
					(int) ((threeCardsPosition[i][0] + 40) * Game.SCALE_HORIAONTAL),
					(int) ((threeCardsPosition[i][1] + 60) * Game.SCALE_VERTICAL));
			RectF.Range rectF = new RectF.Range(dst);
			canvas.drawBitmap(image, src, dst, paint);
			canvas.rect(rectF, 5, 5, paint);
		}
	}

	public void restart() {
		op = 1;
	}
	// 触屏的处理
	public void onTuch(int x, int y) {
		// for (int i = 0; i < players.length; i++) {
		// StringBuffer sb = new StringBuffer();
		// sb.append(i + " : ");
		// for (int j = 0; j < players[i].cards.length; j++) {
		// sb.append(players[i].cards[j] + (players[i].cards[j] >= 10 ? "" :
		// " ") + ",");
		// }
		// System.out.println(sb.toString());
		// }

		// 若游戏结束，则点击任意屏幕重新开始
		if (op == 1) {
			op = -1;
		}
		players[0].onTuch(x, y);
		if (currentId == 0) {

			if (CardsManager.inRect(x, y, (int) (buttonPosition_X * Game.SCALE_HORIAONTAL),
					(int) (buttonPosition_Y * Game.SCALE_VERTICAL),
					(int) (80 * Game.SCALE_HORIAONTAL),
					(int) (40 * Game.SCALE_VERTICAL))) {
				System.out.println("出牌");
				ifClickChupai = true;

			}
			if (currentCircle != 0) {
				if (CardsManager.inRect(x, y,
						(int) ((buttonPosition_X - 80) * Game.SCALE_HORIAONTAL),
						(int) (buttonPosition_Y * Game.SCALE_VERTICAL),
						(int) (80 * Game.SCALE_HORIAONTAL),
						(int) (40 * Game.SCALE_VERTICAL))) {
					System.out.println("不要");
					buyao();
				}
			}
			if (CardsManager.inRect(x, y,
					(int) ((buttonPosition_X + 80) * Game.SCALE_HORIAONTAL),
					(int) (buttonPosition_Y * Game.SCALE_VERTICAL),
					(int) (80 * Game.SCALE_HORIAONTAL),
					(int) (40 * Game.SCALE_VERTICAL))) {
				System.out.println("重选");
				players[0].redo();
			}
			if (CardsManager.inRect(x, y,
					(int) ((buttonPosition_X + 160) * Game.SCALE_HORIAONTAL),
					(int) (buttonPosition_Y * Game.SCALE_VERTICAL),
					(int) (80 * Game.SCALE_HORIAONTAL),
					(int) (40 * Game.SCALE_VERTICAL))) {
				System.out.println("提示（重新）");
				restart();
			}
		}
	}
}
