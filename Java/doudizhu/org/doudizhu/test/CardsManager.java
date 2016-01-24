package org.doudizhu.test;

import java.util.Vector;

import loon.utils.MathUtils;

public class CardsManager {

	public static boolean inRect(int x, int y, int rectX, int rectY, int rectW, int rectH) {
		// 必须要有等号，否则触摸在相邻牌的同一边上会出错
		if (x <= rectX || x >= rectX + rectW || y <= rectY || y >= rectY + rectH) {
			return false;
		}
		return true;
	}

	// 洗牌，cards[0]~cards[53]表示54张牌:3333444455556666...KKKKAAAA2222小王大王
	public static void shuffle(int[] cards) {
		int len = cards.length;
		// 对于54张牌中的任何一张，都随机找一张和它互换，将牌顺序打乱。
		for (int l = 0; l < len; l++) {
			int des = MathUtils.nextInt(54);
			int temp = cards[l];
			cards[l] = cards[des];
			cards[des] = temp;
		}
	}

	// 随机选择地主
	public static int getBoss() {
		return MathUtils.nextInt(3);
	}

	// 对牌进行从大到小排序，冒泡排序
	public static void sort(int[] cards) {
		for (int i = 0; i < cards.length; i++) {
			for (int j = i + 1; j < cards.length; j++) {
				if (cards[i] < cards[j]) {
					int temp = cards[i];
					cards[i] = cards[j];
					cards[j] = temp;
				}
			}
		}
	}

	public static int getImageRow(int poke) {
		return poke / 4;
	}

	public static int getImageCol(int poke) {
		return poke % 4;
	}

	// 获取某张牌的大小
	public static int getCardNumber(int card) {
		// 当扑克值为52时，是小王
		if (card == 52) {
			return 16;
		}
		// 当扑克值为53时，是大王
		if (card == 53) {
			return 17;
		}
		// 其它情况下返回相应的值(3,4,5,6,7,8,9,10,11(J),12(Q),13(K),14(A),15(2))
		return getImageRow(card) + 3;
	}

	// 判断是否单顺
	public static boolean isDanShun(int[] cards) {
		int start = getCardNumber(cards[0]);
		// 单顺最大一张不能大于A
		if (start > 14) {
			return false;
		}
		int next;
		for (int i = 1; i < cards.length; i++) {
			next = getCardNumber(cards[i]);
			if (start - next != 1) {
				return false;
			}
			start = next;
		}
		return true;
	}

	// 判断是否双顺
	public static boolean isShuangShun(int[] cards) {
		int start = getCardNumber(cards[0]);
		// 双顺最大一张不能大于A
		if (start > 14) {
			return false;
		}
		// 奇数张牌不可能是双顺
		if (cards.length % 2 != 0) {
			return false;
		}
		int next;
		for (int i = 2; i < cards.length; i += 2) {
			next = getCardNumber(cards[i]);
			if (start != getCardNumber(cards[i - 1])) {
				return false;
			}
			if (next != getCardNumber(cards[i + 1])) {
				return false;
			}
			if (start - next != 1) {
				return false;
			}
			start = next;
		}
		return true;
	}

	// 判断是否三顺
	public static boolean isSanShun(int[] cards) {
		int start = getCardNumber(cards[0]);
		// 三顺最大一张不能大于A
		if (start > 14) {
			return false;
		}
		// 三顺牌是3的倍数
		if (cards.length % 3 != 0) {
			return false;
		}
		int next;
		for (int i = 3; i < cards.length; i += 3) {
			next = getCardNumber(cards[i]);
			if (start != getCardNumber(cards[i - 1])) {
				return false;
			}
			if (next != getCardNumber(cards[i + 2])) {
				return false;
			}
			if (start - next != 1) {
				return false;
			}
			start = next;
		}
		return true;
	}

