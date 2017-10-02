package in.learntech.rights.Chatroom;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import in.learntech.rights.R;
import in.learntech.rights.messages.MessageChatModel;
import in.learntech.rights.messages.MessageClickListener;

/**
 * Created by munishsethi on 19/09/17.
 */

public class ChatRoomChatAdapter extends RecyclerView.Adapter<ChatRoomChatAdapter.ItemViewHolder> {
    private static ArrayList<ChatRoomChatModel> dataList;
    private LayoutInflater mInflater;
    private Context context;
    private ChatRoomClickListener clicklistener = null;

    public ChatRoomChatAdapter(Context ctx, ArrayList<ChatRoomChatModel> data) {
        context = ctx;
        dataList = data;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_chat_row, parent, false);
        ItemViewHolder itemViewHolder = new ItemViewHolder(view);
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        if(dataList.get(position).isSend()) {
            holder.sendLayout.setVisibility(View.VISIBLE);
            holder.receiveLayout.setVisibility(View.GONE);
            holder.sendMessage.setText(dataList.get(position).getMessage());
            holder.sendTime.setText(dataList.get(position).getTime());
        }else{
            holder.sendLayout.setVisibility(View.GONE);
            holder.receiveLayout.setVisibility(View.VISIBLE);
            holder.receiveMessage.setText(dataList.get(position).getMessage());
            holder.receiveTime.setText(dataList.get(position).getTime());
            holder.messageUser.setText(dataList.get(position).getUserName());
        }
    }

    @Override
    public int getItemCount() {
       return dataList.size();
    }

    public void setClickListener(ChatRoomClickListener listener) {
        this.clicklistener = listener;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private RelativeLayout receiveLayout;
        private RelativeLayout sendLayout;
        //private CardView receiveImage;
        private TextView receiveMessage;
        private TextView receiveTime;
        private TextView sendMessage;
        private TextView sendTime;
        private TextView messageUser;

        public ItemViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            receiveLayout = (RelativeLayout) itemView.findViewById(R.id.receiveLayout);
            sendLayout = (RelativeLayout) itemView.findViewById(R.id.sendLayout);
            //receiveImage = (CardView) itemView.findViewById(R.id.receiveImage);
            receiveMessage = (TextView) itemView.findViewById(R.id.receiveMessage);
            receiveTime = (TextView) itemView.findViewById(R.id.receiveTime);
            sendMessage = (TextView) itemView.findViewById(R.id.sendMessage);
            sendTime = (TextView) itemView.findViewById(R.id.sendTime);
            messageUser = (TextView) itemView.findViewById(R.id.message_username);
        }

        @Override
        public void onClick(View view) {
            if (clicklistener != null) {
                clicklistener.itemClicked(view, getAdapterPosition());
            }
        }

    }
}
