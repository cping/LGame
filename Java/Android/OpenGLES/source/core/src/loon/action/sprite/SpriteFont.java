/**
 * Copyright 2013 The Loon Authors
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
 */
package loon.action.sprite;

import java.util.ArrayList;

import loon.action.sprite.SpriteBatch.SpriteEffects;
import loon.core.geom.RectBox;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.LImage;
import loon.core.graphics.opengl.GLLoader;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTexture.Format;
import loon.core.resource.Resources;
import loon.utils.collection.ArrayByte;

//通过LGame工具提取XNA字库生成的PAK文件，可以通过此类重新读取及显示
public class SpriteFont {

	public static SpriteFont read(String resName) {
		try {
			ArrayList<RectBox> xGlyphs = new ArrayList<RectBox>(), xCropping = new ArrayList<RectBox>();
			ArrayList<Character> xChars = new ArrayList<Character>();
			int xSpacingV;
			float xSpacingH;
			ArrayList<float[]> xKerning = new ArrayList<float[]>();

			ArrayByte arrays = new ArrayByte(Resources.openResource(resName),
					ArrayByte.BIG_ENDIAN);

			int size = arrays.readInt();

			LImage image = LImage.createImage(arrays.readByteArray(size));

			int count = arrays.readInt();
			while (count-- > 0) {
				xGlyphs.add(new RectBox(arrays.readInt(), arrays.readInt(),
						arrays.readInt(), arrays.readInt()));
				xCropping.add(new RectBox(arrays.readInt(), arrays.readInt(),
						arrays.readInt(), arrays.readInt()));
				xChars.add((char) arrays.readInt());
			}

			xSpacingV = arrays.readInt();
			xSpacingH = arrays.readFloat();

			count = arrays.readInt();
			while (count-- > 0) {
				xKerning.add(new float[] { arrays.readFloat(),
						arrays.readFloat(), arrays.readFloat() });
			}
			return new SpriteFont(new LTexture(GLLoader.getTextureData(image),
					Format.LINEAR), xGlyphs, xCropping, xChars, xSpacingV,
					xSpacingH, xKerning, 'A');
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	protected LTexture texture;
	protected ArrayList<RectBox> glyphs;
	protected ArrayList<RectBox> cropping;
	protected ArrayList<Character> charMap;
	protected int lineSpacing;
	protected int maxCharY;
	protected float spacing;
	protected ArrayList<float[]> kerning;
	protected char defaultchar;

	public SpriteFont(LTexture tex2d, ArrayList<RectBox> gs, ArrayList<RectBox> crops,
			ArrayList<Character> chars, int line, float space,
			ArrayList<float[]> kern, char def) {
		this.texture = tex2d;
		this.glyphs = gs;
		this.cropping = crops;
		this.charMap = chars;
		this.lineSpacing = 0;
		this.spacing = 0f;
		this.kerning = kern;
		this.defaultchar = def;
		int max = 0;
		for (RectBox rect : glyphs) {
			if (max == 0 || rect.getHeight() > max) {
				max = (int) rect.getHeight();
			}
		}
		this.maxCharY = max;
	}

	protected void drawChar(SpriteBatch batch, float x, float y, RectBox glyph,
			RectBox cropping, LColor color) {
		batch.draw(texture, x + cropping.x, y + cropping.y, glyph.getWidth(),
				glyph.getHeight(), glyph.getX(), glyph.getY(),
				glyph.getWidth(), glyph.getHeight(), color);
	}

	protected void drawString(SpriteBatch batch, CharSequence cs, float x,
			float y) {
		drawString(batch, cs, x, y, LColor.white);
	}

	protected void drawString(SpriteBatch batch, CharSequence cs, float x,
			float y, LColor color) {
		float xx = 0, yy = 0;
		for (int i = 0; i < cs.length(); i++) {
			char c = cs.charAt(i), c2 = i != 0 ? cs.charAt(i - 1) : 0;
			if (c2 != 0) {
				for (int j = 0; j < kerning.size(); j++) {
					if (kerning.get(j)[1] == c && kerning.get(j)[0] == c2) {
						xx += kerning.get(j)[2];
						break;
					}
				}
			}
			for (int j = 0; j < charMap.size(); j++) {
				if (charMap.get(j) != c) {
					continue;
				}

				drawChar(batch, x + xx, y + yy, glyphs.get(j), cropping.get(j),
						color);
				xx += glyphs.get(j).getWidth() + spacing;
			}
			if (c == '\n') {
				xx = 0;
				yy += getLineHeight();
			}
		}
	}

	private final Vector2f pos = new Vector2f();

	protected void drawString(SpriteBatch batch, CharSequence cs,
			Vector2f local, LColor color, float rotation, Vector2f origin,
			Vector2f scale, SpriteEffects spriteEffects) {
		pos.set(0, 0);
		int flip = 1;
		float beginningofline = 0f;
		boolean flag = true;
		if (spriteEffects == SpriteEffects.FlipHorizontally) {
			beginningofline = this.measure(cs).x * scale.x;
			flip = -1;
		}
		if (spriteEffects == SpriteEffects.FlipVertically) {
			pos.y = (this.measure(cs).y - this.lineSpacing) * scale.y;
		} else {
			pos.y = 0f;
		}
		pos.x = beginningofline;
		for (int i = 0; i < cs.length(); i++) {
			char character = cs.charAt(i);
			switch (character) {
			case '\r':
				break;
			case '\n':
				flag = true;
				pos.x = beginningofline;
				if (spriteEffects == SpriteEffects.FlipVertically) {
					pos.y -= this.lineSpacing * scale.y;
				} else {
					pos.y += this.lineSpacing * scale.y;
				}
				break;
			default: {
				int indexForCharacter = this.characterIndex(character);
				float[] charkerning = this.kerning.get(indexForCharacter);
				if (flag) {
					charkerning[0] = Math.max(charkerning[0], 0f);
				} else {
					pos.x += (this.spacing * scale.x) * flip;
				}
				pos.x += (charkerning[0] * scale.x) * flip;
				RectBox rectangle = this.glyphs.get(indexForCharacter);
				RectBox rectangle2 = this.cropping.get(indexForCharacter);
				Vector2f position = pos.cpy();
				position.x += rectangle2.x * scale.x;
				position.y += rectangle2.y * scale.y;
				position.addLocal(local);
				batch.draw(this.texture, position, rectangle, color, rotation,
						origin, scale, spriteEffects);
				flag = false;
				pos.x += ((charkerning[1] + charkerning[2]) * scale.x) * flip;
				break;
			}
			}
		}
	}

	public int getWidth(CharSequence cs) {
		ArrayList<Float> list = new ArrayList<Float>();
		int y = 0;
		for (int i = 0; i < cs.length(); i++) {
			if (list.size() <= y)
				list.add(0f);
			char c = cs.charAt(i), c2 = i != 0 ? cs.charAt(i - 1) : 0;
			if (c2 != 0)
				for (int j = 0; i < kerning.size(); j++) {
					if (kerning.get(j)[1] == c && kerning.get(j)[0] == c2) {
						list.set(y, list.get(y) + kerning.get(j)[2]);
						break;
					}
				}
			for (int j = 0; j < charMap.size(); j++) {
				if (charMap.get(j) != c) {
					continue;
				}
				list.set(y, list.get(y) + glyphs.get(j).getWidth());
				if (i != cs.length() - 1)
					list.set(y, list.get(y) + spacing);
			}
			if (c == '\n')
				y++;
		}
		float maxX = 0f;
		for (int j = 0; j < list.size(); j++) {
			if (maxX == 0f || list.get(j) > maxX) {
				maxX = list.get(j);
			}
		}
		return (int) (float) maxX;
	}

	public int getHeight(CharSequence cs) {
		return cs.toString().split("\\\\n").length * getLineHeight();
	}

	public int getLineHeight() {
		return maxCharY + lineSpacing;
	}

	public int characterIndex(char character) {
		int lowindex = 0;
		int highindex = this.charMap.size() - 1;
		while (lowindex <= highindex) {
			int index = lowindex + ((highindex - lowindex) >> 1);
			if (this.charMap.get(index) == character) {
				return index;
			}
			if (this.charMap.get(index) < character) {
				lowindex = index + 1;
			} else {
				highindex = index - 1;
			}
		}

		if (this.defaultchar != '\0') {
			char ch = this.defaultchar;
			if (character != ch) {
				return this.characterIndex(ch);
			}
		}
		throw new IllegalArgumentException("Character not in Font");
	}

	protected Vector2f measure(CharSequence cs) {
		if (cs.length() == 0) {
			return new Vector2f();
		}
		Vector2f zero = new Vector2f();
		zero.y = this.lineSpacing;
		float min = 0f;
		int count = 0;
		float z = 0f;
		boolean flag = true;

		for (int i = 0; i < cs.length(); i++) {
			if (cs.charAt(i) != '\r') {
				if (cs.charAt(i) == '\n') {
					zero.x += Math.max(z, 0f);
					z = 0f;
					min = Math.max(zero.x, min);
					zero = new Vector2f();
					zero.y = this.lineSpacing;
					flag = true;
					count++;
				} else {
					float[] vector2 = this.kerning.get(this.characterIndex(cs
							.charAt(i)));
					if (flag) {
						vector2[0] = Math.max(vector2[0], 0f);
					} else {
						zero.x += this.spacing + z;
					}
					zero.x += vector2[0] + vector2[1];
					z = vector2[2];
					RectBox rectangle = this.cropping.get(this
							.characterIndex(cs.charAt(i)));
					zero.y = Math.max(zero.y, rectangle.height);
					flag = false;
				}
			}
		}
		zero.x += Math.max(z, 0f);
		zero.y += count * this.lineSpacing;
		zero.x = Math.max(zero.x, min);
		return zero;
	}

	public Vector2f measureString(CharSequence cs) {
		return measure(cs);
	}
}
