package me.gg.pinit.infrastructure.oauth.dto;

import lombok.Getter;
import lombok.Setter;
import me.gg.pinit.domain.oidc.Profile;

@Getter
@Setter
public class OpenIdProfileResponse {
    private String resultcode;
    private String message;
    private Profile response;
}
