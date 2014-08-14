package io.provis.model;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.eclipse.aether.artifact.AbstractArtifact;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;

import com.google.common.collect.Lists;

public class ProvisioArtifact extends AbstractArtifact {

  private List<ProvisioningAction> actions;
  private Artifact delegate;
  private String coordinate;

  public ProvisioArtifact(String coordinate) {
    this.delegate = new DefaultArtifact(coordinate);
    this.coordinate = coordinate;
  }

  public ProvisioArtifact(Artifact a) {
    this.delegate = a;
  }

  public String getCoordinate() {
    return coordinate;
  }

  public String getGA() {
    return getGroupId() + ":" + getArtifactId();
  }

  public String getGAV() {
    return getGroupId() + ":" + getArtifactId() + ":" + getVersion();
  }
       
  public List<ProvisioningAction> getActions() {
    return actions;
  }
  
  public void addAction(ProvisioningAction action) {
    if(actions == null) {
      actions = Lists.newArrayList();
    }
    actions.add(action);
  }
  
  //
  //
  //  
  
  public String getGroupId() {
    return delegate.getGroupId();
  }

  public String getArtifactId() {
    return delegate.getArtifactId();
  }

  public String getVersion() {
    return delegate.getVersion();
  }

  public Artifact setVersion(String version) {
    delegate = delegate.setVersion(version);
    return this;
  }

  public String getBaseVersion() {
    return delegate.getBaseVersion();
  }

  public boolean isSnapshot() {
    return delegate.isSnapshot();
  }

  public String getClassifier() {
    return delegate.getClassifier();
  }

  public String getExtension() {
    return delegate.getExtension();
  }

  public File getFile() {
    return delegate.getFile();
  }

  public Artifact setFile(File file) {
    delegate = delegate.setFile(file);
    return this;
  }

  public String getProperty(String key, String defaultValue) {
    return delegate.getProperty(key, defaultValue);
  }

  public Map<String, String> getProperties() {
    return delegate.getProperties();
  }

  public Artifact setProperties(Map<String, String> properties) {
    delegate = delegate.setProperties(properties);
    return this;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }

    if (obj instanceof ProvisioArtifact) {
      return delegate.equals(((ProvisioArtifact) obj).delegate);
    }

    return delegate.equals(obj);
  }

  @Override
  public int hashCode() {
    return delegate.hashCode();
  }

  @Override
  public String toString() {
    return delegate.toString();
  }
}
