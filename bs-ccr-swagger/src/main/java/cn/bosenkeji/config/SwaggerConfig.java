package cn.bosenkeji.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.*;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ApiListingScannerPlugin;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.spring.web.readers.operation.CachingOperationNameGenerator;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @ClassName SwaggerConfig
 * @Description TODO
 * @Author Yu XueWen
 * @Email 8586826@qq.com
 * @Versio V1.0
 **/

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    final List<ResponseMessage> globalResponses = Arrays.asList(
            new ResponseMessageBuilder()
                    .code(200)
                    .message("成功")
                    .responseModel(new ModelRef("Success"))
                    .build(),
            new ResponseMessageBuilder()
                    .code(201)
                    .message("添加或者修改成功")
                    .build(),
            new ResponseMessageBuilder()
                    .code(400)
                    .message("错误请求")
                    .build(),
            new ResponseMessageBuilder()
                    .code(401)
                    .message("未授权")
                    .build(),
            new ResponseMessageBuilder()
                    .code(403)
                    .message("拒绝请求")
                    .build(),
            new ResponseMessageBuilder()
                    .code(404)
                    .message("未找到相关的请求")
                    .build(),
            new ResponseMessageBuilder()
                    .code(500)
                    .message("服务器内部错误")
                    .build());
    @Bean
    public Docket createRestApi() {
        ParameterBuilder authorizationParameterBuilder = new ParameterBuilder();
        authorizationParameterBuilder.name("Authorization").description("认证Token").modelRef(new ModelRef("string")).parameterType("header").required(true).build();


        List<Parameter> addParameters = new ArrayList<Parameter>();
        addParameters.add(authorizationParameterBuilder.build());

        return new Docket(DocumentationType.SWAGGER_2).extensions(getExtension())
                .apiInfo(getApiInfo())
                    .select()
                    .apis(RequestHandlerSelectors.basePackage("cn.bosenkeji.controller"))
                    .paths(PathSelectors.any())
                    .build()
                .useDefaultResponseMessages(false)
                .globalResponseMessage(RequestMethod.GET,globalResponses)
                .globalResponseMessage(RequestMethod.POST,globalResponses)
                .globalResponseMessage(RequestMethod.PUT,globalResponses)
                .globalResponseMessage(RequestMethod.DELETE,globalResponses)
                .globalOperationParameters(addParameters)
                ;
    }


    /**
     * @Description 增加顶级扩展，ListVendorExtension表示以列表形式
     * @Author Yu XueWen
     * @return List<VendorExtension>
     */
    private List<VendorExtension> getExtension(){

        List<VendorExtension> vendorExtensionsList = new ArrayList<>();

        List<String> stringList = new ArrayList<>();
        stringList.add("http");

        VendorExtension listVendorExtension = new ListVendorExtension<String>("schemes", stringList);

        vendorExtensionsList.add(listVendorExtension);

        return vendorExtensionsList;
    }

    private ApiInfo getApiInfo() {
        // 定义联系人信息
        Contact contact = new Contact("YuXueWen","https://github.com/xiaoemoxiw", "yuxuewen23@qq.com");
        return new ApiInfoBuilder()
                .title("博森CCR系统") // 标题
                .description("博森CCR系统API信息") // 描述信息
                .version("1.0.0") // //版本
                .license("Apache 2.0")
                .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0")
                .contact(contact)
                .build();
    }

    @Autowired
    private CachingOperationNameGenerator cachingOperationNameGenerator;


    @Bean
    public UiConfiguration uiConfig() {
        return UiConfigurationBuilder.builder().validatorUrl("").build();
    }
}
