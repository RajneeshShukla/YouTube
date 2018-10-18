package app.com.rajneesh.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import app.com.rajneesh.R;
import app.com.rajneesh.adapters.CommentAdapter;
import app.com.rajneesh.models.YoutubeCommentModel;
import app.com.rajneesh.models.YoutubeDataModel;
import at.huber.youtubeExtractor.YouTubeUriExtractor;
import at.huber.youtubeExtractor.YtFile;

import static app.com.rajneesh.utils.ApplicationConstant.GOOGLE_YOUTUBE_API_KEY;

public class VideoDetailsActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {
    private static final int READ_STORAGE_PERMISSION_REQUEST_CODE = 1;
    private YoutubeDataModel mDataModel;

    private TextView mVideoName;
    private TextView mVideoDes;
    private TextView mVideoDateDate;

    private YouTubePlayerView mYoutubePlayerView;
    private YouTubePlayer mYoutubePlayer;
    private ArrayList<YoutubeCommentModel> mListData = new ArrayList<>();
    private CommentAdapter mAdapter;
    private RecyclerView mListVideos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        mDataModel = getIntent().getParcelableExtra(YoutubeDataModel.class.toString());
        Log.e("", mDataModel.getDescription());

        //Initialize youtube playerView
        mYoutubePlayerView = (YouTubePlayerView) findViewById(R.id.youtube_player);
        mYoutubePlayerView.initialize(GOOGLE_YOUTUBE_API_KEY, this);

        mVideoName = (TextView) findViewById(R.id.textViewName);
        mVideoDes = (TextView) findViewById(R.id.textViewDes);

        mVideoDateDate = (TextView) findViewById(R.id.textViewDate);

        mVideoName.setText(mDataModel.getTitle());
        mVideoDes.setText(mDataModel.getDescription());
        mVideoDateDate.setText(mDataModel.getPublishedAt());

        mListVideos = (RecyclerView) findViewById(R.id.mList_videos);

        // request comment API
        new RequestYoutubeCommentAPI().execute();

        // Request for external storage
        if (!checkPermissionForReadExtertalStorage()) {
            try {
                requestPermissionForReadExternalStorage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    /* Handle event when Youtube Player play video */
    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        youTubePlayer.setPlayerStateChangeListener(mPlayerStateChangeListener);
        youTubePlayer.setPlaybackEventListener(mPlaybackEventListener);
        if (!wasRestored) {
            youTubePlayer.cueVideo(mDataModel.getVideo_id());
        }
        mYoutubePlayer = youTubePlayer;
    }

    /* Handle youtube player event */
    private YouTubePlayer.PlaybackEventListener mPlaybackEventListener = new YouTubePlayer.PlaybackEventListener() {
        @Override
        public void onPlaying() {

        }

        @Override
        public void onPaused() {

        }

        @Override
        public void onStopped() {

        }

        @Override
        public void onBuffering(boolean b) {

        }

        @Override
        public void onSeekTo(int i) {

        }
    };


    /* handle Youtube player state change */
    private YouTubePlayer.PlayerStateChangeListener mPlayerStateChangeListener = new YouTubePlayer.PlayerStateChangeListener() {
        @Override
        public void onLoading() {

        }

        @Override
        public void onLoaded(String s) {

        }

        @Override
        public void onAdStarted() {

        }

        @Override
        public void onVideoStarted() {

        }

        @Override
        public void onVideoEnded() {

        }

        @Override
        public void onError(YouTubePlayer.ErrorReason errorReason) {

        }
    };

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

    }



    /* Download youtube Video*/
    public void downloadVideo(View view) {
        //get the download URL
        String mVideoLink = ("https://www.youtube.com/watch?v=" + mDataModel.getVideo_id());
        YouTubeUriExtractor mExtractor = new YouTubeUriExtractor(this) {
            @Override
            public void onUrisAvailable(String videoID, String videoTitle, SparseArray<YtFile> mVideoFiles) {
                if (mVideoFiles != null) {
                    int itag = 22;

                    String downloadURL = mVideoFiles.get(itag).getUrl();

                    //now download it like a file
                    new RequestDownloadVideoStream().execute(downloadURL, videoTitle);
                }

            }
        };

        mExtractor.execute(mVideoLink);
    }

    // will show the status of video download
    private ProgressDialog mDialog;


