package au.gov.dto.servlet.http;

import org.junit.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletRequest;

import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class NoHttpSessionFilterTest {
    @Test(expected = UnsupportedOperationException.class)
    public void doFilterPropagatesRequestThatThrowsExceptionWhenHttpSessionIsAccessedOrCreated() throws Exception {
        MockFilterChain filterChain = new MockFilterChain();
        new NoHttpSessionFilter().doFilter(new MockHttpServletRequest(), new MockHttpServletResponse(), filterChain);
        ((HttpServletRequest) filterChain.getRequest()).getSession();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void doFilterPropagatesRequestThatThrowsExceptionWhenHttpSessionIsAccessedWithFlagSetToCreate() throws Exception {
        MockFilterChain filterChain = new MockFilterChain();
        new NoHttpSessionFilter().doFilter(new MockHttpServletRequest(), new MockHttpServletResponse(), filterChain);
        ((HttpServletRequest) filterChain.getRequest()).getSession(true);
    }

    @Test
    public void doFilterPropagatesRequestThatReturnsNullWhenHttpSessionIsAccessedWithFlagSetToNotCreate() throws Exception {
        MockFilterChain filterChain = new MockFilterChain();
        new NoHttpSessionFilter().doFilter(new MockHttpServletRequest(), new MockHttpServletResponse(), filterChain);
        assertThat(((HttpServletRequest) filterChain.getRequest()).getSession(false), nullValue());
    }
}
