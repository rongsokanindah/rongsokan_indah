package rongsokan.indah.configs;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import rongsokan.indah.constants.AttributeConstant;

@Component
public class AuthFailureHandlerConfig implements AuthenticationFailureHandler {

    @Autowired
    private AttributeConstant attributeConstant;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
        request.getSession().setAttribute(attributeConstant.getLoginError(), exception.getMessage());
        response.sendRedirect("/login");
    }
}