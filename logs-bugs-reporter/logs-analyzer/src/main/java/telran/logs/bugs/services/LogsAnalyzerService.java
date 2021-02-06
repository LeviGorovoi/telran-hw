package telran.logs.bugs.services;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import javax.validation.Validator;

import javax.validation.ConstraintViolation;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;

@Service
public class LogsAnalyzerService {
	static Logger LOG = LoggerFactory.getLogger(LogsAnalyzerService.class);
	@Value("${binding.names:exceptions-out-3,exceptions-out-1,exceptions-out-0, exceptions-out-0,exceptions-out-0,exceptions-out-0,exceptions-out-0}")
	String[] bindingNames;

	@Autowired
	StreamBridge streamBridge;
	@Autowired
	Validator validator;
	
//	Map<LogType, String> topicsMap = new HashMap<>();
//	public LogsAnalyzerService() {
//		for (int i = 0; i<LogType.values().length; i++) {
//			System.out.println(LogType.values()[i]);
//			System.out.println(bindingNames[i]);
//			topicsMap.put(LogType.values()[i], bindingNames[i]);
//		}
//		}

	@Bean
	Consumer<LogDto> getAnalyzerBean() {
		return this::analyzerMethod;
	}

	void analyzerMethod(LogDto logDto) {
		logDto = validateDto(logDto);		
			String topic = getBindingName(logDto);
			streamBridge.send(topic, logDto);
			LOG.debug("log: {} sent to binding name: {}", logDto, topic);
		
	}

	private LogDto validateDto(LogDto logDto) {
		LOG.debug("received log: {}", logDto);
		Set<ConstraintViolation<LogDto>> violations = validator.validate(logDto);
		if (!violations.isEmpty()) {
			for (ConstraintViolation<?> violation : violations) {
				LOG.error("logDto : {}; field: {}; message: {}", logDto,
						violation.getPropertyPath(), violation.getMessage());
			}
			logDto = new LogDto(new Date(), LogType.BAD_REQUEST_EXCEPTION, LogsAnalyzerService.class.getName(), 0,
					violations.toString());			
		}
		return logDto;
	}

	private String getBindingName(LogDto logDto) {
		Map<LogType, String> topicsMap = new HashMap<>();
		for (int i = 0; i<LogType.values().length; i++) {
			topicsMap.put(LogType.values()[i], bindingNames[i]);
		}
		return topicsMap.get(logDto.logType);
	}
}