	// 判断飞机
	public static boolean isFeiJi(int[] cards) {
		// 飞机带翅膀的牌数只能是8、10、12、15、16、20
		if (cards.length == 8 || cards.length == 10 || cards.length == 12 || cards.length == 15
				|| cards.length == 16 || cards.length == 20) {
			// 飞机三顺个数
			int sanshun = 1;
			// 用于记录第一次找到飞机三顺的index
			int key = 0;
			boolean ifFound = false;
			// 找飞机中的三顺
			for (int i = 0; i <= cards.length - 6;) {
				if (getCardNumber(cards[i]) == getCardNumber(cards[i + 3])) {
					return false;
				}
				else
					if (getCardNumber(cards[i]) == getCardNumber(cards[i + 2])
							&& getCardNumber(cards[i + 3]) == getCardNumber(cards[i + 5])
							&& getCardNumber(cards[i]) - getCardNumber(cards[i + 3]) == 1) {
						if (ifFound == false) {
							// 记录第一次找到飞机三顺的index
							key = i;
						}
						ifFound = true;
						// 累计飞机的三顺个数
						sanshun++;
						i += 3;
					}
					else {
						if (ifFound == false) {
							i++;
						}
						else {
							break;
						}
					}
			}

			// 如果存在三顺
			if (ifFound == true) {
				// 用于存放除三顺外的其他牌
				int[] otherCards = new int[cards.length - sanshun * 3];
				// 取出三顺前面的牌
				for (int i = 0; i < key; i++) {
					otherCards[i] = cards[i];
				}
				// 取出三顺后面的牌
				for (int i = key + sanshun * 3; i < cards.length; i++) {
					otherCards[i - sanshun * 3] = cards[i];
				}
				// 判断其他牌是否一串单牌
				if (isDissimilar(otherCards)) {
					// 判断单牌个数是否对应三顺个数
					if (sanshun == otherCards.length) {
						return true;
					}
				}
				// 判断其他牌是否一串对牌
				if (isTwined(otherCards)) {
					// 判断对牌对数是否对应三顺个数
					if (sanshun == otherCards.length / 2) {
						return true;
					}
				}
			}

		}
		return false;
	}

	// 判断是否一串单牌
	public static boolean isDissimilar(int[] cards) {
		for (int i = 0; i < cards.length - 1; i++) {
			for (int j = i + 1; j < cards.length; j++) {
				if (getCardNumber(cards[i]) == getCardNumber(cards[j])) {
					return false;
				}
			}
		}
		return true;
	}

	// 判断是否一串对牌
	public static boolean isTwined(int[] cards) {
		for (int i = 0; i <= cards.length - 2; i += 2) {
			if (getCardNumber(cards[i]) != getCardNumber(cards[i + 1])) {
				return false;
			}
			if (i <= cards.length - 4) {
				if (getCardNumber(cards[i]) == getCardNumber(cards[i + 2])) {
					return false;
				}

			}
		}
		return true;
	}

