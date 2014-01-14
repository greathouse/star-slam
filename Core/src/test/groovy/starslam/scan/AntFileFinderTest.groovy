package starslam.scan

import org.junit.Before
import org.junit.Test
import starslam.FileTestHelper

import java.nio.file.Path

class AntFileFinderTest {
    String rootPath

    @Before
    public void onSetup() {
        rootPath = FileTestHelper.rootPath()
        FileTestHelper.createFile(rootPath, ".negative")
    }

    @Test
    public void rootDirectory_ShouldFindFile() {
        executeRootDirectoryTest('.txt')
    }

    @Test
    public void rootDirectory_DifferentSuffixes() {
        executeRootDirectoryTest('.txt', '.exe')
    }

    @Test
    public void rootDirectory_MutlipleSameSuffixes() {
        executeRootDirectoryTest('.txt', '.txt')
    }

    private void executeRootDirectoryTest(String ... expectedFileSuffixes) {
        def expectedFindFiles = [] as Set
        def searchSuffixes = [] as Set
        expectedFileSuffixes.each { s ->
            expectedFindFiles << createExpectedFound(s)
            searchSuffixes << s
        }
        assert expectedFindFiles.size() == expectedFileSuffixes.size()
        def actualFoundFiles = []
        def finder = new AntFileFinder("*"+searchSuffixes.join("|*"), { f -> actualFoundFiles << f } as Closure<Path>)
        finder.execute(rootPath)
        assertFoundFiles(expectedFindFiles, actualFoundFiles)
    }

    private File createExpectedFound(String searchSuffix) {
        FileTestHelper.createFile(rootPath, searchSuffix)
    }

    private void assertFoundFiles(Set expectedFindFiles,  List actualFoundFiles) {
        assert expectedFindFiles.size() == actualFoundFiles.size()
        assert actualFoundFiles.containsAll(expectedFindFiles)
    }

    @Test
    public void explicitSubdirectory_ShouldBeFound() {
        def searchSuffix1 = ".txt"
        def subdir = "subdir"
        def expectedToFind = FileTestHelper.createFile(rootPath, "subdir", searchSuffix1)

        def actualFoundFiles = []
        def finder = new AntFileFinder("${subdir}/*${searchSuffix1}", { f -> actualFoundFiles << f } as Closure<Path>)
        finder.execute(rootPath)

        assert 1 == actualFoundFiles.size()
        assert actualFoundFiles.contains(expectedToFind)
    }

    @Test
    public void wildcardSubdirectory_ShouldBeFound() {
        def searchSuffix = ".txt"
        def expectedToFind = [] as Set
        expectedToFind << FileTestHelper.createFile(rootPath, "subdir", searchSuffix)
        expectedToFind << FileTestHelper.createFile(rootPath, "another-subdir", searchSuffix)

        def actualFoundFiles = []
        def finder = new AntFileFinder("**/*${searchSuffix}", { f -> actualFoundFiles << f } as Closure<Path>)
        finder.execute(rootPath)

        assert actualFoundFiles.containsAll(expectedToFind)
    }
}
