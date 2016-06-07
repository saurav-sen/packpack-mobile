package com.pack.pack.application.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.pack.pack.application.AppController;
import com.pack.pack.application.R;
import com.pack.pack.application.adapters.FullScreenAttachmentViewAdapter;

/**
 *
 * @author Saurav
 *
 */
public class FullscreenAttachmentViewActivity extends Activity {

    private ViewPager fullscreen_attachment_view_pager;
    private FullScreenAttachmentViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_attachment_view);

        fullscreen_attachment_view_pager = (ViewPager) findViewById(R.id.fullscreen_attachment_view_pager);
        adapter = new FullScreenAttachmentViewAdapter(this,
                AppController.getInstance().getPackAttachments());
        fullscreen_attachment_view_pager.setAdapter(adapter);

        int currentIndex = getIntent().getIntExtra("index", 0);
        adapter.setCurrentIndex(currentIndex);
    }
}
