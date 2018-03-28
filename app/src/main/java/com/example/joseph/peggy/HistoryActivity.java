package com.example.joseph.peggy;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import java.text.SimpleDateFormat;
import java.util.Date;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;


public class HistoryActivity extends AppCompatActivity {
    // Declare RecyclerAdapter as module variable in order to modify it when using onSwipe()
    RideRecyclerAdapter adapter;

    // Tag for debugging purposes
    private static final String TAG = "HistoryActivity";

    // Declare Realm var
    Realm realm;

    //Butterknife references
    @BindView(R.id.history_add_ride_button)
    Button log_button;

    @BindView(R.id.history_user_image)
    ImageView user_image;

    //BluetoothService bleService;

    @Override
    protected void onStop() {
        super.onStop();
        //LocalBroadcastManager.getInstance(this)
        //        .unregisterReceiver(bleReceiver);
    }

    // Close Realm when finished
    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Toast when entering a new activity
        Toast.makeText(this, "Currently in History Activity.", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Now in History Activity");
        setContentView(R.layout.activity_history);

        //Butter
        ButterKnife.bind(this);
        //Realm
        realm = Realm.getDefaultInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();

        /*// Bluetooth Service Start
        bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder iBinder) {
                bleService = ((BluetoothService.LocalBinder)iBinder).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, Context.BIND_AUTO_CREATE);

        IntentFilter filter = new IntentFilter(BluetoothService.ACTION_DATA_AVAILABLE);
        filter.addAction(BluetoothService.ACTION_GATT_CONNECTED);
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(bleReceiver, filter);*/

        // Get default instance for Realm
        realm = Realm.getDefaultInstance();

        // Define behavior of Image View and select user based on intent (to be implemented)
        realm.beginTransaction();
        Person valid_user = realm.where(Person.class).equalTo("Username", "GKC").findFirst();
        realm.commitTransaction();


        // Load image of current user into the user's ImageView with Picasso
        String str = valid_user.getPicture();
        Log.d(TAG,str);
        Picasso.with(this)
                .load(valid_user.getPicture())
                .fit()
                .transform(new CropCircleTransformation())
                .into(user_image);


        // Setup Recycler View
        RecyclerView history_recycler = (RecyclerView) findViewById(R.id.history_recycler);
        LinearLayoutManager manager = new LinearLayoutManager(this);

        adapter = new RideRecyclerAdapter(this);
        history_recycler.setLayoutManager(manager);
        history_recycler.setAdapter(adapter);

        // Define behavior of EditText for location input
        final EditText ride_edittext_location = (EditText) findViewById(R.id.history_add_rides_edittext_location);

        // Define Behavior of history add button on click
        log_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                Log.d(TAG, "Clicking on Log Button");
                // get location info from user
                String loc = ride_edittext_location.getText().toString();
                // get timestamp from system
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                String currentDateandTime = sdf.format(new Date());
                String date = currentDateandTime;

                // Ensure that the user does not leave the location field blank
                if (loc.equals(""))
                    Toast.makeText(HistoryActivity.this, "You must enter a location", Toast.LENGTH_SHORT).show();
                else {
                    String pic_url;
                    // If user inputs San Francisco, show unique image
                    if (loc.equals("San Francisco")) {
                        pic_url = "http://lh3.ggpht.com/NUCGN0HNoOt2WR7kGz-dVLcDjDui7Ux_LJwU-Pp7NhI-ULH1ALHCoMLak-y0gJyXOXI=h310";
                    } else {
                        pic_url = "http://www.bikestore.cc/images/KTM_Strada3000_2009.jpg";
                    }
                    // Create a new ride, add to Realm, and notify adapter of changes
                    Ride new_ride = new Ride(loc, date, pic_url);
                    realm.beginTransaction();
                    realm.copyToRealm(new_ride);
                    realm.commitTransaction();
                    Toast.makeText(HistoryActivity.this, "You entered a ride", Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                }

            }
        });

        // Create a touchHelper with callback in order to delete an entry on swipe
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.DOWN | ItemTouchHelper.UP) {

            // Overide onMove in order to enable onSwipe
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                Toast.makeText(HistoryActivity.this, "on Move", Toast.LENGTH_SHORT).show();
                return false;
            }
            // Override onSwiped to delete realm entry
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                Toast.makeText(HistoryActivity.this, "on Swiped ", Toast.LENGTH_SHORT).show();
                // Find the ride the user wants to delete, delete it, and then update adapter
                realm.beginTransaction();
                RealmResults<Ride> r = realm.where(Ride.class).findAllSorted("date", Sort.DESCENDING);
                Ride ridetodelete = r.get(viewHolder.getAdapterPosition());
                ridetodelete.deleteFromRealm();
                adapter.notifyDataSetChanged();
                realm.commitTransaction();
            }
        };

        // Lastly, ensure to bind the touchHelper to the history recycler
        ItemTouchHelper helper = new ItemTouchHelper(simpleItemTouchCallback);
        helper.attachToRecyclerView(history_recycler);

    }

/*    public BroadcastReceiver bleReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BluetoothService.ACTION_DATA_AVAILABLE)) {

            }

        }
    };*/

}


