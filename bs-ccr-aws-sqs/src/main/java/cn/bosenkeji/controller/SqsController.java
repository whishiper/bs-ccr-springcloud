package cn.bosenkeji.controller;

import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author CAJR
 * @create 2019/8/19 17:40
 */
@RestController
@Api(value = "sqs ")
public class SqsController {

    private static final Logger LOG = LoggerFactory.getLogger(SqsController.class);
    private static final String QUEUE_NAME = "bs-ccr-test";

    @Autowired
    QueueMessagingTemplate queueMessagingTemplate;

    @Value("${cloud.aws.end-point.uri}")
    private String sqsEndPoint;


    @GetMapping("/send")
    public void sendMessageToMessageProcessingQueue(@RequestParam String message){
        LOG.info("send Message  -->"+message);
        this.queueMessagingTemplate.send(sqsEndPoint, MessageBuilder.withPayload(message).build());
    }
}