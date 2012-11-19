package loon.srpg;

import loon.srpg.ability.SRPGAbilityFactory;
import loon.srpg.ability.SRPGDamageData;
import loon.srpg.actor.SRPGActor;
import loon.srpg.actor.SRPGActors;
import loon.srpg.actor.SRPGStatus;
import loon.srpg.field.SRPGField;
import loon.srpg.field.SRPGFieldMove;


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
public class SRPGAI {

	private int[] think;

	private int number;

	private int move_x;

	private int move_y;

	private int[][] route;

	private int abi_x;

	private int abi_y;

	private int ability;

	private int direction;

	private SRPGField field;

	private SRPGActors actors;

	public SRPGAI(SRPGField field, SRPGActors actors, int i, int[] think) {
		this.set(field, actors, i, think);
	}

	public void set(SRPGField field, SRPGActors actors, int i, int[] think) {
		this.field = field;
		this.actors = actors;
		this.number = i;
		this.think = think;
		this.reset();
	}

	public void reset() {
		this.abi_x = -1;
		this.abi_y = -1;
		this.ability = -1;
		this.route = null;
		this.direction = -1;
		this.move_x = -1;
		this.move_y = -1;
	}

	public void runThinking() {
		for (int i = 0; i < think.length && !entryThinking(think[i]); i++) {
			;
		}
	}

	private boolean entryThinking(int type) {
		switch (type) {
		// 普通的AI运算
		case SRPGType.TYPE_NORMAL:
			normalThinking(true);
			return true;
			// 牧师类职业AI运算
		case SRPGType.TYPE_PRIEST:
			return priestThinking();
			// 法师类职业AI运算
		case SRPGType.TYPE_WIZARD:
			return wizardThinking();
			// 不进行移动
		case SRPGType.TYPE_NOMOVE:
			return nomoveThinking();
			// 采取逃避策略
		case SRPGType.TYPE_ESCAPE:
			return escapeThinking();
			// 牧师类职业AI运算(选择较低生命值的对象进行技能释放)
		case SRPGType.TYPE_PRIEST_LOWER:
			return priestLowerThinking();
			// 什么也不做，等待下一个处理
		case SRPGType.TYPE_WAIT:
		default:
			return false;
		}
	}

	private void toDoubleArraySort(int[][] arrays, int index, boolean flag,
			boolean flag1) {
		boolean isCycle = true;
		for (; isCycle;) {
			isCycle = false;
			int j = 0;
			for (; j < arrays[index].length - 1;) {
				boolean res = arrays[index][j] > arrays[index][j + 1];
				if (flag1
						&& (arrays[index][j] > 0 && arrays[index][j + 1] <= 0 || arrays[index][j] <= 0
								&& arrays[index][j + 1] > 0)) {
					res = !res;
				}
				if (!flag) {
					res = !res;
				}
				if (res) {
					for (int i = 0; i < arrays.length; i++) {
						int l = arrays[i][j];
						arrays[i][j] = arrays[i][j + 1];
						arrays[i][j + 1] = l;
						isCycle = true;
					}
				}
				j++;
			}
		}

	}

	/**
	 * 仅选择生命值较低的角色进行恢复
	 * 
	 * @return
	 */
	private boolean priestLowerThinking() {
		int group = actors.find(number).getActorStatus().group;
		for (int i = 0; i < actors.size(); i++) {
			SRPGActor actor = actors.find(i);
			if (!actor.isVisible()) {
				continue;
			}
			SRPGStatus status = actor.getActorStatus();
			if (group != status.group || status.hp >= status.max_hp / 2) {
				continue;
			}
			priestThinking();
			if (ability == -1) {
				reset();
				return false;
			} else {
				return true;
			}
		}
		return false;
	}

