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
