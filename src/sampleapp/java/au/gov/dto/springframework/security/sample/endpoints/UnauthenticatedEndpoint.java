package au.gov.dto.springframework.security.sample.endpoints;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
class UnauthenticatedEndpoint {
    @RequestMapping(path = "/", method = RequestMethod.GET, produces = "text/plain")
    ResponseEntity<String> unauthenticated() {
        return new ResponseEntity<>("Unauthenticated endpoint", HttpStatus.OK);
    }
}
