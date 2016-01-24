package org.doudizhu.test;

import loon.LTexture;
import loon.canvas.LColor;
import loon.canvas.Paint;
import loon.canvas.Paint.Style;
import loon.geom.RectF;
import loon.opengl.GLEx;

public class Player {

	// 玩家手中的牌
	int[] cards;

	// 玩家选中牌的标志
	boolean[] cardsFlag;

	// 玩家ID
	int playerId;

	// 当前玩家
	int currentId;

	// 当前轮回
	int currentCircle;

	// 玩家所在桌面上的坐标
	int top, left;

	// 玩家所在桌子的实例
	Desk desk;

	// 玩家最新一手牌
	CardsHolder latestCards;

	// 桌面最新的一手牌
	CardsHolder cardsOnDesktop;


	int paintDirection = CardsType.direction_Vertical;
	LTexture cardImage;

	private Player last;
	private Player next;

	public Player(int[] cards, int left, int top, int paintDir, int id, Desk desk) {
		this.desk = desk;
		this.playerId = id;
		this.cards = cards;
		cardsFlag = new boolean[cards.length];
		this.setLeftAndTop(left, top);
		this.paintDirection = paintDir;
	}

	public void setLeftAndTop(int left, int top) {
		this.left = left;
		this.top = top;
	}

	// 设置玩家上下家关系
	public void setLastAndNext(Player last, Player next) {
		this.last = last;
		this.next = next;
	}

	// 绘制玩家手中的牌
	public void paint(GLEx canvas) {
		System.out.println("id:" + playerId);
		RectF.Range src = new RectF.Range();
		RectF.Range des = new RectF.Range();

		int row;
		int col;

		// 当玩家是NPC时，竖向绘制，扑克牌全是背面
		if (paintDirection == CardsType.direction_Vertical) {
			Paint paint = new Paint();
			paint.setStyle(Style.STROKE);
			paint.setColor(LColor.black);
			paint.setStrokeWidth(1);
			LTexture backImage = Game.getImage("card_bg");

			src.set(0, 0, backImage.getWidth(), backImage.getHeight());
			des.set((int) (left * Game.SCALE_HORIAONTAL),
					(int) (top * Game.SCALE_VERTICAL),
					(int) ((left + 40) * Game.SCALE_HORIAONTAL),
					(int) ((top + 60) * Game.SCALE_VERTICAL));
			RectF.Range rectF = new RectF.Range(des);
			canvas.rect(rectF, 5, 5, paint);
			canvas.drawBitmap(backImage, src, des, paint);

			// 显示剩余牌数
			paint.setStyle(Style.FILL);
			paint.setColor(LColor.white);
			paint.setTextSize((int) (20 * Game.SCALE_HORIAONTAL));
			canvas.drawText("" + cards.length, (int) (left * Game.SCALE_HORIAONTAL),
					(int) ((top + 80) * Game.SCALE_VERTICAL), paint);

		}
		else {
			Paint paint = new Paint();
			paint.setStyle(Style.STROKE);
			paint.setColor(LColor.black);
			paint.setStrokeWidth(1);
			for (int i = 0; i < cards.length; i++) {
				row = CardsManager.getImageRow(cards[i]);
				col = CardsManager.getImageCol(cards[i]);
				cardImage = Game.getImage(CardImage.cardImages[row][col]);
				int select = 0;
				if (cardsFlag[i]) {
					select = 10;
				}
				src.set(0, 0, cardImage.getWidth(), cardImage.getHeight());
				des.set((int) ((left + i * 20) * Game.SCALE_HORIAONTAL),
						(int) ((top - select) * Game.SCALE_VERTICAL),
						(int) ((left + 40 + i * 20) * Game.SCALE_HORIAONTAL), (int) ((top
								- select + 60) * Game.SCALE_VERTICAL));
				RectF.Range rectF = new RectF.Range(des);
				canvas.rect(rectF, 5, 5, paint);
				canvas.drawBitmap(cardImage, src, des, paint);

			}
		}

	}
	public void paintResultCards(GLEx canvas) {
		RectF.Range src = new RectF.Range();
		RectF.Range des = new RectF.Range();
		int row;
		int col;

		for (int i = 0; i < cards.length; i++) {
			row = CardsManager.getImageRow(cards[i]);
			col = CardsManager.getImageCol(cards[i]);
			cardImage = Game.getImage(CardImage.cardImages[row][col]);
			Paint paint = new Paint();
			paint.setStyle(Style.STROKE);
			paint.setColor(LColor.black);
			paint.setStrokeWidth(1);
			// 当玩家是NPC时，竖向绘制，扑克牌全是背面
			if (paintDirection == CardsType.direction_Vertical) {
				src.set(0, 0, cardImage.getWidth(), cardImage.getHeight());
				des.set((int) (left * Game.SCALE_HORIAONTAL),
						(int) ((top - 40 + i * 15) * Game.SCALE_VERTICAL),
						(int) ((left + 40) * Game.SCALE_HORIAONTAL),
						(int) ((top + 20 + i * 15) * Game.SCALE_VERTICAL));
				RectF.Range rectF = new RectF.Range(des);
				canvas.rect(rectF, 5, 5, paint);
				canvas.drawBitmap(cardImage, src, des, paint);

			}
			else {
				src.set(0, 0, cardImage.getWidth(), cardImage.getHeight());
				des.set((int) ((left + 40 + i * 20) * Game.SCALE_HORIAONTAL),
						(int) (top * Game.SCALE_VERTICAL),
						(int) ((left + 80 + i * 20) * Game.SCALE_HORIAONTAL),
						(int) ((top + 60) * Game.SCALE_VERTICAL));
				RectF.Range rectF = new RectF.Range(des);
				canvas.rect(rectF, 5, 5, paint);
				canvas.drawBitmap(cardImage, src, des, paint);

			}
		}
	}

