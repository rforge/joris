package org.rosuda.util.java.file;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class FileFinderUtilTest {

    private FileFinderUtil fileFinderUtil;
    private FileSystem fileSystem;

    @Before
    public void setUp() {
        fileSystem = mock(FileSystem.class);
        this.fileFinderUtil = new FileFinderUtil(fileSystem);
    }

    @Test
    public void searchByNameWherePATHVariableContainsFolderReturnsResult() {
        File mockFile = mockFile("someEntry", "matched");
        File mockDirectory = mockDirectory(null, "someFolder", new File[] { mockFile });
        when(fileSystem.pathelements()).thenReturn(Arrays.asList(mockDirectory));
        when(fileSystem.getChild(eq(mockDirectory), eq("matched"))).thenReturn(mockFile);
        List<File> result = fileFinderUtil.findFileByName("matched");
        assertThat(result, hasSize(1));
    }

    @Test
    public void searchByNameWherePATHVariableDoesNotContainFolderFindsResultFromUserHome() {
        when(fileSystem.pathelements()).thenReturn(Collections.<File> emptyList());
        File mockFile = mockFile("someEntry", "matched");
        when(fileSystem.listFilesFromHomeDirectory()).thenReturn(new File[] { mockFile });
        List<File> result = fileFinderUtil.findFileByName("matched");
        assertThat(result, hasSize(1));
    }

    @Test
    public void searchByNameWherePATHVariableDoesNotContainFolderFindsResultFromUserHomeChildren() {
        when(fileSystem.pathelements()).thenReturn(Collections.<File> emptyList());
        File mockFile = mockFile("someFolder/someEntry", "matched");
        File mockDirectory = mockDirectory("someFolder", "someEntry", new File[] { mockFile });
        when(fileSystem.listFilesFromHomeDirectory()).thenReturn(new File[] { mockDirectory });
        List<File> result = fileFinderUtil.findFileByName("matched");
        assertThat(result, hasSize(1));
    }

    @Test
    public void searchByNameAndPathDidNotFindMatchButChildrenOfPathDo() {
        File mockFile = mockFile("someEntry", "matched");
        File mockDirectory = mockDirectory("someFolder", "someEntry", new File[] { mockFile });
        File parentMockDirectory = mockDirectory(null, "someFolder", new File[] { mockDirectory });
        when(fileSystem.pathelements()).thenReturn(Arrays.asList(parentMockDirectory));
        when(fileSystem.listFilesFromHomeDirectory()).thenReturn(new File[] {});
        List<File> result = fileFinderUtil.findFileByName("matched");
        assertThat(result, hasSize(1));
    }

    @Test
    public void whenNameSearchEnvironmentDoesNotFindAMatchRootFilesWillBeUsed() {
        File mockFile = mockFile("someEntry", "matched");
        File mockDirectory = mockDirectory("someFolder", "someEntry", new File[] { mockFile });
        File parentMockDirectory = mockDirectory(null, "someFolder", new File[] { mockDirectory });
        when(fileSystem.pathelements()).thenReturn(Collections.<File> emptyList());
        when(fileSystem.listFilesFromHomeDirectory()).thenReturn(new File[] {});
        when(fileSystem.listRoots()).thenReturn(new File[] { parentMockDirectory });
        List<File> result = fileFinderUtil.findFileByName("matched");
        assertThat(result, hasSize(1));
    }

    @Test
    public void searchByFilenameRegexpWherePATHVariableContainsFolderReturnsResult() {
        File mockFile = mockFile("someEntry", "matched");
        File mockDirectory = mockDirectory(null, "someFolder", new File[] { mockFile });
        when(fileSystem.pathelements()).thenReturn(Arrays.asList(mockDirectory));
        when(fileSystem.getChild(eq(mockDirectory), eq("matched"))).thenReturn(mockFile);
        List<File> result = fileFinderUtil.findFileByNameRegularExpression("\\w*");
        assertThat(result, hasSize(1));
    }

    @Test
    public void searchByFilenameRegexpWherePATHVariableDoesNotContainFolderFindsResultFromUserHome() {
        when(fileSystem.pathelements()).thenReturn(Collections.<File> emptyList());
        File mockFile = mockFile("someEntry", "matched");
        when(fileSystem.listFilesFromHomeDirectory()).thenReturn(new File[] { mockFile });
        List<File> result = fileFinderUtil.findFileByNameRegularExpression("\\w*");
        assertThat(result, hasSize(1));
    }

    @Test
    public void searchByFilenameRegexpWherePATHVariableDoesNotContainFolderFindsResultFromUserHomeChildren() {
        when(fileSystem.pathelements()).thenReturn(Collections.<File> emptyList());
        File mockFile = mockFile("someFolder/someEntry", "matched");
        File mockDirectory = mockDirectory("someFolder", "someEntry", new File[] { mockFile });
        when(fileSystem.listFilesFromHomeDirectory()).thenReturn(new File[] { mockDirectory });
        List<File> result = fileFinderUtil.findFileByNameRegularExpression("\\w*");
        assertThat(result, hasSize(1));
    }

    @Test
    public void searchByFilenameRegexpAndPathDidNotFindMatchButChildrenOfPathDo() {
        File mockFile = mockFile("someEntry", "matched");
        File mockDirectory = mockDirectory("someFolder", "someEntry", new File[] { mockFile });
        File parentMockDirectory = mockDirectory(null, "someFolder", new File[] { mockDirectory });
        when(fileSystem.pathelements()).thenReturn(Arrays.asList(parentMockDirectory));
        when(fileSystem.listFilesFromHomeDirectory()).thenReturn(new File[] {});
        List<File> result = fileFinderUtil.findFileByNameRegularExpression("\\w*");
        assertThat(result, hasSize(1));
    }

    @Test
    public void whenFilenameRegexpSearchEnvironmentDoesNotFindAMatchRootFilesWillBeUsed() {
        File mockFile = mockFile("someEntry", "matched");
        File mockDirectory = mockDirectory("someFolder", "someEntry", new File[] { mockFile });
        File parentMockDirectory = mockDirectory(null, "someFolder", new File[] { mockDirectory });
        when(fileSystem.pathelements()).thenReturn(Collections.<File> emptyList());
        when(fileSystem.listFilesFromHomeDirectory()).thenReturn(new File[] {});
        when(fileSystem.listRoots()).thenReturn(new File[] { parentMockDirectory });
        List<File> result = fileFinderUtil.findFileByNameRegularExpression("\\w*");
        assertThat(result, hasSize(1));
    }

    @Test
    public void searchByRegexpWherePATHVariableContainsFolderReturnsResult() {
        File mockFile = mockFile("someEntry", "matched");
        File mockDirectory = mockDirectory(null, "someFolder", new File[] { mockFile });
        when(fileSystem.pathelements()).thenReturn(Arrays.asList(mockDirectory));
        when(fileSystem.getChild(eq(mockDirectory), eq("matched"))).thenReturn(mockFile);
        List<File> result = fileFinderUtil.findFileByRegularExpression("(\\w|/)*");
        assertThat(result, hasSize(1));
    }

    @Test
    public void searchByRegexpWherePATHVariableDoesNotContainFolderFindsResultFromUserHome() {
        when(fileSystem.pathelements()).thenReturn(Collections.<File> emptyList());
        File mockFile = mockFile("someEntry", "matched");
        when(fileSystem.listFilesFromHomeDirectory()).thenReturn(new File[] { mockFile });
        List<File> result = fileFinderUtil.findFileByRegularExpression("(\\w|/)*");
        assertThat(result, hasSize(1));
    }

    @Test
    public void searchByRegexpWherePATHVariableDoesNotContainFolderFindsResultFromUserHomeChildren() {
        when(fileSystem.pathelements()).thenReturn(Collections.<File> emptyList());
        File mockFile = mockFile("someFolder/someEntry", "matched");
        File mockDirectory = mockDirectory("someFolder", "someEntry", new File[] { mockFile });
        when(fileSystem.listFilesFromHomeDirectory()).thenReturn(new File[] { mockDirectory });
        List<File> result = fileFinderUtil.findFileByRegularExpression("(\\w|/)*");
        assertThat(result, hasSize(1));
    }

    @Test
    public void searchByRegexpAndPathDidNotFindMatchButChildrenOfPathDo() {
        File mockFile = mockFile("someEntry", "matched");
        File mockDirectory = mockDirectory("someFolder", "someEntry", new File[] { mockFile });
        File parentMockDirectory = mockDirectory(null, "someFolder", new File[] { mockDirectory });
        when(fileSystem.pathelements()).thenReturn(Arrays.asList(parentMockDirectory));
        when(fileSystem.listFilesFromHomeDirectory()).thenReturn(new File[] {});
        List<File> result = fileFinderUtil.findFileByRegularExpression("(\\w|/)*");
        assertThat(result, hasSize(1));
    }

    @Test
    public void whenRegexpSearchEnvironmentDoesNotFindAMatchRootFilesWillBeUsed() {
        File mockFile = mockFile("someEntry", "matched");
        File mockDirectory = mockDirectory("someFolder", "someEntry", new File[] { mockFile });
        File parentMockDirectory = mockDirectory(null, "someFolder", new File[] { mockDirectory });
        when(fileSystem.pathelements()).thenReturn(Collections.<File> emptyList());
        when(fileSystem.listFilesFromHomeDirectory()).thenReturn(new File[] {});
        when(fileSystem.listRoots()).thenReturn(new File[] { parentMockDirectory });
        List<File> result = fileFinderUtil.findFileByRegularExpression("(\\w|/)*");
        assertThat(result, hasSize(1));
    }

    // -- helper
    private File mockDirectory(String path, String name, File[] children) {
        final File mock = mock(File.class);
        when(mock.getName()).thenReturn(name);
        when(mock.getParent()).thenReturn(path);
        when(mock.exists()).thenReturn(true);
        when(mock.isFile()).thenReturn(false);
        when(mock.isDirectory()).thenReturn(true);
        when(mock.getAbsolutePath()).thenReturn(path + "/" + name);
        when(mock.listFiles()).thenReturn(children);
        return mock;
    }

    private File mockFile(String path, String name) {
        final File mock = mock(File.class);
        when(mock.getName()).thenReturn(name);
        when(mock.getParent()).thenReturn(path);
        when(mock.exists()).thenReturn(true);
        when(mock.isFile()).thenReturn(true);
        when(mock.getAbsolutePath()).thenReturn(path + "/" + name);
        return mock;
    }
}
