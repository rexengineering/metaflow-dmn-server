package com.rexhomes.dmn.server;

import com.rexhomes.feel.FeelEvaluator;

import org.camunda.bpm.dmn.engine.DmnEngine;

import com.sun.net.httpserver.HttpServer;

public interface Server {
    public HttpServer    getHttpServer();
    public String        getHost();
    public int           getPort();
    public DmnEngine     getDmnEngine();
    public FeelEvaluator getFeelEvaluator();

    public void setHttpServer(HttpServer srv);
    public void setHost(String host);
    public void setPort(int port);
    public void setFeelEvaluator(FeelEvaluator eva);
}