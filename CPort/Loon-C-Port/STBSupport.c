#include "STBSupport.h"

static stb_font *_temp_fontinfo;
static cache_surface* _temp_stbsurface;

void ImportSTBInclude()
{
}

int64_t Load_STB_Image_LoadBytes(const int8_t* buffer, int32_t len)
{
	int32_t width = 0, height = 0, format = 0;
	const unsigned char* pixels = stbi_load_from_memory((uint8_t*)buffer, len, &width, &height, &format, STBI_default);
	if (!pixels || width <= 0 || height <= 0) {
		return -1;
	}
	stb_pix* pixmap = (stb_pix*)malloc(sizeof(stb_pix));
	if (!pixmap) {
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
		return -1;
	}
	stb_pix* pixmap = (stb_pix*)malloc(sizeof(stb_pix));
	if (!pixmap) {
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
	void* pixels = stbi_load(path, &width, &height, &format, STBI_default);
	if (!pixels || width <= 0 || height <= 0) {
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
		return;
	}
	SDL_FreeSurface(_temp_stbsurface->surface_data);
	free(_temp_stbsurface);
}

void Load_STB_Image_Free(const int64_t handle)
{
	stb_pix* pixmap = (stb_pix*)handle;
	if (!pixmap) {
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

const uint8_t* Load_STB_Image_GetPixels(const int64_t handle)
{
	stb_pix* pixmap = (stb_pix*)handle;
	if (!pixmap) {
		return 0;
	}
	return pixmap->pixels;
}

void convertSTBUint8ToInt32(const uint8_t* src, int32_t* dst, int width, int height, int format) {
	if (!src || !dst || width <= 0 || height <= 0 || format < 3) {
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

int32_t* Load_STB_Image_GetDefaultPixels32(const int64_t handle)
{
	stb_pix* pixmap = (stb_pix*)handle;
	if (!pixmap) {
		return 0;
	}
	const uint8_t* bytePixels = pixmap->pixels;
	int width = pixmap->width, height = pixmap->height;
	int length = width * height;
	int32_t* pixelsInt32 = malloc(length * sizeof(int32_t));
	if (!pixelsInt32) {
		return 0;
	}
	convertSTBUint8ToInt32(bytePixels, pixelsInt32, width, height, pixmap->format);
	return pixelsInt32;
}

int32_t* Load_STB_Image_GetPixels32(const int64_t handle, const int32_t format)
{
	stb_pix* pixmap = (stb_pix*)handle;
	if (!pixmap) {
		return 0;
	}
	const uint8_t* bytePixels = pixmap->pixels;
	int width = pixmap->width, height = pixmap->height;
	int length = width * height;
	int32_t* pixelsInt32 = malloc(length * sizeof(int32_t));
	if (!pixelsInt32) {
		return 0;
	}
	convertSTBUint8ToInt32(bytePixels, pixelsInt32, width, height, format);
	return pixelsInt32;
}

int32_t Load_STB_Image_GetWidth(const int64_t handle)
{
	stb_pix* pixmap = (stb_pix*)handle;
	if (!pixmap) {
		return 0;
	}
	return pixmap->width;
}

int32_t Load_STB_Image_GetHeight(const int64_t handle)
{
	stb_pix* pixmap = (stb_pix*)handle;
	if (!pixmap) {
		return 0;
	}
	return pixmap->height;
}

int32_t Load_STB_Image_GetFormat(const int64_t handle)
{
	stb_pix* pixmap = (stb_pix*)handle;
	if (!pixmap) {
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
		SDL_Log("Failed to open file '%s': %s", path, SDL_GetError());
		return -1;
	}
	Sint64 file_size = SDL_RWsize(rw);
	if (file_size < 0) {
		SDL_Log("Failed to get file size: %s", SDL_GetError());
		SDL_RWclose(rw);
		return -1;
	}
	unsigned char* fontBuffer = malloc(file_size);
	if (SDL_RWread(rw, fontBuffer, file_size, 1) != 1) {
		return -1;
	}
	SDL_RWclose(rw);
	int offset = stbtt_FindMatchingFont(fontBuffer, fontName, style);
	if (offset < 0) {
		offset = 0;
	}
	stb_font* temp_font = malloc(sizeof(stb_font));
	if (!temp_font) {
		return -1;
	}
	temp_font->info = malloc(sizeof(stbtt_fontinfo));
	if (!stbtt_InitFont(temp_font->info, fontBuffer, 0)) {
		fprintf(stderr, "Failed to init font\n");
		free(fontBuffer);
		return -1;
	}
	_temp_fontinfo = temp_font;
	return (intptr_t)&temp_font;
}

int64_t Load_STB_LoadFontInfo(const char* path)
{
	SDL_RWops* rw = SDL_RWFromFile(path, "rb");
	if (!rw) {
		SDL_Log("Failed to open file '%s': %s", path, SDL_GetError());
		return -1;
	}
	Sint64 file_size = SDL_RWsize(rw);
	if (file_size < 0) {
		SDL_Log("Failed to get file size: %s", SDL_GetError());
		SDL_RWclose(rw);
		return -1;
	}
	unsigned char* fontBuffer = malloc(file_size);
	if (SDL_RWread(rw, fontBuffer, file_size, 1) != 1) {
		return -1;
	}
	SDL_RWclose(rw);
    stb_font* temp_font = malloc(sizeof(stb_font));
	if (!temp_font) {
		return -1;
	}
	temp_font->info = malloc(sizeof(stbtt_fontinfo));
	if (!stbtt_InitFont(temp_font->info, fontBuffer, 0)) {
		fprintf(stderr, "Failed to init font\n");
		free(fontBuffer);
		return -1;
	}
	_temp_fontinfo = temp_font;
	return (intptr_t)&temp_font;
}

int* Load_STB_GetCodepointBitmapBox(const int64_t handle, const float fontsize, const int point)
{
	stb_font* fontinfo = (stb_font*)handle;
	if (!fontinfo) {
		fontinfo = _temp_fontinfo;
		if (!fontinfo) {
			return 0;
		}
	}
	float scale = stbtt_ScaleForPixelHeight(fontinfo->info, fontsize);
	int x0, y0, x1, y1;
	stbtt_GetCodepointBitmapBox(fontinfo->info, point, scale, scale, &x0, &y0, &x1, &y1);
	int rect[] = {x0,y0,x1,y1};
	return rect;
}

int* Load_STB_GetFontVMetrics(const int64_t handle, const float fontsize)
{
	stb_font* fontinfo = (stb_font*)handle;
	if (!fontinfo) {
		fontinfo = _temp_fontinfo;
		if (!fontinfo) {
			return 0;
		}
	}
	float scale = stbtt_ScaleForPixelHeight(fontinfo->info, fontsize);
	int ascent, descent, lineGap;
	stbtt_GetFontVMetrics(fontinfo->info, &ascent, &descent, &lineGap);
	ascent *= scale;
	descent *= scale;
	int rect[] = { ascent,descent,lineGap };
	return rect;
}

int Load_STB_GetCodepointHMetrics(const int64_t handle, const int point)
{
	stb_font* fontinfo = (stb_font*)handle;
	if (!fontinfo) {
		fontinfo = _temp_fontinfo;
		if (!fontinfo) {
			return 0;
		}
	}
	int height;
	stbtt_GetCodepointHMetrics(fontinfo->info, point, &height, 0);
	return height;
}

uint8_t* Load_STB_MakeCodepointBitmap(const int64_t handle,const int point, const float scale, const int width, const int height)
{
	unsigned char* bitmap = calloc(width * height, sizeof(unsigned char));
	if (!bitmap) {
		return 0;
	}
	stb_font* fontinfo = (stb_font*)handle;
	if (!fontinfo) {
		fontinfo = _temp_fontinfo;
		if (!fontinfo) {
			return 0;
		}
	}
	stbtt_MakeCodepointBitmap(fontinfo->info, bitmap, width, height, width, scale, scale, point);
	return bitmap;
}

void convertU8toInt32(const uint8_t* src, int32_t* dst, int width, int height, int order) {
	if (!src || !dst || width <= 0 || height <= 0) {
		return;
	}
	for (int y = 0; y < height; y++) {
		for (int x = 0; x < width; x++) {
			uint8_t alpha = src[y * width + x];
			uint8_t r = 255;
			uint8_t g = 255;
			uint8_t b = 255;
			if (order == 0) {
				dst[y * width + x] = ((int32_t)alpha << 24) | (r << 16) | (g << 8) | b;
			}
			else {
				dst[y * width + x] = (r << 24) | (g << 16) | (b << 8) | (int32_t)alpha;
			}
		}
	}
}

int32_t* Load_STB_MakeCodepointBitmap32(const int64_t handle, const int point, const float scale, const int width, const int height)
{
	uint8_t* bytePixels =  Load_STB_MakeCodepointBitmap(handle, point, scale, width, height);
	int32_t* int32Pixels = malloc(width * height * sizeof(int32_t));
	convertU8toInt32(bytePixels, int32Pixels, width, height, 0);
	free(bytePixels);
	return int32Pixels;
}

uint8_t* Load_STB_MakeDrawTextToBitmap(const int64_t handle, const char* text, const float fontscale, const int width, const int height)
{
	stb_font* fontinfo = (stb_font*)handle;
	if (!fontinfo) {
		return 0;
	}
	unsigned char* bitmap = (unsigned char*)calloc(width * height, sizeof(unsigned char));
	if (!bitmap) {
		fontinfo = _temp_fontinfo;
		if (!fontinfo) {
			return 0;
		}
	}
	float scale = stbtt_ScaleForPixelHeight(fontinfo->info, fontscale);
	int x = 0;
	int ascent, descent, lineGap;
	stbtt_GetFontVMetrics(fontinfo->info, &ascent, &descent, &lineGap);
	ascent *= scale;
	descent *= scale;
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
		x += ax * scale;
		int kern;
		kern = stbtt_GetCodepointKernAdvance(fontinfo->info, text[i], text[i + 1]);
		x += kern * scale;
	}
	return bitmap;
}

int32_t* Load_STB_MakeDrawTextToBitmap32(const int64_t handle, const char* text, const float fontscale, const int width, const int height)
{
	uint8_t* bytePixels = Load_STB_MakeDrawTextToBitmap(handle, text, fontscale, width, height);
	int32_t* int32Pixels = malloc(width * height * sizeof(int32_t));
	convertU8toInt32(bytePixels, int32Pixels, width, height, 0);
	free(bytePixels);
	return int32Pixels;
}

void Load_STB_CloseFontInfo(const int64_t handle)
{
	stb_font* fontinfo = (stb_font*)handle;
	if (!fontinfo) {
			return;
	}
	free(fontinfo->info);
	free(fontinfo);
}

int* Call_STB_GetCodepointBitmapBox(const float fontsize, const int point)
{
	stb_font* fontinfo = _temp_fontinfo;
	if (!fontinfo) {
		return 0;
	}
	float scale = stbtt_ScaleForPixelHeight(fontinfo->info, fontsize);
	int x0, y0, x1, y1;
	stbtt_GetCodepointBitmapBox(fontinfo->info, point, scale, scale, &x0, &y0, &x1, &y1);
	int rect[] = { x0,y0,x1,y1 };
	return rect;
}

int* Call_STB_GetFontVMetrics(const float fontsize)
{
	stb_font* fontinfo = _temp_fontinfo;
	if (!fontinfo) {
		return 0;
	}
	float scale = stbtt_ScaleForPixelHeight(fontinfo->info, fontsize);
	int ascent, descent, lineGap;
	stbtt_GetFontVMetrics(fontinfo->info, &ascent, &descent, &lineGap);
	ascent *= scale;
	descent *= scale;
	int rect[] = { ascent,descent,lineGap };
	return rect;
}

int Call_STB_GetCodepointHMetrics(const int point)
{
	stb_font* fontinfo = _temp_fontinfo;
	if (!fontinfo) {
		return 0;
	}
	int height;
	stbtt_GetCodepointHMetrics(fontinfo->info, point, &height, 0);
	return height;
}

uint8_t* Call_STB_MakeCodepointBitmap(const int point, const float scale, const int width, const int height)
{
	unsigned char* bitmap = calloc(width * height, sizeof(unsigned char));
	if (!bitmap) {
		return 0;
	}
	stb_font* fontinfo = _temp_fontinfo;
	if (!fontinfo) {
		return 0;
	}
	stbtt_MakeCodepointBitmap(fontinfo->info, bitmap, width, height, width, scale, scale, point);
	return bitmap;
}

uint8_t* Call_STB_MakeDrawTextToBitmap(const char* text, const float fontscale, const int width, const int height)
{
	stb_font* fontinfo = _temp_fontinfo;
	if (!fontinfo) {
		return 0;
	}
	unsigned char* bitmap = (unsigned char*)calloc(width * height, sizeof(unsigned char));
	if (!bitmap) {
		return 0;
	}
	float scale = stbtt_ScaleForPixelHeight(fontinfo->info, fontscale);
	int x = 0;
	int ascent, descent, lineGap;
	stbtt_GetFontVMetrics(fontinfo->info, &ascent, &descent, &lineGap);
	ascent *= scale;
	descent *= scale;
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
		x += ax * scale;
		int kern;
		kern = stbtt_GetCodepointKernAdvance(fontinfo->info, text[i], text[i + 1]);
		x += kern * scale;
	}
	return bitmap;
}

void Call_STB_CloseFontInfo()
{
	stb_font* fontinfo = _temp_fontinfo;
	if (!fontinfo) {
		return;
	}
	free(fontinfo->info);
	free(fontinfo);
}