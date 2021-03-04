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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import static org.apache.maven.plugins.annotations.LifecyclePhase.PRE_INTEGRATION_TEST;

@Mojo(name = "start-local-hbase", defaultPhase = PRE_INTEGRATION_TEST)
public class StartHbaseMojo extends AbstractHbaseMojo {
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Starting local Hbase server.");

        LocalHbaseManager localHbaseManager = new LocalHbaseManager();
        try {
            localHbaseManager.start();
        } catch (Exception e) {
            getLog().error(e);
            throw new MojoExecutionException("Failed to start Hbase.", e);
        }

        session.getPluginContext(plugin, project).put("hbaseManager", localHbaseManager);

        getLog().info("Local Hbase server started.");
    }
}
