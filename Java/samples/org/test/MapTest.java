package org.test;

import java.io.IOException;

import loon.Stage;
import loon.action.map.TileMap;
import loon.font.LFont;
import loon.opengl.LTexturePackClip;
import loon.utils.TArray;

public class MapTest extends Stage {

	@Override
	public void create() {
		try {
			// 构建数组地图精灵,每块瓦片32x32
			TileMap map = new TileMap("assets/rpg/map.txt", 32, 32);
			// 设置切图方式
			TArray<LTexturePackClip> clips = new TArray<LTexturePackClip>(10);
			//图片id,地图块名称,切图开始的x,y位置,切图大小(也就是切下来多少)
			clips.add(new LTexturePackClip(0, "1", 0, 0, 32, 32));
			clips.add(new LTexturePackClip(1, "2", 32, 0, 32, 32));
			clips.add(new LTexturePackClip(2, "3", 64, 0, 32, 32));
			clips.add(new LTexturePackClip(3, "4", 96, 0, 32, 32));
			clips.add(new LTexturePackClip(4, "5", 128, 0, 32, 32));
			clips.add(new LTexturePackClip(5, "6", 160, 0, 32, 32));
			// 注入切图用地图，以及切图方式
			map.setImagePack("assets/rpg/map.png", clips);
			// 执行切图
			map.pack();
			// 设置数组瓦片索引id和切图id的绑定关系
			map.putTile(-1, 0);
			map.putTile(1, 1);
			map.putTile(2, 2);
			map.putTile(3, 3);
			map.putTile(4, 4);
			map.putTile(5, 5);
			//偏移地图显示位置
			//map.setOffset(x, y);
			// 注入地图到窗体
			add(map);
		} catch (IOException e) {
			error(e.getMessage());
		}
		LFont.setDefaultFont(LFont.getFont(20));
		add(MultiScreenTest.getBackButton(this, 1));
	}

}
