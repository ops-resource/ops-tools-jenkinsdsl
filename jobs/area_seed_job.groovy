import groovy.io.FileType
import java.lang.reflect.Array
import jenkins.generators.builders.Base
import jenkins.generators.utils.Project
import org.yaml.snakeyaml.constructor.Constructor
import org.yaml.snakeyaml.Yaml

def areasToAutomate  = [:]

hudson.FilePath workspace = hudson.model.Executor.currentExecutor().getCurrentWorkspace()
def resultList = workspace.list('projects/**/*.yaml')

out.println('resultList.length: ' + resultList.length)

Yaml yaml = new Yaml(new Constructor(Project.class))
resultList.each { fileName ->
    Project project = yaml.load(readFileFromWorkspace(fileName.getRemote()))

    if (!areasToAutomate.containsKey(project.getArea())){
        def projectList = []
        areasToAutomate.put(project.getArea(), projectList)
    }

    def projectsInArea = areasToAutomate.get(project.getArea())
    projectsInArea << project
}

areasToAutomate.keySet().each { area ->

    out.println('Generating view for area: ' + area.name)
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
            regex('/(?i)(${areaId}/.*)/')
        }
    }

    def projectsInArea = areasToAutomate.get(area)
    projectsInArea.each { project ->
        projectGeneratorName = areaId + '/' + project.name.toLowerCase() + '_generator'
        out.println('Generating project: ' + projectGeneratorName)

        def projectGenerator = new Base(
            name: projectGeneratorName,
            displayName: project.name + ' Generator',
            description: 'Generates the build configurations for the ' + project.name + ' repository',
        ).build(this).with {

            label('powershell')

            multiscm {
                ScmExtensions.project_repos(delegate, reposToInclude, false)
                git {
                    remote {
                        url('https://github.com/ops-resource/ops-tools-jenkinsdsl')
                    }

                    branch 'refs/heads/feature/initial'
                    extensions {
                        ignoreNotifyCommit()
                        localBranch()
                        relativeTargetDirectory('dsl')
                    }
                }

                git {
                    remote {
                        url(project.url)
                    }

                    branch '**/feature, **/hotfix, **/release, **/master'
                    extensions {
                        localBranch()
                        relativeTargetDirectory('projects')
                    }
                }
            }

            steps {

                powerShell('$ErrorActionPreference = "Stop";$path = Join-Path $env:Workspace "dsl/jobs/Get-Dependencies.ps1";& $path')

                dsl {
                    external "dsl/jobs/project_seed_job.groovy"
                    lookupStrategy('JENKINS_ROOT')
                    removeAction('DELETE')
                    removeViewAction('DELETE')

                    additionalClasspath "lib/*.jar"

                }
            }

            triggers {
                scm 'H/5 * * * *'
            }
        }

        if (false) {
            queue(projectGeneratorName)
        }
    }
}