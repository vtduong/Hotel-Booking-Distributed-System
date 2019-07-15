package frontend;

import java.util.concurrent.SynchronousQueue;

public class FrontEnd {
	
	
	public String addEvent (String eventID, String eventType, int bookingCapacity) {
		SynchronousQueue queue = new SynchronousQueue();
		//TODO
		return null;
	}


	public String removeEvent (String eventID, String eventType) {
		SynchronousQueue queue = new SynchronousQueue();
		//TODO
		return null;
	}


	public String listEventAvailability (String eventType) {
		SynchronousQueue queue = new SynchronousQueue();
		//TODO
		return null;
	}


	public String bookEvent (String customerID, String eventID, String eventType) {
		SynchronousQueue queue = new SynchronousQueue();
		//TODO
		return null;
	}


	public String getBookingSchedule (String customerID) {
		SynchronousQueue queue = new SynchronousQueue();
		//TODO
		return null;
	}


	public String cancelEvent (String customerID, String eventID, String eventType) {
		SynchronousQueue queue = new SynchronousQueue();
		//TODO
		return null;
	}


	public String swapEvent (String customerID, String newEventID, String newEventType, String oldEventID, String oldEventType) {
		SynchronousQueue queue = new SynchronousQueue();
		//TODO
		return null;
	}


}


class ReceiveFromHost implements Runnable {
	
	private SynchronousQueue queue = null;
	
	public ReceiveFromHost(SynchronousQueue queue) {
		this.queue = queue;
	}
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		// store the results in the queue
	}

}


class SendToSequencer {

	public SendToSequencer() {
		//TODO 
	}

	public String send() {

		//TODO
		//return ack
		return null;
	}
}
