package com.rexhomes.dmn.server;

import com.rexhomes.dmn.server.handlers.*;
import com.rexhomes.feel.FeelEvaluator;

import org.camunda.bpm.dmn.engine.DmnEngineConfiguration;
import org.camunda.bpm.dmn.engine.DmnEngine;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.*;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;

public class ServerImpl implements Server {
    private static final Logger logger = Logger.getLogger(ServerImpl.class.getName());

    private String host;
    private int    port;
    private HttpServer httpServer;
    private DmnEngine dmnEngine;
    private FeelEvaluator feelEval; 
    private List<WebHandler> handlers = new ArrayList<>();

    public ServerImpl() {
        Map<String,String> env = System.getenv();
        host = env.getOrDefault("DMN_HOST", "0.0.0.0");
        port = Integer.parseInt(env.getOrDefault("DMN_PORT", "8001"));

        String msg = String.format("Creating DMN Server on %s:%d", host, port);
        logger.info(msg);

        try {
            httpServer = HttpServer.create(new InetSocketAddress(host, port), 0);
        } catch(Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return;
        }

        handlers.add(new VersionHandler(this));         // \version
        handlers.add(new DecisionTableHandler(this));   // \dmn\decision-table OR \dmn\dt
        handlers.add(new FeelHandler(this));            // \feel

        // start the DMN engine before the web server
        dmnEngine = DmnEngineConfiguration.createDefaultDmnEngineConfiguration().buildEngine();

        feelEval = new FeelEvaluator();

        // start the web server
        httpServer.start();

        msg = String.format("Server started on %s:%d", host, port);
        logger.info(msg);
    }

    public HttpServer    getHttpServer() { return httpServer; }
    public String        getHost() { return host; }
    public int           getPort() { return port; }
    public DmnEngine     getDmnEngine() { return dmnEngine; }
    public FeelEvaluator getFeelEvaluator() { return feelEval; }

    public void setHttpServer(HttpServer srv) { this.httpServer = srv; }
    public void setHost(String host) { this.host = host; }
    public void setPort(int port) { this.port = port;}
    public void setDmnEngine(DmnEngine egn) { this.dmnEngine = egn; }
    public void setFeelEvaluator(FeelEvaluator eva) { this.feelEval = eva; }
}
