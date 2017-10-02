package in.learntech.rights.Chatroom;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tubb.smrv.SwipeHorizontalMenuLayout;
import com.tubb.smrv.SwipeMenuLayout;
import com.tubb.smrv.listener.SwipeFractionListener;

import java.util.ArrayList;

import in.learntech.rights.R;
import in.learntech.rights.messages.MessageClickListener;
import in.learntech.rights.messages.MessageModel;
import in.learntech.rights.utils.ImageViewCircleTransform;

/**
 * Created by munishsethi on 18/09/17.
 */

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ItemViewHolder> {

    private static ArrayList<ChatRoomModel> dataList;
    private Context context;
    private ChatRoomClickListener clickListener = null;
    public ChatRoomAdapter(Context ctx, ArrayList<ChatRoomModel> data) {
        context = ctx;
        dataList = data;
    }
    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_room_row, parent, false);
        ItemViewHolder itemViewHolder = new ItemViewHolder(view);
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        Glide.with(context)
                .load(dataList.get(position).getImageURL())
                .transform(new ImageViewCircleTransform(context))
                .into(holder.imgProfile);

        boolean swipeEnable = true;
        holder.sml.setSwipeEnable(swipeEnable);
        holder.textName.setText(dataList.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void setClickListener(ChatRoomClickListener listener) {
        this.clickListener = listener;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imgProfile;
        private SwipeHorizontalMenuLayout sml;
        private LinearLayout itemContainer;
        private ImageView buttonDelete;
        private LinearLayout colorMask;
        private TextView textName;

        public ItemViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            imgProfile = (ImageView) itemView.findViewById(R.id.imgProfile);
            sml = (SwipeHorizontalMenuLayout) itemView.findViewById(R.id.sml);
            buttonDelete = (ImageView) itemView.findViewById(R.id.buttonDelete);
            itemContainer = (LinearLayout) itemView.findViewById(R.id.itemContainer);
            colorMask = (LinearLayout) itemView.findViewById(R.id.colorMask);
            textName = (TextView) itemView.findViewById(R.id.textName);
            colorMask.setAlpha(0.0f);
            buttonDelete.setOnClickListener(this);
            imgProfile.setOnClickListener(this);
            itemContainer.setOnClickListener(this);

            sml.setSwipeFractionListener(new SwipeFractionListener() {
                @Override
                public void beginMenuSwipeFraction(SwipeMenuLayout swipeMenuLayout, float fraction) {
                }

                @Override
                public void endMenuSwipeFraction(SwipeMenuLayout swipeMenuLayout, float fraction) {
                    colorMask.setAlpha(fraction);
                }
            });

        }

        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.buttonDelete) {
            }else if(v.getId() == R.id.check){
                clickListener.itemClicked(v,getAdapterPosition());
            }else if (clickListener != null) {
                clickListener.itemClicked(v, getAdapterPosition());
            }
        }

    }

    private void onItemDismiss(int position) {
        dataList.remove(position);
        notifyItemRemoved(position);
    }
}
