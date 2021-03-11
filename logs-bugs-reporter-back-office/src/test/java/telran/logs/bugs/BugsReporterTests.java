package telran.logs.bugs;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.validation.constraints.*;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;


import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import telran.logs.bugs.dto.*;
import telran.logs.bugs.interfaces.BugsReporter;
import telran.logs.bugs.jpa.entities.Programmer;
import telran.logs.bugs.repo.BugRepository;

import static telran.logs.bugs.api.BugsReporterApi.*;

@SpringBootTest
@AutoConfigureWebTestClient
@AutoConfigureDataJpa
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BugsReporterTests {
	@AllArgsConstructor
	@NoArgsConstructor
	@EqualsAndHashCode
	static class EmailBugsCountTest implements EmailBugsCount {
		String email;
		long count;
		@Override
		public String getEmail() {
			return email;
		}

		@Override
		public long getCount() {			
			return count;
		}
		
	}
	@AllArgsConstructor
	@NoArgsConstructor
	@EqualsAndHashCode
	static class SeriousnessBugsCountTest implements SeriousnessBugCount {
		Seriousness seriousness;
		long count;
		@Override
		public Seriousness getSeriousness() {
			return seriousness;
		}
		@Override
		public long getCount() {
			return count;
		}			
	}
	private static final @NotEmpty String DESCRIPTION = "Not working";
	private static final LocalDate DATE_OPEN = LocalDate.of(2020,12,1);
	private static final @Min(1) long PROGRAMMER_ID_VALUE = 123;
	private static final @Email String EMAIL ="moshe@gmail.com";
	private static final @Email String VASYA_EMAIL = "vasya@gmail.com";
	private static final String ARTIFACT_ID = "artifact";
	BugDto bugUnAssigned = new BugDto(Seriousness.BLOCKING, DESCRIPTION,
			DATE_OPEN);
	BugAssignDto bugAssigned2 = new BugAssignDto(Seriousness.BLOCKING, DESCRIPTION, DATE_OPEN, PROGRAMMER_ID_VALUE);
	BugAssignDto bugAssigned3 = new BugAssignDto(Seriousness.COSMETIC, DESCRIPTION, DATE_OPEN, PROGRAMMER_ID_VALUE);
	BugResponseDto expectedUnAssigned = new BugResponseDto(1, Seriousness.BLOCKING, DESCRIPTION,
			DATE_OPEN, 0, null, BugStatus.OPENNED, OpenningMethod.MANUAL);
	BugResponseDto expectedAssigned2 = new BugResponseDto(2, Seriousness.BLOCKING, DESCRIPTION,
			DATE_OPEN, PROGRAMMER_ID_VALUE, null, BugStatus.ASSIGNED, OpenningMethod.MANUAL);
	BugResponseDto expectedAssigned3 = new BugResponseDto(3, Seriousness.BLOCKING, DESCRIPTION,
			DATE_OPEN, PROGRAMMER_ID_VALUE, null, BugStatus.ASSIGNED, OpenningMethod.MANUAL);
	BugResponseDto expectedAUnssigned4 = new BugResponseDto(3, Seriousness.MINOR, DESCRIPTION,
			DATE_OPEN, 0, null, BugStatus.OPENNED, OpenningMethod.MANUAL);
	BugResponseDto expectedAssigned1 = new BugResponseDto(1, Seriousness.BLOCKING, DESCRIPTION + BugsReporter.ASSIGNMENT_DESCRIPTION_TITLE,
			DATE_OPEN, PROGRAMMER_ID_VALUE, null, BugStatus.ASSIGNED, OpenningMethod.MANUAL);
	List<BugResponseDto> expectedBugs123 = Arrays.asList(expectedAssigned1,
			expectedAssigned2, expectedAssigned3);
	List<EmailBugsCountTest> expectedEmailCounts = Arrays.asList(new EmailBugsCountTest(EMAIL, 3),
			new EmailBugsCountTest(VASYA_EMAIL, 0));
	List<SeriousnessBugsCountTest> expectedSeriousnessCounts = Arrays.asList(new SeriousnessBugsCountTest(Seriousness.BLOCKING, 2),
			new SeriousnessBugsCountTest(Seriousness.COSMETIC, 1));
	
	@Autowired
WebTestClient testClient;
	@Autowired
	BugRepository bugRepository;
//	addProgrammers
	@Test
	@Order(1)
	void addProgrammers() {
		ProgrammerDto programmer = new ProgrammerDto(PROGRAMMER_ID_VALUE,"Moshe", EMAIL);
		normalPostTest(BUGS_PROGRAMMERS, programmer, programmer, ProgrammerDto.class);
		programmer = new ProgrammerDto(PROGRAMMER_ID_VALUE + 1, "Vasya", VASYA_EMAIL);
		normalPostTest(BUGS_PROGRAMMERS, programmer, programmer, ProgrammerDto.class);
	}
	@Test
	void addDuplicatedProgrammers(){
		ProgrammerDto programmer = new ProgrammerDto(PROGRAMMER_ID_VALUE,"Moshe", EMAIL);
		duplicatedPostRequest(BUGS_PROGRAMMERS, programmer);
	}
	@Test
	@Order(5)
	void bugsProgrammers() {
		testClient.get().uri(BUGS_PROGRAMMERS + "?" + PROGRAMMER_ID + "=" + PROGRAMMER_ID_VALUE).exchange().expectStatus().isOk()
		.expectBodyList(BugResponseDto.class).isEqualTo(expectedBugs123);
	}
	@Test
	void bugsProgrammersNoProgrammerID() {
		testClient.get().uri(BUGS_PROGRAMMERS + "?" + PROGRAMMER_ID + "=" + 10000).exchange().expectStatus().isOk()
		.expectBodyList(BugResponseDto.class).isEqualTo(new LinkedList<>());
	}

	@Test
	void invalidAddProgrammer() {
		invalidPostRequest(BUGS_PROGRAMMERS, new Programmer(1, "Moshe", "kuku"));
	}
//====================================================================================================
	
//	addArtifact
	@Test
	@Order(6)
	void addArtifactTest() {
		ArtifactDto artifactDto = new ArtifactDto(ARTIFACT_ID, PROGRAMMER_ID_VALUE);
		normalPostTest(BUGS_ARTIFACT, artifactDto, artifactDto, ArtifactDto.class);
	}
	@Test
	void wrongProgrammerIdInArtifactDto() {
		ArtifactDto artifactDto = new ArtifactDto(ARTIFACT_ID+1, 100000);
		notFoundPostRequest(BUGS_ARTIFACT, artifactDto);
	}
	@Test
	void addDuplicatedArtifactTest() {
		ArtifactDto artifactDto = new ArtifactDto(ARTIFACT_ID, PROGRAMMER_ID_VALUE);
		duplicatedPostRequest(BUGS_ARTIFACT, artifactDto);
	}
	//====================================================================================================
	
//	openBug
	@Test
	@Order(2)
	void openBug() {
		normalPostTest(BUGS_OPEN, bugUnAssigned, expectedUnAssigned, BugResponseDto.class);
	}
	@Test
	void invalidOpenBug() {
		invalidPostRequest(BUGS_OPEN, new BugDto(Seriousness.BLOCKING, null, LocalDate.now()));
	}
	//====================================================================================================

//	openAndAssign
	@Test
	@Order(3) 
	void openAndAssign() {
		normalPostTest(BUGS_OPEN_ASSIGN, bugAssigned2, expectedAssigned2, BugResponseDto.class);
		normalPostTest(BUGS_OPEN_ASSIGN, bugAssigned3, expectedAssigned3, BugResponseDto.class);

	}	
	@Test
	void invalidOpenAssignBug() {
		invalidPostRequest(BUGS_OPEN_ASSIGN, new BugAssignDto(Seriousness.BLOCKING,
				DESCRIPTION, DATE_OPEN, -20));
	}
	@Test
	void invalidOpenAndAssign() {
		BugAssignDto invalidBugAssignDto =
				new BugAssignDto(Seriousness.BLOCKING,
						DESCRIPTION, null, 100000);
		notFoundPostRequest(BUGS_OPEN_ASSIGN, invalidBugAssignDto);
	}
	//====================================================================================================
	
//	assign
	@Test
	@Order(4)
	void assign() {
		AssignBugData assignBugData = new AssignBugData(1, PROGRAMMER_ID_VALUE, "");
		normalPutTest(BUGS_ASSIGN, assignBugData);		
	}
	
	@Test
	void assignWrongBug() {
		AssignBugData assignBugData = new AssignBugData(100, PROGRAMMER_ID_VALUE, "");
		notFoundPutRequest(BUGS_ASSIGN, assignBugData);
	}
	@Test
	void assignToWrongProgrammer() {
		AssignBugData assignBugData = new AssignBugData(4, 300, "");
		notFoundPutRequest(BUGS_ASSIGN, assignBugData);
	}
	@Test
	void DuplicatedAssign() {
		AssignBugData assignBugData = new AssignBugData(1, PROGRAMMER_ID_VALUE, "");
		duplicatedPutRequest(BUGS_ASSIGN, assignBugData);
	}
	@Test
	void invalidAssignBug() {
		invalidPutRequest(BUGS_ASSIGN, new AssignBugData(0, PROGRAMMER_ID_VALUE, DESCRIPTION));
	}
	//====================================================================================================

//	closeBugTest
	@Test
	@Order(7)
	void closeBugTest() {
		CloseBugData closeBugData = new CloseBugData(1, LocalDate.now(), "");
		normalPutTest(CLOSE_BUG, closeBugData);
	}
	
	@Test
	void closeBugWithWrongIdTest() {
		CloseBugData closeBugData = new CloseBugData(50, LocalDate.now(), "");
		notFoundPutRequest(CLOSE_BUG, closeBugData);
		
	}
	@Test
	void closeClosedBugTest() {
		CloseBugData closeBugData = new CloseBugData(1, LocalDate.now(), "");
		duplicatedPutRequest(CLOSE_BUG, closeBugData);
		
	}
	//====================================================================================================
	
//	getEmailBugsCount	
	@Test
	void emailCounts() {
		testClient.get().uri(BUGS_PROGRAMMERS_COUNT).exchange().expectStatus().isOk()
		.expectBodyList(EmailBugsCountTest.class).isEqualTo(expectedEmailCounts);
	}
	//====================================================================================================
	
//	getProgrammersMostBugs
	@Test
	void getProgrammersMostBugsTest() {
		testClient.get().uri(PROGRAMMER_MOST_BUGS+"?"+N_PROGRAMMERS+"=1").exchange().expectStatus().isOk()
		.expectBody(String[].class).returnResult().getResponseBody();
		testClient.get().uri(PROGRAMMER_MOST_BUGS+"?"+N_PROGRAMMERS+"=2").exchange().expectStatus().isOk()
		.expectBody(String[].class).isEqualTo(new String[]{"Moshe", "Vasya"});
	}
	//====================================================================================================
	
//	getProgrammersLeastBugs
	@Test
	void getProgrammersLeastBugsTest() {
		testClient.get().uri(PROGRAMMER_LEAST_BUGS+"?"+N_PROGRAMMERS+"=2").exchange().expectStatus().isOk()
		.expectBody(String[].class).isEqualTo(new String[]{"Vasya", "Moshe"});
	}
	//====================================================================================================

//	getSeriousnessBugCounts
	@Test
	void seriousnessCounts() {
		testClient.get().uri(BUGS_SERIOUSNESS_COUNT).exchange().expectStatus().isOk()
		.expectBodyList(SeriousnessBugsCountTest.class).isEqualTo(expectedSeriousnessCounts);
	}
	//====================================================================================================
	
//	getSeriousnessTypesWithMostBugsTest
	@Test
	void getSeriousnessTypesWithMostBugsTest() {
		testClient.get().uri(BUGS_SERIOUSNESS_TYPES_MOST_BUGS+"?"+N_TYPES+"=2").exchange().expectStatus().isOk()
		.expectBodyList(Seriousness.class).isEqualTo(List.of(Seriousness.BLOCKING, Seriousness.COSMETIC));
		testClient.get().uri(BUGS_SERIOUSNESS_TYPES_MOST_BUGS+"?"+N_TYPES+"=1").exchange().expectStatus().isOk()
		.expectBodyList(Seriousness.class).isEqualTo(List.of(Seriousness.BLOCKING));
	}
	
	@Test
	void invalidSeriousnessMostBugs() {
		String uriStr = BUGS_SERIOUSNESS_TYPES_MOST_BUGS + "?" + N_TYPES + "=" + "-1";
		invalidGetRequest(uriStr);
	}
	//====================================================================================================

	private void invalidGetRequest(String uriStr) {
		testClient.get().uri(uriStr)
		.exchange().expectStatus().isBadRequest();
	}
	
	private void invalidPostRequest(String uriStr, Object invalidObject) {
		testClient.post().uri(uriStr).contentType(MediaType.APPLICATION_JSON).bodyValue(invalidObject)
		.exchange().expectStatus().isBadRequest();
	}
	private void invalidPutRequest(String uriStr, Object invalidObject) {
		testClient.put().uri(uriStr).contentType(MediaType.APPLICATION_JSON).bodyValue(invalidObject)
		.exchange().expectStatus().isBadRequest();
	}
	
	private void notFoundPostRequest(String uriStr, Object invalidObject){
		testClient.post().uri(uriStr).contentType(MediaType.APPLICATION_JSON).bodyValue(invalidObject)
		.exchange().expectStatus().isNotFound();
	}
	private void notFoundPutRequest(String uriStr, Object invalidObject){
		testClient.put().uri(uriStr).contentType(MediaType.APPLICATION_JSON).bodyValue(invalidObject)
		.exchange().expectStatus().isEqualTo(HttpStatus.NOT_FOUND);
	}

	private void duplicatedPutRequest(String uriStr, Object invalidObject) {
		testClient.put().uri(uriStr).contentType(MediaType.APPLICATION_JSON).bodyValue(invalidObject).exchange()
				.expectStatus().isEqualTo(HttpStatus.CONFLICT);
	}
	private void duplicatedPostRequest(String uriStr, Object invalidObject) {
		testClient.post().uri(uriStr).contentType(MediaType.APPLICATION_JSON).bodyValue(invalidObject).exchange()
				.expectStatus().isEqualTo(HttpStatus.CONFLICT);
	}
	private <T> void normalPostTest (String uriStr, Object requestObj, T responseObj, Class<T> responseClass) {
		testClient.post().uri(uriStr)
		.contentType(MediaType.APPLICATION_JSON).bodyValue(requestObj).exchange().expectStatus().isOk()
		.expectBody(responseClass).isEqualTo( responseObj);
	}
	private void normalPutTest(String uriStr, Object responseObj) {
		testClient.put().uri(uriStr).bodyValue(responseObj)
		.exchange().expectStatus().isOk();
	}
}

