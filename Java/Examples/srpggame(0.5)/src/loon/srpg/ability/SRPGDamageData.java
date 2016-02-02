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
package loon.srpg.ability;
import loon.canvas.LColor;
import loon.srpg.SRPGType;
import loon.srpg.actor.SRPGActor;
import loon.srpg.actor.SRPGStatus;
import loon.srpg.effect.SRPGEffect;
import loon.srpg.effect.SRPGNumberEffect;
import loon.srpg.effect.SRPGPopupEffect;
import loon.srpg.field.SRPGMoveStack;


public class SRPGDamageData {

	private final static LColor popColor = new LColor(0.75f, 0.75f, 0),
			numColor1 = new LColor(0.75f, 0.75f, 0), numColor2 = new LColor(0, 0.5f,
					1f), numColor3 = new LColor(0, 0.86f, 0),
			numColor4 = new LColor(1f, 0.5f, 0), numColor5 = new LColor(0.75f, 0.86f,
					0.75f);

	private int damage;

	private int mp;

	private int hitrate;

	private boolean hit;

	private int d_damage;

	private int d_hitrate;

	private int br_damage;

	private int br_hitrate;

	private String d_damagestr;

	private String d_hitratestr;

	private String d_helper;

	private String d_popup;

	int[] status;

	int[] substatus;

	private int[] d_status;

	private boolean[] element;

	private SRPGMoveStack ms;

	private SRPGStatus stat;

	private int genre;

	private int action;

	private int posX;

	private int posY;

	private int direction;

	public SRPGDamageData() {
		this.hit = true;
		this.posX = -1;
		this.posY = -1;
		this.direction = -1;
		this.d_damagestr = null;
		this.d_hitratestr = null;
		this.d_helper = null;
		this.ms = null;
		this.status = new int[15];
		this.substatus = new int[15];
		this.d_status = new int[15];
		this.element = new boolean[10];
	}

	public void setDamage(int d) {
		this.damage = d;
	}

	public int getDamage() {
		return damage;
	}

	public void setMP(int mp) {
		this.mp = mp;
	}

	public int getMP() {
		return mp;
	}

	public void setDamageExpect(int d) {
		this.d_damage = d;
	}

	public int getDamageExpect() {
		return d_damage;
	}

	public void setBeforeRandomDamage(int i) {
		this.br_damage = i;
	}

	public int getBeforeRandomDamage() {
		return br_damage;
	}

	public void setHitrate(int i) {
		if (i > 100) {
			i = 100;
		}
		this.hitrate = i;
	}

	public int getHitrate() {
		return hitrate;
	}

	public void setHitrateExpect(int d) {
		if (d > 100) {
			d = 100;
		}
		this.d_hitrate = d;
	}

	public int getHitrateExpect() {
		return d_hitrate;
	}

	public void setBeforeRandomHitrate(int h) {
		if (h > 100) {
			h = 100;
		}
		this.br_hitrate = h;
	}

	public int getBeforeRandomHitrate() {
		return br_hitrate;
	}

	public void setHit(boolean h) {
		this.hit = h;
	}

	public boolean isHit() {
		return hit;
	}

	public void setGenre(int i) {
		this.genre = i;
	}

	public int getGenre() {
		return genre;
	}

	public void setStatus(int[] s) {
		this.status = s;
	}

	public int[] getStatus() {
		return status;
	}

	public void setStatus(int index, int a, int b) {
		status[index] = a;
		substatus[index] = b;
	}

	public void setStatus(int index, int o) {
		setStatus(index, o, SRPGStatus.getDefaultSubStatus(index));
	}

	public void setStatus(int i) {
		setStatus(i, SRPGStatus.getDefaultStatus(i));
	}

	public int getStatus(int i) {
		return status[i];
	}

	public void setStatusExpect(int[] s) {
		this.d_status = s;
	}

	public int[] getStatusExpect() {
		return d_status;
	}

	public void setStatusExpect(int index, int i) {
		d_status[index] = i;
	}

	public int getStatusExpect(int i) {
		return d_status[i];
	}

	public void setElement(int i, boolean flag) {
		this.element[i] = flag;
	}

	public void setElement(int i) {
		this.element[i] = true;
	}

