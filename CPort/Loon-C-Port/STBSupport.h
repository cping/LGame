#ifndef LOON_STB
#define LOON_STB
#define STB_IMAGE_IMPLEMENTATION
#define STBI_FAILURE_USERMSG
#define STB_TRUETYPE_IMPLEMENTATION

#include "SDLSupport.h"
#include "stb_image.h"
#include "stb_truetype.h"

#ifdef __cplusplus
extern "C" {
#endif

typedef struct {
		int32_t width;
		int32_t height;
		int32_t format;
		const uint8_t* pixels;
} stb_pix;

typedef struct {
	stbtt_fontinfo* info;
	stbtt_packedchar* chars;
	SDL_Texture* atlas;
	int texture_size;
	float size;
	float scale;
	int ascent;
	int baseline;
} stb_font;


int64_t Load_STB_Image_LoadBytes(const int8_t* buffer, int32_t len);

int64_t Load_STB_Image_LoadPath(const char* path);

int64_t Load_STB_Image_LoadPathToSDLSurface(const char* path);

void Load_STB_Image_Free(const int64_t handle);

const uint8_t* Load_STB_Image_GetPixels(const int64_t handle);

int32_t* Load_STB_Image_GetPixels32(const int64_t handle);

int32_t Load_STB_Image_GetWidth(const int64_t handle);

int32_t Load_STB_Image_GetHeight(const int64_t handle);

int32_t Load_STB_Image_GetFormat(const int64_t handle);

const char* Load_STB_Image_FailureReason();

int64_t Load_STB_LoadFontInfo(const char* path, const char* fontName, const int style);

int* Load_STB_GetCodepointBitmapBox(const int64_t handle, const float fontsize, const int point);

int* Load_STB_GetFontVMetrics(const int64_t handle, const float fontsize);

int Load_STB_GetCodepointHMetrics(const int64_t handle, const int point);

uint8_t* Load_STB_MakeCodepointBitmap(const int64_t handle, const int point, const float scale, const int width, const int height);

uint8_t* Load_STB_MakeDrawTextToBitmap(const int64_t handle, const char* text, const float fontscale, const int width, const int height);

void Load_STB_CloseFontInfo(const int64_t handle);

#ifdef __cplusplus
}
#endif

#endif
