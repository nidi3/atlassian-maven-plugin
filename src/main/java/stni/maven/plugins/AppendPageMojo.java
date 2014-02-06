package stni.maven.plugins;

import com.atlassian.confluence.rpc.soap.beans.RemotePage;
import com.atlassian.confluence.rpc.soap.beans.RemotePageUpdateOptions;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import stni.atlassian.remote.confluence.ConfluenceService;
import stni.atlassian.remote.confluence.DefaultConfluenceService;

/**
 * @goal appendPage
 */
public class AppendPageMojo extends AbstractAddMojo {
    /**
     * @parameter expression="${pageId}"
     * @required
     */
    protected long pageId;

    /**
     * @parameter expression="${appendTop}"
     */
    protected boolean appendTop = false;


    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            ConfluenceService service = new DefaultConfluenceService(atlassianUrl, atlassianUsername, atlassianPassword);
            RemotePage page = service.getPage(pageId);
            if (contentFile != null) {
                content = appendTop ? (readFile(contentFile) + content) : (content + readFile(contentFile));
            }
            page.setContent(page.getContent() + evaluate(content));
            service.updatePage(page, new RemotePageUpdateOptions());
            getLog().info("Updated/created page '" + page.getTitle() + "'");
            addLabels(service, page.getId());
        } catch (Exception e) {
            if (failOnError) {
                throw new MojoExecutionException("", e);
            }
            getLog().error(e);
        }
    }
}