	public boolean getElement(int i) {
		return element[i];
	}

	public void setDamageExpectString(String s) {
		this.d_damagestr = s;
	}

	public void setHitrateExpectString(String s) {
		this.d_hitratestr = s;
	}

	public void setHelperString(String s) {
		this.d_helper = s;
	}

	public void setAction(int i) {
		this.action = i;
	}

	public int getAction() {
		return action;
	}

	public SRPGStatus getActorStatus() {
		return stat;
	}

	public void setActorStatus(SRPGStatus s) {
		this.stat = s;
	}

	public int getDamageTrue() {
		if (hit) {
			return damage;
		} else {
			return 0;
		}
	}

	public String getDamageExpectString() {
		if (d_damagestr != null) {
			return d_damagestr;
		}
		if (d_damage == -1) {
			return "---";
		} else {
			return String.valueOf(d_damage);
		}
	}

	public String getHitrateExpectString() {
		if (d_hitratestr != null) {
			return d_hitratestr;
		}
		if (d_hitrate == -1) {
			return "---%";
		} else {
			return String.valueOf(d_hitrate) + "%";
		}
	}

	public String getHelperString() {
		if (d_helper != null) {
			return d_helper;
		} else {
			return "";
		}
	}

	public String getHelper() {
		return d_helper;
	}

	public void setPopupString(String s) {
		this.d_popup = s;
	}

	public String getPopupString() {
		if (d_popup != null) {
			return d_popup;
		} else {
			return "";
		}
	}

	public String getPopup() {
		return d_popup;
	}

	public void setHelpers(String s) {
		this.d_helper = s;
		this.d_popup = s;
	}

	public void setPosition(int x, int y) {
		this.posX = x;
		this.posY = y;
	}

	public void setPosition(SRPGActor actor) {
		setPosition(actor.getPosX(), actor.getPosY());
	}

	public int getPosX() {
		return posX;
	}

	public int getPosY() {
		return posY;
	}

	public void setDirection(int d) {
		this.direction = d;
	}

	public int getDirection() {
		return direction;
	}

	public SRPGMoveStack getMoveStack() {
		return ms;
	}

	public void setMoveStack(SRPGMoveStack move) {
		this.ms = move;
	}

	public SRPGEffect getNumberEffect(int index, String s, int x, int y) {
		LColor color = numColor1;
		if (index != -1) {
			switch (index) {
			case SRPGType.GENRE_ATTACK:
			case SRPGType.GENRE_ALLDAMAGE:
				color = numColor2;
				break;
			case SRPGType.GENRE_RECOVERY:
			case SRPGType.GENRE_ALLRECOVERY:
				color = numColor3;
				break;
			case SRPGType.GENRE_HELPER:
			case SRPGType.GENRE_CURE:
				return new SRPGEffect();
			case SRPGType.GENRE_MPDAMAGE:
				color = numColor4;
				break;
			case SRPGType.GENRE_MPRECOVERY:
				color = numColor5;
				break;
			}
		}
		return new SRPGNumberEffect(x, y, color, s);
	}

	public SRPGEffect getNumberEffect(int x, int y) {
		String s = "";
		int g = -1;
		if (isHit()) {
			s = String.valueOf(getDamage());
			g = getGenre();
		} else {
			s = "Miss";
		}
		return getNumberEffect(g, s, x, y);
	}

	public SRPGEffect[] getPopupEffect(int x, int y) {
		if (!isHit()) {
			return null;
		}
		int count = 0;
		for (int i = 0; i < status.length; i++) {
			if (status[i] != 0) {
				count++;
			}
		}
		if (count <= 0) {
			if (getPopup() != null) {
				SRPGEffect[] effs = new SRPGEffect[1];
				effs[0] = new SRPGPopupEffect(x, y, popColor, getHelperString());
				return effs;
			} else {
				return null;
			}
		}
		SRPGEffect[] effs = new SRPGEffect[count];
		count = 0;
		for (int i = 0; i < status.length; i++) {
			if (status[i] != 0) {
				effs[count] = new SRPGPopupEffect(x, y, popColor,
						SRPGStatus.STATUS_NAME[i]);
				count++;
			}
		}
		return effs;
	}
}
