package starslam.scan.plugins

import groovy.json.JsonSlurper
import org.junit.Before
import org.junit.Test


class JarPluginTest {
    JarPlugin plugin

    @Before
    public void onSetup() {
        plugin = new JarPlugin()
    }

    @Test
    public void shouldReturnName() {
        def actual = plugin.name
        assert actual != null
        assert actual == "jar-v1.0"
    }

    @Test
    public void givenNullFile_shouldReturnResponse() {
        def actual = plugin.process(null)
        assert actual != null
    }

    @Test
    public void givenValidPath_shouldReturnExpectedVersion() {
        def jarStream = ClassLoader.getSystemResourceAsStream("jackson-core-asl-1.9.13.jar")
        def jarFile = File.createTempFile("test", ".jar")
        jarFile.withOutputStream { out ->
            out << jarStream
        }

        def jarPath = jarFile.canonicalPath
        assert jarPath != null

        def actual = plugin.process(new File(jarPath))

        assert actual != null
        def actualJson = new JsonSlurper().parseText(actual.data)
        assert "1.9.13" == actualJson.version
    }
}
