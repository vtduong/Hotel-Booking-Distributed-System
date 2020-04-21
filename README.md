# Software Failure Tolerant and/or Highly Available Distributed Event Management System

## Project Description

CONCORDIA UNIVERSITY
DEPARTMENT OF
COMPUTER SCIENCE AND SOFTWARE ENGINEERING
COMP 6231, Summer 2019 Instructor: Sukhjinder K. Narula

Consider three branches in different cities: Toronto (TOR), Montreal (MTL) and Ottawa
(OTW) for your implementation. The users of the system are event managers and event
booking customers. Event Managers and customers are identified by a unique managerID
and customerID respectively, which is constructed from the acronym of their branch’s city
and a 4 -digit number (e.g. TORM2345 for an event manager and TORC2345 for a
customer) . Whenever the user performs an operation, the system must identify the server
that the user belongs to by looking at the ID prefix (i.e.TORM or TORC) and perform the
operation on that server. The user should also maintain a log (text file) of the actions they
performed on the system and the response from the system when available. For example, if
you have 10 users using your system, you should have a folder containing 10 logs.
In this DEMS, there are different event managers for 3 different servers. They create slots
of events corresponding to event type as per the availability. There are three event types
for which event slots can be created, Conferences, Trade shows and Seminars. A customer
can book an event in any branch of the company, for any event type, if the event is
available for booking (if the events slots corresponding to an event on a particular date is
not yet full). A server (which receives the request) maintains a booking-count for every
customer. A customer can not book more than one event with the same event id and same
event type. There are three time slots available for each event type on a day- Morning(M),
Afternoon(A) and Evening(E). An event id is a combination of city, time slot and event date
(e.g. TORM100519 for a morning event on 10th May 2019 in Toronto, OTWA100519 for an
afternoon event on 10th May 2019 in Ottawa and MTLE100519 for an evening event on 10th
May 2019 in Montreal). You should ensure that if the availability of an event is full, more
customers cannot book the event. Also, a customer can book as many events in his/her own
city, but only at most 3 events from other cities overall in a month.
The EventRecords are maintained in a HashMap as shown in Figure 1. Here event type is
the key, while the value is again a sub-HashMap. The key for sub-HashMap is the eventID,
while the value of the sub-HashMap is the information about the event.

Each server also maintains a log file containing the history of all the operations that have
been performed on that server. This should be an external text file (one per server) and
shall provide as much information as possible about what operations are performed, at
what time and who performed the operation. These are some details that a single log file
record must contain:
  * Date and time the request was sent.
  * Request type (book an event, cancel an event, etc.).
  * Request parameters (clientID, eventID, etc.).
  * Request successfully completed/failed.
  * Server response for the particular request.

Event Manager Role:\
The operations that can be performed by an event manager are the following:
  * addEvent (eventID, eventType, bookingCapacity):When an event manager invokes this method through the server associated with this
event manager (determined by the unique eventManagerID prefix), attempts to add an
event with the information passed, and inserts the record at the appropriate location in
the hash map. The server returns information to the event manager whether the
operation was successful or not and both the server and the client store this information
in their logs. If an event already exists for same event type, the event manager can’t add
it again for the same event type but the new bookingCapacity is updated. If an event does
not exist in the database for that event type, then add it. Log the information into the
event manager log file.
  * removeEvent (eventID, eventType):
When invoked by an event manager, the server associated with that event manager
(determined by the unique eventManagerID) searches in the hash map to find and
delete the event for the indicated eventType and eventID. Upon success or failure it
returns a message to the event manager and the logs are updated with this information.
If an event does not exist, then obviously there is no deletion performed. Just in case
that, if an event exists and a client has booked that event, then, delete the event and
take the necessary actions. Log the information into the log file.
• listEventAvailability (eventType):
When an event manager invokes this method from his/her branch’s city through the
associated server, that branch’s city server concurrently finds out the number of spaces
available for each event in all the servers, for only the given eventType. This requires
inter server communication that will be done using UDP/IP sockets and result will be
returned to the client. Eg: Seminars - MTLE130519 3, OTWA060519 6, TORM180519 0,
MTLE190519 2.
Customer Role:
The operations that can be performed by a customer are the following:
  * bookEvent (customerID, eventID, eventType):
