package org.test;

public class LevelMaker {
	
	  private MainGame m_pGame;
	  
      private Ground m_pGround;

      public LevelMaker()
      {
          this.m_pGame = MainGame.get();
          this.m_pGround = m_pGame.GetGround();
      }

      public void CreateLevel(int iLevel, int iWorld)
      {
          if (iWorld == 0)
          {
              if (iLevel == 1)
              {
                  this.CreateLevel01W1();
              }
              else if (iLevel == 2)
              {
                  this.CreateLevel02W1();
              }
              else if (iLevel == 3)
              {
                  this.CreateLevel03W1();
              }
              else if (iLevel == 4)
              {
                  this.CreateLevel04W1();
              }
              else if (iLevel == 5)
              {
                  this.CreateLevel05W1();
              }
              else if (iLevel == 6)
              {
                  this.CreateLevel06W1();
              }
              else if (iLevel == 7)
              {
                  this.CreateLevel07W1();
              }
              else if (iLevel == 8)
              {
                  this.CreateLevel08W1();
              }
              else if (iLevel == 9)
              {
                  this.CreateLevel09W1();
              }
              else if (iLevel == 10)
              {
                  this.CreateLevel10W1();
              }
              else if (iLevel == 11)
              {
                  this.CreateLevel11W1();
              }
              else if (iLevel == 12)
              {
                  this.CreateLevel12W1();
              }
              else if (iLevel == 13)
              {
                  this.CreateLevel13W1();
              }
              else if (iLevel == 14)
              {
                  this.CreateLevel14W1();
              }
              else if (iLevel == 15)
              {
                  this.CreateLevel15W1();
              }
              else if (iLevel == 0x10)
              {
                  this.CreateLevel16W1();
              }
              else if (iLevel == 0x11)
              {
                  this.CreateLevel17W1();
              }
              else if (iLevel == 0x12)
              {
                  this.CreateLevel18W1();
              }
              else if (iLevel == 0x13)
              {
                  this.CreateLevel19W1();
              }
              else if (iLevel == 20)
              {
                  this.CreateLevel20W1();
              }
              else if (iLevel == 0x15)
              {
                  this.CreateLevel21W1();
              }
          }
          else if (iWorld == 1)
          {
              if (iLevel == 1)
              {
                  this.CreateLevel01W2();
              }
              else if (iLevel == 2)
              {
                  this.CreateLevel02W2();
              }
              else if (iLevel == 3)
              {
                  this.CreateLevel03W2();
              }
              else if (iLevel == 4)
              {
                  this.CreateLevel04W2();
              }
              else if (iLevel == 5)
              {
                  this.CreateLevel05W2();
              }
              else if (iLevel == 6)
              {
                  this.CreateLevel06W2();
              }
              else if (iLevel == 7)
              {
                  this.CreateLevel07W2();
              }
              else if (iLevel == 8)
              {
                  this.CreateLevel08W2();
              }
              else if (iLevel == 9)
              {
                  this.CreateLevel09W2();
              }
              else if (iLevel == 10)
              {
                  this.CreateLevel10W2();
              }
              else if (iLevel == 11)
              {
                  this.CreateLevel11W2();
              }
              else if (iLevel == 12)
              {
                  this.CreateLevel12W2();
              }
              else if (iLevel == 13)
              {
                  this.CreateLevel13W2();
              }
              else if (iLevel == 14)
              {
                  this.CreateLevel14W2();
              }
              else if (iLevel == 15)
              {
                  this.CreateLevel15W2();
              }
              else if (iLevel == 0x10)
              {
                  this.CreateLevel16W2();
              }
              else if (iLevel == 0x11)
              {
                  this.CreateLevel17W2();
              }
              else if (iLevel == 0x12)
              {
                  this.CreateLevel18W2();
              }
              else if (iLevel == 0x13)
              {
                  this.CreateLevel19W2();
              }
              else if (iLevel == 20)
              {
                  this.CreateLevel20W2();
              }
              else if (iLevel == 0x15)
              {
                  this.CreateLevel21W2();
              }
          }
          else if (iWorld == 2)
          {
              if (iLevel == 1)
              {
                  this.CreateLevel01W3();
              }
              else if (iLevel == 2)
              {
                  this.CreateLevel02W3();
              }
              else if (iLevel == 3)
              {
                  this.CreateLevel03W3();
              }
              else if (iLevel == 4)
              {
                  this.CreateLevel04W3();
              }
              else if (iLevel == 5)
              {
                  this.CreateLevel05W3();
              }
              else if (iLevel == 6)
              {
                  this.CreateLevel06W3();
              }
              else if (iLevel == 7)
              {
                  this.CreateLevel07W3();
              }
              else if (iLevel == 8)
              {
                  this.CreateLevel08W3();
              }
              else if (iLevel == 9)
              {
                  this.CreateLevel09W3();
              }
              else if (iLevel == 10)
              {
                  this.CreateLevel10W3();
              }
              else if (iLevel == 11)
              {
                  this.CreateLevel11W3();
              }
              else if (iLevel == 12)
              {
                  this.CreateLevel12W3();
              }
              else if (iLevel == 13)
              {
                  this.CreateLevel13W3();
              }
              else if (iLevel == 14)
              {
                  this.CreateLevel14W3();
              }
              else if (iLevel == 15)
              {
                  this.CreateLevel15W3();
              }
              else if (iLevel == 0x10)
              {
                  this.CreateLevel16W3();
              }
              else if (iLevel == 0x11)
              {
                  this.CreateLevel17W3();
              }
              else if (iLevel == 0x12)
              {
                  this.CreateLevel18W3();
              }
              else if (iLevel == 0x13)
              {
                  this.CreateLevel19W3();
              }
              else if (iLevel == 20)
              {
                  this.CreateLevel20W3();
              }
              else if (iLevel == 0x15)
              {
                  this.CreateLevel21W3();
              }
          }
      }

      private void CreateLevel01W1()
      {
          this.m_pGround.SetSize(30);
          this.m_pGame.CreateObstacle(10, 0, 5, 4, 0);
          this.m_pGame.CreateStar(12);
          this.m_pGame.CreateStar(14);
          this.m_pGame.CreateStar(0x10);
          this.m_pGame.CreateObstacle(20, 0, 5, 4, 0);
          this.m_pGame.CreateStar(0x16);
          this.m_pGame.CreateStar(0x18);
      }

      private void CreateLevel01W2()
      {
          this.m_pGround.SetSize(0x37);
          this.m_pGame.CreateStar(7);
          this.m_pGame.CreateStar(11, 4);
          this.m_pGame.CreateObstacle(10, 0, 11, 13, 0);
          this.m_pGame.CreateStar(0x10);
          this.m_pGame.CreateObstacle(0x12, 1, 5, 9, 0);
          this.m_pGame.CreateStar(20, 4);
          this.m_pGame.CreateObstacle(0x15, 1, 6, 8, 0);
          this.m_pGame.CreateStar(0x18);
          this.m_pGame.CreateObstacle(0x1a, 2, 0x15, 2, 0);
          this.m_pGame.CreateObstacle(0x1c, 0, 9, 9, 2);
          this.m_pGame.CreateStar(0x21);
          this.m_pGame.CreateObstacle(0x23, 3, 6, 8, 0);
          this.m_pGame.CreateStar(0x26);
          this.m_pGame.CreateStar(40);
          this.m_pGame.CreateObstacle(0x2a, 1, 6, 8, 0);
          this.m_pGame.CreateStar(0x2e);
          this.m_pGame.CreateStar(0x30);
          this.m_pGame.CreateStar(50);
      }

      private void CreateLevel01W3()
      {
          this.m_pGround.SetSize(0x41);
          this.m_pGame.CreateStar(7);
          this.m_pGame.CreateStar(9);
          this.m_pGame.CreateStar(11, 2);
          this.m_pGame.CreateObstacle(10, 1, 5, 6, 0);
          this.m_pGame.CreateObstacle(11, 2, 5, 4, 0);
          this.m_pGame.CreateStar(13);
          this.m_pGame.CreateStar(15);
          this.m_pGame.CreateStar(0x11, 2);
          this.m_pGame.CreateObstacle(0x10, 3, 5, 4, 0);
          this.m_pGame.CreateObstacle(0x12, 0, 5, 6, 0);
          this.m_pGame.CreateStar(0x15);
          this.m_pGame.CreateStar(0x17);
          this.m_pGame.CreateStar(0x19, 3);
          this.m_pGame.CreateObstacle(0x18, 3, 5, 9, 0);
          this.m_pGame.CreateObstacle(0x1a, 0, 5, 4, 0);
          this.m_pGame.CreateStar(0x1c);
          this.m_pGame.CreateStar(30);
          this.m_pGame.CreateStar(0x20);
          this.m_pGame.CreateObstacle(0x21, 0, 0x15, 2, 0);
          this.m_pGame.CreateObstacle(0x22, 2, 6, 8, 2);
          this.m_pGame.CreateObstacle(0x24, 0, 6, 5, 2);
          this.m_pGame.CreateStar(0x27);
          this.m_pGame.CreateObstacle(0x2a, 0, 20, 3, 0);
          this.m_pGame.CreateObstacle(0x2b, 2, 5, 9, 3);
          this.m_pGame.CreateObstacle(0x2d, 0, 6, 8, 3);
          this.m_pGame.CreateStar(0x30);
          this.m_pGame.CreateStar(50);
          this.m_pGame.CreateStar(0x34, 3);
          this.m_pGame.CreateObstacle(0x34, 3, 5, 9, 0);
          this.m_pGame.CreateObstacle(0x36, 0, 5, 4, 0);
          this.m_pGame.CreateStar(0x38);
          this.m_pGame.CreateStar(0x3a);
      }

      private void CreateLevel02W1()
      {
          this.m_pGround.SetSize(0x23);
          this.m_pGame.CreateStar(8);
          this.m_pGame.CreateObstacle(12, 0, 5, 9, 0);
          this.m_pGame.CreateStar(15);
          this.m_pGame.CreateStar(0x11);
          this.m_pGame.CreateStar(0x13);
          this.m_pGame.CreateStar(0x15);
          this.m_pGame.CreateObstacle(0x19, 0, 5, 9, 0);
          this.m_pGame.CreateStar(0x1b);
          this.m_pGame.CreateStar(0x1d);
      }

      private void CreateLevel02W2()
      {
          this.m_pGround.SetSize(0x37);
          this.m_pGame.CreateStar(9);
          this.m_pGame.CreateObstacle(10, 3, 5, 4, 0);
          this.m_pGround.AddHoleAt(12);
          this.m_pGround.AddHoleAt(13);
          this.m_pGround.AddHoleAt(14);
          this.m_pGame.CreateStar(15);
          this.m_pGame.CreateStar(0x11);
          this.m_pGround.AddHoleAt(0x12);
          this.m_pGround.AddHoleAt(20);
          this.m_pGround.AddHoleAt(0x15);
          this.m_pGame.CreateStar(0x16);
          this.m_pGame.CreateObstacle(0x18, 1, 5, 4, 0);
          this.m_pGame.CreateStar(0x1c);
          this.m_pGame.CreateObstacle(0x1f, 2, 5, 12, 0);
          this.m_pGame.CreateStar(0x25);
          this.m_pGame.CreateObstacle(40, 2, 5, 12, 0);
          this.m_pGame.CreateStar(0x2d);
          this.m_pGame.CreateStar(0x2f);
      }

      private void CreateLevel02W3()
      {
          this.m_pGround.SetSize(0x4b);
          this.m_pGame.CreateStar(7);
          this.m_pGame.CreateStar(9);
          this.m_pGame.CreateObstacle(10, 1, 6, 4, 0);
          this.m_pGame.CreateObstacle(10, 2, 5, 4, 4);
          this.m_pGame.CreateStar(14);
          this.m_pGame.CreateObstacle(0x11, 3, 6, 13, 0);
          this.m_pGame.CreateExitableForceField(20, 0, 8, 12);
          this.m_pGame.CreateObstacle(0x1c, 0, 6, 0x20, 12);
          this.m_pGame.CreateStar(0x1a);
          this.m_pGame.CreateStar(0x1c);
          this.m_pGame.CreateStar(30);
          this.m_pGame.CreateObstacle(0x20, 1, 5, 6, 0);
          this.m_pGame.CreateStar(0x23);
          this.m_pGame.CreateObstacle(0x25, 1, 5, 6, 0);
          this.m_pGame.CreateStar(0x29);
          this.m_pGame.CreateObstacle(0x2b, 2, 5, 6, 0);
          this.m_pGame.CreateStar(0x2f);
          this.m_pGame.CreateObstacle(0x31, 1, 20, 3, 0);
          this.m_pGame.CreateObstacle(50, 3, 6, 8, 3);
          this.m_pGame.CreateObstacle(0x34, 1, 6, 5, 3);
          this.m_pGame.CreateStar(0x38);
          this.m_pGame.CreateObstacle(0x3b, 2, 6, 4, 0);
          this.m_pGame.CreateObstacle(0x3b, 3, 5, 4, 4);
          this.m_pGame.CreateStar(0x3e);
          this.m_pGame.CreateStar(0x40);
          this.m_pGround.AddHoleAt(0x42);
          this.m_pGround.AddHoleAt(0x43);
          this.m_pGame.CreateStar(70);
      }

      private void CreateLevel03W1()
      {
          this.m_pGround.SetSize(40);
          this.m_pGame.CreateStar(7);
          this.m_pGame.CreateObstacle(10, 0, 5, 6, 0);
          this.m_pGame.CreateStar(13);
          this.m_pGame.CreateStar(15);
          this.m_pGame.CreateObstacle(0x12, 0, 6, 8, 0);
          this.m_pGame.CreateStar(0x15);
          this.m_pGame.CreateStar(0x17);
          this.m_pGame.CreateObstacle(0x1a, 0, 9, 9, 0);
          this.m_pGame.CreateStar(0x1f);
          this.m_pGame.CreateStar(0x21);
      }

      private void CreateLevel03W2()
      {
          this.m_pGround.SetSize(70);
          this.m_pGame.CreateStar(7);
          this.m_pGame.CreateStar(9);
          this.m_pGame.CreateObstacle(10, 2, 6, 5, 0);
          this.m_pGame.CreateObstacle(13, 3, 6, 0x20, 10);
          this.m_pGame.CreateStar(13);
          this.m_pGame.CreateStar(15);
          this.m_pGame.CreateStar(0x11);
          this.m_pGame.CreateObstacle(20, 0, 6, 13, 0);
          this.m_pGame.CreateStar(0x19);
          this.m_pGame.CreateObstacle(0x1c, 1, 6, 13, 0);
          this.m_pGame.CreateSpeedChangeUpStart(0x21);
          this.m_pGame.CreateObstacle(0x24, 3, 6, 5, 0);
          this.m_pGame.CreateStar(40);
          this.m_pGame.CreateObstacle(0x2a, 2, 6, 5, 0);
          this.m_pGame.CreateStar(0x2e);
          this.m_pGame.CreateObstacle(0x30, 1, 6, 5, 0);
          this.m_pGame.CreateStar(0x33);
          this.m_pGame.CreateStar(0x35);
          this.m_pGame.CreateObstacle(0x37, 1, 6, 13, 0);
          this.m_pGame.CreateStar(0x3d);
          this.m_pGame.CreateStar(0x3f);
      }

      private void CreateLevel03W3()
      {
          this.m_pGround.SetSize(80);
          this.m_pGame.CreateStar(7);
          this.m_pGame.CreateStar(9);
          this.m_pGame.CreateObstacle(10, 1, 5, 4, 0);
          this.m_pGame.CreateObstacle(10, 2, 5, 4, 4);
          this.m_pGame.CreateStar(13);
          this.m_pGame.CreateObstacle(0x10, 2, 6, 5, 0);
          this.m_pGame.CreateObstacle(0x10, 3, 5, 4, 5);
          this.m_pGround.AddHoleAt(0x12);
          this.m_pGround.AddHoleAt(0x13);
          this.m_pGround.AddHoleAt(20);
          this.m_pGround.AddHoleAt(0x15);
          this.m_pGround.AddHoleAt(0x16);
          this.m_pGround.AddHoleAt(0x17);
          this.m_pGround.AddHoleAt(0x18);
          this.m_pGame.CreateStar(0x1a);
          this.m_pGame.CreateStar(0x1c);
          this.m_pGame.CreateExitableForceField(20, 0, 12, 12);
          this.m_pGround.AddHoleAt(30);
          this.m_pGround.AddHoleAt(0x1f);
          this.m_pGround.AddHoleAt(0x20);
          this.m_pGame.CreateStar(0x21);
          this.m_pGame.CreateStar(0x23);
          this.m_pGame.CreateObstacle(0x27, 1, 5, 4, 0);
          this.m_pGame.CreateObstacle(0x27, 2, 5, 4, 4);
          this.m_pGame.CreateObstacle(0x27, 0, 5, 4, 8);
          this.m_pGame.CreateObstacle(40, 1, 5, 4, 8);
          this.m_pGame.CreateStar(0x2b);
          this.m_pGame.CreateObstacle(0x30, 1, 5, 4, 0);
          this.m_pGame.CreateObstacle(0x30, 0, 5, 4, 4);
          this.m_pGame.CreateObstacle(0x2f, 0, 5, 4, 8);
          this.m_pGame.CreateObstacle(0x30, 1, 5, 4, 8);
          this.m_pGame.CreateStar(0x34);
          this.m_pGame.CreateStar(0x36);
          this.m_pGame.CreateStar(0x38);
          this.m_pGame.CreateObstacle(0x3a, 0, 20, 3, 0);
          this.m_pGame.CreateObstacle(0x3b, 2, 6, 8, 3);
          this.m_pGame.CreateObstacle(0x3d, 0, 6, 8, 3);
          this.m_pGame.CreateStar(0x41);
          this.m_pGame.CreateObstacle(0x43, 0, 20, 3, 0);
          this.m_pGame.CreateObstacle(0x44, 2, 5, 9, 3);
          this.m_pGame.CreateObstacle(70, 0, 5, 9, 3);
          this.m_pGame.CreateStar(0x49);
          this.m_pGame.CreateStar(0x4b);
      }

