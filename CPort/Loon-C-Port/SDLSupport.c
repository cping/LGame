#include "SDLSupport.h"

#ifndef LOON_DESKTOP
	static EGLDisplay display;
	static EGLContext context;
	static EGLSurface eglsurface;
	static PadState combinedPad;
	static PadState pads[8];
#else
	static SDL_Window* window;
	static SDL_GameController* tempController;
	static int buttons;
	static float joysticks[4];
#endif
	
PlayerController players[MAX_CONTROLLERS];
GamepadState gpStates[MAX_CONTROLLERS];

static int debugMode = 0;
static ButtonCallback buttonCallbacks[BTN_MAX] = { 0 };
static AxisCallback axisCallbacks[AXIS_MAX] = { 0 };
static TriggerCallback triggerCallbacks[TRIGGER_MAX] = { 0 };

static const char b64_table[] =
		"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
static SDL_GLContext* tempContext = NULL;
static uint32_t tempPolleventType = 0;
static game_filesystem* gamefilesys = NULL;
static int g_loopCount = 0;
static int g_windowSize[2];
static int g_screenSize[2];
static int g_initWidth = 0;
static int g_initHeight = 0;
static bool g_isLooping = false;
static bool g_isPause = false;
static int g_keyStates[SDL_NUM_SCANCODES] = { 0 };
static int g_pressedKeys[SDL_NUM_SCANCODES] = { 0 };
static int g_releasedKeys[SDL_NUM_SCANCODES] = { 0 };
static int g_lastPressedScancode = -1;
static int console_status = 0;
static game_music* g_currentMusic = NULL;
static TouchIdMap g_touchMap = { NULL, 0, 0 };
static int g_touches[MAX_TOUCH_DEVICES * 3];
static char g_texts[MAX_TEXTINPUT_CAHR_LEN] = "";
static const int audio_rate = 44100;
static const Uint16 audio_format = MIX_DEFAULT_FORMAT;
static const int audio_channels = MIX_DEFAULT_CHANNELS;
static const int audio_buffers = 4096;
static const int fade_time = 5000;
static const float volume = 1.0;
static cache_surface* _temp_surface = NULL;
static bool musicFinishedEvent = false;
static bool soundFinishedEvent = false;
static bool allowExit = true;
static char curr_dir[MAX_PATH];
static int eventBuffer[MAX_EVENTS * 4]; 
static int eventCount = 0;

static int is_valid_url(const char* url) {
	if (!url) return 0;
	return (strncmp(url, "http://", 7) == 0 || strncmp(url, "https://", 8) == 0);
}

static int safe_system_open(const char* cmd, const char* url) {
	char buf[1024];
	if (snprintf(buf, sizeof(buf), "%s \"%s\"", cmd, url) >= (int)sizeof(buf)) {
		SDL_Log("openURL: URL too long");
		return -1;
	}
	return system(buf) == 0 ? 0 : -1;
}

int Load_SDL_OpenURL(const char* url) {
	if (!is_valid_url(url)) {
		SDL_Log("openURL: Invalid or unsupported URL format: %s", url ? url : "(null)");
		return -1;
	}
	#if SDL_VERSION_ATLEAST(2,0,14)
		if (SDL_OpenURL(url) == 0) {
			return 0;
		}
	#endif
    #if defined(_WIN32) || defined(_WIN64)
	if ((intptr_t)ShellExecuteA(NULL, "open", url, NULL, NULL, SW_SHOWNORMAL) > 32) {
		return 0;
	}
    #elif defined(__APPLE__)
	if (safe_system_open("open", url) == 0) {
		return 0;
	}
    #elif defined(__linux__)
	if (safe_system_open("xdg-open", url) == 0) {
		return 0;
	}
    #else
		if (SDL_OpenURL(url) == 0) {
			return 0;
		}
    #endif
	SDL_Log("openURL: Failed to open URL on this platform");
	return -1;
}

void SDL_InitTouchIdMap() {
	g_touchMap.ids = NULL;
	g_touchMap.count = 0;
	g_touchMap.capacity = 0;
}

void SDL_FreeTouchIdMap() {
	free(g_touchMap.ids);
	g_touchMap.ids = NULL;
	g_touchMap.count = 0;
	g_touchMap.capacity = 0;
}

int SDL_ConvertMapTouchIdToIndex(SDL_TouchID touchId) {
	for (int i = 0; i < g_touchMap.count; i++) {
		if (g_touchMap.ids[i] == touchId) {
			return i;
		}
	}
	if (g_touchMap.count >= g_touchMap.capacity) {
		int newCapacity = (g_touchMap.capacity == 0) ? 4 : g_touchMap.capacity * 2;
		SDL_TouchID* newIds = realloc(g_touchMap.ids, newCapacity * sizeof(SDL_TouchID));
		if (!newIds) {
			return -1;
		}
		g_touchMap.ids = newIds;
		g_touchMap.capacity = newCapacity;
	}
	g_touchMap.ids[g_touchMap.count] = touchId;
	return g_touchMap.count++;
}


static float NormalizeAxis(Sint16 value) {
	float f = value / 32767.0f;
	return (fabsf(f) < DEADZONE) ? 0.0f : f;
}

static float NormalizeTrigger(Sint16 value) {
	float f = value / 32767.0f;
	return (f < TRIGGER_THRESHOLD) ? 0.0f : f;
}

static LogicalButton MapSDLButton(Uint16 vendor, SDL_GameControllerButton sdlBtn) {
	if (vendor == 0x057E) { // Nintendo Switch
		switch (sdlBtn) {
		case SDL_CONTROLLER_BUTTON_A: return BTN_CANCEL;
		case SDL_CONTROLLER_BUTTON_B: return BTN_CONFIRM;
		case SDL_CONTROLLER_BUTTON_X: return BTN_SHOOT;
		case SDL_CONTROLLER_BUTTON_Y: return BTN_JUMP;
		case SDL_CONTROLLER_BUTTON_START: return BTN_MENU;
		default: return BTN_MAX;
		}
	}
	switch (sdlBtn) {
	case SDL_CONTROLLER_BUTTON_A: return BTN_CONFIRM;
	case SDL_CONTROLLER_BUTTON_B: return BTN_CANCEL;
	case SDL_CONTROLLER_BUTTON_X: return BTN_JUMP;
	case SDL_CONTROLLER_BUTTON_Y: return BTN_SHOOT;
	case SDL_CONTROLLER_BUTTON_START: return BTN_MENU;
	default: return BTN_MAX;
	}
}

static LogicalAxis MapSDLAxis(SDL_GameControllerAxis sdlAxis) {
	switch (sdlAxis) {
	case SDL_CONTROLLER_AXIS_LEFTX: return AXIS_MOVE_X;
	case SDL_CONTROLLER_AXIS_LEFTY: return AXIS_MOVE_Y;
	case SDL_CONTROLLER_AXIS_RIGHTX: return AXIS_LOOK_X;
	case SDL_CONTROLLER_AXIS_RIGHTY: return AXIS_LOOK_Y;
	default: return AXIS_MAX;
	}
}

static LogicalTrigger MapSDLTrigger(SDL_GameControllerAxis sdlAxis) {
	switch (sdlAxis) {
	case SDL_CONTROLLER_AXIS_TRIGGERLEFT: return TRIGGER_LEFT;
	case SDL_CONTROLLER_AXIS_TRIGGERRIGHT: return TRIGGER_RIGHT;
	default: return TRIGGER_MAX;
	}
}

void GameController_Init(int dbg) {
	debugMode = dbg;
	memset(players, 0, sizeof(players));
	memset(gpStates, 0, sizeof(gpStates));
	for (int i = 0; i < SDL_NumJoysticks() && i < MAX_CONTROLLERS; i++) {
		if (SDL_IsGameController(i)) {
			players[i].controller = SDL_GameControllerOpen(i);
			SDL_Joystick* joy = SDL_GameControllerGetJoystick(players[i].controller);
			players[i].vendor = SDL_JoystickGetVendor(joy);
			players[i].product = SDL_JoystickGetProduct(joy);
			if (debugMode) {
				printf("Player %d Link: %s (Vendor: 0x%04X, Product: 0x%04X)\n",
					i, SDL_GameControllerName(players[i].controller),
					players[i].vendor, players[i].product);
			}
		}
	}
}

void GameController_ProcessEvent(SDL_Event* e) {
	if (e->type == SDL_CONTROLLERBUTTONDOWN || e->type == SDL_CONTROLLERBUTTONUP) {
		int playerIndex = e->cbutton.which;
		if (playerIndex >= MAX_CONTROLLERS || !players[playerIndex].controller) {
			return;
		}
		LogicalButton btn = MapSDLButton(players[playerIndex].vendor, e->cbutton.button);
		if (btn < BTN_MAX) {
			if (e->type == SDL_CONTROLLERBUTTONDOWN) {
				players[playerIndex].buttonState[btn] = 1;
				gpStates[playerIndex].buttons[btn] = 1;
				players[playerIndex].buttonDownTime[btn] = SDL_GetTicks();
				if (buttonCallbacks[btn]) {
					buttonCallbacks[btn](playerIndex, btn, EVENT_PRESS);
				}
			}
			else {
				players[playerIndex].buttonState[btn] = 0;
				gpStates[playerIndex].buttons[btn] = 0;
				if (buttonCallbacks[btn]) {
					buttonCallbacks[btn](playerIndex, btn, EVENT_RELEASE);
				}
			}
		}
	} else if (e->type == SDL_CONTROLLERAXISMOTION) {
		int playerIndex = e->caxis.which;
		if (playerIndex >= MAX_CONTROLLERS || !players[playerIndex].controller) {
			return;
		}
		LogicalAxis axis = MapSDLAxis(e->caxis.axis);
		LogicalTrigger trigger = MapSDLTrigger(e->caxis.axis);

		if (axis < AXIS_MAX) {
			float value = NormalizeAxis(e->caxis.value);
			gpStates[playerIndex].axes[axis] = value;
			if (axisCallbacks[axis]) {
				axisCallbacks[axis](playerIndex, axis, value);
			}
		}
		else if (trigger < TRIGGER_MAX) {
			float value = NormalizeTrigger(e->caxis.value);
			gpStates[playerIndex].triggers[trigger] = value;
			if (triggerCallbacks[trigger]) {
				triggerCallbacks[trigger](playerIndex, trigger, value);
			}
		}
	} else if (e->type == SDL_CONTROLLERDEVICEADDED) {
		int index = e->cdevice.which;
		for (int i = 0; i < MAX_CONTROLLERS; i++) {
			if (!players[i].controller && SDL_IsGameController(index)) {
				players[i].controller = SDL_GameControllerOpen(index);
				SDL_Joystick* joy = SDL_GameControllerGetJoystick(players[i].controller);
				players[i].vendor = SDL_JoystickGetVendor(joy);
				players[i].product = SDL_JoystickGetProduct(joy);
				if (debugMode) {
					printf("Player %d New Link : %s (Vendor: 0x%04X, Product:", SDL_GameControllerName(players[i].controller),
						players[i].vendor, players[i].product);
				}
				break;
			}
		}
	} else if (e->type == SDL_CONTROLLERDEVICEREMOVED) {
		SDL_JoystickID joyId = e->cdevice.which;
		for (int i = 0; i < MAX_CONTROLLERS; i++) {
			if (players[i].controller &&
				SDL_JoystickInstanceID(SDL_GameControllerGetJoystick(players[i].controller)) == joyId) {
				SDL_GameControllerClose(players[i].controller);
				players[i].controller = NULL;
				memset(&gpStates[i], 0, sizeof(GamepadState));
				if (debugMode) {
					printf("Player %d GameController Closed\n", i);
				}
				break;
			}
		}
	}
	Uint32 nowTime = SDL_GetTicks();
	for (int i = 0; i < MAX_CONTROLLERS; i++) {
		for (int b = 0; b < BTN_MAX; b++) {
			if (players[i].buttonState[b] && buttonCallbacks[b]) {
				if (nowTime - players[i].buttonDownTime[b] > 500) {
					buttonCallbacks[b](i, b, EVENT_HOLD);
				}
			}
		}
	}
}

void GameController_RegisterButtonCallback(LogicalButton btn, ButtonCallback cb) {
	if (btn < BTN_MAX) {
		buttonCallbacks[btn] = cb;
	}
}

void GameController_RegisterAxisCallback(LogicalAxis axis, AxisCallback cb) {
	if (axis < AXIS_MAX) {
		axisCallbacks[axis] = cb;
	}
}

void GameController_RegisterTriggerCallback(LogicalTrigger trigger, TriggerCallback cb) {
	if (trigger < TRIGGER_MAX) {
		triggerCallbacks[trigger] = cb;
	}
}

void GameController_Close() {
	for (int i = 0; i < MAX_CONTROLLERS; i++) {
		if (players[i].controller) {
			SDL_GameControllerClose(players[i].controller);
			players[i].controller = NULL;
		}
	}
}

static void GamePad_PushEvent(int player, int btn, int type) {
	if (eventCount < MAX_EVENTS) {
		int idx = eventCount * 4;
		eventBuffer[idx] = player;
		eventBuffer[idx + 1] = btn;
		eventBuffer[idx + 2] = type;
		eventBuffer[idx + 3] = SDL_GetTicks();
		eventCount++;
	}
}

// load and exit
// 初始化SDL前需要预加载，以及离开SDL前需要释放的平台函数在此调用
static int init_switch() {
	#if defined(__SWITCH__)
	    socketInitializeDefault();
		nxlinkStdio();
		romfsInit();
		padConfigureInput(1, HidNpadStyleSet_NpadStandard);
		return 0;
	#else
		return -1;
	#endif
}

static void quit_switch() {
	#if defined(__SWITCH__)
		romfsExit();
		socketExit();
	#endif
}

static int init_3ds() {
	#if defined(__3DS__)
		gfxInitDefault();
		hidInit();
		return 0;
	#else
		return -1;
	#endif
}

static void quit_3ds() {
	#if defined(__3DS__)
		hidExit();
		gfxExit();
	#endif
}

static int init_psp() {
	#if defined(__PSP__)
		pspDebugScreenInit();
		sceCtrlSetSamplingCycle(0);
		sceCtrlSetSamplingMode(PSP_CTRL_MODE_ANALOG);
		return 0;
	#else
		return -1;
	#endif
}

static void quit_psp() 
{
}

static int init_psv() {
	#if defined(__PSV__)
		sceIoInit();
		return 0;
	#else
		return -1;
	#endif
}

static void quit_psv() 
{
}

static int init_ps4_ps5() {
	#if defined(__ORBIS__) || defined(__PROSPERO__)
		sceSystemServiceInitialize();
		sceUserServiceInitialize(NULL);
		return 0;
	#else
		return -1;
	#endif
}

static void quit_ps4_ps5() {
	#if defined(__ORBIS__) || defined(__PROSPERO__)
		sceUserServiceTerminate();
		sceSystemServiceTerminate();
	#endif
}

static int init_luna() {
	#if defined(__LUNA__) || (defined(__linux__) && defined(LUNA_SDK))
		if (!SteamAPI_Init()) return -1;
		return 0;
	#else
		return -1;
	#endif
}

static void quit_luna() {
	#if defined(__LUNA__) || (defined(__linux__) && defined(LUNA_SDK))
		SteamAPI_Shutdown();
	#endif
}

static int init_xbox() {
	#if defined(_XBOX_ONE) || defined(_XBOX_SERIES_X)
		if (XGameRuntimeInitialize() != S_OK) {
			return -1;
		}
		return 0;
	#else
		return -1;
	#endif
}

static void quit_xbox() {
	#if defined(_XBOX_ONE) || defined(_XBOX_SERIES_X)
		XGameRuntimeUninitialize();
	#endif
}

static int init_steam_linux() {
	#if defined(__linux__)
		if (!SteamAPI_Init()) return -1;
		return 0;
	#else
		return -1;
	#endif
}

static void quit_steam_linux() {
	#if defined(__linux__)
		SteamAPI_Shutdown();
	#endif
}

static int init_bsd() {
	#if defined(__FreeBSD__) || defined(__OpenBSD__) || defined(__NetBSD__)
		return 0; 
	#else
		return -1;
	#endif
}

static void quit_bsd() 
{
}

static int init_android() {
	#if defined(__ANDROID__)
		__android_log_print(ANDROID_LOG_INFO, "SDL Running", "Android pre-init done");
		return 0;
	#else
		return -1;
	#endif
}

