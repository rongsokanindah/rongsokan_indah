package rongsokan.indah.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import jakarta.servlet.http.HttpServletRequest;
import rongsokan.indah.constants.AttributeConstant;

@Service
public class PageService {

    @Autowired
    private AttributeConstant attribute;

    public String getLoginPage(HttpServletRequest request, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            return "redirect:/dashboard";
        } else {
            String loginError = (String) request.getSession().getAttribute(attribute.getLoginError());

            if(loginError != null) {
                model.addAttribute(attribute.getLoginError(), loginError);
                request.getSession().removeAttribute(attribute.getLoginError());
            }
            return "pages/login";
        }
    }
}