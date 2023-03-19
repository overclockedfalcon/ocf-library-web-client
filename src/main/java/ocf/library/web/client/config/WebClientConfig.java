package ocf.library.web.client.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

	@Bean
	@Primary
	@Scope("prototype")
	public WebClient webClient(WebClient.Builder webClientBuilder) {
		return webClientBuilder.build();
	}

}