static void quit_android() 
{
}

static int init_ios() {
	#if defined(__APPLE__) && TARGET_OS_IPHONE
		return 0;
	#else
		return -1;
	#endif
}

static void quit_ios() 
{
}

static int init_macos() {
	#if defined(__APPLE__) && !TARGET_OS_IPHONE
		return 0;
	#else
		return -1;
	#endif
}

static void quit_macos() 
{
}

static int init_linux() {
	#if defined(__linux__)
		return 0;
	#else
		return -1;
	#endif
}

static void quit_linux() 
{
}

static int init_web() {
	#if defined(__EMSCRIPTEN__)
		printf("INFO: Initializing WebAssembly (Emscripten)...\n");
		return 0;
	#else
		return -1;
	#endif
}

static void quit_web() 
{
}

static int init_windows() {
		return 0;
}

static void quit_windows() {
}

static int SDL_platform_pre_init(bool debug) {
	const char* platform = SDL_GetPlatform();
	if (debug) {
		printf("INFO: SDL_GetPlatform() detected: %s\n", platform);
	}
	if (strcmp(platform, "Nintendo Switch") == 0) return init_switch();
	if (strcmp(platform, "Xbox") == 0) return init_xbox();
	if (strcmp(platform, "Steam") == 0) return init_steam_linux();
	if (strcmp(platform, "Windows") == 0) return init_windows();
	if (strcmp(platform, "Linux") == 0) return init_linux();
	if (strcmp(platform, "Android") == 0) return init_android();
	if (strcmp(platform, "iOS") == 0) return init_ios();
	if (strcmp(platform, "Mac OS X") == 0) return init_macos();
	if (strcmp(platform, "Nintendo 3DS") == 0) return init_3ds();
	if (strcmp(platform, "PSVita") == 0) return init_psv();
	if (strcmp(platform, "PS4") == 0 || strcmp(platform, "PS5") == 0) return init_ps4_ps5();
	if (strcmp(platform, "Luna") == 0) return init_luna();
	if (strcmp(platform, "PSP") == 0) return init_psp();
	if (strcmp(platform, "FreeBSD") == 0 || strcmp(platform, "OpenBSD") == 0 || strcmp(platform, "NetBSD") == 0) return init_bsd();
	if (strcmp(platform, "Emscripten") == 0) return init_web();
	if (debug) {
		printf("WARN: Unknown platform, no pre-init performed.\n");
	}
	return 0;
}

static void SDL_platform_free_quit() {
	const char* platform = SDL_GetPlatform();
	if (strcmp(platform, "Nintendo Switch") == 0) { quit_switch(); return; }
	if (strcmp(platform, "Xbox") == 0) { quit_xbox(); return; }
	if (strcmp(platform, "Steam") == 0) { quit_steam_linux(); return; }
	if (strcmp(platform, "Windows") == 0) { quit_windows(); return; }
	if (strcmp(platform, "Linux") == 0) { quit_linux(); return; }
	if (strcmp(platform, "Android") == 0) { quit_android(); return; }
	if (strcmp(platform, "iOS") == 0) { quit_ios(); return; }
	if (strcmp(platform, "Mac OS X") == 0) { quit_macos(); return; }
	if (strcmp(platform, "Nintendo 3DS") == 0) { quit_3ds(); return; }
	if (strcmp(platform, "PSVita") == 0) { quit_psv(); return; }
	if (strcmp(platform, "PS4") == 0 || strcmp(platform, "PS5") == 0) { quit_ps4_ps5(); return; }
	if (strcmp(platform, "Luna") == 0) { quit_luna(); return; }
	if (strcmp(platform, "PSP") == 0) { quit_psp(); return; }
	if (strcmp(platform, "FreeBSD") == 0 || strcmp(platform, "OpenBSD") == 0 || strcmp(platform, "NetBSD") == 0) { quit_bsd(); return; }
	if (strcmp(platform, "Emscripten") == 0) { quit_web(); return; }
}

static int SDL_check_required_conditions() {
	printf("INFO: Checking required runtime conditions...\n");
	if (SDL_GetNumVideoDrivers() <= 0) {
		return -1;
	}
	int num_render_drivers = SDL_GetNumRenderDrivers();
	if (num_render_drivers <= 0) return -1;
	if (SDL_GetNumAudioDrivers() <= 0) {
		return -1;
	}
	char* base_path = SDL_GetBasePath();
	if (!base_path) {
		return -1;
	}
	SDL_free(base_path);
	char* pref_path = SDL_GetPrefPath("TestCompany", "TestGame");
	if (!pref_path) {
		return -1;
	}
	SDL_free(pref_path);
	SDL_Locale* locales = SDL_GetPreferredLocales();
	if (locales) {
		SDL_free(locales);
	}
	printf("INFO: All required runtime conditions passed.\n");
	return 0;
}

static int SDL_pre_init(bool debug) {
	if (debug) {
		printf("INFO: Starting SDL Pre Initialization...\n");
	}
	if (SDL_platform_pre_init(debug) != 0) {
		if (debug) {
			fprintf(stderr, "ERROR: SDL Pre Initialization failed.\n");
		}
		return -1;
	}
	return 0;
}

#if defined(_WIN32) || defined(_WIN64)
HANDLE g_mutex = NULL;
static bool check_single_instance() {
    g_mutex = CreateMutex(NULL, TRUE, "MySDLAppMutex");
    if (GetLastError() == ERROR_ALREADY_EXISTS) {
        printf("The program is already running !\n");
        return false;
    }
    return true;
}
#else
#define LOCK_FILE "/tmp/my_sdl_app.lock"
int g_fd = -1;
bool check_single_instance() {
    g_fd = open(LOCK_FILE, O_CREAT | O_RDWR, 0666);
    if (g_fd < 0) {
        return false;
    }
    if (lockf(g_fd, F_TLOCK, 0) < 0) {
        if (errno == EACCES || errno == EAGAIN) {
            printf("The program is already running !\n");
            close(g_fd);
            return false;
        }
        close(g_fd);
        return false;
    }
    return true;
}
void release_instance_lock() {
    if (g_fd >= 0) {
        close(g_fd);
        unlink(LOCK_FILE);
    }
}
#endif

void release_instance_lock() {
    if (g_mutex) CloseHandle(g_mutex);
}

bool CreateSingleInstanceLock(){
  return check_single_instance();
}

void FreeSingleLock(){
  release_instance_lock();
}

#ifdef _WIN32
static int is_parent_vs() {
    DWORD pid = GetCurrentProcessId();
    DWORD ppid = 0;
    HANDLE snap = CreateToolhelp32Snapshot(TH32CS_SNAPPROCESS, 0);
    if (snap == INVALID_HANDLE_VALUE) return 0;
    PROCESSENTRY32 pe;
    pe.dwSize = sizeof(pe);
    if (Process32First(snap, &pe)) {
        do {
            if (pe.th32ProcessID == pid) {
                ppid = pe.th32ParentProcessID;
                break;
            }
        } while (Process32Next(snap, &pe));
    }
    CloseHandle(snap);
    snap = CreateToolhelp32Snapshot(TH32CS_SNAPPROCESS, 0);
    if (snap == INVALID_HANDLE_VALUE) return 0;
    if (Process32First(snap, &pe)) {
        do {
            if (pe.th32ProcessID == ppid) {
                CloseHandle(snap);
                return (_stricmp(pe.szExeFile, "devenv.exe") == 0);
            }
        } while (Process32Next(snap, &pe));
    }
    CloseHandle(snap);
    return 0;
}
#endif

int is_debug_console() {
#ifdef _WIN32
    if (IsDebuggerPresent()) {
        if (is_parent_vs()) {
            return 2; 
        }
        return 1; 
    }
    return 0; 
#elif __linux__
    FILE *f = fopen("/proc/self/status", "r");
    if (!f) return 0;
    char line[256];
    while (fgets(line, sizeof(line), f)) {
        if (strncmp(line, "TracerPid:", 10) == 0) {
            int pid = atoi(line + 10);
            fclose(f);
            return pid != 0 ? 1 : 0;
        }
    }
    fclose(f);
    return 0;

#elif __APPLE__
    #include <sys/types.h>
    #include <sys/sysctl.h>
    int mib[4];
    struct kinfo_proc info;
    size_t size = sizeof(info);
    info.kp_proc.p_flag = 0;
    mib[0] = CTL_KERN;
    mib[1] = KERN_PROC;
    mib[2] = KERN_PROC_PID;
    mib[3] = getpid();
    sysctl(mib, 4, &info, &size, NULL, 0);
    return ((info.kp_proc.p_flag & P_TRACED) != 0) ? 1 : 0;
#else
    return 0; 
#endif
}

char* ConvertTeaVMString(uint16_t* chars){
   return (char*)chars;
}

bool ISDebugStatus(){
	return console_status;
}

void LOG_Println(const char* mes){	
	if (mes && strlen(mes) > 0) {
		printf("%s\n", mes);
	}
}

void SDL_AllowExit(bool a){
	allowExit = a;
}

static char* detectPlatformCompileString() {
	#if defined(__WINRT__)
		return "winrt";
	#elif defined(_WIN32) || defined(__WIN32__) || defined(WIN32)
		return "win32";
	#elif defined(__APPLE__) && defined(__MACH__)
	#include <TargetConditionals.h>
	#if TARGET_OS_IPHONE && TARGET_IPHONE_SIMULATOR
		return "ios-simulator";
	#elif TARGET_OS_IPHONE
		return "ios";
	#else
		return "macos";
	#endif
	#elif defined(__ANDROID__)
		return "android";
	#elif defined(__linux__)
		return "linux";
	#elif defined(__unix__)
		return "unix";
	#elif defined(__SWITCH__)
		return "nintendo-switch";
	#elif defined(__ORBIS__) || defined(__PS4__)
		return "ps4";
	#elif defined(__PROSPERO__) || defined(__PS5__)
		return "ps5";
	#elif defined(_DURANGO) || defined(__XBOX_ONE__)
		return "xbox-one";
	#elif defined(_SCARLETT) || defined(__XBOX_SERIES_X__)
		return "xbox-series-x";
	#elif defined(__STEAM_DECK__)
		return "steam-deck";
	#else
		return NULL;
	#endif
}

static char* detectPlatformRuntimeString() {
	#if defined(_WIN32)
		return "windows";
	#elif defined(__unix__) || defined(__APPLE__) || defined(__linux__)
		static char buffer[64];
		struct utsname sysinfo;
		if (uname(&sysinfo) == 0) {
			if (strstr(sysinfo.sysname, "Linux")) {
				if (getenv("ANDROID_ROOT") != NULL) return "android";
				return "linux";
			}
			if (strstr(sysinfo.sysname, "Darwin")) return "macos";
			if (strstr(sysinfo.sysname, "Unix")) return "unix";
		}
		return "unknown-unix";
	#else
		return "unknown";
	#endif
}

static char* getOSVersionString() {
	#if defined(_WIN32) || defined(_WIN64)
		char* ver_str = "unknown";
		if (IsWindows10OrGreater()) {
			if (IsWindowsServer()) {
				ver_str = "Windows Server 2016/2019/2022 or newer";
			}
			else {
				ver_str = "Windows 10 or newer";
			}
		}
		else if (IsWindows8Point1OrGreater()) {
			if (IsWindowsServer()) {
				ver_str = "Windows Server 2012 R2";
			}
			else {
				ver_str = "Windows 8.1";
			}
		}
		else if (IsWindows8OrGreater()) {
			if (IsWindowsServer()) {
				ver_str = "Windows Server 2012";
			}
			else {
				ver_str = "Windows 8";
			}
		}
		else if (IsWindows7SP1OrGreater()) {
			if (IsWindowsServer()) {
				ver_str = "Windows Server 2008 R2 SP1";
			}
			else {
				ver_str = "Windows 7 SP1";
			}
		}
		else if (IsWindows7OrGreater()) {
			if (IsWindowsServer()) {
				ver_str = "Windows Server 2008 R2";
			}
			else {
				ver_str = "Windows 7";
			}
		}
		else if (IsWindowsVistaSP2OrGreater()) {
			if (IsWindowsServer()) {
				ver_str = "Windows Server 2008 SP2";
			}
			else {
				ver_str = "Windows Vista SP2";
			}
		}
		else if (IsWindowsVistaSP1OrGreater()) {
			ver_str = "Windows Vista SP1 / Server 2008";
		}
		else if (IsWindowsVistaOrGreater()) {
			ver_str = "Windows Vista / Server 2008";
		}
		else if (IsWindowsXPOrGreater()) {
			ver_str = "Windows XP or newer";
		}
		else {
			ver_str = "Older than Windows XP";
		}
	    return ver_str;
	#elif defined(__unix__) || defined(__APPLE__) || defined(__linux__)
		static char version[128];
		struct utsname sysinfo;
		if (uname(&sysinfo) == 0) {
			snprintf(version, sizeof(version), "%s", sysinfo.release);
			return version;
		}
		return "unknown";
	#else
		return "unknown";
	#endif
}

static char* getArchitectureString() {
	#if defined(_WIN64) || defined(__x86_64__) || defined(__amd64__)
		return "x86_64";
	#elif defined(_WIN32)
		return "x86";
	#elif defined(__aarch64__)
		return "arm64";
	#elif defined(__arm__)
		return "arm";
	#elif defined(__ppc64__)
		return "ppc64";
	#elif defined(__ppc__)
		return "ppc";
	#else
		return "unknown";
	#endif
}

static char* detectVirtualizationString() {
#if defined(__linux__)
	FILE* f = fopen("/proc/1/cgroup", "r");
	if (f) {
		char line[256];
		while (fgets(line, sizeof(line), f)) {
			if (strstr(line, "docker") || strstr(line, "containerd")) {
				fclose(f);
				return "docker";
			}
			if (strstr(line, "kubepods")) {
				fclose(f);
				return "kubernetes";
			}
		}
		fclose(f);
	}
	FILE* cpuinfo = fopen("/proc/version", "r");
	if (cpuinfo) {
		char buf[256];
		if (fgets(buf, sizeof(buf), cpuinfo)) {
			if (strstr(buf, "Microsoft")) {
				fclose(cpuinfo);
				return "wsl";
			}
		}
		fclose(cpuinfo);
	}
	return "none";
#elif defined(_WIN32)
	return "unknown";
#else
	return "unknown";
#endif
}

static char* getGpuInfoString() {
#if defined(_WIN32)
	static char gpu[128] = "unknown";
	DISPLAY_DEVICE dd;
	ZeroMemory(&dd, sizeof(dd));
	dd.cb = sizeof(dd);
	if (EnumDisplayDevices(NULL, 0, &dd, 0)) {
		snprintf(gpu, sizeof(gpu), "%ws", dd.DeviceString);
	}
	return gpu;
#elif defined(__linux__) && !defined(__ANDROID__)
	FILE* fp = popen("lspci | grep -i 'vga\\|3d\\|2d'", "r");
	static char gpu[256] = "unknown";
	if (fp) {
		if (fgets(gpu, sizeof(gpu), fp)) {
			gpu[strcspn(gpu, "\n")] = 0;
		}
		pclose(fp);
	}
	return gpu;
#else
	return "unknown";
#endif
}

static int getCpuCores() {
#if defined(_WIN32)
	SYSTEM_INFO sysinfo;
	GetSystemInfo(&sysinfo);
	return sysinfo.dwNumberOfProcessors;
#elif defined(_SC_NPROCESSORS_ONLN)
	return (int)sysconf(_SC_NPROCESSORS_ONLN);
#else
	return 1;
#endif
}

static void getMemoryInfoInts(int64_t* total, int64_t* free) {
#if defined(_WIN32)
	MEMORYSTATUSEX statex;
	statex.dwLength = sizeof(statex);
	if (GlobalMemoryStatusEx(&statex)) {
		*total = statex.ullTotalPhys;
		*free = statex.ullAvailPhys;
	}
	else {
		*total = *free = 0;
	}
#elif defined(__linux__) && !defined(__APPLE__)
	struct sysinfo info;
	if (sysinfo(&info) == 0) {
		*total = (int64_t)info.totalram * info.mem_unit;
		*free = (int64_t)info.freeram * info.mem_unit;
	}
	else {
		*total = *free = 0;
	}
#elif defined(__APPLE__)
	*total = *free = 0;
#else
	* total = *free = 0;
#endif
}

