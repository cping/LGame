#include "SDLSupport.h"

#ifdef __SWITCH__
static EGLDisplay display;
static EGLContext context;
static EGLSurface surface;

static PadState combinedPad;
static PadState pads[8];

static int nxlinkSock = -1;
static bool socketInit;
#else
static SDL_Window* window;
static int buttons;
static float joysticks[4];
#endif

static bool musicFinishedEvent;
static bool soundFinishedEvent;
static int touches[16 * 3];
static char curr_dir[MAX_PATH];
#ifdef _WIN32
#include <windows.h>
#else
#include <dlfcn.h>
#endif

void Load_SDL_Cleanup() {
#ifdef __SWITCH__
	if (display) {
		eglMakeCurrent(display, EGL_NO_SURFACE, EGL_NO_SURFACE, EGL_NO_CONTEXT);
		if (context)
			eglDestroyContext(display, context);
		if (surface)
			eglDestroySurface(display, surface);
		eglTerminate(display);
	}
	Mix_Quit();
	SDL_Quit();
#endif
}

#ifndef __SWITCH__
static int keyToButton(int key) {
	switch (key) {
	case SDL_SCANCODE_Z:
		return 1 << 3; // Y
	case SDL_SCANCODE_X:
		return 1 << 1; // B
	case SDL_SCANCODE_C:
		return 1 << 0; // A
	case SDL_SCANCODE_V:
		return 1 << 2; // X
	case SDL_SCANCODE_F:
		return 1 << 4; // Left stick
	case SDL_SCANCODE_G:
		return 1 << 5; // Right stick
	case SDL_SCANCODE_Q:
		return 1 << 6; // L
	case SDL_SCANCODE_E:
		return 1 << 7; // R
	case SDL_SCANCODE_R:
		return 1 << 8; // ZL
	case SDL_SCANCODE_T:
		return 1 << 9; // ZR
	case SDL_SCANCODE_N:
		return 1 << 10; // Plus
	case SDL_SCANCODE_M:
		return 1 << 11; // Minus
	case SDL_SCANCODE_UP:
		return 1 << 13; // D-up
	case SDL_SCANCODE_DOWN:
		return 1 << 15; // D-down
	case SDL_SCANCODE_LEFT:
		return 1 << 12; // D-left
	case SDL_SCANCODE_RIGHT:
		return 1 << 14; // D-right
	default:
		return 0;
	}
}

static int keyToAxis(int scancode) {
	switch (scancode) {
	case SDL_SCANCODE_W:
		return 0x4 + 1;
	case SDL_SCANCODE_S:
		return 1;
	case SDL_SCANCODE_A:
		return 0x4 + 0;
	case SDL_SCANCODE_D:
		return 0;
	case SDL_SCANCODE_I:
		return 0x4 + 3;
	case SDL_SCANCODE_K:
		return 3;
	case SDL_SCANCODE_J:
		return 0x4 + 2;
	case SDL_SCANCODE_L:
		return 2;
	default:
		return -1;
	}
}

static int mapButtonSDL(int button) {
	switch (button) {
	case SDL_CONTROLLER_BUTTON_A:
		return 1 << 0;
	case SDL_CONTROLLER_BUTTON_B:
		return 1 << 1;
	case SDL_CONTROLLER_BUTTON_X:
		return 1 << 2;
	case SDL_CONTROLLER_BUTTON_Y:
		return 1 << 3;
	case SDL_CONTROLLER_BUTTON_LEFTSTICK:
		return 1 << 4;
	case SDL_CONTROLLER_BUTTON_RIGHTSTICK:
		return 1 << 5;
	case SDL_CONTROLLER_BUTTON_LEFTSHOULDER:
		return 1 << 6;
	case SDL_CONTROLLER_BUTTON_RIGHTSHOULDER:
		return 1 << 7;
	case SDL_CONTROLLER_BUTTON_START:
		return 1 << 10;
	case SDL_CONTROLLER_BUTTON_BACK:
		return 1 << 11;
	case SDL_CONTROLLER_BUTTON_DPAD_LEFT:
		return 1 << 12;
	case SDL_CONTROLLER_BUTTON_DPAD_UP:
		return 1 << 13;
	case SDL_CONTROLLER_BUTTON_DPAD_RIGHT:
		return 1 << 14;
	case SDL_CONTROLLER_BUTTON_DPAD_DOWN:
		return 1 << 15;
	default:
		return 0;
	}
}
#else
static u64 remapPadButtons(u64 buttons, u32 style) {
	u64 mapped = buttons;

	if (style & HidNpadStyleTag_NpadJoyLeft) {
		mapped &= ~(
			HidNpadButton_Left | HidNpadButton_Right | HidNpadButton_Up | HidNpadButton_Down |
			HidNpadButton_StickLLeft | HidNpadButton_StickLRight | HidNpadButton_StickLUp | HidNpadButton_StickLDown |
			HidNpadButton_LeftSL | HidNpadButton_LeftSR
			);

		if (buttons & HidNpadButton_Left)
			mapped |= HidNpadButton_B;
		if (buttons & HidNpadButton_Down)
			mapped |= HidNpadButton_A;
		if (buttons & HidNpadButton_Up)
			mapped |= HidNpadButton_Y;
		if (buttons & HidNpadButton_Right)
			mapped |= HidNpadButton_X;

		if (buttons & HidNpadButton_StickLLeft)
			mapped |= HidNpadButton_StickLDown;
		if (buttons & HidNpadButton_StickLDown)
			mapped |= HidNpadButton_StickLRight;
		if (buttons & HidNpadButton_StickLRight)
			mapped |= HidNpadButton_StickLUp;
		if (buttons & HidNpadButton_StickLUp)
			mapped |= HidNpadButton_StickLLeft;

		if (buttons & HidNpadButton_LeftSL)
			mapped |= HidNpadButton_L;
		if (buttons & HidNpadButton_LeftSR)
			mapped |= HidNpadButton_R;
	}
	else if (style & HidNpadStyleTag_NpadJoyRight) {
		mapped &= ~(
			HidNpadButton_A | HidNpadButton_B | HidNpadButton_X | HidNpadButton_Y |
			HidNpadButton_StickLLeft | HidNpadButton_StickLRight | HidNpadButton_StickLUp | HidNpadButton_StickLDown |
			HidNpadButton_LeftSL | HidNpadButton_LeftSR
			);

		if (buttons & HidNpadButton_A)
			mapped |= HidNpadButton_B;
		if (buttons & HidNpadButton_X)
			mapped |= HidNpadButton_A;
		if (buttons & HidNpadButton_B)
			mapped |= HidNpadButton_Y;
		if (buttons & HidNpadButton_Y)
			mapped |= HidNpadButton_X;

		if (buttons & HidNpadButton_StickRLeft)
			mapped |= HidNpadButton_StickRUp;
		if (buttons & HidNpadButton_StickRDown)
			mapped |= HidNpadButton_StickRLeft;
		if (buttons & HidNpadButton_StickRRight)
			mapped |= HidNpadButton_StickRDown;
		if (buttons & HidNpadButton_StickRUp)
			mapped |= HidNpadButton_StickRRight;

		if (buttons & HidNpadButton_RightSL)
			mapped |= HidNpadButton_L;
		if (buttons & HidNpadButton_RightSR)
			mapped |= HidNpadButton_R;
	}

	return mapped;
}

