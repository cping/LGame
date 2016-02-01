package org.test.wuziqi;

public class Chess implements Comparable<Chess> {
	public static final int BLACK = 1;
	public static final int WHITE = 2;
	public static final int BORDER = -1;
	public static final int EMPTY = 0;
	protected int x;
	protected int y;
	protected int offense;// 攻击分
	protected int defence;// 防守分
	protected int sum;// 综合分
	protected int side;// 落子
	private StringBuilder detail;// 该点的落子估值

	public Chess(int x, int y) {
		this.x = x;
		this.y = y;
		detail = new StringBuilder();
	}

	public int getOffense() {
		return offense;
	}

	public void setOffense(int offense) {
		this.offense = offense;
	}

	public int getDefence() {
		return defence;
	}

	public void setDefence(int defence) {
		this.defence = defence;
	}

	public int getSum() {
		return sum;
	}

	public void setSum(int sum) {
		this.sum = sum;
	}

	public int getSide() {
		return side;
	}

	public void setSide(int side) {
		this.side = side;
	}

	public String getDetail() {
		return detail.toString();
	}

	public StringBuilder append(String more) {
		return this.detail.append(more);
	}

	// 清空
	public void reset() {
		clearDetail();
		offense = defence = sum = 0;
		side = EMPTY;
	}

	public void clearDetail() {
		detail = new StringBuilder();
	}

	@Override
	public String toString() {
		return x + "," + y + "-(" + (char) (64 + x) + ","
				+ (16 - y) + ") " + offense + "," + defence + "," + sum;
	}

	public boolean isEmpty() {
		return side == EMPTY ? true : false;
	}

	// 重写比较，从大到小
	@Override
	public int compareTo(Chess o) {
		if (o == null)
			return 0;
		int val1 = sum;
		int val2 = o.getSum();
		if (val1 == val2)
			return 0;
		else if (val1 < val2)
			return 1;
		else
			return -1;
	}
}
