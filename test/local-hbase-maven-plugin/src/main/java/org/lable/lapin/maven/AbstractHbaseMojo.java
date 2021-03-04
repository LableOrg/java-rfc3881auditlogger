/*
 * Copyright © 2015 Lable (info@lable.nl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.lable.lapin.maven;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Settings;

import java.io.File;

public abstract class AbstractHbaseMojo extends AbstractMojo {
    @Parameter(defaultValue = "${session}", readonly = true)
    protected MavenSession session;

    @Parameter( defaultValue = "${project}", readonly = true )
    protected MavenProject project;

    @Parameter( defaultValue = "${mojoExecution}", readonly = true )
    protected MojoExecution mojo;

    @Parameter( defaultValue = "${plugin}", readonly = true )
    protected PluginDescriptor plugin;

    @Parameter( defaultValue = "${settings}", readonly = true )
    protected Settings settings;

    @Parameter( defaultValue = "${project.basedir}", readonly = true )
    protected File basedir;

    @Parameter( defaultValue = "${project.build.directory}", readonly = true )
    protected File target;
}