static void remapPadAxes(float* axes, u32 style) {
	if (style & HidNpadStyleTag_NpadJoyLeft) {
		float temp = axes[0];
		axes[0] = -axes[1];
		axes[1] = temp;
	}
	else if (style & HidNpadStyleTag_NpadJoyRight) {
		axes[0] = axes[3];
		axes[1] = -axes[2];
		axes[2] = 0;
		axes[3] = 0;
	}
}
#endif

static void OnSoundFinished(int channel) {
	musicFinishedEvent = true;
}

static void OnMusicFinished(void) {
	musicFinishedEvent = true;
}

int64_t Load_SDL_ScreenInit(const char* title, const int w, const int h, const bool vsync) {
	atexit(SDL_Quit);
	for (int i = 0; i < 16; i++) {
		touches[i * 3] = -1;
	}

#ifdef __SWITCH__
	padConfigureInput(8, HidNpadStyleSet_NpadStandard);
	padInitializeAny(&combinedPad);

	padInitializeDefault(&pads[0]);
	for (int i = 1; i < 8; i++)
		padInitialize(&pads[i], static_cast<HidNpadIdType>(HidNpadIdType_No1 + i));

	setInitialize();

	hidInitializeTouchScreen();

	Result result = romfsInit();
	if (R_FAILED(result))
		;
	display = eglGetDisplay(EGL_DEFAULT_DISPLAY);
	eglInitialize(display, NULL, NULL);
	eglBindAPI(EGL_OPENGL_API);
	EGLConfig config;
	EGLint numConfigs;
	static const EGLint framebufferAttributeList[] = {
		EGL_RENDERABLE_TYPE, EGL_OPENGL_BIT,
		EGL_RED_SIZE,     8,
		EGL_GREEN_SIZE,   8,
		EGL_BLUE_SIZE,    8,
		EGL_ALPHA_SIZE,   8,
		EGL_DEPTH_SIZE,   24,
		EGL_STENCIL_SIZE, 8,
		EGL_NONE
	};
	eglChooseConfig(display, framebufferAttributeList, &config, 1, &numConfigs);
	surface = eglCreateWindowSurface(display, config, nwindowGetDefault(), NULL);
	static const EGLint contextAttributeList[] =
	{
		EGL_CONTEXT_OPENGL_PROFILE_MASK_KHR, EGL_CONTEXT_OPENGL_CORE_PROFILE_BIT_KHR,
		EGL_CONTEXT_MAJOR_VERSION_KHR, 2,
		EGL_CONTEXT_MINOR_VERSION_KHR, 0,
		EGL_NONE
	};
	context = eglCreateContext(display, config, EGL_NO_CONTEXT, contextAttributeList);
	eglMakeCurrent(display, surface, surface, context);
	gladLoadGL();

	SDL_Init(SDL_INIT_AUDIO);
#else
	SDL_Init(SDL_INIT_VIDEO | SDL_INIT_AUDIO | SDL_INIT_GAMECONTROLLER);
	window = SDL_CreateWindow(title, SDL_WINDOWPOS_CENTERED, SDL_WINDOWPOS_CENTERED, w, h, SDL_WINDOW_OPENGL | SDL_WINDOW_RESIZABLE);
	SDL_GL_SetAttribute(SDL_GL_CONTEXT_PROFILE_MASK, SDL_GL_CONTEXT_PROFILE_CORE);
	SDL_GL_SetAttribute(SDL_GL_CONTEXT_MAJOR_VERSION, 2);
#ifdef __WINRT__
	SDL_SetHint(SDL_HINT_OPENGL_ES_DRIVER, "1");
	SDL_SetHint("SDL_WINRT_HANDLE_BACK_BUTTON", "1");
	SDL_GL_SetAttribute(SDL_GL_RED_SIZE, 8);
	SDL_GL_SetAttribute(SDL_GL_GREEN_SIZE, 8);
	SDL_GL_SetAttribute(SDL_GL_BLUE_SIZE, 8);
	SDL_GL_SetAttribute(SDL_GL_CONTEXT_MAJOR_VERSION, 2);
	SDL_GL_SetAttribute(SDL_GL_CONTEXT_PROFILE_MASK, SDL_GL_CONTEXT_PROFILE_ES);
#endif
	SDL_GLContext context = SDL_GL_CreateContext(window);
	SDL_GL_MakeCurrent(window, context);
	SDL_GL_SetSwapInterval(vsync ? 1 : 0);
   #if !defined(GLEW) 
    gladLoadGLES2((GLADloadfunc)SDL_GL_GetProcAddress);
   #endif
#endif
	Mix_Init(MIX_INIT_MP3 | MIX_INIT_OGG);
	Mix_OpenAudio(44100, MIX_DEFAULT_FORMAT, MIX_DEFAULT_CHANNELS, 4096);
	Mix_AllocateChannels(32);
	Mix_HookMusicFinished(OnMusicFinished);
	Mix_ChannelFinished(OnSoundFinished);
	return (intptr_t)window;
}

bool Load_SDL_Update() {
#ifdef __SWITCH__
	padUpdate(&combinedPad);
	u64 kDown = padGetButtonsDown(&combinedPad);
	if (kDown & HidNpadButton_Plus) {
		return false;
	}
	for (int i = 0; i < 8; i++) {
		padUpdate(&pads[i]);
	}
	HidTouchScreenState touchState;
	if (hidGetTouchScreenStates(&touchState, 1)) {
		for (int i = 0; i < 16; i++)
			if (i < touchState.count) {
				touches[i * 3 + 0] = touchState.touches[i].finger_id;
				touches[i * 3 + 1] = touchState.touches[i].x;
				touches[i * 3 + 2] = touchState.touches[i].y;
			}
			else {
				touches[i * 3 + 0] = -1;
				touches[i * 3 + 1] = 0;
				touches[i * 3 + 2] = 0;
			}
	}

	eglSwapBuffers(display, surface);
	return appletMainLoop();
#else
      int running = 1;
	  SDL_Event event;
	  int axis;
	  while (SDL_PollEvent(&event)) {
		  switch (event.type) {
		  case SDL_QUIT:
			  running = 0;
			  return Load_SDL_Exit(running);
		  case SDL_MOUSEMOTION:
			  touches[1] = event.motion.x;
			  touches[2] = event.motion.y;
			  break;
		  case SDL_MOUSEBUTTONDOWN:
			  touches[0] = 0;
			  touches[1] = event.button.x;
			  touches[2] = event.button.y;
			  break;
		  case SDL_MOUSEBUTTONUP:
			  touches[0] = -1;
			  break;
		  case SDL_KEYDOWN:
			  buttons |= keyToButton(event.key.keysym.scancode);
			  axis = keyToAxis(event.key.keysym.scancode);
			  if (axis > -1 && !event.key.repeat)
				  joysticks[axis & 0x3] += axis & 0x4 ? -1 : 1;
			  break;
		  case SDL_KEYUP:
			  buttons &= ~keyToButton(event.key.keysym.scancode);
			  axis = keyToAxis(event.key.keysym.scancode);
			  if (axis > -1 && !event.key.repeat)
				  joysticks[axis & 0x3] = 0;
			  break;
		  case SDL_CONTROLLERBUTTONDOWN:
			  buttons |= mapButtonSDL(event.cbutton.button);
			  break;
		  case SDL_CONTROLLERBUTTONUP:
			  buttons &= ~mapButtonSDL(event.cbutton.button);
			  break;
		  case SDL_CONTROLLERAXISMOTION:
			  if (event.caxis.axis >= 0 && event.caxis.axis < 4)
				  joysticks[event.caxis.axis] = (float)event.caxis.value / 32768.f;
			  for (int i = 0; i < 2; i++)
				  if (event.caxis.axis == SDL_CONTROLLER_AXIS_TRIGGERLEFT + i) {
					  if (event.caxis.value > 512)
						  buttons |= 1 << (8 + i);
					  else
						  buttons &= ~(1 << (8 + i));
				  }
			  break;
		  case SDL_CONTROLLERDEVICEADDED:
			  SDL_GameControllerOpen(event.cdevice.which);
			  break;
		  case SDL_CONTROLLERDEVICEREMOVED:
			  SDL_GameControllerClose(SDL_GameControllerFromPlayerIndex(event.cdevice.which));
			  break;
		  }
	  }

	{
		if (musicFinishedEvent) {
			OnMusicFinished();
			musicFinishedEvent = false;
		}
		if (soundFinishedEvent) {
			OnSoundFinished(0);
			soundFinishedEvent = false;
		}
	}
	SDL_GL_SwapWindow(window);
	return  Load_SDL_Exit(running);
#endif
}

