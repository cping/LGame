package loon.live2d.draw;

import loon.live2d.*;
import loon.live2d.graphics.*;
import loon.live2d.id.*;
import loon.live2d.io.*;

public abstract class IDrawData implements IOBase
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int r = -2;
    public static final int s = 500;
    public static final int t = 2;
    public static final int u = 3;
    public static final int v = 4;
    static int w;
    static int x;
    protected loon.live2d.param.ParamIOList y;
    protected int z;
    DrawDataID A;
    BaseDataID _baseid;
    DrawDataID C;
    int[] D;
    float[] E;
    
    static {
        IDrawData.w = 500;
        IDrawData.x = 500;
    }
    
    public IDrawData() {
        this.y = null;
        this.C = null;
    }
    
    @Override
    public void readV2(final BReader br) {
        this.A = (DrawDataID)br.reader();
        this._baseid = (BaseDataID)br.reader();
        this.y = (loon.live2d.param.ParamIOList)br.reader();
        this.z = br.readInt();
        this.D = br.readInts();
        this.E = br.readFloats();
        if (br.getVersion() >= 11) {
            this.C = (DrawDataID)br.reader();
        }
        this.a(this.D);
    }
    
    public abstract IDrawContext a(final ModelContext p0);
    
    public void loadModel(final ModelContext modelContext, final IDrawContext drawContext) {
        drawContext.i[0] = false;
        drawContext.g = loon.live2d.util.ModelContextUtil.loadModel(modelContext, this.y, drawContext.i, this.D);
        if (!Live2D.L2D_OUTSIDE_PARAM_AVAILABLE && drawContext.i[0]) {
            return;
        }
        drawContext.h = loon.live2d.util.ModelContextUtil.a(modelContext, this.y, drawContext.i, this.E);
    }
    
    public void b(final ModelContext modelContext, final IDrawContext drawContext) {
    }
    
    public DrawDataID i() {
        return this.A;
    }
    
    public void a(final DrawDataID a) {
        this.A = a;
    }
    
    public float c(final ModelContext modelContext, final IDrawContext drawContext) {
        return drawContext.h;
    }
    
    public int d(final ModelContext modelContext, final IDrawContext drawContext) {
        return drawContext.g;
    }
    
    private void a(final int[] array) {
        for (int i = array.length - 1; i >= 0; --i) {
            final int n = array[i];
            if (n < IDrawData.w) {
                IDrawData.w = n;
            }
            else if (n > IDrawData.x) {
                IDrawData.x = n;
            }
        }
    }
    
    public BaseDataID getBaseId() {
        return this._baseid;
    }
    
    public void setBaseId(final BaseDataID b) {
        this._baseid = b;
    }
    
    public boolean existBaseId() {
        return this._baseid != null && this._baseid != BaseDataID.DST_BASE_ID();
    }
    
    public static int l() {
        return IDrawData.w;
    }
    
    public static int m() {
        return IDrawData.x;
    }
    
    public abstract void loadDraw(final DrawParam p0, final ModelContext p1, final IDrawContext p2);
    
    public abstract int e();
    
    public abstract void a(final ModelContext p0, final IDrawContext p1, final float p2);
    
    public DrawDataID n() {
        return this.C;
    }
    
    public void b(final DrawDataID c) {
        this.C = c;
    }
}
