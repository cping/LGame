package org.doudizhu.test;

import java.util.Vector;

public class CardsAnalyzer {
	private int[] cards;
	private int[] countCards = new int[12];
	private int count2;
	private int countWang;
	private Vector<int[]> card_zhadan = new Vector<int[]>(3);
	private Vector<int[]> card_sanshun = new Vector<int[]>(3);
	private Vector<int[]> card_shuangshun = new Vector<int[]>(3);
	private Vector<int[]> card_sanzhang = new Vector<int[]>(3);
	private Vector<int[]> card_danshun = new Vector<int[]>(3);
	private Vector<int[]> card_duipai = new Vector<int[]>(3);
	private Vector<int[]> card_danpai = new Vector<int[]>(5);

	public int[] getCountPokes() {
		return countCards;
	}

	public int getCount2() {
		return count2;
	}

	public int getCountWang() {
		return countWang;
	}

	public Vector<int[]> getCard_zhadan() {
		return card_zhadan;
	}

	public Vector<int[]> getCard_sanshun() {
		return card_sanshun;
	}

	public Vector<int[]> getCard_shuangshun() {
		return card_shuangshun;
	}

	public Vector<int[]> getCard_sanzhang() {
		return card_sanzhang;
	}

	public Vector<int[]> getCard_danshun() {
		return card_danshun;
	}

	public Vector<int[]> getCard_duipai() {
		return card_duipai;
	}

	public Vector<int[]> getCard_danpai() {
		return card_danpai;
	}

	private CardsAnalyzer() {
	}

	public static CardsAnalyzer getInstance() {
		return new CardsAnalyzer();
	}

	private void init() {
		for (int i = 0; i < countCards.length; i++) {
			countCards[i] = 0;
		}
		count2 = 0;
		countWang = 0;
		card_zhadan.clear();
		card_sanshun.clear();
		card_shuangshun.clear();
		card_sanzhang.clear();
		card_danshun.clear();
		card_duipai.clear();
		card_danpai.clear();
	}

	public boolean lastCardTypeEq(int pokeType) {
		if (remainCount() > 1) {
			return false;
		}
		switch (pokeType) {
			case CardsType.sanzhang :
				return card_sanzhang.size() == 1;
			case CardsType.duipai :
				return card_duipai.size() == 1;
			case CardsType.danpai :
				return card_danpai.size() == 1;
		}
		return false;
	}

	public int[] getPokes() {
		return cards;
	}

