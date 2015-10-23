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
package loon.android;

import loon.LObject;
import loon.event.InputMake;
import loon.event.KeyMake;
import loon.event.SysKey;
import loon.event.TouchMake;
import loon.geom.Vector2f;
import loon.utils.reply.GoFuture;
import loon.utils.reply.GoPromise;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.EditText;

public class AndroidInputMake extends InputMake {

	private final AndroidGame game;

	public AndroidInputMake(AndroidGame game) {
		this.game = game;
	}

	@Override
	public boolean hasHardwareKeyboard() {
		return false;
	}

	@Override
	public GoFuture<String> getText(final KeyMake.TextType ttype,
			final String label, final String initVal) {
		final GoPromise<String> result = game.asyn().deferredPromise();
		game.activity.runOnUiThread(new Runnable() {
			public void run() {
				final AlertDialog.Builder alert = new AlertDialog.Builder(
						game.activity);

				alert.setMessage(label);

				final EditText input = new EditText(game.activity);
				final int inputType;
				switch (ttype) {
				case NUMBER:
					inputType = InputType.TYPE_CLASS_NUMBER
							| InputType.TYPE_NUMBER_FLAG_SIGNED;
					break;
				case EMAIL:
					inputType = InputType.TYPE_CLASS_TEXT
							| InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;
					break;
				case URL:
					inputType = InputType.TYPE_CLASS_TEXT
							| InputType.TYPE_TEXT_VARIATION_URI;
					break;
				case DEFAULT:
				default:
					inputType = InputType.TYPE_CLASS_TEXT
							| InputType.TYPE_TEXT_VARIATION_NORMAL;
					break;
				}
				input.setInputType(inputType);
				input.setText(initVal);
				alert.setView(input);

				alert.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								result.succeed(input.getText().toString());
							}
						});

				alert.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								result.succeed(null);
							}
						});
				alert.show();
			}
		});
		return result;
	}

	void onKeyDown(int keyCode, KeyEvent nativeEvent) {
		long time = nativeEvent.getEventTime();
		char charCode = (char) nativeEvent.getUnicodeChar();
		if (nativeEvent.getKeyCode() == 67) {
			charCode = '\b';
		}
		KeyMake.KeyEvent event = new KeyMake.KeyEvent(0, time, charCode,
				keyForCode(keyCode), true);
		event.setFlag(mods(nativeEvent));
		dispatch(event);
	}

	void onKeyUp(int keyCode, KeyEvent nativeEvent) {
		long time = nativeEvent.getEventTime();
		char charCode = (char) nativeEvent.getUnicodeChar();
		if (nativeEvent.getKeyCode() == 67) {
			charCode = '\b';
		}
		KeyMake.KeyEvent event = new KeyMake.KeyEvent(0, time, charCode,
				keyForCode(keyCode), false);
		event.setFlag(mods(nativeEvent));
		dispatch(event);
	}

	boolean onTouch(MotionEvent event) {
		int actionType = event.getActionMasked();
		TouchMake.Event.Kind kind = (actionType < TO_KIND.length) ? TO_KIND[actionType]
				: null;
		if (kind != null) {
			final TouchMake.Event[] touches = parseMotionEvent(event, kind);
			game.asyn().invokeLater(new Runnable() {
				public void run() {
					game.input().touchEvents.emit(touches);
				}
			});
		}

		return kind != null;
	}

	private int mods(KeyEvent event) {
		return modifierFlags(event.isAltPressed(), event.isCtrlPressed(),
				event.isMetaPressed(), event.isShiftPressed());
	}

	private void dispatch(final KeyMake.Event event) {
		game.asyn().invokeLater(new Runnable() {
			@Override
			public void run() {
				keyboardEvents.emit(event);
			}
		});
	}

	private static int keyForCode(int keyCode) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_0:
			return SysKey.NUM_0;
		case KeyEvent.KEYCODE_1:
			return SysKey.NUM_1;
		case KeyEvent.KEYCODE_2:
			return SysKey.NUM_2;
		case KeyEvent.KEYCODE_3:
			return SysKey.NUM_3;
		case KeyEvent.KEYCODE_4:
			return SysKey.NUM_4;
		case KeyEvent.KEYCODE_5:
			return SysKey.NUM_5;
		case KeyEvent.KEYCODE_6:
			return SysKey.NUM_6;
		case KeyEvent.KEYCODE_7:
			return SysKey.NUM_7;
		case KeyEvent.KEYCODE_8:
			return SysKey.NUM_8;
		case KeyEvent.KEYCODE_9:
			return SysKey.NUM_9;
		case KeyEvent.KEYCODE_A:
			return SysKey.A;
		case KeyEvent.KEYCODE_ALT_LEFT:
			return SysKey.ALT_LEFT;
		case KeyEvent.KEYCODE_ALT_RIGHT:
			return SysKey.ALT_RIGHT;
		case KeyEvent.KEYCODE_APOSTROPHE:
			return SysKey.APOSTROPHE;
		case KeyEvent.KEYCODE_AT:
			return SysKey.AT;
		case KeyEvent.KEYCODE_B:
			return SysKey.B;
		case KeyEvent.KEYCODE_BACK:
			return SysKey.BACK;
		case KeyEvent.KEYCODE_BACKSLASH:
			return SysKey.BACKSLASH;
		case KeyEvent.KEYCODE_C:
			return SysKey.C;
		case KeyEvent.KEYCODE_CALL:
			return SysKey.CALL;
		case KeyEvent.KEYCODE_CAMERA:
			return SysKey.CAMERA;
		case KeyEvent.KEYCODE_CLEAR:
			return SysKey.CLEAR;
		case KeyEvent.KEYCODE_COMMA:
			return SysKey.COMMA;
		case KeyEvent.KEYCODE_D:
			return SysKey.D;
		case KeyEvent.KEYCODE_DEL:
			return SysKey.DEL;
		case KeyEvent.KEYCODE_DPAD_CENTER:
			return SysKey.DPAD_CENTER;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			return SysKey.DPAD_DOWN;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			return SysKey.DPAD_LEFT;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			return SysKey.DPAD_RIGHT;
		case KeyEvent.KEYCODE_DPAD_UP:
			return SysKey.DPAD_UP;
		case KeyEvent.KEYCODE_E:
			return SysKey.E;
		case KeyEvent.KEYCODE_ENDCALL:
			return SysKey.ENDCALL;
		case KeyEvent.KEYCODE_ENTER:
			return SysKey.ENTER;
		case KeyEvent.KEYCODE_ENVELOPE:
			return SysKey.ENVELOPE;
		case KeyEvent.KEYCODE_EQUALS:
			return SysKey.EQUALS;
		case KeyEvent.KEYCODE_EXPLORER:
			return SysKey.EXPLORER;
		case KeyEvent.KEYCODE_F:
			return SysKey.F;
		case KeyEvent.KEYCODE_FOCUS:
			return SysKey.FOCUS;
		case KeyEvent.KEYCODE_G:
			return SysKey.G;
		case KeyEvent.KEYCODE_GRAVE:
			return SysKey.GRAVE;
		case KeyEvent.KEYCODE_H:
			return SysKey.H;
		case KeyEvent.KEYCODE_HEADSETHOOK:
			return SysKey.HEADSETHOOK;
		case KeyEvent.KEYCODE_HOME:
			return SysKey.HOME;
		case KeyEvent.KEYCODE_I:
			return SysKey.I;
		case KeyEvent.KEYCODE_J:
			return SysKey.J;
		case KeyEvent.KEYCODE_K:
			return SysKey.K;
		case KeyEvent.KEYCODE_L:
			return SysKey.L;
		case KeyEvent.KEYCODE_LEFT_BRACKET:
			return SysKey.LEFT_BRACKET;
		case KeyEvent.KEYCODE_M:
			return SysKey.M;
		case KeyEvent.KEYCODE_MENU:
			return SysKey.MENU;
		case KeyEvent.KEYCODE_MINUS:
			return SysKey.MINUS;
		case KeyEvent.KEYCODE_MUTE:
			return SysKey.MUTE;
		case KeyEvent.KEYCODE_N:
			return SysKey.N;
		case KeyEvent.KEYCODE_NOTIFICATION:
			return SysKey.NOTIFICATION;
		case KeyEvent.KEYCODE_NUM:
			return SysKey.NUM;
		case KeyEvent.KEYCODE_O:
			return SysKey.O;
		case KeyEvent.KEYCODE_P:
			return SysKey.P;
		case KeyEvent.KEYCODE_PAGE_DOWN:
			return SysKey.PAGE_DOWN;
		case KeyEvent.KEYCODE_PAGE_UP:
			return SysKey.PAGE_UP;
		case KeyEvent.KEYCODE_PERIOD:
			return SysKey.PERIOD;
		case KeyEvent.KEYCODE_PICTSYMBOLS:
			return SysKey.PICTSYMBOLS;
		case KeyEvent.KEYCODE_PLUS:
			return SysKey.PLUS;
		case KeyEvent.KEYCODE_POUND:
			return SysKey.POUND;
		case KeyEvent.KEYCODE_POWER:
			return SysKey.POWER;
		case KeyEvent.KEYCODE_Q:
			return SysKey.Q;
		case KeyEvent.KEYCODE_R:
			return SysKey.R;
		case KeyEvent.KEYCODE_RIGHT_BRACKET:
			return SysKey.RIGHT_BRACKET;
		case KeyEvent.KEYCODE_S:
			return SysKey.S;
		case KeyEvent.KEYCODE_SEARCH:
			return SysKey.SEARCH;
		case KeyEvent.KEYCODE_SEMICOLON:
			return SysKey.SEMICOLON;
		case KeyEvent.KEYCODE_SHIFT_LEFT:
			return SysKey.SHIFT_LEFT;
		case KeyEvent.KEYCODE_SHIFT_RIGHT:
			return SysKey.SHIFT_RIGHT;
		case KeyEvent.KEYCODE_SLASH:
			return SysKey.SLASH;
		case KeyEvent.KEYCODE_SOFT_LEFT:
			return SysKey.SOFT_LEFT;
		case KeyEvent.KEYCODE_SOFT_RIGHT:
			return SysKey.SOFT_RIGHT;
		case KeyEvent.KEYCODE_SPACE:
			return SysKey.SPACE;
		case KeyEvent.KEYCODE_STAR:
			return SysKey.STAR;
		case KeyEvent.KEYCODE_SWITCH_CHARSET:
			return SysKey.SWITCH_CHARSET;
		case KeyEvent.KEYCODE_SYM:
			return SysKey.SYM;
		case KeyEvent.KEYCODE_T:
			return SysKey.T;
		case KeyEvent.KEYCODE_TAB:
			return SysKey.TAB;
		case KeyEvent.KEYCODE_U:
			return SysKey.U;
		case KeyEvent.KEYCODE_UNKNOWN:
			return SysKey.UNKNOWN;
		case KeyEvent.KEYCODE_V:
			return SysKey.V;
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			return SysKey.VOLUME_DOWN;
		case KeyEvent.KEYCODE_VOLUME_UP:
			return SysKey.VOLUME_UP;
		case KeyEvent.KEYCODE_W:
			return SysKey.W;
		case KeyEvent.KEYCODE_X:
			return SysKey.X;
		case KeyEvent.KEYCODE_Y:
			return SysKey.Y;
		case KeyEvent.KEYCODE_Z:
			return SysKey.Z;
		default:
			return SysKey.UNKNOWN;
		}
	}

	private TouchMake.Event[] parseMotionEvent(MotionEvent event,
			TouchMake.Event.Kind kind) {
		int actionType = event.getActionMasked();
		boolean isChanged = (actionType == MotionEvent.ACTION_POINTER_UP || actionType == MotionEvent.ACTION_POINTER_DOWN);
		int changedIdx = isChanged ? event.getActionIndex() : 0;
		int count = event.getPointerCount();
		TouchMake.Event[] touches = new TouchMake.Event[isChanged ? 1 : count];
		double time = event.getEventTime();
		int tidx = 0;
		for (int tt = 0; tt < count; tt++) {
			if (isChanged && tt != changedIdx) {
				continue;
			}
			Vector2f xy = game.graphics().transformTouch(event.getX(tt),
					event.getY(tt));
			float pressure = event.getPressure(tt);
			float size = event.getSize(tt);
			int id = event.getPointerId(tt);
			touches[tidx++] = new TouchMake.Event(0, time, xy.x(), xy.y(),
					kind, id, pressure, size);
		}
		return touches;
	}

	private static TouchMake.Event.Kind[] TO_KIND = new TouchMake.Event.Kind[16];
	static {
		TO_KIND[MotionEvent.ACTION_DOWN] = TouchMake.Event.Kind.START;
		TO_KIND[MotionEvent.ACTION_UP] = TouchMake.Event.Kind.END;
		TO_KIND[MotionEvent.ACTION_POINTER_DOWN] = TouchMake.Event.Kind.START;
		TO_KIND[MotionEvent.ACTION_POINTER_UP] = TouchMake.Event.Kind.END;
		TO_KIND[MotionEvent.ACTION_MOVE] = TouchMake.Event.Kind.MOVE;
		TO_KIND[MotionEvent.ACTION_CANCEL] = TouchMake.Event.Kind.CANCEL;
	}

	@Override
	public boolean hasTouch() {
		return true;
	}

	@Override
	public void callback(LObject o) {

	}
}
