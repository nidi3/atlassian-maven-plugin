package stni.maven.plugins;

import com.atlassian.confluence.rpc.soap.beans.RemotePage;
import com.atlassian.confluence.rpc.soap.beans.RemotePageUpdateOptions;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import stni.atlassian.remote.confluence.ConfluenceService;
import stni.atlassian.remote.confluence.ConfluenceTasks;
import stni.atlassian.remote.confluence.DefaultConfluenceService;

/**
 * @goal addPage
 */
public class AddPageMojo extends AbstractAddMojo {
    /**
     * @parameter expression="${title}"
     * @required
     */
    protected String title;

    /**
     * @parameter expression="${parentId}"
     * @required
     */
    protected long parentId;

    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            ConfluenceService service = new DefaultConfluenceService(atlassianUrl, atlassianUsername, atlassianPassword);
            ConfluenceTasks tasks = new ConfluenceTasks(service);
            RemotePage page = tasks.getOrCreatePage(parentId, evaluate(title));
            if (contentFile != null) {
                content += readFile(contentFile);
            }
            page.setContent(evaluate(content));
            service.updatePage(page, new RemotePageUpdateOptions());
            getLog().info("Updated/created page '" + title + "'");
            addLabels(service, page.getId());
        } catch (Exception e) {
            if (failOnError) {
                throw new MojoExecutionException("", e);
            }
            getLog().error(e);
        }
    }
}
