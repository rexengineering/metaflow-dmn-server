package com.rexhomes.dmn.server;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;

public class HttpUtils {
    @SuppressWarnings("unchecked")
    public static Map<String,Object> extractParmsFromQuery(HttpExchange t) throws UnsupportedEncodingException {
        Map<String,Object> parms = new HashMap<>();
        URI reqUri = t.getRequestURI();
        String query = reqUri.getRawQuery();
        if (query != null) {
            String pairs[] = query.split("[&]");

            for (String pair : pairs) {
                String param[] = pair.split("[=]");

                String key = null;
                String value = null;
                if (param.length > 0) {
                    key = URLDecoder.decode(param[0],
                        System.getProperty("file.encoding"));
                }

                if (param.length > 1) {
                    value = URLDecoder.decode(param[1],
                        System.getProperty("file.encoding"));
                }

                if (parms.containsKey(key)) {
                    Object obj = parms.get(key);
                    if(obj instanceof List<?>) {
                        List<Object> values = (List<Object>)obj;
                        values.add(convertToNumber(value));
                    } else if(obj instanceof String) {
                        List<Object> values = new ArrayList<>();
                        values.add(obj);
                        values.add(convertToNumber(value));
                        parms.put(key, values);
                    }
                } else {
                    Object obj = convertToNumber(value);
                    parms.put(key, obj);
                }
            }
        }
        return parms;
    }

    public static Object convertToNumber(Object obj) {
        if (obj instanceof String && NumberUtils.isCreatable((String)obj)) {
            obj = (Object)NumberUtils.createNumber((String)obj);
        }
        return obj;
    }

    public static JSONObject readJsonFromRequest(HttpExchange t) throws IOException {
        InputStreamReader reader = new InputStreamReader(t.getRequestBody(), StandardCharsets.UTF_8);
        String json = new BufferedReader(reader).lines().collect(Collectors.joining("\n"));
        return new JSONObject(json);
    }

    public static JSONArray readJsonArrayFromRequest(HttpExchange t) throws IOException {
        InputStreamReader reader = new InputStreamReader(t.getRequestBody(), StandardCharsets.UTF_8);
        String json = new BufferedReader(reader).lines().collect(Collectors.joining("\n"));
        return new JSONArray(json);
    }

    public static String readTextFromRequest(HttpExchange t) throws IOException {
        InputStreamReader reader = new InputStreamReader(t.getRequestBody(), StandardCharsets.UTF_8);
        return new BufferedReader(reader).lines().collect(Collectors.joining("\n"));
    }

    public static void sendJsonResponse(HttpExchange t, JSONObject json) throws IOException {
        String html = json.toString();
        t.getResponseHeaders()
            .set("Content-Type", "application/json");   
        t.sendResponseHeaders(200, html.length());
        OutputStream os = t.getResponseBody();
        os.write(html.getBytes());
        os.close();
        t.close();
    }

    public static void sendJsonResponse(HttpExchange t, JSONArray json) throws IOException {
        String html = json.toString();
        t.getResponseHeaders()
            .set("Content-Type", "application/json");   
        t.sendResponseHeaders(200, html.length());
        OutputStream os = t.getResponseBody();
        os.write(html.getBytes());
        os.close();
        t.close();
    }

}
