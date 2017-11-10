package in.learntech.rights.Leaderboard;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import in.learntech.rights.Leaderboard.LeaderBoardFragment.OnListFragmentInteractionListener;

import in.learntech.rights.R;
import in.learntech.rights.utils.ImageViewCircleTransform;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link LeaderboardModel} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class LeaderboardRecyclerViewAdapter extends RecyclerView.Adapter<LeaderboardRecyclerViewAdapter.ViewHolder> {

    private final List<LeaderboardModel> mValues;
    private Context mContext;

    public LeaderboardRecyclerViewAdapter(Context context, List<LeaderboardModel> items) {
        mValues = items;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Glide.with(mContext)
                .load(mValues.get(position).getUserImage())
                .transform(new ImageViewCircleTransform(mContext))
                .into(holder.mImageView);
        holder.mItem = mValues.get(position);
        holder.mUserName.setText(mValues.get(position).getUserName());
        holder.mScore.setText(mValues.get(position).getScore());
        holder.mDated.setText(mValues.get(position).getDateDiff());
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageView mImageView;
        public final TextView mUserName;
        public final TextView mScore;
        public final TextView mDated;
        public LeaderboardModel mItem;

        public ViewHolder(View view) {
            super(view);
            mImageView = (ImageView) view.findViewById(R.id.imageView_leaderboard);
            mUserName = (TextView)view.findViewById(R.id.textview_username);
            mScore = (TextView)view.findViewById(R.id.textview_score);
            mDated = (TextView)view.findViewById(R.id.textview_date);
        }
    }
}
