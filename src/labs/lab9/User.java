package labs.lab9;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class User
{
	String name;
    int ID;
	ArrayList<Email>emails = new ArrayList<>();
	
	public User(String name)
	{
		this.name = name;
        this.ID = hashCode();
	}

    public String getName()
    {
        return this.name; 
    }

    public int getID()
    {
        return this.ID;
    }

    public void addEmail(Email e)
    {
        emails.add(e);
    }

    public ArrayList<Email> sortEmails()
    {
        Collections.sort(emails);   //provide custom behavior for sort via email @override of compareTo method
        return this.emails;
    }

	// for using Users in a list, we need to ensure that uniqueness can be verified
	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(name, user.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString()
    {
        return this.name; 
    }
}