bool Load_SDL_Exit(const int run) {
	return true;
}

int* Load_SDL_TouchData(int* data)
{
	memcpy((void*)data, touches, sizeof(touches));
	return data;
}


int Load_SDL_GL_SetSwapInterval(int on)
{
	return SDL_GL_SetSwapInterval(on);
}

void Load_SDL_GL_SwapWindow(const int64_t window)
{
	SDL_GL_SwapWindow((SDL_Window*)window);
}

int64_t Load_SDL_GL_CreateContext(const int64_t window)
{
	return (intptr_t)SDL_GL_CreateContext((SDL_Window*)window);
}

int Load_SDL_GL_SetAttribute(const int attribute, const int value)
{
	return SDL_GL_SetAttribute((SDL_GLattr)attribute, value);
}

int* Load_SDL_GetDrawableSize(const int64_t window, int* values)
{
	int w, h;
	SDL_GL_GetDrawableSize((SDL_Window*)window, &w, &h);
	values[0] = w;
	values[1] = h;
	return values;
}

bool Load_SDL_PathIsFile(char* path)
{
	SDL_RWops* f = SDL_RWFromFile(path, "r");
	if (f == NULL) { return false; }
	else { SDL_RWclose(f); return true; }
}

#ifdef _WIN32
char* GetPathFullName(char* dst, const char* path) {
	return GetFullPathName(path, MAX_PATH, dst, NULL);
}
#elif defined(__unix__) || defined(__APPLE__)
char* GetPathFullName(char* dst, const char* path) {
	char* ret = realpath(path, dst);
	return ret;
}
#endif

char* GetSystemProperty(const char* key)
{
#if defined(__SWITCH__)
	if (strcmp(key, "os.name") == 0)
		return "horizon";
	if (strcmp(name, "os.arch") == 0)
		return "aarch64";
#elif defined(__WINRT__)
	if (strcmp(name, "os.name") == 0)
		return "uwp";
	if (strcmp(name, "os.arch") == 0)
		return "x86_64";
#else
	if (strcmp(key, "os.name") == 0)
		return "unknown";
	if (strcmp(key, "os.arch") == 0)
		return "x86_64";
#endif
	if (strcmp(key, "line.separator") == 0)
		return "\n";
	if (strcmp(key, "java.io.tmpdir") == 0)
		return "temp";
	if (strcmp(key, "user.home") == 0)
		return "home";
	if (strcmp(key, "user.name") == 0)
		return "user";
	return "null";
}

void Load_RemapControllers(const int min, const int max, const int dualJoy, const int singleMode)
{
#ifdef __SWITCH__
	HidLaControllerSupportArg arg;
	hidLaCreateControllerSupportArg(&arg);
	arg.hdr.player_count_min = min;
	arg.hdr.player_count_max = max;
	arg.hdr.enable_permit_joy_dual = dualJoy;
	arg.hdr.enable_single_mode = singleMode;
	hidLaShowControllerSupportForSystem(NULL, &arg, false);
#endif
}

bool Load_IsConnected(const int controller)
{
#ifdef __SWITCH__
	return pads[controller].active_handheld || pads[controller].active_id_mask;
#else
	return controller == 0;
#endif
}

int Load_Buttons()
{
#ifdef __SWITCH__
	PadState pad = controller == -1 ? combinedPad : pads[controller];
	return remapPadButtons(padGetButtons(&pad), padGetStyleSet(&pad));
#else
	return buttons;
#endif
}

float* Load_Axes(const int controller, float* axes)
{
#ifdef __SWITCH__
    const PadState &pad = controller == -1 ? combinedPad : pads[controller];
	HidAnalogStickState stickLeft = padGetStickPos(&pad, 0);
	HidAnalogStickState stickRight = padGetStickPos(&pad, 1);
    array[0] = (float)stickLeft.x / JOYSTICK_MAX;
    array[1] = (float)stickLeft.y / JOYSTICK_MAX;
    array[2] = (float)stickRight.x / JOYSTICK_MAX;
    array[3] = (float)stickRight.y / JOYSTICK_MAX;
    remapPadAxes(array, padGetStyleSet(&pad));
    array[1] *= -1;
    array[3] *= -1;
#else
    memcpy(axes, joysticks, sizeof(joysticks));
#endif
	return axes;
}

int* Load_SDL_GetWindowSize(const int64_t handle)
{
	int width = 0;
	int height = 0;
	SDL_GetWindowSize(window, &width, &height);
	int result[2] = { width,height };
	return &result;
}

int Load_SDL_LockSurface(const int64_t handle)
{
	return SDL_LockSurface((SDL_Surface*)handle);
}

void Load_SDL_UnlockSurface(const int64_t handle)
{
	 SDL_UnlockSurface((SDL_Surface*)handle);
}

void Load_SDL_Delay(const int32_t d)
{
	SDL_Delay(d);
}

int64_t Load_SDL_CreateRGBSurface(const int32_t flags, const int width, const int height, const int depth, const int32_t rmask, const int32_t gmask, const int32_t bmask, const int32_t amask)
{
	return (intptr_t)SDL_CreateRGBSurface(flags, width, height, depth,	rmask, gmask, bmask, amask);
}

int64_t Load_SDL_CreateRGBSurfaceFrom(const int32_t* pixels, const int w, const int h, const int format)
{
	Uint32 rmask, gmask, bmask, amask;
#if SDL_BYTEORDER == SDL_BIG_ENDIAN
	int shift = (req_format == STBI_rgb) ? 8 : 0;
	rmask = 0xff000000 >> shift;
	gmask = 0x00ff0000 >> shift;
	bmask = 0x0000ff00 >> shift;
	amask = 0x000000ff >> shift;
#else 
	rmask = 0x000000ff;
	gmask = 0x0000ff00;
	bmask = 0x00ff0000;
	amask = (format == 3) ? 0 : 0xff000000;
#endif
	int depth, pitch;
	if (format == 3) {
		depth = 24;
		pitch = 3 * w; 
	}
	else { // if STBI_rgb_alpha (RGBA)
		depth = 32;
		pitch = 4 * w;
	}
	return (intptr_t)SDL_CreateRGBSurfaceFrom((void*)pixels, w, h, depth, pitch,
		rmask, gmask, bmask, amask);
}

int64_t Load_SDL_ConvertSurfaceFormat(const int64_t handle, int32_t pixel_format, int32_t flags)
{
	return SDL_ConvertSurfaceFormat((SDL_Surface*)handle,(Uint32)pixel_format, (Uint32)flags);
}

