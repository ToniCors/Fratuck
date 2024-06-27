set location=C:\Users\ToniCors\.m2\repository\com\aruba
echo %location%

start java -Xms128m -Xmx512m -jar "%location%\DiscoveryServer\1.0.0\DiscoveryServer-1.0.0.jar"
timeout 10
start java -Xms128m -Xmx512m -jar "%location%\ApiGateway\1.0.0\ApiGateway-1.0.0.jar"
timeout 10
start java -Xms128m -Xmx512m -jar "%location%\Orchestrator\1.0.0\Orchestrator-1.0.0.jar"
timeout 10
start java -Xms128m -Xmx512m -jar "%location%\Inventory\1.0.0\Inventory-1.0.0.jar"
timeout 10
start java -Xms128m -Xmx512m -jar "%location%\Order\1.0.0\Order-1.0.0.jar"
timeout 10
start java -Xms128m -Xmx512m -jar "%location%\Payment\1.0.0\Payment-1.0.0.jar"
timeout 10
start java -Xms128m -Xmx512m -jar "%location%\Delivery\1.0.0\Delivery-1.0.0.jar"
timeout 10
