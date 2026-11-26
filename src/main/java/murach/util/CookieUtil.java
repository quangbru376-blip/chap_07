package murach.util;

import jakarta.servlet.http.*;

public class CookieUtil {

    public static String getCookieValue(
            Cookie[] cookies, String cookieName) {

        String cookieValue = "";
        if (cookies != null && cookieName != null) {
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    cookieValue = cookie.getValue();
                    break;
                }
            }
        }
        return cookieValue;
    }
}
