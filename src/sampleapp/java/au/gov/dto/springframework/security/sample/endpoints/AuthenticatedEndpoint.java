package au.gov.dto.springframework.security.sample.endpoints;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
class AuthenticatedEndpoint {
    @RequestMapping(path = "/authenticated", method = RequestMethod.GET, produces = "text/plain")
    ResponseEntity<String> authenticated(@AuthenticationPrincipal User user) {
        return new ResponseEntity<>("Authenticated user: " + user.getUsername(), HttpStatus.OK);
    }
}
