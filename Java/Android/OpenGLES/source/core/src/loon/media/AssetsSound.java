package loon.media;

import loon.core.LSystem;
import loon.utils.StringUtils;


import android.content.Context;

/**
 * Copyright 2008 - 2010
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
 * @email javachenpeng@yahoo.com
 * @version 0.1.5
 */
public class AssetsSound implements Runnable,
		android.media.MediaPlayer.OnCompletionListener,
		android.media.MediaPlayer.OnErrorListener,
		android.media.MediaPlayer.OnBufferingUpdateListener {

	public static final int PREPARED = 0;

	public static final int PLAYING = 1;

	public static final int PAUSE = 2;

	public static final int EXIT = 3;

	private Object lock;

	private boolean loop;

	private int buffer;

	private boolean done;

	private boolean started;

	private boolean paused;

	private int status = 0;

	private android.media.MediaPlayer player;

	private String fileName;

	private Context context;

	private Thread soundThread;

	private final String assets = "assets";

	public AssetsSound(String file) {
		this.lock = new Object();
		if (file.startsWith(assets)) {
			this.fileName = StringUtils.replace(file, assets, "");
		} else if (file.startsWith(LSystem.FS + assets)) {
			this.fileName = StringUtils
					.replace(file, LSystem.FS + assets, "");
		} else {
			this.fileName = file;
		}
		if (fileName.startsWith("/")) {
			fileName = fileName.replace("/", "");
		} 
		this.context = LSystem.screenActivity;
	}

	public void run() {
		synchronized (this.lock) {
			if (player == null) {
				player = new android.media.MediaPlayer();
			}
		}
		try {
			player.setOnCompletionListener(this);
			player.setOnErrorListener(this);
			player.setOnBufferingUpdateListener(this);

			synchronized (this.lock) {
				while (fileName == null
						&& (this.status == PREPARED || this.status == PLAYING)) {
					this.lock.wait(250);
				}
			}
			if (fileName == null) {
				synchronized (this.lock) {
					this.status = EXIT;
				}
			} else {
				setDataSource(fileName, loop);

			}
			synchronized (this.lock) {
				while (this.status == PREPARED) {
					this.lock.wait();
				}
			}

			int currentStatus = 0;
			synchronized (this.lock) {
				currentStatus = this.status;
			}

			if (currentStatus == PLAYING) {
				player.start();
			}

			synchronized (this.lock) {
				this.started = true;

				while (this.status == PLAYING) {
					if (!this.done) {
						int duration = player.getDuration();
						int position = player.getCurrentPosition();
						if (duration > 0 && position + 10000 > duration) {
							this.done = true;
							this.schedule(duration, position);
						}
					}
					this.lock.wait(3000);

				}
				this.started = false;
			}

			player.stop();
			player.release();
			synchronized (this.lock) {
				player = null;
			}

		} catch (Exception e) {
		}
		synchronized (this.lock) {
			this.status = EXIT;
		}
		callback();

	}

	/**
	 * 音乐播放进度，重载此函数可获得音乐的播放流程
	 * 
	 * @param duration
	 * @param position
	 */
	public void schedule(int duration, int position) {

	}

	/**
	 * 回调函数，当音乐播放完毕后将调用此函数。
	 */
	public void callback() {

	}

	/**
	 * 播放音乐
	 */
	public void play() {
		try {
			synchronized (this.lock) {
				stopLoop();
				if (status != PLAYING) {
					this.loop = false;
					this.paused = false;
					this.status = PLAYING;
					soundThread = new Thread(this);
					soundThread.start();
					this.lock.notifyAll();
				}
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 循环播放音乐
	 */
	public void loop() {
		try {
			synchronized (this.lock) {
				stopLoop();
				if (status != PLAYING) {
					this.loop = true;
					this.paused = false;
					this.status = PLAYING;
					soundThread = new Thread(this);
					soundThread.start();
					this.lock.notifyAll();
				}
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 播放指定音乐
	 */
	public void play(String file) {
		try {
			synchronized (this.lock) {
				stopLoop();
				if (status != PLAYING) {
					this.loop = false;
					this.paused = false;
					this.status = PLAYING;
					this.fileName = file;
					soundThread = new Thread(this);
					soundThread.start();
					this.lock.notifyAll();
				}
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 停止线程用循环
	 */
	private void stopLoop() {
		if (loop) {
			if (player != null) {
				status = EXIT;
				player.stop();
				player.release();
				player = null;
			}
		}
	}

	/**
	 * 暂停音乐播放
	 */
	public void pause() {
		synchronized (this.lock) {
			if (this.player != null) {
				if (this.paused) {
					this.player.start();
				} else {
					this.player.pause();
				}
				this.paused = true;
				this.lock.notifyAll();
			}
		}
	}

	/**
	 * 刷新音乐播放
	 */
	public void reset() {
		this.stop();
	}

	/**
	 * 释放音乐播放所占据的空间
	 */
	public void release() {
		synchronized (this.lock) {
			if (this.player != null) {
				this.loop = false;
				this.status = EXIT;
				this.player.release();
				this.lock.notifyAll();
			}
		}
	}

	/**
	 * 停止音乐播放
	 */
	public void stop() {
		synchronized (this.lock) {
			this.loop = false;
			this.status = EXIT;
			this.lock.notifyAll();
		}
	}

	/**
	 * 设定音乐源
	 * 
	 * @param file
	 */
	public void setDataSource(String file) {
		setDataSource(file, false);
	}

	/**
	 * 设定音乐播放源，并设置是否循环播放
	 * 
	 * @param file
	 * @param looping
	 */
	public void setDataSource(String file, boolean looping) {
		synchronized (this.lock) {
			try {
				player.setDataSource(context.getAssets().openFd(file)
						.getFileDescriptor(), context.getAssets().openFd(file)
						.getStartOffset(), context.getAssets().openFd(file)
						.getLength());
				player.prepare();
				player.setLooping(looping);
			} catch (Exception e) {

			}
			lock.notifyAll();
		}
	}

	private void exit() {
		synchronized (this.lock) {
			this.loop = false;
			this.status = EXIT;
			this.lock.notifyAll();
		}
		try {
			soundThread.join();
		} catch (InterruptedException e) {
		}
	}

	public void setLooping(boolean isLooping) {
		synchronized (this.lock) {
			if (this.player != null && this.started) {
				loop = isLooping;
				player.setLooping(isLooping);
				lock.notifyAll();
			}
		}
	}

	public boolean isLooping() {
		synchronized (this.lock) {
			if (this.player != null && this.started) {
				return player.isLooping();
			}
			return false;
		}
	}

	public boolean isPlaying() {
		synchronized (this.lock) {
			if (this.player != null && this.started) {
				return player.isPlaying();
			}
			return false;
		}
	}

	public void setVolume(int vol) {
		synchronized (this.lock) {
			if (this.player != null && this.started) {
				player.setVolume((float) Math.log10(vol), (float) Math
						.log10(vol));
				lock.notifyAll();
			}

		}
	}

	public int getPosition() {
		synchronized (this.lock) {
			if (this.player != null && this.started) {
				return this.player.getCurrentPosition();
			}
		}
		return 0;
	}

	public int getDuration() {
		synchronized (this.lock) {
			if (this.player != null && this.started) {
				return this.player.getDuration();
			}
		}
		return 0;
	}

	protected void finalize() throws Throwable {
		this.exit();
		super.finalize();
	}

	public int getBuffer() {
		synchronized (this.lock) {
			return this.buffer;
		}
	}

	public String getName() {
		return fileName;
	}

	public void onCompletion(android.media.MediaPlayer mp) {
		synchronized (this.lock) {
			this.status = EXIT;
			this.lock.notifyAll();
		}
	}

	public boolean onError(android.media.MediaPlayer mp, int what, int extra) {
		synchronized (this.lock) {
			this.status = EXIT;
			this.lock.notifyAll();
		}
		return false;
	}

	public void onBufferingUpdate(android.media.MediaPlayer mp, int percent) {
		synchronized (this.lock) {
			this.buffer = percent;
		}
	}

}
