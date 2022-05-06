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
package cn.dgchong.maven.yapi.builder;

import cn.dgchong.maven.yapi.config.YapiConfig;
import cn.dgchong.maven.yapi.constant.YapiConstant;
import cn.dgchong.maven.yapi.model.YapiCatItem;
import cn.dgchong.maven.yapi.model.YapiCatItemParam;
import cn.dgchong.maven.yapi.model.YapiCatModel;
import cn.dgchong.maven.yapi.model.req.ApiDocModel;
import cn.dgchong.maven.yapi.model.req.PropertiesItem;
import cn.dgchong.maven.yapi.model.req.ReqHeadersItem;
import cn.dgchong.maven.yapi.model.req.ReqQueryItem;
import cn.dgchong.maven.yapi.model.rsp.YapiCatMenuAddResult;
import cn.dgchong.maven.yapi.model.rsp.YapiCatMenuModel;
import cn.dgchong.maven.yapi.model.rsp.YapiCatMenuResult;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.power.common.util.FileUtil;
import com.power.doc.builder.ApiDataBuilder;
import com.power.doc.model.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Yapi sync Builder
 *
 * @author : laonong
 * @date : Created in 2022/4/24 10:56
 */
public class YapiBuilder {

    /**
     * log
     */
    private static Log log = new SystemStreamLog();

    /**
     * sync api doc
     *
     * @param yapiConfig
     */
    public static void buildApiDoc(YapiConfig yapiConfig) throws FileNotFoundException {
        //1、get all api doc info
        ApiAllData docList = buildSmartDocApiDoc(yapiConfig);

        //2、analysis api doc ,then adapter to Yapi api data model
        List<YapiCatModel> yapiCatModelList = buildYapiCatModel(yapiConfig, docList);

        //3、sync to Yapi
        syncToYapi(yapiConfig, yapiCatModelList);
    }

    /**
     * get cat_menus list
     *
     * @param yapiConfig
     * @return  List<YapiCatMenuModel>
     */
    public static List<YapiCatMenuModel> getCatMenus(YapiConfig yapiConfig) {
        String path = new StringBuilder()
                .append(yapiConfig.getYapiServerUrl())
                .append(YapiConstant.INTERFACE_GET_CAT_MENU)
                .append("?token=")
                .append(yapiConfig.getYapiProjectToken())
                .toString();
        String catMenuResult = HttpUtil.get(path);
        if (StringUtils.isEmpty(catMenuResult)) {
            return null;
        }
        YapiCatMenuResult yapiCatMenuResult = JSON.parseObject(catMenuResult, YapiCatMenuResult.class);
        return yapiCatMenuResult.getData();
    }

    /**
     * add cat_menus
     *
     * @param yapiConfig
     * @param projectId
     * @param name
     * @param desc
     * @return YapiCatMenuModel
     */
    public static YapiCatMenuModel addCat(YapiConfig yapiConfig, Integer projectId, String name, String desc) {
        Map<String, Object> bodyParamMap = new HashMap<>();
        bodyParamMap.put("token", yapiConfig.getYapiProjectToken());
        bodyParamMap.put("project_id", projectId);
        bodyParamMap.put("name", name);
        bodyParamMap.put("desc", desc);
        String addCatResult = HttpUtil.post(yapiConfig.getYapiServerUrl() + YapiConstant.INTERFACE_ADD_CAT, bodyParamMap);
        YapiCatMenuAddResult yapiCatMenuAddResult = JSON.parseObject(addCatResult, YapiCatMenuAddResult.class);
        return yapiCatMenuAddResult.getData();
    }

