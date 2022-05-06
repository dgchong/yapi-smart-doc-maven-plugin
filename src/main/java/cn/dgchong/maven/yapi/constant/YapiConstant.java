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
package cn.dgchong.maven.yapi.constant;

/**
 * Yapi Constant config class
 *
 * @author : laonong
 * @date : Created in 2022/4/24 10:36
 */
public class YapiConstant {

    /**
     * Yapi rest mojo name
     */
    public static final String YAPI_REST_MOJO = "yapi-rest";

    /**
     * Yapi server data import interface
     */
    public static final String INTERFACE_SAVE =  "/api/interface/save";

    /**
     * Yapi add_cat interface
     */
    public static final String INTERFACE_ADD_CAT =  "/api/interface/add_cat";

    /**
     * Yapi getCatMenu interface
     */
    public static final String INTERFACE_GET_CAT_MENU =  "/api/interface/getCatMenu";

    /**
     * required true
     */
    public static final String REQUIRED_TRUE = "1";

    /**
     * required false
     */
    public static final String REQUIRED_FALSE = "0";

    /**
     * skip sync tag
     */
    public static final String SKIP_TRUE = "true";

    /**
     * get request type
     */
    public static final String REQUEST_TYPE_GET = "GET";

}
