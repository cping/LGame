package org.test.crazyjumpergles;

import java.util.ArrayList;

import org.test.crazyjumpergles.common.Font;
import org.test.crazyjumpergles.common.ObjectPool;
import org.test.crazyjumpergles.common.Sprite;
import org.test.crazyjumpergles.common.SpriteAnim;
import org.test.crazyjumpergles.common.Tools;

import loon.LSystem;
import loon.LTransition;
import loon.action.sprite.SpriteBatch;
import loon.action.sprite.painting.DrawableScreen;
import loon.events.GameKey;
import loon.events.GameTouch;
import loon.events.SysKey;
import loon.geom.RectBox;
import loon.utils.RefObject;
import loon.utils.timer.GameTime;

public class MainGame extends DrawableScreen {

	private static MainGame instance;

	public static MainGame get() {
		return instance;
	}

	private EMenu m_ActiveMenu;
	protected boolean m_bAudioInterrupted;
	protected boolean m_bEngineInitialized;
	protected boolean m_bFirstRun = true;
	private boolean m_bFutureWorldPack1Unlocked;
	private SpriteAnim m_Bg1SpriteAnim;
	private SpriteAnim m_Bg2SpriteAnim;
	private SpriteAnim m_Bg3SpriteAnim;
	protected boolean m_bGameInitialized;
	protected boolean m_bGamePaused;
	private SpriteAnim m_BgSpriteAnim;
	private boolean m_bJumpBig;
	private boolean m_bJumpSmall;
	private boolean[][] m_bLevelAllStarsDone = new boolean[3][0x15];
	private boolean[][] m_bLevelInFirstTryDone = new boolean[3][0x15];
	private boolean m_bLoadContent = true;
	private boolean m_bLoadContentReady;
	protected boolean m_bMusicOn = true;
	private ObjectPool<BouncePad> m_BouncePadPool = new ObjectPool<BouncePad>();
	private boolean m_bSkipLevel;
	protected boolean m_bSomeMusicTrackIsPlaying;
	protected boolean m_bSoundsOn = true;
	private Sprite m_BtnBackSprite;
	private Sprite m_BtnMoreGamesSprite;
	private SpriteAnim m_BtnMusicSprite;
	private Sprite m_BtnNoSprite;
	private Sprite m_BtnOptionsSprite;
	private Sprite m_BtnPlaySprite;
	private Sprite m_BtnQuitToMenuSprite;
	private Sprite m_BtnResumeSprite;
	private SpriteAnim m_BtnSoundsSprite;
	private Sprite m_BtnUseTokenSprite;
	private SpriteAnim m_BtnWorld1Sprite;
	private SpriteAnim m_BtnWorld2Sprite;
	private SpriteAnim m_BtnWorld3Sprite;
	private Sprite m_BtnYesSprite;
	protected boolean m_bVibrationOn;
	private boolean[] m_bWorldUnlocked = new boolean[3];
	private Sprite m_CreditsBgSprite;
	private float m_fBgPosX;
	protected float m_fDeltaTime;
	private float m_fGameSpeed;
	private float m_fGroundDeltaY;
	private float m_fGroundPosY;
	private float m_fMenuBackBtnDistFromFirstBtn;
	private float m_fMenuButtonsBetweenDist;
	private float m_fMenuButtonsDestPosY;
	private float m_fMenuButtonsFirstPosY;
	private float m_fMenuButtonsStartPosY;
	private float m_fMenuCharacterIdlePosY;
	private float m_fMenuCharacterJumpMaxPosY;
	private float m_fMenuCharacterMoveValue;
	private float m_fMenuCharacterStartPosY;
	private float m_fMsgMoveStateValue;
	private ObjectPool<ForceField> m_ForceFieldPool = new ObjectPool<ForceField>();
	private float m_fScreenFaderAlpha;
	private float m_fScreenFaderAlphaDir;
	private float m_fTitleCloudsMaxSize;
	private float m_fTitleMoveStateValue;

	private EGameState m_GameState;
	private int m_iBgCur;
	private int m_iBgForWorld;
	private int m_iBgNum;
	private int m_iCurrentLevel;
	private int m_iCurrentLevelCollectedStars;
	private int m_iCurrentLevelScore;
	private int m_iCurrentLevelTotalStars;
	private int m_iCurrentWorld;
	protected int m_iDeltaTimeMS;
	protected int m_iEngineRunningTimeMS;
	protected int m_iHandednessType;
	private int m_iJumpBigTime;
	private int m_iJumpSmallTime;
	private int[][] m_iLevelBestScore = new int[3][0x15];
	private int[][] m_iLevelState = new int[3][0x15];
	private int m_iRTGLogoTimer;
	private int m_iScoreToCurrentLevel;

	protected int m_iScreenOrientation;

	protected int m_iSocialGamingType;

	private int[] m_iWorldTokens = new int[3];
	private EMenu m_LastActiveMenu;
	private float m_LoadContentEnd;
	private float m_LoadContentStart;
	private Sprite m_MainMenuBoxesSprite;
	private Sprite m_MenuBgSprite;
	private EItemMoveState m_MenuButtonsState;
	private EItemMoveState m_MenuCharacterMoveState;
	private Sprite m_MenuCharacterSprite;
	private EMenuCharacterState m_MenuCharacterState;
	private Sprite m_MsgBigJumpSprite;
	private Sprite m_MsgBouncePadSprite;
	private Sprite m_MsgLevelCompleteSprite;
	private EItemMoveState m_MsgMoveState;
	private Sprite m_MsgSmallJumpSprite;
	private Sprite m_MsgTapToStartSprite;
	private SpriteAnim m_MsgWorldCompletedSprite;

	private EMenu m_NextActiveMenu;
	private ObjectPool<Obstacle> m_ObstaclePool = new ObjectPool<Obstacle>();
	private Sprite m_PauseSprite;
	private Font m_pDefaultFont;
	private Ground m_pGround;
	private LevelMaker m_pLevelMaker;
	private GamePlayer m_pPlayer;

	private Sprite m_RTGLogoSprite;

	private SpriteAnim m_SelLevelItemSprite;

	private ObjectPool<SpeedChange> m_SpeedChangePool = new ObjectPool<SpeedChange>();
	private ObjectPool<Star> m_StarPool = new ObjectPool<Star>();
	private Sprite m_TitleCloudSprite;
	private Sprite m_TitleCrazyLittleSprite;
	private Sprite m_TitleJumperSprite;
	private EItemMoveState m_TitleMoveState;
	private ETutorial m_Tutorial;

	public final int NUM_LEVELS_PER_WORLD = 0x15;
	public final int NUM_WORLDS = 3;

	public final int WORLD_1 = 0;
	public final int WORLD_2 = 1;
	public final int WORLD_3 = 2;
	public final int WORLD_LAST = 2;
	public final int WORLD_NONE = -1;

	public MainGame() {
		MainGame.instance = this;
		this.m_BgSpriteAnim = null;
		this.m_iBgNum = 3;
		this.m_iRTGLogoTimer = 0;
		this.m_ActiveMenu = EMenu.Menu_None;
		this.m_NextActiveMenu = EMenu.Menu_None;
		this.m_LastActiveMenu = EMenu.Menu_None;
		this.m_MenuCharacterMoveState = EItemMoveState.ItemMoveState_Out;
		this.m_bFutureWorldPack1Unlocked = false;
		for (int i = 0; i < 3; i++) {
			this.m_bWorldUnlocked[i] = false;
			this.m_iWorldTokens[i] = 1;
			for (int j = 0; j < 0x15; j++) {
				this.m_iLevelState[i][j] = 5;
				this.m_iLevelBestScore[i][j] = 0;
				this.m_bLevelInFirstTryDone[i][j] = false;
				this.m_bLevelAllStarsDone[i][j] = false;
			}
		}
		this.m_bWorldUnlocked[0] = true;
		this.m_iLevelState[0][0] = 0;
	}

	private void BeginGameState(EGameState newGameState) {
		this.m_GameState = newGameState;
		if (this.m_GameState == EGameState.GameState_Menu) {
			this.m_MainMenuBoxesSprite.SetColorAlpha(1f);

			this.GoToMenu(EMenu.Menu_Main, true);
		} else if (this.m_GameState == EGameState.GameState_PlaySelectedLevel) {
			this.m_fScreenFaderAlpha = 0f;
			this.m_fScreenFaderAlphaDir = 1f;

		} else if (this.m_GameState == EGameState.GameState_PlayFadeIn) {
			this.m_fScreenFaderAlpha = 1f;
			this.m_fScreenFaderAlphaDir = -1f;
			this.m_MsgMoveState = EItemMoveState.ItemMoveState_GoIn;
			this.m_MsgTapToStartSprite.SetPositionX(this.GetScreenWidth()
					+ this.m_MsgTapToStartSprite.GetHalfSizeX());
		} else if (this.m_GameState == EGameState.GameState_Playing) {
			this.m_ActiveMenu = EMenu.Menu_None;
			this.m_LastActiveMenu = EMenu.Menu_None;
			this.m_iCurrentLevelTotalStars = this.m_StarPool.GetUsedCount();
		} else if (this.m_GameState == EGameState.GameState_LevelFailed) {
			this.m_fScreenFaderAlpha = 0f;
			this.m_fScreenFaderAlphaDir = 1f;
		} else if (this.m_GameState == EGameState.GameState_LevelComplete) {
			this.m_fScreenFaderAlpha = 0f;
			this.m_fScreenFaderAlphaDir = 1f;
			this.m_fMsgMoveStateValue = 1f;
			this.m_MsgMoveState = EItemMoveState.ItemMoveState_GoIn;
			this.m_MsgLevelCompleteSprite.SetPositionX(this.GetScreenWidth()
					+ this.m_MsgLevelCompleteSprite.GetHalfSizeX());
		} else if (this.m_GameState == EGameState.GameState_WorldComplete) {
			this.m_fScreenFaderAlpha = 1f;
			this.m_fScreenFaderAlphaDir = -1f;
			this.m_fMsgMoveStateValue = 1f;
			this.m_MsgMoveState = EItemMoveState.ItemMoveState_GoIn;
			this.m_MsgWorldCompletedSprite
					.SetPositionX(-this.m_MsgWorldCompletedSprite
							.GetHalfSizeX());
			this.m_MsgWorldCompletedSprite.SetCurrentFrame(
					this.m_iCurrentWorld, false);
		} else if (this.m_GameState == EGameState.GameState_Tutorial) {
			this.m_fScreenFaderAlpha = 0f;
			this.m_fScreenFaderAlphaDir = 1f;
			this.m_MsgMoveState = EItemMoveState.ItemMoveState_GoIn;
			if (this.m_Tutorial == ETutorial.Tutorial_Jumping) {
				this.m_MsgSmallJumpSprite
						.SetPositionX(-this.m_MsgSmallJumpSprite.GetHalfSizeX());
				this.m_MsgBigJumpSprite.SetPositionX(this.GetScreenWidth()
						+ this.m_MsgBigJumpSprite.GetHalfSizeX());
			} else if (this.m_Tutorial == ETutorial.Tutorial_BouncePad) {
				this.m_MsgBouncePadSprite
						.SetPositionX(-this.m_MsgBouncePadSprite.GetHalfSizeX());
			}
		}
	}

	private void BeginMenuCharacterMoveState(EItemMoveState moveState) {
		this.m_MenuCharacterMoveState = moveState;
		if (this.m_MenuCharacterMoveState == EItemMoveState.ItemMoveState_GoIn) {
			this.m_fMenuCharacterMoveValue = 0f;
			this.m_MenuCharacterState = EMenuCharacterState.MenuCharacterState_Jump;
			this.m_MenuCharacterSprite.SetPositionY(this.GetScreenHeight()
					+ this.m_MenuCharacterSprite.GetHalfSizeY());
		} else if (this.m_MenuCharacterMoveState == EItemMoveState.ItemMoveState_GoOut) {
			this.m_fMenuCharacterMoveValue = 0f;
			this.m_MenuCharacterState = EMenuCharacterState.MenuCharacterState_Jump;
		}
	}

	private void BeginTitleMoveState(EItemMoveState moveState) {
		this.m_TitleMoveState = moveState;
		if (this.m_TitleMoveState == EItemMoveState.ItemMoveState_GoIn) {
			this.m_fTitleMoveStateValue = 0f;
			this.m_TitleCloudSprite.SetSize(0f, 0f);
			this.m_TitleCrazyLittleSprite.SetPositionX(this.GetScreenWidth()
					+ this.m_TitleCrazyLittleSprite.GetHalfSizeX());
			this.m_TitleJumperSprite.SetPositionX(-this.m_TitleJumperSprite
					.GetHalfSizeX());
		} else if (this.m_TitleMoveState == EItemMoveState.ItemMoveState_GoOut) {
			this.m_fTitleMoveStateValue = 1f;
		}
	}

	public void ChangeGameSpeed(float fBySpeed) {
		this.m_fGameSpeed += fBySpeed;
	}

	public void ClearJumpBig() {
		this.m_bJumpBig = false;
	}

	public void ClearJumpSmall() {
		this.m_bJumpSmall = false;
	}

	public void CreateBouncePadDown(int iAtBlock, int iDeltaQuarters,
			int iQuartersFromGround) {
		BouncePad nextFree = this.m_BouncePadPool.GetNextFree();
		nextFree.inuse = true;
		nextFree.Create(iAtBlock, iDeltaQuarters, iQuartersFromGround);
		nextFree.SetBounceDirection(BouncePad.EBounceDirection.Bounce_Down);
	}

	public void CreateBouncePadUp(int iAtBlock, int iDeltaQuarters,
			int iQuartersFromGround) {
		BouncePad nextFree = this.m_BouncePadPool.GetNextFree();
		nextFree.inuse = true;
		nextFree.Create(iAtBlock, iDeltaQuarters, iQuartersFromGround);
		nextFree.SetBounceDirection(BouncePad.EBounceDirection.Bounce_Up);
	}

	public void CreateExitableForceField(int iAtBlock, int iDeltaQuarters,
			int iSizeBlocks, int iQuartersFromGround) {
		ForceField nextFree = this.m_ForceFieldPool.GetNextFree();
		nextFree.inuse = true;
		nextFree.CreateExitable(iAtBlock, iDeltaQuarters, iSizeBlocks,
				iQuartersFromGround);
	}

	public void CreateForceField(int iAtBlock, int iDeltaQuarters,
			int iSizeBlocks, int iQuartersFromGround) {
		ForceField nextFree = this.m_ForceFieldPool.GetNextFree();
		nextFree.inuse = true;
		nextFree.Create(iAtBlock, iDeltaQuarters, iSizeBlocks,
				iQuartersFromGround);
	}

