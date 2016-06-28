package au.gov.dto.servlet.http;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class HttpSessionCreatedListener implements HttpSessionListener {
    private final Log logger = LogFactory.getLog(this.getClass());

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        String stackTrace = StringUtils.arrayToDelimitedString(Thread.currentThread().getStackTrace(), " ");
        logger.warn("HttpSession was created: " + stackTrace);
        if (event.getSession() == null) {
            return;
        }
        try {
            logger.warn("Invalidating unexpected HttpSession");
            event.getSession().invalidate();
        } catch (IllegalStateException e) {
            logger.warn("Could not invalidate already invalidated HttpSession", e);
        }
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
    }
}
