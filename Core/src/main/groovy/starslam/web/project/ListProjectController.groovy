package starslam.web.project

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import starslam.project.Project

@Controller
class ListProjectController extends ProjectController {
    @RequestMapping("")
    @ResponseBody List<Project> index() {
        return projectStore.list()
    }
}
