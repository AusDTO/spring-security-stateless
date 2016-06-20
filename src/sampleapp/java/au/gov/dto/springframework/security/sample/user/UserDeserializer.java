package au.gov.dto.springframework.security.sample.user;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

public class UserDeserializer extends JsonDeserializer<User> {
    @Override
    public User deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        String username = node.get("username").asText();
        String password = node.get("password").asText();
        boolean enabled = node.get("enabled").booleanValue();
        boolean accountNonExpired = node.get("accountNonExpired").booleanValue();
        boolean credentialsNonExpired = node.get("credentialsNonExpired").booleanValue();
        boolean accountNonLocked = node.get("accountNonLocked").booleanValue();
        Collection<? extends GrantedAuthority> authorities = Collections.emptyList();
        return new User(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
    }
}
