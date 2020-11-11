package com.azoft.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private String name;

    private String surname;

    private String patronymic;

    private String email;
}
