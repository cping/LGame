package loon.live2d.param;

import loon.live2d.io.*;
import loon.utils.TArray;

public class ParamDefSet implements loon.live2d.io.IOBase
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	TArray<ParamDefFloat> list;
    
    public ParamDefSet() {
        this.list = null;
    }
    
    public TArray<ParamDefFloat> getParamDefFloatList() {
        return this.list;
    }
    
    public void initDirect() {
        this.list = new TArray<ParamDefFloat>();
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    public void readV2(final BReader br) {
        this.list = (TArray)br.reader();
    }
    
    public void addParamDefFloat_TestImpl(final ParamDefFloat pdf) {
        this.list.add(pdf);
    }
}
