package io.spring.concourse.artifactoryresource.command;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.spring.concourse.artifactoryresource.artifactory.ArtifactoryBuildRuns;
import io.spring.concourse.artifactoryresource.artifactory.ArtifactoryServer;
import io.spring.concourse.artifactoryresource.artifactory.HttpArtifactory;
import io.spring.concourse.artifactoryresource.artifactory.payload.BuildRunsResponse;
import io.spring.concourse.artifactoryresource.command.payload.CheckResponse;
import io.spring.concourse.artifactoryresource.command.payload.Version;
import io.spring.concourse.artifactoryresource.system.MockSystemStreams;
import io.spring.concourse.artifactoryresource.system.SystemInput;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link CheckCommand}.
 *
 * @author Madhura Bhave
 */
@RunWith(SpringRunner.class)
public class CheckCommandTests {

	@MockBean
	HttpArtifactory artifactory;

	private final String RESOURCE_PATH = "/io/spring/concourse/artifactoryresource/command/payload";

	private final ObjectMapper mapper = new ObjectMapper();

	private ArtifactoryServer artifactoryServer;

	private ArtifactoryBuildRuns buildRuns;

	@Test
	public void checkCommandWhenNoVersionShouldPrintLatest() throws Exception {
		byte[] outBytes = getCheckCommandOutput(RESOURCE_PATH + "/check-request.json");
		InputStream stream = this.getClass().getResourceAsStream(
				RESOURCE_PATH + "/check-response-without-version.json");
		String response = IOUtils.toString(stream);
		CheckResponse expected = this.mapper.readValue(response, CheckResponse.class);
		CheckResponse actual = this.mapper.readValue(new String(outBytes),
				CheckResponse.class);
		assertThat(outBytes).isNotEmpty();
		List<String> actualBuildNumbers = actual.getVersions().stream()
				.map(Version::getBuildNumber).collect(Collectors.toList());
		List<String> expectedBuildNumbers = expected.getVersions().stream()
				.map(Version::getBuildNumber).collect(Collectors.toList());
		assertThat(actualBuildNumbers).isEqualTo(expectedBuildNumbers);
	}

	@Test
	public void checkCommandWhenVersionPresentShouldPrintListOfVersionsAfterSpecifiedVersion()
			throws Exception {
		byte[] outBytes = getCheckCommandOutput(
				RESOURCE_PATH + "/check-request-with-version.json");
		InputStream stream = this.getClass()
				.getResourceAsStream(RESOURCE_PATH + "/check-response.json");
		String response = IOUtils.toString(stream);
		CheckResponse expected = this.mapper.readValue(response, CheckResponse.class);
		CheckResponse actual = this.mapper.readValue(new String(outBytes),
				CheckResponse.class);
		assertThat(outBytes).isNotEmpty();
		List<String> actualBuildNumbers = actual.getVersions().stream()
				.map(Version::getBuildNumber).collect(Collectors.toList());
		List<String> expectedBuildNumbers = expected.getVersions().stream()
				.map(Version::getBuildNumber).collect(Collectors.toList());
		assertThat(actualBuildNumbers).isEqualTo(expectedBuildNumbers);
	}

	private byte[] getCheckCommandOutput(String path) throws Exception {
		InputStream stream = this.getClass().getResourceAsStream(path);
		String json = IOUtils.toString(stream);
		MockSystemStreams systemStreams = new MockSystemStreams(json);
		SystemInput inputJson = new SystemInput(systemStreams,
				new ObjectMapper());
		this.artifactoryServer = mock(ArtifactoryServer.class);
		this.buildRuns = mock(ArtifactoryBuildRuns.class);
		given(this.artifactory.server("http://repo.example.com", "admin", "password"))
				.willReturn(artifactoryServer);
		given(this.artifactoryServer.buildRuns("my-build")).willReturn(this.buildRuns);
		given(this.buildRuns.getAll()).willReturn(getAll());
		CheckCommand checkCommand = new CheckCommand(inputJson, this.artifactory);
		checkCommand.run(new DefaultApplicationArguments(new String[] { "check" }));
		return systemStreams.getOutBytes();
	}

	public BuildRunsResponse getAll() throws Exception {
		InputStream stream = this.getClass().getResourceAsStream(
				"/io/spring/concourse/artifactoryresource/artifactory/payload/build-runs.json");
		String json = IOUtils.toString(stream);
		return new ObjectMapper().readValue(json, BuildRunsResponse.class);

	}
}