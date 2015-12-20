package loon.srpg.actor;

import java.util.ArrayList;
import java.util.Arrays;

import loon.srpg.SRPGType;
import loon.utils.CollectionUtils;


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
public class SRPGActorFactory {

	private final static ArrayList<SRPGActorStatus> lazyStatusClass = new ArrayList<SRPGActorStatus>(16);

	public static void putActorStatus(SRPGActorStatus s) {
		lazyStatusClass.add(s);
	}

	public static void setActorStatus(int index, SRPGActorStatus s) {
		lazyStatusClass.set(index, s);
	}

	public static void makeDefActorStatus() {
		if (lazyStatusClass.size() == 0) {
			int index = 0;

			SRPGActorStatus[] status = new SRPGActorStatus[16];

			// 0, 默认的骑士设置(下列数组中第一项参数为最大值，第二项为升级时上升的潜力值，以下同)
			status[index] = new SRPGActorStatus();
			status[index].status_jobname = "骑士";
			status[index].status_max_hp = new float[] { 80F, 9F };
			status[index].status_max_mp = new float[] { 0F, 0.0F };
			status[index].status_power = new float[] { 90F, 8.8F };
			status[index].status_vitality = new float[] { 45F, 6.5F };
			status[index].status_agility = new float[] { 40F, 4F };
			status[index].status_magic = new float[] { 0F, 0F };
			status[index].status_resume = new float[] { 0F, 0F };
			status[index].status_mind = new float[] { 25F, 3F };
			status[index].status_sp = new float[] { 35F, 5.5F };
			status[index].status_dexterity = new float[] { 55F, 6.5F };
			status[index].status_regeneration = new float[] { 7F, 0.9F };
			status[index].status_guardelement = new int[] { 105, 95, 105, 105,
					105, 95, 105, 105 };
			status[index].status_move = new float[] { 3F, 0F };
			status[index].ability = new int[] { 0, 1, 3, 6 };
			status[index].status_movetype = 0;
			index++;

			// 1, 默认的剑士设置
			status[index] = new SRPGActorStatus();
			status[index].status_jobname = "剑士";
			status[index].status_max_hp = new float[] { 92F, 10F };
			status[index].status_max_mp = new float[] { 15F, 0.5F };
			status[index].status_power = new float[] { 76F, 7.4F };
			status[index].status_vitality = new float[] { 50F, 7F };
			status[index].status_agility = new float[] { 50F, 6.8F };
			status[index].status_magic = new float[] { 10F, 0.01F };
			status[index].status_resume = new float[] { 1F, 0.01F };
			status[index].status_mind = new float[] { 40F, 4.2F };
			status[index].status_sp = new float[] { 10F, 6.5F };
			status[index].status_dexterity = new float[] { 80F, 9F };
			status[index].status_regeneration = new float[] { 9F, 1.0F };
			status[index].status_guardelement = new int[] { 95, 95, 95, 95, 95,
					95, 90, 95 };
			status[index].status_move = new float[] { 4F, 0F };
			status[index].ability = new int[] { 0, 5, 11, 6 };
			status[index].status_movetype = 0;
			index++;

			// 2, 默认的法师设置
			status[index] = new SRPGActorStatus();
			status[index].status_jobname = "法师";
			status[index].status_max_hp = new float[] { 45F, 5.7F };
			status[index].status_max_mp = new float[] { 100F, 10F };
			status[index].status_power = new float[] { 35F, 4.5F };
			status[index].status_vitality = new float[] { 25F, 10F };
			status[index].status_agility = new float[] { 30F, 4.5F };
			status[index].status_magic = new float[] { 70F, 9F };
			status[index].status_resume = new float[] { 12F, 0.02F };
			status[index].status_mind = new float[] { 40F, 7F };
			status[index].status_sp = new float[] { 50F, 9.8F };
			status[index].status_dexterity = new float[] { 40F, 7F };
			status[index].status_regeneration = new float[] { 7F, 2.4F };
			status[index].status_guardelement = new int[] { 80, 75, 80, 80, 80,
					75, 80, 70 };
			status[index].status_move = new float[] { 2F, 0F };
			status[index].ability = new int[] { 6, 18, 12, 15, 17 };
			status[index].status_movetype = 0;
			index++;

			// 3, 默认的弓箭手设置
			status[index] = new SRPGActorStatus();
			status[index].status_jobname = "弓箭手";
			status[index].status_max_hp = new float[] { 43F, 6.5F };
			status[index].status_max_mp = new float[] { 60F, 0.1F };
			status[index].status_power = new float[] { 30F, 3.1F };
			status[index].status_vitality = new float[] { 38F, 4F };
			status[index].status_agility = new float[] { 36F, 3.6F };
			status[index].status_magic = new float[] { 45F, 4.0F };
			status[index].status_resume = new float[] { 0F, 0.0F };
			status[index].status_mind = new float[] { 80F, 8F };
			status[index].status_sp = new float[] { 34F, 4.3F };
			status[index].status_dexterity = new float[] { 75F, 8F };
			status[index].status_regeneration = new float[] { 3F, 0.3F };
			status[index].status_guardelement = new int[] { 115, 110, 110, 110,
					110, 110, 110, 110 };
			status[index].status_move = new float[] { 3F, 0F };
			status[index].ability = new int[] { 1, 2, 5, 6 };
			status[index].status_movetype = 0;
			index++;

			// 4, 默认的医师设置
			status[index] = new SRPGActorStatus();
			status[index].status_jobname = "医师";
			status[index].status_max_hp = new float[] { 55F, 5.5F };
			status[index].status_max_mp = new float[] { 95F, 2.7F };
			status[index].status_power = new float[] { 40F, 4.4F };
			status[index].status_vitality = new float[] { 35F, 4F };
			status[index].status_agility = new float[] { 55F, 6.1F };
			status[index].status_magic = new float[] { 75F, 7.5F };
			status[index].status_resume = new float[] { 12F, 0.01F };
			status[index].status_mind = new float[] { 55F, 6.5F };
			status[index].status_sp = new float[] { 40F, 5.8F };
			status[index].status_dexterity = new float[] { 35F, 3.5F };
			status[index].status_regeneration = new float[] { 4F, 0.45F };
			status[index].status_guardelement = new int[] { 115, 95, 95, 90,
					90, 95, 105, 90 };
			status[index].status_move = new float[] { 2F, 0F };
			status[index].ability = new int[] { 0, 6, 7, 8, 9 };
			status[index].status_movetype = 0;
			index++;

			// 5, 默认的魔剑士设置
			status[index] = new SRPGActorStatus();
			status[index].status_jobname = "魔剑士";
			status[index].status_max_hp = new float[] { 85F, 9.5F };
			status[index].status_max_mp = new float[] { 90F, 1.7F };
			status[index].status_power = new float[] { 65F, 7.4F };
			status[index].status_vitality = new float[] { 49F, 6.8F };
			status[index].status_agility = new float[] { 55F, 7.5F };
			status[index].status_magic = new float[] { 65F, 7.5F };
			status[index].status_resume = new float[] { 4F, 0.02F };
			status[index].status_mind = new float[] { 40F, 4.2F };
			status[index].status_sp = new float[] { 50F, 6.8F };
			status[index].status_dexterity = new float[] { 65F, 7.5F };
			status[index].status_regeneration = new float[] { 7F, 0.88F };
			status[index].status_guardelement = new int[] { 95, 100, 100, 90,
					100, 90, 80, 110 };
			status[index].status_move = new float[] { 4F, 0F };
			status[index].ability = new int[] { 0, 5, 20, 17, 6 };
			status[index].status_movetype = 0;
			index++;

			// 6, 默认的舞女设置
			status[index] = new SRPGActorStatus();
			status[index].status_jobname = "舞女";
			status[index].status_max_hp = new float[] { 40F, 4F };
			status[index].status_max_mp = new float[] { 10F, 1.2F };
			status[index].status_power = new float[] { 55F, 6.2F };
			status[index].status_vitality = new float[] { 38F, 4F };
			status[index].status_agility = new float[] { 90F, 6.3F };
			status[index].status_magic = new float[] { 15F, 1.0F };
			status[index].status_resume = new float[] { 1F, 0.1F };
			status[index].status_mind = new float[] { 30F, 3F };
			status[index].status_sp = new float[] { 35F, 4.5F };
			status[index].status_dexterity = new float[] { 85F, 8F };
			status[index].status_regeneration = new float[] { 6F, 0.7F };
			status[index].status_guardelement = new int[] { 100, 100, 100, 100,
					100, 100, 120, 100 };
			status[index].status_move = new float[] { 5F, 0F };
			status[index].ability = new int[] { 0, 4, 6 };
			status[index].status_movetype = 0;
			index++;

			// 7, 默认的飞行兵设置
			status[index] = new SRPGActorStatus();
			status[index].status_jobname = "飞行兵";
			status[index].status_max_hp = new float[] { 80F, 4F };
			status[index].status_max_mp = new float[] { 80F, 1.4F };
			status[index].status_power = new float[] { 69F, 7F };
			status[index].status_vitality = new float[] { 42F, 6F };
			status[index].status_agility = new float[] { 80F, 7.3F };
			status[index].status_magic = new float[] { 11F, 1.0F };
			status[index].status_resume = new float[] { 1F, 0.1F };
			status[index].status_mind = new float[] { 45F, 6.5F };
			status[index].status_sp = new float[] { 35F, 4.5F };
			status[index].status_dexterity = new float[] { 85F, 8F };
			status[index].status_regeneration = new float[] { 7F, 0.7F };
			status[index].status_guardelement = new int[] { 100, 150, 150, 150,
					150, 150, 150, 150 };
			status[index].status_move = new float[] { 5F, 0F };
			status[index].ability = new int[] { 0, 18, 6 };
			status[index].status_movetype = 32;
			index++;

			// 8,默认的士兵设置
			status[index] = new SRPGActorStatus();
			status[index].status_jobname = "士兵";
			status[index].status_max_hp = new float[] { 100F, 7.1F };
			status[index].status_max_mp = new float[] { 0F, 0F };
			status[index].status_power = new float[] { 30F, 3.0F };
			status[index].status_vitality = new float[] { 33F, 4F };
			status[index].status_agility = new float[] { 13F, 1.3F };
			status[index].status_magic = new float[] { 11F, 0.1F };
			status[index].status_resume = new float[] { 0F, 0.0F };
			status[index].status_mind = new float[] { 15F, 1.5F };
			status[index].status_sp = new float[] { 13F, 1.5F };
			status[index].status_dexterity = new float[] { 30F, 3F };
			status[index].status_regeneration = new float[] { 0F, 0.0F };
			status[index].status_guardelement = new int[] { 100, 100, 100, 100,
					100, 100, 100, 100 };
			status[index].status_move = new float[] { 3F, 0.0F };
			status[index].ability = new int[] { 0, 6 };
			status[index].status_movetype = 0;
			index++;

			// 9,默认的大将设置
			status[index] = new SRPGActorStatus();
			status[index].status_jobname = "大将";
			status[index].status_max_hp = new float[] { 110F, 11F };
			status[index].status_max_mp = new float[] { 100F, 1.0F };
			status[index].status_power = new float[] { 80F, 8.0F };
			status[index].status_vitality = new float[] { 70F, 8.0F };
			status[index].status_agility = new float[] { 70F, 8.0F };
			status[index].status_magic = new float[] { 50F, 5.1F };
			status[index].status_resume = new float[] { 14F, 0.0F };
			status[index].status_mind = new float[] { 40F, 4.2F };
			status[index].status_sp = new float[] { 40F, 6.5F };
			status[index].status_dexterity = new float[] { 70F, 7.5F };
			status[index].status_regeneration = new float[] { 9F, 1.0F };
			status[index].status_guardelement = new int[] { 90, 95, 95, 95, 95,
					95, 95, 95 };
			status[index].status_move = new float[] { 4F, 0.0F };
			status[index].ability = new int[] { 0, 3, 6, 11 };
			status[index].status_movetype = 0;
			index++;

			// 10,默认的君主设置
			status[index] = new SRPGActorStatus();
			status[index].status_jobname = "君主";
			status[index].status_max_hp = new float[] { 120F, 12F };
			status[index].status_max_mp = new float[] { 110F, 3.0F };
			status[index].status_power = new float[] { 90F, 8.6F };
			status[index].status_vitality = new float[] { 71F, 8.1F };
			status[index].status_agility = new float[] { 71F, 8.1F };
			status[index].status_magic = new float[] { 60F, 6.1F };
			status[index].status_resume = new float[] { 18F, 0.0F };
			status[index].status_mind = new float[] { 42F, 4.2F };
			status[index].status_sp = new float[] { 40F, 6.5F };
			status[index].status_dexterity = new float[] { 90F, 8.5F };
			status[index].status_regeneration = new float[] { 9F, 1.0F };
			status[index].status_guardelement = new int[] { 80, 85, 95, 85, 95,
					95, 85, 95 };
			status[index].status_move = new float[] { 4F, 0.0F };
			status[index].ability = new int[] { 0, 3, 6, 8, 19 };
			status[index].status_movetype = 0;
			index++;

			// 11,默认的护国神蟹设置
			status[index] = new SRPGActorStatus();
			status[index].status_jobname = "护国神蟹";
			status[index].status_max_hp = new float[] { 9999F, 99F };
			status[index].status_max_mp = new float[] { 1000F, 0.01F };
			status[index].status_power = new float[] { 3333F, 33F };
			status[index].status_vitality = new float[] { 9999F, 99F };
			status[index].status_agility = new float[] { 1F, 0.1F };
			status[index].status_magic = new float[] { 1F, 0.1F };
			status[index].status_resume = new float[] { 1F, 0.0F };
			status[index].status_mind = new float[] { 1F, 0.1F };
			status[index].status_sp = new float[] { 1F, 0.1F };
			status[index].status_dexterity = new float[] { 1F, 0.1F };
			status[index].status_regeneration = new float[] { 1F, 0.1F };
			status[index].status_guardelement = null;
			status[index].status_move = new float[] { 1F, 0.01F };
			status[index].ability = new int[] { 0, 10, 22 };
			status[index].computer = SRPGType.NOMOVE;
			status[index].immunity = new int[] { SRPGStatus.ELEMENT_VOID };
			status[index].status_movetype = 16;
			index++;

			// 12,默认的真龙设置
			status[index] = new SRPGActorStatus();
			status[index].status_jobname = "真龙";
			status[index].status_max_hp = new float[] { 6666F, 66F };
			status[index].status_max_mp = new float[] { 3000F, 13.3F };
			status[index].status_power = new float[] { 3000F, 16F };
			status[index].status_vitality = new float[] { 1650F, 11F };
			status[index].status_agility = new float[] { 300F, 16.5F };
			status[index].status_magic = new float[] { 366F, 9F };
			status[index].status_resume = new float[] { 21.0F, 0.01F };
			status[index].status_mind = new float[] { 329F, 4.5F };
			status[index].status_sp = new float[] { 144F, 1.4F };
			status[index].status_dexterity = new float[] { 980F, 9.8F };
			status[index].status_regeneration = new float[] { 10.0F, 1.3F };
			status[index].status_guardelement = new int[] { 110, 110, 110, 110,
					110, 110, 120, 100 };
			status[index].status_move = new float[] { 6F, 0.03F };
			status[index].ability = new int[] { 0, 11, 13, 16, 19 };
			status[index].immunity = new int[] { SRPGStatus.ELEMENT_FIRE };
			status[index].status_movetype = 32;
			index++;

			// 13,默认的圣魔皇帝设置
			status[index] = new SRPGActorStatus();
			status[index].status_jobname = "圣魔皇帝";
			status[index].status_max_hp = new float[] { 5000F, 50F };
			status[index].status_max_mp = new float[] { 1000F, 10F };
			status[index].status_power = new float[] { 2000F, 20F };
			status[index].status_vitality = new float[] { 1950F, 15F };
			status[index].status_agility = new float[] { 600F, 6.5F };
			status[index].status_magic = new float[] { 666F, 6F };
			status[index].status_resume = new float[] { 20F, 0.2F };
			status[index].status_mind = new float[] { 666F, 6F };
			status[index].status_sp = new float[] { 444F, 4F };
			status[index].status_dexterity = new float[] { 1000F, 10F };
			status[index].status_regeneration = new float[] { 8.0F, 0.88F };
			status[index].status_guardelement = new int[] { 90, 95, 95, 95, 95,
					95, 95, 95 };
			status[index].status_move = new float[] { 4F, 0.02F };
			status[index].ability = new int[] { 0, 10, 14, 19, 21 };
			status[index].immunity = new int[] { SRPGStatus.ELEMENT_DARK };
			status[index].status_movetype = 1;
			index++;

			// 14,默认的创世神设置
			status[index] = new SRPGActorStatus();
			status[index].status_jobname = "创世神";
			status[index].status_max_hp = new float[] { 1000F, 15F };
			status[index].status_max_mp = new float[] { 8888F, 20F };
			status[index].status_power = new float[] { 1000F, 15F };
			status[index].status_vitality = new float[] { 1000F, 15F };
			status[index].status_agility = new float[] { 1000F, 15F };
			status[index].status_magic = new float[] { 9999F, 20F };
			status[index].status_resume = new float[] { 200F, 0.02F };
			status[index].status_mind = new float[] { 3000F, 30F };
			status[index].status_sp = new float[] { 1000F, 10F };
			status[index].status_dexterity = new float[] { 1000F, 10F };
			status[index].status_regeneration = new float[] { 15F, 1.0F };
			status[index].status_guardelement = new int[] { 85, 95, 95, 95, 95,
					95, 85, 105 };
			status[index].status_move = new float[] { 5F, 0.05F };
			status[index].ability = new int[] { 0, 10, 13, 20, 21 };
			status[index].immunity = new int[] { SRPGStatus.ELEMENT_PHYSICS,
					SRPGStatus.ELEMENT_SAINT, SRPGStatus.ELEMENT_PHYSICS,
					SRPGStatus.ELEMENT_DARK };
			status[index].status_movetype = 32;
			index++;

			// 15,默认的程序员设置
			status[index] = new SRPGActorStatus();
			status[index].status_jobname = "程序员";
			status[index].status_max_hp = new float[] { 10F, 0.5F };
			status[index].status_max_mp = new float[] { 99.9F, 99.9F };
			status[index].status_power = new float[] { 10F, 0.4F };
			status[index].status_vitality = new float[] { 90F, 9F };
			status[index].status_agility = new float[] { 10F, 1.2F };
			status[index].status_magic = new float[] { 99.9F, 99.9F };
			status[index].status_resume = new float[] { 15F, 0.1F };
			status[index].status_mind = new float[] { 100F, 100F };
			status[index].status_sp = new float[] { 40F, 5.5F };
			status[index].status_dexterity = new float[] { 60F, 6F };
			status[index].status_regeneration = new float[] { 4F, 1.0F };
			status[index].status_guardelement = new int[] { 90, 100, 100, 95,
					100, 100, 95, 105 };
			status[index].status_move = new float[] { 1F, 0.01F };
			status[index].ability = new int[] { 0, 6, 22, 23 };
			status[index].immunity = new int[] { SRPGStatus.ELEMENT_DARK };
			status[index].status_movetype = 1;
			index++;

			lazyStatusClass.addAll(Arrays.asList(status));

		}
	}


