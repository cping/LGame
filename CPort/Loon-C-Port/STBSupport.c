#include "STBSupport.h"

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
	SDL_Surface* surface = SDL_CreateRGBSurfaceFrom(pixels, width, height, format * 8, pitch, rmask, gmask,
		bmask, amask);
	if (!surface)
	{
		return -1;
	}
	return (intptr_t)surface;
}

void Load_STB_Image_Free(const int64_t handle)
{
	stb_pix* pixmap = (stb_pix*)handle;
	if (!pixmap) {
		return;
	}
	free((void*)pixmap->pixels);
	free((void*)pixmap);
}

const uint8_t* Load_STB_Image_GetPixels(const int64_t handle)
{
	stb_pix* pixmap = (stb_pix*)handle;
	if (!pixmap) {
		return 0;
	}
	return pixmap->pixels;
}

int32_t* Load_STB_Image_GetPixels32(const int64_t handle)
{
	stb_pix* pixmap = (stb_pix*)handle;
	if (!pixmap) {
		return 0;
	}
	return (int32_t*)pixmap->pixels;
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

int64_t Load_STB_LoadFontInfo(const char* path, const char* fontName , const int style)
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
	const unsigned char* fontBuffer = malloc(file_size);
	if (SDL_RWread(rw, fontBuffer, file_size, 1) != 1) {
		return -1;
	}
	SDL_RWclose(rw);
	int offset = stbtt_FindMatchingFont(fontBuffer, fontName, style);
	if (offset < 0) {
		offset = 0;
	}
	stbtt_fontinfo font;
	if (!stbtt_InitFont(&font, fontBuffer, offset)) {
		fprintf(stderr, "Failed to init font\n");
		free(fontBuffer);
		return -1;
	}
	return *(int64_t*)&font;
}

int* Load_STB_GetCodepointBitmapBox(const int64_t handle, const float fontsize, const int point)
{
	stbtt_fontinfo* fontinfo = (stbtt_fontinfo*)handle;
	if (!fontinfo) {
		return 0;
	}
	float scale = stbtt_ScaleForPixelHeight(&fontinfo, fontsize);
	int x0, y0, x1, y1;
	stbtt_GetCodepointBitmapBox(&fontinfo, point, scale, scale, &x0, &y0, &x1, &y1);
	int rect[] = {x0,y0,x1,y1};
	return rect;
}

int* Load_STB_GetFontVMetrics(const int64_t handle, const float fontsize)
{
	stbtt_fontinfo* fontinfo = (stbtt_fontinfo*)handle;
	if (!fontinfo) {
		return 0;
	}
	float scale = stbtt_ScaleForPixelHeight(&fontinfo, fontsize);
	int ascent, descent, lineGap;
	stbtt_GetFontVMetrics(&fontinfo, &ascent, &descent, &lineGap);
	ascent *= scale;
	descent *= scale;
	int rect[] = { ascent,descent,lineGap };
	return rect;
}

int Load_STB_GetCodepointHMetrics(const int64_t handle, const int point)
{
	stbtt_fontinfo* fontinfo = (stbtt_fontinfo*)handle;
	if (!fontinfo) {
		return 0;
	}
	int height;
	stbtt_GetCodepointHMetrics(&fontinfo, point, &height, 0);
	return height;
}

uint8_t* Load_STB_MakeCodepointBitmap(const int64_t handle,const int point, const float scale, const int width, const int height)
{
	unsigned char* bitmap = calloc(width * height, sizeof(unsigned char));
	if (!bitmap) {
		return 0;
	}
	stbtt_fontinfo* fontinfo = (stbtt_fontinfo*)handle;
	if (!fontinfo) {
		return 0;
	}
	stbtt_MakeCodepointBitmap(&fontinfo, bitmap, width, height, width, scale, scale, point);
	return bitmap;
}

uint8_t* Load_STB_MakeDrawTextToBitmap(const int64_t handle, const char* text, const float fontscale, const int width, const int height)
{
	stbtt_fontinfo* fontinfo = (stbtt_fontinfo*)handle;
	if (!fontinfo) {
		return 0;
	}
	unsigned char* bitmap = (unsigned char*)calloc(width * height, sizeof(unsigned char));
	if (!bitmap) {
		return 0;
	}
	float scale = stbtt_ScaleForPixelHeight(&fontinfo, fontscale);
	int x = 0;
	int ascent, descent, lineGap;
	stbtt_GetFontVMetrics(&fontinfo, &ascent, &descent, &lineGap);
	ascent *= scale;
	descent *= scale;
	int i;
	for (i = 0; i < strlen(text); ++i)
	{
		int c_x1, c_y1, c_x2, c_y2;
		stbtt_GetCodepointBitmapBox(&fontinfo, text[i], scale, scale, &c_x1, &c_y1, &c_x2, &c_y2);
		int y = ascent + c_y1;
		int byteOffset = x + (y * width);
		stbtt_MakeCodepointBitmap(&fontinfo, bitmap + byteOffset, c_x2 - c_x1, c_y2 - c_y1, width, scale, scale, text[i]);
		int ax;
		stbtt_GetCodepointHMetrics(&fontinfo, text[i], &ax, 0);
		x += ax * scale;
		int kern;
		kern = stbtt_GetCodepointKernAdvance(&fontinfo, text[i], text[i + 1]);
		x += kern * scale;
	}
	return bitmap;
}

void Load_STB_CloseFontInfo(const int64_t handle)
{
	stbtt_fontinfo* fontinfo = (void*)handle;
	if (!fontinfo) {
		return;
	}
	free(fontinfo);
}
