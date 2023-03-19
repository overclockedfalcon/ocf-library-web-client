package ocf.library.web.client.component;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import ocf.library.web.client.exception.DownStreamDataException;
import ocf.library.web.client.exception.DownStreamUnavailableException;
import reactor.core.publisher.Mono;

@Component
public class RestClient {

	@Autowired
	private WebClient webClient;

	private <T, U> U post(String url, T request, Map<String, String> headers, Class<U> responseType) {
		return (U) doPost(url, request, modifyHeaders(headers), responseType);
	}

	private <U> U get(String url, Map<String, String> headers, Class<U> responseType) {
		return (U) doGet(url, modifyHeaders(headers), responseType);
	}

	private MultiValueMap modifyHeaders(Map<String, String> headers) {
		MultiValueMap map = new HttpHeaders();
		if (!CollectionUtils.isEmpty(headers)) {
			headers.forEach((k, v) -> map.put(k, v));
		}
		return map;
	}

	private <T, U> U doPost(String url, T request, MultiValueMap<String, String> headers, Class<U> responseType) {
		return webClient.post().uri(url).headers(items -> items.addAll(headers)).bodyValue(request).retrieve()
				.onStatus(HttpStatus::is4xxClientError, (response) -> handleBackendException(url, response, true))
				.onStatus(HttpStatus::is5xxServerError, (response) -> handleBackendException(url, response, false))
				.bodyToMono(responseType).doOnError(exception -> identifyException(exception)).block();
	}

	private <T> T doGet(String url, MultiValueMap<String, String> headers, Class<T> responseType) {
		return webClient.get().uri(url).headers(items -> items.addAll(headers)).retrieve()
				.onStatus(HttpStatus::is4xxClientError, (response) -> handleBackendException(url, response, true))
				.onStatus(HttpStatus::is5xxServerError, (response) -> handleBackendException(url, response, false))
				.bodyToMono(responseType).doOnError(exception -> identifyException(exception)).block();
	}

	private RuntimeException identifyException(Throwable exception) {
		if (exception instanceof DownStreamDataException)
			return (DownStreamDataException) exception;
		else if (exception instanceof DownStreamUnavailableException)
			return (DownStreamUnavailableException) exception;
		else
			return new DownStreamUnavailableException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "");
	}

	private Mono<? extends Throwable> handleBackendException(final String url, final ClientResponse body,
			final Boolean isDataException) {
		return body.bodyToMono(String.class).map(data -> {
			if (Boolean.TRUE.equals(isDataException))
				return new DownStreamDataException(body.statusCode(), data, url);
			else
				return new DownStreamUnavailableException(body.statusCode(), data, url);
		});

	}

}
