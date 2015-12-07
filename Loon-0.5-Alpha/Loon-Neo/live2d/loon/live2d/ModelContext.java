package loon.live2d;

import loon.live2d.base.*;
import loon.live2d.context.*;
import loon.live2d.draw.*;
import loon.live2d.framework.L2DModelMatrix;
import loon.live2d.graphics.*;
import loon.live2d.id.*;
import loon.live2d.model.*;
import loon.live2d.model.PartsData.PartsDataContext;
import loon.live2d.param.*;
import loon.opengl.GLEx;
import loon.utils.ListMap;
import loon.utils.TArray;

public class ModelContext {
	static boolean a;

	boolean h;
	ALive2DModel i;
	int j;

	int k;
	ParamID[] l;
	float[] m;
	float[] n;
	float[] o;
	float[] p;
	float[] q;
	boolean[] r;
	TArray s;
	TArray t;
	ListMap<DrawDataID, IDrawData> drawDatas;
	TArray v;
	TArray w;
	TArray x;
	TArray y;
	short[] z;
	short[] A;
	short[] B;
	short[] C;
	float[] D;
	static final boolean E = false;

	static {
		ModelContext.a = true;
	}

	public ModelContext(final ALive2DModel model) {
		this.h = true;
		this.j = -1;
		this.k = 0;
		this.l = new ParamID[32];
		this.m = new float[32];
		this.n = new float[32];
		this.o = new float[32];
		this.p = new float[32];
		this.q = new float[32];
		this.r = new boolean[32];
		this.s = new TArray();
		this.t = new TArray();
		this.v = new TArray();
		this.w = new TArray();
		this.x = new TArray();
		this.y = new TArray();
		this.C = new short[65];
		this.D = new float[10];
		this.i = model;
	}

	public int getDrawDataIndex(final DrawDataID id) {
		for (int i = this.t.size - 1; i >= 0; --i) {
			if (this.t.get(i) != null
					&& ((IDrawData) this.t.get(i)).getDrawDataID() == id) {
				return i;
			}
		}
		return -1;
	}

	public IDrawData getDrawData(final DrawDataID id) {
		if (this.drawDatas == null) {
			this.drawDatas = new ListMap<DrawDataID, IDrawData>();
			for (int size = this.t.size, i = 0; i < size; ++i) {
				final IDrawData drawData = (IDrawData) this.t.get(i);
				final DrawDataID j = drawData.getDrawDataID();
				if (j != null) {
					this.drawDatas.put(j, drawData);
				}
			}
		}
		return this.drawDatas.get(id);
	}

	public IDrawData getDrawData(final int drawIndex) {
		if (drawIndex < this.t.size) {
			return (IDrawData) this.t.get(drawIndex);
		}
		return null;
	}

	private void a() {
		this.s.clear();
		this.t.clear();
		this.v.clear();
		if (this.drawDatas != null) {
			this.drawDatas.clear();
		}
		this.w.clear();
		this.x.clear();
		this.y.clear();
	}

	public void init() {
		++this.j;
		if (this.v.size > 0) {
			this.a();
		}
		final ModelImpl modelImpl = this.i.getModelImpl();
		final TArray partsDataList = modelImpl.getPartsDataList();
		final int size = partsDataList.size;
		final TArray<IBaseData> list = new TArray<IBaseData>();
		final TArray<IBaseContext> list2 = new TArray<IBaseContext>();
		for (int i = 0; i < size; ++i) {
			final PartsData partsData = (PartsData) partsDataList.get(i);
			this.v.add(partsData);
			this.y.add(partsData.init(this));
			final TArray baseData = partsData.getBaseData();
			final int size2 = baseData.size;
			for (int j = 0; j < size2; ++j) {
				list.add((IBaseData) baseData.get(j));
			}
			for (int k = 0; k < size2; ++k) {
				final IBaseContext a = ((IBaseData) baseData.get(k)).a(this);
				a.a(i);
				list2.add(a);
			}
			final TArray drawData = partsData.getDrawData();
			for (int size3 = drawData.size, l = 0; l < size3; ++l) {
				final IDrawData drawData2 = (IDrawData) drawData.get(l);
				final IDrawContext a2 = drawData2.a(this);
				a2.f = i;
				this.t.add(drawData2);
				this.x.add(a2);
			}
		}
		final int size4 = list.size;
		final BaseDataID dst_BASE_ID = BaseDataID.DST_BASE_ID();
		boolean b;
		do {
			b = false;
			for (int n = 0; n < size4; ++n) {
				final IBaseData baseData2 = list.get(n);
				if (baseData2 != null) {
					final BaseDataID d = baseData2.d();
					if (d == null || d == dst_BASE_ID
							|| this.getBaseDataIndex(d) >= 0) {
						this.s.add(baseData2);
						this.w.add(list2.get(n));
						list.set(n, null);
						b = true;
					}
				}
			}
		} while (b);
		final ParamDefSet paramDefSet = modelImpl.getParamDefSet();
		if (paramDefSet != null) {
			final TArray<ParamDefFloat> paramDefFloatList = paramDefSet
					.getParamDefFloatList();
			if (paramDefFloatList != null) {
				for (int size5 = paramDefFloatList.size, n2 = 0; n2 < size5; ++n2) {
					final ParamDefFloat paramDefFloat = paramDefFloatList
							.get(n2);
					if (paramDefFloat != null) {
						this.addFloatParam(paramDefFloat.getParamID(),
								paramDefFloat.getDefaultValue(),
								paramDefFloat.getMinValue(),
								paramDefFloat.getMaxValue());
					}
				}
			}
		}
		this.h = true;
	}

