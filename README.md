## Loon Game Engine

![LGame](https://raw.github.com/cping/LGame/master/engine_logo.png "engine_logo")

[![License](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

[免费的游戏素材可见此](https://github.com/cping/LGame/blob/master/dev-res/README.md "Game Source of Free")

*干眼症（角膜干燥综合症）基本控制住了（用了两年环孢素+各种中药-_-|||），2019年复活继续更新 - 2019-03-08。

*目前的0.5版已经初步可用，我会逐渐增加测试用例和文档，然后据此改出C++，C#(基于MonoGame，Unity3D之类，反正底层都是Mono库，基于第三方架构开发是因为不用自己适配多平台)之类其它语法版本，当然核心还是Java，方便Java版一键语法转换和平台迁移而已。顺便我会出一个API精简的"非OpenGL"Java版封装(其实就是把最古老的AWT版重写一次)，这个封装会基于Android和JavaScript的Canvas进行渲染(而不是像现在一样渲染和主窗口是默认绑定的)，方便用户把Loon功能嵌入一些非游戏应用中(比如直接添加成一个TextureView，给应用加缓动动画特效什么的)，或者一些更轻度的小游戏开发，这个精简版本只会支持JavaSE，JavaFX，Android，JavaScript(HTML5)。
_________

源自中國本土的Java遊戲引擎項目

International Entertainment Machines

## Loon
formal name : Loon

A fast, simple & powerful game framework, powered by Java (also supports C# and C++).

LGame Project Restart,The game's just started.

## Features
LGame(LoonGame) is a very cool and small game library designed to simplify the complex and shorten the tedious for beginners and veterans alike. With it, you can use the best aspects of OpenGL/OpenGLES in an easy and organized way optimized for game programming. It is built around the concept that beginners should be able to start with the basics and then move up into a more complex plane of development with the veterans, all on the same platform.

LGame puts all of its effort into keeping things short and simple. The initial setup of a game consists only of making a single class; then you are done. The interface is entirely documented for easy and fast learning, so once you are started, there is nothing between you and your killer game but coding and creativity.

LGame is built around the users wishes, so do not hesitate to suggest and critique!

### Games Code Samples

![LGame](https://raw.github.com/cping/LGame/master/sample.png "samples")

[Samples](https://github.com/cping/LGame/tree/master/Java/samples "Game Sample")

[Examples](https://github.com/cping/LGame/tree/master/Java/Examples "Game Example")

### Game Run the Example(JavaSE)
```java

package org.test;

import loon.LSetting;
import loon.LazyLoading;
import loon.Screen;
import loon.javase.Loon;

public class Main  {

	public static void main(String[] args) {
		LSetting setting = new LSetting();
		//是否显示基础的debug数据(内存，精灵，桌面组件等使用情况)
		setting.isDebug = true;
		//是否显示log数据到窗体
		setting.isDisplayLog = false;
		//是否显示初始logo
		setting.isLogo = false;
		// 初始化页面用logo
		setting.logoPath = "loon_logo.png";
		// 原始大小
		setting.width = 480;
		setting.height = 320;
		// 缩放为
		setting.width_zoom = 640;
		setting.height_zoom = 480;
		//帧率
		setting.fps = 60;
		//字体
		setting.fontName = "黑体";
		//应用名
		setting.appName = "test";
		//是否模拟触屏事件（仅桌面有效）
		setting.emulateTouch = false;
		/*
		 * 设置全局IFont字体为BMFont字体,fnt和png文件默认使用loon的jar中自带<br>
		 * (不填写时默认使用内置的LFont贴图，用户也可以自定义IFont字体)<br>*/
		//setting.setSystemGameFont(BMFont.getDefaultFont());
		Loon.register(setting, new LazyLoading.Data() {

			@Override
			public Screen onScreen() {
				return new YourScreen();
			}
		});
	}
}
```
## Create a LGame project

LGame comes with a file called LGameProjectMake.jar which is an executable UI and command line tool. You can simply execute the JAR file which will open the setup UI.


![LGame](https://raw.github.com/cping/LGame/master/install.png "install")

Built-in over 30 game example(Part screenshots):

![LGame](https://raw.github.com/cping/LGame/master/e0x.png "0")

![LGame](https://raw.github.com/cping/LGame/master/e1x.png "1")

![LGame](https://raw.github.com/cping/LGame/master/e2x.png "2")

![LGame](https://raw.github.com/cping/LGame/master/e3x.png "3")

![LGame](https://raw.github.com/cping/LGame/master/e4x.png "4")

![LGame](https://raw.github.com/cping/LGame/master/live2dsupport.png "live2d_support")

License : http://www.apache.org/licenses/LICENSE-2.0

## Source Files
```
src
├── assets
├── ├── loon_bar.png
├── ├── loon_control_base.png
├── ├── loon_control_dot.png
├── ├── loon_creese.png
├── ├── loon_deffont.png
├── ├── loon_deffont.txt
├── ├── loon_e1.png
├── ├── loon_e2.png
├── ├── loon_logo.png
├── ├── loon_natural.png
├── ├── loon_natural.txt
├── ├── loon_pad_ui.png
├── ├── loon_pad_ui.txt
├── ├── loon_par.png
├── ├── loon_ui.png
├── └── loon_wbar.png
├── loon
├── ├── Accelerometer.java
├── ├── AccelerometerDefault.java
├── ├── AccelerometerState.java
├── ├── action
├── ├── ├── ActionBind.java
├── ├── ├── ActionCallback.java
├── ├── ├── ActionControl.java
├── ├── ├── ActionEvent.java
├── ├── ├── ActionLinear.java
├── ├── ├── ActionListener.java
├── ├── ├── ActionMode.java
├── ├── ├── ActionPath.java
├── ├── ├── Actions.java
├── ├── ├── ActionScript.java
├── ├── ├── ActionSmooth.java
├── ├── ├── ActionTween.java
├── ├── ├── ActionTweenBase.java
├── ├── ├── ActionTweenPool.java
├── ├── ├── ActionType.java
├── ├── ├── ArrowTo.java
├── ├── ├── avg
├── ├── ├── ├── AVGAnm.java
├── ├── ├── ├── AVGCG.java
├── ├── ├── ├── AVGChara.java
├── ├── ├── ├── AVGDialog.java
├── ├── ├── ├── AVGScreen.java
├── ├── ├── └── drama
├── ├── ├──     ├── Command.java
├── ├── ├──     ├── CommandLink.java
├── ├── ├──     ├── CommandType.java
├── ├── ├──     ├── Conversion.java
├── ├── ├──     ├── DefScriptLog.java
├── ├── ├──     ├── Expression.java
├── ├── ├──     ├── IMacros.java
├── ├── ├──     ├── IRocFunction.java
├── ├── ├──     ├── IScriptLog.java
├── ├── ├──     ├── RocFunctions.java
├── ├── ├──     ├── RocScript.java
├── ├── ├──     └── RocSSprite.java
├── ├── ├── BezierBy.java
├── ├── ├── BezierTo.java
├── ├── ├── camera
├── ├── ├── ├── BaseCamera.java
├── ├── ├── ├── EmptyCamera.java
├── ├── ├── ├── FPSCamera.java
├── ├── ├── ├── OrthographicCamera.java
├── ├── ├── └── PerspectiveCamera.java
├── ├── ├── CircleTo.java
├── ├── ├── collision
├── ├── ├── ├── BSPCollisionChecker.java
├── ├── ├── ├── BSPCollisionNode.java
├── ├── ├── ├── CollisionBaseQuery.java
├── ├── ├── ├── CollisionChecker.java
├── ├── ├── ├── CollisionClassQuery.java
├── ├── ├── ├── CollisionHelper.java
├── ├── ├── ├── CollisionInRangeQuery.java
├── ├── ├── ├── CollisionManager.java
├── ├── ├── ├── CollisionMask.java
├── ├── ├── ├── CollisionNeighbourQuery.java
├── ├── ├── ├── CollisionNode.java
├── ├── ├── ├── CollisionObject.java
├── ├── ├── ├── CollisionPointQuery.java
├── ├── ├── ├── CollisionQuery.java
├── ├── ├── ├── Gravity.java
├── ├── ├── ├── GravityHandler.java
├── ├── ├── ├── GravityResult.java
├── ├── ├── └── Hitbox.java
├── ├── ├── ColorTo.java
├── ├── ├── DelayTo.java
├── ├── ├── EffectTo.java
├── ├── ├── EventTo.java
├── ├── ├── FadeTo.java
├── ├── ├── FireTo.java
├── ├── ├── FlashTo.java
├── ├── ├── FlipXTo.java
├── ├── ├── FlipYTo.java
├── ├── ├── JumpTo.java
├── ├── ├── map
├── ├── ├── ├── AStarFinder.java
├── ├── ├── ├── AStarFinderListener.java
├── ├── ├── ├── AStarFinderPool.java
├── ├── ├── ├── AStarFindHeuristic.java
├── ├── ├── ├── Attribute.java
├── ├── ├── ├── battle
├── ├── ├── ├── ├── BattleAction.java
├── ├── ├── ├── ├── BattleControl.java
├── ├── ├── ├── ├── BattleEffectState.java
├── ├── ├── ├── ├── BattleLevel.java
├── ├── ├── ├── ├── BattleRNG.java
├── ├── ├── ├── ├── BattleSkillValue.java
├── ├── ├── ├── ├── BattleTeam.java
├── ├── ├── ├── ├── BattleView.java
├── ├── ├── ├── ├── behavior
├── ├── ├── ├── ├── ├── IActor.java
├── ├── ├── ├── ├── ├── IActors.java
├── ├── ├── ├── ├── ├── IActorStatus.java
├── ├── ├── ├── ├── └── IMove.java
├── ├── ├── ├── └── DamagesState.java
├── ├── ├── ├── Character.java
├── ├── ├── ├── CharacterInfo.java
├── ├── ├── ├── CharacterValue.java
├── ├── ├── ├── colider
├── ├── ├── ├── ├── HexagonalTileColider.java
├── ├── ├── ├── ├── IsometricTileColider.java
├── ├── ├── ├── ├── OrthogonalTileColider.java
├── ├── ├── ├── ├── Tile.java
├── ├── ├── ├── ├── TileColider.java
├── ├── ├── ├── ├── TileEvent.java
├── ├── ├── ├── ├── TileHelper.java
├── ├── ├── ├── ├── TileImpl.java
├── ├── ├── ├── ├── TileImplPathFind.java
├── ├── ├── ├── └── TileManager.java
├── ├── ├── ├── Config.java
├── ├── ├── ├── Field2D.java
├── ├── ├── ├── heuristics
├── ├── ├── ├── ├── Closest.java
├── ├── ├── ├── ├── ClosestSquared.java
├── ├── ├── ├── ├── Diagonal.java
├── ├── ├── ├── ├── DiagonalShort.java
├── ├── ├── ├── ├── Euclidean.java
├── ├── ├── ├── ├── EuclideanNoSQR.java
├── ├── ├── ├── ├── Manhattan.java
├── ├── ├── ├── └── Mixing.java
├── ├── ├── ├── Hexagon.java
├── ├── ├── ├── HexagonMap.java
├── ├── ├── ├── Inventory.java
├── ├── ├── ├── Item.java
├── ├── ├── ├── Story.java
├── ├── ├── ├── TileMap.java
├── ├── ├── ├── TileMapConfig.java
├── ├── ├── ├── TileRoom.java
├── ├── ├── ├── TileVisit.java
├── ├── ├── └── tmx
├── ├── ├──     ├── objects
├── ├── ├──     ├── ├── TMXEllipse.java
├── ├── ├──     ├── ├── TMXObject.java
├── ├── ├──     ├── ├── TMXPoint.java
├── ├── ├──     ├── ├── TMXPolygon.java
├── ├── ├──     ├── └── TMXPolyLine.java
├── ├── ├──     ├── renderers
├── ├── ├──     ├── ├── TMXHexagonalMapRenderer.java
├── ├── ├──     ├── ├── TMXIsometricMapRenderer.java
├── ├── ├──     ├── ├── TMXMapRenderer.java
├── ├── ├──     ├── ├── TMXOrthogonalMapRenderer.java
├── ├── ├──     ├── └── TMXStaggeredMapRenderer.java
├── ├── ├──     ├── tiles
├── ├── ├──     ├── ├── TMXAnimationFrame.java
├── ├── ├──     ├── ├── TMXMapTile.java
├── ├── ├──     ├── ├── TMXTerrain.java
├── ├── ├──     ├── └── TMXTile.java
├── ├── ├──     ├── TMXImage.java
├── ├── ├──     ├── TMXImageLayer.java
├── ├── ├──     ├── TMXMap.java
├── ├── ├──     ├── TMXMapLayer.java
├── ├── ├──     ├── TMXObjectLayer.java
├── ├── ├──     ├── TMXProperties.java
├── ├── ├──     ├── TMXTileLayer.java
├── ├── ├──     └── TMXTileSet.java
├── ├── ├── MoveBy.java
├── ├── ├── MoveOvalTo.java
├── ├── ├── MoveRoundTo.java
├── ├── ├── MoveTo.java
├── ├── ├── page
├── ├── ├── ├── AccordionPage.java
├── ├── ├── ├── BasePage.java
├── ├── ├── ├── BTFPage.java
├── ├── ├── ├── CubeInPage.java
├── ├── ├── ├── DepthPage.java
├── ├── ├── ├── FadePage.java
├── ├── ├── ├── RotateDownPage.java
├── ├── ├── ├── RotatePage.java
├── ├── ├── ├── RotateUpPage.java
├── ├── ├── ├── ScreenSwitch.java
├── ├── ├── ├── StackPage.java
├── ├── ├── ├── ZoomInPage.java
├── ├── ├── └── ZoomOutPage.java
├── ├── ├── ParallelTo.java
├── ├── ├── RemoveActionsTo.java
├── ├── ├── ReplayTo.java
├── ├── ├── RotateTo.java
├── ├── ├── ScaleTo.java
├── ├── ├── ShakeTo.java
├── ├── ├── ShowTo.java
├── ├── ├── sprite
├── ├── ├── ├── ActionObject.java
├── ├── ├── ├── AnimatedEntity.java
├── ├── ├── ├── Animation.java
├── ├── ├── ├── AnimationData.java
├── ├── ├── ├── AnimationHelper.java
├── ├── ├── ├── AnimationStorage.java
├── ├── ├── ├── Background.java
├── ├── ├── ├── Bullet.java
├── ├── ├── ├── BulletEntity.java
├── ├── ├── ├── CanvasPlayer.java
├── ├── ├── ├── ColorBackground.java
├── ├── ├── ├── Cycle.java
├── ├── ├── ├── DisplayObject.java
├── ├── ├── ├── effect
├── ├── ├── ├── ├── ArcEffect.java
├── ├── ├── ├── ├── BaseEffect.java
├── ├── ├── ├── ├── CrossEffect.java
├── ├── ├── ├── ├── FadeDotEffect.java
├── ├── ├── ├── ├── FadeEffect.java
├── ├── ├── ├── ├── FadeOvalEffect.java
├── ├── ├── ├── ├── FadeSpiralEffect.java
├── ├── ├── ├── ├── FadeTileEffect.java
├── ├── ├── ├── ├── IKernel.java
├── ├── ├── ├── ├── ILightning.java
├── ├── ├── ├── ├── LightningBolt.java
├── ├── ├── ├── ├── LightningBranch.java
├── ├── ├── ├── ├── LightningEffect.java
├── ├── ├── ├── ├── LightningLine.java
├── ├── ├── ├── ├── LightningRandom.java
├── ├── ├── ├── ├── NaturalEffect.java
├── ├── ├── ├── ├── OutEffect.java
├── ├── ├── ├── ├── PetalKernel.java
├── ├── ├── ├── ├── PixelBaseEffect.java
├── ├── ├── ├── ├── PixelChopEffect.java
├── ├── ├── ├── ├── PixelDarkInEffect.java
├── ├── ├── ├── ├── PixelDarkOutEffect.java
├── ├── ├── ├── ├── PixelSnowEffect.java
├── ├── ├── ├── ├── PixelThunderEffect.java
├── ├── ├── ├── ├── PixelWindEffect.java
├── ├── ├── ├── ├── PShadowEffect.java
├── ├── ├── ├── ├── RainKernel.java
├── ├── ├── ├── ├── RippleEffect.java
├── ├── ├── ├── ├── RippleKernel.java
├── ├── ├── ├── ├── ScrollEffect.java
├── ├── ├── ├── ├── SnowKernel.java
├── ├── ├── ├── ├── SplitEffect.java
├── ├── ├── ├── ├── StringEffect.java
├── ├── ├── ├── ├── SwipeEffect.java
├── ├── ├── ├── └── TriangleEffect.java
├── ├── ├── ├── Entity.java
├── ├── ├── ├── GifAnimation.java
├── ├── ├── ├── IEntity.java
├── ├── ├── ├── ImageBackground.java
├── ├── ├── ├── ISprite.java
├── ├── ├── ├── JumpObject.java
├── ├── ├── ├── MoveControl.java
├── ├── ├── ├── MoveObject.java
├── ├── ├── ├── MovieClip.java
├── ├── ├── ├── MovieSprite.java
├── ├── ├── ├── NumberSprite.java
├── ├── ├── ├── painting
├── ├── ├── ├── ├── ComponentEvent.java
├── ├── ├── ├── ├── Drawable.java
├── ├── ├── ├── ├── DrawableEvent.java
├── ├── ├── ├── ├── DrawableGameComponent.java
├── ├── ├── ├── ├── DrawableScreen.java
├── ├── ├── ├── ├── DrawableState.java
├── ├── ├── ├── ├── GameComponent.java
├── ├── ├── ├── ├── GameComponentCollection.java
├── ├── ├── ├── ├── IDrawable.java
├── ├── ├── ├── ├── IGameComponent.java
├── ├── ├── ├── └── IUpdateable.java
├── ├── ├── ├── Picture.java
├── ├── ├── ├── Scene.java
├── ├── ├── ├── ScrollText.java
├── ├── ├── ├── ShapeEntity.java
├── ├── ├── ├── SimpleObject.java
├── ├── ├── ├── Sprite.java
├── ├── ├── ├── SpriteBatch.java
├── ├── ├── ├── SpriteBatchScreen.java
├── ├── ├── ├── SpriteBatchSheet.java
├── ├── ├── ├── SpriteControls.java
├── ├── ├── ├── SpriteLabel.java
├── ├── ├── ├── SpriteRegion.java
├── ├── ├── ├── Sprites.java
├── ├── ├── ├── SpriteSheet.java
├── ├── ├── ├── SpriteSheetFont.java
├── ├── ├── ├── SpriteToEntity.java
├── ├── ├── ├── StatusBar.java
├── ├── ├── ├── StatusBars.java
├── ├── ├── ├── TextureObject.java
├── ├── ├── └── WaitSprite.java
├── ├── ├── TimeLine.java
├── ├── ├── TransferTo.java
├── ├── ├── TransformTo.java
├── ├── ├── TweenTo.java
├── ├── ├── UpdateTo.java
├── ├── └── WaitTo.java
├── ├── ActionCounter.java
├── ├── Assets.java
├── ├── Asyn.java
├── ├── BaseIO.java
├── ├── canvas
├── ├── ├── Alpha.java
├── ├── ├── Canvas.java
├── ├── ├── ConvolutionMatrix.java
├── ├── ├── Gradient.java
├── ├── ├── Image.java
├── ├── ├── ImageImpl.java
├── ├── ├── ImageNinePatch.java
├── ├── ├── LColor.java
├── ├── ├── LColorPool.java
├── ├── ├── LGradation.java
├── ├── ├── Limit.java
├── ├── ├── LShadow.java
├── ├── ├── NinePatchAbstract.java
├── ├── ├── NinePatchRegion.java
├── ├── ├── Paint.java
├── ├── ├── Path.java
├── ├── ├── Pattern.java
├── ├── ├── Pixmap.java
├── ├── ├── PixmapFImpl.java
├── ├── ├── PixmapNinePatch.java
├── ├── ├── Row.java
├── ├── └── TGA.java
├── ├── component
├── ├── ├── AbstractBox.java
├── ├── ├── Actor.java
├── ├── ├── ActorLayer.java
├── ├── ├── ActorListener.java
├── ├── ├── ActorSet.java
├── ├── ├── ActorTreeSet.java
├── ├── ├── BaseBox.java
├── ├── ├── DefUI.java
├── ├── ├── Desktop.java
├── ├── ├── layout
├── ├── ├── ├── AbsoluteLayout.java
├── ├── ├── ├── BoxSize.java
├── ├── ├── ├── CenterLayout.java
├── ├── ├── ├── HorizontalLayout.java
├── ├── ├── ├── LayoutConstraints.java
├── ├── ├── ├── LayoutManager.java
├── ├── ├── ├── LayoutPort.java
├── ├── ├── ├── OverlayLayout.java
├── ├── ├── └── VerticalLayout.java
├── ├── ├── LButton.java
├── ├── ├── LCheckBox.java
├── ├── ├── LCheckGroup.java
├── ├── ├── LClickButton.java
├── ├── ├── LComponent.java
├── ├── ├── LContainer.java
├── ├── ├── LControl.java
├── ├── ├── LDecideName.java
├── ├── ├── LGesture.java
├── ├── ├── LLabel.java
├── ├── ├── LLabels.java
├── ├── ├── LLayer.java
├── ├── ├── LMenu.java
├── ├── ├── LMenuSelect.java
├── ├── ├── LMessage.java
├── ├── ├── LMessageBox.java
├── ├── ├── LPad.java
├── ├── ├── LPanel.java
├── ├── ├── LPaper.java
├── ├── ├── LProgress.java
├── ├── ├── LScrollBar.java
├── ├── ├── LScrollContainer.java
├── ├── ├── LSelect.java
├── ├── ├── LSelectorIcon.java
├── ├── ├── LSlider.java
├── ├── ├── LSpriteUI.java
├── ├── ├── LTextArea.java
├── ├── ├── LTextBar.java
├── ├── ├── LTextField.java
├── ├── ├── LTextList.java
├── ├── ├── LTextTree.java
├── ├── ├── LToast.java
├── ├── ├── LToolTip.java
├── ├── ├── LWindow.java
├── ├── ├── Print.java
├── ├── ├── skin
├── ├── ├── ├── CheckBoxSkin.java
├── ├── ├── ├── ClickButtonSkin.java
├── ├── ├── ├── ControlSkin.java
├── ├── ├── ├── MenuSkin.java
├── ├── ├── ├── MessageSkin.java
├── ├── ├── ├── ProgressSkin.java
├── ├── ├── ├── ScrollBarSkin.java
├── ├── ├── ├── SelectSkin.java
├── ├── ├── ├── SkinManager.java
├── ├── ├── ├── SliderSkin.java
├── ├── ├── ├── TableSkin.java
├── ├── ├── ├── TextAreaSkin.java
├── ├── ├── ├── TextBarSkin.java
├── ├── ├── ├── TextListSkin.java
├── ├── ├── ├── ToastSkin.java
├── ├── ├── └── WindowSkin.java
├── ├── ├── table
├── ├── ├── ├── ICellRenderer.java
├── ├── ├── ├── ITableModel.java
├── ├── ├── ├── ListItem.java
├── ├── ├── ├── LTable.java
├── ├── ├── ├── SimpleTableModel.java
├── ├── ├── ├── TableColumn.java
├── ├── ├── ├── TableColumnLayout.java
├── ├── ├── ├── TableLayout.java
├── ├── ├── ├── TableLayoutRow.java
├── ├── ├── ├── TextCellRenderer.java
├── ├── ├── └── TextureCellRenderer.java
├── ├── └── UIControls.java
├── ├── Counter.java
├── ├── Director.java
├── ├── Display.java
├── ├── EmptyBundle.java
├── ├── EmptyGame.java
├── ├── EmptyObject.java
├── ├── EmulatorButton.java
├── ├── EmulatorButtons.java
├── ├── EmulatorListener.java
├── ├── event
├── ├── ├── ActionKey.java
├── ├── ├── ActionUpdate.java
├── ├── ├── CallFunction.java
├── ├── ├── ClickListener.java
├── ├── ├── DrawListener.java
├── ├── ├── Event.java
├── ├── ├── EventDispatcher.java
├── ├── ├── FrameLoopEvent.java
├── ├── ├── GameKey.java
├── ├── ├── GameTouch.java
├── ├── ├── IEventListener.java
├── ├── ├── InputMake.java
├── ├── ├── InputMakeImpl.java
├── ├── ├── KeyMake.java
├── ├── ├── LTouchArea.java
├── ├── ├── LTouchCollection.java
├── ├── ├── LTouchLocation.java
├── ├── ├── LTouchLocationState.java
├── ├── ├── MouseMake.java
├── ├── ├── QueryEvent.java
├── ├── ├── SysInput.java
├── ├── ├── SysInputFactory.java
├── ├── ├── SysKey.java
├── ├── ├── SysTouch.java
├── ├── ├── Touched.java
├── ├── ├── TouchedClick.java
├── ├── ├── TouchMake.java
├── ├── ├── Updateable.java
├── ├── └── ValueListener.java
├── ├── font
├── ├── ├── AutoWrap.java
├── ├── ├── BMFont.java
├── ├── ├── Font.java
├── ├── ├── FontSet.java
├── ├── ├── FontUtils.java
├── ├── ├── IFont.java
├── ├── ├── LFont.java
├── ├── ├── ShadowFont.java
├── ├── ├── Text.java
├── ├── ├── TextFormat.java
├── ├── ├── TextLayout.java
├── ├── ├── TextOptions.java
├── ├── └── TextWrap.java
├── ├── GameType.java
├── ├── geom
├── ├── ├── AABB.java
├── ├── ├── Affine2f.java
├── ├── ├── Alignment.java
├── ├── ├── Bezier.java
├── ├── ├── BooleanValue.java
├── ├── ├── BoundingBox.java
├── ├── ├── Circle.java
├── ├── ├── Curve.java
├── ├── ├── Dimension.java
├── ├── ├── Ellipse.java
├── ├── ├── FloatValue.java
├── ├── ├── IntValue.java
├── ├── ├── Line.java
├── ├── ├── Matrix3.java
├── ├── ├── Matrix4.java
├── ├── ├── Padding.java
├── ├── ├── Path.java
├── ├── ├── Plane.java
├── ├── ├── Point.java
├── ├── ├── PointF.java
├── ├── ├── PointI.java
├── ├── ├── Polygon.java
├── ├── ├── Quaternion.java
├── ├── ├── RectBox.java
├── ├── ├── RectF.java
├── ├── ├── RectI.java
├── ├── ├── Region.java
├── ├── ├── Shape.java
├── ├── ├── ShapeUtils.java
├── ├── ├── SizeValue.java
├── ├── ├── Transform.java
├── ├── ├── Transforms.java
├── ├── ├── Triangle.java
├── ├── ├── Triangle2f.java
├── ├── ├── TriangleBasic.java
├── ├── ├── TriangleNeat.java
├── ├── ├── TriangleOver.java
├── ├── ├── Vector2f.java
├── ├── ├── Vector3f.java
├── ├── ├── Vector4f.java
├── ├── └── XY.java
├── ├── Graphics.java
├── ├── HorizontalAlign.java
├── ├── IDGenerator.java
├── ├── Json.java
├── ├── LazyLoading.java
├── ├── LGame.java
├── ├── LimitedCounter.java
├── ├── LObject.java
├── ├── Log.java
├── ├── LogDisplay.java
├── ├── LProcess.java
├── ├── LRelease.java
├── ├── LSetting.java
├── ├── LSystem.java
├── ├── LSystemView.java
├── ├── LTexture.java
├── ├── LTextureBatch.java
├── ├── LTextures.java
├── ├── LTrans.java
├── ├── LTransition.java
├── ├── opengl
├── ├── ├── BaseBatch.java
├── ├── ├── BlendMode.java
├── ├── ├── BlendState.java
├── ├── ├── ExpandVertices.java
├── ├── ├── GL20.java
├── ├── ├── GLBase.java
├── ├── ├── GLBatch.java
├── ├── ├── GLEx.java
├── ├── ├── GLExt.java
├── ├── ├── GLPaint.java
├── ├── ├── GLRenderer.java
├── ├── ├── IndexArray.java
├── ├── ├── IndexBufferObject.java
├── ├── ├── IndexBufferObjectSubData.java
├── ├── ├── IndexData.java
├── ├── ├── light
├── ├── ├── ├── AmbientCubemap.java
├── ├── ├── ├── BaseLight.java
├── ├── ├── ├── DirectionalLight.java
├── ├── ├── ├── Lights.java
├── ├── ├── ├── LLight.java
├── ├── ├── └── PointLight.java
├── ├── ├── LSTRDictionary.java
├── ├── ├── LSTRFont.java
├── ├── ├── LSubTexture.java
├── ├── ├── LTextureBind.java
├── ├── ├── LTextureFree.java
├── ├── ├── LTextureImage.java
├── ├── ├── LTexturePack.java
├── ├── ├── LTexturePackClip.java
├── ├── ├── LTextureRegion.java
├── ├── ├── Mesh.java
├── ├── ├── MeshDefault.java
├── ├── ├── Painter.java
├── ├── ├── PreBoxViewer3D.java
├── ├── ├── RenderTarget.java
├── ├── ├── ShaderCmd.java
├── ├── ├── ShaderProgram.java
├── ├── ├── ShaderUtils.java
├── ├── ├── TextureSource.java
├── ├── ├── TextureUtils.java
├── ├── ├── TrilateralBatch.java
├── ├── ├── VertexArray.java
├── ├── ├── VertexAttribute.java
├── ├── ├── VertexAttributes.java
├── ├── ├── VertexBufferObject.java
├── ├── ├── VertexBufferObjectSubData.java
├── ├── └── VertexData.java
├── ├── particle
├── ├── ├── ParticleSprite.java
├── ├── ├── SimpleConfigurableEmitter.java
├── ├── ├── SimpleEmitter.java
├── ├── ├── SimpleFireEmitter.java
├── ├── ├── SimpleParticle.java
├── ├── ├── SimpleParticleConfig.java
├── ├── └── SimpleParticleSystem.java
├── ├── physics
├── ├── ├── PBody.java
├── ├── ├── PBoxShape.java
├── ├── ├── PCircleCirlceCollider.java
├── ├── ├── PCirclePolygonCollider.java
├── ├── ├── PCircleShape.java
├── ├── ├── PCollider.java
├── ├── ├── PCollisionChooser.java
├── ├── ├── PConcavePolygonShape.java
├── ├── ├── PContact.java
├── ├── ├── PContactData.java
├── ├── ├── PConvexPolygonShape.java
├── ├── ├── PDragJoint.java
├── ├── ├── PFigure.java
├── ├── ├── PHingeJoint.java
├── ├── ├── PInsertionSorter.java
├── ├── ├── PJoint.java
├── ├── ├── PJointType.java
├── ├── ├── PPhysManager.java
├── ├── ├── PPhysWorld.java
├── ├── ├── PPolygon.java
├── ├── ├── PPolygonDrawer.java
├── ├── ├── PPolygonizer.java
├── ├── ├── PPolygonPolygonCollider.java
├── ├── ├── PRodJoint.java
├── ├── ├── PShape.java
├── ├── ├── PShapeType.java
├── ├── ├── PSolver.java
├── ├── ├── PSortableAABB.java
├── ├── ├── PSortableObject.java
├── ├── ├── PSpringJoint.java
├── ├── ├── PSweepAndPrune.java
├── ├── ├── PTransformer.java
├── ├── ├── PTriangulator.java
├── ├── ├── PVertexLoop.java
├── ├── └── PWorldBox.java
├── ├── Platform.java
├── ├── PlayerUtils.java
├── ├── Save.java
├── ├── SaveBatchImpl.java
├── ├── Screen.java
├── ├── ScreenAction.java
├── ├── Session.java
├── ├── Sound.java
├── ├── SoundBox.java
├── ├── SoundImpl.java
├── ├── Stage.java
├── ├── Support.java
├── ├── utils
├── ├── ├── ARC4.java
├── ├── ├── Array.java
├── ├── ├── ArrayByte.java
├── ├── ├── ArrayByteOutput.java
├── ├── ├── ArrayByteReader.java
├── ├── ├── ArrayMap.java
├── ├── ├── Base64Coder.java
├── ├── ├── Bundle.java
├── ├── ├── Calculator.java
├── ├── ├── CharArray.java
├── ├── ├── CharUtils.java
├── ├── ├── CollectionUtils.java
├── ├── ├── ConfigReader.java
├── ├── ├── Easing.java
├── ├── ├── Flip.java
├── ├── ├── FloatArray.java
├── ├── ├── GestureData.java
├── ├── ├── GestureLoader.java
├── ├── ├── GifDecoder.java
├── ├── ├── GifEncoder.java
├── ├── ├── GLUtils.java
├── ├── ├── I18N.java
├── ├── ├── IArray.java
├── ├── ├── InsertionSorter.java
├── ├── ├── IntArray.java
├── ├── ├── IntHashMap.java
├── ├── ├── IntIntMap.java
├── ├── ├── IntMap.java
├── ├── ├── json
├── ├── ├── ├── JsonArray.java
├── ├── ├── ├── JsonBuilder.java
├── ├── ├── ├── JsonImpl.java
├── ├── ├── ├── JsonObject.java
├── ├── ├── ├── JsonParser.java
├── ├── ├── ├── JsonParserException.java
├── ├── ├── ├── JsonSink.java
├── ├── ├── ├── JsonStringTypedArray.java
├── ├── ├── └── JsonTypes.java
├── ├── ├── Language.java
├── ├── ├── LayerSorter.java
├── ├── ├── ListMap.java
├── ├── ├── LIterator.java
├── ├── ├── LongArray.java
├── ├── ├── LoopStringBuilder.java
├── ├── ├── MapBundle.java
├── ├── ├── MathUtils.java
├── ├── ├── MD5.java
├── ├── ├── MessageQueue.java
├── ├── ├── NumberUtils.java
├── ├── ├── ObjectIntMap.java
├── ├── ├── ObjectMap.java
├── ├── ├── ObjectSet.java
├── ├── ├── OrderedMap.java
├── ├── ├── OrderedSet.java
├── ├── ├── ParsePythonData.java
├── ├── ├── Pool.java
├── ├── ├── Pools.java
├── ├── ├── processes
├── ├── ├── ├── GameProcess.java
├── ├── ├── ├── ProgressCallable.java
├── ├── ├── ├── ProgressListener.java
├── ├── ├── ├── ProgressMonitor.java
├── ├── ├── ├── RealtimeProcess.java
├── ├── ├── ├── RealtimeProcessEvent.java
├── ├── ├── ├── RealtimeProcessHost.java
├── ├── ├── ├── RealtimeProcessManager.java
├── ├── ├── └── WaitProcess.java
├── ├── ├── reflect
├── ├── ├── ├── Annotation.java
├── ├── ├── ├── ArrayReflection.java
├── ├── ├── ├── ClassReflection.java
├── ├── ├── ├── Constructor.java
├── ├── ├── ├── Field.java
├── ├── ├── ├── Method.java
├── ├── ├── └── ReflectionException.java
├── ├── ├── RefManager.java
├── ├── ├── RefObject.java
├── ├── ├── reply
├── ├── ├── ├── AbstractAct.java
├── ├── ├── ├── AbstractValue.java
├── ├── ├── ├── Act.java
├── ├── ├── ├── ActView.java
├── ├── ├── ├── Bypass.java
├── ├── ├── ├── Callback.java
├── ├── ├── ├── CallbackList.java
├── ├── ├── ├── Closeable.java
├── ├── ├── ├── Connection.java
├── ├── ├── ├── Cons.java
├── ├── ├── ├── Function.java
├── ├── ├── ├── GoFuture.java
├── ├── ├── ├── GoPromise.java
├── ├── ├── ├── MappedAct.java
├── ├── ├── ├── MappedValue.java
├── ├── ├── ├── Port.java
├── ├── ├── ├── Try.java
├── ├── ├── ├── UnitPort.java
├── ├── ├── ├── Var.java
├── ├── ├── └── VarView.java
├── ├── ├── res
├── ├── ├── ├── FontSheet.java
├── ├── ├── ├── MovieSpriteSheet.java
├── ├── ├── ├── ResourceGetter.java
├── ├── ├── ├── ResourceItem.java
├── ├── ├── ├── ResourceLocal.java
├── ├── ├── ├── ResourceType.java
├── ├── ├── ├── Texture.java
├── ├── ├── ├── TextureAtlas.java
├── ├── ├── └── TextureData.java
├── ├── ├── Resolution.java
├── ├── ├── Scale.java
├── ├── ├── SortedList.java
├── ├── ├── Sorter.java
├── ├── ├── SortUtils.java
├── ├── ├── StringKeyValue.java
├── ├── ├── StringUtils.java
├── ├── ├── TArray.java
├── ├── ├── TArrayMap.java
├── ├── ├── TArrayValueMap.java
├── ├── ├── timer
├── ├── ├── ├── CountdownTimer.java
├── ├── ├── ├── EaseTimer.java
├── ├── ├── ├── FloatTimerEvent.java
├── ├── ├── ├── GameTime.java
├── ├── ├── ├── LTimer.java
├── ├── ├── ├── LTimerContext.java
├── ├── ├── ├── StopwatchTimer.java
├── ├── ├── └── TempTimer.java
├── ├── ├── TimeUtils.java
├── ├── ├── TreeNode.java
├── ├── ├── UNByte.java
├── ├── ├── UNInt.java
├── ├── ├── UNShort.java
├── ├── ├── URecognizer.java
├── ├── ├── URecognizerAnalyze.java
├── ├── ├── URecognizerObject.java
├── ├── ├── URecognizerResult.java
├── ├── └── xml
├── ├──     ├── XMLAttribute.java
├── ├──     ├── XMLComment.java
├── ├──     ├── XMLData.java
├── ├──     ├── XMLDocument.java
├── ├──     ├── XMLElement.java
├── ├──     ├── XMLListener.java
├── ├──     ├── XMLOutput.java
├── ├──     ├── XMLParser.java
├── ├──     ├── XMLProcessing.java
├── ├──     └── XMLTokenizer.java
├── ├── VerticalAlign.java
├── ├── Visible.java
├── └── ZIndex.java
└── loon.gwt.xml

40 directories, 745 files
```

### This Java implemented game framework(but will provide a cross-compiler contain C#,C/C++,Go,JavaScript or other programming language implemented),Support IOS\Android\WP7\PSM\Mac\Windows\Linux.