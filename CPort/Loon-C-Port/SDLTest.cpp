#include <iostream>
#include <SDL.h>
#include <SDL_opengl.h>
#include <GL/GL.h>
#include <GL/GLU.h>
#include <stdio.h>
#include <stdlib.h>

 /*
  * Lesson 0: Test to make sure SDL is setup properly
 */
 int main(int, char**) {
	     if (SDL_Init(SDL_INIT_VIDEO) != 0) {
		         std::cout << "SDL_Init Error: " << SDL_GetError() << std::endl;
		         return 1;
		
	}
	     std::cout << "SDL_Init OK!!!" << std::endl;
	     SDL_Quit();
	
		     return 0;
	
}