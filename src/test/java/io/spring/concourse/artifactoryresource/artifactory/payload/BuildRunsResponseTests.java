package io.spring.concourse.artifactoryresource.artifactory.payload;

import io.spring.concourse.artifactoryresource.util.ArtifactoryDateFormat;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link BuildRunsResponse}.
 *
 * @author Madhura Bhave
 * @author Phillip Webb
 */
@RunWith(SpringRunner.class)
@JsonTest
public class BuildRunsResponseTests {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Autowired
	private JacksonTester<BuildRunsResponse> json;

	@Test
	public void readShouldDeserialize() throws Exception {
		BuildRunsResponse response = this.json.readObject("build-runs-response.json");
		assertThat(response.getUri())
				.isEqualTo("http://localhost:8081/artifactory/api/build/my-build");
		assertThat(response.getBuildsRuns()).hasSize(2);
		assertThat(response.getBuildsRuns().get(0).getBuildNumber()).isEqualTo("1234");
		assertThat(response.getBuildsRuns().get(0).getUri()).isEqualTo("/1234");
		assertThat(response.getBuildsRuns().get(0).getStarted())
				.isEqualTo(ArtifactoryDateFormat.parse("2014-09-28T12:00:19.893+0000"));
		assertThat(response.getBuildsRuns().get(1).getBuildNumber()).isEqualTo("5678");
		assertThat(response.getBuildsRuns().get(1).getUri()).isEqualTo("/5678");
		assertThat(response.getBuildsRuns().get(1).getStarted())
				.isEqualTo(ArtifactoryDateFormat.parse("2014-09-30T12:00:19.893+0000"));
	}

}
