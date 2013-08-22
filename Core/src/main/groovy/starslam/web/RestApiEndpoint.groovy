package starslam.web

import static groovy.json.JsonOutput.toJson
import org.ratpackframework.handling.Context
import org.ratpackframework.handling.Handler

abstract class RestApiEndpoint implements Handler {
	protected void get(Context context, String id){
		status(context, 405).sendJson(context, [success:false, errorMessage:"This method is not implemented"])
	}
	protected void post(Context context){
		status(context, 405).sendJson(context, [success:false, errorMessage:"This method is not implemented"])
	}
	protected void put(Context context, String id){
		status(context, 405).sendJson(context, [success:false, errorMessage:"This method is not implemented"])
	}
	protected void delete(Context context, String id){
		status(context, 405).sendJson(context, [success:false, errorMessage:"This method is not implemented"])
	}
	protected void list(Context context){
		status(context, 405).sendJson(context, [success:false, errorMessage:"This method is not implemented"])
	}
	
	@Override
	final void handle(Context context) {
		if (context.request.method.isPost()) {
			post(context)
			return
		}
		
		if (context.request.method.isGet()) {
			def id = context.pathTokens.id
			if (id) {
				get(context, id)
			}
			else {
				list(context)
			}
			return
		}
		
		if (context.request.method.isPut()) {
			put(context, context.pathTokens.id)
			return
		}
		
		if (context.request.method.isDelete()) {
			delete(context, context.pathTokens.id)
			return
		}
	}
	
	protected final Context header(Context context, String key, String value) {
		context.response.headers.add(key,  value)
		context
	}
	
	protected final Context status(Context context, int status) {
		context.response.status = status
		context
	}
	
	protected final Context sendJson(Context context, object) {
		context.response.send("application/json", toJson(object))
		context
	}

}
