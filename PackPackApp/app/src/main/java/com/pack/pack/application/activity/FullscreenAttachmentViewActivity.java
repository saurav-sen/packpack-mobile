package com.pack.pack.application.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

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

        fullscreen_attachment_view_pager.setCurrentItem(currentIndex);

        ImageButton btnClose = (ImageButton) findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
