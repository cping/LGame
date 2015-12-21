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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import loon.LSystem;
import loon.utils.reply.Function;
import loon.utils.reply.GoFuture;

public abstract class RoboVMAbstractNet {

	  public static class HttpException extends IOException {
	    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public final int errorCode;

	    public HttpException(int errorCode, String message) {
	      super(message);
	      this.errorCode = errorCode;
	    }

	    @Override
	    public String toString() {
	      String msg = getLocalizedMessage();
	      return "HTTP " + errorCode + (msg == null ? "" : (": " + msg));
	    }
	  }

	  public static class Header {
	    public final String name, value;

	    public Header(String name, String value) {
	      this.name = name;
	      this.value = value;
	    }
	  }

	  public class Builder {
	    public final String url;
	    public final List<Header> headers = new ArrayList<Header>();
	    public String contentType = "text/plain";
	    public String payloadString;
	    public byte[] payloadBytes;

	    public Builder setPayload(String payload) {
	      return setPayload(payload, "text/plain");
	    }

	    public Builder setPayload(String payload, String contentType) {
	      this.payloadString = payload;
	      this.contentType = contentType;
	      return this;
	    }

	    public Builder setPayload(byte[] payload) {
	      return setPayload(payload, "application/octet-stream");
	    }

	    public Builder setPayload(byte[] payload, String contentType) {
	      this.payloadBytes = payload;
	      this.contentType = contentType;
	      return this;
	    }

	    public Builder addHeader(String name, String value) {
	      headers.add(new Header(name, value));
	      return this;
	    }

	    public GoFuture<Response> execute() {
	      return RoboVMAbstractNet.this.execute(this);
	    }

	    public boolean isPost() {
	      return payloadString != null || payloadBytes != null;
	    }

	    public String method() {
	      return isPost() ? "POST" : "GET";
	    }

	    public String contentType() {
	      return contentType + (payloadString != null ? ("; charset=" + LSystem.ENCODING) : "");
	    }

	    protected Builder(String url) {
	      assert url.startsWith("http:") || url.startsWith("https:") :
	        "Only http and https URLs are supported";
	      this.url = url;
	    }
	  }

	  public static abstract class Response {
	    private int responseCode;
	    private Map<String,List<String>> headersMap;

	    public static abstract class Binary extends Response {
	      private final byte[] payload;
	      private final String encoding;

	      public Binary(int responseCode, byte[] payload, String encoding) {
	        super(responseCode);
	        this.payload = payload;
	        this.encoding = encoding;
	      }

	      @Override public String payloadString() {
	        try {
	          return new String(payload, encoding);
	        } catch (UnsupportedEncodingException uee) {
	          return uee.toString();
	        }
	      }

	      @Override public byte[] payload() {
	        return payload;
	      }
	    }

	    public int responseCode() {
	      return this.responseCode;
	    }

	    public Iterable<String> headerNames() {
	      return headers().keySet();
	    }

	    public String header(String name) {
	      List<String> values = headers().get(name);
	      return (values == null) ? null : values.get(0);
	    }

	    public List<String> headers(String name) {
	      List<String> values = headers().get(name);
	      return values == null ? Collections.<String>emptyList() : values;
	    }

	    public abstract String payloadString();

	    public byte[] payload() {
	      throw new UnsupportedOperationException();
	    }

	    protected Response(int responseCode) {
	      this.responseCode = responseCode;
	    }

	    protected abstract Map<String,List<String>> extractHeaders();

	    private Map<String,List<String>> headers() {
	      if (headersMap == null) {
	        headersMap = extractHeaders();
	      }
	      return headersMap;
	    }
	  }

	  public GoFuture<String> get(String url) {
	    return req(url).execute().map(GET_PAYLOAD);
	  }

	  public GoFuture<String> post(String url, String data) {
	    return req(url).setPayload(data).execute().map(GET_PAYLOAD);
	  }

	  public Builder req (String url) {
	    return new Builder(url);
	  }

	  protected GoFuture<Response> execute(Builder req) {
	    return GoFuture.failure(new UnsupportedOperationException());
	  }

	  private static final Function<Response,String> GET_PAYLOAD = new Function<Response,String>() {
	    public String apply (Response rsp) { return rsp.payloadString(); }
	  };
}