	public void setPokes(int[] pokes) {
		CardsManager.sort(pokes);
		this.cards = pokes;
		try {
			this.analyze();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public int remainCount() {
		return card_danpai.size() + card_duipai.size() + card_sanzhang.size() + card_danshun.size()
				+ card_shuangshun.size() + card_sanshun.size() + card_zhadan.size();
	}
	public int[] getMinType(Player last, Player next) {

		CardsAnalyzer lastAna = CardsAnalyzer.getInstance();
		lastAna.setPokes(last.cards);

		CardsAnalyzer nextAna = CardsAnalyzer.getInstance();
		nextAna.setPokes(next.cards);

		// int lastCount = lastAna.remainCount();
		// int nextCount = nextAna.remainCount();

		int needSmart = -1;
		if (Desk.boss == next.playerId
				|| (Desk.boss != next.playerId && Desk.boss != last.playerId)) {
			// 是对手
			if (next.cards.length <= 2) {
				needSmart = next.cards.length;
			}
		}

		int pokeType = -1;
		int minValue = 55;
		int pokeIdx = 0;
		int size;
		Vector<int[]> temp;

		temp = card_sanshun;
		size = temp.size();

		for (int i = 0; i < size; i++) {
			int[] p = temp.elementAt(i);
			if (minValue > p[0]) {
				pokeType = CardsType.sanshun;
				minValue = p[0];
				pokeIdx = i;
			}
		}

		temp = card_shuangshun;
		size = temp.size();

		for (int i = 0; i < size; i++) {
			int[] p = temp.elementAt(i);
			if (minValue > p[0]) {
				pokeType = CardsType.shuangshun;
				minValue = p[0];
				pokeIdx = i;
			}
		}

		temp = card_danshun;
		size = temp.size();

		for (int i = 0; i < size; i++) {
			int[] p = temp.elementAt(i);
			if (minValue > p[0]) {
				pokeType = CardsType.danshun;
				minValue = p[0];
				pokeIdx = i;
			}
		}
		temp = card_sanzhang;
		size = temp.size();

		for (int i = 0; i < size; i++) {
			int[] p = temp.elementAt(i);
			if (minValue > p[0]) {
				pokeType = CardsType.sanzhang;
				minValue = p[0];
				pokeIdx = i;
			}
		}

		if (needSmart == 2) {
			if (pokeType != -1) {
				return new int[]{pokeType, pokeIdx};
			}
			else {
				temp = card_duipai;
				size = temp.size();
				int min2 = -1;
				for (int i = 0; i < size; i++) {
					int[] p = temp.elementAt(i);
					if (min2 <= p[0]) {
						pokeType = CardsType.duipai;
						minValue = p[0];
						min2 = p[0];
						pokeIdx = i;
					}
				}
			}

		}
		else {
			temp = card_duipai;
			size = temp.size();

			for (int i = 0; i < size; i++) {
				int[] p = temp.elementAt(i);
				if (minValue > p[0]) {
					pokeType = CardsType.duipai;
					minValue = p[0];
					pokeIdx = i;
				}
			}
		}
		if (needSmart == 1) {
			if (pokeType != -1) {
				return new int[]{pokeType, pokeIdx};
			}
			else {
				int min1 = -1;
				for (int i = 0; i < size; i++) {
					int[] p = temp.elementAt(i);
					if (min1 <= p[0]) {
						pokeType = CardsType.danpai;
						minValue = p[0];
						min1 = p[0];
						pokeIdx = i;
					}
				}
			}

		}
		else {
			temp = card_danpai;
			size = temp.size();

			for (int i = 0; i < size; i++) {
				int[] p = temp.elementAt(i);
				if (minValue > p[0]) {
					pokeType = CardsType.danpai;
					minValue = p[0];
					pokeIdx = i;
				}
			}
		}
		return new int[]{pokeType, pokeIdx};
	}

	// 分析几大主要牌型
	private void analyze() {

		// 初始化牌型容器
		init();

		// 分析王，2，普通牌的数量
		for (int i = 0; i < cards.length; i++) {
			int v = CardsManager.getCardNumber(cards[i]);
			if (v == 16 || v == 17) {
				countWang++;
			}
			else
				if (v == 15) {
					count2++;
				}
				else {
					countCards[v - 3]++;
				}
		}

		// 分析三顺牌型
		int start = -1;
		int end = -1;
		for (int i = 0; i < countCards.length; i++) {
			if (countCards[i] == 3) {
				if (start == -1) {
					start = i;
				}
				else {
					end = i;
				}
			}
			else {
				if (end != -1 && start != -1) {
					int dur = end - start + 1;
					int[] ss = new int[dur * 3];
					int m = 0;
					for (int j = 0; j < cards.length; j++) {
						int v = CardsManager.getCardNumber(cards[j]) - 3;
						if (v >= start && v <= end) {
							ss[m++] = cards[j];
						}
					}
					if (m == dur * 3 - 1) {
						System.out.println("sanshun is over!!!");
					}
					else {
						System.out.println("sanshun error!!!");
					}
					card_sanshun.addElement(ss);
					for (int s = start; s <= end; s++) {
						countCards[s] = -1;
					}
					start = end = -1;
					continue;
				}
				else {
					start = end = -1;
				}
			}
		}

		// 分析双顺牌型
		int sstart = -1;
		int send = -1;
		for (int i = 0; i < countCards.length; i++) {
			if (countCards[i] == 2) {
				if (sstart == -1) {
					sstart = i;
				}
				else {
					send = i;
				}
			}
			else {
				if (sstart != -1 && send != -1) {
					int dur = send - sstart + 1;
					if (dur < 3) {
						sstart = send = -1;
						continue;
					}
					else {
						int shuangshun[] = new int[dur * 2];
						int m = 0;
						for (int j = 0; j < cards.length; j++) {
							int v = CardsManager.getCardNumber(cards[j]) - 3;
							if (v >= sstart && v <= send) {
								shuangshun[m++] = cards[j];
							}
						}
						card_shuangshun.addElement(shuangshun);
						for (int s = sstart; s <= send; s++) {
							countCards[s] = -1;
						}
						sstart = send = -1;
						continue;
					}
				}
				else {
					sstart = send = -1;
				}
			}
		}

		// 分析单顺牌型
		int dstart = -1;
		int dend = -1;
		for (int i = 0; i < countCards.length; i++) {
			if (countCards[i] >= 1) {
				if (dstart == -1) {
					dstart = i;
				}
				else {
					dend = i;
				}
			}
			else {
				if (dstart != -1 && dend != -1) {
					int dur = dend - dstart + 1;
					if (dur >= 5) {
						int m = 0;
						int[] danshun = new int[dur];
						for (int j = 0; j < cards.length; j++) {
							int v = CardsManager.getCardNumber(cards[j]) - 3;
							if (v == dend) {
								danshun[m++] = cards[j];
								countCards[dend]--;
								dend--;
							}
							if (dend == dstart - 1) {
								break;
							}
						}
						card_danshun.addElement(danshun);
					}
					dstart = dend = -1;
				}
				else {
					dstart = dend = -1;
				}
			}
		}

		// 分析三张牌型
		for (int i = 0; i < countCards.length; i++) {
			if (countCards[i] == 3) {
				countCards[i] = -1;
				int[] sanzhang = new int[3];
				int m = 0;
				for (int j = 0; j < cards.length; j++) {
					int v = CardsManager.getCardNumber(cards[j]) - 3;
					if (v == i) {
						sanzhang[m++] = cards[j];
					}
				}
				card_sanzhang.addElement(sanzhang);
			}
		}

		// 分析对牌
		for (int i = 0; i < countCards.length; i++) {
			if (countCards[i] == 2) {
				int[] duipai = new int[2];
				for (int j = 0; j < cards.length; j++) {
					int v = CardsManager.getCardNumber(cards[j]) - 3;
					if (v == i) {
						duipai[0] = cards[j];
						duipai[1] = cards[j + 1];
						card_duipai.addElement(duipai);
						break;
					}
				}
				countCards[i] = -1;
			}
		}

		// 分析单牌
		for (int i = 0; i < countCards.length; i++) {
			if (countCards[i] == 1) {
				for (int j = 0; j < cards.length; j++) {
					int v = CardsManager.getCardNumber(cards[j]) - 3;
					if (v == i) {
						card_danpai.addElement(new int[]{cards[j]});
						countCards[i] = -1;
						break;
					}

				}
			}
		}

		// 根据2的数量进行分析
		switch (count2) {
			case 4 :
				card_zhadan.addElement(new int[]{cards[countWang], cards[countWang + 1],
						cards[countWang + 2], cards[countWang + 3]});
				break;
			case 3 :
				card_sanzhang.addElement(new int[]{cards[countWang], cards[countWang + 1],
						cards[countWang + 2]});
				break;
			case 2 :
				card_duipai.addElement(new int[]{cards[countWang], cards[countWang + 1]});
				break;
			case 1 :
				card_danpai.addElement(new int[]{cards[countWang]});
				break;
		}

		// 分析炸弹
		for (int i = 0; i < countCards.length - 1; i++) {
			if (countCards[i] == 4) {
				card_zhadan.addElement(new int[]{i * 4 + 3, i * 4 + 2, i * 4 + 1, i * 4});
				countCards[i] = -1;
			}
		}

		// 分析火箭
		if (countWang == 1) {
			card_danpai.addElement(new int[]{cards[0]});
		}
		else
			if (countWang == 2) {
				card_zhadan.addElement(new int[]{cards[0], cards[1]});
			}
	}
}
