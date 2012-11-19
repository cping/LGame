using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Loon.Core.Graphics;
using Loon.Core.Timer;
using Loon.Action.Sprite;
using Loon.Core.Graphics.Component;
using Loon.Core.Graphics.OpenGL;
using Loon.Core.Resource;
using Loon.Java;
using Loon.Utils;
using Loon.Core.Input;
using Loon.Java.Collections;

namespace LLK_Sample
{
    public class LLKScreen : Screen
    {

        private const String SORRY = "抱歉";

        private const String START_MES = "游戏开始！", SORRY1_MES = SORRY
               + ", <r刷新/> 在目前使用了。", SORRY2_MES = SORRY + ", <r提示/> 在目前无法使用了。",
               SORRY3_MES = SORRY + ", <r炸弹/> 在目前无法使用了。", EASY_MES = "好的，这非常容易～";

        private const String WAIT_MES = "预备……", HELP_MES = "我能为你提供什么服务吗？";

        private int bomb_number, refresh_number, tip_number, progress_number;

        private int xBound;

        private int yBound;

        private int pcount;

        private int refreshcount;

        private int bombcount;

        private int tipcount;

        private int sub;

        private LTimer timer, timer1;

        private Level levelInfo;

        private Grid[][] grid;

        private Grid nexts;

        private Grid nexte;

        private LinkedList[] path;

        private StatusBar progress;

        private Label stage, time;

        private Picture role;

        private LPaper title, over;

        private Grid prev;

        private LMessage mes;

        private LSelect select;

        private Sprite helpRole;

        private bool wingame, failgame, init, overFlag;

        private int stageNo, count;

        public int offsetX, offsetY;

        public LLKScreen()
        {

        }

        public LTexture GetImage(int i)
        {
            return images[i];
        }

        private static LTexture[] images;

        public override void OnLoad()
        {

            images = new LTexture[17];
            for (int i = 0; i < 8; i++)
            {
                images[i] = new LTexture("assets/" + i + ".jpg");
            }

            String res = "assets/res.lpk";

            images[8] = LPKResource.OpenTexture(res, "a0.png");
            images[9] = LPKResource.OpenTexture(res, "dot.png");
            images[10] = LPKResource.OpenTexture(res, "background.jpg");
            images[11] = LPKResource.OpenTexture(res, "role0.png");
            images[12] = LPKResource.OpenTexture(res, "role1.png");
            images[13] = LPKResource.OpenTexture(res, "role2.png");
            images[14] = LPKResource.OpenTexture(res, "win.png");
            images[15] = LPKResource.OpenTexture(res, "start.png");
            images[16] = LPKResource.OpenTexture(res, "gameover.png");
            SetBackground(GetImage(10));
            Stage(1);
        }

        private class AnimateThread : Thread
        {

            private LinkedList v;

            public AnimateThread(LinkedList temp)
            {
                v = temp;
            }

            public override void Run()
            {
                LLKScreen screen = (LLKScreen)StaticCurrentSceen;
                Grid prev = null;
                for (int j = 0; j < v.Count; j++)
                {
                    prev = (Grid)v.Poll();
                    prev.SetVisible(true);
                    v.Add(prev);
                    try
                    {
                        Sleep(20L);
                    }
                    catch (Exception)
                    {
                    }
                }
                Grid current = prev;
                prev = (Grid)v.Poll();
                while (!v.IsEmpty())
                {
                    Grid o = (Grid)v.Poll();
                    o.SetVisible(false);
                    try
                    {
                        Sleep(20L);
                    }
                    catch (Exception)
                    {
                    }
                }
                prev.SetVisible(false);
                current.SetVisible(false);
                current.SetImage(screen.GetImage(9));
                prev.SetImage(screen.GetImage(9));
                current.SetBorder(1);
                prev.SetBorder(1);
                if (!screen.FindPair())
                {
                    screen.Refreshs();
                }
            }

        }

        private void Reset()
        {
            overFlag = false;
            failgame = false;
            if (path == null)
            {
                path = new LinkedList[3];
            }
            path[0] = new LinkedList();
            path[1] = new LinkedList();
            path[2] = new LinkedList();
            init = false;
            count = 0;
            if (progress != null)
            {
                progress.Set(progress_number);
            }
            InitUI();
        }