    /**
     * Handle video download in background
    * */
    private class RequestDownloadVideoStream extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = new ProgressDialog(VideoDetailsActivity.this);
            mDialog.setMessage("Downloading...");
            mDialog.setIndeterminate(false);
            mDialog.setMax(100);
            mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mDialog.setCancelable(false);
            mDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            InputStream mInputStream = null;
            URL mUrl = null;
            int mLen1 = 0;
            int mTemp_progress = 0;
            int progress = 0;
            try {
                mUrl = new URL(params[0]);
                mInputStream = mUrl.openStream();
                URLConnection mUrlConnection =  mUrl.openConnection();
                mUrlConnection.connect();
                int size = mUrlConnection.getContentLength();

                if (mUrlConnection != null) {
                    String mFileName = params[1] + ".mp4";
                    String mtoragePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/YoutubeVideos";
                    File mfile = new File(mtoragePath);
                    if (!mfile.exists()) {
                        mfile.mkdir();
                    }

                    FileOutputStream mFileOutputStream = new FileOutputStream(mfile + "/" + mFileName);
                    byte[] buffer = new byte[1024];
                    int total = 0;
                    if (mInputStream != null) {
                        while ((mLen1 = mInputStream.read(buffer)) != -1) {
                            total += mLen1;
                            // publishing the progress....
                            // After this onProgressUpdate will be called
                            progress = (int) ((total * 100) / size);
                            if (progress >= 0) {
                                mTemp_progress = progress;
                                publishProgress("" + progress);
                            } else
                                publishProgress("" + mTemp_progress + 1);

                            mFileOutputStream.write(buffer, 0, mLen1);
                        }
                    }

                    if (mFileOutputStream != null) {
                        publishProgress("" + 100);
                        mFileOutputStream.close();
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (mInputStream != null) {
                    try {
                        mInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            mDialog.setProgress(Integer.parseInt(values[0]));
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (mDialog.isShowing())
                mDialog.dismiss();
        }
    }


    /**
     * Youtube Comment API call using asynctask in background
     */

    private class RequestYoutubeCommentAPI extends AsyncTask<Void, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            String VIDEO_COMMENT_URL = "https://www.googleapis.com/youtube/v3/commentThreads?part=snippet&maxResults=100&videoId="
                    + mDataModel.getVideo_id() + "&key=" + GOOGLE_YOUTUBE_API_KEY;
            Log.e("COMMENT_URL", VIDEO_COMMENT_URL);
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(VIDEO_COMMENT_URL);

            try {
                HttpResponse mResponse = httpClient.execute(httpGet);
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

                    //Parse the Json into list
                    mListData = parseJson(jsonObject);
                    // Initialize video List
                    initVideoList(mListData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Display Video list
    public void initVideoList(ArrayList<YoutubeCommentModel> mListData) {
        mListVideos.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new CommentAdapter(this, mListData);
        mListVideos.setAdapter(mAdapter);
    }

    /* Parse the JSON into list */
    public ArrayList<YoutubeCommentModel> parseJson(JSONObject jsonObject) {
        ArrayList<YoutubeCommentModel> mList = new ArrayList<>();

        if (jsonObject.has("items")) {
            try {
                JSONArray jsonArray = jsonObject.getJSONArray("items");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject json = jsonArray.getJSONObject(i);

                    YoutubeCommentModel youtubeObject = new YoutubeCommentModel();
                    JSONObject jsonTopLevelComment = json.getJSONObject("snippet").getJSONObject("topLevelComment");
                    JSONObject jsonSnippet = jsonTopLevelComment.getJSONObject("snippet");

                    String title = jsonSnippet.getString("authorDisplayName");
                    String thumbnail = jsonSnippet.getString("authorProfileImageUrl");
                    String comment = jsonSnippet.getString("textDisplay");

                    youtubeObject.setTitle(title);
                    youtubeObject.setComment(comment);
                    youtubeObject.setThumbnail(thumbnail);
                    mList.add(youtubeObject);


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return mList;

    }

    /* Request for external storage*/
    public void requestPermissionForReadExternalStorage() throws Exception {
        try {
            ActivityCompat.requestPermissions((Activity) this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    READ_STORAGE_PERMISSION_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Check permission to write in external storage
    public boolean checkPermissionForReadExtertalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            int result2 = this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

            return (result == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED);
        }
        return false;
    }
}
