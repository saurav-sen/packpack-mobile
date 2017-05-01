package com.pack.pack.application.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.pack.pack.application.AppController;
import com.pack.pack.application.R;
import com.pack.pack.application.activity.PackDetailActivity;
import com.pack.pack.application.data.cache.InMemory;
import com.pack.pack.application.data.util.AbstractNetworkTask;
import com.pack.pack.application.data.util.DateTimeUtil;
import com.pack.pack.application.db.DBUtil;
import com.pack.pack.application.image.loader.DownloadImageTask;
import com.pack.pack.application.topic.activity.model.ParcelablePack;
import com.pack.pack.application.view.CircleImageView;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.model.web.JPack;
import com.pack.pack.model.web.JPackAttachment;
import com.pack.pack.model.web.JUser;
import com.pack.pack.model.web.Pagination;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Saurav on 02-05-2016.
 */
public class TopicDetailAdapter extends ArrayAdapter<JPack> {

    private LayoutInflater inflator;

    private Activity activity;

    public void setPacks(List<JPack> packs) {
        this.packs = packs;
    }

    private List<JPack> packs;

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int position = (int) view.getTag();
            if(position < 0 || packs == null || position >= packs.size())
                return;
            JPack pack = (JPack) packs.get(position);
            ParcelablePack parcel = new ParcelablePack(pack);
            Intent intent = new Intent(getContext(), PackDetailActivity.class);
            intent.putExtra(AppController.PACK_PARCELABLE_KEY, parcel);
            getContext().startActivity(intent);
        }
    };

    public TopicDetailAdapter(Activity activity, List<JPack> packs) {
        super(activity, R.layout.inside_topic_detail_item);
        this.activity = activity;
        this.packs = packs;
    }

    @Override
    public int getCount() {
        return packs != null ? packs.size() : 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(inflator == null) {
            inflator = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if(convertView == null) {
            convertView = inflator.inflate(R.layout.inside_topic_detail_item, null);
        }
        TextView packTitle = (TextView) convertView.findViewById(R.id.pack_title);
        packTitle.setTag(position);
        packTitle.setOnClickListener(onClickListener);

        CircleImageView pack_creator_picture = (CircleImageView) convertView.findViewById(R.id.pack_creator_picture);
        TextView pack_creator_name = (TextView) convertView.findViewById(R.id.pack_creator_name);
        TextView pack_create_time = (TextView) convertView.findViewById(R.id.pack_create_time);

        TextView packStory = (TextView) convertView.findViewById(R.id.pack_story);
        packStory.setTag(position);
        packStory.setOnClickListener(onClickListener);

        //TextView packStoryContinue = (TextView) convertView.findViewById(R.id.pack_story_continue);

        GridView packAttachmentsGrid = (GridView) convertView.findViewById(R.id.pack_attachments);
        packAttachmentsGrid.setTag(position);
        //packAttachmentsGrid.setOnClickListener(onClickListener);

        if(position < packs.size()) {
            JPack pack = packs.get(position);
            packTitle.setText(pack.getTitle());

            String storyContent = pack.getStory();
            String[] split = storyContent.split("[\n|\r]");
            int storyContentLineCount = split.length;

            if(storyContentLineCount > 3) {
                StringBuilder str = new StringBuilder();
                for(int i=0; i<storyContentLineCount; i++) {
                    String s = split[i];
                    str.append(s);
                    if(s.trim().length() > 0) {
                        str.append("\n");
                    }
                }
                storyContent = str.toString();
            }

            packStory.setText(storyContent);

            //pack.getStory());
           /* if(storyContent.length() > 1000) {
                packStoryContinue.setText(Html.fromHtml("<a href=\\\"#\\\">Continue</a> "));
                packStoryContinue.setTextColor(0x0a80d1);

                stripUnderlines(packStoryContinue);
            }*/
            pack_create_time.setText(DateTimeUtil.sentencify(pack.getCreationTime(),
                    InMemory.INSTANCE.getServerCurrentTimeInMillis()));
            JUser creator = pack.getCreator();
            if(creator != null) {
                if(creator.getName() != null) {
                    pack_creator_name.setText(creator.getName());
                }
                String profilePictureUrl = creator.getProfilePictureUrl();
                pack_creator_picture.setImageResource(R.drawable.default_profile_picture_big);
                if(profilePictureUrl != null && !profilePictureUrl.trim().isEmpty()) {
                    new DownloadImageTask(pack_creator_picture, getContext()).execute(profilePictureUrl);
                }
            }
            new LoadPackAttachmentsTask(packAttachmentsGrid).execute(pack);
        }
        return convertView;
    }

    private void stripUnderlines(TextView textView) {
        Spannable s = new SpannableString(textView.getText());
        URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
        for (URLSpan span: spans) {
            int start = s.getSpanStart(span);
            int end = s.getSpanEnd(span);
            s.removeSpan(span);
            span = new URLSpanNoUnderline(span.getURL());
            s.setSpan(span, start, end, 0);
        }
        textView.setText(s);
    }

    private class URLSpanNoUnderline extends URLSpan {
        public URLSpanNoUnderline(String url) {
            super(url);
        }
        @Override public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }
    }

    private class LoadPackAttachmentsTask extends AbstractNetworkTask<JPack, Integer, List<JPackAttachment>> {

        private GridView packAttachmentsGrid;

        LoadPackAttachmentsTask(GridView packAttachmentsGrid) {
            super(true, true, TopicDetailAdapter.this.activity, false);
            this.packAttachmentsGrid = packAttachmentsGrid;
        }

        @Override
        protected List<JPackAttachment> executeApi(API api) throws Exception {
            Pagination<JPackAttachment> page = (Pagination<JPackAttachment>) api.execute();
            List<JPackAttachment> attachments = null;
            if(page != null) {
                attachments = page.getResult();
            }
            return attachments;
        }

        @Override
        protected String getContainerIdForObjectStore() {
            return getInputObject().getId();
        }

        @Override
        protected COMMAND command() {
            return COMMAND.GET_ALL_ATTACHMENTS_IN_PACK;
        }

        @Override
        protected Map<String, Object> prepareApiParams(JPack jPack) {
            Map<String, Object> apiParams = new HashMap<String, Object>();
            String userId = AppController.getInstance().getUserId();
            apiParams.put(APIConstants.User.ID, userId);
            String packId = jPack.getId();
            apiParams.put(APIConstants.Pack.ID, packId);
            String topicId = jPack.getParentTopicId();
            apiParams.put(APIConstants.Topic.ID, topicId);
            return apiParams;
        }

        @Override
        protected void onPostExecute(List<JPackAttachment> attachments) {
            super.onPostExecute(attachments);
            if(attachments != null && !attachments.isEmpty()) {
                ImageGridAdapter adapter = new ImageGridAdapter(getContext());
                List<String> imageUrls = new ArrayList<String>();
                for(int j=0; j<attachments.size(); j++) {
                    JPackAttachment attachment = attachments.get(j);
                    if("VIDEO".equalsIgnoreCase(attachment.getMimeType())) {
                        imageUrls.add(attachment.getAttachmentThumbnailUrl());
                    } else {
                        imageUrls.add(attachment.getAttachmentUrl());
                    }
                }
                adapter.getImageUrls().addAll(imageUrls);
                packAttachmentsGrid.setAdapter(adapter);
            }
        }

        @Override
        protected String getFailureMessage() {
            return null;
        }

        @Override
        protected List<JPackAttachment> doRetrieveFromDB(SQLiteDatabase readable, JPack inputObject) {
            return DBUtil.loadAllAttachmentInfo(readable, inputObject.getId());
        }
    }
}
