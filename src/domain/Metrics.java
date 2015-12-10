package domain;

public enum Metrics {
	
	AVG_RESPONSE_TIME("Average Response Time (ms)", 0),
	PERCETILE_RESPONSE_TIME("95th Percentile Response Time (ms)", 1),
	PERCETILE2_RESPONSE_TIME("94th Percentile Response Time (ms)", 1),
	CALLS_PER_MINUTE("Calls per Minute", 2),
	ERRORS_PER_MINUTE("Errors per Minute", 3),
	SLOW_CALLS("Number of Slow Calls", 4),
	VERY_SLOW_CALLS("Number of Very Slow Calls", 5),
	STALL_CALLS("Stall Count", 6),
	NORMAL_AVERAGE_RESPONSE_TIME("Normal Average Response Time (ms)", 7),
	NUMBER_SLOW_CALLS("Number of Slow Calls", 8),
	NUMBER_VERY_SLOW_CALLS("Number of Very Slow Calls", 9),
	STALL_COUNT("Stall Count", 10);
	
	Metrics(String description, int position){
		this.description = description;
		this.position = position;
	}

	public String toString(){
		return this.description;
	}
	
	public String getDescription() {
		return description;
	}
	
	public int getPosition() {
		return position;
	}
	
	private String description;
	private int position;
	
}
