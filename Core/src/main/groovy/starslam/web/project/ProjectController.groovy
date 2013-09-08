package starslam.web.project

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import starslam.project.Project

@Controller
@RequestMapping("/project")
class ProjectController {

	@RequestMapping("/blah")
	@ResponseBody Project blah() {
		return new Project(fileGlob: "*.txt", id: UUID.randomUUID())
	}
}
