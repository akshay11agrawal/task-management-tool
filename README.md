## Task Management Tool


### Feature 1 : Extend the application

The task is to extend the current functionality of the backend by
- implementing a new task type
- showing the progress of the task execution
- implementing a task cancellation mechanism.

The new task type is a simple counter which is configured with two input parameters, `x` and `y` of type `integer`.
When the task is executed, counter should start in the background and progress should be monitored.
Counting should start from `x` and get increased by one every second.
When counting reaches `y`, the task should finish successfully.

The progress of the task should be exposed via the API so that a web client can monitor it.
Canceling a task that is being executed should be possible, in which case the execution should stop.

### Feature 2 : Periodically clean up the tasks

The API can be used to create tasks, but the user is not required to execute those tasks.
The tasks that are not executed after an extended period (e.g. a week) should be periodically cleaned up (deleted).




#### Technologies Used :

💻 Languages: Java  
🌱 Frameworks: Spring, SpringBoot, SpringData JPA  
📡 Version Control: Git, Github  
🛠 Build Tools: Maven  
🧪 Testing: TDD - Junit, Mockito  
🚀 APIs: RESTful  
📦 Caching : Redis  
🛢️ Database :  H2