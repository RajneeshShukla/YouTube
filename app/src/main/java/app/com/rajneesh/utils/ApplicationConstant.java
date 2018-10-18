package app.com.rajneesh.utils;

public class ApplicationConstant {

    public static String GOOGLE_YOUTUBE_API_KEY = "AIzaSyAVqiCsznkcaoIIYqpphJIsDdbwuZpMFFw";

    // Channel Id of Technical Guruji
    public static String CHANNEL_ID = "UCOhHO2ICt0ti9KAh-QHvttQ";

    // URL fetch the videos of
    public static String CHANNLEL_ViDEOS_GET_URL = "https://www.googleapis.com/youtube/v3/search?part=snippet&order=date&channelId="
            + CHANNEL_ID + "&maxResults=40&key=" + GOOGLE_YOUTUBE_API_KEY + "";

}
