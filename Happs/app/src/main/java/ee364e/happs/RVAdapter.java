package ee364e.happs;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

/**
 * Created by cykim on 2016-10-31.
 */

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.EventViewHolder>  {


    EventViewHolder pvh;
    View v;

    public static class EventViewHolder extends RecyclerView.ViewHolder{
        CardView cv;
        TextView eventName;
        TextView eventUsername;
        ImageView eventPhoto;
        EventViewHolder(View itemView) {
            super(itemView);
            eventName = (TextView)itemView.findViewById(R.id.event_name);
            eventUsername = (TextView)itemView.findViewById(R.id.event_username);
            eventPhoto = (ImageView)itemView.findViewById(R.id.event_photo);
        }

        public void bind(final Event event, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View view) {
                    listener.onItemClick(event);
                }
            });
        }
    }


    List<Event> events;
    private final OnItemClickListener listener;
    Context context;


    RVAdapter(List<Event> events, OnItemClickListener listener, Context context){
        this.events = events;
        this.listener = listener;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.event, viewGroup, false);
        pvh = new EventViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(EventViewHolder eventViewHolder, int i) {
        URI uri = null;
        String URL = "http://teamhapps.herokuapp.com/static/images/" + events.get(i).getName() + events.get(i).getId() + ".jpg";
        eventViewHolder.eventName.setText(events.get(i).getName());
        eventViewHolder.eventUsername.setText("created by " + events.get(i).getUsername());
        Glide.with(context).load("https://pbs.twimg.com/profile_images/447374371917922304/P4BzupWu.jpeg").centerCrop().placeholder(R.drawable.happs).crossFade().into(eventViewHolder.eventPhoto);
        eventViewHolder.bind(events.get(i) , listener);
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public interface OnItemClickListener {
        void onItemClick(Event event);
    }




}