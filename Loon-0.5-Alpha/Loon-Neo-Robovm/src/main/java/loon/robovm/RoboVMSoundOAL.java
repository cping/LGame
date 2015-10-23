/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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
package loon.robovm;

import loon.SoundImpl;

public class RoboVMSoundOAL extends SoundImpl<Integer> {

  private final RoboVMAudio audio;
  private int sourceIdx;

  public RoboVMSoundOAL(RoboVMAudio a) {
    this.audio = a;
  }

  public int bufferId() {
    return impl;
  }

  @Override
  protected boolean prepareImpl() {
    return true; 
  }

  @Override
  protected boolean playingImpl() {
    return audio.isPlaying(sourceIdx, this);
  }

  @Override
  protected boolean playImpl() {
    sourceIdx = audio.play(this, volume, looping);
    return true; 
  }

  @Override
  protected void stopImpl() {
    audio.stop(sourceIdx, this);
  }

  @Override
  protected void setLoopingImpl(boolean looping) {
    audio.setLooping(sourceIdx, this, looping);
  }

  @Override
  protected void setVolumeImpl(float volume) {
    audio.setVolume(sourceIdx, this, volume);
  }

  @Override
  protected void releaseImpl() {
  }
}