int* Load_SDL_GetPixels(const int64_t handle, int x, int y, int w, int h)
{
	SDL_Surface* surface = (SDL_Surface*)handle;
	if (!surface) return -1;
	if (x >= 0 && y >= 0 && x < surface->w && x < w && y < surface->h && y < h)
	{
		int bpp = surface->format->BytesPerPixel;
		Uint8* pixel = (Uint8*)surface->pixels + y * surface->pitch + x * bpp;
		switch (bpp) {
		case 1:
			return *pixel;
		case 2:
			return *(Uint16*)pixel;
		case 3:
#if SDL_BYTEORDER == SDL_BIG_ENDIAN
			return pixel[0] << 16 | pixel[1] << 8 | pixel[2];
#else
			return pixel[0] | pixel[1] << 8 | pixel[2] << 16;
#endif
		case 4:
			return *(Uint32*)pixel;
		default:
			return 0;
		}
	}
	return 0;
}

void Load_SDL_SetPixel(const int64_t handle, int x, int y, int32_t pixel)
{
	SDL_Surface* surface = (SDL_Surface*)handle;
	if (!surface) return;
	if (x < 0 || y < 0 || x >= surface->w || y >= surface->h) {
		return;
	}
	Uint8* target_pixel = (Uint8*)surface->pixels + y * surface->pitch + x * surface->format->BytesPerPixel;
	switch (surface->format->BytesPerPixel) {
	case 1: 
		*target_pixel = (Uint8)pixel;
		break;
	case 2: 
		*(Uint16*)target_pixel = (Uint16)pixel;
		break;
	case 3: 
		if (SDL_BYTEORDER == SDL_BIG_ENDIAN) {
			target_pixel[0] = (pixel >> 16) & 0xFF;
			target_pixel[1] = (pixel >> 8) & 0xFF;
			target_pixel[2] = pixel & 0xFF;
		}
		else {
			target_pixel[0] = pixel & 0xFF;
			target_pixel[1] = (pixel >> 8) & 0xFF;
			target_pixel[2] = (pixel >> 16) & 0xFF;
		}
		break;
	case 4: 
		*(Uint32*)target_pixel = (Uint32)pixel;
		break;
	}
}

void Load_SDL_SetPixel32(const int64_t handle, int x, int y,int32_t pixel)
{
	SDL_Surface* surface = (SDL_Surface*)handle;
	if (!surface) return;
	Uint32* const target_pixel = (Uint32*)((Uint8*)surface->pixels
		+ y * surface->pitch
		+ x * surface->format->BytesPerPixel);
	*target_pixel = (Uint32)pixel;
}

void Load_SDL_SetPixels32(const int64_t handle, int nx, int ny,int nw,int nh, int32_t* pixels)
{
	SDL_Surface* surface = (SDL_Surface*)handle;
	if (!surface || !pixels) return;
	Uint8* dst = (Uint8*)surface->pixels;
	int pitch = surface->pitch; 
	for (int y = ny; y < nh; y++) {
		Uint32* pixel = (Uint32*)(dst + y * pitch);
		for (int x = nx; x < nw; x++) {
			pixel[x] = (Uint32)pixels[y * nw + x];
		}
	}
}

const int blendModeToInt(SDL_BlendMode mode) {
	switch (mode) {
	case SDL_BLENDMODE_NONE:  return 0;
	case SDL_BLENDMODE_BLEND: return 1;
	case SDL_BLENDMODE_ADD:   return 2;
	case SDL_BLENDMODE_MOD:   return 3;
	case SDL_BLENDMODE_MUL:   return 4;
	default:                  return -1;
	}
}
const SDL_BlendMode blendIntToMode(int mode) {
	switch (mode) {
	case 0: return SDL_BLENDMODE_NONE;
	case 1: return SDL_BLENDMODE_BLEND;
	case 2: return SDL_BLENDMODE_ADD;
	case 3: return SDL_BLENDMODE_MOD;
	case 4: return SDL_BLENDMODE_MUL;
	default: return SDL_BLENDMODE_NONE;
	}
}

void Load_SDL_SetSurfaceBlendMode(const int64_t handle, const int mode)
{
	SDL_Surface* surface = (SDL_Surface*)handle;
	if (!surface) return;
	SDL_SetSurfaceBlendMode(surface, blendIntToMode(mode));
}

int Load_SDL_GetSurfaceBlendMode(const int64_t handle)
{
	SDL_Surface* surface = (SDL_Surface*)handle;
	if (!surface) return -1;
	SDL_BlendMode mode;
	SDL_GetSurfaceBlendMode(surface, &mode);
	return blendModeToInt(mode);
}

void Load_SDL_FillRect(const int64_t handle, const int x, const int y, const int w, const int h, const int r, const int g, const int b, const int a)
{
	SDL_Surface* surface = (SDL_Surface*)handle;
	if (!surface) return;
	Uint32 color = SDL_MapRGBA(surface->format, r, g, b,a);
	SDL_Rect rect = { x, y, w, h };
	SDL_FillRect(surface, &rect, color);
}

void Load_SDL_SetClipRect(const int64_t handle, const int x, const int y, const int w, const int h)
{
	SDL_Surface* surface = (SDL_Surface*)handle;
	if (!surface) return;
	SDL_Rect clipRect = { x, y, w, h };
	SDL_SetClipRect(surface, &clipRect);
}

int* Load_SDL_GetClipRect(const int64_t handle)
{
	SDL_Surface* surface = (SDL_Surface*)handle;
	if (!surface) return 0;
	SDL_Rect currentClip;
	SDL_GetClipRect(surface, &currentClip);
	int rect[] = {currentClip.x,currentClip.y,currentClip.w,currentClip.h};
	return &rect;
}

int Load_SDL_Init(const int flags)
{
	return SDL_Init(flags);
}

int Load_SDL_InitSubSystem(const int flags)
{
	return SDL_InitSubSystem(flags);
}

int Load_SDL_WasInit(const int flags)
{
	return SDL_WasInit(flags);
}

void Load_SDL_QuitSubSystem(const int flags)
{
	SDL_QuitSubSystem(flags);
}

char* Load_SDL_GetError()
{
	return SDL_GetError();
}

int Load_SDL_SetClipboardText(const char* text)
{
	return SDL_SetClipboardText(text);
}

char* Load_SDL_GetClipboardText()
{
	return SDL_GetClipboardText();
}

void Load_SDL_MaximizeWindow(const int64_t handle)
{
	SDL_MaximizeWindow((SDL_Window*)handle);
}

void Load_SDL_MinimizeWindow(const int64_t handle)
{
	SDL_MinimizeWindow((SDL_Window*)handle);
}

int Load_SDL_SetWindowFullscreen(const int64_t handle, const int flags)
{
	return SDL_SetWindowFullscreen((SDL_Window*)handle, flags);
}

void Load_SDL_SetWindowBordered(const int64_t handle, const bool bordered)
{
	SDL_SetWindowBordered((SDL_Window*)handle, (SDL_bool)bordered);
}

void Load_SDL_SetWindowSize(const int64_t handle, const int w, const int h)
{
	SDL_SetWindowSize((SDL_Window*)handle, w, h);
}

void Load_SDL_SetWindowPosition(const int64_t handle, const int x, const int y)
{
	SDL_SetWindowPosition((SDL_Window*)handle, x, y);
}

