package stni.maven.plugins;

import com.atlassian.confluence.rpc.soap.beans.RemoteBlogEntry;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import stni.atlassian.remote.confluence.ConfluenceService;
import stni.atlassian.remote.confluence.DefaultConfluenceService;

/**
 * @goal addBlog
 */
public class AddBlogMojo extends AbstractAddMojo {
    /**
     * @parameter expression="${title}"
     * @required
     */
    protected String title;

    /**
     * @parameter expression="${space}"
     * @required
     */
    protected String space;

    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            ConfluenceService service = new DefaultConfluenceService(atlassianUrl, atlassianUsername, atlassianPassword);
            RemoteBlogEntry blog = new RemoteBlogEntry();
            blog.setSpace(space);
            blog.setTitle(evaluate(title));
            if (contentFile != null) {
                content += readFile(contentFile);
            }
            blog.setContent(evaluate(content));
            blog = service.storeBlogEntry(blog);
            getLog().info("Created blog '" + title + "'");
            addLabels(service,blog.getId());
        } catch (Exception e) {
            if (failOnError) {
                throw new MojoExecutionException("", e);
            }
            getLog().error(e);
        }
    }


}
