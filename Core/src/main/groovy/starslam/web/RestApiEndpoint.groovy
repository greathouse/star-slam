package starslam.web

import static groovy.json.JsonOutput.toJson
import org.ratpackframework.handling.Exchange
import org.ratpackframework.handling.Handler

abstract class RestApiEndpoint implements Handler {

	protected abstract String path();
	protected void get(Exchange exchange, String id){}
	protected void post(Exchange exchange){}
	protected void put(Exchange exchange, String id){}
	protected void delete(Exchange exchange, String id){}
	protected void list(Exchange exchange){}
	
	@Override
	final void handle(Exchange exchange) {
		if (!exchange.request.path.startsWith(path())) {
			exchange.next()
		}
		
		if (exchange.request.method.isPost()) {
			post(exchange)
			return
		}
		
		if (exchange.request.method.isGet()) {
			def id = parseId(exchange)
			if (id) {
				get(exchange, id)
			}
			else {
				list(exchange)
			}
			return
		}
		
		if (exchange.request.method.isPut()) {
			put(exchange, parseId(exchange))
			return
		}
		
		if (exchange.request.method.isDelete()) {
			delete(exchange, parseId(exchange))
			return
		}
	}
	
	protected void sendJson(Exchange exchange, object) {
		exchange.response.send("application/json", toJson(object))
	}

	private String parseId(Exchange exchange) {
		def path = exchange.request.path
		if (path.contains('/') == false) {
			return null
		}
		def id = path.substring(path.lastIndexOf('/')+1)
		return id
	}

}
