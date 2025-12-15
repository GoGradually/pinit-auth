package me.gg.pinit.infrastructure.oauth.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import me.gg.pinit.domain.oidc.OpenIdCommand;
import me.gg.pinit.domain.oidc.OpenIdPublishCommand;
import me.gg.pinit.domain.oidc.OpenIdRefreshCommand;
import me.gg.pinit.domain.oidc.OpenIdRevokeCommand;

@Slf4j
@Getter
@Setter
public class OpenIdTokenRequest {
    private String grant_type;
    private String client_id;
    private String client_secret;
    private String code;
    private String state;
    private String refresh_token;
    private String access_token;
    private String service_provider;

    public static OpenIdTokenRequest from(OpenIdCommand command, String client_id, String client_secret, String service_provider) {
        OpenIdTokenRequest request = new OpenIdTokenRequest();
        request.setGrant_type(command.getGrantType());
        request.setClient_id(client_id);
        request.setClient_secret(client_secret);
        if (command instanceof OpenIdRefreshCommand refreshCommand) {
            request.setRefresh_token((refreshCommand.getRefreshToken()));
        }
        if (command instanceof OpenIdPublishCommand publishCommand) {
            request.setCode(publishCommand.getCode());
            request.setState(publishCommand.getState());
        }
        if (command instanceof OpenIdRevokeCommand revokeCommand) {
            request.setAccess_token(revokeCommand.getAccessToken());
            request.setService_provider(service_provider);
        }
        return request;
    }

    @Override
    public String toString() {
        return "OpenIdTokenRequest{" +
                "grant_type='" + grant_type + '\'' +
                ", client_id='" + client_id + '\'' +
                ", client_secret='" + client_secret + '\'' +
                ", code='" + code + '\'' +
                ", state='" + state + '\'' +
                ", refresh_token='" + refresh_token + '\'' +
                ", access_token='" + access_token + '\'' +
                ", service_provider='" + service_provider + '\'' +
                '}';
    }
}
