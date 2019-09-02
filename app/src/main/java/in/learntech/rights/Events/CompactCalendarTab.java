package in.learntech.rights.Events;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import in.learntech.rights.Chatroom.ChatRoomModel;
import in.learntech.rights.Events.domain.Event;
import in.learntech.rights.Events.lib.CompactCalendarView;
import in.learntech.rights.Managers.UserMgr;
import in.learntech.rights.R;
import in.learntech.rights.services.Interface.IServiceHandler;
import in.learntech.rights.services.ServiceHandler;
import in.learntech.rights.utils.DateUtil;
import in.learntech.rights.utils.StringConstants;

public class CompactCalendarTab extends Fragment implements IServiceHandler {

    private static final String TAG = "EventActivity";
    private Calendar currentCalender = Calendar.getInstance(Locale.getDefault());
    private SimpleDateFormat dateFormatForDisplaying = new SimpleDateFormat("dd-M-yyyy hh:mm:ss a", Locale.getDefault());
    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("MMM - yyyy", Locale.getDefault());
    private CompactCalendarView compactCalendarView;
    private ActionBar toolbar;
    private UserMgr mUserMgr;
    private ServiceHandler mAuthTask;
    private List<Event>rowListItem;
    private List<Event> mutableBookings;
    private EventAdapter adapter;
    public Date directEventDate;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.main_tab,container,false);

        mutableBookings = new ArrayList<>();
        final ListView bookingsListView = (ListView) v.findViewById(R.id.bookings_listview);
        final Button showPreviousMonthBut = (Button) v.findViewById(R.id.prev_button);
        final Button showNextMonthBut = (Button) v.findViewById(R.id.next_button);

        adapter = new EventAdapter(mutableBookings, getContext());
        bookingsListView.setAdapter(adapter);
        compactCalendarView = (CompactCalendarView) v.findViewById(R.id.compactcalendar_view);

        compactCalendarView.setUseThreeLetterAbbreviation(false);
        compactCalendarView.setFirstDayOfWeek(Calendar.MONDAY);
        mUserMgr = UserMgr.getInstance(getContext());
        rowListItem = new  ArrayList<>();
        loadEvents();
        loadEventsForYear(2017);
        compactCalendarView.invalidate();
        logEventsByMonth(compactCalendarView);

        //set initial title
        toolbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        toolbar.setTitle(dateFormatForMonth.format(compactCalendarView.getFirstDayOfCurrentMonth()));

        //set title on calendar scroll
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                addAllEventsByDate(dateClicked);
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                toolbar.setTitle(dateFormatForMonth.format(firstDayOfNewMonth));
            }
        });

        showPreviousMonthBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compactCalendarView.showPreviousMonth();
                Date date = compactCalendarView.getFirstDayOfCurrentMonth();
                addAllEventsByDate(date);
            }
        });



        showNextMonthBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compactCalendarView.showNextMonth();
                Date date = compactCalendarView.getFirstDayOfCurrentMonth();
                addAllEventsByDate(date);
            }
        });

        compactCalendarView.setAnimationListener(new CompactCalendarView.CompactCalendarAnimationListener() {
            @Override
            public void onOpened() {
            }

            @Override
            public void onClosed() {
            }
        });

        return v;
    }

    private void loadDirectEvent(){
        if(directEventDate != null)
        compactCalendarView.showDirectEvent(directEventDate);
        addAllEventsByDate(directEventDate);
    }
    private void addAllEventsByDate(Date date){
        toolbar.setTitle(dateFormatForMonth.format(date));
        List<Event> bookingsFromMap = compactCalendarView.getEvents(date);
        Log.d(TAG, "inside onclick " + dateFormatForDisplaying.format(date));
        if (bookingsFromMap != null) {
            Log.d(TAG, bookingsFromMap.toString());
            mutableBookings.clear();
            mutableBookings.addAll(bookingsFromMap);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        toolbar.setTitle(dateFormatForMonth.format(compactCalendarView.getFirstDayOfCurrentMonth()));
    }

    public void executeGetChatRoomsCall(){
        Object[] args = {mUserMgr.getLoggedInUserSeq(),mUserMgr.getLoggedInUserCompanySeq()};
        String getChatRoomUrl = MessageFormat.format(StringConstants.GET_ALL_EVENTS,args);
        mAuthTask = new ServiceHandler(getChatRoomUrl,this,getActivity());
        mAuthTask.execute();
    }

    private void loadEvents() {
       // addEvents(-1, -1);
       //addEvents(Calendar.DECEMBER, -1);
       // addEvents(Calendar.AUGUST, -1);
        executeGetChatRoomsCall();
    }

    private void loadEventsForYear(int year) {
        //addEvents(Calendar.DECEMBER, year);
        //addEvents(Calendar.AUGUST, year);
    }

    private void logEventsByMonth(CompactCalendarView compactCalendarView) {
        currentCalender.setTime(new Date());
        currentCalender.set(Calendar.DAY_OF_MONTH, 1);
        currentCalender.set(Calendar.MONTH, Calendar.AUGUST);
        List<String> dates = new ArrayList<>();
        for (Event e : compactCalendarView.getEventsForMonth(new Date())) {
            dates.add(dateFormatForDisplaying.format(e.getTimeInMillis()));
        }
        Log.d(TAG, "Events for Aug with simple date formatter: " + dates);
        Log.d(TAG, "Events for Aug month using default local and timezone: " + compactCalendarView.getEventsForMonth(currentCalender.getTime()));
    }





    @Override
    public void processServiceResponse(JSONObject response) {
        mAuthTask = null;
        String message = null;
        try{
            boolean success = response.getInt(StringConstants.SUCCESS) == 1 ? true : false;
            message = response.getString(StringConstants.MESSAGE);
            if(success){
                JSONArray notesJsonArr = response.getJSONArray("chatrooms");
                for (int i=0; i < notesJsonArr.length(); i++) {
                    JSONObject jsonObject = notesJsonArr.getJSONObject(i);
                    int seq = jsonObject.getInt("seq");
                    String title = jsonObject.getString("title");
                    String detail = jsonObject.getString("detail");
                    String imageUrl = StringConstants.WEB_URL + jsonObject.getString("imagepath");
                    String from = jsonObject.getString("from");
                    String to = jsonObject.getString("to");
                    String eventType = jsonObject.getString("eventtype");
                    Date fromDate = DateUtil.stringToDate(from);
                    Date toDate = DateUtil.stringToDate(to);
                    List<Date> difDays = DateUtil.getDaysBetweenDates(fromDate,toDate);
                    String des =  title + "\nFrom - " + DateUtil.dateToFromat(fromDate) + "\nTo - " + DateUtil.dateToFromat(toDate) ;
                    if(difDays.size() > 1) {
                        for (Date d : difDays) {
                            Event mm = new Event(seq,Color.argb(255, 169, 68, 65), d.getTime(), des,eventType,title,imageUrl,detail);
                            rowListItem.add(mm);
                        }
                    }else{
                        Event mm = new Event(seq,Color.argb(255, 169, 68, 65), fromDate.getTime(), des,eventType,title,imageUrl,detail);
                        rowListItem.add(mm);
                    }

                }
                compactCalendarView.addEvents(rowListItem);
                loadDirectEvent();
            }
        }catch (Exception e){
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void setCallName(String call) {

    }
}