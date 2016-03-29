import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Class ProcessServer
 * Each Process receives event from other process and updates its vector clock.
 * @author Raghav Babu
 * Date : 03/22/2016
 */
public class ProcessServer extends Thread {

	private InetSocketAddress boundPort = null;
	private static int port;
	private ServerSocket serverSocket;
	private int id;

	public ProcessServer(int  id) {
		this.id = id;
		port = ProcessIPPortXmlParser.processIDToPortMap.get(id);
	}

	@Override
	public void run(){

		try {

			initServerSocket();

			while(true) {

				Socket connectionSocket;
				ObjectInputStream ois;
				InputStream inputStream;


				connectionSocket = serverSocket.accept();
				inputStream = connectionSocket.getInputStream();
				ois = new ObjectInputStream(inputStream);

				Event event = (Event) ois.readObject();

				Process.currentTotalAmount += event.amt;

				int[] remoteVectorClock = event.vectorClock;

				//updating local vector clock.
				for(int i = 0; i < Process.vectorClock.length; i++){

					//same process if, increment clock count.
					if(i == Process.processId - 1)
						Process.vectorClock[Process.processId - 1] += 1;
					
					//if different process id, update the local vector clock to max of both.
					else{
						
						if(remoteVectorClock[i] > Process.vectorClock[i])
							Process.vectorClock[i] = remoteVectorClock[i];
					}

				}
				System.out.println("After receiving "+event.amt+" from process "+event.fromProcessId);
				
				//create a new event to print and show current state.
				event = new Event(EventType.RECEIVE, event.fromProcessId, event.amt, Process.vectorClock );
				System.out.println(event);
				
				System.out.println("Current Balance = "+Process.currentTotalAmount);
				System.out.println("---------------------------------------");
			}
		}catch(Exception e){
			System.out.println("Exception while receiving event in process : "+id+" ");
			e.printStackTrace();
		}

	}

	/**
	 * method which initialized and bounds a server socket to a port.
	 * @return void.
	 */
	private void initServerSocket()
	{
		boundPort = new InetSocketAddress(port);
		try
		{
			serverSocket = new ServerSocket(port);

			if (serverSocket.isBound())
			{
				System.out.println("Server bound to data port " + serverSocket.getLocalPort() + " and is ready...");
			}
		}
		catch (Exception e)
		{
			System.out.println("Unable to initiate socket.");
		}

	}

}
