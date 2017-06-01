package com.pack.pack.application.data.util;

import android.util.Patterns;

import com.pack.pack.model.web.JUser;

import java.util.regex.Pattern;

/**
 * Created by Saurav on 17-05-2017.
 */
public class UserUtil {

    private static final String PASSWORD_PATTERN_REGEX =
            "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@$]).{6,20})";
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_PATTERN_REGEX);

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

    public static final String applyPasswordPolicy(String passwd) {
        if(PASSWORD_PATTERN.matcher(passwd).matches()) {
            return null;
        }
        return "Password needs 6 to 20 length,at least one digit, upper case,one lower case,one of @,$";
    }
}
