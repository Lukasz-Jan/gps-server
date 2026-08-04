
#### Gps data servers application 
###### Allows for gps devices regitrations and positions data receiving
###### Returns confirmations in different channels
###### Tested with docker v. 29.2.1 on ubuntu jammy

#### Starting gps data servers
##### From terminal with command: 
###### sh start_gps_server.sh
###### Then wait some seconds until servers are started

#### Starting message producers 
##### From main project directory:

###### mvn test -f devices/ -am -Dtest=com.gps.devices.GpsDataSenderTest#send_registrations_and_their_positions_2
###### mvn test -f devices/ -am -Dtest=com.gps.devices.GpsDataSenderTest#send_registrations_and_their_positions_large_numbers

##### From devices subdirectory:

###### mvn test -Dtest=GpsDataSenderTest#send_registrations_and_their_positions_2
###### mvn test -Dtest=GpsDataSenderTest#send_registrations_and_their_positions_large_numbers

#### Results previevs:
##### Mongodb:
###### with mongosh on port 27017
##### ActiveMq:
###### http://localhost:8162/admin/
###### http://localhost:8161/admin/


##### To do:
###### sharding
###### basic authorization