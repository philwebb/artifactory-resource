package io.spring.concourse.artifactoryresource;

import io.spring.concourse.artifactoryresource.command.CheckCommand;
import io.spring.concourse.artifactoryresource.command.InCommand;
import io.spring.concourse.artifactoryresource.command.OutCommand;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.assertj.core.api.Assertions.assertThat;

public class ApplicationTests {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void initializeShouldSetupCommands() throws Exception {
		Application application = new Application();
		assertThat(application.getCommand("check")).isInstanceOf(CheckCommand.class);
		assertThat(application.getCommand("in")).isInstanceOf(InCommand.class);
		assertThat(application.getCommand("out")).isInstanceOf(OutCommand.class);
	}

	@Test
	public void runWhenNoArgumentsShouldThrowException() {
		this.thrown.expect(RuntimeException.class);
		this.thrown.expectMessage("Missing arguments");
		new Application().run(new String[] {});
	}

	@Test
	public void runWhenCommandIsNotKnownShouldThrowException() throws Exception {

	}

	@Test
	public void runShouldDelegateToCommand() throws Exception {

	}

}
