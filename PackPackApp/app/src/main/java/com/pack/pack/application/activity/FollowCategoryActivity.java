package com.pack.pack.application.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.pack.pack.application.AppController;
import com.pack.pack.application.R;
import com.pack.pack.application.data.util.ApiConstants;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.pack.pack.application.data.util.ApiConstants.*;

/**
 * Created by Saurav on 16-08-2016.
 */
public class FollowCategoryActivity extends Activity {

    private Map<Integer, Boolean> map = new HashMap<Integer, Boolean>();

    private LinearLayout layout;
    private AppCompatButton submit_categories;

    private ProgressDialog progressDialog;

    private static final String LOG_TAG = "FollowCategoryActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_categories);

        layout = (LinearLayout) findViewById(R.id.follow_categories_main);
        int i = 0;
        List<String> categories = new ArrayList<String>();
        categories.addAll(Arrays.asList(SUPPORTED_CATEGORIES));
        categories.add("Romance");
        categories.add("Politics");
        categories.add("Sports");
        categories.add("Jokes");
        categories.add("PartyZone");
        for(String category : SUPPORTED_CATEGORIES) {
            LinearLayout linearLayout = new LinearLayout(this);
            //linearLayout.setBackgroundColor(getResources().getColor(R.color.feed_bg));
            linearLayout.setBackground(getResources().getDrawable(R.drawable.border_shadow));
            linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
            linearLayout.setPadding(30, 0, 5, 10);
            LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(
                    TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
            linearLayoutParams.topMargin = 10;
            linearLayoutParams.bottomMargin = 10;
            linearLayoutParams.leftMargin = 80;
            linearLayout.setLayoutParams(linearLayoutParams);

            TextView textView = new TextView(this);
            textView.setText(category);
            textView.setTextSize(20);
            //textView.setTextColor(Color.BLACK);
            textView.setGravity(Gravity.CENTER_VERTICAL);
            textView.setEnabled(true);
            textView.setPadding(20, 2, 10, 2);
            LinearLayout.LayoutParams textViewLayoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            textView.setLayoutParams(textViewLayoutParams);
            linearLayout.addView(textView);

            final ImageButton button = new ImageButton(this);
            LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            buttonParams.gravity = Gravity.CENTER_VERTICAL;
            button.setLayoutParams(buttonParams);
            button.setBackgroundResource(R.drawable.remove);
            button.setTag(new Tag("enabled", textView, i));
            map.put(i, true);
            button.setOnHoverListener(new View.OnHoverListener() {
                @Override
                public boolean onHover(View view, MotionEvent motionEvent) {
                    Tag tag = (Tag) view.getTag();
                    if ("enabled".equals(tag.tag)) {
                        Snackbar.make(view, "Subscribe to Category", Snackbar.LENGTH_SHORT);
                    } else {
                        Snackbar.make(view, "Un-subscribe from Category", Snackbar.LENGTH_SHORT);
                    }
                    return true;
                }
            });
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Tag tag = (Tag) button.getTag();
                    if ("enabled".equals(tag.tag)) {
                        map.put(tag.index, false);
                        tag.textView.setEnabled(false);
                        button.setBackgroundResource(R.drawable.add);
                        button.setTag(new Tag("disabled", tag.textView, tag.index));
                    } else {
                        map.put(tag.index, true);
                        tag.textView.setEnabled(true);
                        button.setBackgroundResource(R.drawable.remove);
                        button.setTag(new Tag("enabled", tag.textView, tag.index));
                    }
                }
            });
            linearLayout.addView(button);
            layout.addView(linearLayout);
            i++;
        }
        submit_categories = (AppCompatButton) findViewById(R.id.submit_categories);
        submit_categories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doEditCategories();
            }
        });
    }

    private void doEditCategories() {
        List<String> categories = new ArrayList<String>();
        Iterator<Map.Entry<Integer, Boolean>> itr = map.entrySet().iterator();
        while(itr.hasNext()) {
            Map.Entry<Integer, Boolean> entry = itr.next();
            if(entry.getValue()) {
                categories.add(SUPPORTED_CATEGORIES[entry.getKey()]);
            }
        }
        String userId = AppController.getInstance().getUserId();
        new EditCategoriesTask().execute(categories);
    }

    private void onCategoriesSignUpSuccess() {
        getIntent().putExtra("loginStatus", true);
        finish();
        startMainActivity();
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void onCategoriesSignUpFailure(String errorMsg) {
        hideProgressDialog();
    }

    private void showProgressDialog() {
        if(progressDialog != null)
            progressDialog.dismiss();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Signing Up...");
        progressDialog.show();
    }

    private void hideProgressDialog() {
        if(progressDialog == null)
            return;
        progressDialog.dismiss();
        progressDialog = null;
    }

    private class Tag {
        String tag;
        TextView textView;
        int index;

        Tag(String tag, TextView textView, int index) {
            this.tag = tag;
            this.textView = textView;
            this.index = index;
        }
    }

    private class EditCategoriesTask extends AsyncTask<List<String>, Void, Void> {

        private String errorMsg;

        @Override
        protected void onPreExecute() {
            showProgressDialog();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(List<String>... categories) {
            if(categories == null || categories.length == 0) {
                errorMsg = "Failed, No categories specified";
                return null;
            }
            List<String> categoriesList = categories[0];
            errorMsg = null;
            String userId = AppController.getInstance().getUserId();
            String oAuthToken = AppController.getInstance().getoAuthToken();
            try {
                API api = APIBuilder.create(ApiConstants.BASE_URL)
                        .setAction(COMMAND.EDIT_USER_CATEGORIES)
                        .setOauthToken(oAuthToken)
                        .addApiParam(APIConstants.User.ID, userId)
                        .addApiParam(APIConstants.TopicCategories.FOLLOWED_CATEGORIES, categoriesList)
                        .build();
                api.execute();
                AppController.getInstance().getFollowedCategories().clear();
                AppController.getInstance().getFollowedCategories().addAll(categoriesList);
            } catch (Exception e) {
                Log.i(LOG_TAG, e.getMessage());
                errorMsg = "ERROR: " + e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            hideProgressDialog();
            if(errorMsg == null) {
                onCategoriesSignUpSuccess();
            }
            else {
                onCategoriesSignUpFailure(errorMsg);
            }
        }
    }
}