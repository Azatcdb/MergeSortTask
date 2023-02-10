# MergeSortTask
Сортировка слиянием

Версия JDK 17.0.2  
Система сборки Maven (Version 3.8.1)  
Сторонние библиотеки: Project Lombok 1.18.24  
       ` <dependency>  `\
           `    <groupId>org.projectlombok</groupId>`\
           `    <artifactId>lombok</artifactId>  `\
           `    <version>1.18.24</version>  `\
           `    <scope>provided</scope>  `\
      `  </dependency> ` 



Параметры программы задаются при запуске через аргументы командной строки:  
  -режим сортировки (-a или -d), необязательный, по умолчанию сортируем по возрастанию;  
  -тип данных (-s или -i), обязательный;  
  -имя выходного файла, обязательное;  
  -остальные параметры – имена входных файлов, не менее одного.  
  
Входные файлы должны находиться в одной директории с исполняемым файлом.  
Примеры запуска из командной строки для Windows:  

java -jar focus_task.jar -i -a out.txt in.txt (для целых чисел по возрастанию)  
java -jar focus_task.jar -s out.txt in1.txt in2.txt in3.txt (для строк по возрастанию)  
java -jar focus_task.jar -d -s out.txt in1.txt in2.txt (для строк по убыванию)  
