package com.example.joseph.peggy;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BTControlActivity extends Activity implements OnSharedPreferenceChangeListener{
    // Tag activity for debugging in log
    private static final String TAG = "BTControlActivity";

    // Message types sent from the BluetoothRfcommClient Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothRfcommClient Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the RFCOMM services
    private BluetoothRfcommClient mRfcommClient = null;
    private TextView mTxtStatus;


    // Menu
    private MenuItem mItemConnect;
    private MenuItem mItemOptions;
    private MenuItem mItemAbout;



    //Views with Butter

    @BindView(R.id.btcontrol_connection_button)
    Button connect_button;

    @BindView(R.id.btcontrol_preferences_button)
    Button preferences_button;

    @BindView(R.id.btcontrol_BTconnection_status)
    TextView connection_status;

    @BindView(R.id.btcontrol_deviceID)
    TextView Device_ID;

    @BindView(R.id.btcontrol_switch_steer_mode)
    Switch steer_mode;

    @BindView(R.id.btcontrol_switch_throttle_mode)
    Switch throttle_mode;

    @BindView(R.id.kill_button)
    Button kill_button;

    // JOY
    RelativeLayout layout_joystick;
    //ImageView image_joystick, image_border;
    TextView textView1, textView2, textView3, textView4, textView5;

    JoyStickClass js;

    //Timer
    public Context ctx;
    Timer mUpdateTimer;
    int mTimeoutCounter = 0;
    int mMaxTimeoutCount; // actual timeout = count * updateperiod
    long mUpdatePeriod;

    //BT
/*    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice = null;
    final byte delimiter = 33;
    int readBufferPosition = 0;
    UUID uuid = UUID.fromString("94f39d29-7d6d-437d-973b-fba39e49d4ee"); //Standard SerialPortService ID*/

/*    public void sendBtMsg(String msg2send){
        //UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard SerialPortService ID
        //UUID uuid = UUID.fromString("94f39d29-7d6d-437d-973b-fba39e49d4ee"); //Standard SerialPortService ID

        try {
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            if (!mmSocket.isConnected()){
                mmSocket.connect();
                Log.d(TAG, "connecting socket");
            }
            //msg += "\n";
            OutputStream mmOutputStream = mmSocket.getOutputStream();
            mmOutputStream.write(msg2send.getBytes());
            Log.d(TAG, "Sending BT Message: " + msg2send);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }*/



    private void UpdateMethod() {

        // if either of the joysticks is not on the center, or timeout occurred
        if(!String.valueOf(js.getX()).equals("0") || !String.valueOf(js.getX()).equals("0")) {
                String message = String.valueOf(js.getX()) + " " + String.valueOf(js.getY());
                byte[] send = message.getBytes();
                mRfcommClient.write(send);
        }
        else{

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_btcontrol);

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // If BT is not on, request that it be enabled.
        if (!mBluetoothAdapter.isEnabled()){
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }

        // Initialize the BluetoothRfcommClient to perform bluetooth connections
        mRfcommClient = new BluetoothRfcommClient(this, mHandler);
        mTxtStatus = (TextView) findViewById(R.id.btcontrol_BTconnection_status);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);

        // mUpdatePeriod = prefs.getLong( "updates_interval", 200 ); // in milliseconds
        mUpdatePeriod = Long.parseLong(prefs.getString( "updates_interval", "200" ));
        mMaxTimeoutCount = Integer.parseInt(prefs.getString( "maxtimeout_count", "20" ));
       // mDataFormat = Integer.parseInt(prefs.getString( "data_format", "5" ));

        // fix me: use Runnable class instead
        mUpdateTimer = new Timer();
        mUpdateTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                UpdateMethod();
            }
        }, 2000, mUpdatePeriod);

