package main;

import java.io.IOException;
import java.util.List;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;

public class YTAPI
{
	private static YouTube youtube;
	
	static
	{
		youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
            public void initialize(HttpRequest request) throws IOException {
            }
        }).setApplicationName("aigis").build();
	}
	
	public static VideoStats searchYoutube(String inquery)
	{
		try {
            String queryTerm = inquery;
            YouTube.Search.List search = youtube.search().list("id,snippet");

            search.setKey("REDACTED");
            search.setQ(queryTerm);

            search.setType("video");

            search.setFields("items(id/kind,id/videoId,snippet/title)");
            search.setMaxResults(1L);
            
            VideoStats outStats = new VideoStats();

            // Call the API and print results.
            SearchListResponse searchResponse = search.execute();
            if (searchResponse.getItems().size()<1) return null;
            SearchResult r = searchResponse.getItems().get(0);
            outStats.title = r.getSnippet().getTitle();
            outStats.id = r.getId().getVideoId();
       	 
        	YouTube.Videos.List listVideosRequest = youtube.videos().list("contentDetails,snippet").setId(r.getId().getVideoId());
        	listVideosRequest.setKey("REDACTED");
            VideoListResponse listResponse = listVideosRequest.execute();

            Video v = listResponse.getItems().get(0);
//            	System.out.println(v.getSnippet().getChannelTitle());
//            	String dur = v.getContentDetails().getDuration();
//            	System.out.println(dur.substring(2).toLowerCase());
            outStats.duration = v.getContentDetails().getDuration().substring(2).toLowerCase();
            outStats.owner = v.getSnippet().getChannelTitle();	
            
            return outStats;
            
        } catch (GoogleJsonResponseException e) {
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());
        } catch (IOException e) {
            System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }
		
		return null;
	}
	
	public static VideoStats searchYoutube(String inquery, int length)
	{
		try {
            String queryTerm = inquery;
            YouTube.Search.List search = youtube.search().list("id,snippet");
            
            search.setKey("REDACTED");
            search.setQ(queryTerm);
            
            search.setType("video");

            search.setFields("items(id/kind,id/videoId,snippet/title)");
            search.setMaxResults(7L);
            
            VideoStats outStats = new VideoStats();
            int index = 0;

            // Call the API and print results.
            SearchListResponse searchResponse = search.execute();
            if (searchResponse.getItems().size()<1) return null;
            
            List<SearchResult> resultList = searchResponse.getItems();
            
            boolean toobig = false;
            do
            {
            	toobig = false;
            	SearchResult r = resultList.get(index);
                outStats.title = r.getSnippet().getTitle();
                outStats.id = r.getId().getVideoId();
           	 
            	YouTube.Videos.List listVideosRequest = youtube.videos().list("contentDetails,snippet").setId(r.getId().getVideoId());
            	listVideosRequest.setKey("REDACTED");
                VideoListResponse listResponse = listVideosRequest.execute();
            	
            	Video v = listResponse.getItems().get(0);
                outStats.owner = v.getSnippet().getChannelTitle();
                outStats.duration = v.getContentDetails().getDuration().substring(2).toLowerCase();
               
                if (outStats.duration.contains("h"))
                {
                	toobig = true;
                }
                else if (outStats.duration.contains("m"))
                {
                	String m = outStats.duration.split("m")[0];
                	int minutes = Integer.parseInt(m);
                	if (minutes > length)
                	{
                		toobig = true;
                	}
                }
                index++;
            }
            while (index < 7 && toobig);
            
            return outStats;
            
        } catch (GoogleJsonResponseException e) {
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());
        } catch (IOException e) {
            System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }
		
		return null;
	}
	
	public static class VideoStats
	{
		public String title;
		public String duration;
		public String owner;
		public String id;
	}
	
}
