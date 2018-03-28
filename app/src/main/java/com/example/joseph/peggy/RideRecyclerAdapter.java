package com.example.joseph.peggy;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by Joseph on 3/16/2018.
 */

public class RideRecyclerAdapter extends
    RecyclerView.Adapter<RideRecyclerAdapter.ViewHolder> {

    Context ctx;
    // Tag Activity for debugging
    private static final String TAG = "HistoryActivity_Recycler";

    // Pass in the contact array into the constructor
    public RideRecyclerAdapter(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the custom layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ride_recycler, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Ride ride;
        // populate Realm results to display and sort by date
        try(Realm realm = Realm.getDefaultInstance()) {
            RealmResults<Ride> r = realm.where(Ride.class).findAllSorted("date", Sort.DESCENDING);
            ride = r.get(position);
        }

        // populate the row with info form the ride class
        viewHolder.date.setText(ride.getDate());
        viewHolder.location.setText(ride.getLocation());

        Picasso.with(ctx)
                .load(ride.getPic_URL())
                .fit()
                .into(viewHolder.picture);
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        int size;
        try(Realm realm = Realm.getDefaultInstance()) {
            RealmResults<Ride> r = realm.where(Ride.class).findAll();
            size = r.size();
        }
        return size;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        // Butter!
        @BindView(R.id.ride_recycler_date)
            TextView date;
        @BindView(R.id.ride_recycler_location)
            TextView location;
        @BindView(R.id.ride_recycler_pic)
            ImageView picture;

        // bind viewholder to the itemview

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }


}
