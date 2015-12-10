package org.test;

import loon.EmulatorListener;
import loon.LSetting;
import loon.LSystem;
import loon.LTexture;
import loon.LTextureBatch;
import loon.LTextures;
import loon.LTransition;
import loon.LazyLoading;
import loon.Screen;
import loon.action.avg.AVGDialog;
import loon.action.sprite.effect.FadeOvalEffect;
import loon.canvas.Canvas;
import loon.canvas.Image;
import loon.canvas.LColor;
import loon.event.GameTouch;
import loon.event.Updateable;
import loon.font.LFont;
import loon.font.TextLayout;
import loon.geom.Triangle2f;
import loon.javase.Loon;
import loon.opengl.BlendState;
import loon.opengl.GL20;
import loon.opengl.GLEx;
import loon.opengl.LSTRFont;
import loon.opengl.Painter;
import loon.opengl.GLEx.Direction;
import loon.stage.ImagePlayer;
import loon.stage.Player;
import loon.stage.PlayerUtils;
import loon.stage.Stage;
import loon.stage.StageSystem;
import loon.stage.StageTransition;
import loon.utils.Scale;
import loon.utils.processes.RealtimeProcess;
import loon.utils.timer.GameTime;
import loon.utils.timer.LTimerContext;

public class StageTest {

	public static class ScreenTest extends Screen implements EmulatorListener {

		public Stage newStage1() {

			Stage stage = new Stage() {

				@Override
				public void onAdded() {

					addPlayer(new ImagePlayer("back1.png"));

					Player player = createTextPlayer(LFont.getDefaultFont(),
							"人民万岁");
					player.setLocation(155, 155);
					addPlayer(player);
				}

				@Override
				public void onRemoved() {

				}

				@Override
				public void onShowTransitionCompleted() {
remove(this);
				}

				@Override
				public void onHideTransitionStarted() {

				}
			};

			return stage;
		}

		// LTexture texture = loadTexture("loon_wbar.png");

		public void onLoad() {

			puspStage(newStage1(), StageSystem.flip());

			add(MultiScreenTest.getBackButton(this));
		}

		boolean flag;
		LSTRFont font = new LSTRFont(LFont.getFont(20));
		LTexture text2 = null;
		LTextureBatch batch;

		@Override
		public void draw(GLEx g) {
	
			/*
			 * if (text2 == null) { // Image image
			 * =Image.createImage("loon_wbar.png");
			 * 
			 * text2 = AVGDialog.getRMXPDialog("w6.png", 400, 400); }
			 * g.draw(text2, 0, 0);
			 */
			/*
			 * g.setColor(LColor.red); g.drawLine(55, 55, 112, 112);
			 * g.resetColor();
			 * 
			 * g.draw(texture, 66, 66);
			 */
			// if (batch == null) {
			// batch = new LTextureBatch(text2);
			// }
			// batch.begin();
			// batch.draw(55, 55);
			// batch.end();
			// g.draw(text2,66,66,100,100,270);
			// g.translate(200, 200);
			// g.drawString("中国人民", 66, 66);
			// g.translate(-200, -200);

			/*
			 * if (text2 == null) { text2 = texture.copy(240, 240, 43, 43); } if
			 * (batch == null) { batch = new LTextureBatch(text2); }
			 * batch.begin(); batch.draw(155, 155); batch.end();
			 */
			// if(!flag){
			/*
			 * flag=true; Canvas canvas =
			 * LSystem.base().graphics().createCanvas(400, 400);
			 * canvas.setColor(LColor.red); canvas.setAlpha(1f);
			 * canvas.fillRect(0, 0, 400, 400);
			 * 
			 * if(batch==null){ batch=new LTextureBatch(canvas.toTexture()); }
			 * batch.begin(); batch.draw(33, 33); batch.end();
			 */
			// }
			// if(!flag){
			// flag=true;
			// font.startChar();
			// if(!flag){
			// flag=true;
			// g.translate(255, 155);
			// g.drawString("中国人民", 66, 66);
			// g.translate(-255, -155);
			// g.restoreTx();
			// }
			// font.stopChar();
			// }
			// if(!flag){
			// g.begin();
			// flag=true;

			// batch.begin();
			// batch.draw(66, 66);
			// batch.draw(166, 166);
			// batch.end();
			// g.initBatch();

		}

		// g.end();
		// g.restoreTx();
		/*
		 * (if(!flag){ flag=true; batch.begin(); batch.draw(66, 66);
		 * batch.draw(166, 166); batch.end(); }
		 */
		@Override
		public void close() {
			// TODO Auto-generated method stub

		}

		@Override
		public void alter(LTimerContext timer) {
			// TODO Auto-generated method stub

		}

		@Override
		public void touchDown(GameTouch e) {
			// add(new
			// FadeOvalEffect(FadeOvalEffect.TYPE_FADE_IN,LColor.black));

			Updateable update = new Updateable() {

				@Override
				public void action(Object a) {
					Stage stage = new Stage() {

						@Override
						public void onAdded() {

							addPlayer(new ImagePlayer(AVGDialog.getRMXPDialog(
									"w6.png", 400, 400)));

						}

						@Override
						public void onRemoved() {

						}

						@Override
						public void onShowTransitionCompleted() {
							RealtimeProcess process = new RealtimeProcess() {

								@Override
								public void run(LTimerContext time) {

									addPlayer(new ImagePlayer("back1.png"));

									Player player = createTextPlayer(LFont.getDefaultFont(),
											"人民万岁");
									player.setLocation(155, 155);
									addPlayer(player);
							
									kill();
								}
							};
							addProcess(process);

							process.setDelay(0);
							
						}

						@Override
						public void onHideTransitionStarted() {

						}
					};
					puspStage(stage, StageSystem.flip());
				}
			};
			LSystem.load(update);
		}

		@Override
		public void touchUp(GameTouch e) {

			System.out.println(e.x() + "," + e.y());
			System.out.println("up");
		}

		@Override
		public void touchMove(GameTouch e) {

			System.out.println("move");

		}

		@Override
		public void touchDrag(GameTouch e) {

			System.out.println("drag");

		}

		@Override
		public void resize(int width, int height) {
			// TODO Auto-generated method stub

		}

		@Override
		public void resume() {
			// TODO Auto-generated method stub

		}

		@Override
		public void pause() {

		}

		@Override
		public void onUpClick() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onLeftClick() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onRightClick() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onDownClick() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTriangleClick() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onSquareClick() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onCircleClick() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onCancelClick() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void unUpClick() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void unLeftClick() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void unRightClick() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void unDownClick() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void unTriangleClick() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void unSquareClick() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void unCircleClick() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void unCancelClick() {
			// TODO Auto-generated method stub
			
		}

	}

	public static void main(String[] args) {
		LSetting setting = new LSetting();
		setting.isFPS = true;
		setting.isLogo = false;
		setting.logoPath = "loon_logo.png";
		setting.fps = 60;
		setting.fontName = "黑体";
		setting.appName = "test";
		setting.emulateTouch = false;
		Loon.register(setting, new LazyLoading.Data() {
			
			@Override
			public Screen onScreen() {
				return new ScreenTest();
			}
		});
	}

}
