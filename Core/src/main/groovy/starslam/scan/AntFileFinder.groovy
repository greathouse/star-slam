package starslam.scan

import java.nio.file.Path

class AntFileFinder {
	private final Closure<Path> onFound
	private final String pattern
	
	public AntFileFinder(String pattern, Closure<Path> onFound) {
		this.pattern = pattern
		this.onFound = onFound
	}
	
	public void execute(String sourcePath) {
		def scanner = new AntBuilder().fileScanner {
			fileset(dir:sourcePath, casesensitive:false) {
				pattern.split(/\|/).each { p ->
					include(name:p)
				}
			}
		}
		
		scanner.each { f ->
			onFound f
		}
	}
}
