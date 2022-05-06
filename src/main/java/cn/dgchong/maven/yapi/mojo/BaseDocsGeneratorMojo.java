/*
 * yapi-smart-doc-maven-plugin https://github.com/dgchong/yapi-smart-doc-maven-plugin
 *
 * Copyright 2022-2024 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.dgchong.maven.yapi.mojo;

import cn.dgchong.maven.yapi.config.YapiConfig;
import cn.dgchong.maven.yapi.constant.YapiConstant;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;

/**
 * mojo base abstract class
 *
 * @author : laonong
 * @date : Created in 2022/4/24 10:20
 */
public abstract class BaseDocsGeneratorMojo extends AbstractMojo {

    /**
     * Yapi server url
     */
    @Parameter(property = "yapiServerUrl",required = true)
    private String yapiServerUrl;

    /**
     *  Yapi project token
     */
    @Parameter(property = "yapiProjectToken",required = true)
    private String yapiProjectToken;

    /**
     * filter controllers
     */
    @Parameter(property = "packageFilters")
    private String packageFilters;

    /**
     * config file
     */
    @Parameter(property = "configFile")
    private File configFile;

    /**
     * project object
     */
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    protected MavenProject project;

    /**
     * skip sync
     */
    @Parameter(property = "skip")
    private String skip;

    /**
     * do execute
     * @param yapiConfig
     * @throws MojoExecutionException
     * @throws MojoFailureException
     */
    public abstract void executeMojo(YapiConfig yapiConfig)
            throws MojoExecutionException, MojoFailureException;

    /**
     * init load execute
     * @throws MojoExecutionException
     * @throws MojoFailureException
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("starting sync api doc to Yapi.");
        if (YapiConstant.SKIP_TRUE.equals(skip)) {
            getLog().debug("skip sync api doc to yapi.");
            return;
        }

        YapiConfig yapiConfig = YapiConfig.builder()
                .yapiServerUrl(yapiServerUrl)
                .yapiProjectToken(yapiProjectToken)
                .packageFilters(packageFilters)
                .configFile(configFile)
                .build();
        this.executeMojo(yapiConfig);

        getLog().info("finished sync api doc to Yapi.");
    }

}
