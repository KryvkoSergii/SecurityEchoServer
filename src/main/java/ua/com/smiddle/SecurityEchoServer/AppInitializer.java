package ua.com.smiddle.SecurityEchoServer;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import ua.com.smiddle.SecurityEchoServer.config.AppConfig;
import ua.com.smiddle.logger.produser.LogProducerImpl;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * Added to ${PACKAGE_NAME} by A.Osadchuk on 08.04.2016 at 16:07.
 * Project: Manager
 */
@SuppressWarnings("ALL")
public class AppInitializer implements WebApplicationInitializer {
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        try {
            //DataBaseService preparation
            DBFiller.checkDB();
            //Connecting servletContext with Manifests
            //Creating Spring context
            AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
            rootContext.register(AppConfig.class);
            //Binding MVC context to IoC
            rootContext.setServletContext(servletContext);
            servletContext.addListener(new SessionListener());
            //Add listener for manage life cycle of IoC context
            servletContext.addListener(new ContextLoaderListener(rootContext));
            //MVC Servlet-dispatcher registration
            DispatcherServlet servlet = new DispatcherServlet(rootContext);
            servlet.setThrowExceptionIfNoHandlerFound(true);
            ServletRegistration.Dynamic dispatcher = servletContext.addServlet("dispatcher", servlet);
            dispatcher.setLoadOnStartup(1);
            dispatcher.addMapping("/");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //Inner class
    private class SessionListener implements HttpSessionListener {
        @Override
        public void sessionCreated(HttpSessionEvent se) {
            LogProducerImpl.logStdOut("SmiddleManager", "SessionListener" + "====== Session=" + se.getSession().getId() + " is created ======");
            se.getSession().setMaxInactiveInterval(30 * 60);    //seconds
        }

        @Override
        public void sessionDestroyed(HttpSessionEvent se) {
            LogProducerImpl.logStdOut("SmiddleManager", "SessionListener" + "====== Session=" + se.getSession().getId() + " is destroyed =====");
        }
    }
}