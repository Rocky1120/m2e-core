/*******************************************************************************
 * Copyright (c) 2014 Takari, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Takari, Inc. - initial API and implementation
 *******************************************************************************/

package org.eclipse.m2e.core.internal.project;

import java.io.File;
import java.util.Set;

import javax.inject.Singleton;

import org.apache.maven.project.DefaultProjectRealmCache;
import org.apache.maven.project.MavenProject;

import org.eclipse.m2e.core.embedder.ArtifactKey;


/**
 * @since 1.6
 */
@Singleton
@SuppressWarnings("synthetic-access")
public class EclipseProjectRealmCache extends DefaultProjectRealmCache implements IManagedCache {

  private final ProjectCachePlunger<Key> plunger = new ProjectCachePlunger<Key>() {
    protected void flush(Key cacheKey) {
      CacheRecord cacheRecord = cache.remove(cacheKey);
      if(cacheRecord != null) {
        disposeClassRealm(cacheRecord.realm);
      }
    }
  };

  @Override
  public void register(MavenProject project, Key key, CacheRecord record) {
    plunger.register(project, key);
  }

  @Override
  public Set<File> removeProject(File pom, ArtifactKey mavenProject, boolean forceDependencyUpdate) {
    return plunger.removeProject(pom, forceDependencyUpdate);
  }

  @Override
  public void flush() {
    super.flush();
    plunger.flush();
  }

}
