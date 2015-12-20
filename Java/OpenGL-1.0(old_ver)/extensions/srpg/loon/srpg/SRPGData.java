package loon.srpg;

import loon.srpg.actor.SRPGActor;
import loon.srpg.actor.SRPGActors;
import loon.srpg.actor.SRPGPosition;
import loon.srpg.actor.SRPGStatus;
import loon.utils.collection.ArrayByte;




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
// SRPG模块专用的角色数据记录与读取器
public class SRPGData {

	private SRPGActor[] actorArray;

	private SRPGStatus[] statusArray;

	private static SRPGData instance;

	private int size;

	public final static SRPGData getInstnace() {
		if (instance == null) {
			instance = new SRPGData();
		}
		return instance;
	}

	SRPGData() {
	}

	/**
	 * 初始化游戏角色数据,以供存储器调用
	 * 
	 * @param saveName
	 * @param actors
	 */
	public void initActors(SRPGActors actors) {
		this.size = actors.size();
		this.actorArray = actors.getActors();
	}

	/**
	 * 保存游戏角色所在位置
	 * 
	 * @return
	 * @throws Exception
	 */
	public byte[] savePosition() throws Exception {
		ArrayByte bytes = new ArrayByte();
		try {
			bytes.writeInt(actorArray.length);
			for (int i = 0; i < actorArray.length; i++) {
				SRPGActor actor = actorArray[i];
				if (actor != null) {
					bytes.writeInt(i);
					bytes.writeInt(actor.getDirection());
					bytes.writeInt(actor.getPosX());
					bytes.writeInt(actor.getPosY());
				}
			}
		} catch (Exception ex) {
			throw new Exception("An exception when the save srpg game data : "
					+ ex.getMessage());
		}
		return bytes.getData();
	}

	/**
	 * 读取游戏角色所在位置
	 * 
	 * @return
	 * @throws Exception
	 */
	public SRPGPosition[] loadPosition(byte[] res) throws Exception {
		ArrayByte bytes = new ArrayByte(res);
		SRPGPosition[] positions = null;
		try {
			int size = bytes.readInt();
			positions = new SRPGPosition[size];
			for (int i = 0; i < size; i++) {
				positions[i] = new SRPGPosition();
				int id = bytes.readInt();
				int d = bytes.readInt();
				int posX = bytes.readInt();
				int posY = bytes.readInt();
				positions[i].number = id;
				positions[i].setPos(posX, posY);
				positions[i].vector = d;
			}

		} catch (Exception ex) {
			throw new Exception(
					"An exception when the loading srpg game data : "
							+ ex.getMessage());
		}
		return positions;
	}

	/**
	 * 保存游戏角色状态
	 * 
	 * @return
	 * @throws Exception
	 */
	public byte[] saveStatus() throws Exception {

		ArrayByte bytes = new ArrayByte();

		try {

			checkInitStatus();

			bytes.writeInt(statusArray.length);

			for (int i = 0; i < statusArray.length; i++) {

				SRPGStatus status = statusArray[i];
				if (status != null) {
					bytes.writeUTF(status.name);
					bytes.writeUTF(status.jobname);
					bytes.writeInt(status.number);
					bytes.writeInt(status.level);
					bytes.writeInt(status.exp);
					bytes.writeInt(status.hp);
					bytes.writeInt(status.max_hp);
					bytes.writeInt(status.max_mp);
					bytes.writeInt(status.strength);
					bytes.writeInt(status.vitality);
					bytes.writeInt(status.agility);
					bytes.writeInt(status.magic);
					bytes.writeInt(status.resume);
					bytes.writeInt(status.mind);
					bytes.writeInt(status.sp);
					bytes.writeInt(status.dexterity);
					bytes.writeInt(status.regeneration);
					bytes.writeInt(status.move);
					bytes.writeInt(status.movetype);
					bytes.writeInt(status.team);
					bytes.writeInt(status.group);
					bytes.writeInt(status.action);
					bytes.writeInt(status.leader);
					bytes.writeBoolean(status.isComputer);

					if (status.status != null) {
						bytes.writeInt(status.status.length);
						for (int j = 0; j < status.status.length; j++) {
							bytes.writeInt(status.status[j]);
						}
					} else {
						bytes.writeInt(0);
					}

					if (status.substatus != null) {
						bytes.writeInt(status.substatus.length);
						for (int j = 0; j < status.substatus.length; j++) {
							bytes.writeInt(status.substatus[j]);
						}
					} else {
						bytes.writeInt(0);
					}

					if (status.ability != null) {
						bytes.writeInt(status.ability.length);
						for (int j = 0; j < status.ability.length; j++) {
							bytes.writeInt(status.ability[j]);
						}
					} else {
						bytes.writeInt(0);
					}

					if (status.guardelement != null) {
						bytes.writeInt(status.guardelement.length);
						for (int j = 0; j < status.guardelement.length; j++) {
							bytes.writeInt(status.guardelement[j]);
						}
					} else {
						bytes.writeInt(0);
					}

					if (status.skill != null) {
						bytes.writeInt(status.skill.length);
						for (int j = 0; j < status.skill.length; j++) {
							bytes.writeInt(status.skill[j]);
						}
					} else {
						bytes.writeInt(0);
					}

					if (status.computer != null) {
						bytes.writeInt(status.computer.length);
						for (int j = 0; j < status.computer.length; j++) {
							bytes.writeInt(status.computer[j]);
						}
					} else {
						bytes.writeInt(0);
					}

				}

			}
		} catch (Exception ex) {
			throw new Exception("An exception when the save srpg game data : "
					+ ex.getMessage());
		}
		return bytes.getData();
	}

