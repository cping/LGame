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
package loon.jni;

import com.google.gwt.core.client.JavaScriptObject;

public final class XDomainRequest extends JavaScriptObject {

  public static interface Handler {
    void onError(XDomainRequest xdr);

    void onLoad(XDomainRequest xdr);

    void onProgress(XDomainRequest xdr);

    void onTimeout(XDomainRequest xdr);
  }

  public static native XDomainRequest create() /*-{
    return new $wnd.XDomainRequest();
  }-*/;

  protected XDomainRequest() {
  }

  public native void abort() /*-{
    this.abort();
  }-*/;

  public native String getResponseText() /*-{
    return this.responseText;
  }-*/;

  public native String getStatus() /*-{
    return this.contentType;
  }-*/;

  public native int getTimeout() /*-{
    return this.timeout;
  }-*/;


  public native void open(String httpMethod, String url) /*-{
    this.open(httpMethod, url, true);
  }-*/;

  public native void send() /*-{
    this.send();
  }-*/;

  public native void send(String requestData) /*-{
    this.send(requestData);
  }-*/;

  public native void setHandler(Handler handler) /*-{

    var _this = this;

    this.onerror = $entry(function() {
      handler.@loon.jni.XDomainRequest.Handler::onError(Lloon/jni/XDomainRequest;)(_this);
    });

    this.onload = $entry(function() {
      handler.@loon.jni.XDomainRequest.Handler::onLoad(Lloon/jni/XDomainRequest;)(_this);
    });

    this.onprogress = $entry(function() {
      handler.@loon.jni.XDomainRequest.Handler::onProgress(Lloon/jni/XDomainRequest;)(_this);
    });

    this.ontimeout = $entry(function() {
      handler.@loon.jni.XDomainRequest.Handler::onTimeout(Lloon/jni/XDomainRequest;)(_this);
    });
  }-*/;
}
