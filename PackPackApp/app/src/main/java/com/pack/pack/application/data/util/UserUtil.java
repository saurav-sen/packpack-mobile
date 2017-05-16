package com.pack.pack.application.data.util;

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
}
