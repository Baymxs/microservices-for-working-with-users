package com.azoft.service;


import com.azoft.api.UserDto;
import com.azoft.api.UserEditDto;
import com.azoft.api.UserServiceResponseDto;
import com.azoft.service.entity.User;
import com.azoft.service.exception.ResourceNotFoundException;
import com.azoft.service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queues = "${rabbitmq.queue.name}", errorHandler = "rabbitErrorHandler", returnExceptions = "true")
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    @RabbitHandler
    public UserServiceResponseDto<?> userDtoHandler(@Payload UserDto userDto, @Header(AmqpHeaders.TYPE) String requestType) {
        if (requestType.equals("create")) {
            if (userRepository.findByEmail(userDto.getEmail()) == null) {
                User user = new User(null, userDto.getName(), userDto.getSurname(), userDto.getPatronymic(), userDto.getEmail());

                userRepository.save(user);
                log.info("User with email [{}] was created", user.getEmail());

                return new UserServiceResponseDto<>(null, 200, "Success");
            } else {
                log.info("User with email [{}] has already been created", userDto.getEmail());
                return new UserServiceResponseDto<>(null, 400, "User with email [" + userDto.getEmail() + "] has already been created");
            }
        }
        log.info("Unable to process a request of the [{}] type for payload [UserDto] type", requestType);
        return new UserServiceResponseDto<>(null, 405, "Unable to process a request of the [" + requestType + "] type for payload [UserDto] type");
    }

    @RabbitHandler
    public UserServiceResponseDto<?> userEditDtoHandler(@Payload UserEditDto userEditDto, @Header(AmqpHeaders.TYPE) String requestType) {
        if (requestType.equals("update")) {
            User user = new User();

            user.setId(userEditDto.getId());
            user.setName(userEditDto.getUser().getName());
            user.setSurname(userEditDto.getUser().getSurname());
            user.setPatronymic(userEditDto.getUser().getPatronymic());
            user.setEmail(userEditDto.getUser().getEmail());

            userRepository.save(user);

            log.info("User with id [{}] was updated", userEditDto.getId());
            return new UserServiceResponseDto<>(null, 200, "Success");
        }
        log.info("Unable to process a request of the [{}] type for payload [UserEditDto] type", requestType);
        return new UserServiceResponseDto<>(null, 405, "Unable to process a request of the [" + requestType + "] type for payload [UserEditDto] type");
    }

    @RabbitHandler
    public Object integerHandler(@Payload Integer id, @Header(AmqpHeaders.TYPE) String requestType) {
        if (requestType.equals("get")) {
            User user = userRepository.
                    findById(id).
                    orElseThrow(() -> new ResourceNotFoundException("User with id [" + id + "] not found"));

            log.info("User with id [{}] was received", id);
            return new UserServiceResponseDto<>(new UserDto(user.getName(), user.getSurname(), user.getPatronymic(), user.getEmail()), 200, "Success");
        } else if (requestType.equals("delete")) {
            userRepository.deleteById(id);

            log.info("User with id [{}] was deleted", id);
            return new UserServiceResponseDto<>(null, 200, "Success");
        }
        log.info("Unable to process a request of the [{}] type for payload [Integer] type", requestType);
        return new UserServiceResponseDto<>(null, 405, "Unable to process a request of the [" + requestType + "] type for payload [Integer] type");
    }
}
