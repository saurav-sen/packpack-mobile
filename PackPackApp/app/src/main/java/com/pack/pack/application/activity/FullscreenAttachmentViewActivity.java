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
import com.pack.pack.application.view.AttachmentViewPager;
import com.pack.pack.model.web.JPackAttachment;
import com.pack.pack.model.web.PackAttachmentType;

import java.util.List;

/**
 *
 * @author Saurav
 *
 */
public class FullscreenAttachmentViewActivity extends Activity {

    private AttachmentViewPager fullscreen_attachment_view_pager;
    private FullScreenAttachmentViewAdapter adapter;

    private int currentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_attachment_view);

        currentIndex = getIntent().getIntExtra("index", 0);

        /*boolean paginationSupported = true;
        FullScreenAttachmentViewAdapter.Mode mode = FullScreenAttachmentViewAdapter.Mode.IMAGE;*/

        List<JPackAttachment> attachments = AppController.getInstance().getPackAttachments();
        /*if(currentIndex < attachments.size()) {
            JPackAttachment attachment = attachments.get(currentIndex);
            if(PackAttachmentType.VIDEO.name().equals(attachment.getMimeType().toUpperCase())) {
                mode = FullScreenAttachmentViewAdapter.Mode.VIDEO;
                paginationSupported = false;
            }
        }*/

        fullscreen_attachment_view_pager = (AttachmentViewPager) findViewById(R.id.fullscreen_attachment_view_pager);
        fullscreen_attachment_view_pager.setPaginationSupported(true);

        adapter = new FullScreenAttachmentViewAdapter(this, attachments);
        fullscreen_attachment_view_pager.setAdapter(adapter);
        fullscreen_attachment_view_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentIndex = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        adapter.setCurrentIndex(currentIndex);

        fullscreen_attachment_view_pager.setCurrentItem(currentIndex);

        Button btnClose = (Button) findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
