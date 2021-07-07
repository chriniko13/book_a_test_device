package com.chriniko.mob.booking.service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@NoArgsConstructor
@Data
public class BookMobileInfo {

    @NotEmpty(message = "User cannot be empty or null")
    private String user;
}