	public void CreateObstacle(int iAtBlock, int iDeltaQuarters,
			int iSizeXQuarters, int iSizeYQuarters, int iQuartersFromGround) {
		Obstacle nextFree = this.m_ObstaclePool.GetNextFree();
		nextFree.inuse = true;
		nextFree.Create(iAtBlock, iDeltaQuarters, iSizeXQuarters,
				iSizeYQuarters, iQuartersFromGround);
	}

	public SpeedChange CreateSpeedChange(int iAtBlock) {
		SpeedChange nextFree = this.m_SpeedChangePool.GetNextFree();
		nextFree.inuse = true;
		nextFree.Create(iAtBlock);
		return nextFree;
	}

	public void CreateSpeedChangeDownEnd(int iAtBlock) {
		this.CreateSpeedChange(iAtBlock).SetSpeedChangeType(
				SpeedChange.ESpeedChangeType.SpeedChange_DownEnd);
	}

	public void CreateSpeedChangeDownStart(int iAtBlock) {
		this.CreateSpeedChange(iAtBlock).SetSpeedChangeType(
				SpeedChange.ESpeedChangeType.SpeedChange_DownStart);
	}

	public void CreateSpeedChangeUpEnd(int iAtBlock) {
		this.CreateSpeedChange(iAtBlock).SetSpeedChangeType(
				SpeedChange.ESpeedChangeType.SpeedChange_UpEnd);
	}

	public void CreateSpeedChangeUpStart(int iAtBlock) {
		this.CreateSpeedChange(iAtBlock).SetSpeedChangeType(
				SpeedChange.ESpeedChangeType.SpeedChange_UpStart);
	}

	public void CreateStar(int iAtBlock) {
		this.CreateStar(iAtBlock, 0);
	}

	public void CreateStar(int iAtBlock, int iBlocksFromGround) {
		Star nextFree = this.m_StarPool.GetNextFree();
		nextFree.inuse = true;
		nextFree.Create(iAtBlock, iBlocksFromGround);
	}

	public void EndOfGround() {
		this.m_pPlayer.StartMovingOut();
	}

	public void EnterHiSpeed() {
		this.m_pPlayer.EnterHiSpeed();
	}

	public void EnterLowSpeed() {
		this.m_pPlayer.EnterLowSpeed();
	}

	public void ExitHiSpeed() {
		this.m_pPlayer.ExitHiSpeed();
	}

	public void ExitLowSpeed() {
		this.m_pPlayer.ExitLowSpeed();
	}

	public int GetCurrentWorld() {
		return this.m_iCurrentWorld;
	}

	public Font GetDefaultFont() {
		return this.m_pDefaultFont;
	}

	public float GetDeviceUnitScale() {
		return 1.5f;
	}

	public float GetDeviceUnitScaleX() {
		return 1.5f;
	}

	public float GetDeviceUnitScaleY() {
		return 1.66f;
	}

	public float GetGameSpeed() {
		return this.m_fGameSpeed;
	}

	public EGameState GetGameState() {
		return this.m_GameState;
	}

	public Ground GetGround() {
		return this.m_pGround;
	}

	public float GetGroundDeltaY() {
		return this.m_fGroundDeltaY;
	}

	public float GetGroundMaxDeltaY() {
		return (this.GetUnitBlockSize() * 3f);
	}

	public float GetGroundPosY() {
		return (this.m_fGroundPosY + this.m_fGroundDeltaY);
	}

	public String GetSaveGamePath() {
		return "";
	}

	public float GetScreenHeight() {
		return LSystem.viewSize.height;
	}

	public int GetScreenOrientation() {
		return this.m_iScreenOrientation;
	}

	public float GetScreenWidth() {
		return LSystem.viewSize.width;
	}

	private void GetSelectLevelButtonPosition(int iLevel, RefObject<Float> rX,
			RefObject<Float> rY) {
		int num = (iLevel - 1) / 7;
		int num2 = (iLevel - (num * 7)) - 1;
		rX.argvalue = 48f * this.GetDeviceUnitScale();
		rX.argvalue += ((this.GetScreenWidth() - (rX.argvalue * 2f)) / 6f)
				* num2;
		rY.argvalue = this.m_fMenuButtonsFirstPosY
				+ (num * this.m_fMenuButtonsBetweenDist);
	}

	public String GetSettingsDataFilePath() {
		return "";
	}

	public String GetSocialGamingDataFilePath() {
		return "";
	}

	public float GetUnitBlockHalfSize() {
		return (this.GetUnitBlockSize() * 0.5f);
	}

	public float GetUnitBlockQuarterSize() {
		return (this.GetUnitBlockSize() * 0.25f);
	}

	public float GetUnitBlockSize() {
		return 48f;
	}

	private int GetWorldScore(int iWorld, int iToLevel) {
		int num = 0;
		if ((iToLevel <= 0) || (iToLevel > 0x15)) {
			iToLevel = 0x15;
		}
		for (int i = 0; i < iToLevel; i++) {
			num += this.m_iLevelBestScore[iWorld][i];
		}
		return num;
	}

	private void GoToMenu(EMenu newMenu, boolean bImmediately) {
		this.m_LastActiveMenu = this.m_ActiveMenu;
		if (bImmediately) {
			this.m_ActiveMenu = newMenu;
			this.m_NextActiveMenu = EMenu.Menu_None;
			this.m_MenuButtonsState = EItemMoveState.ItemMoveState_GoIn;
			this.m_fMenuButtonsFirstPosY = this.m_fMenuButtonsStartPosY;
		} else {
			this.m_NextActiveMenu = newMenu;
			this.m_MenuButtonsState = EItemMoveState.ItemMoveState_GoOut;
		}
		float num = 0f;
		if (this.m_ActiveMenu == EMenu.Menu_Main) {
			num = 3f;
		} else if (this.m_ActiveMenu == EMenu.Menu_Options) {
			num = 2f;
		} else if (this.m_ActiveMenu == EMenu.Menu_ChooseSocialGaming) {
			num = 4f;
		} else if (this.m_ActiveMenu == EMenu.Menu_SelectWorld) {
			num = 3f;
		} else if (this.m_ActiveMenu == EMenu.Menu_Paused) {
			if (this.m_iWorldTokens[this.m_iCurrentWorld] > 0) {
				num = 3f;
			} else {
				num = 2f;
			}
		} else if (this.m_ActiveMenu == EMenu.Menu_UseToken) {
			num = 2f;
		}
		if (num == 0f) {
			if (this.m_ActiveMenu == EMenu.Menu_SelectLevel) {
				this.m_fMenuButtonsDestPosY = 45f * this.GetDeviceUnitScale();
				this.m_fMenuButtonsBetweenDist = 86f * this
						.GetDeviceUnitScale();
			} else if (this.m_ActiveMenu == EMenu.Menu_Credits) {
				this.m_fScreenFaderAlpha = 0f;
				this.m_CreditsBgSprite.SetColorAlpha(this.m_fScreenFaderAlpha);
			} else {
				this.m_fMenuButtonsDestPosY = this.GetScreenHeight()
						- (this.m_BtnBackSprite.GetHalfSizeY() + (5f * this
								.GetDeviceUnitScale()));
			}
		} else {
			this.m_fMenuButtonsBetweenDist = 55f * this.GetDeviceUnitScale();
			if (this.m_ActiveMenu == EMenu.Menu_Paused) {
				this.m_fMenuButtonsDestPosY = (this.GetScreenHeight() * 0.5f)
						- (this.m_fMenuButtonsBetweenDist * 0.5f);
			} else if (this.m_ActiveMenu == EMenu.Menu_UseToken) {
				this.m_fMenuButtonsDestPosY = (this.GetScreenHeight() * 0.75f)
						- (this.m_fMenuButtonsBetweenDist * 0.5f);
			} else {
				this.m_fMenuButtonsDestPosY = this.GetScreenHeight()
						- ((this.m_fMenuButtonsBetweenDist * (num - 1f)) + (this.m_fMenuButtonsBetweenDist * 0.5f));
			}
		}
		float num2 = this.GetScreenHeight()
				- (this.m_BtnBackSprite.GetHalfSizeY() + (5f * this
						.GetDeviceUnitScale()));
		this.m_fMenuBackBtnDistFromFirstBtn = num2
				- this.m_fMenuButtonsDestPosY;
		if (((this.m_ActiveMenu == EMenu.Menu_Main) || (this.m_ActiveMenu == EMenu.Menu_SelectWorld))
				|| (this.m_ActiveMenu == EMenu.Menu_Options)) {
			if (this.m_TitleMoveState != EItemMoveState.ItemMoveState_In) {
				this.BeginTitleMoveState(EItemMoveState.ItemMoveState_GoIn);
			}
		} else if ((((this.m_ActiveMenu == EMenu.Menu_SelectLevel) || (this.m_ActiveMenu == EMenu.Menu_Credits)) || (this.m_ActiveMenu == EMenu.Menu_ChooseSocialGaming))
				&& (this.m_TitleMoveState != EItemMoveState.ItemMoveState_Out)) {
			this.BeginTitleMoveState(EItemMoveState.ItemMoveState_GoOut);
		}
		if (this.m_ActiveMenu == EMenu.Menu_Main) {
			if ((this.m_MenuCharacterMoveState != EItemMoveState.ItemMoveState_In)
					&& (this.m_MenuCharacterMoveState != EItemMoveState.ItemMoveState_GoIn)) {
				this.BeginMenuCharacterMoveState(EItemMoveState.ItemMoveState_GoIn);
			}
		} else if ((this.m_MenuCharacterMoveState != EItemMoveState.ItemMoveState_Out)
				&& (this.m_MenuCharacterMoveState != EItemMoveState.ItemMoveState_GoOut)) {
			this.BeginMenuCharacterMoveState(EItemMoveState.ItemMoveState_GoOut);
		}
		if ((this.m_ActiveMenu == EMenu.Menu_Options)
				|| (this.m_NextActiveMenu == EMenu.Menu_Options)) {
			if (this.m_bSoundsOn) {
				this.m_BtnSoundsSprite.SetCurrentFrame(1, false);
			} else {
				this.m_BtnSoundsSprite.SetCurrentFrame(0, false);
			}
			if (this.m_bMusicOn) {
				this.m_BtnMusicSprite.SetCurrentFrame(1, false);
			} else {
				this.m_BtnMusicSprite.SetCurrentFrame(0, false);
			}
		} else if (this.m_ActiveMenu == EMenu.Menu_SelectWorld) {
			if (this.m_bWorldUnlocked[1]) {
				this.m_BtnWorld2Sprite.SetCurrentFrame(0, false);
			} else {
				this.m_BtnWorld2Sprite.SetCurrentFrame(1, false);
			}
			if (this.m_bWorldUnlocked[2]) {
				this.m_BtnWorld3Sprite.SetCurrentFrame(0, false);
			} else {
				this.m_BtnWorld3Sprite.SetCurrentFrame(1, false);
			}
		}
	}

	private void GotoNextLevel(boolean bSkip) {
		if (bSkip) {
			this.m_iWorldTokens[this.m_iCurrentWorld]--;
			this.m_iLevelState[this.m_iCurrentWorld][this.m_iCurrentLevel - 1] = 4;
			this.m_iLevelBestScore[this.m_iCurrentWorld][this.m_iCurrentLevel - 1] = 0;
			this.m_bLevelInFirstTryDone[this.m_iCurrentWorld][this.m_iCurrentLevel - 1] = false;
			this.m_bLevelAllStarsDone[this.m_iCurrentWorld][this.m_iCurrentLevel - 1] = false;
		} else {
			if (this.m_pPlayer.GetLevelTryCount() == 1) {
				this.m_iCurrentLevelScore += 500;
				this.m_bLevelInFirstTryDone[this.m_iCurrentWorld][this.m_iCurrentLevel - 1] = true;
			} else {
				int num = 100 - ((this.m_pPlayer.GetLevelTryCount() - 2) * 10);
				if (num > 0) {
					this.m_iCurrentLevelScore += num;
				}
			}
			if (this.m_iCurrentLevelTotalStars == this.m_iCurrentLevelCollectedStars) {
				this.m_iCurrentLevelScore += 500;
				this.m_bLevelAllStarsDone[this.m_iCurrentWorld][this.m_iCurrentLevel - 1] = true;
			} else {
				this.m_iCurrentLevelScore += ((int) (((float) this.m_iCurrentLevelCollectedStars) / ((float) this.m_iCurrentLevelTotalStars))) * 100;
			}
			if ((this.m_pPlayer.GetLevelTryCount() == 1)
					&& (this.m_iCurrentLevelTotalStars == this.m_iCurrentLevelCollectedStars)) {
				this.m_iCurrentLevelScore += 0x3e8;
				this.m_iLevelState[this.m_iCurrentWorld][this.m_iCurrentLevel - 1] = 3;
			} else if ((this.m_bLevelInFirstTryDone[this.m_iCurrentWorld][this.m_iCurrentLevel - 1] && this.m_bLevelAllStarsDone[this.m_iCurrentWorld][this.m_iCurrentLevel - 1])
					&& (this.m_iLevelState[this.m_iCurrentWorld][this.m_iCurrentLevel - 1] != 3)) {
				this.m_iLevelState[this.m_iCurrentWorld][this.m_iCurrentLevel - 1] = 2;
			} else if ((this.m_bLevelInFirstTryDone[this.m_iCurrentWorld][this.m_iCurrentLevel - 1] || this.m_bLevelAllStarsDone[this.m_iCurrentWorld][this.m_iCurrentLevel - 1])
					&& (this.m_iLevelState[this.m_iCurrentWorld][this.m_iCurrentLevel - 1] == 0)) {
				this.m_iLevelState[this.m_iCurrentWorld][this.m_iCurrentLevel - 1] = 1;
			} else if (this.m_iLevelState[this.m_iCurrentWorld][this.m_iCurrentLevel - 1] == 4) {
				this.m_iLevelState[this.m_iCurrentWorld][this.m_iCurrentLevel - 1] = 0;
			}
			if (this.m_iCurrentLevelScore > this.m_iLevelBestScore[this.m_iCurrentWorld][this.m_iCurrentLevel - 1]) {
				this.m_iLevelBestScore[this.m_iCurrentWorld][this.m_iCurrentLevel - 1] = this.m_iCurrentLevelScore;
			}
		}
		boolean flag = true;
		for (int i = 0; i < 0x15; i++) {
			if ((this.m_iLevelState[this.m_iCurrentWorld][i] == 5)
					|| (this.m_iLevelState[this.m_iCurrentWorld][i] == 4)) {
				flag = false;
				break;
			}
		}
		if (flag) {

			for (int j = 0; j < 0x15; j++) {
				if (this.m_iLevelState[this.m_iCurrentWorld][j] != 3) {
					break;
				}
			}
		}
		this.m_iCurrentLevel++;
		if (this.m_iCurrentLevel > 0x15) {
			this.m_iCurrentLevel = 1;
			this.m_iCurrentWorld++;
			if (this.m_iCurrentWorld > 2) {
				this.m_iCurrentWorld = -1;
				this.m_bFutureWorldPack1Unlocked = true;
			}
			if (this.m_iCurrentWorld != -1) {
				if (!this.m_bWorldUnlocked[this.m_iCurrentWorld]) {
					this.m_bWorldUnlocked[this.m_iCurrentWorld] = true;
					this.m_iWorldTokens[this.m_iCurrentWorld] = 1;
				}
				this.PrepareWorldBackground();
			}
		}
		if (this.m_iCurrentWorld != -1) {
			if (this.m_iCurrentLevel > 1) {
				this.m_iScoreToCurrentLevel = this.GetWorldScore(
						this.m_iCurrentWorld, this.m_iCurrentLevel - 1);
			} else {
				this.m_iScoreToCurrentLevel = 0;
			}
			if (this.m_iLevelState[this.m_iCurrentWorld][this.m_iCurrentLevel - 1] == 5) {
				this.m_iLevelState[this.m_iCurrentWorld][this.m_iCurrentLevel - 1] = 0;
				if (((this.m_iCurrentLevel == 6) || (this.m_iCurrentLevel == 11))
						|| (this.m_iCurrentLevel == 0x10)) {
					this.m_iWorldTokens[this.m_iCurrentWorld]++;
				}
			}
		}
		if (this.m_iCurrentWorld != -1) {
			this.ResetLevel(false);
			this.m_pLevelMaker.CreateLevel(this.m_iCurrentLevel,
					this.m_iCurrentWorld);
		}
	}

