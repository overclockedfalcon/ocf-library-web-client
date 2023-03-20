package ocf.library.web.client.config;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

@Configuration
public class WebClientConfig {

	@Value("${webClient.connection.maxConnection:200}")
	private int maxConnection;
	@Value("${webClient.connection.maxIdleTime:25000}")
	private long maxIdleTime;
	@Value("${webClient.connection.readTimeout:5000}")
	private int readTimeoutInMs;
	@Value("${webClient.connection.writeTimeout:5000}")
	private int writeTimeoutInMs;
	@Value("${webClient.connection.bufferInMB:5}")
	private int bufferInMB;

	@Bean
	@Primary
	@Scope("prototype")
	public WebClient webClient(WebClient.Builder webClientBuilder) {

		ConnectionProvider connectionProvider = ConnectionProvider.builder("").maxConnections(maxConnection)
				.maxIdleTime(Duration.ofMillis(maxIdleTime)).build();

		HttpClient httpClient = HttpClient.create(connectionProvider);
		configureHttpClient(httpClient);
		return webClientBuilder
				.exchangeStrategies(ExchangeStrategies.builder()
						.codecs(config -> config.defaultCodecs().maxInMemorySize(bufferInMB * 1024 * 1024)).build())
				.clientConnector(new ReactorClientHttpConnector(httpClient)).build();

	}

	private void configureHttpClient(HttpClient httpClient) {
		httpClient
				.doOnConnected(con -> con.addHandlerLast(new ReadTimeoutHandler(readTimeoutInMs, TimeUnit.MILLISECONDS))
						.addHandlerLast(new WriteTimeoutHandler(writeTimeoutInMs, TimeUnit.MILLISECONDS)));

	}

}