#ifdef _WIN32
char* GetPathFullName(const char* path) {
	char* buffer = (char*)malloc(MAX_PATH * sizeof(char));
	DWORD length = GetFullPathNameA(path, MAX_PATH, buffer, NULL);
	if (length == 0) {
		return 0;
	}
	return buffer;
}
#elif defined(__unix__) || defined(__APPLE__)
int32_t GetPathFullName(const char* path) {
	char* ret = realpath(path, NULL);
	return ret;
}
#endif

static char* get_path_separator() {
#ifdef _WIN32
	return "\\";
#else
	return "/";
#endif
}

static char* get_newline_separator() {
#ifdef _WIN32
	return "\r\n";
#else
	return "\n";
#endif
}

#if defined(__SWITCH__) || defined(NINTENDO_SWITCH)
static char* switch_get_username() { return "SwitchUser"; }
static char* switch_get_temp_folder() { return "sdmc:/temp"; }
static char* switch_get_home_folder() { return "sdmc:/"; }
#endif

#if defined(PS5) || defined(__ORBIS__) || defined(__PROSPERO__)
static char* ps5_get_username() { return "PS5User"; }
static char* ps5_get_temp_folder() { return "/data/temp"; }
static char* ps5_get_home_folder(vid) { return "/data/home"; }
#endif

#if defined(XBOX) || defined(_DURANGO) || defined(_GAMING_XBOX)
static char* xbox_get_username() { return "XboxUser"; }
static char* xbox_get_temp_folder() { return "D:/Temp"; }
static char* xbox_get_home_folder() { return "D:/Home"; }
#endif

static char* get_system_username() {
	static char username[256] = { 0 };
	#if defined(__SWITCH__) || defined(NINTENDO_SWITCH)
		return switch_get_username();
	#elif defined(PS5) || defined(__ORBIS__) || defined(__PROSPERO__)
		return ps5_get_username();
	#elif defined(XBOX) || defined(_DURANGO) || defined(_GAMING_XBOX)
		return xbox_get_username();
	#elif defined(_WIN32)
		DWORD size = sizeof(username);
		if (GetUserNameA(username, &size)) return username;
	#elif defined(__unix__) || defined(__APPLE__) || defined(__MACH__)
		const char* env_user = getenv("USER");
		if (env_user && *env_user) { strncpy(username, env_user, sizeof(username) - 1); return username; }
		struct passwd* pw = getpwuid(getuid());
		if (pw && pw->pw_name) { strncpy(username, pw->pw_name, sizeof(username) - 1); return username; }
	#endif
		return "";
}

static char* get_temp_folder() {
	static char temp_path[512] = { 0 };
	#if defined(__SWITCH__) || defined(NINTENDO_SWITCH)
		return switch_get_temp_folder();
	#elif defined(PS5) || defined(__ORBIS__) || defined(__PROSPERO__)
		return ps5_get_temp_folder();
	#elif defined(XBOX) || defined(_DURANGO) || defined(_GAMING_XBOX)
		return xbox_get_temp_folder();
	#elif defined(_WIN32)
		DWORD len = GetTempPathA(sizeof(temp_path), temp_path);
		if (len > 0 && len < sizeof(temp_path)) return temp_path;
	#elif defined(__unix__) || defined(__APPLE__) || defined(__MACH__)
		const char* tmp = getenv("TMPDIR");
		if (!tmp) tmp = getenv("TEMP");
		if (!tmp) tmp = getenv("TMP");
		if (!tmp) tmp = "/tmp";
		strncpy(temp_path, tmp, sizeof(temp_path) - 1);
		return temp_path;
	#endif
		return ".";
}

static char* get_home_folder(void) {
	static char home_path[512] = { 0 };
	#if defined(__SWITCH__) || defined(NINTENDO_SWITCH)
		return switch_get_home_folder();
	#elif defined(PS5) || defined(__ORBIS__) || defined(__PROSPERO__)
		return ps5_get_home_folder();
	#elif defined(XBOX) || defined(_DURANGO) || defined(_GAMING_XBOX)
		return xbox_get_home_folder();
	#elif defined(_WIN32)
		if (SUCCEEDED(SHGetFolderPathA(NULL, CSIDL_PROFILE, NULL, 0, home_path))) return home_path;
		const char* env_home = getenv("USERPROFILE");
		if (env_home && *env_home) { chars_strncpy(home_path, env_home, sizeof(home_path) - 1); return home_path; }
	#elif defined(__unix__) || defined(__APPLE__) || defined(__MACH__)
		const char* home = getenv("HOME");
		if (home && *home) { chars_strncpy(home_path, home, sizeof(home_path) - 1); return home_path; }
		struct passwd* pw = getpwuid(getuid());
		if (pw && pw->pw_dir) { chars_strncpy(home_path, pw->pw_dir, sizeof(home_path) - 1); return home_path; }
	#endif
	return ".";
}

static int is_printable_text(const unsigned char* data, size_t len) {
	for (size_t i = 0; i < len; i++) {
		if (!isprint(data[i]) && !isspace(data[i])) return 0;
	}
	return 1;
}

static char* base64_encode(const unsigned char* data, size_t len) {
	char* encoded;
	size_t out_len = 4 * ((len + 2) / 3);
	encoded = (char*)malloc(out_len + 1);
	if (!encoded) return NULL;

	size_t i, j;
	for (i = 0, j = 0; i < len;) {
		unsigned octet_a = i < len ? data[i++] : 0;
		unsigned octet_b = i < len ? data[i++] : 0;
		unsigned octet_c = i < len ? data[i++] : 0;

		unsigned triple = (octet_a << 16) | (octet_b << 8) | octet_c;

		encoded[j++] = b64_table[(triple >> 18) & 0x3F];
		encoded[j++] = b64_table[(triple >> 12) & 0x3F];
		encoded[j++] = (i > len + 1) ? '=' : b64_table[(triple >> 6) & 0x3F];
		encoded[j++] = (i > len) ? '=' : b64_table[triple & 0x3F];
	}
	encoded[j] = '\0';
	return encoded;
}

static unsigned char* base64_decode(const char* data, size_t* out_len) {
	if (!data) return NULL;
	size_t len = strlen(data);
	if (len % 4 != 0) return NULL;

	size_t alloc_len = len / 4 * 3;
	unsigned char* decoded = (unsigned char*)malloc(alloc_len);
	if (!decoded) return NULL;

	int table[256];
	memset(table, -1, sizeof(table));
	for (int i = 0; i < 64; i++) table[(unsigned char)b64_table[i]] = i;

	size_t i, j;
	for (i = 0, j = 0; i < len;) {
		int sextet_a = data[i] == '=' ? 0 & i++ : table[(unsigned char)data[i++]];
		int sextet_b = data[i] == '=' ? 0 & i++ : table[(unsigned char)data[i++]];
		int sextet_c = data[i] == '=' ? 0 & i++ : table[(unsigned char)data[i++]];
		int sextet_d = data[i] == '=' ? 0 & i++ : table[(unsigned char)data[i++]];

		unsigned triple = (sextet_a << 18) | (sextet_b << 12) |
			(sextet_c << 6) | sextet_d;

		if (j < alloc_len) decoded[j++] = (triple >> 16) & 0xFF;
		if (j < alloc_len) decoded[j++] = (triple >> 8) & 0xFF;
		if (j < alloc_len) decoded[j++] = triple & 0xFF;
	}
	*out_len = j;
	return decoded;
}

static char* joinLocales(const SDL_Locale* locales) {
	if (!locales) return 0;
	size_t total_len = 0;
	int count = 0;
	for (const SDL_Locale* loc = locales; loc->language != NULL; loc++) {
		total_len += strlen(loc->language);
		if (loc->country) {
			total_len += 1 + strlen(loc->country);
		}
		total_len += 2;
		count++;
	}
	if (count == 0) {
		return 0;
	}
	char* result = (char*)malloc(total_len);
	if (!result) {
		return 0;
	}
	result[0] = '\0';
	for (const SDL_Locale* loc = locales; loc->language != NULL; loc++) {
		chars_append(result, total_len, loc->language);
		if (loc->country) {
			chars_append(result, total_len, "_");
			chars_append(result, total_len, loc->country);
		}
		if ((loc + 1)->language != NULL) {
			chars_append(result, total_len, ", ");
		}
	}
	return result;
}

int64_t CreatePrefs() {
	game_preferences* prefs = (game_preferences*)malloc(sizeof(game_preferences));
	if (!prefs) return 0;
	prefs->head = NULL;
	return (intptr_t)prefs;
}

bool LoadPrefs(int64_t handle, const char* filename) {
	game_preferences* prefs = (game_preferences*)handle;
	if (!prefs) return false;
	FILE* f = fopen(filename, "r");
	if (!f) return false;

	char line[512];
	char current_section[128] = "";

	while (fgets(line, sizeof(line), f)) {
		char* newline = strchr(line, '\n');
		if (newline) *newline = '\0';
		if (line[0] == '\0') continue;
		if (line[0] == '[') {
			char* end = strchr(line, ']');
			if (end) {
				*end = '\0';
				chars_append(current_section, sizeof(current_section) - 1, line + 1);
				current_section[sizeof(current_section) - 1] = '\0';
			}
		}
		else {
			char* eq = strchr(line, '=');
			if (!eq) continue;
			*eq = '\0';
			char* key = line;
			char* val_str = eq + 1;
			char* b64_marker = strstr(key, "|b64");
			if (b64_marker) {
				*b64_marker = '\0';
				size_t val_len;
				unsigned char* decoded = base64_decode(val_str, &val_len);
				if (decoded) {
					SetPrefs(handle, current_section, key, decoded, val_len);
					free(decoded);
				}
			}
			else {
				SetPrefs(handle, current_section, key,
					(const unsigned char*)val_str, strlen(val_str));
			}
		}
	}
	fclose(f);
	return true;
}

void SetPrefs(int64_t handle, const char* section, const char* key,
	const uint8_t* value, size_t value_len) {
	game_preferences* prefs = (game_preferences*)handle;
	if (!prefs) return;
	game_prefnode* prev = NULL, * cur = prefs->head;
	while (cur && (strcmp(cur->section, section) < 0 ||
		(strcmp(cur->section, section) == 0 && strcmp(cur->key, key) < 0))) {
		prev = cur;
		cur = cur->next;
	}
	if (cur && strcmp(cur->section, section) == 0 && strcmp(cur->key, key) == 0) {
		free(cur->value);
		cur->value = (uint8_t*)malloc(value_len);
		if (cur->value) {
			memcpy(cur->value, value, value_len);
			cur->value_len = value_len;
		}
		return;
	}
	game_prefnode* newNode = (game_prefnode*)malloc(sizeof(game_prefnode));
	if (newNode) {
		newNode->section = _strdup(section);
		newNode->key = _strdup(key);
		newNode->value = (uint8_t*)malloc(value_len);
		if (newNode->value) {
			memcpy(newNode->value, value, value_len);
			newNode->value_len = value_len;
		}
	}
	if (prev) {
		if (newNode && newNode->next) {
			newNode->next = prev->next;
			prev->next = newNode;
		}
	} else if(newNode && newNode->next){
		newNode->next = prefs->head;
		prefs->head = newNode;
	}
}

int64_t GetPrefs(int64_t handle, const char* section, const char* key,uint8_t* outBytes) {
	game_preferences* prefs = (game_preferences*)handle;
	if (!prefs) return 0;
	game_prefnode* cur = prefs->head;
	while (cur) {
		if (strcmp(cur->section, section) == 0 && strcmp(cur->key, key) == 0) {
			size_t len = 0;
			if (cur->value_len) {
				len = cur->value_len;
			}
			unsigned char* copy = (unsigned char*)malloc(cur->value_len);
			if (copy) {
				memcpy(copy, cur->value, cur->value_len);
				if (copy) {
					copy_uint8_array(outBytes, len, copy, len);
				}
				free(copy);
			}
			return (int32_t)len;
		}
		cur = cur->next;
	}
	return 0;
}

const char* GetPrefsKeys(int64_t handle, const char* section, const char* delimiter) {
	game_preferences* prefs = (game_preferences*)handle;
	if (!prefs) return NULL;
	game_prefnode* cur = prefs->head;
	while (cur) {
		if (strcmp(cur->section, section) == 0) {
			size_t total_len = 0;
			size_t delim_len = strlen(delimiter);
			int count = 0;
			for (game_prefnode* n = cur->next; n; n = n->next) {
				total_len += strlen(n->key);
				if (n->next) total_len += delim_len;
				count++;
			}
			if (count == 0) return _strdup("");
			char* result = malloc(total_len + 1);
			if (!result) return NULL;
			result[0] = '\0';
			for (game_prefnode* n = cur->next; n; n = n->next) {
				chars_append(result, total_len + 1, n->key);
				if (n->next) chars_append(result, total_len + 1, delimiter);
			}
			return result;
		}
		cur = cur->next;
	}
	return NULL;
}


bool SavePrefs(int64_t handle, const char* filename) {
	game_preferences* prefs = (game_preferences*)handle;
	if (!prefs) return false;
	FILE* f = fopen(filename, "w");
	if (!f) return false;
	char current_section[128] = "";
	game_prefnode* cur = prefs->head;
	while (cur) {
		if (strcmp(current_section, cur->section) != 0) {
			fprintf(f, "[%s]\n", cur->section);
			chars_append(current_section, sizeof(current_section) - 1, cur->section);
			current_section[sizeof(current_section) - 1] = '\0';
		}
		if (is_printable_text(cur->value, cur->value_len)) {
			fprintf(f, "%s=%.*s\n", cur->key, (int)cur->value_len, cur->value);
		}
		else {
			char* encoded = base64_encode(cur->value, cur->value_len);
			if (!encoded) {
				fclose(f);
				return 0;
			}
			fprintf(f, "%s|b64=%s\n", cur->key, encoded);
			free(encoded);
		}
		cur = cur->next;
	}
	fclose(f);
	return true;
}

void RemovePrefs(int64_t handle, const char* section, const char* key) {
	game_preferences* prefs = (game_preferences*)handle;
	if (!prefs) return;
	game_prefnode* cur = prefs->head, * prev = NULL;
	while (cur) {
		if (strcmp(cur->section, section) == 0 && strcmp(cur->key, key) == 0) {
			if (prev) prev->next = cur->next;
			else prefs->head = cur->next;
			free(cur->section);
			free(cur->key);
			free(cur->value);
			free(cur);
			return;
		}
		prev = cur;
		cur = cur->next;
	}
}

void FreePrefs(int64_t handle) {
	game_preferences* prefs = (game_preferences*)handle;
	if (!prefs) return;
	game_prefnode* cur = prefs->head;
	while (cur) {
		game_prefnode* tmp = cur;
		cur = cur->next;
		free(tmp->section);
		free(tmp->key);
		free(tmp->value);
		free(tmp);
	}
	free(prefs);
}

const char* Load_SDL_RW_FileToChars(const char* filename) {
	SDL_RWops* rw = SDL_RWFromFile(filename, "rb");
	if (!rw) {
		return NULL;
	}
	char* buffer = NULL;
	size_t totalSize = 0;
	size_t capacity = 0;
	char temp[MAX_CHUNK_SIZE];
	size_t bytesRead;
	while ((bytesRead = SDL_RWread(rw, temp, 1, MAX_CHUNK_SIZE)) > 0) {
		if (totalSize + bytesRead + 1 > capacity) {
			size_t newCapacity = (capacity == 0) ? bytesRead + 1 : capacity * 2;
			if (newCapacity < totalSize + bytesRead + 1) {
				newCapacity = totalSize + bytesRead + 1;
			}
			char* newBuffer = (char*)realloc(buffer, newCapacity);
			if (!newBuffer) {
				free(buffer);
				SDL_RWclose(rw);
				return NULL;
			}
			buffer = newBuffer;
			capacity = newCapacity;
		}
		memcpy(buffer + totalSize, temp, bytesRead);
		totalSize += bytesRead;
	}
	if (buffer) {
		buffer[totalSize] = '\0';
	}
	SDL_RWclose(rw);
	return buffer;
}

int64_t Load_SDL_RW_FileSize(const char* filename)
{
	if (!filename) {
		return 0;
	}
	SDL_RWops* rw = SDL_RWFromFile(filename, "rb");
	if (!rw) {
		return 0;
	}
	Sint64 fileSize = SDL_RWsize(rw);
	if (fileSize <= 0) {
		SDL_RWclose(rw);
		return 0;
	}
	return (int64_t)fileSize;
}

