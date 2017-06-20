package io.spring.concourse.artifactoryresource.artifactory;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.spring.concourse.artifactoryresource.artifactory.payload.BuildArtifact;
import io.spring.concourse.artifactoryresource.artifactory.payload.BuildInfo;
import io.spring.concourse.artifactoryresource.artifactory.payload.BuildModule;
import io.spring.concourse.artifactoryresource.artifactory.payload.BuildRuns;
import io.spring.concourse.artifactoryresource.artifactory.payload.BuildRuns.BuildNumber;
import io.spring.concourse.artifactoryresource.artifactory.payload.ContinuousIntegrationAgent;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.web.client.MockServerRestTemplateCustomizer;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Tests for {@link HttpArtifactoryBuildRuns}.
 *
 * @author Madhura Bhave
 */
@RunWith(SpringRunner.class)
@RestClientTest(HttpArtifactory.class)
public class HttpArtifactoryBuildRunsTests {

	@Autowired
	private MockRestServiceServer server;

	@Autowired
	private MockServerRestTemplateCustomizer customizer;

	@Autowired
	private Artifactory artifactory;

	@ClassRule
	public static final TemporaryFolder temporaryFolder = new TemporaryFolder();

	private final ObjectMapper mapper = new ObjectMapper();

	@After
	public void tearDown() throws Exception {
		this.customizer.getExpectationManagers().clear();
	}

	@Test
	public void addShouldAddBuildInfo() throws Exception {
		ContinuousIntegrationAgent agent = new ContinuousIntegrationAgent("Concourse", null);
		ArtifactoryBuildRuns buildRuns = this.artifactory
				.server("http://repo.example.com", "admin", "password")
				.buildRuns("my-build");
		List<BuildArtifact> artifacts = Collections.singletonList(new BuildArtifact("file", "sha-1", "md5", "my-artifact"));
		List<BuildModule> modules = Collections.singletonList(new BuildModule("artifact-id", artifacts));
		BuildInfo expected = new BuildInfo("my-build", "1234", agent, null, "ci.build.io", modules);
		this.server.expect(requestTo("http://repo.example.com/api/build"))
				.andExpect(content().contentType(APPLICATION_JSON))
				.andExpect(method(PUT))
				.andExpect(content().string(Matchers.containsString(this.mapper.writeValueAsString(expected))))
				.andRespond(withSuccess());
		buildRuns.add("1234", "ci.build.io", agent, modules);
		this.server.verify();
	}

	@Test
	public void getAllShouldReturnBuildRuns() throws Exception {
		ArtifactoryBuildRuns buildRuns = this.artifactory
				.server("http://repo.example.com", "admin", "password")
				.buildRuns("my-build");
		List<BuildNumber> buildsNumbers = Collections.singletonList(new BuildNumber("/1234", null));
		BuildRuns runs = new BuildRuns("http://my-build-run.com", buildsNumbers);
		this.server.expect(requestTo("http://repo.example.com/api/build/my-build"))
				.andExpect(method(GET))
				.andRespond(withSuccess(this.mapper.writeValueAsString(runs), APPLICATION_JSON));
		BuildRuns all = buildRuns.getAll();
		assertThat(all.getUri()).isEqualTo("http://my-build-run.com");
		assertThat(all.getBuildsNumbers().get(0).getUri()).isEqualTo("/1234");
	}
}