package com.leopardslab.dunner;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

public class DunnerTaskFileCreatorTest {

	@Test
	public void testGetTaskFile() {
		HashMap<String, Map> configMap = new HashMap<String, Map>() {{
		    put("NAME", new HashMap<String, String>() {{
		    	put("value", "golang_test");
		    }});
		    put("IMAGE", new HashMap<String, String>() {{
		    	put("value", "golang");
		    }});
		    put("COMMANDS", new HashMap<String, String>() {{
		    	put("value", "go version\ncurl version");
		    }});
		    put("MOUNTS", new HashMap<String, String>() {{
		    	put("value", "abcd:foo:r\nfoo:bar");
		    }});
		    put("ENVS", new HashMap<String, String>() {{
		    	put("value", "FOO=bar\nONE=two\niam=awesome");
		    }});
		}};
		Config config = new Config(configMap);
		DunnerTaskFileCreator creator = new DunnerTaskFileCreator(config);

		DunnerTaskFile taskFile = creator.getTask();

		assertEquals("Task name invalid", "golang_test", taskFile.name);
		assertEquals("Image name invalid", "golang", taskFile.image);
		assertArrayEquals("Commands invalid", new String[]{"go version", "curl version"}, taskFile.commands);
		assertArrayEquals("Mounts invalid", new String[]{"abcd:foo:r", "foo:bar"}, taskFile.mounts);
		assertArrayEquals("Envs invalid", new String[]{"FOO=bar", "ONE=two", "iam=awesome"}, taskFile.envs);
	}

	@Test
	public void testSaveToTempFile() throws IOException {
		HashMap<String, Map> configMap = new HashMap<String, Map>() {{
		    put("NAME", new HashMap<String, String>() {{
		    	put("value", "golang_test");
		    }});
		    put("IMAGE", new HashMap<String, String>() {{
		    	put("value", "golang");
		    }});
		    put("COMMANDS", new HashMap<String, String>() {{
		    	put("value", "go   version\ncurl version");
		    }});
		    put("MOUNTS", new HashMap<String, String>() {{
		    	put("value", "abcd:foo:r\nfoo:bar");
		    }});
		    put("ENVS", new HashMap<String, String>() {{
		    	put("value", "FOO=bar\nONE=two\niam=awesome");
		    }});
		}};
		Config config = new Config(configMap);
		DunnerTaskFileCreator creator = new DunnerTaskFileCreator(config);

		String taskFilePath = creator.saveToTempFile();

		String expected = "golang_test:\n" +
	"  - image: golang\n"+
	"    name: golang_test\n" +
	"    commands:\n" +
	"      - [\"go\",\"version\"]\n" +
	"      - [\"curl\",\"version\"]\n" +
	"    mounts:\n" +
	"      - \"abcd:foo:r\"\n" +
	"      - \"foo:bar\"\n" +
	"    envs:\n" +
	"      - \"FOO=bar\"\n" +
	"      - \"ONE=two\"\n" +
	"      - \"iam=awesome\"\n";
		String obtained = new String(Files.readAllBytes(Paths.get(taskFilePath)));
		FileUtils.deleteDirectory(new File(taskFilePath).getParentFile());
		assertEquals("Task file contents not matching", expected, obtained);
		assertTrue(taskFilePath.endsWith(DunnerTaskFileCreator.taskFileName));
	}

}