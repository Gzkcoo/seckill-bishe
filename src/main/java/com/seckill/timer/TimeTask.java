package com.seckill.timer;

import com.seckill.dao.AnnounceDOMapper;
import com.seckill.dataobject.AnnounceDO;
import com.seckill.service.model.UserModel;
import com.seckill.socket.WebSocket;
import org.checkerframework.checker.units.qual.A;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@EnableScheduling
public class TimeTask {

    @Autowired
    private WebSocket webSocket;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private AnnounceDOMapper announceDOMapper;

    //顾客预定秒杀活动的通知
    //十秒推送一次
    @Scheduled(cron = "*/20 * * * * ?")
    public void sendMsgByUserId(){
        CopyOnWriteArraySet<WebSocket> webSocketSet = webSocket.getWebSocketSet();
        if( webSocketSet == null || webSocketSet.size() <= 0 ){
            return;
        }

        for(WebSocket w : webSocketSet){
            UserModel userModel = (UserModel) redisTemplate.opsForValue().get(w.getToken());
            if (userModel == null){
                continue;
            }
            List<AnnounceDO> list = announceDOMapper.selectAnnounceDOByUserId(userModel.getId());
            if (list != null){
                for (AnnounceDO announceDO : list){
                    if (announceDO.getFlag() > 0){
                        continue;
                    }
                    Date nowTime = new Date();
                    //在秒杀开始前15分钟内发布提醒
                    if (announceDO.getPostTime().getTime() - nowTime.getTime() > 0 && announceDO.getPostTime().getTime() - nowTime.getTime() < 15*60*1000){
                            w.sendMessage(announceDO.getContent());
                            announceDO.setFlag((byte) 1);
                            announceDOMapper.updateByPrimaryKeySelective(announceDO);
                    }
                }
            }
        }
    }


}
