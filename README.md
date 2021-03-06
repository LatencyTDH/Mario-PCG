# Mario-AI

This project uses the simulated annealing algorithm for procedural content generation of Mario levels. The function used
to evaluate the quality of a generated level is based on a "funness" metric that the player assigns. In order to find
the function that best approximates the player's rating pattern, the game builds a nonlinear regression model with
algorithms provided by the **scikit-learn** library. In particular, the nonlinear regression model uses a
[support vector machine] (http://scikit-learn.org/stable/modules/svm.html#regression) with a radial basis kernel
function. Hence, the game incorporates AI as a key element in the gaming experience, as the generated levels will be
directly tailored to a player's unique gaming preferences.

In this project, the map generation approach I followed is a hybrid one that combines constructionist and randomized
optimization techniques. That is, the constructionist approach first initializes a randomly generated map that is pieced
together using pre-made building blocks. The simulated annealing algorithm is then used to find the optimal
configuration of components based on a user-refined evaluation function.

Player preference modeling in this project is done using a human-in-the-loop approach. At the end of every
procedurally-generated level, the player assigns a "fun" rating (0="dislike" or 1="like"). These ratings are then input
as training data to the nonlinear regression model, which is used to refine the parameters of the evaluation function.

## How to Run the Project

1. Clone this repository.

2. Install the required dependencies. For Java, install **ant**. For the game to build the player "funness" model,
install **Python** and the Python libraries **numpy**, **scipy**, and **scikit-learn**.

3. Type **ant play** into the command line to play a customized PCG level.

4. After after every level, enter a "funness" rating (0="dislike" or 1="like") when prompted.

5. Play more levels until an accurate regression model of "funness" can be approximated.

By default, the file "ratings.arff" contains sample ratings that were obtained after playing 50+ randomly generated
levels. The file "svr_model.pkl" stores the support vector regression model that is used to predict the "funness" of a
level generated by the simulated annealing algorithm. To remove these pre-loaded training examples and the corresponding
regression model, type **ant clean-super**. Bear in mind that the game requires a minimum of 50 ratings before a
regression model can be built\! Therefore, the game will continue to generate random levels until the minimum threshold
of games is reached.

## Commands

**ant compile**: Compiles src/ directory files.

**ant play**: Plays a custom level that uses procedural content generation.

**ant play-default**: Plays a random level that is built using pre-authored chunks.

**ant clean**: Removes the .class files contained in the bin/ directory.

**ant clean-super**: Performs a regular clean and then removes the ratings.arff and svr_model.pkl files.

## Configuration File

The values stored in **config.txt** contains the parameters for the simulated annealing algorithm and rating procedures.
They may be modified for testing purposes, but please do NOT delete it, because doing so will break the game.

**temperature** = the starting temperature for the SA algorithm {double}

**coolingRate** = the rate of temperature decrease {double: 0-1}

**TRAINING_MINIMUM** = the minimum number of funness ratings required before the game can build the regression model {int}

**regression** = the type of regression used to build the player's "funness" function {"sv", "linear"}

**ratingsFile** = the .arff file that the game will use to read and store the player's funness ratings {"ratings.arff"}

## Format of Ratings File

The data contained within **ratings.arff** is formatted as a 10-dimensional feature vector and an additional class label
which indicates the player's rating of a given level. The feature vector in the game has the following structure:

```
x = <probBuildJump,probBuildCannons,probBuildHillStraight,probBuildTubes,probBuildStraight,difficulty,blocksCoins,blocksEmpty,blocksPower,enemies,fun>
```

where

    probBuildJump = probability of creating a gap or gap with stairs in the map

    probBuildCannons = probability of building a cannon that shoots a Bullet Bill

    probBuildHillStraight = probability of raising the level elevation with hill

    probBuildTubes = probability of building a pipe that may or may not have a Piranha Flower

    probBuildStraight = probability of creating straight terrain with infrequent dips in elevation

    difficulty = the level's difficulty (0-4)

    blocksCoins = the number of blocks containing coins in the level

    blocksEmpty = the number of empty blocks

    blocksPower = the number of blocks containing power-ups

    enemies = the total number of enemies in the level

    fun = 0 for dislike, 1 for like

## Sample Images

![1](images/pca_fun.png)
![2](images/lin_model.png)
![3](images/sa_fun.png)

## Sources
1. C. Pedersen, J. Togelius, and G. N. Yannakakis, “Modeling player experience in super mario bros,” in Computational Intelligence and Games, 2009.
CIG 2009. IEEE Symposium on, 2009, pp. 132–139.

2. S. Rabin, Game AI Pro 2: Collected Wisdom of Game AI Professionals. Boca Raton, Florida: CRC Press.

3. S. J. Russell, P. Norvig, and E. Davis, Artificial Intelligence: A Modern Approach, 3rd ed.
Upper Saddle River: Prentice Hall, 2010.

4. http://scikit-learn.org/stable/modules/svm.html