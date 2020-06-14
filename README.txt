## Build

To build the application

	../scripts/build.sh

## Run Application

To run the application

    ../scripts/run.sh

## Tests

To run the complete test cases

    ../scripts/runtest.sh

## Log Files
runConsoleOutput.txt - will have the console logs of build.bat

testRunConsoleOutput.txt - will have the console logs of runtest.bat

## Application logs
will be stored in  ../logs/datingapp.log

## User details will be fetched from the TSV file stored in
Path : ../resources/dataset/user.tsv
User can use different tsv file from other location as well and same should be configured in application.properties as below
inputfile.name=classpath:dataset/user.tsv

##Rest endpoint is also available for searching the matching users through curl or Mozilla Rest Client
application is started in the port 5555, it can modified in application.properties.

## To search through testing tool - Example
http://localhost:5555/datingapp/searchMatch/UserB
Content-type : Application-JSON
Output:["UserA","UserD"]