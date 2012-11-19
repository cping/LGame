package loon.net;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Observable;

import loon.core.LRelease;
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
public class HttpDownload extends Observable implements LRelease, Runnable {

	private static final int MAX_BUFFER_SIZE = 2048;

	public static final String STATUSES[] = { "Downloading", "Paused",
			"Complete", "Cancelled", "Error" };

	public static final int DOWNLOADING = 0;

	public static final int PAUSED = 1;

	public static final int COMPLETE = 2;

	public static final int CANCELLED = 3;

	public static final int ERROR = 4;

	public static interface HttpDownloadListener {

		public void downloading(float progress);

		public void error(Exception ex);

		public void completed();

		public void paused();

		public void cancel();

	}

	private boolean daemon;

	private float size;

	private float downloaded;

	private int status, priority;

	private ByteArrayOutputStream out;

	private HttpClientAbstract client;

	private HttpDownloadListener listener;

	private Exception exception;

	HttpDownload(HttpClientAbstract client) {
		this.client = client;
		this.daemon = false;
		this.out = new ByteArrayOutputStream(MAX_BUFFER_SIZE);
		this.size = -1;
		this.downloaded = 0;
		this.priority = Thread.NORM_PRIORITY;
		this.status = DOWNLOADING;
	}

	public URL getURL() {
		return client.getURL();
	}

	public String getURLString() {
		return client.getURLString();
	}

	public synchronized ArrayByte getArrayByte() {
		return new ArrayByte(out.toByteArray());
	}

	public synchronized byte[] getBytes() {
		return out.toByteArray();
	}

	public synchronized OutputStream getOutputStream() {
		return out;
	}

	public synchronized boolean save(File file) {
		boolean result = false;
		if (status != COMPLETE) {
			return result;
		}
		try {
			if (!file.exists()) {
				result = file.createNewFile();
			}
			FileOutputStream output = new FileOutputStream(file);
			output.write(out.toByteArray());
			output.close();
			result = true;
		} catch (Exception e) {
			result = false;
		}
		return result;
	}

	public boolean save(String fileName) {
		return save(new File(fileName));
	}

	public float getSize() {
		return size;
	}

	public float getProgressValue() {
		return downloaded / size;
	}

	public float getProgress() {
		return (downloaded / size) * 100;
	}

	public int getStatus() {
		return status;
	}

	public void pause() {
		status = PAUSED;
		stateChanged();
	}

	public void resume() {
		status = DOWNLOADING;
		stateChanged();
		start();
	}

	public void cancel() {
		status = CANCELLED;
		stateChanged();
	}

	private void error() {
		status = ERROR;
		stateChanged();
	}

	public void reset() {
		out.reset();
		size = 0;
		downloaded = 0;
		status = CANCELLED;
		stateChanged();
	}

	public void stop() {
		reset();
		client.stop();
	}

	public void start() {
		Thread thread = new Thread(this);
		thread.setPriority(priority);
		thread.setDaemon(daemon);
		thread.start();
	}

	public void run() {
		if (client == null) {
			return;
		}
		if (!client.isRunning) {
			client.start();
		}
		try {
			if (client.connection.getResponseCode() / 100 != 2) {
				error();
			}
			int contentLength = client.connection.getContentLength();
			if (contentLength < 1) {
				error();
			}
			if (size == -1) {
				size = contentLength;
				stateChanged();
			}
			InputStream in = client.getInputStream();
			if (in == null) {
				in = client.connection.getInputStream();
			}
			if (in == null) {
				if (!client.isRunning) {
					client.start();
				}
				in = client.getConnection().getInputStream();
			}
			if (in == null) {
				error();
			}
			byte[] buffer = new byte[MAX_BUFFER_SIZE];
			for (; status == DOWNLOADING;) {
				int read = in.read(buffer);
				if (read == -1) {
					break;
				}
				out.write(buffer, 0, read);
				downloaded += read;
				stateChanged();
			}
			if (status == DOWNLOADING) {
				status = COMPLETE;
				stateChanged();
			}
		} catch (Exception e) {
			this.error();
			this.exception = e;
		} finally {
			client.stop();
		}
	}

	public boolean isDaemon() {
		return daemon;
	}

	public void setDaemon(boolean daemon) {
		this.daemon = daemon;
	}

	public synchronized HttpDownloadListener getListener() {
		return listener;
	}

	public synchronized void setListener(HttpDownloadListener listener) {
		this.listener = listener;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	private synchronized void stateChanged() {
		if (listener != null) {
			switch (status) {
			case DOWNLOADING:
				listener.downloading(getProgress());
				break;
			case PAUSED:
				listener.paused();
				break;
			case COMPLETE:
				listener.completed();
				break;
			case CANCELLED:
				listener.cancel();
				break;
			case ERROR:
				if (exception == null) {
					exception = new Exception("404 Error !");
				}
				listener.error(exception);
				break;
			default:
				break;
			}
		}
		setChanged();
		notifyObservers();
	}

	public synchronized void dispose() {
		stop();
		if (out != null) {
			try {
				out.close();
				out = null;
			} catch (IOException e) {
			}
		}
	}

}
