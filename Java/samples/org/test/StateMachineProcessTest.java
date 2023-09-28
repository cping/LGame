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
import loon.component.LClickButton;
import loon.utils.processes.state.StateMachineProcess;

public class StateMachineProcessTest extends Stage {

	@Override
	public void create() {

		//构建一个状态机进程,Root进程为main
		StateMachineProcess<Stage> process = new StateMachineProcess<Stage>(this).create("main", build -> {
			   //设定main初始状态进入时触发事件
				build.enter(state -> {
					//获得当前舞台对象
					Stage stage = state.getTagret();
					System.out.println("test main in " + stage.getName());
				//设定循环时触发事件	
				}).update((state, e) -> {
					System.out.println(e);
				//分支判断时触发事件(为false不执行),跳转去状态second	
				}).condition((state, ref) -> {
					state.getParent().changeState("second");
				}, true)
				//离开main状态时触发进程
				.exit(state -> {
					System.out.println("exit main state");
				})
			//结束main状态设定
			.end()
			//构建second状态
			.state("second")
			//初始进入state
			.enter(state -> {
					System.out.println("enter second state");
				//分支判断断言触发(false不执行),跳转去third	
				}).condition((state, ref) -> {
					state.getParent().changeState("third");
				//离开second状态	
				}, true).exit(state -> {
					System.out.println("exit second state");
				})
			//结束second构建
			.end()
			//同上
			.state("third").enter(state -> {
				System.out.println("enter third state");
			}).
			end();
		});
		addProcess(process);

		LClickButton btn1 = LClickButton.make("main", 66, 66, 100, 30);
		btn1.up((x, y) -> {
			//执行main状态
			process.play("main");
		});
		add(btn1);
		LClickButton btn2 = LClickButton.make("second", 66, 126, 100, 30);
		btn2.up((x, y) -> {
			process.play("second");
		});
		add(btn2);
		LClickButton btn3 = LClickButton.make("third", 66, 186, 100, 30);
		btn3.up((x, y) -> {
			process.play("third");
		});
		add(btn3);
	}

}
