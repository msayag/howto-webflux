package io.github.msayag.webflux;

import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.function.Consumer;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM;

public class BlockingClient {
    public static void main(String... args) throws InterruptedException {
        BlockingClient client = new BlockingClient();
        client.processOneString(str -> process(str));
        client.processSomeStrings(str -> process(str));
    }

    public void processOneString(Consumer<String> consumer) {
        WebClient client = WebClient.create("http://localhost:8080");
        String str = client.get()
                .uri("/str")
                .accept(APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        consumer.accept(str);
    }

    public void processSomeStrings(Consumer<String> consumer) {
        WebClient client = WebClient.create("http://localhost:8080");
        List<String> list = client.get()
                .uri("/list")
                .accept(TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(String.class)
                .collectList()
                .block();
        for (String str : list) {
            consumer.accept(str);
        }
    }

    private static void process(String str) {
        System.out.println(str);
    }
}
