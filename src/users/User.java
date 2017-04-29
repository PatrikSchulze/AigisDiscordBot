package users;

import sx.blah.discord.api.IDiscordClient;

public class User
{
	public String id;

	public User(String _id)
	{
		id = _id;
	}
	
	public String getName(IDiscordClient client) { return client.getUserByID(id).getName(); }
}
