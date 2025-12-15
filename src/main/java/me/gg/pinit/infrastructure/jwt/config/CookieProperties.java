package me.gg.pinit.infrastructure.jwt.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.cookie")
public class CookieProperties {
    /**
     * Domain that will receive token cookies (e.g. ".pinit.go-gradually.me").
     * Leave blank to fall back to the current host only.
     */
    private String domain = ".pinit.go-gradually.me";
    /**
     * Whether the cookies should be marked Secure. Must be true when sameSite is "None".
     */
    private boolean secure = true;
    /**
     * SameSite attribute for token cookies.
     */
    private String sameSite = "None";

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public boolean isSecure() {
        return secure;
    }

    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    public String getSameSite() {
        return sameSite;
    }

    public void setSameSite(String sameSite) {
        this.sameSite = sameSite;
    }
}
