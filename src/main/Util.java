package main;

import java.util.Random;

public class Util
{
	public static Random random = new Random();
	
	public static final int getRandom(int minimum, int maximum)
    {
        return (int)(java.lang.Math.random()*((maximum+1)-minimum)+minimum);
    }
	
	public static final float getRandom(float minimum, float maximum)
    {
        return (float)(java.lang.Math.random()*((maximum+Math.ulp(maximum))-minimum)+minimum);
    }
	
	static private final int BIG_ENOUGH_INT = 16 * 1024;
	static private final double BIG_ENOUGH_ROUND = BIG_ENOUGH_INT + 0.5f;
	static public int myround (float value) {
		return (int)(value + BIG_ENOUGH_ROUND) - BIG_ENOUGH_INT;
	}
	
	public static final int getIntPercentage(int all, int yourValue)
	{
		return (int)myround((float)((100.0d/(double)all)*(double)yourValue));
	}
	
	public static final float getPercent(int all, int yourValue)
	{
		return (float)getIntPercentage(all, yourValue)/100.0f;
	}
	
	
}
