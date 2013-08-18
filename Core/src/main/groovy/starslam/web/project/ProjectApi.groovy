package starslam.web.project

import static groovy.json.JsonOutput.toJson
import groovy.json.JsonSlurper

import org.ratpackframework.handling.Context

import starslam.project.DuplicateProjectNameException
import starslam.project.IProjectStore
import starslam.project.Project
import starslam.web.RestApiEndpoint

import com.google.inject.Inject

class ProjectApi extends RestApiEndpoint {
	private IProjectStore projectStore

	@Inject
	public ProjectApi(IProjectStore projectStore) {
		this.projectStore = projectStore
	}
	
	protected void post(Context context) {
		def body = new JsonSlurper().parseText(context.request.text)
		try {
			def id = projectStore.persist(new Project(null, body.name, body.rootPath, body.fileGlob))
			header(context, 'Location', "/"+context.request.path+"/$id")
			sendJson(context, [
				success:true, 
				id:id
			])
		}
		catch(DuplicateProjectNameException e) {
			context.response.status(400)
			sendJson(context, [
				success:false, 
				message:"A project named ${body.name} already exists. Please choose a different name.", 
				errorCode:"DUPLICATE_PROJECT_NAME"
			])
		}
	}
	
	protected void get(Context context, String id) {
		def project = projectStore.retrieve(id)
		context.response.send("application/json", toJson(project))
	}
	
	protected void put(Context context, String id) {
		def body = new JsonSlurper().parseText(context.request.text)
		projectStore.persist(new Project([
				id:id
				, name:body.name
				, rootPath:body.rootPath
				, fileGlob:body.fileGlob
			]))
		sendJson(context, [success:true])
	}
	
	protected void list(Context context) {
		def projects = projectStore.list()
		sendJson(context, projects)
	}
}