	private void normalThinking(boolean flag) {
		SRPGActor actor = actors.find(number);
		int group = actor.getActorStatus().group;
		int index1 = -1;
		int location = -1;
		for (int i = 0; i < actors.size(); i++) {
			SRPGActor actorObject = actors.find(i);
			if (!actorObject.isVisible()
					|| group == actorObject.getActorStatus().group) {
				continue;
			}
			int x = actorObject.getPosX() - actor.getPosX();
			int y = actorObject.getPosY() - actor.getPosY();
			if (x < 0) {
				x *= -1;
			}
			if (y < 0) {
				y *= -1;
			}
			if (location > x + y || location == -1) {
				index1 = i;
				location = x + y;
			}
		}
		if (index1 == -1) {
			return;
		}
		SRPGActor actorObject1 = actors.find(index1);
		SRPGFieldMove fieldmove =  SRPGFieldMove.getInstance(field.getMoveSpaceAll(actors, number));
		int[][] power1 = fieldmove.movePower(actorObject1.getPosX(),
				actorObject1.getPosY(), actor.getPosX(), actor.getPosY());
		int[][] power2 = fieldmove.movePower(actor.getPosX(), actor.getPosY(),
				actor.getActorStatus().move);
		int nx = actor.getPosX();
		int ny = actor.getPosY();
		if (power1 != null) {
			int direction = 0;
			int index = power1[actor.getPosY()][actor.getPosX()];
			for (int y = 0; y < power2.length; y++) {
				for (int x = 0; x < power2[y].length; x++) {
					if (power2[y][x] == -1 || actors.checkActor(x, y) != -1
							&& actors.checkActor(x, y) != number
							|| power1[y][x] > index || power1[y][x] == -1) {
						continue;
					}
					int d1 = actorObject1.findDirection(x, y);
					int d2 = actorObject1.getDirection();
					int d = SRPGType.MOVE_DOWN;
					if (d1 == d2) {
						d = SRPGType.MOVE_DOWN;
					} else if (d1 == SRPGType.MOVE_UP
							&& d2 == SRPGType.MOVE_DOWN
							|| d1 == SRPGType.MOVE_DOWN
							&& d2 == SRPGType.MOVE_UP
							|| d1 == SRPGType.MOVE_RIGHT
							&& d2 == SRPGType.MOVE_LEFT
							|| d1 == SRPGType.MOVE_LEFT
							&& d2 == SRPGType.MOVE_RIGHT) {
						d = SRPGType.MOVE_RIGHT;
					} else {
						d = SRPGType.MOVE_LEFT;
					}
					boolean running = false;
					if (power1[y][x] == index) {
						if (d >= direction) {
							running = true;
						}
					} else {
						running = true;
					}
					if (running) {
						index = power1[y][x];
						nx = x;
						ny = y;
						direction = d;
					}
				}
			}
		}
		this.move_x = nx;
		this.move_y = ny;
		this.route = fieldmove.moveRoute(actor.getPosX(), actor.getPosY(),
				move_x, move_y, actor.getActorStatus().move);
		int[] target = SRPGAbilityFactory.getTargetTrue(SRPGAbilityFactory.filtedRange(SRPGAbilityFactory
				.filtedAbility(actor.getActorStatus().ability, actor
						.getActorStatus(), true), field, move_x, move_y,
				actorObject1.getPosX(), actorObject1.getPosY()), actor
				.getActorStatus().group, actorObject1.getActorStatus().group);
		if (target != null) {
			int optimizeAbility = SRPGAbilityFactory.getOptimizeAbility(target, field,
					actors, number, index1);
			if (flag
					&& (SRPGAbilityFactory.getInstance(optimizeAbility)).getOptimizeOriginal(field,
							actors, number, index1)[0] <= 0) {
				return;
			}
			this.ability = optimizeAbility;
			this.abi_x = actorObject1.getPosX();
			this.abi_y = actorObject1.getPosY();
		} else {
			this.ability = -1;
			int location1 = -1;
			int x1 = -1;
			int x2 = -1;
			for (int i = 0; i < actors.size(); i++) {
				SRPGActor actorObject2 = actors.find(i);
				if (!actorObject2.isVisible()
						|| actorObject2.getActorStatus().group == group) {
					continue;
				}
				int x = move_x - actorObject2.getPosX();
				int y = move_y - actorObject2.getPosY();
				if (x < 0) {
					x *= -1;
				}
				if (y < 0) {
					y *= -1;
				}
				if (x + y < location1 || location1 == -1) {
					location1 = x + y;
					x1 = actorObject2.getPosX();
					x2 = actorObject2.getPosY();
				}
			}

			if (location1 != -1) {
				int[] pos = actor.getPos();
				actor.setPos(move_x, move_y);
				direction = actor.findDirection(x1, x2);
				actor.setPos(pos);
			}
		}
	}

