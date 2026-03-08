package labs.lab9;
import java.util.ArrayList;
import java.util.Objects;

public class User
{
	String name;
	ArrayList<Email>emails = new ArrayList<>();
	
	public User(String name)
	{
		this.name = name;
	}
	
	
	//for using Users in a list, we need to ensure that uniqueness can be verified 
	
	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(name, user.name);
    }

	//needed? 
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}