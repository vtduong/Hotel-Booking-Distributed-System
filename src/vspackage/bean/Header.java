package vspackage.bean;

public class Header {
	
	private int PROTOCOL_TYPE;
	private String userID;
	private String fromServer;
	private String toServer;
	private String eventID;
	private String eventType;
	private int capacity;
	private String newEventType;
	private String newEventID;
	private int sequenceId;
	
	
	public Header(int PROTOCOL_TYPE, String userID, String fromServer, String toServer, String oldEventID,
			String oldEventType, String newEventID, String newEventType, int capacity) {
		super();
		this.PROTOCOL_TYPE = PROTOCOL_TYPE;
		this.userID = userID;
		this.fromServer = fromServer;
		this.toServer = toServer;
		this.eventID = oldEventID;
		this.eventType = oldEventType;
		this.newEventID = newEventID;
		this.newEventType = newEventType;
		this.capacity = capacity;
	}
	public Header(int PROTOCOL_TYPE, String userID, String fromServer, String toServer, String eventID,
			String eventType, int capacity) {
		this(PROTOCOL_TYPE, userID, fromServer, toServer, eventID, eventType, null, null, capacity);
	}

	public Header() {
		// TODO Auto-generated constructor stub
	}

	public String getNewEventType() {
		return newEventType;
	}

	public void setNewEventType(String newEventType) {
		this.newEventType = newEventType;
	}

	public String getNewEventID() {
		return newEventID;
	}

	public void setNewEventID(String newEventID) {
		this.newEventID = newEventID;
	}

	/**
	 * @param pROTOCOL_TYPE the pROTOCOL_TYPE to set
	 */
	public void setProtocol(int PROTOCOL_TYPE) {
		this.PROTOCOL_TYPE = PROTOCOL_TYPE;
	}
	
	/**
	 * @param userID the userID to set
	 */
	public void setUserID(String userID) {
		this.userID = userID;
	}
	
	/**
	 * @param fromServer the fromServer to set
	 */
	public void setFromServer(String fromServer) {
		this.fromServer = fromServer;
	}
	
	/**
	 * @param toServer the toServer to set
	 */
	public void setToServer(String toServer) {
		this.toServer = toServer;
	}

	public void setEventID(String eventID) {
		this.eventID = eventID;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	/**
	 * @return the pROTOCOL_TYPE
	 */
	public int getProtocol() {
		return PROTOCOL_TYPE;
	}

	/**
	 * @return the userID
	 */
	public String getUserID() {
		return userID;
	}

	/**
	 * @return the fromServer
	 */
	public String getFromServer() {
		return fromServer;
	}

	/**
	 * @return the toServer
	 */
	public String getToServer() {
		return toServer;
	}

	/**
	 * @return the eventID
	 */
	public String getEventID() {
		return eventID;
	}

	/**
	 * @return the eventType
	 */
	public String getEventType() {
		return eventType;
	}

	/**
	 * @return the capacity
	 */
	public int getCapacity() {
		return capacity;
	}
	public int getSequenceId() {
		return sequenceId;
	}
	public void setSequenceId(int sequenceId) {
		this.sequenceId = sequenceId;
	}
	
	
	
}
