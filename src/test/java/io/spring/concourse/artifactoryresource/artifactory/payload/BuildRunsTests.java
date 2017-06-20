package io.spring.concourse.artifactoryresource.artifactory.payload;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import io.spring.concourse.artifactoryresource.artifactory.payload.BuildRuns.BuildNumber;
import org.junit.Before;
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
 * Tests for {@link BuildRuns}.
 *
 * @author Madhura Bhave
 */
@RunWith(SpringRunner.class)
@JsonTest
public class BuildRunsTests {

	private static final String URI = "http://localhost:8081/artifactory/api/build/my-build";

	private static final List<BuildNumber> BUILD_NUMBERS = new ArrayList<>();

	@Before
	public void setUp() throws Exception {
		BUILD_NUMBERS.add(new BuildNumber("/1234", parseDate("2014-09-28T12:00:19.893+0000")));
		BUILD_NUMBERS.add(new BuildNumber("/5678", parseDate("2014-09-30T12:00:19.893+0000")));
	}

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Autowired
	private JacksonTester<BuildRuns> json;

	@Test
	public void writeShouldSerialize() throws Exception {
		BuildRuns buildInfo = new BuildRuns(URI, BUILD_NUMBERS);
		assertThat(this.json.write(buildInfo)).isEqualToJson("build-runs.json");
	}

	private static Date parseDate(String date) {
		try {
			return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse(date);
		}
		catch (ParseException ex) {
			throw new RuntimeException(ex);
		}
	}

}
