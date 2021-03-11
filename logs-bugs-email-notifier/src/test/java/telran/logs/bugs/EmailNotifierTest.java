package telran.logs.bugs;


import java.util.Date;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mail.MailSenderValidatorAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.support.GenericMessage;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;

import telran.logs.bugs.client.EmailProviderClient;
import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;

@SpringBootTest
@Import({TestChannelBinderConfiguration.class, MailSenderValidatorAutoConfiguration.class})
public class EmailNotifierTest {
	private static final String PROGRAMMER_EMAIL = "moshe@gmail.com";
	private static final String TEAMLEADER_EMAIL = "boss@gmail.com";
	@RegisterExtension
	static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
	.withConfiguration(GreenMailConfiguration.aConfig().withUser("log", "logs-bugs"));
	@MockBean
	EmailProviderClient client;
	@Autowired
	InputDestination input;
	@Value("${message.to.programmer.subject:exception}")
	String messageToProgrammerSubject;
	@Value("${message.to.teamleader.subject:exception}")
	String messageToTeamleaderSubject;
	@Value("${message.to.programmer.addressee}")
	String messageToProgrammerAddresee;
	@Value("${message.to.teamleader.addressee}")
	String messageToTeamleaderAdressee;
	
	private void  tests (String mail, String subject, String addresee) throws MessagingException {
		LogDto logException = new LogDto(new Date(), LogType.AUTHENTICATION_EXCEPTION,
				"artifact", 0, "result");
		input.send(new GenericMessage<LogDto>(logException));
		MimeMessage message = greenMail.getReceivedMessages()[0];
		assertEquals(mail, message.getAllRecipients()[0].toString());
		assertEquals(subject, message.getSubject());
		assertEquals(getText(logException, addresee), GreenMailUtil.getBody(message));
	}
	@Test
	void normalFlow() throws MessagingException {
		when(client.getEmailByArtifact(anyString())).thenReturn(PROGRAMMER_EMAIL);
		tests (PROGRAMMER_EMAIL, messageToProgrammerSubject, messageToProgrammerAddresee);
		
	}
	@Test
	void withoutProgrammerEmail() throws MessagingException {
		when(client.getEmailByArtifact(anyString())).thenReturn(null);
		when(client.getAssignerMail()).thenReturn(TEAMLEADER_EMAIL);
		tests (TEAMLEADER_EMAIL, messageToTeamleaderSubject, messageToTeamleaderAdressee);
		
		
	}
	
	@Test
	void withoutAnyEmail() throws MessagingException {
		when(client.getEmailByArtifact(anyString())).thenReturn(null);
		when(client.getAssignerMail()).thenReturn(null);
		LogDto logException = new LogDto(new Date(), LogType.AUTHENTICATION_EXCEPTION,
				"artifact", 0, "result");
		input.send(new GenericMessage<LogDto>(logException));
		assertTrue(greenMail.getReceivedMessages().length==0);
	}
	private String getText(LogDto logDto, String addressee) {
		return String.format(
				"Hello, %s%nException has been received%nDate: %s%nException type: %s%nArtifact: %s%nExplanation: %s",
				addressee, logDto.dateTime, logDto.logType, logDto.artifact, logDto.result);
	}

}