	private void InitSounds() {

	}

	private void InitUI() {
		float x = 256f * this.GetDeviceUnitScale();
		float y = 64f * this.GetDeviceUnitScale();
		this.m_TitleCloudSprite = new Sprite();
		this.m_TitleCloudSprite.AddTextureByName("title_cloud", true);
		this.m_TitleCrazyLittleSprite = new Sprite();
		this.m_TitleCrazyLittleSprite.AddTextureByName("title_crazylittle",
				true);
		this.m_TitleJumperSprite = new Sprite();
		this.m_TitleJumperSprite.AddTextureByName("title_jumper", true);
		this.m_fTitleCloudsMaxSize = 220f * this.GetDeviceUnitScale();
		this.m_TitleCloudSprite.SetSize(220f * this.GetDeviceUnitScale(),
				220f * this.GetDeviceUnitScale());
		this.m_TitleCloudSprite.SetPosition(
				this.GetScreenWidth() / 2f,
				this.m_TitleCloudSprite.GetHalfSizeY()
						- (40f * this.GetDeviceUnitScale()));
		this.m_TitleCrazyLittleSprite.SetSize(220f * this.GetDeviceUnitScale(),
				55f * this.GetDeviceUnitScale());
		this.m_TitleCrazyLittleSprite.SetPosition(
				this.GetScreenWidth() / 2f,
				this.m_TitleCloudSprite.GetPositionY()
						- (25f * this.GetDeviceUnitScale()));
		this.m_TitleJumperSprite.SetSize(220f * this.GetDeviceUnitScale(),
				55f * this.GetDeviceUnitScale());
		this.m_TitleJumperSprite.SetPosition(
				this.GetScreenWidth() / 2f,
				this.m_TitleCloudSprite.GetPositionY()
						+ (25f * this.GetDeviceUnitScale()));
		this.m_MenuBgSprite = new Sprite();
		this.m_MenuBgSprite.EnableBlending(false);
		this.m_MenuBgSprite.AddTextureByName("menu_bg", true);
		this.m_MenuBgSprite.SetSize(800f, 480f);
		this.m_MenuBgSprite.SetPosition(this.m_MenuBgSprite.GetHalfSizeX(),
				this.m_MenuBgSprite.GetHalfSizeY());
		this.m_MainMenuBoxesSprite = new Sprite();
		this.m_MainMenuBoxesSprite.AddTextureByName("menu_main_boxes", true);
		this.m_MainMenuBoxesSprite.SetSize(160f, 480f);
		this.m_MainMenuBoxesSprite.SetPosition(this.GetScreenWidth()
				- this.m_MainMenuBoxesSprite.GetHalfSizeX(),
				this.m_MainMenuBoxesSprite.GetHalfSizeY());
		this.m_BtnBackSprite = new Sprite();
		this.m_BtnBackSprite.AddTextureByName("btn_back", false);
		this.m_BtnBackSprite.SetSize(64f * this.GetDeviceUnitScale(),
				64f * this.GetDeviceUnitScale());
		this.m_BtnBackSprite.SetPosition(
				this.m_BtnBackSprite.GetHalfSizeX()
						+ (5f * this.GetDeviceUnitScale()),
				this.GetScreenHeight()
						- (this.m_BtnBackSprite.GetHalfSizeY() + (5f * this
								.GetDeviceUnitScale())));
		this.m_BtnYesSprite = new Sprite();
		this.m_BtnYesSprite.AddTextureByName("btn_yes", false);
		this.m_BtnYesSprite.SetSize(x, y);
		this.m_BtnYesSprite.SetPosition(this.GetScreenWidth() / 2f, 100f);
		this.m_BtnNoSprite = new Sprite();
		this.m_BtnNoSprite.AddTextureByName("btn_no", false);
		this.m_BtnNoSprite.SetSize(x, y);
		this.m_BtnNoSprite.SetPosition(this.GetScreenWidth() / 2f, 100f);
		this.m_BtnPlaySprite = new Sprite();
		this.m_BtnPlaySprite.AddTextureByName("btn_play", true);
		this.m_BtnPlaySprite.SetSize(x, y);
		this.m_BtnPlaySprite.SetPosition(this.GetScreenWidth() / 2f, 100f);
		this.m_BtnOptionsSprite = new Sprite();
		this.m_BtnOptionsSprite.AddTextureByName("btn_options", true);
		this.m_BtnOptionsSprite.SetSize(x, y);
		this.m_BtnOptionsSprite.SetPosition(this.GetScreenWidth() / 2f, 100f);
		this.m_BtnMoreGamesSprite = new Sprite();
		this.m_BtnMoreGamesSprite.AddTextureByName("btn_moregames", true);
		this.m_BtnMoreGamesSprite.SetSize(x, y);
		this.m_BtnMoreGamesSprite.SetPosition(this.GetScreenWidth() / 2f, 100f);
		this.m_BtnSoundsSprite = new SpriteAnim();
		this.m_BtnSoundsSprite.AddTextureByName("btn_soundsoff", false);
		this.m_BtnSoundsSprite.AddTextureByName("btn_soundson", false);
		this.m_BtnSoundsSprite.SetSize(x, y);
		this.m_BtnSoundsSprite.SetPosition(this.GetScreenWidth() / 2f, 100f);
		this.m_BtnMusicSprite = new SpriteAnim();
		this.m_BtnMusicSprite.AddTextureByName("btn_musicoff", false);
		this.m_BtnMusicSprite.AddTextureByName("btn_musicon", false);
		this.m_BtnMusicSprite.SetSize(x, y);
		this.m_BtnMusicSprite.SetPosition(this.GetScreenWidth() / 2f, 100f);
		this.m_BtnWorld1Sprite = new SpriteAnim();
		this.m_BtnWorld1Sprite.AddTextureByName("btn_world1", false);
		this.m_BtnWorld1Sprite.SetSize(x, y);
		this.m_BtnWorld1Sprite.SetPosition(this.GetScreenWidth() / 2f, 100f);
		this.m_BtnWorld1Sprite.SetCurrentFrame(0, false);
		this.m_BtnWorld2Sprite = new SpriteAnim();
		this.m_BtnWorld2Sprite.AddTextureByName("btn_world2", false);
		this.m_BtnWorld2Sprite.AddTextureByName("btn_world2_locked", false);
		this.m_BtnWorld2Sprite.SetSize(x, y);
		this.m_BtnWorld2Sprite.SetPosition(this.GetScreenWidth() / 2f, 100f);
		this.m_BtnWorld3Sprite = new SpriteAnim();
		this.m_BtnWorld3Sprite.AddTextureByName("btn_world3", false);
		this.m_BtnWorld3Sprite.AddTextureByName("btn_world3_locked", false);
		this.m_BtnWorld3Sprite.SetSize(x, y);
		this.m_BtnWorld3Sprite.SetPosition(this.GetScreenWidth() / 2f, 100f);
		this.m_BtnResumeSprite = new Sprite();
		this.m_BtnResumeSprite.AddTextureByName("btn_resume", false);
		this.m_BtnResumeSprite.SetSize(x, y);
		this.m_BtnResumeSprite.SetPosition(this.GetScreenWidth() / 2f, 100f);
		this.m_BtnQuitToMenuSprite = new Sprite();
		this.m_BtnQuitToMenuSprite.AddTextureByName("btn_quittomenu", false);
		this.m_BtnQuitToMenuSprite.SetSize(x, y);
		this.m_BtnQuitToMenuSprite
				.SetPosition(this.GetScreenWidth() / 2f, 100f);
		this.m_BtnUseTokenSprite = new Sprite();
		this.m_BtnUseTokenSprite.AddTextureByName("btn_usetoken", false);
		this.m_BtnUseTokenSprite.SetSize(x, y);
		this.m_BtnUseTokenSprite.SetPosition(this.GetScreenWidth() / 2f, 100f);
		this.m_SelLevelItemSprite = new SpriteAnim();
		this.m_SelLevelItemSprite.AddTextureByName("sel_level_stars0", false);
		this.m_SelLevelItemSprite.AddTextureByName("sel_level_stars1", false);
		this.m_SelLevelItemSprite.AddTextureByName("sel_level_stars2", false);
		this.m_SelLevelItemSprite.AddTextureByName("sel_level_stars3", false);
		this.m_SelLevelItemSprite.AddTextureByName("sel_level_skipped", false);
		this.m_SelLevelItemSprite.AddTextureByName("sel_level_locked", false);
		this.m_SelLevelItemSprite.SetSize(60f * this.GetDeviceUnitScale(),
				120f * this.GetDeviceUnitScale());
		this.m_CreditsBgSprite = new Sprite();
		this.m_CreditsBgSprite.AddTextureByName("credits", false);
		this.m_CreditsBgSprite.SetSize(800f, 480f);
		this.m_CreditsBgSprite.SetPosition(
				this.m_CreditsBgSprite.GetHalfSizeX(),
				this.m_CreditsBgSprite.GetHalfSizeY());
		this.m_PauseSprite = new Sprite();
		this.m_PauseSprite.AddTextureByName("hud_pause", false);
		this.m_PauseSprite.SetSize(50f * this.GetDeviceUnitScale(),
				50f * this.GetDeviceUnitScale());
		this.m_PauseSprite.SetPosition(this.GetScreenWidth()
				- this.m_PauseSprite.GetHalfSizeX(),
				this.m_PauseSprite.GetHalfSizeY());
		this.m_MsgTapToStartSprite = new Sprite();
		this.m_MsgTapToStartSprite.AddTextureByName("msg_taptostart", false);
		this.m_MsgTapToStartSprite.SetSize(256f * this.GetDeviceUnitScale(),
				64f * this.GetDeviceUnitScale());
		this.m_MsgTapToStartSprite.SetPosition(this.GetScreenWidth() / 2f,
				this.GetScreenHeight() / 2f);
		this.m_MsgLevelCompleteSprite = new Sprite();
		this.m_MsgLevelCompleteSprite.AddTextureByName("msg_levelcomplete",
				false);
		this.m_MsgLevelCompleteSprite.SetSize(256f * this.GetDeviceUnitScale(),
				64f * this.GetDeviceUnitScale());
		this.m_MsgLevelCompleteSprite.SetPosition(this.GetScreenWidth() / 2f,
				this.GetScreenHeight() / 2f);
		this.m_MsgWorldCompletedSprite = new SpriteAnim();
		this.m_MsgWorldCompletedSprite.AddTextureByName("msg_world1completed",
				false);
		this.m_MsgWorldCompletedSprite.AddTextureByName("msg_world2completed",
				false);
		this.m_MsgWorldCompletedSprite.AddTextureByName("msg_world3completed",
				false);
		this.m_MsgWorldCompletedSprite.SetSize(
				256f * this.GetDeviceUnitScale(),
				128f * this.GetDeviceUnitScale());
		this.m_MsgWorldCompletedSprite.SetPosition(
				this.GetScreenWidth() / 2f,
				this.m_fGroundPosY
						- this.m_MsgWorldCompletedSprite.GetHalfSizeY());
		this.m_MsgSmallJumpSprite = new Sprite();
		this.m_MsgSmallJumpSprite.AddTextureByName("msg_smalljump", false);
		this.m_MsgSmallJumpSprite.SetSize(200f * this.GetDeviceUnitScale(),
				50f * this.GetDeviceUnitScale());
		this.m_MsgSmallJumpSprite.SetPosition(this.GetScreenWidth() / 2f,
				this.m_fGroundPosY
						- (this.m_MsgSmallJumpSprite.GetSizeY() * 2f));
		this.m_MsgBigJumpSprite = new Sprite();
		this.m_MsgBigJumpSprite.AddTextureByName("msg_bigjump", false);
		this.m_MsgBigJumpSprite.SetSize(200f * this.GetDeviceUnitScale(),
				50f * this.GetDeviceUnitScale());
		this.m_MsgBigJumpSprite.SetPosition(this.GetScreenWidth() / 2f,
				this.m_fGroundPosY - (this.m_MsgBigJumpSprite.GetSizeY() * 2f));
		this.m_MsgBouncePadSprite = new Sprite();
		this.m_MsgBouncePadSprite.AddTextureByName("msg_bouncepad", false);
		this.m_MsgBouncePadSprite.SetSize(200f * this.GetDeviceUnitScale(),
				200f * this.GetDeviceUnitScale());
		this.m_MsgBouncePadSprite.SetPosition(this.GetScreenWidth() / 2f,
				this.m_fGroundPosY - this.m_MsgBouncePadSprite.GetHalfSizeY());
		float num3 = 1f;
		this.m_MenuCharacterSprite = new Sprite();
		this.m_MenuCharacterSprite.AddTextureByName("menu_character", true);
		this.m_MenuCharacterSprite.SetSize(120f * this.GetDeviceUnitScale(),
				120f * this.GetDeviceUnitScale());
		this.m_fMenuCharacterIdlePosY = this.GetScreenHeight()
				- (65f * this.GetDeviceUnitScale());
		num3 = 0.95f;
		this.m_MenuCharacterSprite.SetPosition(
				this.m_MenuCharacterSprite.GetHalfSizeX() * num3,
				this.m_fMenuCharacterIdlePosY);
		this.m_fMenuCharacterStartPosY = this.GetScreenHeight()
				+ this.m_MenuCharacterSprite.GetHalfSizeY();
		this.m_fMenuCharacterJumpMaxPosY = this.m_fMenuCharacterIdlePosY
				- (this.m_MenuCharacterSprite.GetSizeY() * 1.5f);
		this.m_fMenuButtonsStartPosY = this.GetScreenHeight()
				+ this.m_BtnPlaySprite.GetHalfSizeY();
	}