        private void Stage(int no)
        {
            switch (no)
            {
                case 1:
                    stageNo = 1;
                    sub = 4;
                    bomb_number = 3;
                    refresh_number = 3;
                    tip_number = 3;
                    progress_number = 3200;
                    levelInfo = new Level(4, 4);
                    break;
                case 2:
                    stageNo = 2;
                    sub = 6;
                    bomb_number = 2;
                    refresh_number = 2;
                    tip_number = 2;
                    progress_number = 6400;
                    levelInfo = new Level(6, 4);
                    break;
                case 3:
                    stageNo = 3;
                    sub = 6;
                    bomb_number = 1;
                    refresh_number = 1;
                    tip_number = 1;
                    progress_number = 5400;
                    levelInfo = new Level(6, 4);
                    break;
                case 4:
                    stageNo = 4;
                    sub = 6;
                    bomb_number = 1;
                    refresh_number = 1;
                    tip_number = 1;
                    progress_number = 10800;
                    levelInfo = new Level(6, 5);
                    break;
                case 5:
                    stageNo = 5;
                    sub = 8;
                    bomb_number = 1;
                    refresh_number = 1;
                    tip_number = 1;
                    progress_number = 12400;
                    levelInfo = new Level(8, 5);
                    break;
                default:
                    stageNo++;
                    sub = 20;
                    bomb_number = 1;
                    refresh_number = 1;
                    tip_number = 1;
                    progress_number = 19400;
                    levelInfo = new Level(8, 5);
                    break;
            }
            Reset();
        }

        public override void Dispose()
        {

        }

        class _Runnable : Runnable
        {

            LLKScreen screen;

            LMessage message;

            public _Runnable(LLKScreen s, LMessage m)
            {
                this.screen = s;
                this.message = m;
            }


            public void Run()
            {

                screen.stage = new Label("Stage - " + screen.stageNo, 140, 25);
                screen.stage.SetColor(LColor.black);
                screen.stage.SetFont(LFont.GetDefaultFont());
                screen.Add(screen.stage);
                screen.time = new Label("time", 270, 25);
                screen.time.SetColor(LColor.black);
                screen.time.SetFont(LFont.GetDefaultFont());
                screen.Add(screen.time);
                message.SetVisible(false);
                screen.role.SetVisible(false);
                message.SetVisible(false);
                screen.init = true;
                screen.count = 0;

                screen.progress = new StatusBar(screen.progress_number,
                        screen.progress_number, 325, 5, 150, 25);
                screen.progress.SetDead(true);
                screen.Add(screen.progress);
                if (screen.title == null)
                {
                    screen.title = new LPaper(screen.GetImage(15), 55, 55);
                }
                else
                {
                    screen.title.SetLocation(55, 55);
                }
                screen.CenterOn(screen.title);
                screen.Add(screen.title);
                if (screen.stageNo < 5)
                {
                    if (screen.helpRole == null)
                    {
                        screen.helpRole = new Sprite(screen.GetImage(8));
                        screen.helpRole.SetLocation(screen
                                .GetWidth()
                                - screen.helpRole.GetWidth() - 10,
                                screen.GetHeight()
                                        - screen.helpRole.GetHeight()
                                        - 10);
                        screen.Add(screen.helpRole);
                    }
                    else
                    {
                        screen.helpRole.SetVisible(true);
                        screen.Add(screen.helpRole);
                    }
                }
                else
                {
                    if (screen.helpRole != null)
                    {
                        screen.helpRole.SetVisible(false);
                    }
                }

            }
        }


        class SelectClick : ClickListener
        {

            public void DownClick(LComponent comp, float x, float y)
            {
                if (comp.Tag is Screen)
                {
                    LLKScreen screen = (LLKScreen)comp.Tag;
                    LSelect select = (LSelect)comp;

                    switch (select.GetResultIndex())
                    {
                        case 0:
                            screen.mes.SetVisible(true);
                            if (screen.refreshcount > 0)
                            {
                                screen.mes.SetMessage(EASY_MES);
                                screen.Refreshs();
                            }
                            else
                            {
                                screen.mes.SetMessage(SORRY1_MES);
                            }
                            screen.Remove(select);
                            break;
                        case 1:
                            screen.mes.SetVisible(true);
                            if (screen.tipcount > 0)
                            {
                                screen.mes.SetMessage(EASY_MES);
                                screen.ShowNext();
                            }
                            else
                            {
                                screen.mes.SetMessage(SORRY2_MES);
                            }
                            screen.Remove(select);
                            break;
                        case 2:
                            screen.mes.SetVisible(true);
                            if (screen.bombcount > 0)
                            {
                                screen.mes.SetMessage(EASY_MES);
                                screen.UseBomb();
                            }
                            else
                            {
                                screen.mes.SetMessage(SORRY3_MES);
                            }
                            screen.Remove(select);
                            break;
                        case 3:
                            screen.mes.SetVisible(true);
                            screen.Remove(select);
                            screen.mes.SetVisible(false);
                            screen.role.SetVisible(false);
                            screen.helpRole.SetVisible(true);
                            if (screen.stage != null)
                            {
                                screen.stage.SetVisible(true);
                            }
                            break;
                        default:
                            break;
                    }
                }

            }

