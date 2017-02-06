package ee364e.happs;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

/**
 * Created by cykim on 2016-10-31.
 */

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.EventViewHolder>  {

    ImageLoader imageLoader = ImageLoader.getInstance();
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

    RVAdapter(List<Event> events, OnItemClickListener listener){
        this.events = events;
        this.listener = listener;
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
        String URL = "http://teamhapps.herokuapp.com/static/images/" + events.get(i).getName() + events.get(i).getId() + ".jpg";
        eventViewHolder.eventName.setText(events.get(i).getName());
        eventViewHolder.eventUsername.setText("created by " + events.get(i).getUsername());
        imageLoader.displayImage("https://pbs.twimg.com/profile_images/447374371917922304/P4BzupWu.jpeg", eventViewHolder.eventPhoto);
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