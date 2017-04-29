package main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

public class PageLinks
{
	public static String glare = "https://danbooru.donmai.us/data/13ea77f61bd0b150b668f8b51f54b85f.jpg";
	public static String stare = "http://puu.sh/oK8pO/bcf95909f2.jpg";
	public static String tea = "http://www.java-breeze.com/images/teas/tea-cup.png";
	public static String love = "https://danbooru.donmai.us/data/sample/sample-b95d302421d18cd7e4b0446e6b31bb7b.jpg";
	public static String evasiveAction = "http://img09.deviantart.net/ed52/i/2010/235/6/e/aigis_by_revenbg.jpg";
	private static Random rng;
	
	static
	{
		rng = new Random();
	}
	
	//should be async usually
	public static String getGelbooruImageLink(String tags)
    {
		tags = tags.replaceAll("rating:s ", "rating:safe ");
		tags = tags.replaceAll("rating:q ", "rating:questionable ");
		tags = tags.replaceAll("rating:e ", "rating:explicit ");
		tags = tags.replaceAll(" rating:s", " rating:safe");
		tags = tags.replaceAll(" rating:q", " rating:questionable");
		tags = tags.replaceAll(" rating:e", " rating:explicit");
		tags = tags.replaceAll(" ", "+");
		String url = "http://gelbooru.com/index.php?page=dapi&s=post&q=index&limit=100&tags="+tags;
//		System.out.println("URL: "+url);
        String webpage = getHTTPQuery(url);
        String[] entries = webpage.split("file_url=\"");
        
        if (entries.length <= 1)
            return null;
        
        String[] files = new String[entries.length-1];
        for (int i=0;i<files.length;i++)
        {
        	files[i] = entries[i+1].split("\"")[0];
//        	System.out.println("#"+i+": "+files[i]);
        }
        
//        System.out.println("entries: "+entries.length);
        int rr = rng.nextInt(files.length);
        return files[rr];
    }
	
	public static String getSafebooruImageLink(String tags)
    {
		tags = tags.replaceAll(" ", "+");
		String url = "http://safebooru.org/index.php?page=dapi&s=post&q=index&limit=100&tags="+tags;
//		System.out.println("URL: "+url);
        String webpage = getHTTPQuery(url);
        String[] entries = webpage.split("file_url=\"");
        
        if (entries.length <= 1)
            return null;
        
        String[] files = new String[entries.length-1];
        for (int i=0;i<files.length;i++)
        {
        	files[i] = entries[i+1].split("\"")[0];
//        	System.out.println("#"+i+": "+files[i]);
        }
        
//        System.out.println("entries: "+entries.length);
        int rr = rng.nextInt(files.length);
        return files[rr];
    }
	
	public static String getBunnyImage()
    {
		String url = "http://imgur.com/r/Rabbits";
        String webpage = getHTTPQuery(url);
        String[] entries = webpage.split("<img alt=\"\" src=\"//");
        
        if (entries.length <= 1)
            return null;
        
        String[] files = new String[entries.length-1];
        for (int i=0;i<files.length;i++)
        {
        	files[i] = "http://" + entries[i+1].split("\" />")[0];
//        	files[i] = files[i].substring(0, files[i].length()-4);
        	files[i] = files[i].replaceAll("b.jpg", ".jpg");
        	files[i] = files[i].replaceAll("b.gif", ".gif");
//        	System.out.println("#"+i+": "+files[i]);
        }
        
        int rr = rng.nextInt(files.length);
        return files[rr];
    }
	
	public static String getCatImage()
    {
		String url = "http://imgur.com/r/Cats";
		int randomurl = rng.nextInt(2);
		if (randomurl == 0)
		{
			url = "http://imgur.com/r/Cat";
		}
        String webpage = getHTTPQuery(url);
        String[] entries = webpage.split("<img alt=\"\" src=\"//");
        
        if (entries.length <= 1)
            return null;
        
        String[] files = new String[entries.length-1];
        for (int i=0;i<files.length;i++)
        {
        	files[i] = "http://" + entries[i+1].split("\" />")[0];
        	files[i] = files[i].replaceAll("b.jpg", ".jpg");
			files[i] = files[i].replaceAll("b.gif", ".gif");
//        	System.out.println("#"+i+": "+files[i]);
        }
        
        
        int rr = rng.nextInt(files.length);
        return files[rr];
    }
	
