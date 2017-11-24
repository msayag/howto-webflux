package io.github.msayag.webflux;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM;

public class NonBlockingClient {
    public static void main(String... args) throws InterruptedException {
        new NonBlockingClient().processOneString(str -> process(str));
        new NonBlockingClient().processSomeStrings(str -> process(str));
        new NonBlockingClient().processStreamOfLongs(l -> process(l));

        keepProgramAlive();
    }

    public void processOneString(Consumer<String> consumer) {
        WebClient client = WebClient.create("http://localhost:8080");
        Mono<String> mono = client.get()
                .uri("/str")
                .accept(APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class);
        mono.subscribe(consumer);
    }

    public void processSomeStrings(Consumer<String> consumer) {
        WebClient client = WebClient.create("http://localhost:8080");
        Flux<String> flux = client.get()
                .uri("/list")
                .accept(TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(String.class);
        flux.subscribe(consumer,
                t -> t.printStackTrace(),
                () -> System.out.println("DONE"));
    }

    public void processStreamOfLongs(Consumer<Long> consumer) {
        WebClient client = WebClient.create("http://localhost:8080");
        Flux<Long> flux = client.get()
                .uri("/infinite")
                .accept(TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(Long.class);
        Disposable disposable = flux.subscribe(consumer,
                t -> t.printStackTrace(),
                () -> System.out.println("DONE"));
        Executors.newSingleThreadScheduledExecutor().schedule(() -> {
            System.out.println("DISPOSING");
            disposable.dispose();
        }, 5, TimeUnit.SECONDS);
    }

    private static void keepProgramAlive() throws InterruptedException {
        while (true) {
            Thread.sleep(1000);
        }
    }

    private static <T> void process(T t) {
        System.out.println(t);
    }
}
