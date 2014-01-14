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
    public void givenJarWithManifestEntry_shouldReturnExpectedVersion() {
        runVersionTest("jackson-core-asl-1.9.13.jar", "1.9.13")
    }

    private void runVersionTest(String jarName, String expectedVersion) {
        def jarPath = getPathToJar(jarName)
        def actual = plugin.process(new File(jarPath))
        assert actual != null
        def actualJson = new JsonSlurper().parseText(actual.data)
        assert expectedVersion == actualJson.version
    }

    private String getPathToJar(String jarName) {
        def jarStream = ClassLoader.getSystemResourceAsStream(jarName)
        def jarFile = File.createTempFile("test", ".jar")
        jarFile.withOutputStream { out ->
            out << jarStream
        }
        def jarPath = jarFile.canonicalPath
        assert jarPath != null
        jarPath
    }
}
