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
package loon.utils.qrcode;

import loon.BaseIO;
import loon.LSysException;
import loon.LTexture;
import loon.action.sprite.Picture;
import loon.canvas.Canvas;
import loon.canvas.Image;
import loon.canvas.LColor;
import loon.canvas.Pixmap;
import loon.component.LPaper;
import loon.utils.MathUtils;
import loon.utils.TArray;

public class QRCode {

	public static final int MIN_VERSION = 1;

	public static final int MAX_VERSION = 40;

	private static final int PAD0 = 0xEC;

	private static final int PAD1 = 0x11;

	private int _typeCode;

	private Boolean[][] _modules;

	private int _moduleLength;

	private int _errorLevel;

	private TArray<QRData> _dataList;

	public QRCode() {
		this(1, QRErrorLevel.H);
	}

	public QRCode(int num, int level) {
		this._typeCode = 1;
		this._errorLevel = QRErrorLevel.H;
		this._dataList = new TArray<QRData>(1);
	}

	public LPaper createPaper(int width, int height, LColor color) {
		return createPaper(width, height, color, false);
	}

	public LPaper createPaper(int width, int height, LColor color, boolean trans) {
		return new LPaper(createTexture(width, height, color, trans));
	}

	public Picture createPicture(int width, int height, LColor color) {
		return createPicture(width, height, color, false);
	}

	public Picture createPicture(int width, int height, LColor color, boolean trans) {
		return new Picture(createTexture(width, height, color, trans));
	}

	public LTexture createTexture(int width, int height, LColor color) {
		return createTexture(width, height, color, false);
	}

	public LTexture createTexture(int width, int height, LColor color, boolean trans) {
		return createImage(width, height, color, trans).onHaveToClose(true).texture();
	}

	public Image createImage(int width, int height, LColor color, boolean trans) {
		int count = getModuleCount();
		int newWidth = MathUtils.floor(width / count);
		int newHeight = MathUtils.floor(height / count);
		int curWidth = newWidth * count;
		int curHeight = newHeight * count;
		Canvas canvas = Image.createCanvas(curWidth, curHeight);
		if (!trans) {
			canvas.setColor(LColor.white);
			canvas.fillRect(0, 0, curWidth, curHeight);
		}
		canvas.setColor(color);
		for (int row = 0; row < count; row++) {
			for (int col = 0; col < count; col++) {
				boolean b = isDark(row, col);
				if (b) {
					canvas.fillRect(col * newWidth, row * newHeight, newWidth, newHeight);
				}
			}
		}
		return canvas.image;
	}

	public Pixmap createPixmap(int width, int height, LColor color, boolean trans) {
		int count = getModuleCount();
		int newWidth = MathUtils.floor(width / count);
		int newHeight = MathUtils.floor(height / count);
		int curWidth = newWidth * count;
		int curHeight = newHeight * count;
		Pixmap pixmap = new Pixmap(curWidth, curHeight, true);
		if (!trans) {
			pixmap.setColor(LColor.white);
			pixmap.fillRect(0, 0, curWidth, curHeight);
		}
		pixmap.setColor(color);
		for (int row = 0; row < count; row++) {
			for (int col = 0; col < count; col++) {
				boolean b = isDark(row, col);
				if (b) {
					pixmap.fillRect(col * newWidth, row * newHeight, newWidth, newHeight);
				}
			}
		}
		return pixmap;
	}

	public LPaper createPaper(int width, int height, Image img) {
		return createPaper(width, height, img);
	}

	public LPaper createPaper(int width, int height, String path) {
		return new LPaper(createTexture(width, height, path));
	}

	public Picture createPicture(int width, int height, Image img) {
		return createPicture(width, height, img);
	}

	public Picture createPicture(int width, int height, String path) {
		return new Picture(createTexture(width, height, path));
	}

	public LTexture createTexture(int width, int height, String path) {
		return createPixmap(width, height, BaseIO.loadImage(path).getPixmap()).texture();
	}

