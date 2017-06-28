package io.spring.concourse.artifactoryresource.artifactory.payload;

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
 * Tests for {@link DeployedArtifactQueryResponse}.
 *
 * @author Madhura Bhave
 * @author Phillip Webb
 */
@RunWith(SpringRunner.class)
@JsonTest
public class DeployedArtifactQueryResponseTests {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Autowired
	private JacksonTester<DeployedArtifactQueryResponse> json;

	@Test
	public void readShouldDeserialize() throws Exception {
		DeployedArtifactQueryResponse response = this.json
				.readObject("fetch-artifacts.json");
		assertThat(response.getResults()).hasSize(1);
		DeployedArtifact artifact = response.getResults().get(0);
		assertThat(artifact.getRepo()).isEqualTo("libs-release-local");
		assertThat(artifact.getPath()).isEqualTo("org/jfrog/artifactory");
		assertThat(artifact.getName()).isEqualTo("artifactory.war");
		assertThat(artifact.getType()).isEqualTo("item type");
		assertThat(artifact.getSize()).isEqualTo(75500000);
		assertThat(artifact.getCreated())
				.isEqualTo(ArtifactoryDateFormat.parse("2017-06-19T17:17:33.423-0700"));
		assertThat(artifact.getCreatedBy()).isEqualTo("jfrog");
		assertThat(artifact.getModified())
				.isEqualTo(ArtifactoryDateFormat.parse("2017-06-19T17:17:34.423-0700"));
		assertThat(artifact.getModifiedBy()).isEqualTo("spring");
		assertThat(artifact.getUpdated())
				.isEqualTo(ArtifactoryDateFormat.parse("2017-06-19T17:17:35.423-0700"));
		assertThat(response.getRange().getStartPos()).isEqualTo(0);
		assertThat(response.getRange().getEndPos()).isEqualTo(1);
		assertThat(response.getRange().getTotal()).isEqualTo(1);
	}

}