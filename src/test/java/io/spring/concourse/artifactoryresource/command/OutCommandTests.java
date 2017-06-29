package io.spring.concourse.artifactoryresource.command;

import java.io.File;

import io.spring.concourse.artifactoryresource.command.payload.InRequest;
import io.spring.concourse.artifactoryresource.command.payload.OutRequest;
import io.spring.concourse.artifactoryresource.command.payload.OutResponse;
import io.spring.concourse.artifactoryresource.system.SystemInput;
import io.spring.concourse.artifactoryresource.system.SystemOutput;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Tests for {@link OutCommand}.
 *
 * @author Madhura Bhave
 * @author Phillip Webb
 */
@RunWith(SpringRunner.class)
public class OutCommandTests {

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private SystemInput systemInput;

	@Mock
	private SystemOutput systemOutput;

	@Mock
	private OutHandler handler;

	@Captor
	private ArgumentCaptor<Directory> directoryCaptor;

	private OutCommand command;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.command = new OutCommand(this.systemInput, this.systemOutput, this.handler);
	}

	@Test
	public void runShouldCallHandler() throws Exception {
		OutRequest request = mock(OutRequest.class);
		OutResponse response = mock(OutResponse.class);
		given(this.systemInput.read(OutRequest.class)).willReturn(request);
		given(this.handler.handle(eq(request), any())).willReturn(response);
		File tempFolder = this.temporaryFolder.newFolder();
		String dir = StringUtils.cleanPath(tempFolder.getPath());
		this.command.run(new DefaultApplicationArguments(new String[] { dir }));
		verify(this.handler).handle(eq(request), this.directoryCaptor.capture());
		verify(this.systemOutput).write(response);
		assertThat(this.directoryCaptor.getValue().getFile()).isEqualTo(tempFolder);
	}

	@Test
	public void runWhenFolderArgIsMissingShouldThrowException() throws Exception {
		InRequest request = mock(InRequest.class);
		given(this.systemInput.read(InRequest.class)).willReturn(request);
		this.thrown.expect(IllegalStateException.class);
		this.thrown.expectMessage("No directory argument specified");
		this.command.run(new DefaultApplicationArguments(new String[] {}));
	}

}