      private void CreateLevel04W1()
      {
          this.m_pGround.SetSize(40);
          this.m_pGame.CreateStar(11, 4);
          this.m_pGame.CreateObstacle(11, 0, 5, 4, 0);
          this.m_pGame.CreateObstacle(12, 1, 5, 4, 0);
          this.m_pGame.CreateObstacle(11, 2, 6, 5, 4);
          this.m_pGame.CreateStar(15);
          this.m_pGame.CreateStar(0x11);
          this.m_pGame.CreateObstacle(0x15, 1, 6, 13, 0);
          this.m_pGame.CreateObstacle(0x16, 3, 5, 6, 0);
          this.m_pGame.CreateStar(0x1a);
          this.m_pGame.CreateStar(0x1c);
          this.m_pGame.CreateObstacle(30, 1, 6, 4, 0);
          this.m_pGame.CreateStar(0x21);
          this.m_pGame.CreateStar(0x23);
      }

      private void CreateLevel04W2()
      {
          this.m_pGround.SetSize(70);
          this.m_pGame.CreateStar(7);
          this.m_pGame.CreateStar(9);
          this.m_pGround.AddHoleAt(10);
          this.m_pGround.AddHoleAt(11);
          this.m_pGround.AddHoleAt(13);
          this.m_pGame.CreateObstacle(14, 3, 6, 0x20, 10);
          this.m_pGame.CreateStar(14);
          this.m_pGame.CreateStar(0x10);
          this.m_pGame.CreateObstacle(0x13, 0, 6, 13, 0);
          this.m_pGame.CreateStar(0x17);
          this.m_pGame.CreateStar(0x19);
          this.m_pGame.CreateStar(0x1a, 2);
          this.m_pGame.CreateStar(0x1c, 2);
          this.m_pGame.CreateObstacle(0x1a, 3, 6, 5, 0);
          this.m_pGame.CreateStar(0x1d);
          this.m_pGround.AddHoleAt(0x1f);
          this.m_pGround.AddHoleAt(0x22);
          this.m_pGame.CreateStar(0x1f, 2);
          this.m_pGame.CreateStar(0x21, 2);
          this.m_pGame.CreateStar(0x23);
          this.m_pGround.AddHoleAt(0x25);
          this.m_pGame.CreateObstacle(40, 0, 5, 12, 0);
          this.m_pGame.CreateStar(40, 4);
          this.m_pGame.CreateStar(0x2b);
          this.m_pGround.AddHoleAt(0x2d);
          this.m_pGround.AddHoleAt(0x2e);
          this.m_pGame.CreateStar(0x2e, 2);
          this.m_pGame.CreateStar(0x31);
          this.m_pGame.CreateStar(0x33);
          this.m_pGame.CreateObstacle(0x34, 3, 14, 8, 0);
          this.m_pGame.CreateStar(0x3a);
          this.m_pGame.CreateStar(60);
      }

      private void CreateLevel04W3()
      {
          this.m_pGround.SetSize(80);
          this.m_pGame.CreateStar(7);
          this.m_pGame.CreateStar(9);
          this.m_pGame.CreateObstacle(10, 1, 20, 3, 0);
          this.m_pGame.CreateObstacle(11, 0, 9, 9, 3);
          this.m_pGame.CreateStar(0x10);
          this.m_pGame.CreateObstacle(0x13, 1, 5, 6, 0);
          this.m_pGame.CreateObstacle(0x13, 2, 5, 4, 6);
          this.m_pGame.CreateStar(0x17);
          this.m_pGame.CreateSpeedChangeUpStart(0x19);
          this.m_pGame.CreateStar(0x1b);
          this.m_pGame.CreateObstacle(0x1d, 0, 5, 4, 0);
          this.m_pGame.CreateObstacle(0x1d, 0, 5, 4, 4);
          this.m_pGame.CreateStar(0x20);
          this.m_pGround.AddHoleAt(0x22);
          this.m_pGround.AddHoleAt(0x23);
          this.m_pGround.AddHoleAt(0x24);
          this.m_pGame.CreateStar(0x26);
          this.m_pGame.CreateObstacle(40, 0, 5, 4, 0);
          this.m_pGame.CreateObstacle(40, 0, 5, 4, 4);
          this.m_pGame.CreateStar(0x2b);
          this.m_pGame.CreateStar(0x2e, 2);
          this.m_pGame.CreateForceField(0x2f, 0, 4, 12);
          this.m_pGround.AddHoleAt(0x2d);
          this.m_pGround.AddHoleAt(0x2e);
          this.m_pGround.AddHoleAt(0x2f);
          this.m_pGame.CreateObstacle(0x30, 0, 5, 12, 0);
          this.m_pGame.CreateSpeedChangeUpEnd(0x34);
          this.m_pGame.CreateStar(0x36);
          this.m_pGame.CreateStar(0x38);
          this.m_pGame.CreateObstacle(0x3b, 0, 5, 12, 0);
          this.m_pGame.CreateStar(0x3f);
          this.m_pGame.CreateStar(0x41);
          this.m_pGame.CreateObstacle(0x43, 2, 5, 4, 0);
          this.m_pGame.CreateObstacle(0x44, 3, 5, 12, 0);
          this.m_pGame.CreateStar(0x49);
          this.m_pGame.CreateStar(0x4b);
      }

      private void CreateLevel05W1()
      {
          this.m_pGround.SetSize(40);
          this.m_pGame.CreateObstacle(10, 0, 9, 9, 0);
          this.m_pGame.CreateStar(14);
          this.m_pGame.CreateStar(0x10);
          this.m_pGame.CreateObstacle(0x12, 0, 5, 6, 0);
          this.m_pGame.CreateStar(0x15);
          this.m_pGame.CreateStar(0x17);
          this.m_pGame.CreateObstacle(0x1a, 0, 9, 9, 0);
          this.m_pGame.CreateStar(0x1f);
          this.m_pGame.CreateStar(0x21);
      }

      private void CreateLevel05W2()
      {
          this.m_pGround.SetSize(70);
          this.m_pGame.CreateStar(7);
          this.m_pGame.CreateStar(11, 4);
          this.m_pGame.CreateObstacle(10, 0, 11, 13, 0);
          this.m_pGame.CreateStar(15);
          this.m_pGround.AddHoleAt(0x11);
          this.m_pGround.AddHoleAt(0x12);
          this.m_pGame.CreateStar(20);
          this.m_pGame.CreateStar(0x16);
          this.m_pGame.CreateStar(0x19, 4);
          this.m_pGame.CreateObstacle(0x18, 1, 11, 13, 0);
          this.m_pGame.CreateStar(0x1d);
          this.m_pGround.AddHoleAt(0x1f);
          this.m_pGround.AddHoleAt(0x20);
          this.m_pGame.CreateStar(0x21, 3);
          this.m_pGame.CreateStar(0x23);
          this.m_pGame.CreateStar(0x25);
          this.m_pGame.CreateStar(0x27, 4);
          this.m_pGame.CreateStar(0x29, 4);
          this.m_pGame.CreateObstacle(0x27, 1, 11, 13, 0);
          this.m_pGame.CreateStar(0x2c);
          this.m_pGame.CreateStar(0x30, 3);
          this.m_pGame.CreateStar(50);
          this.m_pGame.CreateObstacle(0x2f, 0, 6, 8, 0);
          this.m_pGround.AddHoleAt(0x34);
          this.m_pGround.AddHoleAt(0x35);
          this.m_pGround.AddHoleAt(0x36);
          this.m_pGame.CreateStar(0x35, 3);
          this.m_pGame.CreateObstacle(0x39, 0, 6, 5, 0);
          this.m_pGame.CreateStar(60);
          this.m_pGame.CreateStar(0x3e);
      }

      private void CreateLevel05W3()
      {
          this.m_pGround.SetSize(80);
          this.m_pGame.CreateStar(7);
          this.m_pGame.CreateStar(9);
          this.m_pGround.AddHoleAt(10);
          this.m_pGround.AddHoleAt(11);
          this.m_pGame.CreateStar(14);
          this.m_pGround.AddHoleAt(15);
          this.m_pGame.CreateStar(0x13);
          this.m_pGame.CreateObstacle(0x16, 2, 5, 12, 0);
          this.m_pGame.CreateObstacle(0x18, 1, 5, 9, 0);
          this.m_pGame.CreateStar(0x1c);
          this.m_pGame.CreateStar(30);
          this.m_pGround.AddHoleAt(0x1f);
          this.m_pGround.AddHoleAt(0x20);
          this.m_pGround.AddHoleAt(0x21);
          this.m_pGame.CreateExitableForceField(0x20, 0, 6, 12);
          this.m_pGame.CreateObstacle(0x22, 0, 5, 4, 0);
          this.m_pGame.CreateObstacle(0x26, 0, 4, 0x20, 8);
          this.m_pGame.CreateStar(0x24);
          this.m_pGame.CreateStar(0x26);
          this.m_pGame.CreateExitableForceField(0x27, 0, 6, 12);
          this.m_pGround.AddHoleAt(40);
          this.m_pGame.CreateObstacle(40, 3, 5, 4, 0);
          this.m_pGround.AddHoleAt(0x2a);
          this.m_pGround.AddHoleAt(0x2b);
          this.m_pGame.CreateStar(0x2d);
          this.m_pGame.CreateStar(0x2f);
          this.m_pGround.AddHoleAt(0x30);
          this.m_pGame.CreateObstacle(50, 1, 6, 13, 0);
          this.m_pGame.CreateStar(0x36);
          this.m_pGame.CreateStar(0x38);
          this.m_pGround.AddHoleAt(0x3a);
          this.m_pGround.AddHoleAt(60);
          this.m_pGame.CreateStar(0x3e);
          this.m_pGame.CreateObstacle(0x41, 0, 6, 13, 0);
          this.m_pGame.CreateObstacle(0x42, 2, 6, 13, 0);
          this.m_pGame.CreateStar(70);
          this.m_pGame.CreateStar(0x48);
          this.m_pGround.AddHoleAt(0x4b);
      }

      private void CreateLevel06W1()
      {
          this.m_pGround.SetSize(40);
          this.m_pGame.CreateStar(6);
          this.m_pGame.CreateStar(8);
          this.m_pGame.CreateObstacle(10, 0, 20, 3, 0);
          this.m_pGame.CreateStar(0x10);
          this.m_pGame.CreateStar(0x12);
          this.m_pGame.CreateObstacle(20, 1, 5, 9, 0);
          this.m_pGame.CreateStar(0x16);
          this.m_pGame.CreateStar(0x18);
          this.m_pGame.CreateObstacle(0x1a, 3, 9, 9, 0);
          this.m_pGame.CreateStar(0x20);
          this.m_pGame.CreateStar(0x22);
      }

      private void CreateLevel06W2()
      {
          this.m_pGround.SetSize(0x4b);
          this.m_pGame.CreateStar(7);
          this.m_pGame.CreateStar(9);
          this.m_pGame.CreateStar(11, 4);
          this.m_pGame.CreateObstacle(10, 0, 20, 3, 0);
          this.m_pGame.CreateObstacle(11, 0, 9, 9, 3);
          this.m_pGame.CreateStar(0x11);
          this.m_pGame.CreateStar(0x13);
          this.m_pGround.AddHoleAt(20);
          this.m_pGround.AddHoleAt(0x15);
          this.m_pGround.AddHoleAt(0x16);
          this.m_pGround.AddHoleAt(0x17);
          this.m_pGame.CreateStar(20, 2);
          this.m_pGame.CreateStar(0x16, 4);
          this.m_pGame.CreateStar(0x18, 4);
          this.m_pGame.CreateStar(0x1a, 2);
          this.m_pGame.CreateObstacle(0x18, 0, 9, 9, 0);
          this.m_pGame.CreateStar(0x1c);
          this.m_pGame.CreateSpeedChangeUpStart(30);
          this.m_pGame.CreateStar(0x20);
          this.m_pGame.CreateStar(0x22);
          this.m_pGame.CreateStar(0x24, 4);
          this.m_pGame.CreateObstacle(0x23, 0, 20, 3, 0);
          this.m_pGame.CreateObstacle(0x24, 0, 9, 9, 3);
          this.m_pGame.CreateStar(40);
          this.m_pGame.CreateStar(0x2a);
          this.m_pGame.CreateObstacle(0x2d, 0, 5, 6, 0);
          this.m_pGame.CreateObstacle(0x2e, 1, 5, 6, 0);
          this.m_pGame.CreateStar(0x30);
          this.m_pGame.CreateStar(50);
          this.m_pGame.CreateStar(0x34);
          this.m_pGame.CreateObstacle(0x36, 0, 20, 3, 0);
          this.m_pGame.CreateObstacle(0x36, 3, 5, 6, 3);
          this.m_pGame.CreateObstacle(0x38, 0, 6, 8, 3);
          this.m_pGame.CreateStar(60);
          this.m_pGame.CreateStar(0x3e);
          this.m_pGame.CreateStar(0x40);
          this.m_pGround.AddHoleAt(0x40);
      }

      private void CreateLevel06W3()
      {
          this.m_pGround.SetSize(0x55);
          this.m_pGame.CreateStar(7);
          this.m_pGame.CreateStar(9);
          this.m_pGame.CreateObstacle(10, 3, 5, 12, 0);
          this.m_pGame.CreateStar(15);
          this.m_pGame.CreateObstacle(0x13, 0, 5, 12, 0);
          this.m_pGame.CreateObstacle(20, 1, 5, 12, 0);
          this.m_pGame.CreateStar(0x18);
          this.m_pGame.CreateStar(0x1a);
          this.m_pGame.CreateStar(0x1c);
          this.m_pGame.CreateBouncePadUp(0x20, 0, 7);
          this.m_pGame.CreateObstacle(0x23, 1, 5, 12, 0);
          this.m_pGame.CreateObstacle(0x23, 1, 5, 6, 12);
          this.m_pGame.CreateStar(0x27);
          this.m_pGame.CreateStar(0x29);
          this.m_pGame.CreateObstacle(0x2d, 2, 5, 12, 0);
          this.m_pGame.CreateObstacle(0x2d, 2, 5, 4, 12);
          this.m_pGame.CreateStar(0x31);
          this.m_pGame.CreateStar(0x33);
          this.m_pGame.CreateObstacle(0x36, 0, 6, 4, 0);
          this.m_pGame.CreateObstacle(0x36, 1, 5, 4, 4);
          this.m_pGame.CreateStar(0x39);
          this.m_pGame.CreateObstacle(60, 0, 5, 4, 0);
          this.m_pGame.CreateObstacle(60, 1, 6, 4, 4);
          this.m_pGame.CreateStar(0x3f);
          this.m_pGame.CreateStar(0x41);
          this.m_pGame.CreateStar(0x43);
          this.m_pGame.CreateBouncePadUp(0x47, 0, 7);
          this.m_pGame.CreateObstacle(0x4a, 1, 5, 12, 0);
          this.m_pGame.CreateObstacle(0x4a, 1, 5, 6, 12);
          this.m_pGame.CreateStar(0x4e);
          this.m_pGame.CreateStar(80);
      }

      private void CreateLevel07W1()
      {
          this.m_pGround.SetSize(40);
          this.m_pGame.CreateStar(6);
          this.m_pGame.CreateStar(8);
          this.m_pGame.CreateObstacle(10, 0, 20, 3, 0);
          this.m_pGame.CreateObstacle(11, 3, 6, 4, 3);
          this.m_pGame.CreateStar(0x10);
          this.m_pGame.CreateStar(0x12);
          this.m_pGame.CreateObstacle(20, 1, 6, 5, 0);
          this.m_pGame.CreateObstacle(0x16, 0, 5, 6, 0);
          this.m_pGame.CreateStar(0x18);
          this.m_pGame.CreateStar(0x1a);
          this.m_pGame.CreateObstacle(0x1d, 3, 5, 9, 0);
          this.m_pGame.CreateStar(0x20);
          this.m_pGame.CreateStar(0x22);
      }

      private void CreateLevel07W2()
      {
          this.m_pGround.SetSize(0x4b);
          this.m_pGame.CreateStar(7);
          this.m_pGame.CreateStar(9);
          this.m_pGame.CreateStar(11);
          this.m_pGame.CreateObstacle(9, 0, 8, 0x20, 12);
          this.m_pGame.CreateObstacle(13, 1, 9, 9, 0);
          this.m_pGame.CreateStar(0x12);
          this.m_pGround.AddHoleAt(20);
          this.m_pGround.AddHoleAt(0x15);
          this.m_pGame.CreateStar(0x18);
          this.m_pGame.CreateStar(0x1a);
          this.m_pGame.CreateStar(0x1c);
          this.m_pGame.CreateObstacle(0x19, 0, 8, 0x20, 9);
          this.m_pGame.CreateObstacle(30, 0, 9, 9, 0);
          this.m_pGame.CreateStar(0x23);
          this.m_pGame.CreateObstacle(0x27, 1, 5, 12, 0);
          this.m_pGame.CreateObstacle(40, 2, 6, 8, 0);
          this.m_pGame.CreateStar(0x2c);
          this.m_pGame.CreateObstacle(0x2e, 3, 5, 4, 0);
          this.m_pGame.CreateObstacle(0x30, 0, 5, 4, 0);
          this.m_pGame.CreateStar(0x2f, 2);
          this.m_pGame.CreateStar(50);
          this.m_pGame.CreateStar(0x34);
          this.m_pGame.CreateObstacle(0x35, 3, 5, 6, 0);
          this.m_pGame.CreateStar(0x36, 2);
          this.m_pGame.CreateStar(0x38);
          this.m_pGame.CreateSpeedChangeUpStart(0x3a);
          this.m_pGame.CreateStar(60);
          this.m_pGame.CreateObstacle(0x3e, 2, 5, 6, 0);
          this.m_pGame.CreateObstacle(0x3e, 2, 5, 6, 6);
          this.m_pGame.CreateStar(0x44);
          this.m_pGame.CreateObstacle(0x48, 0, 5, 6, 0);
          this.m_pGame.CreateObstacle(0x48, 0, 5, 9, 6);
      }

