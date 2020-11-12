package com.azoft.service.config;

import com.azoft.api.UserServiceResponseDto;
import com.azoft.service.exception.ResourceNotFoundException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.RabbitListenerErrorHandler;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitErrorHandler implements RabbitListenerErrorHandler {
    @Override
    public UserServiceResponseDto<?> handleError(Message amqpMessage, org.springframework.messaging.Message<?> message, ListenerExecutionFailedException exception) throws Exception {
        Throwable throwable = exception.getCause();

        if (throwable instanceof ResourceNotFoundException) {
            return new UserServiceResponseDto<>(null, 404, throwable.getMessage());
        }

        return new UserServiceResponseDto<>(null, 500, "");
    }
}

