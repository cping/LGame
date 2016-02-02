package org.doudizhu.test;

import loon.LTexture;
import loon.canvas.LColor;
import loon.canvas.Paint;
import loon.geom.RectF;
import loon.opengl.GLEx;

public class CardsHolder {

	int value = 0;
	int cardsType = 0;
	int[] cards;
	LTexture cardImage;
	int playerId;

	public CardsHolder(int[] cards, int id) {
		this.playerId = id;
		this.cards = cards;
		cardsType = CardsManager.getType(cards);
		value = CardsManager.getValue(cards);
		// 如果有炸弹牌出现，分数翻倍
		if (cardsType == CardsType.huojian || cardsType == CardsType.zhadan) {
			Desk.multiple *= 2;
		}
	}

	public void paint(GLEx g, int left, int top, int dir) {

		RectF.Range src =new RectF.Range();
		RectF.Range des =new RectF.Range();
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(LColor.black);
		paint.setStrokeWidth(1);
		for (int i = 0; i < cards.length; i++) {
			int row = CardsManager.getImageRow(cards[i]);
			int col = CardsManager.getImageCol(cards[i]);
			cardImage = Game.getImage(CardImage.cardImages[row][col]);
			if (dir == CardsType.direction_Vertical) {
				row = CardsManager.getImageRow(cards[i]);
				col = CardsManager.getImageCol(cards[i]);
				src.set(0, 0, cardImage.getWidth(), cardImage.getHeight());
				des.set((int) (left * Game.SCALE_HORIAONTAL),
						(int) ((top + i * 15) * Game.SCALE_VERTICAL),
						(int) ((left + 40) * Game.SCALE_HORIAONTAL),
						(int) ((top + 60 + i * 15) * Game.SCALE_VERTICAL));
			}
			else {
				row = CardsManager.getImageRow(cards[i]);
				col = CardsManager.getImageCol(cards[i]);
				src.set(0, 0, cardImage.getWidth(), cardImage.getHeight());
				des.set((int) ((left + i * 20) * Game.SCALE_HORIAONTAL),
						(int) (top * Game.SCALE_VERTICAL),
						(int) ((left + 40 + i * 20) * Game.SCALE_HORIAONTAL),
						(int) ((top + 60) * Game.SCALE_VERTICAL));
			}
			RectF.Range rectF = new RectF.Range(des);
			g.rect(rectF, 5, 5, paint);
			g.drawBitmap(cardImage, src, des, paint);

		}

	}
}
