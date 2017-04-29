package main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.handle.impl.events.DisconnectedEvent;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Status;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.Image;
import sx.blah.discord.util.MessageList;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;
import sx.blah.discord.util.audio.AudioPlayer;
import users.User;
import users.UsersUtil;

/*
 * purge / timeout
 * 
 * command queue
 * 
 * reform games and currency
 * 
 * !buy stats with value input
 * 
 */

public class AigisBot implements sx.blah.discord.api.events.IListener<MessageReceivedEvent>
{
	public static String comfyGuildID = "420"; // all IDS REDACTED
//	public static String retroGuildID = "204772876123897866";
//	public static String CGNTVGuildID = "147289640662073344";
//	public static String cinemaGuildID = "208437175358849026";
//	public static String gaijinGuildID = "208444775349747713";

	public static String nsfwID = "420"; // all IDS REDACTED
	public static String imagesID = "420";
	public static String AigisID = "420";
	public static String PuppyID = "420";
	public static String KhaosID = "420";
	public static String mutedID = "420";
	public static String judgesID = "420";
	public static String councilID = "420";
	public static String streamingID = "420";
	public static String eventChan = "420";
	//role group Comfys - 131468366886993920
	public static String music_voice_chan_ID = "420";
	public static String music_text_chan_ID = "420";
	public static double REWARDCOINS_PER_TIMEINTERVAL = 0.04d;
	public static int INTEREST_TIMEINTERVAL = 1 * 60 * 1000; // milliseconds (1 minute)
	public static int ACTIVITY_TIMEINTERVAL = 60; // 30 ticks * INTEREST_TIMEINTERVAL
	public static double ACTIVITY_MAXREWARD = 4.6d;
	public static long SPAM_PROTECTION_TIME = 1200; //ms
	public static int GDQ_TIMEINTERVAL = 30 * 60 * 1000; // 30 mins
	
	public IDiscordClient client; //The instance of the discord client.
	public IGuild comfyGuild; //The instance of the discord client.
	public IGuild retroGuild; //The instance of the discord client.
	
	static AigisBot bot;
	static Thread InterestThread;
	static AudioPlayer audioPlayer;
	
	int timeTickForActivity = 0;
	long lastTimeIReacted = 0;
	ArrayList<IMessage> queue = new ArrayList<IMessage>();
	
	ArrayList<User> customers = new ArrayList<User>();
	
	public static void main(String[] args)
	{
		bot = new AigisBot();
		bot.init();
	}
	
	public void init()
	{
		ClientBuilder builder = new ClientBuilder(); //Creates a new client builder instance
		builder.withToken("REDACTED");
		
		try {
			client = builder.login(); //Builds the IDiscordClient instance and logs it in
		} catch (DiscordException e) { //Error occurred logging in
			System.err.println("Error occurred while logging in!");
			System.out.println("Error occurred while logging in!");
			e.printStackTrace();
		}
		
		EventDispatcher dispatcher = client.getDispatcher(); //Gets the client's event dispatcher
		dispatcher.registerListener(this); //Registers the event listener
		dispatcher.registerListener(new Reconnecter()); //Registers the event listener
		dispatcher.registerListener(new MrReady()); //Registers the event listener
		
		loadDataFromFiles();
		InterestThread = new Thread(new Interest());
		InterestThread.start();
	}
	
	public void loadDataFromFiles()
	{
		String string_actTimeTick = Files.readFromFile("data/time_active_tick.txt");
		if (string_actTimeTick != null)
		{
			timeTickForActivity = Integer.parseInt(string_actTimeTick);
		}

	}
	
	public boolean usersAlreadyContainsID(String id)
	{
		for (User c : customers)
		{
			if (c.id.equals(id)) return true;
		}
		
		return false;
	}
	
	public void saveDataToFiles()
	{
		StringBuilder strb = new StringBuilder();
		
		strb = new StringBuilder();
		//save timetick
		Files.writeToFile(""+timeTickForActivity, "data/time_active_tick.txt");
	}

	public boolean isCustomer(IUser user)
	{
		for (User c : customers)
		{
			if (c.id.equals(user.getID())) return true;
		}
		return false;
	}
	
