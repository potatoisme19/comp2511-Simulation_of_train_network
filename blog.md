## Assignment-i Blog - z5606467 (replace with your zID)

> **Hint**: You should be writing down your design decision in this blog as you complete the assignment. This is not formal writing, so feel free to use dot points or short sentences. Try and keep your blog concise - a short blog focused on ***why*** you made your design decisions will do much better than a rambling one that just recounts ***what*** you did.

### Week 2

> I made a folder inside trains called station to store station (abstract class) and its subclasses (different types of train). I thought it would make my code look more organised (segregated in different folders) but then I got confused by the changes in package so I reverted back to the original. I will look into organizing after I'm done.
> avoided defensive programming becuase code is written using design by contract.

### Week 3

> I wrote all the train code into a single file (not an abstract class) because I thought it made it more manageable (lesser files - same issue as before in organising) but then it didnt make sense because it lacked abstraction and extensibility so I made train an abstract class. 
> I thought making createTrain and createStation have factory pattern would be a good idea, to comply to open/close principle and improve extensibility. I didnt do it because it seems too complex for only 4 types of train and it makes it difficult for me to follow the code flow.(unlike the chess example in tutorial, which has more pieces to create and does not have a lot of classes already related to one another)

### Week 4

> I did a lot of refactoring in terms of changing magic numbers to constants to improve adaptability. 
> Thought about making isMechanic an interface since its a superpower, but didnt move forward with it because it is only one property and adding an interface will add unnecessary complexity. I will add an interface if there are more roles of people that can eb a mecahnic. 

### Week 5

> I made load an abtract class and moved passenger and cargo into it as subclass and refactored my code because passenger and cargo has common properties. Open/ Closed principles states that I can add new load types without modifying existing code. it also makes controller method cleaner 

### Overall Reflection

> How do you think you did, challenges you faced, and what you learnt.