package loon.live2d;

import loon.live2d.base.IBaseData;
import loon.live2d.draw.IDrawData;
import loon.live2d.id.*;
import loon.live2d.io.*;
import loon.live2d.model.*;
import loon.utils.TArray;

public class Live2DObject implements IOBase
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	PartsDataID _partsDataID;

	TArray<IBaseData> _listBase;
    TArray<IDrawData> _listDraw;
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
    
    public TArray<IBaseData> listBase() {
        return this._listBase;
    }
    
    public TArray<IDrawData> listDraw() {
        return this._listDraw;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
    public void readV2(final BReader br) {
        this._partsDataID = (PartsDataID)br.reader();
        this._listDraw = (TArray)br.reader();
        this._listBase = ((TArray)br.reader());
    }
    
    public void update(final PartsData partsData) {
        partsData.setBaseData(this._listBase);
        partsData.setDrawData(this._listDraw);
        this._listBase = null;
        this._listDraw = null;
    }
}
