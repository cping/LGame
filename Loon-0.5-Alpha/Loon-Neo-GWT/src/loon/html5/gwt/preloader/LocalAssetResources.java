package loon.html5.gwt.preloader;

import loon.LSystem;
import loon.utils.Base64Coder;
import loon.utils.ObjectMap;

import com.google.gwt.typedarrays.shared.Int8Array;
import com.google.gwt.typedarrays.shared.TypedArrays;

public class LocalAssetResources {

	static class AssetItem {

		public String url;

		public Object obj;

		public AssetItem(String u, Object o) {
			this.url = u;
			this.obj = o;
		}

		public String toString() {
			String ext = LSystem.getExtension(url).toLowerCase();
			StringBuilder sbr = new StringBuilder();
			if (LSystem.isImage(ext)) {
				sbr.append('i');
			} else if (LSystem.isText(ext)) {
				sbr.append('t');
			} else if (LSystem.isAudio(ext)) {
				sbr.append('a');
			} else {
				sbr.append('b');
			}
			sbr.append(':');
			sbr.append(url);
			sbr.append(':');
			if (obj == null) {
				sbr.append(0);
			} else if (obj instanceof String) {
				sbr.append(((String) obj).length());
			} else if (obj instanceof Integer) {
				sbr.append(((Integer) obj).intValue());
			} else if (obj instanceof Long) {
				sbr.append(((Long) obj).longValue());
			} else if (obj instanceof Blob) {
				sbr.append(((Blob) obj).length());
			} else {
				sbr.append(Long.MAX_VALUE);
			}
			sbr.append(':');
			if (LSystem.isImage(ext)) {
				sbr.append("image");
				sbr.append('/');
				if (ext.equals("jpg")) {
					sbr.append("jpeg");
				} else {
					sbr.append(ext);
				}
			} else if (LSystem.isText(ext)) {
				sbr.append("text/plan");
			} else if (LSystem.isAudio(ext)) {
				sbr.append("video");
				sbr.append('/');
				sbr.append(ext);
			} else {
				sbr.append("application/unknown");
			}
			return sbr.toString();
		}

	}

	public static LocalAssetResources copy() {
		return new LocalAssetResources();
	}

	public StringBuffer _context = new StringBuffer();

	public ObjectMap<String, String> texts = new ObjectMap<String, String>();
	public ObjectMap<String, Blob> binaries = new ObjectMap<String, Blob>();
	public ObjectMap<String, String> images = new ObjectMap<String, String>();

	public LocalAssetResources() {
		loon_def();
	}

