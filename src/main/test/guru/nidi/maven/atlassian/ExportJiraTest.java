package guru.nidi.maven.atlassian;

import guru.nidi.atlassian.remote.meta.JiraGenerateRequest;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

/**
 *
 */
public class ExportJiraTest {
    @Test
    @Ignore
    public void simple() throws MojoFailureException, MojoExecutionException {
        final ExportJiraMojo mojo = new ExportJiraMojo();
        mojo.request = request();
        mojo.exportServerUrl = System.getenv("EXPORTS_URL");
        mojo.atlassianPassword = System.getenv("JIRA_PASS");
        mojo.atlassianUsername = System.getenv("JIRA_USER");
        mojo.atlassianUrl = System.getenv("JIRA_URL");
        mojo.exportFile = new File("output.pdf");
        mojo.execute();
    }

    private JiraGenerateRequest request() {
        final JiraGenerateRequest request = new JiraGenerateRequest();
        request.setTemplate("Release notes (User Story per Epic)");
        request.setStyle("mimacom");
        request.setProjectKey("LS");
        request.setProjectName("LIVING SERVICES");
        request.setDocType("release notes");
        request.setVersions("r1.0.1");
        return request;
    }
}
