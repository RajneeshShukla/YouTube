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
import app.com.rajneesh.models.YoutubeCommentModel;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.YoutubeCommentHolder> {

    private ArrayList<YoutubeCommentModel> dataSet;
    private static Context mContext = null;

    public CommentAdapter(Context mContext, ArrayList<YoutubeCommentModel> data) {
        this.dataSet = data;
        this.mContext = mContext;
    }

    @Override
    public CommentAdapter.YoutubeCommentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.youtube_comment_layout, parent, false);
        YoutubeCommentHolder mPostHolder = new YoutubeCommentHolder(view);
        return mPostHolder;
    }

    @Override
    public void onBindViewHolder(YoutubeCommentHolder holder, int position) {
        TextView textViewName = holder.textViewName;
        TextView feedback = holder.feedback;
        ImageView imageView = holder.imageViewIcon;
        YoutubeCommentModel object = dataSet.get(position);
        textViewName.setText(object.getTitle());
        feedback.setText(object.getComment());
        try {
            if (object.getThumbnail() != null) {
                if (object.getThumbnail().startsWith("http")) {
                    Picasso.with(mContext)
                            .load(object.getThumbnail())
                            .into(imageView);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class YoutubeCommentHolder extends RecyclerView.ViewHolder {

        TextView textViewName;
        TextView feedback;
        ImageView imageViewIcon;

        public YoutubeCommentHolder(View itemView) {
            super(itemView);
            this.textViewName =  itemView.findViewById(R.id.textViewName);
            this.imageViewIcon = itemView.findViewById(R.id.profile_image);
            this.feedback = itemView.findViewById(R.id.feedback);
        }
    }
}


