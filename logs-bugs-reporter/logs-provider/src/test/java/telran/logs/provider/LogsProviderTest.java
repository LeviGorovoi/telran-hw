package telran.logs.provider;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import telran.logs.bugs.dto.LogType;
import telran.logs.bugs.mongo.doc.LogDoc;
import telran.logs.bugs.provider.RandomLogs;
import static telran.logs.bugs.provider.RandomLogs.*;

class LogsProviderTest {

	private static final double EXCEPTION_PROB = 0.1;
	private static final double SEC_EXCEPTION_PROB = 0.3;
	private static final double AUTHENT_SEC_EXCEPTION_PROB = 0.7;
	private static final int NUMBER_OF_DOCS = 100000;
	static RandomLogs randomLogsDoc;
	static double rendomNumber;
	
	static private LogDoc createRandomLogsDoc() throws Exception {
		rendomNumber = Math.random();
	randomLogsDoc = new RandomLogs(EXCEPTION_PROB, SEC_EXCEPTION_PROB, AUTHENT_SEC_EXCEPTION_PROB, rendomNumber);	
	return randomLogsDoc.createRandomLog();
	}	
	
	static private List<LogDoc> generateListOfRandomDocs(){
		List<LogDoc> docStream = Stream.generate(() -> {
		try {
			return createRandomLogsDoc();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}).parallel().limit(NUMBER_OF_DOCS).collect(Collectors.toList());
		return docStream;
	}
	
	private Map<LogType, Long> createOccurrenceTableByLogType() {
		 return  generateListOfRandomDocs().stream().parallel().collect(Collectors.groupingBy(doc->doc.getLogType(), Collectors.counting()));
	}

	@Test
	void logTypeTest() {
		Map<LogType, Long> docsMap = createOccurrenceTableByLogType();
		Map<LogType, Double> docsMapExpected = new HashMap<>();
		docsMapExpected.put(LogType.NO_EXCEPTION, 1-EXCEPTION_PROB);
		docsMapExpected.put(LogType.BAD_REQUEST_EXCEPTION, EXCEPTION_PROB*(1-SEC_EXCEPTION_PROB)/4);
		docsMapExpected.put(LogType.NOT_FOUND_EXCEPTION, EXCEPTION_PROB*(1-SEC_EXCEPTION_PROB)/4);
		docsMapExpected.put(LogType.DUPLICATED_KEY_EXCEPTION, EXCEPTION_PROB*(1-SEC_EXCEPTION_PROB)/4);
		docsMapExpected.put(LogType.SERVER_EXCEPTION, EXCEPTION_PROB*(1-SEC_EXCEPTION_PROB)/4);
		docsMapExpected.put(LogType.AUTHENTICATION_EXCEPTION, EXCEPTION_PROB*SEC_EXCEPTION_PROB*AUTHENT_SEC_EXCEPTION_PROB);
		docsMapExpected.put(LogType.AUTHORIZATION_EXCEPTION, EXCEPTION_PROB*SEC_EXCEPTION_PROB*(1-AUTHENT_SEC_EXCEPTION_PROB));

		docsMapExpected.entrySet().forEach(item->{			
			System.out.println(item.getKey() +" " +item.getValue());			
			assertTrue(Math.abs(item.getValue() - (double) docsMap.get(item.getKey())/NUMBER_OF_DOCS)<0.05);
		});		
	}

	@Test
	void othersTests() {
		generateListOfRandomDocs().stream().parallel().forEach(doc->{
//			Artifact
			if(doc.getLogType().equals(LogType.AUTHENTICATION_EXCEPTION)) {
				assertTrue(doc.getArtifact().equalsIgnoreCase(AUTHENTICATION));
				assertFalse(doc.getArtifact().matches("class"));
				assertFalse(doc.getArtifact().equalsIgnoreCase(AUTHORIZATION));
			}else if(doc.getLogType().equals(LogType.AUTHORIZATION_EXCEPTION)) {
				assertTrue(doc.getArtifact().equalsIgnoreCase(AUTHORIZATION));
				assertFalse(doc.getArtifact().matches("class"));
				assertFalse(doc.getArtifact().equalsIgnoreCase(AUTHENTICATION));
			}else {
				assertTrue(doc.getArtifact().matches("class[0-9]+"));
				assertFalse(doc.getArtifact().matches("class"));
				assertFalse(doc.getArtifact().equalsIgnoreCase(AUTHORIZATION));
			}
			
//			responseTime
			if(doc.getLogType().equals(LogType.NO_EXCEPTION)) {
				assertTrue(doc.getResponseTime()==1);
				assertFalse(doc.getResponseTime()==0);
			}else {
				assertTrue(doc.getResponseTime()==0);
				assertFalse(doc.getResponseTime()==1);
			}
			
//			result			
				assertTrue(doc.getResult().isEmpty());
				assertFalse(!doc.getResult().isEmpty());			
		});
	}
	
}
