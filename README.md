# marvin-be
marvin backend application

* test db path: http://localhost:8080/campustrade/h2-console

* new db path: mysql -h marvim-test.cvykiq06g0wo.eu-north-1.rds.amazonaws.com -u admin -p
  * contact with admin for password


* Bearer (login) tokens' expiration duration can be changed from prod
* Inactive bearer tokens are deleted from db every 15 minutes 
  * Duration can be changed from TokenCleanupScheduler.