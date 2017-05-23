package com.pack.pack.application.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.pack.pack.application.Constants;
import com.pack.pack.application.R;
import com.pack.pack.application.data.util.EditAttachmentStoryTask;
import com.pack.pack.application.data.util.IAsyncTaskStatusListener;
import com.pack.pack.application.view.RTFEditor;
import com.pack.pack.application.view.RTFListener;

import org.apache.commons.lang3.StringEscapeUtils;

/**
 * Created by Saurav on 30-04-2017.
 */
public class AttachmentStoryEditActivity  extends AbstractActivity implements RTFListener, IAsyncTaskStatusListener {

    private RTFEditor editor;
    private String attachmentStory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attachment_story_edit);

        editor = (RTFEditor) findViewById(R.id.discussion_editor);
        editor.setEscapeHtml(false);
        editor.setOnSaveListener(this);
    }

    @Override
    public void onSave(String rtfText) {
        attachmentStory = rtfText;
        //attachmentStory = StringEscapeUtils.escapeHtml4(rtfText);

        String attachmentId = getIntent().getStringExtra(Constants.ATTACHMENT_ID);

        EditAttachmentStoryTask.EditInfo editInfo = new EditAttachmentStoryTask.EditInfo(attachmentId, attachmentStory);
        EditAttachmentStoryTask editAttachmentStoryTask = new EditAttachmentStoryTask(this);
        editAttachmentStoryTask.execute(editInfo);
    }

    public void done(String storyId) {
        Intent intent = getIntent();
        intent.putExtra(Constants.ATTACHMENT_STORY_ID, storyId);
        this.setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onPreStart(String taskID) {

    }

    @Override
    public void onSuccess(String taskID, Object data) {
        if(data != null && (data instanceof String)) {
            done((String)data);
        }
        Toast.makeText(this, "Successfully created long story", Toast.LENGTH_SHORT);
    }

    @Override
    public void onPostComplete(String taskID) {

    }

    @Override
    public void onFailure(String taskID, String errorMsg) {
        Toast.makeText(this, "Failed to edit long story", Toast.LENGTH_SHORT);
    }
}
