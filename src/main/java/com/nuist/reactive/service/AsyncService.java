package com.nuist.reactive.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;

/**
 * @Author wtx
 * @Date 2024/5/22
 */
@Service
public class AsyncService {

    @Async
    public Future<String> doSomethingAsync() {
        // 异程中执行的操作
        return new AsyncResult<>("Task completed asynchronously");
    }

}
