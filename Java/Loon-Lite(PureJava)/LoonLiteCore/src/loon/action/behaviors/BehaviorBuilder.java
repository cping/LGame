/**
 * Copyright 2008 - 2020 The Loon Game Engine Authors
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
package loon.action.behaviors;

import loon.LRelease;
import loon.LSysException;
import loon.Screen;
import loon.utils.Stack;
import loon.utils.processes.GameProcessType;
import loon.utils.processes.RealtimeProcess;
import loon.utils.processes.RealtimeProcessManager;
import loon.utils.reply.Function;
import loon.utils.timer.LTimerContext;

/**
 * 此类为人工智能实现提供基础分支树选择器(本身不具备游戏逻辑,需要具体实现)
 */
public class BehaviorBuilder<T> implements IBaseAction, LRelease {

	private static class BehaviorProcess<T> extends RealtimeProcess {

		private BehaviorBuilder<T> builder = null;

		public BehaviorProcess(BehaviorBuilder<T> b) {
			this.builder = b;
			this.setProcessType(GameProcessType.Behavior);
		}

		@Override
		public void run(LTimerContext time) {
			if (builder != null) {
				builder.tick();
				if (builder._closed) {
					kill();
				}
			}
		}

	}

	protected T _context;

	protected Behavior<T> _currentNode;

	protected BehaviorAction _currentBehaviorAction;

	protected final Stack<Behavior<T>> _parentNodeStack = new Stack<Behavior<T>>();

	private BehaviorProcess<T> _process;

	private boolean _closed;

	public static <T> BehaviorBuilder<T> begin(T context) {
		return new BehaviorBuilder<T>(context);
	}

	public static BehaviorBuilder<Screen> begin(Screen context) {
		if (context == null) {
			return new BehaviorBuilder<Screen>(null);
		}
		BehaviorBuilder<Screen> builder = new BehaviorBuilder<Screen>(context);
		context.putRelease(builder);
		return builder;
	}

	protected void checkNodeStack(String msg) {
		if (_parentNodeStack.size() == 0) {
			throw new LSysException(msg);
		}
	}

	public BehaviorBuilder(T context) {
		_context = context;
	}

	public BehaviorBuilder<T> submit() {
		synchronized (RealtimeProcessManager.class) {
			if (_process != null) {
				RealtimeProcessManager.get().delete(_process);
			}
			if (_process == null || _process.isDead()) {
				_process = new BehaviorProcess<T>(this);
			}
			_process.setDelay(0);
			RealtimeProcessManager.get().addProcess(_process);
		}
		return this;
	}

	public BehaviorTree<T> tree() {
		return tree(0.2f);
	}

	public BehaviorTree<T> tree(float updatePeriod) {
		if (_currentNode == null) {
			throw new LSysException("Can't create a behaviour tree with zero nodes");
		}
		return new BehaviorTree<T>(_context, _currentNode, updatePeriod);
	}

	public BehaviorBuilder<T> tick() {
		if (_currentBehaviorAction != null) {
			_currentBehaviorAction.tick(_context);
		}
		for (; _parentNodeStack.hashNext();) {
			Behavior<T> node = _parentNodeStack.next();
			if (node != null) {
				node.tick(_context);
			}
		}
		_parentNodeStack.stopNext();
		return this;
	}

	protected BehaviorBuilder<T> setChildOnParent(Behavior<T> child) {
		Behavior<T> parent = _parentNodeStack.peek();
		if (parent != null) {
			if (parent instanceof Composite) {
				((Composite<T>) parent).addChild(child);
			} else if (parent instanceof Decorator) {
				((Decorator<T>) parent).child = child;
				popParentNode();
			}
		}
		return this;
	}

	protected BehaviorBuilder<T> pushParentNode(Behavior<T> composite) {
		if (_parentNodeStack.size() > 0) {
			setChildOnParent(composite);
		}
		_parentNodeStack.push(composite);
		return this;
	}

	public BehaviorBuilder<T> logAction(String text) {
		checkNodeStack("Can't create an unnested LogAction node. It must be a leaf node.");
		return setChildOnParent(new LogAction<T>(text));
	}

	public BehaviorBuilder<T> waitAction(float waitTime) {
		checkNodeStack("Can't create an unnested WaitAction node. It must be a leaf node.");
		return setChildOnParent(new WaitAction<T>(waitTime));
	}

