open module loon.javafx {
	requires transitive javafx.controls;
	requires transitive javafx.graphics;
	requires transitive loon.core;
	requires javafx.media;
	requires java.prefs;
	requires javafx.base;
	exports loon.fx;
	exports loon.log;
}