package org.test;

import java.io.UTFDataFormatException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import loon.utils.RecordStoreUtils;
import loon.utils.collection.ArrayByte;

public class Settings {

	protected static Settings instance;
	public int levels = 0x21;
	protected boolean[] levelUnlocked;
	private boolean musicEnabled = false;
	public Random random;
	public HashMap<String, String> savingData;
	private boolean vibrateEnabled = false;

	protected Settings() {
		this.levelUnlocked = new boolean[this.levels];
		this.savingData = new HashMap<String, String>();
		this.random = new Random();
	}

	protected final String ArrayToSaveString(boolean[] array) {
		String str = "";
		for (int i = 0; i < array.length; i++) {
			if (!str.equals("")) {
				str = str + ",";
			}
			str = str + array[i];
		}
		return str;
	}

	protected final String ArrayToSaveString(int[] array) {
		String str = "";
		for (int i = 0; i < array.length; i++) {
			if (!str.equals("")) {
				str = str + ",";
			}
			str = str + array[i];
		}
		return str;
	}

	public static Settings Instance() {
		if (instance == null) {
			instance = new Settings();
		}
		return instance;
	}

	public final boolean LevelUnocked(int levelIndex) {
		return this.levelUnlocked[levelIndex];
	}

	public final void LoadSettings() {
		byte[] buffer = RecordStoreUtils.getBytes("bubblegame");
		if (buffer != null) {
			ArrayByte array = new ArrayByte(buffer);
			while (array.available() != -1) {
				String[] strArray;
				try {
					strArray = array.readUTF().split("[#]", -1);
					if (strArray.length > 1) {
						this.OrderLoadedData(strArray[0], strArray[1]);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	protected void OrderLoadedData(String key, String value) {
		String str = key;
		if (str != null) {
			if (!(str.equals("vibrateEnabled"))) {
				if (str.equals("musicEnabled")) {
					this.musicEnabled = Boolean.parseBoolean(value);
				} else if (str.equals("levelUnlocked")) {
					String[] strArray = value.split("[,]", -1);
					for (int i = 0; (i < strArray.length) && (i < this.levels); i++) {
						this.levelUnlocked[i] = Boolean
								.parseBoolean(strArray[i]);
					}
				}
			} else {
				this.vibrateEnabled = Boolean.parseBoolean(value);
			}
		}
	}

	protected void PrepareSaving() {
		this.savingData.put("vibrateEnabled",
				(new Boolean(this.vibrateEnabled)).toString());
		this.savingData.put("musicEnabled",
				(new Boolean(this.musicEnabled)).toString());
		this.savingData.put("levelUnlocked",
				this.ArrayToSaveString(this.levelUnlocked));
	}

	public final void SaveSettings() {
		this.savingData.clear();
		this.PrepareSaving();
		ArrayByte array = new ArrayByte();
		Set<Entry<String, String>> entry = savingData.entrySet();
		for (Iterator<Entry<String, String>> it = entry.iterator(); it
				.hasNext();) {
			Entry<String, String> e = it.next();
			try {
				array.writeUTF(e.getKey() + "#" + e.getValue());
			} catch (UTFDataFormatException ex) {
				ex.printStackTrace();
			}
		}
		RecordStoreUtils.setBytes("bubblegame", array.getData());
	}

	public final boolean getMusicEnabled() {
		return this.musicEnabled;
	}

	public final void setMusicEnabled(boolean value) {
		this.musicEnabled = value;
	}

	public final boolean getVibrateEnabled() {
		return this.vibrateEnabled;
	}

	public final void setVibrateEnabled(boolean value) {
		this.vibrateEnabled = value;
	}
}
