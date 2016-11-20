package loon.lwjgl;

import java.util.concurrent.ExecutorService;

import loon.Asyn;
import loon.Log;
import loon.utils.reply.Act;

public class Lwjgl3Asyn extends Asyn.Default {

	private ExecutorService pool;

	public Lwjgl3Asyn(ExecutorService p, Log log, Act<? extends Object> frame) {
		super(log, frame);
		this.pool = p;
	}

	@Override
	public boolean isAsyncSupported() {
		return true;
	}

	@Override
	public void invokeAsync(Runnable action) {
		pool.execute(action);
	}
}
