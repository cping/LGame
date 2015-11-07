package loon.html5.gwt;

import loon.LSetting;
import loon.canvas.LColor;
import loon.html5.gwt.preloader.Preloader.PreloaderCallback;
import loon.html5.gwt.preloader.Preloader.PreloaderState;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class GWTProgressDef {

	private static native boolean isImageComplete(ImageElement img) /*-{
		return img.complete;
	}-*/;

	/**
	 * 一个基于Canvas的进度抽象类，用于让用户自行在Canvas上绘制出自己需要的进度样式.
	 */
	public static abstract class LogoCanvasProcess implements GWTProgress {

		private float currentStep;
		private Canvas canvas;
		protected final int canvasWidth;
		protected final int canvasHeight;
		private float maxStep = 100;

		public LogoCanvasProcess(int w, int h, int step) {
			this.canvasWidth = w;
			this.canvasHeight = h;
			this.maxStep = step;
		}

		private void progress(final Canvas g, final int tick,
				final float currentStep) {
			render(g, tick, currentStep, maxStep);
		}

		public abstract void init();

		public final boolean isCompleted() {
			return currentStep >= maxStep;
		}

		public abstract void render(final Canvas g, final int tick,
				final float curStep, final float maxStep);

		public PreloaderCallback getPreloaderCallback(Loon loon, Panel root) {
			init();
			VerticalPanel mainPanel = new VerticalPanel();
			canvas = GWTCanvasUtils.createCanvas(canvasWidth, canvasHeight);
			mainPanel.add(canvas);
			root.add(mainPanel);
			final double start = startNow();
			return new PreloaderCallback() {

				@Override
				public void error(String file) {
					Loon.consoleLog("error: " + file);
				}

				@Override
				public void update(PreloaderState state) {
					progress(canvas, (int) (nowTime() - start),
							(currentStep = maxStep * state.getProgress()));
				}

			};
		}

		private static native double startNow() /*-{
			if (!Date.now) {
				Date.now = function now() {
					return +(new Date);
				};
			}
			return Date.now();
		}-*/;

		private static native double nowTime() /*-{
			return Date.now();
		}-*/;

	}

	/**
	 * 默认初始进度条之一，严格来讲是一个示例，它演示了如何重载LogoCanvasProcess绘制进度条.
	 */
	public static class SimpleLogoProcess extends LogoCanvasProcess {

		private ImageElement logoImage;
		private int pWidth = 200, pHeight = 60;

		private String bgColor = LColor.black.toCSS();
		private String barColor = LColor.red.toCSS();
		private String barBgColor = "#808080";

		private LSetting config;
		private int centerX = -1, centerY = -1;
		private int logoX = -1, logoY = -1;

		public SimpleLogoProcess(LSetting config) {
			super(config.getShowWidth(), config.getShowHeight(), 100);
			this.config = config;
			this.pWidth = config.getShowWidth() - 80;
			this.centerX = (config.getShowWidth() - pWidth) / 2;
			this.centerY = (config.getShowHeight() - pHeight) / 2;
		}

		@Override
		public void render(Canvas g, int tick, final float currentStep,
				float maxStep) {
			if (centerX == -1 || centerY == -1) {
				this.pWidth = config.getShowWidth() - 80;
				this.centerX = config.getShowWidth() / 2 - pWidth / 2;
				this.centerY = config.getShowHeight() / 2 - pHeight / 2;
			}
			Context2d context = g.getContext2d();
			GWTCanvasUtils.fillRect(g, bgColor);
			if (isImageComplete(logoImage)) {
				this.logoX = (config.getShowWidth() - logoImage.getWidth()) / 2;
				this.logoY = (config.getShowHeight() - logoImage.getHeight()) / 2;
				context.drawImage(logoImage, logoX,
						logoY - logoImage.getHeight() - 40);
				context.setFillStyle(barBgColor);
				context.fillRect(centerX, centerY, pWidth, pHeight);
				if (currentStep >= maxStep) {
					context.fillRect(centerX, centerY, pWidth, pHeight);
				} else {
					context.setFillStyle(barColor);
					context.fillRect(centerX, centerY,
							(int) (pWidth / maxStep * currentStep), pHeight);
				}
			}
		}

		@Override
		public void init() {
			Image logo = new Image(GWT.getModuleBaseURL() + "logo.png");
			logo.setStyleName("logo");
			Element element = logo.getElement();
			this.logoImage = ImageElement.as(element);

		}

	}

	/**
	 * 默认初始进度条之一，使用组件直接构建，而并非Canvas渲染.
	 */
	public static class LogoProcess implements GWTProgress {

		public LogoProcess() {

		}

		public PreloaderCallback getPreloaderCallback(Loon loon, Panel root) {
			final VerticalPanel preloaderPanel = new VerticalPanel();
			preloaderPanel.setStyleName("loon-preloader");
			final Image logo = new Image(GWT.getModuleBaseURL() + "logo.png");
			logo.setStyleName("logo");
			preloaderPanel.add(logo);
			final Panel meterPanel = new SimplePanel();
			meterPanel.getElement().getStyle().setBackgroundColor("#808080");
			meterPanel.setStyleName("loon-meter");
			meterPanel.addStyleName("red");
			final InlineHTML meter = new InlineHTML();
			final Style meterStyle = meter.getElement().getStyle();
			meterStyle.setWidth(0, Unit.PCT);
			meterPanel.add(meter);
			preloaderPanel.add(meterPanel);
			root.add(preloaderPanel);
			return new PreloaderCallback() {

				@Override
				public void error(String file) {
					Loon.consoleLog("error: " + file);
				}

				@Override
				public void update(PreloaderState state) {
					meterStyle.setWidth(100f * state.getProgress(), Unit.PCT);
				}

			};
		}

	}
	
	public static GWTProgress newLogoProcess() {
		return new LogoProcess();
	}

	public static GWTProgress newSimpleLogoProcess(LSetting config) {
		return new SimpleLogoProcess(config);
	}
}