	private void InitWorldsBackground() {
		this.m_Bg1SpriteAnim = new SpriteAnim();
		this.m_Bg1SpriteAnim.SetAnimLoopType(0);
		this.m_Bg1SpriteAnim.AddTextureByName("bg01_1", false);
		this.m_Bg1SpriteAnim.AddTextureByName("bg01_2", false);
		this.m_Bg1SpriteAnim.AddTextureByName("bg01_3", false);
		this.m_Bg1SpriteAnim.SetSize(1024f, 768f);
		this.m_Bg2SpriteAnim = new SpriteAnim();
		this.m_Bg2SpriteAnim.SetAnimLoopType(0);
		this.m_Bg2SpriteAnim.AddTextureByName("bg02_1", false);
		this.m_Bg2SpriteAnim.AddTextureByName("bg02_2", false);
		this.m_Bg2SpriteAnim.AddTextureByName("bg02_3", false);
		this.m_Bg2SpriteAnim.SetSize(1024f, 768f);
		this.m_Bg3SpriteAnim = new SpriteAnim();
		this.m_Bg3SpriteAnim.SetAnimLoopType(0);
		this.m_Bg3SpriteAnim.AddTextureByName("bg03_1", false);
		this.m_Bg3SpriteAnim.AddTextureByName("bg03_2", false);
		this.m_Bg3SpriteAnim.AddTextureByName("bg03_3", false);
		this.m_Bg3SpriteAnim.SetSize(1024f, 768f);
	}

	private boolean IsBtnSpriteTouch(Sprite pSprite, float x, float y,
			boolean bUIWideBtn) {
		float halfSizeY = 25f * this.GetDeviceUnitScale();
		if (!bUIWideBtn) {
			halfSizeY = pSprite.GetHalfSizeY();
		}
		return (((x > (pSprite.GetPositionX() - pSprite.GetHalfSizeX())) && (x < (pSprite
				.GetPositionX() + pSprite.GetHalfSizeX()))) && ((y > (pSprite
				.GetPositionY() - halfSizeY)) && (y < (pSprite.GetPositionY() + halfSizeY))));
	}

	private boolean IsFacebookTouch(float x, float y) {
		return (((x > 715f) && (x < 795f)) && ((y > 290f) && (y < 365f)));
	}

	public boolean IsGamePaused() {
		return this.m_bGamePaused;
	}

	public boolean IsJumpBig() {
		return this.m_bJumpBig;
	}

	public boolean IsJumpSmall() {
		return this.m_bJumpSmall;
	}

	public boolean IsMusicOn() {
		return this.m_bMusicOn;
	}

	private boolean IsRTGTouch(float x, float y) {
		return (((x > 665f) && (x < 765f)) && (y > 365f));
	}

	public boolean IsSoundsOn() {
		return this.m_bSoundsOn;
	}

	private boolean IsTwitterTouch(float x, float y) {
		return (((x > 635f) && (x < 715f)) && ((y > 290f) && (y < 365f)));
	}

	public void LevelFailed() {
		this.BeginGameState(EGameState.GameState_LevelFailed);
	}

	private void LoadGameData() {

	}

	private void LoadSettings() {

	}

	private void LoadSocialGamingData() {
	}

	public void OnBounce() {

	}

	public void OnCollided() {

	}

	public void OnForceFieldEnter() {

	}

	public void OnGroundBlockDone() {
		this.m_iCurrentLevelScore += 2;
	}

	private void OnInitGame() {
		this.m_pDefaultFont = new Font("font");
		this.InitUI();
		this.InitSounds();
		this.InitWorldsBackground();
		this.m_pPlayer = new GamePlayer();
		this.m_pPlayer.Init();
		this.m_pGround = new Ground();
		this.m_pGround.Init();
		for (int i = 0; i < 50; i++) {
			Obstacle pObject = new Obstacle();
			pObject.Init();
			this.m_ObstaclePool.AddObject(pObject);
		}
		for (int j = 0; j < 50; j++) {
			Star star = new Star();
			star.Init();
			this.m_StarPool.AddObject(star);
		}
		for (int k = 0; k < 10; k++) {
			SpeedChange change = new SpeedChange();
			change.Init();
			this.m_SpeedChangePool.AddObject(change);
		}
		for (int m = 0; m < 10; m++) {
			ForceField field = new ForceField();
			field.Init();
			this.m_ForceFieldPool.AddObject(field);
		}
		for (int n = 0; n < 10; n++) {
			BouncePad pad = new BouncePad();
			pad.Init();
			this.m_BouncePadPool.AddObject(pad);
		}
		this.m_pLevelMaker = new LevelMaker();
		this.m_iBgForWorld = -1;
		this.m_iCurrentWorld = -1;
		this.m_iCurrentLevel = 0;
	}

	public void OnJumpBig() {

	}

	public void OnJumpSmall() {

	}

	public void OnLanded() {
	}

	public final void ExtractVarAndVal(String str, RefObject<String> sVar,
			RefObject<String> sVal) {
		int index = str.indexOf('=');
		if (index != -1) {
			sVar.argvalue = str.substring(0, index);
			sVal.argvalue = str.substring(index + 1);
		}
	}

	public final void OnPickupStar(Star pStar) {
		this.m_iCurrentLevelScore += 10;
		this.m_iCurrentLevelCollectedStars++;
	}

	private void OnPreInit() {
		this.m_fGroundPosY = this.GetScreenHeight()
				- (this.GetUnitBlockSize() * 4f);
		this.m_fGroundDeltaY = 0f;
		this.m_fGameSpeed = this.GetUnitBlockSize() * 6f;
		this.m_RTGLogoSprite = new Sprite();
		this.m_RTGLogoSprite.AddTextureByName("rtg_logo", true);
		this.m_RTGLogoSprite.SetPosition(this.GetScreenWidth() * 0.5f,
				this.GetScreenHeight() * 0.5f);
		this.m_RTGLogoSprite.SetSize(256f, 256f);
		this.m_bJumpSmall = false;
		this.m_bJumpBig = false;
		this.LoadSettings();
		this.LoadSocialGamingData();
		this.LoadGameData();
		this.BeginGameState(EGameState.GameState_Logo);
	}

	private void OnUpdateGame() {
		if (this.m_GameState == EGameState.GameState_Logo) {
			if (this.m_bLoadContentReady && this.m_bLoadContent) {
				this.m_LoadContentStart = System.currentTimeMillis() / 1000f;
				this.OnInitGame();
				this.m_bLoadContent = false;
				this.m_LoadContentEnd = System.currentTimeMillis() / 1000f;
			} else if (!this.m_bLoadContent) {
				float span = (this.m_LoadContentEnd - this.m_LoadContentStart);
				int num2 = (int) (span * 1000.0);
				this.m_iRTGLogoTimer += this.m_iDeltaTimeMS;
				if (this.m_iRTGLogoTimer > (0x7d0 - num2)) {
					this.BeginGameState(EGameState.GameState_Menu);
					this.m_RTGLogoSprite.Unload();
				}
			}
		} else if (this.m_GameState == EGameState.GameState_Menu) {
			this.TickUI();
		} else if (this.m_GameState == EGameState.GameState_PlaySelectedLevel) {
			this.m_fScreenFaderAlpha += (this.m_fScreenFaderAlphaDir * this.m_fDeltaTime) * 2f;
			if (this.m_fScreenFaderAlpha > 1f) {
				this.PreparePlaySelectedLevel();
				this.BeginGameState(EGameState.GameState_PlayFadeIn);
			}
		} else if (this.m_GameState == EGameState.GameState_PlayFadeIn) {
			this.TickGame();
			if (this.m_MsgMoveState == EItemMoveState.ItemMoveState_GoIn) {
				this.m_fScreenFaderAlpha += (this.m_fScreenFaderAlphaDir * this.m_fDeltaTime) * 2f;
				if (this.m_fScreenFaderAlpha < 0f) {
					this.m_fScreenFaderAlpha = 0f;
					this.m_MsgMoveState = EItemMoveState.ItemMoveState_In;
					this.m_fMsgMoveStateValue = 0f;
				}
				float num3 = (this.GetScreenWidth() / 2f)
						+ this.m_MsgTapToStartSprite.GetHalfSizeX();
				this.m_MsgTapToStartSprite
						.SetPositionX((this.GetScreenWidth() / 2f)
								+ (num3 * this.m_fScreenFaderAlpha));
			} else if (this.m_MsgMoveState == EItemMoveState.ItemMoveState_GoOut) {
				float num4 = (this.GetScreenWidth() / 2f)
						+ this.m_MsgTapToStartSprite.GetHalfSizeX();
				this.m_fMsgMoveStateValue += this.m_fDeltaTime * 2f;
				if (this.m_fMsgMoveStateValue >= 1f) {
					this.m_fMsgMoveStateValue = 1f;
					this.m_MsgMoveState = EItemMoveState.ItemMoveState_Out;
					if ((this.m_iCurrentWorld == 0)
							&& (this.m_iCurrentLevel == 1)) {
						this.m_Tutorial = ETutorial.Tutorial_Jumping;
						this.BeginGameState(EGameState.GameState_Tutorial);
					} else if ((this.m_iCurrentWorld == 2)
							&& (this.m_iCurrentLevel == 6)) {
						this.m_Tutorial = ETutorial.Tutorial_BouncePad;
						this.BeginGameState(EGameState.GameState_Tutorial);
					} else {
						this.BeginGameState(EGameState.GameState_Playing);
						this.m_pPlayer.Go();
					}
				}
				this.m_MsgTapToStartSprite
						.SetPositionX((this.GetScreenWidth() / 2f)
								- (num4 * this.m_fMsgMoveStateValue));
			}
		} else if (this.m_GameState == EGameState.GameState_Playing) {
			if (this.IsGamePaused()) {
				this.TickUI();
			}
			this.TickGame();
		} else if (this.m_GameState == EGameState.GameState_LevelFailed) {
			this.m_fScreenFaderAlpha += (this.m_fScreenFaderAlphaDir * this.m_fDeltaTime) * 2f;
			if (this.m_fScreenFaderAlpha >= 1f) {
				this.m_fScreenFaderAlpha = 1f;
				this.RestartLevel();
				this.BeginGameState(EGameState.GameState_PlayFadeIn);
			} else {
				this.TickGame();
			}
		} else if (this.m_GameState == EGameState.GameState_LevelComplete) {
			float num5 = (this.GetScreenWidth() / 2f)
					+ this.m_MsgLevelCompleteSprite.GetHalfSizeX();
			if (this.m_MsgMoveState == EItemMoveState.ItemMoveState_GoIn) {
				this.m_fMsgMoveStateValue -= this.m_fDeltaTime * 2f;
				if (this.m_fMsgMoveStateValue < 0f) {
					this.m_fMsgMoveStateValue = 0f;
					this.m_MsgMoveState = EItemMoveState.ItemMoveState_In;
				}
				this.m_MsgLevelCompleteSprite.SetPositionX((this
						.GetScreenWidth() / 2f)
						+ (num5 * this.m_fMsgMoveStateValue));
			} else if (this.m_MsgMoveState == EItemMoveState.ItemMoveState_In) {
				this.m_fMsgMoveStateValue += this.m_fDeltaTime;
				if (this.m_fMsgMoveStateValue > 1f) {
					this.m_MsgMoveState = EItemMoveState.ItemMoveState_GoOut;
				}
			} else if (this.m_MsgMoveState == EItemMoveState.ItemMoveState_GoOut) {
				this.m_fScreenFaderAlpha += (this.m_fScreenFaderAlphaDir * this.m_fDeltaTime) * 2f;
				if (this.m_fScreenFaderAlpha >= 1f) {
					this.m_fScreenFaderAlpha = 1f;
					boolean flag = false;
					if (this.m_iCurrentLevel == 0x15) {
						if (this.m_iCurrentWorld == 2) {
							if (!this.m_bFutureWorldPack1Unlocked) {
								flag = true;
							}
						} else if (!this.m_bWorldUnlocked[this.m_iCurrentWorld + 1]) {
							flag = true;
						}
					}
					if (flag) {
						this.BeginGameState(EGameState.GameState_WorldComplete);
					} else {
						this.GotoNextLevel(this.m_bSkipLevel);
						if (this.m_iCurrentWorld == -1) {
							this.BeginGameState(EGameState.GameState_Menu);
						} else {
							this.BeginGameState(EGameState.GameState_PlayFadeIn);
						}
					}
				}
				this.m_MsgLevelCompleteSprite.SetPositionX((this
						.GetScreenWidth() / 2f)
						- (num5 * this.m_fScreenFaderAlpha));
			}
		} else if (this.m_GameState == EGameState.GameState_WorldComplete) {
			float num6 = (this.GetScreenWidth() / 2f)
					+ this.m_MsgWorldCompletedSprite.GetHalfSizeX();
			if (this.m_MsgMoveState == EItemMoveState.ItemMoveState_GoIn) {
				this.m_fScreenFaderAlpha -= this.m_fDeltaTime * 2f;
				if (this.m_fScreenFaderAlpha < 0f) {
					this.m_fScreenFaderAlpha = 0f;
					this.m_fScreenFaderAlphaDir = 1f;
					this.m_MsgMoveState = EItemMoveState.ItemMoveState_In;
				}
				this.m_MsgWorldCompletedSprite.SetPositionX((this
						.GetScreenWidth() / 2f)
						- (num6 * this.m_fScreenFaderAlpha));
			} else if (this.m_MsgMoveState == EItemMoveState.ItemMoveState_GoOut) {
				this.m_fScreenFaderAlpha += (this.m_fScreenFaderAlphaDir * this.m_fDeltaTime) * 2f;
				if (this.m_fScreenFaderAlpha >= 1f) {
					this.m_fScreenFaderAlpha = 1f;
					this.GotoNextLevel(this.m_bSkipLevel);
					if (this.m_iCurrentWorld == -1) {
						this.BeginGameState(EGameState.GameState_Menu);
					} else {
						this.BeginGameState(EGameState.GameState_PlayFadeIn);
					}
				}
				this.m_MsgWorldCompletedSprite.SetPositionX((this
						.GetScreenWidth() / 2f)
						+ (num6 * this.m_fScreenFaderAlpha));
			}
		} else if (this.m_GameState == EGameState.GameState_Tutorial) {
			this.TickGame();
			if (this.m_MsgMoveState == EItemMoveState.ItemMoveState_GoIn) {
				this.m_fScreenFaderAlpha += (this.m_fScreenFaderAlphaDir * this.m_fDeltaTime) * 2f;
				if (this.m_fScreenFaderAlpha >= 0.5f) {
					this.m_fScreenFaderAlpha = 0.5f;
					this.m_fScreenFaderAlphaDir = -1f;
					this.m_MsgMoveState = EItemMoveState.ItemMoveState_In;
				}
			} else if (this.m_MsgMoveState == EItemMoveState.ItemMoveState_GoOut) {
				this.m_fScreenFaderAlpha += (this.m_fScreenFaderAlphaDir * this.m_fDeltaTime) * 2f;
				if (this.m_fScreenFaderAlpha <= 0f) {
					this.m_fMsgMoveStateValue = 0f;
					this.m_MsgMoveState = EItemMoveState.ItemMoveState_Out;
					this.BeginGameState(EGameState.GameState_Playing);
					this.m_pPlayer.Go();
				}
			}
			if (this.m_Tutorial == ETutorial.Tutorial_Jumping) {
				float num7 = (this.GetScreenWidth() / 4f)
						+ this.m_MsgSmallJumpSprite.GetHalfSizeX();
				this.m_MsgSmallJumpSprite
						.SetPositionX((this.GetScreenWidth() * 0.25f)
								- (num7 * (1f - (this.m_fScreenFaderAlpha * 2f))));
				this.m_MsgBigJumpSprite
						.SetPositionX((this.GetScreenWidth() * 0.75f)
								+ (num7 * (1f - (this.m_fScreenFaderAlpha * 2f))));
			} else if (this.m_Tutorial == ETutorial.Tutorial_BouncePad) {
				float num8 = (this.GetScreenWidth() * 0.5f)
						+ this.m_MsgBouncePadSprite.GetHalfSizeX();
				if (this.m_MsgMoveState == EItemMoveState.ItemMoveState_GoOut) {
					this.m_MsgBouncePadSprite.SetPositionX((this
							.GetScreenWidth() * 0.5f)
							+ (num8 * (1f - (this.m_fScreenFaderAlpha * 2f))));
				} else {
					this.m_MsgBouncePadSprite.SetPositionX((this
							.GetScreenWidth() * 0.5f)
							- (num8 * (1f - (this.m_fScreenFaderAlpha * 2f))));
				}
			}
		}

	}