      private void CreateLevel07W3()
      {
          this.m_pGround.SetSize(0x55);
          this.m_pGame.CreateStar(7);
          this.m_pGame.CreateStar(9);
          this.m_pGame.CreateObstacle(10, 2, 5, 4, 0);
          this.m_pGame.CreateObstacle(10, 2, 5, 4, 4);
          this.m_pGame.CreateStar(13);
          this.m_pGame.CreateStar(15);
          this.m_pGame.CreateObstacle(0x11, 2, 5, 4, 0);
          this.m_pGame.CreateObstacle(0x11, 2, 5, 6, 4);
          this.m_pGame.CreateStar(20);
          this.m_pGame.CreateStar(0x16);
          this.m_pGame.CreateObstacle(0x18, 2, 5, 4, 0);
          this.m_pGame.CreateObstacle(0x18, 2, 5, 6, 4);
          this.m_pGame.CreateStar(0x1b);
          this.m_pGame.CreateStar(0x1d);
          this.m_pGame.CreateStar(0x1f);
          this.m_pGame.CreateBouncePadUp(0x23, 0, 7);
          this.m_pGame.CreateObstacle(0x26, 1, 5, 12, 0);
          this.m_pGame.CreateObstacle(0x26, 1, 5, 6, 12);
          this.m_pGame.CreateStar(0x2b);
          this.m_pGame.CreateStar(0x2d);
          this.m_pGame.CreateBouncePadUp(0x2f, 3, 7);
          this.m_pGame.CreateObstacle(0x33, 0, 5, 12, 0);
          this.m_pGame.CreateObstacle(0x33, 0, 5, 6, 12);
          this.m_pGame.CreateStar(0x39);
          this.m_pGame.CreateStar(0x3b);
          this.m_pGame.CreateObstacle(0x3e, 1, 14, 8, 0);
          this.m_pGame.CreateObstacle(0x42, 0, 14, 8, 0);
          this.m_pGame.CreateBouncePadUp(0x41, 2, 8);
          this.m_pGame.CreateStar(0x4a);
          this.m_pGame.CreateStar(0x4c);
      }

      private void CreateLevel08W1()
      {
          this.m_pGround.SetSize(0x2d);
          this.m_pGame.CreateStar(7);
          this.m_pGame.CreateStar(9);
          this.m_pGame.CreateStar(11);
          this.m_pGame.CreateObstacle(10, 0, 6, 0x20, 7);
          this.m_pGame.CreateObstacle(13, 1, 0x15, 2, 0);
          this.m_pGame.CreateObstacle(0x10, 0, 6, 4, 2);
          this.m_pGame.CreateStar(20);
          this.m_pGame.CreateObstacle(0x16, 2, 6, 4, 0);
          this.m_pGame.CreateObstacle(0x19, 2, 6, 4, 0);
          this.m_pGame.CreateStar(0x1d);
          this.m_pGame.CreateObstacle(0x1f, 2, 6, 4, 0);
          this.m_pGame.CreateStar(0x22);
          this.m_pGame.CreateStar(0x24);
      }

      private void CreateLevel08W2()
      {
          this.m_pGround.SetSize(80);
          this.m_pGame.CreateStar(6);
          this.m_pGame.CreateStar(8);
          this.m_pGame.CreateObstacle(10, 1, 14, 8, 0);
          this.m_pGame.CreateStar(0x10);
          this.m_pGround.AddHoleAt(0x11);
          this.m_pGround.AddHoleAt(0x12);
          this.m_pGame.CreateStar(0x15);
          this.m_pGame.CreateStar(0x17);
          this.m_pGame.CreateStar(0x19);
          this.m_pGame.CreateObstacle(0x17, 0, 8, 0x20, 8);
          this.m_pGame.CreateObstacle(0x1b, 2, 14, 8, 0);
          this.m_pGame.CreateStar(0x21);
          this.m_pGround.AddHoleAt(0x24);
          this.m_pGround.AddHoleAt(0x25);
          this.m_pGround.AddHoleAt(0x26);
          this.m_pGround.AddHoleAt(0x27);
          this.m_pGame.CreateObstacle(40, 1, 6, 8, 0);
          this.m_pGround.AddHoleAt(0x2a);
          this.m_pGround.AddHoleAt(0x2b);
          this.m_pGround.AddHoleAt(0x2c);
          this.m_pGround.AddHoleAt(0x2d);
          this.m_pGame.CreateStar(0x25, 4);
          this.m_pGame.CreateForceField(0x25, 0, 8, 9);
          this.m_pGame.CreateStar(0x30);
          this.m_pGame.CreateObstacle(0x31, 3, 14, 8, 0);
          this.m_pGame.CreateObstacle(0x33, 2, 5, 6, 8);
          this.m_pGame.CreateStar(0x34, 4);
          this.m_pGame.CreateStar(0x37);
          this.m_pGame.CreateObstacle(0x39, 3, 6, 5, 0);
          this.m_pGame.CreateObstacle(0x3b, 1, 5, 6, 0);
          this.m_pGame.CreateStar(0x3d);
          this.m_pGame.CreateStar(0x3f);
          this.m_pGame.CreateObstacle(0x40, 3, 6, 5, 0);
          this.m_pGame.CreateObstacle(0x41, 0, 5, 6, 5);
          this.m_pGame.CreateObstacle(0x42, 1, 5, 6, 0);
          this.m_pGame.CreateStar(70);
          this.m_pGround.AddHoleAt(0x48);
      }

      private void CreateLevel08W3()
      {
          this.m_pGround.SetSize(0x55);
          this.m_pGame.CreateStar(7);
          this.m_pGame.CreateStar(9);
          this.m_pGround.AddHoleAt(10);
          this.m_pGround.AddHoleAt(13);
          this.m_pGame.CreateStar(14);
          this.m_pGame.CreateStar(0x10);
          this.m_pGame.CreateObstacle(20, 0, 14, 8, 0);
          this.m_pGame.CreateObstacle(0x18, 0, 14, 8, 0);
          this.m_pGame.CreateBouncePadUp(20, 0, 8);
          this.m_pGame.CreateStar(0x17, 5);
          this.m_pGame.CreateBouncePadUp(0x19, 2, 8);
          this.m_pGame.CreateObstacle(30, 2, 5, 4, 0);
          this.m_pGame.CreateStar(0x1f, 4);
          this.m_pGame.CreateStar(0x20, 2);
          this.m_pGame.CreateStar(0x21);
          this.m_pGround.AddHoleAt(0x23);
          this.m_pGround.AddHoleAt(0x26);
          this.m_pGame.CreateStar(40);
          this.m_pGame.CreateObstacle(0x2b, 0, 14, 8, 0);
          this.m_pGame.CreateObstacle(0x2c, 2, 5, 4, 8);
          this.m_pGame.CreateStar(0x31);
          this.m_pGame.CreateObstacle(0x33, 3, 14, 8, 0);
          this.m_pGame.CreateObstacle(0x35, 1, 5, 6, 8);
          this.m_pGame.CreateStar(0x39);
          this.m_pGame.CreateStar(0x3b);
          this.m_pGame.CreateBouncePadUp(0x41, 1, 8);
          this.m_pGame.CreateStar(70, 6);
          this.m_pGame.CreateObstacle(0x44, 0, 14, 8, 0);
          this.m_pGame.CreateObstacle(0x44, 1, 12, 6, 8);
          this.m_pGame.CreateObstacle(0x44, 2, 5, 4, 14);
          this.m_pGame.CreateObstacle(0x45, 3, 5, 4, 14);
          this.m_pGame.CreateStar(0x4b);
          this.m_pGame.CreateStar(0x4d);
      }

      private void CreateLevel09W1()
      {
          this.m_pGround.SetSize(0x2d);
          this.m_pGame.CreateStar(6);
          this.m_pGame.CreateStar(8);
          this.m_pGame.CreateObstacle(10, 0, 6, 4, 0);
          this.m_pGame.CreateStar(14);
          this.m_pGame.CreateObstacle(0x10, 2, 20, 3, 0);
          this.m_pGame.CreateStar(0x16);
          this.m_pGame.CreateObstacle(0x19, 1, 6, 4, 0);
          this.m_pGame.CreateStar(0x1c);
          this.m_pGame.CreateObstacle(0x1f, 3, 5, 6, 0);
          this.m_pGame.CreateStar(0x22);
          this.m_pGame.CreateStar(0x24);
          this.m_pGame.CreateStar(0x26);
      }

      private void CreateLevel09W2()
      {
          this.m_pGround.SetSize(0x55);
          this.m_pGame.CreateStar(8);
          this.m_pGame.CreateObstacle(10, 0, 20, 3, 0);
          this.m_pGame.CreateObstacle(10, 3, 6, 4, 3);
          this.m_pGame.CreateObstacle(13, 2, 6, 8, 3);
          this.m_pGround.AddHoleAt(11);
          this.m_pGround.AddHoleAt(12);
          this.m_pGround.AddHoleAt(13);
          this.m_pGame.CreateStar(0x10);
          this.m_pGame.CreateStar(20, 3);
          this.m_pGame.CreateObstacle(0x16, 0, 8, 0x20, 12);
          this.m_pGame.CreateStar(0x16);
          this.m_pGround.AddHoleAt(0x18);
          this.m_pGround.AddHoleAt(0x19);
          this.m_pGround.AddHoleAt(0x1a);
          this.m_pGround.AddHoleAt(0x1b);
          this.m_pGame.CreateStar(0x1c);
          this.m_pGame.CreateStar(30);
          this.m_pGame.CreateObstacle(0x1f, 0, 14, 8, 0);
          this.m_pGame.CreateObstacle(0x20, 2, 5, 6, 8);
          this.m_pGame.CreateStar(0x26);
          this.m_pGame.CreateStar(40);
          this.m_pGame.CreateForceField(0x2b, 3, 4, 0x11);
          this.m_pGame.CreateObstacle(0x2a, 1, 11, 13, 0);
          this.m_pGame.CreateObstacle(0x2d, 0, 11, 13, 0);
          this.m_pGame.CreateStar(0x33);
          this.m_pGround.AddHoleAt(0x35);
          this.m_pGround.AddHoleAt(0x36);
          this.m_pGame.CreateObstacle(0x36, 3, 11, 13, 0);
          this.m_pGame.CreateStar(0x38, 4);
          this.m_pGame.CreateStar(60);
          this.m_pGame.CreateStar(0x3e);
          this.m_pGame.CreateObstacle(0x40, 2, 11, 13, 0);
          this.m_pGame.CreateStar(0x42, 4);
          this.m_pGame.CreateStar(70);
          this.m_pGame.CreateStar(0x48);
          this.m_pGame.CreateObstacle(0x4a, 2, 11, 13, 0);
          this.m_pGame.CreateStar(0x4c, 4);
          this.m_pGame.CreateStar(80);
          this.m_pGame.CreateStar(0x52);
      }

      private void CreateLevel09W3()
      {
          this.m_pGround.SetSize(0x55);
          this.m_pGame.CreateStar(7);
          this.m_pGame.CreateStar(9);
          this.m_pGame.CreateObstacle(11, 1, 5, 4, 0);
          this.m_pGame.CreateObstacle(10, 2, 20, 3, 4);
          this.m_pGame.CreateObstacle(13, 2, 5, 4, 0);
          this.m_pGame.CreateStar(0x10);
          this.m_pGame.CreateStar(0x12);
          this.m_pGame.CreateObstacle(20, 2, 5, 4, 0);
          this.m_pGame.CreateObstacle(0x15, 3, 5, 4, 0);
          this.m_pGame.CreateObstacle(0x15, 0, 5, 4, 4);
          this.m_pGame.CreateStar(0x18);
          this.m_pGame.CreateObstacle(0x1a, 2, 5, 4, 0);
          this.m_pGame.CreateObstacle(0x1b, 3, 5, 4, 0);
          this.m_pGame.CreateObstacle(0x1b, 0, 5, 6, 4);
          this.m_pGame.CreateStar(30);
          this.m_pGame.CreateObstacle(0x23, 0, 14, 8, 0);
          this.m_pGame.CreateObstacle(0x23, 0, 14, 8, 8);
          this.m_pGame.CreateForceField(0x24, 0, 3, 0x10);
          this.m_pGame.CreateStar(0x2a);
          this.m_pGame.CreateStar(0x2c);
          this.m_pGame.CreateExitableForceField(0x2f, 0, 8, 12);
          this.m_pGround.AddHoleAt(0x2d);
          this.m_pGround.AddHoleAt(0x2e);
          this.m_pGame.CreateObstacle(0x2f, 0, 6, 5, 0);
          this.m_pGame.CreateObstacle(0x2f, 0, 5, 6, 5);
          this.m_pGame.CreateStar(0x33);
          this.m_pGround.AddHoleAt(0x36);
          this.m_pGame.CreateStar(0x39);
          this.m_pGame.CreateExitableForceField(60, 0, 6, 12);
          this.m_pGame.CreateObstacle(0x3d, 0, 11, 11, 0);
          this.m_pGame.CreateStar(0x42);
          this.m_pGame.CreateStar(0x44);
          this.m_pGame.CreateObstacle(0x47, 3, 14, 8, 0);
          this.m_pGame.CreateStar(0x4e);
          this.m_pGame.CreateStar(80);
      }

      private void CreateLevel10W1()
      {
          this.m_pGround.SetSize(0x2d);
          this.m_pGame.CreateStar(6);
          this.m_pGame.CreateStar(8);
          this.m_pGame.CreateObstacle(10, 1, 0x15, 2, 0);
          this.m_pGame.CreateObstacle(11, 1, 5, 4, 2);
          this.m_pGame.CreateObstacle(12, 2, 5, 6, 2);
          this.m_pGame.CreateStar(0x10);
          this.m_pGame.CreateObstacle(0x12, 3, 6, 5, 0);
          this.m_pGame.CreateStar(0x15);
          this.m_pGame.CreateStar(0x17);
          this.m_pGame.CreateObstacle(0x19, 2, 5, 9, 0);
          this.m_pGame.CreateStar(0x1d);
          this.m_pGame.CreateObstacle(0x21, 0, 6, 13, 0);
          this.m_pGame.CreateStar(0x25);
          this.m_pGame.CreateStar(0x27);
      }

      private void CreateLevel10W2()
      {
          this.m_pGround.SetSize(80);
          this.m_pGame.CreateStar(8);
          this.m_pGame.CreateObstacle(10, 0, 5, 6, 0);
          this.m_pGame.CreateStar(13);
          this.m_pGame.CreateObstacle(15, 1, 5, 6, 0);
          this.m_pGame.CreateStar(0x12);
          this.m_pGame.CreateObstacle(20, 3, 5, 6, 0);
          this.m_pGame.CreateStar(0x17);
          this.m_pGame.CreateObstacle(0x1a, 1, 5, 6, 0);
          this.m_pGround.AddHoleAt(0x1c);
          this.m_pGame.CreateStar(0x1d);
          this.m_pGame.CreateSpeedChangeUpStart(0x20);
          this.m_pGame.CreateObstacle(0x24, 2, 5, 6, 0);
          this.m_pGame.CreateStar(0x27);
          this.m_pGame.CreateObstacle(0x2a, 0, 5, 6, 0);
          this.m_pGame.CreateStar(0x2d);
          this.m_pGame.CreateObstacle(0x2f, 3, 5, 6, 0);
          this.m_pGame.CreateStar(0x33);
          this.m_pGame.CreateObstacle(0x34, 2, 5, 6, 0);
          this.m_pGame.CreateStar(0x37);
          this.m_pGame.CreateObstacle(0x3a, 1, 5, 6, 0);
          this.m_pGame.CreateStar(0x3d);
          this.m_pGame.CreateObstacle(0x40, 3, 5, 6, 0);
          this.m_pGame.CreateStar(0x43);
          this.m_pGame.CreateStar(0x45);
      }

