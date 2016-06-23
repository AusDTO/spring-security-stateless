package au.gov.dto.springframework.security.web.context;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class JsonAuthenticationSerializerTest {
    @Test
    public void serializeAndDeserializeUsernamePasswordAuthenticationTokenWithUserAsPrincipal() throws Exception {
        AuthenticationSerializer serializer = new JsonAuthenticationSerializer();
        List<GrantedAuthority> userAuthorities = Arrays.asList(new SimpleGrantedAuthority("userRole1"), new SimpleGrantedAuthority("userRole2"));
        User principal = new User("username", "password", userAuthorities);
        String credentials = null;
        List<GrantedAuthority> tokenAuthorities = Arrays.asList(new SimpleGrantedAuthority("tokenRole1"), new SimpleGrantedAuthority("tokenRole2"));
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principal, credentials, tokenAuthorities);
        authentication.setDetails(new WebAuthenticationDetails(new MockHttpServletRequest()));

        String serializedAuthentication = serializer.serialize(authentication);
        Authentication deserializedAuthentication = serializer.deserialize(serializedAuthentication);

        assertThat(deserializedAuthentication.getClass(), equalTo(UsernamePasswordAuthenticationToken.class));
    }
}