	// 判断基本牌型，判断时已从大到小排序
	public static int getType(int[] cards) {
		// TODO Auto-generated method stub
		int len = cards.length;

		// 当牌数量为1时,单牌
		if (len == 1) {
			return CardsType.danpai;
		}

		// 当牌数量为2时,可能是对牌和火箭
		if (len == 2) {
			if (cards[0] == 53 && cards[1] == 52) {
				return CardsType.huojian;
			}
			if (getCardNumber(cards[0]) == getCardNumber(cards[1])) {
				return CardsType.duipai;
			}
		}

		// 当牌数为3时,三张
		if (len == 3) {
			if (getCardNumber(cards[0]) == getCardNumber(cards[2])) {
				return CardsType.sanzhang;
			}
		}

		// 当牌数为4时,可能是三带一或炸弹
		if (len == 4) {
			if (getCardNumber(cards[0]) == getCardNumber(cards[2])
					|| getCardNumber(cards[1]) == getCardNumber(cards[3])) {
				if (getCardNumber(cards[0]) == getCardNumber(cards[3])) {
					return CardsType.zhadan;
				}
				else {
					return CardsType.sandaiyi;
				}
			}
		}

		// 当牌数大于等于5时,判断是不是单顺
		if (len >= 5) {
			if (isDanShun(cards)) {
				return CardsType.danshun;
			}
		}

		// 当牌数等于5时，三带一对
		if (len == 5) {
			if (getCardNumber(cards[0]) == getCardNumber(cards[2])
					&& getCardNumber(cards[3]) == getCardNumber(cards[4])) {
				return CardsType.sandaiyi;
			}
			if (getCardNumber(cards[0]) == getCardNumber(cards[1])
					&& getCardNumber(cards[2]) == getCardNumber(cards[4])) {
				return CardsType.sandaiyi;
			}
		}

		// 当牌数大于等于6时,判断是不是双顺和三顺
		if (len >= 6) {
			if (isShuangShun(cards)) {
				return CardsType.shuangshun;
			}
			if (isSanShun(cards)) {
				return CardsType.sanshun;
			}

		}

		// 当牌数为6时,判断四带二
		if (len == 6) {
			if (getCardNumber(cards[0]) == getCardNumber(cards[3])
					|| getCardNumber(cards[1]) == getCardNumber(cards[4])
					|| getCardNumber(cards[2]) == getCardNumber(cards[5])) {
				return CardsType.sidaier;
			}
		}

		// 当牌数为7时只能是单顺，已判断过

		// 当牌数大于等于8,判断是不是飞机
		if (len >= 8) {
			if (isFeiJi(cards)) {
				return CardsType.feiji;
			}
		}

		// 当牌数等于8,判断是不是四代二
		if (len == 8) {
			int key = 0;
			boolean ifFound = false;
			for (int i = 0; i <= cards.length - 4; i++) {
				if (getCardNumber(cards[i]) == getCardNumber(cards[i + 3])) {
					ifFound = true;
					key = i;
					break;
				}
			}
			if (ifFound == true) {
				// 用于存放除炸弹外的其他牌
				int[] otherCards = new int[4];
				// 取出炸弹前面的牌
				for (int i = 0; i < key; i++) {
					otherCards[i] = cards[i];
				}
				// 取出三顺后面的牌
				for (int i = key + 4; i < cards.length; i++) {
					otherCards[i - 4] = cards[i];
				}
				// 判断其他牌是否一串对牌
				if (isTwined(otherCards)) {
					return CardsType.sidaier;

				}
			}

		}
		// 如果不是规定牌型,返回错误型
		return CardsType.error;
	}

	public static int getValue(int[] cards) {
		// TODO Auto-generated method stub
		int type;
		type = getType(cards);
		// 这几种类型直接返回第一个值
		if (type == CardsType.danpai || type == CardsType.duipai || type == CardsType.sanzhang
				|| type == CardsType.danshun || type == CardsType.shuangshun
				|| type == CardsType.sanshun || type == CardsType.zhadan) {
			return getCardNumber(cards[0]);
		}
		// 三带一和飞机返回数量为3的牌的最大牌值
		if (type == CardsType.sandaiyi || type == CardsType.feiji) {
			for (int i = 0; i <= cards.length - 3; i++) {
				if (getCardNumber(cards[i]) == getCardNumber(cards[i + 2])) {
					return getCardNumber(cards[i]);
				}
			}
		}
		// 四带二返回数量为4的牌值
		if (type == CardsType.sidaier) {
			for (int i = 0; i <= cards.length - 4; i++) {
				if (getCardNumber(cards[i]) == getCardNumber(cards[i + 3])) {
					return getCardNumber(cards[i]);
				}
			}
		}
		return 0;
	}

	/**
	 * 是不是一个有效的牌型
	 * 
	 * @param cards
	 * @return
	 */
	public static boolean isCard(int[] cards) {
		if (getType(cards) == CardsType.error)
			return false;
		return true;
	}

	/**
	 * 返回true 前者牌大
	 * 
	 * @param f
	 * @param s
	 * @return
	 */
	public static int compare(CardsHolder f, CardsHolder s) {
		// 当两种牌型相同时
		if (f.cardsType == s.cardsType) {
			// 数量不同将无法比较
			if (f.cards.length != s.cards.length)
				return -1;
			else {
				if (f.value > s.value) {
					return 1;
				}
				else {
					return 0;
				}
			}

		}
		// 在牌型不同的时候,火箭最大
		if (f.cardsType == CardsType.huojian) {
			return 1;
		}
		if (s.cardsType == CardsType.huojian) {
			return 0;
		}
		// 在牌型不同的时候,排除火箭的类型，炸弹最大
		if (f.cardsType == CardsType.zhadan) {
			return 1;
		}
		if (s.cardsType == CardsType.zhadan) {
			return 0;
		}
		// 无法比较的情况，默认为s大于f
		return -1;
	}

