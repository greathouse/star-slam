package starslam.web.project

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import starslam.project.Project
import starslam.project.ProjectStore

@Controller
class ListProjectController {

    @Autowired protected ProjectStore projectStore

    @RequestMapping("/projects")
    @ResponseBody List<Project> index() {
        return projectStore.list()
    }
}
