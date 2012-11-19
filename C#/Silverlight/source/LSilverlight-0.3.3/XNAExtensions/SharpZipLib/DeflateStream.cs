/*
 * DeflateStream.cs - Implementation of the
 *		"System.IO.Compression.DeflateStream" class.
 *
 * Copyright (C) 2004  Southern Storm Software, Pty Ltd.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

namespace System.IO.Compression
{

using System;
using System.IO;
using ICSharpCode.SharpZipLib.Zip.Compression;

public class DeflateStream : Stream
{
	// Internal state.
	private Stream stream;
	private CompressionMode mode;
	private bool leaveOpen;
	private Inflater inflater;
	private Deflater deflater;
	private byte[] buf;

	// Constructors.
	public DeflateStream(Stream stream, CompressionMode mode)
			: this(stream, mode, false) {}
	public DeflateStream(Stream stream, CompressionMode mode, bool leaveOpen)
			{
				if(stream == null)
				{
					throw new ArgumentNullException("stream");
				}
				if(mode == CompressionMode.Decompress)
				{
					if(!stream.CanRead)
					{
						throw new ArgumentException
							("IO_NotReadable", "stream");
					}
				}
				else if(mode == CompressionMode.Compress)
				{
					if(!stream.CanWrite)
					{
						throw new ArgumentException
							("IO_NotWritable", "stream");
					}
				}
				else
				{
					throw new ArgumentException
						("IO_CompressionMode", "mode");
				}
				this.stream = stream;
				this.mode = mode;
				this.leaveOpen = leaveOpen;
				this.buf = new byte [4096];
				if(mode == CompressionMode.Decompress)
				{
					inflater = new Inflater();
				}
				else
				{
					deflater = new Deflater();
				}
			}

	// Get the base stream that underlies this one.
	public Stream BaseStream
			{
				get
				{
					return stream;
				}
			}

	// Determine if the stream supports reading, writing, or seeking.
	public override bool CanRead
			{
				get
				{
					return (stream != null &&
							mode == CompressionMode.Decompress);
				}
			}
	public override bool CanWrite
			{
				get
				{
					return (stream != null &&
							mode == CompressionMode.Compress);
				}
			}
	public override bool CanSeek
			{
				get
				{
					return false;
				}
			}

	// Get the length of the stream.
	public override long Length
			{
				get
				{
					throw new NotSupportedException("IO_NotSupp_Seek");
				}
			}

	// Get or set the current seek position within this stream.
	public override long Position
			{
				get
				{
					throw new NotSupportedException("IO_NotSupp_Seek");
				}
				set
				{
					throw new NotSupportedException("IO_NotSupp_Seek");
				}
			}

	// Begin an asynchronous read operation.
	public override IAsyncResult BeginRead
				(byte[] buffer, int offset, int count,
				 AsyncCallback callback, Object state)
			{
				if(stream == null)
				{
					throw new ObjectDisposedException
						("Exception_Disposed");
				}
				if(mode != CompressionMode.Decompress)
				{
					throw new NotSupportedException("IO_NotSupp_Read");
				}
				return base.BeginRead(buffer, offset, count, callback, state);
			}

	// Wait for an asynchronous read operation to end.
	public override int EndRead(IAsyncResult asyncResult)
			{
				return base.EndRead(asyncResult);
			}

	// Begin an asychronous write operation.
	public override IAsyncResult BeginWrite
				(byte[] buffer, int offset, int count,
				 AsyncCallback callback, Object state)
			{
				if(stream == null)
				{
					throw new ObjectDisposedException
						("Exception_Disposed");
				}
				if(mode != CompressionMode.Compress)
				{
					throw new NotSupportedException("IO_NotSupp_Write");
				}
				return base.BeginWrite(buffer, offset, count, callback, state);
			}

	// Wait for an asynchronous write operation to end.
	public override void EndWrite(IAsyncResult asyncResult)
			{
				base.EndWrite(asyncResult);
			}

	// Close this stream.
	public override void Close()
			{
				if(stream != null)
				{
					if(deflater != null)
					{
						int temp;
						deflater.Finish();
						while(!deflater.IsFinished)
						{
							temp = deflater.Deflate(buf, 0, buf.Length);
							if(temp <= 0)
							{
								if(!deflater.IsFinished)
								{
									throw new IOException
										("IO_Compress_Input");
								}
								break;
							}
							stream.Write(buf, 0, temp);
						}
					}
					if(!leaveOpen)
					{
						stream.Close();
					}
					stream = null;
					inflater = null;
					deflater = null;
					buf = null;
				}
			}

	// Flush this stream.
	public override void Flush()
			{
				if(stream == null)
				{
					throw new ObjectDisposedException
						("Exception_Disposed");
				}
			}

	// Read data from this stream.
	public override int Read(byte[] buffer, int offset, int count)
			{
				int temp;
				if(stream == null)
				{
					throw new ObjectDisposedException
						("Exception_Disposed");
				}
				if(mode != CompressionMode.Decompress)
				{
					throw new NotSupportedException("IO_NotSupp_Read");
				}
				ValidateBuffer(buffer, offset, count);
				for(;;)
				{
					temp = inflater.Inflate(buffer, offset, count);
					if(temp > 0)
					{
						return temp;
					}
					if(inflater.IsNeedingDictionary)
					{
						throw new IOException
							("IO_Decompress_NeedDict");
					}
					else if(inflater.IsFinished)
					{
						return 0;
					}
					else if(inflater.IsNeedingInput)
					{
						temp = stream.Read(buf, 0, buf.Length);
						if(temp <= 0)
						{
							throw new IOException
								("IO_Decompress_Truncated");
						}
						inflater.SetInput(buf, 0, temp);
					}
					else
					{
						throw new IOException
							("IO_Decompress_Invalid");
					}
				}
			}

	// Seek to a new position within this stream.
	public override long Seek(long offset, SeekOrigin origin)
			{
				throw new NotSupportedException("IO_NotSupp_Seek");
			}

	// Set the length of this stream.
	public override void SetLength(long value)
			{
				throw new NotSupportedException("IO_NotSupp_SetLength");
			}

	// Write data to this stream.
	public override void Write(byte[] buffer, int offset, int count)
			{
				int temp;
				if(stream == null)
				{
					throw new ObjectDisposedException
						("Exception_Disposed");
				}
				if(mode != CompressionMode.Compress)
				{
					throw new NotSupportedException("IO_NotSupp_Write");
				}
				ValidateBuffer(buffer, offset, count);
				deflater.SetInput(buffer, offset, count);
				while(!deflater.IsNeedingInput)
				{
					temp = deflater.Deflate(buf, 0, buf.Length);
					if(temp <= 0)
					{
						if(!deflater.IsNeedingInput)
						{
							throw new IOException("IO_Compress_Input");
						}
						break;
					}
					stream.Write(buf, 0, temp);
				}
			}

	// Helper function for validating buffer arguments.
	internal static void ValidateBuffer
				(byte[] buffer, int offset, int count)
			{
				if(buffer == null)
				{
					throw new ArgumentNullException("buffer");
				}
				else if(offset < 0 || offset > buffer.Length)
				{
					throw new ArgumentOutOfRangeException
						("offset", "ArgRange_Array");
				}
				else if(count < 0)
				{
					throw new ArgumentOutOfRangeException
						("count", "ArgRange_Array");
				}
				else if((buffer.Length - offset) < count)
				{
					throw new ArgumentException("Arg_InvalidArrayRange");
				}
			}

}; // class DeflateStream


}; // namespace System.IO.Compression
