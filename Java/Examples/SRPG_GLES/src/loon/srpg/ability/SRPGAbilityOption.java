package loon.srpg.ability;

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
public class SRPGAbilityOption {

	public boolean counter;

	public boolean extinctmp;

	public int attack_value;

	public int[] warp_pos;

	public boolean warp;

	private static SRPGAbilityOption instance;

	public static SRPGAbilityOption getInstance(boolean flag) {
		if (instance == null) {
			instance = new SRPGAbilityOption(flag);
		} else {
			instance.set(flag);
		}
		return instance;
	}

	public static SRPGAbilityOption getInstance() {
		return getInstance(true);
	}

	public SRPGAbilityOption(boolean flag) {
		this.set(flag);
	}

	public void set(boolean flag) {
		this.counter = flag;
		this.extinctmp = true;
		this.attack_value = 0;
		this.warp = false;
		this.warp_pos = null;
	}

	public void setWarpPos(int x, int y) {
		this.warp_pos = new int[] { x, y };
	}

}
