package app.com.rajneesh.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import app.com.rajneesh.R;
import app.com.rajneesh.interfaces.OnItemClickListener;
import app.com.rajneesh.models.YoutubeDataModel;


public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.YoutubePostHolder> {

    private ArrayList<YoutubeDataModel> mDataSet;
    private Context mContext ;
    private final OnItemClickListener mListener;


    public VideoListAdapter(Context mContext, ArrayList<YoutubeDataModel> dataSet, OnItemClickListener listener) {
        this.mDataSet = dataSet;
        this.mContext = mContext;
        this.mListener = listener;

    }

    @Override
    public YoutubePostHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.youtube_post_layout,parent,false);
        YoutubePostHolder mPostHolder = new YoutubePostHolder(view);
        return mPostHolder;
    }

    @Override
    public void onBindViewHolder(YoutubePostHolder holder, int position) {

        //set the values to views
        TextView textViewTitle = holder.textViewTitle;
        TextView textViewDes = holder.textViewDes;
        TextView textViewDate = holder.textViewDate;
        ImageView ImageThumb = holder.ImageThumb;

        YoutubeDataModel object = mDataSet.get(position);

        textViewTitle.setText(object.getTitle());
        textViewDes.setText(object.getDescription());
        textViewDate.setText(object.getPublishedAt());
        holder.bind(mDataSet.get(position), mListener);

        // Download the photos from the url
        Picasso.with(mContext).load(object.getThumbnail()).into(ImageThumb);

    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public static class YoutubePostHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        TextView textViewDes;
        TextView textViewDate;
        ImageView ImageThumb;

        public YoutubePostHolder(View itemView) {
            super(itemView);
            this.textViewTitle = itemView.findViewById(R.id.textViewTitle);
            this.textViewDes = itemView.findViewById(R.id.textViewDes);
            this.textViewDate = itemView.findViewById(R.id.textViewDate);
            this.ImageThumb = itemView.findViewById(R.id.ImageThumb);

        }

        public void bind(final YoutubeDataModel item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}
