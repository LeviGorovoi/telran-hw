package telran.logs.bugs;

import javax.persistence.*;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class represents simple console of JPQL Select queries.
 * 
 * Integration with your application:
 * 1. To instantiate bean, do one of following solutions:
 * 	  Solution a) Move this class to be under root package of your Spring Boot application
 *    Solution b) Add this package to your Spring Boot application component scan path as:
 *         @ComponentScan({"telran.utils"})      
 *    Solution c) Add bean creating method to your application configuration as:
 *    	   @Bean
 *         JPQLQueryConsole createConsole() {return new JPQLQueryConsole();}
 *         
 * 2. To run the console in Spring Boot application add the following code in your main():
 *     ...
 *     ConfigurableApplicationContext ctx = SpringApplication.run(...);
 *     JPQLQueryConsole console = ctx.getBean(JPQLQueryConsole.class);
 *     console.run();
 *     
 * 3. To have the readable output ensure that your entities have toString() implementation
 * 
 */
@Service
public class JPQLQueryConsole {
	@PersistenceContext
	EntityManager em;

	public void run() {
		Scanner scanner=new Scanner(System.in);
		while(true){
			try {
				System.out.println("Enter JPQL query or exit");
				String line=scanner.nextLine();
				if(line.isEmpty())
					continue;
				if(line.equalsIgnoreCase("exit"))
					break;
				runAnyQuery(line).forEach(System.out::println);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			
		}
		scanner.close();	
	}
	
	@SuppressWarnings("unchecked")
	public Iterable<String> runAnyQuery(String jpql) {
		Query query = em.createQuery(jpql);
		List<?> listRes = query.getResultList();
		if (listRes.isEmpty())
			return new ArrayList<>();
		return listRes.get(0).getClass().isArray() 
				? getResultProjection((List<Object[]>) listRes)
				: getResult((List<Object>) listRes);
	}

	private Iterable<String> getResult(List<Object> listRes) {
		return listRes.stream().map(Object::toString).collect(Collectors.toList());
	}

	private Iterable<String> getResultProjection(List<Object[]> listRes) {

		return listRes.stream().map(Arrays::deepToString).collect(Collectors.toList());
	}
}