int64_t Load_SDL_RW_FileToBytes(const char* filename , uint8_t* outBytes) {
	if (!filename) {
		return 0;
	}
	SDL_RWops* rw = SDL_RWFromFile(filename, "rb");
	if (!rw) {
		return 0;
	}
	Sint64 fileSize = SDL_RWsize(rw);
	if (fileSize <= 0) {
		SDL_RWclose(rw);
		return 0;
	}
	uint8_t* buffer = (uint8_t*)malloc(fileSize);
	if (!buffer) {
		SDL_RWclose(rw);
		return 0;
	}
	size_t totalRead = SDL_RWread(rw, buffer, 1, fileSize);
	SDL_RWclose(rw);
	if (totalRead != (size_t)fileSize) {
		free(buffer);
		return 0;
	}
	copy_uint8_array(outBytes, (size_t)fileSize, buffer, (size_t)fileSize);
	free(buffer);
	return (int64_t)fileSize;
}

bool Load_SDL_RW_FileExists(const char* filename) {
	if (!filename || !*filename) return false;
	SDL_RWops* rw = SDL_RWFromFile(filename, "rb");
	if (rw) {
		SDL_RWclose(rw);
		return true;
	}
	return false;
}

bool FileExists(const char* filename) {
	if (!filename || !*filename) return false;
	FILE* f = fopen(filename, "rb");
	if (f) {
		fclose(f);
		return true;
	}
	return false;
}

void Load_SDL_Cleanup() {
#ifndef LOON_DESKTOP
	if (display) {
		eglMakeCurrent(display, EGL_NO_SURFACE, EGL_NO_SURFACE, EGL_NO_CONTEXT);
		if (context)
			eglDestroyContext(display, context);
		if (eglsurface)
			eglDestroySurface(display, eglsurface);
		eglTerminate(display);
	}
	#ifdef __SWITCH__
		romfsExit();
	#endif
#endif
	Load_SDL_Quit();
}

