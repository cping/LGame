package loon.an;

import android.content.SharedPreferences;

import java.util.ArrayList;

import loon.Save;
import loon.SaveBatchImpl;

public class JavaANSave implements Save {

    final class AndroidBatchImpl extends SaveBatchImpl {

        private SharedPreferences.Editor edit;

        public AndroidBatchImpl(Save storage) {
            super(storage);
        }

        @Override
        protected void onBeforeCommit() {
            edit = saves.edit();
        }

        @Override
        protected void setImpl(String key, String data) {
            edit.putString(key, data);
        }

        @Override
        protected void removeImpl(String key) {
            edit.remove(key);
        }

        @Override
        protected void onAfterCommit() {
            edit.commit();
            edit = null;
        }
    }

    private SharedPreferences saves;

    public JavaANSave(JavaANGame game) {
        this.saves = game.mainPlatform.getSharedPreferences(game.setting.appName);
    }

    @Override
    public void setItem(String key, String data) throws RuntimeException {
        saves.edit().putString(key, data).commit();
    }

    @Override
    public void removeItem(String key) {
        saves.edit().remove(key).commit();
    }

    @Override
    public String getItem(String key) {
        return saves.getString(key, null);
    }

    @Override
    public Batch startBatch() {
        return new AndroidBatchImpl(this);
    }

    @Override
    public Iterable<String> keys() {
        return new ArrayList<String>(saves.getAll().keySet());
    }

    @Override
    public boolean isPersisted() {
        return true;
    }
}
