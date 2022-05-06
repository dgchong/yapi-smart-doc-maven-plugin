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
package cn.dgchong.maven.yapi.model.req;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * api model
 *
 * @author : laonong
 * @date : Created in 2022/4/25 22:50
 */
@Data
@Builder
public class ApiDocModel {
    /**
     * project token
      */
    private String token;
    /**
     * query parameter
     */
    private List<ReqQueryItem> req_query;
    /**
     * header parameter
     */
    private List<ReqHeadersItem> req_headers;
    /**
     * not form parameter
     */
    private String req_body_other;
    /**
     * cat id
     */
    private Integer catid;
    /**
     * interface name
     */
    private String title;
    /**
     * interface path
     */
    private String path;
    /**
     * interface status (undone , done)
     */
    private String status;
    /**
     * request body type (json)
     */
    private String req_body_type;
    /**
     * response body type
     */
    private String res_body_type;
    /**
     * response body
     */
    private String res_body;
    /**
     * request type （GET、POST）
     */
    private String method;
    /**
     * request parameter
     */
    private String req_params;
    /**
     * request body tag
     */
    private Boolean req_body_is_json_schema;
    /**
     * response body tag
     */
    private Boolean res_body_is_json_schema;

}
