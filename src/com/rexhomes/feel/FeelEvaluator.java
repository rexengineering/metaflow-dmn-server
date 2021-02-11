package com.rexhomes.feel;

import java.util.HashMap;
import java.util.logging.*;
import java.util.Map;

import org.camunda.feel.*;
import org.camunda.feel.impl.*;
import org.json.JSONObject;

import camundajar.impl.scala.util.Either;

public class FeelEvaluator {
    private static final Logger logger = Logger.getLogger(FeelEvaluator.class.getName());
    private FeelEngine engine;

    public FeelEvaluator() {
        engine = new FeelEngine.Builder()
            .valueMapper(SpiServiceLoader.loadValueMapper())
            .functionProvider(SpiServiceLoader.loadFunctionProvider())
            .build();
        // make a dummy call to the engine to get everything loaded and primed
        engine.evalExpression("1+1", new HashMap<String,Object>());
        logger.info("FEEL Evaluator started");
    }

    /**
     * Answer the instance of the current FEEL engine.
     * @return Instance of <code>FeelEngine</code>
     */
    public final FeelEngine engine() { return engine; }
    /**
     * Set a FEEL engine implementation for testing purposes
     * @param engine - Instance of <code>FeelEngine</code>
     */
    public void setEngine(FeelEngine engine) { this.engine = engine; }

    public Object evalExpression(String expr, Map<String,Object> parms) {
        Object ret = null;
        final Either<FeelEngine.Failure, Object> result = engine.evalExpression(expr, parms);

        if (result.isRight()) {
            ret = result.getOrElse(null);
        } else {
            final FeelEngine.Failure failure = result.left().getOrElse(null);
            if (null == failure) {
                ret = "Unknown failure.";
            } else {
                ret = failure.message();
            }
        }
        return ret;
    }

}
