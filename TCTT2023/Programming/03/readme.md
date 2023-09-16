# Write-ups for TCTT2023/Programming/03

## Flag pattern

`CTT23{xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx}`

## Challenge Files

[Northwind4SQLServer.zip](./Northwind4SQLServer.zip)

## Solution

Solution is not available yet.

1. First we import the sql file into mysql first.

```bash
sqlcmd -S localhost -U sa -P 'THISISTHESECUREPASSWORD' -i ./Northwind4SQLServer.sql
```

2. Let find category id of the second most sales quantity. By grouping the products with the category and sorting them.

```sql
USE Northwind;

SELECT p.CategoryID, SUM(od.Quantity) AS TotalQuantity
FROM "Order Details" od
JOIN Products p ON od.ProductID = p.ProductID
GROUP BY p.CategoryID
ORDER BY TotalQuantity DESC;
```

```
CategoryID|TotalQuantity|
----------+-------------+
         1|         9532|
         4|         9149|
         3|         7906|
         8|         7681|
         2|         5298|
         5|         4562|
         6|         4199|
         7|         2990|

8 row(s) fetched.
```

Now, we now that the category id of second most sales quantity, which is `4`.

3. So, we only need to find the name of the product that has most stock price in the category id `4`. We can do this by joining the `Products` table with `Alphabetical list of products` table and sort by `UnitPrice * UnitsInStock`

```sql
USE Northwind;

SELECT p.ProductName, i.CategoryName, p.UnitPrice * p.UnitsInStock AS UnitStockPrice
FROM "Alphabetical list of products" i
JOIN Products p ON p.CategoryId = 4 AND p.ProductId = i.ProductId
ORDER BY UnitStockPrice DESC;
```

```
ProductName              |CategoryName  |UnitStockPrice|
-------------------------+--------------+--------------+
Raclette Courdavault     |Dairy Products|     4345.0000|
Queso Manchego La Pastora|Dairy Products|     3268.0000|
Gudbrandsdalsost         |Dairy Products|      936.0000|
Camembert Pierrot        |Dairy Products|      646.0000|
Flotemysost              |Dairy Products|      559.0000|
Mozzarella di Giovanni   |Dairy Products|      487.2000|
Queso Cabrales           |Dairy Products|      462.0000|
Mascarpone Fabioli       |Dairy Products|      288.0000|
Geitost                  |Dairy Products|      280.0000|
Gorgonzola Telino        |Dairy Products|        0.0000|

10 row(s) fetched.
```

So the answer we want to know is `Raclette Courdavault Dairy Products`

4. After we `MD5` the answer and combine it with flag pattern, we will get the flag `CTT23{b6c2aaabec23452ab13a124b5e1ea6e7}`
