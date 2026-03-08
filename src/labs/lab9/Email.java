package labs.lab9;

public class Email implements Comparable<Email>
{
	String sender;
	String recipient;
	Integer priority; //overload compareTo for arraylist to be able to sort emails
	String subject;
	String contents;
	
	@Override
	public int compareTo(Email other) {
	    return this.priority.compareTo(other.priority);
	}
	
	@Override
	public String toString() {
	    return "From: " + sender + ", Subject: " + subject;
	}
}