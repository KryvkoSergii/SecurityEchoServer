package ua.com.smiddle.SecurityEchoServer.core.web;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ua.com.smiddle.annotations.SmiddleValidator;


/**
 * @author ksa on 2/20/18.
 * @project SecurityEchoServer
 */
@SmiddleValidator
public class Val implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    public void validate(Object target, Errors errors) {

    }
}
