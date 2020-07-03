package com.mq.producer.config;

import com.google.common.base.Predicates;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@ConditionalOnProperty(prefix="swagger",value={"enable"},havingValue="true")
@EnableSwagger2
public class Swagger2 {
	/**
	 * createRestApi函数创建Docket的Bean
	 * apiInfo()用来创建该Api的基本信息(这些基本信息会展现在文档页面中)
	 * select()函数返回一个ApiSelectorBuilder实例用来控制哪些接口暴露给Swagger来展现
	 * 本例采用指定扫描的包路径来定义，Swagger会扫描该包下所有Controller定义的API
	 * 并产生文档内容(除了被@ApiIgnore指定的请求)
	 * @return
	 */
	@Bean
	public Docket createRestApi(){
		return new Docket(DocumentationType.SWAGGER_2)
				.apiInfo(apiInfo())
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.mq.producer.controller"))
				.paths(Predicates.not(PathSelectors.regex("/error.*")))
				.build();
	}


	private ApiInfo apiInfo(){
		return new ApiInfoBuilder()
				.title("拓维盛通")
				.description("Mq 生产者")
				.version("1.0.0")
				.build();
	}
}
