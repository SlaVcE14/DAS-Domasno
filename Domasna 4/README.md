За **backend** и **frontend** се користи MVC шаблон. Давата проекти се Spring Boot апликација. 
Во backend, класата [**FileSystem**](Domasna%204/backend/src/main/java/mk/finki/ukim/das/backend/database/FileSystem.java) која служи за читање на податоците од базата на податици е Singleton за да се осигура да нема 
повење инстанци од таа класа.

DataScraper е јава апликација која служи за превземање на податоците од [mse.mk](https://www.mse.mk/) и зачувување во json датотека која се наоѓа во [database/data.json](/Domasna%204/database).
Оваа програма е екпортирана во [datascraper.jar](/Domasna%204/DataScraper/datascraper.jar) датотека која backend-од ја стартува секој 24 часа за да ги превземе новите податоци за издавачите.

https://github.com/user-attachments/assets/90fdcc0f-92eb-41d2-8b2b-650c774f468b