	public void loon_def() {
		putImage(
				"assets/loon_bar.png",
				"iVBORw0KGgoAAAANSUhEUgAAAAUAAAALBAMAAABFS1qmAAAAMFBMVEUAAACamppeXl7S0tKJiYlJSUn////n5+fY2NiPj4/29va+vr6kpKSCgoJwcHBOTk6W6nbuAAAA"
						+ "AXRSTlMAQObYZgAAACdJREFUCNdjYBRiYBCrVGAQ61VgkNqnwCB+SIFBYr4Cg7ArCgapAwCWRwZBUDpJTwAAAABJRU5ErkJggg==");
		putImage(
				"assets/loon_control_base.png",
				"iVBORw0KGgoAAAANSUhEUgAAAIAAAACACAMAAAD04JH5AAAA0lBMVEUAAACZmZmZmZmZmZmZmZmZmZmZmZmZmZmZmZmZmZmZmZmZmZmZmZmZmZmZmZmZmZmZmZmZmZmZ"
						+ "mZmZmZmZmZmZmZmZmZmZmZmZmZmZmZmZmZn///9mZmaZmZn8/Pyzs7P+/v709PT6+vru7u7U1NSpqanOzs7c3Nzx8fHLy8vf39/r6+vY2Njj4+PR0dH4+Pi+vr7IyMj2"
						+ "9va7u7u3t7etra3o6OewsLChoaGmpqbZ2dnAwMDFxcWenp6cnJzl5eWjo6PCwsLDw8OLi4t5eXlvb2/WwcrFAAAAG3RSTlMADAMHJBJWlzxILxuzbWM21BeMgHX25Meo"
						+ "7LyNqu8YAAAIlElEQVR42tRYXZOaMBSt6KKyCorfxqABR9GI2WWmPKAdZmv7//9SbwKF1iERafvQ04fOfrDn5Jxzb3b59F9C05rNRqPxkgM+aDY17a8RqLmBefjaahmG"
						+ "ZXUELMswWq3XIej4xyqAnHMbVrer66bZ748F+n3T1PVu1zKEiuZDDfXZgbzT1c3xyG7P573lciawXPbm87Y9Gpt6twMiuIZ/w25xcrvdWw4WE3yHyWKw7LVtLsKSa6hP"
						+ "P4Sz6/1Ruzebcu71xy4OqM+czcZhPg3i3ceaq5jOeu1RXwcfhhIJdemtrgnsgwlwv8fUQSVwaPy+BhED0GB2LYmEevRweHvO2S8nDynhnS5cw9wGGyQSatG3e1OM32KC"
						+ "KoDEbxhPe+2/IUFrvLQsoF8uMN55pWzlPuwwXixBgtV6aWh/cvxXo2vaPaA/MVSK9RWVgp1AQs82u8YrmFD/+B19NAfz947M7u/fkATOHoKYj/QON6H28fvtAZzeRTJc"
						+ "V6sYyeCCC4N2v5YJwC+OD+4fVM37tlrdVH08QA7ChKb2PH96/AApsFsBKFIgSE14VoHWGBrdMaR/dpEKNy7gC1LBPUMTxl1j+EwRePxg/wQfkRLBSoAgJY54AjHwIjzB"
						+ "b+n2DGMPqfElFbBGangYz2zdAgXV+U17gA8MqUFXKb6jB2AHPLDNqgqAv2NC/c7oEdarDFf0CGeootmppEDj529P8enxzv++yvANPcQJT9vcA60qf4we4rrKUeG742oK"
						+ "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"
						+ "FJqVAafUAAAAAElFTkSuQmCC");
		putImage(
				"assets/loon_control_dot.png",
				"iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAMAAACdt4HsAAAAulBMVEUAAABmZmZmZmZmZmZmZmZmZmZmZmZmZmZnZ2dmZmZmZmZmZmZmZmZnZ2dmZmZnZ2dmZmZtbW1m"
						+ "ZmZ4eHiBgYFzc3OWlpaIiIh9fX2Tk5OFhYWPj4+MjIz////+/v77+/v5+fnv7+/9/f3n5+f09PTc3Nzk5OTs7OzU1NTf39+ampqenp7CwsLz8/Ph4eHGxsawsLCqqqrP"
						+ "z8/29vbp6em5ubm2trajo6PY2Nj4+PjKysq/v7+np6e7u7sJzPl4AAAAHXRSTlMAAwwGCSgQFlIfJBo/TDdGMl0sfJls87mL5qvXytf9twMAAASySURBVFjD7VfZkqJA"
						+ "EFwUBFRQPFbHmRGQwwaaUy5F//+3toABxHbdI2Lftp4wwszOzG6o6m//6x8WRQ1pelAXPRxSf4YG8Iid8GOGWSwWzJifsKMBcPw+fMDyzHw6EzhOkjiOm03FBc+OaIr6"
						+ "Xfh4PpWWu4+37zbU97f3zXY9E5kJUPwmfLbcZKGfa678+XnQtfwSZPaSExmWMELiR5MFwEPfPH92tZe1KLS3wpz/hQiKHo1FaXe9HBtoS3FQUbhZT0EE9QrPMtOlU9Rw"
						+ "ksLPVsJiAgwv8LPVFSk1gmRQjGDHzYHhBX4XePsGQTLIZroBhp/kMBzB+oHWwxAa4tQWFuxTBmrAT7fhc3y3G4qW7mbMaPjMwERcZ9YDgjRx8sLtlB9Qzwxw9uVAYAgJ"
						+ "OnKkOUs/M7BKT71/x1ESOk6YRPE9w0ktbGE8oggBC8npGdBwcLFUWVYtP8DavQTvuhQn9GMC/HRXyHd4FHTn4Qw/mueDoqs+SHhIgQIB9p0AxcfqvZxj4SudBytbztlh"
						+ "38FEXKV3CfoXuR/eCfsNwdk93uwZT/cjHAt2tO/04z4elLsJakM4Ro7EjKi+g/X12OUXtPo7U9ZVa0PwruCBunfAiqtb9w5h9EmUrGLcEsBxnE6GvQimm0tLEIcnAg8e"
						+ "jCxuCfBHPwR6LLznbYYR3j85g7oZRC1B9C6M7wkgwzezJUjy5wRJ0hKgN44Z3BMw3Pej3MBC4HpiwcNhS2B8JwnUlsA5ys9CNLDTnAPNsl8SxApJoBzzoiUwDZLgTVMa"
						+ "4WGuHwgHugnvZvkARzk2UWuh2wVDaSQkvqrsHyJUVMMPEnioIvCiN27cI+BnH9GpIYgC05UfEnDNqLhGbQT+u8DT/YO0K9RGQpxFmt4mUvnWNYRTJy4FlA6sZNM/iRQ7"
						+ "3wbHVgJOoS22kcDGuVru30LcCPCMcCWyPYIRIzlIbyRoWYFMFX4e9vAZBdMm8m+Bo7UCkLNewNvYD8G+qSflUDOgK44M86i6uu6qsGkRvqUZAvyXAGxzD5+kITtfZqZ7"
						+ "lr8Y/GvhR7nhmaZn5JFfJGnmf+73oKYUkIerLoLui/Lhq3rDoPhZWmD/AuVjgAeZr1R40GMZF0fqO4CCtrK8WqXvxoUTpklVaRpcHVStf1LBgJUHNtlaIEbBxsdOA3zW"
						+ "nSwMoMLMwWV+NR4E+CCAbG40O19nqGSoRfQbCywvn/Uaj66rVkA/hekqMGLVPSlyuX/3B7la/gufQ3+uuyvZmzg79WIQ8UWxr8GAlpUz7GeseYAvqtZIPW3Pc8m+eVq5"
						+ "/ScwAiRQMqBhdbdcHvAGdpZiY4AwAQ3evhkmUNQcVZ1PFTwu5Rs5drbTcWOAMDHggSFFJlBUHFW5rgrwenl0q/HUT4dEXpTs8GJ5phbHJQlgjyW6gudRYK8BT1MvxtTJ"
						+ "XFg5CbI8D3RUZZboEo5umS2JfH990gVMiutNdkOGZQELlAXoEl6E9nK2mBB4clbl58Jy4wQYIQOqAiM/zT62gjjuhuXX07Y4W9vwLuBLhBC64KSctQVyXH89sEvbDcz7"
						+ "UDDtr9ZfcOqPbxxQQnff+Js7D1PfeOgh9de3LhruXLD4//p39QO4pw/QZc8saAAAAABJRU5ErkJggg==");
		putImage(
				"assets/loon_creese.png",
				"iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAMAAADXqc3KAAABlVBMVEUAAAAAAAAAAAAAAAACAgMAAAAAAAAAAAAAAQEAAAA7R1YAAAAAAAAAAABTSApATl5oaVsAAAAB"
						+ "AQAAAAAAAAABAQAAAAAAAAAAAADAsVhzYxM/NwY3LgRKPwiFlJV8jZBvf4MKCQE6MhILCgFXUjYFBABHQCEIBwEAAABMTEIAAAAAAAAAAAAAAAAKCAICAQDUwWGSgSXN"
						+ "zZeOoIS8qD61nSqZhiE6MQaBcBivwtD8/NT4+Njl7tcfGgN5bCEyKwcoIgRMWWQQEAI2QUcfHgVSSQ6xsZp3hop0g4g0SnFgaWVGPhmAkZBKRSkMCgEEAwBJUVgAAAAA"
						+ "AAAAAAAaFgkQDgMQGCYFBAAsMTECAgIhIR0AAAAAAQH///+x2v///++85P+mzv+ex/////v///WTt+620d+hu8mmkSWpkx2MeBru//+awvys0vqXvfamy/Wixu/v/O2V"
						+ "wu2ewun9/+i+2uX3++SZu+OvydarxNGnwM57aoafjzzCqCyJeSWxjhyaQxiafBSKYhSPORFnWA1gPgt3AABHyJOxAAAAXXRSTlMAAQkCIQZePiUT1S0jB/bVg3l2Wx0Z"
						+ "FhAM/v349O2/vr66trGpoqCUcXBlV0pDNDH9/fz8+vrs7Oro5+Hh4NvZ19HPzsnFxL6+urSurJyai4p+aWdfW1hXT0xBKBsPJg9wAAABNklEQVQoz3XQ5XKDUBAFYMIF"
						+ "LiQlnkCsjbvU3d3dW6DRJqm7uzx3aWc6gZLu329m95xF/p/NiF6/Xg0io13d0xhQgr6T47haUqWA+Y69HVEwhaxOZHhR6hTCbAxc8OkqgpImXYFPZ7mxPwJYktAVDjK5"
						+ "7PiPqPBfUaE1hO76aF/IuUWxzkylKkIS6pvjw3PBbWb8NvsaCqRye5a/FCaD9UWn0Szbpt69yp80Nj9oPWENisjl9O75SeszECJIxTTU9Pb5OmyMQguOSCXlbXn5KLeH"
						+ "4DYtK2Tx24qOwbZS6wot64kH78sOz0LAVepdTsrAq3X6DEuxOddjwwgOKgCgMWSIQjI5+27vw6SxKEiYoIVmYv09gQQlTWXWbFE4Aii4GI5bpV/GURp8H8MScQ2LKAeg"
						+ "lJUFyBc82zMEqyVAYAAAAABJRU5ErkJggg==");
		putImage(
				"assets/loon_e1.png",
				"iVBORw0KGgoAAAANSUhEUgAAAHgAAAB4CAMAAAAOusbgAAAAOVBMVEUAAACrq6urq6urq6urq6urq6urq6urq6urq6urq6urq6urq6urq6urq6urq6urq6urq6urq6ur"
						+ "q6t1Wv5PAAAAEnRSTlMAzfAP40rCZtu1NiWlmgeJWniDDFMPAAACoUlEQVRo3u3a3ZKjIBCG4QYE+VE03/1f7O4cbFpQSGXKzk7V8J7l6ImmSrAJjV5kV3VKA1qpdc8k"
						+ "lgnoNO0klSslryo6kky2ZNPpFgSSaaldolzKmUTy4KYv90tWH7jX+uwSGQ/Okkj66D5L+nOwZpdlYZhdzn4I1pGqdmmY3aqHMMxu3SYLs3tqkYbZLctOFt6olfGSsDbU"
						+ "LErCjjp5QXihTm7AAx7wgH8i/L8emd2N8wwx2ANQzQQ39BtepEimvPke6x+GRqPRLyza+TKbvvWgUbhOq9Ue1aDRTJmbH63B8Gt1t+1bi4lbnzmNy2mY0ZXk1TP/9fFt"
						+ "uF6m8lYC9vKCfawX2Xddc16mogb3bx+zFq4zVKQA8z7sqCeri8nokqks3ADX8nQeFG5EMjDLDE/sPkgOZvkM7yQJU5quYW1JDGb5DE+RxGCWz7BPJAizXMPKkCjMcgkH"
						+ "Q7Iwy/4Ih0zCMBePsKGb4UDtPMOefgNswHDvJc/dDYcj7NOn4BxwgFmWho1CCWNKn4CTRwGzLAvH60UiScNRo4JZloT35g5EJ0l4RhOGjnLwBs4zzLIMnBcccs+d80G+"
						+ "B3bdPzWsfEbWltX7MAGY6VD04HhTaVG05fowid7N1y9gZdrw3Tym3fosAPB3TwB3/gVufk01Hu1CJM4uCo3Ulun90j5fZmOm0Wj0N/N4MaHPJJPCizYSKeLVKYwnkSyA"
						+ "ufu9NIlkX1ySE4QddVoGPOABD/gnwp46BTm4Ox/KWhL2hlo9IAnD5c5qLQljabhaGsbWcMVhzNeuPHw+rUkaH4Fhr115WKfL8yR5GFMqXHGY8+bgSsMRh1SuXbkNfcax"
						+ "kHkQzCfSMoVSNrULSzJFlKmShSOp9gmdnCGx8r4qpcGTO261NPqR/QEVuH97v3yl8AAAAABJRU5ErkJggg==");
		putImage(
				"assets/loon_e2.png",
				"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");
		putImage(
				"assets/loon_logo.png",
				"iVBORw0KGgoAAAANSUhEUgAAAIAAAACACAMAAAD04JH5AAAAb1BMVEUAAAD/////////////////////////////////////////////////////////////////////"
						+ "//////////////////////////////////////////////////////////////////////////8v0wLRAAAAJHRSTlMAA/gUJQ787ugJ8jYb4UTRx4PZvraVrYydpXku"
						+ "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");
		putImage(
				"assets/loon_natural.png",
				"iVBORw0KGgoAAAANSUhEUgAAAQAAAAEACAMAAABrrFhUAAAAkFBMVEUAAACdyOmnsNOZ0vGY1feZ2PalsNWiu96iudyistiezfOx1/ykvOGY1PT////+/v7/////////"
						+ "//////+Y0uv////9/f3////////////////////////+/v7+/v7+/v7+/v79/f3////9/f3////+/v79/f39/f3/////////////////////////7+//1tbpADd9AAAA"
						+ "LnRSTlMAHR0rHiMpJCwhDQYqFySpaEs+CxErinZeVzo0E87FvLWTj4Fu1p2ZfVNDHx0ZUK+hKwAAB7pJREFUeNrsmNtu4jAURQ9W63ASNXECuXO/Uyj9/78bM22ApNiy"
						+ "q5mU2F2qVInAgxd4ex8DACGgitK7BwefcxgAwOl0uvP8GHOO5+feKC7LeOTB+wdwZlRGUTmCt2K/L96gBRDBdXXeLWfgRyEn8gdcQEXteRHmeVjw554fZYxlkc8NXBSM"
						+ "IrbbsWhUrBaLVQEtQIjga3W/8wsY+K85W61Y/loZuPD5PMzS5TLNQn/A15pstwlfLbxXQMkWQbBg5f78bw8tQAhQCndwRALk62dpMp8nKeMGuICmgmORLTdBsFlmxTHO"
						+ "kmA6DZIshquBaHd+bRc9gID+NwQcojydB7NZME/z6ACnBgBxmG5mk8lsk4ZxybbT8Xi6ZeUdAe1tAUoBUUPAcAhi/JAlwWQ8ngQJC304NYE4XwaT9XoSLPOaAGhugfZC"
						+ "kAsgREMAolTAaj4br9fj2XzFBXw1cCvgdgtUBi4hCK2BqCWAoy6gGYP1LXATglApuByD7UGIjgBEQtS3QPMnUA/Bm2NQix8VQCkh6iH4xUDtGLwWIT1+VIDDUT8G7xm4"
						+ "FqEHQU8AIZSqFyFoKrhW4YdZv7YAROUqfKVa/yOiH4JKw9DjfMP/IQTN4leAZgYYJ4DSXwF2C1CeBTzbM+BFJsB1oasoC3BkPeDpCbqKsoB+1QQtF0Cp5QIcx3IB1meA"
						+ "5QJEp8BwCF1FXYC5RQhRSYC5RYhSpSJkbgaoCejJBDw/Q1cRZICmgJcX6CrK47BjaAj+FWBzCCKqCvidBi0Pwb6hIfhvpkHPg66ifx9guQBEq7cAor0h+FmE7D0Ge3UB"
						+ "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");
		final String txt_assets_loon_natural_txt = ("<?xml version=\"1.0\" standalone=\"yes\" ?>"
				+ DefaultAssetFilter.special_symbols
				+ "<pack file=\"assets/loon_natural.png\">"
				+ DefaultAssetFilter.special_symbols
				+ "<block name=\"loon_rain_0\" left=\"0\" top=\"0\" right=\"23\" bottom=\"259\" />"
				+ DefaultAssetFilter.special_symbols
				+ "<block name=\"loon_rain_2\" left=\"23\" top=\"0\" right=\"47\" bottom=\"207\" />"
				+ DefaultAssetFilter.special_symbols
				+ "<block name=\"loon_rain_1\" left=\"47\" top=\"0\" right=\"70\" bottom=\"206\" />"
				+ DefaultAssetFilter.special_symbols
				+ "<block name=\"loon_rain_3\" left=\"70\" top=\"0\" right=\"79\" bottom=\"122\" />"
				+ DefaultAssetFilter.special_symbols
				+ "<block name=\"loon_snow_4\" left=\"79\" top=\"0\" right=\"92\" bottom=\"14\" />"
				+ DefaultAssetFilter.special_symbols
				+ "<block name=\"loon_snow_3\" left=\"112\" top=\"0\" right=\"124\" bottom=\"12\" />"
				+ DefaultAssetFilter.special_symbols
				+ "<block name=\"loon_snow_2\" left=\"124\" top=\"0\" right=\"134\" bottom=\"10\" />"
				+ DefaultAssetFilter.special_symbols
				+ "<block name=\"loon_snow_1\" left=\"145\" top=\"0\" right=\"153\" bottom=\"8\" />"
				+ DefaultAssetFilter.special_symbols
				+ "<block name=\"loon_snow_0\" left=\"153\" top=\"0\" right=\"159\" bottom=\"6\" />"
				+ DefaultAssetFilter.special_symbols
				+ "<block name=\"loon_sakura_0\" left=\"93\" top=\"0\" right=\"112\" bottom=\"13\" />"
				+ DefaultAssetFilter.special_symbols
				+ "<block name=\"loon_sakura_1\" left=\"134\" top=\"0\" right=\"145\" bottom=\"8\" />"
				+ DefaultAssetFilter.special_symbols + "</pack>" + DefaultAssetFilter.special_symbols)
				.replace(DefaultAssetFilter.special_symbols, '\n');
		putText("assets/loon_natural.txt", txt_assets_loon_natural_txt);
		putImage(
				"assets/loon_pad_ui.png",
				"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"
						+ "QmCC");
		final String txt_assets_loon_pad_ui_txt = ("<?xml version=\"1.0\" standalone=\"yes\"?>"
				+ DefaultAssetFilter.special_symbols
				+ "<pack file=\"assets/loon_pad_ui.png\">"
				+ DefaultAssetFilter.special_symbols
				+ "<block id=\"0\" name=\"back\" left=\"0\" top=\"0\" right=\"116\" bottom=\"116\" />"
				+ DefaultAssetFilter.special_symbols
				+ "<block id=\"1\" name=\"fore\" left=\"116\" top=\"0\" right=\"222\" bottom=\"106\" />"
				+ DefaultAssetFilter.special_symbols
				+ "<block id=\"2\" name=\"dot\" left=\"0\" top=\"116\" right=\"48\" bottom=\"164\" />"
				+ DefaultAssetFilter.special_symbols + "</pack>" + DefaultAssetFilter.special_symbols)
				.replace(DefaultAssetFilter.special_symbols, '\n');
		putText("assets/loon_pad_ui.txt", txt_assets_loon_pad_ui_txt);
		putImage(
				"assets/loon_ui.png",
				"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");
		putImage(
				"assets/loon_wbar.png",
				"iVBORw0KGgoAAAANSUhEUgAAAEAAAABABAMAAABYR2ztAAAAIVBMVEUAAAB4dShMShoAAAC+uVQXFgXIxEIoKA5EQg3/+Z2PjBl9o8N9AAAAAXRSTlMAQObYZgAAAZxJ"
						+ "REFUSMeF1jFrg0AYxvEXQqG6nbE4q4NrSoXM0hTXFpqvcIibNpDVsZ9A2k5OoR8zd6fh1TyXyzMkgfz4D4l4Er04R7Ta6ZXh7mrl+LahvBBqUSyuFjT6NVxTHhyPR9Em"
						+ "sEyoL+KU8ihJDjqAiTJJGg3iqGif9rCPLGzFCFTgnWAPKhEbkKkAWaYTBnAAEwZwABMaCA5gQmjAAUxowAFbIqUtB2yJlJ4DB/gMvih3g/U18IY7oK/dwJNycIJeyhrB"
						+ "ImASCDgwJhBwgBMI/G+zDgDODn5o2p8deBVN6wcr6BnUNuBJBnKwgH4OagSenAM5AOiXoAZwWoIKgL8EHQA6zUFFCPw56ADoBIOKbMBn0FkB/fMHADA78H7NHFeUNHNc"
						+ "USepVhEATnAAAf8jLuBzAAD/nE7w2NkAjsH9W9C2dYHslfIyvH0bLA4p5WHzdjMQFQoITmDA3Gk5gYFGg+aSwEBkQHZJYKA1ILkkMBAbEOvEHqYDYgSBSuxgOqCBOVjD"
						+ "VsCiYjpYzZlaJrCDOV1TWk3nMGw8rTd3Hw/OD/3L1zr7IxUAAAAASUVORK5CYII=");
	}

