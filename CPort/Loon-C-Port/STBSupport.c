#include "STBSupport.h"

static stb_font *_temp_fontinfo;
static cache_surface* _temp_stbsurface;

#if defined(_WIN32)
FontEntry system_font_paths[] = {
	{"微软雅黑", "C:\\Windows\\Fonts\\msyh.ttc"},
	{"微软雅黑 Bold", "C:\\Windows\\Fonts\\msyhbd.ttc"},
	{"黑体", "C:\\Windows\\Fonts\\simhei.ttf"},
	{"宋体", "C:\\Windows\\Fonts\\simsun.ttc"},
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
	{"宋体", "/System/Library/Fonts/Supplemental/Songti.ttc"},
	{"黑体", "/System/Library/Fonts/Supplemental/Heiti.ttc"},
	{"Times New Roman", "/System/Library/Fonts/Supplemental/Times New Roman.ttf"},
	{"Courier New", "/System/Library/Fonts/Supplemental/Courier New.ttf"},
	{"Apple Color Emoji", "/System/Library/Fonts/Supplemental/Apple Color Emoji.ttc"},
	{"苹方", "/System/Library/Fonts/Supplemental/PingFang.ttc"},
	{NULL, NULL}
};
#elif defined(__linux__)
FontEntry system_font_paths[] = {
	{"DejaVu Sans", "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf"},
	{"DejaVu Sans Bold", "/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf"},
	{"Liberation Sans", "/usr/share/fonts/truetype/liberation/LiberationSans-Regular.ttf"},
	{"Liberation Sans Bold", "/usr/share/fonts/truetype/liberation/LiberationSans-Bold.ttf"},
	{"黑体", "/usr/share/fonts/truetype/noto/NotoSansCJK-Regular.ttc"},
	{"Noto Sans CJK", "/usr/share/fonts/truetype/noto/NotoSansCJK-Regular.ttc"},
	{"Noto Color Emoji", "/usr/share/fonts/truetype/noto/NotoColorEmoji.ttf"},
	{"FreeSans", "/usr/share/fonts/truetype/freefont/FreeSans.ttf"},
	{"Ubuntu", "/usr/share/fonts/truetype/ubuntu/Ubuntu-R.ttf"},
	{NULL, NULL}
};
#elif defined(__ANDROID__)
FontEntry system_font_paths[] = {
	{"黑体", "/system/fonts/NotoSansCJK-Regular.ttc"},
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
	{"黑体", "/System/Library/Fonts/Core/Heiti.ttc"},
	{"苹方", "/System/
#elif defined(__IPHONE_OS__) || defined(__IOS__)
FontEntry system_font_paths[] = {
	{"Arial", "/System/Library/Fonts/Core/Arial.ttf"},
	{"Arial Bold", "/System/Library/Fonts/Core/Arial Bold.ttf"},
	{"Apple Color Emoji", "/System/Library/Fonts/Core/AppleColorEmoji.ttf"},
	{"黑体", "/System/Library/Fonts/Core/Heiti.ttc"},
	{"苹方", "/System/Library/Fonts/Core/PingFang.ttc"},
	{NULL, NULL}
};
#elif defined(__SWITCH__)
FontEntry system_font_paths[] = {
	{"Nintendo Standard", "romfs:/font/nintendo_std.ttf"},
	{"Nintendo Extended", "romfs:/font/nintendo_ext.ttf"},
	{"黑体", "sdmc:/switch/fonts/NotoSansCJK-Regular.ttc"},
	{"Noto Sans CJK", "sdmc:/switch/fonts/NotoSansCJK-Regular.ttc"},
	{NULL, NULL}
};
#elif defined(__ORBIS__) || defined(__PROSPERO__) 
FontEntry system_font_paths[] = {
	{"黑体", "/app0/fonts/SCE-PS5-UI-Bold.otf"},
	{"SCE UI Regular", "/app0/fonts/SCE-PS5-UI-Regular.otf"},
	{"SCE UI Bold", "/app0/fonts/SCE-PS5-UI-Bold.otf"},
	{"SCE Emoji", "/app0/fonts/SCE-PS5-Emoji.ttf"},
	{NULL, NULL}
};
#elif defined(_XBOX_ONE) || defined(_GAMING_XBOX)
FontEntry system_font_paths[] = {
	{"黑体", "D:\\Windows\\Fonts\\segoeui.ttf"},
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
	uint8_t* pixels = stbi_load(path, &width, &height, &format, STBI_rgb_alpha);
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
	int width, height, channels;
	unsigned char* pixels = stbi_load(path, &width, &height, &channels, STBI_rgb_alpha);
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
	if (pixmap->pixels != NULL) {
		free((void*)pixmap->pixels);
	}
	if (pixmap != NULL) {
		free((void*)pixmap);
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
		uint8_t r = src[i * format + 0];
		uint8_t g = src[i * format + 1];
		uint8_t b = src[i * format + 2];
		uint8_t a = (format >= 4) ? src[i * format + 3] : 255;
		dst[i] = ((int32_t)a << 24) | ((int32_t)r << 16) | ((int32_t)g << 8) | (int32_t)b;
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

int64_t Load_STB_LoadSystemFontStyleInfo(const char* sysfontName, const char* path, const char* fontName , const int style)
{
	 if (sysfontName && sysfontName[0] != '\0') {
        for (int i = 0; system_font_paths[i].name != NULL; i++) {
            if (strcmp(system_font_paths[i].name, sysfontName) == 0) {
                int64_t result = Load_STB_LoadFontStyleInfo(system_font_paths[i].path, sysfontName, style);
				if(result != 0){
                   return result;
				}
            }
        }
    }
    for (int i = 0; system_font_paths[i].name != NULL; i++) {
        int64_t result = Load_STB_LoadFontStyleInfo(system_font_paths[i].path, sysfontName, style);
        if (result != 0) {
            return result;
        }
    }
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
			fprintf(stderr, "Load_STB_GetCharsSize Error !\n");
			return;
		}
	}
	float scale = stbtt_ScaleForPixelHeight(fontinfo->info, fontSize);
	int max_width = 0;
	int total_height = 0;

	int line_width = 0;
	int line_height = 0;

	for (int i = 0; text[i]; i++) {
		int point = text[i];
		if (point == '\n') {
			if (line_width > max_width) max_width = line_width;
			total_height += line_height;
			line_width = 0;
			line_height = 0;
			continue;
		}
		float cw, ch, baseline;
		get_char_size_subpixel(fontinfo->info, point, fontSize, 0.0f, 0.0f, &cw, &ch, &baseline);
		line_width += cw;
		if (ch > line_height) {
			line_height = ch;
		}
		if (text[i + 1] && text[i + 1] != '\n') {
			line_width += float_to_int_threshold(stbtt_GetCodepointKernAdvance(fontinfo->info, text[i], text[i + 1]) * scale);
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
	int i;
	int newWidth = width;
	for (i = 0; i < strlen(text); ++i)
	{
		int c_x1, c_y1, c_x2, c_y2;
		stbtt_GetCodepointBitmapBox(fontinfo->info, text[i], scale, scale, &c_x1, &c_y1, &c_x2, &c_y2);
		int y = ascent + c_y1;
		int byteOffset = x + (y * newWidth);
		stbtt_MakeCodepointBitmap(fontinfo->info, bitmap + byteOffset, c_x2 - c_x1, c_y2 - c_y1, newWidth, scale, scale, text[i]);
		int ax;
		stbtt_GetCodepointHMetrics(fontinfo->info, text[i], &ax, 0);
		x += float_to_int_threshold(ax * scale);
		int kern;
		kern = stbtt_GetCodepointKernAdvance(fontinfo->info, text[i], text[i + 1]);
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
	int baseline = float_to_int_threshold(ascent * scale);
	
	int x0, y0, x1, y1;
	stbtt_GetCodepointBitmapBox(font, codepoint, scale, scale, &x0, &y0, &x1, &y1);
	int oldW = (x1 - x0);
	int oldH = (y1 - y0);
	int width = oldW + 20;
	int height = oldH + 20;

	int length = width * height;
	unsigned char* bitmap = (unsigned char*)calloc(length, 1);
	
	if (!bitmap) return;

	stbtt_MakeCodepointBitmap(font, bitmap + (baseline + y0 + 2) * width + 2,
		x1 - x0, y1 - y0, width, scale, scale, codepoint);

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
			int bytes; uint32_t codepoint = utf8_to_codepoint_full(&line_start[i], &bytes);
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