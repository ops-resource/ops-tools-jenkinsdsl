package jenkins.generators.builders

import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.Job
import javaposse.jobdsl.dsl.*
import jenkins.generators.utils.CommonExtensions

/**
 * Default job definition
 *
 * @param name used to name the job
 * @param description job description
 * @param emails list of developer to get notifications
 */
class Base {
    String name
    String displayName
    String description

    Job build(DslFactory factory) {
        factory.job(name) {
            it.description this.description
            it.displayName this.displayName
            CommonExtensions.addStandardSettings(delegate)
            publishers {
                publishers {
                    CommonExtensions.addExtendedEmail(delegate)
                }
            }
        }
    }
}



