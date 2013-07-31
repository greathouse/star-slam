package starslam.project

class ProjectServiceIProjectStoreTest extends IProjectStoreTestBase {

	@Override
	protected IProjectStore createInstance() {
		return new ProjectService()
	}

}
