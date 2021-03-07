package telran.logs.bugs;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.jdbc.Sql;

import telran.logs.bugs.dto.*;
import telran.logs.bugs.jpa.entities.Bug;
import telran.logs.bugs.jpa.entities.Programmer;
import telran.logs.bugs.repo.ProgrammersRepo;
import telran.logs.bugs.repo.ArtifactsRepo;
import telran.logs.bugs.repo.BugsRepo;

@SpringBootTest
@AutoConfigureTestDatabase
@Import(TestChannelBinderConfiguration.class)
//@EntityScan("telran.logs.bugs.jpa.entities")
public class OpeningBugsTest {
@Autowired
ProgrammersRepo programmersRepo;
@Autowired
BugsRepo bugsRepo;
@Autowired
ArtifactsRepo artifactsRepo;
@Autowired
InputDestination input;




LogDto logOfAuthenticationException = new LogDto(new Date(), LogType.AUTHENTICATION_EXCEPTION, "authentication", 0, "result");
LogDto logOfAuthorizationException = new LogDto(new Date(), LogType.AUTHORIZATION_EXCEPTION, "authorization", 0, "result");
LogDto logOfServerException = new LogDto(new Date(), LogType.SERVER_EXCEPTION, "class", 0, "result");
LogDto logOfNotFoundException = new LogDto(new Date(), LogType.NOT_FOUND_EXCEPTION, "class", 0, "result");

Bug bugOfAuthenticationException = Bug.builder().dateOpen(LocalDate.now()).dateClose(null).seriousness(Seriousness.BLOCKING)
.programmer(Programmer.builder().id(1).name("Moishe").email("moshe@gmail.com").build()).status(BugStatus.ASSIGNED).openningMethod(OpenningMethod.AUTOMATIC).
description("AUTHENTICATION_EXCEPTION result").build();
Bug bugOfAuthorizationException = Bug.builder().dateOpen(LocalDate.now()).dateClose(null).seriousness(Seriousness.CRITICAL)
.programmer(null).status(BugStatus.OPENNED).openningMethod(OpenningMethod.AUTOMATIC).
description("AUTHORIZATION_EXCEPTION result").build();
Bug bugOfServerException = Bug.builder().dateOpen(LocalDate.now()).dateClose(null).seriousness(Seriousness.CRITICAL)
.programmer(null).status(BugStatus.OPENNED).openningMethod(OpenningMethod.AUTOMATIC).
description("SERVER_EXCEPTION result").build();
Bug bugOfNotFoundException = Bug.builder().dateOpen(LocalDate.now()).dateClose(null).seriousness(Seriousness.MINOR)
.programmer(null).status(BugStatus.OPENNED).openningMethod(OpenningMethod.AUTOMATIC).
description("NOT_FOUND_EXCEPTION result").build();

@BeforeEach
void setup() {
	bugsRepo.deleteAll();

	
}
private void sendLog(LogDto logDto) {	
	input.send(new GenericMessage<LogDto>(logDto));
			
}

@Sql("fillTables.sql")
@Test
void testOfAuthenticationException() {
	sendLog(logOfAuthenticationException);
	List<Bug> bugs = bugsRepo.findAll();
	assertEquals(1, bugs.size());
	assertEquals(bugOfAuthenticationException, bugs.get(0));	
}
//@Sql("fillTables.sql")
@Test
void testOfAuthorizationException() {
	sendLog(logOfAuthorizationException);
	List<Bug> bugs = bugsRepo.findAll();
	assertEquals(1, bugs.size());
	assertEquals(bugOfAuthorizationException, bugs.get(0));	
}
@Test
void testOfServerException() {
	sendLog(logOfServerException);
	List<Bug> bugs = bugsRepo.findAll();
	assertEquals(1, bugs.size());
	assertEquals(bugOfServerException, bugs.get(0));	
}
@Test
void testOfNotFoundException() {
	sendLog(logOfNotFoundException);
	List<Bug> bugs = bugsRepo.findAll();
	assertEquals(1, bugs.size());
	assertEquals(bugOfNotFoundException, bugs.get(0));	
}

}
