package com.microservices.lab.transfers.service.impl;

import com.microservices.lab.transfers.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service("NotificationService")
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    KafkaTemplate<String, String> kafkaTemplate;
    private static final String topic = "internal-transfer";
    public @Autowired NotificationServiceImpl(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    @Async("notificationExecutor")
    public void notifyAsync(String content) {
        try {
            kafkaTemplate.send("internal-transfer", content);
            logInfo(String.format("Just sent '%s' to '%s' topic on %s", content, topic, new Date()));
        }catch(Exception cause) {
            logError(String.format("Can't send '%s' to '%s' topic on %s", content, topic, new Date()), cause);
        }
    }

    void logInfo(String info) {
        log.info(info);
    }

    void logError(String error, Throwable cause) {
        log.error(error, cause);
    }
}
