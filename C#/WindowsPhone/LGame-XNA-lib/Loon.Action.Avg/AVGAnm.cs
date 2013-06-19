namespace Loon.Action.Avg
{
    using System;
    using System.IO;
    using System.Collections.Generic;
    using Loon.Core.Geom;
    using Loon.Core;
    using Loon.Core.Graphics.Opengl;
    using Loon.Core.Graphics;
    using Loon.Core.Resource;
    using Loon.Utils;
    using Loon.Java;
    using Loon.Action.Avg.Drama;

    public class AVGAnm : Expression, LRelease
    {

        internal readonly Point.Point2i point = new Point.Point2i();

        private string path;

        internal float alpha = 1.0f;

        internal float angle;

        internal int width, height, imageWidth, imageHeight;

        internal List<Int32> posxTmps = new List<Int32>();

        internal List<Int32> posyTmps = new List<Int32>();

        internal int[] posx = null;

        internal int[] posy = null;

        internal List<Int32> time = new List<Int32>();

        internal int tmp_time = 20;

        internal int alltime = 0;

        internal int count = 0;

        internal long startTime = -1;

        internal bool loop = true, load = false;

        internal LTexture texture;

        internal LColor color;

        public AVGAnm(string resName)
            : this(Resources.OpenStream(resName))
        {
        }

        public AVGAnm(Stream ins0)
        {
            Open(ins0);
        }

        public void Open(Stream ins0)
        {
            try
            {
                StreamReader reader = new StreamReader(ins0, System.Text.Encoding.UTF8);
                string script = null;
                for (; (script = reader.ReadLine()) != null; )
                {
                    if (script.Length > 0 && !script.StartsWith(FLAG_L_TAG)
                            && !script.StartsWith(FLAG_C_TAG)
                            && !script.StartsWith(FLAG_I_TAG))
                    {
                        string[] element = StringUtils.Split(script, ";");
                        for (int j = 0; j < element.Length; j++)
                        {
                            Load(element[j]);
                        }
                    }
                }
            }
            catch (Exception ex)
            {
                this.load = false;
                Loon.Utils.Debugging.Log.Exception(ex);
                return;
            }
            this.load = true;
            this.count = posxTmps.Count;
            this.posx = new int[count];
            this.posy = new int[count];
            for (int i = 0; i < count; i++)
            {
                this.posx[i] = (int)(posxTmps[i]);
                this.posy[i] = (int)(posyTmps[i]);
            }
            if (width == 0)
            {
                width = imageWidth;
            }
            if (height == 0)
            {
                height = imageHeight;
            }
        }

        private void Load(string script)
        {
            string[] op = StringUtils.Split(script, "=");
            if (op.Length == 2)
            {
                string key = op[0].Trim();
                string value_ren = op[1].Trim();
                if ("path".Equals(key, StringComparison.InvariantCultureIgnoreCase))
                {
                    path = value_ren.Replace("\"", "");
                    if (texture != null)
                    {
                        texture.Destroy();
                        texture = null;
                    }
                    if (GLEx.Self != null)
                    {
                        texture = new LTexture(path);
                        imageWidth = texture.GetWidth();
                        imageHeight = texture.GetHeight();
                    }
                }
                if ("imagewidth".Equals(key, StringComparison.InvariantCultureIgnoreCase))
                {
                    if (MathUtils.IsNan(value_ren))
                    {
                        imageWidth = Int32.Parse(value_ren);
                    }
                }
                else if ("alpha".Equals(key, StringComparison.InvariantCultureIgnoreCase))
                {
                    if (MathUtils.IsNan(value_ren))
                    {
                        alpha = Single.Parse(value_ren, JavaRuntime.NumberFormat);
                    }
                }
                else if ("angle".Equals(key, StringComparison.InvariantCultureIgnoreCase))
                {
                    if (MathUtils.IsNan(value_ren))
                    {
                        angle = Single.Parse(value_ren, JavaRuntime.NumberFormat);
                    }
                }
                else if ("color".Equals(key, StringComparison.InvariantCultureIgnoreCase))
                {
                    string[] p = StringUtils.Split(value_ren, ",");
                    if (p.Length > 2 && p.Length < 5)
                    {
                        for (int i = 0; i < p.Length; i++)
                        {
                            p[i] = p[i].Replace("^[\\t ]*", "").Replace(
                                    "[\\t ]*$", "");
                        }
                        if (p.Length == 3)
                        {
                            color = new LColor(Int32.Parse(p[0]),
                                    Int32.Parse(p[1]), Int32.Parse(p[2]));
                        }
                        if (p.Length == 4)
                        {
                            color = new LColor(Int32.Parse(p[0]),
                                    Int32.Parse(p[1]), Int32.Parse(p[2]),
                                    Int32.Parse(p[3]));
                        }
                    }
                }
                else if ("imageheight".Equals(key, StringComparison.InvariantCultureIgnoreCase))
                {
                    if (MathUtils.IsNan(value_ren))
                        imageHeight = Int32.Parse(value_ren);
                }
                else if ("width".Equals(key, StringComparison.InvariantCultureIgnoreCase))
                {
                    if (MathUtils.IsNan(value_ren))
                    {
                        width = Int32.Parse(value_ren);
                    }
                }
                else if ("height".Equals(key, StringComparison.InvariantCultureIgnoreCase))
                {
                    if (MathUtils.IsNan(value_ren))
                    {
                        height = Int32.Parse(value_ren);
                    }
                }
                else if ("time".Equals(key, StringComparison.InvariantCultureIgnoreCase))
                {
                    if (MathUtils.IsNan(value_ren))
                    {
                        tmp_time = Int32.Parse(value_ren);
                    }
                }
                else if ("pos".Equals(key, StringComparison.InvariantCultureIgnoreCase))
                {
                    string[] p_0 = StringUtils.Split(value_ren, ",");
                    for (int i_1 = 0; i_1 < p_0.Length; i_1++)
                    {
                        p_0[i_1] = p_0[i_1].Replace("^[\\t ]*", "").Replace(
                                "[\\t ]*$", "");
                    }
                    switch (p_0.Length)
                    {
                        case 1:
                            if (MathUtils.IsNan(p_0[0]))
                            {
                                CollectionUtils.Add(posxTmps, Int32.Parse(p_0[0]));
                                CollectionUtils.Add(posyTmps, Int32.Parse(p_0[0]));
                                CollectionUtils.Add(time, tmp_time);
                                alltime += tmp_time;
                            }
                            break;
                        case 2:
                            if (MathUtils.IsNan(p_0[0]) && MathUtils.IsNan(p_0[1]))
                            {
                                CollectionUtils.Add(posxTmps, Int32.Parse(p_0[0]));
                                CollectionUtils.Add(posyTmps, Int32.Parse(p_0[1]));
                                CollectionUtils.Add(time, tmp_time);
                                alltime += tmp_time;
                            }
                            break;
                    }
                }
            }
        }

        public string GetPath()
        {
            return path;
        }

        public int GetWidth()
        {
            return width;
        }

        public void SetWidth(int width_0)
        {
            this.width = width_0;
        }

        public int GetHeight()
        {
            return height;
        }

        public void SetHeight(int height_0)
        {
            this.height = height_0;
        }

        public int GetImageWidth()
        {
            return imageWidth;
        }

        public void SetImageWidth(int imageWidth_0)
        {
            this.imageWidth = imageWidth_0;
        }

        public int GetImageHeight()
        {
            return imageHeight;
        }

        public void SetImageHeight(int imageHeight_0)
        {
            this.imageHeight = imageHeight_0;
        }

        public int[] GetPosx()
        {
            return posx;
        }

        public int[] GetPosy()
        {
            return posy;
        }

        public List<Int32> GetTime()
        {
            return time;
        }

        public int GetAlltime()
        {
            return alltime;
        }

        public int GetCount()
        {
            return count;
        }

        public bool IsLoop()
        {
            return loop;
        }

        public LTexture GetTexture()
        {
            return texture;
        }

        public float GetAlpha()
        {
            return alpha;
        }

        public float GetAngle()
        {
            return angle;
        }

        public void Start(long elapsedTime, bool loop_0)
        {
            this.startTime = elapsedTime;
            this.loop = loop_0;
            if (texture != null)
            {
                if (texture.GetWidth() == imageWidth
                        && texture.GetHeight() == imageHeight)
                {
                    this.loop = false;
                }
            }
        }

        public void Start()
        {
            this.Start(0, loop);
        }

        public void Stop()
        {
            this.startTime = -1;
            this.loop = false;
        }

        public Point.Point2i GetPos(long elapsedTime)
        {
            if (startTime != -1)
            {
                int frame = GetFrame(elapsedTime);
                point.x = posx[frame];
                point.y = posy[frame];
            }
            return point;
        }

        private int GetFrame(long elapsedTime)
        {
            long diff = elapsedTime - startTime;
            if (!loop && diff >= alltime)
            {
                startTime = -1;
                return 0;
            }
            long now = diff % alltime;
            int t = 0;
            for (int i = 0; i < count; i++)
            {
                t += time[i];
                if (now < t)
                {
                    return i;
                }
            }
            return 0;
        }

        public void Dispose()
        {
            Stop();
            if (texture != null)
            {
                texture.Destroy();
                texture = null;
            }
        }

    }

}
