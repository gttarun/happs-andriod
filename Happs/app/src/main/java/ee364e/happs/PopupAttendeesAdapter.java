package ee364e.happs;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by Young on 2017-03-02.
 */

public class PopupAttendeesAdapter  extends RecyclerView.Adapter<PopupAttendeesAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<String> data;
    private final OnItemClickListener listener;

    public PopupAttendeesAdapter(Context context, ArrayList<String> data, OnItemClickListener listener) {
        this.context = context;
        this.data = data;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.popup_attendees, parent,false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.textView.setText(data.get(position));
        holder.bind(data.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    //View Holder
    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView textView;
        public ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.user_name);
        }

        public void bind(final String username, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View view) {
                    listener.onItemClick(username);
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String username);
    }

}
