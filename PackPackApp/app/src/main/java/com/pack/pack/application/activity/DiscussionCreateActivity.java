package com.pack.pack.application.activity;

import android.app.Activity;
import android.os.Bundle;

import com.pack.pack.application.R;
import com.pack.pack.application.view.RTFEditor;
import com.pack.pack.application.view.RTFListener;

import org.apache.commons.lang3.StringEscapeUtils;

public class DiscussionCreateActivity extends Activity implements RTFListener {

    private RTFEditor editor;
    private String rtfText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_discussion);

        editor = (RTFEditor) findViewById(R.id.discussion_editor);
        editor.setOnSaveListener(this);
    }

    @Override
    public void onSave(String rtfText) {
        rtfText = StringEscapeUtils.escapeHtml4(rtfText);
    }
}
