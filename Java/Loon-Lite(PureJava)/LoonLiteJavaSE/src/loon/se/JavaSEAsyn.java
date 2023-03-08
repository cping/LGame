package loon.se;

import java.util.concurrent.ExecutorService;

import loon.Asyn;
import loon.Log;
import loon.Platform;
import loon.utils.reply.Act;

public class JavaSEAsyn extends Asyn.Default {

	private ExecutorService pool;
	
	public JavaSEAsyn(ExecutorService p,Log log, Act<? extends Object> frame) {
		super(log, frame);
		
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