	public IChannel getPMChannel(IUser u)
	{
		try
		{
			return client.getOrCreatePMChannel(u);
		}
		catch (DiscordException | RateLimitException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public void deleteLastRequestedImageMessage(IMessage message)
	{
		IMessage delThis = null;
		int i=0;
		for (IMessage m : new MessageList(client, message.getChannel(), MessageList.MESSAGE_CHUNK_COUNT))
		{
			if (m != null && m.getContent() != null && m.getAuthor().getID().equals(AigisID) && Msg.contains(m, "requested") && Msg.contains(m, message.getAuthor().getName()+"#"+message.getAuthor().getDiscriminator()))
			{
				delThis = m;
				i = 30;
			}
			i++;
			if (i > 20) break;
		}
		
		if (delThis != null)
		{
			Msg.delete(delThis);
			Msg.send(message.getChannel(), "I have removed the previous request.", true);
		}
		else
		{
			Msg.send(message.getChannel(), "I cant find any recent requests you have made, "+message.getAuthor().getName()+".", true);
		}
	}
	
	public void reactToAigisWord(IMessage message)
	{
		IUser author = message.getAuthor();
		IChannel channel = message.getChannel();
		if (Msg.contains(message, "shiree") || Msg.contains(message, "tea"))
		{
			IUser u = author;
			if (message.getMentions().size() > 0)
			{
				u = message.getMentions().get(0);
			}
			Msg.send(channel, u.getName()+"-sama, please relax. Have some tea: \n"+PageLinks.tea,400);
		}
		else if (Msg.contains(message, "honk"))
		{
			Msg.send(channel, "http://puu.sh/oOqw8/f0e7bddc59.jpg", 300);
		}
		else if (Msg.contains(message, "nyoron"))
		{
			Msg.send(channel, "http://puu.sh/oQ6Bb/1697a4a733.jpg", 400);
		}
		else if (Msg.contains(message, "mad") || Msg.contains(message, "angry"))
		{
			Msg.send(channel, "*glares* \n"+PageLinks.glare,400);
		}
		else if (Msg.contains(message, "love"))
		{
			Msg.send(channel, PageLinks.love,400);
		}
		else if (Msg.contains(message, "dredd"))
		{
			Msg.send(channel, "Lawgived StiffUpperLip:tr\nPuriESports:bl Powerchin:br",400);
		}
		else if (Msg.contains(message, "nerd"))
		{
			Msg.send(channel, "http://eu03.mechafetus.com/new/sd/sad306.jpg",400);
		}
		else if (Msg.contains(message, "tell"))
		{
			Msg.send(channel, "When you fall, get right back up.\n\nhttp://puu.sh/oNKkf/93e30f2698.mp3", 500);
		}
		else if ( Msg.contains(message, "remove") || Msg.contains(message, "delete") || Msg.contains(message, "kill") || Msg.contains(message, "erase")  || Msg.contains(message, "repost")) 
		{
			deleteLastRequestedImageMessage(message);
		}
		else
		{
//			Msg.send(channel, PageLinks.stare, 2500);
		}
	}
	
	public void reactToAdminCommands(IMessage message)
	{
		IUser author = message.getAuthor();
		IChannel channel = message.getChannel();
		
		if (author.getID().equals(KhaosID))
		{
			if (Msg.equals(message, "!change_avatar"))
			{
				try
				{
					client.changeAvatar(Image.forUrl("jpg", "http://puu.sh/oNEWn/56b3c04e46.jpg"));
				}
				catch (DiscordException | RateLimitException e)
				{
					e.printStackTrace();
				}
				Msg.delete(message);
			}
		}
	}
	
	public void addNewCustomers(IUser author)
	{
		if (!isCustomer(author))
		{
			UsersUtil.getUserByID(author.getID(), customers);
			System.out.println("Added customer "+author.getName());
		}
	}
	
//	public void autoSetUsersToStreamingRole()
//	{
//		IRole strimRole = client.getGuildByID(comfyGuildID).getRoleByID("178602483243941888"); //comfys -> streaming role
//		IUser nu;
//		IUser su;
//		
//		//Discr: 1375   Pres: ONLINE   game: Optional[us like a damn fiddle]
//		
//		for (IUser u : client.)
//		{
//			client.getGuildByID("").ed
//		}
//		
//		System.out.println("Discr: "+author.getDiscriminator()+"   Pres: "+author.getPresence()+"   game: "+author.getGame());
//	}
	
	public void postNSFW(IMessage message)
	{
		IChannel channel = message.getChannel();
		String[] splits = message.getContent().toLowerCase().split(" ");
		if (splits.length < 2)
		{
			Msg.send(channel, "*Usage: !nsfw <tags>\nUse spaces inbetween tags.*");
		}
		else
		{
			String tags = "";
			for (int i=1;i<splits.length;i++)
			{
				if (i != 1) tags = tags + " ";
				tags = tags + splits[i];
			}
			String img = PageLinks.getGelbooruImageLink(tags);
			if (img != null)
			{
				Msg.send(channel, "'**"+tags+"**' requested by "+message.getAuthor().getName()+"#"+message.getAuthor().getDiscriminator()+"\n"+img);
			}
			else
			{
				Msg.send(channel, "No results for the tags "+tags+" , "+message.getAuthor().getName());
			}
		}
		Msg.delete(message);
	}
	
	public void postSafePic(IMessage message)
	{
		IChannel channel = message.getChannel();
		String[] splits = message.getContent().toLowerCase().split(" ");
		if (splits.length < 2)
		{
			Msg.send(channel, "*Usage: !pic <tags>\nUse spaces inbetween tags.*");
		}
		else
		{
			String tags = "";
			for (int i=1;i<splits.length;i++)
			{
				if (i != 1) tags = tags + " ";
				tags = tags + splits[i];
			}
			String img = PageLinks.getSafebooruImageLink(tags);
			if (img != null)
			{
				Msg.send(channel, "'**"+tags+"**' requested by "+message.getAuthor().getName()+"#"+message.getAuthor().getDiscriminator()+"\n"+img);
			}
			else
			{
				Msg.send(channel, "No results for the tags "+tags+" , "+message.getAuthor().getName());
			}
		}
		Msg.delete(message);
	}
	
	public void postAvatar(IMessage message)
	{
		IUser author = message.getAuthor();
		IUser u = author;
		if (message.getMentions().size() > 0)
		{
			u = message.getMentions().get(0);
		}
		
		Msg.send(message.getChannel(), ""+u.getAvatarURL());
	}
	
	public void modCommands(IMessage message)
	{
		IUser author = message.getAuthor();
		IChannel channel = message.getChannel();
		IUser targetUser = null;
		
		if (Msg.equals(message, "!shutdown_bot"))
		{
			Msg.delete(message);
			Msg.send(channel, "Shutting down.");
			shutdown();
		}
		else
		{
			if (message.getMentions().size() > 0)
			{
				targetUser = message.getMentions().get(0);
			}
			else
			{
				Msg.delete(message);
				Msg.send(getPMChannel(author), "You need to @mention a user.");
				return;
			}
			
			if (Msg.contains(message, "!mute"))
			{
				addRoleToUser(targetUser, mutedID);
				Msg.send(channel, targetUser.getName()+" was muted. No timelimit.");
			}
			else if (Msg.contains(message, "!unmute"))
			{
				removeRoleFromUser(targetUser, mutedID);
				Msg.send(channel, targetUser.getName()+" was unmuted.");
			}
			else if (Msg.contains(message, "!timeout"))
			{
				Msg.send(channel, "Timeout is not yet implemented.");
			}
			else if (Msg.contains(message, "!purge"))
			{
				ArrayList<IMessage> killThese = new ArrayList<IMessage>();
				
				for (IMessage m : channel.getMessages())
				{
					if (m.getAuthor() == targetUser)
					{
						killThese.add(m);
					}
					if (killThese.size() >= 5) break;
				}
				
				for (IMessage m : killThese)
				{
					Msg.delete(m);
				}
				
				Msg.delete(message);
				Msg.send(channel, targetUser.getName()+" was purged.", true);
			}
		}
	}
	
	public void addRoleToUser(IUser user, String roleID)
	{
		if (hasUserThisRole(user, roleID)) return;
		
		IRole role = comfyGuild.getRoleByID(roleID);
		
		List<IRole> curRoles = user.getRolesForGuild(comfyGuild);
		curRoles.add(role);
		IRole[] roles = curRoles.stream().toArray(IRole[]::new);
		
		try	{
			comfyGuild.editUserRoles(user, roles);
		}
		catch (MissingPermissionsException | RateLimitException| DiscordException e){e.printStackTrace();}
	}
	
	public void removeRoleFromUser(IUser user, String roleID)
	{
		if (!hasUserThisRole(user, roleID)) return;
		
		IRole role = comfyGuild.getRoleByID(roleID);
		
		List<IRole> curRoles = user.getRolesForGuild(comfyGuild);
		curRoles.remove(role);
		IRole[] roles = curRoles.stream().toArray(IRole[]::new);
		
		try	{
			comfyGuild.editUserRoles(user, roles);
		}
		catch (MissingPermissionsException | RateLimitException| DiscordException e){e.printStackTrace();}
	}
	
	public boolean hasUserThisRole(IUser u, String roleID)
	{
		IRole role = comfyGuild.getRoleByID(roleID);
		
		for (IRole r : u.getRolesForGuild(comfyGuild))
		{
			if (r == role) return true;
		}
		
		return false;
	}
	
//	public void setStreamingSelloutGroup()
//	{
//		for(IUser u : comfyGuild.getUsers())
//		{
//			if (u.getStatus().getType() == Status.StatusType.STREAM)
//			{
//				if (!hasUserThisRole(u, streamingID))
//				{
//					addRoleToUser(u, streamingID);
//				}
//			}
//			else
//			{
//				if (hasUserThisRole(u, streamingID))
//				{
//					removeRoleFromUser(u, streamingID);
//				}
//			}
//		}
//	}
	
	public boolean runSharedCommands(IChannel channel, IMessage message)
	{
		if (Msg.startsWith(message, "!random_game"))
		{
			IMessage m = Msg.send(channel, "Searching. Please hold.");
			PageLinks.GameInfo game = PageLinks.getRandomGame();
			Msg.delete(m);
			Msg.send(channel, "**"+game.name+"**             "+game.releaseDate+"\n\n"+game.description+"\n"+game.youtube);
		}
		else if (Msg.startsWith(message, "!ps1"))
		{
			String splits[] = message.getContent().split(" ");
			String tags = "";
			for (int i=1;i<splits.length;i++)
			{
				if (i != 1) tags = tags + " ";
				tags = tags + splits[i];
			}
			
			IMessage m = Msg.send(channel, "Searching. Please hold.");
			PageLinks.GameInfo game = PageLinks.getMobyPs1Game(tags);
			Msg.delete(m);
			Msg.send(channel, game.name+"\n"+game.releaseDate+"\n\n"+game.coverurl);
		}
		else if (Msg.equals(message, "!commands") || Msg.equals(message, "!cmds") || Msg.equals(message, "!help"))
		{
			Msg.send(channel, "You require assistance? Certainly.\nThese are the current commands: <http://pastebin.com/xFM0gWad>");
		}
		else if (Msg.startsWith(message, "!avatar"))
		{
			postAvatar(message);
		}
		else if (Msg.startsWith(message, "!fortune"))
		{
			Msg.send(channel, ""+PageLinks.getFortune());
		}
		else if (Msg.startsWith(message, "!currency"))
		{
			String[] splits = message.getContent().split(" ");
			
			if (splits.length < 4)
			{
				Msg.send(channel, "*Usage: !currency <amount> <in_currency> <out_currency>");
			}
			
			Msg.send(message.getChannel(), PageLinks.getCurrencyExchange(splits[1], splits[2], splits[3]));
		}
		else if (Msg.startsWith(message, "!footage"))
		{
			String[] splits = message.getContent().split(" ");
			String qstring = "";
			
			for (int i = 1;i<splits.length;i++)
			{
				if (qstring.length() > 0)
					qstring = qstring + " " + splits[i];
				else
					qstring = splits[i];
			}
			
			qstring = qstring + " gameplay";
			
			Msg.send(message.getChannel(), "http://www.youtube.com/watch?v="+YTAPI.searchYoutube(qstring).id+"&t=99");
		}
		else if (Msg.equals(message, "!bunnies") || Msg.equals(message, "!bunny") || 
				Msg.equals(message, "!rabbits") || Msg.equals(message, "!rabbit"))
		{
			Msg.send(channel, ""+PageLinks.getBunnyImage());
		}
		else if (Msg.equals(message, "!cats") || Msg.equals(message, "!cat"))
		{
			Msg.send(channel, ""+PageLinks.getCatImage());
		}
		else if (Msg.equals(message, "!dogs") || Msg.equals(message, "!dog"))
		{
			Msg.send(channel, ""+PageLinks.getDogImage());
		}
		else
		{
			return false;
		}
		
		return true;
	}
	
	@Override
	public void handle(MessageReceivedEvent event)
	{
		IMessage message = event.getMessage();
		
		message.toString();
		IGuild g = message.getGuild();
		if (g == null)
		{
			//whisper
			
		}
		else if (g.getID().equals(comfyGuildID))
		{
			handleComfys(event);
		}
//		else if (message.getGuild().getID().equals(retroGuildID))
//		{
//			handleRetro(event);
//		}
//		else if (message.getGuild().getID().equals(CGNTVGuildID))
//		{
//			handleCGNTV(event);
//		}
	}
	
//	public void handleCGNTV(MessageReceivedEvent event)
//	{
//		IMessage message = event.getMessage(); 
//		IChannel channel = message.getChannel();
//		IUser author = message.getAuthor();
//		System.out.println("CGNTV|"+message.getTimestamp()+"["+channel.getName()+"]: "+author.getName()+": "+message.getContent() );
//		
//		runSharedCommands(channel, message);
//		esaCommands(message, channel);
//	}
//
//	public void handleRetro(MessageReceivedEvent event)
//	{
//		IMessage message = event.getMessage(); 
//		IChannel channel = message.getChannel();
//		IUser author = message.getAuthor();
//		System.out.println("Retro|"+message.getTimestamp()+"["+channel.getName()+"]: "+author.getName()+": "+message.getContent() );
//		
//		runSharedCommands(channel, message);
//	}
	
	public void handleComfys(MessageReceivedEvent event)
	{
		IMessage message = event.getMessage(); //Gets the message from the event object NOTE: This is not the content of the message, but the object itself
		IChannel channel = message.getChannel(); //Gets the channel in which this message was sent.
		IUser author = message.getAuthor();
		System.out.println("Comfy|"+message.getTimestamp()+"["+channel.getName()+"]: "+author.getName()+": "+message.getContent() );
		
//		autoSetUsersToStreamingRole();
		
//		addNewCustomers(author);
		
//		tickSlotsTimer();
//		addCoinsForActivity(message);
		
//		if (channel.getID().equals(music_text_chan_ID))
//		{
//			try{
//				musicCommands(message);
//			}catch(DiscordException e3){e3.printStackTrace();}
//		}
		
		if (author.getID().equals("97004921269813248") || author.getName().toLowerCase().contains("boon")) return;
		
		if (Msg.startsWith(message, "!mute") || Msg.startsWith(message, "!unmute") || Msg.startsWith(message, "!timeout") || Msg.startsWith(message, "!purge")  || Msg.startsWith(message, "!shutdown_bot") )
		{
			if (hasUserThisRole(author, judgesID) || hasUserThisRole(author, councilID) || author.getID().equals(KhaosID))
			{
				modCommands(message);
			}
		}
		
		if (Msg.contains(message, "aigis"))
		{
			reactToAigisWord(message);
		}
		
		
		if (channel.isPrivate() && author.getID().equals(KhaosID))
		{
			if (Msg.startsWith(message, "!say"))
			{
				String split[] = message.getContent().split(" ");
				String chanName = split[1];
				String outMsg = "";
				for (int i=2;i<split.length;i++)
				{
					outMsg = outMsg + split[i]+ " ";
				}
				
				IChannel chan = null;
				for (IChannel c : client.getChannels(false))
				{
					if (c.getName().toLowerCase().equals(chanName.toLowerCase()))
					{
						chan = c;
						break;
					}
				}
				if (chan != null)
				{
					Msg.send(chan, outMsg);
				}
			}
			System.out.println("This is a whisper from sensei");
		}
		
//		if (channel.getID().equals(casinoID) && Msg.startsWith(message, "!"))//casino
//		{
//			if ((System.currentTimeMillis() - lastTimeIReacted) < SPAM_PROTECTION_TIME)
//			{
//				System.out.println("SPAM PROTECTION");
//				Msg.send(channel, "Please dont be hasty. I can't react this fast.", 1200, true);
//				return;
//			}
//			lastTimeIReacted = System.currentTimeMillis();
//		}
		
		if (!runSharedCommands(channel, message))
		{
			if (Msg.startsWith(message, "!nsfw") || Msg.startsWith(message, "!hentai"))
			{
				postNSFW(message);
			}
			else if (Msg.startsWith(message, "!test07"))
			{
				System.out.println(""+channel.getName()+"  "+channel.getID());
				
				PageLinks.getESASchedule(null);
				Msg.delete(message);
			}
			else if (Msg.startsWith(message, "!sfw") || Msg.startsWith(message, "!pic") || Msg.startsWith(message, "!image"))
			{
				postSafePic(message);
			}
			else if (Msg.startsWith(message, "!r"))
			{
				String[] splits = message.getContent().toLowerCase().split(" ");
				if (splits == null || splits.length != 2)
				{
					Msg.send(channel, "*Usage: !r <search-term>*");
				}
				else
				{
					String img = PageLinks.getRImage(splits[1]);
					if (img != null)	Msg.send(channel, "'**"+splits[1]+"**' requested by "+message.getAuthor().getName()+"#"+message.getAuthor().getDiscriminator()+"\n"+img);
					else				Msg.send(channel, "No results for the search term "+splits[1]+" , "+message.getAuthor().getName(), 8000);
				}
				Msg.delete(message);
			}
			else
			{
				reactToAdminCommands(message);
			}
		}
	}
	
	public void shutdown()
	{
		saveDataToFiles();
		InterestThread.stop();
		try{client.logout();}
		catch (DiscordException e)
		{
			e.printStackTrace();
		}
	}
	
	public String getHTTPQuery(String url) throws Exception
	{
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(url);

		// add request header
//		request.addHeader("User-Agent", USER_AGENT);
		HttpResponse response = client.execute(request);

		System.out.println("Response Code : " + response.getStatusLine().getStatusCode());

		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		
		return result.toString();
	}
	
	public void sendPM(IUser user, String msg)
	{
		IChannel pmc = getPMChannel(user);
		if (pmc == null)
		{
			System.out.println("ERROR while creating PM channel with "+user.getName());
			return;
		}
		
		Msg.send(pmc, msg);
		System.out.println("PM to "+user.getName()+":  "+msg);
	}
	
	public void sleep(long time)
	{
		try { Thread.sleep(time); }
		catch (InterruptedException e){}
	}
	
	class Interest implements Runnable
	{
		@Override
		public void run()
		{
			while (true)
			{
				try { Thread.sleep(INTEREST_TIMEINTERVAL); }
				catch (InterruptedException e){}
				
				timeTickForActivity++;
				if (timeTickForActivity >= ACTIVITY_TIMEINTERVAL)
				{
					timeTickForActivity = 0;
				}
				
				saveDataToFiles();
			}
		}
	}
	
	class Reconnecter implements sx.blah.discord.api.events.IListener<DisconnectedEvent>
	{
		@Override
		public void handle(DisconnectedEvent event)
		{
			System.out.println("DiscordDisconnectedEvent "+event.getReason());
			
			new Thread(new DoReconnect()).start();
		}
	}
	
	class DoReconnect implements Runnable
	{
		@Override
		public void run()
		{
			try { Thread.sleep(20000);}
			catch (Exception e){}
			
			try
			{
				client.login();
			}
			catch (RateLimitException | DiscordException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	class MrReady implements sx.blah.discord.api.events.IListener<ReadyEvent>
	{
		@Override
		public void handle(ReadyEvent event)
		{
			System.out.println("Starting up...");
			new Thread(new ReadyCheckBack()).start();
		}
		
	}
	
	class ReadyCheckBack implements Runnable
	{
		@Override
		public void run()
		{
			loadDataFromFiles();

			while (!client.isReady())
			{
				sleep(120);
			}
			
			System.out.println("Aigis rebooted");
			
			for (IGuild g : client.getGuilds())
			{
				System.out.println(""+g.getName()+"   "+g.getID());
			}
			
			comfyGuild = client.getGuildByID(comfyGuildID);
//			retroGuild = client.getGuildByID(retroGuildID);
			
			client.changePresence(false);
			client.changeStatus(Status.game("Use !help"));
			
//			final IChannel eventChannel = client.getChannelByID(eventChan);
//			new Thread(new Runnable()
//			{
//				@Override
//				public void run()
//				{
//					IMessage m = null;
//					while(true)
//					{
//						m = Msg.send(eventChannel, PageLinks.getSGDQSchedule(null), false);
//						sleep(GDQ_TIMEINTERVAL);
//						Msg.delete(m);
//						sleep(9000);
//					}
//				}
//			}).start();
			
			
			
//			Msg.send(chan, "Aigis rebooted. v0.58\nhttp://puu.sh/oNKiM/2e859e293b.mp3");
			
//			for (IRole r : client.getUserByID(AigisID).getRolesForGuild(client.getGuildByID(comfyID)))
//			{
//				System.out.println("\t"+r.getName()+"    "+r.getID());
//			}
			
//			String deathID = "98492785123917824";
			
			
//			for (IVoiceChannel v : client.getGuildByID(comfyID).getVoiceChannels())
//			{
//				System.out.println("\t"+v.getName()+"    "+v.getID());
//			}
			

			
		}
	}
	
}