	public static int[] outCardByItsself(int cards[], Player last, Player next) {
		CardsAnalyzer analyze = CardsAnalyzer.getInstance();
		analyze.setPokes(cards);
		int cardArray[] = null;
		Vector<int[]> card_danpai = analyze.getCard_danpai();
		Vector<int[]> card_sanshun = analyze.getCard_sanshun();

		int danpai = card_danpai.size();
		int sanshun = card_sanshun.size();

		int[] miniType = analyze.getMinType(last, next);
		System.out.println("miniType:" + miniType[0] + "," + miniType[1]);
		switch (miniType[0]) {
			case CardsType.sanshun :
				// 先出三顺和飞机
				System.out.println("sanshun is over");
				if (sanshun > 0) {
					cardArray = card_sanshun.elementAt(miniType[1]);

					if (cardArray.length / 3 < danpai) {
						int[] desArray = new int[cardArray.length / 3 * 4];
						for (int i = 0; i < cardArray.length; i++) {
							desArray[i] = cardArray[i];
						}
						for (int j = 0; j < cardArray.length / 3; j++) {
							desArray[cardArray.length + j] = card_danpai.elementAt(j)[0];
						}
						CardsManager.sort(desArray);
						return desArray;
					}
					else {
						return cardArray;
					}
				}
				break;
			case CardsType.shuangshun :
				System.out.println("shuangshun is over");
				Vector<int[]> card_shuangshun = analyze.getCard_shuangshun();
				System.out.println("shuangshun:" + card_shuangshun.size());
				if (card_shuangshun.size() > 0) {
					cardArray = card_shuangshun.elementAt(miniType[1]);
					return cardArray;
				}
				break;
			case CardsType.danshun :
				System.out.println("danshun is over");
				Vector<int[]> card_danshun = analyze.getCard_danshun();
				if (card_danshun.size() > 0) {
					return card_danshun.elementAt(miniType[1]);
				}
				break;
			case CardsType.sanzhang :
				System.out.println("sanzhang is over");
				Vector<int[]> card_sanzhang = analyze.getCard_sanzhang();
				if (card_sanzhang.size() > 0) {
					int[] sanzhangArray = card_sanzhang.elementAt(miniType[1]);
					if (danpai > 0) {
						int newA[] = new int[]{sanzhangArray[0], sanzhangArray[1],
								sanzhangArray[2], card_danpai.elementAt(0)[0]};
						CardsManager.sort(newA);
						return newA;
					}
					else {
						return sanzhangArray;
					}
				}
				break;
			case CardsType.duipai :
				System.out.println("duipai is over");
				Vector<int[]> card_duipai = analyze.getCard_duipai();
				if (card_duipai.size() > 0) {
					return card_duipai.elementAt(miniType[1]);
				}
				break;
			case CardsType.danpai :
				System.out.println("danpai is over");
				if (danpai > 0) {
					return card_danpai.elementAt(miniType[1]);
				}
				break;
		}

		Vector<int[]> card_zhadan = analyze.getCard_zhadan();
		if (card_zhadan.size() > 0) {
			return card_zhadan.elementAt(0);
		}
		// 还需要判断下家的牌，是否是同盟
		return new int[]{cards[0]};
	}

