package au.gov.dto.springframework.security.sample.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@Controller
class AuthenticatedController {
    @RequestMapping(path = "/authenticated", method = RequestMethod.GET, produces = "text/html")
    ModelAndView authenticated(@AuthenticationPrincipal User user, CsrfToken token) {
        Map<String, Object> model = new HashMap<>();
        model.put("csrf", token);
        model.put("username", user.getUsername());
        return new ModelAndView("authenticated", model);
    }
}