	public static String getDogImage()
    {
		String url = "http://imgur.com/r/Dogs";
		int randomurl = rng.nextInt(2);
		if (randomurl == 0)
		{
			url = "http://imgur.com/r/Dog";
		}
        String webpage = getHTTPQuery(url);
        String[] entries = webpage.split("<img alt=\"\" src=\"//");
        
        if (entries.length <= 1)
            return null;
        
        String[] files = new String[entries.length-1];
        for (int i=0;i<files.length;i++)
        {
        	files[i] = "http://" + entries[i+1].split("\" />")[0];
        	files[i] = files[i].replaceAll("b.jpg", ".jpg");
			files[i] = files[i].replaceAll("b.gif", ".gif");
//        	System.out.println("#"+i+": "+files[i]);
        }
        
        
        int rr = rng.nextInt(files.length);
        return files[rr];
    }
	
	public static String getRImage(String tag)
    {
		String url = "http://imgur.com/r/"+tag;
        String webpage = getHTTPQuery(url);
        String[] entries = webpage.split("<img alt=\"\" src=\"//");
        
        if (entries.length <= 1)
            return null;
        
        String[] files = new String[entries.length-1];
        for (int i=0;i<files.length;i++)
        {
        	files[i] = "http://" + entries[i+1].split("\" />")[0];
        	files[i] = files[i].replaceAll("b.jpg", ".jpg");
			files[i] = files[i].replaceAll("b.gif", ".gif");
//        	System.out.println("#"+i+": "+files[i]);
        }
        
        int rr = rng.nextInt(files.length);
        return files[rr];
    }
	
	public static GameInfo getMobyPs1Game(String name)
	{
		GameInfo game = new GameInfo();
		name = name.replaceAll(" ", "+");
		String ps1URL = "http://www.mobygames.com/search/quick?q="+name+"&p=6&search=Go&sFilter=1&sG=on";
		String page = "";
		try{
			page = getHTTPQuery(ps1URL);
		}catch (Exception e){
			e.printStackTrace();
		}
		
		String focus = "";
		focus = page.split("<div class=\"searchResult\">")[1];
		focus = focus.split("<div class=\"searchResult\">")[0];
		
		String e = focus.split("src=\"")[1];
		e = e.split(".jpg\"")[0];
		e = e + ".jpg";
		e = "http://www.mobygames.com" + e;
		game.coverurl = e.replaceAll("/t/", "/l/");
		
		String n = focus.split("Game: <a href")[1];
		n = n.split(">")[1];
		n = n.split("<")[0];
		game.name = n;
		
		String d = focus.split("white-space: nowrap\">")[1];
		d = d.split("</span>")[0];
		d = d.replaceAll("<em>", "").replaceAll("</em>", "");
		game.releaseDate = d;
		
		return game;
	}
	
	public static String getYouTubeID(String query)
    {
		String url = "https://www.youtube.com/results?search_query="+query;
        String webpage = getHTTPQuery(url);
        System.out.println(webpage);
        String[] entries = webpage.split("href=\"/watch?v=");
        
        if (entries.length <= 1)
            return null;
        
        return entries[1].split("\" class=\"yt-uix-sessionli")[0];
    }
	
	public static String getESASchedule(String searchStr) 
	{
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.HOUR, -2);
//		cal.add(Calendar.HOUR, 80);
		Date now = cal.getTime();
//		System.out.println("Now: "+now);
		
		String url = "http://www.esamarathon.com/schedule";
		String page = "";
		try{
			page = getHTTPQuery(url);
		}catch (Exception e){
			e.printStackTrace();
		}
		
		String[] preEntries = page.split("<tr class=\"border-top\" title=\"");
		String[] entries = new String[preEntries.length-1];
		
		for (int i=0;i<entries.length;i++) //drop index 0
		{
			entries[i] = preEntries[i+1];
			
			
		}
		preEntries = null;
		
		Speedrun[] run = new Speedrun[entries.length];
		
		Speedrun prevRun = null;
		Speedrun currentRun = null;
		Speedrun nextRun = null;
		String[] search = null;
		
