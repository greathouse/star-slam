package starslam.web

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class HomeController {
	@RequestMapping(value="/", produces="text/html")
	@ResponseBody public String index() {
		return this.class.getResource('/assets/index.html').openStream().text
	}
}
