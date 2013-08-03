package starslam.web.project

import static groovy.json.JsonOutput.toJson
import groovy.json.JsonOutput
import groovy.json.JsonSlurper

import org.ratpackframework.handling.Exchange

import starslam.project.DuplicateProjectNameException
import starslam.project.IProjectStore
import starslam.project.Project
import starslam.web.RestApiEndpoint

import com.google.inject.Inject

class ProjectApi extends RestApiEndpoint {
	private IProjectStore projectStore
	
	@Override
	protected String path() {
		return "projects";
	}

	@Inject
	public ProjectApi(IProjectStore projectStore) {
		this.projectStore = projectStore
	}
	
	protected void post(Exchange exchange) {
		def body = new JsonSlurper().parseText(exchange.request.text)
		try {
			def id = projectStore.persist(new Project(null, body.name, body.rootPath))
			sendJson(exchange, [
				success:true, 
				id:id,
				address:"/${path()}/${id}"
			])
		}
		catch(DuplicateProjectNameException e) {
			exchange.response.status(400)
			sendJson(exchange, [
				success:false, 
				message:"A project named ${body.name} already exists. Please choose a different name.", 
				errorCode:"DUPLICATE_PROJECT_NAME"
			])
		}
	}
	
	protected void get(Exchange exchange, String id) {
		def project = projectStore.retrieve(id)
		exchange.response.send("application/json", toJson(project))
	}
	
	protected void put(Exchange exchange, String id) {
		def body = new JsonSlurper().parseText(exchange.request.text)
		projectStore.persist(new Project([
				id:id,
				name:body.name,
				rootPath:body.rootPath
			]))
		sendJson(exchange, [success:true])
	}
	
	protected void list(Exchange exchange) {
		def projects = projectStore.list()
		sendJson(exchange, projects)
	}
}
