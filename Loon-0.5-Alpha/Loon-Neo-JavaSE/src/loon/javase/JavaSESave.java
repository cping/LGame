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
package loon.javase;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import loon.Log;
import loon.Save;
import loon.SaveBatchImpl;

class JavaSESave implements Save {

  private final Log log;
  private final Preferences preferences;
  private boolean isPersisted;

  JavaSESave(Log log, String storageFileName) {
    this.log = log;
    Preferences prefs = null;
    try {
      isPersisted = Preferences.userRoot().nodeExists(storageFileName);
      prefs = Preferences.userRoot().node(storageFileName);
    } catch (Exception e) {
      log.warn("Couldn't open Preferences: " + e.getMessage());
      isPersisted = false;
      prefs = new MemoryPreferences();
    }

    preferences = prefs;
  }

  @Override
  public void setItem(String key, String value) {
    preferences.put(key, value);
    maybePersistPreferences();
  }

  @Override
  public void removeItem(String key) {
    preferences.remove(key);
    maybePersistPreferences();
  }

  @Override
  public String getItem(String key) {
    return preferences.get(key, null);
  }

  @Override
  public Batch startBatch() {
    return new SaveBatchImpl(this) {
      @Override protected void setImpl(String key, String data) {
        preferences.put(key, data);
      }
      @Override protected void removeImpl(String key) {
        preferences.remove(key);
      }
      @Override protected void onAfterCommit() {
        maybePersistPreferences();
      }
    };
  }

  @Override
  public Iterable<String> keys() {
    try {
      return Arrays.asList(preferences.keys());
    } catch (Exception e) {
      log.warn("Error reading preferences: " + e.getMessage());
      return Collections.emptyList();
    }
  }

  @Override
  public boolean isPersisted() {
    return isPersisted;
  }

  private void maybePersistPreferences() {
    if (preferences instanceof MemoryPreferences) return;
    try {
      preferences.flush();
      isPersisted = true;
    } catch (Exception e) {
      log.info("Error persisting properties: " + e.getMessage());
      isPersisted = false;
    }
  }

  /**
   * Wraps a HashMap up as Preferences for in-memory use.
   */
  private class MemoryPreferences extends AbstractPreferences
  {
    MemoryPreferences() {
      super(null, "");
    }
    @Override protected void putSpi (String key, String value) {
      _values.put(key,  value);
    }

    @Override protected String getSpi (String key) {
      return _values.get(key);
    }

    @Override protected void removeSpi (String key) {
      _values.remove(key);
    }

    @Override protected void removeNodeSpi () throws BackingStoreException {
      throw new BackingStoreException("Not implemented");
    }

    @Override protected String[] keysSpi () throws BackingStoreException {
      return _values.keySet().toArray(new String[_values.size()]);
    }

    @Override protected String[] childrenNamesSpi () throws BackingStoreException {
      throw new BackingStoreException("Not implemented");
    }

    @Override protected AbstractPreferences childSpi (String name) {
      throw new RuntimeException("Not implemented");
    }

    @Override protected void syncSpi () throws BackingStoreException {
      throw new BackingStoreException("Not implemented");
    }

    @Override protected void flushSpi () throws BackingStoreException {
      throw new BackingStoreException("Not implemented");
    }
    protected Map<String, String> _values = new HashMap<String, String>();
  }
}
