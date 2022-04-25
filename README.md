# Ai-project

Problem Statement:

In this project we will create a system which will help delivery boys to deliver parcel in a time efficient order. The problem is to rank the delivery location in the order, the delivery boy should visit to optimize the trip. The problem is very similar to the travelling salesman problem and we will try to optimize it using Artificial Intelligence.

The above problem is an NP-hard problem. The brute force solution is not practically possible as the time complexity is O(n!). An optimized approach using DP is also not relevant as its time complexity is O(n^2 . 2^n). Using the optimized approach we can compute for n <= 23 in feasible time.


**Solution:**

We have solved this problem using simmulated annealing. We have tested for two type of neighbourhood generation methods

1. Choose two locations randomly and swap them
2. Choose a random segment of the current path and reverse it

We have tested for both the methods of neighbourhood generation. The output shows number of cases where method 1 (Choose two locations randomly and swap them) performs better than method 2 (Choose a random segment of the current path and reverse it).

Output is 1/100
