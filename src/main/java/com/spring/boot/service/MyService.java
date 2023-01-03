package com.spring.boot.service;

import io.micrometer.core.instrument.Tags;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import io.micrometer.core.instrument.Tag;
import com.google.common.util.concurrent.AtomicDouble;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Service层
 *
 * @author 代码的路
 * @date 2023/1/3
 */

@Slf4j
@Service
@EnableScheduling
public class MyService {

    @Autowired
    private PrometheusMeterRegistry meterRegistry;

    private List<String> idList = new ArrayList();
    private HashMap<String, String> nameMap = new HashMap<>();
    private ConcurrentHashMap<String, AtomicDouble> valueMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, AtomicDouble> updateValueMap = new ConcurrentHashMap<>();

    public void setIdList() {
        idList.clear();
        idList.add("1001");
        idList.add("1002");
        idList.add("1003");
    }

    public void setNameMap() {
        nameMap.clear();
        nameMap.put("1001", "赵一");
        nameMap.put("1002", "钱二");
        nameMap.put("1003", "孙三");
    }

    public void setValueMap() {
        valueMap.clear();
        valueMap.put("1001", new AtomicDouble(1001));
        valueMap.put("1002", new AtomicDouble(1002));
        valueMap.put("1003", new AtomicDouble(1003));
    }


    @Scheduled(cron = "0/5 * * * * ?")
    public void insertPrometheus() {
        meterRegistry.clear();
        setIdList();
        setNameMap();
        setValueMap();
        for (String id : idList) {
            List<Tag> list = new ArrayList<>();
            list.add(Tag.of("id", id));
            list.add(Tag.of("name", nameMap.get(id)));
            String name = "insertPrometheus";
            double value = Double.parseDouble(String.valueOf(valueMap.get(id)));
            meterRegistry.gauge(name, Tags.of(list), value);
        }
    }

    @Scheduled(cron = "0 0/1 * * * ?")
    public void updatePrometheus() {
        String name = "updatePrometheus";
        List<Tag> list = new ArrayList<>();
        list.add(Tag.of("id", "1001"));
        list.add(Tag.of("name", "测试更新"));
        // 通过引用的方式将 Prometheus 的 value 存入 valueMap，修改 valueMap 即可修改 Prometheus
        updateValueMap.put("1001", meterRegistry.gauge(name, Tags.of(list), new AtomicDouble(0)));
        for (int value = 0; value < 12; value++) {
            try {
                updateValueMap.get("1001").set(value); //  修改 valueMap 中的 value
                Thread.sleep(5 * 1000); // 暂停5秒
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}
