package guru.nidi.maven.atlassian;

import com.atlassian.jira.rpc.soap.beans.RemoteProject;
import com.atlassian.jira.rpc.soap.beans.RemoteVersion;
import guru.nidi.atlassian.remote.jira.DefaultJiraService;
import guru.nidi.atlassian.remote.jira.JiraService;
import guru.nidi.atlassian.remote.jira.JiraTasks;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

/**
 * @goal releaseNotes
 */
public class ReleaseNotesMojo extends AbstractAtlassianMojo {
    /**
     * @parameter expression="${projectKey}"
     * @required
     */
    protected String projectKey;

    /**
     * @parameter expression="${version}"
     * @required
     */
    protected String version;

    /**
     * @parameter expression="${onlyFixed}"
     */
    protected boolean onlyFixed = true;

    /**
     * @parameter expression="${encoding}"
     */
    protected String encoding = "iso-8859-1";

    /**
     * @parameter expression="${destination}"
     */
    protected File destination = new File(basedir, "target/release-notes.html");

    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            JiraService service = new DefaultJiraService(atlassianUrl, atlassianUsername, atlassianPassword);
            JiraTasks tasks = new JiraTasks(service);
            RemoteProject project = service.getProjectByKey(projectKey);
            if (project == null) {
                throw new MojoExecutionException("Project '" + projectKey + "' not found.");
            }
            RemoteVersion remoteVersion = tasks.versionByName(project, version);
            if (remoteVersion == null) {
                throw new MojoExecutionException("Version '" + version + "' not found.");
            }
            String releaseNotes = tasks.makeReleaseNotes(project, remoteVersion, onlyFixed);
            if (destination.isDirectory()) {
                destination = new File(destination, "release-notes.html");
            }
            destination.getParentFile().mkdirs();
            getLog().info("Writing release notes to " + destination.getAbsolutePath());
            OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(destination), encoding);
            out.write(releaseNotes);
            out.close();
        } catch (Exception e) {
            if (failOnError) {
                throw new MojoExecutionException("", e);
            }
            getLog().error(e);
        }
    }
}
