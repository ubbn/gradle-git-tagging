package com.testwithspring.master

/**
 * @author eulzbay
 */

import org.junit.experimental.categories.Category
import spock.lang.Specification

@Category(UnitTest.class)
class GitTagTest extends Specification {
	def setup() {
		println "Setup asidf asidf "
	}

	def "should return 2"() {
		println "What"
		expect:
		1+1 == 2
	}

	def "should return 2 from first element of list"() {
		println "a0sdf 0asd fa0sd+++"
		given:
		List<Integer> list = new ArrayList<>()
		when:
		list.add(1)
		then:
		1 == list.get(0)
	}
}