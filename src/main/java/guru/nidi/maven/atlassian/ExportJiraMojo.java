package guru.nidi.maven.atlassian;

import guru.nidi.atlassian.remote.meta.JiraGenerateRequest;
import guru.nidi.atlassian.remote.meta.client.JiraExportClient;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.*;
import java.util.Map;

/**
 * @goal jira-export
 */
public class ExportJiraMojo extends AbstractAtlassianMojo {
    /**
     * @parameter expression="${request}"
     * @required
     */
    protected JiraGenerateRequest request;

    /**
     * @parameter expression="${exportFile}"
     * @required
     */
    protected File exportFile;

    /**
     * @parameter expression="${exportServerUrl}"
     * @required
     */
    protected String exportServerUrl;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            final JiraExportClient client = new JiraExportClient(exportServerUrl, atlassianUsername, atlassianPassword);
            resolveIncludes();
            if (request.getUrl() == null) {
                request.setUrl(atlassianUrl);
            }
            final InputStream export = client.getExport(request);
            exportFile.getParentFile().mkdirs();
            final BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(exportFile));
            copy(export, out);
        } catch (IOException e) {
            throw new MojoExecutionException("Problem generating export", e);
        }

    }

    private void resolveIncludes() throws IOException {
        if (request.getInclude() != null) {
            for (Map.Entry<String, String> entry : request.getInclude().entrySet()) {
                entry.setValue(readFile(new File(basedir, entry.getValue())));
            }
        }
    }

    private String readFile(File file) throws IOException {
        if (!file.exists()) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        copy(new FileInputStream(file), out);
        return out.toString("utf-8");
    }

    private void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buf = new byte[10000];
        int read;
        while ((read = in.read(buf)) > 0) {
            out.write(buf, 0, read);
        }
        in.close();
        out.close();
    }
}
