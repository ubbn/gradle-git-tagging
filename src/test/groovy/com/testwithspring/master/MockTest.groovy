package com.testwithspring.master

import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

/**
 * @author eulzbay
 */
class MockTest extends Specification {

	@Rule TemporaryFolder testProjectDir = new TemporaryFolder()
	File buildFile
	String version
	String tagPrefix="test-version"
	String testLocalRepo

	def setup() {
		buildFile = testProjectDir.newFile('build.gradle')
		buildFile << """
            plugins {
                id 'com.es.em.am.tag'
            }
            tag {
                tagPrefix = '${tagPrefix}'
            }            
        """

		def noCheckHostKey = 'GIT_SSH_COMMAND="ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no"'
		def testRemoteRepo = testProjectDir.newFolder("remote")
		testLocalRepo = testProjectDir.newFolder("repo")
		runBashCommand("git init ${testLocalRepo}")
		runBashCommand("git --bare init ${testRemoteRepo}")
		runBashCommand("git --git-dir ${testLocalRepo}/.git remote add origin ssh://localhost/${testLocalRepo}")
		runBashCommand("touch ${testLocalRepo}/temp")
		runBashCommand("git --git-dir ${testLocalRepo}/.git --work-tree=${testLocalRepo} add temp")
		runBashCommand("git --git-dir ${testLocalRepo}/.git commit -m 'Init commit'")
		runBashCommand("${noCheckHostKey} git --git-dir ${testLocalRepo}/.git push -u origin master")
	}

	def "can successfully tag"() {
		version='1.2.8'
		buildFile << """
            version = '${version}'
        """

		when:
		def result = GradleRunner.create()
				.withProjectDir(testProjectDir.root)
				.withArguments("tag", "-PgitdiR=${testLocalRepo}/.git", "-PnoHostKeyCheck")
				.withPluginClasspath()
				.build()
		then:
		result.output.contains("Project repo is successfully tagged as")
		result.task(":tag").outcome == SUCCESS
	}

	def "can't tag snapshot version"() {
		version='1.2.1-SNAPSHOT'
		buildFile << """
            version = '${version}'
        """

		when:
		def result = GradleRunner.create()
				.withProjectDir(testProjectDir.root)
				.withArguments("tag", "-PgitDIR=${testLocalRepo}/.git", "-PnoHostKeyCheck")
				.withPluginClasspath()
				.build()
		then:
		result.output.contains("Skipped git tagging as project version is snapshot")
		result.task(":tag").outcome == SUCCESS
	}

	def "can tag snapshot version with explicit option"() {
		version='1.2.1-SNAPSHOT'
		buildFile << """
            version = '${version}'
        """

		when:
		def result = GradleRunner.create()
				.withProjectDir(testProjectDir.root)
				.withArguments("tag", "-Pallowsnapshot", "-Pgitdir=${testLocalRepo}/.git", "-PnoHostKeyCheck")
				.withPluginClasspath()
				.build()
		then:
		result.output.contains("Project repo is successfully tagged")
		result.task(":tag").outcome == SUCCESS
	}

//	def "Shalga"() {
//		expect:
//			1 == 1
//	}

	def runBashCommand(String command) {
		def proc = ['bash', '-c', command].execute()
		proc.waitForOrKill(5000)
	}

	def cleanup() {
		def proc = ['bash', '-c', "git tag --delete ${tagPrefix}${version}"].execute()
		proc = ['bash', '-c', "git push --delete origin ${tagPrefix}${version}"].execute()
		proc.waitForOrKill(5000)
		def output = proc.in.text
		println output
	}
}
