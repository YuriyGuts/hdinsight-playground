CREATE EXTERNAL TABLE DouSalaryMay2012
(
    ID INT,
    SkipField1 STRING,
    Age INT,
    Sex STRING,
    Position STRING,
    Education STRING,
    ProgrammingLanguage STRING,
    Industry STRING,
    Domain STRING,
    AdditionalSpeciality STRING,
    TotalWorkExperienceStr STRING,
    CurrentWorkExperience FLOAT,
    AverageMonthlySalary INT,
    SalaryCurrency STRING,
    AnnualBonus INT,
    BonusCurrency STRING,
    CompanySize STRING,
    CompanyLocation STRING,
    EnglishLevel STRING,
    ResponseDateTime STRING,
    UserAgent STRING,
    TotalWorkExperience FLOAT,
    CompanyLocationCode STRING,
    AverageMonthlySalaryUSD INT,
    SpecialityCode STRING
)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
LOCATION '/dou/input/may2012'
