package org.loon.framework.javase.game.core.resource;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigInteger;

/**
 * Copyright 2008 - 2009
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
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1
 */
public class LRAFile extends RandomAccessFile {

	final static int LogBuffSz = 13;

	final static public int BuffSz = (1 << LogBuffSz);

	final static int BuffMask = ~(BuffSz - 1);

	private boolean dirty;

	private boolean closed;

	private long curr;

	private long lo, hi;

	private byte[] buff;

	private long maxHi;

	private boolean hitEOF;

	private long diskPos;

	private static Object mu = new Object();

	private static byte[][] availBuffs = new byte[100][];

	private static int numAvailBuffs = 0;

	public LRAFile(File file, String mode) throws IOException {
		super(file, mode);
		this.init();
	}

	public LRAFile(String name, String mode) throws IOException {
		super(name, mode);
		this.init();
	}

	private void init() {
		this.dirty = this.closed = false;
		this.lo = this.curr = this.hi = 0;
		synchronized (mu) {
			this.buff = (numAvailBuffs > 0) ? availBuffs[--numAvailBuffs]
					: new byte[BuffSz];
		}
		this.maxHi = BuffSz;
		this.hitEOF = false;
		this.diskPos = 0L;
	}

	public void close() throws IOException {
		this.flush();
		this.closed = true;
		synchronized (mu) {
			if (numAvailBuffs >= availBuffs.length) {
				byte[][] newBuffs = new byte[numAvailBuffs + 10][];
				System.arraycopy(availBuffs, 0, newBuffs, 0, numAvailBuffs);
				availBuffs = newBuffs;
			}
			availBuffs[numAvailBuffs++] = this.buff;
		}
		super.close();
	}

	public void flush() throws IOException {
		this.flushBuffer();
	}

	private void flushBuffer() throws IOException {
		if (this.dirty) {
			if (this.diskPos != this.lo)
				super.seek(this.lo);
			int len = (int) (this.curr - this.lo);
			super.write(this.buff, 0, len);
			this.diskPos = this.curr;
			this.dirty = false;
		}
	}

	private int fillBuffer() throws IOException {
		int cnt = 0;
		int rem = this.buff.length;
		while (rem > 0) {
			int n = super.read(this.buff, cnt, rem);
			if (n < 0)
				break;
			cnt += n;
			rem -= n;
		}
		this.hitEOF = (cnt < this.buff.length);
		this.diskPos += cnt;
		return cnt;
	}

	public void seek(long pos) throws IOException {
		if (pos >= this.hi || pos < this.lo) {
			this.flushBuffer();
			this.lo = pos & BuffMask;
			this.maxHi = this.lo + this.buff.length;
			if (this.diskPos != this.lo) {
				super.seek(this.lo);
				this.diskPos = this.lo;
			}
			int n = this.fillBuffer();
			this.hi = this.lo + n;
		} else {
			if (pos < this.curr) {
				this.flushBuffer();
			}
		}
		this.curr = pos;
	}

	public long getFilePointer() {
		return this.curr;
	}

	public long length() throws IOException {
		return Math.max(this.curr, super.length());
	}

	public int read() throws IOException {
		if (this.curr == this.hi) {
			if (this.hitEOF)
				return -1;
			this.seek(this.curr);
			if (this.curr == this.hi)
				return -1;
		}
		byte res = this.buff[(int) (this.curr - this.lo)];
		this.curr++;
		return ((int) res) & 0xFF;
	}

	public int read(byte[] b) throws IOException {
		return this.read(b, 0, b.length);
	}

	public int read(byte[] b, int off, int len) throws IOException {
		if (this.curr == this.hi) {
			if (this.hitEOF)
				return -1;
			this.seek(this.curr);
			if (this.curr == this.hi)
				return -1;
		}
		len = Math.min(len, (int) (this.hi - this.curr));
		int buffOff = (int) (this.curr - this.lo);
		System.arraycopy(this.buff, buffOff, b, off, len);
		this.curr += len;
		return len;
	}

	public BigInteger readBigInteger(int size) throws IOException {
		byte[] b = new byte[size];
		return new BigInteger(b);
	}

	public final int readNat() throws IOException {
		int res = this.readShort();
		if (res >= 0)
			return res;
		res = (res << 16) | (this.readShort() & 0xffff);
		return -res;
	}

	public final long readLongNat() throws IOException {
		long res = this.readInt();
		if (res >= 0)
			return res;
		res = (res << 32) | ((long) this.readInt() & 0xffffffffL);
		return -res;
	}

	public void write(int b) throws IOException {
		if (this.curr == this.hi) {
			if (this.hitEOF && this.hi < this.maxHi) {
				this.hi++;
			} else {
				this.seek(this.curr);
				if (this.curr == this.hi) {
					this.hi++;
				}
			}
		}
		this.buff[(int) (this.curr - this.lo)] = (byte) b;
		this.curr++;
		this.dirty = true;
	}

	public void write(byte[] b) throws IOException {
		this.write(b, 0, b.length);
	}

	public void write(byte[] b, int off, int len) throws IOException {
		while (len > 0) {
			int n = this.writeAtMost(b, off, len);
			off += n;
			len -= n;
		}
		this.dirty = true;
	}

	public void writeBigInteger(BigInteger bi, int size) throws IOException {
		byte[] b = bi.toByteArray();
		this.write(b, 0, size);
	}

	public final void writeNat(int x) throws IOException {
		if (x <= 0x7fff) {
			this.writeShort((short) x);
		} else {
			this.writeInt(-x);
		}
	}

	public final void writeLongNat(long x) throws IOException {
		if (x <= 0x7fffffff) {
			this.writeInt((int) x);
		} else {
			this.writeLong(-x);
		}
	}

	/**
	 * 写“len”到“B”的开始位置，在close后返回字节最多写入的字节数。
	 * 
	 * @param b
	 * @param off
	 * @param len
	 * @return
	 * @throws IOException
	 */
	private int writeAtMost(byte[] b, int off, int len) throws IOException {
		if (this.curr == this.hi) {
			if (this.hitEOF && this.hi < this.maxHi) {
				this.hi = this.maxHi;
			} else {
				this.seek(this.curr);
				if (this.curr == this.hi) {
					this.hi = this.maxHi;
				}
			}
		}
		len = Math.min(len, (int) (this.hi - this.curr));
		int buffOff = (int) (this.curr - this.lo);
		System.arraycopy(b, off, this.buff, buffOff, len);
		this.curr += len;
		return len;
	}

	public boolean isClosed() {
		return closed;
	}

}
