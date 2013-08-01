package starslam


class Main {

	static final String DB_URL = "jdbc:h2:~/star-slam/prod"
	static void main(String[] args) {
		new Bootstrapper()
			.porpoise(DB_URL)
			.ratpack(DB_URL)
	}
	
}
