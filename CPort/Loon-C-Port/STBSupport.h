#pragma once
#ifndef LOON_STB
#define LOON_STB
#define STB_IMAGE_IMPLEMENTATION
#define STB_IMAGE_WRITE_IMPLEMENTATION
#define STBI_FAILURE_USERMSG
#define STB_TRUETYPE_IMPLEMENTATION

#include "SDLSupport.h"
#include "stb_image.h"
#include "stb_image_write.h"
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
} stb_font;

void ImportSTBInclude();

void Load_STB_InputDialog(int64_t handle,const int dialogType,const int width,const int height, const char* title, const char* text, const char* textA, const char* textB);

bool Load_STB_Dialog_YesOrNO();

const char* Load_STB_Dialog_InputText();

int64_t Load_STB_Image_LoadBytes(const uint8_t* buffer, int32_t len);

int64_t Load_STB_Image_LoadPath(const char* path);

int64_t Load_STB_Image_LoadPathToSDLSurface(const char* path);

int64_t Load_STB_Image_LoadSDLSurfaceARGB32(const char* path);

void Load_STB_TempSurfaceFree();

void Load_STB_Image_Free(const int64_t handle);

void Load_STB_Image_GetPixels(const int64_t handle, uint8_t* pixels);

void Load_STB_Image_GetDefaultPixels32(const int64_t handle, int w,int h, int32_t* pixels);

void Load_STB_Image_GetPixels32(const int64_t handle , const int32_t format, int32_t* pixels);

int32_t Load_STB_Image_GetWidth(const int64_t handle);

int32_t Load_STB_Image_GetHeight(const int64_t handle);

int32_t Load_STB_Image_GetFormat(const int64_t handle);

void Load_STB_Image_GetSizeFormat(const int64_t handle, int32_t* rect);

const char* Load_STB_Image_FailureReason();

int64_t Load_STB_LoadFontInfo(const char* path);

int64_t Load_STB_LoadFontStyleInfo(const char* path, const char* fontName, const int style);

int64_t Load_STB_LoadSystemFontStyleInfo(const char* sysfontName, const char* path, const char* fontName , const int style);

void Load_STB_GetCodepointBitmapBox(const int64_t handle, const float fontsize, const int point,int* rect);

void Load_STB_GetFontVMetrics(const int64_t handle, const float fontsize,int* rect);

int Load_STB_GetCodepointHMetrics(const int64_t handle, const int point);

void Load_STB_GetCharsSize(const int64_t handle, float fontSize, const char* text, int* rect);

void Load_STB_GetCharSize(const int64_t handle, float fontSize, const int point, int* rect);

void Load_STB_MakeCodepointBitmap(const int64_t handle, const int point, const float scale, const int width, const int height,uint8_t* bytes);

void Load_STB_MakeDrawTextToBitmap(const int64_t handle, const char* text, const float fontscale, const int width, const int height, uint8_t* bitmap);

void Load_STB_MakeCodepointBitmap32(const int64_t handle, const int point, const float scale, const int width, const int height, const int r, const int g, const int b,int32_t* pixels);

void Load_STB_MakeDrawTextToBitmap32(const int64_t handle, const char* text, const float fontscale, const int width, const int height, const int r,const int g,const int b, int32_t* pixels);

int32_t Load_STB_MeasureTextWidth(const int64_t handle, const char* text, const float fontscale);

int32_t Load_STB_MeasureTextHieght(const int64_t handle, const char* text, const float fontscale);

void Load_STB_DrawChar(const int64_t handle, const int32_t codepoint, const float fontSize, const int32_t color, int32_t* outsize, int32_t* outPixels);

void Load_STB_DrawString(const int64_t handle, const char* text, const float fontSize, const int32_t color, int32_t* outsize, int32_t* outPixels);

void Load_STB_DrawTextLinesToBytes(const int64_t handle, const char* text, const float fontscale, int32_t align, int32_t* outDims, uint8_t* bitmap);

void Load_STB_DrawTextLinesToInt32(const int64_t handle, const char* text, const float fontscale, int32_t align, int32_t r, int32_t g, int32_t b, int32_t bgR, int32_t bgG, int32_t bgB, int32_t bgA,int32_t* outDims, int32_t* pixels);

void Load_STB_GetTextLinesSize(const int64_t handle, const char* text, const float fontscale, int32_t align, int32_t* rect);

void Load_STB_CloseFontInfo(const int64_t handle);

void Call_STB_GetCodepointBitmapBox(const float fontsize, const int point,int32_t* rect);

void Call_STB_GetFontVMetrics(const float fontsize, int32_t* rect);

int Call_STB_GetCodepointHMetrics(const int point);

void Call_STB_MakeCodepointBitmap(const int point, const float scale, const int width, const int height, uint8_t* bitmap);

void Call_STB_MakeDrawTextToBitmap(const char* text, const float fontscale, const int width, const int height, uint8_t* rect);

bool Call_STB_SaveArgbToPng(const char* filename, const int32_t* pixels, int32_t w, int32_t h);

void Call_STB_CloseFontInfo();

#ifdef __cplusplus
}
#endif

#endif
