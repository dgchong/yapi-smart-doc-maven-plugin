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
package cn.dgchong.maven.yapi.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * cat model（one cat model means one controller）
 *
 * @author : laonong
 * @date : Created in 2022/4/27 14:51
 */
@Data
@Builder
public class YapiCatModel {
    /**
     * cat id
     */
    private Integer catid;
    /**
     * cat name
     */
    private String catName;
    /**
     * cat items
     */
    private List<YapiCatItem> catItems;
}
