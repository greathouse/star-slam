package starslam

import com.google.common.io.Files


class FileTestHelper {
    public static File rootPath() {
        return Files.createTempDir()
    }

    public static File createFile(def path, def subdir, def suffix) {
        def subdirPath = new File(path, subdir)
        subdirPath.mkdirs()
        return createFile(subdirPath, suffix)
    }

    public static File createFile(def path, def suffix) {
        def textFile = new File(path, UUID.randomUUID().toString()+"${suffix}")
        Files.touch(textFile)
        return textFile
    }
}