When a customer invokes this method from his/her city through the server associated
with this customer (determined by the unique customerID prefix) attempts to book the
event for the customer and change the capacity left in that event. Also if the booking
was successful or not, an appropriate message is displayed to the customer and both the
server and the client stores this information in their logs.
  * getBookingSchedule (customerID):
When a customer invokes this method from his/her city’s branch through the server
associated with this customer, that city’s branch server gets all the events booked by the
customer and display them on the console. Here, bookings from all the cities, Ottawa,
Montreal and Toronto, should be displayed.
  * cancelEvent (customerID, eventID):
When a customer invokes this method from his/her city’s branch through the server
associated with this customer (determined by the unique customerID prefix) searches
the hash map to find the eventID and remove the event. Upon success or failure it
returns a message to the customer and the logs are updated with this information. It is
required to check that the event can only be removed if it was booked by the same
customer who sends cancel request.
Thus, this application has a number of servers (one per city) each implementing the above
operations for that branch, CustomerClient invoking the customer's operations at the
associated server as necessary and EventManagerClient invoking the event manager's
operations at the associated server. When a server is started, it registers its address and
related/necessary information with a central repository. For each operation, the
CustomerClient/EventManagerClient finds the required information about the associated
COMP 6231 (Distributed System Design), Summer 2019 — Assignment 1 Page 4
server from the central repository and invokes the corresponding operation. Your server
should ensure that a customer can only perform a customer operation and cannot
perform any event manager operation, but an event manager can perform all
operations.


<h2>Software Failure Tolerant and/or Highly Available Distributed Event Management System</h2>

  <h3>Overall Architecture</h3>
    <h4>Client</h4>
      <p>
        A client can be a customer or a manager. A customer may send requests to book or cancel an event, or view event schedule.A manager, in addition to those operations mentioned above, may add, delete events or view availability of events.Client communicates with Front End (FE) of the system to exchange messages. The communication is facilitated by CORBA standards.The client sends a request by invoking one of the services defined in Interface Repository of the system.
      </p>

<h4>Front End</h4>
      <p>

        FE is the only component that directly communicates with clients using CORBA standards. Concurrency is implemented to ensure efficient communication between the FE and clients.Moreover, it also communicates with a sequencer, which, in turn, communicates with replica managers (RMs) to exchange client requests and server responses. More importantly, FE is a gatekeeper that detects any software failure (software incorrectness) or server crash (server inavailability) based on server responses.In particular, if any of the replicas produces incorrect result, the FE informs all the RMs about that replica.If the same replica produces incorrect result for 3 consecutive client requests, then the RMs are informed to have the replica replaced with a correct one.If the FE does not receive the result from a replica within a reasonable time frame, it informs all RMs of a potential crash on that replica.The FE and the sequence communicate with each other via UDP protocol.
      </p>

        FE is the only component that directly communicates with clients using CORBA standards. Concurrency is implemented to ensure efficient communication between the FE and clients.Moreover, it also communicates with a sequencer, which, in turn, communicates with replica managers (RMs) to exchange client requests and server responses</br>. More importantly, FE is a gatekeeper that detects any software failure (software incorrectness) or server crash (server inavailability) based on server responses.In particular, if any of the replicas produces incorrect result, the FE informs all the RMs about that replica.If the same replica produces incorrect result for 3 consecutive client requests, then the RMs are informed to have the replica replaced with a correct one.If the FE does not receive the result from a replica within a reasonable time frame, it informs all RMs of a potential crash on that replica.<br>The FE and the sequence communicate with each other via UDP protocol.

</p>


<h4>Sequencer</h4>
    <p>
    Sequencer acts as a middleware between front-end and replica managers of all the replicas and communicates via UDP protocol. It’s primary job is to generate sequence number based on a chronological order and assign it  to client’s request.The sequencer then multicast those requests to all RMs. It also forwards responses from RMs back to FE.</br>
    </p>
