package loon.nscripter;

import loon.canvas.LColor;
import loon.nscripter.variables.sprites.Sprite;
import loon.utils.TArray;

public class ValuesParser {

    public static TArray<String> getParams(TArray<String> value, TArray<String> mask)
    {
    	return null;
    }
    
    public static String getString(String multi)
    {
        String buf = String.valueOf(multi.charAt(0)), result = null;
        int j = 1;

        while (true)
        {
            if (j == multi.length())
            {
                result += stringVar(buf);
                break;
            }
            else
            {
                if (multi.charAt(j) == '+')
                {
                    result += stringVar(buf);
                    buf = null;
                }
                else
                {
                    buf += multi.charAt(j);
                }
                j++;
            }
        }

        return result;
    }
    
    public static String stringVar(String buffer){
    	
    	return null;
    }

	public static double getNumber(String string) {
		return -1;
	}

	public static Sprite getSprite(String string) {
		return null;
	}

	public static LColor getColor(String string) {
		return null;
	}
}
