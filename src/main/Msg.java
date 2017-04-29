package main;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

public class Msg
{
	public static void delete(IMessage message)
	{
		try	{message.delete();}
		catch (Exception e){e.printStackTrace();}
	}
	
	public static long getRandomTimeTilDelete()
	{
		return Util.random.nextInt(2300) + 7000;
	}
	
	public static boolean contains(IMessage message, String text)
	{
		return message.getContent().toLowerCase().contains(text.toLowerCase()); // this is not case sensitive
	}
	
	public static boolean equals(IMessage message, String text)
	{
		return message.getContent().toLowerCase().equals(text.toLowerCase()); // this is not case sensitive
	}
	
	public static boolean startsWith(IMessage message, String text)
	{
		return message.getContent().toLowerCase().startsWith(text.toLowerCase()); // this is not case sensitive
	}
	
	public static void send(IChannel channel, String msg, long time)
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try { Thread.sleep(time);}
				catch (Exception e){}
				
				send(channel, msg, false);
			}
		}).start();
	}
	
	public static void send(IChannel channel, String msg, long time, boolean del)
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try { Thread.sleep(time);}
				catch (Exception e){}
				
				send(channel, msg, del);
			}
		}).start();
	}
	
	public static IMessage send(IChannel channel, String msg)
	{
		return send(channel, msg, false);
	}
	
	public static IMessage send(IChannel channel, String msg, boolean del)
	{
		try {
			//Builds (sends) and new message in the channel that the original message was sent with the content of the original message.
			final IMessage r = new MessageBuilder(channel.getClient()).withChannel(channel).withContent(msg).build();
			
			if (del && r != null)
			{
				new Thread(new Runnable(){
					@Override
					public void run()
					{
						try { Thread.sleep(getRandomTimeTilDelete());}
						catch (Exception e){}
						
						delete(r);
					}
				}).start();
			}
			
			return r;
			
		} catch (RateLimitException e) { //RateLimitException thrown. The bot is sending messages too quickly!
			System.out.print("SPAM. Sending messages too quickly!");
//			e.printStackTrace();
			send(channel, msg, 2000, del);
		} catch (DiscordException e) { //DiscordException thrown. Many possibilities.
			System.err.print("DiscordException: "+e.getErrorMessage()); //Print the error message sent by Discord
//			e.printStackTrace();
			send(channel, msg, 2000, del);
		} catch (MissingPermissionsException e) { //MissingPermissionsException thrown. The bot doesn't have permission to send the message!
			System.err.print("Missing permissions for channel!");
//			e.printStackTrace();
		}
		
		return null;
	}
	
	
}
