package com.nuist.reactive.controller;

import com.nuist.reactive.service.AsyncService;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import reactor.util.function.Tuple2;

import java.util.concurrent.ThreadFactory;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @Author wtx
 * @Date 2024/5/22
 */
@RestController
@RequestMapping("/reactor")
public class ReactorController {

    /**
     * 基于Flux的异步
     */
    @GetMapping("/webflux")
    public Mono<?> flux() {
        Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("1", "2", "3", "4"))
                // 模拟 每一个元素 延迟 3 * 4 = 12
                .delayElements(Duration.ofSeconds(3));
        return stringFlux.collectList();
    }


    @Autowired
    private AsyncService asyncService;

    /**
     * 使用Spring @Async以及线程池 实现执行异步任务
     */
    @GetMapping("/async")
    public String async() throws ExecutionException, InterruptedException {
        return asyncService.doSomethingAsync().get();
    }

    private static final String SAY = "千万刀锋之力，万丈烈焰之怒在我心中鼓荡。 ";

    @GetMapping(path = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamEvents() {
        // usingWhen 创建响应式对象
        return Flux.usingWhen(
                // 第一个参数为响应式流提供资源
                Mono.fromSupplier(new Supplier<Sinks.Many<String>>() {
                    @Override
                    public Sinks.Many<String> get() {
                        // 使用Sinks.many().unicast().onBackpressureBuffer()方法创建一个Sinks.Many对象
                        // 这个对象可以向多个订阅者发送数据，并缓存未消费的数据
                        // 1. 但是这里可能出现资源泄露风险，每个请求都会创建一个新的Sinks对象，可以使用单例的Sinks对象
                        return Sinks.many().unicast().onBackpressureBuffer();
                    }
                }),
                // 使用Function接口实现apply方法，根据资源生成数据流
                new Function<Sinks.Many<String>, Flux<String>>() {
                    @Override
                    public Flux<String> apply(Sinks.Many<String> sink) {
                        return
                                // 获取数据流对象
                                // 2. sink.asFlux() 当前逻辑中并没有数据流入
                                sink.asFlux()
                                        // 合并数据流对象
                                        .mergeWith(
                                                // 按照指定的时间间隔发出连续的长整型数字
                                                Flux.interval(Duration.ofMillis(100))
                                                        // 限制数据流序号范围
                                                        .takeWhile(
                                                                seq -> seq.intValue() < SAY.length()
                                                        )
                                                        // 将数据流的序号映射成字符串中的对应字符并加上换行符
                                                        .map(
                                                                seq -> String.valueOf(SAY.charAt(seq.intValue()))
                                                        )
                                        );
                    }
                },
                // 释放资源
                new Function<Sinks.Many<String>, Publisher<?>>() {
                    @Override
                    public Publisher<?> apply(Sinks.Many<String> sink) {
                        // 关闭Sinks对象
                        return Mono.fromRunnable(sink::tryEmitComplete);
                    }
                }

        );
    }

    private final Sinks.Many<String> sink = Sinks.many().unicast().onBackpressureBuffer();

    @GetMapping(path = "/v2/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamEvents2() {

        return sink.asFlux().mergeWith(
                Flux.interval(Duration.ofMillis(100))
                        .onBackpressureDrop()
                        .zipWithIterable(SAY.chars().mapToObj(c -> String.valueOf((char) c)).collect(Collectors.toList()))
                        .map(Tuple2::getT2)
        );
    }


}
