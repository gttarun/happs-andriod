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

import java.util.List;

/**
 * Created by Ali on 3/1/2017.
 */

public class FriendListSearchAdapter extends RecyclerView.Adapter<FriendListSearchAdapter.FriendsListViewHolder> {
    View v;
    FriendsListViewHolder flvh;

    public static class FriendsListViewHolder extends RecyclerView.ViewHolder{
        CardView cv;
        ImageView mPicture;
        TextView mUserName;

        FriendsListViewHolder(View itemView){
            super(itemView);
            mPicture = (ImageView) itemView.findViewById(R.id.picture);
            mUserName = (TextView) itemView.findViewById(R.id.userName);
        }

        public void bind(final Profile profile, final OnItemClickListener listener){
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(profile);
                }
            });
        }
    }

    List<Profile> profiles;
    Context context;
    private final OnItemClickListener listener;

    FriendListSearchAdapter(List<Profile> profiles, OnItemClickListener listener, Context context){
        this.profiles = profiles;
        this.listener = listener;
        this.context = context;
    }

    @Override
    public int getItemCount(){return profiles.size();}

    @Override
    public FriendsListViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rv_item_row_friendsearch, viewGroup, false);
        flvh = new FriendsListViewHolder(v);
        return flvh;
    }

    @Override
    public void onBindViewHolder(FriendsListViewHolder friendsListViewHolder, int i){
        if(profiles.get(i).getProfilePic() == null ) {
            friendsListViewHolder.mPicture.setVisibility(View.INVISIBLE);
        } else {
            String url = profiles.get(i).getProfilePic();
            Glide.with(context).load(url).centerCrop().placeholder(R.drawable.happs).crossFade().into(friendsListViewHolder.mPicture);
        }
        friendsListViewHolder.mUserName.setText(profiles.get(i).getUserName());
        friendsListViewHolder.bind(profiles.get(i), listener);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView){
        super.onAttachedToRecyclerView(recyclerView);
    }


    public interface OnItemClickListener{
        void onItemClick(Profile profile);
    }
}
