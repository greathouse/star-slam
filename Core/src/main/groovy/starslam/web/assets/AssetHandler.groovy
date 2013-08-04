package starslam.web.assets

import org.ratpackframework.handling.Context
import org.ratpackframework.handling.Handler

class AssetHandler implements Handler { 
	@Override
	public void handle(Context exchange) {
		def asset = this.class.getResource(exchange.request.path)?.text
		def context = exchange.request.path.endsWith('.css') ? "text/css" : "text/javascript"
		exchange.response.send(context, asset)
	}
}
