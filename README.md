# Akari-Puzzle-Solver
This java program reads in akari puzzle(s) and solves them using 2 different A.I. searches and 3 different heuristics, total of 6 combinations

Searches: Backtracking, forward-checking
Heuristics: Random, Most-Constrained and Most-Constraining.

Random heuristic selects the next available node randomly/sequentially.
Most contrained heuristic selects the next available node with the least option possible.
Most constraing heuristic selects the next available node that lights up the maximum number of cells on the board.

Please have your akari puzzle ready as a .txt file. A sample input file looks like this


/# Start of puzzle

10 10

__3_____2_

1__3__0__3

_120_101__

___2__3_11

1___2_____

__1__2__0_

1__12_202_

__3002_3__

___1____2_

_1_0__110_



## Things to keep in mind before running the program
- Backtracking search does not always guarantee you a solution because it takes too long time to find a solution.
- Heuristic 3, which is the most constraining nodes, is very inefficient one. It does not guarantee a solution.
