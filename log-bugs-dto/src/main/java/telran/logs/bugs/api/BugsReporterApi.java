package telran.logs.bugs.api;



	public interface BugsReporterApi {
		 String BUGS_PROGRAMMERS =  "/bugs/programmers";
		String BUGS_OPEN = "/bugs/open"; 
		String BUGS_OPEN_ASSIGN = "/bugs/open/assign" ;
		String BUGS_ASSIGN = "/bugs/assign" ;
		String PROGRAMMER_ID = "programmer_id";
		String BUGS_PROGRAMMERS_COUNT = "/bugs/programmers/count";
		String BUGS_ARTIFACT = "/bugs/artifact";
		String CLOSE_BUG = "/bugs/close/bug";
		String PROGRAMMER_MOST_BUGS = "/programmers/most/bugs";
		String N_PROGRAMMERS = "n_programmers";
		String PROGRAMMER_LEAST_BUGS = "/programmers/least/bugs";
		String BUGS_SERIOUSNESS_COUNT = "/bugs/seriousness/count";
		String BUGS_SERIOUSNESS_TYPES_MOST_BUGS = "/bugs/seriousness/types/most/bugs";
		String N_TYPES = "n_types";
	}

