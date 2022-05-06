# yapi-smart-doc maven plugin

[![License](https://img.shields.io/badge/license-Apache%202-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)
![java version](https://img.shields.io/badge/JAVA-1.8+-orange.svg)
[![Latest version](https://img.shields.io/badge/Latest-v0.0.3-blue)](https://github.com/dgchong/yapi-smart-doc-maven-plugin/releases/tag/v0.0.3)

## 一、简介
&emsp;&emsp; **yapi-smart-doc maven plugin** 是一个基于 [smart-doc](https://github.com/smart-doc-group/smart-doc) 的同步项目接口文档到 [YApi](https://github.com/YMFE/yapi) 平台的maven插件小工具。
其目的在于帮助使用 **Yapi** 接口管理平台的团队能够更加便利的维护/管理接口文档的更新，从而提高工作效率，降低维护成本。

&emsp;&emsp;
## 二、使用说明
&emsp;&emsp;**yapi-smart-doc maven plugin** 的使用和一个普通的maven插件一样，只需要在项目的 `pom.xml` 配置文件中添加相应插件配置即可。

示例配置如下：
```xml
<plugin>
      <groupId>cn.dgchong.maven</groupId>
      <artifactId>yapi-smart-doc-maven-plugin</artifactId>
      <version>【version-latest】</version>
      <configuration>
          <yapiServerUrl>【YApi管理平台地址】</yapiServerUrl>
          <yapiProjectToken>【YApi中的项目token】</yapiProjectToken>
      </configuration>
      <executions>
          <execution>
              <phase>compile</phase>
              <goals>
                  <goal>yapi-rest</goal>
              </goals>
          </execution>
      </executions>
</plugin>
```
&emsp;&emsp;当使用Idea进行开发时，就可以通过maven Helper插件中看到 `yapi-smart-doc` 插件，执行插件即会自动扫描项目中的相应接口文档同步至 `YApi` 平台。

![http://www.dgchong.cn/img/yapi-plugin.jpg](http://www.dgchong.cn/img/yapi-plugin.jpg "yapi-smart-doc")

&emsp;&emsp;
## 其它
1. **yapi-smart-doc maven plugin** 依赖于`smart-doc`进行接口扫描，所以相关接口规范需符合其要求。（[smart-doc文档](https://smart-doc-group.github.io/#/zh-cn/)）
2. 如你所看到的，同步API文档到`YApi`平台后，参数的数据类型和 Java 实体中的数据类型并不完全一一对应，这个是`YApi平`的数据类型支持问题。
3.**yapi-smart-doc maven plugin**支持更多灵活的配置，具体说明如下：

```xml
<plugin>
    <groupId>cn.dgchong.maven</groupId>
    <artifactId>yapi-smart-doc-maven-plugin</artifactId>
    <!-- yapi-smart-doc-maven-plugin插件版本号，建议使用最新版本 -->
    <version>【version-latest】</version>
    <configuration>
        <!-- YApi接口管理平台地址，即私有化部署YApi的服务地址 -->
        <yapiServerUrl>【YApi管理平台地址】</yapiServerUrl>
        <!-- YApi中的项目token，在YApi中一个项目有一个token,在项目中，设置-token配置选项下可查看到 -->
        <yapiProjectToken>【YApi中的项目token】</yapiProjectToken>
        <!-- 配置后，将只扫描同步指定的 Controller / package，具体配置规则同smart-doc的packageFilters配置项 -->
        <packageFilters>cn.dgchong.demo.apidoc.controller.UserController</packageFilters>
        <!-- 配置为ture，则将跳过同步 -->
        <skip>true</skip>
        <!-- 指定配置文件目录，主要可用于通用的请求头中的参数配置，如token-->
        <configFile>./src/main/resources/yapi-smart-doc.json</configFile>
    </configuration>
    <executions>
        <execution>
            <!-- 默认会在maven执行的 compile 阶段执行插件同步，如不需要，则将phase注释掉-->
            <phase>compile</phase>
            <goals>
                <goal>yapi-rest</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

yapi-smart-doc.json示例配置：

```json
{
  "requestHeaders": [
    {
      "name": "token",
      "type": "string",
      "desc": "鉴权token",
      "value": "",
      "required": true,
      "since": "-",
      "pathPatterns": "/**",
      "excludePathPatterns":"/user/**"
    }
  ]
}
```

&emsp;&emsp;
&emsp;&emsp;		
&emsp;&emsp;
&emsp;&emsp;