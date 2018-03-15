[![Build Status](https://travis-ci.org/cronn-de/diff-to-html.svg?branch=master)](https://travis-ci.org/cronn-de/diff-to-html)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.cronn/diff-to-html/badge.svg)](http://maven-badges.herokuapp.com/maven-central/de.cronn/diff-to-html)
[![Apache 2.0](https://img.shields.io/github/license/cronn-de/diff-to-html.svg)](http://www.apache.org/licenses/LICENSE-2.0)


# diff-to-html #

Displays diffs in a convenient html page. 

* Useful when dealing with large validation text files or directory structures in automated builds, e.g. on Jenkins. 
* Collapsible diff sections for each file make the result easy to read when comparing directories
* Pure Java solution (optionally, use Linux diff under the hood - might be faster depending on your machine)

##### Example outputs:
---
[![cronn-diff-to-html_outputExample3](cronn-diff-to-html_outputExample1.png)](cronn-diff-to-html_outputExample1_hiRes.png)
[![cronn-diff-to-html_outputExample2](cronn-diff-to-html_outputExample2.png)](cronn-diff-to-html_outputExample2_hiRes.png)
---

### Usage 

Install: 
```
$ ./gradlew installDist
```
To compare two directories (or files), e.g. dir1 and dir2, use the fat jar

```
$ java -jar build/libs/diff-to-html-1.1.jar dir1 dir2
```
or the start script
```
$ ./build/install/diff-to-html/scripts/diff-to-html dir1 dir2
```
which will give you something like
```
Output written to: file:///home/maurice/Git-Projects/openSource/diff-to-html/diff_dir1_dir2.html

Directories differ!
```
Call without arguments for help
```
$ java -jar build/libs/cronn-diff-to-html-1.1.jar 
usage: cronn-diff-to-html <input_left> <input_right> [<output_html>]  [-w]
       [-b] [-crlf] [-or] [-od] [-iu] [-de] [-u <arg>]
 -w,--ignorewhitespaces      ignore all white spaces
 -b,--ignorespacechange      ignore changes in the amount of white space
 -crlf,--ignorelineendings   ignore line endings, i.e. normalize CRLF / LF
                             while comparing files
 -or,--onlyreports           always exits with zero
 -od,--osdiff                uses operating system's diff instead of Java
                             implementation and parses the output. Might
                             slightly improve performance, depending on
                             your machine. Try for large diffs in
                             time-critial situations. Windows not
                             supported currently
 -iu,--ignoreunique          ignore unique files
 -de,--detectencoding        tries to determine encoding type
 -u,--unified <arg>          output <arg> (default 3) lines of unified
                             context
Parsing failed. Reason: Input arguments are missing
```
Generate your tar (or zip) to take wherever you want
```
$ ./gradlew distTar distZip
$ ls build/distributions/
diff-to-html-1.1.tar  diff-to-html-1.1.zip
``` 

### Prerequisites 
- Java 8
- On Unix system (only if you want to use OS diff under the hood)

### Related 
- [java-diff-utils](https://github.com/dnaumenko/java-diff-utils)
- [j2html](https://j2html.com/)
- [System Rules](http://stefanbirkner.github.io/system-rules/)