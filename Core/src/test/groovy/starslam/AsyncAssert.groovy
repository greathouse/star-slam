package starslam

class AsyncAssert {
	public static void run(Closure closure) {
		run(30, closure)
	}
	
	public static void run(int timeout, Closure closure) {
		def timestamp = System.currentTimeMillis()
		
		while(true){
			try {
				closure()
				break
			}
			catch(Throwable all) {
				Thread.sleep(250)
				def processTime = (System.currentTimeMillis() - timestamp) / 1000
				println processTime
				if (processTime > timeout ) {
					throw all
				}
			}
		}
		
	}
}
