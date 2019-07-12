package com.leopardslab.dunner;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Pattern;

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
		String wd = "foo";
		Context context = new Context(new HashMap<String, Object>() {{
			put("workingDirectory", wd);
			put("environmentVariables", new HashMap<String,String>());
		}});

		DunnerTaskFileCreator creator = new DunnerTaskFileCreator(config, context);

		DunnerTaskFile taskFile = creator.getTask();

		assertEquals("Task name invalid", "golang_test", taskFile.name);
		assertEquals("Image name invalid", "golang", taskFile.image);
		assertTrue("Commands invalid", Arrays.asList("go version", "curl version").equals(taskFile.commands));
		assertTrue("Mounts invalid", Arrays.asList("abcd:foo:r", "foo:bar").equals(taskFile.mounts));
		assertTrue("Envs invalid", Arrays.asList("FOO=bar", "ONE=two", "iam=awesome").equals(taskFile.envs));
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
		String wd = "foo";
		HashMap<String, String> envs = new HashMap<String, String>() {{
			put("foo", "bar");
			put("hello", "world");
		}};
		Context context = new Context(new HashMap<String, Object>() {{
			put("workingDirectory", wd);
			put("environmentVariables", envs);
		}});
		DunnerTaskFileCreator creator = new DunnerTaskFileCreator(config, context);

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
	"      - \"iam=awesome\"\n" +
	"      - \"foo=bar\"\n" +
	"      - \"hello=world\"\n";
		String obtained = new String(Files.readAllBytes(Paths.get(taskFilePath)));
		FileUtils.deleteDirectory(new File(taskFilePath).getParentFile());
		assertEquals("Task file contents not matching", expected, obtained);
		assertTrue(taskFilePath.endsWith("gocd_dunner/.dunner.yaml"));
	}

		@Test
	public void testSaveToTempFileWithNoEnvs() throws IOException {
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
		    }});
		}};
		String wd = "foo";
		Context context = new Context(new HashMap<String, Object>() {{
			put("workingDirectory", wd);
			put("environmentVariables", new HashMap<String, String>());
		}});

		DunnerTaskFileCreator creator = new DunnerTaskFileCreator(new Config(configMap), context);

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
	"    envs:\n";
		String obtained = new String(Files.readAllBytes(Paths.get(taskFilePath)));
		FileUtils.deleteDirectory(new File(taskFilePath).getParentFile());
		assertEquals("Task file contents not matching", expected, obtained);
		assertTrue(taskFilePath.endsWith("gocd_dunner/.dunner.yaml"));
	}

}