int Load_SDL_GetWindowDisplayIndex(const int64_t handle)
{
	return SDL_GetWindowDisplayIndex((SDL_Window*)handle);
}

int* Load_SDL_GetDisplayUsableBounds(const int display, int* xywh)
{
	SDL_Rect bounds = { 0,0,0,0 };
	int result = SDL_GetDisplayUsableBounds(display, &bounds);

	xywh[0] = bounds.x;
	xywh[1] = bounds.y;
	xywh[2] = bounds.w;
	xywh[3] = bounds.h;

	return xywh;
}

int* Load_SDL_GetDisplayBounds(const int display, int* xywh)
{
	SDL_Rect bounds = { 0,0,0,0 };
	int result = SDL_GetDisplayBounds(display, &bounds);

	xywh[0] = bounds.x;
	xywh[1] = bounds.y;
	xywh[2] = bounds.w;
	xywh[3] = bounds.h;

	return xywh;
}

int Load_SDL_GetNumVideoDisplays()
{
	return SDL_GetNumVideoDisplays();
}

int Load_SDL_GetWindowFlags(const int64_t handle)
{
	return SDL_GetWindowFlags((SDL_Window*)handle);
}

void Load_SDL_SetWindowTitle(const int64_t handle, const char* title)
{
	SDL_SetWindowTitle((SDL_Window*)handle, title);
}

int64_t Load_SDL_CreateRGBSurfaceFrom32(void* pixels, int width, int height)
{
	return (intptr_t)SDL_CreateRGBSurfaceFrom(pixels, width, height, 32, 4 * width, 0x000000ff, 0x0000ff00, 0x00ff0000, 0xff000000);
}

int64_t Load_SDL_CreateColorCursor(const int64_t surface, const int hotx, const int hoty)
{
	return (intptr_t)SDL_CreateColorCursor((SDL_Surface*)surface, hotx, hoty);
}

int64_t Load_SDL_CreateSystemCursor(const int type)
{
	return (intptr_t)SDL_CreateSystemCursor((SDL_SystemCursor)type);
}

void Load_SDL_SetCursor(const int64_t handle)
{
	SDL_SetCursor((SDL_Cursor*)handle);
}

void Load_SDL_FreeCursor(const int64_t handle)
{
	SDL_FreeCursor((SDL_Cursor*)handle);
}

void Load_SDL_FreeSurface(const int64_t handle)
{
	SDL_FreeSurface((SDL_Surface*)handle);
}

int Load_SDL_ShowSimpleMessageBox(const int flags, const char* title, const char* message)
{
	return SDL_ShowSimpleMessageBox(flags, title, message, NULL);
}

void Load_SDL_StartTextInput()
{
	SDL_StartTextInput();
}

void Load_SDL_StopTextInput()
{
	SDL_StopTextInput();
}

bool Load_SDL_IsTextInputActive()
{
	return SDL_IsTextInputActive();
}

bool Load_SDL_GL_ExtensionSupported(const char* exte) {
	return SDL_GL_ExtensionSupported(exte);
}

void Load_SDL_SetTextInputRect(const int x, const int y, const int w, const int h)
{
	SDL_Rect rect = { x,y,w,h };
	rect.x = x;
	rect.y = y;
	rect.w = w;
	rect.h = h;
	SDL_SetTextInputRect(&rect);
}

void Load_SDL_RestoreWindow(const int64_t handle)
{
	SDL_RestoreWindow((SDL_Window*)handle);
}

void Load_SDL_SetWindowIcon(const int64_t handle, const int64_t surface)
{
	SDL_SetWindowIcon((SDL_Window*)handle, (SDL_Surface*)surface);
}

void Load_SDL_DestroyWindow(const int64_t handle)
{
	SDL_DestroyWindow((SDL_Window*)handle);
}

bool Load_SDL_SetHint(const char* name, const char* value)
{
	return (SDL_SetHint(name, value) == SDL_TRUE);
}

int64_t Load_SDL_CreateWindow(const char* title, int w, int h, int flags)
{
	return (intptr_t)SDL_CreateWindow(title, SDL_WINDOWPOS_UNDEFINED, SDL_WINDOWPOS_UNDEFINED, w, h, flags);
}

int Load_SDL_PollEvent(char* data)
{
	SDL_Event e;
	if (SDL_PollEvent(&e)) {
		switch (e.type) {
		case SDL_QUIT:
			data[0] = 0;
			break;
		case SDL_WINDOWEVENT:
			data[0] = 1;
			data[1] = e.window.event;
			data[2] = e.window.data1;
			data[3] = e.window.data2;
			break;
		case SDL_MOUSEMOTION:
			data[0] = 2;
			data[1] = e.motion.x;
			data[2] = e.motion.y;
			break;
		case SDL_MOUSEBUTTONDOWN:
		case SDL_MOUSEBUTTONUP:
			data[0] = 3;
			data[1] = (e.type == SDL_MOUSEBUTTONDOWN);
			data[2] = e.button.x;
			data[3] = e.button.y;
			data[4] = e.button.button;
			break;
		case SDL_MOUSEWHEEL:
			data[0] = 4;
			data[1] = e.wheel.x;
			data[2] = e.wheel.y;
			break;
		case SDL_KEYDOWN:
		case SDL_KEYUP:
			data[0] = 5;
			data[1] = (e.type == SDL_KEYDOWN);
			data[2] = e.key.keysym.sym;
			data[3] = e.key.repeat;
			data[4] = e.key.keysym.scancode;
			data[5] = e.key.keysym.mod;
			data[6] = e.key.timestamp;
			break;
		case SDL_TEXTINPUT:
			data[0] = 6;
			for (int i = 0; i < 32; i++) {
				data[i + 1] = e.text.text[i];
				if (e.text.text[i] == '\0') {
					break;
				}
			}
			break;
		case SDL_TEXTEDITING:
			data[0] = 8;
			data[1] = e.edit.start;
			data[2] = e.edit.length;
			for (int i = 0; i < 32; i++) {
				data[i + 3] = e.edit.text[i];
				if (e.edit.text[i] == '\0') {
					break;
				}
			}

			break;
		default:
			data[0] = 7;
			break;
		}
		return 1;
	}
	return 0;
}

void Load_SDL_GetCompiledVersion(char* data)
{
	SDL_version compiled = { 0,0,0 };
	SDL_VERSION(&compiled);
	data[0] = compiled.major;
	data[1] = compiled.minor;
	data[2] = compiled.patch;
}

void Load_SDL_GetVersion(char* data)
{
	SDL_version compiled = { 0,0,0 };
	SDL_GetVersion(&compiled);
	data[0] = compiled.major;
	data[1] = compiled.minor;
	data[2] = compiled.patch;
}

int64_t Load_SDL_Mix_LoadMUS(const char* filename)
{
	Mix_Music* mix = Mix_LoadMUS(filename);
	if (!mix) {
		return -1;
	}
	return (intptr_t)mix;
}

void Load_SDL_Quit()
{
	SDL_Quit();
}

char* Load_GL_Init() {
#ifdef GLEW
	GLenum glewError = glewInit();
	if (glewError != GLEW_OK) {
		return (const char*)glewGetErrorString(glewError);
	}
	if (glGenFramebuffers != 0 || glGenFramebuffersEXT != 0) {
		return NULL;
	}
	else {
		return "Missing framebuffer_object extension.";
	}
#endif
	return "null";
}

void Load_GL_UseProgram(const int program)
{
	glUseProgram(program);
}

