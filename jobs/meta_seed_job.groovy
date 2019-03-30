import com.ecwid.consul.v1.*
import java.lang.reflect.Array
import jenkins.generators.builders.Base

client = new ConsulClient("127.0.0.1:8500")
def keyValueResponse = client.getKVValue('config/projects/projectrepository')
def projectsRepositoryUrl = keyValueResponse.getValue().getDecodedValue()

def areaGenerator = new Base(
    name: 'meta/area_generator',
    displayName: 'Area Generator',
    description: 'Generates the different areas or categories of jobs',
).build(this).with {

    multiscm {
        git {
            remote {
                url('https://github.com/ops-resource/ops-tools-jenkinsdsl')
            }

            branch 'refs/heads/master'
            extensions {
                ignoreNotifyCommit()
                localBranch()
                relativeTargetDirectory('dsl')
            }
        }

        git {
            remote {
                url(projectsRepositoryUrl)
            }

            branch 'refs/heads/master'
            extensions {
                localBranch()
                relativeTargetDirectory('projects')
            }
        }
    }

    triggers {
        scm 'H/5 * * * *'
    }
    steps {

        powerShell('$ErrorActionPreference = "Stop";$path = Join-Path $env:Workspace "dsl/jobs/jenkins/Get-Dependencies.ps1";& $path')

        dsl {
            external "dsl/jobs/area_seed_job.groovy"
            lookupStrategy('JENKINS_ROOT')
            removeAction('DELETE')
            removeViewAction('DELETE')

            additionalClasspath "lib/*.jar"

        }
    }
}

queue(areaGenerator)
