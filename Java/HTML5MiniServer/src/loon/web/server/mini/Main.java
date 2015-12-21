package loon.web.server.mini;

import java.io.File;

import javax.swing.JFrame;

public class Main {

	public static void main(String[] args) {
		try {
			System.setProperty("loon.web.config",
					new File("config.txt").getAbsolutePath());
			Server server = new Server("config.txt");
			
			JFrame frame = new JFrame();
			frame.setTitle("Loon静态页面迷你服务器");
			frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
			frame.setSize(510, 255);
			frame.add(new ServerPanel(server,"在浏览器中输入 127.0.0.1:%s 即可看到服务器运行结果"));
			frame.setResizable(false);
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		
			
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}

	}
}
