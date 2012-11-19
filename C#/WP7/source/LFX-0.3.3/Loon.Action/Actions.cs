namespace Loon.Action {

    using Loon.Utils.Collection;
    using Loon.Utils;
    using System.Runtime.CompilerServices;
    using System.Collections.Generic;

	public class Actions {
	
		private readonly ArrayMap actions;
	
		internal Actions() {
			this.actions = new ArrayMap(CollectionUtils.INITIAL_CAPACITY);
		}
	
		[MethodImpl(MethodImplOptions.Synchronized)]
		public void Clear() {
            actions.Clear();
		}

        [MethodImpl(MethodImplOptions.Synchronized)]
        public void AddAction(ActionEvent action, Event actObject,
                bool paused)
        {
            Actions.ActionElement element = (Actions.ActionElement)actions.Get(actObject);
            if (element == null)
            {
                element = new Actions.ActionElement(actObject, paused);
                actions.Put(actObject, element);
            }
            CollectionUtils.Add(element.actions, action);
            action.Start(actObject);
        }
	
		[MethodImpl(MethodImplOptions.Synchronized)]
		private void DeleteElement(Actions.ActionElement  element) {
            CollectionUtils.Clear(element.actions);
            actions.Remove(element.key);
		}
	
		[MethodImpl(MethodImplOptions.Synchronized)]
        public void RemoveAllActions(Event actObject)
        {
            if (actObject == null)
            {
                return;
            }
            Actions.ActionElement element = (Actions.ActionElement)actions.Get(actObject);
            if (element != null)
            {
                CollectionUtils.Clear(element.actions);
                DeleteElement(element);
            }
		}
	
		[MethodImpl(MethodImplOptions.Synchronized)]
		private void RemoveAction(int index, Actions.ActionElement element) {
            CollectionUtils.RemoveAt(element.actions, index);
            if (element.actionIndex >= index)
            {
                element.actionIndex--;
            }
            if ((element.actions.Count == 0))
            {
                DeleteElement(element);
            }
		}
	
		[MethodImpl(MethodImplOptions.Synchronized)]
		public int GetCount() {
			return actions.Size();
		}
	
		[MethodImpl(MethodImplOptions.Synchronized)]
		public void RemoveAction(object tag, Event actObject) {
			Actions.ActionElement  element = (Actions.ActionElement ) actions.Get(actObject);
			if (element != null) {
				if (element.actions != null) {
					int limit = element.actions.Count;
					for (int i = 0; i < limit; i++) {
						ActionEvent a = (ActionEvent) element.actions[i];
						if (a.GetTag() == tag && a.GetOriginal() == actObject) {
							RemoveAction(i, element);
						}
					}
				}
			}
		}
	
		[MethodImpl(MethodImplOptions.Synchronized)]
		public void RemoveAction(ActionEvent action) {
			if (action == null) {
				return;
			}
			Actions.ActionElement  element = (Actions.ActionElement ) actions.Get(action
					.GetOriginal());
			if (element != null) {
				int i = element.actions.IndexOf(action);
				if (i != -1) {
					RemoveAction(i, element);
				}
			}
		}
	
		[MethodImpl(MethodImplOptions.Synchronized)]
        public ActionEvent GetAction(object tag, Event actObject)
        {
			Actions.ActionElement  element = (Actions.ActionElement ) actions.Get(actObject);
			if (element != null) {
				if (element.actions != null) {
					int limit = element.actions.Count;
					for (int i = 0; i < limit; i++) {
						ActionEvent a = (ActionEvent) element.actions[i];
						if (a.GetTag() == tag)
							return a;
					}
				}
			}
			return null;
		}
	
		[MethodImpl(MethodImplOptions.Synchronized)]
		public void Update(long elapsedTime) {
			int Size = actions.Size();
			for (int i = Size - 1; i > -1; --i) {
				Actions.ActionElement  currentTarget = (Actions.ActionElement ) actions.Get(i);
				if (currentTarget == null) {
					continue;
				}
				 lock (currentTarget) {
								if (!currentTarget.paused) {
									for (currentTarget.actionIndex = 0; currentTarget.actionIndex < currentTarget.actions
											.Count; currentTarget.actionIndex++) {
										currentTarget.currentAction = (ActionEvent) currentTarget.actions
												[currentTarget.actionIndex];
										if (currentTarget.currentAction == null) {
											continue;
										}
										if (!currentTarget.currentAction.isInit) {
											currentTarget.currentAction.isInit = true;
											currentTarget.currentAction.OnLoad();
										}
										currentTarget.currentAction.Step(elapsedTime);
										if (currentTarget.currentAction.IsComplete()) {
											currentTarget.currentAction.Stop();
											RemoveAction(currentTarget.currentAction);
										}
										currentTarget.currentAction = null;
									}
								}
                                if (currentTarget.actions.Count == 0)
                                {
									DeleteElement(currentTarget);
								}
							}
			}
		}
	
		[MethodImpl(MethodImplOptions.Synchronized)]
        public void Paused(bool pause, Event actObject)
        {
			Actions.ActionElement  element = (Actions.ActionElement ) actions.Get(actObject);
			if (element != null) {
				element.paused = pause;
			}
		}
	
		[MethodImpl(MethodImplOptions.Synchronized)]
        public void Stop(Event actObject)
        {
			Actions.ActionElement  element = (Actions.ActionElement ) actions.Get(actObject);
			if (element != null) {
				element.paused = true;
			}
		}
	
		[MethodImpl(MethodImplOptions.Synchronized)]
        public void Start(Event actObject)
        {
			Actions.ActionElement  element = (Actions.ActionElement ) actions.Get(actObject);
			if (element != null) {
				element.paused = false;
			}
		}
	
		[MethodImpl(MethodImplOptions.Synchronized)]
		public void Start() {
			for (int i = 0; i < actions.Size(); i++) {
				((Actions.ActionElement ) actions.Get(i)).paused = false;
			}
		}
	
		[MethodImpl(MethodImplOptions.Synchronized)]
		public void Stop() {
			for (int i = 0; i < actions.Size(); i++) {
				((Actions.ActionElement ) actions.Get(i)).paused = true;
			}
		}
	
		internal sealed class ActionElement {

            internal Event key;

             internal int actionIndex;

             internal bool paused;

             internal List<ActionEvent> actions;

             internal ActionEvent currentAction;

             public ActionElement(Event k, bool v)
             {
                this.actions = new List<ActionEvent>(
						CollectionUtils.INITIAL_CAPACITY);
				this.key = k;
				this.paused = v;
			}
		}
	}
}
