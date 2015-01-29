package loon;

import loon.core.graphics.opengl.GL20;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.webgl.client.WebGLContextAttributes;
import com.google.gwt.webgl.client.WebGLRenderingContext;

public class LGame implements EntryPoint {

	private Panel root = null;

	private TextArea log = null;

	private GwtInitConfiguration config = null;

	private static AgentInfo agentInfo;

	public void onModuleLoad() {
		LGame.agentInfo = computeAgentInfo();
		this.config = getConfig();
		this.log = config.log;

		if (config.rootPanel != null) {
			this.root = config.rootPanel;
		} else {
			Element element = Document.get().getElementById("embed-" + GWT.getModuleName());
			if (element == null) {
				VerticalPanel panel = new VerticalPanel();
				panel.setWidth("" + config.width + "px");
				panel.setHeight("" + config.height + "px");
				panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
				panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
				RootPanel.get().add(panel);
				RootPanel.get().setWidth("" + config.width + "px");
				RootPanel.get().setHeight("" + config.height + "px");
				this.root = panel;
			} else {
				VerticalPanel panel = new VerticalPanel();
				panel.setWidth("" + config.width + "px");
				panel.setHeight("" + config.height + "px");
				panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
				panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
				element.appendChild(panel.getElement());
				root = panel;
			}
		}
	}
	CanvasElement canvas;
	WebGLRenderingContext context;
	GL20 gl;
	String extensions;
	float fps = 0;
	long lastTimeStamp = System.currentTimeMillis();
	long frameId = -1;
	float deltaTime = 0;
	float time = 0;
	int frames;

	boolean inFullscreenMode = false;
	private void setupCanvas(){
		Canvas canvasWidget = Canvas.createIfSupported();
		if (canvasWidget == null) {
			throw new RuntimeException("Canvas not supported");
		}
		canvas = canvasWidget.getCanvasElement();
		root.add(canvasWidget);
		canvas.setWidth(config.width);
		canvas.setHeight(config.height);
		WebGLContextAttributes attributes = WebGLContextAttributes.create();
		attributes.setAntialias(config.antialiasing);
		attributes.setStencil(config.stencil);
		attributes.setAlpha(false);
		attributes.setPremultipliedAlpha(false);
		attributes.setPreserveDrawingBuffer(config.preserveDrawingBuffer);

		context = WebGLRenderingContext.getContext(canvas, attributes);
		context.viewport(0, 0, config.width, config.height);
	}

	public GwtInitConfiguration getConfig(){
		return new GwtInitConfiguration(480, 320);
	}

	private static native AgentInfo computeAgentInfo() /*-{
		var userAgent = navigator.userAgent.toLowerCase();
		return {

			isFirefox : userAgent.indexOf("firefox") != -1,
			isChrome : userAgent.indexOf("chrome") != -1,
			isSafari : userAgent.indexOf("safari") != -1,
			isOpera : userAgent.indexOf("opera") != -1,
			isIE : userAgent.indexOf("msie") != -1,

			isMacOS : userAgent.indexOf("mac") != -1,
			isLinux : userAgent.indexOf("linux") != -1,
			isWindows : userAgent.indexOf("win") != -1
		};
	}-*/;

	public static class AgentInfo extends JavaScriptObject {
		public final native boolean isFirefox() /*-{
			return this.isFirefox;
		}-*/;

		public final native boolean isChrome() /*-{
			return this.isChrome;
		}-*/;

		public final native boolean isSafari() /*-{
			return this.isSafari;
		}-*/;

		public final native boolean isOpera() /*-{
			return this.isOpera;
		}-*/;

		public final native boolean isIE() /*-{
			return this.isIE;
		}-*/;

		public final native boolean isMacOS() /*-{
			return this.isMacOS;
		}-*/;

		public final native boolean isLinux() /*-{
			return this.isLinux;
		}-*/;

		public final native boolean isWindows() /*-{
			return this.isWindows;
		}-*/;

		protected AgentInfo() {
		}
	}

	native static public void consoleLog(String message) /*-{
		console.log("GWT: " + message);
	}-*/;

	private native void addEventListeners() /*-{
		var self = this;
		$doc.addEventListener('visibilitychange', function(e) {
			self.@loon.LGame::onVisibilityChange(Z)($doc['hidden'] !== true);
		});
	}-*/;

	private void onVisibilityChange(boolean visible) {
		if (visible) {
			/*
			 * for (LifecycleListener listener : lifecycleListeners) {
			 * listener.resume(); } listener.resume();
			 */
		} else {
			/*
			 * for (LifecycleListener listener : lifecycleListeners) {
			 * listener.pause(); } listener.pause();
			 */
		}
	}
	

	public interface LoadingListener{

		public void beforeSetup();

		public void afterSetup();
	}

	public String getPreloaderBaseURL() {
		return GWT.getHostPageBaseURL() + "assets/";
	}

}
