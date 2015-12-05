package loon.live2d.draw;

import loon.live2d.*;
import loon.live2d.base.*;
import loon.live2d.context.*;
import loon.live2d.framework.L2DModelMatrix;
import loon.live2d.graphics.*;
import loon.live2d.id.*;
import loon.live2d.io.*;
import loon.opengl.GLEx;
import loon.utils.ListMap;
import loon.utils.TArray;

public class DrawDataImpl extends IDrawData {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static int a;
	public static final int b = 30;
	public static final int c = 0;
	public static final int d = 1;
	public static final int e = 2;
	int f;
	int g;
	int h;
	int i;
	ListMap<String, Integer> listMap;
	short[] k;
	TArray<?> l;
	float[] m;
	int n;
	boolean o;
	static boolean[] p;
	static final/* synthetic */boolean q;

	static {
		q = !DrawDataImpl.class.desiredAssertionStatus();
		loon.live2d.draw.DrawDataImpl.a = 0;
		loon.live2d.draw.DrawDataImpl.p = new boolean[1];
	}

	public DrawDataImpl() {
		this.f = -1;
		this.g = 0;
		this.h = 0;
		this.listMap = null;
		this.o = true;
		++loon.live2d.draw.DrawDataImpl.a;
	}

	public void a(final int f) {
		this.f = f;
	}

	public int a() {
		return this.f;
	}

	public float[] b() {
		return this.m;
	}

	public int c() {
		return this.i;
	}

	public int d() {
		return this.g;
	}

	@Override
	public int e() {
		return 2;
	}

	@Override
	public void a(final ModelContext modelContext,
			final IDrawContext drawContext, final float n) {
		aa locala = (aa) drawContext;
		float[] arrayOfFloat = locala.c != null ? locala.c : locala.b;
		int i1 = 1;
		switch (i1) {
		case 1:
		default:
			throw new RuntimeException("Not Implemented ");
		case 2:
		}
		for (int i2 = this.g - 1; i2 >= 0; i2--) {
			int i3 = i2 * 2;
			arrayOfFloat[(i3 + 4)] = n;
		}
	}

	public void init() {
		(this.y = new loon.live2d.param.ParamIOList()).init();
	}

	@Override
	public void readV2(final BReader br) {
		super.readV2(br);
		this.f = br.readInt();
		this.g = br.readInt();
		this.h = br.readInt();
		final int[] array = (int[]) br.reader();
		this.k = new short[this.h * 3];
		for (int i = this.h * 3 - 1; i >= 0; --i) {
			this.k[i] = (short) array[i];
		}
		this.l = (TArray<?>) br.reader();
		this.m = (float[]) br.reader();
		if (br.getVersion() >= 8) {
			this.i = br.readInt();
			if (this.i != 0) {
				if ((this.i & 0x1) != 0x0) {
					final int e = br.readInt();
					if (this.listMap == null) {
						this.listMap = new ListMap<String, Integer>();
					}
					this.listMap.put("BK_OPTION_COLOR", new Integer(e));
				}
				if ((this.i & 0x1E) != 0x0) {
					this.n = (this.i & 0x1E) >> 1;
				} else {
					this.n = 0;
				}
				if ((this.i & 0x20) != 0x0) {
					this.o = false;
				}
			}
		} else {
			this.i = 0;
		}
	}