/*        //Shared Preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);

        // mUpdatePeriod = prefs.getLong( "updates_interval", 200 ); // in milliseconds
        mUpdatePeriod = Long.parseLong(prefs.getString( "updates_interval", "100" ));
        mMaxTimeoutCount = Integer.parseInt(prefs.getString( "maxtimeout_count", "20" ));
        //mDataFormat = Integer.parseInt(prefs.getString( "data_format", "5" ));

        // reschedule task
        mUpdateTimer.cancel();
        mUpdateTimer.purge();
        mUpdatePeriod = Long.parseLong(prefs.getString( "updates_interval", "200" ));
        mUpdateTimer = new Timer();
        mUpdateTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                UpdateMethod();
            }
        }, mUpdatePeriod, mUpdatePeriod);*/

        //Butter
        ButterKnife.bind(this);
        // Toast when moving to new activity
        Toast.makeText(this, "Currently in BTControl Activity.", Toast.LENGTH_SHORT).show();

        // JOY

        textView1 = (TextView)findViewById(R.id.textView1);
        textView2 = (TextView)findViewById(R.id.textView2);
        textView3 = (TextView)findViewById(R.id.textView3);
        textView4 = (TextView)findViewById(R.id.textView4);
        textView5 = (TextView)findViewById(R.id.textView5);

        layout_joystick = (RelativeLayout)findViewById(R.id.layout_joystick);

        js = new JoyStickClass(getApplicationContext(), layout_joystick, R.drawable.image_button);
        js.setStickSize(150, 150);
        js.setLayoutSize(500, 500);
        js.setLayoutAlpha(150);
        js.setStickAlpha(100);
        js.setOffset(90);
        js.setMinimumDistance(1);

        layout_joystick.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
                js.drawStick(arg1);
                if(arg1.getAction() == MotionEvent.ACTION_DOWN
                        || arg1.getAction() == MotionEvent.ACTION_MOVE) {
                    textView1.setText("X : " + String.valueOf(js.getX()));
                    textView2.setText("Y : " + String.valueOf(js.getY()));
                    textView3.setText("Angle : " + String.valueOf(js.getAngle()));
                    textView4.setText("Distance : " + String.valueOf(js.getDistance()));

                    int direction = js.get8Direction();
                    if(direction == JoyStickClass.STICK_UP) {
                        textView5.setText("Direction : Up");
                    } else if(direction == JoyStickClass.STICK_UPRIGHT) {
                        textView5.setText("Direction : Up Right");
                    } else if(direction == JoyStickClass.STICK_RIGHT) {
                        textView5.setText("Direction : Right");
                    } else if(direction == JoyStickClass.STICK_DOWNRIGHT) {
                        textView5.setText("Direction : Down Right");
                    } else if(direction == JoyStickClass.STICK_DOWN) {
                        textView5.setText("Direction : Down");
                    } else if(direction == JoyStickClass.STICK_DOWNLEFT) {
                        textView5.setText("Direction : Down Left");
                    } else if(direction == JoyStickClass.STICK_LEFT) {
                        textView5.setText("Direction : Left");
                    } else if(direction == JoyStickClass.STICK_UPLEFT) {
                        textView5.setText("Direction : Up Left");
                    } else if(direction == JoyStickClass.STICK_NONE) {
                        textView5.setText("Direction : Center");
                    }
                    //sendBtMsg(String.valueOf(js.getX()) + " " + String.valueOf(js.getY()));
                    //sendBtMsg("X : " + String.valueOf(js.getX()) + "Y : " + String.valueOf(js.getY()));
                    //sendBtMsg("Y : " + String.valueOf(js.getY()));
                } else if(arg1.getAction() == MotionEvent.ACTION_UP) {
                    textView1.setText("X :");
                    textView2.setText("Y :");
                    textView3.setText("Angle :");
                    textView4.setText("Distance :");
                    textView5.setText("Direction :");
                    String message = String.valueOf("0 0");
                    byte[] send = message.getBytes();
                    mRfcommClient.write(send);
                } else if(arg1.getAction() == MotionEvent.ACTION_BUTTON_RELEASE){
                        String message = String.valueOf("0 0");
                        byte[] send = message.getBytes();
                        mRfcommClient.write(send);
                }
                return true;
            }
        });



        // Specify behavior of unlock button
        /*final Dialog dialog = new Dialog(this);
        connect_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Execute the custom unlock dialog on clicking
                Log.d(TAG, "Clicking on BT Connect Button");
                dialog.setTitle("Connection Dialog.");
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.btcontrol_dialog);
                final EditText Device_ID_edittext = (EditText) dialog.findViewById(R.id.BTcontrol_dialog_deviceID);

                // Specify behavior of accept button on click
                Button accept_button = (Button) dialog.findViewById(R.id.control_dialog_accept);
                accept_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view1) {
                        Log.d(TAG, "Clicking on Accept Button");
                        final String devicestring = Device_ID_edittext.getText().toString();

                        // BT Connect
                        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

                        if(pairedDevices.size() > 0) {
                            for(BluetoothDevice device : pairedDevices) {
                                if(device.getName().equals(devicestring)) {//Note, you will need to change this to match the name of your device
                                    Log.e("Peggy",device.getName());
                                    mmDevice = device;
                                    Toast.makeText(BTControlActivity.this, "You connected to: " + devicestring, Toast.LENGTH_SHORT).show();
                                    Device_ID.setText("Device ID: " + devicestring);
                                    connection_status.setText("Connected");
                                    break;
                                } else{
                                    Toast.makeText(BTControlActivity.this, "You were unable to connect to the right device", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else{
                            Toast.makeText(BTControlActivity.this, "No paired devices found", Toast.LENGTH_SHORT).show();

                        }

                        dialog.dismiss();
                    }
                });

                // Specify behavior of cancel button on click
                Button cancel_button = (Button) dialog.findViewById(R.id.control_dialog_cancel);
                cancel_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view1) {
                        Log.d(TAG, "Clicking on Cancel Button");
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });*/

    }


