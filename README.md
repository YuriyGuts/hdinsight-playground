HDInsight Hadoop Examples
=========================

A few sample MapReduce jobs that can be run on Microsoft HDInsight Hadoop distribution.

Languages and frameworks used:
  * Java
  * C#/.NET
  * JavaScript
  * Hive
  * Pig

Input file formats: 
  * Plain text
  * JSON
  * CSV

Problem 1: IIS HTTP Access Logs
-------------------------------
  * Folder: __iis__
  * Dataset: __iis-log-small.txt__

###HadoopIISStatusCodeCount Job
A MapReduce application that counts the number of log entries for each HTTP status code.

####How to run (Java version):
  1. Build the project and export it to a JAR file.
  2. Load the dataset into HDFS: run the following command in Hadoop command line:

    `hadoop fs -put <path-to-dataset-file> /iis/input/iis-log-small.txt`
    
  3. To execute the MapReduce job, run:

    `hadoop jar <path-to-JAR-file> HadoopIISStatusCodeCount -m 3 -r 3 /iis/input /iis/output-java`
    
  4. To browse the results, run:

    `hadoop fs -cat /iis/output-java/part-00000`
    
####How to run (.NET version):
  1. Build the project, check if `HadoopIISStatusCodeCount.dll` and `MRLib` folder exist in the build output folder.
  2. Make sure that the build output path does not contain spaces.
  3. Upload the dataset to HDFS (see Java version above).
  4. Navigate to `MRLib` folder and execute the MapReduce job via `MRRunner`:

    `MRRunner.exe -dll ..\HadoopIISStatusCodeCount.dll -- /iis/input /iis/output-dotnet`

  5. To browse the results, run:

    `hadoop fs -cat /iis/output-dotnet/part-00000`

Problem 2: UFO Sightings in the USA
-----------------------------------
  * Folder: __ufo__
  * Dataset: __ufo-sightings.json__

###SightingCountByState Job
A MapReduce script that counts the number of reported UFO sightings for each U.S. state.

####How to run (JavaScript version)
  1. Upload the dataset to HDFS:
    
    `hadoop fs -put <path-to-dataset-file> /ufo/input/json-sightings.json`

  2. Upload the script to HDFS:

    `hadoop fs -put <path-to-JS-file> /ufo/jobs/SightingCountByState.js`
    
  3. Run the following command in HDInsight Interactive JavaScript Console:

    `runJs("/ufo/jobs/SightingCountByState.js", "/ufo/input", "/ufo/output-js")`
    
  4. To browse the results, use the following JavaScript Console command:

    `#cat /ufo/output-js/part-r-00000`

###Top10StatesBySightingCount Job
A Pig job defined in HDInsight JavaScript syntax that determines the top 10 U.S. states by the number of UFO reports.

####How to run
  1. Upload the dataset and JavaScript file from the SightingCountByState job (see above) if they don't exist yet.
  2. Navigate to HDInsight Interactive JavaScript Console and run the following command:

    `pig.from("/ufo/input").mapReduce("/ufo/jobs/SightingCountByState.js", "state, count: int").orderBy("count DESC").take(10).to("/ufo/output-pig")`

  3. Browse the results via JavaScript Console:

    `#cat /ufo/output-pig/part-r-00000`

Problem 3: DOU.ua Salary Poll
-----------------------------
  * Folder: __dou__
  * Dataset: __dou-2012-may-final.csv__

###AverageAgeAndSalaryByPosition Job
A Hive script that uses SQL-like language to query the average age and monthly salary for various software engineering professions.

####How to run:
  1. Upload the dataset to HDFS:

    `hadoop fs -put <path-to-CSV-file> /dou/input/may2012/dou-2012-may-final.csv`

  2. Navigate to HDInsight Interactive Hive Console.
  3. Paste and execute the command from `01-CreateTable.hql` file to create Hive table schema.
  4. Paste and execute the query from `02-AverageAgeAndSalaryByPosition.hql`.
  
  _Note_: you can also redirect Hive output to a local file or use Hive Excel Add-in.