            public void UpClick(LComponent comp, float x, float y)
            {

            }

            public void DragClick(LComponent comp, float x, float y)
            {

            }
        }

        class StartClick : ClickListener
        {


            public void DownClick(LComponent comp, float x, float y)
            {
                if (comp.Tag is LLKScreen)
                {
                    LMessage message = (LMessage)comp;
                    LLKScreen screen = (LLKScreen)comp.Tag;
                    if (!screen.init)
                    {
                        if (screen.count == 0)
                        {
                            screen.role.SetImage(screen.GetImage(12));
                            message.SetMessage(START_MES);
                        }
                        else if (message.IsComplete())
                        {

                            screen.CallEvent(new _Runnable(screen, message));

                        }
                        screen.count++;
                    }

                    if (HELP_MES.Equals(message.GetMessage(), StringComparison.InvariantCultureIgnoreCase) && message.IsComplete())
                    {
                        message.SetVisible(false);
                        screen.select = new LSelect(screen.GetImage(14), (screen
                                .GetWidth() - 460) / 2,
                                screen.GetHeight() - 126 - 10);
                        screen.select.Tag = screen;
                        screen.select.SetFontColor(LColor.black);
                        screen.select.SetAlpha(0.8f);
                        screen.select.SetTopOffset(-5);
                        screen.select.SetMessage(new String[] { "1.刷新", "2.提示", "3.炸弹",
							"4.取消" });
                        screen.select.Click = new SelectClick();
                        screen.Add(screen.select);
                        return;

                    }
                    else if ((EASY_MES.Equals(message.GetMessage(), StringComparison.InvariantCultureIgnoreCase) || message.GetMessage()
                          .StartsWith(SORRY))
                          && message.IsComplete())
                    {

                        screen.mes.SetVisible(false);
                        screen.role.SetVisible(false);
                        screen.helpRole.SetVisible(true);
                        if (screen.stage != null)
                        {
                            screen.stage.SetVisible(true);
                        }
                    }
                }
            }



            public void UpClick(LComponent comp, float x, float y)
            {

            }

            public void DragClick(LComponent comp, float x, float y)
            {

            }


        }

        private void InitRole()
        {
            role = new Picture(GetImage(11));
            mes = new LMessage(GetImage(14), (GetWidth() - 460) / 2,
                    GetHeight() - 126 - 10);
            mes.SetMessageLength(20);
            mes.SetAlpha(0.8f);
            mes.SetFontColor(LColor.black);
            mes.SetMessage(WAIT_MES);
            mes.Tag = this;
            mes.Click = new StartClick();
            Add(role);
            Add(mes);

        }

        private void InitUI()
        {

            xBound = levelInfo.GetXBound() + 2;
            yBound = levelInfo.GetYBound() + 2;

            grid = (Grid[][])CollectionUtils.XNA_CreateJaggedArray(typeof(Grid), yBound, xBound);
            int count = 0;

            Grid[] temp = new Grid[xBound * yBound];
            for (int y = 0; y < yBound; y++)
            {
                for (int x = 0; x < xBound; x++)
                {
                    grid[y][x] = new Grid(x, y);
                    if (x == 0 || x == xBound - 1 || y == 0 || y == yBound - 1)
                    {
                        LTexture img = GetImage(count % sub);
                        int nx = offsetX + x * img.GetWidth();
                        int ny = offsetY + y * img.GetHeight();

                        grid[y][x].SetLocation(nx, ny);
                        grid[y][x].SetImage(GetImage(9));
                        grid[y][x].SetVisible(false);
                    }
                    else
                    {
                        LTexture img = GetImage(count % sub);
                        grid[y][x].SetImage(img);
                        grid[y][x].SetBorder(3);
                        int nx = offsetX + x * img.GetWidth();
                        int ny = offsetY + y * img.GetHeight();

                        grid[y][x].SetLocation(nx, ny);
                        temp[count] = grid[y][x];
                        count++;
                    }
                    GetSprites().Add(grid[y][x]);
                }

            }

            Shuffle(temp, count);
            wingame = false;
            tipcount = tip_number;
            bombcount = bomb_number;
            refreshcount = refresh_number;
            InitRole();
        }

