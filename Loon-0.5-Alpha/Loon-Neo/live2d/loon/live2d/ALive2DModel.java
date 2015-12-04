package loon.live2d;

import loon.BaseIO;
import loon.live2d.draw.*;
import loon.live2d.graphics.*;
import loon.live2d.id.*;
import loon.live2d.io.*;
import loon.live2d.model.*;
import loon.utils.ArrayByte;

public abstract class ALive2DModel {
	public static final int a = 1;
	public static final int b = 2;
	protected static int c;
	protected ModelImpl d;
	protected ModelContext e;
	protected int f;

	static {
		ALive2DModel.c = 0;
	}

	public ALive2DModel() {
		this.d = null;
		this.e = null;
		this.f = 0;
		++ALive2DModel.c;
		this.e = new ModelContext(this);
	}

	public void setModelImpl(final ModelImpl m) {
		this.d = m;
	}

	public ModelImpl getModelImpl() {
		if (this.d == null) {
			(this.d = new ModelImpl()).initDirect();
		}
		return this.d;
	}

	public float getCanvasWidth() {
		if (this.d == null) {
			return 0.0f;
		}
		return this.d.getCanvasWidth();
	}

	public float getCanvasHeight() {
		if (this.d == null) {
			return 0.0f;
		}
		return this.d.getCanvasHeight();
	}

	public float getParamFloat(final String paramID) {
		return this.e
				.getParamFloat(this.e.getParamIndex(ParamID.getID(paramID)));
	}

	public void setParamFloat(final String paramID, final float value) {
		this.e.setParamFloat(this.e.getParamIndex(ParamID.getID(paramID)),
				value);
	}

	public void setParamFloat(final String paramID, final float value,
			final float weight) {
		this.setParamFloat(this.e.getParamIndex(ParamID.getID(paramID)), value,
				weight);
	}

	public void addToParamFloat(final String paramID, final float value) {
		this.addToParamFloat(paramID, value, 1.0f);
	}

	public void addToParamFloat(final String paramID, final float value,
			final float weight) {
		this.addToParamFloat(this.e.getParamIndex(ParamID.getID(paramID)),
				value, weight);
	}

	public void multParamFloat(final String paramID, final float mult) {
		this.multParamFloat(paramID, mult, 1.0f);
	}

	public void multParamFloat(final String paramID, final float mult,
			final float weight) {
		this.multParamFloat(this.e.getParamIndex(ParamID.getID(paramID)), mult,
				weight);
	}

	public void loadParam() {
		this.e.loadParam();
	}

	public void saveParam() {
		this.e.saveParam();
	}

	public void init() {
		this.e.init();
	}

	public void update() {
		this.e.update();
	}

	public int generateModelTextureNo() {
		return -1;
	}

	public void releaseModelTextureNo(final int no) {
		
	}

	public abstract void deleteTextures();

	public abstract void draw();

