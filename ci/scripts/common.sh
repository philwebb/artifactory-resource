# ========================================================================
# Common CI utilities for use in concourse scripts.
#
# Importing scripts should use as follows:
#
#     #!/bin/bash
#     set -e
#     source $(dirname $0)/common.sh
#
# ========================================================================


# Setup Maven and Gradle symlinks for caching
setup_symlinks() {
	[[ -d $PWD/maven && ! -d $HOME/.m2 ]] && ln -s $PWD/maven $HOME/.m2
	[[ -d $PWD/gradle && ! -d $HOME/.gradle ]] && ln -s $PWD/gradle $HOME/.gradle
}



# Cleanup a local maven repo. For example `cleanup_maven_repo "org.springframework.boot"`
cleanup_maven_repo() {
	[[ -n $1 ]] || { echo "missing cleanup_maven_repo() argument" >&2; return 1; }
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



# Update the revision tag in a POM file
set_revision_to_pom() {
	[[ -n $1 ]] || { echo "missing set_revision_to_pom() argument" >&2; return 1; }
	sed -ie "s|<revision>.*</revision>|<revision>${1}</revision>|" pom.xml
}



# Bump version number by incrementing the last numeric, RC or M token
bump_version_number() {
	local version=$1
	[[ -n $version ]] || { echo "missing bump_version_number() argument" >&2; return 1; }
	if [[ $version =~ ^(.*(\.|-)(M|RC))([0-9]+)$ ]]; then
		local prefix=${BASH_REMATCH[1]}
		local suffix=${BASH_REMATCH[4]}
		(( suffix++ ))
		echo "${prefix}${suffix}"
		return 0;
	fi
	local suffix
	if [[ $version =~ ^(.*)(\-SNAPSHOT)$ ]]; then
		version=${BASH_REMATCH[1]}
		suffix="-SNAPSHOT"
	fi
	tokens=(${version//\./ })
	local bumpIndex
	for i in "${!tokens[@]}"; do
		if [[ "${tokens[$i]}" =~ ^[0-9]+$ ]] ; then
			bumpIndex=$i
		fi
	done
	[[ -n $bumpIndex ]] || { echo "unsupported version number" >&2; return 1; }
 	(( tokens[bumpIndex]++ ))
	IFS=. eval 'bumpedVersion="${tokens[*]}"'
	echo "${bumpedVersion}${suffix}"
}



# Remove any "-SNAPSHOT" or ".BUILD-SNAPSHOT" suffix
strip_snapshot_suffix() {
	[[ -n $1 ]] || { echo "missing get_relase_version() argument" >&2; return 1; }
	if [[ $1 =~ ^(.*)\.BUILD-SNAPSHOT$ ]]; then
		echo "${BASH_REMATCH[1]}"
	elif [[ $1 =~ ^(.*)-SNAPSHOT$ ]]; then
		echo "${BASH_REMATCH[1]}"
	else
		echo "$1"
	fi
}



# Gets a version number based on the latest matching git tag.
# The first argument should be the patch version prefix (usually "M" or "RC), the second argument
# should be the full version number to match. If no matching tags are found, "0" is used.
# For example, given the tags "v1.0.0.M1", "v1.0.0.M2" the following results would be returned:
#   get_tagged_version "M" "1.0.0.BUILD-SNAPSHOT" # 1.0.0.M3
#   get_tagged_version "M" "1.0.0-SNAPSHOT" # 1.0.0-M3
#   get_tagged_version "RC" "1.0.0.BUILD-SNAPSHOT" # 1.0.0.RC0
get_tagged_version() {
echo "foo"
}




