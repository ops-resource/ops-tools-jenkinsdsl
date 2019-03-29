package jenkins.generators.utils

/**
 * Utility class to provide nicer, terser DSL for common tasks
 */
class ScmExtensions {

/***
 *
 * Utility method to create a multiscm block from a list of repos.
 * @see <a href="https://github.com/cfpb/jenkins-automation/blob/gh-pages/docs/examples.md#using-multiscm-utility" target="_blank">example</a>
 */
    static void project_repos(context, repos, use_versions = true) {
        Boolean disable_submodule = false
        context.with {
            repos.each { repo ->
                git {
                    remote {
                        url(repo.url)
                    }

                    branch repo.branch
                    extensions {
                        localBranch()
                        if (repo.name) {
                            relativeTargetDirectory(repo.name)
                        }
                    }
                }
            }
        }
    }
}
