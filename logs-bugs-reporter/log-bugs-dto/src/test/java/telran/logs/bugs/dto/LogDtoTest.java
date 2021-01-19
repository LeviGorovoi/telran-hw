package telran.logs.bugs.dto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Date;

import javax.validation.Valid;

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
	public static @RestController class TestController{
		static LogDto logDtoExp = new LogDto(new Date(), LogType.NO_EXCEPTIONS, "artifact", 0, "");
		@PostMapping("/")
		void testPost (@RequestBody @Valid LogDto logDto) {
			assertEquals(logDtoExp, logDto);
		}
		static LogDto logDtoExpWithNullDate = new LogDto(null, LogType.NO_EXCEPTIONS, "artifact", 0, "");
		@PostMapping("/WithNull/Date")
		void testPostWithNullDate (@RequestBody @Valid LogDto logDto) {
			assertEquals(logDtoExp, logDto);
		}
		
		static LogDto logDtoExpWithNullLogType = new LogDto(new Date(), null, "artifact", 0, "");
		@PostMapping("/WithNull/LogTypee")
		void testPostWithNulllLogType (@RequestBody @Valid LogDto logDto) {
			assertEquals(logDtoExp, logDto);
		}
	}
	ObjectMapper mapper = new ObjectMapper();
	@Autowired
	MockMvc mock;
	@Test
	void testPostRun () throws JsonProcessingException, Exception {
		assertEquals(200, mock.perform(post("/").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(TestController.logDtoExp)))			
				.andReturn().getResponse()
				.getStatus());
	}
	@Test
	void testPostRunWithNullDate () throws JsonProcessingException, Exception {
		assertEquals(400, mock.perform(post("/WithNull/Date").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(TestController.logDtoExpWithNullDate)))			
				.andReturn().getResponse()
				.getStatus());
	}
	@Test
	void testPostRunWithNulllLogType () throws JsonProcessingException, Exception {
		assertEquals(404, mock.perform(post("/WithNull/LogType").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(TestController.logDtoExpWithNullLogType)))			
				.andReturn().getResponse()
				.getStatus());
	}

}