      private void CreateLevel10W3()
      {
          this.m_pGround.SetSize(90);
          this.m_pGame.CreateStar(7);
          this.m_pGame.CreateStar(9);
          this.m_pGame.CreateObstacle(10, 3, 6, 8, 0);
          this.m_pGame.CreateStar(12, 4);
          this.m_pGame.CreateObstacle(12, 2, 6, 8, 0);
          this.m_pGame.CreateStar(14, 4);
          this.m_pGame.CreateObstacle(14, 1, 6, 8, 0);
          this.m_pGame.CreateStar(0x11);
          this.m_pGame.CreateStar(0x12, 2);
          this.m_pGround.AddHoleAt(0x13);
          this.m_pGround.AddHoleAt(0x16);
          this.m_pGame.CreateStar(0x17, 2);
          this.m_pGame.CreateStar(0x18);
          this.m_pGame.CreateSpeedChangeUpStart(0x1a);
          this.m_pGame.CreateStar(0x1c);
          this.m_pGame.CreateObstacle(0x1d, 3, 6, 8, 0);
          this.m_pGame.CreateStar(0x1f, 4);
          this.m_pGame.CreateObstacle(0x1f, 2, 6, 8, 0);
          this.m_pGame.CreateStar(0x21, 4);
          this.m_pGame.CreateObstacle(0x21, 1, 6, 8, 0);
          this.m_pGame.CreateStar(0x24);
          this.m_pGame.CreateObstacle(0x26, 2, 5, 6, 0);
          this.m_pGame.CreateStar(0x29);
          this.m_pGame.CreateStar(0x2b);
          this.m_pGame.CreateObstacle(0x2c, 3, 5, 4, 0);
          this.m_pGame.CreateObstacle(0x2c, 3, 5, 4, 4);
          this.m_pGame.CreateStar(0x2f);
          this.m_pGame.CreateStar(0x31);
          this.m_pGame.CreateObstacle(0x34, 0, 6, 13, 0);
          this.m_pGame.CreateBouncePadUp(0x37, 0, 8);
          this.m_pGame.CreateObstacle(0x3a, 0, 6, 13, 0);
          this.m_pGame.CreateObstacle(0x3b, 2, 6, 13, 0);
          this.m_pGame.CreateStar(0x40);
          this.m_pGame.CreateSpeedChangeUpEnd(0x42);
          this.m_pGame.CreateStar(0x44);
          this.m_pGame.CreateExitableForceField(0x47, 0, 5, 12);
          this.m_pGround.AddHoleAt(70);
          this.m_pGround.AddHoleAt(0x48);
          this.m_pGame.CreateStar(0x4a);
          this.m_pGame.CreateStar(0x4c);
          this.m_pGame.CreateObstacle(0x4e, 0, 6, 4, 0);
          this.m_pGame.CreateStar(0x51);
          this.m_pGame.CreateStar(0x53);
      }

      private void CreateLevel11W1()
      {
          this.m_pGround.SetSize(0x2d);
          this.m_pGame.CreateStar(6);
          this.m_pGame.CreateStar(8);
          this.m_pGame.CreateObstacle(10, 0, 5, 4, 0);
          this.m_pGame.CreateStar(12);
          this.m_pGame.CreateStar(14);
          this.m_pGame.CreateObstacle(0x10, 1, 5, 9, 0);
          this.m_pGame.CreateStar(0x12);
          this.m_pGame.CreateStar(0x15);
          this.m_pGame.CreateObstacle(0x16, 3, 12, 6, 0);
          this.m_pGame.CreateStar(0x1b);
          this.m_pGame.CreateObstacle(0x1d, 0, 5, 4, 0);
          this.m_pGame.CreateStar(0x20);
          this.m_pGame.CreateStar(0x22);
          this.m_pGame.CreateObstacle(0x24, 1, 9, 9, 0);
          this.m_pGame.CreateStar(0x2a);
      }

      private void CreateLevel11W2()
      {
          this.m_pGround.SetSize(0x55);
          this.m_pGame.CreateStar(8);
          this.m_pGame.CreateObstacle(10, 0, 6, 8, 0);
          this.m_pGame.CreateObstacle(10, 0, 5, 4, 8);
          this.m_pGame.CreateStar(14);
          this.m_pGame.CreateStar(0x10);
          this.m_pGame.CreateObstacle(0x13, 0, 6, 8, 0);
          this.m_pGame.CreateObstacle(0x13, 0, 5, 6, 8);
          this.m_pGame.CreateStar(0x17);
          this.m_pGame.CreateObstacle(0x1a, 2, 6, 4, 0);
          this.m_pGame.CreateObstacle(0x1a, 3, 5, 6, 4);
          this.m_pGame.CreateObstacle(0x1b, 0, 5, 4, 10);
          this.m_pGame.CreateStar(0x1f);
          this.m_pGame.CreateObstacle(0x22, 1, 5, 6, 0);
          this.m_pGame.CreateStar(0x25);
          this.m_pGame.CreateObstacle(40, 1, 11, 11, 0);
          this.m_pGame.CreateStar(0x2d);
          this.m_pGame.CreateStar(0x2f);
          this.m_pGame.CreateObstacle(0x31, 3, 5, 4, 0);
          this.m_pGame.CreateObstacle(0x33, 0, 5, 4, 0);
          this.m_pGame.CreateObstacle(50, 2, 6, 4, 4);
          this.m_pGame.CreateObstacle(50, 2, 6, 4, 8);
          this.m_pGame.CreateStar(0x37);
          this.m_pGame.CreateObstacle(0x3a, 1, 14, 8, 0);
          this.m_pGame.CreateObstacle(0x3a, 3, 5, 6, 8);
          this.m_pGame.CreateStar(0x3f);
          this.m_pGame.CreateObstacle(0x42, 3, 5, 6, 0);
          this.m_pGame.CreateStar(70);
          this.m_pGame.CreateStar(0x48);
          this.m_pGame.CreateObstacle(0x49, 2, 6, 4, 0);
          this.m_pGame.CreateObstacle(0x49, 3, 5, 6, 4);
          this.m_pGame.CreateObstacle(0x4a, 1, 5, 4, 10);
          this.m_pGame.CreateStar(0x4f);
      }

      private void CreateLevel11W3()
      {
          this.m_pGround.SetSize(90);
          this.m_pGame.CreateStar(7);
          this.m_pGame.CreateStar(9);
          this.m_pGame.CreateSpeedChangeUpStart(11);
          this.m_pGame.CreateStar(13);
          this.m_pGame.CreateStar(0x10, 4);
          this.m_pGame.CreateObstacle(15, 2, 6, 8, 0);
          this.m_pGame.CreateObstacle(0x10, 0, 5, 6, 8);
          this.m_pGame.CreateStar(20);
          this.m_pGame.CreateStar(0x16);
          this.m_pGame.CreateStar(0x1a, 4);
          this.m_pGame.CreateObstacle(0x19, 3, 5, 6, 0);
          this.m_pGame.CreateObstacle(0x19, 2, 6, 8, 6);
          this.m_pGame.CreateStar(30);
          this.m_pGame.CreateSpeedChangeUpEnd(0x20);
          this.m_pGround.AddHoleAt(0x21);
          this.m_pGame.CreateObstacle(0x22, 0, 5, 6, 0);
          this.m_pGame.CreateObstacle(0x22, 0, 5, 4, 6);
          this.m_pGame.CreateStar(0x25);
          this.m_pGame.CreateStar(0x27);
          this.m_pGame.CreateStar(0x29);
          this.m_pGame.CreateObstacle(0x26, 3, 8, 0x20, 7);
          this.m_pGame.CreateObstacle(0x2b, 1, 5, 4, 0);
          this.m_pGame.CreateObstacle(0x2a, 2, 20, 3, 4);
          this.m_pGame.CreateObstacle(0x2d, 2, 5, 4, 0);
          this.m_pGame.CreateStar(0x30);
          this.m_pGame.CreateStar(50);
          this.m_pGame.CreateStar(0x34);
          this.m_pGame.CreateObstacle(0x31, 3, 8, 0x20, 6);
          this.m_pGame.CreateObstacle(0x36, 1, 6, 8, 0);
          this.m_pGame.CreateObstacle(0x38, 0, 6, 8, 0);
          this.m_pGame.CreateObstacle(0x39, 2, 6, 8, 0);
          this.m_pGame.CreateForceField(60, 0, 3, 0);
          this.m_pGround.AddHoleAt(60);
          this.m_pGround.AddHoleAt(0x3d);
          this.m_pGround.AddHoleAt(0x3e);
          this.m_pGame.CreateObstacle(0x3d, 0, 8, 0x20, 6);
          this.m_pGame.CreateObstacle(0x3f, 0, 4, 0x20, 6);
          this.m_pGame.CreateStar(0x40);
          this.m_pGame.CreateObstacle(0x42, 0, 6, 8, 0);
          this.m_pGame.CreateObstacle(0x43, 2, 5, 4, 0);
          this.m_pGame.CreateStar(70);
          this.m_pGame.CreateStar(0x48);
          this.m_pGame.CreateObstacle(0x49, 3, 5, 9, 0);
          this.m_pGame.CreateObstacle(0x4b, 0, 5, 4, 0);
          this.m_pGame.CreateStar(0x4e);
          this.m_pGame.CreateStar(80);
          this.m_pGame.CreateStar(0x52);
      }

      private void CreateLevel12W1()
      {
          this.m_pGround.SetSize(0x2d);
          this.m_pGame.CreateStar(6);
          this.m_pGame.CreateStar(8);
          this.m_pGame.CreateObstacle(10, 0, 5, 4, 0);
          this.m_pGame.CreateStar(12);
          this.m_pGame.CreateStar(14);
          this.m_pGame.CreateObstacle(15, 1, 5, 6, 0);
          this.m_pGame.CreateStar(0x12);
          this.m_pGame.CreateStar(20);
          this.m_pGame.CreateStar(0x16);
          this.m_pGame.CreateObstacle(0x13, 3, 4, 0x20, 5);
          this.m_pGame.CreateObstacle(0x18, 3, 12, 6, 0);
          this.m_pGame.CreateStar(30);
          this.m_pGame.CreateObstacle(0x20, 1, 9, 9, 0);
          this.m_pGame.CreateStar(0x26);
          this.m_pGame.CreateStar(40);
      }

      private void CreateLevel12W2()
      {
          this.m_pGround.SetSize(90);
          this.m_pGame.CreateStar(8);
          this.m_pGame.CreateObstacle(10, 0, 11, 11, 0);
          this.m_pGame.CreateObstacle(13, 0, 5, 9, 0);
          this.m_pGame.CreateStar(0x10);
          this.m_pGame.CreateObstacle(0x12, 1, 5, 6, 0);
          this.m_pGround.AddHoleAt(20);
          this.m_pGame.CreateStar(0x15, 4);
          this.m_pGround.AddHoleAt(0x15);
          this.m_pGame.CreateObstacle(0x16, 0, 5, 6, 0);
          this.m_pGame.CreateStar(0x18);
          this.m_pGame.CreateObstacle(0x1b, 1, 5, 6, 0);
          this.m_pGame.CreateObstacle(0x1c, 2, 14, 8, 0);
          this.m_pGame.CreateStar(0x22);
          this.m_pGround.AddHoleAt(0x23);
          this.m_pGround.AddHoleAt(0x24);
          this.m_pGround.AddHoleAt(0x25);
          this.m_pGround.AddHoleAt(0x26);
          this.m_pGame.CreateForceField(0x27, 1, 3, 0x11);
          this.m_pGame.CreateStar(40);
          this.m_pGame.CreateObstacle(0x2a, 2, 6, 8, 0);
          this.m_pGame.CreateStar(0x2c, 2);
          this.m_pGame.CreateStar(0x2d);
          this.m_pGame.CreateObstacle(0x31, 0, 5, 9, 0);
          this.m_pGame.CreateObstacle(50, 1, 11, 11, 0);
          this.m_pGame.CreateStar(0x37);
          this.m_pGame.CreateObstacle(0x3a, 0, 5, 9, 0);
          this.m_pGame.CreateObstacle(0x3b, 2, 11, 11, 0);
          this.m_pGame.CreateStar(0x40);
          this.m_pGame.CreateObstacle(0x43, 0, 6, 8, 0);
          this.m_pGame.CreateStar(70);
          this.m_pGame.CreateStar(0x48);
          this.m_pGame.CreateObstacle(0x4b, 0, 5, 6, 0);
          this.m_pGame.CreateStar(0x4e);
          this.m_pGame.CreateStar(80);
          this.m_pGame.CreateObstacle(0x52, 3, 5, 9, 0);
          this.m_pGame.CreateObstacle(0x54, 0, 5, 9, 0);
      }

      private void CreateLevel12W3()
      {
          this.m_pGround.SetSize(0x5f);
          this.m_pGame.CreateStar(7);
          this.m_pGame.CreateStar(9);
          this.m_pGround.AddHoleAt(10);
          this.m_pGame.CreateObstacle(11, 1, 5, 6, 0);
          this.m_pGame.CreateObstacle(11, 2, 5, 4, 6);
          this.m_pGround.AddHoleAt(13);
          this.m_pGame.CreateStar(14);
          this.m_pGame.CreateStar(0x10);
          this.m_pGame.CreateObstacle(0x12, 0, 5, 6, 0);
          this.m_pGame.CreateObstacle(0x12, 3, 5, 4, 6);
          this.m_pGame.CreateStar(0x15);
          this.m_pGame.CreateForceField(0x18, 0, 4, 12);
          this.m_pGame.CreateObstacle(0x19, 0, 8, 0x20, 0x11);
          this.m_pGame.CreateObstacle(0x18, 3, 5, 6, 0);
          this.m_pGame.CreateObstacle(0x19, 1, 6, 4, 6);
          this.m_pGame.CreateObstacle(0x1a, 0, 5, 4, 0);
          this.m_pGame.CreateStar(0x1d, 1);
          this.m_pGame.CreateStar(0x20, 1);
          this.m_pGame.CreateExitableForceField(0x21, 0, 7, 0x10);
          this.m_pGame.CreateObstacle(0x23, 0, 5, 6, 0);
          this.m_pGame.CreateObstacle(0x23, 0, 5, 9, 6);
          this.m_pGame.CreateObstacle(0x24, 1, 5, 6, 0);
          this.m_pGame.CreateObstacle(0x24, 1, 5, 9, 6);
          this.m_pGame.CreateObstacle(40, 0, 6, 0x20, 13);
          this.m_pGame.CreateStar(0x27, 1);
          this.m_pGame.CreateStar(0x29);
          this.m_pGame.CreateStar(0x2b);
          this.m_pGame.CreateStar(0x2d);
          this.m_pGame.CreateBouncePadUp(0x31, 0, 7);
          this.m_pGame.CreateObstacle(0x34, 1, 5, 12, 0);
          this.m_pGame.CreateObstacle(0x34, 0, 5, 6, 12);
          this.m_pGame.CreateStar(0x36, 4);
          this.m_pGame.CreateStar(0x37, 2);
          this.m_pGame.CreateStar(0x38);
          this.m_pGame.CreateStar(0x3a);
          this.m_pGround.AddHoleAt(60);
          this.m_pGame.CreateStar(0x3d, 2);
          this.m_pGround.AddHoleAt(0x3e);
          this.m_pGame.CreateStar(0x40);
          this.m_pGame.CreateObstacle(0x42, 2, 5, 6, 0);
          this.m_pGame.CreateStar(70);
          this.m_pGame.CreateStar(0x48, 3);
          this.m_pGame.CreateObstacle(0x4a, 1, 5, 12, 0);
          this.m_pGame.CreateObstacle(0x4a, 2, 6, 4, 12);
          this.m_pGame.CreateStar(0x4d, 3);
          this.m_pGame.CreateStar(0x4e);
          this.m_pGame.CreateStar(80);
          this.m_pGround.AddHoleAt(0x54);
      }

      private void CreateLevel13W1()
      {
          this.m_pGround.SetSize(0x37);
          this.m_pGame.CreateStar(7);
          this.m_pGame.CreateStar(11, 4);
          this.m_pGame.CreateObstacle(10, 0, 11, 13, 0);
          this.m_pGame.CreateStar(0x10);
          this.m_pGame.CreateStar(20, 4);
          this.m_pGame.CreateObstacle(0x13, 0, 11, 13, 0);
          this.m_pGame.CreateStar(0x19);
          this.m_pGame.CreateStar(0x1d, 4);
          this.m_pGame.CreateObstacle(0x1c, 0, 11, 13, 0);
          this.m_pGame.CreateStar(0x22);
          this.m_pGame.CreateStar(0x26, 4);
          this.m_pGame.CreateObstacle(0x25, 0, 11, 13, 0);
          this.m_pGame.CreateStar(0x2b);
          this.m_pGame.CreateStar(0x2f, 4);
          this.m_pGame.CreateObstacle(0x2e, 0, 11, 13, 0);
          this.m_pGame.CreateStar(0x34);
      }

      private void CreateLevel13W2()
      {
          this.m_pGround.SetSize(0x5f);
          this.m_pGame.CreateStar(8);
          this.m_pGame.CreateObstacle(10, 0, 5, 6, 0);
          this.m_pGame.CreateObstacle(10, 3, 5, 4, 6);
          this.m_pGame.CreateObstacle(12, 0, 5, 4, 6);
          this.m_pGame.CreateObstacle(13, 1, 5, 4, 6);
          this.m_pGame.CreateObstacle(14, 0, 5, 6, 0);
          this.m_pGame.CreateStar(12, 4);
          this.m_pGame.CreateStar(0x10);
          this.m_pGame.CreateObstacle(0x13, 0, 5, 6, 0);
          this.m_pGame.CreateStar(0x16);
          this.m_pGame.CreateStar(0x18);
          this.m_pGame.CreateObstacle(0x16, 1, 6, 0x20, 11);
          this.m_pGame.CreateObstacle(0x1a, 0, 5, 6, 0);
          this.m_pGame.CreateSpeedChangeUpStart(0x1d);
          this.m_pGame.CreateObstacle(0x1f, 3, 5, 6, 0);
          this.m_pGame.CreateStar(0x23);
          this.m_pGame.CreateStar(0x25);
          this.m_pGame.CreateObstacle(0x24, 1, 6, 0x20, 10);
          this.m_pGame.CreateObstacle(40, 0, 5, 6, 0);
          this.m_pGame.CreateStar(0x2b);
          this.m_pGame.CreateObstacle(0x2d, 2, 5, 6, 0);
          this.m_pGame.CreateSpeedChangeUpEnd(0x30);
          this.m_pGame.CreateStar(50);
          this.m_pGame.CreateObstacle(0x37, 0, 5, 6, 0);
          this.m_pGame.CreateObstacle(0x36, 0, 12, 6, 6);
          this.m_pGame.CreateStar(0x3b);
          this.m_pGame.CreateStar(0x3f, 4);
          this.m_pGame.CreateObstacle(0x3e, 1, 12, 6, 0);
          this.m_pGame.CreateObstacle(0x3e, 2, 5, 4, 6);
          this.m_pGame.CreateStar(0x44);
          this.m_pGame.CreateSpeedChangeUpStart(0x45);
          this.m_pGame.CreateStar(70);
          this.m_pGame.CreateStar(0x4a, 4);
          this.m_pGame.CreateObstacle(0x48, 0, 5, 6, 0);
          this.m_pGame.CreateObstacle(0x48, 3, 5, 4, 6);
          this.m_pGame.CreateObstacle(0x4a, 0, 5, 4, 6);
          this.m_pGame.CreateObstacle(0x4b, 1, 5, 4, 6);
          this.m_pGame.CreateObstacle(0x4b, 1, 5, 6, 0);
          this.m_pGame.CreateStar(0x4f);
          this.m_pGame.CreateStar(0x52, 2);
          this.m_pGame.CreateObstacle(0x51, 1, 5, 6, 0);
          this.m_pGame.CreateStar(0x54);
          this.m_pGame.CreateStar(0x58, 2);
          this.m_pGame.CreateObstacle(0x57, 2, 5, 6, 0);
          this.m_pGame.CreateStar(0x5b);
      }

