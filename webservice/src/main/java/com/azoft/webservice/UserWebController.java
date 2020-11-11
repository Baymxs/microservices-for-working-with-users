package com.azoft.webservice;

import com.azoft.api.UserDto;
import com.azoft.webservice.dto.UserWebServiceResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/user")
@RequiredArgsConstructor
public class UserWebController {
    private final UserWebService userWebService;

    @PostMapping("/create")
    public UserWebServiceResponseDto create(@RequestBody UserDto user) throws JsonProcessingException, JSONException {
        return userWebService.createUser(user);
    }

    @PutMapping("/{id}")
    public UserWebServiceResponseDto update(@PathVariable Integer id, @RequestBody UserDto user) throws JsonProcessingException, JSONException {
        return userWebService.updateUser(id, user);
    }

    @GetMapping("/{id}")
    public UserWebServiceResponseDto get(@PathVariable Integer id) throws JsonProcessingException, JSONException {
        return userWebService.getUser(id);
    }

    @DeleteMapping("/{id}")
    public UserWebServiceResponseDto delete(@PathVariable Integer id) throws JsonProcessingException, JSONException {
        return userWebService.deleteUser(id);
    }
}



