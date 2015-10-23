/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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
 * @version 0.5
 */
package loon.stage;

import loon.LTextures;
import loon.font.TextureSource;
import loon.geom.Dimension;
import loon.geom.RectBox;
import loon.opengl.GLEx;
import loon.opengl.Painter;
import loon.utils.reply.GoFuture;
import loon.utils.reply.Port;

public class ImagePlayer extends Player {

	private Painter painter;

	public float forceWidth = -1, forceHeight = -1;

	public RectBox region;

	public ImagePlayer(String path) {
		setPainter(LTextures.loadTexture(path));
	}
	
	public ImagePlayer(Painter painter) {
		setPainter(painter);
	}

	public ImagePlayer(TextureSource source) {
		setSource(source);
	}

	public ImagePlayer() {
	}

	public Painter getPainter() {
		return painter;
	}

	public ImagePlayer setPainter(Painter painter) {
		if (this.painter != painter) {
			if (this.painter != null) {
				this.painter.texture().release();
			}
			this.painter = painter;
			checkOrigin();
			if (painter != null) {
				painter.texture().reference();
			}
		}
		return this;
	}

	public ImagePlayer setSource(TextureSource source) {
		if (source.isLoaded()) {
			setPainter(source.draw());
		} else {
			source.tileAsync().onSuccess(new Port<Painter>() {
				public void onEmit(Painter painter) {
					setPainter(painter);
				}
			});
		}
		return this;
	}

	public ImagePlayer setPainter(GoFuture<? extends Painter> painter) {
		painter.onSuccess(new Port<Painter>() {
			public void onEmit(Painter painter) {
				setPainter(painter);
			}
		});
		return this;
	}

	public ImagePlayer setSize(float width, float height) {
		forceWidth = width;
		forceHeight = height;
		checkOrigin();
		return this;
	}

	public ImagePlayer setSize(Dimension size) {
		return setSize(size.width, size.height);
	}

	public ImagePlayer setRegion(RectBox region) {
		this.region = region;
		checkOrigin();
		return this;
	}

	@Override
	public float width() {
		if (forceWidth >= 0) {
			return forceWidth;
		}
		if (region != null) {
			return region.width;
		}
		return (painter == null) ? 0 : painter.width();
	}

	@Override
	public float height() {
		if (forceHeight >= 0) {
			return forceHeight;
		}
		if (region != null) {
			return region.height;
		}
		return (painter == null) ? 0 : painter.height();
	}

	@Override
	public void close() {
		super.close();
		setPainter((Painter) null);
	}

	@Override
	public void update(long elapsedTime) {
		
	}
	
	@Override
	protected void paintImpl(GLEx gl) {
		if (painter != null) {
			float dwidth = width(), dheight = height();
			if (region == null) {
				gl.draw(painter, 0, 0, dwidth, dheight);
			} else {
				gl.draw(painter, 0, 0, dwidth, dheight, region.x, region.y,
						region.width, region.height);
			}
		}
	}

	@Override
	protected void finalize() {
		setPainter((Painter) null);
	}


}
