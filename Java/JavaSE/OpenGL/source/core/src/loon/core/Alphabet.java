package loon.core;

public class Alphabet {
	
	private final static char[] DEFAULT = ("abcdefghijklmnopqrstuvwxyz"
			+ "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "`1234567890-=" + "~!@#$%^&*()_+"
			+ "[]{}\\|" + ":;\"'" + "<>,.?/ ").toCharArray();
	
	private char[] alphabet = DEFAULT;
	
	public static final Alphabet ENGLISH = new Alphabet();

	public static final Alphabet GERMAN = new Alphabet(new char[] { 0x00C4,
			0x00D6, 0x00DC, 0x00E4, 0x00F6, 0x00FC, 0x00DF });

	public static final Alphabet FRENCH = new Alphabet(new char[] { 0x00C0,
			0x00C2, 0x00C6, 0x00C8, 0x00C9, 0x00CA, 0x00CB, 0x00CE, 0x00CF,
			0x00D4, 0x0152, 0x00D9, 0x00DB, 0x00DC, 0x0178, 0x00C7, 0x00E0,
			0x00E2, 0x00E6, 0x00E8, 0x00E9, 0x00EA, 0x00EB, 0x00EE, 0x00EF,
			0x00F4, 0x0153, 0x00F9, 0x00FB, 0x00FC, 0x00FF, 0x00E7 });
	public static final Alphabet ESTONIAN = new Alphabet(new char[] { 0x00F6,
			0x00E4, 0x00F5, 0x00FC, 0x00D6, 0x00C4, 0x00D5, 0x00DC });

	private static Alphabet defaultAlphabet = ENGLISH;

	public static Alphabet getDefaultAlphabet() {
		return defaultAlphabet;
	}

	public static void setDefaultAlphabet(Alphabet alphabet) {
		defaultAlphabet = alphabet;
	}

	public Alphabet() {

	}


	public Alphabet(char[] additionalChars) {
		String a = new String(additionalChars);
		a += new String(alphabet);
		alphabet = a.toCharArray();
	}

	public char[] getAlphabet() {
		return alphabet;
	}

	public boolean valid(char c) {
		for (char p : alphabet) {
			if (c == p)
				return true;
		}
		return false;
	}
}