		if (searchStr != null)
		{
			searchStr = searchStr.toLowerCase();
			searchStr = searchStr.substring(0, searchStr.length()-1);
			search = searchStr.split(" ");
		}
		
		for (int i=0;i<entries.length;i++) 
		{
//			entries[i] = entries[i].replaceAll("&#039;", "'").replaceAll("&amp;", "&");
			
			run[i] = new Speedrun();
			String timeStr = entries[i].split("datetime=\"")[1];
			timeStr = timeStr.split("Z\">")[0];
			timeStr = timeStr.replaceAll("T", " ");
			try	{
				run[i].time = simpleDateFormat.parse(timeStr);
			}catch (ParseException e){e.printStackTrace();}
			
			
//			System.out.println(entries[i]);
			if (entries[i].contains("Run Invalid"))
			{
				run[i].game		= "Run Invalid (Routing Competition)";
				run[i].runner	= "Cobras vs. Hawks";
				run[i].runName	= "Segmented";
				run[i].length	= "0:40";
			}
			else
			{
				run[i].game		= entries[i].split("www.speedrun.com/")[1].split("\">")[1].split("</a>")[0];
				run[i].runner	= entries[i].split("blackoutline username")[1].split(">")[1].split("</span>")[0].replaceAll("</span", "");
				run[i].runName	= entries[i].split("<span class=\"helplink\">")[1].split("</span>")[0];
				run[i].length	= entries[i].split("Length: ")[1].split("; Platform")[0];
			}
			
			if (search != null)
			{
				for (int j=0;j<search.length;j++)
				{
					String s = search[j];
					if (run[i].game.toLowerCase().contains(s))
					{
						if (j == search.length-1)
						{
							long timeTil = getDateDiff(now, run[i].time, TimeUnit.MINUTES);
							int hours = (int)(timeTil / 60.0f);
							String minutes = ""+(int)(timeTil % 60.0f);
							if (minutes.length() < 2) minutes = "0"+minutes;
							return "**"+run[i].game+"** : "+run[i].runName+"      by      "+run[i].runner+"\nis in **"+hours+":"+minutes+" hours"+"**     Estimated length: "+run[i].length;
						}
						else
						{
							continue;
						}
					}
					else
					{
						break;
					}
				}
				
//				System.out.println("Comparing '"+search + "' to '" + run[i].game.toLowerCase()+"'");
			}
			else
			{
				if (run[i].time.after(now))
				{
					if (nextRun == null)
					{
						nextRun = run[i];
						if (currentRun == null && i >= 1) currentRun = run[i-1];
						if (prevRun == null && i >= 2) prevRun = run[i-2];
						
						String out = "";
						if (prevRun != null) 	out = out + "Previous run:  "+prevRun.game+" : "+prevRun.runName+"   by "+prevRun.runner+"\n\n";
						if (currentRun != null) out = out + "NOW:   **"+currentRun.game+"** : "+currentRun.runName+"   by "+currentRun.runner+" ,   Estimate: "+currentRun.length+"\n\n";
						if (nextRun != null) 	out = out + "Next run coming up in ~ "+getDateDiff(now, nextRun.time, TimeUnit.MINUTES)+" minutes:  \n**"+nextRun.game+"** : "+nextRun.runName+"   by "+nextRun.runner+" ,   Estimate: "+nextRun.length;
						
						return out;
					}
				}
			}
		}
		
