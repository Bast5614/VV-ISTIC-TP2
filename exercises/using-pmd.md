# Using PMD

Pick a Java project from Github (see the [instructions](../sujet.md) for suggestions). Run PMD on its source code using any ruleset. Describe below an issue found by PMD that you think should be solved (true positive) and include below the changes you would add to the source code. Describe below an issue found by PMD that is not worth solving (false positive). Explain why you would not solve this issue.

## Answer

True positive:

../../../TCA/TP1/TPDockerSampleApp/src/main/java/main/Main.java:61:     ForLoopCanBeForeach:    Cette boucle peut-être remplacé par une foreach boucle

![image](https://user-images.githubusercontent.com/51517595/222704260-60291f51-dc75-4e38-be80-61926b2ebf26.png)

./core/src/terraria/game/actors/Inventory/Inventory.java 	22 	Unused import 'terraria.game.actors.entities.EntityLoader'
Dans un autre projet il y a également des import inutile qui peuvent être retiré.

 ./core/src/terraria/game/actors/entities/player/Player.java 	119 	Useless parentheses.
 ou en core des parenthèses inutiles

False positive:

./core/src/terraria/game/screens/GameScreen.java 	275 	Switch statements should be exhaustive, add a default case (or missing enum branches)
Ici les seuls états possible étant GAME_PAUSED/GAME_RUNNING/GAME_OVER. Il n'est donc pas nécéssaire d'ajouter un case 'default'. 

![image](https://user-images.githubusercontent.com/51517595/223387644-324c1e88-d2fa-472a-92a5-b66c48f0f922.png)