void Load_GL_ValidateProgram(const int program)
{
	glValidateProgram(program);
}

void Load_GL_ActiveTexture(const int tex)
{
	glActiveTexture(tex);
}

void Load_GL_BindTexture(const int target,const int tex)
{
	glBindTexture(target, tex);
}

void Load_GL_BlendFunc(const int sfactor, const int dfactor)
{
	glBlendFunc(sfactor, dfactor);
}

void Load_GL_Clear(const int mask)
{
	glClear(mask);
}

void Load_GL_ClearColor(const float red, const float green, const float blue, const float alpha)
{
	glClearColor(red, green, blue, alpha);
}

void Load_GL_ClearDepthf(const float depth)
{
	glClearDepthf(depth);
}

void Load_GL_ClearStencil(const int sc)
{
	glClearStencil(sc);
}

void Load_GL_ColorMask(const bool red, const bool green, const bool blue, const bool alpha)
{
	glColorMask(red, green, blue, alpha);
}

void Load_GL_CompressedTexImage2D(const int target, const int level, const int internalformat, const int width, const int height, const int border, const int imageSize, void* data)
{
	glCompressedTexImage2D(target, level, internalformat, width, height, border, imageSize, data);
}

void Load_GL_CompressedTexSubImage2D(const int target, const int level, const int xoffset, const int yoffset, const int width, const int height, const int format, const int imageSize, void* data)
{
	glCompressedTexSubImage2D(target, level, xoffset, yoffset, width, height, format, imageSize, data);
}

void Load_GL_CopyTexImage2D(const int target, const int level, const int internalformat, const int x, const int y, const int width, const int height, const int border)
{
	glCopyTexImage2D(target, level, internalformat, x, y, width, height, border);
}

void Load_GL_CopyTexSubImage2D(const int target, const int level, const int xoffset, const int yoffset, const int x, const int y, const int width, const int height)
{
	glCopyTexSubImage2D(target, level, xoffset, yoffset, x, y, width, height);
}

void Load_GL_CullFace(const int mode)
{
	glCullFace(mode);
}

void Load_GL_DeleteTexture(const int texture)
{
	GLuint b = texture;
	glDeleteTextures(1, &b);
}

void Load_GL_DepthFunc(const int func)
{
	glDepthFunc(func);
}

void Load_GL_DepthMask(const bool flag)
{
	glDepthMask(flag);
}

void Load_GL_DepthRangef(const float zNear, const float zFar)
{
	glDepthRangef(zNear, zFar);
}

void Load_GL_Disable(const int cap)
{
	glDisable(cap);
}

void Load_GL_DrawArrays(const int mode, const int first, const int count)
{
	glDrawArrays(mode, first, count);
}

void Load_GL_DrawElements(const int mode, const int count, const int type, const void* indices)
{
	glDrawElements(mode, count, type, indices);
}

void Load_GL_Enable(const int cap)
{
	glEnable(cap);
}

void Load_GL_Finish()
{
	glFinish();
}

void Load_GL_Flush()
{
	glFlush();
}

void Load_GL_FrontFace(const int mode)
{
	glFrontFace(mode);
}

int Load_GL_GenTexture()
{
	GLuint result;
	glGenTextures(1, &result);
	return result;
}

int Load_GL_GetError()
{
	return glGetError();
}

void Load_GL_GetIntegerv(const int pname, const int32_t* params)
{
	glGetIntegerv(pname, (GLint*)params);
}

char* Load_GL_GetString(const int name)
{
	return (const char*)glGetString((GLenum)name);
}

void Load_GL_Hint(const int target, const int mode)
{
	glHint(target, mode);
}

void Load_GL_LineWidth(const float width)
{
	glLineWidth(width);
}

void Load_GL_PixelStorei(const int pname, const int param)
{
	glPixelStorei(pname, param);
}

void Load_GL_PolygonOffset(const float factor, const float units)
{
	glPolygonOffset(factor, units);
}

void Load_GL_ReadPixels(const int x, const int y, const int width, const int height, const int format, const int type, const void* pixels)
{
	glReadPixels(x, y, width, height, format, type, pixels);
}

void Load_GL_Scissor(const int x, const int y, const int width, const int height)
{
	glScissor(x, y, width, height);
}

void Load_GL_StencilFunc(const int func, const int ref, const int mask)
{
	glStencilFunc(func, ref, mask);
}

void Load_GL_StencilMask(const int mask)
{
	glStencilMask(mask);
}

void Load_GL_StencilOp(const int fail, const int zfail, const int zpass)
{
	glStencilOp(fail, zfail, zpass);
}

void Load_GL_TexImage2D(const int target, const int level, const int internalformat, const int width, const int height, const int border, const int format, const int type, const void* pixels)
{
	glTexImage2D(target, level, internalformat, width, height, border, format, type, pixels);
}

void Load_GL_TexParameterf(const int target, const int pname, const float param)
{
	glTexParameterf(target, pname, param);
}

void Load_GL_TexSubImage2D(const int target, const int level, const int xoffset, const int yoffset, const int width, const int height, const int format, const int type, const void* pixels)
{
	glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, pixels);
}

void Load_GL_Viewport(const int x, const int y, const int width, const int height)
{
	glViewport(x, y, width, height);
}

void Load_GL_AttachShader(const int program, const int shader)
{
	glAttachShader(program, shader);
}

void Load_GL_BindAttribLocation(const int program, const int index, const char* name)
{
	glBindAttribLocation(program, index, name);
}

void Load_GL_BindBuffer(const int target, const int buffer)
{
	glBindBuffer(target, buffer);
}

void Load_GL_BindFramebuffer(const int target, const int framebuffer)
{
	if (glBindFramebuffer) {
		glBindFramebuffer(target, framebuffer);
		return;
	}
#ifdef GLEW
	glBindRenderbufferEXT(target, renderbuffer);
#endif
}

void Load_GL_BindRenderbuffer(const int target, const int renderbuffer)
{
	if (glBindRenderbuffer) {
		glBindRenderbuffer(target, renderbuffer);
		return;
	}
#ifdef GLEW
	glBindRenderbufferEXT(target, renderbuffer);
#endif
}

void Load_GL_BlendColor(const float red, const float green, const float blue, const float alpha)
{
	glBlendColor(red, green, blue, alpha);
}

void Load_GL_BlendEquation(const int mode)
{
	glBlendEquation(mode);
}

void Load_GL_BlendEquationSeparate(const int modeRGB, const int modeAlpha)
{
	glBlendEquationSeparate(modeRGB, modeAlpha);
}

void Load_GL_BlendFuncSeparate(const int srcRGB, const int dstRGB, const int srcAlpha, const int dstAlpha)
{
	glBlendFuncSeparate(srcRGB, dstRGB, srcAlpha, dstAlpha);
}

void Load_GL_BufferData(const int target, const int size, const void* data, const int usage)
{
	glBufferData(target, size, data, usage);
}

void Load_GL_BufferSubData(int target, int offset, int size, const void* data)
{
	glBufferSubData(target, offset, size, data);
}

int Load_GL_CheckFramebufferStatus(const int target)
{
	if (glCheckFramebufferStatus) {
		return glCheckFramebufferStatus(target);
	}
#ifdef GLEW
	return glCheckFramebufferStatusEXT(target);
#else
	return -1;
#endif
}

void Load_GL_CompileShader(const int shader)
{
	glCompileShader(shader);
}

int Load_GL_CreateProgram()
{
	return glCreateProgram();
}

