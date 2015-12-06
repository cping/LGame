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
    TArray<PartsData> list;
    int width;
    int heigth;
    
    static {
        ModelImpl.a = 0;
    }
    
    public ModelImpl() {
        this.b = null;
        this.list = null;
        this.width = 400;
        this.heigth = 400;
        ++ModelImpl.a;
    }
    
    public void initDirect() {
        if (this.b == null) {
            this.b = new ParamDefSet();
        }
        if (this.list == null) {
            this.list = new TArray<PartsData>();
        }
    }
    
    public float getCanvasWidth() {
        return this.width;
    }
    
    public float getCanvasHeight() {
        return this.heigth;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    public void readV2(final BReader br) {
        this.b = (ParamDefSet)br.reader();
        this.list = (TArray)br.reader();
        this.width = br.readInt();
        this.heigth = br.readInt();
    }
    
    public void addPartsData(final PartsData parts) {
        this.list.add(parts);
    }
    
    public TArray<PartsData> getPartsDataList() {
        return this.list;
    }
    
    public ParamDefSet getParamDefSet() {
        return this.b;
    }
}
