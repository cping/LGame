/**
 * Copyright 2008 - 2010
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.3.3
 */
package loon.action.avg;

import loon.LRelease;
import loon.LSystem;
import loon.LTexture;
import loon.action.avg.drama.Expression;
import loon.canvas.LColor;
import loon.geom.PointI;
import loon.utils.IntArray;
import loon.utils.MathUtils;
import loon.utils.StringUtils;
import loon.utils.parse.StrTokenizer;
import loon.utils.res.TextResource;

//0.3.3新增类,用以按指定的格式循环播放动画图像
public final class AVGAnm implements Expression, LRelease {

	protected final PointI point = new PointI();

	protected float alpha = 1f;

	protected float angle;

	protected int width, height, imageWidth, imageHeight;

	protected IntArray posxTmps = new IntArray();

	protected IntArray posyTmps = new IntArray();

	protected IntArray time = new IntArray();

	protected int[] posx = null;

	protected int[] posy = null;

	protected int anmtime = 20;

	protected int alltime = 0;

	protected int count = 0;

	protected long startTime = -1;

	protected boolean loop = true, loaded = false, closed = false;

	protected LTexture texture;

	protected LColor color;

	private String _anmPath;

	private String _texturePath;

	public AVGAnm(String resName) {
		open(TextResource.get().loadText(this._anmPath = resName));
	}

	public void open(String text) {
		try {
			StrTokenizer reader = new StrTokenizer(text, LSystem.NL);
			String script = null;
			for (; reader.hasMoreTokens();) {
				script = reader.nextToken().trim();
				if (script.length() > 0 && !script.startsWith(FLAG_L_TAG) && !script.startsWith(FLAG_C_TAG)
						&& !script.startsWith(FLAG_I_TAG)) {
					final String[] element = script.split(";");
					for (int j = 0; j < element.length; j++) {
						load(element[j]);
					}
				}
			}
		} catch (Throwable ex) {
			this.loaded = false;
			LSystem.error("AVGAnm exception", ex);
			return;
		}
		this.loaded = true;
		this.count = posxTmps.size();
		this.posx = new int[count];
		this.posy = new int[count];
		for (int i = 0; i < count; i++) {
			this.posx[i] = (posxTmps.get(i));
			this.posy[i] = (posyTmps.get(i));
		}
		if (width <= 0) {
			width = imageWidth;
		}
		if (height <= 0) {
			height = imageHeight;
		}
	}

