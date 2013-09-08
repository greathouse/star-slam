package starslam


class Main {
	static void main(String[] args) {
		new Bootstrapper()
			.porpoise(Configuration.DBURL)
			.jetty()
	}
}
