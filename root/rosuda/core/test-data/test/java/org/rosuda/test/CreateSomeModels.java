package org.rosuda.test;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rosuda.util.combinatorics.BinaryArray;
import org.rosuda.util.combinatorics.BinaryArrayFactory;

public class CreateSomeModels {

	private CreateNodeModels creator;

	@Before
	public void setUp() {
		this.creator = new CreateNodeModels();
	}

	@Test
	public void createTestModels() throws ParserConfigurationException,
			TransformerException, IOException {
		Assert.assertNotNull(creator);
		creator.executeRCommand("library(stats)");// AIC command for lm
		creator.executeRCommand("library(datasets)");
		creator.executeRCommand("attach(airquality)");
		// vars: "Ozone" "Solar.R" "Wind" "Temp" "Month" "Day"
		final File folder = new File("robjectsFolder");
		if (folder.exists()&&!folder.isDirectory()) {
			throw new IllegalArgumentException("folder corrupted");
		}
		if (!folder.exists()) {
			folder.mkdir();
		}
			

		final String[] variables = new String[] { "Solar.R", "Wind", "Temp" };

		final BinaryArray<String> combineUs = new BinaryArrayFactory<String>()
				.create(variables.length);
		short counter = 1;
		while (combineUs.hasNext()) {
			//skip ""
			combineUs.next();
			final StringBuilder cmdBuilder = new StringBuilder("lm(Ozone~");
			final Iterator<String> cmdVars = combineUs.matchArray(variables);
			while (cmdVars.hasNext()) {
				cmdBuilder.append(cmdVars.next());
				if (cmdVars.hasNext())
					cmdBuilder.append("+");
			}
			cmdBuilder.append(")");
			creator.writeSummaryAndAICForCommand(cmdBuilder.toString(),
					new File(folder, "airquality-" + (counter++) + ".rObj"));
		}
	}
}