	/**
	 * 牧师的行动模式
	 * 
	 * @return
	 */
	private boolean priestThinking() {
		SRPGActor actor = actors.find(number);
		int group = actor.getActorStatus().group;
		int[] target = SRPGAbilityFactory.getTargetTrue(SRPGAbilityFactory.filtedAbility(actor
				.getActorStatus().ability, actor.getActorStatus(), true),
				group, group);
		int[] groups = actors.getGroupArray(group);
		int cgroups[] = new int[groups.length];
		for (int j = 0; j < groups.length; j++) {
			SRPGStatus status = actors.find(groups[j]).getActorStatus();
			cgroups[j] = (status.hp * 100) / status.max_hp;
		}

		int[][] res = { groups, cgroups };
		toDoubleArraySort(res, 1, true, true);
		SRPGFieldMove fieldmove = SRPGFieldMove.getInstance(field.getMoveSpaceAll(actors, number));
		int[][] moveArea = fieldmove.moveArea(actor.getPosX(), actor.getPosY(),
				actor.getActorStatus().move);
		boolean flag = false;
		int mp = -1;
		int point = -1;
		int cx = actor.getPosX();
		int cy = actor.getPosY();
		int abilityIndex = -1;
		boolean ret = false;
		int mx = cx;
		int my = cy;
		int location = -1;
		int location1 = -1;
		for (int l2 = 0; l2 < groups.length; l2++) {
			SRPGActor actorObject = actors.find(groups[l2]);
			if (cgroups[l2] > 75
					&& !actorObject.getActorStatus().getBadStatus()) {
				continue;
			}
			for (int y = 0; y < moveArea.length; y++) {
				for (int x = 0; x < moveArea[y].length; x++) {
					if (moveArea[y][x] != 0 || actors.checkActor(x, y) != -1
							&& actors.checkActor(x, y) != number) {
						continue;
					}
					int posX = x - actorObject.getPosX();
					int posY = y - actorObject.getPosY();
					if (posX < 0) {
						posX *= -1;
					}
					if (posY < 0) {
						posY *= -1;
					}
					int[] range = SRPGAbilityFactory.filtedRange(target, field, x, y,
							actorObject.getPosX(), actorObject.getPosY());
					if (cgroups[l2] > 50
							&& actorObject.getActorStatus().getBadStatus()
							&& range != null) {
						int size = 0;
						do {
							if (size >= range.length) {
								break;
							}
							if ((SRPGAbilityFactory.getInstance(range[size])).getGenre() == 3) {
								int crange = range[size];
								range = new int[1];
								range[0] = crange;
								break;
							}
							size++;
						} while (true);
					}
					int[] pos = actor.getPos();
					actor.setPos(x, y);
					int optimizeAbility = SRPGAbilityFactory.getOptimizeAbility(range,
							field, actors, number, groups[l2]);
					int optimizePoint = SRPGAbilityFactory.getOptimizePoint(
							optimizeAbility, field, actors, number, groups[l2]);
					actor.setPos(pos);
					if (optimizePoint != -1) {
						SRPGAbilityFactory ability1 = SRPGAbilityFactory.getInstance(optimizeAbility);
						boolean result = false;
						if (point < optimizePoint) {
							result = true;
						} else if (point == optimizePoint) {
							if (location1 < posX + posY) {
								result = true;
							} else if (location1 == posX + posY
									&& (ability1.getMP() < mp || mp == -1)) {
								result = true;
							}
						}
						if (result) {
							if (!actorObject.getActorStatus().getBadStatus()
									&& ability1.getGenre() == 3
									|| ability1.getGenre() == 1
									&& (optimizePoint == 0 || actorObject
											.getActorStatus().hp >= actorObject
											.getActorStatus().max_hp)) {
								continue;
							}
							flag = true;
							point = optimizePoint;
							location1 = posX + posY;
							mp = ability1.getMP();
							abilityIndex = optimizeAbility;
							if (number != groups[l2]) {
								cx = x;
								cy = y;
							}
							continue;
						}
					}
					if (l2 != 0 || location <= posX + posY && location != -1) {
						continue;
					}
					location = posX + posY;
					if (number != groups[l2]) {
						mx = x;
						my = y;
					}
					ret = true;
				}

			}

			if (!flag) {
				continue;
			}
			route = fieldmove.moveRoute(actor.getPosX(), actor.getPosY(), cx,
					cy, actor.getActorStatus().move);
			ability = abilityIndex;
			move_x = cx;
			move_y = cy;
			abi_x = actorObject.getPosX();
			abi_y = actorObject.getPosY();
			if ((SRPGAbilityFactory.getInstance(ability)).getSelectNeed() == 1) {
				int[] pos = actor.getPos();
				actor.setPos(cx, cy);
				int[] position = optimizePosition(SRPGAbilityFactory.getInstance(ability), field,
						actors, number, cx, cy, actorObject.getPosX(),
						actorObject.getPosY())[0];
				abi_x = position[0];
				abi_y = position[1];
				actor.setPos(pos);
			}
			break;
		}

		if (!flag && ret) {
			route = fieldmove.moveRoute(actor.getPosX(), actor.getPosY(), mx,
					my, actor.getActorStatus().move);
			move_x = mx;
			move_y = my;
		}
		return route != null || ability != -1;
	}

