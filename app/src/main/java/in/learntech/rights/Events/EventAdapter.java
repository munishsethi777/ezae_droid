package in.learntech.rights.Events;

import android.content.Context;
import android.content.Intent;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import in.learntech.rights.Chatroom.ChatRoomChatActivity;
import in.learntech.rights.Chatroom.ChatRoomModel;
import in.learntech.rights.Events.domain.Event;
import in.learntech.rights.R;
/**
 * Created by baljeetgaheer on 23/11/17.
 */

public class EventAdapter  extends BaseAdapter implements ListAdapter {


        private List<Event> list = new ArrayList<>();
        private Context context;
        private ViewGroup mParent;


        public EventAdapter(List<Event> list, Context context) {
            this.list = list;
            this.context = context;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int pos) {
            return list.get(pos);
        }

        @Override
        public long getItemId(int pos) {
            //return list.get(pos).getId();
            return 0;
            //just return 0 if your list items do not have an Id variable.
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            mParent = parent;
            View view = convertView;
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.event_detail, null);
            }

            //Handle TextView and display string from your list
            TextView listItemText = (TextView)view.findViewById(R.id.text1);
            final Event event = list.get(0);
            listItemText.setText(event.getData().toString());

            //Handle buttons and add onClickListeners
            Button deleteBtn = (Button)view.findViewById(R.id.delete_btn);

            deleteBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                   viewEvent(event);
                }
            });
            return view;
        }

        public void viewEvent(Event event){
            if(event.getEventType().equals("classroom")){
                Intent classroomActivity = new Intent(mParent.getContext(),ClassroomActivity.class);
                classroomActivity.putExtra("event",event);
                mParent.getContext().startActivity(classroomActivity);
            }else{
                Intent messageChatActivity = new Intent(mParent.getContext(),ChatRoomChatActivity.class);
                ChatRoomModel chatRoomModel = new ChatRoomModel(event.getSeq(),event.getTitle(),event.getImageUrl());
                messageChatActivity.putExtra("messageModel",chatRoomModel);
                mParent.getContext().startActivity(messageChatActivity);
            }

           // mParent.getContext().overridePendingTransition(R.anim.firstactivity_enter, R.anim.firstactivity_exit);
        }
    }