	public LTexture createTexture(int width, int height, Image img) {
		return createPixmap(width, height, img.getPixmap(), false).texture();
	}

	public Pixmap createPixmap(int width, int height, String path) {
		return createPixmap(width, height, BaseIO.loadImage(path).getPixmap(), false);
	}

	public LTexture createPixmap(int width, int height, Pixmap backPixmap) {
		return createPixmap(width, height, backPixmap, false).texture();
	}

	public LTexture createPixmap(int width, int height, Image img, boolean trans) {
		return createPixmap(width, height, img.getPixmap(), trans).texture();
	}

	public Pixmap createPixmap(int width, int height, String path, boolean trans) {
		return createPixmap(width, height, BaseIO.loadImage(path).getPixmap(), trans);
	}

	public Pixmap createPixmap(int width, int height, Pixmap backPixmap, boolean trans) {
		int count = getModuleCount();
		int newWidth = MathUtils.floor(width / count);
		int newHeight = MathUtils.floor(height / count);
		int curWidth = newWidth * count;
		int curHeight = newHeight * count;
		Pixmap pixmap = new Pixmap(curWidth, curHeight, false);
		Pixmap newBack = Pixmap.getResize(backPixmap, curWidth, curHeight);
		newBack.filter(LColor.black);
		if (!trans) {
			pixmap.setColor(LColor.white);
			pixmap.fillRect(0, 0, curWidth, curHeight);
		}
		for (int row = 0; row < count; row++) {
			for (int col = 0; col < count; col++) {
				boolean b = isDark(row, col);
				if (b) {
					pixmap.drawPixmap(newBack, col * newWidth, row * newHeight, newWidth, newHeight, col * newWidth,
							row * newHeight, newWidth, newHeight);
				}
			}
		}
		return pixmap;
	}

	public LPaper createPaperLogo(int width, int height, String path, LColor color) {
		return new LPaper(createTextureLogo(width, height, path, color));
	}

	public LPaper createPaperLogo(int width, int height, Image img, LColor color) {
		return new LPaper(createTextureLogo(width, height, img, color));
	}

	public Picture createPictureLogo(int width, int height, String path, LColor color) {
		return new Picture(createTextureLogo(width, height, path, color));
	}

	public Picture createPictureLogo(int width, int height, Image img, LColor color) {
		return new Picture(createTextureLogo(width, height, img, color));
	}

	public LTexture createTextureLogo(int width, int height, String path, LColor color) {
		return createTextureLogo(width, height, path, color, false);
	}

	public LTexture createTextureLogo(int width, int height, Image img, LColor color) {
		return createTextureLogo(width, height, img, color, false);
	}

	public LTexture createTextureLogo(int width, int height, String path, LColor color, boolean trans) {
		return createPixmapLogo(width, height, path, color, trans).texture();
	}

	public LTexture createTextureLogo(int width, int height, Image img, LColor color, boolean trans) {
		return createPixmapLogo(width, height, img, color, trans).texture();
	}

	public Pixmap createPixmapLogo(int width, int height, String path, LColor color, boolean trans) {
		return createPixmapLogo(width, height, BaseIO.loadImage(path).getPixmap(), color, trans);
	}

	public Pixmap createPixmapLogo(int width, int height, Image img, LColor color, boolean trans) {
		return createPixmapLogo(width, height, img.getPixmap(), color, trans);
	}

