package com.bsstudio.counter.status_saver.Activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hbb20.CountryCodePicker;
import com.bsstudio.counter.status_saver.R;


public class Direct_Chat_Activtiy extends AppCompatActivity {

    private CountryCodePicker codePicker;
    private static final String TAG = "Direct_Chat_Activity";

    private EditText userPhoneNumber,message;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(com.bsstudio.counter.status_saver.R.layout.activity_direct_chat_activtiy);

        userPhoneNumber=findViewById(R.id.phone_number);
        message=findViewById(R.id.massage);
        codePicker=findViewById(R.id.country_code);
        button=findViewById(R.id.send);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });


    }

    private void sendMessage() {
        String phoneNumber = userPhoneNumber.getText().toString();
        String messages = message.getText().toString();

        if (TextUtils.isEmpty(phoneNumber)) {
            Toast.makeText(this, "Please enter a phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        // Construct URI
        String uriString = "https://api.whatsapp.com/send?phone=" + phoneNumber + "&text=" + Uri.encode(messages);

        try {
            // Create a URI object
            Uri uri = Uri.parse(uriString);

            // Create an Intent with the ACTION_VIEW action and set the URI
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);

            // Check if there's an activity available to handle this intent
            if (intent.resolveActivity(getPackageManager()) != null) {
                // Start the activity with the intent
                startActivity(intent);
            } else {
                // No activity found to handle the intent
                Toast.makeText(this, "No app found to handle this action", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            // Exception occurred while creating or starting the intent
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private boolean isWhatsAppInstalled() {
        try {
            // Check if WhatsApp is installed by trying to find its package
            getPackageManager().getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
            Intent intent=new Intent(Intent.ACTION_SEND);
                    intent.setPackage("com.whatsapp");
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            // WhatsApp is not installed
            return false;
        }
    }

}