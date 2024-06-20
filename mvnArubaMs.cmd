cd Lib
call mvn clean install -DskipTests -U
cd ..
cd DiscoveryServer
call mvn clean install -DskipTests -U
cd ..
cd Inventory
call mvn clean install -DskipTests -U
cd ..
cd Delivery
call mvn clean install -DskipTests -U
cd ..
cd Order
call mvn clean install -DskipTests -U
cd ..
cd Payment
call mvn clean install -DskipTests -U
cd ..