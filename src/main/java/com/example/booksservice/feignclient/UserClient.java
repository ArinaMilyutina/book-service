package com.example.booksservice.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "library2")
public interface UserClient {

    @GetMapping("/user/current-user")
    Long getCurrentUserId();
}
