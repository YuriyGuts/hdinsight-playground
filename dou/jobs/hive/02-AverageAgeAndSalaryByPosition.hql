SELECT
    Position,
    CAST(AVG(Age) AS INT) AverageAge,
    CAST(AVG(AverageMonthlySalaryUSD) AS INT) AverageSalary
FROM
    DouSalaryMay2012
GROUP BY
    Position
ORDER BY
    AverageSalary
