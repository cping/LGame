package loon.nscripter;

import loon.LTexture;
import loon.canvas.LColor;
import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.nscripter.variables.Variable;
import loon.nscripter.variables.alias.NumVariable;
import loon.nscripter.variables.buttons.UserButton;
import loon.nscripter.variables.displaying.StyledChar;
import loon.nscripter.variables.displaying.TextPart;
import loon.nscripter.variables.effect.UserEffect;
import loon.nscripter.variables.functions.UserFunctionsVariables;
import loon.nscripter.variables.menubar.MenuItem;
import loon.nscripter.variables.procedures.Procedure;
import loon.nscripter.variables.procedures.SubroutineLevel;
import loon.nscripter.variables.sprites.Sprite;
import loon.utils.Array;
import loon.utils.ObjectMap;
import loon.utils.TArray;

public class Config {

	public static int GameStatus = 0;

	public static TArray<Variable> EngineVarList = new TArray<Variable>();

	public static Variable getEngineVarList(String name) {
		for (Variable v : EngineVarList) {
			if (v != null && name.equals(v.getName())) {
				return v;
			}
		}
		return null;
	}

	public static class UserVariables {
		public static ObjectMap<NumVariable, Integer> NumVarList = new ObjectMap<NumVariable, Integer>();
		public static ObjectMap<String, String> StringVarList = new ObjectMap<String, String>();
		public static ObjectMap<String, Integer[][]> ArrayVarList = new ObjectMap<String, Integer[][]>();
	}

	public static class Parsing {
		public static boolean Wait = false;

		public static TArray<String> ScriptText = new TArray<String>();
		public static int CurrentLine = 0;

		public static Array<SubroutineLevel> SubroutinesLevels = new Array<SubroutineLevel>();
	}

	public static class UserDefine {
		public static TArray<Procedure> ProceduresList = new TArray<Procedure>();

		public static TArray<UserFunctionsVariables> UserFunctionsVariablesList = new TArray<UserFunctionsVariables>();
		public static TArray<Integer> UserFunctionsVariablesIndexsList = new TArray<Integer>();
		public static int UserFunctionsLevel = 0;
	}

	public static class Drawing {
		public static boolean IsVideoPlaying = false;

		public static boolean IsCompatibilityOn = true;

		public static ObjectMap<Integer, UserEffect> UserEffectsList = new ObjectMap<Integer, UserEffect>();

		public static ObjectMap<Integer, Sprite> SpritesList = new ObjectMap<Integer, Sprite>();

		public static LTexture background = null;

		public static RectBox BackgroundRectangle = new RectBox(0, 0, 800, 480);
		public static float backgroundScale = 1f;

		public static LColor TintColor = new LColor(LColor.white);
	}

	public static class Delay {
		public static boolean Sleeping = false;

		public static boolean StartOnTouch = false;
		public static boolean GetTouch = false;

		public static int TimeToSleep = 0;
		public static int ElapsedTime = 0;
	}

	public static class SaveSystem {
		public static String SaveMenuTitle = "Save";
		public static String LoadMenuTitle = "Load";
		public static String SlotTitle = "Slot";

		public static int SaveNumber = 9;

		public static boolean IsSaveEnabled = false;
	}

	public static class RightMenu {
		public static ObjectMap<String, String> RightMenuList = new ObjectMap<String, String>();

		public static int TextFontWidth = 0;
		public static int TextFontHeighth = 0;
		public static int TextSpacingXh = 0;
		public static int TextSpacingYh = 0;
		public static boolean BoldFace = false;
		public static boolean DropShadow = false;
		public static LColor WindowColor = new LColor();

		public static LColor EmptySavefileColor = new LColor();
		public static LColor MouseoffColor = new LColor();
		public static LColor MouseoverColor = new LColor();

		public static boolean IsOn = false;
		public static Vector2f StartPosition = new Vector2f();
	}

	public static class Choices {
		public static LColor mouseoverColor = new LColor();
		public static LColor mouseoffColor = new LColor();
	}

	public static class TextWindow {
		public static UserEffect WindowEffect = new UserEffect();

		public static ObjectMap<Integer, String> FontsList = new ObjectMap<Integer, String>();

		public static RectBox TextRect = new RectBox(0, 0, 800, 480);

		public static boolean IsSmthChanged = false;

		public static boolean IsShowing = false;

		public static int TextShowMode = 1;

		public static TArray<StyledChar> CharMode1 = new TArray<StyledChar>();

		public static StyledChar[][] CharMode2 = null;
		public static Vector2f TextPointer = new Vector2f(0, 0);

		public static LColor TextColor = new LColor(LColor.white);

		public static boolean WaitForClick = false;
		public static int AfteClick = 0;

		public static boolean Skip = false;

		public static Sprite ClickWaitCursor = new Sprite();
		public static Vector2f ClickWaitCursorVector = new Vector2f();

		public static Vector2f PageWaitCursorVector = new Vector2f();
		public static Sprite PageWaitCursor = new Sprite();

		public static LTexture TextBackground = null;
		public static RectBox TextBackgroundRectangle = new RectBox();

		public static TArray<TextPart> TextParts = new TArray<TextPart>();
		public static Vector2f TextCurrentPosition = new Vector2f(0, 0);
		public static boolean WaitForClickPressed = false;
	}

	public static class UserButtons {
		public static LTexture ButtonsBuffer = null;

		public static ObjectMap<Integer, UserButton> ButtonsList = new ObjectMap<Integer, UserButton>();

		public static boolean WaitForPress = false;

		public static String WaitVariable = null;

		public static int TimeSpend = 0;
	}

	public static class TopMenu {
		public static TArray<MenuItem> MenuList = new TArray<MenuItem>();
	}

	public static class LogMode {
		public static Sprite PageUpAc = new Sprite();
		public static Sprite PageUpInAc = new Sprite();
		public static Sprite PageDownAc = new Sprite();
		public static Sprite PageDownInAc = new Sprite();

		public static LColor TextColor = new LColor();
	}

}