		return null;
	}
	
	static class Speedrun
	{
		String runner;
		String game;
		String runName; //like any%
		String length; 
		Date time;
	}
	
	/**
	 * Get a diff between two dates
	 * @param date1 the oldest date
	 * @param date2 the newest date
	 * @param timeUnit the unit in which you want the diff
	 * @return the diff value, in the provided unit
	 */
	public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
	    long diffInMillies = date2.getTime() - date1.getTime();
	    return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
	}
	
	public static GameInfo getRandomGame()
	{
//		String str = "";
//		try{
//			str = getHTTPQuery("https://www.randomlists.com/random-video-games?qty=1");
//		}catch (Exception e){
//			e.printStackTrace();
//		}
		
		int page_number = 1;
		String ps1URL = "http://www.giantbomb.com/games/?platform=22&region=&fromYear=2/24/1970&toYear=7/03/2007&page=";
		int max_pages = 1;
		
		String page = "";
		try{
			page = getHTTPQuery(ps1URL + page_number);
		}catch (Exception e){
			e.printStackTrace();
		}
		
//		System.out.println(page);
		
		String focus = "";
		focus = page.split("paginate__ellipse\">")[1];
		focus = focus.split("<li class=\"paginate__item skip next\">")[0];
		focus = focus.split("class=\"btn\">")[1];
		focus = focus.split("</a>")[0];
		max_pages = Integer.parseInt(focus);
		page_number = rng.nextInt(max_pages)+1;
		
		try{
			page = getHTTPQuery(ps1URL + page_number);
		}catch (Exception e){
			e.printStackTrace();
		}
		
		focus = page.split("<ul class=\"editorial")[1].split("<div class=\"js-taboola-mod")[0];
		String allgames = focus;
		String games[] = allgames.split("<li>");
		
		String[] blacklist = {"nfl","nba","fifa","golf","basketball","baseball","mlb","mlb"};
		
		int gameIndex = 1;
		String name = "";
		String g = null;
		
		do{
			gameIndex = rng.nextInt(games.length-1)+1;
			g = games[gameIndex];
			name = g.split("<h3 class=\"title\">")[1].split("</h3")[0];
		}
		while (violatesBlacklist(name, blacklist));
		
		System.out.println();
		
		GameInfo game = new GameInfo();
		game.name = name;
		game.coverurl = g.split("<div class=\"img imgboxart\"><img src=\"")[1].split("\" alt=")[0];
		game.description = g.split("<p class=\"deck\">")[1].split("</p>")[0].replaceAll("&#039;", "'").replaceAll("&quot;", "'");
		game.releaseDate = g.split("<time class=\"date\">")[1].split("</time>")[0].trim().replaceAll("<span class=\"flag ", "").replaceAll("\">", "").replaceAll("</span>", "")
				.replaceFirst("                                      ", "").replaceFirst("us", "").replaceFirst("jp", "").replaceFirst("uk", "");
		game.youtube = "http://www.youtube.com/watch?v="+YTAPI.searchYoutube(game.name+" gameplay").id+"&t=45";
		
		return game;
	}
	
	private static boolean violatesBlacklist(String source, String[] list)
	{
		source = source.toLowerCase();
		for (String b : list)
		{
			if (source.contains(b))
			{
				System.out.println(source + " skipped.");
				return true;
			}
		}
		return false;
	}
	
	static class GameInfo
	{
		String name;
		String coverurl;
		String description;
		String releaseDate;
		String youtube;
	}
	
	public static String getFortune()
	{
		String str = "";
		try{
			str = getHTTPQuery("http://www.fortunecookiemessage.com/");
		}catch (Exception e){
			e.printStackTrace();
		}
		
		String[] split = str.split("class=\"cookie-link\">");
		str = split[1];
		split = str.split("</a>");
		return split[0].replaceAll("<p>", "").replaceAll("</p>", "");
	}
	
	public static String getCurrencyExchange(String in_amount, String in_currency, String out_currency)
	{
//		String in_currency = "CAD";
//		String out_currency = "EUR";
//		String in_amount = "1200";
		
		String o = PageLinks.getHTTPQuery("http://www.xe.com/currencyconverter/convert/?Amount="+in_amount+"&From="+in_currency+"&To="+out_currency);
		o = o.split("<td width=\"47%\" align=\"left\" class=\"rightCol\">")[1].split("&nbsp;<span")[0];
		
		if (o.equals("0.00"))
		{
			o = "Wrong currency.";
		}
		else
		{
			o = in_amount+" "+in_currency+" is "+o+" "+out_currency+".";
		}
		
		return o;
	}
	
	public static String getHTTPQuery(String url)
	{
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(url);
		request.setHeader("User-Agent", "Discord-Bot AIGIS");
		request.setHeader("From", "patrikschulze07@gmail.com");

		// add request header
//		request.addHeader("User-Agent", USER_AGENT);
		try{
			HttpResponse response = client.execute(request);
	
			System.out.println("Response Code : " + response.getStatusLine().getStatusCode());
	
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
	
			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			
			return result.toString();
		}catch(Exception e){ e.printStackTrace(); System.out.println("ERROR HTTP"); }
		return null;
	}
}
