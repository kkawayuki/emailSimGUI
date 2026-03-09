package labs.lab9;

import java.time.LocalDateTime; // Import the LocalDateTime class
import java.time.format.DateTimeFormatter; // Import the DateTimeFormatter class

public class Email implements Comparable<Email>
{
	String sender;
	String recipient;
	String priority; //overload compareTo for arraylist to be able to sort emails
	String subject;
	String contents;
	LocalDateTime timeSent;
	
	public Email(String se, String r, String p, String su, String c, LocalDateTime t)
	{
		sender = se;
		recipient = r;
		priority = p;
		subject = su;
		contents = c;
		timeSent = t;
	}

	public String getContents()
	{
		DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		StringBuilder sb = new StringBuilder();
		sb.append("From: " + sender + '\n');
		sb.append("To: " + recipient + '\n');
		sb.append("Priority: " + priority + '\n');
		sb.append("Subject: " + subject + '\n');
		sb.append(timeSent.format(myFormatObj) + "\n\n");
		sb.append(contents);

		return(sb.toString());
	}

	public Integer getPriorityValue()
	{
		switch(priority)
		{
			case("High"): return 1;
			case("Medium"): return 2;
			case("Low"): return 3;
			default: return 4;
		}
	}
	
	@Override
	public int compareTo(Email other) {
		int priorityComparison = this.getPriorityValue().compareTo(other.getPriorityValue());
		if(priorityComparison != 0) return priorityComparison;

		//newest first, swap order of comparison
		else return other.timeSent.compareTo(this.timeSent);
	}
	
	@Override
	public String toString() {
	    return "From: " + sender + ", Subject: " + subject;
	}
}