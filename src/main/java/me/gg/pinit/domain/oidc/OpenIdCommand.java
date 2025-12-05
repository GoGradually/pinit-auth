package me.gg.pinit.domain.oidc;

import lombok.Getter;

@Getter
public abstract class OpenIdCommand {
    private String grant_type;
    private String client_id;
    private String client_secret;

    public OpenIdCommand(String grant_type, String client_id, String client_secret) {
        this.grant_type = grant_type;
        this.client_id = client_id;
        this.client_secret = client_secret;
    }

    public abstract void execute(Oauth2Provider provider);
}