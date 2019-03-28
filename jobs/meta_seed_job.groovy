@Grab( 'com.ecwid.consul:consul-api:1.4.2' )

import com.ecwid.consul.v1.*
import java.lang.reflect.Array
import jenkins.generators.builders.Base
import jenkins.generators.utils.ScmExtensions

client = new ConsulClient("127.0.0.1:8500")
def keyValueResponse = client.getKVValue('config/projects/projectrepository')
def projectsRepositoryUrl = keyValueResponse.getValue().getDecodedValue()

def reposToInclude = [
    [
        branch: 'refs/heads/master',
        name: "dsl",
        url: 'https://github.com/ops-resource/ops-tools-jenkinsdsl'
    ],
    [
        branch: 'refs/heads/master',
        name: "projects",
        url: projectsRepositoryUrl
    ]
]

def areaGenerator = new Base(
    name: 'area_generator',
    displayName: 'Area Generator',
    description: 'Generates the different areas or categories of jobs',
).build(this).with {

    multiscm {
        ScmExtensions.project_repos(delegate, reposToInclude, false)
    }

    triggers {
        scm 'H/5 * * * *'
    }
    steps {

        dsl {
            external "dsl/jobs/area_seed_job.groovy"
            lookupStrategy('JENKINS_ROOT')
            removeAction('DELETE')
            removeViewAction('DELETE')

            additionalClasspath "dsl/src/main/groovy"

        }
    }
}

queue(areaGenerator)
