package starslam.web.assets

import org.ratpackframework.handling.Context
import org.ratpackframework.handling.Handler

class AssetHandler implements Handler {
	private static final Map<String,String> MIME_TYPES = [
		'html':'text/html'
		, 'js':'application/javascript'
		, 'css':'text/css'
		, 'ico':'image/x-icon'
	] 
	@Override
	public void handle(Context exchange) {
		def path = exchange.request.path?:'index.html'
		def map = URLConnection.getFileNameMap()
		def ext = path.substring(path.lastIndexOf('.')+1)
		def mimeType = MIME_TYPES[ext]
		def asset = this.class.getResourceAsStream('/starslam/web/assets/'+path)?.text
		if (asset == null) {
			def e404 = this.class.getResourceAsStream('/starslam/web/assets/404.html')
			def e404Text = e404.text
			exchange.response.status 404
			exchange.response.send('text/html',e404Text)
			return
		}
		exchange.response.send(mimeType, asset)
	}
}
