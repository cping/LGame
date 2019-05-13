/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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
package loon;

import loon.event.InputMake;
import loon.event.InputMakeImpl;
import loon.utils.ObjectMap;
import loon.utils.TimeUtils;
import loon.utils.reply.Act;

public class EmptyGame extends LGame {

	public EmptyGame(LSetting config, Platform plat) {
		super(config, plat);
	}

	// 为了方便直接转码到C#和C++，无法使用匿名内部类(也就是在构造内直接构造实现的方式)，只能都写出类来.
	// PS:别提delegate，委托那玩意写出来太不优雅了，而且大多数J2C#的工具也不能直接转换过去……
	private static class SaveEmpty implements Save {

		private final ObjectMap<String, String> _data = new ObjectMap<String, String>();

		@Override
		public void setItem(String key, String data) throws RuntimeException {
			_data.put(key, data);
		}

		@Override
		public void removeItem(String key) {
			_data.remove(key);
		}

		@Override
		public String getItem(String key) {
			return _data.get(key);
		}

		@Override
		public Batch startBatch() {
			return new SaveBatchImpl(this);
		}

		@Override
		public Iterable<String> keys() {
			return _data.keys();
		}

		@Override
		public boolean isPersisted() {
			return true;
		}

	}

	private Save save = new SaveEmpty();

	private InputMake input = new InputMakeImpl();

	private static class LogEmpty extends Log {

		@Override
		public void onError(Throwable e) {

		}

		@Override
		protected void callNativeLog(Level level, String msg, Throwable e) {
			System.err.println(level.levelString + msg);
			if (e != null) {
				e.printStackTrace(System.err);
			}

		}

	}

	private Log log = new LogEmpty();

	private static class AsynEmpty extends Asyn.Default {

		public AsynEmpty(Log log, Act<? extends Object> frame) {
			super(log, frame);
		}

		@Override
		public void invokeLater(Runnable action) {
			action.run();
		}
	}

	private Asyn exec = new AsynEmpty(log, frame);

	@Override
	public Support support() {
		throw new UnsupportedOperationException();
	}

	private final long start = TimeUtils.millis();

	@Override
	public LGame.Type type() {
		return LGame.Type.STUB;
	}

	@Override
	public double time() {
		return (double) TimeUtils.millis();
	}

	@Override
	public int tick() {
		return (int) (TimeUtils.millis() - start);
	}

	@Override
	public void openURL(String url) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Assets assets() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Graphics graphics() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Asyn asyn() {
		return exec;
	}

	@Override
	public InputMake input() {
		return input;
	}

	@Override
	public Log log() {
		return log;
	}

	@Override
	public Save save() {
		return save;
	}
	
	Accelerometer accelerometer = new AccelerometerDefault();

	@Override
	public Accelerometer accel() {
		return accelerometer;
	}

	@Override
	public boolean isMobile() {
		return false;
	}

	@Override
	public boolean isDesktop() {
		return false;
	}

}
