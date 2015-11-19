package loon.stage;

import loon.LGame;
import loon.LProcess;
import loon.LSystem;
import loon.utils.TArray;
import loon.utils.reply.Closeable;
import loon.utils.reply.Port;
import loon.utils.timer.LTimerContext;

public class StageSystem extends PlayerUtils {

	protected final GroupPlayer _rootLayer;

	protected Controller _transitor;

	protected final TArray<Stage> _screens = new TArray<Stage>();

	public static final StageTransition DEF = new StageTransition() {
		public boolean update(Stage o, Stage n, float elapsed) {
			return true;
		}
	};

	public float originX = 0;

	public float originY = 0;

	protected void handleError(RuntimeException error) {
		LSystem.base().log().warn("Stage error", error);
	}

	public SlideTransition newSlide() {
		return new SlideTransition(this);
	}

	public static PageTransition pageTurn() {
		return new PageTransition();
	}

	public static FlipTransition flip() {
		return new FlipTransition();
	}

	public StageSystem(GroupPlayer rootLayer) {
		_rootLayer = rootLayer;
	}

	public void push(Stage stage) {
		push(stage, defaultPushTransition());
	}

	public void push(Stage stage, StageTransition trans) {
		if (_screens.isEmpty()) {
			addAndShow(stage);
		} else {
			final Stage otop = top();
			transition(new Controller(otop, stage, trans) {
				@Override
				protected void onComplete() {
					hide(otop);
				}
			});
		}
	}

	public void push(Iterable<? extends Stage> stages) {
		push(stages, defaultPushTransition());
	}

	public void push(Iterable<? extends Stage> stages, StageTransition trans) {
		if (!stages.iterator().hasNext()) {
			throw new IllegalArgumentException(
					"Cannot push empty list of stages.");
		}
		if (_screens.isEmpty()) {
			for (Stage stage : stages) {
				add(stage);
			}
			justShow(top());
		} else {
			final Stage otop = top();
			Stage last = null;
			for (Stage stage : stages) {
				if (last != null) {
					add(last);
				}
				last = stage;
			}
			transition(new Controller(otop, last, trans) {
				@Override
				protected void onComplete() {
					hide(otop);
				}
			});
		}
	}

	public void popTo(Stage newTopStage) {
		popTo(newTopStage, defaultPopTransition());
	}

	public void popTo(Stage newTopStage, StageTransition trans) {
		if (top() == newTopStage) {
			return;
		}
		while (_screens.size > 1 && _screens.get(1) != newTopStage) {
			justRemove(_screens.get(1));
		}
		remove(top(), trans);
	}

	public void replace(Stage stage) {
		replace(stage, defaultPushTransition());
	}

	public void replace(Stage stage, StageTransition trans) {
		if (_screens.isEmpty()) {
			addAndShow(stage);
		} else {
			final Stage otop = _screens.removeIndex(0);
			transition(new Controller(otop, stage, trans) {
				@Override
				protected void onComplete() {
					hide(otop);
					wasRemoved(otop);
				}
			});
		}
	}

	public boolean remove(Stage stage) {
		return remove(stage, defaultPopTransition());
	}

	public boolean remove(Stage stage, StageTransition trans) {
		if (top() != stage) {
			return justRemove(stage);
		}
		if (_screens.size > 1) {
			final Stage otop = _screens.removeIndex(0);
			transition(new UnController(otop, top(), trans) {
				@Override
				protected void onComplete() {
					hide(otop);
					wasRemoved(otop);
				}
			});
		} else {
			hide(stage);
			justRemove(stage);
		}
		return true;
	}

	public Stage top() {
		return _screens.isEmpty() ? null : _screens.get(0);
	}

	public boolean isTransiting() {
		return _transitor != null;
	}

	public int size() {
		return _screens.size;
	}

	protected StageTransition defaultPushTransition() {
		return DEF;
	}

	protected StageTransition defaultPopTransition() {
		return DEF;
	}

	protected void add(Stage stage) {
		if (_screens.contains(stage)) {
			throw new IllegalArgumentException(
					"Cannot add stage to stack twice.");
		}
		_screens.insert(0, stage);
		try {
			stage.onAdded();
		} catch (RuntimeException e) {
			handleError(e);
		}
	}

	protected void addAndShow(Stage stage) {
		add(stage);
		justShow(stage);
	}

	protected void justShow(Stage stage) {
		_rootLayer.addAt(stage.players, originX, originY);
		try {
			stage.wasShown();
		} catch (RuntimeException e) {
			handleError(e);
		}
	}

	protected void hide(Stage stage) {
		_rootLayer.remove(stage.players);
		try {
			stage.wasHidden();
		} catch (RuntimeException e) {
			handleError(e);
		}
	}

	protected boolean justRemove(Stage stage) {
		boolean removed = _screens.remove(stage);
		if (removed) {
			wasRemoved(stage);
		}
		return removed;
	}

	protected void wasRemoved(Stage stage) {
		try {
			stage.onRemoved();
		} catch (RuntimeException e) {
			handleError(e);
		}
	}

	protected void transition(Controller transitor) {
		if (_transitor != null) {
			_transitor.complete();
		}
		_transitor = transitor;
		_transitor.init();
	}

	protected int transSkipFrames() {
		return 0;
	}

	protected void setInputEnabled(boolean enabled) {
		LGame game = LSystem.base();
		if (game != null) {
			game.input().mouseEnabled = enabled;
			game.input().touchEnabled = enabled;
		}
	}

	protected class Controller {

		protected final Stage _oscreen, _nscreen;
		protected final StageTransition _trans;
		protected Closeable _onPaint = Closeable.Shutdown.DEF;
		protected int _skipFrames = transSkipFrames();
		protected float _elapsed;

		public Controller(Stage o, Stage n, StageTransition trans) {
			_oscreen = o;
			_nscreen = n;
			_trans = trans;
		}

		public void init() {
			_oscreen.onHideTransitionStarted();
			showNewScreen();
			_trans.init(_oscreen, _nscreen);
			setInputEnabled(false);
			if (_trans == DEF) {
				complete();
			} else
				_onPaint = LSystem.base().display().paint
						.connect(new Port<LTimerContext>() {
							public void onEmit(LTimerContext clock) {
								paint(clock);
							}
						});
		}

		public void paint(LTimerContext clock) {
			LProcess process = LSystem.getProcess();
			if (process != null && process.isScreenTransitionCompleted()) {
				if (_skipFrames > 0) {
					_skipFrames -= 1;
				} else {
					_elapsed += clock.timeSinceLastUpdate;
					if (_trans.update(_oscreen, _nscreen, _elapsed)) {
						complete();
					}
				}
			}
		}

		public void complete() {
			_transitor = null;
			_onPaint.close();
			setInputEnabled(true);
			_trans.complete(_oscreen, _nscreen);
			_nscreen.players.setTranslation(originX, originY);
			_nscreen.onShowTransitionCompleted();
			onComplete();
		}

		protected void showNewScreen() {
			addAndShow(_nscreen);
		}

		protected void onComplete() {
		}

	}

	protected class UnController extends Controller {
		public UnController(Stage o, Stage n, StageTransition trans) {
			super(o, n, trans);
		}

		@Override
		protected void showNewScreen() {
			justShow(_nscreen);
		}
	}

}
