package me.gg.pinit.controller.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NaverLoginResult {
    private String code;
    private String state;
    private String error;
    private String error_description;
}
