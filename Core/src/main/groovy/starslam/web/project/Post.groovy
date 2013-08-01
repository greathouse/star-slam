package starslam.web.project

import static groovy.json.JsonOutput.toJson
import groovy.json.JsonSlurper

import org.ratpackframework.handling.Exchange
import org.ratpackframework.handling.Handler

import starslam.project.DuplicateProjectNameException
import starslam.project.IProjectStore
import starslam.project.Project

import com.google.inject.Inject

class Post implements Handler {

	private IProjectStore projectStore
	
	@Inject
	public Post(IProjectStore projectStore) {
		this.projectStore = projectStore
	}
	
	@Override
	public void handle(Exchange exchange) {
		def body = new JsonSlurper().parseText(exchange.request.text)
		try {
			def id = projectStore.persist(new Project(null, body.name, new Date(), body.rootPath))
			exchange.response.send('text/json', toJson([success:true, id:id]))
		}
		catch(DuplicateProjectNameException e) {
			exchange.response.status(400)
			exchange.response.send('text/json', toJson([success:false, message:"A project named ${body.name} already exists. Please choose a different name.", errorCode:"DUPLICATE_PROJECT_NAME"]))
		}
		
	}

}
