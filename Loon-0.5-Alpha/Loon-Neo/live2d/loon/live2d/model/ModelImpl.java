package loon.live2d.model;

import loon.live2d.io.*;
import loon.live2d.param.*;
import loon.utils.TArray;

public class ModelImpl implements loon.live2d.io.IOBase
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static int a;
    ParamDefSet b;
    TArray list;
    int d;
    int e;
    
    static {
        ModelImpl.a = 0;
    }
    
    public ModelImpl() {
        this.b = null;
        this.list = null;
        this.d = 400;
        this.e = 400;
        ++ModelImpl.a;
    }
    
    public void initDirect() {
        if (this.b == null) {
            this.b = new ParamDefSet();
        }
        if (this.list == null) {
            this.list = new TArray();
        }
    }
    
    public float getCanvasWidth() {
        return this.d;
    }
    
    public float getCanvasHeight() {
        return this.e;
    }
    
    @Override
    public void readV2(final BReader br) {
        this.b = (ParamDefSet)br.reader();
        this.list = (TArray)br.reader();
        this.d = br.readInt();
        this.e = br.readInt();
    }
    
    public void addPartsData(final PartsData parts) {
        this.list.add(parts);
    }
    
    public TArray getPartsDataList() {
        return this.list;
    }
    
    public ParamDefSet getParamDefSet() {
        return this.b;
    }
}
