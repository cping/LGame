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
package loon.event;

import loon.utils.StrBuilder;
import loon.utils.reply.Port;

public class MouseMake {

  public static class Event extends loon.event.Event.XYEvent {

    protected Event (int flags, double time, float x, float y) {
      super(flags, time, x, y);
    }
  }

  public static class ButtonEvent extends Event {

    public final int button;

    public boolean down;

    public ButtonEvent (int flags, double time, float x, float y, int button, boolean down) {
      super(flags, time, x, y);
      this.button = button;
      this.down = down;
    }

    @Override protected String name () {
      return "Button";
    }

    @Override protected void addFields (StrBuilder builder) {
      super.addFields(builder);
      builder.append(", id=").append(button).append(", down=").append(down);
    }
  }

  public static abstract class ButtonSlot extends Port<Event> {
    public void onEmit (Event event) {
      if (event instanceof ButtonEvent) onEmit((ButtonEvent)event);
    }
    public abstract void onEmit (ButtonEvent event);
  }

}
