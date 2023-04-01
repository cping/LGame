package org.test;

import loon.BaseIO;
import loon.LTexture;
import loon.Stage;
import loon.action.sprite.Picture;
import loon.canvas.Canvas;
import loon.canvas.Image;
import loon.canvas.LColor;
import loon.opengl.LTextureImage;
import loon.utils.reply.Port;

public class TextureImageTest extends Stage {

	static float width = 100;
	static float height = 100;
	static int offset = 5;
	static String imageSrc = "imagetest1.png";
	static String imageGroundSrc = "imagetest2.png";

	@Override
	public void create() {

		setBackground(LColor.red);
		
		float nwidth = 4 * width, nheight = 4 * height;
		// 创建一个绑定GLEx的Texture图片
		LTextureImage bg = createTextureImage(nwidth, nheight);
		// 直接渲染纹理画面(内部使用framebuffer)
		bg.begin().setColor(LColor.red).fillRect(0, 0, nwidth, nheight)
				.setColor(LColor.blue)
				.fillRect(0, nwidth / 2, nwidth, nheight / 2).end().close();

		// 添加纹理图片到Screen
		add(new Picture(bg.texture));

		// 加载指定图片，当图片加载成功后
		BaseIO.loadImage(imageSrc).state.onSuccess(new Port<Image>() {
			// 执行如下代码
			public void onEmit(Image image) {

				// 渲染纹理，并作为表演者加入Screen
				LTexture imtex = image.texture();
				ELF().addAt(new Picture(imtex), offset, offset);
				ELF().addAt(new Picture(imtex), offset,
						offset + 2 * height);

				LTextureImage surf = createTextureImage(image.width(),
						image.height());
				surf.begin().clear().draw(imtex, 0, 0).end().close();
				ELF().addAt(new Picture(surf.texture),
						offset + width, offset);
				ELF().addAt(new Picture(surf.texture),
						offset + width, offset + 2 * height);

				Canvas canvas = image.getCanvas();
				canvas.draw(image, 0, 0);
				LTexture texture = canvas.toTexture();
				ELF().addAt(new Picture(texture),
						offset + 2 * width, offset);
				ELF().addAt(new Picture(texture),
						offset + 2 * width, offset + 2 * height);
			}
		});

		// 添加图片并载入Screen
		Image baseGround = BaseIO.loadImage(imageGroundSrc);
		ELF().addAt(new Picture(baseGround), 3 * width, 0);
		

		add(MultiScreenTest.getBackButton(this,0));
	}

}