	public Pixmap createPixmapLogo(int width, int height, Pixmap logoPixmap, LColor color, boolean trans) {
		int count = getModuleCount();
		int newWidth = MathUtils.floor(width / count);
		int newHeight = MathUtils.floor(height / count);
		int curWidth = newWidth * count;
		int curHeight = newHeight * count;
		Pixmap pixmap = new Pixmap(curWidth, curHeight, true);
		Pixmap newLogo = logoPixmap;
		if (logoPixmap.getWidth() >= curWidth || logoPixmap.getHeight() >= curHeight) {
			newLogo = Pixmap.getResize(logoPixmap, curWidth, curHeight);
		}
		newLogo.filter(0);
		if (!trans) {
			pixmap.setColor(LColor.white);
			pixmap.fillRect(0, 0, curWidth, curHeight);
		}
		pixmap.drawPixmap(newLogo, (curWidth - newLogo.getWidth()) / 2, (curHeight - newLogo.getHeight()) / 2);
		pixmap.setColor(color);
		for (int row = 0; row < count; row++) {
			for (int col = 0; col < count; col++) {
				boolean b = isDark(row, col);
				if (b) {
		
					pixmap.fillRect(col * newWidth, row * newHeight, newWidth, newHeight);
				}
			}
		}
		return pixmap;
	}

	public static QRCode getNumber(String message) {
		return getNumber(message, 1, QRErrorLevel.H);
	}

	public static QRCode getNumber(String message, int numType, int errorLevel) {
		QRCode code = new QRCode(numType, errorLevel);
		int mode = QRMode.MODE_NUMBER;
		code.addData(message, mode);
		int length = code.getData(0).getLength();
		for (int typeCode = 1; typeCode <= 10; typeCode++) {
			if (length <= QRUtil.getMaxLength(typeCode, mode, errorLevel)) {
				code.setTypeNumber(typeCode);
				break;
			}
		}
		code.make();
		return code;
	}

	public static QRCode getAlphaNumber(String message) {
		return getAlphaNumber(message, 1, QRErrorLevel.H);
	}

	public static QRCode getAlphaNumber(String message, int numType, int errorLevel) {
		QRCode code = new QRCode(numType, errorLevel);
		int mode = QRMode.MODE_ALPHA_NUM;
		code.addData(message, mode);
		int length = code.getData(0).getLength();
		for (int typeCode = 1; typeCode <= 10; typeCode++) {
			if (length <= QRUtil.getMaxLength(typeCode, mode, errorLevel)) {
				code.setTypeNumber(typeCode);
				break;
			}
		}
		code.make();
		return code;
	}

	public static QRCode getKanji(String message) {
		return getKanji(message, 1, QRErrorLevel.H);
	}

	public static QRCode getKanji(String message, int numType, int errorLevel) {
		QRCode code = new QRCode(numType, errorLevel);
		int mode = QRMode.MODE_KANJI;
		code.addData(message, mode);
		int length = code.getData(0).getLength();
		for (int typeCode = 1; typeCode <= 10; typeCode++) {
			if (length <= QRUtil.getMaxLength(typeCode, mode, errorLevel)) {
				code.setTypeNumber(typeCode);
				break;
			}
		}
		code.make();
		return code;
	}

	public static QRCode get8BitByte(String message) {
		return get8BitByte(message, 1, QRErrorLevel.H);
	}

	public static QRCode get8BitByte(String message, int numType, int errorLevel) {
		QRCode code = new QRCode(numType, errorLevel);
		int mode = QRMode.MODE_8BIT_BYTE;
		code.addData(message, mode);
		int length = code.getData(0).getLength();
		for (int typeCode = 1; typeCode <= 10; typeCode++) {
			if (length <= QRUtil.getMaxLength(typeCode, mode, errorLevel)) {
				code.setTypeNumber(typeCode);
				break;
			}
		}
		code.make();
		return code;
	}

	public static QRCode getECI(String message) {
		return getECI(message, 1, QRErrorLevel.H);
	}

	public static QRCode getECI(String message, int numType, int errorLevel) {
		QRCode code = new QRCode(numType, errorLevel);
		int mode = QRMode.MODE_ECI;
		code.addData(message, mode);
		int length = code.getData(0).getLength();
		for (int typeCode = 1; typeCode <= 10; typeCode++) {
			if (length <= QRUtil.getMaxLength(typeCode, mode, errorLevel)) {
				code.setTypeNumber(typeCode);
				break;
			}
		}
		code.make();
		return code;
	}

