package com.rexhomes.dmn.server.handlers;

import com.rexhomes.dmn.server.Server;

import com.sun.net.httpserver.HttpHandler;

public interface WebHandler extends HttpHandler {
    public Server getServer();
    public void setServer(Server srv);
}
