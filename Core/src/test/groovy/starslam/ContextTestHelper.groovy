package starslam

import org.springframework.context.ApplicationContext
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext
import starslam.project.ProjectModuleConfiguration
import starslam.scan.ScanModuleConfiguration
import starslam.scan.plugins.PluginModuleConfiguration
import starslam.web.WebConfig

class ContextTestHelper {
    private static wired = false;
    private static File pluginDirectory
    private static ApplicationContext context

    public static ApplicationContext wireContext() {
        if (!wired) {
            context = new AnnotationConfigWebApplicationContext()
            context.register(
                    TestConfiguration
                    ,	ProjectModuleConfiguration
                    , ScanModuleConfiguration
                    , PluginModuleConfiguration
                    , WebConfig
            )

            context.refresh()
            pluginDirectory = context.getBean("pluginDirectory")
            wired = true
        }
        context
    }
}
