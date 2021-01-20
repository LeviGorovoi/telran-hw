package telran.logs.bugs.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.Date;

import javax.validation.Valid;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@WebMvcTest(LogDtoTest.TestController.class)
@ContextConfiguration(classes = LogDtoTest.TestController.class)
public class LogDtoTest {
	static LogDto logDtoExp;
	int status;
	public static @RestController class TestController{
		@PostMapping("/")
		void testPost (@RequestBody @Valid LogDto logDto) {
			assertEquals(logDtoExp, logDto);
		}
	}
	ObjectMapper mapper = new ObjectMapper();
	@Autowired
	MockMvc mock;
	
	@BeforeEach
	void setup() {
		logDtoExp = new LogDto(new Date(), LogType.NO_EXCEPTIONS, "artifact", 0, "");
		status = 200;
		
	}
	private void equalsTest () throws JsonProcessingException, Exception {
		assertEquals(status, mock.perform(post("/").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(logDtoExp)))			
				.andReturn().getResponse()
				.getStatus());
	}
	
	@Test
	void testPostRun () throws JsonProcessingException, Exception {	
		equalsTest ();
	}
	@Test
	void testPostRunWithNullDate () throws JsonProcessingException, Exception {
		status = 400;
		logDtoExp.logType = null;
		equalsTest ();
	}
	@Test
	void testPostRunWithNulllLogType () throws JsonProcessingException, Exception {
		status = 400;
		logDtoExp.dateTime = null;
		equalsTest ();
	}
	@Test
	void testPostRunWithEnptyArtifact () throws JsonProcessingException, Exception {
		status = 400;
		logDtoExp.artifact = "";
		equalsTest ();
	}
}
