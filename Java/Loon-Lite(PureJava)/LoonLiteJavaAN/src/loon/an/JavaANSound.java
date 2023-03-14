package loon.an;

import loon.SoundImpl;

abstract class JavaANSound<I> extends SoundImpl<I> {

    abstract void onPause();

    abstract void onResume();
}