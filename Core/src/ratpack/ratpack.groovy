import org.ratpackframework.groovy.templating.TemplateRenderer

import static groovy.json.JsonOutput.toJson
import static org.ratpackframework.groovy.RatpackScript.ratpack

import starslam.ScanService

ratpack {
    handlers {
        get {
            get(TemplateRenderer).render "index.html", title: "Groovy Web Console" 
        }
		
		get("blog") {
			get(TemplateRenderer).render "blog.html"
		}
		
		post("blah") {
			
			response.send "application/json", toJson([hello:new ScanService().hello('Robert')])
		}

        assets "public"
    }
}