	/**
	 * 法师的行动模式
	 * 
	 * @return
	 */
	private boolean wizardThinking() {
		SRPGActor actor = actors.find(number);
		int group = actor.getActorStatus().group;
		int[] target = SRPGAbilityFactory.getTargetTrue(SRPGAbilityFactory.filtedAbility(actor
				.getActorStatus().ability, actor.getActorStatus(), true),
				group, group + 1);
		if (target == null) {
			return false;
		}
		int[][] groups = new int[2][1];
		groups[0] = actors.getGroupArray(group, false);
		groups[1] = new int[groups[0].length];
		for (int i = 0; i < groups[0].length; i++) {
			groups[1][i] = actors.find(groups[0][i]).getActorStatus().hp;
		}
		toDoubleArraySort(groups, 1, true, true);
		SRPGFieldMove fieldmove = SRPGFieldMove.getInstance(field.getMoveSpaceAll(actors, number));
		int[][] moveArea = fieldmove.moveArea(actor.getPosX(), actor.getPosY(),
				actor.getActorStatus().move);
		for (int i = 0; i < actors.size(); i++) {
			SRPGActor actorObject = actors.find(i);
			if (actorObject.isVisible() && i != number) {
				moveArea[actorObject.getPosY()][actorObject.getPosX()] = -1;
			}
		}
		boolean flag = false;
		boolean flag1 = false;
		int crandomDamage = 0;
		int crandomHitrate = 0;
		int ncurrentDamage = 0;
		int[] pos = new int[2];
		pos[0] = actor.getPosX();
		pos[1] = actor.getPosY();
		int ctarget = -1;
		int groupIndex = -1;
		int size = 0;
		do {
			if (size >= groups[0].length) {
				break;
			}
			SRPGActor actorObject = actors.find(groups[0][size]);
			for (int i = 0; i < target.length; i++) {
				SRPGAbilityFactory ability1 = SRPGAbilityFactory.getInstance(target[i]);
				int minLength = ability1.getMinLength();
				int maxLength = ability1.getMaxLength();
				int range = ability1.getRange();
				if (ability1.getSelectNeed() == 1) {
					maxLength += range;
					minLength -= range;
					if (minLength < 0) {
						minLength = 0;
					}
				}
				SRPGFieldMove fieldMove1 = SRPGFieldMove.getInstance(field.getMoveSpace(19));
				int[][] movePower = fieldMove1.movePower(actorObject.getPosX(),
						actorObject.getPosY(), maxLength);
				for (int y = 0; y < field.getHeight(); y++) {
					for (int x = 0; x < field.getWidth(); x++) {
						if (moveArea[y][x] != 0 || movePower[y][x] < minLength
								|| movePower[y][x] > maxLength
								|| movePower[y][x] == -1) {
							continue;
						}
						int[] move = actor.getPos();
						actor.setPos(x, y);
						SRPGDamageData damagedata = ability1.getDamageExpect(field,
								actors, number, groups[0][size]);
						actor.setPos(move);
						int randomDamage = 0;
						int randomHitrate = 0;
						int currentDamage = 0;
						boolean result = false;
						boolean hpCheck = actorObject.getActorStatus().hp > 0;
						if (damagedata.getGenre() == SRPGType.GENRE_ATTACK) {
							randomDamage = damagedata.getBeforeRandomDamage();
							randomHitrate = damagedata.getBeforeRandomHitrate();
							currentDamage = (randomDamage * randomHitrate) / 100;
							if (currentDamage <= 0 && ability1.getDirect() == 0) {
								continue;
							}
							if (randomDamage >= actorObject.getActorStatus().hp
									&& hpCheck) {
								result = true;
							}
						}
						if (damagedata.getGenre() == SRPGType.GENRE_HELPER) {
							randomHitrate = damagedata.getBeforeRandomHitrate();
							boolean ret = true;
							for (int j = 0; j < SRPGStatus.STATUS_MAX; j++) {
								if (damagedata.getStatusExpect(j) != 0
										&& actorObject.getActorStatus().status[j] == 0) {
									ret = false;
								}
							}
							if (ret || randomHitrate <= 0) {
								continue;
							}
						}
						boolean ret = false;
						if (result) {
							if (!flag) {
								ret = true;
							} else if (crandomHitrate <= randomHitrate) {
								if (crandomHitrate < randomHitrate) {
									ret = true;
								} else if (ncurrentDamage <= currentDamage
										&& ncurrentDamage < currentDamage) {
									ret = true;
								}
							}
						} else if (!flag)
							if (!flag1
									&& hpCheck
									&& (ncurrentDamage <= 0
											|| currentDamage > 0 || ability1
											.getGenre() == 2
											&& randomHitrate > 0)) {
								ret = true;
							} else if (flag1 == hpCheck
									&& ncurrentDamage <= currentDamage) {
								if (ncurrentDamage < currentDamage) {
									ret = true;
								} else if (crandomDamage <= randomDamage) {
									if (crandomDamage < randomDamage) {
										ret = true;
									} else if (crandomHitrate <= randomHitrate
											&& crandomHitrate < randomHitrate) {
										ret = true;
									}
								}
							}
						if (ret) {
							pos[0] = x;
							pos[1] = y;
							crandomHitrate = randomHitrate;
							crandomDamage = randomDamage;
							ncurrentDamage = currentDamage;
							ctarget = target[i];
							groupIndex = groups[0][size];
							flag = result;
							flag1 = hpCheck;
						}
					}

				}

			}

			if (ctarget != -1 && (SRPGAbilityFactory.getInstance(ctarget)).getGenre() == 2) {
				break;
			}
			size++;
		} while (true);
		if (groupIndex != -1 && ctarget != -1) {
			SRPGActor actorObject = actors.find(groupIndex);
			int nctarget = ctarget;
			int mx = pos[0];
			int my = pos[1];
			int[] npos = new int[2];
			npos[0] = actorObject.getPosX();
			npos[1] = actorObject.getPosY();
			int[] ds = new int[4];
			ds[0] = -1;
			ds[1] = -1;
			ds[2] = -1;
			ds[3] = -1;
			boolean flag2 = false;
			int clocation = 0;
			int cmp = 0;
			if ((SRPGAbilityFactory.getInstance(ctarget)).getGenre() == 2) {
				target = new int[1];
				target[0] = ctarget;
			}
			for (int count = 0; count < target.length; count++) {
				for (int y = 0; y < field.getHeight(); y++) {
					for (int x = 0; x < field.getWidth(); x++) {
						if (moveArea[y][x] != 0) {
							continue;
						}
						int[] res = actor.getPos();
						actor.setPos(x, y);
						int[][] positions = optimizePosition(SRPGAbilityFactory.getInstance(
								target[count]), field, actors, number, x, y,
								actorObject.getPosX(), actorObject.getPosY());
						actor.setPos(res);
						if (positions == null
								|| (SRPGAbilityFactory.getInstance(target[count])).getGenre() == 0
								&& positions[1][1] <= 0
								&& (SRPGAbilityFactory.getInstance(target[count])).getDirect() == 0) {
							continue;
						}
						int nx = positions[0][0] - x;
						int ny = positions[0][1] - y;
						if (nx < 0) {
							nx *= -1;
						}
						if (ny < 0) {
							ny *= -1;
						}
						int location = nx + ny;
						int mp = (SRPGAbilityFactory.getInstance(target[count])).getMP(actor
								.getActorStatus());
						boolean result = false;
						if (ds[0] < positions[1][0]) {
							result = true;
						} else if (ds[0] == positions[1][0]) {
							if (ds[1] < positions[1][1]) {
								result = true;
							} else if (ds[1] == positions[1][1]) {
								if (ds[2] < positions[1][2]) {
									result = true;
								} else if (ds[2] == positions[1][2]) {
									if (ds[3] < positions[1][3]) {
										result = true;
									} else if (ds[3] == positions[1][3]) {
										if (clocation < location) {
											result = true;
										} else if (clocation == location) {
											if (cmp > mp) {
												result = true;
											} else if (cmp == mp) {
												if (actorObject.getPosX() == positions[0][0]
														&& actorObject
																.getPosY() == positions[0][1]) {
													result = true;
												} else if (!flag2) {
													result = true;
												}
											}
										}
									}
								}
							}
						}
						if (!result) {
							continue;
						}
						mx = x;
						my = y;
						for (int i = 0; i < npos.length; i++) {
							npos[i] = positions[0][i];
						}
						for (int i = 0; i < ds.length; i++) {
							ds[i] = positions[1][i];
						}
						clocation = location;
						cmp = mp;
						nctarget = target[count];
						flag2 = true;
					}

				}

			}
			move_x = mx;
			move_y = my;
			route = fieldmove.moveRoute(actor.getPosX(), actor.getPosY(),
					move_x, move_y, actor.getActorStatus().move);
			if (ctarget != -1) {
				ability = nctarget;
				abi_x = npos[0];
				abi_y = npos[1];
			}
		} else {
			return false;
		}
		return true;
	}