        public void SetPaused(bool p)
        {
            if (p)
            {
                GetSprites().SetVisible(false);
            }
            else
            {
                GetSprites().SetVisible(true);
            }
        }

        public bool IsWait()
        {
            bool result = false;
            if (role != null)
            {
                result = role.IsVisible();
            }
            return result;
        }

        private void Shuffle(Grid[] array, int count)
        {
            if (wingame)
            {
                return;
            }
            int number = 0;
            do
            {
                GetSprites().SetVisible(false);
                for (int i = 0; i < count; i++)
                {
                    int j = (int)(MathUtils.Random() * (double)count);
                    int k = (int)(MathUtils.Random() * (double)count);
                    LTexture temp = array[k].GetBitmap();

                    array[k].SetImage(array[j].GetBitmap());
                    array[j].SetImage(temp);
                }

                GetSprites().SetVisible(true);
                number++;
                if (number > 5)
                {
                    wingame = true;
                    break;
                }
            } while (!FindPair());
        }

        public void Refreshs()
        {
            if (wingame || progress.GetValue() == 0)
            {
                return;
            }
            if (progress != null)
            {
                progress.Set(progress_number);
            }
            wingame = false;
            overFlag = false;
            failgame = false;
            init = false;
            Grid[] temp = new Grid[xBound * yBound];
            int count = 0;
            for (int y = 1; y < yBound - 1; y++)
            {
                for (int x = 1; x < xBound - 1; x++)
                    if (grid[y][x].IsVisible())
                    {
                        int nx = offsetX + x * grid[y][x].GetWidth();
                        int ny = offsetY + y * grid[y][x].GetHeight();
                        grid[y][x].SetLocation(nx, ny);
                        grid[y][x].SetBorder(3);
                        temp[count] = grid[y][x];
                        count++;
                    }

            }
            if (count != 0)
            {
                refreshcount--;
                Shuffle(temp, count);
            }
            else
            {
                wingame = true;
            }
        }

        private bool Xdirect(Grid start, Grid end, LinkedList path)
        {
            if (start.GetYpos() != end.GetYpos())
                return false;
            int direct = 1;
            if (start.GetXpos() > end.GetXpos())
            {
                direct = -1;
            }
            path.Clear();
            for (int x = start.GetXpos() + direct; x != end.GetXpos() && x < xBound
                    && x >= 0; x += direct)
            {
                if (grid[start.GetYpos()][x].IsVisible())
                {
                    return false;
                }
                path.Add(grid[start.GetYpos()][x]);
            }

            path.Add(end);
            return true;
        }

        private bool Ydirect(Grid start, Grid end, LinkedList path)
        {
            if (start.GetXpos() != end.GetXpos())
            {
                return false;
            }
            int direct = 1;
            if (start.GetYpos() > end.GetYpos())
            {
                direct = -1;
            }
            path.Clear();
            for (int y = start.GetYpos() + direct; y != end.GetYpos() && y < yBound
                    && y >= 0; y += direct)
            {
                if (grid[y][start.GetXpos()].IsVisible())
                {
                    return false;
                }
                path.Add(grid[y][start.GetXpos()]);
            }

            path.Add(end);
            return true;
        }

