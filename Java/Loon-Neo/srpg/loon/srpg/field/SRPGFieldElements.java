/**
 * Copyright 2008 - 2011
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
package loon.srpg.field;

import loon.LRelease;
import loon.LTexture.Format;
import loon.canvas.Image;
import loon.opengl.LTexturePack;
import loon.utils.MathUtils;

public class SRPGFieldElements implements LRelease {

	private boolean packFlag;

	private LTexturePack imagePack;

	// ---- 默认的地形与参数(建议自行设置) ----//
	private final static String[] ELEMENT_NAMES = { "平地", "山丘", "耕地", "荒野",
			"沼泽", "河水", "大海", "雪地", "冰山", "沙地", "沙丘", "墙壁", "岩石", "皇宫", "城墙",
			"泥潭", "室内", "堤坝", "道路", "湿地", "烈火", "核辐射源" };

	private final static int ELEMENT_TYPES[][] = {
			{ 1, SRPGField.FIELD_NORMAL, 85, 65 },
			{ 2, SRPGField.FIELD_NORMAL, 81, 69 },
			{ 1, SRPGField.FIELD_NORMAL, 70, 60 },
			{ 1, SRPGField.FIELD_NORMAL, 80, 58 },
			{ 3, SRPGField.FIELD_MIRE, 55, 70 },
			{ 2, SRPGField.FIELD_WATER, 55, 35 },
			{ 3, SRPGField.FIELD_WATER, 25, 15 },
			{ 1, SRPGField.FIELD_MIRE, 77, 55 },
			{ 2, SRPGField.FIELD_MIRE, 60, 60 },
			{ 1, SRPGField.FIELD_MIRE, 83, 63 },
			{ 2, SRPGField.FIELD_MIRE, 80, 60 },
			{ -1, SRPGField.FIELD_WALL, 85, 65 },
			{ -1, SRPGField.FIELD_NORMAL, 85, 75 },
			{ 1, SRPGField.FIELD_NORMAL, 100, 80 },
			{ -1, SRPGField.FIELD_WALL, 100, 100 },
			{ 2, SRPGField.FIELD_MIRE, 70, 50 },
			{ -1, SRPGField.FIELD_NORMAL, 85, 65 },
			{ 1, SRPGField.FIELD_NORMAL, 90, 70 },
			{ 1, SRPGField.FIELD_NORMAL, 93, 73 },
			{ 2, SRPGField.FIELD_NORMAL, 65, 65 },
			{ 1, SRPGField.FIELD_KILL, 20, 5 },
			{ -1, SRPGField.FIELD_KILL, 0, 0 } };

	private final SRPGFieldElement[] battleTypes;

	private final int size;

	public final static int[] getDefElement(int index) {
		if (index >= 0 && index <= ELEMENT_NAMES.length) {
			return ELEMENT_TYPES[index];
		}
		return null;
	}

	public final static String getDefElementName(int index) {
		if (index >= 0 && index <= ELEMENT_NAMES.length) {
			return ELEMENT_NAMES[index];
		}
		return null;
	}

	public SRPGFieldElements(int size) {
		this.battleTypes = new SRPGFieldElement[size];
		this.size = size;
	}

	public SRPGFieldElements() {
		// 默认设定为最多支持32种地形，精细地图建议使用BIGMAP模式运行(假如小图太多,
		// Android版可能无法运行,因此JavaSE版只好向它看齐……)
		this(32);
	}

	public static SRPGFieldElement[] copyOf(SRPGFieldElement[] obj, int newSize) {
		SRPGFieldElement tempArr[] = new SRPGFieldElement[newSize];
		System.arraycopy(obj, 0, tempArr, 0, MathUtils.min(obj.length, newSize));
		return tempArr;
	}

	public SRPGFieldElements(SRPGFieldElements elements) {
		this.battleTypes = copyOf(elements.battleTypes,
				elements.battleTypes.length);
		this.size = battleTypes.length;
	}

	private void initImagePack() {
		if (imagePack == null) {
			imagePack = new LTexturePack(false);
			packFlag = true;
		}
	}

	public void putBattleElement(int index, int id, String name) {
		int[] res = getDefElement(id);
		putBattleElement(index, getDefElementName(id), "", res[0], res[2],
				res[3], res[1]);
	}

	public void putBattleElement(int index, String name, String depict, int mv,
			int atk, int def, int state) {
		this.isRangeCheck(index);
		this.addBattleElement(index, (Image) null, name, depict, mv, atk, def,
				state);
	}

	public void addBattleElement(int index, int id, String fileName) {
		int[] res = getDefElement(id);
		addBattleElement(index, fileName, getDefElementName(id), "", res[0],
				res[2], res[3], res[1]);
	}

	public void addBattleElement(int index, int id, String fileName, String name) {
		int[] res = getDefElement(id);
		addBattleElement(index, fileName, name, "", res[0], res[2], res[3],
				res[1]);
	}

	public void addBattleElement(int index, int mv, int state, String fileName,
			String name) {
		addBattleElement(index, fileName, name, "", mv, 100, 100, state);
	}

	public void addBattleElement(int index, String fileName, String name,
			String depict, int mv, int atk, int def, int state) {
		addBattleElement(index, Image.createImage(fileName), name, depict, mv,
				atk, def, state);
	}

	public void addBattleElement(int index, int mv, int state, Image img,
			String name) {
		addBattleElement(index, img, name, "", mv, 100, 100, state);
	}

	public void addBattleElement(int index, Image img, String name,
			String depict, int mv, int atk, int def, int state) {
		this.isRangeCheck(index);
		int id = -1;
		if (img != null) {
			initImagePack();
			id = imagePack.putImage(img);
		}
		SRPGFieldElement element = new SRPGFieldElement(id, name, depict, mv,
				atk, def, state);
		if (battleTypes[index] != null) {
			battleTypes[index] = null;
		}
		battleTypes[index] = element;
	}

	private void isRangeCheck(int index) {
		if (index >= size) {
			throw new IndexOutOfBoundsException("Elemetns Index: " + index
					+ ", Size: " + size);
		}
	}

	public SRPGFieldElement getBattleElement(int index) {
		if (index < 0) {
			return null;
		}
		this.isRangeCheck(index);
		return battleTypes[index];
	}

	public Image getBattleElementImage(int index) {
		SRPGFieldElement ele = getBattleElement(index);
		if (ele != null && ele.imgId != -1 && packFlag) {
			return imagePack.getImage(ele.imgId);
		}
		return null;
	}

	public void packed() {
		if (packFlag) {
			imagePack.packed(Format.NEAREST);
		}
	}

	public boolean isBatch() {
		return packFlag && imagePack.isBatch();
	}

	public void begin() {
		if (packFlag) {
			imagePack.glBegin();
		}
	}

	public void draw(int index, float x, float y) {
		if (!packFlag) {
			return;
		}
		if (battleTypes[index] == null) {
			return;
		}
		if (battleTypes[index].imgId != -1) {
			imagePack.draw(battleTypes[index].imgId, x, y);
		}
	}

	public void draw(int index, float x, float y, float w, float h) {
		if (!packFlag) {
			return;
		}
		if (battleTypes[index] == null) {
			return;
		}
		if (battleTypes[index].imgId != -1) {
			imagePack.draw(battleTypes[index].imgId, x, y, w, h);
		}
	}

	public void end() {
		if (packFlag) {
			imagePack.glEnd();
		}
	}

	public boolean isBatchLocked() {
		return packFlag && imagePack.isBatchLocked();
	}

	public void glCache() {
		if (packFlag) {
			imagePack.saveCache();
		}
	}

	@Override
	public void close() {
		this.packFlag = false;
		for (int i = 0; i < size; i++) {
			battleTypes[i] = null;
		}
		if (imagePack != null) {
			imagePack.close();
			imagePack = null;
		}
	}

}
