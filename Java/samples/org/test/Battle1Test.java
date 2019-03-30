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
package org.test;

import loon.Stage;
import loon.utils.TArray;
import loon.utils.reply.Port;
import loon.utils.timer.LTimerContext;

public class Battle1Test extends Stage{
/*
	class Hero {
		String name = "Akari";
		int level = 0;
		int healthMax = 100;
		int armor = 0;
		int armorMod = 5;
		int strength = 15;
		int strengthMod = 0;
		int staminaMax = 20;
		int exp = 0;
		int nextLv = 25;
		int enemyDefeated = 0;
		int health = 100;
		int stamina = 20;
	}

	TArray<Hero> area;
	Hero hero;
	public boolean deadCheck() {
		  if (hero.health <= 0) fadeDeath();
		}

	
	 public void move(String moveName,int strength,int sCost,int sCharge,int heal,int armorBoost, boolean shake,int soundId) {
	   
	      if (hero.health >= 1 && (sCost <= hero.stamina)) {
	        // check to see if the move creates a shake effect
	        if (shake === true) {enemyShake(hero.stamina, sCost);}
	        //modify stats of player and enemy
	        area.get(0).health -= strength;
	        hero.armor += armorBoost;
	        hero.stamina -= sCost;
	        hero.stamina += sCharge;
	        hero.health += heal;
	        //before rendering check to see if health and stamina are above max values, and fix
	        if (hero.health > hero.healthMax) {hero.health = hero.healthMax}
	        if (hero.stamina > hero.staminaMax) {hero.stamina = hero.staminaMax;}
	        //render sounds, stats, trigger fades, and start enemy atack
	        playMoveSound(soundId);
	        renderHealth();
	        renderSkill(moveName);
	        renderStamina();
	        spellFadein();
	        area[0].attack(hero);
	     
	        add(new Port<LTimerContext>() {
				
				@Override
				public void onEmit(LTimerContext event) {
					hero.armor -= armorBoost;
				}
			});
	      }
	    }
	  }*/

	@Override
	public void create() {
		// TODO Auto-generated method stub
		
	}
	
}
