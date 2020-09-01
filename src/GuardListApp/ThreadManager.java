package GuardListApp;

import java.util.ArrayList;

public class ThreadManager {

	ArrayList<Thread> runningThreads;

	public ThreadManager(ArrayList<Thread> runningThreads) {
		this.runningThreads = runningThreads;
	}
	
	public Thread startThread(Object classObject) {
		Thread chatThread = new Thread((Runnable) classObject);
		chatThread.start();
		return chatThread;
	}
}
