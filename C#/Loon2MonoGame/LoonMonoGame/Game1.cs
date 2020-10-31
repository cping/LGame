using java.lang;
using loon;
using loon.canvas;
using loon.utils;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using Microsoft.Xna.Framework.Input;
using System;

namespace LoonMonoGame
{
    public class Game1 : Game
    {
        private GraphicsDeviceManager _graphics;
        private SpriteBatch _spriteBatch;

        public Game1()
        {
            _graphics = new GraphicsDeviceManager(this);
            Content.RootDirectory = "Content";
            IsMouseVisible = true;

            //1123418112

            JavaSystem.Out.Println(NumberUtils.FloatToIntBits(123f));


            //2079648233
            JavaSystem.Out.Println(LColor.GetARGB(244,233,233,123));
            //ff000000
            JavaSystem.Out.Println(new LColor(LColor.TRANSPARENT));
            //7bf4e9e9
            JavaSystem.Out.Println(new LColor(244, 233, 233, 123));
            //1,2,3,4
            JavaSystem.Out.Println(new LColor(1, 2, 3, 4).GetXNAColor());
        }

        protected override void Initialize()
        {

            // TODO: Add your initialization logic here

            base.Initialize();
        }

        protected override void LoadContent()
        {
            _spriteBatch = new SpriteBatch(GraphicsDevice);

            // TODO: use this.Content to load your game content here
        }

        protected override void Update(GameTime gameTime)
        {
            if (GamePad.GetState(PlayerIndex.One).Buttons.Back == ButtonState.Pressed || Keyboard.GetState().IsKeyDown(Keys.Escape))
                Exit();

            // TODO: Add your update logic here

            base.Update(gameTime);
        }

        protected override void Draw(GameTime gameTime)
        {
            GraphicsDevice.Clear(Color.CornflowerBlue);

            // TODO: Add your drawing code here

            base.Draw(gameTime);
        }
    }
}