	public static QRCode getQRCode(String data, int errorLevel) {
		int mode = QRUtil.getMode(data);
		QRCode qr = new QRCode();
		qr.setErrorLevel(errorLevel);
		qr.addData(data, mode);
		int length = qr.getData(0).getLength();
		for (int typeCode = 1; typeCode <= 10; typeCode++) {
			if (length <= QRUtil.getMaxLength(typeCode, mode, errorLevel)) {
				qr.setTypeNumber(typeCode);
				break;
			}
		}
		qr.make();
		return qr;
	}

	public int getTypeNumber() {
		return _typeCode;
	}

	public void setTypeNumber(int typeCode) {
		this._typeCode = typeCode;
	}

	public int getErrorLevel() {
		return _errorLevel;
	}

	public void setErrorLevel(int level) {
		this._errorLevel = level;
	}

	public void addData(String data) {
		addData(data, QRUtil.getMode(data));
	}

	public void addData(String data, int mode) {
		switch (mode) {
		case QRMode.MODE_NUMBER:
			addData(new QRNumber(data));
			break;
		case QRMode.MODE_ALPHA_NUM:
			addData(new QRAlphaNum(data));
			break;
		case QRMode.MODE_8BIT_BYTE:
			addData(new QR8BitByte(data));
			break;
		case QRMode.MODE_KANJI:
			addData(new QRKANJI(data));
			break;
		case QRMode.MODE_ECI:
			addData(new QRECI(data));
			break;
		default:
			throw new LSysException("mode:" + mode);
		}
	}

	public void clearData() {
		_dataList.clear();
	}

	protected void addData(QRData qrData) {
		_dataList.add(qrData);
	}

	protected int getDataCount() {
		return _dataList.size();
	}

	protected QRData getData(int index) {
		return _dataList.get(index);
	}

	public boolean isDark(int row, int col) {
		if (_modules[row][col] != null) {
			return _modules[row][col].booleanValue();
		} else {
			return false;
		}
	}

	public int getModuleCount() {
		return _moduleLength;
	}

	public void make() {
		make(false, getBestMaskPattern());
	}

	private int getBestMaskPattern() {

		int minLostPoint = 0;
		int pattern = 0;

		for (int i = 0; i < 8; i++) {

			make(true, i);

			int lostPoint = QRUtil.getLostPoint(this);

			if (i == 0 || minLostPoint > lostPoint) {
				minLostPoint = lostPoint;
				pattern = i;
			}
		}

		return pattern;
	}

	private void make(boolean test, int maskPattern) {

		_moduleLength = _typeCode * 4 + 17;
		_modules = new Boolean[_moduleLength][_moduleLength];

		setupPositionProbePattern(0, 0);
		setupPositionProbePattern(_moduleLength - 7, 0);
		setupPositionProbePattern(0, _moduleLength - 7);

		setupPositionAdjustPattern();
		setupTimingPattern();

		setupTypeInfo(test, maskPattern);

		if (_typeCode >= 7) {
			setupTypeNumber(test);
		}

		QRData[] dataArray = _dataList.toArray(new QRData[_dataList.size()]);

		byte[] data = createData(_typeCode, _errorLevel, dataArray);

		mapData(data, maskPattern);
	}

	private void mapData(byte[] data, int maskPattern) {

		int inc = -1;
		int row = _moduleLength - 1;
		int bitIndex = 7;
		int byteIndex = 0;

		for (int col = _moduleLength - 1; col > 0; col -= 2) {
			if (col == 6) {
				col--;
			}
			for (;;) {
				for (int c = 0; c < 2; c++) {

					if (_modules[row][col - c] == null) {

						boolean dark = false;

						if (byteIndex < data.length) {
							dark = (((data[byteIndex] >>> bitIndex) & 1) == 1);
						}

						boolean mask = QRUtil.getMask(maskPattern, row, col - c);

						if (mask) {
							dark = !dark;
						}

						_modules[row][col - c] = Boolean.valueOf(dark);
						bitIndex--;

						if (bitIndex == -1) {
							byteIndex++;
							bitIndex = 7;
						}
					}
				}

				row += inc;

				if (row < 0 || _moduleLength <= row) {
					row -= inc;
					inc = -inc;
					break;
				}
			}
		}
	}

