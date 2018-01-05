package com.whh.middleware.kafka.demo;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author huahui.wu.
 *         Created on 2018/1/5.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring-config.xml")
@Transactional
public class KafkaTestProducerTest {

    @Resource
    private KafkaTestProducer kafkaTestProducer;

    @Test
    public void testTest1() throws Exception {
        kafkaTestProducer.test();
    }

}