package com.sg.assignment.config;

import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.sg.assignment.common.jwt.JwtInfo;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@Configuration
public class SwaggerConfig {

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2).apiInfo(getApiInfo()).select()
				.apis(RequestHandlerSelectors.any()).paths(PathSelectors.any()).build()
				.useDefaultResponseMessages(false)
				.globalOperationParameters(Arrays.asList(new ParameterBuilder().name(JwtInfo.TOKEN_NAME)
						.description("JWT Header").modelRef(new ModelRef("string")).parameterType("header").build()));
	}

	private ApiInfo getApiInfo() {
		return new ApiInfoBuilder().title("SG API").description("TEST API")
				.contact(new Contact("SG Swagger", "", "sg.object@gmail.com")).version("1.0").build();
	}
}