	private void PauseGame(boolean bPause) {
		this.m_bGamePaused = bPause;
		if (this.m_bGamePaused) {
			this.m_fScreenFaderAlpha = 0f;
			this.m_fScreenFaderAlphaDir = 1f;
			this.GoToMenu(EMenu.Menu_Paused, true);
		}
	}

	private void PreparePlaySelectedLevel() {
		this.m_bGamePaused = false;
		this.PrepareWorldBackground();
		if (this.m_iCurrentLevel > 1) {
			this.m_iScoreToCurrentLevel = this.GetWorldScore(
					this.m_iCurrentWorld, this.m_iCurrentLevel - 1);
		} else {
			this.m_iScoreToCurrentLevel = 0;
		}
		this.ResetLevel(false);
		this.m_pLevelMaker.CreateLevel(this.m_iCurrentLevel,
				this.m_iCurrentWorld);
	}

	private void PrepareWorldBackground() {
		if (this.m_iCurrentWorld != this.m_iBgForWorld) {
			this.UnloadWorldBackground();
			if (this.m_iCurrentWorld == 0) {
				this.m_BgSpriteAnim = this.m_Bg1SpriteAnim;
			} else if (this.m_iCurrentWorld == 1) {
				this.m_BgSpriteAnim = this.m_Bg2SpriteAnim;
			} else if (this.m_iCurrentWorld == 2) {
				this.m_BgSpriteAnim = this.m_Bg3SpriteAnim;
			}
		}
		this.m_iBgForWorld = this.m_iCurrentWorld;
		this.m_iBgCur = 1;
		this.m_fBgPosX = 0f;
		this.m_BgSpriteAnim.Reload();
	}

	private void ReleaseBouncePad(BouncePad pBouncePad) {
		pBouncePad.inuse = false;
		this.m_BouncePadPool.Release(pBouncePad);
	}

	private void ReleaseForceField(ForceField pForceField) {
		pForceField.inuse = false;
		this.m_ForceFieldPool.Release(pForceField);
	}

	private void ReleaseObstacle(Obstacle pObstacle) {
		pObstacle.inuse = false;
		this.m_ObstaclePool.Release(pObstacle);
	}

	private void ReleaseSpeedChange(SpeedChange pSpeedChange) {
		pSpeedChange.inuse = false;
		this.m_SpeedChangePool.Release(pSpeedChange);
	}

	private void ReleaseStar(Star pStar) {
		pStar.inuse = false;
		this.m_StarPool.Release(pStar);
	}

	private void RenderGame(SpriteBatch batch) {
		this.RenderLevelBackground(batch);
		for (Entity entity : this.m_ObstaclePool.GetUsedList()) {
			entity.Render(batch);
		}
		for (Entity entity2 : this.m_SpeedChangePool.GetUsedList()) {
			entity2.Render(batch);
		}
		for (Entity entity3 : this.m_BouncePadPool.GetUsedList()) {
			entity3.Render(batch);
		}
		for (Entity entity4 : this.m_ForceFieldPool.GetUsedList()) {
			entity4.Render(batch);
		}
		for (Entity entity5 : this.m_StarPool.GetUsedList()) {
			entity5.Render(batch);
		}
		this.m_pGround.Render(batch);
		this.m_pPlayer.Render(batch);
		this.RenderHUD(batch);
	}

	private void RenderHUD(SpriteBatch batch) {
		this.m_PauseSprite.Render(batch);
		if (this.m_GameState == EGameState.GameState_PlayFadeIn) {
			this.m_MsgTapToStartSprite.Render(batch);
		} else if (this.m_GameState == EGameState.GameState_LevelComplete) {
			this.m_MsgLevelCompleteSprite.Render(batch);
		} else if (this.m_GameState == EGameState.GameState_WorldComplete) {
			this.m_MsgWorldCompletedSprite.Render(batch);
		}
		this.m_pDefaultFont.SetColor(1f, 1f, 1f, 1f);
		float textHeight = this.m_pDefaultFont.GetTextHeight(1.5f * this
				.GetDeviceUnitScale());
		String text = "SCORE: "
				+ ((this.m_iScoreToCurrentLevel + "" + this.m_iCurrentLevelScore));
		this.m_pDefaultFont.Print((int) (5f * this.GetDeviceUnitScale()),
				(int) (2f * this.GetDeviceUnitScale()),
				1.5f * this.GetDeviceUnitScale(),
				1.5f * this.GetDeviceUnitScale(), text);
		text = "LEVEL: " + (new Integer(this.m_iCurrentLevel)).toString();
		this.m_pDefaultFont.Print((float) (5f * this.GetDeviceUnitScale()),
				(float) ((2f * this.GetDeviceUnitScale()) + textHeight),
				1.5f * this.GetDeviceUnitScale(),
				1.5f * this.GetDeviceUnitScale(), text);
		text = "TOKENS: "
				+ (new Integer(this.m_iWorldTokens[this.m_iCurrentWorld]))
						.toString();
		this.m_pDefaultFont.Print((float) (5f * this.GetDeviceUnitScale()),
				(float) ((2f * this.GetDeviceUnitScale()) + (textHeight * 2f)),
				1.5f * this.GetDeviceUnitScale(),
				1.5f * this.GetDeviceUnitScale(), text);
	}

	private void RenderLevelBackground(SpriteBatch batch) {
		float num = this.GetScreenHeight() - this.m_BgSpriteAnim.GetHalfSizeY();
		float num2 = 0f;
		float screenWidth = this.GetScreenWidth();
		float sizeX = this.m_BgSpriteAnim.GetSizeX();
		if ((this.m_fBgPosX + (this.m_iBgCur * sizeX)) < num2) {
			this.m_iBgCur++;
		}
		if (this.m_iBgCur > this.m_iBgNum) {
			this.m_fBgPosX += this.m_iBgNum * sizeX;
			this.m_iBgCur = 1;
		}
		int iBgCur = this.m_iBgCur;
		do {
			float num6 = this.m_fBgPosX + ((iBgCur - 1) * sizeX);
			this.m_BgSpriteAnim.SetPositionX(num6
					+ this.m_BgSpriteAnim.GetHalfSizeX());
			this.m_BgSpriteAnim.SetPositionY(num + (this.m_fGroundDeltaY / 2f));
			this.m_BgSpriteAnim.SetCurrentFrame(iBgCur - 1, false);
			this.m_BgSpriteAnim.Render(batch);
			if ((this.m_fBgPosX + (iBgCur * sizeX)) > screenWidth) {
				break;
			}
			iBgCur++;
		} while (iBgCur <= this.m_iBgNum);
		if ((this.m_fBgPosX + (this.m_iBgNum * sizeX)) >= screenWidth) {
			return;
		}
		iBgCur = 1;
		while (true) {
			float num7 = this.m_fBgPosX
					+ (((this.m_iBgNum + iBgCur) - 1) * sizeX);
			this.m_BgSpriteAnim.SetPositionX(num7
					+ this.m_BgSpriteAnim.GetHalfSizeX());
			this.m_BgSpriteAnim.SetPositionY(num + (this.m_fGroundDeltaY / 2f));
			this.m_BgSpriteAnim.SetCurrentFrame(iBgCur - 1, false);
			this.m_BgSpriteAnim.Render(batch);
			if ((this.m_fBgPosX + ((this.m_iBgNum + iBgCur) * sizeX)) > screenWidth) {
				return;
			}
			iBgCur++;
		}
	}

	private RectBox rect = new RectBox();

	private void RenderTutorial(SpriteBatch batch) {
		if (this.m_Tutorial == ETutorial.Tutorial_Jumping) {
			this.m_MsgSmallJumpSprite.Render(batch);
			this.m_MsgBigJumpSprite.Render(batch);
			
			batch.submit();
			
			float a = this.m_fScreenFaderAlpha * 2f;
			float num4 = 1.4f * this.GetDeviceUnitScale();
			float num5 = this.GetScreenWidth() * 0.25f;
			float num6 = this.GetScreenWidth() * 0.75f;
			this.m_pDefaultFont.SetColor(1f, 1f, 1f, a);
			float y = this.m_MsgSmallJumpSprite.GetPositionY()
					+ this.m_MsgSmallJumpSprite.GetHalfSizeY();
			float textWidth = this.m_pDefaultFont.GetTextWidth("tap the left",
					num4);
			this.m_pDefaultFont.Print(num5 - (textWidth * 0.5f), y, num4, num4,
					"tap the left");
			textWidth = this.m_pDefaultFont.GetTextWidth("tap the right", num4);
			this.m_pDefaultFont.Print(num6 - (textWidth * 0.5f), y, num4, num4,
					"tap the right");
			y += this.m_pDefaultFont.GetTextHeight(num4);
			textWidth = this.m_pDefaultFont.GetTextWidth("half of the screen",
					num4);
			this.m_pDefaultFont.Print(num5 - (textWidth * 0.5f), y, num4, num4,
					"half of the screen");
			this.m_pDefaultFont.Print(num6 - (textWidth * 0.5f), y, num4, num4,
					"half of the screen");
			y += this.m_pDefaultFont.GetTextHeight(num4);
			textWidth = this.m_pDefaultFont.GetTextWidth("for a small jump",
					num4);
			this.m_pDefaultFont.Print(num5 - (textWidth * 0.5f), y, num4, num4,
					"for a small jump");
			textWidth = this.m_pDefaultFont
					.GetTextWidth("for a big jump", num4);
			this.m_pDefaultFont.Print(num6 - (textWidth * 0.5f), y, num4, num4,
					"for a big jump");

			rect.setBounds(
					(float) ((int) ((this.GetScreenWidth() * 0.5f) - (5f * this
							.GetDeviceUnitScale()))), 0f, 5f * this
							.GetDeviceUnitScale(), this.GetScreenHeight());

			this.drawRectangle(batch,rect, 0.9f, 0.8f, 0f, a);

		} else if (this.m_Tutorial == ETutorial.Tutorial_BouncePad) {
			this.m_MsgBouncePadSprite.Render(batch);
			float num9 = this.m_fScreenFaderAlpha * 2f;
			float num10 = 1.5f * this.GetDeviceUnitScale();
			float num11 = this.GetScreenWidth() * 0.5f;
			this.m_pDefaultFont.SetColor(1f, 1f, 1f, num9);
			float fGroundPosY = this.m_fGroundPosY;
			float num8 = this.m_pDefaultFont.GetTextWidth(
					"jump on the bounce pads", num10);
			this.m_pDefaultFont.Print(num11 - (num8 * 0.5f), fGroundPosY,
					num10, num10, "jump on the bounce pads");
			fGroundPosY += this.m_pDefaultFont.GetTextHeight(num10);
			num8 = this.m_pDefaultFont
					.GetTextWidth("to bounce yourself", num10);
			this.m_pDefaultFont.Print(num11 - (num8 * 0.5f), fGroundPosY,
					num10, num10, "to bounce yourself");
			this.m_pDefaultFont.SetColor(1f, 1f, 1f, 1f);
		}
	}

