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
            context = new AnnotationConfigApplicationContext()
            context.register(
                    TestConfiguration
                    ,	ProjectModuleConfiguration
                    , ScanModuleConfiguration
                    , PluginModuleConfiguration
            )

            context.refresh()
            pluginDirectory = context.getBean("pluginDirectory")
            wired = true
        }
        context
    }
}
