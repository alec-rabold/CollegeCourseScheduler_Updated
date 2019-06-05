package io.collegeplanner.my.collegecoursescheduler.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import static io.collegeplanner.my.collegecoursescheduler.util.Constants.*;

@Controller
public class ErrorHandlingController implements ErrorController {

    @RequestMapping(value = "/error")
    public String handleError(final HttpServletRequest request,
                              final ModelMap modelMap) {
        final Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            final Integer statusCode = Integer.valueOf(status.toString());
            modelMap.addAttribute(ERROR_CODE_ATTRIBUTE, statusCode);

            // TODO: additional processing for better UX
            if(statusCode == HttpStatus.NOT_FOUND.value()) {
                modelMap.addAttribute(ERROR_CODE_MESSAGE, ERROR_MESSAGE_404);
            }
            else if(statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                modelMap.addAttribute(ERROR_CODE_MESSAGE, ERROR_MESSAGE_500);
            }
            else {
                modelMap.addAttribute(ERROR_CODE_MESSAGE, ERROR_MESSAGE_BASE);
            }
        }

        return "error";
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}
