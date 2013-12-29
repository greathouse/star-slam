package starslam.project;


public interface ProjectStore {
	String persist(Project project);
	Project retrieve(String projectId);
	Iterable<Project> list();
}