	private void RenderUI(SpriteBatch batch) {
		if ((this.m_GameState == EGameState.GameState_Menu)
				|| (this.m_GameState == EGameState.GameState_PlaySelectedLevel)) {
			this.m_MenuBgSprite.Render(batch);
		}
		if (((this.m_ActiveMenu == EMenu.Menu_Main) || (this.m_ActiveMenu == EMenu.Menu_Options))
				|| (((this.m_ActiveMenu == EMenu.Menu_ChooseSocialGaming) || (this.m_ActiveMenu == EMenu.Menu_SelectWorld)) || (this.m_ActiveMenu == EMenu.Menu_Credits))) {
			this.m_MainMenuBoxesSprite.Render(batch);
		}
		if (this.m_TitleMoveState != EItemMoveState.ItemMoveState_Out) {
			this.m_TitleCloudSprite.Render(batch);
			this.m_TitleCrazyLittleSprite.Render(batch);
			this.m_TitleJumperSprite.Render(batch);
		}
		if (this.m_MenuCharacterMoveState != EItemMoveState.ItemMoveState_Out) {
			this.m_MenuCharacterSprite.Render(batch);
		}
		if (this.m_ActiveMenu == EMenu.Menu_Main) {
			this.m_BtnPlaySprite.Render(batch);
			this.m_BtnOptionsSprite.Render(batch);
			this.m_BtnMoreGamesSprite.Render(batch);
		} else if (this.m_ActiveMenu == EMenu.Menu_Options) {
			this.m_BtnSoundsSprite.Render(batch);
			this.m_BtnMusicSprite.Render(batch);
			this.m_BtnBackSprite.Render(batch);
		} else if (this.m_ActiveMenu == EMenu.Menu_Credits) {
			this.m_CreditsBgSprite.Render(batch);
		} else if (this.m_ActiveMenu == EMenu.Menu_SelectWorld) {
			this.m_BtnWorld1Sprite.Render(batch);
			this.m_BtnWorld2Sprite.Render(batch);
			this.m_BtnWorld3Sprite.Render(batch);
			this.m_BtnBackSprite.Render(batch);
		} else if (this.m_ActiveMenu == EMenu.Menu_SelectLevel) {
			for (int i = 0; i < 0x15; i++) {
				float num2 = 0F;
				float num3 = 0F;
				RefObject<Float> tempRef_num2 = new RefObject<Float>(num2);
				RefObject<Float> tempRef_num3 = new RefObject<Float>(num3);
				this.GetSelectLevelButtonPosition(i + 1, tempRef_num2,
						tempRef_num3);
				num2 = tempRef_num2.argvalue;
				num3 = tempRef_num3.argvalue;
				this.m_SelLevelItemSprite.SetPosition(num2, num3);
				this.m_SelLevelItemSprite.SetCurrentFrame(
						this.m_iLevelState[this.m_iCurrentWorld][i], false);
				this.m_SelLevelItemSprite.Render(batch);
				if (this.m_iLevelState[this.m_iCurrentWorld][i] != 5) {
					String text = (new Integer(i + 1)).toString();
					float textWidth = this.m_pDefaultFont.GetTextWidth(text,
							2f * this.GetDeviceUnitScale());
					float textHeight = this.m_pDefaultFont
							.GetTextHeight(2f * this.GetDeviceUnitScale());
					this.m_pDefaultFont.Print(
							(float) (num2 - (textWidth * 0.5f)),
							(float) (num3 - (textHeight * 0.75f)),
							2f * this.GetDeviceUnitScale(),
							2f * this.GetDeviceUnitScale(), text);
				}
			}
			this.m_BtnBackSprite.Render(batch);
		} else if (this.m_ActiveMenu == EMenu.Menu_Paused) {
			this.m_BtnResumeSprite.Render(batch);
			this.m_BtnQuitToMenuSprite.Render(batch);
			if (this.m_iWorldTokens[this.m_iCurrentWorld] > 0) {
				this.m_BtnUseTokenSprite.Render(batch);
			}
		} else if (this.m_ActiveMenu == EMenu.Menu_UseToken) {
			this.m_BtnYesSprite.Render(batch);
			this.m_BtnNoSprite.Render(batch);
			float num6 = this.GetScreenHeight() * 0.2f;
			float num7 = this.m_fMenuButtonsStartPosY
					- this.m_fMenuButtonsDestPosY;
			float num8 = this.m_fMenuButtonsFirstPosY
					- this.m_fMenuButtonsDestPosY;
			float a = 1f - (num8 / num7);
			float num10 = 2.1f * this.GetDeviceUnitScale();
			this.m_pDefaultFont.SetColor(1f, 1f, 1f, a);
			this.m_pDefaultFont.PrintCentered((int) this.GetScreenWidth(),
					(int) num6, num10, num10, "are you sure");
			num6 += this.m_pDefaultFont.GetTextHeight(num10);
			this.m_pDefaultFont.PrintCentered((int) this.GetScreenWidth(),
					(int) num6, num10, num10, "you want to use a token");
			num6 += this.m_pDefaultFont.GetTextHeight(num10);
			this.m_pDefaultFont.PrintCentered((int) this.GetScreenWidth(),
					(int) num6, num10, num10, "to skip the current level?");
			this.m_pDefaultFont.SetColor(1f, 1f, 1f, 1f);
		}
	}

	private void ResetLevel(boolean bRestart) {
		this.m_bSkipLevel = false;
		this.m_iCurrentLevelScore = 0;
		this.m_iCurrentLevelTotalStars = 0;
		this.m_iCurrentLevelCollectedStars = 0;
		this.m_fGroundDeltaY = 0f;
		float num = 0f;
		float num2 = (this.GetUnitBlockSize() * 2f) / 20f;
		if (this.m_iCurrentWorld == 0) {
			num = this.GetUnitBlockSize() * 5f;
		} else if (this.m_iCurrentWorld == 1) {
			num = this.GetUnitBlockSize() * 5.5f;
		} else if (this.m_iCurrentWorld == 2) {
			num = this.GetUnitBlockSize() * 6f;
		}
		this.m_fGameSpeed = num + (num2 * this.m_iCurrentLevel);
		this.m_pGround.Reset();
		if (bRestart) {
			this.m_pPlayer.Restart();
		} else {
			this.m_pPlayer.Reset();
		}
		this.m_ObstaclePool.Release();
		this.m_StarPool.Release();
		this.m_SpeedChangePool.Release();
		this.m_ForceFieldPool.Release();
		this.m_BouncePadPool.Release();
	}

	private void RestartLevel() {
		this.ResetLevel(true);
		this.m_pLevelMaker.CreateLevel(this.m_iCurrentLevel,
				this.m_iCurrentWorld);
	}

	public final void SetGroundDeltaY(float fDeltaY) {
		this.m_fGroundDeltaY = fDeltaY;
	}

	private java.util.ArrayList<Obstacle> Obscache = new ArrayList<Obstacle>(10);

	public void ResetObstacle(ObjectPool<Obstacle> pool) {
		java.util.ArrayList<Obstacle> result = pool.GetUsedList();
		if (result.size() > 0) {

			for (Obstacle obstacle : pool.GetUsedList()) {
				if (!obstacle.Tick(this.m_iDeltaTimeMS)) {
					Obscache.add(obstacle);
				}
			}
			for (Obstacle obstacle2 : Obscache) {
				this.ReleaseObstacle(obstacle2);
			}
			Obscache.clear();
		}
	}

	private java.util.ArrayList<Star> Starcache = new ArrayList<Star>(10);

	public void ResetStar(ObjectPool<Star> pool) {
		java.util.ArrayList<Star> result = pool.GetUsedList();
		if (result.size() > 0) {

			for (Star star : pool.GetUsedList()) {
				if (!star.Tick(this.m_iDeltaTimeMS)) {
					Starcache.add(star);
				}
			}
			for (Star star2 : Starcache) {
				this.ReleaseStar(star2);
			}
			Starcache.clear();
		}
	}

	private java.util.ArrayList<SpeedChange> SpeedChangecache = new ArrayList<SpeedChange>(
			10);

	public void ResetSpeedChange(ObjectPool<SpeedChange> pool) {
		java.util.ArrayList<SpeedChange> result = pool.GetUsedList();
		if (result.size() > 0) {
			for (SpeedChange speedChange : pool.GetUsedList()) {
				if (!speedChange.Tick(this.m_iDeltaTimeMS)) {
					SpeedChangecache.add(speedChange);
				}
			}
			for (SpeedChange speedChange2 : SpeedChangecache) {
				this.ReleaseSpeedChange(speedChange2);
			}
			SpeedChangecache.clear();
		}
	}

	private java.util.ArrayList<ForceField> ForceFieldcache = new ArrayList<ForceField>(
			10);

	public void ResetForceField(ObjectPool<ForceField> pool) {
		java.util.ArrayList<ForceField> result = pool.GetUsedList();
		if (result.size() > 0) {
			for (ForceField forceField : pool.GetUsedList()) {
				if (!forceField.Tick(this.m_iDeltaTimeMS)) {
					ForceFieldcache.add(forceField);
				}
			}
			for (ForceField forceField2 : ForceFieldcache) {
				this.ReleaseForceField(forceField2);
			}
			ForceFieldcache.clear();
		}
	}

	private java.util.ArrayList<BouncePad> BouncePadcache = new ArrayList<BouncePad>(
			10);

	public void ResetBouncePad(ObjectPool<BouncePad> pool) {
		java.util.ArrayList<BouncePad> result = pool.GetUsedList();
		if (result.size() > 0) {
			for (BouncePad bouncePad : pool.GetUsedList()) {
				if (!bouncePad.Tick(this.m_iDeltaTimeMS)) {
					BouncePadcache.add(bouncePad);
				}
			}
			for (BouncePad bouncePad2 : BouncePadcache) {
				this.ReleaseBouncePad(bouncePad2);
			}
			BouncePadcache.clear();
		}
	}

	private void TickGame() {
		if (!this.IsGamePaused()) {
			if (!this.m_pPlayer.IsCollided() && !this.m_pPlayer.IsLevelFailed()) {
				this.m_pGround.Tick(this.m_iDeltaTimeMS);
			}
			this.m_pPlayer.Tick(this.m_iDeltaTimeMS);

			ResetObstacle(this.m_ObstaclePool);
			ResetStar(this.m_StarPool);
			ResetSpeedChange(this.m_SpeedChangePool);
			ResetForceField(this.m_ForceFieldPool);
			ResetBouncePad(this.m_BouncePadPool);

			float cRadius = this.m_pPlayer.GetCollSizeX() * 0.5f;
			float positionX = this.m_pPlayer.GetPositionX();
			float positionY = this.m_pPlayer.GetPositionY();
			if (((!this.m_pPlayer.IsFallingOut() && !this.m_pPlayer
					.IsCollided()) && (!this.m_pPlayer.IsLevelFailed() && (this.m_GameState != EGameState.GameState_LevelComplete)))
					&& ((this.m_GameState != EGameState.GameState_WorldComplete) && (this.m_GameState != EGameState.GameState_Tutorial))) {
				float collPosX;
				float num5;
				float collPosY;
				float num7;
				for (Entity entity : this.m_ObstaclePool.GetUsedList()) {
					collPosX = entity.GetCollPosX();
					num5 = entity.GetCollPosX() + entity.GetCollSizeX();
					collPosY = entity.GetCollPosY();
					num7 = entity.GetCollPosY() + entity.GetCollSizeY();
					if (entity.CanCollide()
							&& Tools.isCircleIntersectingRect(positionX,
									positionY, cRadius, collPosX, num5,
									collPosY, num7)) {
						this.OnCollided();
						this.m_pPlayer.CollidedWithObstacle();
						return;
					}
				}
				for (Entity entity2 : this.m_StarPool.GetUsedList()) {
					collPosX = entity2.GetCollPosX();
					num5 = entity2.GetCollPosX() + entity2.GetCollSizeX();
					collPosY = entity2.GetCollPosY();
					num7 = entity2.GetCollPosY() + entity2.GetCollSizeY();
					if (entity2.CanCollide()
							&& Tools.isCircleIntersectingRect(positionX,
									positionY, cRadius, collPosX, num5,
									collPosY, num7)) {
						((Star) entity2).PickUp();
					}
				}
				for (Entity entity3 : this.m_SpeedChangePool.GetUsedList()) {
					collPosX = entity3.GetCollPosX();
					num5 = entity3.GetCollPosX() + entity3.GetCollSizeX();
					collPosY = entity3.GetCollPosY();
					num7 = entity3.GetCollPosY() + entity3.GetCollSizeY();
					if (entity3.CanCollide()
							&& Tools.isCircleIntersectingRect(positionX,
									positionY, cRadius, collPosX, num5,
									collPosY, num7)) {
						((SpeedChange) entity3).ChangeGameSpeed();
					}
				}
				ForceField pForceField = null;
				ForceField inForceField = this.m_pPlayer.GetInForceField();
				for (Entity entity4 : this.m_ForceFieldPool.GetUsedList()) {
					collPosX = entity4.GetCollPosX();
					num5 = entity4.GetCollPosX() + entity4.GetCollSizeX();
					collPosY = entity4.GetCollPosY();
					num7 = entity4.GetCollPosY() + entity4.GetCollSizeY();
					if (entity4.CanCollide()
							&& Tools.isCircleIntersectingRect(positionX,
									positionY, cRadius * 0.25f, collPosX, num5,
									collPosY, num7)) {
						pForceField = (ForceField) entity4;
					}
				}
				if (inForceField != pForceField) {
					if (!this.m_pPlayer.IsInForceField()
							&& (pForceField != null)) {
						this.m_pPlayer.EnterForceField(pForceField);
						this.OnForceFieldEnter();
					} else if (this.m_pPlayer.IsInForceField()
							&& (pForceField == null)) {
						this.m_pPlayer.ExitForceField();
					}
				}
				for (Entity entity5 : this.m_BouncePadPool.GetUsedList()) {
					collPosX = entity5.GetCollPosX();
					num5 = entity5.GetCollPosX() + entity5.GetCollSizeX();
					collPosY = entity5.GetCollPosY();
					num7 = entity5.GetCollPosY() + entity5.GetCollSizeY();
					if (entity5.CanCollide()
							&& Tools.isCircleIntersectingRect(positionX,
									positionY, cRadius, collPosX, num5,
									collPosY, num7)) {
						if (((BouncePad) entity5).CanBounce(this.m_pPlayer)) {
							if (this.m_pPlayer.IsBouncing()
									&& (this.m_pPlayer.GetBouncedByPad() == entity5)) {
								continue;
							}
							this.OnBounce();
							this.m_pPlayer.Bounce((BouncePad) entity5);
							break;
						}
						this.OnCollided();
						this.m_pPlayer.CollidedWithObstacle();
						return;
					}
				}
			}
			if (this.m_pPlayer.IsMoving()
					&& this.m_pGround.IsHoleBelow(positionX)) {
				this.m_pPlayer.StartFallingOut();
			} else if (this.m_pPlayer.IsFallingOut()
					&& (this.m_GameState != EGameState.GameState_LevelFailed)) {
				if ((positionY - cRadius) >= this.GetScreenHeight()) {
					this.LevelFailed();
					return;
				}
				if (this.m_pGround.IsCollidingWithGroundAtRightSide(positionX,
						positionY, cRadius)) {
					this.OnCollided();
					this.LevelFailed();
					return;
				}
			}
			if (((this.m_pPlayer.IsMovingOut() && (this.m_GameState != EGameState.GameState_LevelComplete)) && ((this.m_GameState != EGameState.GameState_WorldComplete) && (this.m_GameState != EGameState.GameState_Tutorial)))
					&& ((positionX - cRadius) >= this.GetScreenWidth())) {
				this.m_pPlayer.LevelComplete();
				this.BeginGameState(EGameState.GameState_LevelComplete);
			} else {
				if (((!this.m_pPlayer.IsMovingOut() && !this.m_pPlayer
						.IsCollided()) && (!this.m_pPlayer.IsLevelFailed() && (this.m_GameState != EGameState.GameState_LevelComplete)))
						&& (((this.m_GameState != EGameState.GameState_WorldComplete) && (this.m_GameState != EGameState.GameState_PlayFadeIn)) && (this.m_GameState != EGameState.GameState_Tutorial))) {
					this.m_fBgPosX -= (this.GetGameSpeed() * 0.1f)
							* this.m_fDeltaTime;
				}
				if (this.m_bJumpSmall
						&& (this.m_iEngineRunningTimeMS > (this.m_iJumpSmallTime + 100))) {
					this.ClearJumpSmall();
				}
				if (this.m_bJumpBig
						&& (this.m_iEngineRunningTimeMS > (this.m_iJumpBigTime + 100))) {
					this.ClearJumpBig();
				}
			}
		}
	}

