package com.rexhomes.dmn.server.handlers;

import java.io.IOException;
import java.util.logging.*;

import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import com.rexhomes.dmn.server.Server;
import com.rexhomes.dmn.server.HttpUtils;

public class VersionHandler implements WebHandler {
    private static final Logger logger = Logger.getLogger(VersionHandler.class.getName());
    private Server server;

    public VersionHandler(Server srv) {
        server = srv;
        server.getHttpServer().createContext("/version", this);
    }

    @Override
    public void handle(HttpExchange t) throws IOException {
        JSONObject json = new JSONObject()
                        .put("response", new JSONObject()
                            .put("title", "DMN Evaluator Engine")
                            .put("version", "0.0")
                            .put("host", server.getHost())
                            .put("port", server.getPort())
                        );

        HttpUtils.sendJsonResponse(t, json);
    }
    public void setServer(Server srv) { this.server = srv; }
    public Server getServer() { return server; }    
}
