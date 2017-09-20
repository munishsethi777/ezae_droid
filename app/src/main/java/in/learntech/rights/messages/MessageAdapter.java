package com.example.munishsethi.myapplication.messages;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.munishsethi.myapplication.R;
import com.example.munishsethi.myapplication.tools.ImageViewCircleTransform;
import com.tubb.smrv.SwipeHorizontalMenuLayout;
import com.tubb.smrv.SwipeMenuLayout;
import com.tubb.smrv.listener.SwipeFractionListener;

import java.util.ArrayList;

/**
 * Created by munishsethi on 18/09/17.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ItemViewHolder> {

    private static ArrayList<MessageModel> dataList;
    private LayoutInflater mInflater;
    private Context context;
    private MessageClickListener clickListener = null;

    public MessageAdapter(Context ctx, ArrayList<MessageModel> data) {
        context = ctx;
        dataList = data;
        mInflater = LayoutInflater.from(context);
    }
    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_row, parent, false);
        ItemViewHolder itemViewHolder = new ItemViewHolder(view);
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        Glide.with(context)
                .load("http://www.ezae.in/" + dataList.get(position).getImageURL())
                .transform(new ImageViewCircleTransform(context))
                .into(holder.imgProfile);

        boolean swipeEnable = true;
        holder.sml.setSwipeEnable(swipeEnable);
        holder.textName.setText(dataList.get(position).getChattingUser());
        holder.message.setText(dataList.get(position).getMessageText());
        holder.timeMesage.setText(dataList.get(position).getDated());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void setClickListener(MessageClickListener listener) {
        this.clickListener = listener;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imgProfile;
        private SwipeHorizontalMenuLayout sml;
        private ImageView buttonDelete;
        private LinearLayout itemContainer;
        private LinearLayout colorMask;
        private TextView textName,message,timeMesage;

        public ItemViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            imgProfile = (ImageView) itemView.findViewById(R.id.imgProfile);
            sml = (SwipeHorizontalMenuLayout) itemView.findViewById(R.id.sml);
            buttonDelete = (ImageView) itemView.findViewById(R.id.buttonDelete);
            itemContainer = (LinearLayout) itemView.findViewById(R.id.itemContainer);
            colorMask = (LinearLayout) itemView.findViewById(R.id.colorMask);
            textName = (TextView) itemView.findViewById(R.id.textName);
            message = (TextView) itemView.findViewById(R.id.message);
            timeMesage = (TextView) itemView.findViewById(R.id.timeMesage);
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
                onItemDismiss(getAdapterPosition());
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
