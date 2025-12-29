#include <iostream>
#include "SDLSupport.h"

static const int SCREEN_FULLSCREEN = 1;
static const int SCREEN_WIDTH = 960;
static const int SCREEN_HEIGHT = 540;
static SDL_Window* window = NULL;
static SDL_GLContext maincontext;

static void sdl_die(const char* message) {
	fprintf(stderr, "%s: %s\n", message, SDL_GetError());
	exit(2);
}

 int main(int, char**) {
	     if (SDL_Init(SDL_INIT_VIDEO) != 0) {
		         std::cout << "SDL_Init Error: " << SDL_GetError() << std::endl;
		         return 1;
		
	     }
	     std::cout << "SDL_Init OK!!!" << std::endl;
		 Load_SDL_ScreenInit("testing", 640, 480, true, SDL_WINDOW_OPENGL | SDL_WINDOW_SHOWN,false);
		 SDL_Event event;
		 bool quit = false;
		 while (!quit) {
			 SDL_GL_SwapWindow(window);
			 while (SDL_PollEvent(&event)) {
				 if (event.type == SDL_QUIT) {
					 quit = true;
				 }
			 }
		 }
	     SDL_Quit();
	
		     return 0;
	
}