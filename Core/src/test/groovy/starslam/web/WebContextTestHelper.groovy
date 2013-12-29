package starslam.web

import org.springframework.context.ApplicationContext
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext
import starslam.TestConfiguration
import starslam.project.ProjectModuleConfiguration
import starslam.scan.ScanModuleConfiguration
import starslam.scan.plugins.PluginModuleConfiguration

class WebContextTestHelper {
    private static wired = false;
    private static File pluginDirectory
    private static ApplicationContext context

    public static ApplicationContext wireContext() {
        if (!wired) {
            context = wire()
        }
        context
    }

    private static AnnotationConfigWebApplicationContext wire() {
        context = new AnnotationConfigWebApplicationContext()
        context.register(
                TestConfiguration
                , ProjectModuleConfiguration
                , ScanModuleConfiguration
                , PluginModuleConfiguration
                , WebConfig
        )

        wired = true
        context
    }
}