	private void TickUI() {
		if (this.m_MenuButtonsState == EItemMoveState.ItemMoveState_Out) {
			if (this.m_GameState == EGameState.GameState_Playing) {
				if (this.m_NextActiveMenu == EMenu.Menu_None) {
					this.PauseGame(false);
					if (this.m_bSkipLevel) {
						this.BeginGameState(EGameState.GameState_LevelComplete);
					}
				} else if ((this.m_NextActiveMenu == EMenu.Menu_UseToken)
						|| (this.m_NextActiveMenu == EMenu.Menu_Paused)) {
					this.GoToMenu(this.m_NextActiveMenu, true);
				} else {
					this.BeginGameState(EGameState.GameState_Menu);
				}
			} else if (this.m_NextActiveMenu != EMenu.Menu_None) {
				this.GoToMenu(this.m_NextActiveMenu, true);
			}
		}
		if (this.m_MenuButtonsState == EItemMoveState.ItemMoveState_GoIn) {
			if (this.m_ActiveMenu == EMenu.Menu_Credits) {
				this.m_fScreenFaderAlpha += this.m_fDeltaTime * 2f;
				if (this.m_fScreenFaderAlpha >= 1f) {
					this.m_fScreenFaderAlpha = 1f;
					this.m_MenuButtonsState = EItemMoveState.ItemMoveState_In;
				}
			} else {
				this.m_fMenuButtonsFirstPosY -= ((1250f * this
						.GetDeviceUnitScale()) * this.m_fDeltaTime) * 0.5f;
				if (this.m_fMenuButtonsFirstPosY <= this.m_fMenuButtonsDestPosY) {
					this.m_MenuButtonsState = EItemMoveState.ItemMoveState_In;
					this.m_fMenuButtonsFirstPosY = this.m_fMenuButtonsDestPosY;
				}
			}
		} else if (this.m_MenuButtonsState == EItemMoveState.ItemMoveState_GoOut) {
			if (this.m_ActiveMenu == EMenu.Menu_Credits) {
				this.m_fScreenFaderAlpha -= this.m_fDeltaTime * 2f;
				if (this.m_fScreenFaderAlpha <= 0f) {
					this.m_fScreenFaderAlpha = 0f;
					this.m_MenuButtonsState = EItemMoveState.ItemMoveState_Out;
				}
			} else {
				this.m_fMenuButtonsFirstPosY += ((1250f * this
						.GetDeviceUnitScale()) * this.m_fDeltaTime) * 0.5f;
				if (this.m_fMenuButtonsFirstPosY >= this.m_fMenuButtonsStartPosY) {
					this.m_MenuButtonsState = EItemMoveState.ItemMoveState_Out;
					this.m_fMenuButtonsFirstPosY = this.m_fMenuButtonsStartPosY;
				}
			}
		}
		if (this.m_ActiveMenu == EMenu.Menu_Main) {
			this.m_BtnPlaySprite.SetPositionY(this.m_fMenuButtonsFirstPosY);
			this.m_BtnOptionsSprite.SetPositionY(this.m_fMenuButtonsFirstPosY
					+ this.m_fMenuButtonsBetweenDist);
			this.m_BtnMoreGamesSprite.SetPositionY(this.m_fMenuButtonsFirstPosY
					+ (this.m_fMenuButtonsBetweenDist * 2f));
		} else if (this.m_ActiveMenu == EMenu.Menu_Options) {
			this.m_BtnSoundsSprite.SetPositionY(this.m_fMenuButtonsFirstPosY);
			this.m_BtnMusicSprite.SetPositionY(this.m_fMenuButtonsFirstPosY
					+ this.m_fMenuButtonsBetweenDist);
			this.m_BtnBackSprite.SetPositionY(this.m_fMenuButtonsFirstPosY
					+ this.m_fMenuBackBtnDistFromFirstBtn);
		} else if (this.m_ActiveMenu == EMenu.Menu_SelectWorld) {
			this.m_BtnWorld1Sprite.SetPositionY(this.m_fMenuButtonsFirstPosY);
			this.m_BtnWorld2Sprite.SetPositionY(this.m_fMenuButtonsFirstPosY
					+ this.m_fMenuButtonsBetweenDist);
			this.m_BtnWorld3Sprite.SetPositionY(this.m_fMenuButtonsFirstPosY
					+ (this.m_fMenuButtonsBetweenDist * 2f));
			this.m_BtnBackSprite.SetPositionY(this.m_fMenuButtonsFirstPosY
					+ this.m_fMenuBackBtnDistFromFirstBtn);
			float num = this.m_fMenuButtonsStartPosY
					- this.m_fMenuButtonsDestPosY;
			float num2 = this.m_fMenuButtonsStartPosY
					- this.m_fMenuButtonsFirstPosY;
			float fAlpha = num2 / num;
			if ((this.m_MenuButtonsState == EItemMoveState.ItemMoveState_GoOut)
					&& (this.m_NextActiveMenu == EMenu.Menu_SelectLevel)) {
				this.m_MainMenuBoxesSprite.SetColorAlpha(fAlpha);
			} else if ((this.m_MenuButtonsState == EItemMoveState.ItemMoveState_GoIn)
					&& (this.m_LastActiveMenu == EMenu.Menu_SelectLevel)) {
				this.m_MainMenuBoxesSprite.SetColorAlpha(fAlpha);
			}
		} else if (this.m_ActiveMenu == EMenu.Menu_SelectLevel) {
			this.m_BtnBackSprite.SetPositionY(this.m_fMenuButtonsFirstPosY
					+ this.m_fMenuBackBtnDistFromFirstBtn);
		} else if (this.m_ActiveMenu == EMenu.Menu_Credits) {
			this.m_CreditsBgSprite.SetColorAlpha(this.m_fScreenFaderAlpha);
		} else if (this.m_ActiveMenu == EMenu.Menu_Paused) {
			this.m_BtnResumeSprite.SetPositionY(this.m_fMenuButtonsFirstPosY);
			this.m_BtnQuitToMenuSprite
					.SetPositionY(this.m_fMenuButtonsFirstPosY
							+ this.m_fMenuButtonsBetweenDist);
			this.m_BtnUseTokenSprite.SetPositionY(this.m_fMenuButtonsFirstPosY
					+ (this.m_fMenuButtonsBetweenDist * 2f));
			float num4 = this.m_fMenuButtonsStartPosY
					- this.m_fMenuButtonsDestPosY;
			float num5 = this.m_fMenuButtonsStartPosY
					- this.m_fMenuButtonsFirstPosY;
			float num6 = num5 / num4;
			if (this.m_MenuButtonsState == EItemMoveState.ItemMoveState_GoIn) {
				if (this.m_LastActiveMenu != EMenu.Menu_UseToken) {
					this.m_fScreenFaderAlpha = 0.5f * num6;
				}
			} else if (this.m_MenuButtonsState == EItemMoveState.ItemMoveState_GoOut) {
				if (this.m_NextActiveMenu == EMenu.Menu_None) {
					this.m_fScreenFaderAlpha = 0.5f * num6;
				} else if (this.m_NextActiveMenu != EMenu.Menu_UseToken) {
					this.m_fScreenFaderAlpha = 0.5f + (0.5f * (1f - num6));
				}
			}
		} else if (this.m_ActiveMenu == EMenu.Menu_UseToken) {
			this.m_BtnYesSprite.SetPositionY(this.m_fMenuButtonsFirstPosY);
			this.m_BtnNoSprite.SetPositionY(this.m_fMenuButtonsFirstPosY
					+ this.m_fMenuButtonsBetweenDist);
		}
		if (this.m_TitleMoveState == EItemMoveState.ItemMoveState_GoIn) {
			this.m_fTitleMoveStateValue += this.m_fDeltaTime * 3f;
			if (this.m_fTitleMoveStateValue >= 1f) {
				this.m_fTitleMoveStateValue = 1f;
				this.BeginTitleMoveState(EItemMoveState.ItemMoveState_In);
			}
		} else if (this.m_TitleMoveState == EItemMoveState.ItemMoveState_GoOut) {
			this.m_fTitleMoveStateValue -= this.m_fDeltaTime * 3f;
			if (this.m_fTitleMoveStateValue <= 0f) {
				this.m_fTitleMoveStateValue = 0f;
				this.BeginTitleMoveState(EItemMoveState.ItemMoveState_Out);
			}
		}
		if (this.m_TitleMoveState != EItemMoveState.ItemMoveState_Out) {
			this.m_TitleCloudSprite.SetSize(this.m_fTitleCloudsMaxSize
					* this.m_fTitleMoveStateValue, this.m_fTitleCloudsMaxSize
					* this.m_fTitleMoveStateValue);
			float num7 = (this.GetScreenWidth() + this.m_TitleCrazyLittleSprite
					.GetHalfSizeX()) - this.m_TitleCloudSprite.GetPositionX();
			this.m_TitleCrazyLittleSprite.SetPositionX(this.m_TitleCloudSprite
					.GetPositionX()
					+ (num7 * (1f - this.m_fTitleMoveStateValue)));
			this.m_TitleJumperSprite.SetPositionX(this.m_TitleCloudSprite
					.GetPositionX()
					- (num7 * (1f - this.m_fTitleMoveStateValue)));
		}
		if (this.m_MenuCharacterMoveState == EItemMoveState.ItemMoveState_GoIn) {
			if (this.m_MenuCharacterState == EMenuCharacterState.MenuCharacterState_Jump) {
				this.m_fMenuCharacterMoveValue += this.m_fDeltaTime * 3f;
				if (this.m_fMenuCharacterMoveValue >= 1f) {
					this.m_fMenuCharacterMoveValue = 1f;
					this.m_MenuCharacterState = EMenuCharacterState.MenuCharacterState_Falling;
				}
				float num8 = this.m_fMenuCharacterJumpMaxPosY
						- this.m_fMenuCharacterStartPosY;
				this.m_MenuCharacterSprite
						.SetPositionY(this.m_fMenuCharacterStartPosY
								+ (num8 * this.m_fMenuCharacterMoveValue));
			} else if (this.m_MenuCharacterState == EMenuCharacterState.MenuCharacterState_Falling) {
				this.m_fMenuCharacterMoveValue -= this.m_fDeltaTime * 3f;
				if (this.m_fMenuCharacterMoveValue <= 0f) {
					this.m_fMenuCharacterMoveValue = 0f;
					this.m_MenuCharacterState = EMenuCharacterState.MenuCharacterState_Idle;
					this.BeginMenuCharacterMoveState(EItemMoveState.ItemMoveState_In);
				}
				float num9 = this.m_fMenuCharacterJumpMaxPosY
						- this.m_fMenuCharacterIdlePosY;
				this.m_MenuCharacterSprite
						.SetPositionY(this.m_fMenuCharacterIdlePosY
								+ (num9 * this.m_fMenuCharacterMoveValue));
			}
		} else if (this.m_MenuCharacterMoveState == EItemMoveState.ItemMoveState_GoOut) {
			if (this.m_MenuCharacterState == EMenuCharacterState.MenuCharacterState_Jump) {
				this.m_fMenuCharacterMoveValue += this.m_fDeltaTime * 3f;
				if (this.m_fMenuCharacterMoveValue >= 1f) {
					this.m_fMenuCharacterMoveValue = 1f;
					this.m_MenuCharacterState = EMenuCharacterState.MenuCharacterState_Falling;
				}
				float num10 = this.m_fMenuCharacterJumpMaxPosY
						- this.m_fMenuCharacterIdlePosY;
				this.m_MenuCharacterSprite
						.SetPositionY(this.m_fMenuCharacterIdlePosY
								+ (num10 * this.m_fMenuCharacterMoveValue));
			} else if (this.m_MenuCharacterState == EMenuCharacterState.MenuCharacterState_Falling) {
				this.m_fMenuCharacterMoveValue -= this.m_fDeltaTime * 3f;
				if (this.m_fMenuCharacterMoveValue <= 0f) {
					this.m_fMenuCharacterMoveValue = 0f;
					this.m_MenuCharacterState = EMenuCharacterState.MenuCharacterState_Idle;
					this.BeginMenuCharacterMoveState(EItemMoveState.ItemMoveState_Out);
				}
				float num11 = this.m_fMenuCharacterJumpMaxPosY
						- this.m_fMenuCharacterStartPosY;
				this.m_MenuCharacterSprite
						.SetPositionY(this.m_fMenuCharacterStartPosY
								+ (num11 * this.m_fMenuCharacterMoveValue));
			}
		}
	}

	protected void UnloadContent() {
	}

	private void UnloadWorldBackground() {
		if (this.m_BgSpriteAnim != null) {
			this.m_BgSpriteAnim.Unload();
		}
		this.m_iBgForWorld = -1;
		this.m_iBgCur = 0;
	}

	public enum EFlipTexture {
		FlipTexture_No, FlipTexture_Vertical, FlipTexture_Horizontal;

		public int getValue() {
			return this.ordinal();
		}

		public static EFlipTexture forValue(int value) {
			return values()[value];
		}
	}

	public enum EGameState {
		GameState_Logo, GameState_Menu, GameState_PlaySelectedLevel, GameState_PlayFadeIn, GameState_Playing, GameState_LevelFailed, GameState_LevelComplete, GameState_WorldComplete, GameState_Tutorial;

		public int getValue() {
			return this.ordinal();
		}

		public static EGameState forValue(int value) {
			return values()[value];
		}
	}

	public enum EHandednessType {
		Handedness_Right, Handedness_Left;

		public int getValue() {
			return this.ordinal();
		}

		public static EHandednessType forValue(int value) {
			return values()[value];
		}
	}

