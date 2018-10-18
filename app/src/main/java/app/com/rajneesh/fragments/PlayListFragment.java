package app.com.rajneesh.fragments;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import app.com.rajneesh.activities.VideoDetailsActivity;
import app.com.rajneesh.R;
import app.com.rajneesh.adapters.VideoListAdapter;
import app.com.rajneesh.interfaces.OnItemClickListener;
import app.com.rajneesh.models.YoutubeDataModel;

import static app.com.rajneesh.utils.ApplicationConstant.GOOGLE_YOUTUBE_API_KEY;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlayListFragment extends Fragment {

    private static String PLAYLIST_ID = "UU7V6hW6xqPAiUfataAZZtWA";
    private static String CHANNLE_GET_URL = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&playlistId=" + PLAYLIST_ID + "&maxResults=50&key=" + GOOGLE_YOUTUBE_API_KEY + "";

    private RecyclerView mVideosList ;
    private VideoListAdapter mVideoListdapter ;
    private ArrayList<YoutubeDataModel> mListData = new ArrayList<>();

    public PlayListFragment() {
        // Required empty public constructor
    }

/* Inflate the Playlist fragment */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mPlayListView = inflater.inflate(R.layout.fragment_play_list, container, false);
        mVideosList = mPlayListView.findViewById(R.id.mList_videos);
       //initialize the list
        initList(mListData);
        //request the playlist api
        new RequestYoutubeAPI().execute();
        return mPlayListView;
    }


    /* Initialize the the list of videos */
    private void initList(ArrayList<YoutubeDataModel> mListData) {
        mVideosList.setLayoutManager(new LinearLayoutManager(getActivity()));

        //set Listener when video is clicked
        mVideoListdapter = new VideoListAdapter(getActivity(), mListData, new OnItemClickListener() {
            @Override
            public void onItemClick(YoutubeDataModel item) {
                Intent mIntent = new Intent(getActivity(), VideoDetailsActivity.class);
                mIntent.putExtra(YoutubeDataModel.class.toString(), item);
                startActivity(mIntent);
            }
        });
        mVideosList.setAdapter(mVideoListdapter);

    }


    //create an asynctask to get all the data from youtube
    private class RequestYoutubeAPI extends AsyncTask<Void, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            HttpClient mHttpClient = new DefaultHttpClient();
            HttpGet mHttpGet = new HttpGet(CHANNLE_GET_URL);
            Log.e("URL", CHANNLE_GET_URL);
            try {
                HttpResponse mResponse = mHttpClient.execute(mHttpGet);
                HttpEntity mHttpEntity = mResponse.getEntity();
                String json = EntityUtils.toString(mHttpEntity);
                return json;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if (response != null) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.e("response", jsonObject.toString());
                     //parse the json into video list
                    mListData = parseVideoListFromResponse(jsonObject);
                    //initialize the list
                    initList(mListData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public ArrayList<YoutubeDataModel> parseVideoListFromResponse(JSONObject jsonObject) {
        ArrayList<YoutubeDataModel> mList = new ArrayList<>();

        if (jsonObject.has("items")) {
            try {
                JSONArray mJsonArray = jsonObject.getJSONArray("items");
                for (int i = 0; i < mJsonArray.length(); i++) {
                    JSONObject mJson = mJsonArray.getJSONObject(i);
                    if (mJson.has("kind")) {
                        if (mJson.getString("kind").equals("youtube#playlistItem")) {
                            YoutubeDataModel mYoutubeObject = new YoutubeDataModel();
                            JSONObject jsonSnippet = mJson.getJSONObject("snippet");
                            String mVedioId = "";
                            if (jsonSnippet.has("resourceId")) {
                                JSONObject jsonResource = jsonSnippet.getJSONObject("resourceId");
                                mVedioId = jsonResource.getString("videoId");

                            }
                            String title = jsonSnippet.getString("title");
                            String description = jsonSnippet.getString("description");
                            String publishedAt = jsonSnippet.getString("publishedAt");
                            String thumbnail = jsonSnippet.getJSONObject("thumbnails").getJSONObject("high").getString("url");

                            mYoutubeObject.setTitle(title);
                            mYoutubeObject.setDescription(description);
                            mYoutubeObject.setPublishedAt(publishedAt);
                            mYoutubeObject.setThumbnail(thumbnail);
                            mYoutubeObject.setVideo_id(mVedioId);
                            mList.add(mYoutubeObject);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return mList;

    }


}