int Load_GL_CreateShader(const int type)
{
	return glCreateShader(type);
}

void Load_GL_DeleteBuffer(const int buffer)
{
	GLuint b = buffer;
	glDeleteBuffers(1, &b);
}

void Load_GL_DeleteFramebuffer(const int framebuffer)
{
	if (glDeleteFramebuffers) {
		GLuint b = framebuffer;
		glDeleteFramebuffers(1, &b);
		return;
	}
#ifdef GLEW
	GLuint b = framebuffer;
	glDeleteFramebuffersEXT(1, &b);
#endif
}

void Load_GL_DeleteProgram(const int program)
{
	glDeleteProgram(program);
}

void Load_GL_DeleteRenderbuffer(const int renderbuffer)
{
	GLuint b = renderbuffer;

	if (glDeleteRenderbuffers) {
		glDeleteRenderbuffers(1, &b);
		return;
	}
#ifdef GLEW
	glDeleteRenderbuffersEXT(1, &b);
#endif
}

void Load_GL_DeleteShader(const int shader)
{
	glDeleteShader(shader);
}

void Load_GL_DetachShader(const int program, const int shader)
{
	glDetachShader(program, shader);
}

void Load_GL_DisableVertexAttribArray(const int index)
{
	glDisableVertexAttribArray(index);
}

void Load_GL_EnableVertexAttribArray(const int index)
{
	glEnableVertexAttribArray(index);
}

void Load_GL_FramebufferRenderbuffer(const int target, const int attachment, const int renderbuffertarget, const int renderbuffer)
{
	if (glFramebufferRenderbuffer) {
		glFramebufferRenderbuffer(target, attachment, renderbuffertarget, renderbuffer);
		return;
	}
#ifdef GLEW
	glFramebufferRenderbufferEXT(target, attachment, renderbuffertarget, renderbuffer);
#endif
}

void Load_GL_FramebufferTexture2D(const int target, const int attachment, const int textarget, const int texture, const int level)
{
	if (glFramebufferTexture2D) {
		glFramebufferTexture2D(target, attachment, textarget, texture, level);
		return;
	}
#ifdef GLEW
	glFramebufferTexture2DEXT(target, attachment, textarget, texture, level);
#endif
}

int Load_GL_GenBuffer()
{
	GLuint result;
	glGenBuffers(1, &result);
	return result;
}

void Load_GL_GenerateMipmap(const int target)
{
	if (glGenerateMipmap) {
		glGenerateMipmap(target);
		return;
	}
#ifdef GLEW
	glGenerateMipmapEXT(target);
#endif
}

int Load_GL_GenFramebuffer()
{
	if (glGenFramebuffers) {
		GLuint result;
		glGenFramebuffers(1, &result);
		return result;
	}
#ifdef GLEW
	GLuint result;
	glGenFramebuffersEXT(1, &result);
	return result;
#else
	return -1;
#endif
}

char* Load_GL_GetActiveAttrib(const int program, const int index, const int32_t* size, const void* type)
{
	char cname[2048];
	glGetActiveAttrib(program, index, 2048, NULL, (GLint*)size, (GLenum*)type, cname);
	return cname;
}

char* Load_GL_GetActiveUniform(const int program, const int index, const int32_t* size, const void* type)
{
	char cname[2048];
	glGetActiveUniform(program, index, 2048, NULL, (GLint*)size, (GLenum*)type, cname);
	return cname;
}

int Load_GL_GetAttribLocation(const int program, const char* name)
{
	return glGetAttribLocation(program, name);
}

void Load_GL_GetBooleanv(const int pname, const void* params)
{
	glGetBooleanv(pname, params);
}

void Load_GL_GetBufferParameteriv(const int target, const int pname, const int32_t* params)
{
	glGetBufferParameteriv(target, pname, params);
}

void Load_GL_GetFloatv(const int pname, const float* params)
{
	glGetFloatv(pname, params);
}

void Load_GL_GetFramebufferAttachmentParameteriv(int target, int attachment, int pname, const int32_t* params)
{
	if (glGetFramebufferAttachmentParameteriv) {
		glGetFramebufferAttachmentParameteriv(target, attachment, pname, params);
		return;
	}
#ifdef GLEW
	glGetFramebufferAttachmentParameterivEXT(target, attachment, pname, params);
#endif
}

void Load_GL_GetProgramiv(int program, int pname, const int32_t* params)
{
	glGetProgramiv(program, pname, params);
}

char* Load_GL_GetProgramInfoLog(const int program)
{
	char info[1024 * 10]; 
	int length = 0;
	glGetProgramInfoLog(program, 1024 * 10, &length, info);
	return info;
}

void Load_GL_GetRenderbufferParameteriv(const int target, const int pname, const int32_t* params)
{
	if (glGetRenderbufferParameteriv) {
		glGetRenderbufferParameteriv(target, pname, params);
		return;
	}
#ifdef GLEW
	glGetRenderbufferParameterivEXT(target, pname, params);
#endif
}

void Load_GL_GetShaderiv(const int shader, const int pname, const int32_t* params)
{
	glGetShaderiv(shader, pname, params);
}

char* Load_GL_GetShaderInfoLog(const int shader)
{
	char info[1024 * 10]; 
	int length = 0;
	glGetShaderInfoLog(shader, 1024 * 10, &length, info);
	return info;
}

void Load_GL_GetShaderPrecisionFormat(const int shadertype, const int precisiontype, const int32_t* range, const int32_t* precision)
{
	glGetShaderPrecisionFormat(shadertype, precisiontype, range, precision);
}

void Load_GL_GetTexParameterfv(const int target, const int pname, const float* params)
{
	glGetTexParameterfv(target, pname, params);
}

void Load_GL_GetTexParameteriv(int target, int pname, const int32_t* params)
{
	glGetTexParameteriv(target, pname, params);
}

void Load_GL_GetUniformfv(const int program, const int location, const float* params)
{
	glGetUniformfv(program, location, params);
}

void Load_GL_GetUniformiv(const int program, const int location, const int32_t* params)
{
	glGetUniformiv(program, location, (GLint*)params);
}

int Load_GL_GetUniformLocation(const int program, const char* name)
{
	return glGetUniformLocation(program, name);
}

void Load_GL_GetVertexAttribfv(const int index, const int pname, const float* params)
{
	glGetVertexAttribfv(index, pname, params);
}

void Load_GL_GetVertexAttribiv(const int index, const int pname, const int32_t* params)
{
	glGetVertexAttribiv(index, pname, params);
}

bool Load_GL_IsBuffer(const int buffer)
{
	return glIsBuffer(buffer);
}

bool Load_GL_IsEnabled(const int cap)
{
	return glIsEnabled(cap);
}

bool Load_GL_IsFramebuffer(const int framebuffer)
{
	if (glIsFramebuffer) {
		return glIsFramebuffer(framebuffer);
	}
#ifdef GLEW
	return glIsFramebufferEXT(framebuffer);
#else
	return false;
#endif
}

bool Load_GL_IsProgram(const int program)
{
	return glIsProgram(program);
}

bool Load_GL_IsRenderbuffer(const int renderbuffer)
{
	if (glIsRenderbuffer) {
		return glIsRenderbuffer(renderbuffer);
	}
#ifdef GLEW
	return glIsRenderbufferEXT(renderbuffer);
#else
	return false;
#endif
}

bool Load_GL_IsShader(const int shader)
{
	return glIsShader(shader);
}

bool Load_GL_IsTexture(const int texture)
{
	return glIsTexture(texture);
}

