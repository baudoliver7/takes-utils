package com.minlessika.tk;

import com.jcabi.http.request.JdkRequest;
import com.jcabi.http.response.RestResponse;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.takes.facets.fork.FkRegex;
import org.takes.facets.fork.TkFork;
import org.takes.http.FtRemote;
import org.takes.tk.TkClasspath;
import org.takes.tk.TkWithType;

/**
 * IT Tests for {@link TkCachedFiles}.
 *
 * @since 0.2
 */
final class TkCachedFilesITCase {

    /**
     * Max age.
     */
    final static int MAX_AGE = 60;

    @ParameterizedTest
    @ValueSource(strings = {"txt", "TXT", "TXt"})
    void cachesFileFromClasspathTest(final String ext) throws Exception {
        try (InputStream input = this.getClass().getResourceAsStream("/file.txt")) {
            final String content = IOUtils.toString(input, "UTF-8");
            new FtRemote(
                new TkCachedFiles(
                    new TkFork(
                        new FkRegex(
                            "/file\\.txt",
                            new TkWithType(new TkClasspath(), "text/plain")
                        )
                    ), TkCachedFilesITCase.MAX_AGE, "txt"
                )
            ).exec(
                home -> {
                    final URI uri = home.resolve(String.format("/file.%s", ext));
                    final String etag = DigestUtils.md5Hex(content).toUpperCase();
                    new JdkRequest(uri)
                        .fetch()
                        .as(RestResponse.class)
                        .assertHeader(
                            "Cache-Control",
                            String.format("public, max-age=%s", TkCachedFilesITCase.MAX_AGE)
                        )
                        .assertHeader(
                            "ETag",
                            etag
                        )
                        .assertStatus(HttpURLConnection.HTTP_OK)
                        .assertBody(Matchers.equalTo(content));
                    new JdkRequest(uri)
                        .header("If-None-Match", etag)
                        .fetch()
                        .as(RestResponse.class)
                        .assertStatus(HttpURLConnection.HTTP_NOT_MODIFIED)
                        .assertBody(Matchers.isEmptyString());
                }
            );
        }
    }
}