    /**
     * analysis api doc , to adapter Yapi data model
     *
     * @param yapiConfig
     * @param docList
     * @return List<YapiCatModel>
     */
    private static List<YapiCatModel> buildYapiCatModel(YapiConfig yapiConfig, ApiAllData docList) {
        List<YapiCatModel> yapiCatModelList = new ArrayList<>();
        //1、analysis api doc data
        for (ApiDoc apiDoc : docList.getApiDocList()) {
            //1.1、controller basic data
            String catName = apiDoc.getDesc();
            if (StringUtils.isEmpty(catName)) {
                catName = apiDoc.getName();
            }
            YapiCatModel yapiCatModel = YapiCatModel.builder()
                    .catName(catName)
                    .catItems(new ArrayList<>())
                    .build();
            //1.2、controller interface data
            for (ApiMethodDoc apiMethodDoc : apiDoc.getList()) {
                //1.2.1、header parameter
                List<YapiCatItemParam> yapiHeaders = adapterSmartDocHeadersParam(apiMethodDoc.getRequestHeaders());
                //1.2.2、query parameter
                List<YapiCatItemParam> yapiQuerys = adapterSmartDocParam(apiMethodDoc.getQueryParams().stream().filter(apiParam -> apiParam.isQueryParam()).collect(Collectors.toList()));
                //1.2.3、body parameter
                List<YapiCatItemParam> yapiBodys = adapterSmartDocParam(apiMethodDoc.getQueryParams().stream().filter(apiParam -> !apiParam.isQueryParam()).collect(Collectors.toList()));
                //1.2.4、response parameter
                List<YapiCatItemParam> yapiResponse = adapterSmartDocParam(apiMethodDoc.getResponseParams());

                //structure YapiCatItem model
                YapiCatItem yapiCatItem = YapiCatItem.builder()
                        .requestType(apiMethodDoc.getType())
                        .interfaceTitle(apiMethodDoc.getDesc())
                        .interfacePath(apiMethodDoc.getUrl())
                        .headers(yapiHeaders)
                        .querys(yapiQuerys)
                        .bodys(yapiBodys)
                        .response(yapiResponse)
                        .build();
                yapiCatModel.getCatItems().add(yapiCatItem);
            }
            yapiCatModelList.add(yapiCatModel);
        }

        //2、fill catid
        List<YapiCatMenuModel> yapiCatMenuModels = getCatMenus(yapiConfig);
        Map<String, YapiCatMenuModel> catNameMap = yapiCatMenuModels.stream()
                .collect(Collectors.toMap(YapiCatMenuModel::getName, Function.identity()));
        for (YapiCatModel yapiCatModel : yapiCatModelList) {
            String catName = yapiCatModel.getCatName();
            if (catNameMap.containsKey(catName)) {
                yapiCatModel.setCatid(catNameMap.get(catName).get_id());
            } else {
                //not include，then add cat_menu
                YapiCatMenuModel yapiCatMenuModel = addCat(yapiConfig, yapiCatMenuModels.get(0).getProject_id(), catName, "");
                yapiCatModel.setCatid(yapiCatMenuModel.get_id());
            }
        }

        return yapiCatModelList;
    }

    /**
     * get all api doc info by smart-doc
     *
     * @return ApiAllData
     */
    public static ApiAllData buildSmartDocApiDoc(YapiConfig yapiConfig) throws FileNotFoundException {
        ApiConfig apiConfig = new ApiConfig();
        if (StringUtils.isNotEmpty(yapiConfig.getPackageFilters())) {
            apiConfig.setPackageFilters(yapiConfig.getPackageFilters());
        }
        if(Objects.nonNull(yapiConfig.getConfigFile())){
            String data = FileUtil.getFileContent(new FileInputStream(yapiConfig.getConfigFile()));
            ApiConfig apiConfigJson = JSON.parseObject(data,ApiConfig.class);
            apiConfig.setRequestHeaders(apiConfigJson.getRequestHeaders());
        }
        return ApiDataBuilder.getApiDataTree(apiConfig);
    }