	public BehaviorBuilder<T> subTree(BehaviorTree<T> subTree) {
		checkNodeStack("Can't splice an unnested sub tree, there must be a parent tree.");
		return setChildOnParent(new BehaviorTreeReference<T>(subTree));
	}

	public BehaviorAction doAction() {
		return (this._currentBehaviorAction = new BehaviorAction(this));
	}

	public BehaviorBuilder<T> action(Function<T, TaskStatus> func) {
		return setChildOnParent(new ExecuteAction<T>(func));
	}

	public BehaviorBuilder<T> action(TaskFunc<T> func) {
		return setChildOnParent(new ExecuteAction<T>(func));
	}

	public BehaviorBuilder<T> conditional(Function<T, TaskStatus> func) {
		return setChildOnParent(new ExecuteActionConditional<T>(func));
	}

	public BehaviorBuilder<T> conditional(TaskFunc<T> func) {
		return setChildOnParent(new ExecuteActionConditional<T>(func));
	}

	public BehaviorBuilder<T> conditionalDecorator(Function<T, TaskStatus> func) {
		return conditionalDecorator(func, true);
	}

	public BehaviorBuilder<T> conditionalDecorator(TaskFunc<T> func) {
		return conditionalDecorator(func, true);
	}

	public BehaviorBuilder<T> conditionalDecorator(Function<T, TaskStatus> func, boolean shouldReevaluate) {
		ExecuteActionConditional<T> conditional = new ExecuteActionConditional<T>(func);
		return pushParentNode(new DecoratorConditional<T>(conditional, shouldReevaluate));
	}

	public BehaviorBuilder<T> conditionalDecorator(TaskFunc<T> func, boolean shouldReevaluate) {
		ExecuteActionConditional<T> conditional = new ExecuteActionConditional<T>(func);
		return pushParentNode(new DecoratorConditional<T>(conditional, shouldReevaluate));
	}

	public BehaviorBuilder<T> alwaysFail() {
		return pushParentNode(new AlwaysFail<T>());
	}

	public BehaviorBuilder<T> alwaysSucceed() {
		return pushParentNode(new AlwaysSucceed<T>());
	}

	public BehaviorBuilder<T> inverter() {
		return pushParentNode(new Inverter<T>());
	}

	public BehaviorBuilder<T> repeater(int count) {
		return pushParentNode(new Repeater<T>(count));
	}

	public BehaviorBuilder<T> untilFail() {
		return pushParentNode(new UntilFail<T>());
	}

	public BehaviorBuilder<T> untilSuccess() {
		return pushParentNode(new UntilSuccess<T>());
	}

	public BehaviorBuilder<T> parallel() {
		return pushParentNode(new Parallel<T>());
	}

	public BehaviorBuilder<T> parallelSelector() {
		return pushParentNode(new ParallelSelector<T>());
	}

	public BehaviorBuilder<T> selector() {
		return selector(AbortTypes.None);
	}

	public BehaviorBuilder<T> selector(AbortTypes abortType) {
		return pushParentNode(new Selector<T>(abortType));
	}

	public BehaviorBuilder<T> randomSelector() {
		return pushParentNode(new RandomSelector<T>());
	}

	public BehaviorBuilder<T> sequence() {
		return sequence(AbortTypes.None);
	}

	public BehaviorBuilder<T> sequence(AbortTypes abortType) {
		return pushParentNode(new Sequence<T>(abortType));
	}

	public BehaviorBuilder<T> randomSequence() {
		return pushParentNode(new RandomSequence<T>());
	}

	public BehaviorBuilder<T> popParentNode() {
		_currentNode = _parentNodeStack.pop();
		return this;
	}

	public BehaviorBuilder<T> end() {
		if (_currentBehaviorAction != null) {
			_currentBehaviorAction.close();
		}
		for (; _parentNodeStack.hashNext();) {
			Behavior<T> node = _parentNodeStack.next();
			if (node != null) {
				node.close();
			}
		}
		_parentNodeStack.clear();
		_closed = true;
		return this;
	}

	@Override
	public IBaseAction getBaseAction() {
		return (_currentNode == null) ? this._currentNode : this._currentBehaviorAction;
	}

	public boolean isClosed() {
		return _closed;
	}

	@Override
	public void close() {
		end();
	}

}
