package users;

import java.util.ArrayList;

public class UsersUtil
{
	
	public UsersUtil()
	{
		// TODO Auto-generated constructor stub
	}
	
	public static User getUserByID(String id, ArrayList<User> list)
	{
		for (User c : list)
		{
			if (c.id.equals(id)) return c;
		}
		
		//since I reached this point, none was found.
		User nc = new User(id);
		list.add(nc);
		
		return nc;
	}
	
}