    /**
     * sync api doc to Yapi
     *
     * @param yapiConfig
     * @param yapiCatModelList
     */
    public static void syncToYapi(YapiConfig yapiConfig, List<YapiCatModel> yapiCatModelList) {
        long start = System.currentTimeMillis();
        int totalInterface = 0;
        int successCount = 0;
        for (YapiCatModel yapiCatModel : yapiCatModelList) {
            //build sync request parameter
            List<YapiCatItem> catItems = yapiCatModel.getCatItems();
            for (YapiCatItem yapiCatItem : catItems) {
                //header parameter
                List<ReqHeadersItem> req_headers = null;
                List<YapiCatItemParam> headers = yapiCatItem.getHeaders();
                if (Objects.nonNull(headers) && (0 < headers.size())) {
                    req_headers = new ArrayList<>();
                    for (YapiCatItemParam itemParam : headers) {
                        req_headers.add(new ReqHeadersItem(itemParam.getName(), itemParam.getValue(), itemParam.getDesc(), adapterRequired(itemParam.getRequired()), ""));
                    }
                }
                //query parameter
                List<ReqQueryItem> req_query = buildReqQuery(yapiCatItem.getQuerys());
                //GET type request,set the parameters in the body to query, to adapt to Yapi
                if (YapiConstant.REQUEST_TYPE_GET.equals(yapiCatItem.getRequestType())) {
                    List<ReqQueryItem> req_query_body = buildReqQuery(yapiCatItem.getBodys());
                    if (Objects.nonNull(req_query_body)) {
                        if (Objects.isNull(req_query)) {
                            req_query = new ArrayList<>();
                        }
                        req_query.addAll(req_query_body);
                    }
                }
                //body parameter
                String body_other_str = buildReqResBodyJson(yapiCatItem.getBodys());
                //response parameter
                String res_body_str = buildReqResBodyJson(yapiCatItem.getResponse());

                //structure request data model
                ApiDocModel apiDocModel = ApiDocModel.builder()
                        .token(yapiConfig.getYapiProjectToken())
                        .catid(yapiCatModel.getCatid())
                        .method(yapiCatItem.getRequestType())
                        .title(yapiCatItem.getInterfaceTitle())
                        .path(yapiCatItem.getInterfacePath())
                        .req_headers(req_headers)
                        .req_query(req_query)
                        .req_body_type("json")
                        .req_body_is_json_schema(true)
                        .req_body_other(body_other_str)
                        .res_body_type("json")
                        .res_body_is_json_schema(true)
                        .res_body(res_body_str)
                        .build();
                //sync
                String body = JSON.toJSONString(apiDocModel);
                String result = HttpRequest.post(yapiConfig.getYapiServerUrl() + YapiConstant.INTERFACE_SAVE)
                        .header("Content-Type", "application/json")
                        .body(body)
                        .execute()
                        .body();

                if(0 == (int)JSON.parseObject(result, JSONObject.class).get("errcode")){
                    successCount++;
                } else {
                    log.error(String.format("sync error,body:%s,result:%s",body,result));
                }
            }
            totalInterface += catItems.size();
        }
        StringBuilder sb = new StringBuilder("sync api doc,")
                .append("cost:").append(System.currentTimeMillis() - start).append("ms,")
                .append("cat:").append(yapiCatModelList.size()).append(",")
                .append("api:").append(totalInterface).append(",")
                .append("success:").append(successCount).append(",")
                .append("fail:").append(totalInterface - successCount).append(".");
        log.info(sb.toString());
    }

    /**
     * bulid req_query parameter
     *
     * @param querys
     * @return
     */
    private static List<ReqQueryItem> buildReqQuery(List<YapiCatItemParam> querys) {
        if (Objects.isNull(querys) || (0 == querys.size())) {
            return null;
        }
        List<ReqQueryItem> req_query = new ArrayList<>();
        for (YapiCatItemParam itemParam : querys) {
            req_query.add(new ReqQueryItem(itemParam.getName(), itemParam.getDesc(), adapterRequired(itemParam.getRequired()), null));
        }
        return req_query;
    }