	// 出牌智能
	public static int[] findTheRightCard(CardsHolder card, int cards[], Player last, Player next) {
		CardsAnalyzer cardsAnalyzer = CardsAnalyzer.getInstance();
		cardsAnalyzer.setPokes(cards);
		int c = cardsAnalyzer.remainCount();
		// 当玩家只剩下一手牌的时候，无论如何都要出牌
		if (c == 1) {
			return findBigerCards(card, cards, 100);
		}

		// 判断我该不该要牌
		if (Desk.boss != last.playerId && Desk.boss != next.playerId) {
			// 我是boss，就要要牌
			// 判断他的剩余牌数
			int pokeLength = Desk.players[card.playerId].cards.length;
			int must = pokeLength * 100 / 17;
			if (pokeLength <= 2) {
				must = 100;
			}
			return findBigerCards(card, cards, must);

		}

		if (Desk.boss == last.playerId) {
			if (card.playerId == last.playerId) {
				int pokeLength = Desk.players[card.playerId].cards.length;
				int must = pokeLength * 100 / 17;
				if (pokeLength <= 2) {
					must = 100;
				}
				return findBigerCards(card, cards, must);
			}
			else
				if (card.playerId == next.playerId) {
					if (c <= 3) {
						return findBigerCards(card, cards, 100);
					}
					return null;
				}
		}

		if (Desk.boss == next.playerId) {
			if (card.playerId == last.playerId) {
				if (card.value < 12) {
					int pokeLength = Desk.players[card.playerId].cards.length;
					int must = 100 - pokeLength * 100 / 17;
					if (pokeLength <= 4) {
						must = 0;
					}
					CardsAnalyzer ana = CardsAnalyzer.getInstance();
					ana.setPokes(next.cards);
					if (ana.remainCount() <= 1) {
						if (ana.lastCardTypeEq(card.cardsType)
								&& (Desk.boss == next.playerId || (Desk.boss != next.playerId && Desk.boss != last.playerId))) {
							return findBigerCards(card, cards, 100);
						}
					}
					else {
						return findBigerCards(card, cards, must);
					}

				}
				else {
					return null;
				}
			}
			else
				if (card.playerId == next.playerId) {
					int pokeLength = Desk.players[card.playerId].cards.length;
					int must = pokeLength * 100 / 17;
					if (pokeLength <= 2) {
						must = 100;
					}
					return findBigerCards(card, cards, must);
				}
		}
		return null;
	}

