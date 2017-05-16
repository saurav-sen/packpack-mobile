package com.pack.pack.application.data.util;

import android.util.Patterns;

import com.pack.pack.model.web.JUser;

/**
 * Created by Saurav on 17-05-2017.
 */
public class UserUtil {

    private UserUtil() {

    }

    public static final String resolveUserDisplayName(JUser user) {
        String displayName = user.getDisplayName();
        if(displayName == null || displayName.trim().isEmpty()) {
            return user.getName();
        }
        return displayName;
    }

    public static final boolean isValidEmailAddressFormat(CharSequence emailAddr) {
        if(emailAddr == null) {
            return false;
        }
        return Patterns.EMAIL_ADDRESS.matcher(emailAddr).matches();
    }
}
