package CG.admin;

import CG.admin.model.AdminDetails;
import CG.admin.model.WashPacks;
import CG.admin.repository.AdminRepo;
import CG.admin.repository.WashPackRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

@EnableEurekaClient
@EnableSwagger2
@SpringBootApplication
public class AdminApplication implements CommandLineRunner {
	@Autowired
	private AdminRepo ar;
	@Autowired
	private WashPackRepo wpr;
	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}
	public static void main(String[] args) {
		SpringApplication.run(AdminApplication.class, args);
	}
	@Bean
	public Docket SwaggerConfig(){
		//Returns a prepared docket instance
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.paths(PathSelectors.ant("/admins/**"))
				.apis(RequestHandlerSelectors.basePackage("CG.admin"))
				.build()
				.apiInfo(apiinform());

	}
	private ApiInfo apiinform(){
		return new ApiInfo(
				"Admin's Panel",
				"The Admin has all the below controls",
				"1.0",
				"Can be used by anyone testing the app",
				new springfox.documentation.service.Contact("Vivek Shukla","https://github.com/vivekshukla24","vivek@gmail.com"),
				"API license",
				"https://github.com/vivekshukla24",
				Collections.emptyList());
	}
	@Override
	public void run(String... args) throws Exception {
		if(ar.findAll().isEmpty()){
			ar.save(new AdminDetails(1,"Vivek Shukla","vivek123@"));
		}
		if(wpr.findAll().isEmpty()){
			wpr.save(new WashPacks(1,"All clean pack",2500,"Interior cleaning, Exterior cleaning, Mats cleaning, polishing"));
			wpr.save(new WashPacks(2,"Selective Pack",1500,"Interior cleaning, Exterior cleaning"));
		}
	}
}
