/*
 * Copyright 2010 Google Inc.
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
 */
package java.net;

public class InetAddress {

	private byte[] address;

	InetAddress(byte[] address) {
		this.address = address;
	}

	public static InetAddress getByName(String name) throws UnknownHostException {
		if (name == null || "localhost".equals(name)) {
			return new InetAddress(new byte[] { 127, 0, 0, 1 });
		}
		String[] parts = name.split("\\.");

		byte[] parsed = new byte[4];
		try {
			if (parts.length != 4) {
				throw new RuntimeException("4 parts expected");
			}
			for (int i = 0; i < 4; i++) {
				parsed[i] = (byte) Integer.parseInt(parts[i]);
			}
		} catch (Exception e) {
			System.out.println("InetAddress parsing issue: " + e);
			System.out.println("nnn.nnn.nnn.nnn expected; actual: '" + name + "' -- assuming 127.0.0.1");
			parsed = new byte[] { 127, 0, 0, 1 };
		}
		return new InetAddress(parsed);
	}

	public static InetAddress getByAddress(byte[] address) throws UnknownHostException {
		return new InetAddress(address);
	}

	public byte[] getAddress() {
		return address;
	}

	public String getHostAddress() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 4; i++) {
			if (i > 0) {
				sb.append('.');
			}
			sb.append(address[i] & 255);
		}
		return sb.toString();
	}

	public String toString() {
		return "/" + getHostAddress();
	}

}