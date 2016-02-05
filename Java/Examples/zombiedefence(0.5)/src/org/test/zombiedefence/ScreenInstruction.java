package org.test.zombiedefence;

import loon.LTexture;
import loon.action.sprite.SpriteBatch;
import loon.action.sprite.SpriteBatch.SpriteEffects;
import loon.canvas.LColor;
import loon.geom.Vector2f;
import loon.utils.timer.GameTime;

public class ScreenInstruction extends Screen {
	private Vector2f aimingAreaPosition;
	private float alpha;
	private float alphaInc;
	private Vector2f displayingAreaPosition;
	private Vector2f firingAreaPosition;
	private Vector2f GrenadeAreaPosition;
	private int iPhase;
	private boolean isInstrctionsFInished;
	private Phase phase = Phase.values()[0];
	private int phaseLength;
	private LTexture t2DAimingArea;
	private LTexture t2DButtonReplay;
	private LTexture t2DButtonSkip;
	private LTexture t2DDisplayingArea;
	private LTexture t2DFiringArea;
	private LTexture t2DGrenadeArea;
	private LTexture t2DTitle;
	private String txtAim;
	private Vector2f txtAimPosition;
	private String txtDisplay;
	private Vector2f txtDisplayPosition = new Vector2f();
	private String txtFire;
	private Vector2f txtFirePosition;
	private String txtGrenade;
	private Vector2f txtGrenadePosition;

	public ScreenInstruction() {
		super.screenPause = new ScreenPause(this, Help.GameScreen.Instruction);
		this.phase = Phase.Aiming;
		this.phaseLength = 150;
		this.iPhase = 0;
		this.alpha = 1f;
		this.alphaInc = 0.08f;
		this.isInstrctionsFInished = false;
		this.aimingAreaPosition = new Vector2f(750f, 350f);
		this.firingAreaPosition = new Vector2f(50f, 430f);
		this.GrenadeAreaPosition = new Vector2f(400f, 350f);
		this.displayingAreaPosition = this.aimingAreaPosition;
		this.txtAim = "推动刻度尺进行瞄准";
		this.txtFire = "点击红色按钮进行射击";
		this.txtGrenade = "单击屏幕区域使用手雷";
		this.txtDisplay = this.txtAim;
		this.txtAimPosition = new Vector2f(360f, 430f);
		this.txtFirePosition = new Vector2f(120f, 430f);
		this.txtGrenadePosition = new Vector2f(50f, 430f);
	}

	@Override
	public void Draw(SpriteBatch batch) {
		super.Draw(batch);
		if (!super.isPaused) {
			batch.draw(this.t2DTitle, 10f, 20f);
			batch.draw(ScreenGameplay.t2DBarrierOriginal, 640f, 0f);
			if (!this.isInstrctionsFInished) {
				batch.draw(this.t2DDisplayingArea, this.displayingAreaPosition,
						null, Global.Pool.getColor(this.alpha, this.alpha,
								this.alpha, this.alpha), 0f,

						(this.t2DDisplayingArea.getWidth() / 2),
						(this.t2DDisplayingArea.getHeight() / 2), 1f,
						SpriteEffects.None);
				batch.drawString(Screen.myFont, this.txtDisplay,
						this.txtDisplayPosition, LColor.white);
			}
		}
	}

	@Override
	public void LoadContent() {
		super.LoadContent();
		super.bgTexture = Global.Load("PlayGround");
		this.t2DTitle = Global.Load("TitleInstruction");
		this.t2DButtonReplay = Global.Load("ButtonReplay");
		this.t2DButtonSkip = Global.Load("ButtonSkip");
		this.t2DAimingArea = Global.Load("InstructionAim");
		this.t2DFiringArea = Global.Load("InstructionButton");
		this.t2DGrenadeArea = Global.Load("InstructionGrenade");
		this.t2DDisplayingArea = this.t2DAimingArea;
		super.buttonList.add(new Button(this.t2DButtonSkip, new Vector2f(720f,
				110f), 0f, Help.ButtonID.Proceed, 15));
		super.screenPause.LoadContent();
	}

	@Override
	public void Update(GameTime gameTime) {
		super.Update(gameTime);
		switch (this.phase) {
		case Aiming:
			this.t2DDisplayingArea = this.t2DAimingArea;
			this.displayingAreaPosition = this.aimingAreaPosition;
			this.txtDisplay = this.txtAim;
			this.txtDisplayPosition = this.txtAimPosition;
			break;

		case Firing:
			this.t2DDisplayingArea = this.t2DFiringArea;
			this.displayingAreaPosition = this.firingAreaPosition;
			this.txtDisplay = this.txtFire;
			this.txtDisplayPosition = this.txtFirePosition;
			break;

		case LaunchingGrenade:
			this.t2DDisplayingArea = this.t2DGrenadeArea;
			this.displayingAreaPosition = this.GrenadeAreaPosition;
			this.txtDisplay = this.txtGrenade;
			this.txtDisplayPosition = this.txtGrenadePosition;
			break;
		}
		if (this.phase.getValue() <= Phase.LaunchingGrenade.getValue()) {
			this.iPhase++;
			if (this.iPhase >= this.phaseLength) {
				this.iPhase = 0;
				this.phase = Phase.forValue(this.phase.getValue());
				if (this.phase.getValue() > Phase.LaunchingGrenade.getValue()) {
					this.isInstrctionsFInished = true;
					super.buttonList
							.add(new Button(this.t2DButtonReplay, new Vector2f(
									80f, 420f), 0f, Help.ButtonID.Back, 0));
				}
			}
		}
		this.alpha += this.alphaInc;
		if (this.alpha >= 1f) {
			this.alphaInc = -0.05f;
		} else if (this.alpha <= 0f) {
			this.alphaInc = 0.05f;
		}
		if (super.isTranAnimFinished) {
			if (super.buttonClicked != null) {
				switch (super.buttonClicked.getButtonID()) {
				case Proceed:
					Help.currentGameState = Help.GameScreen.Gameplay;
					super.buttonClicked = null;
					break;

				case Back:
					this.phase = Phase.Aiming;
					this.iPhase = 0;
					this.isInstrctionsFInished = false;
					super.buttonList.remove(super.buttonList.size() - 1);
					super.buttonClicked = null;
					break;
				default:
					break;
				}
			}
			super.buttonClicked = null;
			super.isTranAnimFinished = false;
			this.t2DDisplayingArea = this.t2DAimingArea;
			this.displayingAreaPosition = this.aimingAreaPosition;
			this.iPhase = 0;
			this.phase = Phase.Aiming;
		}
		if (super.mousePositionList.size() > 0) {
			super.mousePositionList.clear();
		}
	}

	public enum Phase {
		Aiming, Firing, LaunchingGrenade;

		public int getValue() {
			return this.ordinal();
		}

		public static Phase forValue(int value) {
			return values()[value];
		}
	}
}