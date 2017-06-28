package io.spring.concourse.artifactoryresource.command;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.spring.concourse.artifactoryresource.artifactory.ArtifactoryBuildRuns;
import io.spring.concourse.artifactoryresource.artifactory.ArtifactoryRepository;
import io.spring.concourse.artifactoryresource.artifactory.ArtifactoryServer;
import io.spring.concourse.artifactoryresource.artifactory.HttpArtifactory;
import io.spring.concourse.artifactoryresource.artifactory.payload.BuildModule;
import io.spring.concourse.artifactoryresource.artifactory.payload.ContinuousIntegrationAgent;
import io.spring.concourse.artifactoryresource.command.payload.OutRequest;
import io.spring.concourse.artifactoryresource.system.MockSystemStreams;
import io.spring.concourse.artifactoryresource.system.SystemInput;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;

import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Tests for {@link OutCommand}.
 *
 * @author Madhura Bhave
 */
@RunWith(SpringRunner.class)
public class OutCommandTests {

	@ClassRule
	public static final TemporaryFolder temporaryFolder = new TemporaryFolder();

	@MockBean
	HttpArtifactory artifactory;

	private final String COMMAND_RESOURCE_PATH = "/io/spring/concourse/artifactoryresource/command/payload";

	private final ObjectMapper mapper = new ObjectMapper();

	private ArtifactoryRepository repository;

	private ArtifactoryBuildRuns buildRuns;

	@Before
	public void setUp() throws Exception {
		temporaryFolder.create();
		this.repository = mock(ArtifactoryRepository.class);
		this.buildRuns = mock(ArtifactoryBuildRuns.class);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void runWhenIncludeSpecifiedShouldDeployIncluded() throws Exception {
		SystemInput inputJson = setUp(COMMAND_RESOURCE_PATH + "/out-request.json");
		new OutCommand(inputJson, this.artifactory)
				.run(new DefaultApplicationArguments(new String[] { "out" }));
		ArgumentCaptor<ContinuousIntegrationAgent> agentCaptor = ArgumentCaptor
				.forClass(ContinuousIntegrationAgent.class);
		ArgumentCaptor<List<BuildModule>> buildModuleCaptor = ArgumentCaptor
				.forClass(List.class);
		verify(this.buildRuns).add("1234", "http://ci.example.com", agentCaptor.capture(),
				buildModuleCaptor.capture());
		assertThat(agentCaptor.getValue().getName()).isEqualTo("Concourse");
		assertThat(buildModuleCaptor.getValue().size()).isEqualTo(2);
	}

	@Test
	public void runWhenExcludeSpecifiedShouldNotDeployExcluded() throws Exception {
		SystemInput inputJson = setUp(COMMAND_RESOURCE_PATH + "/out-request.json");
		new OutCommand(inputJson, this.artifactory)
				.run(new DefaultApplicationArguments(new String[] { "out" }));
	}

	@Test
	public void runWhenBuildUriSpecifiedShouldAddBuildUri() throws Exception {
		SystemInput inputJson = setUp(COMMAND_RESOURCE_PATH + "/out-request.json");
		new OutCommand(inputJson, this.artifactory)
				.run(new DefaultApplicationArguments(new String[] { "out" }));
	}

	private SystemInput setUp(String request) throws Exception {
		String requestJson = getJson(request);
		OutRequest outRequest = this.mapper.readValue(requestJson, OutRequest.class);
		OutRequest.Params params = outRequest.getParams();
		OutRequest.Params newParams = new OutRequest.Params(params.getBuildNumber(),
				temporaryFolder.getRoot().toString() + "/" + params.getFolder(),
				params.getInclude(), params.getExclude(), params.getBuildUri());
		outRequest = new OutRequest(outRequest.getSource(), newParams);
		MockSystemStreams systemStreams = new MockSystemStreams(
				this.mapper.writeValueAsString(outRequest));
		SystemInput inputJson = new SystemInput(systemStreams,
				new ObjectMapper());
		createFolderToDeploy();
		ArtifactoryServer artifactoryServer = mock(ArtifactoryServer.class);
		given(this.artifactory.server("http://repo.example.com", "admin", "password"))
				.willReturn(artifactoryServer);
		given(artifactoryServer.repository("libs-snapshot-local")).willReturn(repository);
		given(artifactoryServer.buildRuns("my-build")).willReturn(buildRuns);
		return inputJson;
	}

	private void createFolderToDeploy() throws IOException {
		String path = temporaryFolder.getRoot().toString();
		Files.createDirectories(Paths.get(path + "/dist/hello"));
		Files.write(Paths.get(path + "/dist/foo"), "foo".getBytes());
		Files.write(Paths.get(path + "/dist/baz"), "baz".getBytes());
		Files.write(Paths.get(path + "/dist/bar"), "bar".getBytes());
		Files.write(Paths.get(path + "/dist/hello/bar"), "bar".getBytes());
	}

	private String getJson(String name) throws Exception {
		InputStream stream = this.getClass().getResourceAsStream(name);
		return IOUtils.toString(stream);
	}

}