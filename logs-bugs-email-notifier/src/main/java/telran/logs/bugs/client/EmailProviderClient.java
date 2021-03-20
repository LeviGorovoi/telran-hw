package telran.logs.bugs.client;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class EmailProviderClient {
	RestTemplate restTemplate = new RestTemplate();
	@Value("${app-url-assigner-mail:xxxx}")
	String urlAssignerMail;
	@Value("${app-url-programmer-mail}")
	String urlProgrammerMail;
	public String getEmailByArtifact(String artifact) {
		String res;
		try {
			ResponseEntity<String> response =
					restTemplate.exchange(getUrlProgrammer(artifact), HttpMethod.GET, null, String.class);
			res = response.getBody();
		} catch (RestClientException e) {
			res = "";
		}
		log.debug("programmer email is {}", res);
		return res;
	}
	private String getUrlProgrammer(String artifact) {
		String res = urlProgrammerMail + "/mail/"+artifact;
		log.debug("URL for getting programmer mail is {}", res);
		return res ;
	}
	public String getAssignerMail () {
		String res;
		try {
			ResponseEntity<String> response =
					restTemplate.exchange(getUrlAssigner(), HttpMethod.GET, null, String.class);
			res = response.getBody();
		} catch (RestClientException e) {
			res = "";
		}
		log.debug("assigner email is {}", res);
		return res;
	}
	
	private String getUrlAssigner() {
		
		String res = urlAssignerMail + "/mail/assigner";
		log.debug("URL for getting assigner mail is {}", res);
		return res ;
	}
}