/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mItemConnect = menu.add("Connect");
        mItemOptions = menu.add("Options");
        mItemAbout = menu.add("About");
        return (super.onCreateOptionsMenu(menu));

    }*/

/*    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if ( item == mItemConnect ) {
            Intent serverIntent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
        } else if ( item == mItemOptions ) {
            startActivity( new Intent(this, OptionsActivity.class) );
        } else if ( item == mItemAbout ) {
            AlertDialog about = new AlertDialog.Builder(this).create();
            about.setCancelable(false);
            about.setMessage("Peggy Controller");
            about.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            about.show();
        }
        return super.onOptionsItemSelected(item);
    }*/

    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        if ( key.equals("updates_interval") ) {
            // reschedule task
            mUpdateTimer.cancel();
            mUpdateTimer.purge();
            mUpdatePeriod = Long.parseLong(prefs.getString( "updates_interval", "200" ));
            mUpdateTimer = new Timer();
            mUpdateTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    UpdateMethod();
                }
            }, mUpdatePeriod, mUpdatePeriod);
        }else if( key.equals("maxtimeout_count") ){
            mMaxTimeoutCount = Integer.parseInt(prefs.getString( "maxtimeout_count", "20" ));
        }/*else if( key.equals("data_format") ) {
            mDataFormat = Integer.parseInt(prefs.getString("data_format", "5"));
        }else if( key.equals("btnA_data") ){
            mStrA = prefs.getString( "btnA_data", "A" );
        }else if( key.equals("btnB_data") ){
            mStrB = prefs.getString( "btnB_data", "B" );
        }else if( key.equals("btnC_data") ){
            mStrC = prefs.getString( "btnC_data", "C" );
        }else if( key.equals("btnD_data") ){
            mStrD = prefs.getString( "btnD_data", "D" );
        }*/
    }

    @Override
    public synchronized void onResume() {
        super.onResume();

        final Intent serverIntent = new Intent(this, DeviceListActivity.class);
        final Intent OptionsIntent = new Intent(this, OptionsActivity.class);


        if (mRfcommClient != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mRfcommClient.getState() == BluetoothRfcommClient.STATE_NONE) {
                // Start the Bluetooth  RFCOMM services
                mRfcommClient.start();
            }
        }

        preferences_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "clicking preferences button");
                startActivity(serverIntent);
                //startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
            }
        });

        preferences_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "clicking option button");
                startActivity(OptionsIntent);
            }
        });

        // Specify behavior of steer Mode switch
        steer_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // on clicking, the switch flips and the text changes to indicate the new mode
                String current = (String) steer_mode.getText();
                if (steer_mode.isChecked() && (connection_status.getText()!="Connection Status: Not Connected")) {
                    String message = "steer: A";
                    byte[] send = message.getBytes();
                    mRfcommClient.write(send);
                    steer_mode.setText(R.string.BTcontrol_steer_mode_switching_to_autonomous);
                    steer_mode.setTextColor(0xf040028);
                } else {
                    String message = "steer: M";
                    byte[] send = message.getBytes();
                    mRfcommClient.write(send);
                    steer_mode.setText(R.string.BTcontrol_steer_mode_switching_to_manual);
                    steer_mode.setTextColor(0xf040028);
                }
            }
        });

        // Specify behavior of throttle State switch
        throttle_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // on clicking, the switch flips and the text changes to indicate the new state
                if (throttle_mode.isChecked() && (connection_status.getText()!="Connection Status: Not Connected")) {
                    String message = "throttle: A";
                    byte[] send = message.getBytes();
                    mRfcommClient.write(send);
                    throttle_mode.setText(R.string.BTcontrol_throttle_mode_switching_to_autonomous); // <-- this should be changed when confirmed by Peggy;
                    throttle_mode.setTextColor(0xf040028);
                } else {
                    String message = "throttle: M";
                    byte[] send = message.getBytes();
                    mRfcommClient.write(send);
                    throttle_mode.setText(R.string.BTcontrol_throttle_mode_switching_to_manual);
                    throttle_mode.setTextColor(0xf040028);
                }
            }
        });

        // Specify behavior of JoyControl button
        kill_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Execute the custom unlock dialog on clicking
                Log.d(TAG, "Kill Switch Engaged");
                String message = "0 0";
                byte[] send = message.getBytes();
                mRfcommClient.write(send);

            }
        });
    }

    // The Handler that gets information back from the BluetoothRfcommClient
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothRfcommClient.STATE_CONNECTED:
                            mTxtStatus.setText(R.string.title_connected_to);
                            mTxtStatus.append(" " + mConnectedDeviceName);
                            break;
                        case BluetoothRfcommClient.STATE_CONNECTING:
                            mTxtStatus.setText(R.string.title_connecting);
                            break;
                        case BluetoothRfcommClient.STATE_NONE:
                            mTxtStatus.setText(R.string.title_not_connected);
                            break;
                    }
                    break;
                case MESSAGE_READ:
                    // byte[] readBuf = (byte[]) msg.obj;
                    // int data_length = msg.arg1;
                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "Connected to "
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


 /*   @Override
    protected void onResume() {
        super.onResume();
        //final Intent JoyControl = new Intent(this, JoyControlActivity.class);

        //BT
        final Handler handler = new Handler();
        //final TextView myLabel = (TextView) findViewById(R.id.btResult);
        //final Button tempButton = (Button) findViewById(R.id.tempButton);
        //final Button lightOnButton = (Button) findViewById(R.id.lightOn);
        //final Button lightOffButton = (Button) findViewById(R.id.lightOff);
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        final class workerThread implements Runnable {
            private String btMsg;

            public workerThread(String msg) {
                btMsg = msg;
            }
            public void run() {
                sendBtMsg(btMsg);
                while(!Thread.currentThread().isInterrupted()) {
                    int bytesAvailable;
                    boolean workDone = false;
                    try {
                        final InputStream mmInputStream;
                        mmInputStream = mmSocket.getInputStream();
                        bytesAvailable = mmInputStream.available();
                        if(bytesAvailable > 0) {
                            byte[] packetBytes = new byte[bytesAvailable];
                            Log.e("Aquarium recv bt","bytes available");
                            byte[] readBuffer = new byte[1024];
                            mmInputStream.read(packetBytes);
                            for(int i=0;i<bytesAvailable;i++) {
                                byte b = packetBytes[i];
                                if(b == delimiter) {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;
                                    //The variable data now contains our full command
                                    handler.post(new Runnable() {
                                        public void run() {
                                            Log.d(TAG, "Received BT Message: " + data);
                                            //myLabel.setText(data);
                                            if (data.equals("SteerAuto")) {
                                                steer_mode.setText(R.string.BTcontrol_steer_mode_switch_on);
                                                steer_mode.setTextColor(0x7f04002a);
                                            }
                                            if (data.equals("ThrottleAuto")) {
                                                throttle_mode.setText(R.string.BTcontrol_throttle_mode_switch_on);
                                                throttle_mode.setTextColor(0x7f04002a);

                                            }
                                            if (data.equals("SteerManual")) {
                                                steer_mode.setText(R.string.BTcontrol_steer_mode_switch_off);
                                                steer_mode.setTextColor(0x7f04002a);
                                            }
                                            if (data.equals("ThrottleManual")) {
                                                throttle_mode.setText(R.string.BTcontrol_throttle_mode_switch_off);
                                                throttle_mode.setTextColor(0x7f04002a);

                                            }
                                        }

                                    });
                                    workDone = true;
                                    break;
                                }
                                else{
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                            if (workDone == true){
                                mmSocket.close();
                                break;
                            }
                        }
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        };


        if(!mBluetoothAdapter.isEnabled())  {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }*/
