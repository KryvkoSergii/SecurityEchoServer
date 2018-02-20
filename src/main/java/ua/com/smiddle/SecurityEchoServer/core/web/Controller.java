package ua.com.smiddle.SecurityEchoServer.core.web;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ua.com.smiddle.common.model.User;

import javax.servlet.http.HttpServletRequest;

/**
 * @author ksa on 2/19/18.
 * @project SecurityEchoServer
 */
@RestController
@RequestMapping("/ses/test")
public class Controller {
    @CrossOrigin
    @RequestMapping(value = "/anonymous", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Object anonymous(@AuthenticationPrincipal User user, @RequestBody Test body, HttpServletRequest request) {
        return new Wrapper(body, user, request.getRequestURI(), request.getMethod());
    }

    @CrossOrigin
    @RequestMapping(value = "/principal_all", method = {RequestMethod.POST, RequestMethod.PUT},
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Object principal(@AuthenticationPrincipal User user,@RequestBody Test body, HttpServletRequest request) {
        return new Wrapper(body, user, request.getRequestURI(), request.getMethod());
    }

    @CrossOrigin
    @RequestMapping(value = "/principal_all", method = {RequestMethod.GET},
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Object principal1(@AuthenticationPrincipal User user, HttpServletRequest request) {
        return new Wrapper(null, user, request.getRequestURI(), request.getMethod());
    }

    @CrossOrigin
    @RequestMapping(value = "/principal_post", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Object principal1(@AuthenticationPrincipal User user, @RequestBody Test body, HttpServletRequest request) {
        return new Wrapper(body, user, request.getRequestURI(), request.getMethod());
    }

    @CrossOrigin
    @RequestMapping(value = "/matcher/{path}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Object principal_path(@AuthenticationPrincipal User user, @RequestBody Test body, @PathVariable("path") String path, HttpServletRequest request) {
        return new Wrapper(body, user, request.getRequestURI(), request.getMethod());
    }

    public class Wrapper {
        private Test request;
        private User user;
        private String path;
        private String method;

        public Wrapper() {
        }

        public Wrapper(Test request, User user, String path, String method) {
            this.request = request;
            this.user = user;
            this.path = path;
            this.method = method;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }
    }

    public static class Test{
        private String someField;

        public String getSomeField() {
            return someField;
        }

        public void setSomeField(String someField) {
            this.someField = someField;
        }
    }

}
