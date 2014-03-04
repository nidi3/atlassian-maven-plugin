package guru.nidi.maven.atlassian;

import guru.nidi.atlassian.remote.confluence.ConfluenceService;
import org.apache.maven.project.MavenProject;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.tools.generic.DateTool;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public abstract class AbstractAddMojo extends AbstractAtlassianMojo {
    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;

    /**
     * @parameter expression="${labels}"
     */
    protected String labels;

    /**
     * @parameter expression="${contentFile}"
     */
    protected File contentFile;

    /**
     * @parameter expression="${content}"
     */
    protected String content = "";

    /**
     * @parameter expression="${encoding}"
     */
    protected String encoding = "iso-8859-1";

    protected String readFile(File file) throws IOException {
        InputStreamReader in = new InputStreamReader(new FileInputStream(file), encoding);
        char[] buf = new char[(int) contentFile.length()];
        in.read(buf);
        in.close();
        return new String(buf);
    }

    protected void addLabels(ConfluenceService service, long id) {
        if (labels != null) {
            for (String label : labels.split(",")) {
                service.addLabelByName(label, id);
                getLog().info("Added label '" + label + "'");
            }
        }
    }

    protected String evaluate(String content) {
        try {
            VelocityEngine velocity = createVelocityEngine();
            StringWriter sw = new StringWriter();
            velocity.evaluate(createVelocityContext(), sw, "velocity", content);
            return sw.toString();
        } catch (Exception e) {
            getLog().error("Problem evaluating with Velocity", e);
            return content;
        }
    }

    private VelocityEngine createVelocityEngine() {
        VelocityEngine velocity = new VelocityEngine();
        velocity.init();
        return velocity;
    }

    private VelocityContext createVelocityContext() {
        Map<String, Object> context = new HashMap<String, Object>();
        context.put("project", project);
        context.put("prop", createInnerContext());
        context.put("date", new DateTool());
        return new VelocityContext(context);
    }

    private InterpolatedPropertiesMap createInnerContext() {
        Map<String, Object> context = new HashMap<String, Object>();
        context.put("project", project);
        for (Map.Entry prop : project.getProperties().entrySet()) {
            context.put((String) prop.getKey(), prop.getValue());
        }
        return new InterpolatedPropertiesMap(context);
    }
}