/*        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0) {
            for(BluetoothDevice device : pairedDevices) {
                if(device.getName().equals("joecym-desktop #2")) {//Note, you will need to change this to match the name of your device
                    Log.e("Aquarium",device.getName());
                    mmDevice = device;
                    break;
                }
            }
        }*/

        //end BT

        // Declare all the components of the control activity
        /*final Dialog dialog = new Dialog(this);*/

        /*// Specify behavior of unlock button
        connect_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Execute the custom unlock dialog on clicking
                Log.d(TAG, "Clicking on BT Connect Button");
                dialog.setTitle("Connection Dialog.");
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.btcontrol_dialog);
                final EditText Device_ID_edittext = (EditText) dialog.findViewById(R.id.BTcontrol_dialog_deviceID);

                // Specify behavior of accept button on click
                Button accept_button = (Button) dialog.findViewById(R.id.control_dialog_accept);
                accept_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view1) {
                        Log.d(TAG, "Clicking on Accept Button");
                        final String device = Device_ID_edittext.getText().toString();
                        // I hardcoded a string here because it is a unique combination of
                        // a string and an edittext converted to a string
                        Device_ID.setText("Device ID: " + device);
                        Toast.makeText(BTControlActivity.this, "You entered: " + device, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });

                // Specify behavior of cancel button on click
                Button cancel_button = (Button) dialog.findViewById(R.id.control_dialog_cancel);
                cancel_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view1) {
                        Log.d(TAG, "Clicking on Cancel Button");
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });*/

        /*// Specify behavior of steer Mode switch
        steer_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // on clicking, the switch flips and the text changes to indicate the new mode
                String current = (String) steer_mode.getText();
                if (steer_mode.isChecked() && (connection_status.getText()!="Connection Status: Not Connected")) {
                    (new Thread(new workerThread("steer: A"))).start();
                    steer_mode.setText(R.string.BTcontrol_steer_mode_switching_to_autonomous);
                    steer_mode.setTextColor(0xf040028);
                } else {
                    (new Thread(new workerThread("steer: M"))).start();
                    steer_mode.setText(R.string.BTcontrol_steer_mode_switching_to_manual);
                    steer_mode.setTextColor(0xf040028);
                }
            }
        });

        // Specify behavior of Light State switch
        throttle_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // on clicking, the switch flips and the text changes to indicate the new state
                if (throttle_mode.isChecked() && (connection_status.getText()!="Connection Status: Not Connected")) {
                    (new Thread(new workerThread("throttle: A"))).start();
                    throttle_mode.setText(R.string.BTcontrol_throttle_mode_switching_to_autonomous); // <-- this should be changed when confirmed by Peggy;
                    throttle_mode.setTextColor(0xf040028);
                } else {
                    (new Thread(new workerThread("throttle: M"))).start();
                    throttle_mode.setText(R.string.BTcontrol_throttle_mode_switching_to_manual);
                    throttle_mode.setTextColor(0xf040028);
                }
            }
        });

         // Specify behavior of JoyControl button
        kill_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Execute the custom unlock dialog on clicking
                Log.d(TAG, "Kill Switch Engaged");
                sendBtMsg("0 0");

            }
        });


    }*/


}

