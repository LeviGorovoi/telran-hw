package telran.logs.bugs;

import java.time.Duration;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import telran.logs.bugs.dto.*;
import telran.logs.bugs.mongo.doc.LogDoc;
import telran.logs.bugs.random.RandomLogs;
import telran.logs.bugs.repo.LogRepository;

@SpringBootTest(classes=LogsInfoAppl.class)
@AutoConfigureWebTestClient
public class LogsInfoTests {
	private static final long BUFFERING_TIME_SPAN = 100;
	@Autowired
	WebTestClient webClient;
	@Autowired
	LogRepository logRepo;
	@Autowired
RandomLogs randomLogs;
	@Value("${app-number-logs:0}")
	int nLogs;
	Logger LOG = LoggerFactory.getLogger(LogsInfoTests.class);
	LogDto exceptionLessLog = new LogDto(new Date(), LogType.NO_EXCEPTION, "artifact", 0, "");
	LogDto exceptionLog = new LogDto(new Date(), LogType.AUTHENTICATION_EXCEPTION, "artifact", 0, "");
	@BeforeEach
	void setup() {
		logRepo.deleteAll().block();	
	logRepo.save(new LogDoc(exceptionLessLog)).block();
	logRepo.save(new LogDoc(exceptionLog)).block();	
	}
	@Test
		void getAllLogsTest() {
			webClient.get().uri("/logs")
			.exchange().expectStatus().isOk().expectBodyList(LogDto.class).hasSize(2).contains(exceptionLessLog).contains(exceptionLog);
		}
	
	@Test
	void getAllExceptionsTest() {
		webClient.get().uri("/logs/exceptions")
		.exchange().expectStatus().isOk().expectBodyList(LogDto.class).hasSize(1).doesNotContain(exceptionLessLog).contains(exceptionLog);
	}
	
	@Test
	void getLogsTypeTest() {
		LinkedMultiValueMap<String,LogType> map =new LinkedMultiValueMap<>();
		map.add("type", LogType.AUTHENTICATION_EXCEPTION);
		webClient.get().uri("/logs/type?type=AUTHENTICATION_EXCEPTION")
		.exchange().expectStatus().isOk().expectBodyList(LogDto.class).hasSize(1).doesNotContain(exceptionLessLog).contains(exceptionLog);
	}
	@Test
	void createFluxTest() {
		 Flux.create(emiter->{
				for(int i = 0;i<nLogs; i++) {
					emiter.next(new LogDoc(randomLogs.createRandomLog()));
				}
				emiter.complete();
			}).count().block();
	long fluxValuesNumber = Flux.create(emiter->{
		for(int i = 0;i<nLogs; i++) {
			emiter.next(new LogDoc(randomLogs.createRandomLog()));
		}
		emiter.complete();
	}).count().block();
	assertEquals(nLogs, fluxValuesNumber);
	long BufferedValuesNumber = Flux.create(emiter->{
		for(int i = 0;i<nLogs; i++) {
			emiter.next(new LogDoc(randomLogs.createRandomLog()));
		}
		emiter.complete();
	}).buffer(Duration.ofMillis(BUFFERING_TIME_SPAN)).take(1).blockFirst().size();
	LOG.info("create method created {} items for {} ms", BufferedValuesNumber, BUFFERING_TIME_SPAN);
	}
	
	@Test
	void pushFluxTest() {
//	long fluxValuesNumber = Flux.push(emiter->{
//		for(int i = 0;i<nLogs; i++) {
//			emiter.next(new LogDoc(randomLogs.createRandomLog()));
//		}
//		emiter.complete();
//	}).count().block();
//	assertEquals(nLogs, fluxValuesNumber);
	long BufferedValuesNumber = Flux.push(emiter->{
		for(int i = 0;i<nLogs; i++) {
			emiter.next(new LogDoc(randomLogs.createRandomLog()));
		}
		emiter.complete();
	}).buffer(Duration.ofMillis(BUFFERING_TIME_SPAN)).take(1).blockFirst().size();
	LOG.info("push method created {} items for {} ms", BufferedValuesNumber, BUFFERING_TIME_SPAN);
	}
	
	@Test
	void multyThreadPushFluxTest() {
		Scheduler scheduler = Schedulers.parallel();
//	long fluxValuesNumber = Flux.push(emiter->{
//		for(int i = 0;i<nLogs; i++) {
//			emiter.next(new LogDoc(randomLogs.createRandomLog()));
//		}
//		emiter.complete();
//	}).publishOn(scheduler).doFinally(cause->scheduler.dispose()).count().block();
//	assertEquals(nLogs, fluxValuesNumber);
	long BufferedValuesNumber = Flux.push(emiter->{
		for(int i = 0;i<nLogs; i++) {
			emiter.next(new LogDoc(randomLogs.createRandomLog()));
		}
		emiter.complete();
	}).publishOn(scheduler).doFinally(cause->scheduler.dispose()).buffer(Duration.ofMillis(BUFFERING_TIME_SPAN)).take(1).blockFirst().size();
	LOG.info("multythread push method created {} items for {} ms", BufferedValuesNumber, BUFFERING_TIME_SPAN);
	}	
	

	
	
}
