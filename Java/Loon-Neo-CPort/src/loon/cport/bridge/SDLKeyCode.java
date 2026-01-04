/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
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
package loon.cport.bridge;

public final class SDLKeyCode {

	public static final int SDLK_SCANCODE_MASK = 1 << 30;

	public static int SDL_SCANCODE_TO_KEYCODE(int x) {
		return x | SDLK_SCANCODE_MASK;
	}

	public static final int SDLK_UNKNOWN = 0;
	public static final int SDLK_RETURN = '\r';
	public static final int SDLK_ESCAPE = '\u001B';
	public static final int SDLK_BACKSPACE = '\b';
	public static final int SDLK_TAB = '\t';
	public static final int SDLK_SPACE = ' ';
	public static final int SDLK_EXCLAIM = '!';
	public static final int SDLK_QUOTEDBL = '"';
	public static final int SDLK_HASH = '#';
	public static final int SDLK_PERCENT = '%';
	public static final int SDLK_DOLLAR = '$';
	public static final int SDLK_AMPERSAND = '&';
	public static final int SDLK_QUOTE = '\'';
	public static final int SDLK_LEFTPAREN = '(';
	public static final int SDLK_RIGHTPAREN = ')';
	public static final int SDLK_ASTERISK = '*';
	public static final int SDLK_PLUS = '+';
	public static final int SDLK_COMMA = ';';
	public static final int SDLK_MINUS = '-';
	public static final int SDLK_PERIOD = '.';
	public static final int SDLK_SLASH = '/';
	public static final int SDLK_0 = '0';
	public static final int SDLK_1 = '1';
	public static final int SDLK_2 = '2';
	public static final int SDLK_3 = '3';
	public static final int SDLK_4 = '4';
	public static final int SDLK_5 = '5';
	public static final int SDLK_6 = '6';
	public static final int SDLK_7 = '7';
	public static final int SDLK_8 = '8';
	public static final int SDLK_9 = '9';
	public static final int SDLK_COLON = ':';
	public static final int SDLK_SEMICOLON = ';';
	public static final int SDLK_LESS = '<';
	public static final int SDLK_EQUALS = '=';
	public static final int SDLK_GREATER = '>';
	public static final int SDLK_QUESTION = '?';
	public static final int SDLK_AT = '@';
	public static final int SDLK_LEFTBRACKET = '[';
	public static final int SDLK_BACKSLASH = '\\';
	public static final int SDLK_RIGHTBRACKET = ']';
	public static final int SDLK_CARET = '^';
	public static final int SDLK_UNDERSCORE = '_';
	public static final int SDLK_BACKQUOTE = '`';
	public static final int SDLK_A = 'a';
	public static final int SDLK_B = 'b';
	public static final int SDLK_C = 'c';
	public static final int SDLK_D = 'd';
	public static final int SDLK_E = 'e';
	public static final int SDLK_F = 'f';
	public static final int SDLK_G = 'g';
	public static final int SDLK_H = 'h';
	public static final int SDLK_I = 'i';
	public static final int SDLK_J = 'j';
	public static final int SDLK_K = 'k';
	public static final int SDLK_L = 'l';
	public static final int SDLK_M = 'm';
	public static final int SDLK_N = 'n';
	public static final int SDLK_O = 'o';
	public static final int SDLK_P = 'p';
	public static final int SDLK_Q = 'q';
	public static final int SDLK_R = 'r';
	public static final int SDLK_S = 's';
	public static final int SDLK_T = 't';
	public static final int SDLK_U = 'u';
	public static final int SDLK_V = 'v';
	public static final int SDLK_W = 'w';
	public static final int SDLK_X = 'x';
	public static final int SDLK_Y = 'y';
	public static final int SDLK_Z = 'z';
	public static final int SDLK_CAPSLOCK = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_CAPSLOCK);
	public static final int SDLK_F1 = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_F1);
	public static final int SDLK_F2 = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_F2);
	public static final int SDLK_F3 = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_F3);
	public static final int SDLK_F4 = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_F4);
	public static final int SDLK_F5 = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_F5);
	public static final int SDLK_F6 = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_F6);
	public static final int SDLK_F7 = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_F7);
	public static final int SDLK_F8 = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_F8);
	public static final int SDLK_F9 = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_F9);
	public static final int SDLK_F10 = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_F10);
	public static final int SDLK_F11 = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_F11);
	public static final int SDLK_F12 = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_F12);
	public static final int SDLK_PRINTSCREEN = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_PRINTSCREEN);
	public static final int SDLK_SCROLLLOCK = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_SCROLLLOCK);
	public static final int SDLK_PAUSE = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_PAUSE);
	public static final int SDLK_INSERT = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_INSERT);
	public static final int SDLK_HOME = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_HOME);
	public static final int SDLK_PAGEUP = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_PAGEUP);
	public static final int SDLK_DELETE = '\177';
	public static final int SDLK_END = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_END);
	public static final int SDLK_PAGEDOWN = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_PAGEDOWN);
	public static final int SDLK_RIGHT = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_RIGHT);
	public static final int SDLK_LEFT = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_LEFT);
	public static final int SDLK_DOWN = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_DOWN);
	public static final int SDLK_UP = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_UP);
	public static final int SDLK_NUMLOCKCLEAR = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_NUMLOCKCLEAR);
	public static final int SDLK_KP_DIVIDE = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_KP_DIVIDE);
	public static final int SDLK_KP_MULTIPLY = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_KP_MULTIPLY);
	public static final int SDLK_KP_MINUS = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_KP_MINUS);
	public static final int SDLK_KP_PLUS = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_KP_PLUS);
	public static final int SDLK_KP_ENTER = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_KP_ENTER);
	public static final int SDLK_KP_1 = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_KP_1);
	public static final int SDLK_KP_2 = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_KP_2);
	public static final int SDLK_KP_3 = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_KP_3);
	public static final int SDLK_KP_4 = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_KP_4);
	public static final int SDLK_KP_5 = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_KP_5);
	public static final int SDLK_KP_6 = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_KP_6);
	public static final int SDLK_KP_7 = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_KP_7);
	public static final int SDLK_KP_8 = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_KP_8);
	public static final int SDLK_KP_9 = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_KP_9);
	public static final int SDLK_KP_0 = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_KP_0);
	public static final int SDLK_KP_PERIOD = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_KP_PERIOD);
	public static final int SDLK_APPLICATION = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_APPLICATION);
	public static final int SDLK_POWER = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_POWER);
	public static final int SDLK_KP_EQUALS = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_KP_EQUALS);
	public static final int SDLK_F13 = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_F13);
	public static final int SDLK_F14 = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_F14);
	public static final int SDLK_F15 = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_F15);
	public static final int SDLK_F16 = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_F16);
	public static final int SDLK_F17 = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_F17);
	public static final int SDLK_F18 = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_F18);
	public static final int SDLK_F19 = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_F19);
	public static final int SDLK_F20 = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_F20);
	public static final int SDLK_F21 = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_F21);
	public static final int SDLK_F22 = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_F22);
	public static final int SDLK_F23 = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_F23);
	public static final int SDLK_F24 = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_F24);
	public static final int SDLK_EXECUTE = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_EXECUTE);
	public static final int SDLK_HELP = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_HELP);
	public static final int SDLK_MENU = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_MENU);
	public static final int SDLK_SELECT = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_SELECT);
	public static final int SDLK_STOP = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_STOP);
	public static final int SDLK_AGAIN = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_AGAIN);
	public static final int SDLK_UNDO = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_UNDO);
	public static final int SDLK_CUT = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_CUT);
	public static final int SDLK_COPY = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_COPY);
	public static final int SDLK_PASTE = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_PASTE);
	public static final int SDLK_FIND = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_FIND);
	public static final int SDLK_MUTE = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_MUTE);
	public static final int SDLK_VOLUMEUP = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_VOLUMEUP);
	public static final int SDLK_VOLUMEDOWN = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_VOLUMEDOWN);
	public static final int SDLK_KP_COMMA = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_KP_COMMA);
	public static final int SDLK_KP_EQUALSAS400 = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_KP_EQUALSAS400);
	public static final int SDLK_ALTERASE = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_ALTERASE);
	public static final int SDLK_SYSREQ = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_SYSREQ);
	public static final int SDLK_CANCEL = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_CANCEL);
	public static final int SDLK_CLEAR = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_CLEAR);
	public static final int SDLK_PRIOR = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_PRIOR);
	public static final int SDLK_RETURN2 = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_RETURN2);
	public static final int SDLK_SEPARATOR = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_SEPARATOR);
	public static final int SDLK_OUT = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_OUT);
	public static final int SDLK_OPER = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_OPER);
	public static final int SDLK_CLEARAGAIN = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_CLEARAGAIN);
	public static final int SDLK_CRSEL = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_CRSEL);
	public static final int SDLK_EXSEL = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_EXSEL);
	public static final int SDLK_KP_00 = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_KP_00);
	public static final int SDLK_KP_000 = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_KP_000);
	public static final int SDLK_THOUSANDSSEPARATOR = SDL_SCANCODE_TO_KEYCODE(
			SDLScanCode.SDL_SCANCODE_THOUSANDSSEPARATOR);
	public static final int SDLK_DECIMALSEPARATOR = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_DECIMALSEPARATOR);
	public static final int SDLK_CURRENCYUNIT = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_CURRENCYUNIT);
	public static final int SDLK_CURRENCYSUBUNIT = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_CURRENCYSUBUNIT);
	public static final int SDLK_KP_LEFTPAREN = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_KP_LEFTPAREN);
	public static final int SDLK_KP_RIGHTPAREN = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_KP_RIGHTPAREN);
	public static final int SDLK_KP_LEFTBRACE = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_KP_LEFTBRACE);
	public static final int SDLK_KP_RIGHTBRACE = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_KP_RIGHTBRACE);
	public static final int SDLK_KP_TAB = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_KP_TAB);
	public static final int SDLK_KP_BACKSPACE = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_KP_BACKSPACE);
	public static final int SDLK_KP_A = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_KP_A);
	public static final int SDLK_KP_B = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_KP_B);
	public static final int SDLK_KP_C = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_KP_C);
	public static final int SDLK_KP_D = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_KP_D);
	public static final int SDLK_KP_E = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_KP_E);
	public static final int SDLK_KP_F = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_KP_F);
	public static final int SDLK_KP_XOR = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_KP_XOR);
	public static final int SDLK_KP_POWER = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_KP_POWER);
	public static final int SDLK_KP_PERCENT = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_KP_PERCENT);
	public static final int SDLK_KP_LESS = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_KP_LESS);
	public static final int SDLK_KP_GREATER = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_KP_GREATER);
	public static final int SDLK_KP_AMPERSAND = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_KP_AMPERSAND);
	public static final int SDLK_KP_DBLAMPERSAND = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_KP_DBLAMPERSAND);
	public static final int SDLK_KP_VERTICALBAR = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_KP_VERTICALBAR);
	public static final int SDLK_KP_DBLVERTICALBAR = SDL_SCANCODE_TO_KEYCODE(
			SDLScanCode.SDL_SCANCODE_KP_DBLVERTICALBAR);
	public static final int SDLK_KP_COLON = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_KP_COLON);
	public static final int SDLK_KP_HASH = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_KP_HASH);
	public static final int SDLK_KP_SPACE = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_KP_SPACE);
	public static final int SDLK_KP_AT = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_KP_AT);
	public static final int SDLK_KP_EXCLAM = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_KP_EXCLAM);
	public static final int SDLK_KP_MEMSTORE = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_KP_MEMSTORE);
	public static final int SDLK_KP_MEMRECALL = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_KP_MEMRECALL);
	public static final int SDLK_KP_MEMCLEAR = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_KP_MEMCLEAR);
	public static final int SDLK_KP_MEMADD = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_KP_MEMADD);
	public static final int SDLK_KP_MEMSUBTRACT = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_KP_MEMSUBTRACT);
	public static final int SDLK_KP_MEMMULTIPLY = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_KP_MEMMULTIPLY);
	public static final int SDLK_KP_MEMDIVIDE = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_KP_MEMDIVIDE);
	public static final int SDLK_KP_PLUSMINUS = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_KP_PLUSMINUS);
	public static final int SDLK_KP_CLEAR = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_KP_CLEAR);
	public static final int SDLK_KP_CLEARENTRY = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_KP_CLEARENTRY);
	public static final int SDLK_KP_BINARY = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_KP_BINARY);
	public static final int SDLK_KP_OCTAL = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_KP_OCTAL);
	public static final int SDLK_KP_DECIMAL = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_KP_DECIMAL);
	public static final int SDLK_KP_HEXADECIMAL = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_KP_HEXADECIMAL);
	public static final int SDLK_LCTRL = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_LCTRL);
	public static final int SDLK_LSHIFT = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_LSHIFT);
	public static final int SDLK_LALT = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_LALT);
	public static final int SDLK_LGUI = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_LGUI);
	public static final int SDLK_RCTRL = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_RCTRL);
	public static final int SDLK_RSHIFT = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_RSHIFT);
	public static final int SDLK_RALT = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_RALT);
	public static final int SDLK_RGUI = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_RGUI);
	public static final int SDLK_MODE = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_MODE);
	public static final int SDLK_AUDIONEXT = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_AUDIONEXT);
	public static final int SDLK_AUDIOPREV = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_AUDIOPREV);
	public static final int SDLK_AUDIOSTOP = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_AUDIOSTOP);
	public static final int SDLK_AUDIOPLAY = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_AUDIOPLAY);
	public static final int SDLK_AUDIOMUTE = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_AUDIOMUTE);
	public static final int SDLK_MEDIASELECT = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_MEDIASELECT);
	public static final int SDLK_WWW = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_WWW);
	public static final int SDLK_MAIL = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_MAIL);
	public static final int SDLK_CALCULATOR = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_CALCULATOR);
	public static final int SDLK_COMPUTER = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_COMPUTER);
	public static final int SDLK_AC_SEARCH = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_AC_SEARCH);
	public static final int SDLK_AC_HOME = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_AC_HOME);
	public static final int SDLK_AC_BACK = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_AC_BACK);
	public static final int SDLK_AC_FORWARD = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_AC_FORWARD);
	public static final int SDLK_AC_STOP = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_AC_STOP);
	public static final int SDLK_AC_REFRESH = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_AC_REFRESH);
	public static final int SDLK_AC_BOOKMARKS = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_AC_BOOKMARKS);
	public static final int SDLK_BRIGHTNESSDOWN = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_BRIGHTNESSDOWN);
	public static final int SDLK_BRIGHTNESSUP = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_BRIGHTNESSUP);
	public static final int SDLK_DISPLAYSWITCH = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_DISPLAYSWITCH);
	public static final int SDLK_KBDILLUMTOGGLE = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_KBDILLUMTOGGLE);
	public static final int SDLK_KBDILLUMDOWN = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_KBDILLUMDOWN);
	public static final int SDLK_KBDILLUMUP = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_KBDILLUMUP);
	public static final int SDLK_EJECT = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_EJECT);
	public static final int SDLK_SLEEP = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_SLEEP);
	public static final int SDLK_APP1 = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_APP1);
	public static final int SDLK_APP2 = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_APP2);
	public static final int SDLK_AUDIOREWIND = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_AUDIOREWIND);
	public static final int SDLK_AUDIOFASTFORWARD = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_AUDIOFASTFORWARD);
	public static final int SDLK_SOFTLEFT = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_SOFTLEFT);
	public static final int SDLK_SOFTRIGHT = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_SOFTRIGHT);
	public static final int SDLK_CALL = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_CALL);
	public static final int SDLK_ENDCALL = SDL_SCANCODE_TO_KEYCODE(SDLScanCode.SDL_SCANCODE_ENDCALL);

	private SDLKeyCode() {
	}
}
