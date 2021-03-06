package starslam.web

import com.greenmoonsoftware.tea.Tea

class Kettle {
	static void withTea(Closure<Tea> c) {
		def tea = new Tea("http://localhost:9090")
		c(tea)
		tea.log()
		tea.brew()
	}
}
