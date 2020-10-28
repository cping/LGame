namespace loon.utils
{
    public class MessageQueue
    {
        public class Message
        {
            public string text;

            public int ticksRemaining;

            public Message(string text)
            {
                this.text = text;
                ticksRemaining = 100;
            }
        }

        private Array<Message> messages;
        private readonly int maxSize;

        public MessageQueue(int size)
        {
            maxSize = size;
            messages = new Array<Message>();
        }

        public Array<Message> GetArrays()
        {
            return messages.Cpy();
        }

        public void Tick()
        {
            Message mes = messages.Next();
            mes.ticksRemaining--;
            if (mes.ticksRemaining <= 0)
            {
                messages.Remove(mes);
            }
        }

        public int GetMaxSize()
        {
            return maxSize;
        }

        public int Size()
        {
            return messages.Size();
        }

        public string Get(int i)
        {
            if (i < Size())
            {
                return messages.Get(i).text;
            }
            else
            {
                return null;
            }
        }

        public void Add(string text)
        {
            messages.Add(new Message(text));
            for (; messages.Size() > maxSize;)
            {
                messages.Remove(0);
            }
        }
    }
}
