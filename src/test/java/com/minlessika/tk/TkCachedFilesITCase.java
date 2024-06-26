package com.minlessika.tk;

import com.jcabi.http.request.JdkRequest;
import com.jcabi.http.response.RestResponse;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.takes.facets.fork.FkRegex;
import org.takes.facets.fork.TkFork;
import org.takes.http.FtRemote;
import org.takes.tk.TkClasspath;
import org.takes.tk.TkFiles;
import org.takes.tk.TkWithType;

/**
 * IT Tests for {@link TkCachedFiles}.
 *
 * @since 0.2
 */
final class TkCachedFilesITCase {

    @ParameterizedTest
    @ValueSource(strings = {"txt", "TXT", "TXt"})
    void cachesFileFromClasspathTest(final String ext) throws Exception {
        try (InputStream input = this.getClass().getResourceAsStream("/file.txt")) {
            final String content = IOUtils.toString(input, StandardCharsets.UTF_8);
            new FtRemote(
                new TkCachedFiles(
                    new TkFork(
                        new FkRegex(
                            "/file\\.txt",
                            new TkWithType(new TkClasspath(), "text/plain")
                        )
                    ), "txt"
                )
            ).exec(
                home -> {
                    final URI uri = home.resolve(String.format("/file.%s", ext));
                    final String etag = DigestUtils.md5Hex(content).toUpperCase();
                    new JdkRequest(uri)
                        .fetch()
                        .as(RestResponse.class)
                        .assertHeader(
                            "Cache-Control", "public, max-age=0, no-cache"
                        )
                        .assertHeader("ETag", etag)
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

    @ParameterizedTest
    @ValueSource(strings = {"txt", "TXT", "TXt"})
    void cachesFileFromFileSystemTest(final String ext) throws Exception {
        final Path path = Paths.get("./src/test/resources/file.txt");
        final String content = Files.readAllLines(path).get(0);
        new FtRemote(
            new TkCachedFiles(
                new TkFork(
                    new FkRegex(
                        "/file\\.txt",
                        new TkWithType(new TkFiles("./src/test/resources"), "text/plain")
                    )
                ),"txt"
            )
        ).exec(
            home -> {
                final URI uri = home.resolve(String.format("/file.%s", ext));
                final String etag = DigestUtils.md5Hex(content).toUpperCase();
                new JdkRequest(uri)
                    .fetch()
                    .as(RestResponse.class)
                    .assertHeader(
                        "Cache-Control", "public, max-age=0, no-cache"
                    )
                    .assertHeader("ETag", etag)
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

    @Test
    void usesCacheWithNewQueryAndSameContentTest() throws Exception {
        try (InputStream input = this.getClass().getResourceAsStream("/file.txt")) {
            final String content = IOUtils.toString(input, StandardCharsets.UTF_8);
            new FtRemote(
                new TkCachedFiles(
                    new TkFork(
                        new FkRegex(
                            "/file\\.txt",
                            new TkWithType(new TkClasspath(), "text/plain")
                        )
                    ), "txt"
                )
            ).exec(
                home -> {
                    final URI uri = home.resolve("/file.txt?v1.0");
                    final String etag = DigestUtils.md5Hex(content).toUpperCase();
                    new JdkRequest(uri)
                        .fetch()
                        .as(RestResponse.class)
                        .assertHeader(
                            "Cache-Control", "public, max-age=0, no-cache"
                        )
                        .assertHeader("ETag", etag)
                        .assertStatus(HttpURLConnection.HTTP_OK)
                        .assertBody(Matchers.equalTo(content));
                    new JdkRequest(home.resolve("/file.txt?v2.0"))
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