        private int FindPath(Grid start, Grid end)
        {
            if (Xdirect(start, end, path[0]))
            {
                return 1;
            }
            if (Ydirect(start, end, path[0]))
            {
                return 1;
            }
            Grid xy = grid[start.GetYpos()][end.GetXpos()];
            if (!xy.IsVisible() && Xdirect(start, xy, path[0])
                    && Ydirect(xy, end, path[1]))
            {
                return 2;
            }
            Grid yx = grid[end.GetYpos()][start.GetXpos()];
            if (!yx.IsVisible() && Ydirect(start, yx, path[0])
                    && Xdirect(yx, end, path[1]))
            {
                return 2;
            }
            path[0].Clear();
            for (int y = start.GetYpos() - 1; y >= 0; y--)
            {
                xy = grid[y][start.GetXpos()];
                yx = grid[y][end.GetXpos()];
                if (xy.IsVisible())
                {
                    break;
                }
                path[0].Add(xy);
                if (!yx.IsVisible() && Xdirect(xy, yx, path[1])
                        && Ydirect(yx, end, path[2]))
                {
                    return 3;
                }
            }

            path[0].Clear();
            for (int y = start.GetYpos() + 1; y < yBound; y++)
            {
                xy = grid[y][start.GetXpos()];
                yx = grid[y][end.GetXpos()];
                if (xy.IsVisible())
                {
                    break;
                }
                path[0].Add(xy);
                if (!yx.IsVisible() && Xdirect(xy, yx, path[1])
                        && Ydirect(yx, end, path[2]))
                {
                    return 3;
                }
            }

            path[0].Clear();
            for (int x = start.GetXpos() - 1; x >= 0; x--)
            {
                yx = grid[start.GetYpos()][x];
                xy = grid[end.GetYpos()][x];
                if (yx.IsVisible())
                {
                    break;
                }
                path[0].Add(yx);
                if (!xy.IsVisible() && Ydirect(yx, xy, path[1])
                        && Xdirect(xy, end, path[2]))
                {
                    return 3;
                }
            }

            path[0].Clear();
            for (int x = start.GetXpos() + 1; x < xBound; x++)
            {
                yx = grid[start.GetYpos()][x];
                xy = grid[end.GetYpos()][x];
                if (yx.IsVisible())
                {
                    break;
                }
                path[0].Add(yx);
                if (!xy.IsVisible() && Ydirect(yx, xy, path[1])
                        && Xdirect(xy, end, path[2]))
                {
                    return 3;
                }
            }

            return 0;
        }

        private void DeletePair(Grid prev, Grid current)
        {
            LinkedList temp = new LinkedList();
            temp.Add(prev);
            for (int i = 0; i < pcount; i++)
            {
                temp.AddAll(path[i]);
                path[i].Clear();
            }
            AnimateThread thread = new AnimateThread(temp);
            thread.Tag = this;
            thread.Start();
        }

        public void ShowNext()
        {
            if (wingame || progress.GetValue() == 0 || tipcount == 0)
            {
                return;
            }
            tipcount--;
            if (nexts != null && nexte != null)
            {
                nexts.SetBorder(2);
                nexte.SetBorder(2);
            }
        }

        public void UseBomb()
        {
            if (wingame || progress.GetValue() == 0 || bombcount == 0)
            {
                return;
            }
            bombcount--;
            if (nexts != null && nexte != null)
            {
                DeletePair(nexts, nexte);
            }
        }

        private bool FindPair()
        {
            nexts = null;
            nexte = null;
            for (int sy = 1; sy < yBound - 1; sy++)
            {
                for (int sx = 1; sx < xBound - 1; sx++)
                    if (grid[sy][sx].IsVisible())
                    {
                        for (int ey = sy; ey < yBound - 1; ey++)
                        {
                            for (int ex = 1; ex < xBound - 1; ex++)
                                if (grid[ey][ex].IsVisible()
                                        && (ey != sy || ex != sx)
                                        && grid[sy][sx].Equals(grid[ey][ex]))
                                {
                                    pcount = FindPath(grid[sy][sx], grid[ey][ex]);
                                    if (pcount != 0)
                                    {
                                        nexts = grid[sy][sx];
                                        nexte = grid[ey][ex];
                                        return true;
                                    }
                                }

                        }

                    }

            }

            return false;
        }



