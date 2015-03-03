package loon.net;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
/**
 * 
 * Copyright 2008 - 2009
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 *
 * @project loonframework
 * @author chenpeng  
 * @email：ceponline@yahoo.com.cn 
 * @version 0.1
 */
public class HttpRequest {
	
	public HttpRequest() {
	}

	public void getRequest(Socket socket, String data) {
		try {
			DataOutputStream dos = new DataOutputStream(socket
					.getOutputStream());
			dos.writeBytes(data);
			dos.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public BufferedInputStream getBufferedInputStream(Socket socket) {
		BufferedInputStream bis = null;
		try {
			bis = new BufferedInputStream(socket.getInputStream());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return bis;
	}
}
