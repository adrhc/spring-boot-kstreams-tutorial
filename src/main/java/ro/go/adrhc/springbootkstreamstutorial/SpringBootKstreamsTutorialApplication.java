package ro.go.adrhc.springbootkstreamstutorial;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties
@SpringBootApplication
public class SpringBootKstreamsTutorialApplication {
	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(SpringBootKstreamsTutorialApplication.class, args);
		Thread.sleep(Long.MAX_VALUE);
	}
}
