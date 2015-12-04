package loon.live2d;

import loon.live2d.id.*;
import loon.live2d.io.*;
import loon.live2d.model.*;
import loon.utils.TArray;

@SuppressWarnings("rawtypes")
public class Live2DObject implements IOBase
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	PartsDataID _partsDataID;

	TArray _listBase;
    TArray _listDraw;
    static int index;
    
    static {
    	index = 0;
    }
    
    public Live2DObject() {
        this._partsDataID = null;
        this._listBase = null;
        this._listDraw = null;
        ++loon.live2d.Live2DObject.index;
    }
    
    public TArray listBase() {
        return this._listBase;
    }
    
    public TArray listDraw() {
        return this._listDraw;
    }
    
    @Override
    public void readV2(final BReader br) {
        this._partsDataID = (PartsDataID)br.reader();
        this._listDraw = (TArray)br.reader();
        this._listBase = (TArray)br.reader();
    }
    
    public void update(final PartsData partsData) {
        partsData.setBaseData(this._listBase);
        partsData.setDrawData(this._listDraw);
        this._listBase = null;
        this._listDraw = null;
    }
}
