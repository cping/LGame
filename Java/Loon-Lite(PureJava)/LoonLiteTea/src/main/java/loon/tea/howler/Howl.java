/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.5
 */
package loon.tea.howler;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSClass;
import org.teavm.jso.JSMethod;
import org.teavm.jso.JSObject;

import loon.tea.dom.typedarray.ArrayBufferViewWrapper;

@JSClass
public class Howl implements JSObject {

    @JSBody(params = { "arrayBufferView"}, script = "" +
            "var blob = new Blob( [ arrayBufferView ]);" +
            "var howlSource = URL.createObjectURL(blob);" +
            "return new Howl({ src: [howlSource], format: ['ogg', 'webm', 'mp3', 'wav']});")
    public static native Howl create(ArrayBufferViewWrapper arrayBufferView);

    public native int play();
    public native int play(int soundId);
    public native void stop(int soundId);
    public native void pause(int soundId);
    @JSMethod("rate")
    public native void setRate(float rate, int soundId);
    @JSMethod("rate")
    public native float getRate(int soundId);
    @JSMethod("volume")
    public native void setVolume(float volume, int soundId);
    @JSMethod("volume")
    public native float getVolume(int soundId);
    @JSMethod("mute")
    public native void setMute(boolean mute, int soundId);
    @JSMethod("mute")
    public native boolean getMute(int soundId);
    @JSMethod("seek")
    public native void setSeek(float seek, int soundId);
    @JSMethod("seek")
    public native float getSeek(int soundId);
    @JSMethod("duration")
    public native int getDuration(int spriteId);
    @JSMethod("duration")
    public native float getDuration();
    @JSMethod("loop")
    public native void setLoop (boolean loop , int soundId);
    @JSMethod("loop")
    public native boolean getLoop(int soundId);
    @JSMethod("playing")
    public native boolean isPlaying(int soundId);

    @JSMethod("pannerAttr")
    public native void setPannerAttr(HowlPannerAttr pannerAttr, int soundId);
    @JSMethod("pannerAttr")
    public native HowlPannerAttr getPannerAttr(int soundId);
    @JSMethod("stereo")
    public native void setStereo(float pan, int soundId);
    @JSMethod("stereo")
    public native float getStereo(int soundId);

    public native void stop();
    
    public native void pause();
    
    @JSMethod("volume")
    public native void setVolume(float volume);
    @JSMethod("volume")
    public native float getVolume();
    public native float on(String event, HowlEventFunction function, int soundId);
    @JSMethod("stereo")
    public native void setStereo(float pan);
}