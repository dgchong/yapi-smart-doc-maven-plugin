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
package cn.dgchong.maven.yapi;

import cn.dgchong.maven.yapi.builder.YapiBuilder;
import cn.dgchong.maven.yapi.config.YapiConfig;
import cn.dgchong.maven.yapi.constant.YapiConstant;
import cn.dgchong.maven.yapi.mojo.BaseDocsGeneratorMojo;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 * Yapi plugin mojo . sync api doc to Yapi.
 *
 * @author : laonong
 * @date : Created in 2022/4/24 10:21
 */
@Execute(phase = LifecyclePhase.COMPILE)
@Mojo(name = YapiConstant.YAPI_REST_MOJO, requiresDependencyResolution = ResolutionScope.COMPILE)
public class YapiRestMojo extends BaseDocsGeneratorMojo {

    @Override
    public void executeMojo(YapiConfig yapiConfig) {
        try {
            YapiBuilder.buildApiDoc(yapiConfig);
        } catch (Exception e) {
            getLog().error(e);
        }
    }
}
