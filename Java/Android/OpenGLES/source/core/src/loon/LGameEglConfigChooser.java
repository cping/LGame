/**
 * Copyright 2008 - 2012
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
 * @version 0.3.3
 */
package loon;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

import android.opengl.GLSurfaceView.EGLConfigChooser;

public class LGameEglConfigChooser implements EGLConfigChooser {

	public static final int EGL_COVERAGE_BUFFERS_NV = 0x30E0;

	public static final int EGL_COVERAGE_SAMPLES_NV = 0x30E1;

	protected int m_RedSize;
	protected int m_GreenSize;
	protected int m_BlueSize;
	protected int m_AlphaSize;
	protected int m_DepthSize;
	protected int m_StencilSize;
	protected int m_NumSamples;

	protected final int[] m_ConfigAttribs;
	private int[] m_Value = new int[1];

	public LGameEglConfigChooser(int r, int g, int b, int a, int depth,
			int stencil, int numSamples) {
		m_RedSize = r;
		m_GreenSize = g;
		m_BlueSize = b;
		m_AlphaSize = a;
		m_DepthSize = depth;
		m_StencilSize = stencil;
		m_NumSamples = numSamples;
		m_ConfigAttribs = new int[] { EGL10.EGL_RED_SIZE, 4,
				EGL10.EGL_GREEN_SIZE, 4, EGL10.EGL_BLUE_SIZE, 4, EGL10.EGL_NONE };
	}

	@Override
	public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
		int[] num_config = new int[1];
		egl.eglChooseConfig(display, m_ConfigAttribs, null, 0, num_config);
		int numConfigs = num_config[0];

		if (numConfigs <= 0) {
			throw new IllegalArgumentException("No configs match configSpec");
		}

		EGLConfig[] configs = new EGLConfig[numConfigs];
		egl.eglChooseConfig(display, m_ConfigAttribs, configs, numConfigs,
				num_config);

		EGLConfig config = chooseConfig(egl, display, configs);

		return config;
	}

	public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display,
			EGLConfig[] configs) {
		EGLConfig best = null;
		EGLConfig bestAA = null;

		for (EGLConfig config : configs) {
			int d = findConfigAttrib(egl, display, config,
					EGL10.EGL_DEPTH_SIZE, 0);
			int s = findConfigAttrib(egl, display, config,
					EGL10.EGL_STENCIL_SIZE, 0);

			if (d < m_DepthSize || s < m_StencilSize) {
				continue;
			}

			int r = findConfigAttrib(egl, display, config, EGL10.EGL_RED_SIZE,
					0);
			int g = findConfigAttrib(egl, display, config,
					EGL10.EGL_GREEN_SIZE, 0);
			int b = findConfigAttrib(egl, display, config, EGL10.EGL_BLUE_SIZE,
					0);
			int a = findConfigAttrib(egl, display, config,
					EGL10.EGL_ALPHA_SIZE, 0);

			if (best == null && r == m_RedSize && g == m_GreenSize
					&& b == m_BlueSize && a == m_AlphaSize) {
				best = config;
				if (m_NumSamples == 0) {
					break;
				}
			}

			int hasSampleBuffers = findConfigAttrib(egl, display, config,
					EGL10.EGL_SAMPLE_BUFFERS, 0);
			int numSamples = findConfigAttrib(egl, display, config,
					EGL10.EGL_SAMPLES, 0);

			if (bestAA == null && hasSampleBuffers == 1
					&& numSamples >= m_NumSamples && r == m_RedSize
					&& g == m_GreenSize && b == m_BlueSize && a == m_AlphaSize) {
				bestAA = config;
				continue;
			}

			hasSampleBuffers = findConfigAttrib(egl, display, config,
					EGL_COVERAGE_BUFFERS_NV, 0);
			numSamples = findConfigAttrib(egl, display, config,
					EGL_COVERAGE_SAMPLES_NV, 0);

			if (bestAA == null && hasSampleBuffers == 1
					&& numSamples >= m_NumSamples && r == m_RedSize
					&& g == m_GreenSize && b == m_BlueSize && a == m_AlphaSize) {
				bestAA = config;
				continue;
			}
		}

		if (bestAA != null)
			return bestAA;
		else
			return best;
	}

	private int findConfigAttrib(EGL10 egl, EGLDisplay display,
			EGLConfig config, int attribute, int defaultValue) {
		if (egl.eglGetConfigAttrib(display, config, attribute, m_Value)) {
			return m_Value[0];
		}
		return defaultValue;
	}

}
