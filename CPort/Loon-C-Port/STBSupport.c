#include "STBSupport.h"

static stb_font *_temp_fontinfo;
static cache_surface* _temp_stbsurface;

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
		arr[idx++] = _strdup(token);
		token = chars_secure_strtok(NULL, "\n");
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
			width += (int)(advance * scale);
			if (*(p + 1)) {
				width += (int)(stbtt_GetGlyphKernAdvance(font, glyph, stbtt_FindGlyphIndex(font, *(p + 1))) * scale);
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
	int pitch = width * format;
	pitch = (pitch + 3) & ~3;
	int32_t rmask, gmask, bmask, amask;
#if SDL_BYTEORDER == SDL_LIL_ENDIAN
	rmask = 0x000000FF;
	gmask = 0x0000FF00;
	bmask = 0x00FF0000;
	amask = (format == 4) ? 0xFF000000 : 0;
#else
	int s = (bytesPerPixel == 4) ? 0 : 8;
	rmask = 0xFF000000 >> s;
	gmask = 0x00FF0000 >> s;
	bmask = 0x0000FF00 >> s;
	amask = 0x000000FF >> s;
#endif
	cache_surface* newsurface = (cache_surface*)malloc(sizeof(cache_surface));
	if (!newsurface) {
		fprintf(stderr, "Load_STB_Image_LoadPathToSDLSurface Error !\n");
		return 0;
	}
	SDL_Surface* newImage = SDL_CreateRGBSurfaceFrom(pixels, width, height, format * 8, pitch, rmask, gmask,
		bmask, amask);
	newsurface->surface_data = newImage;
	newsurface->width = newImage->w;
	newsurface->height = newImage->h;
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
	newsurface->width = converted->w;
	newsurface->height = converted->h;
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
		fprintf(stderr, "Load_STB_LoadFontStyleInfo Error !\n");
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

int64_t Load_STB_LoadFontInfo(const char* path)
{
	SDL_RWops* rw = SDL_RWFromFile(path, "rb");
	if (!rw) {
		fprintf(stderr, "Load_STB_LoadFontInfo Error !\n");
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
	float scale = stbtt_ScaleForPixelHeight(fontinfo->info, fontsize);
	int ascent, descent, lineGap;
	stbtt_GetFontVMetrics(fontinfo->info, &ascent, &descent, &lineGap);
	ascent *= (int)scale;
	descent *= (int)scale;
	rect[0] = ascent;
	rect[1] = descent;
	rect[2] = lineGap;
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

 void Load_STB_GetCharsSize(const int64_t handle, float fontSize, const char* text, int* rect) {
	stb_font* fontinfo = (stb_font*)handle;
	if (!fontinfo) {
		fontinfo = _temp_fontinfo;
		if (!fontinfo) {
			fprintf(stderr, "Load_STB_GetCharsSize Error !\n");
			return;
		}
	}
	int max_width = 0;
	int total_height = 0;

	int line_width = 0;
	int line_height = 0;

	for (int i = 0; text[i]; i++) {
		if (text[i] == '\n') {
			if (line_width > max_width) max_width = line_width;
			total_height += line_height;
			line_width = 0;
			line_height = 0;
			continue;
		}
		int x0, y0, x1, y1;
		stbtt_GetCodepointBitmapBox(fontinfo->info, text[i], fontSize, fontSize, &x0, &y0, &x1, &y1);
		line_width += (x1 - x0);
		if ((y1 - y0) > line_height) {
			line_height = (y1 - y0);
		}
		if (text[i + 1] && text[i + 1] != '\n') {
			line_width += (int)(stbtt_GetCodepointKernAdvance(fontinfo->info, text[i], text[i + 1]) * fontSize);
		}
	}

	if (line_width > max_width) max_width = line_width;
	total_height += line_height;
	rect[0] = max_width;
	rect[1] = total_height;
}

 void Load_STB_GetCharSize(const int64_t handle, float fontSize, const int point,int* rect) {
	stb_font* fontinfo = (stb_font*)handle;
	if (!fontinfo) {
		fontinfo = _temp_fontinfo;
		if (!fontinfo) {
			fprintf(stderr, "Load_STB_GetCharSize Error !\n");
			return;
		}
	}
	float scale = stbtt_ScaleForPixelHeight(fontinfo->info, fontSize);
	int x0, y0, x1, y1;
	stbtt_GetCodepointBitmapBox(fontinfo->info, point, scale, scale, &x0, &y0, &x1, &y1);
	rect[0] = (x1 - x0);
	rect[1] = (y1 - y0);
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
	ascent *= (int)scale;
	descent *= (int)scale;
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
		x += (int)(ax * scale);
		int kern;
		kern = stbtt_GetCodepointKernAdvance(fontinfo->info, text[i], text[i + 1]);
		x += (int)(kern * scale);
	}
}

void Load_STB_MakeDrawTextToBitmap32(const int64_t handle, const char* text, const float fontscale, const int width, const int height,const int r,const int g,const int b,int32_t* pixels)
{
	unsigned char* bytePixels = (unsigned char*)calloc(width * height, sizeof(unsigned char));
	Load_STB_MakeDrawTextToBitmap(handle, text, fontscale, width, height, bytePixels);
	convertU8toInt32(bytePixels, pixels, width, height, 0,r,g,b);
	free(bytePixels);
}

int32_t Load_STB_MeasureTextWidth(const int64_t handle, const char* text, const float fontscale) {
	stb_font* fontinfo = (stb_font*)handle;
	if (!fontinfo) {
		fprintf(stderr, "Load_STB_MeasureTextWidth Error !\n");
		return 0;
	}
	int width = 0;
	const char* p = text;
	while (*p) {
		int glyph = stbtt_FindGlyphIndex(fontinfo->info, *p);
		int advance, lsb;
		stbtt_GetGlyphHMetrics(fontinfo->info, glyph, &advance, &lsb);
		width += (int)(advance * fontscale);
		if (*(p + 1)) {
			width += (int)(stbtt_GetGlyphKernAdvance(fontinfo->info, glyph, stbtt_FindGlyphIndex(fontinfo->info, *(p + 1))) * fontscale);
		}
		p++;
	}
	return width;
}

int32_t Load_STB_MeasureTextHieght(const int64_t handle, const char* text, const float fontscale)
{
	stb_font* fontinfo = (stb_font*)handle;
	if (!fontinfo) {
		fprintf(stderr, "Load_STB_MeasureTextHieght Error !\n");
		return 0;
	}
	int ascent, descent, lineGap;
	stbtt_GetFontVMetrics(fontinfo->info, &ascent, &descent, &lineGap);
	return (int)((ascent - descent + lineGap) * fontscale);
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

	int ascent, descent, lineGap;
	stbtt_GetFontVMetrics(fontinfo->info, &ascent, &descent, &lineGap);
	int lineHeight = (int)((ascent - descent + lineGap) * fontscale);

	int maxWidth = measure_max_width_lines(fontinfo->info, lines, numLines, fontscale);
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

	int ascent, descent, lineGap;
	stbtt_GetFontVMetrics(fontinfo->info, &ascent, &descent, &lineGap);
	int lineHeight = (int)((ascent - descent + lineGap) * fontscale);

	int maxWidth = measure_max_width_lines(fontinfo->info, lines, numLines, fontscale);
	int totalHeight = lineHeight * numLines;

	if (!bitmap) {
		fprintf(stderr, "Load_STB_DrawTextLinesToBytes Error !\n");
		return;
	}
	for (int i = 0; i < numLines; i++) {
		int lineWidth = measure_max_width_lines(fontinfo->info, &lines[i], 1, fontscale);
		int x = compute_alignment_offset(align, lineWidth, maxWidth);
		int baseline = (int)(ascent * fontscale) + i * lineHeight;
		const char* p = lines[i];

		while (*p) {
			int glyph = stbtt_FindGlyphIndex(fontinfo->info, *p);
			int advance, lsb;
			stbtt_GetGlyphHMetrics(fontinfo->info, glyph, &advance, &lsb);

			int gw, gh, gxoff, gyoff;
			unsigned char* gbitmap = stbtt_GetGlyphBitmap(fontinfo->info, fontscale, fontscale, glyph, &gw, &gh, &gxoff, &gyoff);

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
			x += (int)(advance * fontscale);

			if (*(p + 1)) {
				x += (int)(stbtt_GetGlyphKernAdvance(fontinfo->info, glyph,
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
	//SetConsoleOutputCP(65001);
	//SetConsoleCP(65001);
	char** lines = split_lines(text, &numLines);

	int ascent, descent, lineGap;
	stbtt_GetFontVMetrics(fontinfo->info, &ascent, &descent, &lineGap);
	int lineHeight = (int)((ascent - descent + lineGap) * fontscale);

	int maxWidth = measure_max_width_lines(fontinfo->info, lines, numLines, fontscale);
	int totalHeight = lineHeight * numLines;

	if (!outPixels) {
		fprintf(stderr, "Load_STB_DrawTextLinesToInt32 Error !\n");
		return;
	}
	for (int i = 0; i < maxWidth * totalHeight; i++) {
		outPixels[i] = (bgA & 0xFF << 24) | ((bgR & 0xFF) << 16) | ((bgG & 0xFF) << 8) | (bgB & 0xFF);
	}

	for (int i = 0; i < numLines; i++) {
		int lineWidth = measure_max_width_lines(fontinfo->info, &lines[i], 1, fontscale);
		int x = compute_alignment_offset(align, lineWidth, maxWidth);
		int baseline = (int)(ascent * fontscale) + i * lineHeight;
		const char* p = lines[i];

		while (*p) {
			int glyph = stbtt_FindGlyphIndex(fontinfo->info, *p);
			int advance, lsb;
			stbtt_GetGlyphHMetrics(fontinfo->info, glyph, &advance, &lsb);

			int gw, gh, gxoff, gyoff;
			unsigned char* gbitmap = stbtt_GetGlyphBitmap(fontinfo->info, fontscale, fontscale, glyph, &gw, &gh, &gxoff, &gyoff);

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
			x += (int)(advance * fontscale);
			if (*(p + 1)) {
				x += (int)(stbtt_GetGlyphKernAdvance(fontinfo->info, glyph,
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
	ascent *= (int)scale;
	descent *= (int)scale;
	rect[0] = ascent;
	rect[1] = descent;
	rect[2] = lineGap;
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
	ascent *= (int)scale;
	descent *= (int)scale;
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
		x += (int)(ax * scale);
		int kern;
		kern = stbtt_GetCodepointKernAdvance(fontinfo->info, text[i], text[i + 1]);
		x += (int)(kern * scale);
	}
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