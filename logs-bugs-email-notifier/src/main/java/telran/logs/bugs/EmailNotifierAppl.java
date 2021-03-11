package telran.logs.bugs;

import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import lombok.extern.slf4j.Slf4j;
import telran.logs.bugs.client.EmailProviderClient;
import telran.logs.bugs.dto.LogDto;

@SpringBootApplication
@EntityScan("telran.logs.bugs.jpa.entities")
@Slf4j
public class EmailNotifierAppl {
	@Autowired
	EmailProviderClient emailClient;
	@Autowired
	JavaMailSender mailSender;
	@Value("${message.to.programmer.subject:exception}")
	String messageToProgrammerSubject;
	@Value("${message.to.assigner.subject:exception}")
	String messageToAssignerSubject;
	@Value("${message.to.programmer.addressee}")
	String messageToProgrammerAddressee;
	@Value("${message.to.assigner.addressee}")
	String messageToAssignerAddressee;
	@Value("${no.found.email.log}")
	String noFoundEmailLog;

	public static void main(String[] args) {
		SpringApplication.run(EmailNotifierAppl.class, args);

	}

	@Bean
	Consumer<LogDto> getExceptionsConsumer() {
		return this::takeLogAndSendMail;
	}

	void takeLogAndSendMail(LogDto logDto) {
		String addressee = messageToProgrammerAddressee;
		String messageSubject = messageToProgrammerSubject;
		String email = emailClient.getEmailByArtifact(logDto.artifact);

		if (email == null||email.isEmpty()) {
			email = emailClient.getAssignerMail();
			addressee = messageToAssignerAddressee;
			messageSubject = messageToAssignerSubject;

		}
		if (email == null||email.isEmpty()) {
			log.error(noFoundEmailLog);
			return;
		}
		sendMail(logDto, email, messageSubject, addressee);

	}

	private void sendMail(LogDto logDto, String email, String messageSubject, String addressee) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setSubject(messageSubject);
		message.setTo(email);
		message.setText(getText(logDto, addressee));
		mailSender.send(message);

	}

	private String getText(LogDto logDto, String addressee) {
		return String.format(
				"Hello, %s%nException has been received%nDate: %s%nException type: %s%nArtifact: %s%nExplanation: %s",
				addressee, logDto.dateTime, logDto.logType, logDto.artifact, logDto.result);
	}
}
