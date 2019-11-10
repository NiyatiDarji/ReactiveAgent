# ReactiveAgent-
Implementing reactive agent in krislet environment.

My reactive agent performs the actions based on the following rules:
1. If you don't know where ball is then turn right and wait for new info
2. If ball is too far to kick it then
2.1 If we are directed towards the ball then dash to the ball, else turn to the ball
3. If we don’t know where opponent goal is then turn and wait for new info, else kick ball
To implement these rules, I have used 4bit binary combination; each combination has a predefined corresponding action. The 4 bits are: 
1st bit: B - set if ball object != null
2nd bit: D - set if distance > 1.0
3rd bit: d - set if direction != 0
4th bit: G – set if goalpost object != null
combination = B + D + d + G
All the combinations with their corresponding actions are stored in “question1.txt” file.
“question1.txt” format-  combination: action(action_argument).action_argument can be one or two arguments separated by comas.
e.g.: ‘1011:kick(100,direction)’ means B = 1, D =0, d = 1, G = 1 i.e. We got the ball object, its distance >1 is false, its direction is correct and we got the goalpost object. Action to be performed is kick with power 100 and in the goalpost direction.
To edit the behaviour of the agent, one can change the actions for the combination. Actions should be one among turn, dash, kick and waitturn.
My Brain.java file run(): First gets the environment variables (ball B, goalpost G, distance D, direction d), checks their status and sets their bits accordingly. This forms the entire combination= B+D+d+G. Then,
action = findRuleGetAction(combination): reads the question1.txt file, fetch the line for the combination and returns the corresponding action. Now, the run() will perform this action with its arguments.
To run the code, start server -> start monitor -> question1- TeamStart . Once all the players appear on the monitor , Refree -> Kick_off and the players will start playing the game.
