package ua.com.smiddle.SecurityEchoServer.core.web;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ua.com.smiddle.common.model.User;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @author ksa on 2/19/18.
 * @project SecurityEchoServer
 */
@RestController
@RequestMapping("/ses/test")
public class Controller {
    @CrossOrigin
    @RequestMapping(value = "/anonymous", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Object anonymous(@AuthenticationPrincipal User user, @RequestBody Objects body, HttpServletRequest request) {
        return new Wrapper<Object, User>(body, user, request.getRequestURI(), request.getMethod());
    }

    @CrossOrigin
    @RequestMapping(value = "/principal_all", method = {RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Object principal(@AuthenticationPrincipal User user, HttpServletRequest request) {
        return new Wrapper<Object, User>(null, user, request.getRequestURI(), request.getMethod());
    }

    @CrossOrigin
    @RequestMapping(value = "/principal_post", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Object principal(@AuthenticationPrincipal User user, @RequestBody Objects body, HttpServletRequest request) {
        return new Wrapper<Object, User>(body, user, request.getRequestURI(), request.getMethod());
    }

    @CrossOrigin
    @RequestMapping(value = "/matcher/{path}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Object principal_path(@AuthenticationPrincipal User user, @RequestBody Objects body, @PathVariable("path") String path, HttpServletRequest request) {
        return new Wrapper<Object, User>(body, user, request.getRequestURI(), request.getMethod());
    }

    private class Wrapper<T, U> {
        private final T request;
        private final U user;
        private final String path;
        private final String method;

        public Wrapper(T request, U user, String path, String method) {
            this.request = request;
            this.user = user;
            this.path = path;
            this.method = method;
        }
    }

}
