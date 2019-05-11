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
package loon.fx;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class JavaFXAppliaction extends Application {

	private Scene fxScene;

	private JavaFXGame game;

	public JavaFXAppliaction(JavaFXGame g) {
		this.game = g;
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Group group = new Group();
		group.getChildren().add(game.gameCanvas);
		this.fxScene = new Scene(group, game.getWidth(), game.getHeight());
		primaryStage.setTitle(game.setting.appName);
		primaryStage.setScene(fxScene);
		primaryStage.show();
	}
	
	public int getWidth(){
		return (int) fxScene.getWidth();
	}

	public int getHeight(){
		return (int) fxScene.getHeight();
	}
}
