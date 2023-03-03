# Using PMD

Pick a Java project from Github (see the [instructions](../sujet.md) for suggestions). Run PMD on its source code using any ruleset. Describe below an issue found by PMD that you think should be solved (true positive) and include below the changes you would add to the source code. Describe below an issue found by PMD that is not worth solving (false positive). Explain why you would not solve this issue.

## Answer

True positive:

../../../TCA/TP1/TPDockerSampleApp/src/main/java/main/Main.java:61:     ForLoopCanBeForeach:    This for loop can be replaced by a foreach loop
![image](https://user-images.githubusercontent.com/51517595/222704260-60291f51-dc75-4e38-be80-61926b2ebf26.png)
