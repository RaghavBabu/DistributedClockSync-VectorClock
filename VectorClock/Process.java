import java.util.Random;

/**
 * Class Process
 * Each Process running initiates an event.
 * Event can be withdraw,deposit within the process or transfer of amount to another process which happens 
 * for every 5 seconds.
 * It maintains a vector clock that will be updated for each event.
 * @author Raghav Babu
 * Date : 03/22/2016
 */
public class Process{

	static int processId;
	static int[] vectorClock;
	static int currentTotalAmount;

	public Process(){

	}

	/*
	 * Main Function
	 * input arguments are pro.cessId and total number of process involved
	 */
	public static  void main(String args[]) {

		//command line args.
		processId = Integer.parseInt(args[0]);
		int totalProcess = Integer.parseInt(args[1]);
		currentTotalAmount = 1000;

		Process p = new Process();

		//read XML file.
		ProcessIPPortXmlParser parser = new ProcessIPPortXmlParser();
		parser.parseXML();

		//start process server to receive events from other processes.
		ProcessServer server = new ProcessServer(processId);
		server.start();

		vectorClock = new int[totalProcess];
		//initializing vector clock.
		for(int i = 0; i < totalProcess; i++){
			vectorClock[i] = 0;
		}
		
		while(true) {
			
		//choosing random send event. deposit, withdraw or send.
		EventType randomEventType = p.pickRandomEventType();

		int randomAmt = new Random().nextInt(100) + 1;

		Event event = null;

		//if event is transfer, choose another process to send to.
		if(randomEventType == EventType.TRANSFER) {

			//choosing a random process to send to.
			int randomToProcess = new Random().nextInt(totalProcess) + 1;

			//choosing a different process id other than its own.
			while(randomToProcess == processId){
				randomToProcess = new Random().nextInt(totalProcess) + 1;
				continue;
			}

			vectorClock[processId - 1] += 1;

			System.out.println("Sending amount "+randomAmt+ " to process "+randomToProcess);
			event = new Event(randomEventType, processId,randomAmt, vectorClock);
			
			//prompt the client to send it to destination process.
			ProcessClient client = new ProcessClient(event, randomToProcess);
			client.start();
		}
		//event to withdraw amount.
		else if(randomEventType == EventType.WITHDRAW) {

			Process.currentTotalAmount -= randomAmt;
			vectorClock[processId - 1] += 1;	

			event = new Event(randomEventType, processId,randomAmt, vectorClock);
			System.out.println(event);
			System.out.println("Current Balance = "+Process.currentTotalAmount);
			System.out.println("---------------------------------------");
		}
		//event to deposit amount.
		else if(randomEventType == EventType.DEPOSIT){

			Process.currentTotalAmount += randomAmt;
			vectorClock[processId - 1] += 1;	

			event = new Event(randomEventType, processId,randomAmt, vectorClock);

			System.out.println(event);
			System.out.println("Current Balance = "+Process.currentTotalAmount);
			System.out.println("---------------------------------------");
		}

		try {
			Thread.currentThread().sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	}

	/**
	 * Method to pick a random event.
	 * @return withdraw or depoist or transfer eventtype enum.
	 */
	private EventType pickRandomEventType() {

		int num = new Random().nextInt(EventType.values().length);
		return EventType.values()[num];

	}
}
