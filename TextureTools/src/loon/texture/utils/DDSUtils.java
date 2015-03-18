package loon.texture.utils;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVListImpl;
import gov.nasa.worldwind.data.MipMappedBufferedImageRaster;
import gov.nasa.worldwind.exception.WWRuntimeException;
import gov.nasa.worldwind.formats.dds.DDSCompressor;
import gov.nasa.worldwind.formats.dds.DDSConstants;
import gov.nasa.worldwind.formats.dds.DDSConverter;
import gov.nasa.worldwind.formats.dds.DDSHeader;
import gov.nasa.worldwind.formats.dds.DDSPixelFormat;
import gov.nasa.worldwind.formats.dds.DXT1Compressor;
import gov.nasa.worldwind.formats.dds.DXT1Decompressor;
import gov.nasa.worldwind.formats.dds.DXT3Decompressor;
import gov.nasa.worldwind.formats.dds.DXTCompressionAttributes;
import gov.nasa.worldwind.formats.dds.DXTCompressor;
import gov.nasa.worldwind.formats.dds.DXTDecompressor;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.util.WWIO;
import gov.nasa.worldwind.util.WWMath;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class DDSUtils {

	public static void createImageToDDS(File source, File dds)
			throws IOException {
		create(source, dds);
	}

	private static void create(File source, File dds) throws IOException {
		BufferedImage image = ImageIO.read(source);
		create(image, dds);
	}

	public static void create(BufferedImage image, File dds) throws IOException {
		createDXT1(image, dds);
	}

	public static void createDXT3(BufferedImage image, File dds)
			throws IOException {
		ByteBuffer buffer = DDSConverter.convertToDxt3(image);
		OutputStream outputStream = new FileOutputStream(dds);
		outputStream.write(buffer.array());
		outputStream.close();
	}

	public static void createDXT1(BufferedImage image, File dds)
			throws IOException {
		DXTCompressionAttributes attributes = new DXTCompressionAttributes();
		attributes.setEnableDXT1Alpha(true);
		DXT1Compressor compressor = new DXT1Compressor();
		DDSHeader header = createDDSHeader(compressor, image, attributes);
		int fileSize = 4 + header.getSize();
		fileSize += compressor.getCompressedSize(image, attributes);
		ByteBuffer buffer = ByteBuffer.allocate(fileSize);
		buffer.order(java.nio.ByteOrder.LITTLE_ENDIAN);
		buffer.putInt(DDSConstants.MAGIC);
		writeDDSHeader(header, buffer);
		compressor.compressImage(image, attributes, buffer);
		OutputStream outputStream = new FileOutputStream(dds);
		outputStream.write(buffer.array());
		outputStream.close();
	}

	protected static DDSHeader createDDSHeader(DXTCompressor compressor,
			java.awt.image.BufferedImage image,
			DXTCompressionAttributes attributes) {
		DDSPixelFormat pixelFormat = new DDSPixelFormat();
		pixelFormat.setFlags(pixelFormat.getFlags() | DDSConstants.DDPF_FOURCC);
		pixelFormat.setFourCC(compressor.getDXTFormat());
		DDSHeader header = new DDSHeader();
		header.setFlags(header.getFlags() | DDSConstants.DDSD_WIDTH
				| DDSConstants.DDSD_HEIGHT | DDSConstants.DDSD_LINEARSIZE
				| DDSConstants.DDSD_PIXELFORMAT | DDSConstants.DDSD_CAPS);
		header.setWidth(image.getWidth());
		header.setHeight(image.getHeight());
		header.setLinearSize(compressor.getCompressedSize(image, attributes));
		header.setPixelFormat(pixelFormat);
		header.setCaps(header.getCaps() | DDSConstants.DDSCAPS_TEXTURE);

		return header;
	}

	protected static void writeDDSHeader(DDSHeader header,
			java.nio.ByteBuffer buffer) {
		int pos = buffer.position();

		buffer.putInt(header.getSize());
		buffer.putInt(header.getFlags());
		buffer.putInt(header.getHeight());
		buffer.putInt(header.getWidth());
		buffer.putInt(header.getLinearSize());
		buffer.putInt(header.getDepth());
		buffer.putInt(header.getMipMapCount());
		buffer.position(buffer.position() + 44);
		writeDDSPixelFormat(header.getPixelFormat(), buffer);
		buffer.putInt(header.getCaps());
		buffer.putInt(header.getCaps2());
		buffer.putInt(header.getCaps3());
		buffer.putInt(header.getCaps4());
		buffer.position(buffer.position() + 4);

		buffer.position(pos + header.getSize());
	}

	protected static void writeDDSPixelFormat(DDSPixelFormat pixelFormat,
			java.nio.ByteBuffer buffer) {
		int pos = buffer.position();

		buffer.putInt(pixelFormat.getSize());
		buffer.putInt(pixelFormat.getFlags());
		buffer.putInt(pixelFormat.getFourCC());
		buffer.putInt(pixelFormat.getRGBBitCount());
		buffer.putInt(pixelFormat.getRBitMask());
		buffer.putInt(pixelFormat.getGBitMask());
		buffer.putInt(pixelFormat.getBBitMask());
		buffer.putInt(pixelFormat.getABitMask());

		buffer.position(pos + pixelFormat.getSize());
	}

	public static void createImageToDDS(File file) throws IOException {
		if (file == null) {
			String message = Logging.getMessage("nullValue.FileIsNull");
			Logging.logger().severe(message);
			throw new IllegalArgumentException(message);
		}

		if (!file.exists() || !file.canRead()) {
			String message = Logging
					.getMessage("DDSConverter.NoFileOrNoPermission");
			Logging.logger().severe(message);
			throw new IllegalArgumentException(message);
		}

		if (file.isDirectory()) {
			createDDSDirectory(file, new String[] { "jpg", "png", "bmp", "gif" });
		} else {
			createDDSFile(file);
		}
	}
	
	public static void createDDSToImage(File file) throws Exception {
		if (file == null) {
			String message = Logging.getMessage("nullValue.FileIsNull");
			Logging.logger().severe(message);
			throw new IllegalArgumentException(message);
		}

		if (!file.exists() || !file.canRead()) {
			String message = Logging
					.getMessage("DDSConverter.NoFileOrNoPermission");
			Logging.logger().severe(message);
			throw new IllegalArgumentException(message);
		}

		if (file.isDirectory()) {
			createImageDirectory(file, new String[] { "dds" });
		} else {
			createImageFile(file);
		}
	}
	
	public static void createImageFile(File source) throws Exception {
		AVListImpl params = new AVListImpl();
		params.setValue(AVKey.SECTOR, Sector.FULL_SPHERE);

		if (null == params || !params.hasKey(AVKey.SECTOR)) {
			String message = Logging.getMessage(
					"generic.MissingRequiredParameter", AVKey.SECTOR);
			Logging.logger().severe(message);
			throw new WWRuntimeException(message);
		}

		File file = WWIO.getFileForLocalAddress(source);
		if (null == file) {
			String message = Logging.getMessage(
					"generic.UnrecognizedSourceType", source.getClass()
							.getName());
			Logging.logger().severe(message);
			throw new IllegalArgumentException(message);
		}

		if (!file.exists()) {
			String message = Logging.getMessage("generic.FileNotFound",
					file.getAbsolutePath());
			Logging.logger().severe(message);
			throw new FileNotFoundException(message);
		}

		if (!file.canRead()) {
			String message = Logging.getMessage("generic.FileNoReadPermission",
					file.getAbsolutePath());
			Logging.logger().severe(message);
			throw new IOException(message);
		}

		RandomAccessFile raf = null;
		FileChannel channel = null;

		try {
			raf = new RandomAccessFile(file, "r");
			channel = raf.getChannel();

			java.nio.MappedByteBuffer buffer = mapFile(channel, 0,
					channel.size());

			buffer.position(0);
			DDSHeader header = DDSHeader.readFrom(source);

			int width = header.getWidth();
			int height = header.getHeight();

			if (!WWMath.isPowerOfTwo(width) || !WWMath.isPowerOfTwo(height)) {
				String message = Logging.getMessage("generic.InvalidImageSize",
						width, height);
				Logging.logger().severe(message);
				throw new WWRuntimeException(message);
			}

			int mipMapCount = header.getMipMapCount();
			// int ddsFlags = header.getFlags();

			DDSPixelFormat pixelFormat = header.getPixelFormat();
			if (null == pixelFormat) {
				String reason = Logging.getMessage(
						"generic.MissingRequiredParameter", "DDSD_PIXELFORMAT");
				String message = Logging.getMessage(
						"generic.InvalidImageFormat", reason);
				Logging.logger().severe(message);
				throw new WWRuntimeException(message);
			}

			DXTDecompressor decompressor = null;

			int dxtFormat = pixelFormat.getFourCC();
			if (dxtFormat == DDSConstants.D3DFMT_DXT3) {
				decompressor = new DXT3Decompressor();
			} else if (dxtFormat == DDSConstants.D3DFMT_DXT1) {
				decompressor = new DXT1Decompressor();
			}

			if (null == decompressor) {
				String message = Logging.getMessage("generic.UnsupportedCodec",
						dxtFormat);
				Logging.logger().severe(message);
				throw new WWRuntimeException(message);
			}

			Sector sector = (Sector) params.getValue(AVKey.SECTOR);
			params.setValue(AVKey.PIXEL_FORMAT, AVKey.IMAGE);

			if (mipMapCount == 0) {
				buffer.position(DDSConstants.DDS_DATA_OFFSET);
				BufferedImage image = decompressor.decompress(buffer,
						header.getWidth(), header.getHeight());
				File newFile = new File(WWIO.replaceSuffix(file.getPath(),
						".png"));
				ImageIO.write(image, "png", newFile);
			} else if (mipMapCount > 0) {
				ArrayList<BufferedImage> list = new ArrayList<BufferedImage>();

				int mmLength = header.getLinearSize();
				int mmOffset = DDSConstants.DDS_DATA_OFFSET;

				for (int i = 0; i < mipMapCount; i++) {
					int zoomOut = (int) Math.pow(2d, (double) i);

					int mmWidth = header.getWidth() / zoomOut;
					int mmHeight = header.getHeight() / zoomOut;

					if (mmWidth < 4 || mmHeight < 4) {
						break;
					}

					buffer.position(mmOffset);
					BufferedImage image = decompressor.decompress(buffer,
							mmWidth, mmHeight);
					list.add(image);

					mmOffset += mmLength;
					mmLength /= 4;
				}

				BufferedImage[] images = new BufferedImage[list.size()];
				images = (BufferedImage[]) list.toArray(images);

				MipMappedBufferedImageRaster raster = new MipMappedBufferedImageRaster(
						sector, images);

				File newFile = new File(WWIO.replaceSuffix(file.getPath(),
						".png"));
				ImageIO.write(raster.getBufferedImage(), "png", newFile);
			}

		} finally {
			String name = (null != file) ? file.getAbsolutePath()
					: ((null != source) ? source.toString() : "unknown");
			WWIO.closeStream(channel, name);
			WWIO.closeStream(raf, name);
		}

	}

	private static java.nio.MappedByteBuffer mapFile(FileChannel channel,
			long offset, long length) throws Exception {
		if (null == channel || !channel.isOpen()) {
			String message = Logging.getMessage("nullValue.ChannelIsNull");
			Logging.logger().fine(message);
			throw new IllegalArgumentException(message);
		}

		if (channel.size() < (offset + length)) {
			String reason = channel.size() + " < " + (offset + length);
			String message = Logging.getMessage("generic.LengthIsInvalid",
					reason);
			Logging.logger().severe(message);
			throw new IOException(message);
		}

		return channel.map(FileChannel.MapMode.READ_ONLY, offset, length);
	}

	private static void createDDSDirectory(File dir, final String[] suffixes) {
		System.out.printf("Converting %s\n", dir.getPath());
		File[] files = dir.listFiles(new FileFilter() {
			public boolean accept(File file) {
				for (String suffix : suffixes) {
					if (file.getPath().endsWith(suffix))
						return true;
				}

				return false;
			}
		});

		if (files != null) {
			for (File file : files) {
				try {
					createDDSFile(file);
				} catch (Exception e) {
					System.out.printf(
							"Exception converting %s, skipping file\n",
							file.getPath());
					e.printStackTrace();
				}
			}
		}

		File[] directories = dir.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return file.isDirectory();
			}
		});

		if (directories != null) {
			for (File directory : directories) {
				createDDSDirectory(directory, suffixes);
			}
		}
	}
	
	private static void createImageDirectory(File dir, final String[] suffixes) {
		System.out.printf("Converting %s\n", dir.getPath());
		File[] files = dir.listFiles(new FileFilter() {
			public boolean accept(File file) {
				for (String suffix : suffixes) {
					if (file.getPath().endsWith(suffix))
						return true;
				}

				return false;
			}
		});

		if (files != null) {
			for (File file : files) {
				try {
					createImageFile(file);
				} catch (Exception e) {
					System.out.printf(
							"Exception converting %s, skipping file\n",
							file.getPath());
					e.printStackTrace();
				}
			}
		}

		File[] directories = dir.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return file.isDirectory();
			}
		});

		if (directories != null) {
			for (File directory : directories) {
				createImageDirectory(directory, suffixes);
			}
		}
	}

	private static void createDDSFile(File file) throws IOException {
		System.out.printf("Converting %s\n", file.getPath());
		ByteBuffer buffer = DDSCompressor.compressImageFile(file);
		File newFile = new File(WWIO.replaceSuffix(file.getPath(), ".dds"));
		WWIO.saveBuffer(buffer, newFile);
	}

}
