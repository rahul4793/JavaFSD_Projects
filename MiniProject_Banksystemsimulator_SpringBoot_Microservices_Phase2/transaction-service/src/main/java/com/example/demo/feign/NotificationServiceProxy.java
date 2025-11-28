package com.example.demo.feign;


import com.example.demo.dto.NotificationDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "NOTIFICATION-SERVICE") 
public interface NotificationServiceProxy {
    @PostMapping("/api/notifications/send")
    void sendNotification(@RequestBody NotificationDto notification);
}