package com.gymtrack.app.utils;

import com.gymtrack.app.R;

public class AvatarHelper {
    public static int getAvatarResource(String avatarName) {
        if (avatarName == null) return R.drawable.ic_person; // Default fallback if needed
        switch (avatarName) {
            case "avatar_1":
                return R.drawable.avatar_1;
            case "avatar_2":
                return R.drawable.avatar_2;
            case "avatar_3":
                return R.drawable.avatar_3;
            default:
                return R.drawable.ic_person; // Fallback para cualquier otro caso
        }
    }
}
