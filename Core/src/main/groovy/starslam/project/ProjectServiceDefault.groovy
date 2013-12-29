package starslam.project

import starslam.DbConnection
import starslam.OpenDatabase

final class ProjectServiceDefault implements ProjectStore {
	final DbConnection dbConnector
	
	final def projectRowMapper = { it ->
		return new Project([
			id:it.id
			, name:it.name
			, rootPath:it.root_path
			, fileGlob:it.file_glob
		])
	}

	public ProjectServiceDefault(DbConnection conn) {
		dbConnector = conn
	}

	@Override
	public String persist(Project project) {
		use(OpenDatabase) {
			dbConnector.getConnection { sql ->
                checkForExistingProject(sql, project)

				def id = project.id?:UUID.randomUUID().toString()
				sql.execute("""
					merge into Project (
						id
						, name
						, root_path
						, file_glob
					)
					key (id)
					values (
						${id}
						, ${project.name}
						, ${project.rootPath}
						, ${project.fileGlob}
					)
				""")
				return id
			}
		}
	}

    private void checkForExistingProject(sql, project) {
        def existing = sql.firstRow("select '1' from Project where name = ${project.name} and id <> ${project.id}")
        if (existing) {
            throw new DuplicateProjectNameException("There already exists a project named \"${project.name}\"")
        }
    }

    @Override
	public Project retrieve(String projectId) {
		use(OpenDatabase) { 
			dbConnector.getConnection { sql ->
				def row = sql.firstRow("select id, name, root_path, file_glob from project where id = ${projectId}")
				return (row == null) ? null : projectRowMapper(row)
			}
		}
	}

	@Override
	public Iterable<Project> list() {
		use(OpenDatabase) {
			dbConnector.getConnection { sql ->
				def rtn = []
				sql.eachRow("select id, name, root_path, file_glob from project") {
					rtn.add(projectRowMapper(it))
				}
				return rtn;
			}
		}
	}
}
