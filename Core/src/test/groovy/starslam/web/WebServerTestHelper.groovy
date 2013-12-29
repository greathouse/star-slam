package starslam.web

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import org.springframework.context.ApplicationContext
import org.springframework.web.servlet.DispatcherServlet
import starslam.DatabaseTestHelper

class WebServerTestHelper {
    private static boolean hasStarted = false;

    public static void startServer() {
        def applicationContext = WebContextTestHelper.wireContext()

        if (!hasStarted) {
            startJetty(applicationContext)
        }

        DatabaseTestHelper.setup(applicationContext)
    }

    private static void startJetty(ApplicationContext applicationContext) {
        final def servletHolder = new ServletHolder(new DispatcherServlet(applicationContext))
        final def context = new ServletContextHandler()
        context.setContextPath("/")
        context.addServlet(servletHolder, "/*")
        final def server = new Server(Integer.valueOf("9090"))
        server.setHandler(context)
        server.start()
        hasStarted = true
    }
}
