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
package loon.opengl;

import loon.LRelease;

/**
 * A batch manages the delivery of groups of drawing calls to the GPU. It is
 * usually a combination of a {@link GLProgram} and one or more buffers.
 */
public abstract class GLBase implements LRelease {

	public boolean begun; 
	
	public abstract void init();
	
	public abstract void freeBuffer();
	
	public boolean running(){
		return begun;
	}

	public void begin(float fbufWidth, float fbufHeight, boolean flip) {
		if (begun) {
			throw new IllegalStateException(getClass().getSimpleName()
					+ " mismatched begin()");
		}
		begun = true;
	}

	/**
	 * Sends any accumulated drawing calls to the GPU. Depending on the nature
	 * of the batch, this may be necessary before certain state changes (like
	 * switching to a new texture). This should be a NOOP if there's nothing to
	 * flush.
	 */
	public void flush() {
		if (!begun) {
			throw new IllegalStateException(getClass().getSimpleName()
					+ " flush() without begin()");
		}
	}

	/**
	 * Must be called when one is done using this batch to accumulate and send
	 * drawing commands. The default implementation calls {@link #flush} and
	 * marks this batch as inactive.
	 */
	public void end() {
		if (!begun){
			throw new IllegalStateException(getClass().getSimpleName()
					+ " mismatched end()");
		}
		try {
			flush();
		}catch(Exception ex){
			ex.printStackTrace();
		}	finally {
		
			begun = false;
		}
	}

	/**
	 * Releases any GPU resources retained by this batch. This should be called
	 * when the batch will never again be used.
	 */
	@Override
	public void close() {
		if (begun){
			throw new IllegalStateException(getClass().getSimpleName()
					+ " close() without end()");
		}
	}
}
