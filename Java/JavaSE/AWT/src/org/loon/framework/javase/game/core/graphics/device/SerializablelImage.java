package org.loon.framework.javase.game.core.graphics.device;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.loon.framework.javase.game.core.LSystem;
import org.loon.framework.javase.game.utils.GraphicsUtils;

/**
 * Copyright 2008 - 2009
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
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1
 */
public class SerializablelImage implements Serializable {
	/**
	 * 序列化Image图像
	 */
	private static final long serialVersionUID = -1982984646473630901L;

	private transient BufferedImage image = null;

	public SerializablelImage() {
		this.image = null;
	}

	public SerializablelImage(Image img) {
		this.image = GraphicsUtils.getBufferImage(img);
	}

	public SerializablelImage(BufferedImage img) {
		this.image = img;
	}

	public void setImage(BufferedImage img) {
		this.image = img;
	}

	public BufferedImage getImage() {
		return image;
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
		if (image == null) {
			LSystem.writeInt(out, 0);
		} else {
			LSystem.writeInt(out, 1);
			WritableRaster wr = image.getRaster();
			int pixels[] = (int[]) wr.getPixels(0, 0, image.getWidth(), image
					.getHeight(), (int[]) null);
			LSystem.writeInt(out, image.getWidth());
			LSystem.writeInt(out, image.getHeight());
			LSystem.writeInt(out, pixels.length);
			for (int i = 0; i < pixels.length; i++) {
				LSystem.writeInt(out, pixels[i]);
			}
		}
	}

	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		in.defaultReadObject();
		int result = LSystem.readInt(in);
		if (result == 1) {
			int width = LSystem.readInt(in);
			int height = LSystem.readInt(in);
			int pixelCount = LSystem.readInt(in);
			int pixels[] = new int[pixelCount];
			for (int i = 0; i < pixels.length; i++) {
				pixels[i] = LSystem.readInt(in);
			}
			BufferedImage image = GraphicsUtils
					.createImage(width, height, true);
			WritableRaster wr = image.getRaster();
			wr.setPixels(0, 0, width, height, pixels);
			this.image = image;
		}
	}

}
