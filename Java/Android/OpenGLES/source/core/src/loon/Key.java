package loon;

public class Key {

	public static final int KEY_DOWN = 0;

	public static final int KEY_UP = 1;

	public static final int KEY_TYPED = 2;

	public static final int ANY_KEY = -1;

	public static final int NUM_0 = 7;

	public static final int NUM_1 = 8;

	public static final int NUM_2 = 9;

	public static final int NUM_3 = 10;

	public static final int NUM_4 = 11;

	public static final int NUM_5 = 12;

	public static final int NUM_6 = 13;

	public static final int NUM_7 = 14;

	public static final int NUM_8 = 15;

	public static final int NUM_9 = 16;

	public static final int A = 29;

	public static final int ALT_LEFT = 57;

	public static final int ALT_RIGHT = 58;

	public static final int APOSTROPHE = 75;

	public static final int AT = 77;

	public static final int B = 30;

	public static final int BACK = 4;

	public static final int BACKSLASH = 73;

	public static final int C = 31;

	public static final int CALL = 5;

	public static final int CAMERA = 27;

	public static final int CLEAR = 28;

	public static final int COMMA = 55;

	public static final int D = 32;

	public static final int DEL = 67;

	public static final int BACKSPACE = 67;

	public static final int FORWARD_DEL = 112;

	public static final int DPAD_CENTER = 23;

	public static final int DPAD_DOWN = 20;

	public static final int DPAD_LEFT = 21;

	public static final int DPAD_RIGHT = 22;

	public static final int DPAD_UP = 19;

	public static final int CENTER = 23;

	public static final int DOWN = 20;

	public static final int LEFT = 21;

	public static final int RIGHT = 22;

	public static final int UP = 19;

	public static final int E = 33;

	public static final int ENDCALL = 6;

	public static final int ENTER = 66;

	public static final int ENVELOPE = 65;

	public static final int EQUALS = 70;

	public static final int EXPLORER = 64;

	public static final int F = 34;

	public static final int FOCUS = 80;

	public static final int G = 35;

	public static final int GRAVE = 68;

	public static final int H = 36;

	public static final int HEADSETHOOK = 79;

	public static final int HOME = 3;

	public static final int I = 37;

	public static final int J = 38;

	public static final int K = 39;

	public static final int L = 40;

	public static final int LEFT_BRACKET = 71;

	public static final int M = 41;

	public static final int MEDIA_FAST_FORWARD = 90;

	public static final int MEDIA_NEXT = 87;

	public static final int MEDIA_PLAY_PAUSE = 85;

	public static final int MEDIA_PREVIOUS = 88;

	public static final int MEDIA_REWIND = 89;

	public static final int MEDIA_STOP = 86;

	public static final int MENU = 82;

	public static final int MINUS = 69;

	public static final int MUTE = 91;

	public static final int N = 42;

	public static final int NOTIFICATION = 83;

	public static final int NUM = 78;

	public static final int O = 43;

	public static final int P = 44;

	public static final int PERIOD = 56;

	public static final int PLUS = 81;

	public static final int POUND = 18;

	public static final int POWER = 26;

	public static final int Q = 45;

	public static final int R = 46;

	public static final int RIGHT_BRACKET = 72;

	public static final int S = 47;

	public static final int SEARCH = 84;

	public static final int SEMICOLON = 74;

	public static final int SHIFT_LEFT = 59;

	public static final int SHIFT_RIGHT = 60;

	public static final int SLASH = 76;

	public static final int SOFT_LEFT = 1;

	public static final int SOFT_RIGHT = 2;

	public static final int SPACE = 62;

	public static final int STAR = 17;

	public static final int SYM = 63;

	public static final int T = 48;

	public static final int TAB = 61;

	public static final int U = 49;

	public static final int UNKNOWN = 0;

	public static final int V = 50;

	public static final int VOLUME_DOWN = 25;

	public static final int VOLUME_UP = 24;

	public static final int W = 51;

	public static final int X = 52;

	public static final int Y = 53;

	public static final int Z = 54;

	public static final int META_ALT_LEFT_ON = 16;

	public static final int META_ALT_ON = 2;

	public static final int META_ALT_RIGHT_ON = 32;

	public static final int META_SHIFT_LEFT_ON = 64;

	public static final int META_SHIFT_ON = 1;

	public static final int META_SHIFT_RIGHT_ON = 128;

	public static final int META_SYM_ON = 4;

	public static final int CONTROL_LEFT = 129;

	public static final int CONTROL_RIGHT = 130;

	public static final int ESCAPE = 131;

	public static final int END = 132;

	public static final int INSERT = 133;

	public static final int PAGE_UP = 92;

	public static final int PAGE_DOWN = 93;

	public static final int PICTSYMBOLS = 94;

	public static final int SWITCH_CHARSET = 95;

	public static final int BUTTON_A = 96;

	public static final int BUTTON_B = 97;

	public static final int BUTTON_C = 98;

	public static final int BUTTON_X = 99;

	public static final int BUTTON_Y = 100;

	public static final int BUTTON_Z = 101;

	public static final int BUTTON_L1 = 102;

	public static final int BUTTON_R1 = 103;

	public static final int BUTTON_L2 = 104;

	public static final int BUTTON_R2 = 105;

	public static final int BUTTON_THUMBL = 106;

	public static final int BUTTON_THUMBR = 107;

	public static final int BUTTON_START = 108;

	public static final int BUTTON_SELECT = 109;

	public static final int BUTTON_MODE = 110;

	public static final int BUTTON_CIRCLE = 255;

	public static char getKeyChar() {
		return AndroidInputFactory.finalKey.keyChar;
	}

	public static int getKeyCode() {
		return AndroidInputFactory.finalKey.keyCode;
	}

	public static int getType() {
		return AndroidInputFactory.finalKey.keyCode;
	}

	public static boolean isDown() {
		return AndroidInputFactory.finalKey.isDown();
	}

	public static boolean isUp() {
		return AndroidInputFactory.finalKey.isUp();
	}

	public static void clear() {
		AndroidInputFactory.keys.clear();
	}

	public static void addKey(int key) {
		AndroidInputFactory.keys.add(key);
	}

	public static void removeKey(int key) {
		AndroidInputFactory.keys.removeValue(key);
	}

	public static boolean isKeyPressed(int key) {
		if (key == Key.ANY_KEY) {
			return AndroidInputFactory.keys.length > 0 && AndroidInputFactory.only_key.isPressed();
		} else {
			return AndroidInputFactory.keys.contains(key) && AndroidInputFactory.only_key.isPressed();
		}
	}

	public static boolean isKeyRelease(int key) {
		if (key == Key.ANY_KEY) {
			return AndroidInputFactory.keys.length > 0 && !AndroidInputFactory.only_key.isPressed();
		} else {
			return AndroidInputFactory.keys.contains(key) && !AndroidInputFactory.only_key.isPressed();
		}
	}

}
