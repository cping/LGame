package loon.live2d.util;

public class StringUtil
{
	
	public static boolean indexOf(final byte[] array, final int n,
			final String s) {
		final int n2 = n + s.length();
		if (n2 >= array.length) {
			return false;
		}
		for (int i = n; i < n2; ++i) {
			if (array[i] != s.charAt(i - n)) {
				return false;
			}
		}
		return true;
	}

    public static double indexOf(final byte[] array, final int n, final int n2, final int[] array2) {
        int i = n2;
        boolean b = false;
        boolean b2 = false;
        double n3 = 0.0;
        if ((char)(array[i] & 0xFF) == '-') {
            b = true;
            ++i;
        }
    Label_0281:
        for (;i < n;) {
            switch ((char)(array[i] & 0xFF)) {
                case '0': {
                    n3 *= 10.0;
                    break;
                }
                case '1': {
                    n3 = n3 * 10.0 + 1.0;
                    break;
                }
                case '2': {
                    n3 = n3 * 10.0 + 2.0;
                    break;
                }
                case '3': {
                    n3 = n3 * 10.0 + 3.0;
                    break;
                }
                case '4': {
                    n3 = n3 * 10.0 + 4.0;
                    break;
                }
                case '5': {
                    n3 = n3 * 10.0 + 5.0;
                    break;
                }
                case '6': {
                    n3 = n3 * 10.0 + 6.0;
                    break;
                }
                case '7': {
                    n3 = n3 * 10.0 + 7.0;
                    break;
                }
                case '8': {
                    n3 = n3 * 10.0 + 8.0;
                    break;
                }
                case '9': {
                    n3 = n3 * 10.0 + 9.0;
                    break;
                }
                case '.': {
                    b2 = true;
                    ++i;
                    break Label_0281;
                }
                default: {
                    break Label_0281;
                }
            }
            ++i;
        }
        Label_0507: {
            if (b2) {
                double n4 = 0.1;
                for (;i < n;) {
                    switch ((char)(array[i] & 0xFF)) {
                        case '1': {
                            n3 += n4 * 1.0;
                            break;
                        }
                        case '2': {
                            n3 += n4 * 2.0;
                            break;
                        }
                        case '3': {
                            n3 += n4 * 3.0;
                            break;
                        }
                        case '4': {
                            n3 += n4 * 4.0;
                            break;
                        }
                        case '5': {
                            n3 += n4 * 5.0;
                            break;
                        }
                        case '6': {
                            n3 += n4 * 6.0;
                            break;
                        }
                        case '7': {
                            n3 += n4 * 7.0;
                            break;
                        }
                        case '8': {
                            n3 += n4 * 8.0;
                            break;
                        }
                        case '9': {
                            n3 += n4 * 9.0;
                            break;
                        }
                        default: {
                            break Label_0507;
                        }
                    }
                    n4 *= 0.1;
                    ++i;
                }
            }
        }
        if (b) {
            n3 = -n3;
        }
        array2[0] = i;
        return n3;
    }
}
