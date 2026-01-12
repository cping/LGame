/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
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
package loon.cport.bridge;

import org.teavm.backend.c.intrinsic.RuntimeInclude;
import org.teavm.interop.Import;

public final class SocketCall {

	private SocketCall() {
		importInclude();
	}

	@RuntimeInclude("SocketSupport.h")
	@Import(name = "ImportSocketInclude")
	public final static native void importInclude();

	@Import(name = "Load_Socket_Init")
	public final static native int socketInit();

	@Import(name = "Load_Socket_Free")
	public final static native int socketFree();

	@Import(name = "Load_Socket_Close")
	public final static native int socketClose(int socket);

	@Import(name = "Load_Socket_Send")
	public final static native int socketSend(int sock, char[] msg, int flags);

	@Import(name = "Load_Socket_Recv")
	public final static native int socketRecv(int sock, char[] msg, int bufsize);

	@Import(name = "Load_Socket_FirstIP")
	public final static native void socketFirstIP(char[] outips, int outsize, int preferipv6);

	@Import(name = "Load_Create_Server")
	public final static native int createServer(int port);

	@Import(name = "Load_Create_Client")
	public final static native int createClient(String ip, int port);

	@Import(name = "Load_Create_LinkServerToClient")
	public final static native int linkServerToClient(int server);

}
