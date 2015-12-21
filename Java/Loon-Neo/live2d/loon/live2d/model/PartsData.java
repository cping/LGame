package loon.live2d.model;

import loon.live2d.*;
import loon.live2d.base.*;
import loon.live2d.draw.*;
import loon.live2d.id.*;
import loon.live2d.io.*;
import loon.utils.TArray;

public class PartsData implements loon.live2d.io.IOBase
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static int a;
    boolean b;
    boolean c;
    PartsDataID d;
    TArray<IBaseData> e;
    TArray<IDrawData> f;
    
    static {
        PartsData.a = 0;
    }
    
    public PartsData() {
        this.b = true;
        this.c = false;
        this.d = null;
        this.e = null;
        this.f = null;
        ++PartsData.a;
    }
    
    public void initDirect() {
        this.e = new TArray<IBaseData>();
        this.f = new TArray<IDrawData>();
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
    public void readV2(final BReader br) {
        this.c = br.readBool();
        this.b = br.readBool();
        this.d = (PartsDataID)br.reader();
        this.e = (TArray)br.reader();
        this.f = (TArray)br.reader();
    }
    
    public PartsDataContext init(final ModelContext mdc) {
        final PartsDataContext partsDataContext = new PartsDataContext(this);
        partsDataContext.setPartsOpacity(this.isVisible() ? 1.0f : 0.0f);
        return partsDataContext;
    }
    
    public void addBaseData(final IBaseData baseData) {
        if (this.e == null) {
            throw new RuntimeException("baseDataList not initialized@addBaseData");
        }
        this.e.add(baseData);
    }
    
    public void addDrawData(final IDrawData drawData) {
        if (this.f == null) {
            throw new RuntimeException("drawDataList not initialized@addDrawData");
        }
        this.f.add(drawData);
    }
    
    public void setBaseData(final TArray<IBaseData> baseDataList) {
        this.e = baseDataList;
    }
    
    public void setDrawData(final TArray<IDrawData> drawDataList) {
        this.f = drawDataList;
    }
    
    public boolean isVisible() {
        return this.b;
    }
    
    public boolean isLocked() {
        return this.c;
    }
    
    public void setVisible(final boolean v) {
        this.b = v;
    }
    
    public void setLocked(final boolean v) {
        this.c = v;
    }
    
    public TArray<IBaseData> getBaseData() {
        return this.e;
    }
    
    public TArray<IDrawData> getDrawData() {
        return this.f;
    }
    
    public PartsDataID getPartsDataID() {
        return this.d;
    }
    
    public void setPartsDataID(final PartsDataID id) {
        this.d = id;
    }
    
    public PartsDataID getPartsID() {
        return this.d;
    }
    
    public void setPartsID(final PartsDataID id) {
        this.d = id;
    }
    
    public class PartsDataContext extends loon.live2d.io.IOType
    {
        float a;
        PartsData b;
        
        public PartsDataContext(final PartsData src) {
            this.b = src;
        }
        
        public float getPartsOpacity() {
            return this.a;
        }
        
        public void setPartsOpacity(final float v) {
            this.a = v;
        }
    }
}
