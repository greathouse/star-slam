package starslam.scan.plugins

import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import starslam.TestConfiguration
import starslam.project.ProjectModuleConfiguration
import starslam.scan.ScanModuleConfiguration

class PluginsContextTestHelper {
    private static wired = false;
    public static File pluginDirectory
    private static ApplicationContext context

    public static ApplicationContext wireContext() {
        if (!wired) {
            context = wire()
        }
        context
    }

    private static AnnotationConfigApplicationContext wire() {
        context = new AnnotationConfigApplicationContext(
                TestConfiguration
                , ProjectModuleConfiguration
                , ScanModuleConfiguration
                , PluginModuleConfiguration
        )
        pluginDirectory = context.getBean("pluginDirectory")
        wired = true
        context
    }
}
