package security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {
    private List<String> publicUris = new ArrayList<>();

    public List<String> getPublicUris() {
        return publicUris;
    }

    public void setPublicUris(List<String> publicUris) {
        this.publicUris = publicUris;
    }
}