      private void CreateLevel13W3()
      {
          this.m_pGround.SetSize(0x5f);
          this.m_pGame.CreateStar(6);
          this.m_pGame.CreateStar(8);
          this.m_pGame.CreateStar(12, 4);
          this.m_pGame.CreateBouncePadUp(14, 0, 8);
          this.m_pGame.CreateStar(0x12, 6);
          this.m_pGame.CreateObstacle(0x12, 0, 5, 12, 0);
          this.m_pGame.CreateObstacle(0x12, 0, 5, 12, 12);
          this.m_pGame.CreateStar(20, 4);
          this.m_pGame.CreateStar(0x15, 2);
          this.m_pGame.CreateStar(0x16);
          this.m_pGame.CreateStar(0x18);
          this.m_pGame.CreateStar(0x1b, 3);
          this.m_pGame.CreateBouncePadUp(0x1c, 0, 7);
          this.m_pGame.CreateObstacle(30, 2, 5, 12, 0);
          this.m_pGame.CreateObstacle(30, 3, 5, 6, 12);
          this.m_pGame.CreateStar(0x1f, 5);
          this.m_pGame.CreateStar(0x21, 2);
          this.m_pGame.CreateStar(0x23);
          this.m_pGame.CreateStar(0x26, 3);
          this.m_pGame.CreateObstacle(0x26, 0, 5, 6, 0);
          this.m_pGame.CreateStar(0x29);
          this.m_pGame.CreateStar(0x2c, 3);
          this.m_pGame.CreateObstacle(0x2b, 3, 5, 4, 0);
          this.m_pGame.CreateObstacle(0x2b, 3, 5, 6, 4);
          this.m_pGame.CreateStar(0x2e);
          this.m_pGame.CreateStar(0x30);
          this.m_pGame.CreateObstacle(0x33, 0, 5, 12, 0);
          this.m_pGame.CreateObstacle(0x35, 0, 5, 12, 0);
          this.m_pGame.CreateObstacle(0x34, 0, 12, 6, 12);
          this.m_pGame.CreateForceField(0x33, 2, 4, 0x12);
          this.m_pGame.CreateStar(0x38, 4);
          this.m_pGame.CreateStar(0x39, 2);
          this.m_pGame.CreateStar(0x3b);
          this.m_pGame.CreateStar(0x3d);
          this.m_pGame.CreateObstacle(0x3f, 0, 5, 9, 0);
          this.m_pGame.CreateObstacle(0x42, 0, 6, 0x20, 10);
          this.m_pGame.CreateStar(0x42);
          this.m_pGame.CreateStar(0x44);
          this.m_pGame.CreateObstacle(70, 0, 5, 9, 0);
          this.m_pGame.CreateStar(0x49);
          this.m_pGame.CreateStar(0x4b);
          this.m_pGame.CreateStar(0x4e, 4);
          this.m_pGame.CreateObstacle(0x4d, 0, 12, 6, 0);
          this.m_pGame.CreateObstacle(0x4d, 3, 12, 6, 6);
          this.m_pGame.CreateStar(80, 4);
          this.m_pGame.CreateStar(0x53);
          this.m_pGame.CreateStar(0x55);
      }

      private void CreateLevel14W1()
      {
          this.m_pGround.SetSize(60);
          this.m_pGame.CreateStar(7);
          this.m_pGame.CreateStar(9);
          this.m_pGame.CreateStar(11);
          this.m_pGame.CreateObstacle(10, 0, 6, 0x20, 6);
          this.m_pGame.CreateObstacle(13, 1, 20, 3, 0);
          this.m_pGame.CreateStar(14, 4);
          this.m_pGame.CreateStar(0x10, 4);
          this.m_pGame.CreateStar(0x13);
          this.m_pGame.CreateStar(0x15);
          this.m_pGame.CreateStar(0x17);
          this.m_pGame.CreateObstacle(0x15, 0, 6, 0x20, 6);
          this.m_pGame.CreateObstacle(0x19, 1, 14, 8, 0);
          this.m_pGame.CreateStar(30);
          this.m_pGame.CreateObstacle(0x20, 0, 6, 0x20, 6);
          this.m_pGame.CreateStar(0x20);
          this.m_pGame.CreateStar(0x22);
          this.m_pGame.CreateObstacle(0x23, 2, 5, 6, 0);
          this.m_pGame.CreateStar(40);
          this.m_pGame.CreateObstacle(0x29, 2, 5, 6, 0);
          this.m_pGame.CreateStar(0x2e);
          this.m_pGame.CreateObstacle(0x2f, 2, 5, 6, 0);
          this.m_pGame.CreateStar(0x33);
          this.m_pGame.CreateStar(0x35);
      }

      private void CreateLevel14W2()
      {
          this.m_pGround.SetSize(0x5f);
          this.m_pGame.CreateStar(8);
          this.m_pGame.CreateStar(10);
          this.m_pGame.CreateStar(12);
          this.m_pGame.CreateObstacle(10, 0, 6, 0x20, 10);
          this.m_pGame.CreateObstacle(13, 1, 5, 6, 0);
          this.m_pGame.CreateObstacle(14, 0, 5, 6, 6);
          this.m_pGame.CreateObstacle(15, 1, 5, 4, 6);
          this.m_pGame.CreateObstacle(0x10, 0, 5, 6, 0);
          this.m_pGame.CreateStar(0x10, 4);
          this.m_pGame.CreateStar(0x13);
          this.m_pGame.CreateStar(0x15);
          this.m_pGame.CreateForceField(0x16, 0, 3, 0);
          this.m_pGame.CreateObstacle(0x16, 2, 8, 0x20, 6);
          this.m_pGround.AddHoleAt(0x16);
          this.m_pGround.AddHoleAt(0x17);
          this.m_pGround.AddHoleAt(0x18);
          this.m_pGame.CreateStar(0x19);
          this.m_pGame.CreateObstacle(0x1b, 3, 5, 6, 0);
          this.m_pGame.CreateStar(0x1c, 4);
          this.m_pGame.CreateStar(30, 4);
          this.m_pGame.CreateObstacle(0x1d, 3, 5, 6, 0);
          this.m_pGame.CreateStar(0x22);
          this.m_pGame.CreateStar(0x24);
          this.m_pGame.CreateObstacle(0x26, 0, 5, 6, 0);
          this.m_pGame.CreateForceField(40, 0, 4, 4);
          this.m_pGame.CreateObstacle(0x29, 0, 8, 0x20, 8);
          this.m_pGame.CreateStar(0x2c);
          this.m_pGame.CreateStar(0x2e);
          this.m_pGame.CreateStar(50, 4);
          this.m_pGame.CreateObstacle(0x31, 1, 11, 11, 0);
          this.m_pGame.CreateObstacle(50, 0, 6, 4, 11);
          this.m_pGame.CreateStar(0x37);
          this.m_pGame.CreateObstacle(0x39, 3, 5, 4, 0);
          this.m_pGame.CreateObstacle(0x39, 3, 5, 4, 4);
          this.m_pGame.CreateStar(0x3a, 2);
          this.m_pGame.CreateStar(60);
          this.m_pGame.CreateStar(0x3e);
          this.m_pGame.CreateObstacle(0x40, 3, 20, 3, 0);
          this.m_pGame.CreateObstacle(0x42, 0, 9, 9, 3);
          this.m_pGame.CreateObstacle(0x42, 3, 5, 4, 12);
          this.m_pGame.CreateStar(0x47);
          this.m_pGame.CreateForceField(0x49, 2, 3, 8);
          this.m_pGame.CreateStar(0x4c, 4);
          this.m_pGround.AddHoleAt(0x4a);
          this.m_pGround.AddHoleAt(0x4b);
          this.m_pGround.AddHoleAt(0x4c);
          this.m_pGame.CreateStar(0x4f);
          this.m_pGame.CreateStar(0x51);
          this.m_pGame.CreateStar(0x53);
          this.m_pGround.AddHoleAt(0x54);
          this.m_pGround.AddHoleAt(0x57);
          this.m_pGround.AddHoleAt(0x58);
          this.m_pGame.CreateStar(0x59);
          this.m_pGame.CreateStar(0x5b);
      }

      private void CreateLevel14W3()
      {
          this.m_pGround.SetSize(0x5f);
          this.m_pGame.CreateStar(7);
          this.m_pGame.CreateStar(9);
          this.m_pGame.CreateStar(10, 3);
          this.m_pGame.CreateObstacle(11, 0, 0x15, 2, 0);
          this.m_pGame.CreateObstacle(11, 3, 5, 6, 2);
          this.m_pGame.CreateObstacle(14, 1, 5, 6, 2);
          this.m_pGame.CreateObstacle(11, 0, 0x15, 2, 8);
          this.m_pGame.CreateForceField(14, 0, 3, 12);
          this.m_pGame.CreateStar(0x12, 2);
          this.m_pGame.CreateStar(0x13);
          this.m_pGame.CreateStar(0x15, 2);
          this.m_pGame.CreateExitableForceField(0x16, 0, 10, 0x10);
          this.m_pGame.CreateObstacle(0x17, 3, 6, 13, 0);
          this.m_pGame.CreateStar(0x1d);
          this.m_pGame.CreateObstacle(0x20, 1, 6, 4, 0);
          this.m_pGame.CreateObstacle(0x21, 3, 6, 13, 0);
          this.m_pGame.CreateStar(0x25);
          this.m_pGame.CreateObstacle(40, 2, 6, 8, 0);
          this.m_pGround.AddHoleAt(0x2a);
          this.m_pGame.CreateStar(0x2b);
          this.m_pGame.CreateSpeedChangeUpStart(0x2d);
          this.m_pGame.CreateStar(0x2f);
          this.m_pGame.CreateStar(0x35, 6);
          this.m_pGame.CreateBouncePadUp(50, 0, 7);
          this.m_pGame.CreateObstacle(0x35, 2, 5, 12, 0);
          this.m_pGame.CreateObstacle(0x35, 1, 6, 5, 12);
          this.m_pGame.CreateStar(0x39);
          this.m_pGame.CreateStar(0x3d, 4);
          this.m_pGame.CreateObstacle(60, 2, 5, 9, 0);
          this.m_pGame.CreateObstacle(0x3e, 0, 5, 9, 0);
          this.m_pGame.CreateForceField(0x41, 0, 3, 8);
          this.m_pGame.CreateStar(0x45, 1);
          this.m_pGame.CreateStar(70);
          this.m_pGame.CreateSpeedChangeUpEnd(0x48);
          this.m_pGame.CreateStar(0x4a);
          this.m_pGround.AddHoleAt(0x4c);
          this.m_pGame.CreateObstacle(0x4e, 0, 9, 9, 0);
          this.m_pGame.CreateObstacle(0x4e, 2, 6, 5, 9);
          this.m_pGame.CreateStar(0x54);
          this.m_pGame.CreateStar(0x56);
          this.m_pGround.AddHoleAt(0x58);
      }

      private void CreateLevel15W1()
      {
          this.m_pGround.SetSize(60);
          this.m_pGame.CreateStar(6);
          this.m_pGame.CreateStar(8);
          this.m_pGame.CreateObstacle(10, 0, 5, 4, 0);
          this.m_pGame.CreateStar(12);
          this.m_pGame.CreateStar(14);
          this.m_pGame.CreateObstacle(13, 2, 4, 0x20, 6);
          this.m_pGame.CreateStar(0x10);
          this.m_pGame.CreateObstacle(0x11, 1, 5, 4, 0);
          this.m_pGame.CreateStar(0x15);
          this.m_pGame.CreateObstacle(0x17, 0, 9, 9, 0);
          this.m_pGame.CreateStar(0x1c);
          this.m_pGame.CreateStar(30);
          this.m_pGame.CreateObstacle(0x20, 3, 6, 13, 0);
          this.m_pGame.CreateStar(0x26);
          this.m_pGame.CreateObstacle(40, 0, 5, 4, 0);
          this.m_pGame.CreateStar(0x2c);
          this.m_pGame.CreateObstacle(0x2e, 2, 5, 6, 0);
          this.m_pGame.CreateStar(0x31);
          this.m_pGame.CreateStar(0x33);
      }

      private void CreateLevel15W2()
      {
          this.m_pGround.SetSize(0x5f);
          this.m_pGame.CreateStar(7);
          this.m_pGame.CreateStar(9);
          this.m_pGame.CreateObstacle(10, 3, 6, 5, 0);
          this.m_pGame.CreateStar(14);
          this.m_pGame.CreateObstacle(15, 3, 6, 5, 0);
          this.m_pGame.CreateStar(0x13);
          this.m_pGame.CreateForceField(0x16, 1, 8, 0x10);
          this.m_pGame.CreateForceField(0x17, 0, 3, 12);
          this.m_pGround.AddHoleAt(0x18);
          this.m_pGround.AddHoleAt(0x19);
          this.m_pGround.AddHoleAt(0x1a);
          this.m_pGame.CreateStar(0x1d);
          this.m_pGame.CreateObstacle(30, 2, 5, 6, 0);
          this.m_pGame.CreateStar(0x22);
          this.m_pGame.CreateStar(0x24);
          this.m_pGame.CreateObstacle(0x23, 2, 6, 0x20, 8);
          this.m_pGame.CreateSpeedChangeUpStart(0x26);
          this.m_pGround.AddHoleAt(40);
          this.m_pGame.CreateStar(0x2a, 2);
          this.m_pGame.CreateStar(0x2c);
          this.m_pGround.AddHoleAt(0x2d);
          this.m_pGame.CreateStar(0x30, 2);
          this.m_pGame.CreateStar(50);
          this.m_pGround.AddHoleAt(0x33);
          this.m_pGame.CreateStar(0x36, 2);
          this.m_pGame.CreateStar(0x38);
          this.m_pGame.CreateObstacle(0x3a, 3, 6, 8, 0);
          this.m_pGame.CreateStar(0x3b, 2);
          this.m_pGame.CreateStar(0x3d);
          this.m_pGame.CreateStar(0x3f);
          this.m_pGame.CreateObstacle(0x42, 0, 5, 6, 0);
          this.m_pGame.CreateObstacle(0x43, 3, 5, 9, 0);
          this.m_pGame.CreateSpeedChangeUpEnd(0x48);
          this.m_pGame.CreateStar(0x4a);
          this.m_pGame.CreateStar(0x4c);
          this.m_pGame.CreateStar(0x4e);
          this.m_pGame.CreateStar(0x51, 4);
          this.m_pGame.CreateStar(0x53, 4);
          this.m_pGame.CreateObstacle(80, 0, 20, 3, 0);
          this.m_pGame.CreateObstacle(80, 0, 20, 3, 3);
          this.m_pGame.CreateStar(0x56);
          this.m_pGame.CreateStar(0x58);
      }

      private void CreateLevel15W3()
      {
          this.m_pGround.SetSize(0x5f);
          this.m_pGame.CreateStar(6);
          this.m_pGame.CreateStar(8);
          this.m_pGame.CreateObstacle(10, 0, 9, 9, 0);
          this.m_pGame.CreateObstacle(11, 2, 5, 4, 9);
          this.m_pGame.CreateObstacle(12, 2, 5, 9, 0);
          this.m_pGame.CreateStar(15);
          this.m_pGame.CreateObstacle(0x12, 1, 6, 8, 0);
          this.m_pGame.CreateStar(0x15);
          this.m_pGame.CreateStar(0x17);
          this.m_pGame.CreateObstacle(0x15, 2, 6, 0x20, 9);
          this.m_pGame.CreateObstacle(0x19, 0, 5, 9, 0);
          this.m_pGame.CreateStar(0x1c);
          this.m_pGame.CreateStar(30);
          this.m_pGame.CreateObstacle(0x1c, 1, 8, 0x20, 9);
          this.m_pGame.CreateObstacle(0x20, 3, 5, 4, 0);
          this.m_pGame.CreateObstacle(0x20, 3, 5, 6, 4);
          this.m_pGame.CreateStar(0x24);
          this.m_pGame.CreateStar(0x26);
          this.m_pGame.CreateExitableForceField(0x29, 0, 7, 12);
          this.m_pGame.CreateObstacle(0x2a, 2, 5, 12, 0);
          this.m_pGame.CreateStar(0x2f);
          this.m_pGame.CreateObstacle(0x33, 3, 6, 13, 0);
          this.m_pGame.CreateStar(0x34, 4);
          this.m_pGame.CreateForceField(0x37, 0, 3, 6);
          this.m_pGame.CreateStar(0x3b, 1);
          this.m_pGame.CreateStar(60);
          this.m_pGame.CreateStar(0x3e);
          this.m_pGame.CreateStar(0x40);
          this.m_pGame.CreateObstacle(0x3e, 0, 6, 0x20, 9);
          this.m_pGame.CreateObstacle(0x41, 3, 20, 3, 0);
          this.m_pGame.CreateObstacle(0x41, 3, 5, 6, 3);
          this.m_pGame.CreateStar(0x47);
          this.m_pGame.CreateStar(0x49);
          this.m_pGame.CreateObstacle(0x48, 2, 6, 0x20, 8);
          this.m_pGame.CreateObstacle(0x4c, 2, 5, 4, 0);
          this.m_pGame.CreateObstacle(0x4d, 3, 5, 4, 0);
          this.m_pGame.CreateObstacle(0x4f, 0, 5, 4, 0);
          this.m_pGame.CreateObstacle(0x4c, 2, 6, 8, 4);
          this.m_pGame.CreateStar(0x52);
          this.m_pGame.CreateStar(0x54);
          this.m_pGame.CreateStar(0x56);
          this.m_pGame.CreateObstacle(0x57, 3, 6, 8, 0);
      }

