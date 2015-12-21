package loon;

public class IDGenerator {
	
	private static int _idNo = 0;

	private IDGenerator() {
	}

	public static int generate() {
		return ++_idNo;
	}
	
	public static int getID(){
		return _idNo;
	}
	
}
