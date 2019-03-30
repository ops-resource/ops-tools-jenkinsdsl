package jenkins.generators.utils

/**
 * Defines general methods for adding capabilities to a job definition.
 * @param context delegate passed in context
 */
class CommonExtensions {

    /**
     * Adds defaults
     */

    static void addStandardSettings(context) {
        context.with {
            wrappers {
                preBuildCleanup()
                timeout {
                    elastic(300, 3, 30)
                }
                timestamps()
            }
            logRotator {
                numToKeep(50)
            }
            publishers {
                allowBrokenBuildClaiming()
                wsCleanup {
                    deleteDirectories(true)
                    setFailBuild(false)
                }
            }
            configure { Node project ->
                project / 'properties' / 'com.sonyericsson.jenkins.plugins.bfa.model.ScannerJobProperty'(plugin: "build-failure-analyzer") {
                    doNotScan 'false'
                }
            }
        }
    }

    /** Utility function to add extended email
     *
     * @param List emails List of email string to make it seamlessly compatible with builders
     * @param triggersList List<String> triggers E.g failure, fixed etc...
     * @param sendToDevelopers Default false,
     * @param sendToRequester Default true,
     * @param includeCulprits Default false,
     * @param sendToRecipientList Default true
     * @param preSendScript Default $DEFAULT_PRESEND_SCRIPT
     *
     * @see <a href="https://github.com/cfpb/jenkins-automation/blob/gh-pages/docs/examples.md#common-utils" target="_blank">Common utils</a>
     */

    static void addExtendedEmail(context, List<String> emails, List<String> triggerList = ["failure", "unstable", "fixed"], sendToDevelopers = false, sendToRequester = true, includeCulprits = false, sendToRecipientList = true, preSendScript = "\$DEFAULT_PRESEND_SCRIPT", attachmentPattern = "") {
        addExtendedEmail(context, emails.join(","), triggerList, sendToDevelopers, sendToRequester, includeCulprits, sendToRecipientList, preSendScript, attachmentPattern)
    }

    /**
     * Utility function to add extended email
     * @param String emails Comma separated string of emails
     * @param triggerList List<String> triggers E.g failure, fixed etc...
     * @param sendToDevelopers Default false,
     * @param sendToRequester Default true,
     * @param includeCulprits Default false,
     * @param sendToRecipientList Default true
     * @param preSendScript Default $DEFAULT_PRESEND_SCRIPT
     * @param attachmentPattern Ant style pattern matching for attachments
     *
     * @see <a href="https://github.com/cfpb/jenkins-automation/blob/gh-pages/docs/examples.md#common-utils" target="_blank">Common utils</a>
     */

    static void addExtendedEmail(context, String emails, List<String> triggerList = ["failure", "unstable", "fixed"], sendToDevelopers = false, sendToRequester = true, includeCulprits = false, sendToRecipientList = true, preSendScript = "\$DEFAULT_PRESEND_SCRIPT", attachmentPattern = "") {
        context.with {
            extendedEmail {
                delegate.recipientList(emails)
                delegate.preSendScript(preSendScript)
                delegate.attachmentPatterns(attachmentPattern)

                triggers {
                    triggerList.each {
                        "${it}" {
                            sendTo {
                                if (sendToDevelopers) developers()
                                if (sendToRequester) requester()
                                if (includeCulprits) culprits()
                                if (sendToRecipientList) recipientList()
                            }
                        }
                    }
                }
            }

        }
    }

    /**
     *
     * @param context Closure context, i.e delegate
     * @param params Maps of params
     * emails: <String>, please note it does not support ArrayList
     * triggers: <Array> ["failure", "unstable", "fixed"],
     * sendToDevs:<Boolean>,
     * sendToRequester:<Boolean>,
     * includeCulprits:<Boolean>,
     * endToRecipient:<Boolean>,
     * preSendScript = <String>,
     * attachmentPattern = <String>
     *
     * @see <a href="https://github.com/cfpb/jenkins-automation/blob/gh-pages/docs/examples.md#common-utils" target="_blank">Common utils</a>

     */
    static void addExtendedEmail(Map params, context) {

        params.triggerList = params.triggerList ?: ["failure", "unstable", "fixed"]
        params.sendToDevelopers = params.sendToDevelopers ?: false
        params.sendToRequester = params.sendToRequester ?: true
        params.includeCulprits = params.includeCulprits ?: false
        params.sendToRecipientList = params.sendToRecipientList ?: true
        params.preSendScript = params.preSendScript ?: "\$DEFAULT_PRESEND_SCRIPT"
        params.attachmentPattern = params.attachmentPattern ?: ""
        def emails = params.emails

        context.with {
            extendedEmail {
                recipientList(emails)
                preSendScript(params.preSendScript)
                attachmentPatterns(params.attachmentPattern)

                triggers {
                    params.triggerList.each {
                        "${it}" {
                            sendTo {
                                if (params.sendToDevelopers) developers()
                                if (params.sendToRequester) requester()
                                if (params.includeCulprits) culprits()
                                if (params.sendToRecipientList) recipientList()
                            }
                        }
                    }
                }
            }

        }
    }

    /**
     * Utility function to add log parser publisher
     *
     * @see <a href="https://github.com/cfpb/jenkins-automation/blob/gh-pages/docs/examples.md#common-utils" target="_blank">Common utils</a>
     */

    static void addLogParserPublisher(context, rulesPath = "/var/lib/jenkins/shell_parse_rules.txt") {
        context.with {
            configure {
                it / publishers << 'hudson.plugins.logparser.LogParserPublisher' {
                    unstableOnWarning true
                    failBuildOnError true
                    parsingRulesPath rulesPath
                }
            }
        }
    }
}
