namespace Loon.Action {
	
	public interface ActionListener {

        void Start(Event o);

        void Process(Event o);

        void Stop(Event o);
	
	}
}
