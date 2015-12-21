package loon.live2d.base;

import loon.live2d.*;
import loon.live2d.context.*;
import loon.live2d.id.*;
import loon.live2d.io.*;

public abstract class IBaseData implements loon.live2d.io.IOBase
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int k = -2;
    public static final int l = 1;
    public static final int m = 2;
    protected BaseDataID n;
    protected BaseDataID o;
    protected boolean p;
    float[] q;
    
    public IBaseData() {
        this.n = null;
        this.o = null;
        this.p = true;
        this.q = null;
    }
    
    @Override
    public void readV2(final BReader br) {
        this.n = (BaseDataID)br.reader();
        this.o = (BaseDataID)br.reader();
    }
    
    protected void a(final BReader bReader) {
        if (bReader.getVersion() >= 10) {
            this.q = bReader.readFloats();
        }
    }
    
    public abstract IBaseContext a(final ModelContext p0);
    
    public abstract void a(final ModelContext p0, final IBaseContext p1);
    
    protected void a(final ModelContext modelContext, final loon.live2d.param.ParamIOList b, final IBaseContext baseContext, final boolean[] array) {
        if (this.q == null) {
            baseContext.b(1.0f);
        }
        else {
            baseContext.b(loon.live2d.util.ModelContextUtil.a(modelContext, b, array, this.q));
        }
    }
    
    public abstract void b(final ModelContext p0, final IBaseContext p1);
    
    public abstract void a(final ModelContext p0, final IBaseContext p1, final float[] p2, final float[] p3, final int p4, final int p5, final int p6);
    
    public abstract int b();
    
    public void a(final BaseDataID o) {
        this.o = o;
    }
    
    public void b(final BaseDataID n) {
        this.n = n;
    }
    
    public BaseDataID d() {
        return this.o;
    }
    
    public BaseDataID e() {
        return this.n;
    }
    
    public boolean f() {
        return this.o != null && this.o != BaseDataID.DST_BASE_ID();
    }
}
