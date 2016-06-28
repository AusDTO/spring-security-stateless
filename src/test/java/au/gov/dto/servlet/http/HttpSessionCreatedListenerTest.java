package au.gov.dto.servlet.http;

import org.junit.Test;
import org.springframework.mock.web.MockHttpSession;

import javax.servlet.http.HttpSessionEvent;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class HttpSessionCreatedListenerTest {
    @Test
    public void sessionShouldBeInvalidatedOnCreatedEvent() throws Exception {
        MockHttpSession httpSession = new MockHttpSession();

        new HttpSessionCreatedListener().sessionCreated(new HttpSessionEvent(httpSession));

        assertThat(httpSession.isInvalid(), equalTo(true));
    }

    @Test
    public void noExceptionIfSessionInCreatedEventIsAlreadyInvalid() throws Exception {
        MockHttpSession httpSession = new MockHttpSession();
        httpSession.invalidate();

        new HttpSessionCreatedListener().sessionCreated(new HttpSessionEvent(httpSession));

        assertThat(httpSession.isInvalid(), equalTo(true));
    }
}
