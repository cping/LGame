module loon.se {
	requires transitive loon.core;
	requires transitive java.desktop;
	requires java.prefs;
	exports loon.se;
	exports loon.se.window;
	exports loon.log;
}