package io.provis;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.io.ByteStreams;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public abstract class Provisioner {

  protected File resolveFromRepository(String repositoryUrl, String coordinate) throws IOException {
    String path = coordinateToPath(coordinate);
    String url = String.format("%s/%s", repositoryUrl, path);
    return resolveFromServer(url, coordinate);
  }

  protected File resolveFromServer(String archiveUrl, String coordinate) throws IOException {
    File localRepository = new File(System.getProperty("user.home"), ".m2/repository");
    String path = coordinateToPath(coordinate);
    File file = new File(localRepository, path);
    if (file.exists()) {
      return file;
    }
    if (!file.getParentFile().exists()) {
      file.getParentFile().mkdirs();
    }
    OkHttpClient client = new OkHttpClient();
    Request request = new Request.Builder().url(archiveUrl).build();
    Response response = client.newCall(request).execute();
    if (!response.isSuccessful()) {
      throw new IOException("Unexpected code " + response);
    }
    try (OutputStream os = new FileOutputStream(file)) {
      ByteStreams.copy(response.body().byteStream(), os);
    }
    return file;
  }

  protected String coordinateToPath(String coords) {
    Pattern p = Pattern.compile("([^: ]+):([^: ]+)(:([^: ]*)(:([^: ]+))?)?:([^: ]+)");
    Matcher m = p.matcher(coords);
    if (!m.matches()) {
      throw new IllegalArgumentException("Bad artifact coordinates " + coords + ", expected format is <groupId>:<artifactId>[:<extension>[:<classifier>]]:<version>");
    }
    String groupId = m.group(1);
    String artifactId = m.group(2);
    String extension = get(m.group(4), "jar");
    String classifier = get(m.group(6), "");
    String version = m.group(7);
    return repositoryPathOf(groupId, artifactId, extension, classifier, version);
  }

  protected String get(String value, String defaultValue) {
    return (value == null || value.length() <= 0) ? defaultValue : value;
  }

  public String repositoryPathOf(String groupId, String artifactId, String extension, String classifier, String version) {
    //
    // <groupId>:<artifactId>[:<extension>[:<classifier>]]:<version>
    //
    // groupId/artifactId/version/artifactId-version[-classlifier].extension
    //    
    StringBuffer path = new StringBuffer().append(groupId.replace('.', '/')).append('/').append(artifactId).append('/').append(version).append('/').append(artifactId).append('-').append(version);

    //
    // Aether's default classifier is "jar" so the only time we want to write out the classifer
    // is when there is a value that is not "jar".
    //
    if (classifier != null && classifier.isEmpty() == false && classifier.equals("jar") == false) {
      path.append("-").append(classifier);
    }
    path.append('.').append(extension);
    return path.toString();
  }
}