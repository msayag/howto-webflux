package io.github.msayag.webflux;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.ThreadLocalRandom;

import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

@RestController
public class MyController {
    /**
     * Returns a single value
     */
    @GetMapping("/str")
    public Mono<String> getOneString() {
        return Mono.just("Hello");
    }

    /**
     * Returns a collection of values
     */
    @GetMapping("/list")
    public Flux<String> getSomeStrings() {
        return Flux.just("Hello", "World");
    }

    /**
     * Returns an infinite stream of values
     */
//    @GetMapping(path = "/infinite", produces = TEXT_EVENT_STREAM_VALUE)
//    public Flux<Long> getStreamOfLongs() {
//        return Flux.generate(sink -> sink.next(randomLong()));
//    }
    private Long randomLong() {
        return ThreadLocalRandom.current().nextLong();
    }

    @GetMapping(path = "/infinite", produces = TEXT_EVENT_STREAM_VALUE)
    public Flux<String> getStreamOfLongs() {
        return Flux.generate(sink -> sink.next("x"));
    }
}