      private void CreateLevel16W1()
      {
          this.m_pGround.SetSize(0x41);
          this.m_pGame.CreateStar(6);
          this.m_pGame.CreateStar(8);
          this.m_pGame.CreateObstacle(10, 0, 20, 3, 0);
          this.m_pGame.CreateStar(11, 5);
          this.m_pGame.CreateStar(13, 5);
          this.m_pGame.CreateStar(0x10);
          this.m_pGame.CreateObstacle(15, 0, 20, 0x20, 0x10);
          this.m_pGame.CreateStar(0x12, 2);
          this.m_pGame.CreateObstacle(0x12, 0, 5, 4, 0);
          this.m_pGame.CreateStar(0x15);
          this.m_pGame.CreateObstacle(0x19, 1, 6, 13, 0);
          this.m_pGame.CreateStar(0x1f);
          this.m_pGame.CreateStar(0x21);
          this.m_pGame.CreateObstacle(0x1f, 2, 4, 0x20, 6);
          this.m_pGame.CreateObstacle(0x22, 2, 12, 6, 0);
          this.m_pGame.CreateObstacle(0x23, 1, 5, 4, 6);
          this.m_pGame.CreateStar(40);
          this.m_pGround.AddHoleAt(0x2a);
          this.m_pGame.CreateObstacle(0x2b, 1, 5, 6, 0);
          this.m_pGame.CreateStar(0x30);
          this.m_pGame.CreateObstacle(50, 3, 11, 13, 0);
          this.m_pGame.CreateStar(0x38);
          this.m_pGame.CreateStar(0x3a);
      }

      private void CreateLevel16W2()
      {
          this.m_pGround.SetSize(100);
          this.m_pGame.CreateStar(6);
          this.m_pGame.CreateStar(8);
          this.m_pGame.CreateObstacle(10, 3, 5, 9, 0);
          this.m_pGame.CreateObstacle(10, 3, 20, 3, 9);
          this.m_pGame.CreateObstacle(10, 3, 20, 3, 12);
          this.m_pGame.CreateObstacle(15, 1, 5, 9, 0);
          this.m_pGame.CreateForceField(13, 0, 3, 0x10);
          this.m_pGame.CreateStar(0x10, 3);
          this.m_pGame.CreateStar(0x12);
          this.m_pGame.CreateStar(0x15, 3);
          this.m_pGame.CreateStar(0x17, 4);
          this.m_pGame.CreateStar(0x19, 4);
          this.m_pGame.CreateStar(0x1b, 3);
          this.m_pGame.CreateObstacle(0x15, 1, 5, 6, 0);
          this.m_pGame.CreateObstacle(0x17, 1, 6, 8, 0);
          this.m_pGame.CreateObstacle(0x19, 3, 5, 6, 0);
          this.m_pGame.CreateStar(0x1c);
          this.m_pGame.CreateObstacle(0x1d, 2, 6, 0x20, 5);
          this.m_pGame.CreateStar(0x1f);
          this.m_pGame.CreateStar(0x20, 1);
          this.m_pGround.AddHoleAt(0x20);
          this.m_pGround.AddHoleAt(0x22);
          this.m_pGround.AddHoleAt(0x23);
          this.m_pGame.CreateStar(0x23, 1);
          this.m_pGame.CreateStar(0x24);
          this.m_pGame.CreateStar(0x26);
          this.m_pGame.CreateStar(40);
          this.m_pGame.CreateObstacle(0x25, 2, 6, 0x20, 5);
          this.m_pGame.CreateObstacle(0x29, 2, 9, 9, 0);
          this.m_pGame.CreateObstacle(0x2b, 3, 9, 9, 0);
          this.m_pGame.CreateStar(0x2f);
          this.m_pGame.CreateStar(0x31);
          this.m_pGame.CreateObstacle(0x33, 2, 9, 9, 0);
          this.m_pGame.CreateObstacle(0x35, 3, 9, 9, 0);
          this.m_pGame.CreateStar(0x39);
          this.m_pGame.CreateStar(0x3b);
          this.m_pGame.CreateObstacle(0x3d, 1, 6, 8, 0);
          this.m_pGame.CreateStar(0x40);
          this.m_pGround.AddHoleAt(0x42);
          this.m_pGround.AddHoleAt(0x43);
          this.m_pGame.CreateObstacle(0x44, 1, 12, 6, 0);
          this.m_pGame.CreateForceField(0x45, 0, 3, 8);
          this.m_pGame.CreateStar(0x4a);
          this.m_pGame.CreateObstacle(0x4b, 3, 5, 6, 0);
          this.m_pGame.CreateObstacle(0x4d, 0, 5, 4, 0);
          this.m_pGame.CreateStar(0x4f);
          this.m_pGame.CreateStar(0x51);
          this.m_pGame.CreateObstacle(0x54, 2, 5, 6, 0);
          this.m_pGame.CreateObstacle(0x54, 3, 5, 6, 6);
          this.m_pGame.CreateStar(0x59);
          this.m_pGame.CreateStar(0x5b);
          this.m_pGame.CreateObstacle(0x5d, 1, 5, 6, 0);
          this.m_pGame.CreateStar(0x60);
      }

      private void CreateLevel16W3()
      {
          this.m_pGround.SetSize(100);
          this.m_pGame.CreateStar(6);
          this.m_pGame.CreateStar(8);
          this.m_pGame.CreateStar(13, 4);
          this.m_pGame.CreateObstacle(11, 0, 20, 3, 0);
          this.m_pGame.CreateObstacle(11, 1, 6, 8, 3);
          this.m_pGame.CreateBouncePadUp(15, 0, 6);
          this.m_pGame.CreateObstacle(0x10, 1, 12, 6, 0);
          this.m_pGame.CreateObstacle(0x11, 3, 11, 13, 0);
          this.m_pGame.CreateObstacle(0x12, 3, 6, 8, 13);
          this.m_pGame.CreateStar(0x16, 4);
          this.m_pGame.CreateStar(0x17, 2);
          this.m_pGame.CreateStar(0x18);
          this.m_pGame.CreateStar(0x1a);
          this.m_pGround.AddHoleAt(0x1b);
          this.m_pGame.CreateObstacle(0x1d, 0, 6, 8, 0);
          this.m_pGame.CreateStar(0x1f);
          this.m_pGame.CreateStar(0x21);
          this.m_pGame.CreateSpeedChangeUpStart(0x23);
          this.m_pGame.CreateStar(0x25);
          this.m_pGame.CreateStar(0x27);
          this.m_pGround.AddHoleAt(0x29);
          this.m_pGround.AddHoleAt(0x2a);
          this.m_pGame.CreateBouncePadUp(0x2b, 0, 7);
          this.m_pGround.AddHoleAt(0x2d);
          this.m_pGround.AddHoleAt(0x2e);
          this.m_pGame.CreateObstacle(0x2f, 0, 6, 13, 0);
          this.m_pGame.CreateStar(50);
          this.m_pGame.CreateStar(0x34);
          this.m_pGround.AddHoleAt(0x36);
          this.m_pGround.AddHoleAt(0x38);
          this.m_pGame.CreateSpeedChangeUpEnd(0x3a);
          this.m_pGame.CreateStar(60);
          this.m_pGame.CreateForceField(0x3f, 0, 3, 0x10);
          this.m_pGame.CreateBouncePadUp(0x43, 0, 7);
          this.m_pGame.CreateObstacle(70, 1, 6, 8, 0);
          this.m_pGame.CreateObstacle(70, 1, 6, 8, 8);
          this.m_pGame.CreateStar(0x49, 3);
          this.m_pGame.CreateStar(0x4a, 1);
          this.m_pGame.CreateStar(0x4c);
          this.m_pGame.CreateObstacle(0x4f, 2, 6, 5, 0);
          this.m_pGame.CreateObstacle(0x53, 0, 5, 4, 0);
          this.m_pGame.CreateBouncePadUp(0x54, 0, 7);
          this.m_pGame.CreateObstacle(0x56, 2, 12, 6, 0);
          this.m_pGame.CreateObstacle(0x57, 0, 12, 6, 6);
          this.m_pGame.CreateObstacle(0x57, 2, 12, 6, 12);
          this.m_pGame.CreateStar(0x5b, 4);
          this.m_pGame.CreateStar(0x5c, 2);
          this.m_pGame.CreateStar(0x5d);
          this.m_pGame.CreateStar(0x5f);
      }

      private void CreateLevel17W1()
      {
          this.m_pGround.SetSize(0x41);
          this.m_pGame.CreateStar(6);
          this.m_pGame.CreateStar(8);
          this.m_pGame.CreateObstacle(10, 1, 6, 13, 0);
          this.m_pGame.CreateStar(15);
          this.m_pGame.CreateObstacle(0x13, 1, 6, 13, 0);
          this.m_pGame.CreateObstacle(20, 3, 6, 13, 0);
          this.m_pGame.CreateStar(0x18);
          this.m_pGame.CreateStar(0x1a);
          this.m_pGame.CreateObstacle(0x1d, 0, 6, 13, 0);
          this.m_pGame.CreateStar(0x22);
          this.m_pGame.CreateObstacle(0x24, 3, 5, 4, 0);
          this.m_pGame.CreateObstacle(0x25, 0, 5, 4, 4);
          this.m_pGame.CreateStar(40);
          this.m_pGame.CreateObstacle(0x2a, 1, 5, 4, 0);
          this.m_pGame.CreateObstacle(0x2a, 3, 5, 4, 4);
          this.m_pGame.CreateStar(0x2e);
          this.m_pGame.CreateStar(0x30);
          this.m_pGame.CreateObstacle(0x33, 0, 6, 13, 0);
          this.m_pGame.CreateStar(0x37);
          this.m_pGame.CreateStar(0x39);
      }

      private void CreateLevel17W2()
      {
          this.m_pGround.SetSize(100);
          this.m_pGame.CreateStar(6);
          this.m_pGame.CreateStar(8);
          this.m_pGround.AddHoleAt(10);
          this.m_pGround.AddHoleAt(11);
          this.m_pGame.CreateStar(13);
          this.m_pGround.AddHoleAt(14);
          this.m_pGame.CreateStar(0x13);
          this.m_pGround.AddHoleAt(20);
          this.m_pGame.CreateStar(0x18);
          this.m_pGame.CreateObstacle(0x1a, 3, 5, 6, 0);
          this.m_pGame.CreateStar(0x1d);
          this.m_pGame.CreateForceField(0x24, 0, 4, 0x10);
          this.m_pGround.AddHoleAt(0x1f);
          this.m_pGround.AddHoleAt(0x20);
          this.m_pGround.AddHoleAt(0x21);
          this.m_pGround.AddHoleAt(0x22);
          this.m_pGround.AddHoleAt(0x23);
          this.m_pGround.AddHoleAt(0x24);
          this.m_pGame.CreateStar(0x2c);
          this.m_pGame.CreateObstacle(0x2d, 3, 5, 6, 0);
          this.m_pGame.CreateObstacle(0x30, 3, 5, 6, 0);
          this.m_pGame.CreateStar(0x34);
          this.m_pGame.CreateObstacle(0x34, 0, 6, 0x20, 9);
          this.m_pGame.CreateObstacle(0x37, 0, 5, 6, 0);
          this.m_pGame.CreateStar(0x3a);
          this.m_pGame.CreateForceField(60, 0, 6, 10);
          this.m_pGame.CreateObstacle(60, 3, 5, 6, 0);
          this.m_pGame.CreateObstacle(0x3e, 0, 5, 6, 0);
          this.m_pGame.CreateObstacle(0x3f, 1, 5, 6, 0);
          this.m_pGame.CreateObstacle(0x40, 2, 5, 6, 0);
          this.m_pGame.CreateStar(0x43);
          this.m_pGame.CreateStar(0x45);
          this.m_pGround.AddHoleAt(70);
          this.m_pGround.AddHoleAt(0x47);
          this.m_pGround.AddHoleAt(0x4a);
          this.m_pGame.CreateStar(0x4b);
          this.m_pGame.CreateStar(80, 4);
          this.m_pGame.CreateObstacle(0x4f, 0, 6, 13, 0);
          this.m_pGame.CreateForceField(0x52, 0, 3, 8);
          this.m_pGround.AddHoleAt(0x52);
          this.m_pGround.AddHoleAt(0x53);
          this.m_pGame.CreateStar(0x58);
          this.m_pGame.CreateObstacle(90, 2, 6, 8, 0);
          this.m_pGame.CreateStar(0x5d);
          this.m_pGame.CreateStar(0x5f);
      }

      private void CreateLevel17W3()
      {
          this.m_pGround.SetSize(0x69);
          this.m_pGame.CreateStar(6);
          this.m_pGame.CreateStar(8);
          this.m_pGame.CreateObstacle(10, 0, 5, 6, 0);
          this.m_pGame.CreateObstacle(11, 2, 5, 6, 0);
          this.m_pGame.CreateStar(14);
          this.m_pGame.CreateObstacle(0x10, 1, 5, 6, 0);
          this.m_pGame.CreateObstacle(0x11, 2, 5, 6, 0);
          this.m_pGame.CreateStar(20);
          this.m_pGame.CreateSpeedChangeUpStart(0x16);
          this.m_pGame.CreateStar(0x18);
          this.m_pGame.CreateObstacle(0x1a, 0, 5, 6, 0);
          this.m_pGame.CreateObstacle(0x1b, 1, 5, 6, 0);
          this.m_pGame.CreateStar(30);
          this.m_pGame.CreateObstacle(0x20, 0, 5, 6, 0);
          this.m_pGame.CreateObstacle(0x21, 1, 5, 6, 0);
          this.m_pGame.CreateStar(0x24);
          this.m_pGame.CreateSpeedChangeUpEnd(0x26);
          this.m_pGame.CreateStar(40);
          this.m_pGame.CreateObstacle(0x2a, 0, 6, 5, 0);
          this.m_pGame.CreateStar(0x2d);
          this.m_pGame.CreateStar(0x2f);
          this.m_pGame.CreateStar(0x31, 3);
          this.m_pGame.CreateObstacle(50, 0, 12, 6, 0);
          this.m_pGame.CreateObstacle(0x33, 1, 6, 8, 6);
          this.m_pGame.CreateBouncePadUp(0x35, 2, 6);
          this.m_pGame.CreateObstacle(0x39, 3, 14, 8, 0);
          this.m_pGame.CreateObstacle(0x3a, 1, 5, 4, 8);
          this.m_pGame.CreateStar(0x38, 4);
          this.m_pGame.CreateStar(0x3a, 6);
          this.m_pGame.CreateStar(60, 4);
          this.m_pGame.CreateStar(0x3d, 2);
          this.m_pGame.CreateStar(0x3f);
          this.m_pGame.CreateStar(0x41, 2);
          this.m_pGame.CreateBouncePadUp(0x43, 2, 7);
          this.m_pGame.CreateStar(0x47, 6);
          this.m_pGame.CreateObstacle(70, 1, 11, 13, 0);
          this.m_pGame.CreateStar(0x49, 3);
          this.m_pGame.CreateStar(0x4b);
          this.m_pGround.AddHoleAt(0x4d);
          this.m_pGame.CreateObstacle(0x4e, 0, 6, 5, 0);
          this.m_pGame.CreateObstacle(0x4e, 3, 5, 6, 5);
          this.m_pGround.AddHoleAt(80);
          this.m_pGround.AddHoleAt(0x51);
          this.m_pGround.AddHoleAt(0x52);
          this.m_pGame.CreateForceField(0x4f, 0, 3, 12);
          this.m_pGame.CreateStar(0x53, 1);
          this.m_pGame.CreateStar(0x55);
          this.m_pGame.CreateStar(0x57);
          this.m_pGame.CreateObstacle(0x55, 3, 8, 0x20, 8);
          this.m_pGame.CreateObstacle(90, 3, 11, 13, 0);
          this.m_pGame.CreateStar(0x62);
          this.m_pGame.CreateStar(100);
      }

      private void CreateLevel18W1()
      {
          this.m_pGround.SetSize(0x41);
          this.m_pGame.CreateStar(8);
          this.m_pGame.CreateStar(10);
          this.m_pGame.CreateStar(12);
          this.m_pGame.CreateObstacle(10, 0, 8, 0x20, 8);
          this.m_pGame.CreateObstacle(13, 1, 20, 3, 0);
          this.m_pGame.CreateStar(14, 2);
          this.m_pGame.CreateStar(0x10, 4);
          this.m_pGame.CreateStar(0x12, 2);
          this.m_pGame.CreateStar(20);
          this.m_pGame.CreateObstacle(0x16, 0, 20, 3, 0);
          this.m_pGame.CreateStar(0x16, 2);
          this.m_pGame.CreateStar(0x18, 4);
          this.m_pGame.CreateStar(0x1b, 2);
          this.m_pGame.CreateStar(0x1d);
          this.m_pGame.CreateObstacle(0x1f, 1, 20, 3, 0);
          this.m_pGame.CreateStar(0x1f, 2);
          this.m_pGame.CreateStar(0x21, 4);
          this.m_pGame.CreateStar(0x24, 2);
          this.m_pGame.CreateStar(0x26);
          this.m_pGround.AddHoleAt(40);
          this.m_pGame.CreateStar(0x2c);
          this.m_pGame.CreateObstacle(0x30, 1, 11, 11, 0);
          this.m_pGame.CreateStar(0x35);
          this.m_pGame.CreateStar(0x38, 3);
          this.m_pGame.CreateStar(0x3b);
          this.m_pGround.AddHoleAt(0x37);
          this.m_pGround.AddHoleAt(0x38);
          this.m_pGround.AddHoleAt(0x39);
      }