	private void load(String script) {
		String[] op = StringUtils.split(script, '=');
		if (op.length == 2) {
			final String key = op[0].trim();
			final String vv = op[1].trim();
			if ("path".equalsIgnoreCase(key)) {
				_texturePath = StringUtils.replace(vv, "\"", LSystem.EMPTY);
				if (texture != null) {
					texture.close();
					texture = null;
				}
				texture = LSystem.loadTexture(_texturePath);
				if (texture != null) {
					imageWidth = texture.getWidth();
					imageHeight = texture.getHeight();
				}
			}
			if ("imagewidth".equalsIgnoreCase(key)) {
				if (MathUtils.isNan(vv)) {
					imageWidth = Integer.parseInt(vv);
				}
			} else if ("alpha".equalsIgnoreCase(key)) {
				if (MathUtils.isNan(vv)) {
					alpha = Float.parseFloat(vv);
				}
			} else if ("angle".equalsIgnoreCase(key)) {
				if (MathUtils.isNan(vv)) {
					angle = Float.parseFloat(vv);
				}
			} else if ("color".equalsIgnoreCase(key)) {
				String[] p = StringUtils.split(vv, LSystem.COMMA);
				if (p.length > 2 && p.length < 5) {
					for (int i = 0; i < p.length; i++) {
						p[i] = p[i].replaceAll("^[\\t ]*", "").replaceAll("[\\t ]*$", LSystem.EMPTY);
					}
					if (p.length == 3) {
						if (color == null) {
							color = new LColor(Integer.parseInt(p[0]), Integer.parseInt(p[1]), Integer.parseInt(p[2]));
						} else {
							color.setColor(Integer.parseInt(p[0]), Integer.parseInt(p[1]), Integer.parseInt(p[2]));
						}
					}
					if (p.length == 4) {
						if (color == null) {
							color = new LColor(Integer.parseInt(p[0]), Integer.parseInt(p[1]), Integer.parseInt(p[2]),
									Integer.parseInt(p[3]));
						} else {
							color.setColor(Integer.parseInt(p[0]), Integer.parseInt(p[1]), Integer.parseInt(p[2]),
									Integer.parseInt(p[3]));
						}
					}
				} else {
					if (color == null) {
						color = new LColor(vv);
					} else {
						color.setColor(vv);
					}
				}
			} else if ("imageheight".equalsIgnoreCase(key)) {
				if (MathUtils.isNan(vv))
					imageHeight = Integer.parseInt(vv);
			} else if ("width".equalsIgnoreCase(key)) {
				if (MathUtils.isNan(vv)) {
					width = Integer.parseInt(vv);
				}
			} else if ("height".equalsIgnoreCase(key)) {
				if (MathUtils.isNan(vv)) {
					height = Integer.parseInt(vv);
				}
			} else if ("time".equalsIgnoreCase(key)) {
				if (MathUtils.isNan(vv)) {
					anmtime = Integer.parseInt(vv);
				}
			} else if ("pos".equalsIgnoreCase(key)) {
				String[] p = StringUtils.split(vv, LSystem.COMMA);
				for (int i = 0; i < p.length; i++) {
					p[i] = p[i].replaceAll("^[\\t ]*", "").replaceAll("[\\t ]*$", LSystem.EMPTY);
				}
				switch (p.length) {
				case 1:
					if (MathUtils.isNan(p[0])) {
						posxTmps.add(Integer.parseInt(p[0]));
						posyTmps.add(Integer.parseInt(p[0]));
						time.add(anmtime);
						alltime += anmtime;
					}
					break;
				case 2:
					if (MathUtils.isNan(p[0]) && MathUtils.isNan(p[1])) {
						posxTmps.add(Integer.parseInt(p[0]));
						posyTmps.add(Integer.parseInt(p[1]));
						time.add(anmtime);
						alltime += anmtime;
					}
					break;
				}
			}
		}
	}

	public String getTexturePath() {
		return _texturePath;
	}

	public String getAnmPath() {
		return _anmPath;
	}

	public int getWidth() {
		return width;
	}

	public AVGAnm setWidth(int w) {
		this.width = w;
		return this;
	}

	public int getHeight() {
		return height;
	}

	public AVGAnm setHeight(int h) {
		this.height = h;
		return this;
	}

	public int getImageWidth() {
		return imageWidth;
	}

	public void setImageWidth(int imageWidth) {
		this.imageWidth = imageWidth;
	}

	public int getImageHeight() {
		return imageHeight;
	}

	public void setImageHeight(int imageHeight) {
		this.imageHeight = imageHeight;
	}

	public int[] getPosx() {
		return posx;
	}

	public int[] getPosy() {
		return posy;
	}

	public IntArray getTime() {
		return time;
	}

	public int getAlltime() {
		return alltime;
	}

	public int getCount() {
		return count;
	}

	public boolean isLoop() {
		return loop;
	}

	public final LTexture getTexture() {
		return texture;
	}

	public float getAlpha() {
		return alpha;
	}

	public float getAngle() {
		return angle;
	}

	public AVGAnm start(long elapsedTime, boolean loop) {
		this.startTime = elapsedTime;
		this.loop = loop;
		if (texture != null) {
			if (texture.getWidth() == imageWidth && texture.getHeight() == imageHeight) {
				this.loop = false;
			}
		}
		return this;
	}

	public AVGAnm start() {
		return this.start(0, loop);
	}

	public AVGAnm stop() {
		this.startTime = -1;
		this.loop = false;
		return this;
	}

	public PointI getPos(long elapsedTime) {
		if (startTime != -1) {
			int frame = getFrame(elapsedTime);
			point.x = posx[frame];
			point.y = posy[frame];
		}
		return point;
	}

	private int getFrame(long elapsedTime) {
		long diff = elapsedTime - startTime;
		if (!loop && diff >= alltime) {
			startTime = -1;
			return 0;
		}
		long now = diff % alltime;
		int t = 0;
		for (int i = 0; i < count; i++) {
			t += time.get(i);
			if (now < t) {
				return i;
			}
		}
		return 0;
	}

	public boolean isClosed() {
		return closed;
	}

	@Override
	public void close() {
		stop();
		if (texture != null) {
			texture.close();
			texture = null;
		}
		closed = true;
	}

}
