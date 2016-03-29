import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Class ProcessClient
 * Each Process sends event to another process running in different machine or same machine based 
 * on configurations in XML file.
 * @author Raghav Babu
 * Date : 03/22/2016
 */
public class ProcessClient extends Thread {

	Event event;
	int toProcessId;
	String toIPAddress = null;
	int  toPort;
	
	public ProcessClient(Event event, int toProcessId) {
		this.event = event;
		this.toProcessId = toProcessId;
		this.toIPAddress = ProcessIPPortXmlParser.processIDToIpMap.get(toProcessId);
		this.toPort = ProcessIPPortXmlParser.processIDToPortMap.get(toProcessId);
	}

	public void run() {

		try {

			Socket socket = null;

			try {

				try {
					socket = new Socket(toIPAddress, toPort);
				} catch (Exception e) {
					System.out.println("Server in "+toIPAddress+ " not yet bound to "+toPort);
					System.out.println("Start the Process server in "+toIPAddress+ " at port "+toPort);
					return;
				}
				//write event object
				OutputStream os = socket.getOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(os);
				oos.writeObject(event);
				
				//reducing from total amount.
				Process.currentTotalAmount -= event.amt;
				System.out.println(event);
				System.out.println("Current Balance = "+Process.currentTotalAmount);
				System.out.println("---------------------------------------");


			}catch (Exception e){
				System.out.println("Exception while passing event object to  "+toIPAddress);
				e.printStackTrace();
			}
			socket.close();

		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}