      private void CreateLevel18W2()
      {
          this.m_pGround.SetSize(100);
          this.m_pGame.CreateStar(7);
          this.m_pGame.CreateStar(9, 4);
          this.m_pGame.CreateStar(11, 4);
          this.m_pGame.CreateObstacle(10, 2, 6, 8, 0);
          this.m_pGame.CreateObstacle(10, 0, 6, 8, 8);
          this.m_pGame.CreateStar(13, 2);
          this.m_pGame.CreateForceField(14, 0, 4, 0);
          this.m_pGround.AddHoleAt(14);
          this.m_pGround.AddHoleAt(15);
          this.m_pGround.AddHoleAt(0x10);
          this.m_pGround.AddHoleAt(0x11);
          this.m_pGame.CreateObstacle(0x10, 1, 6, 0x20, 5);
          this.m_pGame.CreateStar(0x12);
          this.m_pGround.AddHoleAt(0x13);
          this.m_pGround.AddHoleAt(20);
          this.m_pGame.CreateObstacle(0x15, 0, 5, 4, 0);
          this.m_pGame.CreateObstacle(0x15, 0, 5, 4, 4);
          this.m_pGame.CreateStar(0x17);
          this.m_pGame.CreateSpeedChangeUpStart(0x19);
          this.m_pGame.CreateObstacle(0x1b, 2, 5, 6, 0);
          this.m_pGame.CreateStar(30);
          this.m_pGame.CreateObstacle(0x21, 1, 5, 4, 0);
          this.m_pGame.CreateObstacle(0x21, 1, 5, 4, 4);
          this.m_pGame.CreateStar(0x24);
          this.m_pGround.AddHoleAt(0x26);
          this.m_pGround.AddHoleAt(0x27);
          this.m_pGame.CreateObstacle(40, 1, 14, 8, 0);
          this.m_pGame.CreateStar(0x2d);
          this.m_pGame.CreateObstacle(0x30, 1, 5, 4, 0);
          this.m_pGame.CreateObstacle(0x30, 1, 5, 4, 4);
          this.m_pGround.AddHoleAt(50);
          this.m_pGround.AddHoleAt(0x33);
          this.m_pGame.CreateStar(0x35);
          this.m_pGround.AddHoleAt(0x37);
          this.m_pGround.AddHoleAt(0x39);
          this.m_pGame.CreateSpeedChangeUpEnd(60);
          this.m_pGame.CreateStar(0x3e);
          this.m_pGame.CreateObstacle(0x3f, 3, 5, 4, 0);
          this.m_pGame.CreateObstacle(0x3f, 3, 5, 4, 4);
          this.m_pGround.AddHoleAt(0x41);
          this.m_pGround.AddHoleAt(0x42);
          this.m_pGround.AddHoleAt(0x43);
          this.m_pGame.CreateForceField(0x41, 0, 3, 0);
          this.m_pGround.AddHoleAt(70);
          this.m_pGame.CreateObstacle(0x47, 1, 5, 4, 0);
          this.m_pGame.CreateObstacle(0x47, 1, 5, 4, 4);
          this.m_pGame.CreateStar(0x47, 3);
          this.m_pGame.CreateStar(0x4a);
          this.m_pGame.CreateStar(0x4e, 4);
          this.m_pGame.CreateStar(80, 4);
          this.m_pGame.CreateObstacle(0x4c, 3, 20, 3, 0);
          this.m_pGame.CreateObstacle(0x4c, 3, 20, 3, 3);
          this.m_pGame.CreateStar(0x52, 3);
          this.m_pGame.CreateStar(0x54, 1);
          this.m_pGame.CreateObstacle(0x55, 1, 5, 6, 0);
          this.m_pGame.CreateStar(0x57, 4);
          this.m_pGame.CreateStar(90, 2);
          this.m_pGame.CreateStar(0x5b);
      }

      private void CreateLevel18W3()
      {
          this.m_pGround.SetSize(0x69);
          this.m_pGame.CreateStar(7);
          this.m_pGame.CreateStar(9);
          this.m_pGame.CreateStar(11);
          this.m_pGame.CreateStar(13);
          this.m_pGame.CreateObstacle(10, 0, 8, 0x20, 8);
          this.m_pGame.CreateObstacle(15, 0, 11, 13, 0);
          this.m_pGame.CreateStar(0x10, 4);
          this.m_pGame.CreateStar(20);
          this.m_pGame.CreateObstacle(0x18, 0, 11, 13, 0);
          this.m_pGame.CreateStar(0x19, 4);
          this.m_pGame.CreateStar(0x1d);
          this.m_pGround.AddHoleAt(0x1f);
          this.m_pGame.CreateExitableForceField(0x21, 1, 12, 10);
          this.m_pGame.CreateObstacle(0x20, 0, 9, 9, 0);
          this.m_pGame.CreateStar(0x24);
          this.m_pGame.CreateStar(0x26);
          this.m_pGame.CreateStar(40);
          this.m_pGame.CreateObstacle(0x2a, 0, 5, 6, 0);
          this.m_pGame.CreateStar(0x2c);
          this.m_pGame.CreateStar(0x2e);
          this.m_pGame.CreateObstacle(0x31, 3, 9, 9, 0);
          this.m_pGame.CreateStar(0x33, 4);
          this.m_pGame.CreateBouncePadUp(0x35, 0, 8);
          this.m_pGame.CreateObstacle(0x37, 0, 14, 8, 0);
          this.m_pGame.CreateObstacle(0x37, 2, 12, 6, 8);
          this.m_pGame.CreateObstacle(0x38, 2, 6, 8, 14);
          this.m_pGame.CreateStar(0x37, 4);
          this.m_pGame.CreateStar(0x39, 6);
          this.m_pGame.CreateStar(60, 4);
          this.m_pGame.CreateObstacle(0x3b, 1, 5, 9, 0);
          this.m_pGame.CreateStar(0x3e);
          this.m_pGame.CreateStar(0x40);
          this.m_pGround.AddHoleAt(0x41);
          this.m_pGame.CreateStar(0x43, 2);
          this.m_pGround.AddHoleAt(0x44);
          this.m_pGround.AddHoleAt(0x48);
          this.m_pGround.AddHoleAt(0x49);
          this.m_pGame.CreateStar(0x4a, 2);
          this.m_pGround.AddHoleAt(0x4b);
          this.m_pGame.CreateStar(0x4c);
          this.m_pGame.CreateStar(0x4e);
          this.m_pGame.CreateObstacle(80, 3, 6, 8, 0);
          this.m_pGame.CreateStar(0x52, 4);
          this.m_pGame.CreateObstacle(0x53, 0, 6, 8, 0);
          this.m_pGame.CreateStar(0x56);
          this.m_pGame.CreateStar(0x58);
          this.m_pGame.CreateStar(90, 3);
          this.m_pGame.CreateObstacle(90, 0, 6, 8, 0);
          this.m_pGame.CreateStar(0x5d);
          this.m_pGame.CreateStar(0x5f);
          this.m_pGame.CreateStar(0x61);
      }

      private void CreateLevel19W1()
      {
          this.m_pGround.SetSize(0x41);
          this.m_pGround.AddHoleAt(10);
          this.m_pGround.AddHoleAt(13);
          this.m_pGame.CreateObstacle(15, 2, 5, 4, 0);
          this.m_pGame.CreateStar(0x12);
          this.m_pGame.CreateObstacle(0x15, 1, 11, 13, 0);
          this.m_pGame.CreateStar(0x1a);
          this.m_pGame.CreateObstacle(0x1c, 2, 20, 3, 0);
          this.m_pGame.CreateStar(0x23);
          this.m_pGame.CreateStar(0x25);
          this.m_pGame.CreateObstacle(0x24, 0, 4, 0x20, 7);
          this.m_pGame.CreateObstacle(40, 0, 6, 13, 0);
          this.m_pGame.CreateStar(0x2c);
          this.m_pGround.AddHoleAt(0x2f);
          this.m_pGround.AddHoleAt(50);
          this.m_pGame.CreateStar(0x33);
          this.m_pGround.AddHoleAt(0x37);
          this.m_pGame.CreateStar(0x39);
          this.m_pGame.CreateStar(0x3b);
      }

      private void CreateLevel19W2()
      {
          this.m_pGround.SetSize(0x69);
          this.m_pGame.CreateStar(8);
          this.m_pGame.CreateObstacle(10, 2, 14, 8, 0);
          this.m_pGame.CreateObstacle(11, 0, 5, 6, 8);
          this.m_pGame.CreateObstacle(12, 1, 5, 6, 8);
          this.m_pGame.CreateStar(0x10);
          this.m_pGame.CreateObstacle(0x13, 0, 14, 8, 0);
          this.m_pGame.CreateObstacle(0x13, 2, 5, 6, 8);
          this.m_pGame.CreateObstacle(20, 3, 5, 4, 8);
          this.m_pGame.CreateStar(0x18);
          this.m_pGame.CreateObstacle(0x1b, 3, 14, 8, 0);
          this.m_pGame.CreateObstacle(0x1c, 1, 5, 6, 8);
          this.m_pGame.CreateObstacle(0x1d, 2, 5, 6, 8);
          this.m_pGame.CreateStar(0x20);
          this.m_pGame.CreateObstacle(0x23, 3, 5, 4, 0);
          this.m_pGame.CreateObstacle(0x23, 3, 5, 4, 4);
          this.m_pGame.CreateStar(0x26);
          this.m_pGame.CreateObstacle(0x29, 3, 14, 8, 0);
          this.m_pGame.CreateObstacle(0x2a, 1, 5, 6, 8);
          this.m_pGame.CreateObstacle(0x2b, 2, 5, 6, 8);
          this.m_pGame.CreateStar(0x2f);
          this.m_pGame.CreateObstacle(0x31, 3, 5, 4, 0);
          this.m_pGame.CreateObstacle(0x31, 3, 5, 4, 4);
          this.m_pGame.CreateStar(0x34);
          this.m_pGame.CreateStar(0x36);
          this.m_pGame.CreateObstacle(0x35, 3, 4, 0x20, 6);
          this.m_pGround.AddHoleAt(0x38);
          this.m_pGround.AddHoleAt(0x3a);
          this.m_pGame.CreateStar(60);
          this.m_pGround.AddHoleAt(0x3d);
          this.m_pGame.CreateForceField(0x40, 0, 5, 8);
          this.m_pGround.AddHoleAt(0x41);
          this.m_pGround.AddHoleAt(0x42);
          this.m_pGround.AddHoleAt(0x43);
          this.m_pGame.CreateStar(0x47);
          this.m_pGame.CreateObstacle(0x4a, 0, 11, 11, 0);
          this.m_pGame.CreateObstacle(0x4b, 0, 5, 4, 11);
          this.m_pGame.CreateStar(0x4f);
          this.m_pGame.CreateObstacle(0x51, 2, 14, 8, 0);
          this.m_pGame.CreateObstacle(0x52, 1, 6, 5, 8);
          this.m_pGround.AddHoleAt(0x55);
          this.m_pGround.AddHoleAt(0x56);
          this.m_pGround.AddHoleAt(0x57);
          this.m_pGround.AddHoleAt(0x58);
          this.m_pGround.AddHoleAt(0x59);
          this.m_pGame.CreateForceField(0x56, 0, 5, 12);
          this.m_pGame.CreateStar(0x5e);
          this.m_pGame.CreateObstacle(0x60, 2, 14, 8, 0);
          this.m_pGame.CreateStar(0x66);
          this.m_pGame.CreateStar(0x68);
      }

      private void CreateLevel19W3()
      {
          this.m_pGround.SetSize(110);
          this.m_pGame.CreateStar(6);
          this.m_pGame.CreateStar(8);
          this.m_pGround.AddHoleAt(10);
          this.m_pGame.CreateBouncePadUp(12, 0, 8);
          this.m_pGame.CreateObstacle(12, 1, 6, 8, 0);
          this.m_pGame.CreateObstacle(14, 1, 6, 8, 0);
          this.m_pGame.CreateObstacle(14, 1, 6, 8, 8);
          this.m_pGame.CreateExitableForceField(0x11, 0, 5, 10);
          this.m_pGame.CreateObstacle(0x16, 0, 6, 0x20, 7);
          this.m_pGame.CreateStar(0x15);
          this.m_pGame.CreateStar(0x17);
          this.m_pGame.CreateSpeedChangeUpStart(0x19);
          this.m_pGame.CreateStar(0x1c, 4);
          this.m_pGame.CreateStar(30, 4);
          this.m_pGame.CreateObstacle(0x1c, 0, 9, 9, 0);
          this.m_pGame.CreateStar(0x20);
          this.m_pGame.CreateStar(0x22);
          this.m_pGame.CreateStar(0x23, 3);
          this.m_pGame.CreateBouncePadUp(0x25, 0, 8);
          this.m_pGame.CreateStar(40, 6);
          this.m_pGame.CreateObstacle(0x27, 3, 9, 9, 0);
          this.m_pGame.CreateObstacle(0x27, 3, 9, 9, 9);
          this.m_pGame.CreateStar(0x2c);
          this.m_pGame.CreateSpeedChangeUpEnd(0x2e);
          this.m_pGame.CreateStar(0x30);
          this.m_pGame.CreateStar(0x31, 2);
          this.m_pGame.CreateStar(50, 4);
          this.m_pGame.CreateObstacle(50, 0, 9, 9, 0);
          this.m_pGame.CreateObstacle(0x33, 1, 5, 6, 9);
          this.m_pGame.CreateStar(0x34, 4);
          this.m_pGame.CreateStar(0x36, 1);
          this.m_pGame.CreateStar(0x38);
          this.m_pGame.CreateBouncePadUp(60, 1, 6);
          this.m_pGame.CreateStar(0x40, 5);
          this.m_pGame.CreateObstacle(0x3f, 3, 6, 13, 0);
          this.m_pGame.CreateObstacle(0x40, 0, 6, 5, 13);
          this.m_pGame.CreateStar(0x45);
          this.m_pGame.CreateSpeedChangeDownStart(0x47);
          this.m_pGame.CreateStar(0x49);
          this.m_pGame.CreateStar(0x4b);
          this.m_pGame.CreateObstacle(0x49, 0, 6, 0x20, 5);
          this.m_pGame.CreateObstacle(0x4d, 1, 6, 8, 0);
          this.m_pGame.CreateStar(80);
          this.m_pGame.CreateObstacle(0x53, 3, 6, 8, 0);
          this.m_pGame.CreateStar(0x57);
          this.m_pGame.CreateSpeedChangeDownStart(0x59);
          this.m_pGame.CreateStar(0x5b);
          this.m_pGame.CreateObstacle(0x5e, 0, 9, 9, 0);
          this.m_pGame.CreateObstacle(0x60, 3, 5, 9, 0);
          this.m_pGame.CreateStar(0x65);
          this.m_pGame.CreateSpeedChangeDownEnd(0x67);
          this.m_pGame.CreateStar(0x69);
          this.m_pGame.CreateStar(0x6b);
      }

      private void CreateLevel20W1()
      {
          this.m_pGround.SetSize(0x41);
          this.m_pGround.AddHoleAt(9);
          this.m_pGround.AddHoleAt(10);
          this.m_pGround.AddHoleAt(11);
          this.m_pGround.AddHoleAt(12);
          this.m_pGame.CreateStar(13);
          this.m_pGround.AddHoleAt(15);
          this.m_pGround.AddHoleAt(0x10);
          this.m_pGame.CreateObstacle(0x11, 0, 5, 6, 0);
          this.m_pGame.CreateStar(20);
          this.m_pGame.CreateObstacle(0x15, 2, 5, 6, 0);
          this.m_pGame.CreateStar(0x19);
          this.m_pGame.CreateObstacle(0x1b, 2, 5, 6, 0);
          this.m_pGame.CreateStar(30);
          this.m_pGame.CreateStar(0x20);
          this.m_pGame.CreateObstacle(0x22, 3, 6, 13, 0);
          this.m_pGame.CreateStar(40);
          this.m_pGame.CreateObstacle(0x2b, 2, 6, 13, 0);
          this.m_pGame.CreateStar(0x31);
          this.m_pGame.CreateObstacle(0x34, 2, 6, 13, 0);
          this.m_pGame.CreateStar(0x39);
          this.m_pGame.CreateStar(0x3b);
      }