	SRPGActorFactory() {
	}

	public static SRPGStatus makeActorStatus(int index) {
		return makeActorStatus(index, 1);
	}

	public static SRPGStatus makeActorStatus(String name, int index, int level) {
		return makeActorStatus(name, index, level, 0);
	}

	public static SRPGStatus makeActorStatus(String name, int index, int level,
			int team) {
		return makeActorStatus(name, index, level, team, team);
	}

	public static SRPGStatus makeActorStatus(String name, int index, int level,
			int team, int group) {
		SRPGStatus status = makeActorStatus(index, level, team, group);
		if (name != null) {
			status.name = name;
		} else {
			status.name = "Actor" + index;
		}
		return status;
	}

	public static SRPGStatus makeActorStatus(int index, int level) {
		return makeActorStatus(index, level, 0, 0);
	}

	public static SRPGStatus makeActorStatus(int index, int level, int team,
			int group) {
		SRPGActorStatus actorStatus = lazyStatusClass
				.get(index);
		SRPGStatus status = new SRPGStatus();
		if (actorStatus != null) {
			status.number = index;
			status.jobname = actorStatus.status_jobname;
			runLevelUp(status, level);
			status.hp = status.max_hp;
			status.mp = status.max_mp;
			status.team = team;
			status.group = group;
			status.action = 0;
			if (actorStatus.immunity != null) {
				status.immunity = CollectionUtils.copyOf(actorStatus.immunity);
			}
			if (actorStatus.computer != null) {
				status.computer = CollectionUtils.copyOf(actorStatus.computer);
			}
			if (actorStatus.ability != null) {
				status.ability = CollectionUtils.copyOf(actorStatus.ability);
			}
			if (actorStatus.status_guardelement != null) {
				status.guardelement = CollectionUtils
						.copyOf(actorStatus.status_guardelement);
			}
		}
		return status;
	}

