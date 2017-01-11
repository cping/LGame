package loon.utils;

public class MessageQueue {

	private static class Message {
		String text;
		int ticksRemaining;

		public Message(String text) {
			this.text = text;
			ticksRemaining = 100;
		}
	}

	private Array<Message> messages;
	private final int maxSize;

	public MessageQueue(int size) {
		maxSize = size;
		messages = new Array<Message>();
	}

	public Array<Message> getArrays() {
		return messages.cpy();
	}

	public void tick() {
		Message mes = messages.next();
		mes.ticksRemaining--;
		if (mes.ticksRemaining <= 0) {
			messages.remove(mes);
		}
	}

	public int getMaxSize() {
		return maxSize;
	}

	public int size() {
		return messages.size();
	}

	public String get(int i) {
		if (i < size()) {
			return messages.get(i).text;
		} else {
			return null;
		}
	}

	public void add(String text) {
		messages.add(new Message(text));
		for (; messages.size() > maxSize;) {
			messages.remove(0);
		}
	}
}