	public void putAssetItem(AssetItem assets) {
		String result = assets.toString() + ";";
		if (_context.indexOf(result) == -1) {
			_context.append(result);
		}
	}

	public void putImage(String url) {
		putAssetItem(new AssetItem(url, Integer.MAX_VALUE));
	}

	public void putImage(String url, int size) {
		putAssetItem(new AssetItem(url, size));
	}

	public void putImage(String url, String base64) {
		images.put(url, base64);
		putAssetItem(new AssetItem(url, Integer.MAX_VALUE));
	}

	public void putText(String url, String context) {
		texts.put(url,
				context.replace(DefaultAssetFilter.special_symbols, '\n'));
		putAssetItem(new AssetItem(url, Integer.MAX_VALUE));
	}

	public void putBlobString(String url, String base64data) {
		if (!Base64Coder.isBase64(base64data)) {
			putText(url, base64data);
			return;
		}
		byte[] bytes = Base64Coder.decodeBase64(base64data.toCharArray());
		Int8Array arrays = TypedArrays.createInt8Array(bytes.length);
		arrays.set(bytes);
		Blob blob = new Blob(arrays);
		binaries.put(url, blob);
		putAssetItem(new AssetItem(url, blob));
	}

	public void commit() {
		texts.put("assets.txt", "list:" + _context.toString());
	}

	public void clear() {
		_context.delete(0, _context.length());
		texts.clear();
		binaries.clear();
	}
}
