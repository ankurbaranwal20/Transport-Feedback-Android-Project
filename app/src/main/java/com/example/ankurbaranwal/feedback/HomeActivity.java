package com.example.ankurbaranwal.feedback;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    CardView feed,report,about,contact;
    Toolbar tb;

    AdView adView;
    InterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        MobileAds.initialize(HomeActivity.this,"ca-app-pub-9044775629101422~7585002201");
        adView =(AdView)findViewById(R.id.adview);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId("ca-app-pub-9044775629101422/9209158189");
        AdRequest request = new AdRequest.Builder().build();
        interstitialAd.loadAd(request);
        interstitialAd.setAdListener(new AdListener(){
            public void onAdLoaded(){
                if (interstitialAd.isLoaded()) {
                    interstitialAd.show();
                }
            }
        });

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
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.my_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(HomeActivity.this,RegisterActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                Toast.makeText(this, "Thankyou for using our service. ", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
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
