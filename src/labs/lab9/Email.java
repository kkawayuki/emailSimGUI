package labs.lab9;

public class Email implements Comparable<Email>
{
	String sender;
	String recipient;
	String priority; //overload compareTo for arraylist to be able to sort emails
	String subject;
	String contents;
	
	public Email(String se, String r, String p, String su, String c)
	{
		sender = se;
		recipient = r;
		priority = p;
		subject = su;
		contents = c;
	}

	public String getContents()
	{
		return(contents);
	}
	
	@Override
	public int compareTo(Email other) {
	    return this.priority.compareTo(other.priority);
	}
	
	@Override
	public String toString() {
	    return "From: " + sender + ", Subject: " + subject;
	}
}