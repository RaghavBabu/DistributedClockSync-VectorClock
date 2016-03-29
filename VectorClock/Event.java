import java.io.Serializable;
import java.util.Arrays;

/**
 * Class Event
 * Event object is sent between process to update their vector clocks.
 * @author Raghav Babu
 *
 */
public class Event implements Serializable {

	private static final long serialVersionUID = -6339854005529033602L;
	EventType type;
	int fromProcessId;
	int amt;
	int[] vectorClock;

	
	public Event(EventType eventType, int fromProcessId, int randomAmt, int[] vectorClock) {
		this.type = eventType;
		this.fromProcessId = fromProcessId;
		this.amt = randomAmt;
		this.vectorClock = vectorClock;
	}


	@Override
	public String toString() {
		return "Event [type=" + type
				+ ", amt=" + amt + ", vectorClock="
				+ Arrays.toString(vectorClock) + "]";
	}

}
