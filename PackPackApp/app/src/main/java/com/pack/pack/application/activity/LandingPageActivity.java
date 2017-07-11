package com.pack.pack.application.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import com.pack.pack.application.R;
import com.pack.pack.application.adapters.LandingPageGridAdapter;
import com.pack.pack.application.data.util.ApiConstants;

public class LandingPageActivity extends AppCompatActivity {

    private GridView landing_page_grid;

    private static String[] texts = new String[] {
            "Broadcast",
            "My Favourite",
            "My Family",
            "My Society"
    };
    private static int[] imageIds = new int[] {
            R.drawable.broadcast,
            R.drawable.art_culture,
            R.drawable.my_family,
            R.drawable.smart_society
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

        landing_page_grid = (GridView) findViewById(R.id.landing_page_grid);
        LandingPageGridAdapter adapter = new LandingPageGridAdapter(this, texts, imageIds);
        landing_page_grid.setAdapter(adapter);
        landing_page_grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) { // Open broadcast
                    Intent intent = new Intent(LandingPageActivity.this, BroadcastActivity.class);
                    startActivity(intent);
                } else if (position == 1) { // Open visions of artists
                    Intent intent = new Intent(LandingPageActivity.this, MainActivity.class);
                    startActivity(intent);
                } else if (position == 2) { // Open My Family
                    Intent intent = new Intent(LandingPageActivity.this, GenericTopicListActivity.class);
                    intent.putExtra(GenericTopicListActivity.CATEGORY_TYPE, ApiConstants.FAMILY);
                    startActivity(intent);
                } else if (position == 3) { // Open My Society
                    Intent intent = new Intent(LandingPageActivity.this, GenericTopicListActivity.class);
                    intent.putExtra(GenericTopicListActivity.CATEGORY_TYPE, ApiConstants.SOCIETY);
                    startActivity(intent);
                }
            }
        });
    }
}
