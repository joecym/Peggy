package com.example.joseph.peggy;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class LoginActivity extends AppCompatActivity {
    // Tag the activity for debugging purposes in the log.
    private static final String TAG = "LoginActivity";

    // Declare Realm var
    Realm realm;

    @BindView(R.id.button_login_text)
    Button button;

    @BindView(R.id.login_user_image)
    ImageView pic;

//    I was unable to get butterknife to work with the dialog
//    @BindView(R.id.dialog_username)
//    EditText username_edittext;

//    @BindView(R.id.dialog_password)
//    EditText password_edittext;

//    @BindView(R.id.login_dialog_button)
//    EditText login_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Butter
        ButterKnife.bind(this);

        // Realm
        realm.init(this);
        realm = Realm.getDefaultInstance();

        // Add people to Person class
/*        Person p1 = new Person("Gilbert", "Chesterton", "GKC", "heretics", "https://cdn.theatlantic.com/assets/media/img/2015/03/CULT_Parker_ChestertonAdel_WEBCrop/lead_large.jpg?1429715750");
        Person p2 = new Person("Bazooka", "Joe", "bazookajoe", "12345", "https://s-media-cache-ak0.pinimg.com/236x/cb/c4/33/cbc433e629b8e6e30f1253914859c73d.jpg");
        Person p3 = new Person("Catherine", "Strother", "CC", "12345", "https://static.vecteezy.com/system/resources/previews/000/034/639/non_2x/droid-vector.jpg");

        // Only send to realm once
        realm.beginTransaction();
        realm.copyToRealm(p1);
        realm.copyToRealm(p2);
        realm.commitTransaction();*/

        // Toast every time the app moves from one activity to another.
        Toast.makeText(this, "Currently in Login Activity.", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Declare final variables needed for the dialog and intent to move to the control activity
        final Dialog dialog = new Dialog(this);
        final Intent Control = new Intent(this, BTControlActivity.class);

        // Define behavior for login button
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Clicking on Login Button");

                // Execute the custom dialog
                dialog.setTitle("Please login.");
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.login_dialog);
                final EditText username_edittext = (EditText) dialog.findViewById(R.id.dialog_username);
                final EditText password_edittext = (EditText) dialog.findViewById(R.id.dialog_password);

                //Define behavior of login button
                Button login_button = (Button) dialog.findViewById(R.id.login_dialog_button);
                login_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view1) {
                        final String user = username_edittext.getText().toString();
                        final String password = password_edittext.getText().toString();
                        //Logic for accessing map of usernames and passwords
                        realm.beginTransaction();
                        Person valid_user = realm.where(Person.class).equalTo("Username", user).findFirst();
                        if (valid_user != null) {
                            Log.d(TAG, "valid user found");
                            Person valid_password = realm.where(Person.class).equalTo("Password",password).findFirst();
                            if (valid_password != null) {
                                // you're in! Go to control activity
                                startActivity(Control);
                                Log.d(TAG, "Access Granted");
                                finish();
                            } else {
                                // incorrect password
                                Log.d(TAG, "Incorrect Password");
                                Toast.makeText(LoginActivity.this, "Incorrect Password. Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Toast.makeText(LoginActivity.this, "Incorrect Username. Please try again.", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Incorrect User");

                        }
                        realm.commitTransaction();
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        // login pic

        Picasso.with(this)
                .load("https://lh3.googleusercontent.com/pyiUd00KSptIRYab92hZcjctuziGWYljeOtM50t3ac8JehZSenWxQMvLSuuYnd8boH7Tq8jik2KjiJ0Q1V9eXQV2zj5J0mlFNTUADSczatyCXrSNtzRfmSTY4HIEeJJVN10mQomkVQ")
                .fit()
                .transform(new CropCircleTransformation())
                .into(pic);
    }
    // Close realm to avoid memory leaks
    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}

