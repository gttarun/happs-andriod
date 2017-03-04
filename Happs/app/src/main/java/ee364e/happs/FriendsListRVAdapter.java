package ee364e.happs;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Ali on 3/1/2017.
 */

public class FriendsListRVAdapter extends RecyclerView.Adapter<FriendsListRVAdapter.FriendsListViewHolder> {
    View v;
    FriendsListViewHolder flvh;

    public static class FriendsListViewHolder extends RecyclerView.ViewHolder{
        TextView mName;
        TextView mUserName;
        ImageView mProfilePic;

        FriendsListViewHolder(View itemView){
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.Name);
            mUserName = (TextView) itemView.findViewById(R.id.UserName);
            mProfilePic = (ImageView) itemView.findViewById(R.id.ProfilePic);
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
    private final OnItemClickListener listener;

    FriendsListRVAdapter(List<Profile> profiles, OnItemClickListener listener){
        this.profiles = profiles;
        this.listener = listener;

    }

    @Override
    public int getItemCount(){return profiles.size();}

    @Override
    public FriendsListViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rv_item_row_friendslist, viewGroup, false);
        flvh = new FriendsListViewHolder(v);
        return flvh;
    }

    @Override
    public void onBindViewHolder(FriendsListViewHolder friendsListViewHolder, int i){
        String URL;
        friendsListViewHolder.mName.setText(profiles.get(i).getName());
        friendsListViewHolder.mUserName.setText(profiles.get(i).getUserName());
        friendsListViewHolder.mProfilePic.setImageResource(R.drawable.cat);
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
