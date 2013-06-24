package starslam

class Bootstrapper {
	void porpoise() {
		run(new File('porpoise/Porpoise.groovy'), ['-SF', '-d','sql', '-U',dbUrl, '--no-exit'] as String[])
	}
}