      private void CreateLevel20W2()
      {
          this.m_pGround.SetSize(110);
          this.m_pGame.CreateObstacle(5, 0, 20, 0x20, 5);
          this.m_pGame.CreateStar(6);
          this.m_pGame.CreateStar(8);
          this.m_pGame.CreateStar(10);
          this.m_pGame.CreateObstacle(12, 2, 6, 8, 0);
          this.m_pGame.CreateStar(15);
          this.m_pGame.CreateObstacle(0x12, 1, 9, 9, 0);
          this.m_pGame.CreateObstacle(0x12, 3, 6, 5, 9);
          this.m_pGame.CreateSpeedChangeUpStart(0x17);
          this.m_pGround.AddHoleAt(0x1a);
          this.m_pGround.AddHoleAt(0x1c);
          this.m_pGame.CreateStar(0x1c, 2);
          this.m_pGame.CreateStar(30);
          this.m_pGame.CreateStar(0x20);
          this.m_pGame.CreateStar(0x22);
          this.m_pGame.CreateObstacle(0x1f, 1, 8, 0x20, 5);
          this.m_pGame.CreateObstacle(0x23, 3, 6, 8, 0);
          this.m_pGame.CreateStar(0x26);
          this.m_pGame.CreateStar(40);
          this.m_pGame.CreateObstacle(0x2a, 3, 9, 9, 0);
          this.m_pGame.CreateObstacle(0x2b, 2, 6, 5, 9);
          this.m_pGame.CreateSpeedChangeUpEnd(0x30);
          this.m_pGame.CreateStar(50);
          this.m_pGame.CreateObstacle(0x34, 3, 5, 4, 0);
          this.m_pGame.CreateObstacle(0x34, 1, 6, 5, 4);
          this.m_pGame.CreateObstacle(0x35, 0, 6, 4, 9);
          this.m_pGame.CreateStar(0x38);
          this.m_pGame.CreateStar(0x3a);
          this.m_pGround.AddHoleAt(0x3b);
          this.m_pGround.AddHoleAt(0x3e);
          this.m_pGame.CreateStar(0x40);
          this.m_pGame.CreateObstacle(0x42, 2, 6, 4, 0);
          this.m_pGame.CreateObstacle(0x42, 0, 5, 6, 4);
          this.m_pGame.CreateObstacle(0x42, 2, 6, 4, 10);
          this.m_pGame.CreateSpeedChangeUpStart(70);
          this.m_pGame.CreateStar(0x49);
          this.m_pGame.CreateObstacle(0x4b, 0, 5, 4, 0);
          this.m_pGame.CreateObstacle(0x4b, 1, 5, 4, 4);
          this.m_pGame.CreateForceField(0x4d, 0, 4, 0);
          this.m_pGame.CreateObstacle(0x4f, 1, 6, 0x20, 6);
          this.m_pGame.CreateStar(0x51);
          this.m_pGame.CreateStar(0x53);
          this.m_pGame.CreateObstacle(0x55, 2, 14, 8, 0);
          this.m_pGame.CreateStar(0x5b);
          this.m_pGame.CreateSpeedChangeUpEnd(0x5d);
          this.m_pGame.CreateObstacle(0x5f, 2, 5, 6, 0);
          this.m_pGame.CreateObstacle(0x61, 2, 6, 8, 0);
          this.m_pGame.CreateObstacle(100, 0, 5, 6, 0);
          this.m_pGame.CreateStar(0x66);
          this.m_pGame.CreateStar(0x68);
      }

      private void CreateLevel20W3()
      {
          this.m_pGround.SetSize(120);
          this.m_pGame.CreateStar(6);
          this.m_pGame.CreateStar(8);
          this.m_pGame.CreateExitableForceField(9, 0, 13, 12);
          this.m_pGame.CreateObstacle(10, 1, 5, 6, 0);
          this.m_pGame.CreateObstacle(10, 1, 5, 6, 6);
          this.m_pGame.CreateStar(13, 1);
          this.m_pGame.CreateStar(15);
          this.m_pGame.CreateStar(0x11, 1);
          this.m_pGame.CreateObstacle(20, 0, 5, 6, 0);
          this.m_pGame.CreateObstacle(20, 0, 5, 6, 6);
          this.m_pGround.AddHoleAt(0x16);
          this.m_pGround.AddHoleAt(0x18);
          this.m_pGame.CreateStar(0x1a);
          this.m_pGround.AddHoleAt(0x1c);
          this.m_pGame.CreateStar(0x1f, 3);
          this.m_pGame.CreateObstacle(30, 0, 6, 8, 0);
          this.m_pGame.CreateStar(0x20);
          this.m_pGame.CreateSpeedChangeDownStart(0x22);
          this.m_pGame.CreateStar(0x24);
          this.m_pGame.CreateBouncePadUp(0x27, 0, 7);
          this.m_pGame.CreateStar(0x2b, 6);
          this.m_pGame.CreateObstacle(0x2a, 0, 6, 8, 0);
          this.m_pGame.CreateObstacle(0x2a, 0, 6, 8, 8);
          this.m_pGame.CreateStar(0x2d, 3);
          this.m_pGround.AddHoleAt(0x30);
          this.m_pGame.CreateStar(50, 2);
          this.m_pGround.AddHoleAt(50);
          this.m_pGround.AddHoleAt(0x33);
          this.m_pGame.CreateSpeedChangeDownEnd(0x34);
          this.m_pGame.CreateStar(0x36);
          this.m_pGame.CreateStar(0x39, 3);
          this.m_pGame.CreateObstacle(0x38, 0, 5, 4, 0);
          this.m_pGame.CreateObstacle(0x38, 2, 5, 4, 4);
          this.m_pGame.CreateStar(0x3b);
          this.m_pGame.CreateStar(0x3f, 3);
          this.m_pGame.CreateObstacle(0x3e, 3, 5, 4, 0);
          this.m_pGame.CreateObstacle(0x3e, 1, 5, 4, 4);
          this.m_pGame.CreateStar(0x41);
          this.m_pGame.CreateStar(0x43);
          this.m_pGame.CreateStar(0x47, 4);
          this.m_pGame.CreateObstacle(70, 3, 5, 4, 0);
          this.m_pGame.CreateObstacle(70, 1, 5, 4, 4);
          this.m_pGame.CreateObstacle(70, 2, 5, 6, 8);
          this.m_pGame.CreateStar(0x4b);
          this.m_pGame.CreateSpeedChangeUpStart(0x4d);
          this.m_pGame.CreateStar(0x4f);
          this.m_pGame.CreateStar(0x52, 3);
          this.m_pGround.AddHoleAt(0x51);
          this.m_pGround.AddHoleAt(0x52);
          this.m_pGame.CreateObstacle(0x53, 0, 14, 8, 0);
          this.m_pGame.CreateObstacle(0x56, 2, 6, 8, 0);
          this.m_pGame.CreateObstacle(0x58, 2, 6, 13, 0);
          this.m_pGame.CreateBouncePadUp(0x56, 0, 8);
          this.m_pGame.CreateStar(0x59, 6);
          this.m_pGame.CreateObstacle(90, 0, 9, 9, 0);
          this.m_pGame.CreateStar(0x5d, 4);
          this.m_pGame.CreateStar(0x5e, 1);
          this.m_pGame.CreateStar(0x60);
          this.m_pGame.CreateStar(0x63, 3);
          this.m_pGame.CreateObstacle(0x62, 3, 6, 4, 0);
          this.m_pGame.CreateObstacle(0x63, 0, 6, 4, 4);
          this.m_pGame.CreateStar(0x66);
          this.m_pGame.CreateStar(0x69, 3);
          this.m_pGame.CreateObstacle(0x69, 2, 5, 4, 0);
          this.m_pGame.CreateObstacle(0x6a, 0, 5, 6, 4);
          this.m_pGame.CreateStar(0x6d);
          this.m_pGround.AddHoleAt(0x6f);
          this.m_pGame.CreateStar(0x71, 2);
          this.m_pGround.AddHoleAt(0x72);
          this.m_pGame.CreateStar(0x74);
      }

      private void CreateLevel21W1()
      {
          this.m_pGround.SetSize(0x4b);
          this.m_pGame.CreateStar(7);
          this.m_pGame.CreateStar(9);
          this.m_pGround.AddHoleAt(10);
          this.m_pGround.AddHoleAt(11);
          this.m_pGame.CreateObstacle(12, 0, 6, 5, 0);
          this.m_pGame.CreateStar(13, 4);
          this.m_pGame.CreateObstacle(14, 2, 6, 8, 0);
          this.m_pGame.CreateStar(0x11);
          this.m_pGround.AddHoleAt(0x13);
          this.m_pGround.AddHoleAt(20);
          this.m_pGround.AddHoleAt(0x15);
          this.m_pGame.CreateObstacle(0x16, 0, 8, 0x20, 0x10);
          this.m_pGame.CreateStar(0x17);
          this.m_pGround.AddHoleAt(0x18);
          this.m_pGame.CreateStar(0x1d);
          this.m_pGame.CreateObstacle(30, 3, 20, 3, 0);
          this.m_pGround.AddHoleAt(0x24);
          this.m_pGame.CreateStar(0x25);
          this.m_pGame.CreateObstacle(0x27, 1, 0x15, 2, 0);
          this.m_pGame.CreateObstacle(40, 3, 6, 8, 2);
          this.m_pGround.AddHoleAt(0x2d);
          this.m_pGame.CreateStar(0x2f);
          this.m_pGame.CreateObstacle(0x31, 0, 5, 4, 0);
          this.m_pGame.CreateStar(0x34);
          this.m_pGround.AddHoleAt(0x35);
          this.m_pGround.AddHoleAt(0x36);
          this.m_pGame.CreateObstacle(0x34, 3, 4, 0x20, 13);
          this.m_pGame.CreateStar(0x3a);
          this.m_pGround.AddHoleAt(60);
          this.m_pGame.CreateObstacle(0x3d, 3, 6, 5, 0);
          this.m_pGround.AddHoleAt(0x3f);
          this.m_pGame.CreateStar(0x40);
          this.m_pGame.CreateStar(0x42);
          this.m_pGame.CreateStar(0x44);
          this.m_pGame.CreateObstacle(0x41, 0, 8, 0x20, 12);
      }

      private void CreateLevel21W2()
      {
          this.m_pGround.SetSize(110);
          this.m_pGame.CreateStar(8);
          this.m_pGame.CreateStar(10, 3);
          this.m_pGame.CreateObstacle(10, 0, 5, 4, 0);
          this.m_pGame.CreateObstacle(9, 2, 5, 4, 4);
          this.m_pGame.CreateStar(12, 4);
          this.m_pGame.CreateObstacle(13, 2, 5, 4, 0);
          this.m_pGame.CreateObstacle(13, 1, 5, 4, 4);
          this.m_pGame.CreateStar(14, 3);
          this.m_pGame.CreateStar(0x10);
          this.m_pGame.CreateObstacle(0x13, 0, 5, 4, 0);
          this.m_pGame.CreateObstacle(0x13, 0, 5, 4, 4);
          this.m_pGround.AddHoleAt(0x17);
          this.m_pGame.CreateStar(0x19);
          this.m_pGame.CreateStar(0x1b);
          this.m_pGame.CreateObstacle(0x1d, 0, 5, 4, 0);
          this.m_pGame.CreateObstacle(0x1d, 0, 5, 4, 4);
          this.m_pGame.CreateStar(0x20);
          this.m_pGame.CreateObstacle(0x23, 1, 9, 9, 0);
          this.m_pGame.CreateStar(0x24, 4);
          this.m_pGame.CreateStar(0x27, 3);
          this.m_pGame.CreateForceField(40, 0, 5, 8);
          this.m_pGround.AddHoleAt(40);
          this.m_pGround.AddHoleAt(0x29);
          this.m_pGround.AddHoleAt(0x2a);
          this.m_pGround.AddHoleAt(0x2b);
          this.m_pGround.AddHoleAt(0x2c);
          this.m_pGame.CreateStar(0x2e);
          this.m_pGame.CreateObstacle(0x31, 3, 5, 4, 0);
          this.m_pGame.CreateObstacle(0x31, 3, 5, 6, 4);
          this.m_pGame.CreateStar(0x34);
          this.m_pGame.CreateStar(0x3a, 4);
          this.m_pGame.CreateObstacle(0x39, 0, 5, 4, 0);
          this.m_pGame.CreateObstacle(0x39, 0, 5, 6, 4);
          this.m_pGame.CreateObstacle(0x39, 0, 5, 4, 10);
          this.m_pGame.CreateStar(0x3d);
          this.m_pGame.CreateStar(0x3f);
          this.m_pGame.CreateForceField(0x45, 2, 3, 0x10);
          this.m_pGame.CreateObstacle(0x42, 3, 5, 6, 0);
          this.m_pGame.CreateObstacle(0x42, 3, 5, 6, 6);
          this.m_pGame.CreateObstacle(0x42, 3, 5, 4, 12);
          this.m_pGame.CreateObstacle(0x45, 0, 9, 9, 0);
          this.m_pGame.CreateStar(0x4b);
          this.m_pGame.CreateStar(0x4d);
          this.m_pGround.AddHoleAt(0x4f);
          this.m_pGround.AddHoleAt(80);
          this.m_pGame.CreateStar(0x52);
          this.m_pGround.AddHoleAt(0x54);
          this.m_pGround.AddHoleAt(0x55);
          this.m_pGround.AddHoleAt(0x56);
          this.m_pGame.CreateStar(0x58);
          this.m_pGame.CreateObstacle(0x5b, 3, 5, 4, 0);
          this.m_pGame.CreateObstacle(0x5b, 1, 5, 6, 4);
          this.m_pGame.CreateObstacle(0x5d, 1, 6, 13, 0);
          this.m_pGame.CreateStar(0x61);
          this.m_pGame.CreateStar(0x63);
          this.m_pGame.CreateStar(0x65);
      }

      private void CreateLevel21W3()
      {
          this.m_pGround.SetSize(140);
          this.m_pGame.CreateStar(7);
          this.m_pGame.CreateStar(9);
          this.m_pGround.AddHoleAt(10);
          this.m_pGround.AddHoleAt(11);
          this.m_pGame.CreateStar(12, 2);
          this.m_pGame.CreateObstacle(12, 2, 5, 6, 0);
          this.m_pGame.CreateStar(15);
          this.m_pGame.CreateStar(0x11);
          this.m_pGame.CreateStar(0x13);
          this.m_pGame.CreateStar(0x16, 4);
          this.m_pGame.CreateObstacle(0x11, 2, 6, 0x20, 6);
          this.m_pGame.CreateObstacle(20, 2, 20, 3, 0);
          this.m_pGame.CreateObstacle(0x15, 2, 6, 8, 3);
          this.m_pGame.CreateStar(0x1b);
          this.m_pGame.CreateStar(0x1d);
          this.m_pGame.CreateObstacle(0x1b, 0, 8, 0x20, 10);
          this.m_pGame.CreateForceField(30, 2, 5, 13);
          this.m_pGame.CreateObstacle(0x20, 0, 6, 13, 0);
          this.m_pGame.CreateObstacle(0x21, 2, 6, 13, 0);
          this.m_pGame.CreateStar(0x24, 2);
          this.m_pGame.CreateSpeedChangeUpStart(0x26);
          this.m_pGame.CreateStar(40);
          this.m_pGame.CreateStar(0x2b, 3);
          this.m_pGame.CreateObstacle(0x2a, 2, 5, 6, 0);
          this.m_pGame.CreateStar(0x2e);
          this.m_pGame.CreateStar(50, 3);
          this.m_pGame.CreateObstacle(0x31, 2, 5, 6, 0);
          this.m_pGame.CreateStar(0x35);
          this.m_pGame.CreateStar(0x37);
          this.m_pGame.CreateStar(0x39, 3);
          this.m_pGame.CreateBouncePadUp(0x3a, 2, 7);
          this.m_pGame.CreateStar(0x3f, 6);
          this.m_pGame.CreateObstacle(0x3d, 2, 5, 12, 0);
          this.m_pGame.CreateObstacle(0x3e, 0, 5, 6, 12);
          this.m_pGame.CreateStar(0x40, 3);
          this.m_pGame.CreateSpeedChangeUpEnd(0x42);
          this.m_pGame.CreateStar(70, 3);
          this.m_pGame.CreateObstacle(0x45, 0, 5, 4, 0);
          this.m_pGame.CreateObstacle(0x45, 1, 5, 4, 4);
          this.m_pGame.CreateStar(0x49);
          this.m_pGame.CreateStar(0x4b);
          this.m_pGame.CreateStar(0x4e, 4);
          this.m_pGame.CreateBouncePadUp(80, 2, 6);
          this.m_pGame.CreateStar(0x4e, 4);
          this.m_pGame.CreateObstacle(0x4d, 0, 0x15, 2, 0);
          this.m_pGame.CreateObstacle(0x4d, 0, 0x15, 2, 2);
          this.m_pGame.CreateObstacle(0x4d, 0, 0x15, 2, 4);
          this.m_pGame.CreateObstacle(0x54, 0, 6, 13, 0);
          this.m_pGame.CreateStar(0x57, 4);
          this.m_pGame.CreateStar(0x58, 2);
          this.m_pGame.CreateStar(0x59);
          this.m_pGame.CreateStar(0x5c, 3);
          this.m_pGame.CreateObstacle(0x5c, 0, 6, 5, 0);
          this.m_pGame.CreateObstacle(0x5c, 2, 5, 4, 5);
          this.m_pGame.CreateStar(0x5f);
          this.m_pGame.CreateSpeedChangeDownStart(0x61);
          this.m_pGame.CreateStar(0x63);
          this.m_pGame.CreateBouncePadUp(0x66, 2, 7);
          this.m_pGame.CreateStar(0x6b, 6);
          this.m_pGame.CreateObstacle(0x69, 1, 11, 13, 0);
          this.m_pGame.CreateObstacle(0x69, 3, 6, 5, 13);
          this.m_pGame.CreateSpeedChangeDownEnd(0x6f);
          this.m_pGame.CreateSpeedChangeUpStart(0x72);
          this.m_pGame.CreateStar(0x74);
          this.m_pGround.AddHoleAt(0x76);
          this.m_pGround.AddHoleAt(120);
          this.m_pGame.CreateStar(0x7a);
          this.m_pGround.AddHoleAt(0x7c);
          this.m_pGame.CreateStar(0x80, 4);
          this.m_pGame.CreateObstacle(0x7e, 3, 11, 13, 0);
          this.m_pGame.CreateStar(0x84);
          this.m_pGame.CreateStar(0x86);
      }
  
}
