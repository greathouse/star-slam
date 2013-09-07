package starslam

import java.util.regex.Pattern

import org.reflections.Reflections
import org.reflections.scanners.ResourcesScanner

import com.google.common.io.Files

class Bootstrapper {
	Bootstrapper porpoise(String dbUrl) {
		ClassLoader parent = getClass().getClassLoader();
		GroovyClassLoader loader = new GroovyClassLoader(parent);
		Class groovyClass = loader.parseClass(parent.getResourceAsStream("porpoise/Porpoise.groovy"),"porpoise/Porpoise.groovy");
		
		def tempSqlDir = Files.createTempDir()
		def sqlfiles = new Reflections("sql", new ResourcesScanner()).getResources(Pattern.compile(".*\\.sql"))
		sqlfiles.each { f ->
			def outfile = new File(tempSqlDir, f.replaceAll('sql/', ''))
			def infile = ClassLoader.getSystemResourceAsStream(f)
			outfile.withWriter { w ->
				w << infile
			}
		}
		
		// let's call some method on an instance
		GroovyObject groovyObject = (GroovyObject) groovyClass.newInstance();
		groovyObject.invokeMethod("main", ['-SF', '-d',tempSqlDir.canonicalPath, '-U',dbUrl, '--no-exit'] as String[]);
		
		this
	}
}
