package com.azoft.webservice;

import com.azoft.api.UserDto;
import com.azoft.api.UserEditDto;
import com.azoft.api.UserServiceResponseDto;
import com.azoft.webservice.dto.BodyWrapperDto;
import com.azoft.webservice.dto.UserWebServiceResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserWebService {
    private final RabbitTemplate rabbitTemplate;

    private final AmqpAdmin amqpAdmin;

    @Value("${rabbitmq.queue.name}")
    private String staticQueueName;

    public UserWebServiceResponseDto createUser(UserDto user) throws JsonProcessingException, JSONException {
        String callbackQueueName = sendMessage(user, "create");
        UserServiceResponseDto<Object> receivedMessage = receiveMessage(callbackQueueName, Object.class);

        return new UserWebServiceResponseDto(new BodyWrapperDto<>(receivedMessage.getData(), receivedMessage.getMessage()), HttpStatus.resolve(receivedMessage.getStatus()));
    }

    public UserWebServiceResponseDto updateUser(int id, UserDto user) throws JsonProcessingException, JSONException {
        String callbackQueueName = sendMessage(new UserEditDto(id, user), "update");
        UserServiceResponseDto<Object> receivedMessage = receiveMessage(callbackQueueName, Object.class);

        return new UserWebServiceResponseDto(new BodyWrapperDto<>(receivedMessage.getData(), receivedMessage.getMessage()), HttpStatus.resolve(receivedMessage.getStatus()));
    }

    public UserWebServiceResponseDto getUser(Integer id) throws JsonProcessingException, JSONException {
        String callbackQueueName = sendMessage(id, "get");

        UserServiceResponseDto<UserDto> receivedMessage = receiveMessage(callbackQueueName, UserDto.class);

        return new UserWebServiceResponseDto(new BodyWrapperDto<>(receivedMessage.getData(), receivedMessage.getMessage()), HttpStatus.resolve(receivedMessage.getStatus()));
    }

    public UserWebServiceResponseDto deleteUser(Integer id) throws JsonProcessingException, JSONException {
        String callbackQueueName = sendMessage(id, "delete");
        UserServiceResponseDto<Object> receivedMessage = receiveMessage(callbackQueueName, Object.class);

        return new UserWebServiceResponseDto(new BodyWrapperDto<>(receivedMessage.getData(), receivedMessage.getMessage()), HttpStatus.resolve(receivedMessage.getStatus()));
    }

    private String sendMessage(Object messageToSend, String type) {
        Queue callbackQueue = new Queue(UUID.randomUUID().toString(), false, false, true);

        String callbackQueueName = amqpAdmin.declareQueue(callbackQueue);
        log.info("Callback non-durable, auto-delete queue with name [{}] was created", callbackQueueName);

        rabbitTemplate.convertAndSend(staticQueueName, messageToSend, message -> {
            message.getMessageProperties().setType(type);
            message.getMessageProperties().setReplyTo(callbackQueueName);
            return message;
        });
        log.info("Message was sent to the queue with name [{}]", staticQueueName);

        return callbackQueueName;
    }

    private <T> UserServiceResponseDto<T> receiveMessage(String callbackQueueName, Class<T> responseClassType) throws JsonProcessingException, JSONException {
        log.info("Receiving response from [{}]... ", callbackQueueName);
        String jsonResponse = new String(rabbitTemplate.receive(callbackQueueName, 10000).getBody());
        log.info("Response was received");

        return jsonToUserServiceResponseDto(jsonResponse, responseClassType);
    }

    public <T> UserServiceResponseDto<T> jsonToUserServiceResponseDto(String json, Class<T> type) throws JsonProcessingException, JSONException {
        ObjectMapper objectMapper = new ObjectMapper();
        JSONObject jsonObject = new JSONObject(json);

        UserServiceResponseDto<T> userServiceResponseDto = new UserServiceResponseDto<>();

        userServiceResponseDto.setData(objectMapper.readValue(jsonObject.getString("data"), type));
        userServiceResponseDto.setMessage(jsonObject.getString("message"));
        userServiceResponseDto.setStatus(Integer.parseInt(jsonObject.getString("status")));

        return userServiceResponseDto;
    }
}