	private void setupPositionAdjustPattern() {

		int[] pos = QRUtil.getPatternPosition(_typeCode);

		for (int i = 0; i < pos.length; i++) {
			for (int j = 0; j < pos.length; j++) {

				int row = pos[i];
				int col = pos[j];

				if (_modules[row][col] != null) {
					continue;
				}

				for (int r = -2; r <= 2; r++) {
					for (int c = -2; c <= 2; c++) {
						if (r == -2 || r == 2 || c == -2 || c == 2 || (r == 0 && c == 0)) {
							_modules[row + r][col + c] = Boolean.valueOf(true);
						} else {
							_modules[row + r][col + c] = Boolean.valueOf(false);
						}
					}
				}

			}
		}
	}

	private void setupPositionProbePattern(int row, int col) {

		for (int r = -1; r <= 7; r++) {
			for (int c = -1; c <= 7; c++) {
				if (row + r <= -1 || _moduleLength <= row + r || col + c <= -1 || _moduleLength <= col + c) {
					continue;
				}
				if ((0 <= r && r <= 6 && (c == 0 || c == 6)) || (0 <= c && c <= 6 && (r == 0 || r == 6))
						|| (2 <= r && r <= 4 && 2 <= c && c <= 4)) {
					_modules[row + r][col + c] = Boolean.valueOf(true);
				} else {
					_modules[row + r][col + c] = Boolean.valueOf(false);
				}
			}
		}
	}

	private void setupTimingPattern() {
		for (int r = 8; r < _moduleLength - 8; r++) {
			if (_modules[r][6] != null) {
				continue;
			}
			_modules[r][6] = Boolean.valueOf(r % 2 == 0);
		}
		for (int c = 8; c < _moduleLength - 8; c++) {
			if (_modules[6][c] != null) {
				continue;
			}
			_modules[6][c] = Boolean.valueOf(c % 2 == 0);
		}
	}

	private void setupTypeNumber(boolean test) {

		int bits = QRUtil.getBCHTypeNumber(_typeCode);

		for (int i = 0; i < 18; i++) {
			Boolean mod = Boolean.valueOf(!test && ((bits >> i) & 1) == 1);
			_modules[i / 3][i % 3 + _moduleLength - 8 - 3] = mod;
		}

		for (int i = 0; i < 18; i++) {
			Boolean mod = Boolean.valueOf(!test && ((bits >> i) & 1) == 1);
			_modules[i % 3 + _moduleLength - 8 - 3][i / 3] = mod;
		}
	}

	private void setupTypeInfo(boolean test, int maskPattern) {

		int data = (_errorLevel << 3) | maskPattern;
		int bits = QRUtil.getBCHTypeInfo(data);

		for (int i = 0; i < 15; i++) {

			Boolean mod = Boolean.valueOf(!test && ((bits >> i) & 1) == 1);
			if (i < 6) {
				_modules[i][8] = mod;
			} else if (i < 8) {
				_modules[i + 1][8] = mod;
			} else {
				_modules[_moduleLength - 15 + i][8] = mod;
			}
		}

		for (int i = 0; i < 15; i++) {
			Boolean mod = Boolean.valueOf(!test && ((bits >> i) & 1) == 1);
			if (i < 8) {
				_modules[8][_moduleLength - i - 1] = mod;
			} else if (i < 9) {
				_modules[8][15 - i - 1 + 1] = mod;
			} else {
				_modules[8][15 - i - 1] = mod;
			}
		}

		_modules[_moduleLength - 8][8] = Boolean.valueOf(!test);
	}

