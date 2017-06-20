package io.spring.concourse.artifactoryresource.artifactory.payload;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

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
 * @author Madhura Bhave
 */
@RunWith(SpringRunner.class)
@JsonTest
public class FetchResultsTests {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Autowired
	private JacksonTester<FetchResults> json;

	@Test
	public void writeShouldSerialize() throws Exception {
		Date date = parseDate("2017-06-19T17:17:33.423-07:00");
		FetchedArtifact artifact = new FetchedArtifact("libs-release-local", "artifactory.war","org/jfrog/artifactory", "item type",
				"123", date, "Jfrog", date, "Jfrog", date);
		FetchResults.Range range = new FetchResults.Range(0, 1, 1);
		FetchResults results = new FetchResults(Collections.singletonList(artifact), range);
		assertThat(this.json.write(results)).isEqualToJson("fetch-artifacts.json");
	}

	@Test
	public void readShouldDeserialize() throws Exception {
		Date date = parseDate("2017-06-19T17:17:33.423-07:00");
		FetchedArtifact artifact = new FetchedArtifact("libs-release-local", "artifactory.war","org/jfrog/artifactory", "item type",
				"123", date, "Jfrog", date, "Jfrog", date);
		FetchResults.Range range = new FetchResults.Range(0, 1, 1);
		FetchResults expected = new FetchResults(Collections.singletonList(artifact), range);
		InputStream stream = this.getClass().getResourceAsStream("/io/spring/concourse/artifactoryresource/artifactory/payload/fetch-artifacts.json");
		FetchResults actual = this.json.read(stream).getObject();
		assertThat(actual).isNotNull();
	}

	private static Date parseDate(String date) {
		try {
			return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").parse(date);
		}
		catch (ParseException ex) {
			throw new RuntimeException(ex);
		}
	}
}