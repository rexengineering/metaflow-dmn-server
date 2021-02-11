package com.rexhomes.dmn.server.handlers;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Stream;
import java.util.stream.Collectors;
import java.util.logging.*;

import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import com.rexhomes.dmn.server.Server;
import com.rexhomes.dmn.server.HttpUtils;

public class FeelHandler implements WebHandler {
    private static final Logger logger = Logger.getLogger(FeelHandler.class.getName());
    private Server server;

    public FeelHandler(Server srv) {
        server = srv;
        server.getHttpServer().createContext("/feel", this);
    }

    @Override
    public void handle(HttpExchange t) throws IOException {
        Map<String,Object> parms = HttpUtils.extractParmsFromQuery(t);
        String expr = null;
        Object result;
        if (t.getRequestMethod().equals("POST")) {
            expr = HttpUtils.readTextFromRequest(t);
        } else if(parms.containsKey("expr")) {
            expr = (String)parms.get("expr");
        }
        
        if ( expr != null) {
            result = server.getFeelEvaluator().evalExpression(expr, parms);
        } else {
            result = "No expression provided";
        }

        JSONObject json = new JSONObject().put("result", result);
        HttpUtils.sendJsonResponse(t, json);
    }

    public void setServer(Server srv) { this.server = srv; }
    public Server getServer() { return server; }
}
