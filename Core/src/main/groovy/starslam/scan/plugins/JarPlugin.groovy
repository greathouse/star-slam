package starslam.scan.plugins

import groovy.transform.Immutable

import java.util.jar.JarInputStream
import java.util.zip.ZipFile

import static groovy.json.JsonOutput.toJson


class JarPlugin implements IPlugin {
    @Override
    String getName() {
        return "jar-v1.0"
    }

    @Override
    PluginResponse process(File file) {
        if (!file) {
            return new PluginResponse(data:null)
        }

        def jarFile = new JarInputStream(file.newInputStream())
        def manifest = jarFile.manifest

        def version = manifest.mainAttributes.getValue("Implementation-Version")
        new PluginResponse(data:toJson(new Data(version:version)))
    }

    @Immutable
    private class Data {
        String version
    }
}
