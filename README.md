## Start with: Docker Compose
###### Set your env variables in .env file then run:
```bash
    cat .env && docker-compose up
```

## Start with: Manuel Setup:

#### 1. Database Setup
###### Create a Postgresql Database for the Application to use:
###### connect to the psql commandline
```bash
psql -U postgres    //connect with your psql user
\l                      //show databases
CREATE DATABASE postgres;
```

##### 2. Env
###### Set the following Environment Variables:
- ENV_POSTGRES_DB_NAME (default: postgres)
- ENV_POSTGRES_DB_USERNAME (default: postgres)
- ENV_POSTGRES_DB_PASSWORD (default: postgres)
- ENV_SPOONACULAR_API_KEY

##### 3. Run 'gradle start' when you have gradle installed, then follow the instructions to set environment variables.
```bash
./gradlew start
```