package io.spring.concourse.artifactoryresource.command;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.spring.concourse.artifactoryresource.artifactory.ArtifactoryRepository;
import io.spring.concourse.artifactoryresource.artifactory.ArtifactoryServer;
import io.spring.concourse.artifactoryresource.artifactory.HttpArtifactory;
import io.spring.concourse.artifactoryresource.artifactory.payload.FetchResults;
import io.spring.concourse.artifactoryresource.command.payload.InResponse;
import io.spring.concourse.artifactoryresource.system.MockSystemStreams;
import io.spring.concourse.artifactoryresource.system.SystemInputJson;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Tests for {@link InCommand}.
 *
 * @author Madhura Bhave
 */
@RunWith(SpringRunner.class)
public class InCommandTests {

	@ClassRule
	public static final TemporaryFolder temporaryFolder = new TemporaryFolder();

	@MockBean
	HttpArtifactory artifactory;

	private final String COMMAND_RESOURCE_PATH = "/io/spring/concourse/artifactoryresource/command/payload";

	private final ObjectMapper mapper = new ObjectMapper();

	private ArtifactoryServer artifactoryServer;

	private ArtifactoryRepository repository;

	@Before
	public void setUp() throws Exception {
		this.artifactoryServer = mock(ArtifactoryServer.class);
		this.repository = mock(ArtifactoryRepository.class);
		given(this.artifactory.server("http://repo.example.com", "admin", "password")).willReturn(this.artifactoryServer);
		given(this.artifactoryServer.repository("libs-snapshot-local")).willReturn(this.repository);
		given(this.repository.fetchAll("my-build", "5678")).willReturn(getAllArtifacts());
	}

	@Test
	public void runWithoutMetadataShouldFetch() throws Exception {
		String path = getPath();
		String requestJson = getJson(COMMAND_RESOURCE_PATH + "/in-request.json");
		MockSystemStreams systemStreams = new MockSystemStreams(requestJson);
		SystemInputJson inputJson = new SystemInputJson(
				systemStreams, new ObjectMapper());
		InCommand inCommand = new InCommand(inputJson, this.artifactory);
		inCommand.run(new DefaultApplicationArguments(new String[] {"in", path}));
		verify(this.repository).fetch("/org/jfrog/artifactory/artifactory.war", path);
		verifyOutput(systemStreams);
	}

	private String getPath() throws Exception {
		temporaryFolder.create();
		String path = temporaryFolder.getRoot().toString();
		return path;
	}

	private FetchResults getAllArtifacts() throws Exception {
		String fetchArtifactsJson = getJson("/io/spring/concourse/artifactoryresource/artifactory/payload/fetch-artifacts.json");
		return new ObjectMapper().readValue(fetchArtifactsJson, FetchResults.class);
	}

	private String getJson(String name) throws Exception {
		InputStream stream = this.getClass().getResourceAsStream(name);
		return IOUtils.toString(stream);
	}

	private void verifyOutput(MockSystemStreams systemStreams) throws IOException {
		InputStream stream = this.getClass().getResourceAsStream(COMMAND_RESOURCE_PATH + "/in-response-without-metadata.json");
		String response = IOUtils.toString(stream);
		byte[] outBytes = systemStreams.getOutBytes();
		assertThat(outBytes).isNotEmpty();
		InResponse expected = this.mapper.readValue(response, InResponse.class);
		InResponse actual = this.mapper.readValue(new String(outBytes), InResponse.class);
		assertThat(actual.getVersion().getBuildNumber()).isEqualTo(expected.getVersion().getBuildNumber());
	}
}