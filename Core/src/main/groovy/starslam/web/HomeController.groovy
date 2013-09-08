package starslam.web

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping

@Controller
class HomeController {
	private String name;

	@Autowired
	public HomeController(@Qualifier("name") String name) {
		this.name = name;
	}

	@RequestMapping("home")
	public String loadHomePage(Model m) {
		m.addAttribute("name", "Robert")
		return "home/whatev";
	}
}
