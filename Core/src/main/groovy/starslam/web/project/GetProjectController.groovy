package starslam.web.project

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import starslam.project.Project

@Controller
class GetProjectController extends ProjectController {
    @RequestMapping("{projectId}")
    @ResponseBody Project get(@PathVariable projectId) {
        projectStore.retrieve(projectId)
    }
}
