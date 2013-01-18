//下列函数都会自动获得浏览器调用，无需用户手动处理
$package('loon.main');

/**
 * loon入口函数
 */
function OnMain(){
	//设置游戏初始化参数
    var setting = new Setting();
        setting.fps = 30;
	    setting.showFps = true;

       //注册游戏设置
       register(setting);
};

/**
 * 游戏画布刷新时将调用此函数(手动API渲染用,如果useCanvasUpdate为false则无效)
 */
function OnUpdate(/*loon.Renderer*/render,/*float*/elapsed){
         render.drawText("loon-min-0.3.3",context().width/2,context().height/2);
}
