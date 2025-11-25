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

		@Override
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
		putImage("assets/loon_bar.png",
				"iVBORw0KGgoAAAANSUhEUgAAAAUAAAALBAMAAABFS1qmAAAAMFBMVEUAAACamppeXl7S0tKJiYlJSUn////n5+fY2NiPj4/29va+vr6kpKSCgoJwcHBOTk6W6nbuAAAA"
						+ "AXRSTlMAQObYZgAAACdJREFUCNdjYBRiYBCrVGAQ61VgkNqnwCB+SIFBYr4Cg7ArCgapAwCWRwZBUDpJTwAAAABJRU5ErkJggg==");
		putImage("assets/loon_control_base.png",
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
		putImage("assets/loon_control_dot.png",
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
		putImage("assets/loon_creese.png",
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
		final String txt_assets_loon_deffont_fnt = ("info face=\"Courier New Bold\" size=16 bold=1 italic=0 charset=\"\" unicode=0 stretchH=100 smooth=1 aa=1 padding=0,0,0,0 spacing=1,1φ"
				+ "common lineHeight=19 base=26 scaleW=256 scaleH=256 pages=1 packed=0φ"
				+ "page id=0 file=\"loon_deffont.png\"φ" + "chars count=189φ"
				+ "char id=32   x=0     y=0     width=0     height=0     xoffset=0     yoffset=14    xadvance=9     page=0  chnl=0φ"
				+ "char id=253   x=0     y=0     width=12     height=16     xoffset=0     yoffset=2    xadvance=9     page=0  chnl=0φ"
				+ "char id=254   x=12     y=0     width=11     height=15     xoffset=0     yoffset=3    xadvance=9     page=0  chnl=0φ"
				+ "char id=221   x=23     y=0     width=12     height=15     xoffset=0     yoffset=0    xadvance=9     page=0  chnl=0φ"
				+ "char id=218   x=35     y=0     width=11     height=15     xoffset=0     yoffset=0    xadvance=9     page=0  chnl=0φ"
				+ "char id=217   x=46     y=0     width=11     height=15     xoffset=0     yoffset=0    xadvance=9     page=0  chnl=0φ"
				+ "char id=211   x=57     y=0     width=11     height=15     xoffset=0     yoffset=0    xadvance=9     page=0  chnl=0φ"
				+ "char id=210   x=68     y=0     width=11     height=15     xoffset=0     yoffset=0    xadvance=9     page=0  chnl=0φ"
				+ "char id=205   x=79     y=0     width=10     height=15     xoffset=1     yoffset=0    xadvance=9     page=0  chnl=0φ"
				+ "char id=204   x=89     y=0     width=10     height=15     xoffset=1     yoffset=0    xadvance=9     page=0  chnl=0φ"
				+ "char id=201   x=99     y=0     width=10     height=15     xoffset=0     yoffset=0    xadvance=9     page=0  chnl=0φ"
				+ "char id=200   x=109     y=0     width=10     height=15     xoffset=0     yoffset=0    xadvance=9     page=0  chnl=0φ"
				+ "char id=199   x=119     y=0     width=11     height=15     xoffset=0     yoffset=4    xadvance=9     page=0  chnl=0φ"
				+ "char id=197   x=130     y=0     width=12     height=15     xoffset=0     yoffset=0    xadvance=9     page=0  chnl=0φ"
				+ "char id=193   x=142     y=0     width=12     height=15     xoffset=0     yoffset=0    xadvance=9     page=0  chnl=0φ"
				+ "char id=192   x=154     y=0     width=12     height=15     xoffset=0     yoffset=0    xadvance=9     page=0  chnl=0φ"
				+ "char id=36   x=166     y=0     width=9     height=15     xoffset=1     yoffset=2    xadvance=9     page=0  chnl=0φ"
				+ "char id=106   x=175     y=0     width=8     height=15     xoffset=1     yoffset=3    xadvance=9     page=0  chnl=0φ"
				+ "char id=255   x=183     y=0     width=12     height=14     xoffset=0     yoffset=4    xadvance=9     page=0  chnl=0φ"
				+ "char id=219   x=195     y=0     width=11     height=14     xoffset=0     yoffset=1    xadvance=9     page=0  chnl=0φ"
				+ "char id=213   x=206     y=0     width=11     height=14     xoffset=0     yoffset=1    xadvance=9     page=0  chnl=0φ"
				+ "char id=212   x=217     y=0     width=11     height=14     xoffset=0     yoffset=1    xadvance=9     page=0  chnl=0φ"
				+ "char id=209   x=228     y=0     width=11     height=14     xoffset=0     yoffset=1    xadvance=9     page=0  chnl=0φ"
				+ "char id=206   x=239     y=0     width=10     height=14     xoffset=1     yoffset=1    xadvance=9     page=0  chnl=0φ"
				+ "char id=202   x=0     y=16     width=10     height=14     xoffset=0     yoffset=1    xadvance=9     page=0  chnl=0φ"
				+ "char id=195   x=10     y=16     width=12     height=14     xoffset=0     yoffset=1    xadvance=9     page=0  chnl=0φ"
				+ "char id=194   x=22     y=16     width=12     height=14     xoffset=0     yoffset=1    xadvance=9     page=0  chnl=0φ"
				+ "char id=162   x=34     y=16     width=10     height=14     xoffset=1     yoffset=2    xadvance=9     page=0  chnl=0φ"
				+ "char id=92   x=44     y=16     width=9     height=14     xoffset=1     yoffset=3    xadvance=9     page=0  chnl=0φ"
				+ "char id=47   x=53     y=16     width=9     height=14     xoffset=1     yoffset=3    xadvance=9     page=0  chnl=0φ"
				+ "char id=93   x=62     y=16     width=6     height=14     xoffset=2     yoffset=3    xadvance=9     page=0  chnl=0φ"
				+ "char id=91   x=68     y=16     width=6     height=14     xoffset=4     yoffset=3    xadvance=9     page=0  chnl=0φ"
				+ "char id=41   x=74     y=16     width=5     height=14     xoffset=2     yoffset=3    xadvance=9     page=0  chnl=0φ"
				+ "char id=40   x=79     y=16     width=5     height=14     xoffset=3     yoffset=3    xadvance=9     page=0  chnl=0φ"
				+ "char id=250   x=84     y=16     width=11     height=13     xoffset=0     yoffset=2    xadvance=9     page=0  chnl=0φ"
				+ "char id=249   x=95     y=16     width=11     height=13     xoffset=0     yoffset=2    xadvance=9     page=0  chnl=0φ"
				+ "char id=243   x=106     y=16     width=10     height=13     xoffset=0     yoffset=2    xadvance=9     page=0  chnl=0φ"
				+ "char id=242   x=116     y=16     width=10     height=13     xoffset=0     yoffset=2    xadvance=9     page=0  chnl=0φ"
				+ "char id=237   x=126     y=16     width=10     height=13     xoffset=1     yoffset=2    xadvance=9     page=0  chnl=0φ"
				+ "char id=236   x=136     y=16     width=10     height=13     xoffset=1     yoffset=2    xadvance=9     page=0  chnl=0φ"
				+ "char id=233   x=146     y=16     width=11     height=13     xoffset=0     yoffset=2    xadvance=9     page=0  chnl=0φ"
				+ "char id=232   x=157     y=16     width=11     height=13     xoffset=0     yoffset=2    xadvance=9     page=0  chnl=0φ"
				+ "char id=229   x=168     y=16     width=10     height=13     xoffset=1     yoffset=2    xadvance=9     page=0  chnl=0φ"
				+ "char id=225   x=178     y=16     width=10     height=13     xoffset=1     yoffset=2    xadvance=9     page=0  chnl=0φ"
				+ "char id=224   x=188     y=16     width=10     height=13     xoffset=1     yoffset=2    xadvance=9     page=0  chnl=0φ"
				+ "char id=220   x=198     y=16     width=11     height=13     xoffset=0     yoffset=2    xadvance=9     page=0  chnl=0φ"
				+ "char id=216   x=209     y=16     width=11     height=13     xoffset=0     yoffset=3    xadvance=9     page=0  chnl=0φ"
				+ "char id=214   x=220     y=16     width=11     height=13     xoffset=0     yoffset=2    xadvance=9     page=0  chnl=0φ"
				+ "char id=207   x=231     y=16     width=10     height=13     xoffset=1     yoffset=2    xadvance=9     page=0  chnl=0φ"
				+ "char id=203   x=241     y=16     width=10     height=13     xoffset=0     yoffset=2    xadvance=9     page=0  chnl=0φ"
				+ "char id=196   x=0     y=30     width=12     height=13     xoffset=0     yoffset=2    xadvance=9     page=0  chnl=0φ"
				+ "char id=182   x=12     y=30     width=10     height=13     xoffset=1     yoffset=3    xadvance=9     page=0  chnl=0φ"
				+ "char id=167   x=22     y=30     width=10     height=13     xoffset=0     yoffset=3    xadvance=9     page=0  chnl=0φ"
				+ "char id=166   x=32     y=30     width=4     height=13     xoffset=4     yoffset=4    xadvance=9     page=0  chnl=0φ"
				+ "char id=124   x=36     y=30     width=4     height=13     xoffset=4     yoffset=4    xadvance=9     page=0  chnl=0φ"
				+ "char id=125   x=40     y=30     width=6     height=13     xoffset=3     yoffset=4    xadvance=9     page=0  chnl=0φ"
				+ "char id=123   x=46     y=30     width=6     height=13     xoffset=2     yoffset=4    xadvance=9     page=0  chnl=0φ"
				+ "char id=81   x=52     y=30     width=11     height=13     xoffset=0     yoffset=4    xadvance=9     page=0  chnl=0φ"
				+ "char id=251   x=63     y=30     width=11     height=12     xoffset=0     yoffset=3    xadvance=9     page=0  chnl=0φ"
				+ "char id=245   x=74     y=30     width=10     height=12     xoffset=0     yoffset=3    xadvance=9     page=0  chnl=0φ"
				+ "char id=244   x=84     y=30     width=10     height=12     xoffset=0     yoffset=3    xadvance=9     page=0  chnl=0φ"
				+ "char id=241   x=94     y=30     width=11     height=12     xoffset=0     yoffset=3    xadvance=9     page=0  chnl=0φ"
				+ "char id=240   x=105     y=30     width=10     height=12     xoffset=0     yoffset=3    xadvance=9     page=0  chnl=0φ"
				+ "char id=238   x=115     y=30     width=10     height=12     xoffset=1     yoffset=3    xadvance=9     page=0  chnl=0φ"
				+ "char id=234   x=125     y=30     width=11     height=12     xoffset=0     yoffset=3    xadvance=9     page=0  chnl=0φ"
				+ "char id=231   x=136     y=30     width=10     height=12     xoffset=0     yoffset=7    xadvance=9     page=0  chnl=0φ"
				+ "char id=227   x=146     y=30     width=10     height=12     xoffset=1     yoffset=3    xadvance=9     page=0  chnl=0φ"
				+ "char id=226   x=156     y=30     width=10     height=12     xoffset=1     yoffset=3    xadvance=9     page=0  chnl=0φ"
				+ "char id=223   x=166     y=30     width=9     height=12     xoffset=0     yoffset=3    xadvance=9     page=0  chnl=0φ"
				+ "char id=181   x=175     y=30     width=11     height=12     xoffset=0     yoffset=6    xadvance=9     page=0  chnl=0φ"
				+ "char id=127   x=186     y=30     width=11     height=12     xoffset=0     yoffset=3    xadvance=9     page=0  chnl=0φ"
				+ "char id=35   x=197     y=30     width=10     height=12     xoffset=1     yoffset=4    xadvance=9     page=0  chnl=0φ"
				+ "char id=64   x=207     y=30     width=8     height=12     xoffset=1     yoffset=4    xadvance=9     page=0  chnl=0φ"
				+ "char id=48   x=215     y=30     width=9     height=12     xoffset=1     yoffset=3    xadvance=9     page=0  chnl=0φ"
				+ "char id=57   x=224     y=30     width=9     height=12     xoffset=1     yoffset=3    xadvance=9     page=0  chnl=0φ"
				+ "char id=56   x=233     y=30     width=9     height=12     xoffset=1     yoffset=3    xadvance=9     page=0  chnl=0φ"
				+ "char id=55   x=242     y=30     width=9     height=12     xoffset=0     yoffset=3    xadvance=9     page=0  chnl=0φ"
				+ "char id=54   x=0     y=43     width=9     height=12     xoffset=1     yoffset=3    xadvance=9     page=0  chnl=0φ"
				+ "char id=53   x=9     y=43     width=9     height=12     xoffset=1     yoffset=3    xadvance=9     page=0  chnl=0φ"
				+ "char id=52   x=18     y=43     width=9     height=12     xoffset=1     yoffset=3    xadvance=9     page=0  chnl=0φ"
				+ "char id=51   x=27     y=43     width=9     height=12     xoffset=0     yoffset=3    xadvance=9     page=0  chnl=0φ"
				+ "char id=50   x=36     y=43     width=8     height=12     xoffset=1     yoffset=3    xadvance=9     page=0  chnl=0φ"
				+ "char id=121   x=44     y=43     width=12     height=12     xoffset=0     yoffset=6    xadvance=9     page=0  chnl=0φ"
				+ "char id=113   x=56     y=43     width=11     height=12     xoffset=0     yoffset=6    xadvance=9     page=0  chnl=0φ"
				+ "char id=112   x=67     y=43     width=11     height=12     xoffset=0     yoffset=6    xadvance=9     page=0  chnl=0φ"
				+ "char id=108   x=78     y=43     width=10     height=12     xoffset=1     yoffset=3    xadvance=9     page=0  chnl=0φ"
				+ "char id=107   x=88     y=43     width=11     height=12     xoffset=0     yoffset=3    xadvance=9     page=0  chnl=0φ"
				+ "char id=105   x=99     y=43     width=10     height=12     xoffset=1     yoffset=3    xadvance=9     page=0  chnl=0φ"
				+ "char id=104   x=109     y=43     width=11     height=12     xoffset=0     yoffset=3    xadvance=9     page=0  chnl=0φ"
				+ "char id=103   x=120     y=43     width=11     height=12     xoffset=0     yoffset=6    xadvance=9     page=0  chnl=0φ"
				+ "char id=102   x=131     y=43     width=10     height=12     xoffset=1     yoffset=3    xadvance=9     page=0  chnl=0φ"
				+ "char id=100   x=141     y=43     width=11     height=12     xoffset=0     yoffset=3    xadvance=9     page=0  chnl=0φ"
				+ "char id=98   x=152     y=43     width=11     height=12     xoffset=0     yoffset=3    xadvance=9     page=0  chnl=0φ"
				+ "char id=252   x=163     y=43     width=11     height=11     xoffset=0     yoffset=4    xadvance=9     page=0  chnl=0φ"
				+ "char id=248   x=174     y=43     width=10     height=11     xoffset=0     yoffset=5    xadvance=9     page=0  chnl=0φ"
				+ "char id=246   x=184     y=43     width=10     height=11     xoffset=0     yoffset=4    xadvance=9     page=0  chnl=0φ"
				+ "char id=239   x=194     y=43     width=10     height=11     xoffset=1     yoffset=4    xadvance=9     page=0  chnl=0φ"
				+ "char id=235   x=204     y=43     width=11     height=11     xoffset=0     yoffset=4    xadvance=9     page=0  chnl=0φ"
				+ "char id=228   x=215     y=43     width=10     height=11     xoffset=1     yoffset=4    xadvance=9     page=0  chnl=0φ"
				+ "char id=222   x=225     y=43     width=10     height=11     xoffset=0     yoffset=4    xadvance=9     page=0  chnl=0φ"
				+ "char id=208   x=235     y=43     width=11     height=11     xoffset=0     yoffset=4    xadvance=9     page=0  chnl=0φ"
				+ "char id=198   x=0     y=55     width=12     height=11     xoffset=-1     yoffset=4    xadvance=9     page=0  chnl=0φ"
				+ "char id=191   x=12     y=55     width=9     height=11     xoffset=1     yoffset=7    xadvance=9     page=0  chnl=0φ"
				+ "char id=190   x=21     y=55     width=12     height=11     xoffset=0     yoffset=4    xadvance=9     page=0  chnl=0φ"
				+ "char id=189   x=33     y=55     width=12     height=11     xoffset=0     yoffset=4    xadvance=9     page=0  chnl=0φ"
				+ "char id=188   x=45     y=55     width=12     height=11     xoffset=0     yoffset=4    xadvance=9     page=0  chnl=0φ"
				+ "char id=177   x=57     y=55     width=9     height=11     xoffset=1     yoffset=4    xadvance=9     page=0  chnl=0φ"
				+ "char id=174   x=66     y=55     width=11     height=11     xoffset=0     yoffset=4    xadvance=9     page=0  chnl=0φ"
				+ "char id=169   x=77     y=55     width=11     height=11     xoffset=0     yoffset=4    xadvance=9     page=0  chnl=0φ"
				+ "char id=165   x=88     y=55     width=12     height=11     xoffset=0     yoffset=4    xadvance=9     page=0  chnl=0φ"
				+ "char id=163   x=100     y=55     width=10     height=11     xoffset=0     yoffset=4    xadvance=9     page=0  chnl=0φ"
				+ "char id=161   x=110     y=55     width=4     height=11     xoffset=3     yoffset=7    xadvance=9     page=0  chnl=0φ"
				+ "char id=38   x=114     y=55     width=9     height=11     xoffset=1     yoffset=4    xadvance=9     page=0  chnl=0φ"
				+ "char id=37   x=123     y=55     width=9     height=11     xoffset=1     yoffset=4    xadvance=9     page=0  chnl=0φ"
				+ "char id=63   x=132     y=55     width=8     height=11     xoffset=1     yoffset=4    xadvance=9     page=0  chnl=0φ"
				+ "char id=33   x=140     y=55     width=4     height=11     xoffset=3     yoffset=4    xadvance=9     page=0  chnl=0φ"
				+ "char id=49   x=144     y=55     width=10     height=11     xoffset=1     yoffset=4    xadvance=9     page=0  chnl=0φ"
				+ "char id=116   x=154     y=55     width=9     height=11     xoffset=0     yoffset=4    xadvance=9     page=0  chnl=0φ"
				+ "char id=90   x=163     y=55     width=9     height=11     xoffset=1     yoffset=4    xadvance=9     page=0  chnl=0φ"
				+ "char id=89   x=172     y=55     width=12     height=11     xoffset=0     yoffset=4    xadvance=9     page=0  chnl=0φ"
				+ "char id=88   x=184     y=55     width=11     height=11     xoffset=0     yoffset=4    xadvance=9     page=0  chnl=0φ"
				+ "char id=87   x=195     y=55     width=13     height=11     xoffset=-1     yoffset=4    xadvance=9     page=0  chnl=0φ"
				+ "char id=86   x=208     y=55     width=11     height=11     xoffset=0     yoffset=4    xadvance=9     page=0  chnl=0φ"
				+ "char id=85   x=219     y=55     width=11     height=11     xoffset=0     yoffset=4    xadvance=9     page=0  chnl=0φ"
				+ "char id=84   x=230     y=55     width=10     height=11     xoffset=0     yoffset=4    xadvance=9     page=0  chnl=0φ"
				+ "char id=83   x=240     y=55     width=9     height=11     xoffset=1     yoffset=4    xadvance=9     page=0  chnl=0φ"
				+ "char id=82   x=0     y=66     width=12     height=11     xoffset=0     yoffset=4    xadvance=9     page=0  chnl=0φ"
				+ "char id=80   x=12     y=66     width=10     height=11     xoffset=0     yoffset=4    xadvance=9     page=0  chnl=0φ"
				+ "char id=79   x=22     y=66     width=11     height=11     xoffset=0     yoffset=4    xadvance=9     page=0  chnl=0φ"
				+ "char id=78   x=33     y=66     width=11     height=11     xoffset=0     yoffset=4    xadvance=9     page=0  chnl=0φ"
				+ "char id=77   x=44     y=66     width=13     height=11     xoffset=-1     yoffset=4    xadvance=9     page=0  chnl=0φ"
				+ "char id=76   x=57     y=66     width=11     height=11     xoffset=0     yoffset=4    xadvance=9     page=0  chnl=0φ"
				+ "char id=75   x=68     y=66     width=11     height=11     xoffset=0     yoffset=4    xadvance=9     page=0  chnl=0φ"
				+ "char id=74   x=79     y=66     width=11     height=11     xoffset=0     yoffset=4    xadvance=9     page=0  chnl=0φ"
				+ "char id=73   x=90     y=66     width=10     height=11     xoffset=1     yoffset=4    xadvance=9     page=0  chnl=0φ"
				+ "char id=72   x=100     y=66     width=11     height=11     xoffset=0     yoffset=4    xadvance=9     page=0  chnl=0φ"
				+ "char id=71   x=111     y=66     width=11     height=11     xoffset=0     yoffset=4    xadvance=9     page=0  chnl=0φ"
				+ "char id=70   x=122     y=66     width=11     height=11     xoffset=0     yoffset=4    xadvance=9     page=0  chnl=0φ"
				+ "char id=69   x=133     y=66     width=10     height=11     xoffset=0     yoffset=4    xadvance=9     page=0  chnl=0φ"
				+ "char id=68   x=143     y=66     width=11     height=11     xoffset=0     yoffset=4    xadvance=9     page=0  chnl=0φ"
				+ "char id=67   x=154     y=66     width=11     height=11     xoffset=0     yoffset=4    xadvance=9     page=0  chnl=0φ"
				+ "char id=66   x=165     y=66     width=10     height=11     xoffset=0     yoffset=4    xadvance=9     page=0  chnl=0φ"
				+ "char id=65   x=175     y=66     width=12     height=11     xoffset=0     yoffset=4    xadvance=9     page=0  chnl=0φ"
				+ "char id=247   x=187     y=66     width=10     height=10     xoffset=0     yoffset=5    xadvance=9     page=0  chnl=0φ"
				+ "char id=62   x=197     y=66     width=10     height=10     xoffset=0     yoffset=5    xadvance=9     page=0  chnl=0φ"
				+ "char id=60   x=207     y=66     width=11     height=10     xoffset=0     yoffset=5    xadvance=9     page=0  chnl=0φ"
				+ "char id=59   x=218     y=66     width=6     height=10     xoffset=2     yoffset=7    xadvance=9     page=0  chnl=0φ"
				+ "char id=230   x=224     y=66     width=12     height=9     xoffset=0     yoffset=6    xadvance=9     page=0  chnl=0φ"
				+ "char id=164   x=236     y=66     width=11     height=9     xoffset=0     yoffset=4    xadvance=9     page=0  chnl=0φ"
				+ "char id=43   x=0     y=77     width=9     height=9     xoffset=1     yoffset=5    xadvance=9     page=0  chnl=0φ"
				+ "char id=122   x=9     y=77     width=9     height=9     xoffset=1     yoffset=6    xadvance=9     page=0  chnl=0φ"
				+ "char id=120   x=18     y=77     width=11     height=9     xoffset=0     yoffset=6    xadvance=9     page=0  chnl=0φ"
				+ "char id=119   x=29     y=77     width=13     height=9     xoffset=-1     yoffset=6    xadvance=9     page=0  chnl=0φ"
				+ "char id=118   x=42     y=77     width=11     height=9     xoffset=0     yoffset=6    xadvance=9     page=0  chnl=0φ"
				+ "char id=117   x=53     y=77     width=11     height=9     xoffset=0     yoffset=6    xadvance=9     page=0  chnl=0φ"
				+ "char id=115   x=64     y=77     width=9     height=9     xoffset=1     yoffset=6    xadvance=9     page=0  chnl=0φ"
				+ "char id=114   x=73     y=77     width=10     height=9     xoffset=0     yoffset=6    xadvance=9     page=0  chnl=0φ"
				+ "char id=111   x=83     y=77     width=10     height=9     xoffset=0     yoffset=6    xadvance=9     page=0  chnl=0φ"
				+ "char id=110   x=93     y=77     width=11     height=9     xoffset=0     yoffset=6    xadvance=9     page=0  chnl=0φ"
				+ "char id=109   x=104     y=77     width=12     height=9     xoffset=0     yoffset=6    xadvance=9     page=0  chnl=0φ"
				+ "char id=101   x=116     y=77     width=11     height=9     xoffset=0     yoffset=6    xadvance=9     page=0  chnl=0φ"
				+ "char id=97   x=127     y=77     width=10     height=9     xoffset=1     yoffset=6    xadvance=9     page=0  chnl=0φ"
				+ "char id=42   x=137     y=77     width=10     height=8     xoffset=1     yoffset=4    xadvance=9     page=0  chnl=0φ"
				+ "char id=58   x=147     y=77     width=3     height=8     xoffset=3     yoffset=7    xadvance=9     page=0  chnl=0φ"
				+ "char id=99   x=150     y=77     width=10     height=8     xoffset=0     yoffset=7    xadvance=9     page=0  chnl=0φ"
				+ "char id=215   x=160     y=77     width=7     height=7     xoffset=1     yoffset=7    xadvance=9     page=0  chnl=0φ"
				+ "char id=187   x=167     y=77     width=10     height=7     xoffset=0     yoffset=8    xadvance=9     page=0  chnl=0φ"
				+ "char id=186   x=177     y=77     width=8     height=7     xoffset=2     yoffset=3    xadvance=9     page=0  chnl=0φ"
				+ "char id=184   x=185     y=77     width=5     height=7     xoffset=3     yoffset=12    xadvance=9     page=0  chnl=0φ"
				+ "char id=178   x=190     y=77     width=7     height=7     xoffset=2     yoffset=3    xadvance=9     page=0  chnl=0φ"
				+ "char id=172   x=197     y=77     width=11     height=7     xoffset=0     yoffset=8    xadvance=9     page=0  chnl=0φ"
				+ "char id=171   x=208     y=77     width=11     height=7     xoffset=0     yoffset=8    xadvance=9     page=0  chnl=0φ"
				+ "char id=94   x=219     y=77     width=8     height=7     xoffset=1     yoffset=3    xadvance=9     page=0  chnl=0φ"
				+ "char id=44   x=227     y=77     width=4     height=7     xoffset=3     yoffset=11    xadvance=9     page=0  chnl=0φ"
				+ "char id=39   x=231     y=77     width=4     height=7     xoffset=4     yoffset=4    xadvance=9     page=0  chnl=0φ"
				+ "char id=34   x=235     y=77     width=8     height=7     xoffset=2     yoffset=4    xadvance=9     page=0  chnl=0φ"
				+ "char id=185   x=243     y=77     width=5     height=6     xoffset=3     yoffset=4    xadvance=9     page=0  chnl=0φ"
				+ "char id=179   x=248     y=77     width=7     height=6     xoffset=3     yoffset=4    xadvance=9     page=0  chnl=0φ"
				+ "char id=170   x=0     y=86     width=7     height=6     xoffset=2     yoffset=4    xadvance=9     page=0  chnl=0φ"
				+ "char id=180   x=7     y=86     width=4     height=5     xoffset=3     yoffset=2    xadvance=9     page=0  chnl=0φ"
				+ "char id=176   x=11     y=86     width=5     height=5     xoffset=2     yoffset=4    xadvance=9     page=0  chnl=0φ"
				+ "char id=126   x=16     y=86     width=9     height=5     xoffset=1     yoffset=7    xadvance=9     page=0  chnl=0φ"
				+ "char id=61   x=25     y=86     width=11     height=5     xoffset=0     yoffset=7    xadvance=9     page=0  chnl=0φ"
				+ "char id=96   x=36     y=86     width=4     height=5     xoffset=3     yoffset=2    xadvance=9     page=0  chnl=0φ"
				+ "char id=183   x=40     y=86     width=3     height=3     xoffset=3     yoffset=8    xadvance=9     page=0  chnl=0φ"
				+ "char id=175   x=43     y=86     width=13     height=3     xoffset=-1     yoffset=1    xadvance=9     page=0  chnl=0φ"
				+ "char id=168   x=56     y=86     width=6     height=3     xoffset=2     yoffset=4    xadvance=9     page=0  chnl=0φ"
				+ "char id=95   x=62     y=86     width=13     height=3     xoffset=-1     yoffset=17    xadvance=9     page=0  chnl=0φ"
				+ "char id=45   x=75     y=86     width=9     height=3     xoffset=1     yoffset=8    xadvance=9     page=0  chnl=0φ"
				+ "char id=46   x=84     y=86     width=3     height=3     xoffset=3     yoffset=12    xadvance=9     page=0  chnl=0φ"
				+ "kernings count=-1φ").replace('φ', '\n');
		putText("assets/loon_deffont.txt", txt_assets_loon_deffont_fnt);
		putImage("assets/loon_deffont.png",
				"iVBORw0KGgoAAAANSUhEUgAAAQAAAAEACAMAAABrrFhUAAAAmVBMVEX///8AAAD/////////////////////////////////////////////////////////////////"
						+ "////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////"
						+ "//9EnpBsAAAAMnRSTlMKAH9AXyCeFt+/BPsQJnLlOvfzDNWxlbnvSus0q88cbFjHMCzbUMNUj8uLeIdoo6dkg5UkDO8AACMCSURBVHja5FvXtusoDMU27r07Tu/tpFz9"
						+ "/8cNknCcnKkPU9bM7LWur4IxZYNAEhzxAdd2xd8G90dby30m/igWghHZkUBYrStmK6GRxeIXsI9+od5fqvJGiZsQ/I/kr/H75VQLm0SMuAwZjhMxYsU/SjP4yPmZ0Uoh"
						+ "7QEcgXgcHDFiGf9CVcvQZcEEUyBkHkc9iyIw0yIJSGrCu3vhAtxZn+rq4iqVm4Y+67qT+IbSqWwSrt07AfahHnOs3YHAwyN6ZZgJjWl6HcTgR7phYda6r5zOt4xlt7NE"
						+ "cAs5fWasxVtduoJFRVUxGWtonjaNqrOnyWoHhrD1bPhq42n1pK/viyavOXV9j2S35alw25xziUO9m9jri/jEZTY01PPF1Bp+rMcRvDclC8TxQ0uHG2dgXpbDUPmx0Lie"
						+ "x5zfMj7hKBTO3BZnPY7KY6yrfDRaOuagYCoqEaVwC4DEmAHMcVBN03T3+FA/Jk6dCEb8VTcxtcN8GgZl2JgE91217IVhRLbNBKQeQKjGdWua0hBXymqrLyx6qN6ZZhLQ"
						+ "AzsxexwHFWzarwXXhbCQdBQwo2Uy9viWoboMI6OYarFMDRyG/S3dT2NjE17FEhASFWEeG1bXYQMqADjO1WMvghrCHHhCr3LoIFR5pyFAZlCGEyCYfMYUADZGD3AiAuB8"
						+ "tBxwRAIQGhvKSpXWCT6I+HCKD1dNUChyYCVoAAroplQXAnVnTlK4EDUwHHzL+CDAxVStAtTAm27aW/ostUpDaVasODkhAaLuXMPi5SBKZ7ZRriDBYp3IkMWuFCILq40R"
						+ "V2CpGlKoAsoQ2LbKbL/r/wwepZGAlxEBlTAMwwnLYBI+jKDKM26VXRp2ulalbuDkGu4JpiKowiW+utO6VLvGpO9d1M0TZHaJJdsZnGhbSWCl5i046i3MlDQDpCwaGZBQ"
						+ "AOtNYNdQrH+enhUQVnqZsYkAC76MuljwxHVUqy2Qiss2UGICe8VQGCtxUdSYoYcLZuDM4h1GmR4C4wcc9RpgKPwAF1s4GdZbEzDV86lyU4km2GJPklGHrgh64m0PCece"
						+ "CLaH9RqsbevSig2OcV/98JSq8ruM2uTAJPzSA9KtTe70Z7qdOHNw3ggQfj8JTfFJwASOhkKJ87mvDcSsowwHmP4yAdjdiUg9XVKuCnB3KY5FleZ++esEXMDF1IkiewuJ"
						+ "gUjrXyPAqYGVGeam7kYNj6c85RVO4WJt+B3PiCfcbMrxkV62U1X+3HsnYAp5sfgkAKk2EFhC6JDoAGWIw/mvERB1bQIrXRJA5Rf5hAYH8lj8OgEOJnK1GUiDc/waAQqa"
						+ "AAXdgK9eyfMpqUhiX7XmV93WPudMxphuw8E2svT8ToA4YPGfBEzhaihMcS5WVYByVXGGBGa/QoB6lZ8DXdLZnFeP6WtP/A0CEsgM+niqNK9B0c2bX1WBI2SZZBUonFKv"
						+ "8yvdkwoQax5Wwu1buk1Sl30QsC5KwTj4qq8SMhGknW0YwoetIpjm5Q0umqEz/BoBUQET8dnnzx9PUKWW3QxlmliOUvxtuFYfL9I0UE0JkYwHTH6VgMAKHI8J2LoWTwf3"
						+ "Mt9xp+9SygdpfgM3Jafrb+nB9bgGZytGAjZWAvVAgBLljw5NAivvzeeBJll5Bkc2YeoKsSx2jr0JQQrLcZTs2B8EiLQK9C6czl9mkOkUqbMkcZGnz9uZu+fAl3TCB+7S"
						+ "UC2TlFjd9sUPWUMbiKhx5jBzUKOWzgzmzkkN9AFax3F2nnoLOyW1moDWw/KPc2gtYbWwnohLUTSRuHjguGI1po+8RpZl1WDx1on8Mk456M0/8wC6a6A3OIAalwmfVLAB"
						+ "qUohWG8EZNYFtMm3wVLbYfxBwWfZUj+KhGUslU3cZwGw47kTzwHCOwo5DGruk9S5ijOGR2+/N4Bem8KkZw+Qx/z8SFeQhUszRKFRzZ1IBUxhuNY00GI8HVOzhfg9ZNq0"
						+ "IqxUodtfzmZFgxhb8SButsEg2pjhr8dCSjkVfyYCiSjF/xaGAnvN35HZ31P2sBIjJiDJNwlEQM9FLBhb8Q/AbS3x9SVGxNgpTLJYYeN2K5xkjCOMBLx5zWbY2omWL2kn"
						+ "hYalLQJenTUO+YLcBJs2qUTU3YIzh8zP8HV5Kd+8+kyTlIzeiBavrvjM9z3jct1GA/kTXfbXUHacS+H7LD9ntbRyU0mYJPOYm2UJzxnjCCMBb16zEyfdPGYRPWyqhvcx"
						+ "Joj+Z2TgoKPoEwG+aYuntnDmIbXWy17uMxepAw/fowqrdM9C4k1/O/wwC74sTYW30WXPuOwInU30HqUgJy2FsOAXSX2nXkgTIdm3ZZFhLHVCRKWojjAkFalxAofWuKg7"
						+ "iAEziPXupPf7YBcuBmLE80BvGU9PC5cmGqMKM54v1falU737yleK94wMedYz4PZW+HXO6ghQoffYCbELl6LsoNVhpy6kXaMDRIE2A8MfGC6AQO6NAqsRf5AGJMY72nOZ"
						+ "iWFc47AVHwRg0Tfa1aYYWzQMmzzBEiM3rnpQZzaW+9o14gBTKLyBcCk/I4gpH6cP0b5yamWBTrI+0tjhjJQygyuOaA2fIAU1GlFfrIJyBk8l2ioL1ZBBoipNBwIMd52q"
						+ "3yc0MxZXKDrBiOK80GteW9zElh0vO6wFowHrOwFbaDDGUwQYWzSWTPIZwEaSbTR5UoDQwf4ufPQDFJmXYQ/ehOOgJMj+g8TpmcYWp1GOkk1FKjgBqgKnsZJqx+cEkXED"
						+ "n5bohJzDYJ6OesyGZG3599ca4HtG8iVzJMfvltRpzn/rWo5X/MCOZrrfrH1udxbfCQho2uVrUV4734hNCjAefbCDpXooMaykdaewgJf/sJK+j8TUdJiA8or5CUuoLWsG"
						+ "FySqS6WVFCmFAs3JsXiQgkirgcmY9kGAA8EmT11y/Wa9gVhC9EFAfcIGjwR0pufpaX0qi0OgF/k+uIRbIuDCKZyFmfuC/ScBCBrC8EBuxLtzyN6TqCp0oE/hQiQwQZ+N"
						+ "fH1s6afvkaZSwStKrMZU4p2yZF9OQxyVq8bhpDFtJOAKey+0jASVtZ0bCAn2BwEAnwSQlcrDe7RrreVTeNh7aEgFQNrWTn9Sw5bCFpX4GQExIDlV/2sEsINugcWuNGf4"
						+ "OQERaNhYG8PUwTKkOKq08TqkfRKwyENIjAVFOJo8wnoaCD5VwA1NOwneVKDeUSw+BcRDk4HII4pOoySFJsahNUf+nIAVXDGoCzYTEKQjARdM7OcRudIbcSFXOu5mvzgD"
						+ "isoiUCskiTbWGxsCi2zyY2QcQQpOizBNhyTO6F3eobglKc3QCcwCw9gU7bc1wJhG2O6RgEWMfTjCSUrZcjSiPyvZZMc8+IIxkHzAGJiXRj8noKWNUeKPqrslc0hXxFXz"
						+ "vIdI2xO85Fkjwa4XNvKr6OyocVo4oHu6dND7dFakXudE/mjnqopN3p+eyb2Sit2HNH1IT6LNr7JJ4bAa03DihVc5g0QRWngdgDbtZnB4mj1qsumgT2qqImdYoTMfCAgu"
						+ "aaF+z8Bc7mCmGrKGdis2LcxXYnNXTR7JYxzhh1i9dMg+EQEnOxBZSBt22XeK+g6gywe3HCCFDt3y7vsuMLiHlvBZcIjUAqdcixktmu87Vf09pCI7d7OjAsF5S1MNqfTW"
						+ "MEvjMtsKRmCGWA87t6zqctQwQtQDw2SnmZ+S2iIHe8G9ihHNUxzPJYmfpnA4ZTMF2D0tR09yMYjTTzvgF8Hb+2Z0Pi1XCxsuMsgsxfeYprHR1UTfiorFX4j4ZULGwpoI"
						+ "xlL8y+AkYvWKLtkK2t5Skiv6i1JepfYXuLO9mufU80v9w34FI8Qbfuu4dvgimPwsIysIdO2vHeUGdsntEyLCxydMsH2PpCT3CmrqdpXxh9ax1LXKjT4MI5Cso4Rm59IM"
						+ "bUknMyooJAUAhxe6Ke9/YtcvqRYooJDam+mcSBv9+Tp+Ht+Pa8s6v7h3/foCnnlXn2dVzy7qmBHNpLW0vkIffYKdZSXi8yjXSqEwKR5liV49sGKLgWw6INY7UoRua9iF"
						+ "en3SG1rsAaTWYNHPAloPCIJh4QEei3bvTWDFq0PUWYYhRwJ4IdR7VRauY2PqhTFOid3ErbVd2cTLvorJUFpvp9VNFdksHiH3ATtOkd04TUqHHesxozinpWEYfivEdT/Z"
						+ "dezCYdlshqWV6cNUUEDwyacMlsFQ8sbPrbTC8b3P0MDAI5LGanAvnHfHfY+HC36xstjs3FWqz1UuCGxrxMw3PA2wgMcVjbV4+csE1D3u6HbRoDNTGqV6DIvSSy+yKetR"
						+ "uX2tgUtnsoQjTWFM+nR2ijud6UTs7Gx5emyt7BVgNtQ/3Qz5jQA3BIIqdZ4Yy10oRav4DLyD2MLNMFa4nQNWQNsMlZ62L8XcFfA1mP8DActwze5P+EsE9A2fOlXozMhX"
						+ "xN3CSUaH49EsBEglujgqjUsSwfLRXtOD+pxNT47TgN60q3OAjouirQl13DXL+SUTkBzg+CsEiElVSTjsA9U+Kbujqjl8qDd3UARItD4bPPd0DdzDGaP9pbI4Pju+Zdrd"
						+ "NAHNOZikngyEFWsCsnC3Em6NhAgwDYTjiamjKjjyzNrmaWKZOcbYD3C3VhWFovPEcqi95RzWhxzyQDzNuSag3BWJJb18g5X4iVy2kKmJsrSWaNPEyteZ7FImoOngNQOW"
						+ "3wkQ61ZgJ5GA9geqADhsGgf9QbgVtDinOy9Ph6l3J+9vcPCkpsM9eZoAN11vS7OYocwE2Hme4Ipi4mi1BqLykUliWJLlvSXPYyWm8KUkt2/Vm2R4bWJi7NES42gCbvAg"
						+ "g9PRsX3If5RC7BvnQvXEptPMQc+AaNrCUUj1RZNDHb0TsPfy3IPiTgQ4u0m6HAhAxvoCcG11oPK7XKtUMG43UbeL43wtNIY1wHXAWdhVkn69VIDmIlneJ9iTVZ98ENB6"
						+ "KhE1lV0TMrLfXtcgqE3uGwGn9/BMCTOynvzB/5E5ScMaYJTpgw05D/L4nQDLh3QOFSryfClaSB2R13hWFeL2UtfPsBFRXgtj288EgS4TMBIgxG8EMLZ+cU+TX1wE3T5s"
						+ "5B3OQhMwoR7OCptmQCImpCPubv5OQEPHyXUuPmbA1UJsaKLVsKAGXQPDRQLCQxaI2UCAzAwMccKSw0GfKgCOVuqmFoS6iIyo4x9RC1NVwhPHxNcagFUxKk9K+Sy+vhEw"
						+ "iQyjCX8Ev0gAr10zl7zDVp4KONi4BvSmbMJdiWtA/UxSmExrOGRRTa+zPMXbJxc8n9sVDh27lUp4Js6Zam88h1XyIi8VqKGEtUzaHJoIPRgfqiS/62Z8J2ABP/b8YoOT"
						+ "3FyITVhL3C2j5mam2InA6xLphNzPKD8IjTvsTJWr778+CfBU8fvcMF4EXLmCXUWRvhpgJ80G3V92Taxf2gVowso459dHdDvuWNw47RctimkiRrhrTMH36MmEPU53iTYi"
						+ "QL8dCfgwhDK4JXoSX8Erdqq9txDCBEcFIH0GOKQebi7RK4iukQ9ndh5NHB9q8Ml7mvt+p1YtXPWvdB8idTkUlLPepAVATarCbs14bDbaASMGL8YV3+Git/+JjaWP3EaX"
						+ "iiuKsNv06juurp0M2eQkeDsYXFjxWIArfgdHKS0pXZbkBE/dp6qMJBAbHTAP5BJH+GgLd3UMxH8D7lGSizE6Qgs9KOVEZjwoSGtgY7jbtYeX04wv19p2FGUx0z4aeVQW"
						+ "I+DEb27M9miJwYYMFpybMy0onP794OtdT/SpF/lwcT2edfG510dihLftrmI4EonFz/BVABSDalItWQgWe1bsP9gVhZ8vAJ2B8es9hYlQu1II3aV6h/KeTxm81zH6qwv2"
						+ "kNgDsMDLCKRajVbqxSv22mPuM+d8P/hymJ6jjO3zkU+9yIezivGsi8+9PhIngJhy8IKjJp9woJ0cJRJw0idDQQVMQL5bWReQqjOm9czbzRwSo3yA75Jr1672OwwZxSeA"
						+ "w2oyD+kipaf7avkjAa6jOyfZrGGCTpYsoKaKUs80H6ZgSLQT95hznBJ64umoUp9bPANfLvx4eJ8tvgVSxALmpim3dKxjxzU8xQcSuIzRPcYSdkxAHLH/UD2IKCML1bJ4"
						+ "yKmKwg/QH/LJ9lnzZStM9z0xlvc6HbJfbIvMKpETuNDNOokdzOCEOQnbrHQ8zhln7jAlaOJRa9fyWADW9KPQd0GOY5hbptqNWKCQ61QoPC+HhOOzYpd+TgHfs2UWUIPX"
						+ "nn9TUpmmCVgDj1+QUJQpmHfY+WjDYeOI/QGfCWC5+k7AAyB0+SjnRUAHUKzwxqWkLyV1ENGRZdQpYccEHADI5BXPuWeU1zmgBeuLss1b2Igf0O4nLfbcNYeDjkl43quY"
						+ "whrn3OloHYAIJLPYPVcmXt7ZGj9ooEeEaQg0qU0o1nNo0BJZvXbbAoADwEENV7TprnXO3+88ZflLPQNYvn8jIKvgkeCOYvojAelq4nU4A9CjZCfkeQLfNJ8cFZms5sAE"
						+ "5KbqTMuGIxv2FH/24WiBdItHgKR377ex1pBgXBvQNFo6zoG7qv0CLwWFm2F+JwDmkwRvPUX7ADXT3YRt8CLAMisqfHGAHwZZsnASmu1Q8QVMAKCcbr8RUMAp0PJIgMtd"
						+ "EQ+opRmCHDrArO5Qlzwm4Mm38N4JEL3fqnQT9nwQQL0Zv++AIdmThoGAg3w2eYOZ9T3Yd6RhpL0GHQCvofN68F40zXMhsjQ02ezEsxJG7HjnH3MmwGl2u4srvhEARSZ+"
						+ "RsDQlaABgPMnARGzrNeAgIpeDQTMgAs9PGtYBzENa1CH7vsM6C2CK6rdRBiXgQCF3clFAshDER+Y5UgACOGyIZmdfN+vYO2Kcd2SebgyCFNoxDuGNUAwPgmQXnj7NQJo"
						+ "7dva32ZAQadnLRNgI+HkgzZhcvNxpQtayPFswUWT9HC7zeFLBBe6j+dscFZWpjTrKhO597zdC5ipxIUDO6fBUxZcA+5deBQfkOBLM6zFJpw95VdRBbjAHmj1kakpb498"
						+ "Ng2xgsc5sqw2tMQI25rPN4ZYgaNTV46Taj9HqbId51AvxQbPYRzHpCAfNPRwxyt/IjhRB1a0t9a3ZQtFEy13oGr/0adCBzbzFDwxBZPvGOhdIL8Ew8kGUXn0ANVxSycz"
						+ "kFKiFQJdu2NPumitn9lBKsPZFmUTAgkKKX+RzbG42rWA0dBTjKgoEJfg86oHmOCzaEc9yhIIHifmrsOlX4crf1H3OhQKnBygSNV7H6DiqwEEKyt5TxcjyowTR3AUsdQv"
						+ "XfGHMF4yjPTFi48hzqLPS3pyIkZM1e+9EWGy+ydd+SvfatyiU/TXIzIZCXa4Yne//9HeBaMuvFFdOvjUHu2SC2k+9digSGTczMTFmHvGulHXK6zrfHlnfq6XGnfZ+tcS"
						+ "NaCKTD8fKgwuG96JCJHgP+HZv/4ix0LzPglQNF18KTnzRkzxiaJNDyESk6EKjBt/tk/emN0DdMMUdVuHmza9rIWG5b3NiF8kwErrS+PRBV0SqySgG6gr1JGW6+g60pJZ"
						+ "IsiyqdqtEM7F+IE8bwpIvfAQiAa8FYRMwLg2mkCw6RqNFHP2H85YdIKWFmniEa9I5Rzlb8QJnzN0bWr2b7zXBrktcq/A0kZk0MDKXntiRNmkjnr+sF8EBGasCcB0V4iJ"
						+ "adbgqBE2LM+ybbesTcy7KbN4geLCK1zxAxLSyBqvDMuxwvMZr777E2PCdyQSDriYMA+y8rWPkIVsByZk9jE8Y/WpKtOEG5ZZovmX5V5MNzNs13YTmPDmQ7vZMxDrNR5a"
						+ "kzHizewZ2Hg/v05jI6o/CLj1T5BJW6HcJkyAEYOY7LrNQEB2LjJNgErvYzSZGCejsh6pv1tFvS0qa1o5lY+isMJZPESe2l4w1hwf3pJ1vUzNXeKuRNTYfPFUkl3FBLxq"
						+ "SEywS6+w2bNTZbaBLhSWabFFKa+NNFywUeQWLd1TKT/2aM8RDtBhQLULlA9SRWJE20jwIItpLByasZW/u5/CWrp43h56ezM/m0chTil4e5W+ipAMunhmi21vWI6xcowm"
						+ "UeJsa7hfSqTgXlroa4xr+OKACjj6lgnheLm3XV1ylXjwBY83AiyykM34J26utLl1GwbCIi3qvi/b8X07sePg//+4BgBl2e17baczbzrtfnAYjWRJECkugV1Hx1HbOxq+"
						+ "ME0fsssK7ebqPEFMmjAAIRDUqV9JSnOnAJh2Cw56t+sWnhBMvz6ENZZv2MtjxzpNVx9I/3ExM5m69ZwvNPUmN1RPAlI1H6nCS7cjx/lu+iMCOQMnJ8S1E1kJnCQRYn4l"
						+ "XHLcz5wYdO2d3djaX9Ij1WguEoChHsawK1ZZqa+kJWIYI48wPYXdoU0lbuiAuC9eWZqlX8G1orJpAA8onoQLJlen8sHYmiguPC0K4iYy11GW0/bQKVU6fQ7AeBoo15z1"
						+ "6PP+3fycjYLJdxOgpFE3fvWOKKzo460qSM7yobLUTzN2kLYld9/rjwKQ5Qcoz8eBUwqu2IZzY9eY7Q3TMzDO3Tas4WcByMqRmdR4hQdO1ThBNVJFzLvoG9/bDMrK8TUP"
						+ "vIy7KxfpdAyLVHtKaIAof+tlXTjjaTKNvptx5Vcf8XcTju4OXVs439MUYPagJfU0noXII7m+j7KWu7OViwZ/DEBk0nTB9/EaABW2cMVaMrWYZL28CzRWMjRu5ObLUD8H"
						+ "YIHJ78j7ZVolpB86oNJ4UpNHJmbveXn15IhWIFh6XtiSR1dA5W56brPdEbi5iLgJCwrAUnK2O2yJD9OMRtFo9E6UO+13AJh5WH61Gi+ETCuxzZ31vSlqXLOIia6JSLUI"
						+ "jp0OT6AKbCkFToXh9EnmuwtEznTQ76ks51d0NScKatjopMIMLExKnJOh4EasuRn6xhAAB6deARZvYSus13LgRT1dV7sVgG3O9/KEhBQPioQaNKacWkDsRPw49VMlFj3R"
						+ "4DUqtIWxR07S9/Gx6YLC6+U7PaYIiiaHNfutQNBYYg4JpTNmA0nvDJicyg7TFfwYW72P4B9gzGx6aP4Ux9iWEh57GxlNskFo8ADDlYR/jjLLfkT9nzeOxdgdwENU/26v"
						+ "pamdyHrPAY7ERYlzskaZ+Cf9Y83dihoL4qMDtX4f7OPsOd+MaRth07R1shwPVvLNMxvXT1r+QNe+k92hZ7Kx9aQnT09MTJ4TspkNiN/CDSv28piCuIVYlfATJMgQTt4h"
						+ "I+n94OnGes9hjvRZ8foyDol/8iZ5B9XUONqZDvbImA32cUqWN3QiuiodYkql/sFKrvtD0Ovzg9OMIrnGLkXEh6Y/1PasMfSIhF6YA3ZJ+dhWiKUwSvMYqEj3lss3woT3"
						+ "finojFYkn7/mJz6UpPPbc25e/OCIVRBxPXmLOOeBuOdcb7oel2BtBFGC1QYYC2JIXNsgab7hB5TwifiRkdthiQ5rEMZjpqewIFc5s/GIWqvCCwBq4tHZDsHuGq+7ks5q"
						+ "BS2v0Cnu+MaC75Z3tKGgud2dtFgdp/xezD3fqw6YwQApYI9S36ZzOFOpXvzg+PCej7FAtehyVL/31sYXPBgQvErzCU9FcrczbHVRIqm+JHGuele5e+4zmHdcgpHJ/D19"
						+ "2NpPuGDbvBW0gNECIRv3lJzIqkJvMMGJyGKNrqZEMqtE3ApdeA3Ah5rUmDwCsGgxfvGDo/Wes1ClWDvY/DEAFVYl/EkAascaA84+POB57BF4dZVLrg9dqvi+7Op8ckRu"
						+ "uNWoX+QV9WNCy3WOnhkyDhO4Yg8FwDr9qGhfA/BcL/JDKiGcXv3gaL3nzEwaLOb6OQBRxAvW1CYL1f6HAWBoe4y2LzJvsO4g4hAAqcbwGXt4/ZvKhIdxL2gplcBYvSLO"
						+ "Y5hV6B0fuegleFU8+kBHKRVJV6Wbeg0A948GGH7u+28TePWDo3jPJQAZon4JQNvKErTGlfShHwYgsYMdqrMUSnLD/bqSpyGu8uh96AENeXueArAej7dO2AyClt9htcO5"
						+ "fQfssNpbUzlAnb8nKVb6g81Ze7yNr2EDT5Ch6aJ+Tum++sHRes+Fm7p18BIA3xdPgOmqoP+G5OfvgAZjfjt+ycCODRDEVa5Rccv+BkY6L7m4G/Gu0iFF0JKEHyAwYikf"
						+ "+9jpAATBteDymMkzkVtMi95bXSMhXcCATYsHB4JzQU41R1joix8cHNw1EXvPgwbnJwBHcsZLFvi7qU8J4DdNHvYVDSL36nQVWPv4gehnXONB0YkygFk+/dBXIrZRQ8eP"
						+ "AWzm2iU2TmejxgHYY5QQa51BcOFTtXj5JEGLcfNe0BLvhIl0TgQDzIXW4RvhHVRCXVix5tqZ6clxGsMALdPvuOiMHWnOww8uc6PH5LNBzQnoztgN4KPAB1ekL1Maxdmn"
						+ "KPSf7eOKvlXbl4A6k/Cl3VivgLKsWaDkEryT0MYdUYLkkTfuXAjparf8+csxziL4J4iyv8jlln+f327UJoBfjktn5fEEEWeUIF5H/iOfXIZnqbmhDQtqLuQqRckjx80y"
						+ "GqyR5eDR2BATFXoUZabMZg91CR9jhcG2WbyLGiK1FYKwSKDGJRwQonMTqIKL6S+Z+SNlp2JtwQTy0ILqEmCoUm5t72tYudnCfQeoKeuaec75NNS+hh4r4ozaFMyI5Y98"
						+ "vtHSb0I9kTZ4pLzwxA3Jg4WPa3aI7ShwQ0S+hCnuCsS2BNp7it2UR5gUYTpeKn8OE3lJq3QDEFMivsHPyYRfFWesmDdWbXQslqDSWy1pkMRAVLluFUGNArYzr9fRqlcG"
						+ "xJ0r+aY93okERMgmQ48Df7v9LgCd85Fjw/WlT1NgPeZUQ5s5KsFzDMrFQ2wa9O8QLD0PjIhB0NsrhxXERxexuH75oxu2k/0aNb9hbpMEHRLO4fqInd7teD19UXvJWry7"
						+ "SyXerM/8gFVljZlRWFNHKhKAheghYIPn4ITL+Wo0crmneO9gktECF2CUAF0w0xSW+PYoPNB0U+a3GSrYYwzdVx+AxS3lKCmtD6j1KoDkA1psAlFWTm1lu+w+Na63vMAO"
						+ "vBRGDcYyX/Yf+bRZKnG7K9zF7LSk0n1QVRQeoZK2iIBfnO6JsdVaL0MmJJPGdblbZJ80WTmldefdmYgAlC16eKebOW9U4cIuGQWtw5NMuKCaRAICe6JV0QQNimtjjdc1"
						+ "7kk4ZDCGbRjAWdsAlDCSfH+Bggwg+OwJoSqwt0X5UxoROBPpzHKR1/AaAHXOEbvJIEHvaaSLfEkvAXAoAM+LP1hzy7Hk48F+F+GJAxBDi050RhifXZhMySy4O8zfIq6Z"
						+ "1zBapgpeAwDx3A32tCdcsAEzLeB2ATjQEQBfGzLUht5kOfdTx2qJWlQqY5bQBNsl3UHXuXg2/XK5DrEQ83eaNmE8BGBNT7Mxo3hVFU8B2LJFFNbdzwIww5MiZPT1iRnN"
						+ "JAAurlr6enM+APjoXnVS5ZBogOjCFHsZS3CeV12dlw+0Rm6pdCOI2d55CQA2NTgG/ojSrmSHcv0bdq5bIYBboBNU6N0tjc9uuAYbjX501WGyPGCxBI3rRCcpAkQNeyBX"
						+ "tF45XJdzvNIz2CWQ4E6RU7COcL7xiyU96vq6bNafAA06VhgMsLqCaSKKD1JujTmbhl+NIQAeMph1uFH/a5dBcSbxNzCiAmfS2lAFvUMfVEEk5Zw9/4LOYxaQ8vgB0SGu"
						+ "k8eI2mf77415zQVgfO6FwU+YCb1VKoZfDxLN/BNWVap+Ig9mf7xQ4gF/CpP1CuMNNf5FjOq3WxeDldpHCNDTtA9PUI3FYVpApnoYkWTqif/ApJ+MIoC2gv8MRrptC0XT"
						+ "UfI1NxmWPuZ3mLgAiS9ozeJSUgAyfKCFTRMFl83ee2APuufzJkf4zyCpNvFUkQR1ZYjGnbCpsOnSl+7+Vi0gMsFK99jCDzD0gDiD/wwmqe+FndlWMyABPKiEeFC6eR0C"
						+ "7z78b/Ebe3VMAwAMAkCQpakAdvzLJEwkaLjz8Pl8P65Z5CZQAQAAAAAAAECzBwcCAAAAAED+r42gqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq0B4cEAAAAAIL+"
						+ "v/aDGQAAAACAJ+TMIbJOX5cBAAAAAElFTkSuQmCC");
		putImage("assets/loon_e1.png",
				"iVBORw0KGgoAAAANSUhEUgAAAHgAAAB4CAMAAAAOusbgAAAAOVBMVEUAAACrq6urq6urq6urq6urq6urq6urq6urq6urq6urq6urq6urq6urq6urq6urq6urq6urq6ur"
						+ "q6t1Wv5PAAAAEnRSTlMAzfAP40rCZtu1NiWlmgeJWniDDFMPAAACoUlEQVRo3u3a3ZKjIBCG4QYE+VE03/1f7O4cbFpQSGXKzk7V8J7l6ImmSrAJjV5kV3VKA1qpdc8k"
						+ "lgnoNO0klSslryo6kky2ZNPpFgSSaaldolzKmUTy4KYv90tWH7jX+uwSGQ/Okkj66D5L+nOwZpdlYZhdzn4I1pGqdmmY3aqHMMxu3SYLs3tqkYbZLctOFt6olfGSsDbU"
						+ "LErCjjp5QXihTm7AAx7wgH8i/L8emd2N8wwx2ANQzQQ39BtepEimvPke6x+GRqPRLyza+TKbvvWgUbhOq9Ue1aDRTJmbH63B8Gt1t+1bi4lbnzmNy2mY0ZXk1TP/9fFt"
						+ "uF6m8lYC9vKCfawX2Xddc16mogb3bx+zFq4zVKQA8z7sqCeri8nokqks3ADX8nQeFG5EMjDLDE/sPkgOZvkM7yQJU5quYW1JDGb5DE+RxGCWz7BPJAizXMPKkCjMcgkH"
						+ "Q7Iwy/4Ih0zCMBePsKGb4UDtPMOefgNswHDvJc/dDYcj7NOn4BxwgFmWho1CCWNKn4CTRwGzLAvH60UiScNRo4JZloT35g5EJ0l4RhOGjnLwBs4zzLIMnBcccs+d80G+"
						+ "B3bdPzWsfEbWltX7MAGY6VD04HhTaVG05fowid7N1y9gZdrw3Tym3fosAPB3TwB3/gVufk01Hu1CJM4uCo3Ulun90j5fZmOm0Wj0N/N4MaHPJJPCizYSKeLVKYwnkSyA"
						+ "ufu9NIlkX1ySE4QddVoGPOABD/gnwp46BTm4Ox/KWhL2hlo9IAnD5c5qLQljabhaGsbWcMVhzNeuPHw+rUkaH4Fhr115WKfL8yR5GFMqXHGY8+bgSsMRh1SuXbkNfcax"
						+ "kHkQzCfSMoVSNrULSzJFlKmShSOp9gmdnCGx8r4qpcGTO261NPqR/QEVuH97v3yl8AAAAABJRU5ErkJggg==");
		putImage("assets/loon_e2.png",
				"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");
		putImage("assets/loon_logo.png",
				"iVBORw0KGgoAAAANSUhEUgAAAIAAAACACAMAAAD04JH5AAAAb1BMVEUAAAD/////////////////////////////////////////////////////////////////////"
						+ "//////////////////////////////////////////////////////////////////////////8v0wLRAAAAJHRSTlMAA/gUJQ787ugJ8jYb4UTRx4PZvraVrYydpXku"
						+ "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");
		putImage("assets/loon_natural.png",
				"iVBORw0KGgoAAAANSUhEUgAAAQAAAAEACAMAAABrrFhUAAABCFBMVEUAAACnsNOfvuOb0O+Z0vGY1velsNWiu96iudyY2/iazu2kvOGk0Puc1PH////+/v7///+z2v/////+/v7///////+V1fT///+b0/T+"
						+ "/v79/f3////////////////////////+/v7+/v7+/v79/f3////////////9/f39/f3////////////////+/v4AAAAHBwcLCwsSEhIPDw8YGBgVFRUhISEbGxseHh4lJSUqKionJyf/7+8sLCwuLi7/1tYx"
						+ "MTE2NjYzMzM6OjpISEhDQ0M/Pz9MTExjY2NcXFw9PT3///9TU1NqampXV1d5eXlQUFB0dHRvb2+Dg4Pn5+fu7u7FxcXNzc3iQsSzAAAAL3RSTlMAHR0dKx4pJCwiDyoFJCSpawo+kVYrGB0XvZt2Xjo0EwvR"
						+ "ybWBZkwNi4l9S0lD2dPx4PQAAAvhSURBVHja3Jlpc9MwEIYXMdglk7FdxyHkJmdPCGehUO4zpdz//78gNRZrVVbWSiiKecsMM0z48Dy7Wq1SALhyBYrG+OnZuoFMbjUCnsYtADg5OQEtO406T2MHALb79V6v"
						+ "3t+Gl4uASL8Xx70+NLpJ0m18BjKMQaUCRcPYZQu4FcSHBweHccANnMhk+YPuNEmm3WAHtoO4E0WdOBAGpIJ+HO3vR3G/G9VqUbdQTQ1lrTjoAMF/O5lE0SS5nRrApPzTTns8bnemwQ5n3Wu19jgtvJSBXlQL"
						+ "w1rUS8RfSTEBngc58R0IEPyT9m6tttueCAOSHRU0up1xKwxb4063Ue/shc1muNepAxqI98W/7cfrC9hyIaARJ+1aOBqFtXYSN+BEM1Cftluj4XDUak/rvajVHA6braiXI6DoERD0jFkIqFT+moC7fwIyweFk"
						+ "N2wOBs1wd3IYSAEYqCfjcDgYDMNxogiAi0dgMQQLCeBUFgLybK3IPpM/GQEHUW00uHNnMKpFBwHoBrICskdAGvgzBC3mup2AvA+vCI8xClANgHoEMkMQpAJ5Ddrc7DYCGFtTgMJOHoGLLaAOwcw1qOWyBHge"
						+ "JYDGz4lpCGoGlGsQF6F/J8DnWVmALL6aezymazDPAC5CmMsXgJ/2vBUF6OQY0yIEFxUoq7ALAfa3AFZfg8cYVmEM8hfMhg1BtfJ6ch9DFtnwISiqb8Z/wAM2KZ0AE/6DRcolgFiEiPZX4TdEgOddrgCsfx79"
						+ "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");
		final String txt_assets_loon_natural_txt = ("<?xml version=\"1.0\" standalone=\"yes\" ?>φ"
				+ "<pack file=\"assets/loon_natural.png\">φ"
				+ "<block name=\"loon_rain_0\" left=\"1\" top=\"0\" right=\"22\" bottom=\"259\" />φ"
				+ "<block name=\"loon_rain_2\" left=\"24\" top=\"0\" right=\"46\" bottom=\"207\" />φ"
				+ "<block name=\"loon_rain_1\" left=\"48\" top=\"0\" right=\"69\" bottom=\"206\" />φ"
				+ "<block name=\"loon_rain_3\" left=\"136\" top=\"0\" right=\"143\" bottom=\"122\" />φ"
				+ "<block name=\"loon_snow_4\" left=\"144\" top=\"0\" right=\"157\" bottom=\"14\" />φ"
				+ "<block name=\"loon_snow_3\" left=\"178\" top=\"0\" right=\"189\" bottom=\"12\" />φ"
				+ "<block name=\"loon_snow_2\" left=\"189\" top=\"0\" right=\"199\" bottom=\"10\" />φ"
				+ "<block name=\"loon_snow_1\" left=\"210\" top=\"0\" right=\"218\" bottom=\"8\" />φ"
				+ "<block name=\"loon_snow_0\" left=\"218\" top=\"0\" right=\"224\" bottom=\"6\" />φ"
				+ "<block name=\"loon_sakura_1\" left=\"199\" top=\"0\" right=\"210\" bottom=\"8\" />φ"
				+ "<block name=\"loon_sakura_0\" left=\"158\" top=\"0\" right=\"177\" bottom=\"13\" />φ"
				+ "<block name=\"loon_pixel\" left=\"224\" top=\"0\" right=\"225\" bottom=\"1\" />φ"
				+ "<block name=\"loon_halfcircle\" left=\"70\" top=\"0\" right=\"134\" bottom=\"128\" />φ"
				+ "<block name=\"loon_lightning\" left=\"134\" top=\"0\" right=\"135\" bottom=\"128\" />φ" + "</pack>φ")
				.replace('φ', '\n');
		putText("assets/loon_natural.txt", txt_assets_loon_natural_txt);
		putImage("assets/loon_pad_ui.png",
				"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"
						+ "QmCC");
		final String txt_assets_loon_pad_ui_txt = ("<?xml version=\"1.0\" standalone=\"yes\"?>φ"
				+ "<pack file=\"assets/loon_pad_ui.png\">φ"
				+ "<block id=\"0\" name=\"back\" left=\"0\" top=\"0\" right=\"116\" bottom=\"116\" />φ"
				+ "<block id=\"1\" name=\"fore\" left=\"116\" top=\"0\" right=\"222\" bottom=\"106\" />φ"
				+ "<block id=\"2\" name=\"dot\" left=\"0\" top=\"116\" right=\"48\" bottom=\"164\" />φ" + "</pack>φ")
				.replace('φ', '\n');
		putText("assets/loon_pad_ui.txt", txt_assets_loon_pad_ui_txt);
		putImage("assets/loon_par.png",
				"iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAFgElEQVR42tXXefTlcx3HcT8Z25RU1mizVMdykKWyJJUlRBtKckpFEVFkpCRLK1nCjzqdUNFKiRyh055k"
						+ "ObSiTo6ytBjEIBn1fIx7f+f+ZjnTTP7x/Of+7p253/f7/Xq/36/P5y6yyOOFsVg8nhhPiafFSrHiAO+fFEvEE+IxDbxUPDWeFc+P9WLD2Dw2i03jxfGCeF6sGsvEYvF/"
						+ "BV80Vo61YqMQbOvYMXaON8Ru8fp4Tfj85SGx9eMZQRXPWeDg5PaATeJl8aoQ7C2xT7w7DoqDw+v+4fM9Q0KvjC1i7VguFqgtS8ca8cJQlUrfHgfGB+Po+GgcN8In4iNx"
						+ "WEhGolShiNasEFNivsEN0ZqhpzvFXiHwh0OQU+NzcXZ8ecAX48z4bJwcH49p8c7YNbaLDWK+SuiVAVK57N8Wh8YxcUqcFV+P78TFcemA78V349vxlfh8fDqOCK2SxCvC"
						+ "LBnouc6ED1cJPd8+3hqqUM0Z4cEXxg/i5/HLuCquDq9Xxk9DQt8KqpwUlHtX7BJUfWZIYq59l6FMDdL7QnCyfiNU+bO4Nn4bN8UfRvh9XB8Sk6RktemE+FCYIfNkm3jG"
						+ "pFbYdesmw1eHjI8Msqv8kvhF/Dr+GH+O2+OvA+6IW+NP8bu4Jn4UF8QXwuy8NwzzS+M5YdYmrZzBs257xCFxfKhAJSoXXACB74x74p9xb/j7rpCM5G4ISXw/qKeQoQrW"
						+ "c914ckzMArNgHHr/jtA30/618BCyq1zw6XFfPBAPjnB/SORvcUtQ4oq4KKignQfEa8OQs+8Jp9STjcPk7xd23KqRUPV6rrJ/hOD/in/HwyM8FJK4O7REwhK/PM4Ns0DZ"
						+ "N8aWYRgpPysBJqH/r4v3xCfDXlst031jqN7DVS74zHhkBElITIISpYK2/TCsrjZ8IN4cElgtloxZG+hU4/P21fQP+2/4rJgpV5Wek1swQf8zgoSoMCPMCMV+Ez+J8+P0"
						+ "4Asckjty2uE6LikBBwivlwATscdWz55LwIBJQJXzSoAyEjAnfwkJ8AYJnBYGkbNuExQYJjCVRVLAIWJdePuXYnYFDNncFPD3UAEtGCqgBT+O88JQO0cowJpXjwkFDKHJ"
						+ "NAMOEhNrBtitGWAy9tyqGTSBJDFzBNVLzlr+PazsdcGUbJMzgq3vHtb92TGcgTGXB2vo8HGAONXYL0vVQw7ngdpABUloxUMjCK568t8WBpcrUlE7PxWObZbMjBjfxBbI"
						+ "5LmhNyRypJ4Y1oe3exAVTLbqbINKZ4xgPgTXKsn+KsjvgLLSjnDF8QG3KTesCR/wB2MwBwaRYRwbTjUqWCU7zeEkYSUlotfTBzCgoR3rPRNS/TlhqMlvBbcNxbpbTjoV"
						+ "WSOL5IYm9fCgwtCOebuNMNmG8uaQjGn36j3Z9V1wyum99dPS4YmoSPJPOguGKnAn60gmX7C3jlQbwRXZsodTg8QcUkIqFtjGkF3lgpP+Y2GzOKDqXWpZ/+A0HJuUhLVw"
						+ "q90qmJKNkAQl+Hnnyvm8/bKgiGCG1Ktp5xt67pakcsHfH+bKndLVTKvnqH70UiK7dcK9wDzsG9rBnsfHx8/Qkq/GN4PBmBGvKjbtqqbaUaFywXeIF4Xb1jxvRENIs2xI"
						+ "ghLMae+wQsN7IUU+EyU0rlImY88ZmGnn+Q41Oz8M7g4wNf6n27F5MJSm1c56CDUc1RJpoKdxNZcWSWmToK7oArvOMTUqkl3lgi/QD5WhEn4NMSl3fENkkt8Ugri0SsrW"
						+ "uMYZNDcq/0/VBm75IPtC/VzTKwNjZ11YJWKNqKI6xiWYV+9fEu58bldPD9/z/YX6ZTS7Gh6kLQ4ukq72KM6U1fk6pZyqHG4Y+DH9kTpQZIo+8nD2vdQI3vvcvw8qHptj"
						+ "z+fCfwHtJD16mHL13wAAAABJRU5ErkJggg==");
		putImage("assets/loon_ui.png",
				"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");
		putImage("assets/loon_wbar.png",
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
		texts.put(url, context.replace(DefaultAssetFilter.special_symbols, '\n'));
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