	public boolean update() {
		for (int length = this.m.length, i = 0; i < length; ++i) {
			if (this.m[i] != this.n[i]) {
				this.r[i] = true;
				this.n[i] = this.m[i];
			}
		}
		final boolean b = false;
		final int size = this.s.size;
		final int size2 = this.t.size;
		final int l = IDrawData.l();
		final int n = IDrawData.m() - l + 1;
		if (this.z == null || this.z.length < n) {
			this.z = new short[n];
			this.A = new short[n];
		}
		for (int j = 0; j < n; ++j) {
			this.z[j] = -1;
			this.A[j] = -1;
		}
		if (this.B == null || this.B.length < size2) {
			this.B = new short[size2];
		}
		for (int k = 0; k < size2; ++k) {
			this.B[k] = -1;
		}
		Throwable t = null;
		for (int n2 = 0; n2 < size; ++n2) {
			final IBaseData baseData = (IBaseData) this.s.get(n2);
			final IBaseContext baseContext = (IBaseContext) this.w.get(n2);
			try {
				baseData.a(this, baseContext);
				baseData.b(this, baseContext);
			} catch (Exception ex) {
				if (t == null) {
					t = ex;
				}
			}
		}
		if (t != null && ModelContext.a) {
			t.printStackTrace();
		}
		Throwable t2 = null;
		for (int n3 = 0; n3 < size2; ++n3) {
			final IDrawData drawData = (IDrawData) this.t.get(n3);
			final IDrawContext drawContext = (IDrawContext) this.x.get(n3);
			try {
				drawData.loadModel(this, drawContext);
				if (!drawContext.b()) {
					drawData.b(this, drawContext);
					final int n4 = drawData.d(this, drawContext) - l;
					short n5;
					try {
						n5 = this.A[n4];
					} catch (Exception ex2) {
						final int n6 = drawData.d(this, drawContext) - l;
						continue;
					}
					if (n5 == -1) {
						this.z[n4] = (short) n3;
					} else {
						this.B[n5] = (short) n3;
					}
					this.A[n4] = (short) n3;
				}
			} catch (Exception ex3) {
				if (t2 == null) {
					t2 = ex3;
					Live2D.setError(Live2D.L2D_ERROR_DDTEXTURE_SETUP_TRANSFORM_FAILED);
				}
			}
		}
		if (t2 != null && ModelContext.a) {
			t2.printStackTrace();
		}
		for (int n7 = this.r.length - 1; n7 >= 0; --n7) {
			this.r[n7] = false;
		}
		this.h = false;
		return b;
	}

	public void draw(final L2DModelMatrix matrix, final GLEx gl,
			final DrawParam dp) {
		if (this.z == null) {
			return;
		}
		final int length = this.z.length;
		for (int i = 0; i < length; ++i) {
			short n = this.z[i];
			if (n != -1) {
				for (;;) {
					final IDrawData drawData = (IDrawData) this.t.get(n);
					final IDrawContext drawContext = (IDrawContext) this.x
							.get(n);
					if (drawContext.exist()) {
						drawContext.j = ((PartsData.PartsDataContext) this.y
								.get(drawContext.f)).getPartsOpacity();
						drawData.loadDraw(matrix, gl, dp, this, drawContext);
					}
					final short n2 = this.B[n];
					if (n2 <= n) {
						break;
					}
					if (n2 == -1) {
						break;
					}
					n = n2;
				}
			}
		}
	}

	public int getParamIndex(final ParamID paramID) {
		for (int i = this.l.length - 1; i >= 0; --i) {
			if (this.l[i] == paramID) {
				return i;
			}
		}
		return this.addFloatParam(paramID, 0.0f, -1000000.0f, 1000000.0f);
	}

	public int getBaseIndex(final BaseDataID baseID) {
		return this.getBaseDataIndex(baseID);
	}

