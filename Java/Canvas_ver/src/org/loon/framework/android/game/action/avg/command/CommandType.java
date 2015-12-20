package org.loon.framework.android.game.action.avg.command;

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
public interface CommandType {

	String L_WAIT = "wait";

	String L_MES = "mes";
	
	String L_SELLEN = "selleft";
	
	String L_SELTOP = "seltop";
	
	String L_MESLEN = "meslen";

	String L_MESTOP = "mestop";
	
	String L_MESLEFT = "mesleft";
	
	String L_MESCOLOR = "mescolor";

	String L_MESSTOP = "messtop";

	String L_SELECT = "select";

	String L_SELECTS = "selects";

	String L_SHAKE = "shake";

	String L_CGWAIT = "cgwait";

	String L_SLEEP = "sleep";

	String L_FLASH = "flash";

	String L_GB = "gb";

	String L_CG = "cg";

	String L_PLAY = "play";

	String L_PLAYLOOP = "playloop";

	String L_PLAYSTOP = "playstop";

	String L_FADEOUT = "fadeout";

	String L_FADEIN = "fadein";

	String L_DEL = "del";

	String L_SNOW = "snow";

	String L_RAIN = "rain";

	String L_PETAL = "petal";

	String L_SNOWSTOP = "snowstop";

	String L_RAINSTOP = "rainstop";

	String L_PETALSTOP = "petalstop";

	String L_TO = "to";

	String L_EXIT = "exit";
}
