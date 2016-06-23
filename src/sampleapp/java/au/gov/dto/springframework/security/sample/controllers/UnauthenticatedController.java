package au.gov.dto.springframework.security.sample.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
class UnauthenticatedController {
    @RequestMapping(path = "/", method = RequestMethod.GET, produces = "text/html")
    String unauthenticated() {
        return "index";
    }
}
