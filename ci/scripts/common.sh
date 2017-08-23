# ========================================================================
# Common CI utils for use in concourse scripts.
#
# Importing scripts should use as follows:
#
#     #!/bin/bash
#     set -e
#     source $(dirname $0)/common.sh
#
# ========================================================================


# Fail the script with a message
fail() {
	echo "$1"
	exit 1
}

# Setup Maven and Gradle symlinks for caching
setup_symlinks() {
	[[ -d $PWD/maven && ! -d $HOME/.m2 ]] && ln -s $PWD/maven $HOME/.m2
	[[ -d $PWD/gradle && ! -d $HOME/.gradle ]] && ln -s $PWD/gradle $HOME/.gradle
}

# Cleanup a local maven repo. For example `cleanup_maven_repo "org.springframework.boot"`
cleanup_maven_repo() {
	[[ -n $1 ]] || fail "missing cleanup_maven_repo() argument"
	local repoPath=$(echo "$1" | tr . /)
	repoPath="${HOME}/.m2/repository/${repoPath}"
	if [[ -d "$repoPath" ]]; then
		echo "Cleaning local maven repo \"$1\""
		# rm -fr "$repoPath" 2> /dev/null || :
	fi
}

# Run maven, tailing relevant output and printing the last part of the log on error
run_maven() {
	echo "./mvnw $@" > build.log
	((tail -n 0 -q -f build.log & echo $! >&3) 3>pid | grep -v --color=never "\] \[INFO\]\|Building\ jar" | grep --color=never "\[INFO.*---\|Building\ \|SUCCESS\|Total\ time\|Finished\ at\|Final\ Memory" &)
	echo "./mvnw $@"
	./mvnw "$@" >> build.log 2>&1 || (kill $(<pid) && sleep 1 && tail -n 3000 build.log && exit 1)
	kill $(<pid)
	sleep 1
}

# Get the revision from a POM file
get_revision_from_pom() {
	xmllint --xpath '/*[local-name()="project"]/*[local-name()="properties"]/*[local-name()="revision"]/text()' pom.xml
}

set_revision_to_pom() {
}