	public int getBaseDataIndex(final BaseDataID baseID) {
		for (int i = this.s.size - 1; i >= 0; --i) {
			if (this.s.get(i) != null
					&& ((IBaseData) this.s.get(i)).e() == baseID) {
				return i;
			}
		}
		return -1;
	}

	float[] a(final float[] array, final int n) {
		final float[] array2 = new float[n];
		System.arraycopy(array, 0, array2, 0, array.length);
		return array2;
	}

	public int addFloatParam(final ParamID id, final float value,
			final float min, final float max) {
		if (this.k >= this.l.length) {
			final int length = this.l.length;
			final ParamID[] l = new ParamID[length * 2];
			System.arraycopy(this.l, 0, l, 0, length);
			this.l = l;
			this.m = this.a(this.m, length * 2);
			this.n = this.a(this.n, length * 2);
			this.o = this.a(this.o, length * 2);
			this.p = this.a(this.p, length * 2);
			final boolean[] r = new boolean[length * 2];
			System.arraycopy(this.r, 0, r, 0, length);
			this.r = r;
		}
		this.l[this.k] = id;
		this.m[this.k] = value;
		this.n[this.k] = value;
		this.o[this.k] = min;
		this.p[this.k] = max;
		this.r[this.k] = true;
		return this.k++;
	}

	public void setBaseData(final int baseDataIndex, final IBaseData baseData) {
		this.s.set(baseDataIndex, baseData);
	}

	public void setParamFloat(final int paramIndex, float value) {
		if (Float.isNaN(value)) {
			value = 0.0f;
		}
		if (value < this.o[paramIndex]) {
			value = this.o[paramIndex];
		}
		if (value > this.p[paramIndex]) {
			value = this.p[paramIndex];
		}
		this.m[paramIndex] = value;
	}

	public void loadParam() {
		int n = this.m.length;
		if (n > this.q.length) {
			n = this.q.length;
		}
		System.arraycopy(this.q, 0, this.m, 0, n);
	}

	public void saveParam() {
		final int length = this.m.length;
		if (length > this.q.length) {
			this.q = new float[length];
		}
		System.arraycopy(this.m, 0, this.q, 0, length);
	}

	public int getInitVersion() {
		return this.j;
	}

	public boolean requireSetup() {
		return this.h;
	}

	public boolean isParamUpdated(final int paramIndex) {
		return this.r[paramIndex];
	}

	public short[] getTmpPivotTableIndicesRef() {
		return this.C;
	}

	public float[] getTmpT_ArrayRef() {
		return this.D;
	}

	public IBaseData getBaseData(final int baseDataIndex) {
		return (IBaseData) this.s.get(baseDataIndex);
	}

	public float getParamFloat(final int paramIndex) {
		return this.m[paramIndex];
	}

	public float getParamMax(final int paramIndex) {
		return this.p[paramIndex];
	}

	public float getParamMin(final int paramIndex) {
		return this.o[paramIndex];
	}

	public void setPartsOpacity(final int partsIndex, final float opacity) {
		((PartsDataContext) this.y.get(partsIndex)).setPartsOpacity(opacity);
	}

	public float getPartsOpacity(final int partsIndex) {
		return ((PartsDataContext) this.y.get(partsIndex)).getPartsOpacity();
	}

	public int getPartsDataIndex(final PartsDataID partsID) {
		for (int i = this.v.size - 1; i >= 0; --i) {
			if (this.v.get(i) != null
					&& ((PartsData) this.v.get(i)).getPartsDataID() == partsID) {
				return i;
			}
		}
		return -1;
	}

	public IBaseContext getBaseContext(final int baseDataIndex) {
		return (IBaseContext) this.w.get(baseDataIndex);
	}

	public IDrawContext getDrawContext(final int drawDataIndex) {
		return (IDrawContext) this.x.get(drawDataIndex);
	}

	public PartsData.PartsDataContext getPartsContext(final int partsDataIndex) {
		return (PartsDataContext) this.y.get(partsDataIndex);
	}

	public void updateZBuffer_TestImpl(final float startZ, final float stepZ) {
		final int length = this.z.length;
		float n = startZ;
		for (int i = 0; i < length; ++i) {
			short n2 = this.z[i];
			if (n2 != -1) {
				while (true) {
					final IDrawContext drawContext = (IDrawContext) this.x
							.get(n2);
					if (drawContext.exist()) {
						drawContext.d().a(this, drawContext, n);
						n += stepZ;
					}
					final short n3 = this.B[n2];
					if (n3 <= n2) {
						break;
					}
					if (n3 == -1) {
						break;
					}
					n2 = n3;
				}
			}
		}
	}
}
