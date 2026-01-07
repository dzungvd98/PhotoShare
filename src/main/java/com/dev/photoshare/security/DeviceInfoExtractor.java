package com.dev.photoshare.security;

import com.dev.photoshare.dto.request.LoginRequest;
import jakarta.servlet.http.HttpServletRequest;
import ua_parser.Client;
import ua_parser.Parser;

public class DeviceInfoExtractor {

    private static final Parser UA_PARSER = new Parser();

    public static LoginRequest.DeviceInfo extract(HttpServletRequest req) {
        String ua = req.getHeader("User-Agent");

        return LoginRequest.DeviceInfo.builder()
                .userAgent(ua)
                .browser(parseBrowser(ua))
                .operatingSystem(parseOS(ua))
                .deviceType(parseDeviceType(ua))
                .deviceName(buildDeviceName(ua))
                .build();
    }

    private static String parseBrowser(String ua) {
        if (ua == null) return null;
        Client c = UA_PARSER.parse(ua);
        return c.userAgent.family + " " + c.userAgent.major;
    }

    private static String parseOS(String ua) {
        if (ua == null) return null;
        Client c = UA_PARSER.parse(ua);
        return c.os.family + " " + c.os.major;
    }

    private static String parseDeviceType(String ua) {
        if (ua == null) return "UNKNOWN";
        if (ua.contains("Mobile")) return "MOBILE";
        if (ua.contains("Tablet")) return "TABLET";
        return "DESKTOP";
    }

    private static String buildDeviceName(String ua) {
        if (ua == null) return "Unknown device";
        if (ua.contains("Windows")) return "Windows PC";
        if (ua.contains("Macintosh")) return "Mac";
        if (ua.contains("Android")) return "Android device";
        if (ua.contains("iPhone")) return "iPhone";
        return "Unknown device";
    }
}

