#include "STBSupport.h"

static stb_font *_temp_fontinfo;
static cache_surface* _temp_stbsurface;
static bool g_YesOrNo = false;
static char g_input_text[256] = "";

typedef struct {
	stbtt_fontinfo font;
	unsigned char* fontBuffer;
} FontData;

typedef struct {
	const char* name;
	const char* path;
} FontEntry;

// 默认的字体文件位置
#if defined(_WIN32)
FontEntry system_font_paths[] = {
	{"Microsoft YaHei", "C:\\Windows\\Fonts\\msyh.ttc"},
	{"Microsoft YaHei Bold", "C:\\Windows\\Fonts\\msyhbd.ttc"},
	{"SimHei", "C:\\Windows\\Fonts\\simhei.ttf"},
	{"SimSun", "C:\\Windows\\Fonts\\simsun.ttc"},
	{"Arial", "C:\\Windows\\Fonts\\arial.ttf"},
	{"Arial Bold", "C:\\Windows\\Fonts\\arialbd.ttf"},
	{"Calibri", "C:\\Windows\\Fonts\\calibri.ttf"},
	{"Times New Roman", "C:\\Windows\\Fonts\\times.ttf"},
	{"Courier New", "C:\\Windows\\Fonts\\cour.ttf"},
	{"Segoe UI", "C:\\Windows\\Fonts\\segoeui.ttf"},
	{"Segoe UI Symbol", "C:\\Windows\\Fonts\\seguisym.ttf"},
	{NULL, NULL}
};
#elif defined(__APPLE__)
FontEntry system_font_paths[] = {
	{"Arial", "/System/Library/Fonts/Supplemental/Arial.ttf"},
	{"Arial Bold", "/System/Library/Fonts/Supplemental/Arial Bold.ttf"},
	{"SimSun", "/System/Library/Fonts/Supplemental/Songti.ttc"},
	{"SimHei", "/System/Library/Fonts/Supplemental/Heiti.ttc"},
	{"Times New Roman", "/System/Library/Fonts/Supplemental/Times New Roman.ttf"},
	{"Courier New", "/System/Library/Fonts/Supplemental/Courier New.ttf"},
	{"Apple Color Emoji", "/System/Library/Fonts/Supplemental/Apple Color Emoji.ttc"},
	{"PingFang", "/System/Library/Fonts/Supplemental/PingFang.ttc"},
	{NULL, NULL}
};
#elif defined(__linux__)
FontEntry system_font_paths[] = {
	{"DejaVu Sans", "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf"},
	{"DejaVu Sans Bold", "/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf"},
	{"Liberation Sans", "/usr/share/fonts/truetype/liberation/LiberationSans-Regular.ttf"},
	{"Liberation Sans Bold", "/usr/share/fonts/truetype/liberation/LiberationSans-Bold.ttf"},
	{"SimHei", "/usr/share/fonts/truetype/noto/NotoSansCJK-Regular.ttc"},
	{"Noto Sans CJK", "/usr/share/fonts/truetype/noto/NotoSansCJK-Regular.ttc"},
	{"Noto Color Emoji", "/usr/share/fonts/truetype/noto/NotoColorEmoji.ttf"},
	{"FreeSans", "/usr/share/fonts/truetype/freefont/FreeSans.ttf"},
	{"Ubuntu", "/usr/share/fonts/truetype/ubuntu/Ubuntu-R.ttf"},
	{NULL, NULL}
};
#elif defined(__ANDROID__)
FontEntry system_font_paths[] = {
	{"SimHei", "/system/fonts/NotoSansCJK-Regular.ttc"},
	{"Noto Sans CJK", "/system/fonts/NotoSansCJK-Regular.ttc"},
	{"Noto Sans", "/system/fonts/NotoSans-Regular.ttf"},
	{"Droid Sans", "/system/fonts/DroidSans.ttf"},
	{"Roboto", "/system/fonts/Roboto-Regular.ttf"},
	{"Roboto Bold", "/system/fonts/Roboto-Bold.ttf"},
	{"Noto Color Emoji", "/system/fonts/NotoColorEmoji.ttf"},
	{NULL, NULL}
};
#elif defined(__IPHONE_OS__) || defined(__IOS__)
FontEntry system_font_paths[] = {
	{"Arial", "/System/Library/Fonts/Core/Arial.ttf"},
	{"Arial Bold", "/System/Library/Fonts/Core/Arial Bold.ttf"},
	{"Apple Color Emoji", "/System/Library/Fonts/Core/AppleColorEmoji.ttf"},
	{"SimHei", "/System/Library/Fonts/Core/Heiti.ttc"},
	{"PingFang", "/System/Library/Fonts/Core/PingFang.ttc"},
	{NULL, NULL}
#elif defined(__IPHONE_OS__) || defined(__IOS__)
FontEntry system_font_paths[] = {
	{"Arial", "/System/Library/Fonts/Core/Arial.ttf"},
	{"Arial Bold", "/System/Library/Fonts/Core/Arial Bold.ttf"},
	{"Apple Color Emoji", "/System/Library/Fonts/Core/AppleColorEmoji.ttf"},
	{"SimHei", "/System/Library/Fonts/Core/Heiti.ttc"},
	{"PingFang", "/System/Library/Fonts/Core/PingFang.ttc"},
	{NULL, NULL}
};
#elif defined(__SWITCH__)
FontEntry system_font_paths[] = {
	{"Nintendo Standard", "romfs:/font/nintendo_std.ttf"},
	{"Nintendo Extended", "romfs:/font/nintendo_ext.ttf"},
	{"SimHei", "sdmc:/switch/fonts/NotoSansCJK-Regular.ttc"},
	{"Noto Sans CJK", "sdmc:/switch/fonts/NotoSansCJK-Regular.ttc"},
	{NULL, NULL}
};
#elif defined(__ORBIS__) || defined(__PROSPERO__) 
FontEntry system_font_paths[] = {
	{"SimHei", "/app0/fonts/SCE-PS5-UI-Bold.otf"},
	{"SCE UI Regular", "/app0/fonts/SCE-PS5-UI-Regular.otf"},
	{"SCE UI Bold", "/app0/fonts/SCE-PS5-UI-Bold.otf"},
	{"SCE Emoji", "/app0/fonts/SCE-PS5-Emoji.ttf"},
	{NULL, NULL}
};
#elif defined(_XBOX_ONE) || defined(_GAMING_XBOX)
FontEntry system_font_paths[] = {
	{"SimHei", "D:\\Windows\\Fonts\\segoeui.ttf"},
	{"Segoe UI", "D:\\Windows\\Fonts\\segoeui.ttf"},
	{"Segoe UI Symbol", "D:\\Windows\\Fonts\\seguisym.ttf"},
	{"Arial", "D:\\Windows\\Fonts\\arial.ttf"},
	{NULL, NULL}
};
#else
FontEntry system_font_paths[] = {
	{NULL, NULL}
};
#endif

typedef enum {
	DIALOG_TYPE_YESNO,
	DIALOG_TYPE_INPUT
} DialogType;

typedef struct {
	SDL_Color bg_color;
	SDL_Color text_color;
	SDL_Color btn_yes_color;
	SDL_Color btn_no_color;
	SDL_Color btn_text_color;
} DialogTheme;

typedef struct {
	DialogType type;
	const char* title;
	const char* message;
	int win_w, win_h;
	DialogTheme theme;
} DialogConfig;

static DialogTheme dialog_light_theme = {
   {240, 240, 240, 255},
   {0, 0, 0, 255},
   {0, 200, 0, 255},
   {200, 0, 0, 255},
   {255, 255, 255, 255}
};

static void ensure_font_extension(char* name, size_t size) {
	size_t len = strlen(name);
	if (!strstr(name, ".ttf") && !strstr(name, ".otf")) {
		if (len + 4 < size) {
			chars_append(name, size - len - 1, ".ttf");
		}
	}
}
#ifdef __linux__
static char* find_font_path_fc(const char* fontName) {
	static char path[512];
	FcInit();
	FcPattern* pat = FcNameParse((const FcChar8*)fontName);
	FcConfigSubstitute(NULL, pat, FcMatchPattern);
	FcDefaultSubstitute(pat);

	FcResult result;
	FcPattern* match = FcFontMatch(NULL, pat, &result);
	if (match) {
		FcChar8* file = NULL;
		if (FcPatternGetString(match, FC_FILE, 0, &file) == FcResultMatch) {
			snprintf(path, sizeof(path), "%s", file);
			FcPatternDestroy(match);
			FcPatternDestroy(pat);
			FcFini();
			return path;
		}
		FcPatternDestroy(match);
	}
	FcPatternDestroy(pat);
	FcFini();
	return NULL;
}
#endif

static char* find_font_path(const char* fontName) {
static char path[512];
#ifdef __linux__
	return find_font_path_fc(fontName);
#elif _WIN32
	const char* win_font_dirs[] = {
		"C:\\Windows\\Fonts\\",
		"C:\\Windows\\Fonts\\subdir\\"
	};
	for (int i = 0; i < sizeof(win_font_dirs) / sizeof(win_font_dirs[0]); i++) {
		snprintf(path, sizeof(path), "%s%s", win_font_dirs[i], fontName);
		ensure_font_extension(path, sizeof(path));
		FILE* f = fopen(path, "rb");
		if (f) { fclose(f); return path; }
	}
#elif __APPLE__
	const char* mac_font_dirs[] = {
		"/System/Library/Fonts/",
		"/Library/Fonts/",
		"~/Library/Fonts/"
	};
	for (int i = 0; i < sizeof(mac_font_dirs) / sizeof(mac_font_dirs[0]); i++) {
		snprintf(path, sizeof(path), "%s%s", mac_font_dirs[i], fontName);
		ensure_font_extension(path, sizeof(path));
		FILE* f = fopen(path, "rb");
		if (f) { fclose(f); return path; }
	}
#elif __ANDROID__
	snprintf(path, sizeof(path), "/system/fonts/%s", fontName);
	ensure_font_extension(path, sizeof(path));
#elif __SWITCH__
	snprintf(path, sizeof(path), "/switch/system/fonts/%s", fontName);
	ensure_font_extension(path, sizeof(path));
#elif __PS4__ || __PS5__
	snprintf(path, sizeof(path), "/app0/fonts/%s", fontName);
	ensure_font_extension(path, sizeof(path));
#else
	snprintf(path, sizeof(path), "%s", fontName);
	ensure_font_extension(path, sizeof(path));
#endif
	return path;
}

#if defined(_WIN32) && defined(_MSC_VER)
static char* chars_secure_strtok(char* str, const char* delim) {
	static __declspec(thread) char* context = NULL;
	char* token;
	if (str != NULL) {
		context = str;
	}
	if (context == NULL) {
		return NULL;
	}
	if (strtok_s(str, delim, &context) != NULL) {
		token = str;
	}
	else {
		token = NULL;
	}
	return token;
}
#elif defined(__unix__) || defined(__APPLE__)
char* chars_secure_strtok(char* str, const char* delim) {
	static _Thread_local char* saveptr = NULL;
	return strtok_r(str, delim, &saveptr);
}
#else
#if defined(__STDC_VERSION__) && __STDC_VERSION__ >= 201112L
static _Thread_local char* tls_strtok_save = NULL;
#else
#if defined(_MSC_VER)
__declspec(thread) static char* tls_strtok_save = NULL;
#else
__thread static char* tls_strtok_save = NULL;
#endif
#endif
char* chars_secure_strtok(char* str, const char* delim) {
	char* token_start;
	if (str == NULL) {
		str = tls_strtok_save;
	}
	while (str && *str && strchr(delim, *str)) {
		str++;
	}
	if (str == NULL || *str == '\0') {
		tls_strtok_save = NULL;
		return NULL;
	}
	token_start = str;
	while (*str && strchr(delim, *str) == NULL) {
		str++;
	}
	if (*str) {
		*str = '\0';
		tls_strtok_save = str + 1;
	}
	else {
		tls_strtok_save = NULL;
	}
	return token_start;
}
#endif

static char** split_lines(const char* text, int* count) {
	char* copy = _strdup(text);
	int lines = 1;
	for (char* p = copy; *p; p++) if (*p == '\n') lines++;

	char** arr = (char**)malloc(lines * sizeof(char*));
	int idx = 0;
	char* token = chars_secure_strtok(copy, "\n");
	while (token) {
		if (arr) {
			arr[idx++] = _strdup(token);
			token = chars_secure_strtok(NULL, "\n");
		}
	}
	*count = lines;
	free(copy);
	return arr;
}

static void free_lines(char** arr, int count) {
	for (int i = 0; i < count; i++) {
		free(arr[i]);
	}
	free(arr);
}

static int measure_max_width_lines(stbtt_fontinfo* font, char** lines, int numLines, float scale) {
	int maxWidth = 0;
	for (int i = 0; i < numLines; i++) {
		const char* text = lines[i];
		int width = 0;
		const char* p = text;
		while (*p) {
			int glyph = stbtt_FindGlyphIndex(font, *p);
			int advance, lsb;
			stbtt_GetGlyphHMetrics(font, glyph, &advance, &lsb);
			width += float_to_int_threshold(advance * scale);
			if (*(p + 1)) {
				width += float_to_int_threshold(stbtt_GetGlyphKernAdvance(font, glyph, stbtt_FindGlyphIndex(font, *(p + 1))) * scale);
			}
			p++;
		}
		if (width > maxWidth) maxWidth = width;
	}
	return maxWidth;
}

enum TextAlign { ALIGN_LEFT = 0, ALIGN_CENTER = 1, ALIGN_RIGHT = 2 };
static int compute_alignment_offset(int align, int lineWidth, int maxWidth) {
	if (align == ALIGN_CENTER) return (maxWidth - lineWidth) / 2;
	if (align == ALIGN_RIGHT) return maxWidth - lineWidth;
	return 0;
}

void ImportSTBInclude()
{
}

static SDL_Texture* render_text_stb(stbtt_fontinfo* font, SDL_Renderer* renderer, const char* text, float fontSize, SDL_Color color) {
	int width = 0, height = 0;
	int ascent, descent, lineGap;
	stbtt_GetFontVMetrics(font, &ascent, &descent, &lineGap);
	float scale = stbtt_ScaleForPixelHeight(font, fontSize);
	ascent = (int)(ascent * scale);
	descent = (int)(descent * scale);
	for (const char* p = text; *p; p++) {
		int ax;
		stbtt_GetCodepointHMetrics(font, *p, &ax, 0);
		width += (int)(ax * scale);
	}
	height = ascent - descent;
	width += 8;
	height += 8;
	unsigned char* bitmap = (unsigned char*)calloc(width * height, 1);
	int x = 0;
	while (*text) {
		uint32_t cp = utf8_decode(&text);
		int ax, lsb;
		stbtt_GetCodepointHMetrics(font, cp, &ax, &lsb);
		int c_x1, c_y1, c_x2, c_y2;
		stbtt_GetCodepointBitmapBox(font, cp, scale, scale, &c_x1, &c_y1, &c_x2, &c_y2);
		int yoff = ascent + c_y1;
		stbtt_MakeCodepointBitmap(font, bitmap + x + yoff * width, c_x2 - c_x1, c_y2 - c_y1, width, scale, scale, cp);
		x += (int)(ax * scale);
	}
	SDL_Surface* surface = SDL_CreateRGBSurface(0, width, height, 32,
		0x00FF0000, 0x0000FF00, 0x000000FF, 0xFF000000);
	Uint32* pixels = (Uint32*)surface->pixels;
	for (int i = 0; i < width * height; i++) {
		Uint8 alpha = bitmap[i];
		pixels[i] = SDL_MapRGBA(surface->format, color.r, color.g, color.b, alpha);
	}
	free(bitmap);
	SDL_Texture* texture = SDL_CreateTextureFromSurface(renderer, surface);
	SDL_FreeSurface(surface);
	return texture;
}

static void draw_text(stbtt_fontinfo* font, SDL_Renderer* ren, const char* text, int x, int y, float size, SDL_Color color) {
	SDL_Texture* tex = render_text_stb(font, ren, text, size, color);
	if (!tex) return;
	int w, h;
	SDL_QueryTexture(tex, NULL, NULL, &w, &h);
	SDL_Rect dst = { x + 10, y - 5, w, h };
	SDL_RenderCopy(ren, tex, NULL, &dst);
	SDL_DestroyTexture(tex);
}

static int show_input_dialog(stbtt_fontinfo* font, DialogConfig cfg, const char* textA, const char* textB) {
	if (!font) {
		return 0;
	}
	if (SDL_InitSubSystem(SDL_INIT_VIDEO | SDL_INIT_EVENTS | SDL_INIT_TIMER) < 0) {
		SDL_Log("SDL Init Error: %s", SDL_GetError());
		return 0;
	}
	SDL_Window* win = SDL_CreateWindow(cfg.title, SDL_WINDOWPOS_CENTERED, SDL_WINDOWPOS_CENTERED,
		cfg.win_w, cfg.win_h, SDL_WINDOW_SHOWN);
	#if SDL_VERSION_ATLEAST(2,0,16)
		SDL_SetWindowAlwaysOnTop(win, SDL_TRUE);
	#endif
	SDL_Renderer* ren = SDL_CreateRenderer(win, -1, SDL_RENDERER_ACCELERATED | SDL_RENDERER_PRESENTVSYNC);

	int is_touch_device = SDL_GetNumTouchDevices() > 0;
	float btn_height_ratio = is_touch_device ? 0.18f : 0.16f;
	float btn_width_ratio = is_touch_device ? 0.25f : 0.20f;

	int running = 1;
	int yesno_choice = -1;
	int confirmed = 0;
	int selected = -1;
	int pressed_button = -1;
	char input_text[256] = { 0 };

	if (cfg.type == DIALOG_TYPE_INPUT) {
		SDL_StartTextInput();
	}

	SDL_Event e;
	while (running) {
		while (SDL_PollEvent(&e)) {
			if (e.type == SDL_QUIT) {
				running = 0;
			}
			else if (e.type == SDL_FINGERDOWN) {
				int mx = (int)(e.tfinger.x * cfg.win_w);
				int my = (int)(e.tfinger.y * cfg.win_h);
				pressed_button = -1;
				if (cfg.type == DIALOG_TYPE_YESNO) {
					if (mx > cfg.win_w * 0.24f && mx < cfg.win_w * (0.24f + btn_width_ratio) &&
						my > cfg.win_h * 0.64f && my < cfg.win_h * (0.64f + btn_height_ratio)) {
						yesno_choice = 1;
						running = 0;
					}
					else if (mx > cfg.win_w * 0.56f && mx < cfg.win_w * (0.56f + btn_width_ratio) &&
						my > cfg.win_h * 0.64f && my < cfg.win_h * (0.64f + btn_height_ratio)) {
						yesno_choice = 0;
						running = 0;
					}
				}
				else if (cfg.type == DIALOG_TYPE_INPUT) {
					if (mx > cfg.win_w * 0.24f && mx < cfg.win_w * (0.24f + btn_width_ratio) &&
						my > cfg.win_h * 0.72f && my < cfg.win_h * (0.72f + btn_height_ratio)) {
						confirmed = 1;
						running = 0;
					}
					else if (mx > cfg.win_w * 0.56f && mx < cfg.win_w * (0.56f + btn_width_ratio) &&
						my > cfg.win_h * 0.72f && my < cfg.win_h * (0.72f + btn_height_ratio)) {
						running = 0;
					}
				}
			} else if (e.type == SDL_MOUSEBUTTONDOWN) {
				int mx = e.button.x;
				int my = e.button.y;
				if (cfg.type == DIALOG_TYPE_YESNO) {
					if (mx > cfg.win_w * 0.24f && mx < cfg.win_w * (0.24f + btn_width_ratio) &&
						my > cfg.win_h * 0.64f && my < cfg.win_h * (0.64f + btn_height_ratio)) {
						yesno_choice = 1;
						running = 0;
					}
					else if (mx > cfg.win_w * 0.56f && mx < cfg.win_w * (0.56f + btn_width_ratio) &&
						my > cfg.win_h * 0.64f && my < cfg.win_h * (0.64f + btn_height_ratio)) {
						yesno_choice = 0;
						running = 0;
					}
				}
				else if (cfg.type == DIALOG_TYPE_INPUT) {
					if (mx > cfg.win_w * 0.24f && mx < cfg.win_w * (0.24f + btn_width_ratio) &&
						my > cfg.win_h * 0.72f && my < cfg.win_h * (0.72f + btn_height_ratio)) {
						confirmed = 1;
						running = 0;
					}
					else if (mx > cfg.win_w * 0.56f && mx < cfg.win_w * (0.56f + btn_width_ratio) &&
						my > cfg.win_h * 0.72f && my < cfg.win_h * (0.72f + btn_height_ratio)) {
						running = 0;
					}
				} 
			} else if (e.type == SDL_TEXTINPUT && cfg.type == DIALOG_TYPE_INPUT) {
				if (strlen(input_text) + strlen(e.text.text) < sizeof(input_text) - 1) {
					chars_strcat(input_text, e.text.text);
				}
			}
			else if (e.type == SDL_KEYDOWN && cfg.type == DIALOG_TYPE_INPUT) {
				if (e.key.keysym.sym == SDLK_BACKSPACE && strlen(input_text) > 0) {
					input_text[strlen(input_text) - 1] = '\0';
				}
				else if (e.key.keysym.sym == SDLK_RETURN) {
					confirmed = 1;
					running = 0;
				}
			}
		}

		SDL_SetRenderDrawColor(ren, cfg.theme.bg_color.r, cfg.theme.bg_color.g, cfg.theme.bg_color.b, 255);
		SDL_RenderClear(ren);
		
		if (cfg.message) {
			draw_text(font, ren, cfg.message, (int)(cfg.win_w * 0.1f), (int)(cfg.win_h * 0.15f), is_touch_device ? 28.0f : 22.0f, cfg.theme.text_color);
		}

		if (cfg.type == DIALOG_TYPE_YESNO) {
			SDL_Rect yes_btn = { (int)(cfg.win_w * 0.24f), (int)(cfg.win_h * 0.64f), (int)(cfg.win_w * btn_width_ratio), (int)(cfg.win_h * btn_height_ratio) };
			SDL_Rect no_btn = { (int)(cfg.win_w * 0.56f), (int)(cfg.win_h * 0.64f), (int)(cfg.win_w * btn_width_ratio), (int)(cfg.win_h * btn_height_ratio) };

			SDL_Color yes_color = cfg.theme.btn_yes_color;
			SDL_Color no_color = cfg.theme.btn_no_color;

			SDL_SetRenderDrawColor(ren, yes_color.r, yes_color.g, yes_color.b, 255);
			SDL_RenderFillRect(ren, &yes_btn);
			if (textA) {
				draw_text(font, ren, textA, yes_btn.x + 10, yes_btn.y + 10, is_touch_device ? 28.0f : 20.0f, cfg.theme.btn_text_color);
			}
			SDL_SetRenderDrawColor(ren, no_color.r, no_color.g, no_color.b, 255);
			SDL_RenderFillRect(ren, &no_btn);
			if (textB) {
				draw_text(font, ren, textB, no_btn.x + 10, no_btn.y + 10, is_touch_device ? 28.0f : 20.0f, cfg.theme.btn_text_color);
			}
		}
		else if (cfg.type == DIALOG_TYPE_INPUT) {

			SDL_SetRenderDrawColor(ren, 255, 255, 255, 255);

			if (cfg.message) {
				SDL_Rect input_box = { (int)(cfg.win_w * 0.2f) ,(int)(cfg.win_h * 0.5f),(int)(cfg.win_w * 0.6f),(int)(cfg.win_h * 0.1f) };
				SDL_RenderFillRect(ren, &input_box);
				SDL_Color newColor = { 0, 0, 0, 255 };
				draw_text(font, ren, input_text, input_box.x + 5, input_box.y + 5, is_touch_device ? 26.0f : 18.0f, newColor);
			}
			else {
				SDL_Rect input_box = { (int)(cfg.win_w * 0.2f) ,(int)(cfg.win_h * 0.2f),(int)(cfg.win_w * 0.6f),(int)(cfg.win_h * 0.2f) };
				SDL_RenderFillRect(ren, &input_box);
				SDL_Color newColor = { 0, 0, 0, 255 };
				draw_text(font, ren, input_text, input_box.x + 5, input_box.y + 5, is_touch_device ? 26.0f : 18.0f, newColor);
			}

			SDL_Rect ok_btn = { (int)(cfg.win_w * 0.24f), (int)(cfg.win_h * 0.72f), (int)(cfg.win_w * btn_width_ratio), (int)(cfg.win_h * btn_height_ratio) };
			SDL_SetRenderDrawColor(ren, cfg.theme.btn_yes_color.r, cfg.theme.btn_yes_color.g, cfg.theme.btn_yes_color.b, 255);
			SDL_RenderFillRect(ren, &ok_btn);
			draw_text(font, ren, textA, ok_btn.x + 10, ok_btn.y + 10, is_touch_device ? 28.0f : 20.0f, cfg.theme.btn_text_color);

			SDL_Rect cancel_btn = { (int)(cfg.win_w * 0.56f), (int)(cfg.win_h * 0.72f), (int)(cfg.win_w * btn_width_ratio), (int)(cfg.win_h * btn_height_ratio)};
			SDL_SetRenderDrawColor(ren, cfg.theme.btn_no_color.r, cfg.theme.btn_no_color.g, cfg.theme.btn_no_color.b, 255);
			SDL_RenderFillRect(ren, &cancel_btn);
			draw_text(font, ren, textB, cancel_btn.x + 10, cancel_btn.y + 10, is_touch_device ? 28.0f : 20.0f, cfg.theme.btn_text_color);
		}

		SDL_RenderPresent(ren);
	}

	if (cfg.type == DIALOG_TYPE_INPUT) {
		SDL_StopTextInput();
	}

	if (cfg.type == DIALOG_TYPE_YESNO ) {
		g_YesOrNo = (yesno_choice == 1);
	}
	else if (cfg.type == DIALOG_TYPE_INPUT) {
		if (confirmed) {
			chars_copy(g_input_text, input_text, 256);
		}
		else {
			memset(g_input_text, '\0', sizeof(g_input_text));
		}
	}
	SDL_DestroyRenderer(ren);
	SDL_DestroyWindow(win);
	SDL_QuitSubSystem(SDL_INIT_VIDEO | SDL_INIT_EVENTS | SDL_INIT_TIMER);
	return 1;
}

bool Load_STB_Dialog_YesOrNO() {
	return g_YesOrNo;
}

const char* Load_STB_Dialog_InputText() {
	return g_input_text;
}

int Load_STB_InputDialog(int64_t handle, const int dialogType, const int width, const int height, const char* title, const char* text, const char* textA, const char* textB) {
	stb_font* fontinfo = (stb_font*)handle;
	if (!fontinfo) {
		fontinfo = _temp_fontinfo;
		if (!fontinfo) {
			fprintf(stderr, "Load_STB_InputDialog Error !\n");
			return 0;
		}
	}
	DialogConfig dcfg = {
	 (DialogType)dialogType,
	 title,
	 text,
	 width, height,
	 dialog_light_theme
	};
	return show_input_dialog(fontinfo->info, dcfg, textA, textB);
}

int64_t Load_STB_Image_LoadBytes(const uint8_t* buffer, int32_t len)
{
	int32_t width = 0, height = 0, format = 0;
	const unsigned char* pixels = stbi_load_from_memory(buffer, len, &width, &height, &format, STBI_rgb_alpha);
	if (!pixels || width <= 0 || height <= 0) {
		fprintf(stderr, "Load_STB_Image_LoadBytes Error !\n");
		return -1;
	}
	stb_pix* pixmap = (stb_pix*)malloc(sizeof(stb_pix));
	if (!pixmap) {
		fprintf(stderr, "Load_STB_Image_LoadBytes Error !\n");
		return -1;
	}
	pixmap->width = width;
	pixmap->height = height;
	pixmap->format = format;
	pixmap->pixels = pixels;
	return (intptr_t)pixmap;
}

int64_t Load_STB_Image_LoadPath(const char* path)
{
	int32_t width = 0 , height = 0, format = 0;
	uint8_t* pixels = stbi_load(path, &width, &height, &format, STBI_default);
	if (!pixels || width <= 0 || height <= 0) {
		fprintf(stderr, "Load_STB_Image_LoadPath Error !\n");
		return -1;
	}
	stb_pix* pixmap = (stb_pix*)malloc(sizeof(stb_pix));
	if (!pixmap) {
		fprintf(stderr, "Load_STB_Image_LoadPath Error !\n");
		return -1;
	}
	pixmap->width = width;
	pixmap->height = height;
	pixmap->format = format;
	pixmap->pixels = pixels;
	return (intptr_t)pixmap;
}

int64_t Load_STB_Image_LoadPathToSDLSurface(const char* path)
{
	int32_t width = 0, height = 0, format = 0;
	void* pixels = stbi_load(path, &width, &height, &format, STBI_rgb_alpha);
	if (!pixels || width <= 0 || height <= 0) {
		fprintf(stderr, "Load_STB_Image_LoadPathToSDLSurface Error !\n");
		return -1;
	}
	cache_surface* newsurface = (cache_surface*)malloc(sizeof(cache_surface));
	if (!newsurface) {
		fprintf(stderr, "Load_STB_Image_LoadPathToSDLSurface Error !\n");
		return 0;
	}
	SDL_Surface* newImage = SDL_CreateRGBSurfaceWithFormatFrom(
		pixels,               
		width, height,         
		32,                   
		width * 4,            
		SDL_PIXELFORMAT_RGBA32 
	);
	newsurface->surface_data = newImage;
	newsurface->width = width;
	newsurface->height = height;
	_temp_stbsurface = newsurface;
	return (intptr_t)newsurface;
}

int64_t Load_STB_Image_LoadSDLSurfaceARGB32(const char* path)
{
	int width, height, format;
	unsigned char* pixels = stbi_load(path, &width, &height, &format, STBI_rgb_alpha);
	if (!pixels) {
		fprintf(stderr, "Load_STB_Image_LoadSDLSurfaceARGB32 Error !\n");
		return 0;
	}
	SDL_Surface* surface = SDL_CreateRGBSurfaceWithFormatFrom(
		pixels,                
		width, height,         
		32,                    
		width * 4,            
		SDL_PIXELFORMAT_ARGB32
	);
	if (!surface) {
		stbi_image_free(pixels);
		return 0;
	}
	SDL_Surface* converted = SDL_ConvertSurfaceFormat(surface, SDL_PIXELFORMAT_ARGB32, 0);
	SDL_FreeSurface(surface);
	stbi_image_free(pixels);
	cache_surface* newsurface = (cache_surface*)malloc(sizeof(cache_surface));
	if (!newsurface) {
		fprintf(stderr, "Load_STB_Image_LoadSDLSurfaceARGB32 Error !\n");
		return 0;
	}
	newsurface->surface_data = converted;
	newsurface->width = width;
	newsurface->height = height;
	_temp_stbsurface = newsurface;
	return (intptr_t)converted;
}

void Load_STB_TempSurfaceFree() {
	if (!_temp_stbsurface) {
		fprintf(stderr, "Load_STB_TempSurfaceFree Error !\n");
		return;
	}
	SDL_FreeSurface(_temp_stbsurface->surface_data);
	free(_temp_stbsurface);
}

void Load_STB_Image_Free(const int64_t handle)
{
	stb_pix* pixmap = (stb_pix*)handle;
	if (!pixmap) {
		fprintf(stderr, "Load_STB_Image_Free Error !\n");
		return;
	}
	stbi_image_free((void*)pixmap->pixels);
	if (pixmap != NULL) {
		free(pixmap);
	}
}

void Load_STB_Image_GetSizeFormat(const int64_t handle,int32_t* rect) {
	stb_pix* pixmap = (stb_pix*)handle;
	if (!pixmap) {
		fprintf(stderr, "Load_STB_Image_GetSizeFormat Error !\n");
		return;
	}
	rect[0] = pixmap->width;
	rect[1] = pixmap->height;
	rect[2] = pixmap->format;
}

void Load_STB_Image_GetPixels(const int64_t handle, uint8_t* pixels)
{
	stb_pix* pixmap = (stb_pix*)handle;
	if (!pixmap) {
		fprintf(stderr, "Load_STB_Image_GetPixels Error !\n");
		return;
	}
	copy_uint8_array(pixels, sizeof(pixels), pixmap->pixels, sizeof(pixmap->pixels));
}

void convertSTBUint8ToInt32(const uint8_t* src, int32_t* dst, int width, int height, int format) {
	if (!src || !dst || width <= 0 || height <= 0 || format < 3) {
		fprintf(stderr, "convertSTBUint8ToInt32 Error !\n");
		return;
	}
	int length = width * height;
	for (int i = 0; i < length; i++) {
		uint8_t r = 0, g = 0, b = 0, a = 255;
		if (format == 1) { 
			r = g = b = src[i];
		} else if (format == 2) { 
			r = g = b = src[i * 2 + 0];
			a = src[i * 2 + 1];
		} else if (format == 3) { 
			r = src[i * 3 + 0];
			g = src[i * 3 + 1];
			b = src[i * 3 + 2];
		} else if (format == 4) {
			r = src[i * 4 + 0];
			g = src[i * 4 + 1];
			b = src[i * 4 + 2];
			a = src[i * 4 + 3];
		}
		dst[i] = ((int32_t)a << 24) | ((int32_t)r << 16) |
			((int32_t)g << 8) | (int32_t)b;
	}
}

void Load_STB_Image_GetDefaultPixels32(const int64_t handle, int width,int height,int32_t* pixels)
{
	stb_pix* pixmap = (stb_pix*)handle;
	if (!pixmap) {
		fprintf(stderr, "Load_STB_Image_GetDefaultPixels32 Error !\n");
		return;
	}
	const uint8_t* bytePixels = pixmap->pixels;
	convertSTBUint8ToInt32(bytePixels, pixels, width, height, pixmap->format);
}

void Load_STB_Image_GetPixels32(const int64_t handle, const int32_t format,int32_t* pixels)
{
	stb_pix* pixmap = (stb_pix*)handle;
	if (!pixmap) {
		fprintf(stderr, "Load_STB_Image_GetPixels32 Error !\n");
		return;
	}
	const uint8_t* bytePixels = pixmap->pixels;
	int width = pixmap->width, height = pixmap->height;
	if (!pixels) {
		fprintf(stderr, "Load_STB_Image_GetPixels32 Error !\n");
		return;
	}
	convertSTBUint8ToInt32(bytePixels, pixels, width, height, format);
}

int32_t Load_STB_Image_GetWidth(const int64_t handle)
{
	stb_pix* pixmap = (stb_pix*)handle;
	if (!pixmap) {
		fprintf(stderr, "Load_STB_Image_GetWidth Error !\n");
		return 0;
	}
	return pixmap->width;
}

int32_t Load_STB_Image_GetHeight(const int64_t handle)
{
	stb_pix* pixmap = (stb_pix*)handle;
	if (!pixmap) {
		fprintf(stderr, "Load_STB_Image_GetHeight Error !\n");
		return 0;
	}
	return pixmap->height;
}

int32_t Load_STB_Image_GetFormat(const int64_t handle)
{
	stb_pix* pixmap = (stb_pix*)handle;
	if (!pixmap) {
		fprintf(stderr, "Load_STB_Image_GetFormat Error !\n");
		return 0;
	}
	return pixmap->format;
}

const char* Load_STB_Image_FailureReason()
{
	return stbi_failure_reason();
}

int64_t Load_STB_LoadFontStyleInfo(const char* path, const char* fontName , const int style)
{
	SDL_RWops* rw = SDL_RWFromFile(path, "rb");
	if (!rw) {
		return 0;
	}
	Sint64 file_size = SDL_RWsize(rw);
	if (file_size < 0) {
		fprintf(stderr, "Load_STB_LoadFontStyleInfo Error !\n");
		SDL_RWclose(rw);
		return 0;
	}
	unsigned char* fontBuffer = (unsigned char*)malloc(file_size);
	if (SDL_RWread(rw, fontBuffer, file_size, 1) != 1) {
		fprintf(stderr, "Load_STB_LoadFontStyleInfo Error !\n");
		return 0;
	}
	SDL_RWclose(rw);
	int offset = stbtt_FindMatchingFont(fontBuffer, fontName, style);
	if (offset < 0) {
		offset = 0;
	}
	stb_font* temp_font = (stb_font*)malloc(sizeof(stb_font));
	if (!temp_font) {
		fprintf(stderr, "Load_STB_LoadFontStyleInfo Error !\n");
		free(temp_font);
		return 0;
	}
	temp_font->info = (stbtt_fontinfo*)malloc(sizeof(stbtt_fontinfo));
	if (!stbtt_InitFont(temp_font->info, fontBuffer, stbtt_GetFontOffsetForIndex(fontBuffer, offset))) {
		fprintf(stderr, "Failed to init font\n");
		free(fontBuffer);
		free(temp_font);
		return 0;
	}
	if (temp_font && temp_font->info && fontBuffer) {
		temp_font->info->userdata = fontBuffer;
	}
	_temp_fontinfo = temp_font;
	return (intptr_t)temp_font;
}

int64_t Load_STB_LoadSystemFontStyleInfo(const char* sysfontName, const char* path, const char* fontName , const int style)
{
	if (sysfontName && sysfontName[0] != '\0') { 	//有默认则优先尝试默认字体路径
        for (int i = 0; system_font_paths[i].name != NULL; i++) {
            if (strcmp(system_font_paths[i].name, fontName) == 0) {
                int64_t result = Load_STB_LoadFontStyleInfo(system_font_paths[i].path, fontName, style);
				if(result != 0){
                   return result;
				}
            }
        }
		//没有默认，穷举当前系统字体文件夹下对应字体
		char* fontPath = find_font_path(sysfontName);
		if (fontPath) {
			int64_t result = Load_STB_LoadFontStyleInfo(fontPath, fontName, style);
			if (result != 0) {
				return result;
			}
		}
    }
	
	//完全没有，尝试使用自备字体文件
	SDL_RWops* rw = SDL_RWFromFile(path, "rb");
	if (!rw) {
		fprintf(stderr, "Load_STB_LoadSystemFontStyleInfo Error !\n");
		return 0;
	}
	Sint64 file_size = SDL_RWsize(rw);
	if (file_size < 0) {
		fprintf(stderr, "Load_STB_LoadSystemFontStyleInfo Error !\n");
		SDL_RWclose(rw);
		return 0;
	}
	unsigned char* fontBuffer = (unsigned char*)malloc(file_size);
	if (SDL_RWread(rw, fontBuffer, file_size, 1) != 1) {
		fprintf(stderr, "Load_STB_LoadSystemFontStyleInfo Error !\n");
		return 0;
	}
	SDL_RWclose(rw);
	int offset = stbtt_FindMatchingFont(fontBuffer, fontName, style);
	if (offset < 0) {
		offset = 0;
	}
	stb_font* temp_font = (stb_font*)malloc(sizeof(stb_font));
	if (!temp_font) {
		fprintf(stderr, "Load_STB_LoadSystemFontStyleInfo Error !\n");
		free(temp_font);
		return 0;
	}
	temp_font->info = (stbtt_fontinfo*)malloc(sizeof(stbtt_fontinfo));
	if (!stbtt_InitFont(temp_font->info, fontBuffer, stbtt_GetFontOffsetForIndex(fontBuffer, offset))) {
		fprintf(stderr, "Failed to init font\n");
		free(fontBuffer);
		free(temp_font);
		return 0;
	}
	if (temp_font && temp_font->info && fontBuffer) {
		temp_font->info->userdata = fontBuffer;
	}
	_temp_fontinfo = temp_font;
	return (intptr_t)temp_font;
}

int64_t Load_STB_LoadFontInfo(const char* path)
{
	SDL_RWops* rw = SDL_RWFromFile(path, "rb");
	if (!rw) {
		return 0;
	}
	Sint64 file_size = SDL_RWsize(rw);
	if (file_size < 0) {
		fprintf(stderr, "Load_STB_LoadFontInfo Error !\n");
		SDL_RWclose(rw);
		return 0;
	}
	unsigned char* fontBuffer = (unsigned char*)malloc(file_size);
	if (SDL_RWread(rw, fontBuffer, file_size, 1) != 1) {
		fprintf(stderr, "Load_STB_LoadFontInfo Error !\n");
		return 0;
	}
	SDL_RWclose(rw);
    stb_font* temp_font = (stb_font*)malloc(sizeof(stb_font));
	if (!temp_font) {
		free(temp_font);
		return 0;
	}
	temp_font->info = (stbtt_fontinfo*)malloc(sizeof(stbtt_fontinfo));
	if (!stbtt_InitFont(temp_font->info, fontBuffer, stbtt_GetFontOffsetForIndex(fontBuffer, 0))) {
		fprintf(stderr, "Failed to init font\n");
		free(fontBuffer);
		free(temp_font);
		return 0;
	}
	if (temp_font && temp_font->info && fontBuffer) {
		temp_font->info->userdata = fontBuffer;
	}
	_temp_fontinfo = temp_font;
	return (intptr_t)temp_font;
}

void Load_STB_GetCodepointBitmapBox(const int64_t handle, const float fontsize, const int point,int* rect)
{
	stb_font* fontinfo = (stb_font*)handle;
	if (!fontinfo) {
		fontinfo = _temp_fontinfo;
		if (!fontinfo) {
			fprintf(stderr, "Load_STB_GetCodepointBitmapBox Error !\n");
			return;
		}
	}
	float scale = stbtt_ScaleForPixelHeight(fontinfo->info, fontsize);
	int x0, y0, x1, y1;
	stbtt_GetCodepointBitmapBox(fontinfo->info, point, scale, scale, &x0, &y0, &x1, &y1);
	rect[0] = x0;
	rect[1] = y0;
	rect[2] = x1;
	rect[3] = y1;
}

void Load_STB_GetFontVMetrics(const int64_t handle, const float fontsize, int *rect)
{
	stb_font* fontinfo = (stb_font*)handle;
	if (!fontinfo) {
		fontinfo = _temp_fontinfo;
		if (!fontinfo) {
			fprintf(stderr, "Load_STB_GetFontVMetrics Error !\n");
			return;
		}
	}
	int ascent, descent, lineGap;
	float scale = stbtt_ScaleForPixelHeight(fontinfo->info, fontsize);
	stbtt_GetFontVMetrics(fontinfo->info, &ascent, &descent, &lineGap);
	rect[0] = float_to_int_threshold(ascent * scale);
	rect[1] = float_to_int_threshold(descent * scale);
	rect[2] = float_to_int_threshold(lineGap * scale);
}

int Load_STB_GetCodepointHMetrics(const int64_t handle, const int point)
{
	stb_font* fontinfo = (stb_font*)handle;
	if (!fontinfo) {
		fontinfo = _temp_fontinfo;
		if (!fontinfo) {
			fprintf(stderr, "Load_STB_GetCodepointHMetrics Error !\n");
			return 0;
		}
	}
	int height;
	stbtt_GetCodepointHMetrics(fontinfo->info, point, &height, 0);
	return height;
}

static inline int get_char_size_subpixel(stbtt_fontinfo* font, uint32_t codepoint, float pixel_size,
	float shift_x, float shift_y,
	float* out_w, float* out_h, float* out_baseline_offset) {
	if (!font || !out_w || !out_h || !out_baseline_offset) return -1;

	int glyph_index = stbtt_FindGlyphIndex(font, codepoint);
	if (glyph_index == 0) {
		*out_w = *out_h = *out_baseline_offset = 0.0f;
		return -1;
	}

	float scale = stbtt_ScaleForPixelHeight(font, pixel_size);

	int advance_width, lsb;
	stbtt_GetGlyphHMetrics(font, glyph_index, &advance_width, &lsb);
	float logical_width = advance_width * scale;

	int x0, y0, x1, y1;
	stbtt_GetGlyphBitmapBoxSubpixel(font, glyph_index, scale, scale, shift_x, shift_y, &x0, &y0, &x1, &y1);

	float draw_width = (float)(x1 - x0);
	float draw_height = (float)(y1 - y0);

	float w = fmaxf(draw_width, logical_width);
	float h = draw_height;

	if (pixel_size < 20.0f) {
		int rounded_w = (int)roundf(w);
		if (rounded_w % 2 != 0) rounded_w += 1;
		w = (float)rounded_w;
		int rounded_h = (int)roundf(h);
		if (rounded_h % 2 != 0) rounded_h += 1;
		h = (float)rounded_h;
	}

	float baseline_offset = (float) - y0;

	*out_w = (float)fix_font_char_size(codepoint, pixel_size, (int)w);
	*out_h = h;
	*out_baseline_offset = baseline_offset;

	return 0;
}

 void Load_STB_GetCharsSize(const int64_t handle, float fontSize, const char* text, int* rect) {
	stb_font* fontinfo = (stb_font*)handle;
	if (!fontinfo) {
		fontinfo = _temp_fontinfo;
		if (!fontinfo) {
			return;
		}
	}
	float scale = stbtt_ScaleForPixelHeight(fontinfo->info, fontSize);
	int max_width = 0;
	int total_height = 0;

	int line_width = 0;
	int line_height = 0;

	int i = 0;
	while (text[i] && text[i] != '\n') {
		int bytes;
		uint32_t cp = utf8_to_codepoint_full(&text[i], &bytes);
		i += bytes;
		if (cp == '\n') {
			if (line_width > max_width) max_width = line_width;
			total_height += line_height;
			line_width = 0;
			line_height = 0;
			continue;
		}
		float cw, ch, baseline;
		get_char_size_subpixel(fontinfo->info, cp, fontSize, 0.0f, 0.0f, &cw, &ch, &baseline);
		line_width += (int)cw;
		if (ch > line_height) {
			line_height = (int)ch;
		}
		if (text[i + 1] && text[i + 1] != '\n') {
			line_width += float_to_int_threshold(stbtt_GetCodepointKernAdvance(fontinfo->info, cp, text[i + 1]) * scale);
		}
	}

	if (line_width > max_width) max_width = line_width;
	total_height += line_height;
	rect[0] = max_width;
	rect[1] = total_height;
}

 void Load_STB_GetCharSize(const int64_t handle, float fontSize, const int point, int* rect) {
	stb_font* fontinfo = (stb_font*)handle;
	if (!fontinfo) {
		fontinfo = _temp_fontinfo;
		if (!fontinfo) {
			fprintf(stderr, "Load_STB_GetCharSize Error !\n");
			return;
		}
	}
	float cw, ch, baseline;
	get_char_size_subpixel(fontinfo->info, point, fontSize, 0.0f, 0.0f, &cw, &ch, &baseline);
	rect[0] = (int)cw;
	rect[1] = (int)ch;
}

void Load_STB_MakeCodepointBitmap(const int64_t handle,const int point, const float scale, const int width, const int height,uint8_t* bitmap)
{
	if (!bitmap) {
		fprintf(stderr, "Load_STB_MakeCodepointBitmap Error !\n");
		return;
	}
	stb_font* fontinfo = (stb_font*)handle;
	if (!fontinfo) {
		fontinfo = _temp_fontinfo;
		if (!fontinfo) {
			return;
		}
	}
	stbtt_MakeCodepointBitmap(fontinfo->info, bitmap, width, height, width, scale, scale, point);
}

void convertU8toInt32(const uint8_t* src, int32_t* dst, int width, int height,int order, int32_t cr, int32_t cg, int32_t cb) {
	if (!src || !dst || width <= 0 || height <= 0) {
		fprintf(stderr, "convertU8toInt32 Error !\n");
		return;
	}
	for (int y = 0; y < height; y++) {
		for (int x = 0; x < width; x++) {
			uint8_t alpha = src[y * width + x];
			uint8_t r = (uint8_t)cr;
			uint8_t g = (uint8_t)cg;
			uint8_t b = (uint8_t)cb;
			if (order == 0) {
				dst[y * width + x] = ((int32_t)alpha << 24) | (r << 16) | (g << 8) | b;
			}
			else {
				dst[y * width + x] = (r << 24) | (g << 16) | (b << 8) | (int32_t)alpha;
			}
		}
	}
}

void Load_STB_MakeCodepointBitmap32(const int64_t handle, const int point, const float scale, const int width, const int height,const int r,const int g,const int b,int32_t* int32Pixels)
{
	unsigned char* bytePixels = (unsigned char*)calloc(width * height, sizeof(unsigned char));
	Load_STB_MakeCodepointBitmap(handle, point, scale, width, height, bytePixels);
	convertU8toInt32(bytePixels, int32Pixels, width, height, 0,r,g,b);
	free(bytePixels);
}

void Load_STB_MakeDrawTextToBitmap(const int64_t handle, const char* text, const float fontscale, const int width, const int height,uint8_t* bitmap)
{
	stb_font* fontinfo = (stb_font*)handle;
	if (!fontinfo) {
		fprintf(stderr, "Load_STB_MakeDrawTextToBitmap Error !\n");
		return;
	}
	float scale = stbtt_ScaleForPixelHeight(fontinfo->info, fontscale);
	int x = 0;
	int ascent, descent, lineGap;
	stbtt_GetFontVMetrics(fontinfo->info, &ascent, &descent, &lineGap);
	ascent = float_to_int_threshold(ascent * scale);
	descent = float_to_int_threshold(descent * scale);
	int i = 0;
	int newWidth = width;
	while (text[i] && text[i] != '\n') {
		int bytes;
		uint32_t cp = utf8_to_codepoint_full(&text[i], &bytes);
		i += bytes;
		int c_x1, c_y1, c_x2, c_y2;
		stbtt_GetCodepointBitmapBox(fontinfo->info, cp, scale, scale, &c_x1, &c_y1, &c_x2, &c_y2);
		int y = ascent + c_y1;
		int byteOffset = x + (y * newWidth);
		stbtt_MakeCodepointBitmap(fontinfo->info, bitmap + byteOffset, c_x2 - c_x1, c_y2 - c_y1, newWidth, scale, scale, cp);
		int ax;
		stbtt_GetCodepointHMetrics(fontinfo->info, cp, &ax, 0);
		x += float_to_int_threshold(ax * scale);
		int kern;
		kern = stbtt_GetCodepointKernAdvance(fontinfo->info, cp, text[i + 1]);
		x += float_to_int_threshold(kern * scale);
	}
}

void Load_STB_MakeDrawTextToBitmap32(const int64_t handle, const char* text, const float fontscale, const int width, const int height,const int r,const int g,const int b,int32_t* pixels)
{
	unsigned char* bytePixels = (unsigned char*)calloc(width * height, sizeof(unsigned char));
	Load_STB_MakeDrawTextToBitmap(handle, text, fontscale, width, height, bytePixels);
	convertU8toInt32(bytePixels, pixels, width, height, 0,r,g,b);
	free(bytePixels);
}

static float MeasureTextWidth(const stbtt_fontinfo* font, float pixel_height, const char* text) {
	float scale = stbtt_ScaleForPixelHeight(font, pixel_height);
	float width = 0.0f;
	uint32_t prev_cp = 0;
	while (*text) {
		uint32_t cp = utf8_decode(&text);
		if (prev_cp) width += stbtt_GetCodepointKernAdvance(font, prev_cp, cp) * scale;
		if (is_cjk(cp)) {
			int x0, y0, x1, y1;
			stbtt_GetCodepointBitmapBox(font, cp, scale, scale, &x0, &y0, &x1, &y1);
			width += (x1 - x0);
		}
		else {
			int advance, lsb;
			stbtt_GetCodepointHMetrics(font, cp, &advance, &lsb);
			width += advance * scale;
		}
		prev_cp = cp;
	}
	return width;
}

static float MeasureTextHeight(const stbtt_fontinfo* font, float pixel_height, const char* text) {
	float scale = stbtt_ScaleForPixelHeight(font, pixel_height);
	int min_y = 999999, max_y = -999999;
	while (*text) {
		uint32_t cp = utf8_decode(&text);
		int x0, y0, x1, y1;
		stbtt_GetCodepointBitmapBox(font, cp, scale, scale, &x0, &y0, &x1, &y1);
		if (y0 < min_y) min_y = y0;
		if (y1 > max_y) max_y = y1;
	}
	if (min_y == 999999 || max_y == -999999) return 0.0f;
	return (float)(max_y - min_y);
}

int32_t Load_STB_MeasureTextWidth(const int64_t handle, const char* text, const float fontscale) {
	stb_font* fontinfo = (stb_font*)handle;
	if (!fontinfo) {
		fprintf(stderr, "Load_STB_MeasureTextWidth Error !\n");
		return 0;
	}
	return float_to_int_threshold(MeasureTextWidth(fontinfo->info,fontscale, text));
}

int32_t Load_STB_MeasureTextHieght(const int64_t handle, const char* text, const float fontscale)
{
	stb_font* fontinfo = (stb_font*)handle;
	if (!fontinfo) {
		fprintf(stderr, "Load_STB_MeasureTextHieght Error !\n");
		return 0;
	}
	return float_to_int_threshold(MeasureTextHeight(fontinfo->info, fontscale, text));
}

void Load_STB_GetTextLinesSize(const int64_t handle, const char* text, const float fontscale, int32_t align, int32_t* rect)
{
	stb_font* fontinfo = (stb_font*)handle;
	if (!fontinfo) {
		fprintf(stderr, "Load_STB_GetTextLinesSize Error !\n");
		return;
	}
	int numLines;
	char** lines = split_lines(text, &numLines);
	float scale = stbtt_ScaleForPixelHeight(fontinfo->info, fontscale);
	int ascent, descent, lineGap;
	stbtt_GetFontVMetrics(fontinfo->info, &ascent, &descent, &lineGap);
	int lineHeight = float_to_int_threshold((ascent - descent + lineGap) * scale);

	int maxWidth = measure_max_width_lines(fontinfo->info, lines, numLines, scale);
	int totalHeight = lineHeight * numLines;
	rect[0] = maxWidth;
	rect[1] = totalHeight;
	free_lines(lines, numLines);
}

void Load_STB_DrawTextLinesToBytes(const int64_t handle, const char* text, const float fontscale, int32_t align, int32_t* outDims,uint8_t* bitmap)
{
	stb_font* fontinfo = (stb_font*)handle;
	if (!fontinfo) {
		fprintf(stderr, "Load_STB_DrawTextLinesToBytes Error !\n");
		return;
	}
	int numLines;
	char** lines = split_lines(text, &numLines);
    float scale = stbtt_ScaleForPixelHeight(fontinfo->info, fontscale);
	int ascent, descent, lineGap;
	stbtt_GetFontVMetrics(fontinfo->info, &ascent, &descent, &lineGap);
	int lineHeight = float_to_int_threshold((ascent - descent + lineGap) * scale);

	int maxWidth = measure_max_width_lines(fontinfo->info, lines, numLines, scale);
	int totalHeight = lineHeight * numLines;

	if (!bitmap) {
		fprintf(stderr, "Load_STB_DrawTextLinesToBytes Error !\n");
		return;
	}
	for (int i = 0; i < numLines; i++) {
		int lineWidth = measure_max_width_lines(fontinfo->info, &lines[i], 1, scale);
		int x = compute_alignment_offset(align, lineWidth, maxWidth);
		int baseline = float_to_int_threshold((ascent * scale) + i * lineHeight);
		const char* p = lines[i];

		while (*p) {
			int glyph = stbtt_FindGlyphIndex(fontinfo->info, *p);
			int advance, lsb;
			stbtt_GetGlyphHMetrics(fontinfo->info, glyph, &advance, &lsb);

			int gw, gh, gxoff, gyoff;
			unsigned char* gbitmap = stbtt_GetGlyphBitmap(fontinfo->info, scale, scale, glyph, &gw, &gh, &gxoff, &gyoff);

			for (int gy = 0; gy < gh; gy++) {
				for (int gx = 0; gx < gw; gx++) {
					int dstX = x + gx + gxoff;
					int dstY = baseline + gy + gyoff;
					if (dstX >= 0 && dstX < maxWidth && dstY >= 0 && dstY < totalHeight) {
						bitmap[dstY * maxWidth + dstX] = gbitmap[gy * gw + gx];
					}
				}
			}

			stbtt_FreeBitmap(gbitmap, NULL);
			x += float_to_int_threshold(advance * fontscale);

			if (*(p + 1)) {
				x += float_to_int_threshold(stbtt_GetGlyphKernAdvance(fontinfo->info, glyph,
					stbtt_FindGlyphIndex(fontinfo->info, *(p + 1))) * fontscale);
			}
			p++;
		}
	}
	if (outDims != NULL) {
		outDims[0] = maxWidth;
		outDims[1] = totalHeight;
	}
	free_lines(lines, numLines);
}

void Load_STB_DrawTextLinesToInt32(const int64_t handle, const char* text, const float fontscale, int32_t align, int32_t r, int32_t g, int32_t b, int32_t bgR, int32_t bgG, int32_t bgB,int32_t bgA, int32_t* outDims,int32_t* outPixels)
{
	stb_font* fontinfo = (stb_font*)handle;
	if (!fontinfo) {
		fprintf(stderr, "Load_STB_DrawTextLinesToInt32 Error !\n");
		return;
	}
	int numLines;
	char** lines = split_lines(text, &numLines);
    float scale = stbtt_ScaleForPixelHeight(fontinfo->info, fontscale);
	int ascent, descent, lineGap;
	stbtt_GetFontVMetrics(fontinfo->info, &ascent, &descent, &lineGap);
	int lineHeight = float_to_int_threshold((ascent - descent + lineGap) * scale);

	int maxWidth = measure_max_width_lines(fontinfo->info, lines, numLines, scale);
	int totalHeight = lineHeight * numLines;

	if (!outPixels) {
		fprintf(stderr, "Load_STB_DrawTextLinesToInt32 Error !\n");
		return;
	}
	for (int i = 0; i < maxWidth * totalHeight; i++) {
		outPixels[i] = (bgA & 0xFF << 24) | ((bgR & 0xFF) << 16) | ((bgG & 0xFF) << 8) | (bgB & 0xFF);
	}

	for (int i = 0; i < numLines; i++) {
		int lineWidth = measure_max_width_lines(fontinfo->info, &lines[i], 1, scale);
		int x = compute_alignment_offset(align, lineWidth, maxWidth);
		int baseline = float_to_int_threshold((ascent * scale) + i * lineHeight);
		const char* p = lines[i];

		while (*p) {
			int glyph = stbtt_FindGlyphIndex(fontinfo->info, *p);
			int advance, lsb;
			stbtt_GetGlyphHMetrics(fontinfo->info, glyph, &advance, &lsb);

			int gw, gh, gxoff, gyoff;
			unsigned char* gbitmap = stbtt_GetGlyphBitmap(fontinfo->info, scale, scale, glyph, &gw, &gh, &gxoff, &gyoff);

			for (int gy = 0; gy < gh; gy++) {
				for (int gx = 0; gx < gw; gx++) {
					int dstX = x + gx + gxoff;
					int dstY = baseline + gy + gyoff;
					if (dstX >= 0 && dstX < maxWidth && dstY >= 0 && dstY < totalHeight) {
						unsigned char alpha = gbitmap[gy * gw + gx];
						if (alpha > 0) {
							outPixels[dstY * maxWidth + dstX] =
								((alpha & 0xFF) << 24) |
								((r & 0xFF) << 16) |
								((g & 0xFF) << 8) |
								(b & 0xFF);
						}
					}
				}
			}

			stbtt_FreeBitmap(gbitmap, NULL);
			x += float_to_int_threshold(advance * fontscale);
			if (*(p + 1)) {
				x += float_to_int_threshold(stbtt_GetGlyphKernAdvance(fontinfo->info, glyph,
					stbtt_FindGlyphIndex(fontinfo->info, *(p + 1))) * fontscale);
			}
			p++;
		}
	}
	if (outDims != NULL) {
		outDims[0] = maxWidth;
		outDims[1] = totalHeight;
	}
	free_lines(lines, numLines);
}

void Load_STB_DrawChar(const int64_t handle, const int32_t codepoint, const float fontSize, const int32_t color, int32_t* outsize, int32_t* outPixels) {
	stb_font* fontinfo = (stb_font*)handle;
	if (!fontinfo) {
		fontinfo = _temp_fontinfo;
		if (!fontinfo) {
			fprintf(stderr, "Load_STB_DrawChar Error !\n");
			return;
		}
	}
	if (codepoint <= 0) return;
	stbtt_fontinfo* font = fontinfo->info;
	float scale = stbtt_ScaleForPixelHeight(font, fontSize);
	int ascent, descent, lineGap;
	stbtt_GetFontVMetrics(font, &ascent, &descent, &lineGap);

	int lineHeight = float_to_int_threshold((ascent - descent + lineGap) * scale);
	int offset = float_to_int_threshold(lineHeight - fontSize) * 2;
	
	int x0, y0, x1, y1;
	stbtt_GetCodepointBitmapBox(font, codepoint, scale, scale, &x0, &y0, &x1, &y1);
	int oldW = (x1 - x0);
	int oldH = (y1 - y0);
	int width = oldW + 20;
	int height = oldH + 20;

	int length = width * height;
	unsigned char* bitmap = (unsigned char*)calloc(length, 1);
	
	if (!bitmap) return;

	stbtt_MakeCodepointBitmap(font, bitmap + (lineHeight + y0 - offset) * width,
		oldW, oldH, width, scale, scale, codepoint);

	uint8_t A = (color >> 24) & 0xFF;
	uint8_t R = (color >> 16) & 0xFF;
	uint8_t G = (color >> 8) & 0xFF;
	uint8_t B = color & 0xFF;
	for (int i = 0; i < width * height; i++) {
		uint8_t glyph_alpha = bitmap[i];
		if (glyph_alpha == 0) {
			outPixels[i] = 0;
		}
		else {
			uint8_t final_a = (uint8_t)((glyph_alpha * A) / 255);
			uint8_t final_r = (uint8_t)((R * final_a) / 255);
			uint8_t final_g = (uint8_t)((G * final_a) / 255);
			uint8_t final_b = (uint8_t)((B * final_a) / 255);
			outPixels[i] = ((final_a & 0xFF) << 24) |
				((final_r & 0xFF) << 16) |
				((final_g & 0xFF) << 8) |
				(final_b & 0xFF);
		}
	}
	outsize[0] = width;
	outsize[1] = height;
	free(bitmap);
}

void Load_STB_DrawString(const int64_t handle, const char* text, const float fontSize, const int32_t color, int32_t* outsize, int32_t* outPixels)
{
	stb_font* fontinfo = (stb_font*)handle;
	if (!fontinfo) {
		fontinfo = _temp_fontinfo;
		if (!fontinfo) {
			fprintf(stderr, "Load_STB_DrawChar Error !\n");
			return;
		}
	}
	if (text == NULL) return;
	stbtt_fontinfo* font = fontinfo->info;
	
	float max_width = 0.0f;
	float total_height = 0.0f;

	const char* line_start = text;

	while (*line_start) {
		float line_width = 0.0f;
		float max_above = 0.0f, max_below = 0.0f;
		int i = 0;
		while (line_start[i] && line_start[i] != '\n') {
			int bytes;
			uint32_t codepoint = utf8_to_codepoint_full(&line_start[i], &bytes);
			i += bytes;
			float cw, ch, baseline;
			if (get_char_size_subpixel(font, codepoint, fontSize, 0.0f, 0.0f, &cw, &ch, &baseline) == 0) {
				line_width += cw;
				if (baseline > max_above) {
					max_above = baseline;
				}
				float below = ch - baseline;
				if (below > max_below) {
					max_below = below;
				}
			}
		}
		if (line_width > max_width) max_width = line_width;
		total_height += max_above + max_below;
		line_start += i;
		if (*line_start == '\n') {
			line_start++;
		}
	}
	
	float scale = stbtt_ScaleForPixelHeight(font, fontSize);
	int ascent, descent, lineGap;
	stbtt_GetFontVMetrics(font, &ascent, &descent, &lineGap);
	int maxBaseLine = float_to_int_threshold(ascent * scale);
	
	int img_w = (int)ceilf(max_width) + 4;
	int img_h = (int)ceilf(total_height) + 4;
	
	outsize[0] = img_w;
	outsize[1] = img_h;

	line_start = text;
	int y_cursor = 0;

	while (*line_start) {
		float line_width = 0.0f;
		float max_above = 0.0f, max_below = 0.0f;
		int i = 0;
		while (line_start[i] && line_start[i] != '\n') {
			int bytes; 
			uint32_t codepoint = utf8_to_codepoint_full(&line_start[i], &bytes);
			i += bytes;

			float cw, ch, baseline;
			if (get_char_size_subpixel(font, codepoint, fontSize, 0.0f, 0.0f,
				&cw, &ch, &baseline) == 0) {
				line_width += cw;
				if (baseline > max_above) max_above = baseline;
				float below = ch - baseline;
				if (below > max_below) max_below = below;
			}
		}

		int x_cursor = 0;
		int j = 0;
		while (line_start[j] && line_start[j] != '\n') {
			int bytes;
			uint32_t codepoint = utf8_to_codepoint_full(&line_start[j], &bytes);
			j += bytes;

			float cw, ch, baseline;
			if (get_char_size_subpixel(font, codepoint, fontSize, 0.0f, 0.0f, &cw, &ch, &baseline) != 0) {
				continue;
			}

			float scale = stbtt_ScaleForPixelHeight(font, fontSize);
			int glyph_index = stbtt_FindGlyphIndex(font, codepoint);

			int gw, gh;
			unsigned char* bitmap = stbtt_GetGlyphBitmapSubpixel(font, scale, scale, 0.0f, 0.0f,
				glyph_index, &gw, &gh, 0, 0);

			int dst_x = x_cursor;
			int dst_y = y_cursor + (int)(maxBaseLine - baseline);

			uint8_t a = (color >> 24) & 0xFF;
			uint8_t r = (color >> 16) & 0xFF;
			uint8_t g = (color >> 8) & 0xFF;
			uint8_t b = color & 0xFF;

			for (int py = 0; py < gh; py++) {
				for (int px = 0; px < gw; px++) {
					int dst_index = (dst_y + py) * img_w + (dst_x + px);
					if (dst_index < 0 || dst_index >= img_w * img_h) {
						continue;
					}
					uint8_t alpha = bitmap[py * gw + px];
					if (alpha > 0) {
						uint8_t final_a = (uint8_t)((alpha / 255.0f) * a);
						outPixels[dst_index] =
							(final_a << 24) |
							((r * final_a / 255) << 16) |
							((g * final_a / 255) << 8) |
							(b * final_a / 255);
					}
				}
			}

			stbtt_FreeBitmap(bitmap, NULL);
			x_cursor += (int)cw;
		}

		y_cursor += (int)(maxBaseLine + max_below);

		line_start += j;
		if (*line_start == '\n') {
			line_start++;
		}
	}
}

void Load_STB_CloseFontInfo(const int64_t handle)
{
	stb_font* fontinfo = (stb_font*)handle;
	if (!fontinfo) {
		fprintf(stderr, "Load_STB_CloseFontInfo Error !\n");
			return;
	}
	if (fontinfo->info->userdata) {
		free(fontinfo->info->userdata);
	}
	free(fontinfo->info);
	free(fontinfo);
}

void Call_STB_GetCodepointBitmapBox(const float fontsize, const int point, int32_t* rect)
{
	stb_font* fontinfo = _temp_fontinfo;
	if (!fontinfo) {
		fprintf(stderr, "Call_STB_GetCodepointBitmapBox Error !\n");
		return;
	}
	float scale = stbtt_ScaleForPixelHeight(fontinfo->info, fontsize);
	int x0, y0, x1, y1;
	stbtt_GetCodepointBitmapBox(fontinfo->info, point, scale, scale, &x0, &y0, &x1, &y1);
	rect[0] = x0;
	rect[1] = y0;
	rect[2] = x1;
	rect[3] = y1;
}

void Call_STB_GetFontVMetrics(const float fontsize,int32_t* rect)
{
	stb_font* fontinfo = _temp_fontinfo;
	if (!fontinfo) {
		fprintf(stderr, "Call_STB_GetFontVMetrics Error !\n");
		return;
	}
	float scale = stbtt_ScaleForPixelHeight(fontinfo->info, fontsize);
	int ascent, descent, lineGap;
	stbtt_GetFontVMetrics(fontinfo->info, &ascent, &descent, &lineGap);
	rect[0] = float_to_int_threshold(ascent * scale);
	rect[1] = float_to_int_threshold(descent * scale);
	rect[2] = float_to_int_threshold(lineGap * scale);
}

int Call_STB_GetCodepointHMetrics(const int point)
{
	stb_font* fontinfo = _temp_fontinfo;
	if (!fontinfo) {
		fprintf(stderr, "Call_STB_GetCodepointHMetrics Error !\n");
		return 0;
	}
	int height;
	stbtt_GetCodepointHMetrics(fontinfo->info, point, &height, 0);
	return height;
}

void Call_STB_MakeCodepointBitmap(const int point, const float scale, const int width, const int height,uint8_t* bitmap)
{
	if (!bitmap) {
		return;
	}
	stb_font* fontinfo = _temp_fontinfo;
	if (!fontinfo) {
		fprintf(stderr, "Call_STB_MakeCodepointBitmap Error !\n");
		return;
	}
	stbtt_MakeCodepointBitmap(fontinfo->info, bitmap, width, height, width, scale, scale, point);
}

void Call_STB_MakeDrawTextToBitmap(const char* text, const float fontscale, const int width, const int height, uint8_t* bitmap)
{
	stb_font* fontinfo = _temp_fontinfo;
	if (!fontinfo) {
		fprintf(stderr, "Call_STB_MakeDrawTextToBitmap Error !\n");
		return;
	}
	if (!bitmap) {
		fprintf(stderr, "Call_STB_MakeDrawTextToBitmap Error !\n");
		return;
	}
	float scale = stbtt_ScaleForPixelHeight(fontinfo->info, fontscale);
	int x = 0;
	int ascent, descent, lineGap;
	stbtt_GetFontVMetrics(fontinfo->info, &ascent, &descent, &lineGap);
	ascent = float_to_int_threshold(ascent * scale);
	descent = float_to_int_threshold(descent * scale);
	int i;
	for (i = 0; i < strlen(text); ++i)
	{
		int c_x1, c_y1, c_x2, c_y2;
		stbtt_GetCodepointBitmapBox(fontinfo->info, text[i], scale, scale, &c_x1, &c_y1, &c_x2, &c_y2);
		int y = ascent + c_y1;
		int byteOffset = x + (y * width);
		stbtt_MakeCodepointBitmap(fontinfo->info, bitmap + byteOffset, c_x2 - c_x1, c_y2 - c_y1, width, scale, scale, text[i]);
		int ax;
		stbtt_GetCodepointHMetrics(fontinfo->info, text[i], &ax, 0);
		x += float_to_int_threshold(ax * scale);
		int kern;
		kern = stbtt_GetCodepointKernAdvance(fontinfo->info, text[i], text[i + 1]);
		x += float_to_int_threshold(kern * scale);
	}
}

bool Call_STB_SaveArgbToPng(const char* filename, const int32_t* pixels, int32_t w, int32_t h) {
	uint8_t* rgba = (uint8_t*)malloc(w * h * 4);
	if (!rgba) return false;
	for (int i = 0; i < w * h; i++) {
		int32_t argb = pixels[i];
		uint8_t a = (argb >> 24) & 0xFF;
		uint8_t r = (argb >> 16) & 0xFF;
		uint8_t g = (argb >> 8) & 0xFF;
		uint8_t b = argb & 0xFF;
		rgba[i * 4 + 0] = r;
		rgba[i * 4 + 1] = g;
		rgba[i * 4 + 2] = b;
		rgba[i * 4 + 3] = a;
	}
	int result = stbi_write_png(filename, w, h, 4, rgba, w * 4);
	free(rgba);
	return true;
}

void Call_STB_CloseFontInfo()
{
	stb_font* fontinfo = _temp_fontinfo;
	if (!fontinfo) {
		fprintf(stderr, "Call_STB_CloseFontInfo Error !\n");
		return;
	}
	if (fontinfo->info->userdata) {
		free(fontinfo->info->userdata);
	}
	free(fontinfo->info);
	free(fontinfo);
}