package starslam.project

import com.google.inject.AbstractModule

class ProjectModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(IProjectStore).to(ProjectService)
	}

}