    /**
     * bulid res_body json str
     *
     * @param yapiCatItemParamList
     * @return String
     */
    private static String buildReqResBodyJson(List<YapiCatItemParam> yapiCatItemParamList) {
        Map<String, Object> resBodyMap = adapterResBodyParam(null, yapiCatItemParamList);
        if (Objects.isNull(resBodyMap)) {
            return "";
        } else {
            return JSON.toJSONString(resBodyMap);
        }
    }

    /**
     * format res_body parameter
     *
     * @param description  node desc
     * @param yapiCatItemParamList  children node data list
     * @return Map<String, Object>
     */
    private static Map<String, Object> adapterResBodyParam(String description, List<YapiCatItemParam> yapiCatItemParamList) {
        if (Objects.isNull(yapiCatItemParamList) || (0 == yapiCatItemParamList.size())) {
            return new HashMap<>();
        }
        Map<String, Object> paramMap = new HashMap<>();
        List<String> requiredParams = new ArrayList<>();
        for (YapiCatItemParam itemParam : yapiCatItemParamList) {
            List<YapiCatItemParam> childrens = itemParam.getChildrens();
            if (Objects.nonNull(childrens) && (0 < childrens.size())) {
                Map<String, Object> childrenMap = adapterResBodyParam(itemParam.getDesc(), childrens);
                paramMap.put(itemParam.getName(), childrenMap);
            } else {
                paramMap.put(itemParam.getName(), new PropertiesItem(itemParam.getType(), itemParam.getDesc()));
            }
            if (itemParam.getRequired()) {
                requiredParams.add(itemParam.getName());
            }
        }

        Map<String, Object> resBodyMap = new HashMap<>(3);
        resBodyMap.put("properties", paramMap);
        resBodyMap.put("required", requiredParams);
        resBodyMap.put("description", description);
        return resBodyMap;
    }

    /**
     * format smart-doc headers data structure
     *
     * @param apiReqParamList
     * @return List<YapiCatItemParam>
     */
    private static List<YapiCatItemParam> adapterSmartDocHeadersParam(List<ApiReqParam> apiReqParamList){
        if(Objects.isNull(apiReqParamList) || (0 == apiReqParamList.size())){
            return null;
        }
        List<YapiCatItemParam> yapiCatItemParamList = new ArrayList<>();
        for(ApiReqParam apiReqParam : apiReqParamList){
            YapiCatItemParam yapiCatItemParam = YapiCatItemParam.builder()
                    .name(apiReqParam.getName())
                    .desc(apiReqParam.getDesc())
                    .value(apiReqParam.getValue())
                    .required(apiReqParam.isRequired())
                    .type(apiReqParam.getType())
                    .build();
            yapiCatItemParamList.add(yapiCatItemParam);
        }
        return yapiCatItemParamList;
    }

    /**
     * format smart-doc parameter data structure
     *
     * @param apiParamList
     * @return List<YapiCatItemParam>
     */
    private static List<YapiCatItemParam> adapterSmartDocParam(List<ApiParam> apiParamList) {
        List<YapiCatItemParam> yapiCatItemParamList = new ArrayList<>();
        for (ApiParam apiParam : apiParamList) {
            YapiCatItemParam yapiCatItemParam = YapiCatItemParam.builder()
                    .name(apiParam.getField())
                    .desc(apiParam.getDesc())
                    .required(apiParam.isRequired())
                    .type(apiParam.getType())
                    .build();
            if ("object".equals(apiParam.getType()) && (0 < apiParam.getChildren().size())) {
                //having children nodes
                List<YapiCatItemParam> childrens = adapterSmartDocParam(apiParam.getChildren());
                yapiCatItemParam.setChildrens(childrens);
            }
            yapiCatItemParamList.add(yapiCatItemParam);
        }
        return yapiCatItemParamList;
    }

    /**
     * required tag exchange
     *
     * @param required
     * @return String
     */
    private static String adapterRequired(boolean required) {
        if (required) {
            return YapiConstant.REQUIRED_TRUE;
        }
        return YapiConstant.REQUIRED_FALSE;
    }
}
