@Grab('org.yaml:snakeyaml:1.18')

import groovy.io.FileType
import java.lang.reflect.Array
import jenkins.generators.utils.Project
import jenkins.generators.utils.ScmExtensions
import org.yaml.snakeyaml.Yaml

def areasToAutomate  = [:]

hudson.FilePath workspace = hudson.model.Executor.currentExecutor().getCurrentWorkspace()
def resultList = workspace.list().findAll { it.name  ==~ /\*\.yml/ }

Yaml yaml = new Yaml(new Constructor(Project.class))
resultlist.each { fileName ->
    Project project = yaml.load(readFileFromWorkspace(fileName))

    if (!areasToAutomate.containsKey(project.getArea())){
        def projectList = []
        areasToAutomate.put(project.getArea(), projectList)
    }

    def projectsInArea = areasToAutomate.get(project.getArea())
    projectsInArea << project
}

areasToAutomate.each { area ->

    def areaId = area.name.toLowerCase()
    folder(areaId) {
        displayName(area.name)
        description(area.description)
    }

    categorizedJobsView(areaId) {
        columns {
            buildButton()
            status()
            name()
            lastSuccess()
            lastFailure()
            lastDuration()
        }
        filterBuildQueue()
        filterExecutors()
        jobs {
            regex(/(?i)(${areaId}//.*)/)
        }
    }

    area.projects.each { project ->
        def reposToInclude = [
            [
                branch: 'refs/heads/master',
                name: "dsl",
                url: 'https://github.com/ops-resource/ops-tools-jenkinsdsl'
            ],
            [
                branch: '**',
                name: "projects",
                url: project.url
            ]
        ]

        def projectGenerator = new Base(
            name: project.name.toLowerCase() + '_generator',
            displayName: project.name + ' Generator',
            description: 'Generates the build configurations for the ' + project.name + ' repository',
        ).build(this).with {

            multiscm {
                ScmExtensions.project_repos(delegate, reposToInclude, false)
            }

            triggers {
                scm 'H/5 * * * *'
            }
            steps {

                dsl {
                    external "dsl/jobs/project_seed_job.groovy"
                    lookupStrategy('JENKINS_ROOT')
                    removeAction('DELETE')
                    removeViewAction('DELETE')

                    additionalClasspath "dsl/src/main/groovy"

                }
            }
        }

        if (false) {
            queue(projectGenerator)
        }
    }
}