package starslam.web

import groovy.text.SimpleTemplateEngine

import org.ratpackframework.handling.Context
import org.ratpackframework.handling.Handler

import starslam.scan.IScanService

import com.google.inject.Inject

class ScanListHandler implements Handler {

	IScanService scanService
	
	@Inject
	def ScanListHandler(IScanService scanService) {
		this.scanService = scanService
	}
	
	@Override
	public void handle(Context context) {
		def html = this.class.getResource('blog.html').text
		def engine = new SimpleTemplateEngine()
		def model = [:]
		
		context.response.send('text/html', engine.createTemplate(html).make(["model":[title:'Hello Bye Bye']]).toString())
	}

}