	/**
	 * 读取游戏角色状态
	 * 
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public boolean loadStatus(byte[] res) throws Exception {

		ArrayByte bytes = new ArrayByte(res);

		try {
			int size = bytes.readInt();
			if (statusArray != null) {
				if (size != statusArray.length) {
					statusArray = new SRPGStatus[size];
				}
			} else {
				statusArray = new SRPGStatus[size];
			}

			for (int i = 0; i < statusArray.length; i++) {

				SRPGStatus status = statusArray[i];
				if (status != null) {
					status.name = bytes.readUTF();
					status.jobname = bytes.readUTF();
					status.number = bytes.readInt();
					status.level = bytes.readInt();
					status.exp = bytes.readInt();
					status.hp = bytes.readInt();
					status.max_hp = bytes.readInt();
					status.max_mp = bytes.readInt();
					status.strength = bytes.readInt();
					status.vitality = bytes.readInt();
					status.agility = bytes.readInt();
					status.magic = bytes.readInt();
					status.resume = bytes.readInt();
					status.mind = bytes.readInt();
					status.sp = bytes.readInt();
					status.dexterity = bytes.readInt();
					status.regeneration = bytes.readInt();
					status.move = bytes.readInt();
					status.movetype = bytes.readInt();
					status.team = bytes.readInt();
					status.group = bytes.readInt();
					status.action = bytes.readInt();
					status.leader = bytes.readInt();
					status.isComputer = bytes.readBoolean();

					int count = bytes.readInt();
					if (count == 0) {
						status.status = null;
					} else {
						status.status = new int[count];
						for (int j = 0; j < count; j++) {
							status.status[j] = bytes.readInt();
						}
					}

					count = bytes.readInt();
					if (count == 0) {
						status.substatus = null;
					} else {
						status.substatus = new int[count];
						for (int j = 0; j < count; j++) {
							status.substatus[j] = bytes.readInt();
						}
					}

					count = bytes.readInt();
					if (count == 0) {
						status.ability = null;
					} else {
						status.ability = new int[count];
						for (int j = 0; j < count; j++) {
							status.ability[j] = bytes.readInt();
						}
					}

					count = bytes.readInt();
					if (count == 0) {
						status.guardelement = null;
					} else {
						status.guardelement = new int[count];
						for (int j = 0; j < count; j++) {
							status.guardelement[j] = bytes.readInt();
						}
					}

					count = bytes.readInt();
					if (count == 0) {
						status.skill = null;
					} else {
						status.skill = new int[count];
						for (int j = 0; j < count; j++) {
							status.skill[j] = bytes.readInt();
						}
					}

					count = bytes.readInt();
					if (count == 0) {
						status.computer = null;
					} else {
						status.computer = new int[count];
						for (int j = 0; j < count; j++) {
							status.computer[j] = bytes.readInt();
						}
					}

				}

			}
		} catch (Exception ex) {
			throw new Exception(
					"An exception when loading the srpg game data : "
							+ ex.getMessage());
		}
		return true;
	}

	/**
	 * 设定特定ID的角色状态
	 * 
	 * @param i
	 * @param status1
	 */
	public void setStatus(int i, SRPGStatus status1) {
		checkInitStatus();
		if (i < statusArray.length) {
			statusArray[i] = status1;
		}
	}

	public void setStatus(int index, SRPGActor actor) {
		setStatus(index, actor.getActorStatus());
	}

	/**
	 * 返回特定ID的角色状态
	 * 
	 * @param i
	 * @return
	 */
	public SRPGStatus getStatus(int i) {
		checkInitStatus();
		if (i < statusArray.length) {
			return statusArray[i];
		} else {
			return null;
		}
	}

	/**
	 * 返回一个特定ID所对应的角色状态拷贝
	 * 
	 * @param i
	 * @return
	 */
	public SRPGStatus getCopyStatus(int i) {
		checkInitStatus();
		if (i < statusArray.length && statusArray[i] != null) {
			return new SRPGStatus(statusArray[i]);
		} else {
			return null;
		}
	}

	public int getMatchStatusIndex(int i) {
		checkInitStatus();
		for (int j = 0; j < statusArray.length; j++) {
			if (statusArray[j].number == i) {
				return j;
			}
		}
		return -1;
	}

	public SRPGStatus getMatchStatus(int i) {
		int j = getMatchStatusIndex(i);
		if (j != -1) {
			return statusArray[j];
		} else {
			return null;
		}
	}

	public void setDefaultStatus(int i, SRPGStatus status1) {
		SRPGStatus status2 = null;
		if (status1 != null) {
			status2 = new SRPGStatus(status1);
			status2.defaultStatus();
			status2.team = 0;
			status2.group = 0;
		}
		setStatus(i, status2);
	}

	public void allDefaultStatus() {
		checkInitStatus();
		for (int i = 0; i < statusArray.length; i++)
			if (statusArray[i] != null) {
				SRPGStatus status1 = new SRPGStatus(statusArray[i]);
				status1.defaultStatus();
				status1.team = 0;
				status1.group = 0;
				setStatus(i, status1);
			}
	}

	private void checkInitStatus() {
		if (statusArray == null) {
			statusArray = new SRPGStatus[size];
			for (int i = 0; i < size; i++) {
				if (actorArray[i] != null) {
					statusArray[i] = actorArray[i].getActorStatus();
				}
			}
		}
	}

	public int size() {
		return size;
	}
}
