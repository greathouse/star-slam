//import groovywebconsole.ScriptExecutor
import org.ratpackframework.groovy.templating.TemplateRenderer

import static groovy.json.JsonOutput.toJson
import static org.ratpackframework.groovy.RatpackScript.ratpack

import starslam.ScanService

ratpack {
    handlers {
        get {
            get(TemplateRenderer).render "index.html", title: "Groovy Web Console" 
        }
		
		post("blah") {
			
			response.send "application/json", toJson([hello:new ScanService().hello('Robert')])
		}
/*
        post("execute") {
            def script = request.form.script
            def result = new ScriptExecutor().execute(script)
            response.send "application/json", toJson(result)
        }

        get("reloadexample") {
            response.send new ReloadingThing().toString()
        }
*/
        assets "public"
    }
}