# Mario-AI  

This project uses the simulated annealing algorithm for procedural content generation of Mario levels. The function used to
evaluate the quality of a generated level is based on a "funness" metric that the player assigns.

Player experience
modeling in this project is done using a human-in-the-loop approach to infer player preference. At the end of every
procedurally-generated level, the player assigns a "fun" rating (0="dislike" or 1="like), which is then used to refine the initially defined evaluation function.
In this project, the map generation approach I followed is a hybrid one that combines constructionist and randomized optimization techniques.
That is, the constructionist approach first initializes a randomly generated map, which is pieced together using pre-made building blocks. The simulated
annealing algorithm is then used to find the optimal configuration of components based on a user-refined evaluation
function.

## How to Run the Project

1.  Clone the repository https://github.gatech.edu/sdai30/Mario-AI.git.

2.  Install **ant**.

3.  Type **ant play** into the command line to play a customized PCG level.

## Commands
**ant compile**: Compiles src/ directory files.

**ant play**: Plays a custom level that uses procedural content generation.

**ant play-default**: Plays a random level that is built using pre-authored chunks.

**ant clean**: Removes the .class files contained in the bin/ directory.
