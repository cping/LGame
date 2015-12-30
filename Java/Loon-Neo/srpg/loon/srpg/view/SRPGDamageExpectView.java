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
package loon.srpg.view;

import loon.LTexture;
import loon.canvas.Canvas;
import loon.canvas.Image;
import loon.canvas.LColor;
import loon.canvas.LGradation;
import loon.font.LFont;
import loon.font.Font.Style;
import loon.opengl.GLEx;
import loon.srpg.SRPGType;
import loon.srpg.ability.SRPGAbilityFactory;
import loon.srpg.ability.SRPGDamageData;
import loon.srpg.actor.SRPGActor;
import loon.srpg.actor.SRPGActors;
import loon.srpg.actor.SRPGStatus;
import loon.srpg.field.SRPGField;



// 默认的角色伤害预期试图
public class SRPGDamageExpectView extends SRPGDrawView {

	private SRPGDamageData dd;

	private SRPGAbilityFactory ab;

	private SRPGActor attacker;

	private SRPGActor defender;

	private final static LColor attackColor = new LColor(255, 220, 220),
			recoveryColornew = new LColor(220, 220, 255);

	private final static LFont deffont = LFont.getFont("Dialog", Style.PLAIN, 12);

	private static LTexture cache;

	public SRPGDamageExpectView(SRPGAbilityFactory ability, SRPGField field,
			SRPGActors actors, int atk, int def) {
		this(ability, field, actors, atk, def, 330, 100);
	}

	public SRPGDamageExpectView(SRPGAbilityFactory ability, SRPGField field,
			SRPGActors actors, int atk, int def, int w, int h) {
		setExist(true);
		setLock(false);
		super.width = w;
		super.height = h;
		this.attacker = actors.find(atk);
		if (def != -1) {
			this.defender = actors.find(def);
			this.dd = ability.getDamageExpect(field, actors, atk, def);
		} else {
			this.defender = null;
			this.dd = new SRPGDamageData();
			this.dd.setGenre(ability.getGenre());
		}
		this.ab = ability;
	}

	private void drawLazy(Canvas g) {
		float offsetY = -15;
		g.setFont(deffont);
		LGradation.getInstance(LColor.blue, LColor.black, super.width,
				super.height).drawHeight(g, 0, 0);
		SRPGStatus status = attacker.getActorStatus();
		g.setColor(LColor.black);
		g.fillRect(5 + 0, 2 + 0, 80, 3);
		if (status.max_hp > 0 && status.hp > 0) {
			int i = (status.hp * 80) / status.max_hp;
			g.setColor(96, 128, 255);
			g.fillRect(5 + 0, 2 + 0, i, 3);
		}
		g.setColor(LColor.white);
		g.drawText("ATTACK", 5 + 0, 15 + offsetY);
		g.drawText(status.name, 5 + 0, 75 + offsetY);
		g.drawText(String.valueOf(status.hp) + " / "
				+ String.valueOf(status.max_hp), 5 + 0, 90 + offsetY);
		g.setColor(LColor.white);
		g.drawText("DEFENCE", 115 + 0, 15 + offsetY);
		if (defender != null) {
			SRPGStatus status1 = defender.getActorStatus();
			g.setColor(LColor.black);
			g.fillRect(115 + 0, 2 + 0, 80, 3);
			if (status1.max_hp > 0 && status1.hp > 0) {
				int hp = (status1.hp * 80) / status1.max_hp;
				g.setColor(96, 128, 255);
				g.fillRect(115 + 0, 2 + 0, hp, 3);
			}
			g.setColor(LColor.white);
			g.drawText(status1.name, 115 + 0, 75 + offsetY);
			g.drawText(String.valueOf(status1.hp) + " / "
					+ String.valueOf(status1.max_hp), 115 + 0, 90 + offsetY);
		} else {
			g.drawText("- Nothing -", 115 + 0, 75 + offsetY);
		}
		LColor color = LColor.white;
		String s = "";
		// 判定使用的技能类型
		switch (dd.getGenre()) {
		// 普通攻击
		case SRPGType.GENRE_ATTACK:
			// 魔法伤害
		case SRPGType.GENRE_MPDAMAGE:
			// 全局伤害
		case SRPGType.GENRE_ALLDAMAGE:
			s = "ATTACK";
			color = attackColor;
			break;
		// 普通恢复
		case SRPGType.GENRE_RECOVERY:
			// 魔法恢复
		case SRPGType.GENRE_MPRECOVERY:
			// 全局恢复
		case SRPGType.GENRE_ALLRECOVERY:
			s = "RECOVERY";
			color = recoveryColornew;
			break;
		// 辅助技能
		case SRPGType.GENRE_HELPER:
			// 治疗
		case SRPGType.GENRE_CURE:
			s = "HELPER";
			break;
		// 不可用的空技能
		case -1:
			s = "---";
			break;
		}
		g.drawText("Ability", 230 + 0, 15 + offsetY);
		g.drawText(ab.getAbilityName(), 230 + 0, 35 + offsetY);
		g.drawText(s, 230 + 0, 60 + 0);
		g.drawText("STR", 230 + 0, 75 + offsetY);
		g.drawText("HIT", 230 + 0, 90 + offsetY);
		String s1 = dd.getHitrateExpectString();
		String s2 = dd.getDamageExpectString() + dd.getHelperString();
		if (defender == null) {
			s1 = "---";
			s2 = "---";
		}
		g.drawText(s1, 260 + 0, 90 + offsetY);
		g.setColor(color);
		g.drawText(s2, 260 + 0, 75 + offsetY);
	}

	@Override
	public void draw(GLEx gl) {
		if (!exist) {
			return;
		}
		if (cache == null) {
			Image image = Image.createImage(super.width, super.height);
			Canvas g = image.getCanvas();
			drawLazy(g);
			cache = image.texture();
			if (image != null) {
				image.close();
				image = null;
			}
			return;
		}
		gl.resetColor();
		gl.draw(cache, super.left, super.top);
		gl.draw(attacker.getImage(), 10 + super.left, 20 + super.top);
		if (defender != null) {
			gl.draw(defender.getImage(), 120 + super.left,
					20 + super.top);
		}
	}

	@Override
	public boolean isExist() {
		boolean exist = super.isExist();
		if (!exist) {
			if (cache != null) {
				cache.close();
				cache = null;
			}
		}
		return exist;
	}
}
