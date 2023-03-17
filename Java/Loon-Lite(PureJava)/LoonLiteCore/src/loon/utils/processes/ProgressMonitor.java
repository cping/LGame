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
package loon.utils.processes;

import loon.utils.MathUtils;
import loon.utils.ObjectMap;
import loon.utils.TArray;

public class ProgressMonitor implements ProgressListener {

	private final TArray<ProgressListener> _progressListeners = new TArray<>();
	private final ObjectMap<ProgressMonitor, ProgressListener> _childProgressMonitorToProgressListenerMap = new ObjectMap<>();

	public ProgressMonitor() {
		this(null);
	}

	public ProgressMonitor(final ProgressListener p) {
		if (p != null) {
			this._progressListeners.add(p);
		}
	}

	@Override
	public void onProgressChanged(final int p) {
		final int progressListenerCount = this._progressListeners.size;
		for (int i = 0; i < progressListenerCount; i++) {
			this._progressListeners.get(i).onProgressChanged(p);
		}
	}

	public void addChildProgressMonitor(final ProgressMonitor childProgressMonitor, final int rangeFrom,
			final int rangeTo) {
		final ProgressListener childProgressMonitorListener = new ProgressListener() {
			@Override
			public void onProgressChanged(final int pss) {
				final int progress = MathUtils.mix(rangeFrom, rangeTo, (float) pss / ProgressListener.PROGRESS_MAX);
				ProgressMonitor.this.onProgressChanged(progress);
			}
		};
		childProgressMonitor.addProgressListener(childProgressMonitorListener);
		this._childProgressMonitorToProgressListenerMap.put(childProgressMonitor, childProgressMonitorListener);
	}

	public void unChildProgressMonitor(final ProgressMonitor childProgressMonitor) {
		childProgressMonitor
				.removeProgressListener(this._childProgressMonitorToProgressListenerMap.get(childProgressMonitor));
	}

	private void addProgressListener(final ProgressListener p) {
		this._progressListeners.add(p);
	}

	private void removeProgressListener(final ProgressListener p) {
		this._progressListeners.add(p);
	}

}
