#ifndef LOON_STB
#define LOON_STB
#define STB_IMAGE_IMPLEMENTATION
#define STBI_FAILURE_USERMSG

#include "SDLSupport.h"
#include "stb_image.h"
#include "stb_rect_pack.h"
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

#ifdef __cplusplus
}
#endif

#endif
