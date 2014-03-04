package guru.nidi.maven.atlassian;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.StringWriter;
import java.util.Map;

/**
 *
 */
public class InterpolatedPropertiesMap extends FunctionMap<String, String> {
    private final VelocityEngine engine = new VelocityEngine();
    private final VelocityContext context;

    public InterpolatedPropertiesMap(Map<String, Object> context) {
        this.context = new VelocityContext(context);
    }

    @Override
    public String get(Object key) {
        final Object prop = context.get((String) key);
        return prop == null ? "" : interpolate(prop.toString());
    }

    private String interpolate(String expression) {
        final StringWriter sw = new StringWriter();
        engine.evaluate(context, sw, "interpolator", expression);
        return sw.toString();
    }
}
