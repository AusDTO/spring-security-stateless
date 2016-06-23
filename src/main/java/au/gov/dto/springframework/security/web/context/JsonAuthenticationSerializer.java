package au.gov.dto.springframework.security.web.context;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.jackson2.SimpleGrantedAuthorityMixin;
import org.springframework.security.jackson2.UnmodifiableListMixin;
import org.springframework.security.jackson2.UnmodifiableSetMixin;
import org.springframework.security.jackson2.UserMixin;
import org.springframework.security.jackson2.UsernamePasswordAuthenticationTokenMixin;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.jackson2.WebAuthenticationDetailsMixin;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.Collections;

public class JsonAuthenticationSerializer implements AuthenticationSerializer {
    private ObjectMapper objectMapper = new ObjectMapper();

    public JsonAuthenticationSerializer() {
        this.objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        this.objectMapper.addMixInAnnotations(SimpleGrantedAuthority.class, SimpleGrantedAuthorityMixin.class);
        this.objectMapper.addMixInAnnotations(Collections.unmodifiableList(Collections.EMPTY_LIST).getClass(), UnmodifiableListMixin.class);
        this.objectMapper.addMixInAnnotations(Collections.unmodifiableSet(Collections.EMPTY_SET).getClass(), UnmodifiableSetMixin.class);
        this.objectMapper.addMixInAnnotations(User.class, UserMixin.class);
        this.objectMapper.addMixInAnnotations(UsernamePasswordAuthenticationToken.class, UsernamePasswordAuthenticationTokenMixin.class);
        this.objectMapper.addMixInAnnotations(WebAuthenticationDetails.class, WebAuthenticationDetailsMixin.class);
    }

    @Override
    public String serialize(Authentication authentication) {
        try {
            return objectMapper.writeValueAsString(authentication);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Problem serializing Authentication principal to JSON", e);
        }
    }

    @Override
    public Authentication deserialize(String serializedAuthentication) {
        try {
            return (Authentication) objectMapper.readValue(serializedAuthentication, Object.class);
        } catch (IOException e) {
            throw new RuntimeException("Problem deserializing JSON to Authentication", e);
        }
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        Assert.notNull(objectMapper);
        this.objectMapper = objectMapper;
    }
}
