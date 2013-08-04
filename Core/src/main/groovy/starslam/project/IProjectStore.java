package starslam.project;


public interface IProjectStore {
	String persist(Project project);
	Project retrieve(String projectId);
	Iterable<Project> list();
}
