package loon.utils.json;

import loon.html5.gwt.GWTJson;

class JsonTypes {
	
  public static boolean isArray(Object o) {
    return GWTJson.isObjectAnArray(o);
  }

  public static boolean isObject(Object o) {
    return GWTJson.isObjectAnObject(o);
  }
}