	@Override
	public IDrawContext a(final ModelContext modelContext) {
		final aa a = new aa(this);
		final int n = this.g * 2;
		final boolean k = this.existBaseId();
		if (a.b != null) {
			a.b = null;
		}
		a.b = new float[n];
		if (a.c != null) {
			a.c = null;
		}
		int i2 = 1;
		boolean updateFix = false;
		a.c = (float[]) (k ? new float[n] : null);
		switch (i2) {
		default: {
			if (updateFix) {
				for (int i = this.g - 1; i >= 0; --i) {
					final int n2 = i << 1;
					this.m[n2 + 1] = 1.0f - this.m[n2 + 1];
				}
			}
			break;
		}
		case 2: {
			for (int listMap = this.g - 1; listMap >= 0; --listMap) {
				final int n3 = listMap << 1;
				final int n4 = listMap * 2;
				final float n5 = this.m[n3];
				final float n6 = this.m[n3 + 1];
				a.b[n4] = n5;
				a.b[n4 + 1] = n6;
				a.b[n4 + 4] = 0.0f;
				if (k) {
					a.c[n4] = n5;
					a.c[n4 + 1] = n6;
					a.c[n4 + 4] = 0.0f;
				}
			}
			break;
		}
		}
		return a;
	}

	@Override
	public void loadModel(final ModelContext modelContext,
			final IDrawContext drawContext) {
		final aa a = (aa) drawContext;
		if (!loon.live2d.draw.DrawDataImpl.q && this != a.d()) {
			throw new AssertionError();
		}
		if (!this.y.a(modelContext)) {
			return;
		}
		super.loadModel(modelContext, a);
		if (a.i[0]) {
			return;
		}
		final boolean[] p2 = loon.live2d.draw.DrawDataImpl.p;
		p2[0] = false;
		loon.live2d.util.ModelContextUtil.loadModel(modelContext, this.y, p2, this.g,
				this.l, a.b, 0, 2);
	}

	@Override
	public void b(final ModelContext modelContext,
			final IDrawContext drawContext) {
		try {
			if (!loon.live2d.draw.DrawDataImpl.q && this != drawContext.d()) {
				throw new AssertionError();
			}
			boolean b = false;
			if (drawContext.i[0]) {
				b = true;
			}
			final aa a = (aa) drawContext;
			if (!b) {
				super.b(modelContext, a);
				if (this.existBaseId()) {
					final BaseDataID listMap = this.getBaseId();
					if (a.a == -2) {
						a.a = modelContext.getBaseDataIndex(listMap);
					}
					if (a.a < 0) {
						if (Live2D.L2D_VERBOSE) {
							// noop
						}
					} else {
						final IBaseData baseData = modelContext
								.getBaseData(a.a);
						final IBaseContext baseContext = modelContext
								.getBaseContext(a.a);
						if (baseData != null && !baseContext.d()) {
							baseData.a(modelContext, baseContext, a.b, a.c,
									this.g, 0, 2);
							a.k = true;
						} else {
							a.k = false;
						}
						a.l = baseContext.g();
					}
				}
			}
		} catch (Live2DException ex) {
			throw ex;
		} catch (Exception e) {
			throw new Live2DException(e, "DrawError" + this.i().toString()
					+ " DDTexture/catch@setupTransform");
		}
	}

	@Override
	public void loadDraw(L2DModelMatrix matrix,final GLEx g,final DrawParam drawParam, final ModelContext modelContext,
			final IDrawContext drawContext) {
		if (!loon.live2d.draw.DrawDataImpl.q && this != drawContext.d()) {
			throw new AssertionError();
		}
		if (drawContext.i[0]) {
			return;
		}
		final aa a = (aa) drawContext;
		int f = this.f;
		if (f < 0) {
			f = 1;
		}
		final float n = this.c(modelContext, a) * drawContext.j * drawContext.l;
		final float[] array = (a.c != null) ? a.c : a.b;
		drawParam.setCulling(this.o);
		drawParam.drawTexture(matrix,g,f, 3 * this.h, this.k, array, this.m, n, this.n);
	}

	public Object getObject(final String s) {
		if (this.listMap == null) {
			return null;
		}
		return this.listMap.get(s);
	}

	public short[] h() {
		return this.k;
	}

	public class aa extends IDrawContext {
		int a;
		float[] b;
		float[] c;

		public aa(final IDrawData src) {
			super(src);
			this.a = -2;
			this.b = null;
			this.c = null;
		}

		public float[] a() {
			return (this.c != null) ? this.c : this.b;
		}
	}
}
