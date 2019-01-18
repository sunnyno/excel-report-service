package com.dzytsiuk.excelreportservice.service.processor;

import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.lang.NonNull;

import javax.jms.JMSException;
import javax.jms.Message;

public class ReplyMessagePostProcessor implements MessagePostProcessor {
    @Override
    @NonNull
    public Message postProcessMessage(@NonNull Message message) throws JMSException {
        message.setBooleanProperty("reply", true);
        return message;
    }
}
