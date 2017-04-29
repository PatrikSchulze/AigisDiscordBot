package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class Files
{
	public static final void writeToFile(String msg, String path)
    {
    	try{
    		BufferedWriter bw = new BufferedWriter(new FileWriter(path));
    		bw.write(msg);
    		bw.close();
    	}catch(Exception e) { e.printStackTrace(); }
    }
	
	public final static String readFromFile(String _f)
    { 
    	try{
    		File file = new File(_f);
    		FileChannel channel = new FileInputStream(file).getChannel(); 
    		ByteBuffer buffer = ByteBuffer.allocate((int) channel.size()); 
    		channel.read(buffer); 
    		channel.close(); 
    		return new String(buffer.array(), "UTF8");
    	}catch(IOException e) { e.printStackTrace(); return null; }
    }
	
}
