package starslam.project

import starslam.IDbConnection
import starslam.OpenDatabase

import com.google.inject.Inject

final class ProjectService implements IProjectStore {
	final IDbConnection dbConnector
	
	final def projectRowMapper = { it ->
		return new Project([
			id:it.id
			, name:it.name
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
			dbConnector.getConnection { sql ->
				def existing = sql.firstRow("select '1' from Project where name = ${project.name} and id <> ${project.id}")
				if (existing) {
					throw new DuplicateProjectNameException("There already exists a project named \"${project.name}\"")
				}

				def id = project.id?:UUID.randomUUID().toString()
				sql.execute("""
					merge into Project (
						id
						, name
						, root_path
					)
					key (id)
					values (
						${id}
						, ${project.name}
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
			dbConnector.getConnection { sql ->
				def row = sql.firstRow("select id, name, root_path from project where id = ${projectId}")
				return (row == null) ? null : projectRowMapper(row)
			}
		}
	}

	@Override
	public Iterable<Project> list() {
		use(OpenDatabase) {
			dbConnector.getConnection { sql ->
				def rtn = []
				sql.eachRow("select id, name, root_path from project") {
					rtn.add(projectRowMapper(it))
				}
				return rtn;
			}
		}
	}
}
