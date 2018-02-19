package ua.com.smiddle.SecurityEchoServer;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.web.multipart.support.MultipartFilter;

import javax.servlet.ServletContext;


public class SecurityInitializer extends AbstractSecurityWebApplicationInitializer {

    //telling spring security to use Multipart ManagementRequest before sec. filter chain (for multipart+CSRF security) WORK's
    @Override
    protected void beforeSpringSecurityFilterChain(ServletContext servletContext) {
        MultipartFilter filter = new MultipartFilter();
        filter.setMultipartResolverBeanName("multipartResolver");
        insertFilters(servletContext, filter);
    }
}