	// 从cards数组中找到比card大的一手牌
	public static int[] findBigerCards(CardsHolder card, int cards[], int must) {
		try {
			// 获取card的信息，牌值，牌型
			int[] cardPokes = card.cards;
			int cardValue = card.value;
			int cardType = card.cardsType;
			int cardLength = cardPokes.length;
			// 使用AnalyzePoke来对牌进行分析
			CardsAnalyzer analyzer = CardsAnalyzer.getInstance();
			analyzer.setPokes(cards);

			Vector<int[]> temp;
			int size = 0;
			// 根据适当牌型选取适当牌
			switch (cardType) {
				case CardsType.danpai :
					temp = analyzer.getCard_danpai();
					size = temp.size();
					for (int i = 0; i < size; i++) {
						int[] cardArray = temp.get(i);
						int v = CardsManager.getCardNumber(cardArray[0]);
						if (v > cardValue) {
							return cardArray;
						}
					}
					// 如果单牌中没有，则选择现有牌型中除火箭和4个2后的最大一个
					int st = 0;
					if (analyzer.getCountWang() == 2) {
						st += 2;
					}
					if (analyzer.getCount2() == 4) {
						st += 4;
					}
					if (CardsManager.getCardNumber(cards[st]) > cardValue)
						return new int[]{cards[st]};

					// 检查炸弹，根据紧迫性几率出牌,如果下家是和自己一伙的则顺延给下家

					break;
				case CardsType.duipai :
					temp = analyzer.getCard_duipai();
					size = temp.size();

					for (int i = 0; i < size; i++) {
						int[] cardArray = temp.get(i);
						int v = CardsManager.getCardNumber(cardArray[0]);
						if (v > cardValue) {
							return cardArray;
						}
					}

					// 如果对子中没有，则需要检查双顺
					temp = analyzer.getCard_shuangshun();
					size = temp.size();
					for (int i = 0; i < size; i++) {
						int[] cardArray = temp.get(i);
						for (int j = cardArray.length - 1; j > 0; j--) {
							int v = CardsManager.getCardNumber(cardArray[j]);
							if (v > cardValue) {
								return new int[]{cardArray[j], cardArray[j - 1]};
							}
						}
					}
					// 如果双顺中没有，则需要检查三张
					temp = analyzer.getCard_sanzhang();
					size = temp.size();
					for (int i = 0; i < size; i++) {
						int[] cardArray = temp.get(i);
						int v = CardsManager.getCardNumber(cardArray[0]);
						if (v > cardValue) {
							return new int[]{cardArray[0], cardArray[1]};
						}
					}
					// 如果三张中没有，则就考虑炸弹，下家也可以顺牌

					break;
				case CardsType.sanzhang :
					temp = analyzer.getCard_sanzhang();
					size = temp.size();
					for (int i = 0; i < size; i++) {
						int[] cardArray = temp.get(i);
						int v = CardsManager.getCardNumber(cardArray[0]);
						if (v > cardValue) {
							return cardArray;
						}
					}
					break;
				case CardsType.sandaiyi :
					if (cards.length < 4) {
						break;
					}
					boolean find = false;
					if (cardLength == 4) {
						int[] sandaiyi = new int[4];
						temp = analyzer.getCard_sanzhang();
						size = temp.size();
						for (int i = size - 1; i >= 0; i--) {
							int[] cardArray = temp.get(i);
							int v = CardsManager.getCardNumber(cardArray[0]);
							if (v > cardValue) {
								for (int j = 0; j < cardArray.length; j++) {
									sandaiyi[j] = cardArray[j];
									find = true;
								}
							}
						}
						// 没有三张满足条件
						if (!find) {
							break;
						}
						// 再找一张组合成三带一
						temp = analyzer.getCard_danpai();
						size = temp.size();
						if (size > 0) {
							int[] t = temp.get(0);
							sandaiyi[3] = t[0];
						}
						else {
							temp = analyzer.getCard_danshun();
							size = temp.size();
							for (int i = 0; i < size; i++) {
								int[] danshun = temp.get(i);
								if (danshun.length >= 6) {
									sandaiyi[3] = danshun[0];
								}
							}
						}
						// 从中随便找一个最小的
						if (sandaiyi[3] == 0) {
							for (int i = cards.length - 1; i >= 0; i--) {
								if (CardsManager.getCardNumber(cards[i]) != CardsManager
										.getCardNumber(sandaiyi[0])) {
									sandaiyi[3] = cards[i];
								}
							}
						}
						if (sandaiyi[3] != 0) {
							CardsManager.sort(sandaiyi);
							return sandaiyi;
						}
					}
					if (cardLength == 5) {
						int[] sandaidui = new int[5];
						temp = analyzer.getCard_sanzhang();
						size = temp.size();
						for (int i = size - 1; i >= 0; i--) {
							int[] cardArray = temp.get(i);
							int v = CardsManager.getCardNumber(cardArray[0]);
							if (v > cardValue) {
								for (int j = 0; j < cardArray.length; j++) {
									sandaidui[j] = cardArray[j];
									find = true;
								}
							}
						}
						// 没有三张满足条件
						if (!find) {
							break;
						}
						// 再找一对组合成三带一对
						temp = analyzer.getCard_duipai();
						size = temp.size();
						if (size > 0) {
							int[] t = temp.get(0);
							sandaidui[3] = t[0];
							sandaidui[4] = t[1];
						}
						else {
							temp = analyzer.getCard_shuangshun();
							size = temp.size();
							for (int i = 0; i < size; i += 2) {
								int[] shuangshun = temp.get(i);
								if (shuangshun.length >= 8) {
									sandaidui[3] = shuangshun[0];
									sandaidui[4] = shuangshun[1];
								}
							}
						}
						// 从中随便找一个最小的对牌
						if (sandaidui[3] == 0) {
							for (int i = cards.length - 1; i > 0; i--) {
								if (CardsManager.getCardNumber(cards[i]) != CardsManager
										.getCardNumber(sandaidui[0])
										&& CardsManager.getCardNumber(cards[i]) == CardsManager
												.getCardNumber(cards[i - 1])) {
									sandaidui[3] = cards[i];
									sandaidui[4] = cards[i - 1];
								}
							}
						}
						if (sandaidui[3] != 0) {
							CardsManager.sort(sandaidui);
							return sandaidui;
						}
					}
					break;
				case CardsType.danshun :
					temp = analyzer.getCard_danshun();
					size = temp.size();
					for (int i = 0; i < size; i++) {
						int[] danshun = temp.get(i);
						if (danshun.length == cardLength) {
							if (cardValue < CardsManager.getCardNumber(danshun[0])) {
								return danshun;
							}
						}
					}
					for (int i = 0; i < size; i++) {
						int[] danshun = temp.get(i);
						if (danshun.length > cardLength) {
							if (danshun.length < cardLength || danshun.length - cardLength >= 3) {
								if (MathUtils.nextInt(100) < must) {
									if (cardValue >= CardsManager.getCardNumber(danshun[0])) {
										continue;
									}

									int index = 0;
									for (int k = 0; k < danshun.length; k++) {
										if (cardValue < CardsManager.getCardNumber(danshun[k])) {
											index = k;
										}
										else {
											break;
										}
									}

									if (index + cardLength > danshun.length) {
										index = danshun.length - cardLength;
									}
									int[] newArray = new int[cardLength];
									int n = 0;
									for (int m = index; m < danshun.length; m++) {
										newArray[n++] = danshun[m];
									}
									return newArray;
								}
								break;
							}
							if (cardValue >= CardsManager.getCardNumber(danshun[0])) {
								continue;
							}
							int start = 0;
							if (danshun.length - cardLength == 1) {
								if (cardValue < CardsManager.getCardNumber(danshun[1])) {
									start = 1;
								}
								else {
									start = 0;
								}
							}
							else
								if (danshun.length - cardLength == 2) {
									if (cardValue < CardsManager.getCardNumber(danshun[2])) {
										start = 2;
									}
									else
										if (cardValue < CardsManager.getCardNumber(danshun[1])) {
											start = 1;
										}
										else {
											start = 0;
										}
								}
							int[] dan = new int[cardLength];
							int m = 0;
							for (int k = start; k < danshun.length; k++) {
								dan[m++] = danshun[k];
							}
							return dan;
						}
					}
					break;
				case CardsType.shuangshun :
					temp = analyzer.getCard_shuangshun();
					size = temp.size();
					for (int i = size - 1; i >= 0; i--) {
						int cardArray[] = temp.get(i);
						if (cardArray.length < cardLength) {
							continue;
						}

						if (cardValue < CardsManager.getCardNumber(cardArray[0])) {
							if (cardArray.length == cardLength) {
								return cardArray;
							}
							else {
								// int d = (cardArray.length - cardLength) / 2;
								int index = 0;
								for (int j = cardArray.length - 1; j >= 0; j--) {
									if (cardValue < CardsManager.getCardNumber(cardArray[j])) {
										index = j / 2;
										break;
									}
								}

								int total = cardArray.length / 2;
								int cardTotal = cardLength / 2;
								if (index + cardTotal > total) {
									index = total - cardTotal;
								}
								int shuangshun[] = new int[cardLength];
								int m = 0;
								for (int k = index * 2; k < cardArray.length; k++) {
									shuangshun[m++] = cardArray[k];
								}
								return shuangshun;
							}
						}
					}
					break;
				case CardsType.sanshun :
					temp = analyzer.getCard_sanshun();
					size = temp.size();
					for (int i = size - 1; i >= 0; i--) {
						int[] cardArray = temp.get(i);
						if (cardLength > cardArray.length) {
							continue;
						}

						if (cardValue < CardsManager.getCardNumber(cardArray[0])) {
							if (cardLength == cardArray.length) {
								return cardArray;
							}
							else {
								int[] newArray = new int[cardLength];
								for (int k = 0; k < cardLength; k++) {
									newArray[k] = cardArray[k];
								}
								return newArray;
							}
						}
					}
					break;
				case CardsType.feiji :
					// 暂时不处理
					break;
				case CardsType.zhadan :
					temp = analyzer.getCard_zhadan();
					size = temp.size();
					int zd[] = null;
					if (size > 0) {
						for (int i = 0; i < size; i++) {
							zd = temp.elementAt(i);
							if (cardValue < CardsManager.getCardNumber(zd[0])) {
								return zd;
							}
						}
					}
					break;
				case CardsType.huojian :
					return null;
				case CardsType.sidaier :
					// 暂时不处理,留待读者完成
					break;
			}
			// 如果可以一次性出完，无论如何都要，留待读者完成
			// 根据must的值来判断要牌的必要性
			boolean needZd = false;
			if (must < 90) {
				must *= 0.2;
				if (MathUtils.nextInt(100) < must) {
					needZd = true;
				}
			}
			else {
				needZd = true;
			}
			if (needZd) {
				temp = analyzer.getCard_zhadan();
				size = temp.size();
				if (size > 0) {
					return temp.elementAt(size - 1);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