        public override void Alter(LTimerContext t)
        {

            if (IsWait())
            {
                return;
            }
            if (timer1 == null)
            {
                timer1 = new LTimer(50);
            }
            if (title != null && timer1.Action(t.GetTimeSinceLastUpdate()))
            {
                if (title.GetY() > 50)
                {
                    title.Move_up(8);
                    title.ValidatePosition();
                }
                else if (title.GetAlpha() > 0.2f)
                {
                    title.SetAlpha(title.GetAlpha() - 0.1f);
                }
                else
                {
                    title.SetVisible(false);
                    Remove(title);
                    title = null;
                }
                return;
            }
            else if (over != null && timer1.Action(t.GetTimeSinceLastUpdate())
                  && !overFlag)
            {
                if (over.GetY() < (GetHeight() - over.GetHeight()) / 2)
                {
                    over.Move_down(8);
                    over.ValidatePosition();
                }
                else if (over.GetAlpha() < 1.0f)
                {
                    over.SetAlpha(over.GetAlpha() + 0.1f);
                }
                else
                {
                    CenterOn(over);
                    overFlag = true;
                }

                return;
            }
            if (!wingame)
            {
                if (timer == null)
                {
                    timer = new LTimer(100);
                }
                if (timer.Action(t.GetTimeSinceLastUpdate()))
                {
                    if (progress != null)
                    {
                  
                        progress.SetUpdate(progress.GetValue() - (stageNo*30));
                        if (progress.GetValue() <= 100 && !failgame)
                        {

                            failgame = true;
                            GetSprites().SetVisible(false);

                            over = new LPaper(GetImage(16), 0, 0);
                            over.SetAlpha(0.1f);
                            CenterOn(over);
                            over.SetY(0);
                            Add(over);
                        }
                    }

                }

            }
            else
            {
                wingame = false;
                RemoveAll();
                Stage(stageNo + 1);
            }
        }

        public override void Draw(GLEx g)
        {

        }

        private Grid GetGrid(int x, int y)
        {

            Sprites ss = GetSprites();
            if (ss == null)
            {
                return null;
            }
            ISprite[] s = ss.GetSprites();
            for (int i = 0; i < s.Length; i++)
            {
                if (s[i] is Grid)
                {
                    Grid g = (Grid)s[i];
                    if (g.GetCollisionBox().Contains(x, y))
                    {
                        return g;
                    }
                }
            }
            return null;
        }

        class Level
        {

            private int xBound;

            private int yBound;

            public Level()
            {
                xBound = 8;
                yBound = 6;
            }

            public Level(int x, int y)
            {
                xBound = x;
                yBound = y;
            }

            public int GetXBound()
            {
                return xBound;
            }

            public int GetYBound()
            {
                return yBound;
            }
        }
        
		class TouchRunnable : Runnable {

			public void Run() {

                LLKScreen s = (LLKScreen)StaticCurrentSceen;
              
				Grid current = null;
				try {
                    if (s.prev != null)
                    {
						s.prev.SetBorder(3);
					}

					if (s.prev == null) {
                        s.prev = s.GetGrid(s.GetTouchX(), s.GetTouchY());
						if (s.prev != null) {
							s.prev.SetBorder(0);
						}
					} else {
						if (s.progress.GetValue() == 0) {
							return;
						}

						current = s.GetGrid(s.GetTouchX(), s.GetTouchY());
						if (current == s.prev) {
							return;
						}
						if (current == null) {
							s.prev = null;
						}
						if (s.prev == null) {
							return;
						}
						if (current.Equals(s.prev)) {
							if (!s.FindPair()) {
								s.Refreshs();
							}
                            s.pcount = s.FindPath(s.prev, current);
                            if (s.pcount != 0)
                            {
                                s.DeletePair(s.prev, current);
                                s.prev = null;
								return;
							}
						}
                        s.prev.SetBorder(1);
                        s.prev = current;
                        s.prev.SetBorder(0);
                        if (!s.FindPair())
                        {
                            s.Refreshs();
						}
					}
				} catch (Exception) {
                    if (s.prev != null)
                    {
                        s.prev.SetBorder(3);
					}
					if (current != null) {
						current.SetBorder(3);
					}
				}
			}
		}

        public override void TouchDown(LTouch e)
        {
            if (!init)
            {
                return;
            }
            if (failgame)
            {
                return;
            }
            if (wingame || progress.GetValue() == 0)
            {
                return;
            }
            if (nexte != null && nexts != null)
            {
                if (helpRole != null)
                {
                    if (!role.IsVisible() && helpRole.IsVisible())
                    {
                        if (failgame)
                        {
                            return;
                        }
                        if (OnClick(helpRole))
                        {
                            if (stage != null)
                            {
                                stage.SetVisible(false);
                            }
                            helpRole.SetVisible(false);
                            role.SetImage(GetImage(13));
                            role.SetVisible(true);
                            mes.SetMessageLength(20);
                            mes.SetMessage(HELP_MES);
                            mes.SetVisible(true);
                            return;
                        }
                    }
                }
            }
          
            CallEvent(new TouchRunnable());
            return;
        }


        public override void TouchMove(LTouch e)
        {

        }

        public override void TouchUp(LTouch e)
        {

        }

        public override void TouchDrag(LTouch e)
        {

        }
    }
}
