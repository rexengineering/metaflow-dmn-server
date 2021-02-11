package com.rexhomes.dmn.server.handlers;

import com.rexhomes.dmn.server.HttpUtils;
import com.rexhomes.dmn.server.Server;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.logging.*;
import java.util.Map;


import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.Headers;

import org.camunda.bpm.dmn.engine.DmnDecision;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.dmn.engine.DmnDecisionRuleResult;
import org.json.JSONObject;
import org.json.JSONArray;

public class DecisionTableHandler implements WebHandler {
    private static final Logger logger = Logger.getLogger(DecisionTableHandler.class.getName());

    private Server server;
    private static final String RESULT = "result";
    private static final String ERROR = "error";
    private static final String DECISION_KEY = "decKey";

    public DecisionTableHandler(Server srv) {
        server = srv;
        server.getHttpServer().createContext("/dmn/decision-table", this);
        server.getHttpServer().createContext("/dmn/dt", this);
    }

    @Override
    public void handle(HttpExchange t) throws IOException {
        JSONArray jarr = new JSONArray();

        try {
            Headers hdr = t.getRequestHeaders();
            if (!hdr.containsKey("Content-type")) {
                jarr.put(new JSONObject().put(ERROR, "No Content-type header"));
            } else {
                String contType = hdr.get("Content-type").get(0);
                if (contType.equals("application/json")) {
                    jarr = processJsonDecisionTable(t);
                } else if (contType.equals("application/xml")) {
                    jarr = processDmnDecisionTable(t);
                } else {
                    jarr.put(new JSONObject().put(ERROR, "Unsupported Content-type " + contType));
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        HttpUtils.sendJsonResponse(t, jarr);
    }

    /**
     * Process a DMN decision table 
     * 
     * @param t - <code>HttpExchange</code>
     * @return - <code>JSONArray</code> of results
     * @throws IOException
     */
    public JSONArray processDmnDecisionTable(HttpExchange t) throws IOException {
        JSONArray jarr = new JSONArray();
        Map<String,Object> parms = HttpUtils.extractParmsFromQuery(t);
        if (!parms.containsKey(DECISION_KEY)) {
            jarr.put(new JSONObject().put(ERROR, "No 'decKey' parm specified"));
        } else {
            String decKey = (String)parms.get(DECISION_KEY);
            DmnDecision decision = server.getDmnEngine().parseDecision(decKey, t.getRequestBody());
            DmnDecisionTableResult result = server.getDmnEngine().evaluateDecisionTable(decision, parms);
            List<Map<String,Object>> rez = result.getResultList();

            for (Map<String,Object> m : rez) {
                JSONObject jo = new JSONObject();
                for (Map.Entry<String,Object> e : m.entrySet()) {
                    jo.put(e.getKey(),e.getValue());
                }
                jarr.put(jo);
            }
        }
        return jarr;
    }

    /**
     * Process a decision table-esque JSON structure. This is a very simple representation
     * of a decision table, which essentially uses a hit policy of FIRST - that is the first
     * positive evaluation determines the return set.
     * 
     * The input is a JSONArray [] containing two JSONObjects. The first (index 0) is a 
     * dictionary of FEEL expressions vs. the result. The second (index 1) is a dict of
     * inputs. 
     * 
     * @param t - <code>HttpExchange</code>
     * @return - <code>JSONArray</code> of results
     * @throws IOException
     */
    public JSONArray processJsonDecisionTable(HttpExchange t) throws IOException {
        // we have to parse this ourselves
        JSONArray json = HttpUtils.readJsonArrayFromRequest(t);
        JSONArray jarr = new JSONArray();
        // JSONArray consists of two elements:
        // [0] - Array of FEEL expression / result pairs
        // [1] - Input parms for FEEL evaluation
        //
        // Convert JSONArray to Map
        Map<String,Object> parms = new HashMap<>();
        JSONObject jsonObj = json.getJSONObject(1);
        for(String key :  jsonObj.keySet()) {
            parms.put(key, jsonObj.get(key));
        }

        // now, evaluate the list of expressions
        jsonObj = json.getJSONObject(0);
        Object def = null;
        for (String expr : jsonObj.keySet()) {
            if (expr.isEmpty()) {
                // this is the default case, so keep in case nothing passes
                def = jsonObj.get(expr);
            } else {
                Object result = server.getFeelEvaluator().evalExpression(expr, parms);
                if (result instanceof Boolean && (boolean)result) {
                    // we have a positive evaluation, so return the results for this expression
                    // this is consitent with the FIRST hit policy.
                    result = jsonObj.get(expr);
                    jarr.put(new JSONObject().put(RESULT, result));
                    break;
                }
            }
        }

        // check if we have any results
        if (jarr.isEmpty()) {
            if (def != null) {
                jarr.put(new JSONObject().put(RESULT, def));
            } else {
                jarr.put(new JSONObject().put(ERROR, "No results and no default case specified"));
            }
        }
        return jarr;
    }

    public void setServer(Server srv) { this.server = srv; }
    public Server getServer() { return server; }
}
