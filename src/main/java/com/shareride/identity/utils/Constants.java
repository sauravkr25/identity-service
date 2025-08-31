package com.shareride.identity.utils;

import static com.shareride.identity.utils.Constants.Routes.API_V1;

public class Constants {

    public static final String SUCCESS = "success";
    public static final String FAILURE = "failure";
    public static final String CAUSE = "cause";
    public static final String ROLES = "roles";
    public static final String TOKEN = "token";
    public static final String JWT_EXCEPTION = "JWTException";
    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer";

    public static final class PropertyKeys {
        public static final String ROLE_USER = "${roles.user}";
        public static final String ROLE_ADMIN = "${roles.admin}";
        public static final String ROLE_DRIVER = "${roles.driver}";
        public static final String JWT_SECRET_KEY = "${jwt.secret.key}";
        public static final String JWT_EXPIRATION = "${jwt.expiry.time}";
    }

    public static final class Routes {
        public static final String API_V1 = "/api/v1";
        public static final String AUTH = "/auth";
        public static final String USERS = "/users";
        public static final String REGISTER = "/register";
        public static final String LOGIN = "/login";
        public static final String VERIFY_EMAIL = "/verify-email";
        public static final String ME = "/me";
        public static final String SEND_VERIFICATION_EMAIL = "/send-verification-email";
    }

    public static final class Security {
        public static final String API_V1_AUTH_REGEX = API_V1 + "/auth/**";
        public static final String API_V1_USERS_REGEX = API_V1 + "/users/**";
        public static final String API_V1_ADMIN_REGEX = API_V1 + "/admin/**";
        public static final String ACTUATOR_REGEX = "/actuator/**";
    }

}
