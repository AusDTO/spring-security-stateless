# Contributing

We welcome pull requests from everyone. By participating in this project, you agree to abide by the [code of conduct](https://github.com/AusDTO/spring-security-stateless/blob/master/CODE_OF_CONDUCT.md).

## Steps

1. Fork the repository.

2. Clone your fork of the repository.

3. Ensure you have the prerequisites described in the [readme](https://github.com/AusDTO/spring-security-stateless/blob/master/README.md) file.  

4. Enable the [EditorConfig](http://editorconfig.org/) plugin in your editor.

5. Make your change.

6. Write tests that document the change, including happy paths, failure modes, and edge cases.

7. Follow the [style guide](http://www.oracle.com/technetwork/java/codeconvtoc-136057.html).

8. Ensure all tests pass:

        ./gradlew clean build

9. If [FindBugs](http://findbugs.sourceforge.net/) catches problems, fix them.

10. [Squash your commits](https://git-scm.com/book/en/v2/Git-Tools-Rewriting-History#Squashing-Commits).

11. Write a [good commit message](http://tbaggery.com/2008/04/19/a-note-about-git-commit-messages.html).

12. Push to your fork.

13. [Submit a pull request](https://github.com/AusDTO/spring-security-stateless/compare/).

14. [CircleCI](https://circleci.com/gh/AusDTO/spring-security-stateless) should pick up your change and trigger a build. Ensure it passes. If it fails, go to step 5.

15. Wait patiently for a response.
