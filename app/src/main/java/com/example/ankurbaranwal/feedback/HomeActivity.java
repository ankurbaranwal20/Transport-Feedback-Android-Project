package com.example.ankurbaranwal.feedback;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    CardView feed,report,about,contact;
    Toolbar tb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        feed =  findViewById(R.id.card_view);
        report =findViewById(R.id.card_view1);
        about = findViewById(R.id.card_view2);
        contact = findViewById(R.id.card_view3);

        tb =findViewById(R.id.tb);
        setSupportActionBar(tb);
        tb.setTitle("Apni Sadak");

        feed.setOnClickListener(this);
        report.setOnClickListener(this);
        about.setOnClickListener(this);
        contact.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.card_view)
        {

            startActivity(new Intent(HomeActivity.this,MainActivity.class));
        }
        else if (v.getId() == R.id.card_view1)
        {
            startActivity(new Intent(HomeActivity.this,MainActivity.class));
        }
        else if(v.getId()== R.id.card_view2)
        {
            startActivity(new Intent(HomeActivity.this,About.class));
        }
        else if (v.getId() == R.id.card_view3)
        {
            startActivity(new Intent(HomeActivity.this,Contact.class));
        }

    }
}
