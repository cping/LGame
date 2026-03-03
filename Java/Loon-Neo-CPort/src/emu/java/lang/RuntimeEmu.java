/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
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
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.5
 */
package emu.java.lang;

import java.lang.Thread;

import org.teavm.runtime.GC;

import loon.cport.make.Emulate;

@Emulate(java.lang.Runtime.class)
public class RuntimeEmu {

	private final static long defaultMinMemory = 256 * 1024 * 1024;

	private final static RuntimeEmu currentRuntime = new RuntimeEmu();

	public static RuntimeEmu getRuntime() {
		return currentRuntime;
	}

	private RuntimeEmu() {
	}

	public void exit(int status) {
	}

	public void addShutdownHook(Thread hook) {

	}

	public void halt(int status) {

	}

	public int availableProcessors() {
		return 1;
	}

	public long freeMemory() {
		return GC.getFreeMemory();
	}

	public long totalMemory() {
		return GC.availableBytes();
	}

	public long maxMemory() {
		return Math.min(defaultMinMemory, GC.availableBytes());
	}

	public void gc() {
		System.gc();
	}
}
