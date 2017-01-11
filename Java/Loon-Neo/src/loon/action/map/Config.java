package loon.action.map;
/**
 *          n
 *      wn     ne
 *    w           e
 *      sw     es
 *          s
 *
 *          TUP
 *     LEFT     UP
 *   TLEFT       TRIGHT
 *     DOWN     RIGHT
 *         TDOWN
 */
public interface Config {
	
	public static final int EMPTY = -1;
	
	public static final int LEFT = 0;

	public static final int RIGHT = 1;

	public static final int UP = 2;

	public static final int DOWN = 3;

	public static final int TLEFT = 4;

	public static final int TRIGHT = 5;

	public static final int TUP = 6;

	public static final int TDOWN = 7;
	
	public static final int WN = LEFT;
	
	public static final int ES = RIGHT;

	public static final int NE = UP;
	
	public static final int SW = DOWN;
	
	public static final int N = TUP;

	public static final int S = TDOWN;
	
	public static final int W = TLEFT;

	public static final int E = TRIGHT;
}