#ifdef LOON_DESKTOP
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
static uint64_t remapPadButtons(uint64_t buttons, uint32_t style) {
	uint64_t mapped = buttons;

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

static void remapPadAxes(float* axes, uint32_t style) {
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
	if (g_isLooping && g_currentMusic && g_loopCount != 0) {
		if (g_loopCount > 0) g_loopCount--; 
		Mix_PlayMusic(g_currentMusic->handle, 0);
	}
	else {
		g_isLooping = false;
		g_currentMusic = NULL;
	}
}

int64_t Load_SDL_ScreenInit(const char* title, const int w, const int h, const bool vsync, const bool emTouch, const int flags , const bool debug) {
	atexit(SDL_Quit);
	SetConsoleOutputCP(65001);
	SetConsoleCP(65001);
	console_status = is_debug_console();
	for (int i = 0; i < MAX_TOUCH_DEVICES; i++) {
		g_touches[i * 3] = -1;
	}
	for (int i = 0; i < SDL_NUM_SCANCODES; i++) {
		g_keyStates[i] = -1;
		g_pressedKeys[i] = -1;
		g_releasedKeys[i] = -1;
	}
	g_initWidth = w;
	g_initHeight = h;
	SDL_InitTouchIdMap();
#ifndef LOON_DESKTOP
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
	eglsurface = eglCreateWindowSurface(display, config, nwindowGetDefault(), NULL);
	static const EGLint contextAttributeList[] =
	{
		EGL_CONTEXT_OPENGL_PROFILE_MASK_KHR, EGL_CONTEXT_OPENGL_CORE_PROFILE_BIT_KHR,
		EGL_CONTEXT_MAJOR_VERSION_KHR, 2,
		EGL_CONTEXT_MINOR_VERSION_KHR, 0,
		EGL_NONE
	};
	context = eglCreateContext(display, config, EGL_NO_CONTEXT, contextAttributeList);
	eglMakeCurrent(display, eglsurface, eglsurface, context);
	gladLoadGL();
	SDL_Init(SDL_INIT_AUDIO);
#else
	SDL_pre_init(debug);
	SDL_SetHint(SDL_HINT_TOUCH_MOUSE_EVENTS, "0");
	SDL_SetHint(SDL_HINT_MOUSE_TOUCH_EVENTS, emTouch ? "1" : "0");
	SDL_SetHint(SDL_HINT_OPENGL_ES_DRIVER, "1");
	#ifdef __WINRT__
		SDL_SetHint("SDL_WINRT_HANDLE_BACK_BUTTON", "1");
	#endif
	SDL_Init(SDL_INIT_VIDEO | SDL_INIT_AUDIO | SDL_INIT_GAMECONTROLLER);
	if (SDL_check_required_conditions() != 0) {
		if (debug) {
			fprintf(stderr, "ERROR: Required runtime conditions not met.\n");
		}
		SDL_Quit();
		SDL_platform_free_quit();
		return -1;
	}
	if (debug) {
		printf("INFO: SDL initialized successfully.\n");
	}
	SDL_GL_SetAttribute(SDL_GL_CONTEXT_PROFILE_MASK, SDL_GL_CONTEXT_PROFILE_ES);
	SDL_GL_SetAttribute(SDL_GL_CONTEXT_MAJOR_VERSION, 2);
	SDL_GL_SetAttribute(SDL_GL_CONTEXT_MINOR_VERSION, 0);
	SDL_GL_SetAttribute(SDL_GL_RED_SIZE, 8);
	SDL_GL_SetAttribute(SDL_GL_GREEN_SIZE, 8);
	SDL_GL_SetAttribute(SDL_GL_BLUE_SIZE, 8);
	SDL_GL_SetAttribute(SDL_GL_ALPHA_SIZE, 8);
	SDL_GL_SetAttribute(SDL_GL_DEPTH_SIZE, 24);
	SDL_GL_SetAttribute(SDL_GL_DOUBLEBUFFER, 1);
	window = SDL_CreateWindow(title, SDL_WINDOWPOS_CENTERED, SDL_WINDOWPOS_CENTERED, w, h, flags == 0 ? SDL_WINDOW_OPENGL : SDL_WINDOW_OPENGL | SDL_WINDOW_ALLOW_HIGHDPI | flags);
    if (!window) {
        printf("Window creation failed: %s\n", SDL_GetError());
        return -1;
    }
	if (tempContext) {
		SDL_GL_DeleteContext(tempContext);
		tempContext = NULL;
	}
	tempContext = (SDL_GLContext*)SDL_GL_CreateContext(window);
	if (!tempContext) {
        printf("GL context creation failed: %s\n", SDL_GetError());
        return -1;
    }
	#ifdef LOON_DESKTOP 
        #ifndef GLEW
		if (!gladLoadGLES2((GLADloadfunc)SDL_GL_GetProcAddress)) {
			printf("Failed to load GLES2 functions\n");
			return -1;
		}
       #endif
	#endif
	SDL_GL_MakeCurrent(window, tempContext);
	SDL_GL_SetSwapInterval(vsync ? 1 : 0);
#endif
	int err = Mix_Init(MIX_INIT_MP3 | MIX_INIT_OGG);
	if (err != -1) {
		err = Mix_OpenAudio(audio_rate, audio_format, audio_channels, audio_buffers);
		if (err != -1) {
			if (debug) {
				int srcfrequency, srcchannels;
				Uint16 srcformat;
				if (Mix_QuerySpec(&srcfrequency, &srcformat, &srcchannels) == 0) {
					SDL_Log("Mix_QuerySpec failed: %s\n", Mix_GetError());
				}
				else {
					SDL_Log("Audio Spec:\n");
					SDL_Log("Frequency: %d Hz\n", srcfrequency);
					SDL_Log("Format:    0x%X\n", srcformat);
					SDL_Log("Channels:  %d\n", srcchannels);
				}
			}
			Mix_AllocateChannels(32);
			Mix_HookMusicFinished(OnMusicFinished);
			Mix_ChannelFinished(OnSoundFinished);
		}
	}
	return (intptr_t)window;
}

int64_t Load_SDL_WindowHandle() {
	if (!window) {
		return -1;
	}
	return (intptr_t)window;
}

static void udate_init_window_size() {
	int winWidth = 0, winHeight = 0;
	SDL_GL_GetDrawableSize(window, &winWidth, &winHeight);
	g_windowSize[0] = winWidth;
	g_windowSize[1] = winHeight;
	g_initWidth = winWidth;
	g_initHeight = winHeight;
	if (g_initWidth == 0 || g_initHeight == 0) {
		SDL_GetWindowSize(window, &winWidth, &winHeight);
		g_windowSize[0] = winWidth;
		g_windowSize[1] = winHeight;
		g_initWidth = winWidth;
		g_initHeight = winHeight;
	}
}

int Load_SDL_WindowWidth() {
	return g_initWidth;
}

int Load_SDL_WindowHeight() {
	return g_initHeight;
}

bool Load_SDL_Update() {
#ifndef LOON_DESKTOP
	padUpdate(&combinedPad);
	uint64_t kDown = padGetButtonsDown(&combinedPad);
	if (kDown & HidNpadButton_Plus) {
		return false;
	}
	for (int i = 0; i < 8; i++) {
		padUpdate(&pads[i]);
	}
	HidTouchScreenState touchState;
	if (hidGetTouchScreenStates(&touchState, 1)) {
		for (int i = 0; i < MAX_TOUCH_DEVICES; i++)
			if (i < touchState.count) {
				g_touches[i * 3 + 0] = touchState.g_touches[i].finger_id;
				g_touches[i * 3 + 1] = touchState.g_touches[i].x;
				g_touches[i * 3 + 2] = touchState.g_touches[i].y;
			}
			else {
				g_touches[i * 3 + 0] = -1;
				g_touches[i * 3 + 1] = 0;
				g_touches[i * 3 + 2] = 0;
			}
	}
	eglSwapBuffers(display, eglsurface);
	return appletMainLoop();
#else
	  memset(g_pressedKeys, 0, sizeof(g_pressedKeys));
	  memset(g_releasedKeys, 0, sizeof(g_releasedKeys));
      int running = 1;
	  SDL_Event event;
	  int axis = 0;
	  int touchId = -1;
	  int touchX = 0;
	  int touchY = 0;
	  if (g_initWidth == 0 || g_initHeight == 0) {
		  udate_init_window_size();
	  }
	  while (SDL_PollEvent(&event)) {
		  tempPolleventType = event.type;
		  switch (tempPolleventType) {
		  case SDL_QUIT:
			  running = 0;
			  g_keyStates[SDL_SCANCODE_ESCAPE] = 1;
			  g_lastPressedScancode = -1;
			  return Load_SDL_Exit(running);
		  case SDL_WINDOWEVENT:
			  if (event.window.event == SDL_WINDOWEVENT_FOCUS_LOST || event.window.event == SDL_WINDOWEVENT_MINIMIZED) {
				  g_isPause = true;
			  }
			  else if (event.window.event == SDL_WINDOWEVENT_FOCUS_GAINED || event.window.event == SDL_WINDOWEVENT_RESTORED) {
				  g_isPause = false;
			  }
			  if (event.window.event == SDL_WINDOWEVENT_RESIZED ||
				  event.window.event == SDL_WINDOWEVENT_SIZE_CHANGED) {
				  udate_init_window_size();
			  }
			  break;
		  case SDL_MOUSEMOTION:
			  g_touches[0] = 1;
			  g_touches[1] = event.motion.x;
			  g_touches[2] = event.motion.y;
			  break;
		  case SDL_MOUSEBUTTONDOWN:
			  g_touches[0] = 0;
			  g_touches[1] = event.button.x;
			  g_touches[2] = event.button.y;
			  break;
		  case SDL_MOUSEBUTTONUP:
			  g_touches[0] = -1;
			  g_touches[1] = event.button.x;
			  g_touches[2] = event.button.y;
			  break;
		  case SDL_FINGERMOTION:
			  touchId = SDL_ConvertMapTouchIdToIndex(event.tfinger.touchId);
			  touchX = (int)roundf(fmaxf(0.0f, fminf(event.tfinger.x, 1.0f)) * g_initWidth);
			  touchY = (int)roundf(fmaxf(0.0f, fminf(event.tfinger.y, 1.0f)) * g_initHeight);
			  if (touchId > -1 && touchId < MAX_TOUCH_DEVICES) {
				  g_touches[touchId * 3] = touchId;
				  g_touches[touchId * 3 + 1] = touchX;
				  g_touches[touchId * 3 + 2] = touchY;
			  }
			  break;
		  case SDL_FINGERDOWN:
			  touchId = SDL_ConvertMapTouchIdToIndex(event.tfinger.touchId);
			  touchX = (int)roundf(fmaxf(0.0f, fminf(event.tfinger.x, 1.0f)) * g_initWidth);
			  touchY = (int)roundf(fmaxf(0.0f, fminf(event.tfinger.y, 1.0f)) * g_initHeight);
			  if (touchId > -1 && touchId < MAX_TOUCH_DEVICES) {
				  g_touches[touchId * 3] = touchId;
				  g_touches[touchId * 3 + 1] = touchX;
				  g_touches[touchId * 3 + 2] = touchY;
			  }
			  break;
		  case SDL_FINGERUP:
		  case SDL_MULTIGESTURE:
			  touchId = SDL_ConvertMapTouchIdToIndex(event.tfinger.touchId);
			  touchX = (int)roundf(fmaxf(0.0f, fminf(event.tfinger.x, 1.0f)) * g_initWidth);
			  touchY = (int)roundf(fmaxf(0.0f, fminf(event.tfinger.y, 1.0f)) * g_initHeight);
			  if (touchId > -1 && touchId < MAX_TOUCH_DEVICES) {
				  g_touches[touchId * 3] = -1;
				  g_touches[touchId * 3 + 1] = touchX;
				  g_touches[touchId * 3 + 2] = touchY;
			  }
			  break;
		  case SDL_KEYDOWN:
			  //分开存储键盘和游戏手柄事件，避免混淆
			  if (!g_keyStates[event.key.keysym.scancode]){
				  g_pressedKeys[event.key.keysym.scancode] = 1;
				  g_lastPressedScancode = event.key.keysym.scancode;
			  }
			  g_keyStates[event.key.keysym.scancode] = 1;
			  buttons |= keyToButton(event.key.keysym.scancode);
			  axis = keyToAxis(event.key.keysym.scancode);
			  if (axis > -1 && !event.key.repeat)
				  joysticks[axis & 0x3] += axis & 0x4 ? -1 : 1;
			  break;
		  case SDL_KEYUP:
			  //分开存储键盘和游戏手柄事件，避免混淆
			  g_keyStates[event.key.keysym.scancode] = 0;
			  g_releasedKeys[event.key.keysym.scancode] = 1;
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
			  if (event.caxis.axis >= 0 && event.caxis.axis < 4) {
				  float normValue = (float)event.caxis.value / 32767.0f;
				  if (fabsf(normValue) < 0.0f) {
					  normValue = 0.0f;
				  }
				  joysticks[event.caxis.axis] = normValue;
			  }
			  for (int i = 0; i < 2; i++)
				  if (event.caxis.axis == SDL_CONTROLLER_AXIS_TRIGGERLEFT + i) {
					  if (event.caxis.value > 512)
						  buttons |= 1 << (8 + i);
					  else
						  buttons &= ~(1 << (8 + i));
				  }
			  break;
		  case SDL_CONTROLLERDEVICEADDED:
			  tempController = SDL_GameControllerOpen(event.cdevice.which);
			  break;
		  case SDL_CONTROLLERDEVICEREMOVED:
			  SDL_GameControllerClose(SDL_GameControllerFromPlayerIndex(event.cdevice.which));
			  break;
		  case SDL_TEXTINPUT:
			  for (int i = 0; i < MAX_TEXTINPUT_CAHR_LEN; i++) {
				  g_texts[i] = event.text.text[i];
				  if (event.text.text[i] == '\0') {
					  break;
				  }
			  }
			  break;
		  case SDL_TEXTEDITING:
			  for (int i = 0; i < MAX_TEXTINPUT_CAHR_LEN; i++) {
				  g_texts[i] = event.edit.text[i];
				  if (event.edit.text[i] == '\0') {
					  break;
				  }
			  }
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
	return Load_SDL_Exit(running);
#endif
}

const char* Load_SDL_GetText() {
	return g_texts;
}

void Load_SDL_GL_SwapScreen(){
#ifndef LOON_DESKTOP
	if (display && eglsurface) {
		eglSwapBuffers(display, eglsurface);
	}
#else
  if(window){
	 SDL_GL_SwapWindow(window);
  }
#endif
}

void Load_SDL_GL_SwapWindowHandle(const int64_t handle){
  	SDL_Window* win = (SDL_Window*)handle;
	if (!win) {
		return;
	}
}

void GetDisplayResolution(int displayIndex, int* w, int* h) {
	SDL_DisplayMode mode;
	if (SDL_GetCurrentDisplayMode(displayIndex, &mode) == 0) {
		*w = mode.w;
		*h = mode.h;
	}
	else {
		*w = 1280;
		*h = 720;
	}
}

void Call_SDL_GetRenderScale(float* scales) {
	if (!window || !scales) {
		return;
	}
	int outRenderW, outRenderH;
	int winW, winH;
	SDL_GetWindowSize(window, &winW, &winH);
	SDL_GL_GetDrawableSize(window, &outRenderW, &outRenderH);
	int displayIndex = SDL_GetWindowDisplayIndex(window);
	int screenW, screenH;
	GetDisplayResolution(displayIndex, &screenW, &screenH);
	if (winW > 0 && winH > 0) {
		scales[0] = (float)(outRenderW) / (float)winW;
		scales[1] = (float)(outRenderH) / (float)winH;
	}
	else {
		scales[0] = scales[1] = 1.0f;
	}
}

void GetPlatformDefaultResolution(const char* platform, int* w, int* h) {
	for (size_t i = 0; i < sizeof(platformResTable) / sizeof(platformResTable[0]); i++) {
		if (strstr(platform, platformResTable[i].platform)) {
			*w = platformResTable[i].width;
			*h = platformResTable[i].height;
			return;
		}
	}
	*w = 1280;
	*h = 720;
}

int SelectBestDisplay() {
    int numDisplays = SDL_GetNumVideoDisplays();
    if (numDisplays <= 1) {
        return 0; 
    }
    int bestIndex = 0;
    int bestArea = 0;
    for (int i = 0; i < numDisplays; i++) {
        int w, h;
        GetDisplayResolution(i, &w, &h);
        int area = w * h;
        if (area > bestArea) {
            bestArea = area;
            bestIndex = i;
        }
    }
    return bestIndex;
}

void GetCurrentScreenSize(int* width, int* height) {
	const char* platform = SDL_GetPlatform();
	if (strstr(platform, "Nintendo") || strstr(platform, "Xbox") || strstr(platform, "PlayStation") || strstr(platform, "Steam Deck")) {
		SDL_DisplayMode mode;
		if (SDL_GetCurrentDisplayMode(0, &mode) == 0) {
			*width = mode.w;
			*height = mode.h;
		}
		else {
			GetPlatformDefaultResolution(platform, width, height);
		}
		return;
	}
	int displayIndex = SelectBestDisplay();
	GetDisplayResolution(displayIndex, width, height);
}

void Load_SDL_Current_Screen_Size(int32_t* values) {
	int width, height;
	GetCurrentScreenSize(&width, &height);
	values[0] = width;
	values[1] = height;
}

void Load_SDL_Current_Window_Size(int32_t* values) {
	int winWidth, winHeight;
	SDL_GetWindowSize(window, &winWidth, &winHeight);
	values[0] = winWidth;
	values[1] = winHeight;
}

bool Load_SDL_Pause() {
	return g_isPause;
}

int32_t Load_SDL_GetKeyStates(int32_t* outKey) {
	size_t len = SDL_NUM_SCANCODES;
	copy_int32_array(outKey, len, g_keyStates, len);
	return (int32_t)len;
}

int32_t Load_SDL_GetPressedKeys(int32_t* outKey) {
	size_t len = SDL_NUM_SCANCODES;
	copy_int32_array(outKey, len, g_pressedKeys, len);
	return (int32_t)len;
}

int32_t Load_SDL_GetReleasedKeys(int32_t* outKey) {
	size_t len = SDL_NUM_SCANCODES;
	copy_int32_array(outKey, len, g_releasedKeys, len);
	return (int32_t)len;
}

int32_t Load_SDL_GetLastPressedScancode() {
	return g_lastPressedScancode;
}

int32_t Load_SDL_GetPolleventType() {
	return tempPolleventType;
}

void Load_SDL_GameControllerClose(const int64_t handle) {
	SDL_GameController* controller = (SDL_GameController*)handle;
	if (controller) {
		SDL_GameControllerClose(controller);
	}
}

const char* Load_SDL_GameControllerName(int64_t handle)
{
	SDL_GameController* controller = (SDL_GameController*)handle;
	if (!controller) {
		return NULL;
	}
	return SDL_GameControllerName(controller);
}

const char* Load_SDL_GameControllerPath(const int64_t handle)
{
	SDL_GameController* controller = (SDL_GameController*)handle;
	if (!controller) {
		return NULL;
	}
	return SDL_GameControllerPath(controller);
}

int32_t Load_SDL_GameControllerGetType(const int64_t handle)
{
	SDL_GameController* controller = (SDL_GameController*)handle;
	if (!controller) {
		return 0;
	}
	return SDL_GameControllerGetType(controller);
}

int32_t Load_SDL_GameControllerGetPlayerIndex(const int64_t handle)
{
	SDL_GameController* controller = (SDL_GameController*)handle;
	if (!controller) {
		return 0;
	}
	return SDL_GameControllerGetPlayerIndex(controller);
}

void Load_SDL_GameControllerSetPlayerIndex(const int64_t handle, int32_t joystickIndex)
{
	SDL_GameController* controller = (SDL_GameController*)handle;
	if (!controller) {
		return;
	}
	SDL_GameControllerSetPlayerIndex(controller, joystickIndex);
}

int16_t Load_SDL_GameControllerGetVendor(const int64_t handle)
{
	SDL_GameController* controller = (SDL_GameController*)handle;
	if (!controller) {
		return 0;
	}
	return SDL_GameControllerGetVendor(controller);
}

int32_t Load_SDL_GameControllerGetNumTouchpads(const int64_t handle)
{
	SDL_GameController* controller = (SDL_GameController*)handle;
	if (!controller) {
		return 0;
	}
	return SDL_GameControllerGetNumTouchpads(controller);
}

void FreeTempContext() {
	if (tempContext != NULL) {
		SDL_GL_DeleteContext(tempContext);
		tempContext = NULL;
	}
}

void FreeTempController() {
	if (tempController != NULL) {
		SDL_GameControllerClose(tempController);
		tempController = NULL;
	}
}

bool Load_SDL_Exit(const int run) {
	if(allowExit && run != 0){
	  return true;
	} else {
	  return false;
	}
}

int32_t Load_SDL_TouchData(int32_t* data)
{
	size_t len = sizeof(g_touches);
	memcpy((void*)data, g_touches, len);
	return (int32_t)len;
}

int Load_SDL_GL_SetSwapInterval(int on)
{
	return SDL_GL_SetSwapInterval(on);
}

int64_t Load_SDL_GL_CreateContext(const int64_t window)
{
	return (intptr_t)SDL_GL_CreateContext((SDL_Window*)window);
}

int Load_SDL_GL_SetAttribute(const int attribute, const int value)
{
	return SDL_GL_SetAttribute((SDL_GLattr)attribute, value);
}

void Load_SDL_GetDrawableSize(const int64_t window, int32_t* values)
{
	SDL_Window* win = (SDL_Window*)window;
	if(!win){
		return;
	}
	int w, h;
	SDL_GL_GetDrawableSize(win, &w, &h);
	values[0] = w;
	values[1] = h;
}

bool Load_SDL_PathIsFile(char* path)
{
	SDL_RWops* f = SDL_RWFromFile(path, "r");
	if (f == NULL) { return false; }
	else { SDL_RWclose(f); return true; }
}

void ImportSDLInclude()
{
}

int64_t CreateGameData(char* fileName)
{
	game_filesystem* fs = (game_filesystem*)malloc(sizeof(game_filesystem));
	if (!fs || !fileName) {
		return false;
	}
    chars_append(fs->basepath, MAX_GAMESAVE_PATH_CAHR_LEN - 1, fileName);
	fs->basepath[MAX_GAMESAVE_PATH_CAHR_LEN - 1] = '\0';
	fs->filecount = 0;
	gamefilesys = fs;
	return (intptr_t)fs;
}

int64_t ReadGameData(const int64_t handle, const char* filename, uint8_t* outBytes) {
	game_filesystem* fs = (game_filesystem*)handle;
	if (!fs) {
		return 0;
	}
	if (!fs || !filename) {
		return 0;
	}
	char fullPath[MAX_GAMESAVE_PATH_CAHR_LEN];
	snprintf(fullPath, sizeof(fullPath), "%s/%s", fs->basepath, filename);
	SDL_RWops* rw = SDL_RWFromFile(fullPath, "rb");
	if (!rw) {
		return 0;
	}
	Sint64 size = SDL_RWsize(rw);
	if (size <= 0) {
		SDL_RWclose(rw);
		return 0;
	}
	uint8_t* buffer = (uint8_t*)malloc(size);
	if (!buffer) {
		SDL_RWclose(rw);
		return 0;
	}
	if (SDL_RWread(rw, buffer, 1, size) != (size_t)size) {
		free(buffer);
		SDL_RWclose(rw);
		return 0;
	}
	SDL_RWclose(rw);
	int64_t outSize = (int64_t)size;
	if (buffer) {
		copy_uint8_array(outBytes,(size_t)outSize, buffer, (size_t)outSize);
		free(buffer);
	}
	return outSize;
}

bool WriteGameData(const int64_t handle, const char* filename, const char* data, int64_t size) {
	game_filesystem* fs = (game_filesystem*)handle;
	if (!fs) {
		return false;
	}
	char fullPath[MAX_GAMESAVE_PATH_CAHR_LEN];
	snprintf(fullPath, sizeof(fullPath), "%s/%s", fs->basepath, filename);
	SDL_RWops* rw = SDL_RWFromFile(fullPath, "wb");
	if (!rw) {
		return false;
	}
	if (SDL_RWwrite(rw, data, 1, size) != size) {
		SDL_RWclose(rw);
		return false;
	}
	SDL_RWclose(rw);
	return true;
}

int32_t GetGameDataFileCount(const int64_t handle) {
	game_filesystem* fs = (game_filesystem*)handle;
	if (!fs) {
		return 0;
	}
#if defined(_WIN32)
	WIN32_FIND_DATA findData;
	char searchPath[MAX_GAMESAVE_PATH_CAHR_LEN];
	snprintf(searchPath, sizeof(searchPath), "%s\\*", fs->basepath);
	HANDLE hFind = FindFirstFile((LPCWSTR)searchPath, &findData);
	if (hFind == INVALID_HANDLE_VALUE) return 0;
	do {
		if (!(findData.dwFileAttributes & FILE_ATTRIBUTE_DIRECTORY)) {
			chars_append(fs->files[fs->filecount].name, MAX_GAMESAVE_PATH_CAHR_LEN - 1, (const char*)findData.cFileName);
			fs->files[fs->filecount].size = findData.nFileSizeLow;
			fs->filecount++;
		}
	} while (FindNextFile(hFind, &findData) && fs->filecount < MAX_GAMESAVE_FILE_LIST);
	FindClose(hFind);
#else
	DIR* dir = opendir(fs->basePath);
	if (!dir) return 0;
	struct dirent* entry;
	while ((entry = readdir(dir)) != NULL && fs->fileCount < MAX_GAMESAVE_FILE_LIST) {
		if (entry->d_type == DT_REG) {
			chars_append(fs->files[fs->fileCount].name, MAX_GAMESAVE_PATH_CAHR_LEN - 1, entry->d_name);
			char fullPath[MAX_GAMESAVE_PATH_CAHR_LEN];
			snprintf(fullPath, sizeof(fullPath), "%s/%s", fs->basePath, entry->d_name);
			FILE* f = fopen(fullPath, "rb");
			if (f) {
				fseek(f, 0, SEEK_END);
				fs->files[fs->fileCount].size = ftell(f);
				fclose(f);
			}
			fs->fileCount++;
		}
	}
	closedir(dir);
#endif
	return fs->filecount;
}

void FreeGameData(const int64_t handle) {
	game_filesystem* fs = (game_filesystem*)handle;
	if (!fs) {
		return;
	}
	free(fs);
}

char* GetSystemProperty(const char* key)
{
	if (strcmp(key, "os.sys") == 0) {
		char* platform = detectPlatformCompileString();
		if (!platform) {
			platform = detectPlatformRuntimeString();
		}
		return platform;
	}
	if (strcmp(key, "os.name") == 0)
		return getOSVersionString();
	if (strcmp(key, "os.arch") == 0)
		return getArchitectureString();
	if (strcmp(key, "os.virt") == 0)
		return detectVirtualizationString();
	if (strcmp(key, "os.gpu") == 0)
		return getGpuInfoString();
	if (strcmp(key, "os.gpu.cores") == 0) 	
		return ints_varargs_to_string("",1,getCpuCores());
	if (strcmp(key, "os.memory") == 0) {
		int64_t totalRAM = 0, freeRAM = 0;
		getMemoryInfoInts(&totalRAM, &freeRAM);
		return ints_varargs_to_string(",", 2, totalRAM, freeRAM);
	}
	if (strcmp(key, "line.separator") == 0)
		return get_newline_separator();
	if (strcmp(key, "file.separator") == 0)
		return get_path_separator();
	if (strcmp(key, "java.io.tmpdir") == 0)
		return get_temp_folder();
	if (strcmp(key, "user.home") == 0)
		return get_home_folder();
	if (strcmp(key, "user.name") == 0)
		return get_system_username();
	return "";
}

const char* Load_SDL_GetPreferredLocales()
{
	SDL_Locale* locales = SDL_GetPreferredLocales();
	if (!locales) {
		return 0;
	}
	const char* langList = joinLocales(locales);
	return langList;
}

const char* Load_SDL_GetBasePath()
{
	return SDL_GetBasePath();
}

const char* Load_SDL_GetPrefPath(const char* org, const char* app)
{
	return SDL_GetPrefPath(org,app);
}

const char* Load_SDL_GetPlatform()
{
	return SDL_GetPlatform();
}

bool Load_SDL_IsGameController(int32_t joystickIndex)
{
	return SDL_IsGameController(joystickIndex);
}

int64_t Load_SDL_GameControllerTemp() {
	if (tempController) {
		return (intptr_t)tempController;
	}
	return 0;
}

int64_t Load_SDL_GameControllerOpen(int32_t joystickIndex)
{
	SDL_GameController* controller = SDL_GameControllerOpen(joystickIndex);
	if (!controller) {
		return 0;
	}
	tempController = controller;
	return (intptr_t)controller;
}

const char* Load_SDL_GameControllerNameForIndex(int32_t joystickIndex)
{
	return SDL_GameControllerNameForIndex(joystickIndex);
}

const char* Load_SDL_GameControllerPathForIndex(int32_t joystickIndex)
{
	return SDL_GameControllerPathForIndex(joystickIndex);
}

const char* Load_SDL_GameControllerMappingForIndex(int32_t mappingIndex)
{
	return SDL_GameControllerMappingForIndex(mappingIndex);
}

const char* Load_SDL_GameControllerMappingForDeviceIndex(int32_t joystickIndex)
{
	return SDL_GameControllerMappingForDeviceIndex(joystickIndex);
}

int32_t Load_SDL_GameControllerEventState(int32_t state)
{
	return SDL_GameControllerEventState(state);
}

int32_t Load_SDL_GameControllerAddMapping(const char* mappingString)
{
	return SDL_GameControllerAddMapping(mappingString);
}

int32_t Load_SDL_GameControllerGetAxisFromString(const char* axisString)
{
	return SDL_GameControllerGetAxisFromString(axisString);
}

int32_t Load_SDL_GameControllerGetButtonFromString(const char* btnString)
{
	return SDL_GameControllerGetButtonFromString(btnString);
}

int32_t Load_SDL_GameControllerNumMappings()
{
	return SDL_GameControllerNumMappings();
}

void Load_SDL_GameControllerUpdate()
{
	SDL_GameControllerUpdate();
}

int64_t Load_SDL_JoystickOpen(int32_t deviceIndex)
{
	SDL_Joystick* joystick = SDL_JoystickOpen(deviceIndex);
	if (!joystick) {
		return 0;
	}
	return (intptr_t)joystick;
}

void Load_SDL_JoystickClose(const int64_t handle)
{
	SDL_Joystick* joystick = (SDL_Joystick*)handle;
	if (joystick) {
		SDL_JoystickClose(joystick);
	}
}

int64_t Load_SDL_GameControllerGetJoystick(const int64_t handle)
{
	SDL_GameController* controller = (SDL_GameController*)handle;
	if (controller) {
		SDL_Joystick* joystick = SDL_GameControllerGetJoystick(controller);
		if (joystick) {
			return (intptr_t)joystick;
		}
	}
	return 0;
}

const char* Load_SDL_JoystickGetGUIDString(const int64_t handle)
{
	SDL_Joystick* joystick = (SDL_Joystick*)handle;
	if (joystick) {
		SDL_JoystickGUID guid = SDL_JoystickGetGUID(joystick);
		static char guids[33];
		SDL_JoystickGetGUIDString(guid, guids, sizeof(guids));
		return guids;
	}
	return NULL;
}

int32_t Load_SDL_JoystickNumAxes(const int64_t handle)
{
	SDL_Joystick* joystick = (SDL_Joystick*)handle;
	if (!joystick) {
		return 0;
	}
	return SDL_JoystickNumAxes(joystick);
}

int32_t Load_SDL_JoystickNumBalls(const int64_t handle)
{
	SDL_Joystick* joystick = (SDL_Joystick*)handle;
	if (!joystick) {
		return 0;
	}
	return SDL_JoystickNumBalls(joystick);
}

int32_t Load_SDL_JoystickNumHats(const int64_t handle)
{
	SDL_Joystick* joystick = (SDL_Joystick*)handle;
	if (!joystick) {
		return 0;
	}
	return SDL_JoystickNumHats(joystick);
}

int32_t Load_SDL_JoystickNumButtons(const int64_t handle)
{
	SDL_Joystick* joystick = (SDL_Joystick*)handle;
	if (!joystick) {
		return 0;
	}
	return SDL_JoystickNumButtons(joystick);
}

void Load_SDL_LockJoysticks()
{
	SDL_LockJoysticks();
}

void Load_SDL_UnlockJoysticks()
{
	SDL_UnlockJoysticks();
}

int32_t Load_SDL_NumJoysticks()
{
	return SDL_NumJoysticks();
}

int16_t Load_SDL_JoystickGetDeviceVendor(int32_t deviceIndex)
{
	return SDL_JoystickGetDeviceVendor(deviceIndex);
}

int16_t Load_SDL_JoystickGetDeviceProduct(int32_t deviceIndex)
{
	return SDL_JoystickGetDeviceProduct(deviceIndex);
}

int16_t Load_SDL_JoystickGetDeviceProductVersion(int32_t deviceIndex)
{
	return SDL_JoystickGetDeviceProductVersion(deviceIndex);
}

int32_t Load_SDL_JoystickDetachVirtual(int32_t deviceIndex)
{
	return SDL_JoystickDetachVirtual(deviceIndex);
}

bool Load_SDL_JoystickIsVirtual(int32_t deviceIndex)
{
	return SDL_JoystickIsVirtual(deviceIndex);
}

const char* Load_SDL_JoystickNameForIndex(int32_t deviceIndex)
{
	return SDL_JoystickNameForIndex(deviceIndex);
}

const char* Load_SDL_JoystickPathForIndex(int32_t deviceIndex)
{
	return SDL_JoystickPathForIndex(deviceIndex);
}

int32_t Load_SDL_JoystickGetDevicePlayerIndex(int32_t deviceIndex)
{
	return SDL_JoystickGetDevicePlayerIndex(deviceIndex);
}

int64_t Load_SDL_SensorOpen(int32_t deviceIndex)
{
	SDL_Sensor* sensor = SDL_SensorOpen(deviceIndex);
	if (!sensor) {
		return 0;
	}
	return (intptr_t)sensor;
}

void Load_SDL_SensorClose(const int64_t handle) {
	SDL_Sensor* sensor = (SDL_Sensor*)handle;
	if (!sensor) {
		return;
	}
	SDL_SensorClose(sensor);
}

const char* Load_SDL_SensorGetName(const int64_t handle)
{
	SDL_Sensor* sensor = (SDL_Sensor*)handle;
	if (!sensor) {
		return "";
	}
	return SDL_SensorGetName(sensor);
}

int32_t Load_SDL_SensorGetType(const int64_t handle)
{
	SDL_Sensor* sensor = (SDL_Sensor*)handle;
	if (!sensor) {
		return 0;
	}
	return SDL_SensorGetType(sensor);
}

int32_t Load_SDL_SensorGetNonPortableType(const int64_t handle)
{
	SDL_Sensor* sensor = (SDL_Sensor*)handle;
	if (!sensor) {
		return 0;
	}
	return SDL_SensorGetNonPortableType(sensor);
}

int32_t Load_SDL_SensorGetData(const int64_t handle, float* data, int32_t numValues)
{
	SDL_Sensor* sensor = (SDL_Sensor*)handle;
	if (!sensor) {
		return 0;
	}
	return SDL_SensorGetData(sensor, data, numValues);
}

void Load_SDL_LockSensors()
{
	SDL_LockSensors();
}

void Load_SDL_UnlockSensors()
{
	SDL_UnlockSensors();
}

int32_t Load_SDL_NumSensors()
{
	return SDL_NumSensors();
}

const char* Load_SDL_SensorGetDeviceName(int32_t deviceIndex)
{
	return SDL_SensorGetDeviceName(deviceIndex);
}

int32_t Load_SDL_SensorGetDeviceType(int32_t deviceIndex)
{
	return SDL_SensorGetDeviceType(deviceIndex);
}

int32_t Load_SDL_SensorGetDeviceNonPortableType(int32_t deviceIndex)
{
	return SDL_SensorGetDeviceNonPortableType(deviceIndex);
}

void Load_SDL_SensorUpdate()
{
	SDL_SensorUpdate();
}

int32_t Load_SDL_GameControllerTypeForIndex(int32_t joystickIndex)
{
	return SDL_GameControllerTypeForIndex(joystickIndex);
}

int32_t Load_SDL_GetTicks()
{
	return SDL_GetTicks();
}

int64_t Load_SDL_GetTicks64()
{
	return SDL_GetTicks64();
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

int32_t Load_Axes(const int controller, float* axes)
{
#ifdef __SWITCH__
    const PadState &pad = controller == -1 ? combinedPad : pads[controller];
	HidAnalogStickState stickLeft = padGetStickPos(&pad, 0);
	HidAnalogStickState stickRight = padGetStickPos(&pad, 1);
	axes[0] = (float)stickLeft.x / JOYSTICK_MAX;
	axes[1] = (float)stickLeft.y / JOYSTICK_MAX;
	axes[2] = (float)stickRight.x / JOYSTICK_MAX;
	axes[3] = (float)stickRight.y / JOYSTICK_MAX;
    remapPadAxes(axes, padGetStyleSet(&pad));
	axes[1] *= -1;
	axes[3] *= -1;
#else
    memcpy(axes, joysticks, sizeof(joysticks));
#endif
	return sizeof(axes);
}

void Load_SDL_GetWindowSize(const int64_t window,int32_t* values)
{
	SDL_Window* win = (SDL_Window*)window;
	if (!win) {
		return;
	}
	int width = 0;
	int height = 0;
	SDL_GetWindowSize(win, &width, &height);
	values[0] = width;
	values[1] = height;
}

int Load_SDL_LockSurface(const int64_t handle)
{
	cache_surface* surface = (cache_surface*)handle;
	if (!surface) {
		return -1;
	}
	return SDL_LockSurface(surface->surface_data);
}

void Load_SDL_UnlockSurface(const int64_t handle)
{
	cache_surface* surface = (cache_surface*)handle;
	if (!surface) {
		return;
	}
	 SDL_UnlockSurface(surface->surface_data);
}

void Load_SDL_Delay(const int32_t d)
{
	SDL_Delay(d);
}

int64_t Load_SDL_CreateRGBSurface(const int32_t flags, const int width, const int height, const int depth, const int32_t rmask, const int32_t gmask, const int32_t bmask, const int32_t amask)
{
	cache_surface* newsurface = (cache_surface*)malloc(sizeof(cache_surface));
	if (!newsurface) {
		return 0;
	}
	SDL_Surface* newImage = SDL_CreateRGBSurface(flags, width, height, depth, rmask, gmask, bmask, amask);
	newsurface->surface_data = newImage;
	newsurface->width = newImage->w;
	newsurface->height = newImage->h;
	_temp_surface = newsurface;
	return (intptr_t)newsurface;
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
	cache_surface* newsurface = (cache_surface*)malloc(sizeof(cache_surface));
	if (!newsurface) {
		return 0;
	}
	SDL_Surface* newImage = SDL_CreateRGBSurfaceFrom((void*)pixels, w, h, depth, pitch,
		rmask, gmask, bmask, amask);
	newsurface->surface_data = newImage;
	newsurface->width = newImage->w;
	newsurface->height = newImage->h;
	_temp_surface = newsurface;
	return (intptr_t)newsurface;
}

int64_t Load_SDL_ConvertSurfaceFormat(const int64_t handle, int32_t pixel_format, int32_t flags)
{
	cache_surface* surface = (cache_surface*)handle;
	if (!surface) return -1;
	cache_surface* newsurface = (cache_surface*)malloc(sizeof(cache_surface));
	if (!newsurface) {
		return 0;
	}
	SDL_Surface* newImage = SDL_ConvertSurfaceFormat(surface->surface_data, (Uint32)pixel_format, (Uint32)flags);
	newsurface->surface_data = newImage;
	newsurface->width = newImage->w;
	newsurface->height = newImage->h;
	_temp_surface = newsurface;
	return (intptr_t)newsurface;
}

void Load_SDL_GetSurfaceSize(const int64_t handle,int32_t* values)
{
	cache_surface* surface = (cache_surface*)handle;
	if (!surface) return;
	values[0] = surface->surface_data->w;
	values[1] = surface->surface_data->h;
}

 void Load_SDL_GetSurfacePixels32(const int64_t handle, int order, int32_t* pixels)
{
	cache_surface* surface = (cache_surface*)handle;
	if (!surface) return;
	int width = surface->surface_data->w;
	int height = surface->surface_data->h;
	if (!pixels) {
		return;
	}
	Uint8* srcPixels = (Uint8*)surface->surface_data->pixels;
	SDL_Palette* palette = surface->surface_data->format->palette;
	for (int y = 0; y < height; y++) {
		Uint8* row = srcPixels + y * surface->surface_data->pitch;
		for (int x = 0; x < width; x++) {
			Uint8 index = row[x];
			SDL_Color color = palette->colors[index];
			if (order == 0) {
				pixels[y * width + x] =  (color.a << 24) | (color.r << 16) | (color.g << 8) | color.b;
			}
			else {
				pixels[y * width + x] =  (color.r << 24) | (color.g << 16) | (color.b << 8) | color.a;
			}
		}
	}

}

void Load_SDL_SetPixel(const int64_t handle, const int x, const int y, const int32_t pixel)
{
	cache_surface* surface = (cache_surface*)handle;
	if (!surface) return;
	if (x < 0 || y < 0 || x >= surface->surface_data->w || y >= surface->surface_data->h) {
		return;
	}
	Uint8* target_pixel = (Uint8*)surface->surface_data->pixels + y * surface->surface_data->pitch + x * surface->surface_data->format->BytesPerPixel;
	switch (surface->surface_data->format->BytesPerPixel) {
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

void Load_SDL_SetPixel32(const int64_t handle, const int x, const int y, const int32_t pixel)
{
	cache_surface* surface = (cache_surface*)handle;
	if (!surface) return;
	Uint32* const target_pixel = (Uint32*)((Uint8*)surface->surface_data->pixels
		+ y * surface->surface_data->pitch
		+ x * surface->surface_data->format->BytesPerPixel);
	*target_pixel = (Uint32)pixel;
}

void Load_SDL_SetPixels32(const int64_t handle, int nx, int ny, int nw, int nh, int32_t* pixels)
{
	cache_surface* surface = (cache_surface*)handle;
	if (!surface || !pixels) return;
	Uint8* dst = (Uint8*)surface->surface_data->pixels;
	int pitch = surface->surface_data->pitch;
	for (int y = ny; y < nh; y++) {
		Uint32* pixel = (Uint32*)(dst + y * pitch);
		for (int x = nx; x < nw; x++) {
			pixel[x] = (Uint32)pixels[y * nw + x];
		}
	}
}

int64_t Load_SDL_LoadBMPHandle(const char* path)
{
	SDL_Surface* image = SDL_LoadBMP(path);
	if (!image) {
		return 0;
	}
	cache_surface* sdlsurface = (cache_surface*)malloc(sizeof(cache_surface));
	if (!sdlsurface) {
		return 0;
	}
	sdlsurface->surface_data = image;
	sdlsurface->width = image->w;
	sdlsurface->height = image->h;
	_temp_surface = sdlsurface;
	return (intptr_t)sdlsurface;
}

bool Load_SDL_MUSTLockSurface(const int64_t handle)
{
	cache_surface* surface = (cache_surface*)handle;
	if (!surface) return false;
	return SDL_MUSTLOCK(surface->surface_data);
}

static const int blendModeToInt(SDL_BlendMode mode) {
	switch (mode) {
	case SDL_BLENDMODE_NONE:  return 0;
	case SDL_BLENDMODE_BLEND: return 1;
	case SDL_BLENDMODE_ADD:   return 2;
	case SDL_BLENDMODE_MOD:   return 3;
	case SDL_BLENDMODE_MUL:   return 4;
	case SDL_BLENDMODE_INVALID: return 5;
	default:                  return -1;
	}
}

static const SDL_BlendMode blendIntToMode(int mode) {
	switch (mode) {
	case 0: return SDL_BLENDMODE_NONE;
	case 1: return SDL_BLENDMODE_BLEND;
	case 2: return SDL_BLENDMODE_ADD;
	case 3: return SDL_BLENDMODE_MOD;
	case 4: return SDL_BLENDMODE_MUL;
	case 5: return SDL_BLENDMODE_INVALID;
	default: return SDL_BLENDMODE_NONE;
	}
}

void Load_SDL_SetSurfaceBlendMode(const int64_t handle, const int mode)
{
	cache_surface* surface = (cache_surface*)handle;
	if (!surface) return;
	SDL_SetSurfaceBlendMode(surface->surface_data, blendIntToMode(mode));
}

int Load_SDL_GetSurfaceBlendMode(const int64_t handle)
{
	cache_surface* surface = (cache_surface*)handle;
	if (!surface) return -1;
	SDL_BlendMode mode;
	SDL_GetSurfaceBlendMode(surface->surface_data, &mode);
	return blendModeToInt(mode);
}

void Load_SDL_FillRect(const int64_t handle, const int x, const int y, const int w, const int h, const int r, const int g, const int b, const int a)
{
	cache_surface* surface = (cache_surface*)handle;
	if (!surface) return;
	Uint32 color = SDL_MapRGBA(surface->surface_data->format, r, g, b,a);
	SDL_Rect rect = { x, y, w, h };
	SDL_FillRect(surface->surface_data, &rect, color);
}

void Load_SDL_SetClipRect(const int64_t handle, const int x, const int y, const int w, const int h)
{
	cache_surface* surface = (cache_surface*)handle;
	if (!surface) return;
	SDL_Rect clipRect = { x, y, w, h };
	SDL_SetClipRect(surface->surface_data, &clipRect);
}

void Load_SDL_GetClipRect(const int64_t handle,int32_t* values)
{
	cache_surface* surface = (cache_surface*)handle;
	if (!surface) return;
	SDL_Rect currentClip;
	SDL_GetClipRect(surface->surface_data, &currentClip);
	values[0] = currentClip.x;
	values[1] = currentClip.y;
	values[2] = currentClip.w;
	values[3] = currentClip.h;
}

int32_t Load_SDL_GetFormat(const int64_t handle) {
	cache_surface* surface = (cache_surface*)handle;
	if (!surface) {
		return 0;
	}
	return (int32_t)surface->surface_data->format->format;
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

int Load_SDL_GetNumTouchDevices() {
	return SDL_GetNumTouchDevices();
}

const char* Load_SDL_GetError()
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

bool Load_SDL_HasClipboardText()
{
	return SDL_HasClipboardText();
}

int32_t Load_SDL_SetPrimarySelectionText(const char* text)
{
	return SDL_SetPrimarySelectionText(text);
}

const char* Load_SDL_GetPrimarySelectionText()
{
	return SDL_GetPrimarySelectionText();
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

void Load_SDL_GetDisplayUsableBounds(const int display, int32_t* xywh)
{
	SDL_Rect bounds = { 0,0,0,0 };
	int result = SDL_GetDisplayUsableBounds(display, &bounds);

	xywh[0] = bounds.x;
	xywh[1] = bounds.y;
	xywh[2] = bounds.w;
	xywh[3] = bounds.h;

}

void Load_SDL_GetDisplayBounds(const int display, int32_t* xywh)
{
	SDL_Rect bounds = { 0,0,0,0 };
	int result = SDL_GetDisplayBounds(display, &bounds);

	xywh[0] = bounds.x;
	xywh[1] = bounds.y;
	xywh[2] = bounds.w;
	xywh[3] = bounds.h;
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

int64_t Load_SDL_CreateColorCursor(const int64_t handle, const int hotx, const int hoty)
{
	cache_surface* surface = (cache_surface*)handle;
	if (!surface) {
		return -1;
	}
	return (intptr_t)SDL_CreateColorCursor(surface->surface_data, hotx, hoty);
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
	cache_surface* surface = (cache_surface*)handle;
	if (!surface) {
		return;
	}
	SDL_FreeSurface(surface->surface_data);
	free(surface);
}

void Load_SDL_FreeTempSurface()
{
	if (!_temp_surface) {
		return;
	}
	SDL_FreeSurface(_temp_surface->surface_data);
	free(_temp_surface);
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

void Load_SDL_SetWindowIcon(const int64_t handle, const int64_t surfaceHandle)
{
	cache_surface* surface = (cache_surface*)surfaceHandle;
	if (!surface) {
		return;
	}
	SDL_SetWindowIcon((SDL_Window*)handle, surface->surface_data);
}

void Load_SDL_DestroyWindow(const int64_t handle)
{
	SDL_DestroyWindow((SDL_Window*)handle);
}

void Call_SDL_GetDrawableSize(int* values)
{
	if (!window) {
		return;
	}
	int w, h;
	SDL_GL_GetDrawableSize(window, &w, &h);
	values[0] = w;
	values[1] = h;
}

void Call_SDL_GetWindowSize(int* values)
{
	if (!window) {
		return;
	}
	int width = 0;
	int height = 0;
	SDL_GetWindowSize(window, &width, &height);
	values[0] = width;
	values[1] = height;
}

void Call_SDL_MaximizeWindow()
{
	if (!window) {
		return;
	}
	SDL_MaximizeWindow(window);
}

void Call_SDL_MinimizeWindow()
{
	if (!window) {
		return;
	}
	SDL_MinimizeWindow(window);
}

int Call_SDL_SetWindowFullscreen(const int flags)
{
	if (!window) {
		return 0;
	}
	return SDL_SetWindowFullscreen(window, flags);
}

void Call_SDL_SetWindowBordered(const bool bordered)
{
	if (!window) {
		return;
	}
	SDL_SetWindowBordered(window, (SDL_bool)bordered);
}

void Call_SDL_SetWindowSize(const int w, const int h)
{
	if (!window) {
		return;
	}
	SDL_SetWindowSize(window, w,h);
}

void Call_SDL_SetWindowPosition(const int x, const int y)
{
	if (!window) {
		return;
	}
	SDL_SetWindowPosition(window, x, y);
}

int Call_SDL_GetWindowDisplayIndex()
{
	if (!window) {
		return 0;
	}
	return SDL_GetWindowDisplayIndex(window);
}

int Call_SDL_GetWindowFlags()
{
	if (!window) {
		return 0;
	}
	return SDL_GetWindowFlags(window);
}

void Call_SDL_SetWindowTitle(const char* title)
{
	if (!window) {
		return;
	}
	SDL_SetWindowTitle(window, title);
}

void Call_SDL_RestoreWindow()
{
	if (!window) {
		return;
	}
	SDL_RestoreWindow(window);
}

void Call_SDL_SetWindowIcon(const int64_t hanlde)
{
	if (!window) {
		return;
	}
	cache_surface* surface = (cache_surface*)hanlde;
	if (!surface) {
		fprintf(stderr, "surface error !\n");
		return;
	}
	if (!surface->surface_data) {
		fprintf(stderr, "surface->surface_data error !\n");
		return;
	}
	SDL_SetWindowIcon(window, surface->surface_data);
}

void Call_SDL_GL_SwapWindow()
{
	if (!window) {
		return;
	}
	SDL_GL_SwapWindow(window);
}

int64_t Call_SDL_GL_CreateContext()
{
	if (!window) {
		return 0;
	}
	return (intptr_t)SDL_GL_CreateContext(window);
}

void Call_SDL_DestroyWindow() {
	if (window) {
		SDL_DestroyWindow(window);
		window = NULL;
	}
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
			if (e.window.event == SDL_WINDOWEVENT_FOCUS_LOST || e.window.event == SDL_WINDOWEVENT_MINIMIZED) {
				g_isPause = true;
			}
			else if (e.window.event == SDL_WINDOWEVENT_FOCUS_GAINED || e.window.event == SDL_WINDOWEVENT_RESTORED) {
				g_isPause = false;
			}
			if (e.window.event == SDL_WINDOWEVENT_RESIZED ||
				e.window.event == SDL_WINDOWEVENT_SIZE_CHANGED) {
				int winWidth = 0, winHeight = 0;
				SDL_GetWindowSize(window, &winWidth, &winHeight);
				g_windowSize[0] = winWidth;
				g_windowSize[1] = winHeight;
			}
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
			data[4] = (char)e.key.keysym.scancode;
			data[5] = (char)e.key.keysym.mod;
			data[6] = e.key.timestamp;
			break;
		case SDL_TEXTINPUT:
			data[0] = 6;
			for (int i = 0; i < MAX_TEXTINPUT_CAHR_LEN; i++) {
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
			for (int i = 0; i < MAX_TEXTINPUT_CAHR_LEN; i++) {
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

char* Load_SDL_GetCompiledVersion(char* data)
{
	SDL_version compiled = { 0,0,0 };
	SDL_VERSION(&compiled);
	data[0] = compiled.major;
	data[1] = compiled.minor;
	data[2] = compiled.patch;
	return data;
}

char* Load_SDL_GetVersion(char* data)
{
	SDL_version compiled = { 0,0,0 };
	SDL_GetVersion(&compiled);
	data[0] = compiled.major;
	data[1] = compiled.minor;
	data[2] = compiled.patch;
	return data;
}

int64_t Load_SDL_Mix_LoadMUSFromMem(void* musData)
{
	if (!musData) return 0;
	SDL_RWops* buffer = SDL_RWFromMem(musData, sizeof(musData));
	Mix_Music* mix = Mix_LoadMUS_RW(buffer, true);
	if (!mix) {
		return 0;
	}
	game_music* music = (game_music*)malloc(sizeof(game_music));
	if (!music) return 0;
	music->handle = mix;
	if (!music->handle) {
		fprintf(stderr, "Mix_LoadMUS_RW Error: %s\n", Mix_GetError());
		free(music);
		return 0;
	}
	music->loopCount = 0;
	return (intptr_t)music;
}

int64_t Load_SDL_Mix_LoadMUS(const char* filename)
{
	if (!filename) return 0;
	game_music* music = (game_music*)malloc(sizeof(game_music));
	if (!music) return 0;
	music->handle = Mix_LoadMUS(filename);
	if (!music->handle) {
		fprintf(stderr, "Mix_LoadMUS Error: %s\n", Mix_GetError());
		free(music);
		return 0;
	}
	music->loopCount = 0;
	return (intptr_t)music;
}

void Load_SDL_Mix_PlayMusic(const int64_t handle, const bool looping)
{
	game_music* music = (game_music*)handle;
	if (!music) {
		return;
	}
	int loopCount = looping ? -1 : 0;
	Mix_PlayMusic(music->handle, loopCount);
	music->loopCount = loopCount;
	g_currentMusic = music;
	g_loopCount = loopCount;
	g_isLooping = (loopCount < 0 || loopCount > 0);
}

bool Load_SDL_Mix_IsLoopingMusic() {
	return g_isLooping;
}

void Load_SDL_Mix_PlayFadeInMusic(const int64_t handle, const bool looping)
{
	game_music* music = (game_music*)handle;
	if (!music) {
		return;
	}
	Mix_FadeInMusic(music->handle, looping ? -1 : 0, fade_time);
}

float Load_SDL_Mix_GetMusicPosition(const int64_t handle)
{
	game_music* music = (game_music*)handle;
	if (!music) {
		return -1.0;
	}
	#if SDL_MIXER_VERSION_ATLEAST(2,6,0)
		if (music && music->handle) {
			double pos = Mix_GetMusicPosition(music->handle);
			if (pos >= 0) return pos;
		}
	#endif
		return -1.0; 
}

bool Load_SDL_Mix_PlayingMusic() {
	return Mix_PlayingMusic() != 0;
}

void Load_SDL_Mix_PlayMusicFadeStop()
{
	Mix_FadeOutMusic(fade_time);
}

void Load_SDL_MIX_SetMusicPosition(const float position)
{
	Mix_RewindMusic();
	Mix_SetMusicPosition(position);
}

void Load_SDL_Mix_SetMusicVolume(const float volume)
{
	Mix_VolumeMusic((int)(volume * MIX_MAX_VOLUME));
}

float Load_SDL_Mix_GetMusicVolume()
{
	return (float)(Mix_VolumeMusic(-1) * MIX_MAX_VOLUME);
}

void Load_SDL_Mix_PauseMusic()
{
	Mix_PauseMusic();
}

void Load_SDL_Mix_ResumeMusic()
{
	Mix_ResumeMusic();
}

void Load_SDL_Mix_HaltMusic()
{
	Mix_HaltMusic();
	g_isLooping = false;
	g_currentMusic = NULL;
}

void Load_SDL_Mix_DisposeMusic(const int64_t handle)
{
	game_music* music = (game_music*)handle;
	if (!music) {
		return;
	}
	Mix_FreeMusic(music->handle);
	free(music);
}

int64_t Load_SDL_Mix_LoadSound(const char* filename)
{
	Mix_Chunk* sound = Mix_LoadWAV(filename);
	if (!sound) {
		return -1;
	}
	return (intptr_t)sound;
}

int64_t Load_SDL_Mix_LoadSoundFromMem(void* wavData)
{
	SDL_RWops* buffer = SDL_RWFromMem(wavData, sizeof(wavData));
	Mix_Chunk* sound = Mix_LoadWAV_RW(buffer, true);
	if (!sound) {
		return -1;
	}
	return (intptr_t)sound;
}

int Load_SDL_Mix_PlaySound(const int64_t handle, const bool looping)
{
	Mix_Chunk* sound = (Mix_Chunk*)handle;
	if (!sound) {
		return -1;
	}
	return Mix_PlayChannel(-1, sound, looping ? -1 : 0);
}

bool Load_SDL_Mix_IsLoopingSound(const int32_t channel)
{
	Mix_Chunk* chunk = Mix_GetChunk(channel);
	return (chunk && chunk->alen > 0) ? true : false;
}

int Load_SDL_Mix_SetPlaySoundLooping(const int64_t handle, const int channel, const bool looping)
{
	Mix_Chunk* sound = (Mix_Chunk*)handle;
	if (!sound) {
		return -1;
	}
	return Mix_PlayChannel(channel, sound, looping ? -1 : 0);
}

void Load_SDL_Mix_SetPosition(const int32_t channel, const int32_t angle, const int32_t distance)
{
	Mix_SetPosition(channel, (Sint16)angle, (Uint8)distance);
}

void Load_SDL_Mix_FadeInChannel(const int32_t channel, const int32_t ms)
{
	Mix_FadeInChannel(channel, NULL, 0, ms);
}

void Load_SDL_Mix_FadeOutChannel(const int32_t channel, const int32_t ms)
{
	Mix_FadeOutChannel(channel, ms);
}

bool Load_SDL_Mix_Playing(const int32_t channel)
{
	return Mix_Playing(channel) ? true : false;
}

void Load_SDL_Mix_PauseSound(const int channel)
{
	Mix_Pause(channel);
}

void Load_SDL_Mix_ResumeSound(const int channel)
{
	Mix_Resume(channel);
}

int Load_SDL_Mix_SetVolume(const int channel, const float volume)
{
	return Mix_Volume(channel, (int)(volume * MIX_MAX_VOLUME));
}

int Load_SDL_Mix_GetVolume(const int channel)
{
	return (int32_t)((float)Mix_Volume(channel, -1) * MIX_MAX_VOLUME);
}

int Load_SDL_Mix_SetPan(const int channel, const float pan)
{
	uint8_t left, right;
	if (pan <= 0) {
		left = 255;
		right = (uint8_t)((1 + pan) * 255);
	}
	else {
		left = (uint8_t)((1 - pan) * 255);
		right = 255;
	}
	return Mix_SetPanning(channel, left, right);
}

int Load_SDL_Mix_HaltSound(const int channel)
{
	return Mix_HaltChannel(channel);
}

void Load_SDL_Mix_DisposeSound(const int64_t handle)
{
	Mix_Chunk* sound = (Mix_Chunk*)handle;
	if (!sound) {
		return;
	}
	Mix_FreeChunk(sound);
}

void Load_SDL_Mix_CloseAudio()
{
	Mix_CloseAudio();
}

void Load_SDL_Quit()
{
	FreeTempController();
	FreeTempContext();
	Mix_HookMusicFinished(NULL);
	Mix_ChannelFinished(NULL);
    Mix_HaltMusic();
    Mix_HaltChannel(-1);
	Mix_CloseAudio();
	SDL_FreeTouchIdMap();
	Call_SDL_DestroyWindow();
    int numJoysticks = SDL_NumJoysticks();
    for (int i = 0; i < numJoysticks; i++) {
        SDL_Joystick* joy = SDL_JoystickOpen(i);
        if (joy) SDL_JoystickClose(joy);
    }
    for (int i = 0; i < numJoysticks; i++) {
        SDL_GameController* ctrl = SDL_GameControllerOpen(i);
        if (ctrl) SDL_GameControllerClose(ctrl);
    }
    int numSensors = SDL_NumSensors();
    for (int i = 0; i < numSensors; i++) {
        SDL_Sensor* sensor = SDL_SensorOpen(i);
        if (sensor) SDL_SensorClose(sensor);
    }
	Mix_Quit();
    SDL_Quit();
	SDL_platform_free_quit();
}

bool Load_SDL_QuitRequested()
{
	SDL_PumpEvents();
	int eventCount = SDL_PeepEvents(NULL, 0, SDL_PEEKEVENT, SDL_QUIT, SDL_QUIT);
	return eventCount > 0;
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

void Load_GL_CompressedTexImage2DOffset(const int target, const int level, const int internalformat, const int width, const int height, const int border, const int imageSize, const int64_t offset)
{
	intptr_t indices = (intptr_t)offset;
	glCompressedTexImage2D(target, level, internalformat, width, height, border, imageSize, (void*)indices);
}

void Load_GL_CompressedTexSubImage2D(const int target, const int level, const int xoffset, const int yoffset, const int width, const int height, const int format, const int imageSize, void* data)
{
	glCompressedTexSubImage2D(target, level, xoffset, yoffset, width, height, format, imageSize, data);
}

void Load_GL_CompressedTexSubImage2DOffset(const int target, const int level, const int xoffset, const int yoffset, const int width, const int height, const int format, const int imageSize, const int64_t offset)
{
	intptr_t indices = (intptr_t)offset;
	glCompressedTexSubImage2D(target, level, xoffset, yoffset, width, height, format, imageSize, (void*)indices);
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

void Load_GL_DeleteTextures(const int n,const void* textures)
{
	glDeleteTextures(n, (GLuint*)textures);
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
	glDrawElements(mode, (GLsizei)count, (GLenum)type, indices);
}

void Load_GL_DrawElementsOffset(const int mode, const int count, const int type, const int64_t offset)
{
	intptr_t indices = (intptr_t)offset;
	glDrawElements(mode, count, type, (void*)indices);
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

void Load_GL_GenTextures(const int n,const void* textures)
{
	glGenTextures(n, (GLuint*)textures);
}

int Load_GL_GetError()
{
	return glGetError();
}

void Load_GL_GetIntegerv(const int pname, const void* params)
{
	glGetIntegerv(pname, (GLint*)params);
}

char* Load_GL_GetString(const int name)
{
	return (char*)glGetString((GLenum)name);
}

void Load_GL_Hint(const int target, const int mode)
{
	glHint((GLenum)target, (GLenum)mode);
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

void Load_GL_ReadPixels(const int x, const int y, const int width, const int height, const int format, const int type, void* pixels)
{
	glReadPixels(x, y, width, height, format, type, pixels);
}

void Load_GL_ReadPixelsOffset(const int x, const int y, const int width, const int height, const int format, const int type, const int64_t pixelsOffset)
{
	intptr_t result = (intptr_t)pixelsOffset;
	glReadPixels(x, y, width, height, format, type, (void*)result);
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

void Load_GL_TexImage2DOffset(const int target, const int level, const int internalformat, const int width, const int height, const int border, const int format, const int type, const int64_t pixelsOffset)
{
	intptr_t result = (intptr_t)pixelsOffset;
	glTexImage2D(target, level, internalformat, width, height, border, format, type, (void*)result);
}

void Load_GL_TexParameterf(const int target, const int pname, const float param)
{
	glTexParameterf(target, pname, param);
}

void Load_GL_TexSubImage2D(const int target, const int level, const int xoffset, const int yoffset, const int width, const int height, const int format, const int type, const void* pixels)
{
	glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, pixels);
}

void Load_GL_TexSubImage2DOffset(const int target, const int level, const int xoffset, const int yoffset, const int width, const int height, const int format, const int type, const int64_t pixelsOffset)
{
	intptr_t result = (intptr_t)pixelsOffset;
	glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, (void*)result);
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
	glBindAttribLocation(program, index, (GLchar*)name);
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

int Load_GL_GenRenderbuffer()
{
    GLuint b;
	if (glGenRenderbuffers) {
		glGenRenderbuffers(1, &b);
	}
	#ifdef GLEW
		glGenRenderbuffersEXT(1, &b);
	#endif
	return b;
}

void Load_GL_DeleteBuffer(const int buffer)
{
	GLuint b = buffer;
	glDeleteBuffers(1, &b);
}

void Load_GL_DeleteBuffers(const int n, const void* buffers)
{
	glDeleteBuffers(n, (GLuint*)buffers);
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

void Load_GL_DeleteFramebuffers(const int n, const void* framebuffer)
{
	if (glDeleteFramebuffers) {
		glDeleteFramebuffers(n, (GLuint*)framebuffer);
	}
#ifdef GLEW
	GLuint b = framebuffer;
	glDeleteFramebuffersEXT(n, (GLuint*)framebuffer);
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

void Load_GL_DeleteRenderbuffers(const int n, const void* renderbuffers)
{
	if (glDeleteRenderbuffers) {
		glDeleteRenderbuffers(n, (GLuint*)renderbuffers);
		return;
	}
#ifdef GLEW
	glDeleteRenderbuffersEXT(n, (GLuint*)renderbuffers);
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

void Load_GL_GenBuffers(const int n, const void* buffers)
{
	if (glGenBuffers) {
		glGenBuffers(n, (GLuint*)buffers);
	}
	#ifdef GLEW
		glGenBuffersEXT(n, (GLuint*)buffers);
	#endif
}

void Load_GL_GenRenderbuffers(const int n, const void* buffers)
{
	if (glGenRenderbuffers) {
		glGenRenderbuffers(n, (GLuint*)buffers);
	}
	#ifdef GLEW
		glGenRenderbuffersEXT(n, (GLuint*)buffers);
	#endif
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

void Load_GL_GenFramebuffers(const int n,const void* buffers)
{
	if (glGenFramebuffers) {
		glGenFramebuffers(n, (GLuint*)buffers);
	}
	#ifdef GLEW
		glGenFramebuffersEXT(n, (GLuint*)buffers);
	#endif
}

char* Load_GL_GetActiveAttrib(const int program, const int index, const void* size, const void* type)
{
	memset(global_cname, '\0', sizeof(global_cname));
	glGetActiveAttrib(program, index, 2048, NULL, (GLint*)size, (GLenum*)type, global_cname);
	return global_cname;
}

char* Load_GL_GetActiveUniform(const int program, const int index, const void* size, const void* type)
{
	memset(global_cname, '\0', sizeof(global_cname));
	glGetActiveUniform(program, index, 2048, NULL, (GLint*)size, (GLenum*)type, global_cname);
	return global_cname;
}

int Load_GL_GetAttribLocation(const int program, const char* name)
{
	return glGetAttribLocation(program, name);
}

void Load_GL_GetBooleanv(const int pname, const void* params)
{
	glGetBooleanv(pname, (GLboolean*)params);
}

bool Load_GL_GetBooleanvResult(const int pname)
{
	GLboolean depthTestEnabled = GL_FALSE;
	glGetBooleanv(GL_DEPTH_TEST, &depthTestEnabled);
	return depthTestEnabled == GL_TRUE;
}

void Load_GL_GetBufferParameteriv(const int target, const int pname, const void* params)
{
	glGetBufferParameteriv(target, pname, (GLint*)params);
}

void Load_GL_GetFloatv(const int pname, const void* params)
{
	glGetFloatv(pname, (GLfloat*)params);
}

float Load_GL_GetFloatvResult(const int pname)
{
	GLfloat result[1];
	glGetFloatv(pname, result);
	return result[0];
}

void Load_GL_GetInteger(const int pname, const void* params)
{
	glGetIntegerv(pname, (GLint*)params);
}

int Load_GL_GetIntegervResult(const int pname)
{
	GLint result[1];
	glGetIntegerv(pname, result);
	return result[0];
}

void Load_GL_GetFramebufferAttachmentParameteriv(int target, int attachment, int pname, const void* params)
{
	if (glGetFramebufferAttachmentParameteriv) {
		glGetFramebufferAttachmentParameteriv(target, attachment, pname, (GLint*)params);
		return;
	}
	#ifdef GLEW
		glGetFramebufferAttachmentParameterivEXT(target, attachment, pname, (GLint*)params);
	#endif
}

void Load_GL_GetProgramiv(int program, int pname, const void* params)
{
	glGetProgramiv(program, pname, (GLint*)params);
}

char* Load_GL_GetProgramInfoLog(const int program)
{
	memset(global_info, '\0', sizeof(global_info));
	int length = 0;
	glGetProgramInfoLog(program, 1024 * 10, &length, global_info);
	return global_info;
}

const char* Load_GL_GetProgramInfoLogs(const int program, const int bufsize, const void* length, const void* infolog)
{
	glGetProgramInfoLog(program, bufsize, (GLsizei*)length, (GLchar*)infolog);
	return infolog;
}

void Load_GL_GetRenderbufferParameteriv(const int target, const int pname, const void* params)
{
	if (glGetRenderbufferParameteriv) {
		glGetRenderbufferParameteriv(target, pname, (GLint*)params);
		return;
	}
#ifdef GLEW
	glGetRenderbufferParameterivEXT(target, pname, params);
#endif
}

void Load_GL_GetShaderiv(const int shader, const int pname, const void* params)
{
	glGetShaderiv(shader, pname, (GLint*)params);
}

const char* Load_GL_GetShaderInfoLog(const int shader)
{
	memset(global_info, '\0', sizeof(global_info));
	int length = 0;
	glGetShaderInfoLog(shader, 1024 * 10, &length, global_info);
	return global_info;
}

const char* Load_GL_GetShaderInfoLogs(const int shader, const int bufsize, const void* length, const void* infolog)
{
	glGetShaderInfoLog(shader, bufsize, (GLsizei*)length, (GLchar*)infolog);
	return infolog;
}

void Load_GL_GetShaderPrecisionFormat(const int shadertype, const int precisiontype, const void* range, const void* precision)
{
	glGetShaderPrecisionFormat(shadertype, precisiontype, (GLint*)range, (GLint*)precision);
}

void Load_GL_ShaderBinary(int count, const void* shaders, int binaryFormat, const void* binary, int length)
{
	glShaderBinary(count, (GLuint*)shaders, binaryFormat, binary, length);
}

void Load_GL_GetTexParameterfv(const int target, const int pname, const void* params)
{
	glGetTexParameterfv(target, pname, (GLfloat*)params);
}

void Load_GL_GetTexParameteriv(int target, int pname, const void* params)
{
	glGetTexParameteriv(target, pname, (GLint*)params);
}

void Load_GL_GetUniformfv(const int program, const int location, const void* params)
{
	glGetUniformfv(program, location, (GLfloat*)params);
}

void Load_GL_GetUniformiv(const int program, const int location, const void* params)
{
	glGetUniformiv(program, location, (GLint*)params);
}

int Load_GL_GetUniformLocation(const int program, const char* name)
{
	return glGetUniformLocation(program, name);
}

void Load_GL_GetVertexAttribfv(const int index, const int pname, const void* params)
{
	glGetVertexAttribfv(index, pname, (GLfloat*)params);
}

void Load_GL_GetVertexAttribiv(const int index, const int pname, const void* params)
{
	glGetVertexAttribiv(index, pname, (GLint*)params);
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

void Load_GL_TexParameterfv(const int target, const int pname, const void* params)
{
	glTexParameterfv(target, pname, (GLfloat*)params);
}

void Load_GL_TexParameteri(const int target, const int pname, const int param)
{
	glTexParameteri(target, pname, param);
}

void Load_GL_TexParameteriv(const int target, const int pname, const void* params)
{
	glTexParameteriv(target, pname, (GLint*)params);
}

void Load_GL_Uniform1f(const int location, const float x)
{
	glUniform1f(location, x);
}

void Load_GL_Uniform1fv(const int location, const int count, const void* v)
{
	glUniform1fv(location, count, (GLfloat*)v);
}

void Load_GL_Uniform1fvOffset(const int location, const int count, const void* v, const int offset)
{
	GLfloat* result = (GLfloat*)v;
	glUniform1fv(location, count, &result[offset]);
}

void Load_GL_Uniform1i(const int location, const int x)
{
	glUniform1i(location, x);
}

void Load_GL_Uniform1iv(const int location, const int count, const void* v)
{
	glUniform1iv(location, count, (GLint*)v);
}

void Load_GL_Uniform1ivOffset(const int location, const int count, const void* v, const int offset)
{
	GLint* result = (GLint*)v;
	glUniform1iv(location, count, &result[offset]);
}

void Load_GL_Uniform2f(const int location, const float x, const float y)
{
	glUniform2f(location, x, y);
}

void Load_GL_Uniform2fv(const int location, const int count, const void* v)
{
	glUniform2fv(location, count, (GLfloat*)v);
}

void Load_GL_Uniform2fvOffset(const int location, const int count, const void* v, const int offset)
{
	GLfloat* result = (GLfloat*)v;
	glUniform2fv(location, count, &result[offset]);
}

void Load_GL_Uniform2i(const int location, const int x, const int y)
{
	glUniform2i(location, x, y);
}

void Load_GL_Uniform2iv(const int location, const int count, const void* v)
{
	glUniform2iv(location, count, (GLint*)v);
}

void Load_GL_Uniform2ivOffset(const int location, const int count, const void* v, const int offset)
{
	GLint* result = (GLint*)v;
	glUniform2iv(location, count, &result[offset]);
}

void Load_GL_Uniform3f(const int location, const float x, const float y, const float z)
{
	glUniform3f(location, x, y, z);
}

void Load_GL_Uniform3fv(const int location, const int count, const void* v)
{
	glUniform3fv(location, count, (GLfloat*)v);
}

void Load_GL_Uniform3fvOffset(const int location, const int count, const void* v, const int offset)
{
	GLfloat* result = (GLfloat*)v;
	glUniform3fv(location, count, &result[offset]);
}

void Load_GL_Uniform3i(const int location, const int x, const int y, const int z)
{
	glUniform3i(location, x, y, z);
}

void Load_GL_Uniform3iv(const int location, const int count, const void* v)
{
	glUniform3iv(location, count, (GLint*)v);
}

void Load_GL_Uniform3ivOffset(const int location, const int count, const void* v, const int offset)
{
	GLint* result = (GLint*)v;
	glUniform3iv(location, count, &result[offset]);
}

void Load_GL_Uniform4f(const int location, const float x, const float y, const float z, const float w)
{
	glUniform4f(location, x, y, z, w);
}

void Load_GL_Uniform4fv(const int location, const int count, const void* v)
{
	glUniform4fv(location, count, (GLfloat*)v);
}

void Load_GL_Uniform4fvOffset(const int location, const int count, const void* v, const int offset)
{
	GLfloat* result = (GLfloat*)v;
	glUniform4fv(location, count, &result[offset]);
}

void Load_GL_Uniform4i(const int location, const int x, const int y, const int z, const int w)
{
	glUniform4i(location, x, y, z, w);
}

void Load_GL_Uniform4iv(const int location, const int count, const void* v)
{
	glUniform4iv(location, count, (GLint*)v);
}

void Load_GL_Uniform4ivOffset(const int location, const int count, const void* v, const int offset)
{
	GLint* result = (GLint*)v;
	glUniform4iv(location, count, &result[offset]);
}

void Load_GL_UniformMatrix2fv(const int location, const int count, const bool transpose, const void* value)
{
	glUniformMatrix2fv(location, count, transpose, (GLfloat*)value);
}

void Load_GL_UniformMatrix2fvOffset(const int location, const int count, const bool transpose, const void* value, const int offset)
{
	GLfloat* result = (GLfloat*)value;
	glUniformMatrix2fv(location, count, transpose, &result[offset]);
}

void Load_GL_UniformMatrix3fv(const int location, const int count, const bool transpose, const void* value)
{
	glUniformMatrix3fv(location, count, transpose, (GLfloat*)value);
}

void Load_GL_UniformMatrix3fvOffset(const int location, const int count, const bool transpose, const void* value, const int offset)
{
	GLfloat* result = (GLfloat*)value;
	glUniformMatrix3fv(location, count, transpose, &result[offset]);
}

void Load_GL_UniformMatrix4fv(const int location, const int count, const bool transpose, const void* value)
{
	glUniformMatrix4fv(location, count, transpose, (GLfloat*)value);
}

void Load_GL_UniformMatrix4fvOffset(const int location, const int count, const bool transpose, const void* value, const int offset)
{
	GLfloat* result = (GLfloat*)value;
	glUniformMatrix4fv(location, count, transpose, &result[offset]);
}

void Load_GL_VertexAttrib1f(const int indx, const float x)
{
	glVertexAttrib1f(indx, x);
}

void Load_GL_VertexAttrib1fv(const int indx, const void* values)
{
	glVertexAttrib1fv(indx, (GLfloat*)values);
}

void Load_GL_VertexAttrib2f(const int indx, const float x, const float y)
{
	glVertexAttrib2f(indx, x, y);
}

void Load_GL_VertexAttrib2fv(const int indx, const void* values)
{
	glVertexAttrib2fv(indx, (GLfloat*)values);
}

void Load_GL_VertexAttrib3f(const int indx, const float x, const float y, const float z)
{
	glVertexAttrib3f(indx, x, y, z);
}

void Load_GL_VertexAttrib3fv(const int indx, const void* values)
{
	glVertexAttrib3fv(indx, (GLfloat*)values);
}

void Load_GL_VertexAttrib4f(const int indx, const float x, const float y, const float z, const float w)
{
	glVertexAttrib4f(indx, x, y, z, w);
}

void Load_GL_VertexAttrib4fv(const int indx, const void* values)
{
	glVertexAttrib4fv(indx, (GLfloat*)values);
}

void Load_GL_VertexAttribPointer(const int indx, const int size, const int type, bool normalized, const int stride, void* ptr)
{
	glVertexAttribPointer(indx, size, type, normalized, stride, ptr);
}

void Load_GL_VertexAttribPointerOffset(const int indx, const int size, const int type, bool normalized, const int stride, const int64_t offset)
{
	intptr_t indices = (intptr_t)offset;
	glVertexAttribPointer(indx, size, type, normalized, stride, (void*)indices);
}

void Load_GL_GetAttachedShaders(const int program, const int maxCount, void* count, void* shaders)
{
	glGetAttachedShaders(program, maxCount, (GLsizei*)count, (GLuint*)shaders);
}

void Load_GL_GetShaderSource(const int shader, const int bufSize, void* length, void* source)
{
	glGetShaderSource(shader, bufSize, (GLsizei*)length, (GLchar*)source);
}
