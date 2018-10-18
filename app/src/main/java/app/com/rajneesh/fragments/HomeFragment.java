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

import app.com.rajneesh.R;
import app.com.rajneesh.activities.VideoDetailsActivity;
import app.com.rajneesh.adapters.VideoListAdapter;
import app.com.rajneesh.interfaces.OnItemClickListener;
import app.com.rajneesh.models.YoutubeDataModel;

import static app.com.rajneesh.utils.ApplicationConstant.CHANNLEL_ViDEOS_GET_URL;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private RecyclerView mListVideos;
    private VideoListAdapter mVideoAdapter;
    private ArrayList<YoutubeDataModel> mListData = new ArrayList<>();

    public HomeFragment() {
        // empty constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mListVideos = view.findViewById(R.id.mList_videos);

        Log.e("HomeFragment", "Fragment is added");
        //Initialize the video List
        initList(mListData);

        new RequestYoutubeAPI().execute();
        return view;
    }

    /* Initialize the video list  */
    private void initList(ArrayList<YoutubeDataModel> mListData) {
        mListVideos.setLayoutManager(new LinearLayoutManager(getActivity()));
        mVideoAdapter = new VideoListAdapter(getActivity(), mListData, new OnItemClickListener() {

            // Open detail activity when a video is clicked
            @Override
            public void onItemClick(YoutubeDataModel myItem) {
                YoutubeDataModel mDataModel = myItem;
                Intent mIntent = new Intent(getActivity(), VideoDetailsActivity.class);
                mIntent.putExtra(YoutubeDataModel.class.toString(), mDataModel);
                startActivity(mIntent);
            }
        });
        mListVideos.setAdapter(mVideoAdapter);
    }

    /**
     * //create an asynctask to get all the data from youtube
     */
    private class RequestYoutubeAPI extends AsyncTask<Void, String, String> {
        @Override
        protected void onPreExecute() {
            Log.e("HomeFragement", "onPreExecute is called");
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(CHANNLEL_ViDEOS_GET_URL);
            Log.e("URL", CHANNLEL_ViDEOS_GET_URL);
            try {
                HttpResponse response = httpClient.execute(httpGet);
                HttpEntity httpEntity = response.getEntity();
                Log.e("HomeFragement", "onPreExecute is called");
                String json = EntityUtils.toString(httpEntity);
                Log.e("JSON", json.toString());

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

                    mListData = parseVideoListFromResponse(jsonObject);
                    initList(mListData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /* Parse json data into list model */
    public ArrayList<YoutubeDataModel> parseVideoListFromResponse(JSONObject jsonObject) {
        ArrayList<YoutubeDataModel> mList = new ArrayList<>();

        if (jsonObject.has("items")) {
            try {
                JSONArray jsonArray = jsonObject.getJSONArray("items");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject json = jsonArray.getJSONObject(i);
                    if (json.has("id")) {
                        JSONObject jsonID = json.getJSONObject("id");
                        String video_id = "";
                        if (jsonID.has("videoId")) {
                            video_id = jsonID.getString("videoId");
                        }
                        if (jsonID.has("kind")) {
                            if (jsonID.getString("kind").equals("youtube#video")) {
                                YoutubeDataModel youtubeObject = new YoutubeDataModel();
                                JSONObject jsonSnippet = json.getJSONObject("snippet");
                                String title = jsonSnippet.getString("title");
                                String description = jsonSnippet.getString("description");
                                String publishedAt = jsonSnippet.getString("publishedAt");
                                String thumbnail = jsonSnippet.getJSONObject("thumbnails").getJSONObject("high").getString("url");

                                youtubeObject.setTitle(title);
                                youtubeObject.setDescription(description);
                                youtubeObject.setPublishedAt(publishedAt);
                                youtubeObject.setThumbnail(thumbnail);
                                youtubeObject.setVideo_id(video_id);
                                mList.add(youtubeObject);
                            }
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
