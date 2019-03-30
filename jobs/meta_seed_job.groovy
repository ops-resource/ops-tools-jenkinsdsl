import com.ecwid.consul.v1.*
import java.lang.reflect.Array
import jenkins.generators.builders.Base

client = new ConsulClient("127.0.0.1:8500")
def keyValueResponse = client.getKVValue('config/projects/projectrepository')
def projectsRepositoryUrl = keyValueResponse.getValue().getDecodedValue()

def areaGeneratorName = 'meta/area_generator'
def areaGenerator = new Base(
    name: areaGeneratorName,
    displayName: 'Area Generator',
    description: 'Generates the different areas or categories of jobs',
).build(this).with {

    label('powershell')

    multiscm {
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
                url(projectsRepositoryUrl)
            }

            branch 'refs/heads/master'
            extensions {
                localBranch()
                relativeTargetDirectory('projects')
            }
        }
    }

    steps {

        powerShell('$ErrorActionPreference = "Stop";$path = Join-Path $env:Workspace "dsl/jobs/Get-Dependencies.ps1";& $path')

        dsl {
            external "dsl/jobs/area_seed_job.groovy"
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

queue(areaGeneratorName)