	public static SRPGStatus runLevelUp(SRPGStatus status, int level) {
		SRPGActorStatus actorStatus = lazyStatusClass
				.get(status.number);
		status.level = level;
		status.max_hp = (int) (actorStatus.status_max_hp[0] + actorStatus.status_max_hp[1]
				* (level - 1));
		status.max_mp = (int) (actorStatus.status_max_mp[0] + actorStatus.status_max_mp[1]
				* (level - 1));
		status.strength = (int) (actorStatus.status_power[0] + actorStatus.status_power[1]
				* (level - 1));
		status.vitality = (int) (actorStatus.status_vitality[0] + actorStatus.status_vitality[1]
				* (level - 1));
		status.agility = (int) (actorStatus.status_agility[0] + actorStatus.status_agility[1]
				* (level - 1));
		status.magic = (int) (actorStatus.status_magic[0] + actorStatus.status_magic[1]
				* (level - 1));
		status.resume = (int) (actorStatus.status_resume[0] + actorStatus.status_resume[1]
				* (level - 1));
		status.mind = (int) (actorStatus.status_mind[0] + actorStatus.status_mind[1]
				* (level - 1));
		status.sp = (int) (actorStatus.status_sp[0] + actorStatus.status_sp[1]
				* (level - 1));
		status.dexterity = (int) (actorStatus.status_dexterity[0] + actorStatus.status_dexterity[1]
				* (level - 1));
		status.regeneration = (int) (actorStatus.status_regeneration[0] + actorStatus.status_regeneration[1]
				* (level - 1));
		status.move = (int) (actorStatus.status_move[0] + actorStatus.status_move[1]
				* (level - 1));
		status.movetype = actorStatus.status_movetype;
		return status;
	}

}