	public static void loadModel_exe(final ALive2DModel ret,
			final String filepath) {
		try {
			final ArrayByte bin = new ArrayByte(
					BaseIO.loadBytes(filepath));
			loadModel_exe(ret, bin);
			bin.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void loadModel_exe(final ALive2DModel ret,
			final ArrayByte bin) {
		try {
			final BReader bReader = new BReader(bin);
			final byte f = bReader.readByte();
			final byte f2 = bReader.readByte();
			final byte f3 = bReader.readByte();
			if (f != 109 || f2 != 111 || f3 != 99) {
				throw new Live2DException("Model load error , Unknown fomart.");
			}
			final byte ver = bReader.readByte();
			bReader.setVersion(ver);
			if (ver > 11) {
				ret.f |= 0x2;
				throw new Live2DException(
								"Model load error , Illegal data version error.\n");
			}
			final ModelImpl modelImpl = (ModelImpl) bReader.reader();
			if (ver >= 8 && bReader.readInt() != -2004318072) {
				ret.f |= 0x1;
				throw new Live2DException("Model load error , EOF not found.");
			}
			ret.setModelImpl(modelImpl);
			ret.getModelContext().init();
		} catch (Exception e) {
			throw new Live2DException(e, "Model load error , Unknown error ");
		}
	}

	public int getParamIndex(final String paramID) {
		return this.e.getParamIndex(ParamID.getID(paramID));
	}

	public float getParamFloat(final int paramIndex) {
		return this.e.getParamFloat(paramIndex);
	}

	public void setParamFloat(final int paramIndex, final float value) {
		this.e.setParamFloat(paramIndex, value);
	}

	public void setParamFloat(final int paramIndex, final float value,
			final float weight) {
		this.e.setParamFloat(paramIndex, this.e.getParamFloat(paramIndex)
				* (1.0f - weight) + value * weight);
	}

	public void addToParamFloat(final int paramIndex, final float value) {
		this.addToParamFloat(paramIndex, value, 1.0f);
	}

	public void addToParamFloat(final int paramIndex, final float value,
			final float weight) {
		this.e.setParamFloat(paramIndex, this.e.getParamFloat(paramIndex)
				+ value * weight);
	}

	public void multParamFloat(final int paramIndex, final float mult) {
		this.multParamFloat(paramIndex, mult, 1.0f);
	}

	public void multParamFloat(final int paramIndex, final float mult,
			final float weight) {
		this.e.setParamFloat(paramIndex, this.e.getParamFloat(paramIndex)
				* (1.0f + (mult - 1.0f) * weight));
	}

	public ModelContext getModelContext() {
		return this.e;
	}

	public int getErrorFlags() {
		return this.f;
	}

	public void setupPartsOpacityGroup_alphaImpl(final String[] paramGroup,
			final String[] partsIDGroup, final float deltaTimeSec,
			final float CLEAR_TIME_SEC) {
		int n = -1;
		float n2 = 0.0f;
		final float n3 = 0.5f;
		final float n4 = 0.15f;
		if (deltaTimeSec == 0.0f) {
			for (int i = 0; i < paramGroup.length; ++i) {
				this.setPartsOpacity(partsIDGroup[i],
						(this.getParamFloat(paramGroup[i]) != 0.0f) ? 1 : 0);
			}
			return;
		}
		if (paramGroup.length == 1) {
			final boolean b = this.getParamFloat(paramGroup[0]) != 0.0f;
			final String s = partsIDGroup[0];
			final float partsOpacity = this.getPartsOpacity(s);
			final float n5 = deltaTimeSec / CLEAR_TIME_SEC;
			float opacity;
			if (b) {
				opacity = partsOpacity + n5;
				if (opacity > 1.0f) {
					opacity = 1.0f;
				}
			} else {
				opacity = partsOpacity - n5;
				if (opacity < 0.0f) {
					opacity = 0.0f;
				}
			}
			this.setPartsOpacity(s, opacity);
		} else {
			for (int j = 0; j < paramGroup.length; ++j) {
				if (this.getParamFloat(paramGroup[j]) != 0.0f) {
					if (n >= 0) {
						break;
					}
					n = j;
					n2 = this.getPartsOpacity(partsIDGroup[j]) + deltaTimeSec
							/ CLEAR_TIME_SEC;
					if (n2 > 1.0f) {
						n2 = 1.0f;
					}
				}
			}
			if (n < 0) {
				n = 0;
				n2 = 1.0f;
				this.loadParam();
				this.setParamFloat(paramGroup[n], n2);
				this.saveParam();
			}
			for (int k = 0; k < paramGroup.length; ++k) {
				final String partsID = partsIDGroup[k];
				if (n == k) {
					this.setPartsOpacity(partsID, n2);
				} else {
					float partsOpacity2 = this.getPartsOpacity(partsID);
					float n6;
					if (n2 < n3) {
						n6 = n2 * (n3 - 1.0f) / n3 + 1.0f;
					} else {
						n6 = (1.0f - n2) * n3 / (1.0f - n3);
					}
					if ((1.0f - n6) * (1.0f - n2) > n4) {
						n6 = 1.0f - n4 / (1.0f - n2);
					}
					if (partsOpacity2 > n6) {
						partsOpacity2 = n6;
					}
					this.setPartsOpacity(partsID, partsOpacity2);
				}
			}
		}
	}

	public void setPartsOpacity(final String partsID, final float opacity) {
		final int partsDataIndex = this.e.getPartsDataIndex(PartsDataID
				.getID(partsID));
		if (partsDataIndex < 0) {
			return;
		}
		this.setPartsOpacity(partsDataIndex, opacity);
	}

	public void setPartsOpacity(final int partsIndex, final float opacity) {
		this.e.setPartsOpacity(partsIndex, opacity);
	}

	public int getPartsDataIndex(final String partsID) {
		return this.e.getPartsDataIndex(PartsDataID.getID(partsID));
	}

	public int getPartsDataIndex(final PartsDataID partsID) {
		return this.e.getPartsDataIndex(partsID);
	}

	public float getPartsOpacity(final String partsID) {
		final int partsDataIndex = this.e.getPartsDataIndex(PartsDataID
				.getID(partsID));
		if (partsDataIndex < 0) {
			return 0.0f;
		}
		return this.getPartsOpacity(partsDataIndex);
	}

	public float getPartsOpacity(final int partsIndex) {
		return this.e.getPartsOpacity(partsIndex);
	}

	public abstract DrawParam getDrawParam();

	public int getDrawDataIndex(final String drawDataID) {
		return this.e.getDrawDataIndex(DrawDataID.getID(drawDataID));
	}

	public IDrawData getDrawData(final int drawIndex) {
		return this.e.getDrawData(drawIndex);
	}

	public float[] getTransformedPoints(final int drawIndex) {
		final IDrawContext drawContext = this.e.getDrawContext(drawIndex);
		if (drawContext instanceof DrawDataImpl.aa) {
			return ((DrawDataImpl.aa) drawContext).a();
		}
		return null;
	}

	public short[] getIndexArray(final int drawIndex) {
		final IDrawData drawData = this.e.getDrawData(drawIndex);
		if (drawData instanceof DrawDataImpl) {
			return ((DrawDataImpl) drawData).h();
		}
		return null;
	}

	public void setPremultipliedAlpha(final boolean b) {
		this.getDrawParam().setPremultipliedAlpha(b);
	}

	public boolean isPremultipliedAlpha() {
		return this.getDrawParam().isPremultipliedAlpha();
	}
}