	public static byte[] createData(final int typeCode, final int errorLevel, final QRData data) {
		return createData(typeCode, errorLevel, new QRData[] { data });
	}

	public static byte[] createData(final int typeCode, final int errorLevel, final QRData[] dataArray) {

		QRRSBlock[] rsBlocks = QRRSBlock.getRSBlocks(typeCode, errorLevel);
		QRBitBuffer buffer = new QRBitBuffer();

		for (int i = 0; i < dataArray.length; i++) {
			QRData data = dataArray[i];
			buffer.put(data.getMode(), 4);
			buffer.put(data.getLength(), data.getLengthInBits(typeCode));
			data.write(buffer);
		}

		int totalDataCount = 0;
		for (int i = 0; i < rsBlocks.length; i++) {
			totalDataCount += rsBlocks[i].getDataCount();
		}

		if (buffer.getLengthInBits() > totalDataCount * 8) {
			throw new LSysException("code length overflow. (" + buffer.getLengthInBits() + ">" + totalDataCount * 8 + ")");
		}

		if (buffer.getLengthInBits() + 4 <= totalDataCount * 8) {
			buffer.put(0, 4);
		}

		while (buffer.getLengthInBits() % 8 != 0) {
			buffer.putBit(false);
		}

		for (;;) {
			if (buffer.getLengthInBits() >= totalDataCount * 8) {
				break;
			}
			buffer.put(PAD0, 8);

			if (buffer.getLengthInBits() >= totalDataCount * 8) {
				break;
			}
			buffer.put(PAD1, 8);
		}

		return createBytes(buffer, rsBlocks);
	}

	private static byte[] createBytes(QRBitBuffer buffer, QRRSBlock[] rsBlocks) {

		int offset = 0;

		int maxDcCount = 0;
		int maxEcCount = 0;

		int[][] dcdata = new int[rsBlocks.length][];
		int[][] ecdata = new int[rsBlocks.length][];

		for (int r = 0; r < rsBlocks.length; r++) {

			int dcCount = rsBlocks[r].getDataCount();
			int ecCount = rsBlocks[r].getTotalCount() - dcCount;

			maxDcCount = MathUtils.max(maxDcCount, dcCount);
			maxEcCount = MathUtils.max(maxEcCount, ecCount);

			dcdata[r] = new int[dcCount];
			for (int i = 0; i < dcdata[r].length; i++) {
				dcdata[r][i] = 0xff & buffer.getBuffer()[i + offset];
			}
			offset += dcCount;

			QRPolynomial rsPoly = QRUtil.getErrorPolynomial(ecCount);
			QRPolynomial rawPoly = new QRPolynomial(dcdata[r], rsPoly.getLength() - 1);

			QRPolynomial modPoly = rawPoly.mod(rsPoly);
			ecdata[r] = new int[rsPoly.getLength() - 1];
			for (int i = 0; i < ecdata[r].length; i++) {
				int modIndex = i + modPoly.getLength() - ecdata[r].length;
				ecdata[r][i] = (modIndex >= 0) ? modPoly.get(modIndex) : 0;
			}

		}

		int totalCodeCount = 0;
		for (int i = 0; i < rsBlocks.length; i++) {
			totalCodeCount += rsBlocks[i].getTotalCount();
		}

		byte[] data = new byte[totalCodeCount];

		int index = 0;

		for (int i = 0; i < maxDcCount; i++) {
			for (int r = 0; r < rsBlocks.length; r++) {
				if (i < dcdata[r].length) {
					data[index++] = (byte) dcdata[r][i];
				}
			}
		}

		for (int i = 0; i < maxEcCount; i++) {
			for (int r = 0; r < rsBlocks.length; r++) {
				if (i < ecdata[r].length) {
					data[index++] = (byte) ecdata[r][i];
				}
			}
		}

		return data;
	}

}