	/**
	 * 回避战斗
	 * 
	 * @return
	 */
	private boolean escapeThinking() {
		SRPGActor actor = actors.find(number);
		int group = actor.getActorStatus().group;
		this.move_x = actor.getPosX();
		this.move_y = actor.getPosY();
		SRPGFieldMove fieldmove = SRPGFieldMove.getInstance(field.getMoveSpaceAll(actors, number));
		int[][] moveArea = fieldmove.moveArea(actor.getPosX(), actor.getPosY(),
				actor.getActorStatus().move);
		int clocation = -1;
		int movePosX = 0;
		int movePosY = 0;
		for (int y = 0; y < field.getHeight(); y++) {
			for (int x = 0; x < field.getWidth(); x++) {
				if (moveArea[y][x] != 0) {
					continue;
				}
				int index = actors.checkActor(x, y);
				if (index != -1 && index != number) {
					continue;
				}
				int location = -1;
				int mx = 0;
				int my = 0;
				for (int i = 0; i < actors.size(); i++) {
					SRPGActor actorObject = actors.find(i);
					if (!actorObject.isVisible()
							|| actorObject.getActorStatus().group == group) {
						continue;
					}
					int cx = x - actorObject.getPosX();
					int cy = y - actorObject.getPosY();
					if (cx < 0) {
						cx *= -1;
					}
					if (cy < 0) {
						cy *= -1;
					}
					if (cx + cy < location || location == -1) {
						location = cx + cy;
						mx = actorObject.getPosX();
						my = actorObject.getPosY();
					}
				}

				if (clocation < location || clocation == -1 && location != -1) {
					move_x = x;
					move_y = y;
					movePosX = mx;
					movePosY = my;
					clocation = location;
				}
			}

		}
		if (clocation != -1) {
			route = fieldmove.moveRoute(actor.getPosX(), actor.getPosY(),
					move_x, move_y, actor.getActorStatus().move);
			int[] res = actor.getPos();
			actor.setPos(move_x, move_y);
			direction = actor.findDirection(movePosX, movePosY);
			actor.setPos(res);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 不进行任何操作
	 * 
	 * @return
	 */
	private boolean nomoveThinking() {
		SRPGActor actor = actors.find(number);
		int move = actor.getActorStatus().move;
		actor.getActorStatus().move = 0;
		boolean result = wizardThinking();
		actor.getActorStatus().move = move;
		return result;
	}

	private int[][] optimizePosition(SRPGAbilityFactory ability1, SRPGField field,
			SRPGActors actors, int i, int mx, int my, int moveX, int moveY) {
		int[] pos = new int[2];
		int result[] = new int[4];
		int maxCount = 0;
		int nPosX = 0;
		int nPosY = 0;
		int optimizeAllCount = 0;
		boolean flag = false;
		boolean flag1 = false;
		pos[0] = moveX;
		pos[1] = moveY;
		boolean[][] trueRange = ability1.setTrueRange(field, mx, my);
		int[][] targetRange = ability1.setTargetRange(field, moveX, moveY);
		for (int y = 0; y < field.getHeight(); y++) {
			for (int x = 0; x < field.getWidth(); x++) {
				if (targetRange[y][x] == -1 || !trueRange[y][x]){
					continue;
				}
				if (ability1.getSelectNeed() == 0) {
					int index = actors.checkActor(x, y);
					if (index == -1
							|| !ability1.filtedActor(actors.find(number),
									actors.find(index))){
						continue;
					}
				}
				int posX = 0;
				int posY = 0;
				int count = 0;
				int optimizeAllsize = 0;
				int[][] targetRang = ability1.setTargetRange(field, x, y);
				for (int ny = 0; ny < field.getHeight(); ny++) {
					for (int nx = 0; nx < field.getWidth(); nx++) {
						if (targetRang[ny][nx] == -1){
							continue;
						}
						int index = actors.checkActor(nx, ny);
						if (index == -1
								|| !ability1.filtedActor(actors.find(i),
										actors.find(index))){
							continue;
						}
						int[] optimizeAll = ability1.getOptimizeAll(field, actors, i,
								index);
						posX += optimizeAll[0];
						optimizeAllsize += optimizeAll[1];
						if (optimizeAll[1] > 0){
							posY++;
						}
						if (ability1.getGenre() == 0
								&& actors.find(index).getActorStatus().hp - optimizeAll[0] <= 0
								&& actors.find(index).getActorStatus().hp > 0){
							count++;
						}
						flag1 = true;
					}

				}

				boolean flag2 = false;
				if (count > maxCount){
					flag2 = true;
				}
				else if (count == maxCount){
					if (posX > nPosX){
						flag2 = true;
					}
					else if (posX == nPosX){
						if (posY > nPosY){
							flag2 = true;
						}
						else if (posY == nPosY){
							if (moveX == x && moveY == y){
								flag2 = true;
							}
							else if (!flag && flag1){
								flag2 = true;
							}
						}
					}
				}
				if (flag2) {
					pos[0] = x;
					pos[1] = y;
					maxCount = count;
					nPosX = posX;
					nPosY = posY;
					optimizeAllCount = optimizeAllsize;
					flag = true;
				}
			}

		}

		if (!flag1 || !flag) {
			return null;
		} else {
			result[0] = maxCount;
			result[1] = nPosX;
			result[2] = nPosY;
			result[3] = optimizeAllCount;
			int[][] res = { pos, result };
			return res;
		}
	}

	public int getMoveX() {
		return move_x;
	}

	public int getMoveY() {
		return move_y;
	}

	public int[] getMove() {
		int res[] = new int[2];
		res[0] = move_x;
		res[1] = move_y;
		return res;
	}

	public int[][] getRoute() {
		return route;
	}

	public int getTargetX() {
		return abi_x;
	}

	public int getTargetY() {
		return abi_y;
	}

	public int[] getTarget() {
		int res[] = new int[2];
		res[0] = abi_x;
		res[1] = abi_y;
		return res;
	}

	public int getAbility() {
		return ability;
	}

	public int getDirection() {
		return direction;
	}

}