void Load_GL_LinkProgram(const int program)
{
	glLinkProgram(program);
}

void Load_GL_ReleaseShaderCompiler()
{
	glReleaseShaderCompiler();
}

void Load_GL_RenderbufferStorage(const int target, const int internalformat, const int width, const int height)
{
	if (glRenderbufferStorage) {
		glRenderbufferStorage(target, internalformat, width, height);
		return;
	}
#ifdef GLEW
	glRenderbufferStorageEXT(target, internalformat, width, height);
#endif
}

void Load_GL_SampleCoverage(const float value, const bool invert)
{
	glSampleCoverage(value, invert);
}

void Load_GL_ShaderSource(const int shader, const char* string)
{
	glShaderSource(shader, 1, &string, NULL);
}

void Load_GL_StencilFuncSeparate(const int face, const int func, const int ref, const int mask)
{
	glStencilFuncSeparate(face, func, ref, mask);
}

void Load_GL_StencilMaskSeparate(const int face, const int mask)
{
	glStencilMaskSeparate(face, mask);
}

void Load_GL_StencilOpSeparate(const int face, const int fail, const int zfail, const int zpass)
{
	glStencilOpSeparate(face, fail, zfail, zpass);
}

void Load_GL_TexParameterfv(const int target, const int pname, const float* params)
{
	glTexParameterfv(target, pname, params);
}

void Load_GL_TexParameteri(const int target, const int pname, const int param)
{
	glTexParameteri(target, pname, param);
}

void Load_GL_TexParameteriv(const int target, const int pname, const int32_t* params)
{
	glTexParameteriv(target, pname, params);
}

void Load_GL_Uniform1f(const int location, const float x)
{
	glUniform1f(location, x);
}

void Load_GL_Uniform1fv(const int location, const int count, const float* v)
{
	glUniform1fv(location, count, v);
}

void Load_GL_Uniform1fvOffset(const int location, const int count, const float* v, const int offset)
{
	glUniform1fv(location, count, (GLfloat*)&v[offset]);
}

void Load_GL_Uniform1i(const int location, const int x)
{
	glUniform1i(location, x);
}

void Load_GL_Uniform1iv(const int location, const int count, const int32_t* v)
{
	glUniform1iv(location, count, v);
}

void Load_GL_Uniform1ivOffset(const int location, const int count, const int32_t* v, const int offset)
{
	glUniform1iv(location, count, (GLint*)&v[offset]);
}

void Load_GL_Uniform2f(const int location, const float x, const float y)
{
	glUniform2f(location, x, y);
}

void Load_GL_Uniform2fv(const int location, const int count, const float* v)
{
	glUniform2fv(location, count, v);
}

void Load_GL_Uniform2fvOffset(const int location, const int count, const float* v, const int offset)
{
	glUniform2fv(location, count, (GLfloat*)&v[offset]);
}

void Load_GL_Uniform2i(const int location, const int x, const int y)
{
	glUniform2i(location, x, y);
}

void Load_GL_Uniform2iv(const int location, const int count, const int32_t* v)
{
	glUniform2iv(location, count, v);
}

void Load_GL_Uniform2ivOffset(const int location, const int count, const int32_t* v, const int offset)
{
	glUniform2iv(location, count, (GLint*)&v[offset]);
}

void Load_GL_Uniform3f(const int location, const float x, const float y, const float z)
{
	glUniform3f(location, x, y, z);
}

void Load_GL_Uniform3fv(const int location, const int count, const float* v)
{
	glUniform3fv(location, count, v);
}

void Load_GL_Uniform3fvOffset(const int location, const int count, const float* v, const int offset)
{
	glUniform3fv(location, count, (GLfloat*)&v[offset]);
}

void Load_GL_Uniform3i(const int location, const int x, const int y, const int z)
{
	glUniform3i(location, x, y, z);
}

void Load_GL_Uniform3iv(const int location, const int count, const int32_t* v)
{
	glUniform3iv(location, count, v);
}

void Load_GL_Uniform3ivOffset(const int location, const int count, const int32_t* v, const int offset)
{
	glUniform3iv(location, count, (GLint*)&v[offset]);
}

void Load_GL_Uniform4f(const int location, const float x, const float y, const float z, const float w)
{
	glUniform4f(location, x, y, z, w);
}

void Load_GL_Uniform4fv(const int location, const int count, const float* v)
{
	glUniform4fv(location, count, v);
}

void Load_GL_Uniform4fvOffset(const int location, const int count, const float* v, const int offset)
{
	glUniform4fv(location, count, (GLfloat*)&v[offset]);
}

void Load_GL_Uniform4i(const int location, const int x, const int y, const int z, const int w)
{
	glUniform4i(location, x, y, z, w);
}

void Load_GL_Uniform4iv(const int location, const int count, const int32_t* v)
{
	glUniform4iv(location, count, v);
}

void Load_GL_Uniform4ivOffset(const int location, const int count, const int32_t* v, const int offset)
{
	glUniform4iv(location, count, (GLint*)&v[offset]);
}

void Load_GL_UniformMatrix2fv(const int location, const int count, const bool transpose, const float* value)
{
	glUniformMatrix2fv(location, count, transpose, value);
}

void Load_GL_UniformMatrix2fvOffset(const int location, const int count, const bool transpose, const float* value, const int offset)
{
	glUniformMatrix2fv(location, count, transpose, (GLfloat*)&value[offset]);
}

void Load_GL_UniformMatrix3fv(const int location, const int count, const bool transpose, const float* value)
{
	glUniformMatrix3fv(location, count, transpose, value);
}

void Load_GL_UniformMatrix3fvOffset(const int location, const int count, const bool transpose, const float* value, const int offset)
{
	glUniformMatrix3fv(location, count, transpose, (GLfloat*)&value[offset]);
}

void Load_GL_UniformMatrix4fv(const int location, const int count, const bool transpose, const float* value)
{
	glUniformMatrix4fv(location, count, transpose, value);
}

void Load_GL_UniformMatrix4fvOffset(const int location, const int count, const bool transpose, const float* value, const int offset)
{
	glUniformMatrix4fv(location, count, transpose, (GLfloat*)&value[offset]);
}

void Load_GL_VertexAttrib1f(const int indx, const float x)
{
	glVertexAttrib1f(indx, x);
}

void Load_GL_VertexAttrib1fv(const int indx, const float* values)
{
	glVertexAttrib1fv(indx, values);
}

void Load_GL_VertexAttrib2f(const int indx, const float x, const float y)
{
	glVertexAttrib2f(indx, x, y);
}

void Load_GL_VertexAttrib2fv(const int indx, const float* values)
{
	glVertexAttrib2fv(indx, values);
}

void Load_GL_VertexAttrib3f(const int indx, const float x, const float y, const float z)
{
	glVertexAttrib3f(indx, x, y, z);
}

void Load_GL_VertexAttrib3fv(const int indx, const float* values)
{
	glVertexAttrib3fv(indx, values);
}

void Load_GL_VertexAttrib4f(const int indx, const float x, const float y, const float z, const float w)
{
	glVertexAttrib4f(indx, x, y, z, w);
}

void Load_GL_VertexAttrib4fv(const int indx, const float* values)
{
	glVertexAttrib4fv(indx, values);
}

void Load_GL_VertexAttribPointer(const int indx, const int size, const int type, bool normalized, const int stride, void* ptr)
{
	glVertexAttribPointer(indx, size, type, normalized, stride, ptr);
}