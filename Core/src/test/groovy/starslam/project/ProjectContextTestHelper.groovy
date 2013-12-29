package starslam.project

import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import starslam.TestConfiguration

class ProjectContextTestHelper {
    private static wired = false;
    private static ApplicationContext context

    public static ApplicationContext wireContext() {
        if (!wired) {
            context = wire()
        }
        context
    }

    private static AnnotationConfigApplicationContext wire() {
        context = new AnnotationConfigApplicationContext()
        context.register(
                TestConfiguration
                , ProjectModuleConfiguration
        )

        context.refresh()
        wired = true
        context
    }
}
