package org.nrg.xnatx.plugins.rapidViewer.rest;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isIn;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.nrg.xnatx.plugins.rapidViewer.dto.WorkItemDto;
import org.nrg.xnatx.plugins.rapidViewer.dto.WorkListDto;
import org.nrg.xnatx.plugins.rapidViewer.entities.WorkList;
import org.nrg.xnatx.plugins.rapidViewer.entities.WorkList.WorkListStatus;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WorkListApiTest {

	public static final String XNAT_ROOT_URL = "http://localhost:8081";
	public static final String SITEADMIN_USERNAME = "admin";
	public static final String SITEADMIN_PASSWORD = "admin";
	public static final String OWNER_USERNAME = "user";

	final static List<String> COOKIES = new ArrayList<>();

	@BeforeClass
	public static void loginToXnat() throws Exception {
		// Given
		String url = String.format("%s/data/services/auth", XNAT_ROOT_URL);
		final HttpPut request = new HttpPut(url);
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("username", SITEADMIN_USERNAME));
		pairs.add(new BasicNameValuePair("password", SITEADMIN_PASSWORD));
		request.setEntity(new UrlEncodedFormEntity(pairs));

		// When
		final HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		if (statusCode != HttpStatus.SC_OK) {
			throw new Exception("Login failed: " + statusCode);
		}

		String msg = EntityUtils.toString(httpResponse.getEntity());
		COOKIES.add(String.format("JSESSIONID=%s", msg));
	}

	@Test
	public void givenAuthNotProvided_whenWorkListRetrieved_then401IsReceived()
			throws ClientProtocolException, IOException {
		// Given
		String url = String.format("%s/xapi/workLists", XNAT_ROOT_URL);
		final HttpUriRequest request = new HttpGet(url);

		// When
		final HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		int statusCode = httpResponse.getStatusLine().getStatusCode();

		// Then
		assertThat(statusCode, equalTo(HttpStatus.SC_UNAUTHORIZED));
	}

	@Test
	public void givenAuthProvided_whenWorkListRetrieved_then200IsReceived()
			throws ClientProtocolException, IOException {
		// Given
		String url = String.format("%s/xapi/workLists", XNAT_ROOT_URL);
		final HttpUriRequest request = new HttpGet(url);
		System.out.println(COOKIES.get(0));
		request.addHeader("Cookie", String.join("; ", COOKIES));

		// When
		final HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		int statusCode = httpResponse.getStatusLine().getStatusCode();

		// Then
		assertThat(statusCode, equalTo(HttpStatus.SC_OK));
	}

	public WorkListDto createWorkListDto(String name, String desc, Date dueDate, String ownerUsername,
			String reportId) {
		return WorkListDto.builder().name(name).description(desc).dueDate(dueDate).ownerUsername(ownerUsername)
				.reportId(reportId).build();
	}

	public WorkItemDto createWorkItemDto(String projectId, String subjectId, String experimentId,
			String experimentLabel) {
		return WorkItemDto.builder().projectId(projectId).subjectId(subjectId).experimentId(experimentId)
				.experimentLabel(experimentLabel).build();
	}

	@Test
	public void givenAuthProvided_whenWorkListPosted_then200IsReceived() throws ClientProtocolException, IOException {
		// Given
		String url = String.format("%s/xapi/workLists", XNAT_ROOT_URL);
		HttpPost request = new HttpPost(url);
		request.addHeader("Cookie", String.join("; ", COOKIES));

		// When
		final ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
				false);
		String name = "name";
		String desc = "desc";
		Date dueDate = new Date();
		String ownerUsername = OWNER_USERNAME;
		String reportId = "RPT101";

		WorkListDto dto = createWorkListDto(name, desc, dueDate, ownerUsername, reportId);
		String json = mapper.writeValueAsString(dto);
		System.out.println("post json: " + json);

		StringEntity entity = new StringEntity(json);
		request.setEntity(entity);
		request.setHeader("Accept", "application/json");
		request.setHeader("Content-type", "application/json");
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		System.out.println("post statusCode: " + statusCode);

		// Then
		assertThat(statusCode, equalTo(HttpStatus.SC_OK));

		WorkList workList = RetrieveUtil.retrieveResourceFromResponse(httpResponse, WorkList.class);
		System.out.println("res json: " + workList.toString());
		assertThat(workList.getId(), notNullValue());
		assertThat(workList.getName(), equalTo(name));
		assertThat(workList.getDescription(), equalTo(desc));
		assertThat(workList.getDueDate(), equalTo(dueDate));
		assertThat(workList.getReportId(), equalTo(reportId));
		assertThat(workList.getStatus(), equalTo(WorkListStatus.InProgress));

		// Given
		Long workListId = workList.getId();
		url = String.format("%s/xapi/workLists/%d", XNAT_ROOT_URL, workListId);
		HttpPut httpPut = new HttpPut(url);
		httpPut.addHeader("Cookie", String.join("; ", COOKIES));

		// When
		String changedReportId = "RPT102";
		dto.setId(workListId);
		dto.setReportId(changedReportId);
		json = mapper.writeValueAsString(dto);
		System.out.println("put json: " + json);

		entity = new StringEntity(json);
		httpPut.setEntity(entity);
		httpPut.setHeader("Accept", "application/json");
		httpPut.setHeader("Content-type", "application/json");
		httpResponse = HttpClientBuilder.create().build().execute(httpPut);
		statusCode = httpResponse.getStatusLine().getStatusCode();
		System.out.println("put statusCode: " + statusCode);

		// Then
		assertThat(statusCode, equalTo(HttpStatus.SC_OK));

		workList = RetrieveUtil.retrieveResourceFromResponse(httpResponse, WorkList.class);
		System.out.println("res json: " + workList.toString());
		assertThat(workList.getId(), equalTo(workListId));
		assertThat(workList.getName(), equalTo(name));
		assertThat(workList.getDescription(), equalTo(desc));
		assertThat(workList.getDueDate(), equalTo(dueDate));
		assertThat(workList.getReportId(), equalTo(changedReportId));
		assertThat(workList.getStatus(), equalTo(WorkListStatus.InProgress));

		// Create a workItem
		// Given
		url = String.format("%s/xapi/workLists/%d", XNAT_ROOT_URL, workListId);
		HttpPost httpPost = new HttpPost(url);
		request.addHeader("Cookie", String.join("; ", COOKIES));

		// When
		String projectId = "proj";
		String subjectId = "subj";
		String experimentId = "name";
		String experimentLabel = "desc";

		WorkItemDto workItemDto = createWorkItemDto(projectId, subjectId, experimentId, experimentLabel);
		json = mapper.writeValueAsString(workItemDto);
		System.out.println("workItem post json: " + json);

		entity = new StringEntity(json);
		httpPost.setEntity(entity);
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Content-type", "application/json");
		httpResponse = HttpClientBuilder.create().build().execute(request);
		statusCode = httpResponse.getStatusLine().getStatusCode();
		System.out.println("workItem post statusCode: " + statusCode);

		// Then
		assertThat(statusCode, isIn(Arrays.asList(HttpStatus.SC_OK)));


		// Delete
		// Given
		url = String.format("%s/xapi/workLists/%d", XNAT_ROOT_URL, workListId);
		HttpDelete httpDelete = new HttpDelete(url);
		httpDelete.addHeader("Cookie", String.join("; ", COOKIES));

		// When
		httpResponse = HttpClientBuilder.create().build().execute(httpDelete);
		statusCode = httpResponse.getStatusLine().getStatusCode();
		System.out.println("delete statusCode: " + statusCode);

		// Then
		assertThat(statusCode, equalTo(HttpStatus.SC_OK));
	}

	@Test
	public void givenRequestWithNoAcceptHeader_whenRequestIsExecuted_thenDefaultResponseContentTypeIsJson()
			throws ClientProtocolException, IOException {
		// Given
		final String jsonMimeType = "application/json";
		final HttpUriRequest request = new HttpGet("https://api.github.com/users/eugenp");

		// When
		final HttpResponse response = HttpClientBuilder.create().build().execute(request);

		// Then
		final String mimeType = ContentType.getOrDefault(response.getEntity()).getMimeType();
		assertEquals(jsonMimeType, mimeType);
	}
}
