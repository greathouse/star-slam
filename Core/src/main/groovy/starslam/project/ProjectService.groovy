package starslam.project

import starslam.IDbConnection
import starslam.OpenDatabase

import com.google.inject.Inject

class ProjectService implements IProjectStore {
	IDbConnection dbConnector
	final def projectRowMapper = { it ->
		return new Project([
			id:it.id
			, name:it.name
			, created:new Date(it.created)
			, rootPath:it.root_path
		])
	}

	@Inject
	public ProjectService(IDbConnection conn) {
		dbConnector = conn
	}

	@Override
	public String persist(Project project) {
		use(OpenDatabase) {
			dbConnector.getConnection() { sql ->
				def existing = sql.firstRow("select '1' from Project where name = ${project.name} and id <> ${project.id}")
				if (existing) {
					throw new DuplicateProjectNameException("There already exists a project named \"${project.name}\"")
				}

				def id = project.id?:UUID.randomUUID().toString()
				sql.execute("""
					merge into Project (
						id
						, name
						, created
						, root_path
					)
					key (id)
					values (
						${id}
						, ${project.name}
						, ${project.created.time}
						, ${project.rootPath}
					)
				""")
				return id
			}
		}
	}

	@Override
	public Project retrieve(String projectId) {
		use(OpenDatabase) { 
			dbConnector.getConnection() { sql ->
				def row = sql.firstRow("select id, name, created, root_path from project where id = ${projectId}")
				return (row == null) ? null : projectRowMapper(row)
			}
		}
	}
}
