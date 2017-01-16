package org.test;

import loon.Session;
import loon.Stage;

public class SessionTest extends Stage {

	@Override
	public void create() {
		Session session = Session.load("session_test");
		int count = session.getInt("count", 0);
		addLabel("你是第" + count + "次访问此Screen", 66, 66);
		session.set("count", count += 1);
		session.save();
		add(MultiScreenTest.getBackButton(this,0));
	
	}
	
}
