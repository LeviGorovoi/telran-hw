package telran.logs.bugs.client;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;
import telran.logs.bugs.discovery.LoadBalancer;

@Component
@Slf4j
public class EmailProviderClient {
	RestTemplate restTemplate = new RestTemplate();
	@Value("${assigner-email-provider-name:assigner-email-provider}")
	String assignerServiceName;
	@Value("${programmer-email-provider_name:programmer-email-provider}")
	String programmerServiceName;
	@Autowired
	LoadBalancer loadBalancer;
	public String getEmailByArtifact(String artifact) {
		String urlMailProvider = getUrlMailArtifact(artifact);
		String res = getMail(urlMailProvider);
		log.debug("Programmer mail is {}", res);
		return res;
	}
	private String getUrlMailArtifact(String artifact) {
		String res = loadBalancer.getBaseUrl(programmerServiceName) + "/email/" + artifact;
		log.debug("url for getting email by artifact is {}", res);
		return res ;
	}
	public String getAssignerMail() {
		String res;
		String urlMailProvider = getUrlAssigner();
		res = getMail(urlMailProvider);
		log.debug("assigner email is {}", res);
		return res;
	}
	private String getMail(String urlMailProvider) {
		String res;
		try {
			ResponseEntity<String> response =
					restTemplate.exchange(urlMailProvider, HttpMethod.GET, null, String.class);
			res = response.getBody();
		} catch (RestClientException e) {
			log.error("request to url {} thrown exception {}", urlMailProvider, e.getMessage());
			res = "";
		}
		return res;
	}
	private String getUrlAssigner() {		
		String res = loadBalancer.getBaseUrl(assignerServiceName) + "/mail/assigner";
		log.debug("URL for getting assigner mail is {}", res);
		return res ;
	}
}
