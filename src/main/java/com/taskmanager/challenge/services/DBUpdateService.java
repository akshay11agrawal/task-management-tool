package com.taskmanager.challenge.services;

import com.taskmanager.challenge.model.CountingTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class DBUpdateService {

    private final RedisTemplate<String, CountingTask> redisTemplate;

    private final RedissonClient redissonClient;

    private final TaskService taskService;
    private static final String LOCK_KEY = "countProgressLock";

    @Async("asyncTaskExecutor")
    @Scheduled(cron = "*/50 * * * * ?")
    public void updateProgressToDatabase() {

        RLock lock = redissonClient.getLock(LOCK_KEY);

        if (lock.tryLock()) {
            try {

                List<CountingTask> tasksList = new ArrayList<>();

                Set<String> keys = redisTemplate.keys("*-*");
                if (!CollectionUtils.isEmpty(keys)) {
                    keys.forEach(key -> tasksList.add(redisTemplate.opsForValue().get(key)));
                }

                //bulk update db call
                if(!tasksList.isEmpty()){
                    taskService.bulkUpdateProgress(tasksList);
                    log.debug("DB update completed : " + Thread.currentThread().getName()+" Updated records :"
                            +tasksList.size());
                }
            } finally {
                lock.unlock();
            }
        }
    }
}