	// 电脑判断出牌的智能
	public CardsHolder chupaiAI(CardsHolder card) {
		int[] pokeWanted = null;

		if (card == null) {
			// 玩家随意出一手牌
			pokeWanted = CardsManager.outCardByItsself(cards, last, next);
		}
		else {
			// 玩家需要出一手比card大的牌
			pokeWanted = CardsManager.findTheRightCard(card, cards, last, next);
		}
		// 如果不能出牌，则返回
		if (pokeWanted == null) {
			return null;
		}
		// 以下为出牌的后续操作，将牌从玩家手中剔除
		for (int i = 0; i < pokeWanted.length; i++) {
			for (int j = 0; j < cards.length; j++) {
				if (cards[j] == pokeWanted[i]) {
					cards[j] = -1;
					break;
				}
			}
		}
		int[] newpokes = new int[0];
		if (cards.length - pokeWanted.length > 0) {
			newpokes = new int[cards.length - pokeWanted.length];
		}
		int j = 0;
		for (int i = 0; i < cards.length; i++) {
			if (cards[i] != -1) {
				newpokes[j] = cards[i];
				j++;
			}
		}
		this.cards = newpokes;
		CardsHolder thiscard = new CardsHolder(pokeWanted, playerId);
		// 更新桌子最近一手牌
		Desk.cardsOnDesktop = thiscard;
		this.latestCards = thiscard;
		return thiscard;
	}

	// 非电脑的出牌
	public CardsHolder chupai(CardsHolder card) {
		int count = 0;
		for (int i = 0; i < cards.length; i++) {
			if (cardsFlag[i]) {
				count++;
				System.out.println("出牌：" + String.valueOf(CardsManager.getCardNumber(cards[i])));
			}
		}
		int[] chupaiPokes = new int[count];
		int j = 0;
		for (int i = 0; i < cards.length; i++) {
			if (cardsFlag[i]) {
				chupaiPokes[j] = cards[i];
				j++;
			}
		}
		int cardType = CardsManager.getType(chupaiPokes);
		System.out.println("cardType:" + cardType);
		if (cardType == CardsType.error) {
			// 出牌错误
			if (chupaiPokes.length != 0) {
				Game.sendEmptyMessage(Game.WRONG_CARD);
			}
			else {
				Game.sendEmptyMessage(Game.EMPTY_CARD);
			}
			return null;
		}
		CardsHolder newLatestCardsHolder = new CardsHolder(chupaiPokes, playerId);
		if (card == null) {
			Desk.cardsOnDesktop = newLatestCardsHolder;
			this.latestCards = newLatestCardsHolder;

			int[] newPokes = new int[cards.length - count];
			int k = 0;
			for (int i = 0; i < cards.length; i++) {
				if (!cardsFlag[i]) {
					newPokes[k] = cards[i];
					k++;
				}

			}
			this.cards = newPokes;
			this.cardsFlag = new boolean[cards.length];
		}
		else {

			if (CardsManager.compare(newLatestCardsHolder, card) == 1) {
				Desk.cardsOnDesktop = newLatestCardsHolder;
				this.latestCards = newLatestCardsHolder;

				int[] newPokes = new int[cards.length - count];
				int ni = 0;
				for (int i = 0; i < cards.length; i++) {
					if (!cardsFlag[i]) {
						newPokes[ni] = cards[i];
						ni++;
					}
				}
				this.cards = newPokes;
				this.cardsFlag = new boolean[cards.length];
			}
			if (CardsManager.compare(newLatestCardsHolder, card) == 0) {
				Game.sendEmptyMessage(Game.SMALL_CARD);
				return null;
			}
			if (CardsManager.compare(newLatestCardsHolder, card) == -1) {
				Game.sendEmptyMessage(Game.WRONG_CARD);
				return null;
			}
		}
		return newLatestCardsHolder;
	}

	// 当玩家自己操作时，触摸屏事件的处理
	public void onTuch(int x, int y) {

		for (int i = 0; i < cards.length; i++) {
			// 判断是那张牌被选中，设置标志
			if (i != cards.length - 1) {
				if (CardsManager.inRect(x, y,
						(int) ((left + i * 20) * Game.SCALE_HORIAONTAL),
						(int) ((top - (cardsFlag[i] ? 10 : 0)) * Game.SCALE_VERTICAL),
						(int) (20 * Game.SCALE_HORIAONTAL),
						(int) (60 * Game.SCALE_VERTICAL))) {
					cardsFlag[i] = !cardsFlag[i];
					break;
				}
			}
			else {
				if (CardsManager.inRect(x, y,
						(int) ((left + i * 20) * Game.SCALE_HORIAONTAL),
						(int) ((top - (cardsFlag[i] ? 10 : 0)) * Game.SCALE_VERTICAL),
						(int) (40 * Game.SCALE_HORIAONTAL),
						(int) (60 * Game.SCALE_VERTICAL))) {
					cardsFlag[i] = !cardsFlag[i];
					break;
				}
			}

		}
	}

	public void redo() {
		for (int i = 0; i < cardsFlag.length; i++) {
			cardsFlag[i] = false;
		}
	}

}
