/*
  Copyright 2014 Magnus Woxblom
  <p/>
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
  <p/>
  http://www.apache.org/licenses/LICENSE-2.0
  <p/>
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */

package in.learntech.rights.utils.sorting;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.woxthebox.draglistview.DragItem;
import com.woxthebox.draglistview.DragListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

import in.learntech.rights.R;
import in.learntech.rights.utils.LayoutHelper;

public class ListFragment extends Fragment {

    private ArrayList<Pair<Long, String>> mItemArray;
    private DragListView mDragListView;
    private MySwipeRefreshLayout mRefreshLayout;
    private static JSONArray mOptions;
    private static boolean mIsShuffle;
    public static ListFragment newInstance(JSONArray options,boolean isShuffle) {
        mOptions = options;
        mIsShuffle = isShuffle;
        return new ListFragment();
    }
    public static ListFragment newInstance() {
        mOptions = null;
        return new ListFragment();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    public ArrayList<Pair<Long, String>>getSortedItemArray(){
        return mItemArray;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_layout, container, false);
        mRefreshLayout = (MySwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        mDragListView = (DragListView) view.findViewById(R.id.drag_list_view);
        mDragListView.getRecyclerView().setVerticalScrollBarEnabled(true);
        mDragListView.setDragListListener(new DragListView.DragListListenerAdapter() {
            @Override
            public void onItemDragStarted(int position) {
                mRefreshLayout.setEnabled(false);
                //Toast.makeText(mDragListView.getContext(), "Start - position: " + position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemDragEnded(int fromPosition, int toPosition) {
                mRefreshLayout.setEnabled(true);
                if (fromPosition != toPosition) {
                    try{
                        mItemArray =  (ArrayList<Pair<Long, String>>) mDragListView.getAdapter().getItemList();
                        JSONObject optionJson = mOptions.getJSONObject(fromPosition);
                        int seq = optionJson.getInt("seq");
                        //Toast.makeText(mDragListView.getContext(), "Start - position: " + seq, Toast.LENGTH_SHORT).show();
                    }catch (Exception e){
                        LayoutHelper.showToast(getActivity(),e.getMessage());
                    }

                }
            }
        });

        mItemArray = new ArrayList<>();
        try {
            if(mIsShuffle) {
                mOptions = shuffleJsonArray(mOptions);
            }
            for (int i = 0; i < mOptions.length(); i++) {
                    JSONObject optionJson = mOptions.getJSONObject(i);
                    int seq = optionJson.getInt("seq");
                    String title = optionJson.getString("title");
                    mItemArray.add(new Pair<>((long) seq, title));
            }
        }catch (Exception e){
                LayoutHelper.showToast(getActivity(),e.getMessage());
        }

        mRefreshLayout.setScrollingView(mDragListView.getRecyclerView());
        mRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.cardview_light_background));
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });

        //mDragListView.setSwipeListener(new ListSwipeHelper.OnSwipeListenerAdapter() {
            //@Override
            //public void onItemSwipeStarted(ListSwipeItem item) {
               // mRefreshLayout.setEnabled(false);
            //}

           // @Override
            //public void onItemSwipeEnded(ListSwipeItem item, ListSwipeItem.SwipeDirection swipedDirection) {
                ///mRefreshLayout.setEnabled(true);

                // Swipe to delete on left
                //if (swipedDirection == ListSwipeItem.SwipeDirection.LEFT) {
                    //Pair<Long, String> adapterItem = (Pair<Long, String>) item.getTag();
                    //int pos = mDragListView.getAdapter().getPositionForItem(adapterItem);
                    //mDragListView.getAdapter().removeItem(pos);
                //}
            //}
       // });

        setupListRecyclerView();
        return view;
    }

//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("List and Grid");
//    }


    public static JSONArray shuffleJsonArray (JSONArray array) throws Exception {
        // Implementing Fisher–Yates shuffle
        Random rnd = new Random();
        for (int i = array.length() - 1; i >= 0; i--)
        {
            int j = rnd.nextInt(i + 1);
            // Simple swap
            Object object = array.get(j);
            array.put(j, array.get(i));
            array.put(i, object);
        }
        return array;
    }

    private void setupListRecyclerView() {
        mDragListView.setLayoutManager(new LinearLayoutManager(getContext()));
        ItemAdapter listAdapter = new ItemAdapter(mItemArray, R.layout.list_item, R.id.image, false);
        mDragListView.setAdapter(listAdapter, true);
        mDragListView.setCanDragHorizontally(false);
        mDragListView.setCustomDragItem(new MyDragItem(getContext(), R.layout.list_item));
    }


    private static class MyDragItem extends DragItem {

        MyDragItem(Context context, int layoutId) {
            super(context, layoutId);
        }

        @Override
        public void onBindDragView(View clickedView, View dragView) {
            CharSequence text = ((TextView) clickedView.findViewById(R.id.text)).getText();
            ((TextView) dragView.findViewById(R.id.text)).setText(text);
            dragView.findViewById(R.id.item_layout).setBackgroundColor(dragView.getResources().getColor(R.color.cardview_light_background));
        }
    }
}
