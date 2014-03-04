package guru.nidi.maven.atlassian;

import com.atlassian.confluence.rpc.soap.beans.RemoteAttachment;
import com.atlassian.confluence.rpc.soap.beans.RemoteUser;
import guru.nidi.atlassian.remote.confluence.ConfluenceService;
import guru.nidi.atlassian.remote.confluence.DefaultConfluenceService;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Calendar;

/**
 * @goal addAttachment
 */
public class AddAttachmentMojo extends AbstractAtlassianMojo {
    /**
     * @parameter expression="${pageId}"
     * @required
     */
    protected long pageId;

    /**
     * @parameter expression="${title}"
     * @required
     */
    protected String title;

    /**
     * @parameter expression="${comment}"
     */
    protected String comment;

    /**
     * @parameter expression="${contentType}"
     * @required
     */
    protected String contentType;

    /**
     * @parameter expression="${attachment}"
     * @required
     */
    protected File attachment;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            ConfluenceService service = new DefaultConfluenceService(atlassianUrl, atlassianUsername, atlassianPassword);
            final RemoteUser user = service.getUser(atlassianUsername);
            final RemoteAttachment remoteAttachment = new RemoteAttachment(comment, contentType, Calendar.getInstance(), user.getFullname(), attachment.getName(), attachment.length(), 0, pageId, title, null);
            service.addAttachment(pageId, remoteAttachment, fileContent(attachment));
        } catch (Exception e) {
            if (failOnError) {
                throw new MojoExecutionException("", e);
            }
            getLog().error(e);
        }
    }

    private byte[] fileContent(File file) throws IOException {
        byte[] res = new byte[(int) file.length()];
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            in.read(res);
            return res;
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }
}
