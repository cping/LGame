package org.test;

import java.util.Arrays;

import loon.LSystem;
import loon.Screen;
import loon.action.sprite.ImageBackground;
import loon.component.LClickButton;
import loon.component.LComponent;
import loon.component.LToast;
import loon.event.ClickListener;
import loon.event.GameTouch;
import loon.opengl.GLEx;
import loon.utils.ArrayMap;
import loon.utils.ArrayMap.Entry;
import loon.utils.ListMap;
import loon.utils.processes.RealtimeProcess;
import loon.utils.timer.LTimerContext;

public class GameView extends Screen {

	private static final int PIECE_WIDTH = 67, PIECE_HEIGHT = 67;
	private static final int SY_COE = 68, SX_COE = 68;
	private static final int SX_OFFSET = 50, SY_OFFSET = 15;
	private ListMap<String, LClickButton> pieceObjects = new ListMap<String, LClickButton>();
	private Board board;
	private String selectedPieceKey;

	private GameController controller;

	private LClickButton lblPlayer;

	public GameView() {

	}

	public void movePieceFromModel(String pieceKey, int[] to) {
		LClickButton pieceObject = pieceObjects.get(pieceKey);
		int[] sPos = modelToViewConverter(to);
		pieceObject.setLocation(sPos[0], sPos[1]);
		selectedPieceKey = null;
	}

	public void movePieceFromAI(String pieceKey, int[] to) {
		Piece inNewPos = board.getPiece(to);
		if (inNewPos != null) {
			remove(pieceObjects.get(inNewPos.key));
			pieceObjects.removeKey(inNewPos.key);
		}

		LClickButton pieceObject = pieceObjects.get(pieceKey);
		int[] sPos = modelToViewConverter(to);
		pieceObject.setLocation(sPos[0], sPos[1]);

		selectedPieceKey = null;
	}

	private int[] modelToViewConverter(int pos[]) {
		int sx = pos[1] * SX_COE + SX_OFFSET, sy = pos[0] * SY_COE + SY_OFFSET;
		return new int[] { sx, sy };
	}

	private int[] viewToModelConverter(int sPos[]) {
		int ADDITIONAL_SY_OFFSET = 25;
		int y = (sPos[0] - SX_OFFSET) / SX_COE, x = (sPos[1] - SY_OFFSET - ADDITIONAL_SY_OFFSET)
				/ SY_COE;
		return new int[] { x, y };
	}

	public void showPlayer(char player) {
		lblPlayer.setTexture("assets/" + player + ".png");

	}

	public void showWinner(char player) {
		String text = (player == 'r') ? "Red player has won!"
				: "Black player has won!";
		add(LToast.makeText(text));
	}

	@Override
	public void draw(GLEx g) {

	}

	@Override
	public void onLoad() {
		controller = new GameController();
		board = controller.playChess();

		add(new ImageBackground("assets/board.png"));

		lblPlayer = LClickButton.makePath("assets/r.png");
		lblPlayer.setLocation(10, 320);
		lblPlayer.setSize(PIECE_WIDTH, PIECE_HEIGHT);
		add(lblPlayer);

		ArrayMap pieces = board.pieces;
		for (int i=0;i<pieces.size();i++) {
			Entry e = pieces.getEntry(i);
			if (e != null) {
				String key = (String) e.getKey();
				int[] pos = ((Piece)(e.getValue())).position;
				int[] sPos = modelToViewConverter(pos);
				LClickButton lblPiece = LClickButton.makePath("assets/"
						+ key.substring(0, 2) + ".png");

				lblPiece.setLocation(sPos[0], sPos[1]);
				lblPiece.setSize(PIECE_WIDTH, PIECE_HEIGHT);
				lblPiece.SetClick(new PieceOnClickListener(key));
				pieceObjects.put(key, lblPiece);
				add(lblPiece);
			}
		}

		RealtimeProcess aiProcess = new RealtimeProcess() {

			@Override
			public void run(LTimerContext time) {

				if (controller.hasWin(board) == 'x') {
					showPlayer('r');
					// 用户走棋
					if (board.player == 'r') {
						return;
					}
					if (controller.hasWin(board) != 'x') {
						showWinner('r');
					}
					// ai走棋
					showPlayer('b');
					controller.responseMoveChess(board, GameView.this);
				} else {
					showWinner('b');
				}
			}
		};
		// 每秒刷新一次AI
		aiProcess.setDelay(LSystem.SECOND);
		addProcess(aiProcess);

	}

	@Override
	public void alter(LTimerContext timer) {

	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void touchDown(GameTouch e) {
		if (selectedPieceKey != null) {
			int[] sPos = new int[] { e.x(), e.y() };
			int[] pos = viewToModelConverter(sPos);
			int[] selectedPiecePos = ((Piece)board.pieces.get(selectedPieceKey)).position;
			for (int[] each : Rules.getNextMove(selectedPieceKey,
					selectedPiecePos, board)) {
				if (Arrays.equals(each, pos)) {
					controller.moveChess(selectedPieceKey, pos, board);
					movePieceFromModel(selectedPieceKey, pos);
					break;
				}
			}
		}
	}

	@Override
	public void touchUp(GameTouch e) {

	}

	@Override
	public void touchMove(GameTouch e) {

	}

	@Override
	public void touchDrag(GameTouch e) {

	}

	@Override
	public void resume() {

	}

	@Override
	public void pause() {

	}

	@Override
	public void close() {

	}

	class PieceOnClickListener implements ClickListener {
		private String key;

		PieceOnClickListener(String key) {
			this.key = key;
		}

		@Override
		public void DoClick(LComponent comp) {

		}

		@Override
		public void DownClick(LComponent comp, float x, float y) {

			if (selectedPieceKey != null && key.charAt(0) != board.player) {

				Piece p = (Piece) board.pieces.getValue(key);
				if (p == null) {
					return;
				}
				int[] pos = p.position;
				int[] selectedPiecePos = ((Piece)board.pieces.get(selectedPieceKey)).position;
				for (int[] each : Rules.getNextMove(selectedPieceKey,
						selectedPiecePos, board)) {
					if (Arrays.equals(each, pos)) {
						remove(pieceObjects.get(key));
						pieceObjects.removeKey(key);
						controller.moveChess(selectedPieceKey, pos, board);
						movePieceFromModel(selectedPieceKey, pos);
						break;
					}
				}
			} else if (key.charAt(0) == board.player) {
				selectedPieceKey = key;
			}

		}

		@Override
		public void UpClick(LComponent comp, float x, float y) {

		}

		@Override
		public void DragClick(LComponent comp, float x, float y) {

		}
	}
}
