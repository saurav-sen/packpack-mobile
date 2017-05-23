package com.pack.pack.application.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

import com.pack.pack.application.Constants;
import com.pack.pack.application.R;
import com.pack.pack.application.data.util.FetchAttachmentStoryTask;
import com.pack.pack.application.data.util.IAsyncTaskStatusListener;
import com.pack.pack.application.view.RTFListener;

import org.apache.commons.lang3.StringEscapeUtils;

public class AttachmentStoryReaderActivity extends AbstractActivity implements IAsyncTaskStatusListener {

    private static final String HTML_START_TEXT = "<html><head>\n" +
            "<body>\n" +
            "<br><div>";

    private static final String HTML_END_TEXT = "</div></body></html>";

    private WebView story_reader_view;

    private String attachmentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attachment_story_reader);

        attachmentId = getIntent().getStringExtra(Constants.ATTACHMENT_ID);

        story_reader_view = (WebView) findViewById(R.id.story_reader_view);
        if(attachmentId != null) {
            new FetchAttachmentStoryTask(this, this).execute(attachmentId);
        }
    }

    @Override
    public void onPreStart(String taskID) {

    }

    @Override
    public void onSuccess(String taskID, Object data) {
        String html = (String) data + "";

        //html = html.replaceAll("&amp;", "&");
        //html = StringEscapeUtils.unescapeHtml4(html);

        //html = HTML_START_TEXT + data + HTML_END_TEXT;
        html = "<br/><br/>" + data;
        story_reader_view.loadDataWithBaseURL("", html, "text/html", "UTF-8", "");
    }

    @Override
    public void onFailure(String taskID, String errorMsg) {

    }

    @Override
    public void onPostComplete(String taskID) {

    }
}
