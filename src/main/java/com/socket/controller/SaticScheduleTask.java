package com.socket.controller;

import com.socket.service.InstancemessagedayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Configuration
@EnableScheduling
public class SaticScheduleTask {
    @Autowired
    private InstancemessagedayService instancemessagedayService;
    @Scheduled(cron = "0/5 * * * * ?")
    private void configureTasks() {
        System.err.println("执行静态定时任务时间: " + LocalDateTime.now());
    }
    @Scheduled(cron = "0 59 23 * * ?")
    private void delMsgLog(){
        //每晚删除前一天的已读的数据
        instancemessagedayService.delete();
    }
}