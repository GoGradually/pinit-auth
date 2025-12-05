package me.gg.pinit.controller.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OauthLoginSetting {
    private String response_type;
    private String client_id;
    private String redirect_uri;
    private String state;
}
