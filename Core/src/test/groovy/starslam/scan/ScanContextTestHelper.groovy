package starslam.scan

import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import starslam.TestConfiguration
import starslam.project.ProjectModuleConfiguration

class ScanContextTestHelper {
    private static wired = false;
    private static ApplicationContext context

    public static ApplicationContext wireContext() {
        if (!wired) {
            context = new AnnotationConfigApplicationContext()
            context.register(
                    TestConfiguration
                    ,	ProjectModuleConfiguration
                    , ScanModuleConfiguration
            )

            context.refresh()
            wired = true
        }
        context
    }
}
