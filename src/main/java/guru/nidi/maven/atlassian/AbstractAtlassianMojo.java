package guru.nidi.maven.atlassian;

import org.apache.maven.plugin.AbstractMojo;

import java.io.File;

/**
 *
 */
public abstract class AbstractAtlassianMojo extends AbstractMojo {
    /**
     * @parameter expression="${atlassianUsername}"
     * @required
     */
    protected String atlassianUsername;

    /**
     * @parameter expression="${atlassianPassword}"
     * @required
     */
    protected String atlassianPassword;

    /**
     * @parameter expression="${atlassianUrl}"
     * @required
     */
    protected String atlassianUrl;

    /**
     * @parameter expression="${failOnError}"
     */
    protected boolean failOnError = false;

    /**
     * @parameter expression="${basedir}"
     * @required
     * @readonly
     */
    protected File basedir;

}
