package loon.live2d.param;

import loon.live2d.id.*;
import loon.live2d.io.*;

public class ParamDefFloat implements loon.live2d.io.IOBase
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	float a;
    float b;
    float c;
    ParamID d;
    
    public ParamDefFloat() {
    }
    
    public ParamDefFloat(final ParamID pid, final float min, final float max, final float defaultV) {
        this.d = pid;
        this.a = min;
        this.b = max;
        this.c = defaultV;
    }
    
    @Override
    public void readV2(final BReader br) {
        this.a = br.readerFloat();
        this.b = br.readerFloat();
        this.c = br.readerFloat();
        this.d = (ParamID)br.reader();
    }
    
    public float getMinValue() {
        return this.a;
    }
    
    public float getMaxValue() {
        return this.b;
    }
    
    public float getDefaultValue() {
        return this.c;
    }
    
    public ParamID getParamID() {
        return this.d;
    }
}
