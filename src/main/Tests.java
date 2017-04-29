package main;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Tests
{

	public static void main(String[] args)
	{
        
//		PageLinks.getSGDQSchedule(null);
		
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.HOUR, -2);
//		cal.add(Calendar.HOUR, 40);
		Date now = cal.getTime();
		System.out.println("Now: "+now);
		
		String timeStr = "2016-07-23T16:00:00Z";
		timeStr = timeStr.replaceAll("T", " ");
		timeStr = timeStr.replaceAll("Z", "");
		System.out.println("Runtime: "+timeStr);
		try
		{
			Date runTime = simpleDateFormat.parse(timeStr);
			
			System.out.println("Runtime after now: "+runTime.after(now));
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		
		System.out.println("\n\n");
		
		PageLinks.getESASchedule(null);
		
		
		
	}
	
	
	
}
