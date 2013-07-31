package starslam.web

import groovy.text.SimpleTemplateEngine

import org.ratpackframework.handling.Exchange
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
	public void handle(Exchange exchange) {
		def html = this.class.getResource('blog.html').text
		def engine = new SimpleTemplateEngine()
		def model = [:]
		
		exchange.response.send('text/html', engine.createTemplate(html).make(["model":[title:'Hello Bye Bye']]).toString())
	}

}
