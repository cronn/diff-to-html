package de.cronn.diff;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import de.cronn.diff.util.FileHelper;

@RunWith(PowerMockRunner.class)
@PrepareForTest({FileHelper.class})
public class MainNoOutputTest extends MainTestBase {

	@Test
	public void testMainJAVADiffDirsToHtml_noOutputProvided() throws Exception {
		PowerMockito.mockStatic(FileHelper.class);
		Mockito.when(FileHelper.getWorkingDir()).thenReturn(TestBase.TEST_DATA_OUTPUT_DIR);
		
		exit.expectSystemExitWithStatus(Main.EXIT_CODE_OK);
		exit.checkAssertionAfterwards(new SystemMessageAssertion());
		Main.main(new String[] { INPUT_DIR_1, INPUT_DIR_1 });
	}
	
}
