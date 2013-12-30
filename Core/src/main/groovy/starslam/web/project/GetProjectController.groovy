package starslam.web.project

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import starslam.project.Project
import starslam.project.ProjectStore

@Controller
class GetProjectController {

    @Autowired protected ProjectStore projectStore

    @RequestMapping("/projects/{projectId}")
    @ResponseBody Project get(@PathVariable projectId) {
        projectStore.retrieve(projectId)
    }
}
