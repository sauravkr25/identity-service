package com.shareride.identity.utils;

import java.util.UUID;

public class PrincipalUtil {

    private PrincipalUtil() {}

    public static boolean isUserPrincipal(String principalName) {
        return principalName != null && principalName.startsWith("user:");
    }

    public static UUID extractUserId(String principalName) {
        return UUID.fromString(principalName.substring(5));
    }
}