	public enum EItemMoveState {
		ItemMoveState_GoIn, ItemMoveState_GoOut, ItemMoveState_In, ItemMoveState_Out;

		public int getValue() {
			return this.ordinal();
		}

		public static EItemMoveState forValue(int value) {
			return values()[value];
		}
	}

	public enum ELevelState {
		LevelState_Stars0, LevelState_Stars1, LevelState_Stars2, LevelState_Stars3, LevelState_Skipped, LevelState_Locked;

		public int getValue() {
			return this.ordinal();
		}

		public static ELevelState forValue(int value) {
			return values()[value];
		}
	}

	public enum EMenu {
		Menu_None, Menu_Main, Menu_Options, Menu_ChooseSocialGaming, Menu_Credits, Menu_SelectWorld, Menu_SelectLevel, Menu_Paused, Menu_UseToken;

		public int getValue() {
			return this.ordinal();
		}

		public static EMenu forValue(int value) {
			return values()[value];
		}
	}

	public enum EMenuCharacterState {
		MenuCharacterState_Idle, MenuCharacterState_Jump, MenuCharacterState_Falling;

		public int getValue() {
			return this.ordinal();
		}

		public static EMenuCharacterState forValue(int value) {
			return values()[value];
		}
	}

	public enum ERotateImage {
		RotateImage_No, RotateImage_CW_90, RotateImage_CW_180, RotateImage_CW_270, RotateImage_CCW, RotateImage_FlipVert, RotateImage_FlipHorz;

		public int getValue() {
			return this.ordinal();
		}

		public static ERotateImage forValue(int value) {
			return values()[value];
		}
	}

	public enum EScreenOrientation {
		ScreenOrientation_Portrait, ScreenOrientation_Landscape;

		public int getValue() {
			return this.ordinal();
		}

		public static EScreenOrientation forValue(int value) {
			return values()[value];
		}
	}

	public enum ETutorial {
		Tutorial_Jumping, Tutorial_BouncePad;

		public int getValue() {
			return this.ordinal();
		}

		public static ETutorial forValue(int value) {
			return values()[value];
		}
	}

	@Override
	public void draw(SpriteBatch batch) {

		if (this.m_GameState == EGameState.GameState_Logo) {
			this.m_RTGLogoSprite.Render(batch);
			this.m_bLoadContentReady = true;
		} else if (this.m_GameState == EGameState.GameState_Menu) {
			this.RenderUI(batch);
		} else if (this.m_GameState == EGameState.GameState_PlaySelectedLevel) {
			this.RenderUI(batch);
		} else if (((this.m_GameState == EGameState.GameState_PlayFadeIn) || (this.m_GameState == EGameState.GameState_LevelFailed))
				|| ((this.m_GameState == EGameState.GameState_LevelComplete) || (this.m_GameState == EGameState.GameState_WorldComplete))) {
			this.RenderGame(batch);
			batch.submit();
			this.fadeBackBufferToBlack(batch,m_fScreenFaderAlpha);
		} else if (this.m_GameState == EGameState.GameState_Tutorial) {
			this.RenderGame(batch);
			batch.submit();
			this.fadeBackBufferToBlack(batch,m_fScreenFaderAlpha);
			this.RenderTutorial(batch);
		} else if (this.m_GameState == EGameState.GameState_Playing) {
			this.RenderGame(batch);
			if (this.IsGamePaused()) {
				batch.submit();
				this.fadeBackBufferToBlack(batch,m_fScreenFaderAlpha);
				this.RenderUI(batch);
			}
		}

	}

	@Override
	public void loadContent() {
		this.OnPreInit();
		this.OnUpdateGame();
	}

	@Override
	public void unloadContent() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pressed(GameTouch e) {

		if ((this.m_GameState != EGameState.GameState_Menu)
				|| (this.m_MenuButtonsState != EItemMoveState.ItemMoveState_In)) {
			if (this.m_GameState == EGameState.GameState_PlayFadeIn) {
				if ((this.m_fScreenFaderAlpha <= 0f)
						&& (this.m_MsgMoveState == EItemMoveState.ItemMoveState_In)) {
					this.m_MsgMoveState = EItemMoveState.ItemMoveState_GoOut;
				}
			} else if (this.m_GameState == EGameState.GameState_WorldComplete) {
				if (this.m_MsgMoveState == EItemMoveState.ItemMoveState_In) {
					this.m_MsgMoveState = EItemMoveState.ItemMoveState_GoOut;

				}
			} else if (this.m_GameState == EGameState.GameState_Tutorial) {
				if (this.m_MsgMoveState == EItemMoveState.ItemMoveState_In) {
					this.m_MsgMoveState = EItemMoveState.ItemMoveState_GoOut;
				}
			} else if ((this.m_GameState == EGameState.GameState_Playing)
					&& !this.IsGamePaused()) {
				float x = e.x();
				float y = e.y();
				if (this.IsBtnSpriteTouch(this.m_PauseSprite, x, y, false)) {

					this.PauseGame(true);
				} else if (x < (this.GetScreenWidth() / 2f)) {
					this.m_bJumpSmall = true;
					this.m_iJumpSmallTime = this.m_iEngineRunningTimeMS;
				} else {
					this.m_bJumpBig = true;
					this.m_iJumpBigTime = this.m_iEngineRunningTimeMS;
				}
			} else if (((this.m_GameState == EGameState.GameState_Playing) && this
					.IsGamePaused())
					&& (this.m_MenuButtonsState == EItemMoveState.ItemMoveState_In)) {
				float num10 = e.x();
				float num11 = e.y();
				if (this.m_ActiveMenu == EMenu.Menu_Paused) {
					if (this.IsBtnSpriteTouch(this.m_BtnResumeSprite, num10,
							num11, false)) {

						this.m_MenuButtonsState = EItemMoveState.ItemMoveState_GoOut;
					} else if (this.IsBtnSpriteTouch(
							this.m_BtnQuitToMenuSprite, num10, num11, false)) {

						this.GoToMenu(EMenu.Menu_Main, false);
					} else if ((this.m_iWorldTokens[this.m_iCurrentWorld] > 0)
							&& this.IsBtnSpriteTouch(this.m_BtnUseTokenSprite,
									num10, num11, false)) {

						this.GoToMenu(EMenu.Menu_UseToken, false);
					}
				} else if (this.m_ActiveMenu == EMenu.Menu_UseToken) {
					if (this.IsBtnSpriteTouch(this.m_BtnYesSprite, num10,
							num11, false)) {

						this.m_bSkipLevel = true;
						this.m_MenuButtonsState = EItemMoveState.ItemMoveState_GoOut;
					} else if (this.IsBtnSpriteTouch(this.m_BtnNoSprite, num10,
							num11, false)) {

						this.GoToMenu(EMenu.Menu_Paused, false);
					}
				}
			}
		} else {
			float num = e.x();
			float num2 = e.y();
			if (this.m_ActiveMenu == EMenu.Menu_Main) {
				if (this.IsBtnSpriteTouch(this.m_BtnPlaySprite, num, num2, true)) {

					this.GoToMenu(EMenu.Menu_SelectWorld, false);
				} else if (this.IsBtnSpriteTouch(this.m_BtnOptionsSprite, num,
						num2, true)) {

					this.GoToMenu(EMenu.Menu_Options, false);
				} else if (this.IsBtnSpriteTouch(this.m_BtnMoreGamesSprite,
						num, num2, true)) {

				}
			} else if (this.m_ActiveMenu == EMenu.Menu_Options) {
				if (this.IsBtnSpriteTouch(this.m_BtnSoundsSprite, num, num2,
						true)) {
					this.m_bSoundsOn = !this.m_bSoundsOn;
					if (this.m_bSoundsOn) {

						this.m_BtnSoundsSprite.SetCurrentFrame(1, false);
					} else {
						this.m_BtnSoundsSprite.SetCurrentFrame(0, false);
					}

				} else if (this.IsBtnSpriteTouch(this.m_BtnMusicSprite, num,
						num2, true)) {

					if (this.m_bMusicOn) {

						this.m_bMusicOn = false;
						this.m_BtnMusicSprite.SetCurrentFrame(0, false);
					} else {
						this.m_bMusicOn = true;

						this.m_BtnMusicSprite.SetCurrentFrame(1, false);
					}
				} else if (this.IsBtnSpriteTouch(this.m_BtnBackSprite, num,
						num2, true)) {

					this.GoToMenu(EMenu.Menu_Main, false);
				}
			} else if (this.m_ActiveMenu == EMenu.Menu_Credits) {

				if ((num > (this.GetScreenWidth() - (110f * this
						.GetDeviceUnitScale())))
						&& (num2 > (this.GetScreenHeight() - (110f * this
								.GetDeviceUnitScale())))) {

				} else {
					this.GoToMenu(this.m_LastActiveMenu, false);
				}
			} else if (this.m_ActiveMenu == EMenu.Menu_SelectWorld) {
				if (this.IsBtnSpriteTouch(this.m_BtnWorld1Sprite, num, num2,
						true)) {

					this.m_iCurrentWorld = 0;
					this.GoToMenu(EMenu.Menu_SelectLevel, false);
				} else if (this.IsBtnSpriteTouch(this.m_BtnWorld2Sprite, num,
						num2, true)) {
					if (this.m_bWorldUnlocked[1]) {

						this.m_iCurrentWorld = 1;
						this.GoToMenu(EMenu.Menu_SelectLevel, false);
					} else {

					}
				} else if (this.IsBtnSpriteTouch(this.m_BtnWorld3Sprite, num,
						num2, true)) {
					if (this.m_bWorldUnlocked[2]) {

						this.m_iCurrentWorld = 2;
						this.GoToMenu(EMenu.Menu_SelectLevel, false);
					} else {

					}
				} else if (this.IsBtnSpriteTouch(this.m_BtnBackSprite, num,
						num2, true)) {

					this.GoToMenu(EMenu.Menu_Main, false);
				}
			} else if (this.m_ActiveMenu == EMenu.Menu_SelectLevel) {
				if (this.IsBtnSpriteTouch(this.m_BtnBackSprite, num, num2, true)) {

					this.GoToMenu(EMenu.Menu_SelectWorld, false);
				} else {
					float num3 = 28f * this.GetDeviceUnitScale();
					float num4 = 38f * this.GetDeviceUnitScale();
					for (int i = 0; i < 0x15; i++) {
						float num6 = 0F;
						float num7 = 0F;
						RefObject<Float> tempRef_num6 = new RefObject<Float>(
								num6);
						RefObject<Float> tempRef_num7 = new RefObject<Float>(
								num7);
						this.GetSelectLevelButtonPosition(i + 1, tempRef_num6,
								tempRef_num7);
						num6 = tempRef_num6.argvalue;
						num7 = tempRef_num7.argvalue;
						if (((num > (num6 - num3)) && (num < (num6 + num3)))
								&& ((num2 > (num7 - num4)) && (num2 < (num7 + num4)))) {
							if (this.m_iLevelState[this.m_iCurrentWorld][i] != 5) {
								this.m_iCurrentLevel = i + 1;

								this.BeginGameState(EGameState.GameState_PlaySelectedLevel);
								break;
							}

						}
					}
				}
			}
			if (((this.m_ActiveMenu == EMenu.Menu_Main) || (this.m_ActiveMenu == EMenu.Menu_Options))
					|| ((this.m_ActiveMenu == EMenu.Menu_ChooseSocialGaming) || (this.m_ActiveMenu == EMenu.Menu_SelectWorld))) {
				if (this.IsTwitterTouch(num, num2)) {

				} else if (this.IsFacebookTouch(num, num2)) {

				} else if (this.IsRTGTouch(num, num2)) {

					this.GoToMenu(EMenu.Menu_Credits, false);
				}
			}
		}

	}

	@Override
	public void released(GameTouch e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void move(GameTouch e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drag(GameTouch e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void pressed(GameKey e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void released(GameKey e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(GameTime gameTime) {

		if (SysKey.isKeyPressed(SysKey.ESCAPE)) {
			if ((this.m_GameState == EGameState.GameState_Menu)
					&& (this.m_MenuButtonsState == EItemMoveState.ItemMoveState_In)) {
				if (this.m_ActiveMenu == EMenu.Menu_Main) {
					LSystem.exit();
				} else if (this.m_ActiveMenu == EMenu.Menu_Options) {

					this.GoToMenu(EMenu.Menu_Main, false);
				} else if (this.m_ActiveMenu == EMenu.Menu_Credits) {

					this.GoToMenu(this.m_LastActiveMenu, false);
				} else if (this.m_ActiveMenu == EMenu.Menu_SelectWorld) {

					this.GoToMenu(EMenu.Menu_Main, false);
				} else if (this.m_ActiveMenu == EMenu.Menu_SelectLevel) {

					this.GoToMenu(EMenu.Menu_SelectWorld, false);
				}
			} else if (this.m_GameState == EGameState.GameState_PlayFadeIn) {
				if (this.m_MsgMoveState == EItemMoveState.ItemMoveState_In) {
					this.m_MsgMoveState = EItemMoveState.ItemMoveState_GoOut;
				}
			} else if (this.m_GameState == EGameState.GameState_WorldComplete) {
				if (this.m_MsgMoveState == EItemMoveState.ItemMoveState_In) {
					this.m_MsgMoveState = EItemMoveState.ItemMoveState_GoOut;

				}
			} else if (this.m_GameState == EGameState.GameState_Tutorial) {
				if (this.m_MsgMoveState == EItemMoveState.ItemMoveState_In) {
					this.m_MsgMoveState = EItemMoveState.ItemMoveState_GoOut;
				}
			} else if ((this.m_GameState == EGameState.GameState_Playing)
					&& !this.IsGamePaused()) {

				this.PauseGame(true);
			} else if (((this.m_GameState == EGameState.GameState_Playing) && this
					.IsGamePaused())
					&& (this.m_MenuButtonsState == EItemMoveState.ItemMoveState_In)) {
				if (this.m_ActiveMenu == EMenu.Menu_Paused) {

					this.m_MenuButtonsState = EItemMoveState.ItemMoveState_GoOut;
				} else if (this.m_ActiveMenu == EMenu.Menu_UseToken) {

					this.GoToMenu(EMenu.Menu_Paused, false);
				}
			}
		}

		this.m_iDeltaTimeMS = (int) gameTime.getMilliseconds();
		this.m_fDeltaTime = (this.m_iDeltaTimeMS) / 1000f;
		this.m_iEngineRunningTimeMS += this.m_iDeltaTimeMS;
		this.OnUpdateGame();

	}

	public LTransition onTransition() {
		return LTransition.newFadeIn();
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
		// TODO Auto-generated method stub
		
	}

}