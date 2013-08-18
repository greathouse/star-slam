package starslam.scan

import java.io.IOException;
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import static java.nio.file.FileVisitResult.*

class FileFinder {
	private final Closure<Path> onFound
	private final String pattern
	
	public FileFinder(String pattern, Closure onFound) {
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
